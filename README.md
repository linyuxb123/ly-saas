# Ly-SaaS 项目说明文档

## 1. 项目概述

Ly-SaaS 是一个基于 Spring Boot 的多租户 SaaS 应用，支持灰度发布功能。该项目采用模块化设计，可以根据不同租户的需求将请求路由到不同的业务环境模块，实现了灵活的多租户管理和灰度发布机制。

### 1.1 技术栈

- **核心框架**: Spring Boot 2.x, Spring Security, Spring Cloud
- **数据库**: MySQL 8.x, MyBatis-Plus
- **消息队列**: Apache RocketMQ
- **配置中心**: Consul（可选）
- **安全认证**: JWT (JSON Web Tokens)
- **构建工具**: Maven
- **Java 版本**: JDK 17

### 1.2 项目结构

```
ly-saas/
├── doc/                    # 项目文档
├── ly-saas-common/         # 通用模块
├── ly-saas-server/         # 主服务模块
├── ly-saas-shu/            # 环境模块 shu
│   ├── shu-api/            # API 控制器
│   ├── shu-core/           # 核心实体和持久层
│   └── shu-service/        # 业务服务层
├── ly-saas-wei/            # 环境模块 wei
│   ├── wei-api/
│   ├── wei-core/
│   └── wei-service/
├── ly-saas-wu/             # 环境模块 wu
│   ├── wu-api/
│   ├── wu-core/
│   └── wu-service/
└── pom.xml                 # 父项目配置
```

## 2. 核心功能

### 2.1 多租户支持

项目通过租户标识（Tenant ID）实现多租户隔离。每个租户的数据通过 `tenant_id` 字段进行区分，确保数据安全和隔离。

#### 租户路由机制

1. 通过 HTTP 请求头 `X-Tenant` 传递租户标识
2. TenantRoutingFilter 拦截请求，根据租户标识确定对应的业务环境
3. 将请求路由到相应的环境模块进行处理

#### 租户配置

租户与环境的映射关系可在配置文件中定义：

```yaml
tenant:
  mapping:
    dingding: wei
    qiwei: wu
    huawei: shu
  default-environment: shu
```

### 2.2 灰度发布

项目支持基于租户的灰度发布功能，不同租户可以使用不同版本的服务：

- **shu 环境**: 主要环境，huawei 租户使用
- **wei 环境**: 灰度环境，dingding 租户使用
- **wu 环境**: 灰度环境，qiwei 租户使用

### 2.3 用户和部门管理

每个环境模块都提供完整的用户和部门管理功能：

#### 用户管理
- 用户注册与登录
- 用户信息维护
- 用户状态管理
- 用户权限控制

#### 部门管理
- 部门结构维护
- 部门层级关系管理
- 部门人员管理

### 2.4 安全认证

项目采用基于 JWT 的安全认证机制：

1. 用户通过 `/auth/login` 接口进行身份认证
2. 认证成功后返回 JWT Token
3. 后续请求需在 Header 中携带 Token: `Authorization: Bearer <token>`
4. 系统通过 Token 验证用户身份和权限

### 2.5 消息队列集成

项目集成了 RocketMQ 消息队列，支持异步消息处理：

- 消息生产者
- 消息消费者
- 消息测试接口

## 3. 数据库设计

### 3.1 用户表 (saas_user)

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键ID |
| username | VARCHAR(50) | 用户名 |
| password | VARCHAR(100) | 密码（加密存储） |
| real_name | VARCHAR(50) | 真实姓名 |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| dept_id | BIGINT | 部门ID |
| status | TINYINT | 状态（0-禁用，1-正常） |
| tenant_id | VARCHAR(50) | 租户ID |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 3.2 部门表 (saas_dept)

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键ID |
| dept_name | VARCHAR(50) | 部门名称 |
| parent_id | BIGINT | 父部门ID |
| ancestors | VARCHAR(100) | 祖级列表 |
| order_num | INT | 显示顺序 |
| leader | VARCHAR(50) | 负责人 |
| phone | VARCHAR(20) | 联系电话 |
| email | VARCHAR(100) | 邮箱 |
| status | TINYINT | 状态（0-禁用，1-正常） |
| tenant_id | VARCHAR(50) | 租户ID |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 3.3 租户配置表 (saas_tenant_config)

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键ID |
| tenant_id | VARCHAR(50) | 租户ID |
| environment | VARCHAR(50) | 环境名称 |
| created_time | DATETIME | 创建时间 |
| updated_time | DATETIME | 更新时间 |

