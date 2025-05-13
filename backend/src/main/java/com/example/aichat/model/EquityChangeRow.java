package com.example.aichat.model;

import lombok.Data;

@Data
public class EquityChangeRow {
    private String item;
    private String rowNo;
    private String currentPaidInCapital;
    private String currentCapitalReserve;
    private String currentLessTreasuryStock;
    private String currentSurplusReserve;
    private String currentUndistributedProfit;
    private String currentTotalEquity;
    private String lastPaidInCapital;
    private String lastCapitalReserve;
    private String lastLessTreasuryStock;
    private String lastSurplusReserve;
    private String lastUndistributedProfit;
    private String lastTotalEquity;
} 