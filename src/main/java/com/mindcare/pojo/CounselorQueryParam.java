package com.mindcare.pojo;

import lombok.Data;

/**
 * 咨询师分页查询参数。
 */
@Data
public class CounselorQueryParam {

    private Integer page;
    private Integer pageSize;
    private String counselorName;
    private String specialty;
    private Integer status;
}
