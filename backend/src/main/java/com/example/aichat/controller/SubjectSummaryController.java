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

        SubjectSummaryRow row2 = new SubjectSummaryRow();
        row2.setName("银行存款");
        row2.setBeginBalance("20000.00");
        row2.setCurrentDebit("8000.00");
        row2.setCurrentCredit("5000.00");
        row2.setEndBalance("23000.00");
        list.add(row2);

        SubjectSummaryRow row3 = new SubjectSummaryRow();
        row3.setName("其他货币资金");
        row3.setBeginBalance("500.00");
        row3.setCurrentDebit("100.00");
        row3.setCurrentCredit("50.00");
        row3.setEndBalance("550.00");
        list.add(row3);

        SubjectSummaryRow row4 = new SubjectSummaryRow();
        row4.setName("应收账款");
        row4.setBeginBalance("3000.00");
        row4.setCurrentDebit("1200.00");
        row4.setCurrentCredit("800.00");
        row4.setEndBalance("3400.00");
        list.add(row4);

        SubjectSummaryRow row5 = new SubjectSummaryRow();
        row5.setName("材料采购");
        row5.setBeginBalance("0.00");
        row5.setCurrentDebit("200.00");
        row5.setCurrentCredit("0.00");
        row5.setEndBalance("200.00");
        list.add(row5);

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