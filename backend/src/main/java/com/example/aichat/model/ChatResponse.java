package com.example.aichat.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ChatResponse {
    private String message;
} 