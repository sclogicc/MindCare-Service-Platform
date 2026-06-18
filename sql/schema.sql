-- =========================================================
-- 心理咨询服务预约平台 (MindCare Service Platform)
-- 数据库建表脚本
--
-- 数据库: mindcare_service_platform
-- 字符集: utf8mb4
-- 引擎:   InnoDB
--
-- 执行顺序: 先执行本脚本建表，再执行 init_test_data.sql 导入数据
-- =========================================================

CREATE DATABASE IF NOT EXISTS mindcare_service_platform
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE mindcare_service_platform;

-- =========================================================
-- 1. 系统用户表 (sys_user)
--    统一登录账号管理，管理员/咨询师/普通用户共用此表
-- =========================================================
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户主键',
    username    VARCHAR(64)     NOT NULL                 COMMENT '登录用户名',
    password    VARCHAR(255)    NOT NULL                 COMMENT '登录密码',
    name        VARCHAR(64)     NOT NULL                 COMMENT '真实姓名',
    phone       VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    gender      TINYINT         DEFAULT NULL             COMMENT '性别: 1-男 2-女',
    role        TINYINT         NOT NULL                 COMMENT '角色: 1-管理员 2-咨询师 3-普通用户',
    status      TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0-停用 1-启用',
    create_time DATETIME        NOT NULL                 COMMENT '创建时间',
    update_time DATETIME        NOT NULL                 COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =========================================================
-- 2. 咨询师表 (counselor)
--    与 sys_user 一对一关联，存储咨询师业务专用信息
-- =========================================================
CREATE TABLE IF NOT EXISTS counselor (
    id                  BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '咨询师主键',
    user_id             BIGINT       NOT NULL                 COMMENT '关联用户ID',
    specialty           VARCHAR(255) DEFAULT NULL             COMMENT '擅长方向',
    title               VARCHAR(128) DEFAULT NULL             COMMENT '职称',
    years_of_experience INT          DEFAULT NULL             COMMENT '从业年限',
    introduction        TEXT         DEFAULT NULL             COMMENT '个人简介',
    avatar_file_id      BIGINT       DEFAULT NULL             COMMENT '头像附件ID',
    status              TINYINT      NOT NULL DEFAULT 1       COMMENT '状态: 0-停用 1-启用',
    create_time         DATETIME     NOT NULL                 COMMENT '创建时间',
    update_time         DATETIME     NOT NULL                 COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咨询师表';

-- =========================================================
-- 3. 可预约时间段表 (counselor_schedule)
--    咨询师与时间段为 1:N 关系
-- =========================================================
CREATE TABLE IF NOT EXISTS counselor_schedule (
    id            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '时间段主键',
    counselor_id  BIGINT       NOT NULL                 COMMENT '所属咨询师ID',
    schedule_date DATE         NOT NULL                 COMMENT '预约日期',
    start_time    TIME         NOT NULL                 COMMENT '开始时间',
    end_time      TIME         NOT NULL                 COMMENT '结束时间',
    status        TINYINT      NOT NULL DEFAULT 1       COMMENT '状态: 0-停用 1-可预约',
    remark        VARCHAR(512) DEFAULT NULL             COMMENT '备注说明',
    create_time   DATETIME     NOT NULL                 COMMENT '创建时间',
    update_time   DATETIME     NOT NULL                 COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_counselor_date (counselor_id, schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='可预约时间段表';

-- =========================================================
-- 4. 预约记录表 (appointment)
--    关联用户、咨询师、时间段，记录完整预约信息
--    status: 1-待确认 2-已确认 3-已完成 4-已取消
-- =========================================================
CREATE TABLE IF NOT EXISTS appointment (
    id              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '预约主键',
    appointment_no  VARCHAR(32)  NOT NULL                 COMMENT '预约单号',
    user_id         BIGINT       NOT NULL                 COMMENT '预约用户ID',
    counselor_id    BIGINT       NOT NULL                 COMMENT '预约咨询师ID',
    schedule_id     BIGINT       NOT NULL                 COMMENT '预约时间段ID',
    status          TINYINT      NOT NULL                 COMMENT '状态: 1-待确认 2-已确认 3-已完成 4-已取消',
    contact_phone   VARCHAR(20)  DEFAULT NULL             COMMENT '联系电话',
    remark          VARCHAR(512) DEFAULT NULL             COMMENT '预约备注',
    cancel_reason   VARCHAR(512) DEFAULT NULL             COMMENT '取消原因',
    create_time     DATETIME     NOT NULL                 COMMENT '创建时间',
    update_time     DATETIME     NOT NULL                 COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_appointment_no (appointment_no),
    KEY idx_user_id (user_id),
    KEY idx_counselor_id (counselor_id),
    KEY idx_schedule_id (schedule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';

-- =========================================================
-- 5. 咨询记录表 (consultation_record)
--    与 appointment 一对一关联，记录咨询完成后的摘要和建议
-- =========================================================
CREATE TABLE IF NOT EXISTS consultation_record (
    id                 BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '记录主键',
    appointment_id     BIGINT   NOT NULL                 COMMENT '关联预约ID',
    counselor_id       BIGINT   NOT NULL                 COMMENT '责任咨询师ID',
    summary            TEXT     DEFAULT NULL             COMMENT '咨询摘要',
    suggestion         TEXT     DEFAULT NULL             COMMENT '咨询建议',
    attachment_file_id BIGINT   DEFAULT NULL             COMMENT '附件ID',
    create_time        DATETIME NOT NULL                 COMMENT '创建时间',
    update_time        DATETIME NOT NULL                 COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_appointment_id (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咨询记录表';

-- =========================================================
-- 6. 反馈评价表 (feedback)
--    与 appointment 一对一关联，用户对已完成预约进行评分和评价
--    score: 1-5 分
-- =========================================================
CREATE TABLE IF NOT EXISTS feedback (
    id              BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '反馈主键',
    appointment_id  BIGINT   NOT NULL                 COMMENT '关联预约ID',
    user_id         BIGINT   NOT NULL                 COMMENT '评价用户ID',
    counselor_id    BIGINT   NOT NULL                 COMMENT '被评咨询师ID',
    score           TINYINT  NOT NULL                 COMMENT '评分: 1-5',
    content         TEXT     DEFAULT NULL             COMMENT '评价内容',
    is_anonymous    TINYINT  DEFAULT 0                COMMENT '是否匿名: 0-否 1-是',
    create_time     DATETIME NOT NULL                 COMMENT '创建时间',
    update_time     DATETIME NOT NULL                 COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_appointment_id (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈评价表';

-- =========================================================
-- 7. 上传文件表 (upload_file)
--    存储文件元数据，被咨询师头像和咨询记录附件引用
-- =========================================================
CREATE TABLE IF NOT EXISTS upload_file (
    id            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '文件主键',
    original_name VARCHAR(255) DEFAULT NULL             COMMENT '原始文件名',
    file_name     VARCHAR(255) DEFAULT NULL             COMMENT '存储文件名',
    file_url      VARCHAR(512) DEFAULT NULL             COMMENT '文件访问URL',
    file_type     VARCHAR(32)  DEFAULT NULL             COMMENT '文件类型: pdf/docx/jpg/png',
    business_type VARCHAR(32)  DEFAULT NULL             COMMENT '业务类型: AVATAR/CONSULTATION_ATTACHMENT',
    uploader_id   BIGINT       DEFAULT NULL             COMMENT '上传人ID',
    create_time   DATETIME     NOT NULL                 COMMENT '创建时间',
    update_time   DATETIME     NOT NULL                 COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='上传文件表';
