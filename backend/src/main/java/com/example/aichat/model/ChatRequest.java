package com.example.aichat.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ChatRequest {
    private String message;
    private List<MultipartFile> files;
} 