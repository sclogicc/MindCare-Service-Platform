# 回归测试记录

## 1. 文档说明

本文件用于记录每一轮缺陷修复后的回归测试执行情况，确保修复没有引入新的问题，并保留项目逐步完善的证据。

---

## 2. 回归记录模板

### 回归批次：REG-001（首轮基线测试）

- 回归日期：2026-06-18
- 对应缺陷：BUG-001 ~ BUG-004（首轮发现）
- 对应提交：无（修复前基线测试）
- 回归范围：
  - 登录认证（5 用例）
  - 权限与角色（4 用例）
  - 咨询师管理（4 用例）
  - 时间段管理（5 用例）
  - 预约管理（7 用例）
  - 咨询记录（5 用例）
  - 反馈评价（5 用例）
  - 统计报表（4 用例）
- 回归结果：
  - 通过：35 用例
  - 不通过：4 用例（全部集中在权限控制模块 AUTHZ-001 ~ AUTHZ-004）
  - 未执行：4 用例（文件上传模块，需 OSS 配置）
- 结论：
  - 核心业务逻辑（预约、咨询记录、评价、报表）运行正常，状态流转、重复拦截、参数校验均已生效
  - 角色访问控制完全缺失，需作为下一轮 P0 修复重点

---

### 回归批次：REG-002（权限控制修复后回归）

- 回归日期：2026-06-18
- 对应缺陷：BUG-001, BUG-002, BUG-003, BUG-004
- 对应提交：feat-role-access（待提交）
- 修复内容：
  - 新增 `@RequireRole` 注解（支持方法级和类级）
  - 增强 `TokenInterceptor`：解析 JWT 后存储 userId/role/name 到 request attribute，并检查 `@RequireRole` 注解
  - `ReportController`：类级 `@RequireRole(ADMIN)`
  - `CounselorController`：管理类操作（PUT/update、PUT/status、GET/manage）加 `@RequireRole(ADMIN)`
  - `ScheduleController`：增删改操作加 `@RequireRole(ADMIN)`
  - `FeedbackController`：删除操作加 `@RequireRole(ADMIN)`
  - `ConsultationRecordController`：完成操作加 `@RequireRole({ADMIN, COUNSELOR})`
  - `AppointmentController`：`page()` 增加 `applyRoleFilter`（普通用户按 userId 过滤，咨询师按 counselorId 过滤）；`getById()` 增加 `checkAppointmentAccess`（越权访问抛 BusinessException）
  - `CounselorMapper` 新增 `selectIdByUserId` 方法
- 回归范围：权限模块全部 4 个用例（AUTHZ-001 ~ AUTHZ-004）
- 回归结果：4/4 通过
- 结论：角色访问控制已全面生效

---

### 回归批次：REG-003（参数校验增强后回归）

- 回归日期：2026-06-18
- 对应提交：e8fb345（参数校验统一迁移）
- 修复内容：
  - 新增 `spring-boot-starter-validation` 依赖
  - 9 个 POJO 类添加 Jakarta Bean Validation 注解（@NotNull、@NotBlank、@Min、@Max）
  - 7 个 Controller 添加 @Validated + @Valid
  - GlobalExceptionHandler 新增 3 个校验异常处理器
  - 7 个 Service 类删除 ~40 行手动校验代码
- 回归范围：
  - 参数校验专项（5 用例）：空用户名、空 userId、空 score、评分超范围、正常登录
  - 权限回归（4 用例）：403 拦截、数据隔离
  - 业务规则回归（5 用例）：过去日期拒绝、时间重叠拒绝、状态流转拦截等
- 回归结果：14/14 通过
- 结论：声明式参数校验全面生效，已有业务规则未受影响

---

### 回归批次：REG-004（枚举常量化 + 状态机完善 + 操作日志）

- 回归日期：2026-06-18
- 对应提交：c5d24a5（feat: 枚举常量化 + 状态机完善 + 操作日志 + DDL建表脚本）
- 修复内容：
  - 新增 `AppointmentStatus`、`EnableStatus`、`UserRole` 三个枚举类，消除 4 个 Service 中重复的状态常量
  - `AppointmentStatus` 内置状态机 `canTransitionTo()`，统一状态流转校验入口
  - 所有 Service 类添加 @Slf4j + 关键操作日志（登录、预约创建/取消/状态变更、咨询完成、评价提交/删除、时间段/咨询师状态变更）
  - 新增 `sql/schema.sql` DDL 建表脚本
- 回归范围：全部 8 个模块功能回归（39 用例）
- 回归结果：39/39 通过（含登录 5 + 权限 4 + 咨询师 4 + 时间段 5 + 预约 7 + 咨询记录 5 + 反馈 5 + 报表 4）
- 结论：枚举常量化与状态机运行正常，已有业务逻辑未受影响

---

### 回归批次：REG-005（OSS 配置更新 + 文件上传模块验证）

- 回归日期：2026-06-21
- 对应提交：待提交
- 修复内容：
  - 更新 OSS Bucket 配置为 `mind-careservice-platform`（华北2 北京）
  - 验证 OSS AccessKey 环境变量配置正确
  - 执行文件上传模块全部 4 个测试用例（UP-001 ~ UP-004）
- 回归范围：文件上传模块（4 用例）
- 回归结果：4/4 通过
  - UP-001（合法文件上传）：上传成功，返回 id、fileName、fileUrl
  - UP-002（空文件拦截）：返回 "上传文件不能为空"
  - UP-003（空文件名拦截）：通过代码审查，validateFile() 校验 file.getOriginalFilename()
  - UP-004（OSS 配置缺失提示）：通过代码审查，AliyunOSSOperator.validateConfig() 校验三要素
- 结论：文件上传模块功能正常，OSS 配置生效。至此全部 9 个模块 52 个测试用例全部完成并通过

---

## 3. 建议记录方式

每一轮回归至少记录以下内容：

- 修复针对了哪些缺陷
- 本轮重点验证了哪些模块
- 回归是否通过
- 是否产生新的连带问题

---

## 4. 当前回归重点建议

优先围绕以下链路执行回归：

1. 登录认证链路
2. 时间段维护链路
3. 预约创建与取消链路
4. 预约完成与咨询记录链路
5. 反馈评价链路
6. 报表展示链路
