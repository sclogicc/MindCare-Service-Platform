package com.mindcare.pojo;

import lombok.Data;

/**
 * 登录成功后返回给前端的用户信息。
 *
 * <p>该对象和数据库实体 User 分离，
 * 目的是避免把 password、status 等不需要暴露给前端的字段直接返回出去。</p>
 */
@Data
public class LoginUserInfo {

    private Long id;
    private String username;
    private String name;
    private String phone;
    private Integer role;
    private String token;
}
