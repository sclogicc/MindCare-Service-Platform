package com.mindcare.mapper;

import com.mindcare.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper。
 *
 * <p>当前登录模块只需要两类查询：
 * 1. 根据用户名查用户，用于登录
 * 2. 根据主键查用户，用于登录后获取当前用户信息</p>
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户。
     *
     * @param username 登录账号
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据主键查询用户。
     *
     * @param id 用户主键
     * @return 用户信息
     */
    User selectById(@Param("id") Long id);
}
