package com.mindcare.service;

import com.mindcare.pojo.LoginInfo;
import com.mindcare.pojo.LoginUserInfo;

/**
 * 登录认证业务接口。
 */
public interface LoginService {

    /**
     * 根据用户名和密码完成登录认证。
     *
     * @param loginInfo 登录参数
     * @return 登录成功返回前端需要的登录用户信息，失败返回 null
     */
    LoginUserInfo login(LoginInfo loginInfo);

    /**
     * 根据 token 获取当前登录用户信息。
     *
     * @param token JWT
     * @return 当前登录用户信息
     */
    LoginUserInfo getLoginUserInfo(String token);
}
