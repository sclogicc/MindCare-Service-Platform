package com.mindcare.controller;

import com.mindcare.pojo.Result;
import com.mindcare.util.SubmitTokenStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 防重复提交控制器。
 *
 * <p>提供一次性提交令牌的获取接口。
 * 前端在提交表单前调用此接口获取 token，
 * 随后在 POST/PUT 请求中通过 {@code X-Submit-Token} 请求头携带。</p>
 */
@Slf4j
@RestController
public class AntiDuplicateController {

    /** 默认令牌有效期 5 分钟 */
    private static final int DEFAULT_TTL = 300;

    private final SubmitTokenStore submitTokenStore;

    public AntiDuplicateController(SubmitTokenStore submitTokenStore) {
        this.submitTokenStore = submitTokenStore;
    }

    /**
     * 获取一次性提交令牌。
     *
     * @return token 及其有效期
     */
    @GetMapping("/anti-duplicate/token")
    public Result getToken() {
        String token = submitTokenStore.generate(DEFAULT_TTL);
        log.debug("生成防重复提交令牌");
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("ttlSeconds", DEFAULT_TTL);
        return Result.success(data);
    }
}
