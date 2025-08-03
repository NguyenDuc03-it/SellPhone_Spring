package com.example.SellPhone.Controller.Management;

import com.example.SellPhone.DTO.Request.Order.OrderUpdateRequest;
import com.example.SellPhone.DTO.Respone.Order.OrderDTO;
import com.example.SellPhone.DTO.Respone.Order.ProductInfoInOrderDTO;
import com.example.SellPhone.Entity.Order;
import com.example.SellPhone.Service.OrderService;
import com.lowagie.text.pdf.BaseFont;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/management/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SpringTemplateEngine templateEngine;

    // Hiển thị danh sách đơn hàng và phân trang
    @GetMapping
    String orderManagement(Model model,
                           @RequestParam(required = false) String searchQuery,
                           @RequestParam(required = false) String statusFilter,
                           @RequestParam(required = false) String action,
                           @RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, 10);  // 10 dòng trên mỗi trang
        Page<Order> orders;
        if ("search".equals(action)) {
            // Chỉ tìm kiếm theo searchQuery
            if (searchQuery != null && !searchQuery.isEmpty()) {
                orders = orderService.searchOrder(searchQuery, pageable);
            } else {
                orders = orderService.getOrder(pageable);
            }

        } else if ("filter".equals(action)) {
            // Lọc (có thể có cả searchQuery và statusFilter)
            if ((searchQuery != null && !searchQuery.isEmpty()) && (statusFilter != null && !statusFilter.isEmpty())) {
                orders = orderService.searchAndFilterOrders(searchQuery, statusFilter, pageable);
            } else if (statusFilter != null && !statusFilter.isEmpty()) {
                orders = orderService.filterByStatus(statusFilter, pageable);
            } else {
                orders = orderService.getOrder(pageable);
            }

        } else {
            // Trường hợp đầu tiên hoặc không rõ hành động
            orders = orderService.getOrder(pageable);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("request", new OrderUpdateRequest());
        model.addAttribute("currentPage", "orders");
        return "DashBoard/order-management";
    }

    // Lấy dữ liệu đơn hàng để hiển thị lên modal xem chi tiết đơn hàng
    @GetMapping("/{id}")
    ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id, Model model) {
        Optional<Order> orderOptionalt = orderService.findById(id); // Gồm cả user + orderItems + product
        if(orderOptionalt.isPresent())
        {
            Order order = orderOptionalt.get();
            OrderDTO orderDTO = new OrderDTO();

            orderDTO.setOrderId(order.getOrderId());
            orderDTO.setFullname(order.getUser().getFullname());
            orderDTO.setPhone(order.getUser().getPhone());
            orderDTO.setAddress(order.getUser().getAddress());
            orderDTO.setOrderTime(order.getOrderTime());
            orderDTO.setOrderStatus(order.getOrderStatus());
            orderDTO.setPaymentMethod(order.getPaymentMethod());
            orderDTO.setTotalPrice(order.getTotalPrice());

            List<ProductInfoInOrderDTO> productInfoDTOs = order.getOrderItems().stream().map(
                    productInfo -> ProductInfoInOrderDTO.builder()
                            .name(productInfo.getProduct().getName())
                            .rom(productInfo.getRom())
                            .color(productInfo.getProduct().getColor())
                            .price(productInfo.getPrice())
                            .quantity(productInfo.getQuantity())
                            .build()).toList();
            orderDTO.setProductInfos(productInfoDTOs);
            model.addAttribute("currentPage", "orders");
            return ResponseEntity.ok(orderDTO);
        }
        else{
            model.addAttribute("errorMessage", "Không tìm thấy sản phẩm");
            model.addAttribute("currentPage", "orders");
            return ResponseEntity.notFound().build();
        }


    }

    // Sửa trạng thái đơn hàng
    @PostMapping("/update")
    String updateOrderStatus(@Valid @ModelAttribute("request") OrderUpdateRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        // Kiểm tra mã đơn hàng có null không
        if(bindingResult.hasFieldErrors("orderId")){
            redirectAttributes.addFlashAttribute("errorMessage", "Không nhận được mã đơn hàng");
            return "redirect:/management/orders"; // Chuyển hướng về trang danh sách sản phẩm
        }

        // Kiểm tra trạng thái đơn hàng có null không
        if(bindingResult.hasFieldErrors("status")){
            redirectAttributes.addFlashAttribute("errorMessage", "Không nhận được thông tin trạng thái");
            return "redirect:/management/orders"; // Chuyển hướng về trang danh sách sản phẩm
        }

        try {
            orderService.updateOrderStatus(request);
            // Thêm thông báo thành công vào FlashAttributes
            redirectAttributes.addFlashAttribute("successMessage", "Sửa thông tin trạng thái đơn hàng thành công!");
            return "redirect:/management/orders"; // Chuyển hướng về trang danh sách sản phẩm
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi sửa trạng thái đơn hàng!");
            System.out.println("Lỗi khi sửa trạng thái đơn hàng: "  + ex.getMessage());
            return "redirect:/management/orders"; // Chuyển hướng về trang danh sách sản phẩm
        }
    }

    // Hủy đơn hàng
    @PostMapping("/cancel")
    String cancelOrder(@RequestParam Long orderId, RedirectAttributes redirectAttributes, Model model){
        Optional<Order> orderOptionalt = orderService.findById(orderId);
        if(orderOptionalt.isPresent()) {
            Order order = orderOptionalt.get();
            if(order.getOrderStatus().equals("Đã hoàn thành")){
                redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng đã hoàn thành, không thể hủy");
                return "redirect:/management/orders";
            }

            orderService.cancelOrder(order);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy đơn hàng thành công!");

        }
        else{
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm");
        }
        return "redirect:/management/orders"; // Chuyển hướng về trang danh sách sản phẩm
    }

    // Xuất PDF
    @GetMapping("/export-pdf/{orderId}")
    public void exportOrderToPdf(@PathVariable Long orderId, HttpServletResponse response) throws Exception {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Chuẩn bị dữ liệu
        Context context = new Context();
        context.setVariable("order", order);

        // Render HTML từ Thymeleaf template
        String htmlContent = templateEngine.process("PDFExport/order-detail", context);

        // Cấu hình response
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=order_" + orderId + ".pdf");

        // Dùng Flying Saucer để xuất PDF
        ITextRenderer renderer = new ITextRenderer();

        // Đăng ký font với đường dẫn tuyệt đối
        String fontPath = getClass().getResource("/static/assets/fonts/DejaVuSerif.ttf").toURI().getPath();
        renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(response.getOutputStream());
    }

    // Đếm số lượng đơn hàng chưa hoàn thành
    @GetMapping("/pending-count")
    @ResponseBody
    public ResponseEntity<Integer> getPendingOrdersCount(){
        int count = orderService.countPendingOrders();
        return ResponseEntity.ok(count);
    }

}
