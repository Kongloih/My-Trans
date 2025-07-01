package com.banking.service;

import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import com.banking.exception.TransactionNotFoundException;
import com.banking.exception.DuplicateTransactionException;
import com.banking.model.Transaction;
import com.banking.model.TransactionType;
import com.banking.repository.TransactionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 股票交易业务服务层
 * 
 * @author Kongloih Zhang F
 */
@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;

    
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    /**
     * 创建新股票交易
     * 
     * @param request 交易请求
     * @return 交易响应
     */
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionResponse createTransaction(TransactionRequest request) {
        validateTransactionRequest(request);
        
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(request.getAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setTransType(request.getTransType());
        transaction.setUnit(request.getUnit());
        transaction.setPrice(request.getPrice());
        transaction.setTransDate(request.getTransDate() != null ? request.getTransDate() : LocalDate.now());
        transaction.setSecurityCode(request.getSecurityCode());
        transaction.setDescription(request.getDescription());
        transaction.setCurrency(request.getCurrency());
        transaction.setTimestamp(LocalDateTime.now());
        
        // 验证价格和金额的一致性：amount = unit * price
        BigDecimal calculatedAmount = BigDecimal.valueOf(transaction.getUnit()).multiply(transaction.getPrice());
        if (transaction.getAmount().compareTo(calculatedAmount) != 0) {
            throw new IllegalArgumentException("交易金额必须等于股数 * 价格");
        }
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return new TransactionResponse(savedTransaction);
    }
    
    /**
     * 更新交易
     * 
     * @param id 交易ID
     * @param request 交易请求
     * @return 交易响应
     */
    @CachePut(value = "transactions", key = "#id")
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("交易ID " + id + " 不存在"));
        
        validateTransactionRequest(request);
        
        // 更新交易信息
        existingTransaction.setAccountNumber(request.getAccountNumber());
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setTransType(request.getTransType());
        existingTransaction.setUnit(request.getUnit());
        existingTransaction.setPrice(request.getPrice());
        existingTransaction.setTransDate(request.getTransDate() != null ? request.getTransDate() : LocalDate.now());
        existingTransaction.setSecurityCode(request.getSecurityCode());
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setCurrency(request.getCurrency());
        
        // 验证价格和金额的一致性
        BigDecimal calculatedAmount = BigDecimal.valueOf(existingTransaction.getUnit()).multiply(existingTransaction.getPrice());
        if (existingTransaction.getAmount().compareTo(calculatedAmount) != 0) {
            throw new IllegalArgumentException("交易金额必须等于股数 * 价格");
        }
        
        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        return new TransactionResponse(updatedTransaction);
    }
    
    /**
     * 获取交易详情
     * 
     * @param id 交易ID
     * @return 交易响应
     */
    @Cacheable(value = "transactions", key = "#id")
    public TransactionResponse getTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("交易ID " + id + " 不存在"));
        return new TransactionResponse(transaction);
    }
    
    /**
     * 获取所有交易
     * 
     * @return 交易响应列表
     */
    @Cacheable(value = "transactions")
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页获取交易
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 交易响应列表
     */
    public List<TransactionResponse> getTransactions(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("页码必须非负，每页大小必须大于0");
        }
        
        return transactionRepository.findAll(page, size)
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据账户号码获取交易
     * 
     * @param accountNumber 账户号码
     * @return 交易响应列表
     */
    public List<TransactionResponse> getTransactionsByAccount(String accountNumber) {
        if (StringUtils.isBlank(accountNumber)) {
            throw new IllegalArgumentException("账户号码不能为空");
        }
        
        return transactionRepository.findByAccountNumber(accountNumber)
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据交易类型获取交易
     * 
     * @param type 交易类型
     * @return 交易响应列表
     */
    public List<TransactionResponse> getTransactionsByType(TransactionType type) {
        return transactionRepository.findByType(type)
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * 删除交易
     * 
     * @param id 交易ID
     */
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("交易ID " + id + " 不存在"));
        
        transactionRepository.deleteById(id);
    }
    
    /**
     * 获取股票交易统计
     * 计算同一个客户账户当天成交的股票股数和成交总金额，按照买入/卖出累加，按照股票代码分组
     * 
     * @param securityCode 股票代码
     * @param accountNumber 账户号码
     * @return 股票交易统计
     */
    @Cacheable(value = "transactions", key = "'security_stats_' + #securityCode + '_' + #accountNumber")
    public Map<String, Object> getSecurityStatistics(String securityCode, String accountNumber) {
        if (StringUtils.isBlank(securityCode)) {
            throw new IllegalArgumentException("股票代码不能为空");
        }
        if (StringUtils.isBlank(accountNumber)) {
            throw new IllegalArgumentException("账户号码不能为空");
        }
        
        LocalDate today = LocalDate.now();
        List<Transaction> todayTransactions = transactionRepository.findAll()
                .stream()
                .filter(t -> t.getSecurityCode().equals(securityCode))
                .filter(t -> t.getAccountNumber().equals(accountNumber))
                .filter(t -> t.getTransDate().equals(today))
                .collect(Collectors.toList());
        
        // 按买入/卖出分组计算
        Map<TransactionType, List<Transaction>> groupedByType = todayTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTransType));
        
        Map<String, Object> statistics = new ConcurrentHashMap<>();
        statistics.put("securityCode", securityCode);
        statistics.put("accountNumber", accountNumber);
        statistics.put("transDate", today);
        
        // 买入统计
        List<Transaction> buyTransactions = groupedByType.getOrDefault(TransactionType.BUY, List.of());
        long totalBuyUnits = buyTransactions.stream().mapToLong(Transaction::getUnit).sum();
        BigDecimal totalBuyAmount = buyTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        statistics.put("buyStatistics", Map.of(
                "totalUnits", totalBuyUnits,
                "totalAmount", totalBuyAmount,
                "transactionCount", buyTransactions.size(),
                "averagePrice", totalBuyUnits > 0 ? totalBuyAmount.divide(BigDecimal.valueOf(totalBuyUnits), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO
        ));
        
        // 卖出统计
        List<Transaction> sellTransactions = groupedByType.getOrDefault(TransactionType.SELL, List.of());
        long totalSellUnits = sellTransactions.stream().mapToLong(Transaction::getUnit).sum();
        BigDecimal totalSellAmount = sellTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        statistics.put("sellStatistics", Map.of(
                "totalUnits", totalSellUnits,
                "totalAmount", totalSellAmount,
                "transactionCount", sellTransactions.size(),
                "averagePrice", totalSellUnits > 0 ? totalSellAmount.divide(BigDecimal.valueOf(totalSellUnits), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO
        ));
        
        // 净持仓统计
        long netUnits = totalBuyUnits - totalSellUnits;
        BigDecimal netAmount = totalBuyAmount.subtract(totalSellAmount);
        
        statistics.put("netPosition", Map.of(
                "netUnits", netUnits,
                "netAmount", netAmount,
                "totalTransactions", todayTransactions.size()
        ));
        
        return statistics;
    }
    
    /**
     * 获取交易总数
     * 
     * @return 交易总数
     */
    public long getTransactionCount() {
        return transactionRepository.count();
    }
    
    /**
     * 验证交易请求
     * 
     * @param request 交易请求
     */
    private void validateTransactionRequest(TransactionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("交易请求不能为空");
        }
        
        if (StringUtils.isBlank(request.getAccountNumber())) {
            throw new IllegalArgumentException("账户号码不能为空");
        }
        
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("交易金额必须大于0");
        }
        
        if (request.getTransType() == null) {
            throw new IllegalArgumentException("交易类型不能为空");
        }
        
        if (request.getUnit() == null || request.getUnit() <= 0) {
            throw new IllegalArgumentException("成交股数必须大于0");
        }
        
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("成交价格必须大于0");
        }
        
        if (StringUtils.isBlank(request.getSecurityCode())) {
            throw new IllegalArgumentException("股票代码不能为空");
        }
        
        if (StringUtils.isBlank(request.getCurrency())) {
            request.setCurrency("CNY");
        }
        
        // 验证交易类型只能是买入或卖出
        if (request.getTransType() != TransactionType.BUY && request.getTransType() != TransactionType.SELL) {
            throw new IllegalArgumentException("交易类型只能是买入(BUY)或卖出(SELL)");
        }
    }
    

} 