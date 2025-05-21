package com.example.banktransaction.service.impl;

import java.time.LocalDateTime;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.banktransaction.exception.TransactionException;
import com.example.banktransaction.model.PageResult;
import com.example.banktransaction.model.Transaction;
import com.example.banktransaction.model.TransactionQuery;
import com.example.banktransaction.repository.TransactionRepository;
import com.example.banktransaction.service.TransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 交易服务实现类
 * 实现交易业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    
    /**
     * 交易存储库
     */
    private final TransactionRepository transactionRepository;
    
    @Override
    public Transaction createTransaction(Transaction transaction) {
        log.info("创建交易记录: {}", transaction);
        
        // 设置创建时间和更新时间
        transaction.initialize();
        
        // 保存交易记录
        return transactionRepository.save(transaction);
    }
    
    @Override
    @CachePut(value = "transactions", key = "#id")
    public Transaction updateTransaction(String id, Transaction transaction) {
        log.info("更新交易记录: {}, {}", id, transaction);
        
        // 获取现有交易记录
        Transaction existingTransaction = getTransaction(id);
        
        // 更新交易记录
        existingTransaction.setType(transaction.getType());
        existingTransaction.setAmount(transaction.getAmount());
        existingTransaction.setDescription(transaction.getDescription());
        existingTransaction.setTransactionTime(transaction.getTransactionTime());
        existingTransaction.setStatus(transaction.getStatus());
        existingTransaction.setUpdatedAt(LocalDateTime.now());
        
        // 保存交易记录
        return transactionRepository.save(existingTransaction);
    }
    
    @Override
    @Cacheable(value = "transactions", key = "#id")
    public Transaction getTransaction(String id) {
        log.info("获取交易记录: {}", id);
        
        // 获取交易记录
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionException("交易记录不存在: " + id));
    }
    
    @Override
    @CacheEvict(value = "transactions", key = "#id")
    public boolean deleteTransaction(String id) {
        log.info("删除交易记录: {}", id);
        
        // 检查交易记录是否存在
        if (!transactionRepository.findById(id).isPresent()) {
            throw new TransactionException("交易记录不存在: " + id);
        }
        
        // 删除交易记录
        return transactionRepository.deleteById(id);
    }
    
    @Override
    public PageResult<Transaction> queryTransactions(TransactionQuery query) {
        log.info("查询交易记录: {}", query);
        
        // 查询交易记录
        return transactionRepository.findByQuery(query);
    }
} 