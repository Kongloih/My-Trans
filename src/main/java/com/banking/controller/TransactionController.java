package com.banking.controller;

import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import com.banking.exception.TransactionNotFoundException;
import com.banking.exception.DuplicateTransactionException;
import com.banking.model.TransactionType;
import com.banking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易管理REST API控制器
 * 
 * @author Kongloih Zhang F
 */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "交易管理", description = "股票交易管理API")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    /**
     * 创建新交易
     * 
     * @param request 交易请求
     * @return 创建的交易响应
     */
    @PostMapping
    @Operation(summary = "创建交易", description = "创建一个新的股票交易")
    public ResponseEntity<Map<String, Object>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        
        TransactionResponse response = transactionService.createTransaction(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "交易创建成功");
        result.put("data", response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * 获取交易详情
     * 
     * @param id 交易ID
     * @return 交易详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取交易详情", description = "根据交易ID获取交易详细信息")
    public ResponseEntity<Map<String, Object>> getTransaction(
            @Parameter(description = "交易ID") @PathVariable Long id) {
        
        TransactionResponse response = transactionService.getTransaction(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "获取交易详情成功");
        result.put("data", response);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新交易
     * 
     * @param id 交易ID
     * @param request 交易请求
     * @return 更新后的交易响应
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新交易", description = "更新指定ID的交易信息")
    public ResponseEntity<Map<String, Object>> updateTransaction(
            @Parameter(description = "交易ID") @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        
        TransactionResponse response = transactionService.updateTransaction(id, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "交易更新成功");
        result.put("data", response);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除交易
     * 
     * @param id 交易ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除交易", description = "删除指定ID的交易")
    public ResponseEntity<Map<String, Object>> deleteTransaction(
            @Parameter(description = "交易ID") @PathVariable Long id) {
        
        transactionService.deleteTransaction(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "交易删除成功");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取所有交易（分页）
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 交易列表
     */
    @GetMapping
    @Operation(summary = "获取交易列表", description = "分页获取所有交易列表")
    public ResponseEntity<Map<String, Object>> getTransactions(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        List<TransactionResponse> transactions;
        long totalCount = transactionService.getTransactionCount();
        
        if (page == 0 && size == Integer.MAX_VALUE) {
            // 获取所有交易
            transactions = transactionService.getAllTransactions();
        } else {
            // 分页获取
            transactions = transactionService.getTransactions(page, size);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "获取交易列表成功");
        result.put("data", transactions);
        result.put("pagination", Map.of(
                "page", page,
                "size", size,
                "total", totalCount,
                "totalPages", (totalCount + size - 1) / size
        ));
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据账户号码获取交易
     * 
     * @param accountNumber 账户号码
     * @return 交易列表
     */
    @GetMapping("/account/{accountNumber}")
    @Operation(summary = "根据账户获取交易", description = "获取指定账户的所有交易")
    public ResponseEntity<Map<String, Object>> getTransactionsByAccount(
            @Parameter(description = "账户号码") @PathVariable String accountNumber) {
        
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccount(accountNumber);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "获取账户交易成功");
        result.put("data", transactions);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据交易类型获取交易
     * 
     * @param type 交易类型
     * @return 交易列表
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "根据类型获取交易", description = "获取指定类型的所有交易")
    public ResponseEntity<Map<String, Object>> getTransactionsByType(
            @Parameter(description = "交易类型") @PathVariable TransactionType type) {
        
        List<TransactionResponse> transactions = transactionService.getTransactionsByType(type);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "获取" + type.getChineseName() + "交易成功");
        result.put("data", transactions);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据股票代码获取交易统计
     * 
     * @param securityCode 股票代码
     * @param accountNumber 账户号码
     * @return 股票交易统计
     */
    @GetMapping("/security/{securityCode}/account/{accountNumber}/statistics")
    @Operation(summary = "获取股票交易统计", description = "获取指定股票和账户的当天交易统计")
    public ResponseEntity<Map<String, Object>> getSecurityStatistics(
            @Parameter(description = "股票代码") @PathVariable String securityCode,
            @Parameter(description = "账户号码") @PathVariable String accountNumber) {
        
        Map<String, Object> statistics = transactionService.getSecurityStatistics(securityCode, accountNumber);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "获取股票交易统计成功");
        result.put("data", statistics);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取交易统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取交易统计", description = "获取交易系统的统计信息")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        
        long totalTransactions = transactionService.getTransactionCount();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalTransactions", totalTransactions);
        statistics.put("transactionTypes", TransactionType.values());
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "获取统计信息成功");
        result.put("data", statistics);
        
        return ResponseEntity.ok(result);
    }
    
    // ==================== 异常处理器 ====================
    
    /**
     * 处理交易未找到异常
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionNotFound(TransactionNotFoundException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("errorCode", "TRANSACTION_NOT_FOUND");
        result.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }
    
    /**
     * 处理重复交易异常
     */
    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateTransaction(DuplicateTransactionException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("errorCode", "DUPLICATE_TRANSACTION");
        result.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("errorCode", "VALIDATION_ERROR");
        result.put("message", "参数校验失败");
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));
        result.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("errorCode", "INVALID_ARGUMENT");
        result.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
    
    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("errorCode", "INTERNAL_SERVER_ERROR");
        result.put("message", "服务器内部错误");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
} 