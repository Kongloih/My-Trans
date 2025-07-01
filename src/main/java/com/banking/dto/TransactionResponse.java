package com.banking.dto;

import com.banking.model.Transaction;
import com.banking.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 交易响应DTO
 * 
 * @author Kongloih Zhang F
 */
public class TransactionResponse {
    
    private Long id;
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType transType;
    private Long unit;
    private BigDecimal price;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transDate;
    
    private String securityCode;
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String currency;
    private BigDecimal balance;
    
    // 构造函数
    public TransactionResponse() {}
    
    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.accountNumber = transaction.getAccountNumber();
        this.amount = transaction.getAmount();
        this.transType = transaction.getTransType();
        this.unit = transaction.getUnit();
        this.price = transaction.getPrice();
        this.transDate = transaction.getTransDate();
        this.securityCode = transaction.getSecurityCode();
        this.description = transaction.getDescription();
        this.timestamp = transaction.getTimestamp();
        this.currency = transaction.getCurrency();
        this.balance = transaction.getBalance();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionType getTransType() {
        return transType;
    }
    
    public void setTransType(TransactionType transType) {
        this.transType = transType;
    }
    
    // 为了向后兼容，保留type的getter和setter
    @Deprecated
    public TransactionType getType() {
        return transType;
    }
    
    @Deprecated
    public void setType(TransactionType type) {
        this.transType = type;
    }
    
    public Long getUnit() {
        return unit;
    }
    
    public void setUnit(Long unit) {
        this.unit = unit;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public LocalDate getTransDate() {
        return transDate;
    }
    
    public void setTransDate(LocalDate transDate) {
        this.transDate = transDate;
    }
    
    public String getSecurityCode() {
        return securityCode;
    }
    
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
} 