## 4. API 接口

### 4.1 认证接口

#### 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "tenantId": "huawei"
}
```

#### 用户注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "test123",
  "realName": "测试用户",
  "tenantId": "huawei"
}
```

### 4.2 用户管理接口

#### 获取用户列表
```
GET /api/user/list?pageNum=1&pageSize=10
Authorization: Bearer <token>
```

#### 获取所有用户
```
GET /api/user/allUsers
Authorization: Bearer <token>
```

#### 获取用户详情
```
GET /api/user/{id}
Authorization: Bearer <token>
```

#### 新增用户
```
POST /api/user
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "newuser",
  "password": "newpass",
  "realName": "新用户",
  "tenantId": "huawei"
}
```

#### 修改用户
```
PUT /api/user
Authorization: Bearer <token>
Content-Type: application/json

{
  "id": 1,
  "username": "updateduser",
  "realName": "更新用户"
}
```

#### 删除用户
```
DELETE /api/user/{id}
Authorization: Bearer <token>
```

### 4.3 部门管理接口

#### 获取部门列表
```
GET /api/dept/list
Authorization: Bearer <token>
```

#### 获取部门树
```
GET /api/dept/tree
Authorization: Bearer <token>
```

#### 获取部门详情
```
GET /api/dept/{id}
Authorization: Bearer <token>
```

#### 新增部门
```
POST /api/dept
Authorization: Bearer <token>
Content-Type: application/json

{
  "deptName": "新部门",
  "parentId": 0,
  "orderNum": 1
}
```

#### 修改部门
```
PUT /api/dept
Authorization: Bearer <token>
Content-Type: application/json

{
  "id": 1,
  "deptName": "更新部门",
  "orderNum": 2
}
```

#### 删除部门
```
DELETE /api/dept/{id}
Authorization: Bearer <token>
```

### 4.4 租户配置接口

#### 获取租户映射配置
```
GET /api/tenant-config/mappings
Authorization: Bearer <token>
```

#### 更新租户环境映射
```
POST /api/tenant-config/mapping?tenantId=test&environment=shu
Authorization: Bearer <token>
```

#### 删除租户环境映射
```
DELETE /api/tenant-config/mapping/{tenantId}
Authorization: Bearer <token>
```

#### 更新默认环境
```
POST /api/tenant-config/default-environment?environment=wei
Authorization: Bearer <token>
```

#### 刷新配置
```
POST /api/tenant-config/refresh
Authorization: Bearer <token>
```

## 5. 部署与配置

### 5.1 环境要求

- JDK 17 或更高版本
- MySQL 8.0 或更高版本
- RocketMQ 服务（可选）
- Consul 服务（可选）

### 5.2 数据库配置

1. 执行 doc/database.sql 创建数据库和表结构
2. 修改 application.yml 中的数据库连接配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ly_saas
    username: root
    password: your_password
```

### 5.3 启动项目

```bash
# 编译项目
mvn clean install

# 启动服务
cd ly-saas-server
mvn spring-boot:run
```

### 5.4 访问地址

项目启动后，可通过以下地址访问：
- API 基础路径: `http://localhost:8081/api`
- 默认登录账号: admin/admin123

## 6. 扩展与维护

### 6.1 添加新环境模块

可以通过 version_replace.sh 脚本快速创建新的环境模块：

```bash
./version_replace.sh shu xia
```

该命令会将 shu 模块复制为 xia 模块，并自动替换相关配置。

### 6.2 配置 Consul（可选）

1. 启动 Consul 服务
2. 在 application.yml 中配置 Consul 地址
3. 设置 `tenant.config.enabled: true` 启用 Consul 配置

## 7. 故障排除

### 7.1 认证失败

如果登录时出现认证失败，检查以下几点：
1. 确认用户名和密码正确
2. 确认租户ID正确且在配置的租户映射中
3. 检查数据库中用户记录的 tenant_id 是否与请求中的租户ID一致

### 7.2 路由问题

如果请求没有正确路由到对应的环境模块：
1. 检查请求头是否包含正确的 `X-Tenant` 字段
2. 检查 TenantProperties 中的租户映射配置是否正确
3. 检查 TenantRoutingFilter 是否正常工作

这个项目提供了一个完整的多租户 SaaS 解决方案，支持灵活的灰度发布和租户隔离，适用于需要为不同客户提供差异化服务的场景。