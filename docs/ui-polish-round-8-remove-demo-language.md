# 前端文案清理记录

## 本轮目标

清理页面中带有“演示项目”“简历展示”“本地演示账号”等明显样例化痕迹的文案，让系统在视觉和表达上更接近正常上线项目。

## 已调整页面

1. `frontend/src/views/login/LoginView.vue`
   - 删除“适合 Java 后端实习简历展示”的描述。
   - 删除“演示账号”区域及页面上的账号密码展示。
   - 将登录表单默认账号密码改为空值。
   - 将能力介绍调整为正式业务表达。

2. `frontend/src/views/dashboard/DashboardView.vue`
   - 将“项目演示与联调说明”调整为正常业务总览文案。
   - 将“演示重点”调整为“当前重点”。
   - 将“适合演示和汇报”等描述改为业务分析型表达。

3. `frontend/src/views/appointment/AppointmentCreateView.vue`
   - 删除“非普通用户也允许本地演示提交预约”的提示条。

4. `frontend/src/views/report/ReportView.vue`
   - 将“适合做项目演示和数据说明”改为正式统计说明。

5. `frontend/src/views/counselor/CounselorListView.vue`
   - 将“满足后台演示场景”改为“满足后台日常管理需求”。

6. `frontend/src/views/schedule/ScheduleListView.vue`
   - 清理注释中的“演示时”表述，改为正常权限与误操作说明。

## 结果

- 前端页面不再出现“演示账号”“简历项目”“本地演示”等显式提示。
- 登录页入口表达更贴近正式系统。
- 后台首页与报表页文案更符合真实业务场景。
