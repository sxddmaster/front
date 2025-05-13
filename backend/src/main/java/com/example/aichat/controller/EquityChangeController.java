package com.example.aichat.controller;

import com.example.aichat.model.EquityChangeRow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/equity-change")
public class EquityChangeController {
    @GetMapping
    public Map<String, Object> getEquityChange() {
        List<EquityChangeRow> list = new ArrayList<>();

        EquityChangeRow row1 = new EquityChangeRow();
        row1.setItem("期初余额");
        row1.setRowNo("1");
        row1.setCurrentPaidInCapital("10000");
        row1.setCurrentCapitalReserve("2000");
        row1.setCurrentLessTreasuryStock("0");
        row1.setCurrentSurplusReserve("1500");
        row1.setCurrentUndistributedProfit("3000");
        row1.setCurrentTotalEquity("16500");
        row1.setLastPaidInCapital("9000");
        row1.setLastCapitalReserve("1800");
        row1.setLastLessTreasuryStock("0");
        row1.setLastSurplusReserve("1200");
        row1.setLastUndistributedProfit("2500");
        row1.setLastTotalEquity("14500");
        list.add(row1);

        EquityChangeRow row2 = new EquityChangeRow();
        row2.setItem("本年增加");
        row2.setRowNo("2");
        row2.setCurrentPaidInCapital("500");
        row2.setCurrentCapitalReserve("100");
        row2.setCurrentLessTreasuryStock("0");
        row2.setCurrentSurplusReserve("200");
        row2.setCurrentUndistributedProfit("400");
        row2.setCurrentTotalEquity("1200");
        row2.setLastPaidInCapital("400");
        row2.setLastCapitalReserve("80");
        row2.setLastLessTreasuryStock("0");
        row2.setLastSurplusReserve("150");
        row2.setLastUndistributedProfit("300");
        row2.setLastTotalEquity("930");
        list.add(row2);

        EquityChangeRow row3 = new EquityChangeRow();
        row3.setItem("本年减少");
        row3.setRowNo("3");
        row3.setCurrentPaidInCapital("0");
        row3.setCurrentCapitalReserve("0");
        row3.setCurrentLessTreasuryStock("100");
        row3.setCurrentSurplusReserve("0");
        row3.setCurrentUndistributedProfit("200");
        row3.setCurrentTotalEquity("-300");
        row3.setLastPaidInCapital("0");
        row3.setLastCapitalReserve("0");
        row3.setLastLessTreasuryStock("80");
        row3.setLastSurplusReserve("0");
        row3.setLastUndistributedProfit("100");
        row3.setLastTotalEquity("-180");
        list.add(row3);

        EquityChangeRow row4 = new EquityChangeRow();
        row4.setItem("期末余额");
        row4.setRowNo("4");
        row4.setCurrentPaidInCapital("10500");
        row4.setCurrentCapitalReserve("2100");
        row4.setCurrentLessTreasuryStock("-100");
        row4.setCurrentSurplusReserve("1700");
        row4.setCurrentUndistributedProfit("3200");
        row4.setCurrentTotalEquity("18400");
        row4.setLastPaidInCapital("9400");
        row4.setLastCapitalReserve("1880");
        row4.setLastLessTreasuryStock("-80");
        row4.setLastSurplusReserve("1350");
        row4.setLastUndistributedProfit("2700");
        row4.setLastTotalEquity("16250");
        list.add(row4);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return result;
    }
} 