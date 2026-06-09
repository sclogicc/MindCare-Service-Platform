package com.mindcare.service;

import com.mindcare.pojo.UploadResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传业务接口。
 */
public interface UploadService {

    /**
     * 上传文件到 OSS，并保存附件元数据。
     *
     * @param file         上传文件
     * @param businessType 业务类型
     * @param token        当前登录用户 token
     * @return 上传结果
     * @throws Exception 上传过程中可能抛出的异常
     */
    UploadResult upload(MultipartFile file, String businessType, String token) throws Exception;
}
