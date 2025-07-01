package com.banking.exception;

/**
 * 交易未找到异常
 * 
 * @author Kongloih Zhang F
 */
public class TransactionNotFoundException extends RuntimeException {
    
    public TransactionNotFoundException(String message) {
        super(message);
    }
    
    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 