package com.banking.dto;

import com.banking.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 交易请求DTO
 * 
 * @author Kongloih Zhang F
 */
public class TransactionRequest {
    
    @NotBlank(message = "账户号码不能为空")
    @Size(min = 10, max = 20, message = "账户号码长度必须在10-20位之间")
    private String accountNumber;
    
    @NotNull(message = "交易金额不能为空")
    @DecimalMin(value = "0.01", message = "交易金额必须大于0")
    private BigDecimal amount;
    
    @NotNull(message = "交易类型不能为空")
    private TransactionType transType;
    
    @NotNull(message = "成交股数不能为空")
    @DecimalMin(value = "1", message = "成交股数必须大于0")
    private Long unit;
    
    @NotNull(message = "成交价格不能为空")
    @DecimalMin(value = "0.01", message = "成交价格必须大于0")
    private BigDecimal price;
    
    @NotNull(message = "成交日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transDate;
    
    @NotBlank(message = "股票代码不能为空")
    @Size(min = 1, max = 20, message = "股票代码长度必须在1-20位之间")
    private String securityCode;
    
    @Size(max = 255, message = "描述长度不能超过255个字符")
    private String description;
    
    @Size(min = 3, max = 3, message = "货币代码必须是3位")
    private String currency = "CNY";
    
    // 构造函数
    public TransactionRequest() {
        this.transDate = LocalDate.now();
    }
    
    public TransactionRequest(String accountNumber, BigDecimal amount, 
                             TransactionType transType, String description,
                             Long unit, BigDecimal price, String securityCode) {
        this();
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transType = transType;
        this.description = description;
        this.unit = unit;
        this.price = price;
        this.securityCode = securityCode;
    }
    
    // Getters and Setters
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
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
} 