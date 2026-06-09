package com.mindcare.controller;

import com.mindcare.pojo.LoginInfo;
import com.mindcare.pojo.LoginUserInfo;
import com.mindcare.pojo.Result;
import com.mindcare.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录认证控制器。
 *
 * <p>当前控制器只负责处理与认证直接相关的请求，
 * 具体的用户查询、密码校验、JWT 生成都放在 Service 层完成，
 * 保持 Controller 的职责足够单一，和 tlias-web-management 的风格保持一致。</p>
 */
@Slf4j
@RestController
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * 登录接口。
     *
     * <p>处理流程：
     * 1. 前端提交用户名和密码
     * 2. 调用 Service 查询用户并校验密码
     * 3. 校验成功后生成 JWT
     * 4. 统一使用 Result 返回给前端</p>
     *
     * @param loginInfo 登录参数
     * @return 登录成功时返回用户信息和 token，失败时返回统一错误结果
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginInfo loginInfo) {
        log.info("用户登录请求: {}", loginInfo.getUsername());

        LoginUserInfo loginUserInfo = loginService.login(loginInfo);
        if (loginUserInfo == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(loginUserInfo);
    }

    /**
     * 获取当前登录用户信息。
     *
     * <p>该接口通常用于前端页面刷新后重新拉取当前登录人信息，
     * 由拦截器先完成 token 校验，Controller 中只负责调用业务层查询并返回数据。</p>
     *
     * @param token 请求头中的 JWT
     * @return 当前登录用户信息
     */
    @GetMapping("/login/info")
    public Result getLoginUserInfo(@RequestHeader("token") String token) {
        return Result.success(loginService.getLoginUserInfo(token));
    }
}
