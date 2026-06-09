package com.mindcare.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.mindcare.exception.BusinessException;
import com.mindcare.pojo.UploadResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 阿里云 OSS 操作工具类。
 *
 * <p>该工具类专门负责“和 OSS 打交道”的技术细节，
 * 例如：
 * - 生成对象存储路径
 * - 创建 OSSClient
 * - 上传字节流
 * - 组装访问地址
 *
 * <p>这样 Controller 和 Service 就不用关心底层 OSS API 细节，
 * 只需要把文件内容和原始文件名传进来即可。</p>
 */
@Component
public class AliyunOSSOperator {

    private final AliyunOSSProperties aliyunOSSProperties;

    public AliyunOSSOperator(AliyunOSSProperties aliyunOSSProperties) {
        this.aliyunOSSProperties = aliyunOSSProperties;
    }

    /**
     * 上传文件到阿里云 OSS。
     *
     * <p>实现说明：
     * 1. 按 yyyy/MM 组织目录，便于后续按月份管理文件
     * 2. 使用 UUID 生成不重复的新文件名，避免文件覆盖
     * 3. AccessKey 和 Secret 从环境变量中读取，避免敏感信息写入代码仓库</p>
     *
     * @param content          文件字节数组
     * @param originalFilename 原始文件名
     * @return 上传结果，包含访问地址和实际存储文件名
     * @throws Exception OSS SDK 调用异常向上抛出
     */
    public UploadResult upload(byte[] content, String originalFilename) throws Exception {
        validateConfig();

        String endpoint = aliyunOSSProperties.getEndpoint();
        String bucketName = aliyunOSSProperties.getBucketName();
        String region = aliyunOSSProperties.getRegion();

        // 从环境变量中获取访问凭证。
        // 运行前请先在系统环境变量中配置：
        // OSS_ACCESS_KEY_ID
        // OSS_ACCESS_KEY_SECRET
        EnvironmentVariableCredentialsProvider credentialsProvider =
                CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        // 目录按年月归档，便于对象存储管理。
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

        // 生成一个不重复的新文件名，保留原文件后缀，避免文件扩展名丢失。
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;
        String objectName = dir + "/" + fileName;

        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));
        } finally {
            ossClient.shutdown();
        }

        UploadResult uploadResult = new UploadResult();
        uploadResult.setFileName(fileName);
        uploadResult.setFileUrl(buildFileUrl(endpoint, bucketName, objectName));
        return uploadResult;
    }

    /**
     * 校验 OSS 基础配置是否完整。
     */
    private void validateConfig() {
        if (!StringUtils.hasText(aliyunOSSProperties.getEndpoint())) {
            throw new BusinessException("OSS endpoint 未配置");
        }
        if (!StringUtils.hasText(aliyunOSSProperties.getBucketName())
                || "your-bucket-name".equals(aliyunOSSProperties.getBucketName())) {
            throw new BusinessException("OSS bucketName 未正确配置");
        }
        if (!StringUtils.hasText(aliyunOSSProperties.getRegion())) {
            throw new BusinessException("OSS region 未配置");
        }
    }

    /**
     * 拼接文件访问地址。
     *
     * @param endpoint   OSS endpoint
     * @param bucketName bucket 名称
     * @param objectName 对象完整路径
     * @return 文件访问地址
     */
    private String buildFileUrl(String endpoint, String bucketName, String objectName) {
        String[] endpointParts = endpoint.split("//");
        return endpointParts[0] + "//" + bucketName + "." + endpointParts[1] + "/" + objectName;
    }
}
