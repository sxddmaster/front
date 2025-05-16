package com.example.aichat.controller;

import com.example.aichat.model.BalanceRow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
public class BalanceSheetController {
    @GetMapping("/balance-sheet")
    public Map<String, Object> getBalanceSheet() {
        List<BalanceRow> list = new ArrayList<>();
        // 资产部分
        list.add(createRow("流动资产：", "1", "", ""));
        list.add(createRow("货币资金", "2", "3,139,571.71", "2,827,818.55"));
        list.add(createRow("以公允价值计量且变动计入当期损益的金融资产", "3", "10,000,000.00", "-"));
        list.add(createRow("应收票据", "4", "-", "-"));
        list.add(createRow("应收账款", "5", "20.90", "20.90"));
        list.add(createRow("预付款项", "6", "6,727,976.00", "9,308,834.56"));
        list.add(createRow("应收利息", "7", "-", "-"));
        list.add(createRow("应收股利", "8", "-", "-"));
        list.add(createRow("其他应收款", "9", "6,167.39", "-"));
        list.add(createRow("存货", "10", "-", "-"));
        list.add(createRow("一年内到期的非流动资产", "11", "-", "-"));
        list.add(createRow("其他流动资产", "12", "-", "-"));
        list.add(createRow("流动资产合计（2+...+12）", "13", "9,873,736.00", "22,136,674.01"));
        // ... 可继续添加非流动资产等
        // 负债和所有者权益部分
        list.add(createRow("流动负债：", "34", "", ""));
        list.add(createRow("短期借款", "35", "8,100,000.00", "5,400,000.00"));
        list.add(createRow("以公允价值计量且变动计入当期损益的金融负债", "36", "-", "-"));
        list.add(createRow("应付票据", "37", "-", "-"));
        list.add(createRow("应付账款", "38", "35,023.67", "35,023.67"));
        list.add(createRow("预收款项", "39", "15,352,000.00", "15,100,000.00"));
        list.add(createRow("应付职工薪酬", "40", "51,527.42", "50,845.42"));
        list.add(createRow("应交税费", "41", "-290,773.91", "-370,995.21"));
        list.add(createRow("应付利息", "42", "-", "-"));
        list.add(createRow("应付股利", "43", "-", "-"));
        list.add(createRow("其他应付款", "44", "51,365.39", "283.39"));
        // ... 可继续添加其他负债和所有者权益
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return result;
    }
    private BalanceRow createRow(String item, String index, String end, String begin) {
        BalanceRow row = new BalanceRow();
        row.setItem(item);
        row.setIndex(index);
        row.setEnd(end);
        row.setBegin(begin);
        return row;
    }
} 