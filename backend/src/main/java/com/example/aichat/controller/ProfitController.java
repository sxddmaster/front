package com.example.aichat.controller;

import com.example.aichat.model.ProfitRow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
public class ProfitController {
    @GetMapping("/profit")
    public Map<String, Object> getProfit() {
        List<ProfitRow> list = new ArrayList<>();
        list.add(createRow("一、营业收入", "1", "8,448,545.52", "", true, true));
        list.add(createRow("减：营业成本", "2", "6,581,113.38", "", false, false));
        list.add(createRow("税金及附加", "3", "-", "", false, false));
        list.add(createRow("销售费用", "4", "3,000.00", "", false, false));
        list.add(createRow("管理费用", "5", "60,174.84", "", false, false));
        list.add(createRow("财务费用", "6", "14,719.50", "", false, false));
        list.add(createRow("资产减值损失", "7", "-", "", false, false));
        list.add(createRow("加：公允价值变动净收益（净损失以'-'号填列）", "8", "-", "", false, false));
        list.add(createRow("投资净收益（净损失以'-'号填列）", "9", "17,081.79", "", false, false));
        list.add(createRow("其中：对联营企业和合营企业的投资收益", "10", "-", "", false, false));
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return result;
    }
    private ProfitRow createRow(String item, String index, String currentAmount, String lastAmount, boolean bold, boolean highlight) {
        ProfitRow row = new ProfitRow();
        row.setItem(item);
        row.setIndex(index);
        row.setCurrentAmount(currentAmount);
        row.setLastAmount(lastAmount);
        row.setBold(bold);
        row.setHighlight(highlight);
        return row;
    }
} 