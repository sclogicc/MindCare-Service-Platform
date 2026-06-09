# 心理咨询服务预约平台预约管理模块说明

## 1. 当前已完成内容

本阶段已完成预约管理模块后端实现，包含：

- 新增预约
- 分页查询预约列表
- 条件查询
- 查询预约详情
- 取消预约
- 修改预约状态
- 新增预约时按 `schedule_id` 做时间段冲突校验
- 使用 `PageHelper` 分页
- 动态 SQL 编写在 MyBatis XML 中
- 统一使用 `Result / PageResult` 返回结果

同时已在 `pom.xml` 中新增：

- `pagehelper-spring-boot-starter:1.4.7`

## 2. 已创建文件

### 2.1 pojo

- `src/main/java/com/mindcare/pojo/Appointment.java`
- `src/main/java/com/mindcare/pojo/AppointmentDetail.java`
- `src/main/java/com/mindcare/pojo/AppointmentQueryParam.java`
- `src/main/java/com/mindcare/pojo/AppointmentStatusUpdateParam.java`

### 2.2 controller

- `src/main/java/com/mindcare/controller/AppointmentController.java`

### 2.3 service

- `src/main/java/com/mindcare/service/AppointmentService.java`

### 2.4 service.impl

- `src/main/java/com/mindcare/service/impl/AppointmentServiceImpl.java`

### 2.5 mapper

- `src/main/java/com/mindcare/mapper/AppointmentMapper.java`

### 2.6 mapper.xml

- `src/main/resources/mapper/AppointmentMapper.xml`

## 3. 后端接口清单

### 3.1 新增预约

- 请求方式：`POST`
- 请求路径：`/appointments`

请求体示例：

```json
{
  "userId": 3,
  "counselorId": 1,
  "scheduleId": 11,
  "contactPhone": "13800000011",
  "remark": "最近工作压力比较大"
}
```

说明：

- 后端会自动生成 `appointmentNo`
- 默认状态写入 `1（待确认）`
- 新增前会校验 `schedule_id` 是否已被未取消预约占用

### 3.2 分页查询预约列表

- 请求方式：`GET`
- 请求路径：`/appointments`

支持参数：

- `page`
- `pageSize`
- `appointmentNo`
- `userId`
- `counselorId`
- `status`
- `beginDate`
- `endDate`

示例：

```text
/appointments?page=1&pageSize=10&status=1&counselorId=1
```

### 3.3 查询预约详情

- 请求方式：`GET`
- 请求路径：`/appointments/{id}`

### 3.4 取消预约

- 请求方式：`PUT`
- 请求路径：`/appointments/cancel/{id}`

请求体示例：

```json
{
  "cancelReason": "临时有事，取消预约"
}
```

说明：

- 该接口会把状态修改为 `4（已取消）`
- 会同步写入 `cancelReason`

### 3.5 修改预约状态

- 请求方式：`PUT`
- 请求路径：`/appointments/status`

请求体示例：

```json
{
  "id": 1001,
  "status": 2
}
```

说明：

- 当前支持的流转：
  - `1（待确认） -> 2（已确认）`
  - `2（已确认） -> 3（已完成）`
- 不允许通过该接口直接改成 `4（已取消）`
- 取消预约必须走专用取消接口

## 4. 时间段冲突校验说明

新增预约时，后端使用以下规则判断冲突：

1. 根据 `schedule_id` 查询预约表
2. 统计同一 `schedule_id` 下 `status != 4` 的记录数量
3. 如果数量大于 0，则说明当前时间段已被占用
4. 返回业务异常：`该时间段已被预约，请重新选择`

这样做的好处是：

- 不需要先做复杂的时间重叠算法
- 与当前数据库设计保持一致
- 便于讲解和联调

## 5. 动态 SQL 说明

动态 SQL 主要写在：

- `AppointmentMapper.xml -> selectPageList`

当前支持动态条件：

- 预约单号模糊查询
- 用户 ID 精确查询
- 咨询师 ID 精确查询
- 预约状态精确查询
- 预约日期区间查询（按 `counselor_schedule.schedule_date`）

## 6. 前端预约列表页接口对接说明

建议页面文件：

- `src/views/appointment/AppointmentListView.vue`

### 6.1 页面用途

用于后台管理系统中的预约列表展示，支持：

- 分页
- 条件筛选
- 查看详情
- 取消预约
- 修改状态

### 6.2 建议调用接口

#### 查询列表

- `GET /appointments`

前端建议查询参数：

```js
{
  page: 1,
  pageSize: 10,
  appointmentNo: '',
  counselorId: '',
  status: '',
  beginDate: '',
  endDate: ''
}
```

#### 查看详情

- `GET /appointments/{id}`

#### 取消预约

- `PUT /appointments/cancel/{id}`

请求体：

```js
{
  cancelReason: '用户主动取消'
}
```

#### 修改状态

- `PUT /appointments/status`

请求体：

```js
{
  id: 1001,
  status: 2
}
```

### 6.3 列表页表格字段建议

- `appointmentNo`
- `userName`
- `counselorName`
- `scheduleDate`
- `startTime`
- `endTime`
- `status`
- `contactPhone`
- `createTime`

### 6.4 列表页交互建议

- 顶部查询区：
  - 预约单号
  - 咨询师
  - 状态
  - 日期范围

- 表格操作列：
  - 详情
  - 确认
  - 完成
  - 取消

## 7. 前端预约表单页接口对接说明

建议页面文件：

- `src/views/appointment/AppointmentFormView.vue`

### 7.1 页面用途

用于普通用户新增预约。

### 7.2 建议调用接口

#### 提交预约

- `POST /appointments`

请求体：

```js
{
  userId: 3,
  counselorId: 1,
  scheduleId: 11,
  contactPhone: '13800000011',
  remark: '最近情绪波动比较大'
}
```

### 7.3 表单字段建议

- 咨询师选择
- 可预约时间段选择
- 联系电话
- 预约备注

### 7.4 表单页联调建议

前端新增预约表单通常还需要两个辅助接口配合：

- 咨询师下拉列表接口
- 某咨询师可预约时间段列表接口

当前预约模块本身不负责这两个接口，它们应分别由：

- 咨询师模块
- 时间段模块

提供支持。

## 8. 当前验证结果

已执行：

```powershell
mvn -q -DskipTests compile
```

结果：编译通过。
