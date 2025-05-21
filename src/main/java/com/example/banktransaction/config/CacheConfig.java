package com.example.banktransaction.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 缓存配置类
 * 配置缓存管理器和缓存相关策略
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * 创建缓存管理器
     * 使用Caffeine缓存库实现高性能的本地缓存
     * 
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("transactions");
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }
    
    /**
     * 配置Caffeine缓存构建器
     * 设置缓存的过期策略、最大容量等属性
     * 
     * @return Caffeine构建器
     */
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)    // 初始容量
                .maximumSize(1000)       // 最大容量
                .expireAfterAccess(30, TimeUnit.MINUTES)  // 访问后过期时间
                .expireAfterWrite(1, TimeUnit.HOURS)      // 写入后过期时间
                .recordStats();          // 记录统计信息
    }
} 