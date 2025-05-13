package com.example.aichat.controller;

import com.example.aichat.model.VoucherRow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class VoucherController {
    @GetMapping("/voucher/list")
    public List<VoucherRow> getVoucherList() {
        List<VoucherRow> list = new ArrayList<>();
        VoucherRow row = new VoucherRow();
        row.setDate("2025.1.2");
        row.setVoucherNo("1");
        row.setSummary("预收深圳市鲲鹏信息服务费");
        row.setSubject("预收账款");
        row.setDetailSubject("深圳市鲲鹏快付科技有限公司");
        row.setDebit("");
        row.setCredit("200,003.47");
        row.setBalance("15,300,003.47");
        list.add(row);
        return list;
    }
} 