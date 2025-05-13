package com.example.aichat.model;

public class ProfitRow {
    private String item;
    private String index;
    private String currentAmount;
    private String lastAmount;
    private Boolean bold;
    private Boolean highlight;

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public String getIndex() { return index; }
    public void setIndex(String index) { this.index = index; }
    public String getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(String currentAmount) { this.currentAmount = currentAmount; }
    public String getLastAmount() { return lastAmount; }
    public void setLastAmount(String lastAmount) { this.lastAmount = lastAmount; }
    public Boolean getBold() { return bold; }
    public void setBold(Boolean bold) { this.bold = bold; }
    public Boolean getHighlight() { return highlight; }
    public void setHighlight(Boolean highlight) { this.highlight = highlight; }
} 