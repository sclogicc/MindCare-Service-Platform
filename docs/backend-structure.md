# 心理咨询服务预约平台后端项目结构设计

## 1. 设计目标

- 项目风格尽量参考 `tlias-web-management`
- 使用典型的 `Spring Boot + MyBatis + MySQL` 三层架构
- 包结构清晰，类职责单一，命名规范，便于实习项目讲解
- 保留以下核心风格：`Result`、`PageResult`、`GlobalExceptionHandler`、`JwtUtils`、`LoginController`、`UploadController`、`ReportController`

建议基础包名：`com.mindcare`

```text
com.mindcare
├─ MindCareServicePlatformApplication
├─ controller
├─ service
├─ service.impl
├─ mapper
├─ pojo
├─ exception
├─ utils
├─ config
└─ interceptor
```

---

## 2. controller 包

### `LoginController`
- 负责登录、获取当前登录用户信息
- 保留传统后台项目登录入口风格

### `UserController`
- 负责轻量账号管理
- 对 `sys_user` 提供基础增删改查、分页、状态启停

### `CounselorController`
- 负责咨询师管理
- 提供分页查询、详情、修改、删除、查询咨询师详情及时间段列表

### `ScheduleController`
- 负责可预约时间段管理
- 提供新增、分页、详情、修改、状态修改、删除、可预约时间段查询

### `AppointmentController`
- 负责预约管理
- 提供新增预约、分页查询、详情、取消、确认、完成、我的预约查询

### `ConsultationRecordController`
- 负责咨询记录管理
- 提供新增、详情、按预约查询、修改、删除

### `FeedbackController`
- 负责反馈评价管理
- 提供新增评价、分页、详情、按预约查询、删除

### `UploadController`
- 负责文件上传
- 处理头像和咨询附件上传、查询、删除

### `ReportController`
- 负责统计报表
- 统一提供预约总量、状态分布、咨询师工作量、月度趋势、评分分布等接口

---

## 3. service 包

### `LoginService`
- 登录校验
- JWT 生成
- 当前登录人信息封装

### `UserService`
- 用户账号基础管理

### `CounselorService`
- 咨询师资料管理
- 查询咨询师详情及可预约时间段列表

### `ScheduleService`
- 时间段管理
- 校验时间段状态，提供可预约列表

### `AppointmentService`
- 预约核心业务
- 负责时间段占用校验、状态流转、我的预约查询

### `ConsultationRecordService`
- 咨询记录新增与维护
- 后续适合加事务，联动预约完成

### `FeedbackService`
- 评价新增、查询、删除

### `UploadService`
- 文件上传与附件记录保存
- 对接 OSS 工具类

### `ReportService`
- 聚合统计业务
- 对外提供报表数据结构

---

## 4. service.impl 包

- `LoginServiceImpl`
- `UserServiceImpl`
- `CounselorServiceImpl`
- `ScheduleServiceImpl`
- `AppointmentServiceImpl`
- `ConsultationRecordServiceImpl`
- `FeedbackServiceImpl`
- `UploadServiceImpl`
- `ReportServiceImpl`

说明：
- 每个接口对应一个实现类
- 保持传统后台项目“一接口一实现类”的结构，便于理解和维护

---

## 5. mapper 包

### `UserMapper`
- 对应 `sys_user`
- 登录查询、分页查询、基础增删改查

### `CounselorMapper`
- 对应 `counselor`
- 咨询师分页、详情、详情带时间段列表
- `resultMap + collection` 主要放这里

### `ScheduleMapper`
- 对应 `counselor_schedule`
- 时间段分页、条件查询、可预约列表查询

### `AppointmentMapper`
- 对应 `appointment`
- 预约分页、详情、状态修改、时间段占用校验

### `ConsultationRecordMapper`
- 对应 `consultation_record`
- 记录增删改查、按预约查询

### `FeedbackMapper`
- 对应 `feedback`
- 评价分页、详情、按预约查询

### `UploadFileMapper`
- 对应 `upload_file`
- 附件记录保存、查询、删除

