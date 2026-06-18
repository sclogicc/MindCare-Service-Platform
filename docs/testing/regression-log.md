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
