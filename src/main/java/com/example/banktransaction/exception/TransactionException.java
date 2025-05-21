package com.example.banktransaction.exception;

/**
 * 交易异常类
 * 用于表示交易过程中的各种异常情况
 */
public class TransactionException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 异常状态码
     */
    private final int code;
    
    /**
     * 创建交易异常
     * 
     * @param message 异常消息
     */
    public TransactionException(String message) {
        this(400, message);
    }
    
    /**
     * 创建交易异常
     * 
     * @param code 异常状态码
     * @param message 异常消息
     */
    public TransactionException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 获取异常状态码
     * 
     * @return 状态码
     */
    public int getCode() {
        return code;
    }
} 