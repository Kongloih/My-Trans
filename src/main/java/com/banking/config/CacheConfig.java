package com.banking.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 * 
 * @author Kongloih Zhang F
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * 配置Caffeine缓存管理器
     * 
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 最大缓存条目数
                .maximumSize(1000)
                // 写入后过期时间
                .expireAfterWrite(30, TimeUnit.MINUTES)
                // 访问后过期时间
                .expireAfterAccess(10, TimeUnit.MINUTES)
                // 初始缓存空间大小
                .initialCapacity(100)
                // 开启统计
                .recordStats()
        );
        
        // 设置缓存名称
        cacheManager.setCacheNames(java.util.Arrays.asList("transactions"));
        
        return cacheManager;
    }
} 