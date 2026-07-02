# 本地启动说明

本文档用于说明项目在本地的启动方式、访问方式和常见问题排查。

## 1. 运行环境

- JDK 17
- Maven 3.9+
- MySQL 8.x
- Node.js 18+
- npm 9+

## 2. 数据库准备

### 2.1 创建数据库

```sql
CREATE DATABASE mindcare_service_platform DEFAULT CHARACTER SET utf8mb4;
```

### 2.2 建表

先执行项目提供的建表脚本：

```powershell
mysql -uroot -p123456 < sql\schema.sql
```

说明：`schema.sql` 会创建 `mindcare_service_platform` 数据库并初始化项目所需表结构。

### 2.3 导入测试数据

项目已提供测试数据脚本：

- [init_test_data.sql](</D:/project/my-project/MindCare-Service-Platform/sql/init_test_data.sql>)

导入方式示例：

```powershell
mysql -uroot -p123456 -D mindcare_service_platform < sql\init_test_data.sql
```

## 3. 默认测试账号

- 管理员：`admin / 123456`
- 咨询师：`counselor01 / 123456`
- 普通用户：`user01 / 123456`

## 4. 后端启动

在项目根目录执行：

```powershell
mvn spring-boot:run
```

后端默认访问地址：

- `http://localhost:8080`

登录页地址：

- `http://localhost:8080/#/login`

## 5. 前端开发模式启动

进入前端目录：

```powershell
cd frontend
npm install
npm run dev
```

开发模式访问地址：

- 前端页面：`http://localhost:5173`
- 后端接口：`http://localhost:8080`

说明：

- `5173` 是 Vite 开发服务器端口。
- `8080` 是 Spring Boot 后端端口。
- 开发环境通过 Vite 代理转发接口请求。

## 6. 单端口访问方式

项目支持将前端打包后交给 Spring Boot 统一托管。

前端打包命令：

```powershell
cd frontend
npm run build
```

打包产物会输出到：

- `src/main/resources/static`

此时只启动后端即可，通过同一个端口访问整套系统：

- 登录页：`http://localhost:8080/#/login`
- 首页：`http://localhost:8080/#/dashboard`

## 7. OSS 配置

`application.yml` 中维护基础配置：

- `aliyun.oss.endpoint`
- `aliyun.oss.bucketName`
- `aliyun.oss.region`

AccessKey 建议通过环境变量提供：

- `OSS_ACCESS_KEY_ID`
- `OSS_ACCESS_KEY_SECRET`

如果未配置有效凭证，上传接口无法正常上传文件。

## 8. 常见问题

### 8.1 `No plugin found for prefix 'spring-boot'`

原因通常是：

- 你没有在项目根目录执行 Maven 命令
- 当前目录下没有 `pom.xml`

正确做法：

```powershell
cd D:\project\my-project\MindCare-Service-Platform
mvn spring-boot:run
```

### 8.2 `Goal requires a project to execute but there is no POM in this directory`

原因：

- 当前终端目录不在项目根目录

处理：

- 切换到 `MindCare-Service-Platform` 根目录后再执行 Maven 命令

### 8.3 `Port 8080 was already in use`

原因：

- 本机已有其他程序占用了 `8080`

处理思路：

- 关闭已占用 `8080` 的进程
- 或修改 Spring Boot 端口

### 8.4 `java.lang.ExceptionInInitializerError` / `com.sun.tools.javac.code.TypeTag :: UNKNOWN`

这类问题通常不是项目代码错误，而是 IDE 的 JDK / Language Level / Maven Importer JDK 配置不一致造成的。

建议检查：

- 项目 SDK 是否为 `JDK 17`
- Language level 是否为 `17`
- Maven 导入使用的 JDK 是否为 `17`

### 8.5 浏览器打开 `5173` 和 `8080` 为什么不同

这是前后端分离开发的正常现象：

- `5173` 提供前端页面
- `8080` 提供后端接口

如果你希望只通过一个端口访问，执行前端打包后，改为直接访问 `8080`。
