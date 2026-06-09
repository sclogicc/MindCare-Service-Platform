# 本地启动说明

## 1. 后端启动前准备

### 1.1 数据库

- 数据库名：`mindcare_service_platform`
- 用户名：`root`
- 密码：`123456`

### 1.2 初始化测试数据

项目已提供初始化脚本：

- [init_test_data.sql](</D:\project\my-project\MindCare-Service-Platform\sql\init_test_data.sql>)

执行方式：

```powershell
mysql -uroot -p123456 -D mindcare_service_platform < sql\init_test_data.sql
```

初始化后可直接使用以下测试账号登录：

- 管理员：`admin / 123456`
- 咨询师：`counselor01 / 123456`
- 普通用户：`user01 / 123456`

### 1.3 OSS 环境变量

如果你要测试上传接口，需要先配置：

- `OSS_ACCESS_KEY_ID`
- `OSS_ACCESS_KEY_SECRET`

同时把 `src/main/resources/application.yml` 里的：

- `aliyun.oss.bucketName`

改成你自己真实可用的 Bucket。

如果当前不测试上传接口，后端仍然可以正常启动。

## 2. 后端启动

在项目根目录执行：

```powershell
mvn spring-boot:run
```

默认端口：

- `8080`

## 3. 登录验证

### 3.1 登录接口

- `POST /login`

请求体：

```json
{
  "username": "admin",
  "password": "123456"
}
```

### 3.2 获取当前登录人

- `GET /login/info`
- 请求头：`token`

## 4. 前端启动规划

后续前端建议放在项目根目录下的 `frontend` 子目录，单独使用 Vite 启动：

```powershell
cd frontend
npm install
npm run dev
```

开发时建议把前端请求统一指向：

- `http://localhost:8080`
