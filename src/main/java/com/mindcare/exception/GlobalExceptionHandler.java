package com.mindcare.exception;

import com.mindcare.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器。
 *
 * <p>作用：
 * 1. 统一拦截业务异常和系统异常
 * 2. 统一处理参数校验异常
 * 3. 统一转换成前端可识别的 Result 结构
 * 4. 避免在 Controller 中到处写 try-catch</p>
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
     * 处理 @Valid @RequestBody 校验失败异常。
     *
     * <p>当请求体参数不符合 Jakarta Validation 注解约束时，
     * Spring 会抛出该异常。这里提取第一个字段校验错误信息返回。</p>
     *
     * @param e 方法参数校验异常
     * @return 统一错误结果
     */
    @ExceptionHandler
    public Result handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数校验失败: {}", message);
        return Result.error(message);
    }

    /**
     * 处理 @Validated 方法级校验失败异常。
     *
     * @param e 约束违反异常
     * @return 统一错误结果
     */
    @ExceptionHandler
    public Result handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.error("参数校验失败: {}", message);
        return Result.error(message);
    }

    /**
     * 处理表单绑定校验失败异常。
     *
     * @param e 绑定异常
     * @return 统一错误结果
     */
    @ExceptionHandler
    public Result handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数绑定失败: {}", message);
        return Result.error(message);
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
