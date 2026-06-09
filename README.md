# 心理咨询服务预约平台

一个基于 `Spring Boot + MyBatis + MySQL + Vue 3` 的前后端分离项目，面向普通用户、心理咨询师、管理员三类角色，提供登录认证、咨询师管理、可预约时间段管理、咨询预约、咨询记录、反馈评价、文件上传和统计报表等功能。

项目整体风格参考传统教学型后台管理项目，采用典型的 `Controller / Service / Mapper` 三层结构，强调分层清晰、命名规范、业务流程完整，适合作为 Java 后端实习阶段的综合练手项目与面试讲解项目。

## 1. 项目亮点

- 基于 `JWT + 拦截器` 实现登录认证与接口鉴权。
- 预约流程包含固定状态流转：`待确认 -> 已确认 -> 已完成 / 已取消`。
- 新增预约时基于 `schedule_id` 做时间段占用校验，避免同一时间段被重复预约。
- 使用 `PageHelper` 实现分页查询，接口风格保持后台管理系统常见写法。
- 使用 `MyBatis XML` 编写动态 SQL，复杂查询集中放在 Mapper XML 中。
- 提供 `resultMap + collection` 的真实业务场景：查询咨询师详情时，同时查询其可预约时间段列表。
- 提供 `@Transactional` 的联动场景：完成预约时，同步写入咨询记录并更新预约状态。
- 支持 `MultipartFile + 阿里云 OSS` 文件上传，可用于头像或咨询附件场景。
- 提供统计报表接口，支持预约状态分布、咨询师工作量、每月预约数量、反馈评分统计。
- 前端使用 `Vue 3 + Element Plus + ECharts` 实现后台管理风格页面，并已打包集成到 Spring Boot 静态资源目录。

## 2. 技术栈

### 后端

- Java 17
- Spring Boot 3.2.10
- Spring MVC
- MyBatis 3.0.3
- MySQL 8.x
- Maven
- PageHelper 1.4.7
- JWT
- Lombok
- 阿里云 OSS

### 前端

- Vue 3
- Vite 6
- Vue Router 4
- Pinia
- Axios
- Element Plus
- ECharts

## 3. 核心业务模块

- 登录认证
- 咨询师管理
- 可预约时间段管理
- 预约管理
- 咨询记录管理
- 反馈评价管理
- 文件上传
- 统计报表

## 4. 角色说明

- 管理员：管理咨询师、时间段、预约信息，查看统计报表。
- 咨询师：查看与自己相关的预约，补充咨询记录，上传咨询附件。
- 普通用户：发起预约申请，查看自己的预约记录，提交反馈评价。

## 5. 项目结构

```text
MindCare-Service-Platform
├─ src/main/java/com/mindcare
│  ├─ config                Spring MVC 配置
│  ├─ controller            控制层
│  ├─ exception             全局异常与业务异常
│  ├─ interceptor           JWT 拦截器
│  ├─ mapper                Mapper 接口
│  ├─ pojo                  实体类、参数类、返回类
│  ├─ service               业务接口
│  ├─ service/impl          业务实现
│  └─ utils                 JWT、OSS 等工具类
├─ src/main/resources
│  ├─ application.yml       后端配置
│  ├─ mapper                MyBatis XML
│  └─ static                前端打包产物
├─ frontend                 Vue 3 前端工程
├─ sql                      数据脚本
├─ docs                     设计文档与迭代记录
├─ pom.xml                  Maven 配置
└─ README.md
```

## 6. 主要接口风格

项目接口统一返回 `Result`，分页接口统一返回 `PageResult`，典型接口如下：

- `POST /login`：登录并返回 JWT
- `GET /login/info`：获取当前登录用户信息
- `GET /appointments`：分页查询预约列表
- `POST /appointments`：新增预约
- `PUT /appointments/cancel/{id}`：取消预约
- `PUT /appointments/status`：修改预约状态
- `GET /reports/appointmentStatus`：预约状态分布统计
- `GET /reports/counselorWorkload`：咨询师工作量统计
- `GET /reports/monthlyAppointments`：每月预约数量统计
- `GET /reports/feedbackScore`：反馈评分统计
- `POST /upload`：上传文件到阿里云 OSS

更完整的接口设计文档见 [docs/api-design.md](docs/api-design.md)。

## 7. 运行环境

- JDK 17
- Maven 3.9+
- MySQL 8.x
- Node.js 18+
- npm 9+

## 8. 本地启动说明

### 8.1 数据库准备

1. 在 MySQL 中创建数据库：

```sql
CREATE DATABASE mindcare_service_platform DEFAULT CHARACTER SET utf8mb4;
```

2. 按你当前项目使用的表结构先建表。

3. 如果需要导入演示数据，执行：

```sql
SOURCE sql/init_test_data.sql;
```

说明：

