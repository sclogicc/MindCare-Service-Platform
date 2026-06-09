package com.mindcare.service.impl;

import com.mindcare.exception.BusinessException;
import com.mindcare.mapper.UserMapper;
import com.mindcare.pojo.LoginInfo;
import com.mindcare.pojo.LoginUserInfo;
import com.mindcare.pojo.User;
import com.mindcare.service.LoginService;
import com.mindcare.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 登录认证业务实现类。
 *
 * <p>这里保持“传统后台项目”的实现风格：
 * 1. Service 负责登录核心流程
 * 2. Mapper 只负责查库
 * 3. JWT 生成与解析交给工具类
 * 4. 业务异常交给全局异常处理器统一处理</p>
 */
@Service
public class LoginServiceImpl implements LoginService {

    private final UserMapper userMapper;

    public LoginServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public LoginUserInfo login(LoginInfo loginInfo) {
        // 对基础参数做空值校验，避免无意义查询进入数据库层。
        if (loginInfo == null
                || !StringUtils.hasText(loginInfo.getUsername())
                || !StringUtils.hasText(loginInfo.getPassword())) {
            return null;
        }

        // 1. 根据用户名查询账号信息。
        User user = userMapper.selectByUsername(loginInfo.getUsername());
        if (user == null) {
            return null;
        }

        // 2. 校验密码。
        // 当前项目按教学型后台项目处理，直接对比明文密码，方便前期联调和讲解。
        // 如果后续项目想提升安全性，可以再替换为加密密码方案。
        if (!Objects.equals(user.getPassword(), loginInfo.getPassword())) {
            return null;
        }

        // 3. 校验账号状态。
        // 只有启用状态的账号才允许登录，禁用账号直接抛出业务异常。
        if (!Objects.equals(user.getStatus(), 1)) {
            throw new BusinessException("当前账号已被禁用，无法登录");
        }

        // 4. 组装 token 中的 claims。
        // 这里放入后续常用的用户核心字段，便于后续模块直接从 token 中解析当前用户身份。
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("name", user.getName());
        claims.put("role", user.getRole());

        // 5. 生成 JWT。
        String token = JwtUtils.generateToken(claims);

        // 6. 返回前端登录成功需要的用户信息。
        return buildLoginUserInfo(user, token);
    }

    @Override
    public LoginUserInfo getLoginUserInfo(String token) {
        try {
            // 1. 解析 token，获取当前登录用户 ID。
            Claims claims = JwtUtils.parseToken(token);
            Long userId = Long.valueOf(String.valueOf(claims.get("id")));

            // 2. 再次查询数据库，拿到最新用户信息。
            // 这么做的好处是：如果用户被禁用或资料更新，前端下一次获取用户信息时能拿到数据库中的最新状态。
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("当前登录用户不存在");
            }
            if (!Objects.equals(user.getStatus(), 1)) {
                throw new BusinessException("当前账号已被禁用");
            }

            return buildLoginUserInfo(user, token);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("登录信息已失效，请重新登录");
        }
    }

    /**
     * 统一组装返回给前端的登录用户信息。
     *
     * <p>单独抽成私有方法有两个好处：
     * 1. 避免登录接口和“获取当前登录人”接口重复写组装逻辑
     * 2. 后续如果返回字段有调整，只需要改一个地方</p>
     *
     * @param user  数据库中的用户实体
     * @param token JWT，可为空
     * @return 返回给前端的登录用户信息
     */
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
