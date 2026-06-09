package com.mindcare.pojo;

import lombok.Data;

/**
 * 咨询师下拉选项对象。
 *
 * <p>该对象只服务于前端“新增预约”表单中的咨询师选择场景，
 * 因此只保留列表展示和选中所需的关键字段，避免把详情对象直接用于下拉框。</p>
 */
@Data
public class CounselorOption {

    /**
     * 咨询师主键。
     */
    private Long id;

    /**
     * 咨询师账号主键，对应 sys_user.id。
     *
     * <p>该字段主要用于前端根据当前登录账号定位“我对应的咨询师身份”，
     * 例如咨询师登录后，首页只看自己的预约数据。</p>
     */
    private Long userId;

    /**
     * 咨询师姓名，来自 sys_user.name。
     */
    private String counselorName;

    /**
     * 擅长方向。
     */
    private String specialty;

    /**
     * 职称。
     */
    private String title;
}
