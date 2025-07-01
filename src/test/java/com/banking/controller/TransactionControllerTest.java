package com.banking.controller;

import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import com.banking.model.TransactionType;
import com.banking.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TransactionController集成测试
 * 
 * @author Kongloih Zhang F
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TransactionService transactionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TransactionRequest validRequest;
    private TransactionResponse sampleResponse;
    
    @BeforeEach
    void setUp() {
        validRequest = new TransactionRequest();
        validRequest.setAccountNumber("1234567890123456");
        validRequest.setAmount(new BigDecimal("10000.00"));
        validRequest.setTransType(TransactionType.BUY);
        validRequest.setUnit(1000L);
        validRequest.setPrice(new BigDecimal("10.00"));
        validRequest.setTransDate(LocalDate.now());
        validRequest.setSecurityCode("000001");
        validRequest.setDescription("测试买入平安银行");
        validRequest.setCurrency("CNY");
        
        sampleResponse = new TransactionResponse();
        sampleResponse.setId(1L);
        sampleResponse.setAccountNumber("1234567890123456");
        sampleResponse.setAmount(new BigDecimal("10000.00"));
        sampleResponse.setTransType(TransactionType.BUY);
        sampleResponse.setUnit(1000L);
        sampleResponse.setPrice(new BigDecimal("10.00"));
        sampleResponse.setTransDate(LocalDate.now());
        sampleResponse.setSecurityCode("000001");
        sampleResponse.setDescription("测试买入平安银行");
        sampleResponse.setCurrency("CNY");
        sampleResponse.setTimestamp(LocalDateTime.now());
    }
    
    @Test
    void testCreateTransaction_Success() throws Exception {
        // Arrange
        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(sampleResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("交易创建成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.accountNumber").value("1234567890123456"))
                .andExpect(jsonPath("$.data.amount").value(10000.00))
                .andExpect(jsonPath("$.data.transType").value("BUY"))
                .andExpect(jsonPath("$.data.securityCode").value("000001"));
    }
    
    @Test
    void testCreateTransaction_InvalidRequest() throws Exception {
        // Arrange - 无效请求（缺少账户号码和其他必需字段）
        TransactionRequest invalidRequest = new TransactionRequest();
        invalidRequest.setAmount(new BigDecimal("10000.00"));
        invalidRequest.setTransType(TransactionType.BUY);
        // 故意省略必需的字段：accountNumber, unit, price, transDate, securityCode
        
        // Act & Assert
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("参数校验失败"))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }
    
    @Test
    void testGetTransaction_Success() throws Exception {
        // Arrange
        when(transactionService.getTransaction(1L)).thenReturn(sampleResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取交易详情成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.accountNumber").value("1234567890123456"));
    }
    
    @Test
    void testUpdateTransaction_Success() throws Exception {
        // Arrange
        when(transactionService.updateTransaction(anyLong(), any(TransactionRequest.class))).thenReturn(sampleResponse);
        
        // Act & Assert
        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("交易更新成功"))
                .andExpect(jsonPath("$.data.id").value(1));
    }
    
    @Test
    void testDeleteTransaction_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("交易删除成功"));
    }
    
    @Test
    void testGetTransactions_Success() throws Exception {
        // Arrange
        List<TransactionResponse> transactions = Arrays.asList(sampleResponse);
        when(transactionService.getTransactions(0, 10)).thenReturn(transactions);
        when(transactionService.getTransactionCount()).thenReturn(1L);
        
        // Act & Assert
        mockMvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取交易列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(10))
                .andExpect(jsonPath("$.pagination.total").value(1));
    }
    
    @Test
    void testGetTransactionsByAccount_Success() throws Exception {
        // Arrange
        List<TransactionResponse> transactions = Arrays.asList(sampleResponse);
        when(transactionService.getTransactionsByAccount("1234567890123456")).thenReturn(transactions);
        
        // Act & Assert
        mockMvc.perform(get("/api/transactions/account/1234567890123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取账户交易成功"))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    void testGetTransactionsByType_Success() throws Exception {
        // Arrange
        List<TransactionResponse> transactions = Arrays.asList(sampleResponse);
        when(transactionService.getTransactionsByType(TransactionType.BUY)).thenReturn(transactions);
        
        // Act & Assert
        mockMvc.perform(get("/api/transactions/type/BUY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取买入交易成功"))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    void testGetStatistics_Success() throws Exception {
        // Arrange
        when(transactionService.getTransactionCount()).thenReturn(5L);
        
        // Act & Assert
        mockMvc.perform(get("/api/transactions/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取统计信息成功"))
                .andExpect(jsonPath("$.data.totalTransactions").value(5))
                .andExpect(jsonPath("$.data.transactionTypes").isArray());
    }
} 