- 当前 `sql` 目录下提供的是测试数据脚本 `init_test_data.sql`。
- 该脚本会清空现有业务数据后重新插入演示数据，执行前请确认环境。

### 8.2 后端启动

在项目根目录执行：

```bash
mvn spring-boot:run
```

默认配置位于 [src/main/resources/application.yml](/D:/project/my-project/MindCare-Service-Platform/src/main/resources/application.yml)，核心配置包括：

- 端口：默认 Spring Boot `8080`
- 数据库：`mindcare_service_platform`
- MyBatis 驼峰映射：已开启
- 文件上传大小限制：单文件 `10MB`

### 8.3 前端开发模式启动

进入前端目录：

```bash
cd frontend
npm install
npm run dev
```

启动后访问：

- 前端开发地址：`http://localhost:5173`
- 后端接口地址：`http://localhost:8080`

Vite 已配置代理，开发环境下可直接调用后端接口。

### 8.4 单端口运行方式

项目当前已支持前端打包后由 Spring Boot 统一托管静态资源。

前端打包命令：

```bash
cd frontend
npm run build
```

打包产物会直接输出到：

- `src/main/resources/static`

此时只需要启动后端，即可统一通过 `8080` 访问完整系统：

- 登录页：`http://localhost:8080/#/login`
- 系统首页：`http://localhost:8080/#/dashboard`

## 9. 默认测试账号

如已导入测试数据，可使用以下账号登录：

- 管理员：`admin / 123456`
- 咨询师：`counselor01 / 123456`
- 普通用户：`user01 / 123456`

## 10. OSS 配置说明

项目使用阿里云 OSS 上传文件，配置分两部分：

1. `application.yml` 中维护基础配置：

- `aliyun.oss.endpoint`
- `aliyun.oss.bucketName`
- `aliyun.oss.region`

2. 凭证通过环境变量提供：

- `OSS_ACCESS_KEY_ID`
- `OSS_ACCESS_KEY_SECRET`

如果未配置有效 OSS 凭证，上传接口将无法正常上传到云端。

## 11. 已实现的代表性后端设计

### 11.1 JWT 登录认证

- `LoginController` 负责接收登录请求。
- `LoginServiceImpl` 负责用户查询、密码校验、生成 JWT。
- `TokenInterceptor` 负责拦截请求并校验 token。
- `WebConfig` 统一注册拦截器和跨域配置。

### 11.2 预约冲突校验

- 新增预约时，不直接做复杂时间重叠算法。
- 优先基于 `schedule_id` 判断该时间段是否已被未取消预约占用。
- 这种实现更贴合当前业务模型，也更适合教学型项目讲解。

### 11.3 resultMap + collection

- 查询咨询师详情时，同时查询该咨询师的可预约时间段列表。
- 该场景能直观体现一对多关系映射，适合使用 `resultMap + collection`。

### 11.4 事务控制

- 完成预约时，需要同时：
  - 写入咨询记录
  - 更新预约状态为已完成
- 该过程通过 `@Transactional` 保证原子性，避免部分成功、部分失败造成数据不一致。

## 12. 前端页面说明

当前前端已实现后台管理风格页面，主要包括：

- 登录页
- 首页 / 仪表盘
- 咨询师管理页
- 时间段管理页
- 预约管理页
- 新增预约页
- 咨询记录页
- 反馈评价页
- 统计报表页

页面位于 `frontend/src/views` 目录下，接口封装位于 `frontend/src/api`，请求实例与拦截器位于 `frontend/src/utils/request.js`。

## 13. 文档目录

项目文档已整理为较稳定的一组说明文件，位于 `docs` 目录：

- `README.md`：文档导航
- `api-design.md`：接口设计
- `backend-structure.md`：后端分层与包结构
- `backend-module-notes.md`：后端重点模块说明
- `frontend-integration-plan.md`：前后端联调方案
- `frontend-ui-summary.md`：前端页面与 UI 说明
- `startup-guide.md`：启动与常见问题说明

## 14. 后续可继续完善的方向

- 增加 `@Valid` 参数校验与统一参数异常处理
- 引入 `BCrypt` 存储加密密码
- 增加基于角色的细粒度权限控制
- 引入 Redis 做缓存或防重复提交
- 补充 Dockerfile / docker-compose 部署方案
- 补充数据库建表脚本与完整初始化脚本
- 补充接口测试用例与项目首页截图

## 15. 项目定位

这个项目不是大型企业级系统，也不是微服务项目，而是一个结构清晰、业务完整、可真实运行和演示的 Java 后端练习项目。它的重点在于：

- 体现规范的后台管理系统分层风格
- 体现完整的业务闭环
- 体现常见后端开发能力：认证、分页、动态 SQL、事务、文件上传、统计查询
- 适合用于实习阶段的项目讲解、代码学习和面试准备
