package com.mindcare.pojo;

import lombok.Data;

/**
 * 登录请求参数。
 *
 * <p>该类只承载登录时前端提交的用户名和密码，
 * 不混入数据库表中的其他字段，保证对象职责单一。</p>
 */
@Data
public class LoginInfo {

    private String username;
    private String password;
}
