# 后端模块说明

本文档汇总当前项目中最值得保留和讲解的后端实现点，替代原来分散的模块过程记录。

## 1. 登录认证模块

核心职责：

- 接收用户名和密码
- 查询 `sys_user`
- 校验账号状态与密码
- 生成 JWT
- 返回统一 `Result`

主要类：

- `LoginController`
- `LoginService`
- `LoginServiceImpl`
- `UserMapper`
- `JwtUtils`
- `TokenInterceptor`
- `WebConfig`

流程说明：

1. 前端调用 `POST /login`
2. `LoginController` 接收 `LoginInfo`
3. `LoginServiceImpl` 查询用户并校验密码
4. 校验成功后调用 `JwtUtils` 生成 token
5. 返回 `LoginUserInfo`
6. 前端后续请求通过请求头携带 `token`
7. `TokenInterceptor` 统一拦截并校验 token

## 2. 预约管理模块

核心功能：

- 新增预约
- 分页查询
- 条件查询
- 详情查询
- 取消预约
- 修改预约状态

主要类：

- `AppointmentController`
- `AppointmentService`
- `AppointmentServiceImpl`
- `AppointmentMapper`
- `AppointmentMapper.xml`

关键业务点：

- 预约状态固定为：
  - `1 待确认`
  - `2 已确认`
  - `3 已完成`
  - `4 已取消`
- 新增预约时优先根据 `schedule_id` 判断该时间段是否已被未取消预约占用
- 分页查询通过 `PageHelper` 实现
- 条件筛选和列表联表查询主要写在 XML 动态 SQL 中

## 3. 咨询记录模块

核心职责：

- 查询咨询记录列表
- 支持按预约、咨询师、用户等条件检索
- 在预约完成时写入咨询记录

主要类：

- `ConsultationRecordController`
- `ConsultationRecordService`
- `ConsultationRecordServiceImpl`
- `ConsultationRecordMapper`

关键业务点：

- 咨询记录与预约更适合按一对一业务关系处理
- 记录通常在预约已确认后，由咨询师补充填写
- 与“完成预约”动作存在联动关系

## 4. 反馈评价模块

核心职责：

- 查询反馈列表
- 展示评分、评价内容、关联预约信息
- 为统计模块提供评分基础数据

主要类：

- `FeedbackController`
- `FeedbackService`
- `FeedbackServiceImpl`
- `FeedbackMapper`

## 5. 文件上传模块

核心职责：

- 接收 `MultipartFile`
- 上传文件到阿里云 OSS
- 保存上传后的文件元数据
- 返回文件访问地址

主要类：

- `UploadController`
- `UploadService`
- `UploadServiceImpl`
- `UploadFileMapper`
- `AliyunOSSOperator`
- `AliyunOSSProperties`

适用场景：

- 用户或咨询师头像
- 咨询记录附件

## 6. 统计报表模块

核心职责：

- 预约状态分布统计
- 咨询师工作量统计
- 每月预约数量统计
- 反馈评分统计

主要类：

- `ReportController`
- `ReportService`
- `ReportServiceImpl`
- `ReportMapper`
- `ReportMapper.xml`

实现方式：

- 以 MySQL 聚合查询为主
- 典型 SQL 使用：
  - `group by`
  - `count`
  - `sum`
  - `case when`

## 7. resultMap + collection 场景

当前项目中最适合使用 `resultMap + collection` 的场景是：

- 查询咨询师详情，同时查询其可预约时间段列表

原因：

- 该业务天然是一对多关系
- 主对象是咨询师，子集合是时间段列表
- 适合集中封装成一个详情对象，便于前端直接展示

对应关系：

- 主表：`counselor`
- 子表：`counselor_schedule`

## 8. @Transactional 场景

当前项目中最典型的事务场景是：

- 完成预约时，同时写入咨询记录并修改预约状态

为什么要加事务：

- 这不是单表操作
- 任何一步失败都不应只提交一半数据
- 必须保证“咨询记录新增”和“预约状态更新”要么一起成功，要么一起回滚

## 9. 统一风格说明

项目整体保持传统 Spring Boot 后台管理风格：

- `Controller` 只负责接收请求和返回结果
- `Service` 负责业务处理
- `Mapper + XML` 负责数据库访问
- 统一返回 `Result`
- 分页统一返回 `PageResult`
- 异常统一交给 `GlobalExceptionHandler`

这套风格适合实习阶段学习、讲解和面试展示。
