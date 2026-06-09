package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 咨询师分页列表展示对象。
 */
@Data
public class CounselorPageItem {

    private Long id;
    private Long userId;
    private String counselorName;
    private String phone;
    private String specialty;
    private String title;
    private Integer yearsOfExperience;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
