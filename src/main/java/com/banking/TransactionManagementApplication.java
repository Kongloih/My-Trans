package com.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 银行交易管理系统主应用程序
 * 
 * @author Kongloih Zhang F
 * @version 1.0
 */
@SpringBootApplication
@EnableCaching
public class TransactionManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionManagementApplication.class, args);
    }
} 