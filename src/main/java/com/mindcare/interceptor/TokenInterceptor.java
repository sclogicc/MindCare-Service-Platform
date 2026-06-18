package com.mindcare.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindcare.annotation.RequireRole;
import com.mindcare.pojo.Result;
import com.mindcare.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * JWT 登录校验与角色权限拦截器。
 *
 * <p>职责：
 * 1. 校验请求中的 JWT token 是否有效
 * 2. 将当前登录用户信息（userId、role、name）存入 request attribute
 * 3. 检查方法上的 {@link RequireRole} 注解，拦截越权请求</p>
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** request attribute key */
    public static final String ATTR_USER_ID = "userId";
    public static final String ATTR_ROLE = "role";
    public static final String ATTR_USER_NAME = "userName";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            log.info("请求未携带 token，拦截请求: {}", request.getRequestURI());
            writeResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录，请先登录");
            return false;
        }

        Claims claims;
        try {
            claims = JwtUtils.parseToken(token);
        } catch (Exception e) {
            log.info("token 校验失败，拦截请求: {}", request.getRequestURI());
            writeResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "登录信息已失效，请重新登录");
            return false;
        }

        // 将用户信息存入 request attribute，供 Controller 使用
        Integer userId = claims.get("id", Integer.class);
        Integer role = claims.get("role", Integer.class);
        String name = claims.get("name", String.class);

        request.setAttribute(ATTR_USER_ID, userId != null ? userId.longValue() : null);
        request.setAttribute(ATTR_ROLE, role);
        request.setAttribute(ATTR_USER_NAME, name);

        // 方法级角色校验
        if (handler instanceof HandlerMethod hm) {
            RequireRole requireRole = getRequireRole(hm);
            if (requireRole != null) {
                int[] allowedRoles = requireRole.value();
                if (allowedRoles.length > 0 && role != null) {
                    boolean hasPermission = Arrays.stream(allowedRoles).anyMatch(r -> r == role);
                    if (!hasPermission) {
                        log.info("角色越权访问: URI={}, 用户角色={}, 需要角色={}",
                                request.getRequestURI(), role, Arrays.toString(allowedRoles));
                        writeResponse(response, HttpServletResponse.SC_FORBIDDEN, "权限不足，无法执行此操作");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * 从 HandlerMethod 上获取 @RequireRole 注解。
     * 优先从方法上取，方法上没有则从类上取。
     */
    private RequireRole getRequireRole(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        RequireRole annotation = method.getAnnotation(RequireRole.class);
        if (annotation != null) {
            return annotation;
        }
        return handlerMethod.getBeanType().getAnnotation(RequireRole.class);
    }

    private void writeResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(Result.error(message)));
    }
}
