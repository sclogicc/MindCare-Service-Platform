# 心理咨询服务预约平台接口设计文档

## 1. 文档说明

- 项目名称：心理咨询服务预约平台
- 接口风格：REST 风格为主，路径命名简洁，便于映射到 Controller / Service / Mapper 三层结构
- 当前目标：保存第一版后端接口清单，作为后续编码实现基线

## 2. 统一约定

### 2.1 统一返回结果

成功返回：

```json
{
  "code": 1,
  "msg": "success",
  "data": {}
}
```

失败返回：

```json
{
  "code": 0,
  "msg": "错误信息"
}
```

### 2.2 分页返回结构

```json
{
  "total": 100,
  "rows": []
}
```

### 2.3 分页参数约定

- `page`：页码
- `pageSize`：每页条数

### 2.4 角色定义

- `1`：管理员
- `2`：咨询师
- `3`：普通用户

### 2.5 预约状态定义

- `1`：待确认
- `2`：已确认
- `3`：已完成
- `4`：已取消

### 2.6 时间段状态定义

- `1`：可预约
- `0`：停用

### 2.7 时间冲突校验规则

预约时优先按照已存在的可预约时间段进行校验：

- 通过 `schedule_id` 判断目标时间段
- 查询是否已存在 `status in (1,2,3)` 的预约记录
- 若存在，则说明该时间段已被占用，不能重复预约

## 3. 接口模块清单

- 登录认证：`LoginController`
- 用户管理：`UserController`
- 咨询师管理：`CounselorController`
- 可预约时间段管理：`ScheduleController`
- 预约管理：`AppointmentController`
- 咨询记录管理：`ConsultationRecordController`
- 反馈评价管理：`FeedbackController`
- 文件上传：`UploadController`
- 统计报表：`ReportController`

---

## 4. 登录认证模块

### 4.1 用户登录

- 请求方式：`POST`
- 请求路径：`/login`
- 请求参数：

