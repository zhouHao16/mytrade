package com.example.banktransaction.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.banktransaction.model.ApiResponse;
import com.example.banktransaction.model.PageResult;
import com.example.banktransaction.model.Transaction;
import com.example.banktransaction.model.TransactionQuery;
import com.example.banktransaction.service.TransactionService;

import lombok.RequiredArgsConstructor;

/**
 * 交易REST API控制器
 * 处理交易相关的REST API请求
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionApiController {
    
    /**
     * 交易服务
     */
    private final TransactionService transactionService;
    
    /**
     * 创建交易
     * 
     * @param transaction 交易记录
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(@Validated @RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("交易记录创建成功", createdTransaction));
    }
    
    /**
     * 更新交易
     * 
     * @param id 交易ID
     * @param transaction 交易记录
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> updateTransaction(
            @PathVariable String id, @Validated @RequestBody Transaction transaction) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(ApiResponse.success("交易记录更新成功", updatedTransaction));
    }
    
    /**
     * 获取交易
     * 
     * @param id 交易ID
     * @return 交易记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> getTransaction(@PathVariable String id) {
        Transaction transaction = transactionService.getTransaction(id);
        return ResponseEntity.ok(ApiResponse.success(transaction));
    }
    
    /**
     * 删除交易
     * 
     * @param id 交易ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable String id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(ApiResponse.successWithNoData("交易记录删除成功"));
    }
    
    /**
     * 查询交易
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param type 交易类型
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @param status 交易状态
     * @param page 页码
     * @param size 每页大小
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<Transaction>>> queryTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 构建查询条件
        TransactionQuery query = TransactionQuery.builder()
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .minAmount(minAmount)
                .maxAmount(maxAmount)
                .status(status)
                .page(page)
                .size(size)
                .build();
        
        // 查询交易记录
        PageResult<Transaction> pageResult = transactionService.queryTransactions(query);
        
        return ResponseEntity.ok(ApiResponse.success(pageResult));
    }
    
    /**
     * 获取服务器当前时间
     * 
     * @return 当前时间
     */
    @GetMapping("/current-time")
    public ResponseEntity<ApiResponse<String>> getCurrentTime() {
        // 获取当前时间并格式化为前端需要的格式
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.toString(); // ISO格式: 2025-05-20T14:30:00
        return ResponseEntity.ok(ApiResponse.success(formattedTime));
    }
} 