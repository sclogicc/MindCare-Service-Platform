package com.mindcare.service.impl;

import com.mindcare.exception.BusinessException;
import com.mindcare.mapper.UploadFileMapper;
import com.mindcare.pojo.UploadFile;
import com.mindcare.pojo.UploadResult;
import com.mindcare.service.UploadService;
import com.mindcare.utils.AliyunOSSOperator;
import com.mindcare.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 文件上传业务实现类。
 *
 * <p>处理流程：
 * 1. 校验上传文件是否合法
 * 2. 调用 OSS 工具类把文件上传到阿里云对象存储
 * 3. 解析当前登录用户 ID
 * 4. 把附件元数据写入 upload_file 表
 * 5. 返回前端需要的访问地址和附件主键</p>
 */
@Service
public class UploadServiceImpl implements UploadService {

    private final AliyunOSSOperator aliyunOSSOperator;
    private final UploadFileMapper uploadFileMapper;

    public UploadServiceImpl(AliyunOSSOperator aliyunOSSOperator, UploadFileMapper uploadFileMapper) {
        this.aliyunOSSOperator = aliyunOSSOperator;
        this.uploadFileMapper = uploadFileMapper;
    }

    @Override
    public UploadResult upload(MultipartFile file, String businessType, String token) throws Exception {
        validateFile(file);

        // 1. 上传到 OSS，拿到访问地址和实际存储文件名。
        UploadResult uploadResult = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename());

        // 2. 从 token 中解析当前上传人，便于后续业务追踪附件来源。
        Claims claims = JwtUtils.parseToken(token);
        Long uploaderId = Long.valueOf(String.valueOf(claims.get("id")));

        // 3. 保存附件元数据。
        UploadFile uploadFile = new UploadFile();
        uploadFile.setOriginalName(file.getOriginalFilename());
        uploadFile.setFileName(uploadResult.getFileName());
        uploadFile.setFileUrl(uploadResult.getFileUrl());
        uploadFile.setFileType(file.getContentType());
        uploadFile.setBusinessType(StringUtils.hasText(businessType) ? businessType : "common");
        uploadFile.setUploaderId(uploaderId);
        uploadFile.setCreateTime(LocalDateTime.now());
        uploadFile.setUpdateTime(LocalDateTime.now());
        uploadFileMapper.insert(uploadFile);

        // 4. 把数据库主键回填到返回结果中，便于后续表单直接使用 attachmentFileId。
        uploadResult.setId(uploadFile.getId());
        uploadResult.setOriginalName(file.getOriginalFilename());
        return uploadResult;
    }

    /**
     * 校验上传文件是否合法。
     *
     * @param file 上传文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (!StringUtils.hasText(file.getOriginalFilename())) {
            throw new BusinessException("上传文件名不能为空");
        }
    }
}
