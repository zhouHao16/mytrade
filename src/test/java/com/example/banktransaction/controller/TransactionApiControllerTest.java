package com.example.banktransaction.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.banktransaction.exception.TransactionException;
import com.example.banktransaction.model.PageResult;
import com.example.banktransaction.model.Transaction;
import com.example.banktransaction.model.TransactionQuery;
import com.example.banktransaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 交易API控制器测试类
 */
@WebMvcTest(TransactionApiController.class)
public class TransactionApiControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TransactionService transactionService;
    
    private ObjectMapper objectMapper;
    private Transaction transaction;
    private List<Transaction> transactions;
    
    @BeforeEach
    void setUp() {
        // 初始化ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
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
    void testCreateTransaction() throws Exception {
        // 模拟Service的行为
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(transaction);
        
        // 执行测试
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("test-id"))
                .andExpect(jsonPath("$.data.type").value("存款"))
                .andExpect(jsonPath("$.data.status").value("成功"));
    }
    
    @Test
    void testGetTransaction() throws Exception {
        // 模拟Service的行为
        when(transactionService.getTransaction(anyString())).thenReturn(transaction);
        
        // 执行测试
        mockMvc.perform(get("/api/transactions/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("test-id"))
                .andExpect(jsonPath("$.data.type").value("存款"));
    }
    
    @Test
    void testGetTransactionNotFound() throws Exception {
        // 模拟Service的行为
        when(transactionService.getTransaction(anyString())).thenThrow(new TransactionException("交易记录不存在"));
        
        // 执行测试
        mockMvc.perform(get("/api/transactions/non-existent-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("交易记录不存在"));
    }
    
    @Test
    void testUpdateTransaction() throws Exception {
        // 模拟Service的行为
        when(transactionService.updateTransaction(anyString(), any(Transaction.class))).thenReturn(transaction);
        
        // 执行测试
        mockMvc.perform(put("/api/transactions/test-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("交易记录更新成功"))
                .andExpect(jsonPath("$.data.id").value("test-id"));
    }
    
    @Test
    void testDeleteTransaction() throws Exception {
        // 模拟Service的行为
        when(transactionService.deleteTransaction(anyString())).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(delete("/api/transactions/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("交易记录删除成功"));
    }
    
    @Test
    void testQueryTransactions() throws Exception {
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
        
        // 模拟Service的行为
        when(transactionService.queryTransactions(any(TransactionQuery.class))).thenReturn(pageResult);
        
        // 执行测试
        mockMvc.perform(get("/api/transactions?type=存款"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value("test-id"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
} 