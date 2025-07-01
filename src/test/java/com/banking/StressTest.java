package com.banking;

import com.banking.dto.TransactionRequest;
import com.banking.model.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * 股票交易系统压力测试
 * 测试系统在高并发场景下的性能和稳定性
 * 
 * @author Kongloih Zhang F
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StressTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final int CONCURRENT_USERS = 100;
    private static final int TRANSACTIONS_PER_USER = 10;
    private static final int TOTAL_TRANSACTIONS = CONCURRENT_USERS * TRANSACTIONS_PER_USER;
    
    // 测试用股票代码
    private static final String[] SECURITY_CODES = {
        "000001", "000002", "600036", "600519", "300015"
    };
    
    @BeforeEach
    void setUp() {
        // 每个测试开始前清理数据
        // 注意：在实际应用中，你可能需要使用 @DirtiesContext 或其他策略
        System.out.println("开始新的压力测试...");
    }
    
    @Test
    void testConcurrentStockTransactionCreation() throws Exception {
        // 记录测试开始前的交易数量
        String initialCountResponse = mockMvc.perform(get("/api/transactions/statistics"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        // 简单解析JSON获取初始交易数量（为了避免复杂的JSON解析依赖）
        long initialCount = 0;
        if (initialCountResponse.contains("\"totalTransactions\":")) {
            String countStr = initialCountResponse.split("\"totalTransactions\":")[1].split(",")[0].split("}")[0];
            initialCount = Long.parseLong(countStr.trim());
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        long startTime = System.currentTimeMillis();
        
        // 创建并发任务
        CompletableFuture<?>[] futures = IntStream.range(0, CONCURRENT_USERS)
                .mapToObj(userId -> CompletableFuture.runAsync(() -> {
                    try {
                        performUserStockTransactions(userId);
                    } catch (Exception e) {
                        throw new RuntimeException("用户 " + userId + " 股票交易失败", e);
                    }
                }, executor))
                .toArray(CompletableFuture[]::new);
        
        // 等待所有任务完成
        CompletableFuture.allOf(futures).get();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 验证新增的交易数量至少等于预期
        long expectedTotalCount = initialCount + TOTAL_TRANSACTIONS;
        mockMvc.perform(get("/api/transactions/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalTransactions").value(greaterThanOrEqualTo((int)expectedTotalCount)));
        
        // 输出性能指标
        printPerformanceMetrics(duration);
        
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
    }
    
    @Test
    void testConcurrentReadOperations() throws Exception {
        // 先创建一些测试数据
        createStockTestData(50);
        
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        long startTime = System.currentTimeMillis();
        
        // 创建并发读取任务
        CompletableFuture<?>[] futures = IntStream.range(0, CONCURRENT_USERS)
                .mapToObj(userId -> CompletableFuture.runAsync(() -> {
                    try {
                        performReadOperations();
                    } catch (Exception e) {
                        throw new RuntimeException("用户 " + userId + " 读取操作失败", e);
                    }
                }, executor))
                .toArray(CompletableFuture[]::new);
        
        // 等待所有任务完成
        CompletableFuture.allOf(futures).get();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("=== 并发读取测试结果 ===");
        System.out.println("并发用户数: " + CONCURRENT_USERS);
        System.out.println("总耗时: " + duration + "ms");
        System.out.println("平均每用户耗时: " + (duration / CONCURRENT_USERS) + "ms");
        
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
    }
    
    @Test
    void testMixedOperationsUnderLoad() throws Exception {
        // 创建初始数据
        createStockTestData(20);
        
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        long startTime = System.currentTimeMillis();
        
        // 创建混合操作任务
        CompletableFuture<?>[] futures = IntStream.range(0, CONCURRENT_USERS)
                .mapToObj(userId -> CompletableFuture.runAsync(() -> {
                    try {
                        performMixedOperations(userId);
                    } catch (Exception e) {
                        throw new RuntimeException("用户 " + userId + " 混合操作失败", e);
                    }
                }, executor))
                .toArray(CompletableFuture[]::new);
        
        // 等待所有任务完成
        CompletableFuture.allOf(futures).get();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("=== 混合操作测试结果 ===");
        System.out.println("并发用户数: " + CONCURRENT_USERS);
        System.out.println("总耗时: " + duration + "ms");
        System.out.println("========================");
        
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
    }
    
    private void performUserStockTransactions(int userId) throws Exception {
        String accountNumber = "ACC" + String.format("%016d", userId); // 16位账户号
        
        for (int i = 0; i < TRANSACTIONS_PER_USER; i++) {
            TransactionRequest request = new TransactionRequest();
            request.setAccountNumber(accountNumber);
            
            // 随机选择股票
            String securityCode = SECURITY_CODES[i % SECURITY_CODES.length];
            request.setSecurityCode(securityCode);
            
            // 随机选择买入或卖出
            TransactionType transType = i % 2 == 0 ? TransactionType.BUY : TransactionType.SELL;
            request.setTransType(transType);
            
            // 设置股数和价格
            Long unit = (long) ((1 + (i % 10)) * 100); // 100-1000股
            BigDecimal price = new BigDecimal("10.00").add(new BigDecimal(i % 50)); // 10-60元
            request.setUnit(unit);
            request.setPrice(price);
            
            // 计算总金额
            BigDecimal amount = price.multiply(new BigDecimal(unit));
            request.setAmount(amount);
            
            request.setTransDate(LocalDate.now());
            request.setDescription("压力测试" + transType.getChineseName() + securityCode);
            request.setCurrency("CNY");
            
            try {
                mockMvc.perform(post("/api/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                System.err.println("交易创建失败: " + e.getMessage());
                throw e;
            }
            
            // 模拟用户操作间隔
            Thread.sleep(1);
        }
    }
    
    private void performReadOperations() throws Exception {
        // 查询交易列表
        mockMvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
        
        // 查询统计信息
        mockMvc.perform(get("/api/transactions/statistics"))
                .andExpect(status().isOk());
        
        // 查询特定类型交易
        mockMvc.perform(get("/api/transactions/type/BUY"))
                .andExpect(status().isOk());
        
        // 查询股票交易统计
        mockMvc.perform(get("/api/transactions/security/000001/account/ACC0000000000000001/statistics"))
                .andExpect(status().isOk());
    }
    
    private void performMixedOperations(int userId) throws Exception {
        String accountNumber = "MIX" + String.format("%015d", userId); // 16位账户号
        
        // 50% 创建交易
        if (userId % 2 == 0) {
            TransactionRequest request = new TransactionRequest();
            request.setAccountNumber(accountNumber);
            request.setSecurityCode("000001");
            request.setTransType(TransactionType.BUY);
            request.setUnit(100L);
            request.setPrice(new BigDecimal("50.00"));
            request.setAmount(new BigDecimal("5000.00"));
            request.setTransDate(LocalDate.now());
            request.setDescription("混合测试买入平安银行");
            request.setCurrency("CNY");
            
            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
        
        // 执行读取操作
        performReadOperations();
        
        // 查询账户交易
        mockMvc.perform(get("/api/transactions/account/" + accountNumber))
                .andExpect(status().isOk());
    }
    
    private void createStockTestData(int count) throws Exception {
        for (int i = 1; i <= count; i++) {
            TransactionRequest request = new TransactionRequest();
            request.setAccountNumber("TEST" + String.format("%012d", i)); // 16位账户号
            request.setSecurityCode(SECURITY_CODES[i % SECURITY_CODES.length]);
            request.setTransType(TransactionType.BUY);
            request.setUnit(1000L);
            request.setPrice(new BigDecimal("10.00"));
            request.setAmount(new BigDecimal("10000.00"));
            request.setTransDate(LocalDate.now());
            request.setDescription("测试数据买入股票 " + i);
            request.setCurrency("CNY");
            
            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
    }
    
    private void printPerformanceMetrics(long duration) {
        System.out.println("=== 股票交易性能测试结果 ===");
        System.out.println("总耗时: " + duration + "ms");
        System.out.println("平均每个交易耗时: " + (duration / TOTAL_TRANSACTIONS) + "ms");
        System.out.println("TPS (每秒交易数): " + (TOTAL_TRANSACTIONS * 1000L / duration));
        System.out.println("并发性能: " + CONCURRENT_USERS + " 用户同时操作");
        System.out.println("===============================");
    }
} 