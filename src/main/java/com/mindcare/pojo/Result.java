package com.mindcare.pojo;

import lombok.Data;

/**
 * 统一返回结果类。
 *
 * <p>保持和 tlias-web-management 一致的风格：
 * - code: 1 表示成功，0 表示失败
 * - msg: 响应消息
 * - data: 业务数据</p>
 */
@Data
public class Result {

    private Integer code;
    private String msg;
    private Object data;

    public static Result success() {
        Result result = new Result();
        result.code = 1;
        result.msg = "success";
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.code = 1;
        result.msg = "success";
        result.data = data;
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.code = 0;
        result.msg = msg;
        return result;
    }
}
