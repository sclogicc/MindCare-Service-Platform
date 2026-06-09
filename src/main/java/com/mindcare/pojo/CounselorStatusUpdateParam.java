package com.mindcare.pojo;

import lombok.Data;

/**
 * 咨询师状态修改参数。
 */
@Data
public class CounselorStatusUpdateParam {

    private Long id;
    private Integer status;
}