```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

- 返回结果：用户基本信息 + JWT
- 接口用途：校验账号密码并生成 JWT

### 4.2 获取当前登录用户信息

- 请求方式：`GET`
- 请求路径：`/login/info`
- 请求参数：无
- 返回结果：当前登录用户信息
- 接口用途：前端刷新后重新拉取当前登录人信息

---

## 5. 用户管理模块

说明：弱化普通用户后台 CRUD，`sys_user` 主要承担统一登录账号管理职责。

### 5.1 新增用户账号

- 请求方式：`POST`
- 请求路径：`/users`
- 请求参数：`username`、`password`、`name`、`phone`、`gender`、`role`、`status`
- 返回结果：`Result.success()`
- 接口用途：管理员新增普通用户账号

### 5.2 分页查询用户列表

- 请求方式：`GET`
- 请求路径：`/users`
- 请求参数：`page`、`pageSize`、`name`、`phone`、`role`、`status`
- 返回结果：`Result.success(PageResult)`
- 接口用途：管理员分页查看账号列表，支持条件查询

### 5.3 查询用户详情

- 请求方式：`GET`
- 请求路径：`/users/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success(user)`
- 接口用途：查看账号详情、编辑回显

### 5.4 修改用户账号

- 请求方式：`PUT`
- 请求路径：`/users`
- 请求参数：`id`、`name`、`phone`、`gender`、`status`
- 返回结果：`Result.success()`
- 接口用途：修改账号基础信息

### 5.5 修改用户状态

- 请求方式：`PUT`
- 请求路径：`/users/status/{id}/{status}`
- 请求参数：路径参数 `id`、`status`
- 返回结果：`Result.success()`
- 接口用途：启用或禁用账号

### 5.6 删除用户账号

- 请求方式：`DELETE`
- 请求路径：`/users/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success()`
- 接口用途：删除普通用户账号

---

## 6. 咨询师管理模块

### 6.1 新增咨询师

- 请求方式：`POST`
- 请求路径：`/counselors`
- 请求参数：账号信息 + 咨询师业务信息
- 返回结果：`Result.success()`
- 接口用途：一次性创建 `sys_user` 和 `counselor`，后续适合用事务实现

### 6.2 分页查询咨询师列表

- 请求方式：`GET`
- 请求路径：`/counselors`
- 请求参数：`page`、`pageSize`、`name`、`specialty`、`status`
- 返回结果：`Result.success(PageResult)`
- 接口用途：后台分页查看咨询师列表

### 6.3 查询咨询师详情

- 请求方式：`GET`
- 请求路径：`/counselors/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success(counselor)`
- 接口用途：查看咨询师详情、编辑回显

### 6.4 查询咨询师详情并带可预约时间段列表

- 请求方式：`GET`
- 请求路径：`/counselors/detail/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success(counselorDetail)`
- 接口用途：作为 `resultMap + collection` 的典型场景，查询咨询师详情并带出 `scheduleList`

### 6.5 修改咨询师信息

- 请求方式：`PUT`
- 请求路径：`/counselors`
- 请求参数：`id`、`specialty`、`title`、`yearsOfExperience`、`introduction`、`avatarFileId`、`status`
- 返回结果：`Result.success()`
- 接口用途：修改咨询师业务信息

### 6.6 删除咨询师

- 请求方式：`DELETE`
- 请求路径：`/counselors/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success()`
- 接口用途：删除咨询师信息

---

## 7. 可预约时间段管理模块

### 7.1 新增时间段

- 请求方式：`POST`
- 请求路径：`/schedules`
- 请求参数：`counselorId`、`scheduleDate`、`startTime`、`endTime`、`status`、`remark`
- 返回结果：`Result.success()`
- 接口用途：为咨询师新增可预约时间段

### 7.2 分页查询时间段列表

- 请求方式：`GET`
- 请求路径：`/schedules`
- 请求参数：`page`、`pageSize`、`counselorId`、`scheduleDate`、`status`
- 返回结果：`Result.success(PageResult)`
- 接口用途：后台分页查看时间段列表，支持条件查询

### 7.3 查询时间段详情

- 请求方式：`GET`
- 请求路径：`/schedules/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success(schedule)`
- 接口用途：查看时间段详情、编辑回显

### 7.4 修改时间段

- 请求方式：`PUT`
- 请求路径：`/schedules`
- 请求参数：`id`、`scheduleDate`、`startTime`、`endTime`、`status`、`remark`
- 返回结果：`Result.success()`
- 接口用途：修改时间段信息

### 7.5 修改时间段状态

- 请求方式：`PUT`
- 请求路径：`/schedules/status/{id}/{status}`
- 请求参数：路径参数 `id`、`status`
- 返回结果：`Result.success()`
- 接口用途：启用或停用时间段

### 7.6 删除时间段

- 请求方式：`DELETE`
- 请求路径：`/schedules/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success()`
- 接口用途：删除未被预约使用的时间段

### 7.7 查询某咨询师可预约时间段列表

- 请求方式：`GET`
- 请求路径：`/schedules/available`
- 请求参数：`counselorId`、`scheduleDate`
- 返回结果：`Result.success(list)`
- 接口用途：普通用户预约时查询指定咨询师可选时间段

---

## 8. 预约管理模块

### 8.1 新增预约

- 请求方式：`POST`
- 请求路径：`/appointments`
- 请求参数：`userId`、`counselorId`、`scheduleId`、`contactPhone`、`remark`
- 返回结果：`Result.success()`
- 接口用途：普通用户提交预约申请，并进行时间段占用校验

### 8.2 分页查询预约列表

- 请求方式：`GET`
- 请求路径：`/appointments`
- 请求参数：`page`、`pageSize`、`appointmentNo`、`userId`、`counselorId`、`status`、`beginDate`、`endDate`
- 返回结果：`Result.success(PageResult)`
- 接口用途：管理员、咨询师、用户按不同身份查看预约列表

### 8.3 查询预约详情

- 请求方式：`GET`
- 请求路径：`/appointments/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success(appointmentDetail)`
- 接口用途：查看预约详情

### 8.4 取消预约

- 请求方式：`PUT`
- 请求路径：`/appointments/cancel/{id}`
- 请求参数：`cancelReason`
- 返回结果：`Result.success()`
- 接口用途：用户取消预约，状态流转为 `已取消`

### 8.5 确认预约

- 请求方式：`PUT`
- 请求路径：`/appointments/confirm/{id}`
- 请求参数：无
- 返回结果：`Result.success()`
- 接口用途：管理员或咨询师确认预约，状态流转为 `待确认 -> 已确认`

### 8.6 完成预约

- 请求方式：`PUT`
- 请求路径：`/appointments/complete/{id}`
- 请求参数：无
- 返回结果：`Result.success()`
- 接口用途：标记预约完成，状态流转为 `已确认 -> 已完成`

### 8.7 查询我的预约列表

- 请求方式：`GET`
- 请求路径：`/appointments/my`
- 请求参数：`page`、`pageSize`、`status`
- 返回结果：`Result.success(PageResult)`
- 接口用途：当前登录普通用户查看自己的预约列表

### 8.8 查询我的待处理预约列表

- 请求方式：`GET`
- 请求路径：`/appointments/my-counselor`
- 请求参数：`page`、`pageSize`、`status`
- 返回结果：`Result.success(PageResult)`
- 接口用途：当前登录咨询师查看自己的预约安排

---

## 9. 咨询记录管理模块

### 9.1 新增咨询记录

- 请求方式：`POST`
- 请求路径：`/consultationRecords`
- 请求参数：`appointmentId`、`counselorId`、`summary`、`suggestion`、`attachmentFileId`
- 返回结果：`Result.success()`
- 接口用途：咨询师填写咨询记录，后续可与“完成预约”联动事务实现

### 9.2 查询咨询记录详情

- 请求方式：`GET`
- 请求路径：`/consultationRecords/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success(record)`
- 接口用途：查看咨询记录详情

### 9.3 根据预约ID查询咨询记录

- 请求方式：`GET`
- 请求路径：`/consultationRecords/appointment/{appointmentId}`
- 请求参数：路径参数 `appointmentId`
- 返回结果：`Result.success(record)`
- 接口用途：通过预约查询对应咨询记录

### 9.4 修改咨询记录

- 请求方式：`PUT`
- 请求路径：`/consultationRecords`
- 请求参数：`id`、`summary`、`suggestion`、`attachmentFileId`
- 返回结果：`Result.success()`
- 接口用途：补充或修改咨询记录

### 9.5 删除咨询记录

- 请求方式：`DELETE`
- 请求路径：`/consultationRecords/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success()`
- 接口用途：删除咨询记录

---

## 10. 反馈评价管理模块

### 10.1 新增反馈评价

- 请求方式：`POST`
- 请求路径：`/feedbacks`
- 请求参数：`appointmentId`、`userId`、`counselorId`、`score`、`content`、`isAnonymous`
- 返回结果：`Result.success()`
- 接口用途：用户在预约完成后提交评价

### 10.2 分页查询反馈列表

- 请求方式：`GET`
- 请求路径：`/feedbacks`
- 请求参数：`page`、`pageSize`、`counselorId`、`score`、`isAnonymous`
- 返回结果：`Result.success(PageResult)`
- 接口用途：管理员或咨询师分页查看评价列表

### 10.3 查询反馈详情

- 请求方式：`GET`
- 请求路径：`/feedbacks/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success(feedback)`
- 接口用途：查看评价详情

### 10.4 根据预约ID查询反馈

- 请求方式：`GET`
- 请求路径：`/feedbacks/appointment/{appointmentId}`
- 请求参数：路径参数 `appointmentId`
- 返回结果：`Result.success(feedback)`
- 接口用途：判断某次预约是否已评价

### 10.5 删除反馈

- 请求方式：`DELETE`
- 请求路径：`/feedbacks/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success()`
- 接口用途：管理员删除不合规评价

---

## 11. 文件上传模块

统一采用 `UploadController` 风格，保持接口简单。

### 11.1 上传文件

- 请求方式：`POST`
- 请求路径：`/upload`
- 请求参数：`file`、`businessType`
- 返回结果：附件ID、原始文件名、访问地址
- 接口用途：上传头像、咨询附件等文件

### 11.2 查询附件详情

- 请求方式：`GET`
- 请求路径：`/upload/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success(fileInfo)`
- 接口用途：查询附件信息

### 11.3 删除附件

- 请求方式：`DELETE`
- 请求路径：`/upload/{id}`
- 请求参数：路径参数 `id`
- 返回结果：`Result.success()`
- 接口用途：删除无效附件，后续可联动 OSS 删除

---

## 12. 统计报表模块

统一采用 `ReportController` 风格，不走普通 CRUD。

### 12.1 预约总量统计

- 请求方式：`GET`
- 请求路径：`/reports/appointmentCount`
- 请求参数：`beginDate`、`endDate`
- 返回结果：预约总量数字
- 接口用途：统计指定时间范围内预约总量

### 12.2 预约状态分布统计

- 请求方式：`GET`
- 请求路径：`/reports/appointmentStatus`
- 请求参数：`beginDate`、`endDate`
- 返回结果：状态分布列表
- 接口用途：前端饼图展示预约状态分布

### 12.3 咨询师工作量统计

- 请求方式：`GET`
- 请求路径：`/reports/counselorWorkload`
- 请求参数：`beginDate`、`endDate`
- 返回结果：咨询师工作量列表
- 接口用途：柱状图展示各咨询师预约量或完成量

### 12.4 月度预约趋势统计

- 请求方式：`GET`
- 请求路径：`/reports/monthlyAppointments`
- 请求参数：`year`
- 返回结果：12 个月趋势数据
- 接口用途：折线图展示月度预约变化趋势

### 12.5 反馈评分分布统计

- 请求方式：`GET`
- 请求路径：`/reports/feedbackScore`
- 请求参数：`beginDate`、`endDate`
- 返回结果：评分分布列表
- 接口用途：展示用户反馈评分分布情况

---

## 13. 实现建议补充

### 13.1 动态 SQL 主要落点

建议主要放在以下分页或条件查询接口的 MyBatis XML 中：

- `/users`
- `/counselors`
- `/schedules`
- `/appointments`
- `/feedbacks`

### 13.2 事务场景建议

优先实现两个事务场景：

- 新增咨询师：同时写入 `sys_user` 和 `counselor`
- 新增咨询记录并完成预约：新增 `consultation_record`，同时更新 `appointment.status`

### 13.3 当前登录人处理建议

接口设计文档中保留了 `userId`、`counselorId` 等字段说明业务含义，但实际编码时应优先通过 JWT 解析当前登录人，而不是完全依赖前端传参。

### 13.4 resultMap + collection 落点

典型场景固定为：

- 查询咨询师详情
- 同时带出其可预约时间段列表 `scheduleList`

后续可以在 `CounselorMapper.xml` 中通过 `resultMap + collection` 实现。
