package com.example.SellPhone.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Chat bot AI có sử dụng Redis nên phải bật Redis để tránh bị lỗi
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenAIService {
    static final int SUMMARY_TRIGGER_TOKENS = 2000; // ngưỡng token
    static final int MIN_MESSAGES_FOR_SUMMARY = 4;
    final String apiKey;
    final JdbcTemplate jdbc;
    final ObjectMapper mapper = new ObjectMapper();
    final StringRedisTemplate redisTemplate;
    // Cache schema để chỉ tạo 1 lần
    String schemaCache;

    public OpenAIService(@Value("${openai.api-key}") String apiKey, JdbcTemplate jdbc, StringRedisTemplate redisTemplate) {
        this.apiKey = apiKey;
        this.jdbc = jdbc;
        this.redisTemplate = redisTemplate;
    }

    // Load schema 1 lần duy nhất
    @PostConstruct
    public void init() {
        this.schemaCache = buildRestrictedSchema();
        System.out.println("=== Schema Loaded Once ===");
        System.out.println(schemaCache);
    }

    // CHỈ CHO PHÉP AI ĐỌC 3 BẢNG: products, specifications, specification_variants
    private String buildRestrictedSchema() {
        String[] allowedTables = {"products", "specifications", "specification_variants"};
        StringBuilder sb = new StringBuilder();

        for (String tableName : allowedTables) {
            sb.append(tableName).append(":\n");

            List<Map<String, Object>> cols =
                    jdbc.queryForList("SHOW COLUMNS FROM " + tableName);
            for (Map<String,Object> col : cols) {
                sb.append(" - ")
                        .append(col.get("Field"))
                        .append(" (")
                        .append(col.get("Type"))
                        .append(")\n");
            }

            List<Map<String,Object>> sample =
                    jdbc.queryForList("SELECT * FROM " + tableName + " LIMIT 3");

            sb.append("Ví dụ dữ liệu:\n");
            for (Map<String,Object> row : sample) {
                sb.append("   ").append(row).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public Map<String,Object> processChat(String sessionId, String question) throws Exception {

        String normalizedQuestion = question
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", " ");

        String key = "chat:question:" + DigestUtils.sha256Hex(normalizedQuestion);
        String cachedAnswer = redisTemplate.opsForValue().get(key);
        if (cachedAnswer != null) {
            return Map.of("answer", cachedAnswer + " (from cache)");
        }

        List<Map<String,String>> history = loadHistory(sessionId);
        // Thêm câu hỏi vào lịch sử
        appendHistory(sessionId,
                Map.of("role","user","content",question));
        history.add(Map.of("role","user","content",question));

        // Kiểm tra xem có nên tóm tắt hay không
        // Nếu không thì cắt và chỉ giữ 5 tin gần nhất
        if (shouldSummarize(history)) {
            List<Map<String,String>> summarized = summarizeHistory(history);

            // lưu tóm tắt vào redis
            replaceHistoryWithSummary(
                    sessionId,
                    summarized.getFirst().get("content")
            );

            history = summarized;
        }

        // Bước 1 — Gọi AI để tạo SQL
        Map<String,Object> resp1 = callOpenAI(history, null);

        Map<String,Object> msg1 = extractMessage(resp1);
        Map<String,Object> fn = (Map<String,Object>) msg1.get("function_call");

        String answer;

        if (fn == null) {
            // AI trả lời luôn không cần SQL
            answer = (String) msg1.get("content");
        } else {
            // AI tạo SQL
            Map<String,Object> args =
                    mapper.readValue((String) fn.get("arguments"), Map.class);
            String sql = (String) args.get("sql");

            if (sql == null || !sql.trim().toLowerCase().startsWith("select")) {
                answer = "Xin lỗi, tôi không thể thực hiện yêu cầu này.";
            } else {
                String sqlHash = DigestUtils.sha256Hex(sql);
                String sqlCacheKey = "sql:result:" + sqlHash;

                String cachedSqlResult = redisTemplate.opsForValue().get(sqlCacheKey);

                List<Map<String, Object>> rows;
                if (cachedSqlResult != null) {
                    rows = mapper.readValue(cachedSqlResult, List.class);
                } else {
                    // Thực thi SQL
                    rows = jdbc.queryForList(sql);

                    redisTemplate.opsForValue().set(sqlCacheKey,
                            mapper.writeValueAsString(rows),
                            Duration.ofDays(2));
                }

                if (rows.isEmpty()) {
                    answer = "Không tìm thấy dữ liệu phù hợp.";
                } else {
                    // Chỉ gửi tối đa 5 dòng để tiết kiệm token
                    rows = rows.subList(0, Math.min(rows.size(), 5));
                    String json = mapper.writeValueAsString(rows);

                    // Bước 2 — Gọi AI để diễn giải kết quả
                    Map<String,Object> resp2 = callOpenAI(history, json);

                    Map<String,Object> msg2 = extractMessage(resp2);
                    answer = (String) msg2.get("content");
                }
            }
        }

        // Lưu vào lịch sử
        appendHistory(sessionId,
                Map.of("role","assistant","content",answer));
        history.add(Map.of("role","assistant","content",answer));

        // Lưu vào Redis
        if(fn!= null) redisTemplate.opsForValue().set(key, answer, Duration.ofDays(1));

        return Map.of("answer", answer);
    }

    private Map<String,Object> callOpenAI(
            List<Map<String,String>> history,
            String sqlResult
    ) throws Exception {

        Map<String,Object> request = new HashMap<>();
        request.put("model", "gpt-4o-mini");

        List<Map<String,String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role","system",
                "content",
                """
                    Bạn là Hiki — trợ lý AI của cửa hàng TĐ Mobile.
                       Nhiệm vụ chính:
                       - Trả lời tất cả câu hỏi của khách liên quan đến điện thoại, sản phẩm, tư vấn mua hàng.
                       - Khi người dùng hỏi về bất kỳ sản phẩm nào (vd: iPhone 17, Samsung A56, Google Pixel 10,…),
                         BẮT BUỘC phải truy vấn SQL để xác định thông tin trước khi trả lời.
                       - Không được tự phán đoán cửa hàng có hay không; luôn kiểm tra bằng SQL trước.
                       Quy tắc khi xử lý sản phẩm:
                       1. Nếu SQL trả về dữ liệu:
                           → Tóm tắt thông tin sản phẩm theo phong cách gọn, đẹp, thân thiện.
                           → Giữ các trường quan trọng: tên, màu, trạng thái, mô tả, camera, màn hình, chip, ram, sạc.
                       Quy tắc về SQL:
                       - Chỉ truy vấn các bảng: products, specifications, specification_variants.
                       - Tự động dùng function queryDatabase khi cần.
                       - Không được thực thi các lệnh ngoài SELECT.
                       Quy tắc khi giới hạn chủ đề:
                       - Bạn có quyền giải thích kiến thức liên quan đến smartphone
                       - Nếu người dùng hỏi ngoài lĩnh vực điện thoại hoặc mua bán:
                           → Từ chối lịch sự.
                       Quy tắc hình ảnh:
                       - Khi trả về hình ảnh, luôn dùng đường dẫn dạng: '/uploads/...' \s
                       - Không bao giờ trả về URL đầy đủ như https://domain.com/uploads/...
                       Quy tắc hội thoại:
                       - Câu trả lời rõ ràng, thân thiện, không quá dài.
                       - Có thể dùng emoji phù hợp nhưng không lạm dụng.
                       Dưới đây là schema của 3 bảng được phép truy vấn:
        """ + schemaCache
                ));

        messages.addAll(history);

        if (sqlResult != null) {
            messages.add(Map.of("role","system",
                    "content","Kết quả SQL dạng JSON:\n" + sqlResult));
        }

        request.put("messages", messages);

        // Function schema
        request.put("functions", List.of(
                Map.of(
                        "name", "queryDatabase",
                        "description", "Thực thi câu SQL SELECT",
                        "parameters", Map.of(
                                "type","object",
                                "properties", Map.of(
                                        "sql", Map.of("type","string")
                                ),
                                "required", List.of("sql")
                        )
                )
        ));

        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization","Bearer " + apiKey);
        conn.setRequestProperty("Content-Type","application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(mapper.writeValueAsBytes(request));
        }

        int status = conn.getResponseCode();
        try(InputStream iss = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream()){
            Map<String,Object> res = mapper.readValue(iss, Map.class);

            Map<String,Object> usage = (Map<String,Object>) res.get("usage");
            if (usage != null) {
                System.out.println("Token usage: " + usage);
            }

            if (status == 429) {
                System.out.println("Bị rate limit! Retry-After: " + conn.getHeaderField("Retry-After"));
            }

            return res;

        } catch(Exception ex) {
            return Map.of(
                    "choices", List.of(
                            Map.of("message", Map.of(
                                    "content",
                                    "Xin lỗi, hệ thống đang tạm thời quá tải. Vui lòng thử lại sau."
                            ))
                    )
            );
        }
    }

    private Map<String,Object> extractMessage(Map<String,Object> res) {
        return (Map<String,Object>)
                ((List<Map<String,Object>>) res.get("choices"))
                        .getFirst()
                        .get("message");
    }

    // Hàm ước lượng Token
    private int estimateTokens(String text) {
        if (text == null) return 0;
        // trung bình 1 token ~ 4 ký tự
        return text.length() / 4;
    }

    // Hàm tính tổng token history
    private int estimateHistoryTokens(List<Map<String,String>> history) {
        return history.stream()
                .mapToInt(m -> estimateTokens(m.get("content")))
                .sum();
    }

    // Hàm kiểm tra history đã có summary chưa
    private boolean hasSummary(List<Map<String,String>> history) {
        return history.stream()
                .anyMatch(m ->
                        "system".equals(m.get("role")) &&
                                m.get("content").startsWith("TÓM TẮT HỘI THOẠI")
                );
    }

    // Hàm kiểm tra xem có vượt quá số lượng token hay không? Để tóm tắt
    private boolean shouldSummarize(List<Map<String,String>> history) {
        if (history.size() < MIN_MESSAGES_FOR_SUMMARY) return false;
        if (hasSummary(history)) return false;

        int totalTokens = estimateHistoryTokens(history);

        // Nếu nội dung vượt quá số lượng token quy định thì sẽ tóm tắt
        return totalTokens >= SUMMARY_TRIGGER_TOKENS;
    }

    private List<Map<String,String>> summarizeHistory(
            List<Map<String,String>> history
    ) throws Exception {

        List<Map<String,String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role","system",
                "content",
                """
                Tóm tắt hội thoại sau để dùng làm ngữ cảnh tiếp theo.
                Chỉ giữ:
                - Sản phẩm đã được nhắc tới
                - Ý định mua hàng
                - Các yêu cầu kỹ thuật quan trọng
                Không thêm lời chào, không suy luận thêm.
                """
        ));

        messages.addAll(history);

        Map<String,Object> req = new HashMap<>();
        req.put("model", "gpt-4o-mini");
        req.put("messages", messages);

        Map<String,Object> res = callRaw(req);
        Map<String,Object> msg = extractMessage(res);

        String summary = "TÓM TẮT HỘI THOẠI:\n" + msg.get("content");

        return List.of(
                Map.of("role","system","content", summary)
        );
    }

    // callOpenAI không thêm schema + function, chỉ dùng cho tóm tắt(không dùng hàm callOpenAi ở trên vì hàm đó gửi schema)
    private Map<String,Object> callRaw(Map<String,Object> request) throws Exception {

        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization","Bearer " + apiKey);
        conn.setRequestProperty("Content-Type","application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(mapper.writeValueAsBytes(request));
        }

        int status = conn.getResponseCode();
        try (InputStream iss =
                     (status >= 200 && status < 300)
                             ? conn.getInputStream()
                             : conn.getErrorStream()
        ) {
            return mapper.readValue(iss, Map.class);
        }
    }

    private String toJson(Map<String,String> msg) throws Exception {
        return mapper.writeValueAsString(msg);
    }

    private Map<String,String> fromJson(String json) throws Exception {
        return mapper.readValue(json, Map.class);
    }

    // Lấy history từ Redis (history giúp bot nhớ được người dùng đang chat gì trước đó)
    private List<Map<String,String>> loadHistory(String sessionId) throws Exception {
        String key = "chat:session:" + sessionId + ":history";

        List<String> raw = redisTemplate.opsForList().range(key, 0, -1);
        if (raw == null || raw.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String,String>> history = new ArrayList<>();
        for (String s : raw) {
            history.add(fromJson(s));
        }
        return history;
    }

    // Ghi message vào Redis
    private void appendHistory(String sessionId, Map<String,String> msg) throws Exception {
        String key = "chat:session:" + sessionId + ":history";

        redisTemplate.opsForList().rightPush(key, toJson(msg));

        // Giữ tối đa 10 message gần nhất
        redisTemplate.opsForList().trim(key, -10, -1);

        // Reset TTL mỗi lần user chat
        // Dọn message sau 8h nếu không có message nào được add vào với key đó nữa
        redisTemplate.expire(key, Duration.ofHours(8));
    }

    // Lưu tóm tắt vào Redis
    private void replaceHistoryWithSummary(
            String sessionId,
            String summary
    ) throws Exception {

        String key = "chat:session:" + sessionId + ":history";

        redisTemplate.delete(key);
        redisTemplate.opsForList().rightPush(key,
                toJson(Map.of("role","system","content", summary))
        );
        redisTemplate.expire(key, Duration.ofHours(8));
    }

}

