package com.mindcare.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统一分页返回类。
 *
 * <p>虽然当前阶段只实现登录模块，暂时不会直接使用分页，
 * 但该类属于整个后台项目的基础骨架，提前保留后续就不需要重复创建。</p>
 *
 * @param <T> 分页数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private Long total;
    private List<T> rows;
}
