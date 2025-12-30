package com.example.SellPhone.Controller.Sell;


import com.example.SellPhone.Service.OpenAIService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/chat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {

    private final OpenAIService chatService;

    @PostMapping
    public Map<String, Object> chat(@RequestBody Map<String, String> body) throws Exception {
        String question = body.get("question");
        String sessionId = body.get("sessionId");
        return chatService.processChat(sessionId, question);
    }
}
