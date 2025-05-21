package com.example.banktransaction.repository.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.banktransaction.model.PageResult;
import com.example.banktransaction.model.Transaction;
import com.example.banktransaction.model.TransactionQuery;
import com.example.banktransaction.repository.TransactionRepository;

/**
 * 内存交易存储库实现类
 * 使用内存数据结构存储交易记录，无需持久化存储
 */
@Repository
public class InMemoryTransactionRepository implements TransactionRepository {
    
    /**
     * 使用线程安全的ConcurrentHashMap存储交易记录
     */
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();
    
    @Override
    public Transaction save(Transaction transaction) {
        // 确保交易记录已初始化
        transaction.initialize();
        
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }
    
    @Override
    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(transactions.get(id));
    }
    
    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }
    
    @Override
    public PageResult<Transaction> findByQuery(TransactionQuery query) {
        // 过滤交易记录
        List<Transaction> filteredTransactions = transactions.values().stream()
                .filter(tx -> matchesQuery(tx, query))
                .sorted(Comparator.comparing(Transaction::getTransactionTime).reversed()) // 按交易时间降序排序
                .collect(Collectors.toList());
        
        // 计算总记录数
        long totalElements = filteredTransactions.size();
        
        // 分页处理
        int pageSize = query.getSize();
        int page = query.getPage();
        
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filteredTransactions.size());
        
        // 处理边界情况
        if (fromIndex >= filteredTransactions.size()) {
            return PageResult.of(Collections.emptyList(), totalElements, page, pageSize);
        }
        
        List<Transaction> pageContent = filteredTransactions.subList(fromIndex, toIndex);
        
        return PageResult.of(pageContent, totalElements, page, pageSize);
    }
    
    @Override
    public boolean deleteById(String id) {
        return transactions.remove(id) != null;
    }
    
    @Override
    public long count() {
        return transactions.size();
    }
    
    /**
     * 判断交易记录是否匹配查询条件
     * 
     * @param transaction 交易记录
     * @param query 查询条件
     * @return 是否匹配
     */
    private boolean matchesQuery(Transaction transaction, TransactionQuery query) {
        // 检查交易时间范围
        LocalDateTime txTime = transaction.getTransactionTime();
        if (query.getStartTime() != null && txTime.isBefore(query.getStartTime())) {
            return false;
        }
        if (query.getEndTime() != null && txTime.isAfter(query.getEndTime())) {
            return false;
        }
        
        // 检查交易类型
        if (query.getType() != null && !query.getType().isEmpty() 
                && !transaction.getType().equals(query.getType())) {
            return false;
        }
        
        // 检查金额范围
        BigDecimal amount = transaction.getAmount();
        if (query.getMinAmount() != null && amount.compareTo(query.getMinAmount()) < 0) {
            return false;
        }
        if (query.getMaxAmount() != null && amount.compareTo(query.getMaxAmount()) > 0) {
            return false;
        }
        
        // 检查交易状态
        if (query.getStatus() != null && !query.getStatus().isEmpty() 
                && !transaction.getStatus().equals(query.getStatus())) {
            return false;
        }
        
        return true;
    }
} 