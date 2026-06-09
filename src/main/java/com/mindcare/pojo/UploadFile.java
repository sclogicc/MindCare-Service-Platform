package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 附件实体类，对应 upload_file 表。
 *
 * <p>当前事务场景中不会直接修改附件内容，
 * 但如果咨询记录携带了 attachmentFileId，仍然需要校验附件是否真实存在。</p>
 */
@Data
public class UploadFile {

    private Long id;
    private String originalName;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private String businessType;
    private Long uploaderId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
