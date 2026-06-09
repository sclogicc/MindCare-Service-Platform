# MyBatis resultMap + collection 场景说明

## 1. 场景选择

本项目选择的典型场景是：

- 查询咨询师详情，同时查询其可预约时间段列表

对应关系：

- 主对象：`CounselorDetail`
- 子集合：`scheduleList`
- 一对多关系：`counselor -> counselor_schedule`

## 2. 为什么这里适合使用 resultMap + collection

这个场景非常适合使用 MyBatis 的 `resultMap + collection`，原因如下：

### 2.1 天然是一对多关系

一个咨询师会对应多个可预约时间段：

- 咨询师 A
  - 2026-05-10 09:00-10:00
  - 2026-05-10 14:00-15:00
  - 2026-05-11 10:00-11:00

这就是非常典型的“一条主记录对应多条子记录”的结构。

### 2.2 前端详情页通常需要一次拿全

在咨询师详情页中，前端一般希望一次请求就拿到：

- 咨询师基本信息
- 可预约时间段列表

如果不用 `resultMap + collection`，通常会变成：

1. 先查咨询师详情
2. 再查该咨询师的时间段列表

而使用 `resultMap + collection` 后，可以一次联表查询完成映射，结构更直观。

### 2.3 非常适合教学型项目讲解

这个场景比“复杂嵌套对象”更容易讲清楚：

- 主表是咨询师
- 子表是时间段
- 一个咨询师拥有多个可预约时间段

面试或项目讲解时，别人一听就能明白为什么要这么做。

## 3. 当前已创建文件

### 3.1 pojo

- `src/main/java/com/mindcare/pojo/CounselorDetail.java`
- `src/main/java/com/mindcare/pojo/Schedule.java`

### 3.2 mapper

- `src/main/java/com/mindcare/mapper/CounselorMapper.java`

### 3.3 mapper.xml

- `src/main/resources/mapper/CounselorMapper.xml`

## 4. 关键映射说明

### 4.1 主对象

`CounselorDetail`

用于承载：

- 咨询师主信息
- 账号姓名与手机号
- 时间段集合 `scheduleList`

### 4.2 子对象

`Schedule`

用于承载一条可预约时间段数据。

### 4.3 collection 映射

当前核心写法：

```xml
<collection property="scheduleList" resultMap="scheduleResultMap"/>
```

含义：

- 把查询结果中属于同一个咨询师的多条时间段记录
- 自动组装到 `CounselorDetail.scheduleList` 中

### 4.4 关键查询方法

Mapper 接口：

```java
CounselorDetail selectDetailWithSchedulesById(Long id);
```

这个方法返回的不是单表对象，而是一个“主对象 + 子集合”的完整详情结果。

## 5. 查询设计说明

当前 SQL 使用：

- `counselor` 作为主表
- `sys_user` 补充咨询师姓名和手机号
- `counselor_schedule` 作为子表
- `left join` 保证即使没有时间段，也能查出咨询师主信息

这样设计更符合真实后台详情页场景：

- 主信息必须可见
- 子列表允许为空

## 6. 当前验证结果

已执行：

```powershell
mvn -q -DskipTests compile
```

结果：编译通过。
