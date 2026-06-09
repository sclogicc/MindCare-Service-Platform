package com.mindcare.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindcare.pojo.Result;
import com.mindcare.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 登录校验拦截器。
 *
 * <p>当前项目使用拦截器，而不是过滤器，原因是：
 * 1. 更贴近参考项目的写法
 * 2. 更适合与 Spring MVC 控制器配合
 * 3. 便于后续按路径放行或扩展更多权限控制逻辑</p>
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 在请求到达 Controller 之前校验 token。
     *
     * <p>校验逻辑：
     * 1. OPTIONS 预检请求直接放行
     * 2. 从请求头获取 token
     * 3. token 为空则返回 401
     * 4. token 解析失败或已过期则返回 401
     * 5. 校验通过则放行请求</p>
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  目标处理器
     * @return 是否放行
     * @throws Exception 发生 IO 异常时抛出
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 前后端分离项目中，浏览器在跨域场景下会先发 OPTIONS 预检请求，
        // 这类请求不需要做 token 校验，直接放行即可。
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            log.info("请求未携带 token，拦截请求: {}", request.getRequestURI());
            writeUnauthorizedResponse(response, "未登录，请先登录");
            return false;
        }

        try {
            JwtUtils.parseToken(token);
        } catch (Exception e) {
            log.info("token 校验失败，拦截请求: {}", request.getRequestURI());
            writeUnauthorizedResponse(response, "登录信息已失效，请重新登录");
            return false;
        }

        return true;
    }

    /**
     * 返回 401 未授权响应。
     *
     * <p>这里返回统一 JSON 结构，而不是只写状态码，
     * 这样前端拦截器可以直接读取 message 并统一跳转登录页。</p>
     *
     * @param response 响应对象
     * @param message  错误提示
     * @throws Exception 写响应时可能抛出异常
     */
    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(Result.error(message)));
    }
}
