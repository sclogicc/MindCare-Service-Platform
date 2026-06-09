package com.mindcare.exception;

/**
 * 自定义业务异常。
 *
 * <p>用于承载“业务上可以预期”的错误，
 * 例如账号被禁用、登录信息失效、用户不存在等场景。</p>
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
