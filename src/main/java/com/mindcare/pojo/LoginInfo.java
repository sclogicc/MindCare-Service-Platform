package com.mindcare.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求参数。
 *
 * <p>该类只承载登录时前端提交的用户名和密码，
 * 不混入数据库表中的其他字段，保证对象职责单一。</p>
 */
@Data
public class LoginInfo {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
