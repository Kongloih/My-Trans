package com.banking.service;

import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import com.banking.exception.TransactionNotFoundException;
import com.banking.model.Transaction;
import com.banking.model.TransactionType;
import com.banking.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * TransactionService股票交易单元测试
 * 
 * @author Kongloih Zhang F
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @InjectMocks
    private TransactionService transactionService;
    
    private TransactionRequest validRequest;
    private Transaction sampleTransaction;
    
    @BeforeEach
    void setUp() {
        // 创建股票交易测试数据
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
        
        sampleTransaction = new Transaction();
        sampleTransaction.setId(1L);
        sampleTransaction.setAccountNumber("1234567890123456");
        sampleTransaction.setAmount(new BigDecimal("10000.00"));
        sampleTransaction.setTransType(TransactionType.BUY);
        sampleTransaction.setUnit(1000L);
        sampleTransaction.setPrice(new BigDecimal("10.00"));
        sampleTransaction.setTransDate(LocalDate.now());
        sampleTransaction.setSecurityCode("000001");
        sampleTransaction.setDescription("测试买入平安银行");
        sampleTransaction.setCurrency("CNY");
        sampleTransaction.setTimestamp(LocalDateTime.now());
    }
    
    @Test
    void testCreateTransaction_Success() {
        // Arrange
        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleTransaction);
        
        // Act
        TransactionResponse response = transactionService.createTransaction(validRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("1234567890123456", response.getAccountNumber());
        assertEquals(new BigDecimal("10000.00"), response.getAmount());
        assertEquals(TransactionType.BUY, response.getTransType());
        assertEquals(1000L, response.getUnit());
        assertEquals(new BigDecimal("10.00"), response.getPrice());
        assertEquals("000001", response.getSecurityCode());
        assertEquals("测试买入平安银行", response.getDescription());
        
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
    
    @Test
    void testCreateTransaction_InvalidRequest() {
        // Arrange
        TransactionRequest invalidRequest = new TransactionRequest();
        invalidRequest.setAccountNumber(""); // 空账户号码
        invalidRequest.setAmount(new BigDecimal("10000.00"));
        invalidRequest.setTransType(TransactionType.BUY);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(invalidRequest);
        });
        
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    
    @Test
    void testCreateTransaction_NegativeAmount() {
        // Arrange
        validRequest.setAmount(new BigDecimal("-100.00"));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(validRequest);
        });
    }
    
    @Test
    void testGetTransaction_Success() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(sampleTransaction));
        
        // Act
        TransactionResponse response = transactionService.getTransaction(1L);
        
        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("1234567890123456", response.getAccountNumber());
        assertEquals("000001", response.getSecurityCode());
        
        verify(transactionRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetTransaction_NotFound() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.getTransaction(1L);
        });
        
        verify(transactionRepository, times(1)).findById(1L);
    }
    
    @Test
    void testUpdateTransaction_Success() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(sampleTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleTransaction);
        
        TransactionRequest updateRequest = new TransactionRequest();
        updateRequest.setAccountNumber("1234567890123456");
        updateRequest.setAmount(new BigDecimal("15000.00"));
        updateRequest.setTransType(TransactionType.SELL);
        updateRequest.setUnit(1500L);
        updateRequest.setPrice(new BigDecimal("10.00"));
        updateRequest.setTransDate(LocalDate.now());
        updateRequest.setSecurityCode("000001");
        updateRequest.setDescription("更新后的卖出平安银行");
        updateRequest.setCurrency("CNY");
        
        // Act
        TransactionResponse response = transactionService.updateTransaction(1L, updateRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        
        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
    
    @Test
    void testUpdateTransaction_NotFound() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.updateTransaction(1L, validRequest);
        });
        
        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    
    @Test
    void testDeleteTransaction_Success() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(sampleTransaction));
        when(transactionRepository.deleteById(1L)).thenReturn(true);
        
        // Act
        assertDoesNotThrow(() -> {
            transactionService.deleteTransaction(1L);
        });
        
        // Assert
        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteTransaction_NotFound() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.deleteTransaction(1L);
        });
        
        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, never()).deleteById(anyLong());
    }
    
    @Test
    void testGetAllTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(sampleTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);
        
        // Act
        List<TransactionResponse> responses = transactionService.getAllTransactions();
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        
        verify(transactionRepository, times(1)).findAll();
    }
    
    @Test
    void testGetTransactions_Pagination() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(sampleTransaction);
        when(transactionRepository.findAll(0, 10)).thenReturn(transactions);
        
        // Act
        List<TransactionResponse> responses = transactionService.getTransactions(0, 10);
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        
        verify(transactionRepository, times(1)).findAll(0, 10);
    }
    
    @Test
    void testGetTransactions_InvalidPagination() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactions(-1, 10);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactions(0, 0);
        });
    }
    
    @Test
    void testGetTransactionsByAccount() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(sampleTransaction);
        when(transactionRepository.findByAccountNumber("1234567890123456")).thenReturn(transactions);
        
        // Act
        List<TransactionResponse> responses = transactionService.getTransactionsByAccount("1234567890123456");
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("1234567890123456", responses.get(0).getAccountNumber());
        
        verify(transactionRepository, times(1)).findByAccountNumber("1234567890123456");
    }
    
    @Test
    void testGetTransactionsByAccount_BlankAccountNumber() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionsByAccount("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionsByAccount(null);
        });
    }
    
    @Test
    void testGetTransactionsByType() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(sampleTransaction);
        when(transactionRepository.findByType(TransactionType.BUY)).thenReturn(transactions);
        
        // Act
        List<TransactionResponse> responses = transactionService.getTransactionsByType(TransactionType.BUY);
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(TransactionType.BUY, responses.get(0).getTransType());
        
        verify(transactionRepository, times(1)).findByType(TransactionType.BUY);
    }
    
    @Test
    void testGetTransactionCount() {
        // Arrange
        when(transactionRepository.count()).thenReturn(5L);
        
        // Act
        long count = transactionService.getTransactionCount();
        
        // Assert
        assertEquals(5L, count);
        
        verify(transactionRepository, times(1)).count();
    }
    
    @Test
    void testGetSecurityStatistics() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(sampleTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);
        
        // Act
        Map<String, Object> statistics = transactionService.getSecurityStatistics("000001", "1234567890123456");
        
        // Assert
        assertNotNull(statistics);
        assertEquals("000001", statistics.get("securityCode"));
        assertEquals("1234567890123456", statistics.get("accountNumber"));
        assertNotNull(statistics.get("buyStatistics"));
        assertNotNull(statistics.get("sellStatistics"));
        assertNotNull(statistics.get("netPosition"));
    }
} 