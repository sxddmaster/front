package com.example.aichat.controller;

import com.example.aichat.model.SubjectSummaryRow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/subject-summary")
public class SubjectSummaryController {
    @GetMapping
    public Map<String, Object> getSubjectSummary() {
        List<SubjectSummaryRow> list = new ArrayList<>();
        SubjectSummaryRow row1 = new SubjectSummaryRow();
        row1.setName("库存现金");
        row1.setBeginBalance("1000.00");
        row1.setCurrentDebit("500.00");
        row1.setCurrentCredit("200.00");
        row1.setEndBalance("1300.00");
        list.add(row1);



        SubjectSummaryRow total = new SubjectSummaryRow();
        total.setName("合计");
        total.setBeginBalance("24500.00");
        total.setCurrentDebit("10000.00");
        total.setCurrentCredit("6050.00");
        total.setEndBalance("28450.00");

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }
} 