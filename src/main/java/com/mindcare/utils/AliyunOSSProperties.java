package com.mindcare.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 配置绑定类。
 *
 * <p>保持和 tlias-web-management 接近的风格：
 * - endpoint / bucketName / region 放在 application.yml
 * - 通过 @ConfigurationProperties 绑定配置项
 * - AccessKey 和 Secret 仍然通过环境变量提供，不写进配置文件</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOSSProperties {

    private String endpoint;
    private String bucketName;
    private String region;
}
