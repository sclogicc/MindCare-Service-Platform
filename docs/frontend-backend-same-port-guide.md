# 前后端统一到 8080 的接入说明

## 本次改造目标

将当前项目从“前端 5173 + 后端 8080”的开发模式，整理为适合演示和本地交付的统一访问模式：

- 浏览器最终只访问后端端口：`http://localhost:8080`

## 为什么不能直接保留原来的前端路由

当前项目的前端页面路径和后端接口路径存在重名，例如：

- 前端页面：`/appointments`
- 后端接口：`/appointments`

如果继续使用 `history` 路由并把前端静态资源直接放到 Spring Boot 中，同一个地址会同时承担“页面访问”和“接口访问”两种职责，容易发生冲突。

所以本次采用的方案是：

1. 前端改为 `Hash 路由`
   - 页面地址会变为：
   - `http://localhost:8080/#/dashboard`
   - `http://localhost:8080/#/appointments`

2. 后端接口地址保持不变
   - 例如：
   - `http://localhost:8080/appointments`

这样改动最小，也最适合当前项目结构。

## 已完成改造

1. 前端路由改为 `createWebHashHistory`
2. 前端打包目录改为：
   - `src/main/resources/static`
3. Vite 开发环境增加代理
   - 本地开发时 `5173` 仍可使用
   - 接口自动代理到 `8080`
4. 后端拦截器放行静态资源
   - 未登录时也能正常访问前端页面资源

## 你以后怎么使用

### 开发阶段

前后端分别启动：

1. 启动后端
   - IDEA 启动 `MindCareServicePlatformApplication`
   - 或命令行：
   ```bash
   mvn spring-boot:run
   ```

2. 启动前端
   ```bash
   cd frontend
   npm run dev
   ```

3. 浏览器访问
   - `http://localhost:5173`

说明：
- 此时页面仍由 Vite 提供
- 但接口会自动代理到 `8080`

### 演示 / 单端口访问阶段

1. 进入前端目录
   ```bash
   cd frontend
   ```

2. 执行打包
   ```bash
   npm run build
   ```

3. 启动后端
   - IDEA 启动 `MindCareServicePlatformApplication`
   - 或：
   ```bash
   mvn spring-boot:run
   ```

4. 浏览器访问
   - `http://localhost:8080`

说明：
- 前端静态资源已经被打包到 Spring Boot 的 `static` 目录
- 此时不需要再启动 Vite
- 整个系统只通过 `8080` 访问

## 你最常用的两条命令

### 平时开发
```bash
cd frontend
npm run dev
```

### 演示前更新前端页面
```bash
cd frontend
npm run build
```

然后只启动后端即可。
