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
        list.add(createRow("应收票据", "4", "-", "-", "应付票据", "37", "-", "-"));
        list.add(createRow("应收账款", "5", "20.90", "20.90", "应付账款", "38", "35,023.67", "35,023.67"));
        list.add(createRow("预付款项", "6", "6,727,976.00", "9,308,834.56", "预收款项", "39", "15,352,000.00", "15,100,000.00"));
        list.add(createRow("应收利息", "7", "-", "-", "应付职工薪酬", "40", "51,527.42", "50,845.42"));
        list.add(createRow("应收股利", "8", "-", "-", "应交税费", "41", "-290,773.91", "-370,995.21"));
        list.add(createRow("其他应收款", "9", "6,167.39", "-", "应付利息", "42", "-", "-"));
        list.add(createRow("存货", "10", "-", "-", "应付股利", "43", "-", "-"));
        list.add(createRow("一年内到期的非流动资产", "11", "-", "-", "其他应付款", "44", "51,365.39", "283.39"));
        list.add(createRow("其他流动资产", "12", "-", "-", "一年内到期的非流动负债", "45", "-", "-"));
        list.add(createRow("流动资产合计（2+...+12）", "13", "9,873,736.00", "22,136,674.01", "流动负债合计（35+...+46）", "47", "23,299,142.57", "20,215,157.27"));
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return result;
    }
    private BalanceRow createRow(String assetItem, String assetIndex, String assetEnd, String assetBegin,
                                 String liabilityItem, String liabilityIndex, String liabilityEnd, String liabilityBegin) {
        BalanceRow row = new BalanceRow();
        row.setAssetItem(assetItem);
        row.setAssetIndex(assetIndex);
        row.setAssetEnd(assetEnd);
        row.setAssetBegin(assetBegin);
        row.setLiabilityItem(liabilityItem);
        row.setLiabilityIndex(liabilityIndex);
        row.setLiabilityEnd(liabilityEnd);
        row.setLiabilityBegin(liabilityBegin);
        return row;
    }
} 