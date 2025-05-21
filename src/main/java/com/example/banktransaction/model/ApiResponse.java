package com.example.banktransaction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API响应类
 * 用于统一API接口的返回格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * 响应状态码，200表示成功
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 创建成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .success(true)
                .build();
    }
    
    /**
     * 创建成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .code(200)
                .message("操作成功")
                .data(null)
                .success(true)
                .build();
    }
    
    /**
     * 创建成功响应（自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .success(true)
                .build();
    }
    
    /**
     * 创建成功响应（自定义消息，无数据，专用于Void类型）
     */
    public static ApiResponse<Void> successWithNoData(String message) {
        return ApiResponse.<Void>builder()
                .code(200)
                .message(message)
                .data(null)
                .success(true)
                .build();
    }
    
    /**
     * 创建错误响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .success(false)
                .build();
    }
    
    /**
     * 创建错误响应（默认状态码为400）
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(400, message);
    }
} 