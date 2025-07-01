package com.banking.repository;

import com.banking.model.Transaction;
import com.banking.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 股票交易JPA Repository接口
 * 
 * @author Kongloih Zhang F
 */
@Repository
public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * 根据账户号码查找交易
     * 
     * @param accountNumber 账户号码
     * @return 交易列表
     */
    List<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber);
    
    /**
     * 根据交易类型查找交易
     * 
     * @param type 交易类型
     * @return 交易列表
     */
    List<Transaction> findByTransTypeOrderByTimestampDesc(TransactionType type);
    
    /**
     * 根据时间范围查找交易
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 交易列表
     */
    List<Transaction> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据金额范围查找交易
     * 
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 交易列表
     */
    List<Transaction> findByAmountBetweenOrderByTimestampDesc(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * 根据账户号码和交易类型查找交易
     * 
     * @param accountNumber 账户号码
     * @param type 交易类型
     * @return 交易列表
     */
    List<Transaction> findByAccountNumberAndTransTypeOrderByTimestampDesc(String accountNumber, TransactionType type);
    

    
    /**
     * 获取账户最近的交易
     * 
     * @param accountNumber 账户号码
     * @param pageable 分页参数
     * @return 分页交易结果
     */
    Page<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber, Pageable pageable);
    
    /**
     * 获取指定时间段内的交易统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 交易数量
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.timestamp BETWEEN :startTime AND :endTime")
    Long countTransactionsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据描述模糊查询交易
     * 
     * @param description 描述关键词
     * @return 交易列表
     */
    List<Transaction> findByDescriptionContainingIgnoreCaseOrderByTimestampDesc(String description);
    
    /**
     * 查找大额交易（金额大于指定值）
     * 
     * @param amount 金额阈值
     * @return 交易列表
     */
    List<Transaction> findByAmountGreaterThanOrderByAmountDesc(BigDecimal amount);
    
    /**
     * 获取账户交易数量
     * 
     * @param accountNumber 账户号码
     * @return 交易数量
     */
    Long countByAccountNumber(String accountNumber);
} 