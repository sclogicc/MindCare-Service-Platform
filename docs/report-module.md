# 统计报表模块说明

## 1. 模块目标

当前统计报表模块围绕后台首页和统计页的核心可视化需求设计，已实现以下接口：

- 预约状态分布统计
- 咨询师工作量统计
- 每月预约数量统计
- 反馈评分统计

这些统计统一由：

- `ReportController`
- `ReportService`
- `ReportMapper`

负责，风格保持和传统 Spring Boot + MyBatis 后台项目一致。

## 2. 已创建文件

### 2.1 controller

- `src/main/java/com/mindcare/controller/ReportController.java`

### 2.2 service

- `src/main/java/com/mindcare/service/ReportService.java`
- `src/main/java/com/mindcare/service/impl/ReportServiceImpl.java`

### 2.3 mapper

- `src/main/java/com/mindcare/mapper/ReportMapper.java`
- `src/main/resources/mapper/ReportMapper.xml`

### 2.4 pojo

- `src/main/java/com/mindcare/pojo/ReportQueryParam.java`
- `src/main/java/com/mindcare/pojo/ReportOption.java`
- `src/main/java/com/mindcare/pojo/MonthlyAppointmentOption.java`
- `src/main/java/com/mindcare/pojo/AppointmentStatusCount.java`

## 3. 接口清单

### 3.1 预约状态分布统计

- 请求方式：`GET`
- 请求路径：`/reports/appointmentStatus`
- 支持参数：
  - `beginDate`
  - `endDate`

返回示例：

```json
[
  { "name": "待确认", "value": 12 },
  { "name": "已确认", "value": 20 },
  { "name": "已完成", "value": 35 },
  { "name": "已取消", "value": 6 }
]
```

### 3.2 咨询师工作量统计

- 请求方式：`GET`
- 请求路径：`/reports/counselorWorkload`
- 支持参数：
  - `beginDate`
  - `endDate`

返回示例：

```json
[
  { "name": "李医生", "value": 18 },
  { "name": "王医生", "value": 14 }
]
```

### 3.3 每月预约数量统计

- 请求方式：`GET`
- 请求路径：`/reports/monthlyAppointments`
- 支持参数：
  - `year`

返回示例：

```json
[
  { "month": 1, "monthLabel": "1月", "value": 8 },
  { "month": 2, "monthLabel": "2月", "value": 11 }
]
```

### 3.4 反馈评分统计

- 请求方式：`GET`
- 请求路径：`/reports/feedbackScore`
- 支持参数：
  - `beginDate`
  - `endDate`

返回示例：

```json
[
  { "name": "1分", "value": 0 },
  { "name": "2分", "value": 1 },
  { "name": "3分", "value": 5 },
  { "name": "4分", "value": 12 },
  { "name": "5分", "value": 20 }
]
```

## 4. SQL 设计说明

### 4.1 预约状态分布统计

使用：

- `case when`
- `sum`

思路：

- 一条 SQL 同时统计四种状态数量
- 避免为每种状态单独写一条查询

### 4.2 咨询师工作量统计

使用：

- `group by`
- `count`

思路：

- 按咨询师分组
- 统计每位咨询师的有效预约数量
- 已取消预约不计入工作量

### 4.3 每月预约数量统计

使用：

- `group by month(...)`
- `count`

思路：

- 按预约日期所在月份统计
- Mapper 只返回数据库中实际存在数据的月份
- Service 再补齐 1~12 月，便于前端图表完整展示

### 4.4 反馈评分统计

使用：

- `group by score`
- `count`

思路：

- 按评分分组
- Service 补齐 1~5 分，避免某个分值没有数据时图表缺失维度

## 5. 前端图表对接建议

建议页面：

- `src/views/report/ReportView.vue`

建议拆成 4 个图表区域：

### 5.1 预约状态分布

适合图表：

- 饼图

建议前端处理：

- 直接把接口返回的 `name/value` 数组作为 ECharts `series.data`

### 5.2 咨询师工作量统计

适合图表：

- 柱状图

建议前端处理：

- `xAxis` 使用 `name`
- `series.data` 使用 `value`

### 5.3 每月预约数量统计

适合图表：

- 折线图
- 柱状图

建议前端处理：

- `xAxis` 使用 `monthLabel`
- `series.data` 使用 `value`

### 5.4 反馈评分统计

适合图表：

- 柱状图
- 饼图

建议前端处理：

- 直接使用 `name/value` 结构

## 6. 前端 API 封装建议

建议文件：

- `src/api/report.js`

建议方法：

- `getAppointmentStatusReport(params)`
- `getCounselorWorkloadReport(params)`
- `getMonthlyAppointmentReport(params)`
- `getFeedbackScoreReport(params)`

示例：

```js
export function getAppointmentStatusReport(params) {
  return request({
    url: '/reports/appointmentStatus',
    method: 'get',
    params
  })
}
```

## 7. 当前验证结果

已执行：

```powershell
mvn -q -DskipTests compile
```

结果：编译通过。
