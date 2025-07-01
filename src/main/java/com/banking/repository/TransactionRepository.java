package com.banking.repository;

import com.banking.model.Transaction;
import com.banking.model.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 股票交易数据访问层
 * 使用JPA和H2数据库存储
 * 
 * @author Kongloih Zhang F
 */
@Repository
public class TransactionRepository {
    
    private final TransactionJpaRepository jpaRepository;
    
    @Autowired
    public TransactionRepository(TransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    /**
     * 保存交易
     * 
     * @param transaction 交易对象
     * @return 保存后的交易对象
     */
    @CacheEvict(value = "transactions", allEntries = true)
    public Transaction save(Transaction transaction) {
        // 设置时间戳
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(LocalDateTime.now());
        }
        return jpaRepository.save(transaction);
    }
    
    /**
     * 根据ID查找交易
     * 
     * @param id 交易ID
     * @return 交易对象（如果存在）
     */
    @Cacheable(value = "transactions", key = "#id")
    public Optional<Transaction> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    /**
     * 查找所有交易
     * 
     * @return 所有交易列表
     */
    @Cacheable(value = "transactions")
    public List<Transaction> findAll() {
        return jpaRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
    
    /**
     * 分页查询交易
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 交易列表
     */
    public List<Transaction> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<Transaction> pageResult = jpaRepository.findAll(pageable);
        return pageResult.getContent();
    }
    
    /**
     * 根据账户号码查找交易
     * 
     * @param accountNumber 账户号码
     * @return 交易列表
     */
    public List<Transaction> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
    }
    
    /**
     * 根据交易类型查找交易
     * 
     * @param type 交易类型
     * @return 交易列表
     */
    public List<Transaction> findByType(TransactionType type) {
        return jpaRepository.findByTransTypeOrderByTimestampDesc(type);
    }
    
    /**
     * 根据账户号码和交易类型查找交易
     * 
     * @param accountNumber 账户号码
     * @param type 交易类型
     * @return 交易列表
     */
    public List<Transaction> findByAccountNumberAndType(String accountNumber, TransactionType type) {
        return jpaRepository.findByAccountNumberAndTransTypeOrderByTimestampDesc(accountNumber, type);
    }
    
    /**
     * 删除交易
     * 
     * @param id 交易ID
     * @return 是否删除成功
     */
    @CacheEvict(value = "transactions", allEntries = true)
    public boolean deleteById(Long id) {
        if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * 检查交易是否存在
     * 
     * @param id 交易ID
     * @return 是否存在
     */
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
    
    /**
     * 获取交易总数
     * 
     * @return 交易总数
     */
    public long count() {
        return jpaRepository.count();
    }
    
    /**
     * 清空所有交易（用于测试）
     */
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
    

    
    /**
     * 根据时间范围查找交易
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 交易列表
     */
    public List<Transaction> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return jpaRepository.findByTimestampBetweenOrderByTimestampDesc(startTime, endTime);
    }
    
    /**
     * 根据金额范围查找交易
     * 
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 交易列表
     */
    public List<Transaction> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        return jpaRepository.findByAmountBetweenOrderByTimestampDesc(minAmount, maxAmount);
    }
    
    /**
     * 查找大额交易
     * 
     * @param threshold 金额阈值
     * @return 交易列表
     */
    public List<Transaction> findLargeTransactions(BigDecimal threshold) {
        return jpaRepository.findByAmountGreaterThanOrderByAmountDesc(threshold);
    }
    
    /**
     * 根据描述模糊查询
     * 
     * @param keyword 关键词
     * @return 交易列表
     */
    public List<Transaction> findByDescriptionContaining(String keyword) {
        return jpaRepository.findByDescriptionContainingIgnoreCaseOrderByTimestampDesc(keyword);
    }
    
    /**
     * 获取账户交易数量
     * 
     * @param accountNumber 账户号码
     * @return 交易数量
     */
    public Long countByAccountNumber(String accountNumber) {
        return jpaRepository.countByAccountNumber(accountNumber);
    }
    
    /**
     * 获取指定时间段内的交易统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 交易数量
     */
    public Long countTransactionsBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return jpaRepository.countTransactionsBetween(startTime, endTime);
    }
} 