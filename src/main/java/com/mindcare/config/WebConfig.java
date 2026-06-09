package com.mindcare.config;

import com.mindcare.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置类。
 *
 * <p>当前主要负责两件事：
 * 1. 注册 token 拦截器
 * 2. 处理前后端分离开发时的跨域访问问题</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    public WebConfig(TokenInterceptor tokenInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
    }

    /**
     * 注册拦截器。
     *
     * <p>登录接口必须放行，否则前端在未登录状态下无法获取 token。
     * 同时放行 error 路径，避免 Spring 默认错误处理再次被拦截。</p>
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/index.html",
                        "/login",
                        "/error",
                        "/favicon.ico",
                        "/assets/**",
                        "/**/*.js",
                        "/**/*.css",
                        "/**/*.svg",
                        "/**/*.png",
                        "/**/*.jpg",
                        "/**/*.jpeg",
                        "/**/*.webp"
                );
    }

    /**
     * 配置跨域。
     *
     * <p>前端后续会使用 Vite 本地开发，端口通常和后端不同，
     * 所以这里提前放开跨域，方便本地联调登录接口。</p>
     *
     * @param registry 跨域配置注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
