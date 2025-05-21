package com.example.banktransaction.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易查询条件类
 * 用于封装交易查询的各种条件参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionQuery {
    
    /**
     * 交易开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 交易结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 交易类型
     */
    private String type;
    
    /**
     * 最小金额
     */
    private BigDecimal minAmount;
    
    /**
     * 最大金额
     */
    private BigDecimal maxAmount;
    
    /**
     * 交易状态
     */
    private String status;
    
    /**
     * 页码，默认为0
     */
    @Builder.Default
    private int page = 0;
    
    /**
     * 每页记录数，默认为10
     */
    @Builder.Default
    private int size = 10;
} 