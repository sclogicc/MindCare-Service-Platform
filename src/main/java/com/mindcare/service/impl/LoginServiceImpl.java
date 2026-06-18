package com.mindcare.service.impl;

import com.mindcare.constant.EnableStatus;
import com.mindcare.constant.UserRole;
import com.mindcare.exception.BusinessException;
import com.mindcare.mapper.UserMapper;
import com.mindcare.pojo.LoginInfo;
import com.mindcare.pojo.LoginUserInfo;
import com.mindcare.pojo.User;
import com.mindcare.service.LoginService;
import com.mindcare.util.PasswordUtil;
import com.mindcare.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 登录认证业务实现类。
 *
 * <p>这里保持"传统后台项目"的实现风格：
 * 1. Service 负责登录核心流程
 * 2. Mapper 只负责查库
 * 3. JWT 生成与解析交给工具类
 * 4. 业务异常交给全局异常处理器统一处理</p>
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private final UserMapper userMapper;

    public LoginServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public LoginUserInfo login(LoginInfo loginInfo) {
        // 基础参数（用户名/密码）的非空校验已由 Controller 层 @Valid 保证
        User user = userMapper.selectByUsername(loginInfo.getUsername());
        if (user == null) {
            log.info("登录失败: 用户名不存在 username={}", loginInfo.getUsername());
            return null;
        }

        // BCrypt 密码校验（加密后的密码存储在数据库中）
        if (!PasswordUtil.matches(loginInfo.getPassword(), user.getPassword())) {
            log.info("登录失败: 密码错误 username={}", loginInfo.getUsername());
            return null;
        }

        // 只有启用状态的账号才允许登录
        if (!Objects.equals(user.getStatus(), EnableStatus.ENABLED.getCode())) {
            throw new BusinessException("当前账号已被禁用，无法登录");
        }

        // 组装 token 中的 claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("name", user.getName());
        claims.put("role", user.getRole());

        // 生成 JWT
        String token = JwtUtils.generateToken(claims);

        log.info("登录成功: username={}, role={}",
                user.getUsername(), UserRole.fromCode(user.getRole()).getDescription());
        return buildLoginUserInfo(user, token);
    }

    @Override
    public LoginUserInfo getLoginUserInfo(String token) {
        try {
            Claims claims = JwtUtils.parseToken(token);
            Long userId = Long.valueOf(String.valueOf(claims.get("id")));

            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("当前登录用户不存在");
            }
            if (!Objects.equals(user.getStatus(), EnableStatus.ENABLED.getCode())) {
                throw new BusinessException("当前账号已被禁用");
            }

            return buildLoginUserInfo(user, token);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("登录信息已失效，请重新登录");
        }
    }

    private LoginUserInfo buildLoginUserInfo(User user, String token) {
        LoginUserInfo loginUserInfo = new LoginUserInfo();
        loginUserInfo.setId(user.getId());
        loginUserInfo.setUsername(user.getUsername());
        loginUserInfo.setName(user.getName());
        loginUserInfo.setPhone(user.getPhone());
        loginUserInfo.setRole(user.getRole());
        loginUserInfo.setToken(token);
        return loginUserInfo;
    }
}
