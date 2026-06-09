# 心理咨询服务预约平台参考配置说明

## 1. 文档用途

这份文档用于记录你提供的旧项目 `tlias-web-management` 配置基线，并明确：

- 哪些配置风格适合在新项目中继续沿用
- 哪些配置值不能直接照搬
- 后续构建各模块时应该如何参考

这份文档不是“最终配置文件”，而是后续开发时的参考说明。

---

## 2. 可以直接参考的部分

以下内容适合作为新项目的整体技术基线继续使用：

### 2.1 技术版本

- `Spring Boot Parent`: `3.2.10`
- `Java`: `17`
- `mybatis-spring-boot-starter`: `3.0.3`
- `mysql-connector-j`
- `lombok`
- `jjwt:0.9.1`
- `jaxb-api:2.3.1`
- `activation:1.1.1`
- `jaxb-runtime:2.3.3`

说明：

- 这些依赖和版本组合已经在旧项目中验证过，适合继续作为新项目的稳定基线
- 当前新项目登录模块已经按这个方向初始化了 Spring Boot、MyBatis、MySQL、Lombok、JWT、JAXB 相关依赖

### 2.2 配置风格

以下配置风格建议继续保留：

- `application.yml` 中配置数据源
- `mybatis.configuration.log-impl: org.apache.ibatis.logging.stdout.StdOutImpl`
- `mybatis.configuration.map-underscore-to-camel-case: true`
- `logging.level.org.springframework.jdbc.support.JdbcTransactionManager: debug`
- `spring.servlet.multipart` 中配置上传大小限制

### 2.3 OSS 配置方式

旧项目的 OSS 配置思路适合保留：

1. `endpoint / bucketName / region` 放到 `application.yml`
2. `AccessKey` 和 `Secret` 不写死在配置文件中
3. 通过环境变量提供：
   - `OSS_ACCESS_KEY_ID`
   - `OSS_ACCESS_KEY_SECRET`
4. Java 侧用配置绑定类 + 工具类方式封装上传逻辑

这套方式比较适合当前项目，后续做上传模块时可以沿用。

---

## 3. 不能直接照搬的部分

以下内容只能参考风格，不能原样复制到新项目中：

### 3.1 Maven 坐标

旧项目：

- `groupId`: `com.itheima`
- `artifactId`: `tlias-web-management`

新项目不能直接使用这组坐标，原因：

- 项目名称和业务方向已经变了
- 包名应该与当前项目保持一致

当前新项目已采用：

- `groupId`: `com.mindcare`
- `artifactId`: `mindcare-service-platform`

### 3.2 application 名称

旧项目：

- `spring.application.name: tlias-web-management`

新项目不能继续使用这个名字，应改为和心理咨询预约平台一致。

### 3.3 数据库名

旧项目：

- `jdbc:mysql://localhost:3306/tlias`

新项目不能继续连接 `tlias` 数据库，应连接当前项目自己的数据库。

当前新项目使用的是：

- `jdbc:mysql://localhost:3306/mindcare_service_platform`

### 3.4 JWT 密钥

旧项目：

- `SECRET_KEY = aXRoZWltYQ==`

这个值不建议直接照搬到新项目中。

当前新项目已经使用新的 JWT 密钥，仍然保留了“工具类静态常量”的风格，以便先与旧项目实现习惯保持一致。

### 3.5 OSS Bucket

旧项目：

- `bucketName: java-ai`

这个值是否继续使用，取决于你当前阿里云 OSS 的实际 Bucket。
如果后续你使用新的 Bucket，则必须替换成你当前真实可用的 Bucket 名称。

---

## 4. 新项目当前采用策略

结合你提供的旧项目配置和当前新项目进度，当前采用的策略如下：

### 4.1 已采用

- Spring Boot 版本风格保持一致
- Java 17 保持一致
- MyBatis 配置风格保持一致
- JWT 工具类风格保持一致
- 数据源配置结构保持一致

### 4.2 暂未加入，但后续会加入

- `pagehelper-spring-boot-starter:1.4.7`
  - 等开始做分页查询模块时加入更合适

- `aliyun-sdk-oss:3.17.4`
  - 等开始做文件上传模块时加入更合适

- `spring.servlet.multipart`
  - 等进入上传模块时加入更完整

- `AliyunOSSProperties`
  - 上传模块实现时创建

- `AliyunOSSOperator`
  - 上传模块实现时创建

### 4.3 已主动做出的区别化调整

- Maven 坐标没有沿用旧项目
- 数据库名没有沿用旧项目
- JWT 密钥没有沿用旧项目
- 包名没有沿用 `com.itheima`

这些调整都是必要的，避免新旧项目在业务身份和配置上混淆。

---

## 5. 后续构建时的参考规则

后续继续实现模块时，按以下规则处理：

### 5.1 后端基础模块

- 优先保持旧项目的代码组织风格
- 继续采用 `Controller / Service / Mapper` 三层结构
- 继续保留 `Result`、`PageResult`、`GlobalExceptionHandler`、`JwtUtils` 这类基础类风格

### 5.2 分页模块

- 新增列表分页时，再引入 `pagehelper-spring-boot-starter`
- 分页返回继续使用 `PageResult`

### 5.3 上传模块

- 新增上传功能时再引入 `aliyun-sdk-oss`
- 配置项写入 `application.yml`
- 密钥走环境变量
- Java 端增加：
  - `AliyunOSSProperties`
  - `AliyunOSSOperator`
  - `UploadController`

### 5.4 JWT 模块

- 当前先继续保持工具类静态配置风格
- 后续如果你希望更规范，再改造成从配置文件中读取密钥和过期时间

---

## 6. 结论

你提供的旧项目配置非常有参考价值，但只能“沿用结构和风格”，不能“原样照搬具体值”。

新项目后续构建时，优先遵循以下原则：

1. 技术栈和配置风格尽量参考旧项目
2. 业务名称、数据库名、包名、JWT 密钥等关键值必须按新项目重新定义
3. 分页和 OSS 等依赖按模块推进时再补充，不急着一次性堆进当前最小骨架
