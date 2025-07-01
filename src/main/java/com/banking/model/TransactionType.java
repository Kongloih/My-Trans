package com.banking.model;

/**
 * 交易类型枚举
 * 
 * @author Kongloih Zhang F
 */
public enum TransactionType {
    
    BUY("买入", "BUY"),
    SELL("卖出", "SELL");
    
    private final String chineseName;
    private final String englishName;
    
    TransactionType(String chineseName, String englishName) {
        this.chineseName = chineseName;
        this.englishName = englishName;
    }
    
    public String getChineseName() {
        return chineseName;
    }
    
    public String getEnglishName() {
        return englishName;
    }
    
    @Override
    public String toString() {
        return this.chineseName + " (" + this.englishName + ")";
    }
} 