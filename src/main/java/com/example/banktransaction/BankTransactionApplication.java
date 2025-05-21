package com.example.banktransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 银行交易管理应用程序入口类
 * 启用缓存机制以提升性能
 */
@SpringBootApplication
@EnableCaching
public class BankTransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankTransactionApplication.class, args);
    }
} 