package com.example.banktransaction.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易实体类
 * 用于表示银行系统中的一笔交易记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    /**
     * 交易ID，使用UUID生成唯一标识
     */
    private String id;
    
    /**
     * 交易类型，如：存款、取款、转账等
     */
    @NotBlank(message = "交易类型不能为空")
    private String type;
    
    /**
     * 交易金额，必须为非负数
     */
    @NotNull(message = "交易金额不能为空")
    @PositiveOrZero(message = "交易金额必须大于或等于0")
    private BigDecimal amount;
    
    /**
     * 交易描述，用于说明交易的具体内容或备注
     */
    private String description;
    
    /**
     * 交易时间，记录交易发生的确切时间
     */
    @NotNull(message = "交易时间不能为空")
    private LocalDateTime transactionTime;
    
    /**
     * 交易状态，如：成功、失败、处理中等
     */
    @NotBlank(message = "交易状态不能为空")
    private String status;
    
    /**
     * 创建时间，记录交易记录创建的时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间，记录交易记录最后一次更新的时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 交易初始化方法，用于设置默认值
     */
    public void initialize() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.transactionTime == null) {
            this.transactionTime = LocalDateTime.now();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }
} 