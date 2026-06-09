# 心理咨询服务预约平台登录认证模块说明

## 1. 当前已完成内容

本阶段只实现后端登录认证模块，已完成以下内容：

- Spring Boot 基础工程初始化
- 登录接口 `POST /login`
- 当前登录用户信息接口 `GET /login/info`
- `sys_user` 用户查询 Mapper 与 XML
- 统一返回结果 `Result`
- 分页返回结果 `PageResult`
- 自定义业务异常 `BusinessException`
- 全局异常处理器 `GlobalExceptionHandler`
- JWT 工具类 `JwtUtils`
- Token 拦截器 `TokenInterceptor`
- Spring MVC 配置类 `WebConfig`

## 2. 已创建的后端文件

### 2.1 pojo

- `src/main/java/com/mindcare/pojo/Result.java`
- `src/main/java/com/mindcare/pojo/PageResult.java`
- `src/main/java/com/mindcare/pojo/User.java`
- `src/main/java/com/mindcare/pojo/LoginInfo.java`
- `src/main/java/com/mindcare/pojo/LoginUserInfo.java`

### 2.2 controller

- `src/main/java/com/mindcare/controller/LoginController.java`

### 2.3 service

- `src/main/java/com/mindcare/service/LoginService.java`

### 2.4 service.impl

- `src/main/java/com/mindcare/service/impl/LoginServiceImpl.java`

### 2.5 mapper

- `src/main/java/com/mindcare/mapper/UserMapper.java`

### 2.6 mapper.xml

- `src/main/resources/mapper/UserMapper.xml`

### 2.7 utils

- `src/main/java/com/mindcare/utils/JwtUtils.java`

### 2.8 interceptor

- `src/main/java/com/mindcare/interceptor/TokenInterceptor.java`

### 2.9 config

- `src/main/java/com/mindcare/config/WebConfig.java`

### 2.10 exception

- `src/main/java/com/mindcare/exception/BusinessException.java`
- `src/main/java/com/mindcare/exception/GlobalExceptionHandler.java`

## 3. 登录流程

后端登录流程如下：

1. 前端调用 `POST /login` 提交 `username` 和 `password`
2. `LoginController` 接收请求并调用 `LoginService`
3. `LoginServiceImpl` 调用 `UserMapper` 根据用户名查询 `sys_user`
4. 校验用户是否存在、密码是否正确、账号是否启用
5. 校验通过后通过 `JwtUtils` 生成 JWT
6. 返回统一 `Result.success(loginUserInfo)`

## 4. 当前接口说明

### 4.1 登录

- 请求方式：`POST`
- 请求路径：`/login`
- 请求体：

```json
{
  "username": "admin",
  "password": "123456"
}
```

- 成功响应：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "name": "管理员",
    "phone": "13800000000",
    "role": 1,
    "token": "JWT字符串"
  }
}
```

### 4.2 获取当前登录人信息

- 请求方式：`GET`
- 请求路径：`/login/info`
- 请求头：`token`

## 5. 前端登录页设计建议

建议先只做最小可联调版本：

- 页面文件：`src/views/login/LoginView.vue`
- 页面内容：
  - 系统标题
  - 用户名输入框
  - 密码输入框
  - 登录按钮
  - 登录中状态

建议登录页逻辑：

1. 用户输入用户名和密码
2. 点击登录后校验必填项
3. 调用 `api/auth.js` 中的登录方法
4. 成功后把 `token` 和用户信息写入 Pinia
5. 跳转到 `/dashboard`

## 6. 前端请求封装建议

### 6.1 `src/api/auth.js`

建议提供两个方法：

- `login(data)`：调用 `/login`
- `getLoginUserInfo()`：调用 `/login/info`

### 6.2 `src/utils/request.js`

建议封装 Axios 实例：

- 请求拦截器：
  - 自动从 Pinia 或本地存储中读取 `token`
  - 自动放到请求头 `token` 中

- 响应拦截器：
  - 如果后端返回 `code === 0`，统一弹出错误提示
  - 如果 HTTP 状态码是 `401`，清空登录状态并跳转 `/login`

### 6.3 `src/stores/auth.js`

建议保存：

- `token`
- `userInfo`

建议提供的方法：

- `setToken`
- `setUserInfo`
- `clearLoginState`

## 7. 当前验证结果

已执行：

```powershell
mvn -q -DskipTests compile
```

结果：编译通过。
