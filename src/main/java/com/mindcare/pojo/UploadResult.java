package com.mindcare.pojo;

import lombok.Data;

/**
 * 文件上传返回对象。
 *
 * <p>虽然前端最关心的是文件访问地址，
 * 但这里额外返回附件主键和存储文件名，便于后续业务表单直接保存附件关联关系。</p>
 */
@Data
public class UploadResult {

    /**
     * 附件主键。
     */
    private Long id;

    /**
     * 原始文件名。
     */
    private String originalName;

    /**
     * OSS 中实际存储的文件名。
     */
    private String fileName;

    /**
     * 文件访问地址。
     */
    private String fileUrl;
}
