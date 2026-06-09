package com.mindcare.pojo;

import lombok.Data;

/**
 * 咨询师信息修改参数。
 *
 * <p>这里先只开放 counselor 业务表字段，
 * 不在当前阶段引入账号信息联动修改，避免把收尾工作重新做成重型模块。</p>
 */
@Data
public class CounselorUpdateParam {

    private Long id;
    private String specialty;
    private String title;
    private Integer yearsOfExperience;
    private String introduction;
    private Long avatarFileId;
    private Integer status;
}
