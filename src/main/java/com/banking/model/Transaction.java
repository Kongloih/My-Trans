package com.banking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 交易实体类
 * 
 * @author Kongloih Zhang F
 */
@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_number", nullable = false, length = 50)
    @NotBlank(message = "Account number cannot be empty")
    @Size(min = 10, max = 20, message = "Account number length must be between 10-20 characters")
    private String accountNumber;
    
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    @NotNull(message = "Transaction amount cannot be null")
    @DecimalMin(value = "0.01", message = "Transaction amount must be greater than 0")
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trans_type", nullable = false, length = 20)
    @NotNull(message = "Transaction type cannot be null")
    private TransactionType transType;
    
    @Column(name = "unit", nullable = false, precision = 19, scale = 0)
    @NotNull(message = "Unit cannot be null")
    @DecimalMin(value = "1", message = "Unit must be greater than 0")
    private Long unit;
    
    @Column(name = "price", nullable = false, precision = 19, scale = 4)
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Column(name = "trans_date", nullable = false)
    @NotNull(message = "Transaction date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transDate;
    
    @Column(name = "security_code", nullable = false, length = 20)
    @NotBlank(message = "Security code cannot be empty")
    @Size(min = 1, max = 20, message = "Security code length must be between 1-20 characters")
    private String securityCode;
    
    @Column(name = "description", length = 500)
    @Size(max = 255, message = "Description length cannot exceed 255 characters")
    private String description;
    
    @Column(name = "timestamp", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    @Column(name = "currency", nullable = false, length = 10)
    @NotBlank(message = "Currency cannot be empty")
    @Size(min = 3, max = 3, message = "Currency code must be 3 characters")
    private String currency;
    
    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance; // Balance after transaction (kept for compatibility)
    
    // Constructors
    public Transaction() {
        this.timestamp = LocalDateTime.now();
        this.transDate = LocalDate.now();
        this.currency = "CNY"; // Default to Chinese Yuan
    }
    
    public Transaction(Long id, String accountNumber, BigDecimal amount, 
                      TransactionType transType, String description,
                      Long unit, BigDecimal price, String securityCode) {
        this();
        this.id = id;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transType = transType;
        this.description = description;
        this.unit = unit;
        this.price = price;
        this.securityCode = securityCode;
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
    
    // Deprecated methods for backward compatibility
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", transType=" + transType +
                ", unit=" + unit +
                ", price=" + price +
                ", transDate=" + transDate +
                ", securityCode='" + securityCode + '\'' +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                ", currency='" + currency + '\'' +
                ", balance=" + balance +
                '}';
    }
} 