package com.example.banktransaction.performance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.banktransaction.model.Transaction;
import com.example.banktransaction.model.TransactionQuery;
import com.example.banktransaction.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

/**
 * 交易性能测试类
 * 用于测试系统在高负载下的性能表现
 * 注意：这些测试默认被禁用，因为它们会消耗大量资源并且运行时间较长
 */
@Slf4j
@SpringBootTest
@Disabled("性能测试应该在需要时手动运行")
public class TransactionPerformanceTest {
    
    @Autowired
    private TransactionService transactionService;
    
    /**
     * 批量创建交易测试
     * 同时创建大量交易记录，测试系统的并发处理能力
     */
    @Test
    void testBatchCreateTransactions() throws InterruptedException, ExecutionException {
        int numTransactions = 1000;
        int numThreads = 10;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        long start = System.currentTimeMillis();
        
        // 创建并发任务
        for (int i = 0; i < numTransactions; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Transaction transaction = Transaction.builder()
                        .type("存款")
                        .amount(new BigDecimal(String.format("%.2f", Math.random() * 1000)))
                        .description("性能测试交易 #" + index)
                        .transactionTime(LocalDateTime.now())
                        .status("成功")
                        .build();
                
                transactionService.createTransaction(transaction);
            }, executor);
            
            futures.add(future);
        }
        
        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        
        long end = System.currentTimeMillis();
        long duration = end - start;
        
        log.info("批量创建{}个交易耗时{}毫秒，平均每个交易{}毫秒", 
                numTransactions, duration, (double) duration / numTransactions);
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
    
    /**
     * 高并发查询测试
     * 同时执行大量查询操作，测试系统的查询性能
     */
    @Test
    void testConcurrentQueries() throws InterruptedException, ExecutionException {
        int numQueries = 500;
        int numThreads = 10;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        // 先创建一些测试数据
        for (int i = 0; i < 100; i++) {
            Transaction transaction = Transaction.builder()
                    .type(i % 2 == 0 ? "存款" : "取款")
                    .amount(new BigDecimal(String.format("%.2f", Math.random() * 1000)))
                    .description("查询测试交易 #" + i)
                    .transactionTime(LocalDateTime.now().minusDays(i % 30))
                    .status(i % 3 == 0 ? "成功" : (i % 3 == 1 ? "失败" : "处理中"))
                    .build();
            
            transactionService.createTransaction(transaction);
        }
        
        long start = System.currentTimeMillis();
        
        // 创建并发查询任务
        for (int i = 0; i < numQueries; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                TransactionQuery query = TransactionQuery.builder()
                        .type(index % 2 == 0 ? "存款" : "取款")
                        .status(index % 3 == 0 ? "成功" : (index % 3 == 1 ? "失败" : "处理中"))
                        .page(index % 5)
                        .size(10)
                        .build();
                
                transactionService.queryTransactions(query);
            }, executor);
            
            futures.add(future);
        }
        
        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        
        long end = System.currentTimeMillis();
        long duration = end - start;
        
        log.info("并发执行{}个查询耗时{}毫秒，平均每个查询{}毫秒", 
                numQueries, duration, (double) duration / numQueries);
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
    
    /**
     * 缓存效果测试
     * 测试缓存对查询性能的提升效果
     */
    @Test
    void testCachePerformance() {
        // 创建一个测试交易
        Transaction transaction = Transaction.builder()
                .type("存款")
                .amount(new BigDecimal("100.00"))
                .description("缓存测试交易")
                .transactionTime(LocalDateTime.now())
                .status("成功")
                .build();
        
        transaction = transactionService.createTransaction(transaction);
        String id = transaction.getId();
        
        // 第一次查询（未缓存）
        long start1 = System.nanoTime();
        transactionService.getTransaction(id);
        long end1 = System.nanoTime();
        long duration1 = end1 - start1;
        
        // 第二次查询（已缓存）
        long start2 = System.nanoTime();
        transactionService.getTransaction(id);
        long end2 = System.nanoTime();
        long duration2 = end2 - start2;
        
        log.info("未缓存查询耗时{}纳秒", duration1);
        log.info("已缓存查询耗时{}纳秒", duration2);
        log.info("缓存提升性能{}倍", (double) duration1 / duration2);
    }
} 