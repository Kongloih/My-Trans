package com.banking.exception;

/**
 * 重复交易异常
 * 
 * @author Kongloih Zhang F
 */
public class DuplicateTransactionException extends RuntimeException {
    
    public DuplicateTransactionException(String message) {
        super(message);
    }
    
    public DuplicateTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
} 