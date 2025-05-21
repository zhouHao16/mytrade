package com.example.banktransaction.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.banktransaction.exception.TransactionException;
import com.example.banktransaction.model.PageResult;
import com.example.banktransaction.model.Transaction;
import com.example.banktransaction.model.TransactionQuery;
import com.example.banktransaction.repository.TransactionRepository;
import com.example.banktransaction.service.impl.TransactionServiceImpl;

/**
 * 交易服务单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    
    @InjectMocks
    private TransactionServiceImpl transactionService;
    
    private Transaction transaction;
    private List<Transaction> transactions;
    
    @BeforeEach
    void setUp() {
        // 初始化测试数据
        transaction = Transaction.builder()
                .id("test-id")
                .type("存款")
                .amount(new BigDecimal("100.00"))
                .description("测试交易")
                .transactionTime(LocalDateTime.now())
                .status("成功")
                .build();
        
        transactions = new ArrayList<>();
        transactions.add(transaction);
    }
    
    @Test
    void testCreateTransaction() {
        // 模拟Repository的行为
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        
        // 执行测试
        Transaction result = transactionService.createTransaction(transaction);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("test-id", result.getId());
        assertEquals("存款", result.getType());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("成功", result.getStatus());
        
        // 验证Repository的方法被调用
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
    
    @Test
    void testGetTransaction() {
        // 模拟Repository的行为
        when(transactionRepository.findById(anyString())).thenReturn(Optional.of(transaction));
        
        // 执行测试
        Transaction result = transactionService.getTransaction("test-id");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("test-id", result.getId());
        
        // 验证Repository的方法被调用
        verify(transactionRepository, times(1)).findById(anyString());
    }
    
    @Test
    void testGetTransactionNotFound() {
        // 模拟Repository的行为
        when(transactionRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // 验证异常被抛出
        assertThrows(TransactionException.class, () -> {
            transactionService.getTransaction("non-existent-id");
        });
        
        // 验证Repository的方法被调用
        verify(transactionRepository, times(1)).findById(anyString());
    }
    
    @Test
    void testUpdateTransaction() {
        // 模拟Repository的行为
        when(transactionRepository.findById(anyString())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        
        // 创建更新后的交易
        Transaction updatedTransaction = Transaction.builder()
                .type("取款")
                .amount(new BigDecimal("200.00"))
                .description("更新测试交易")
                .transactionTime(LocalDateTime.now())
                .status("处理中")
                .build();
        
        // 执行测试
        Transaction result = transactionService.updateTransaction("test-id", updatedTransaction);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("test-id", result.getId());
        assertEquals("取款", result.getType());
        assertEquals(new BigDecimal("200.00"), result.getAmount());
        assertEquals("处理中", result.getStatus());
        
        // 验证Repository的方法被调用
        verify(transactionRepository, times(1)).findById(anyString());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
    
    @Test
    void testDeleteTransaction() {
        // 模拟Repository的行为
        when(transactionRepository.findById(anyString())).thenReturn(Optional.of(transaction));
        when(transactionRepository.deleteById(anyString())).thenReturn(true);
        
        // 执行测试
        boolean result = transactionService.deleteTransaction("test-id");
        
        // 验证结果
        assertTrue(result);
        
        // 验证Repository的方法被调用
        verify(transactionRepository, times(1)).findById(anyString());
        verify(transactionRepository, times(1)).deleteById(anyString());
    }
    
    @Test
    void testDeleteTransactionNotFound() {
        // 模拟Repository的行为
        when(transactionRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // 验证异常被抛出
        assertThrows(TransactionException.class, () -> {
            transactionService.deleteTransaction("non-existent-id");
        });
        
        // 验证Repository的方法被调用
        verify(transactionRepository, times(1)).findById(anyString());
    }
    
    @Test
    void testQueryTransactions() {
        // 创建查询条件
        TransactionQuery query = TransactionQuery.builder()
                .type("存款")
                .build();
        
        // 创建分页结果
        PageResult<Transaction> pageResult = PageResult.<Transaction>builder()
                .content(transactions)
                .totalElements(1)
                .totalPages(1)
                .currentPage(0)
                .pageSize(10)
                .first(true)
                .last(true)
                .build();
        
        // 模拟Repository的行为
        when(transactionRepository.findByQuery(any(TransactionQuery.class))).thenReturn(pageResult);
        
        // 执行测试
        PageResult<Transaction> result = transactionService.queryTransactions(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        
        // 验证Repository的方法被调用
        verify(transactionRepository, times(1)).findByQuery(any(TransactionQuery.class));
    }
} 