### `ReportMapper`
- 负责统计 SQL
- 保持 `ReportController -> ReportService -> ReportMapper` 的传统结构

对应 XML 建议：

- `UserMapper.xml`
- `CounselorMapper.xml`
- `ScheduleMapper.xml`
- `AppointmentMapper.xml`
- `ConsultationRecordMapper.xml`
- `FeedbackMapper.xml`
- `UploadFileMapper.xml`
- `ReportMapper.xml`

---

## 6. pojo 包

建议把实体类、查询参数类、返回对象类统一放在 `pojo` 下，尽量贴近 `tlias-web-management` 的风格。

### 通用类

#### `Result`
- 统一返回结果类

#### `PageResult<T>`
- 统一分页返回类

### 实体类

- `User`
- `Counselor`
- `Schedule`
- `Appointment`
- `ConsultationRecord`
- `Feedback`
- `UploadFile`

### 登录相关

#### `LoginInfo`
- 接收登录请求参数

#### `LoginUserInfo`
- 返回前端的当前登录用户信息和角色信息

### 查询参数类

- `UserQueryParam`
- `CounselorQueryParam`
- `ScheduleQueryParam`
- `AppointmentQueryParam`
- `FeedbackQueryParam`

### 详情/展示类

#### `CounselorDetail`
- 包含咨询师基本信息和 `scheduleList`
- 对应 `resultMap + collection` 典型场景

#### `AppointmentDetail`
- 包含预约、用户、咨询师、时间段等展示信息

### 报表类

#### `ReportOption`
- 通用名称和值结构，适合饼图、柱状图

#### `MonthlyAppointmentOption`
- 月度预约趋势数据

---

## 7. exception 包

### `GlobalExceptionHandler`
- 全局异常处理器
- 统一捕获业务异常和系统异常，返回 `Result.error(...)`

### `BusinessException`
- 业务异常
- 用于时间段已占用、状态流转非法、数据不存在等业务场景

---

## 8. utils 包

### `JwtUtils`
- JWT 生成、解析
- 保留与 `tlias-web-management` 接近的工具类风格

### `AliyunOSSOperator`
- 封装 OSS 上传逻辑

### `CurrentHolder`
- 保存当前登录用户信息
- 供拦截器解析 JWT 后放入上下文，业务层读取当前用户使用

### `AppointmentNoUtils`
- 生成预约单号
- 避免把单号生成逻辑写进 `ServiceImpl`

---

## 9. config 包

### `WebConfig`
- 注册 `TokenInterceptor`
- 配置放行路径，例如 `/login`

### `AliyunOSSProperties`
- 绑定 OSS 配置
- 与 `AliyunOSSOperator` 配合使用

### `JacksonConfig`
- 统一 `LocalDate`、`LocalTime`、`LocalDateTime` 的序列化格式
- 项目中有日期和时间段字段，提前统一格式更稳

---

## 10. interceptor 包

### `TokenInterceptor`
- 拦截请求，校验 JWT
- 解析当前登录用户
- token 非法或未登录时直接拦截请求

---

## 11. 推荐实现顺序

1. 先搭基础骨架：`Result`、`PageResult`、`GlobalExceptionHandler`、`JwtUtils`、`TokenInterceptor`、`WebConfig`
2. 再做登录模块：`LoginController + LoginService + UserMapper`
3. 再做咨询师和时间段模块：为后续预约提供基础数据
4. 再做预约模块：包含动态 SQL、状态流转、时间段占用校验
5. 再做咨询记录和反馈模块：补齐完整业务闭环
6. 最后做上传和统计模块：作为项目亮点补齐

---

## 12. 结构设计原则

- 命名清晰，职责单一
- `Controller / Service / Mapper` 分层明确
- 复杂查询和动态 SQL 主要放到 MyBatis XML 中
- 不过度拆分 `dto / vo / converter` 等额外层次，适合实习项目讲解
- 保持与 `tlias-web-management` 接近的代码组织方式，便于后续统一风格实现
