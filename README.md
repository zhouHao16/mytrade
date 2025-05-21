# 银行交易管理系统

这是一个基于Java和Spring Boot的银行交易管理系统，用于记录、查看和管理金融交易。

## 功能特点

- 交易记录的创建、查询、修改和删除
- 按条件筛选交易（时间范围、金额范围、交易类型等）
- 分页查询功能
- 响应式Web界面
- RESTful API接口
- 内存数据存储
- 缓存机制
- 异常处理和数据校验

## 技术栈

- Java 21
- Spring Boot 3.2.3
- Thymeleaf 模板引擎
- Bootstrap 前端框架
- Maven 构建工具

## 部署方式

### 本地运行

1. 确保安装了JDK 21和Maven
2. 克隆项目到本地
3. 在项目根目录执行命令：`mvn spring-boot:run`
4. 访问 `http://localhost:8080` 

### Docker部署

1. 构建项目：`mvn clean package`
2. 构建Docker镜像：`docker build -t bank-transaction:latest .`
3. 运行容器：`docker run -p 8080:8080 bank-transaction:latest`

### Kubernetes部署

1. 构建Docker镜像并推送到仓库
2. 修改 `kubernetes/deployment.yaml` 中的镜像地址
3. 部署到Kubernetes：`kubectl apply -f kubernetes/deployment.yaml`

## 系统架构

- 控制器层（Controller）：处理HTTP请求和响应
- 服务层（Service）：实现业务逻辑
- 存储层（Repository）：实现数据访问
- 模型层（Model）：定义数据模型
- 异常处理：全局异常处理机制
- 缓存：使用Spring Cache提高性能

## 性能优化

- 使用缓存减少重复查询
- 使用线程安全的ConcurrentHashMap存储数据
- 采用分页查询减少数据传输量
- 前端使用异步加载提高响应速度

## API文档

### 创建交易
- URL: POST /api/transactions
- 请求体: JSON格式的交易数据
- 响应: 创建成功的交易记录

### 更新交易
- URL: PUT /api/transactions/{id}
- 请求体: JSON格式的交易数据
- 响应: 更新后的交易记录

### 获取交易
- URL: GET /api/transactions/{id}
- 响应: 指定ID的交易记录

### 删除交易
- URL: DELETE /api/transactions/{id}
- 响应: 成功消息

### 查询交易
- URL: GET /api/transactions
- 参数: startTime, endTime, type, minAmount, maxAmount, status, page, size
- 响应: 符合条件的交易记录列表

## 第三方库

- Spring Boot Starter Web：Web应用开发
- Spring Boot Starter Thymeleaf：模板引擎
- Spring Boot Starter Validation：数据校验
- Spring Boot DevTools：开发工具
- Spring Boot Starter Cache：缓存支持
- Lombok：减少样板代码

## 测试

运行单元测试：`mvn test`
运行集成测试：`mvn verify`

## 许可证

MIT 