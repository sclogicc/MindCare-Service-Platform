# 文件上传模块说明

## 1. 模块目标

当前文件上传模块实现目标：

1. 使用 `MultipartFile` 接收前端上传文件
2. 使用阿里云 OSS 存储文件
3. 提供 `UploadController`
4. 返回统一 `Result`
5. 返回文件访问地址，并附带附件主键，便于后续业务表单复用

## 2. 已创建文件

### 2.1 controller

- `src/main/java/com/mindcare/controller/UploadController.java`

### 2.2 service

- `src/main/java/com/mindcare/service/UploadService.java`
- `src/main/java/com/mindcare/service/impl/UploadServiceImpl.java`

### 2.3 utils

- `src/main/java/com/mindcare/utils/AliyunOSSProperties.java`
- `src/main/java/com/mindcare/utils/AliyunOSSOperator.java`

### 2.4 pojo

- `src/main/java/com/mindcare/pojo/UploadResult.java`
- `src/main/java/com/mindcare/pojo/UploadFile.java`

### 2.5 mapper

- `src/main/java/com/mindcare/mapper/UploadFileMapper.java`
- `src/main/resources/mapper/UploadFileMapper.xml`

## 3. 工具类设计

### 3.1 `AliyunOSSProperties`

职责：

- 绑定 `application.yml` 中的 OSS 配置
- 读取：
  - `endpoint`
  - `bucketName`
  - `region`

特点：

- 保持和 `tlias-web-management` 接近的使用风格
- AccessKey 和 Secret 不放在配置文件中

### 3.2 `AliyunOSSOperator`

职责：

- 处理和 OSS 相关的底层技术逻辑
- 生成对象存储路径
- 生成唯一文件名
- 创建 OSSClient
- 上传文件字节流
- 拼接文件访问地址

实现特点：

- 文件目录按 `yyyy/MM` 组织
- 使用 `UUID + 原后缀` 生成新文件名
- 通过环境变量读取：
  - `OSS_ACCESS_KEY_ID`
  - `OSS_ACCESS_KEY_SECRET`

## 4. controller 说明

### `POST /upload`

请求参数：

- `file`：上传文件
- `businessType`：业务类型，可选，默认 `common`

请求头：

- `token`

返回结果：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "originalName": "avatar.png",
    "fileName": "uuid.png",
    "fileUrl": "https://bucket.xxx.com/2026/05/uuid.png"
  }
}
```

说明：

- `fileUrl` 是前端最直接使用的访问地址
- `id` 可以作为后续 `avatarFileId` 或 `attachmentFileId` 保存到业务表

## 5. 配置类设计

### 5.1 application.yml

当前已加入：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 100MB

aliyun:
  oss:
    endpoint: https://oss-cn-beijing.aliyuncs.com
    bucketName: your-bucket-name
    region: cn-beijing
```

说明：

- `bucketName` 不能保持占位值，必须改成你真实可用的 OSS Bucket
- `endpoint / region` 可以按你当前 OSS 区域调整

### 5.2 环境变量

上传模块运行前，需要配置：

- `OSS_ACCESS_KEY_ID`
- `OSS_ACCESS_KEY_SECRET`

## 6. 前端如何调用上传接口

### 6.1 基本调用方式

前端通过 `FormData` 提交：

```js
const formData = new FormData()
formData.append('file', file)
formData.append('businessType', 'avatar')
```

然后调用：

- `POST /upload`

并在请求头中自动带上：

- `token`

### 6.2 Axios 调用示例

```js
export function uploadFile(file, businessType = 'common') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('businessType', businessType)

  return request({
    url: '/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
```

### 6.3 前端拿到结果后怎么用

如果是头像上传：

- 直接使用 `data.fileUrl` 做图片预览
- 保存 `data.id` 到后端业务表字段 `avatarFileId`

如果是咨询附件上传：

- 保存 `data.id` 到 `attachmentFileId`
- 页面中展示 `data.fileUrl` 作为访问地址

## 7. 当前验证结果

已执行：

```powershell
mvn -q -DskipTests compile
```

结果：编译通过。
