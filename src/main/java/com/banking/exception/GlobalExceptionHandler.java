package com.banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * @author Kongloih Zhang F
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理交易未找到异常
     * 
     * @param ex 异常
     * @return 错误响应
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionNotFound(TransactionNotFoundException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                "TRANSACTION_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * 处理重复交易异常
     * 
     * @param ex 异常
     * @return 错误响应
     */
    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateTransaction(DuplicateTransactionException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                "DUPLICATE_TRANSACTION",
                ex.getMessage(),
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * 处理参数验证异常
     * 
     * @param ex 异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        
        String errorMessage = bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        Map<String, String> fieldErrors = bindingResult.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));
        
        Map<String, Object> errorResponse = createErrorResponse(
                "VALIDATION_ERROR",
                "输入参数验证失败: " + errorMessage,
                HttpStatus.BAD_REQUEST
        );
        errorResponse.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * 处理非法参数异常
     * 
     * @param ex 异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * 处理数字格式异常
     * 
     * @param ex 异常
     * @return 错误响应
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Map<String, Object>> handleNumberFormat(NumberFormatException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                "INVALID_NUMBER_FORMAT",
                "数字格式错误: " + ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * 处理运行时异常
     * 
     * @param ex 异常
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                "RUNTIME_ERROR",
                "运行时错误: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * 处理通用异常
     * 
     * @param ex 异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                "INTERNAL_ERROR",
                "系统内部错误，请联系管理员",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        
        // 在开发环境可以显示详细错误信息
        errorResponse.put("details", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * 创建错误响应对象
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param status HTTP状态
     * @return 错误响应
     */
    private Map<String, Object> createErrorResponse(String errorCode, String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        return errorResponse;
    }
} 