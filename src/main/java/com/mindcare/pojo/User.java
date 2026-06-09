package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类，对应数据库表 sys_user。
 */
@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private Integer gender;
    private Integer role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
