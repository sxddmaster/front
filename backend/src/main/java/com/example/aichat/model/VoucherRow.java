package com.example.aichat.model;

import lombok.Data;

@Data
public class VoucherRow {
    private String date;
    private String voucherNo;
    private String summary;
    private String subject;
    private String detailSubject;
    private String debit;
    private String credit;
    private String balance;
} 