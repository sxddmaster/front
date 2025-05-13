package com.example.aichat.controller;

import com.example.aichat.model.ChatRequest;
import com.example.aichat.model.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // 允许前端开发服务器访问
public class ChatController {

    @PostMapping(value = "/chat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ChatResponse chat(
            @RequestParam(required = false) String message,
            @RequestParam(required = false) List<MultipartFile> files) {
        
        ChatRequest request = new ChatRequest();
        request.setMessage(message);
        request.setFiles(files);

        return ChatResponse.builder().message("模拟大模型调用返回结果").build();
//        return chatService.processChat(request);
    }
} 