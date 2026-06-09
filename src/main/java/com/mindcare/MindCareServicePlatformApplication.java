package com.mindcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类。
 *
 * <p>当前阶段只落地登录认证模块，但启动类仍然按完整 Spring Boot 项目的标准结构创建，
 * 这样后续继续扩展咨询师、预约、报表等模块时不需要再调整入口结构。</p>
 */
@SpringBootApplication
public class MindCareServicePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MindCareServicePlatformApplication.class, args);
    }
}
