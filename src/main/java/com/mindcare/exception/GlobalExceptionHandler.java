package com.mindcare.exception;

import com.mindcare.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 *
 * <p>作用：
 * 1. 统一拦截业务异常和系统异常
 * 2. 统一转换成前端可识别的 Result 结构
 * 3. 避免在 Controller 中到处写 try-catch</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     *
     * @param e 业务异常
     * @return 统一错误结果
     */
    @ExceptionHandler
    public Result handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    /**
     * 处理其他未知异常。
     *
     * <p>未知异常统一返回固定提示，避免把底层异常细节直接暴露给前端。</p>
     *
     * @param e 未知异常
     * @return 统一错误结果
     */
    @ExceptionHandler
    public Result handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
