package com.mindcare.controller;

import com.mindcare.pojo.Result;
import com.mindcare.pojo.UploadResult;
import com.mindcare.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器。
 *
 * <p>保持和 tlias-web-management 接近的使用方式：
 * - 前端通过 MultipartFile 提交文件
 * - Controller 只负责接收参数和返回结果
 * - 具体上传到 OSS、记录附件元数据等逻辑交给 Service 层处理</p>
 */
@Slf4j
@RestController
@RequestMapping
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    /**
     * 上传文件。
     *
     * <p>接口说明：
     * 1. 使用 MultipartFile 接收前端上传文件
     * 2. businessType 用于标识业务场景，例如 avatar、consultation_attachment
     * 3. token 头用于识别当前上传人，并保存到 upload_file 表中</p>
     *
     * @param file         上传文件
     * @param businessType 业务类型
     * @param token        当前登录用户 token
     * @return 返回附件主键、文件访问地址等信息
     * @throws Exception 上传过程中的异常继续向上抛，由全局异常处理器统一处理
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file,
                         @RequestParam(value = "businessType", defaultValue = "common") String businessType,
                         @RequestHeader("token") String token) throws Exception {
        log.info("文件上传: fileName={}, businessType={}", file.getOriginalFilename(), businessType);
        UploadResult uploadResult = uploadService.upload(file, businessType, token);
        return Result.success(uploadResult);
    }
}
