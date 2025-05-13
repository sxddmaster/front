package com.example.aichat.controller;

import com.example.aichat.model.CashFlowRow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CashFlowController {
    @GetMapping("/cash-flow")
    public Map<String, Object> getCashFlow() {
        List<CashFlowRow> list = new ArrayList<>();
        list.add(createRow("销售商品、提供劳务收到的现金", "1", "100000", "90000", false, false));
        list.add(createRow("收到的税费返还", "2", "5000", "4000", false, false));
        list.add(createRow("收到其他与经营活动有关的现金", "3", "2000", "1500", false, false));
        list.add(createRow("经营活动现金流入小计(2+3+4)", "5", "107000", "95500", true, true));
        list.add(createRow("购买商品、接受劳务支付的现金", "6", "60000", "55000", false, false));
        // ... 可继续补充
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return result;
    }
    private CashFlowRow createRow(String item, String index, String currentAmount, String lastAmount, boolean bold, boolean highlight) {
        CashFlowRow row = new CashFlowRow();
        row.setItem(item);
        row.setIndex(index);
        row.setCurrentAmount(currentAmount);
        row.setLastAmount(lastAmount);
        row.setBold(bold);
        row.setHighlight(highlight);
        return row;
    }
} 