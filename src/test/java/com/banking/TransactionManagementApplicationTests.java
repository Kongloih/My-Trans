package com.banking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring Boot应用程序集成测试
 * 
 * @author Kongloih Zhang F
 */
@SpringBootTest
@ActiveProfiles("test")
class TransactionManagementApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
    }
} 