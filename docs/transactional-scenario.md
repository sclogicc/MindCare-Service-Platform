# @Transactional 事务场景说明

## 1. 场景说明

本项目选择的事务场景是：

- 完成预约时，同步写入咨询记录，并修改预约状态为已完成

对应表：

- `appointment`
- `consultation_record`
- `upload_file`（可选校验附件存在性）

## 2. 为什么这个场景真实且适合做事务

这个场景非常符合心理咨询服务预约平台的实际业务流程：

1. 用户先发起预约
2. 咨询师或管理员确认预约
3. 咨询结束后，咨询师填写咨询记录
4. 系统把这条预约标记为“已完成”

这不是单表操作，而是两个核心动作联动：

- 新增咨询记录
- 修改预约状态

如果不加事务，会出现下面两类典型问题：

### 2.1 咨询记录写成功，但预约状态没改成功

结果就是：

- 数据库里已经有咨询记录
- 但预约还显示“已确认”

这会导致业务状态不一致。

### 2.2 预约状态改成功，但咨询记录写失败

结果就是：

- 预约已经显示“已完成”
- 但没有对应的咨询记录

这同样会破坏业务闭环。

因此，这个场景必须使用 `@Transactional`，保证：

- 要么咨询记录和预约状态一起成功
- 要么一起失败并回滚

## 3. 当前已创建文件

### 3.1 pojo

- `src/main/java/com/mindcare/pojo/ConsultationRecord.java`
- `src/main/java/com/mindcare/pojo/UploadFile.java`
- `src/main/java/com/mindcare/pojo/CompleteAppointmentParam.java`

### 3.2 service

- `src/main/java/com/mindcare/service/ConsultationRecordService.java`

### 3.3 service.impl

- `src/main/java/com/mindcare/service/impl/ConsultationRecordServiceImpl.java`

### 3.4 mapper

- `src/main/java/com/mindcare/mapper/ConsultationRecordMapper.java`
- `src/main/java/com/mindcare/mapper/UploadFileMapper.java`

### 3.5 mapper.xml

- `src/main/resources/mapper/ConsultationRecordMapper.xml`
- `src/main/resources/mapper/UploadFileMapper.xml`

## 4. 事务入口方法

当前事务入口方法：

```java
void completeAppointment(CompleteAppointmentParam param);
```

位于：

- `ConsultationRecordService`
- `ConsultationRecordServiceImpl`

Service 实现中已经使用：

```java
@Transactional(rollbackFor = Exception.class)
```

## 5. 事务流程

当前方法执行流程如下：

1. 校验参数是否完整
2. 查询预约记录，确认预约存在
3. 校验预约状态必须为“已确认”
4. 校验该预约是否已存在咨询记录，防止重复提交
5. 如果提交了附件 ID，则校验附件是否存在
6. 新增 `consultation_record`
7. 更新 `appointment.status = 已完成`

其中第 6 步和第 7 步必须放在同一个事务中。

## 6. 关键校验点

### 6.1 预约状态校验

当前只允许：

- `已确认 -> 已完成`

如果预约还没确认，或者已经完成，就不允许重复处理。

### 6.2 重复提交校验

通过：

- `ConsultationRecordMapper.countByAppointmentId`

判断该预约是否已经存在咨询记录。

### 6.3 附件存在性校验

如果前端传了 `attachmentFileId`，则通过：

- `UploadFileMapper.countById`

校验附件是否真实存在，避免咨询记录引用一个无效附件主键。

## 7. 当前验证结果

已执行：

```powershell
mvn -q -DskipTests compile
```

结果：编译通过。
