package com.example.aichat.model;

import lombok.Data;

@Data
public class SubjectSummaryRow {
    private String name;
    private String beginBalance;
    private String currentDebit;
    private String currentCredit;
    private String endBalance;
} 