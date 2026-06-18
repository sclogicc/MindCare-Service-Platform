package com.mindcare.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindcare.annotation.PreventDuplicate;
import com.mindcare.annotation.RequireRole;
import com.mindcare.pojo.Result;
import com.mindcare.util.SubmitTokenStore;
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
 * JWT 登录校验 + 角色权限 + 防重复提交拦截器。
 *
 * <p>职责：
 * 1. 校验 JWT token 有效性
 * 2. 将当前用户信息存入 request attribute
 * 3. 检查 {@link RequireRole} 注解，拦截越权请求
 * 4. 检查 {@link PreventDuplicate} 注解，拦截重复提交</p>
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final String ATTR_USER_ID = "userId";
    public static final String ATTR_ROLE = "role";
    public static final String ATTR_USER_NAME = "userName";

    /** 防重复提交的请求头 */
    private static final String HEADER_SUBMIT_TOKEN = "X-Submit-Token";

    private final SubmitTokenStore submitTokenStore;

    public TokenInterceptor(SubmitTokenStore submitTokenStore) {
        this.submitTokenStore = submitTokenStore;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 放行令牌获取接口（无需认证）
        if (request.getRequestURI().endsWith("/anti-duplicate/token")) {
            return true;
        }

        // --- JWT 认证 ---
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

        Integer userId = claims.get("id", Integer.class);
        Integer role = claims.get("role", Integer.class);
        String name = claims.get("name", String.class);

        request.setAttribute(ATTR_USER_ID, userId != null ? userId.longValue() : null);
        request.setAttribute(ATTR_ROLE, role);
        request.setAttribute(ATTR_USER_NAME, name);

        if (handler instanceof HandlerMethod hm) {
            // --- 角色权限校验 ---
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

            // --- 防重复提交校验 ---
            PreventDuplicate preventDuplicate = hm.getMethodAnnotation(PreventDuplicate.class);
            if (preventDuplicate != null) {
                String submitToken = request.getHeader(HEADER_SUBMIT_TOKEN);
                if (!StringUtils.hasText(submitToken)) {
                    log.info("缺少防重复提交令牌: URI={}", request.getRequestURI());
                    writeResponse(response, HttpServletResponse.SC_BAD_REQUEST, "缺少提交令牌，请刷新页面后重试");
                    return false;
                }
                if (!submitTokenStore.validateAndConsume(submitToken)) {
                    log.info("重复提交被拦截: URI={}", request.getRequestURI());
                    writeResponse(response, HttpServletResponse.SC_CONFLICT, "请勿重复提交");
                    return false;
                }
            }
        }

        return true;
    }

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
