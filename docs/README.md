# 文档导航

当前 `docs` 目录只保留长期有维护价值的项目文档，便于查阅和交付。

## 保留文档

### 1. [api-design.md](./api-design.md)

后端接口清单，包含：

- 接口分组
- 请求方式
- 请求路径
- 参数说明
- 返回结果

### 2. [backend-structure.md](./backend-structure.md)

后端项目结构说明，包含：

- 包结构设计
- 核心类职责
- 分层说明

### 3. [backend-module-notes.md](./backend-module-notes.md)

后端重点模块说明，包含：

- 登录认证
- 预约管理
- 咨询记录
- 反馈评价
- 文件上传
- 统计报表
- `resultMap + collection`
- `@Transactional`

### 4. [frontend-integration-plan.md](./frontend-integration-plan.md)

前端联调方案，包含：

- Axios 封装
- 请求与响应拦截
- Token 持久化
- 路由守卫
- API 模块划分

### 5. [frontend-ui-summary.md](./frontend-ui-summary.md)

前端页面与 UI 打磨说明，包含：

- 后台布局结构
- 主要页面职责
- 导航与品牌统一
- 表格、背景、交互优化

### 6. [startup-guide.md](./startup-guide.md)

本地启动与常见问题说明，包含：

- 数据库准备
- 前后端启动方式
- 单端口访问方式
- 测试账号
- 常见启动问题排查

### 7. [project-scope.md](./project-scope.md)

实训项目范围说明，包含：

- 项目定位与目标
- 角色说明
- 核心业务闭环
- 本次实训纳入范围
- 企业级方向增强目标
- 已实现内容与后续边界

### 8. [testing/test-cases.md](./testing/test-cases.md)

测试用例文档，包含：

- 登录认证测试
- 权限与角色测试
- 时间段管理测试
- 预约管理测试
- 咨询记录测试
- 反馈评价测试
- 报表与上传测试

### 9. [testing/bug-log.md](./testing/bug-log.md)

缺陷记录文档，包含：

- 问题描述
- 复现步骤
- 修复提交
- 修复说明
- 回归结果

### 10. [testing/regression-log.md](./testing/regression-log.md)

回归测试记录文档，包含：

- 回归批次
- 对应缺陷
- 对应提交
- 回归范围
- 回归结果

## 清理说明

此前目录中存在较多按“某一轮修改”拆分的过程文档，例如：

- `ui-polish-round-*`
- `*-module.md` 的阶段性实现记录
- 启动问题、端口说明、数据刷新等零散记录

这些内容已经合并进上面的长期文档中，因此不再单独保留。
