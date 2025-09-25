# Spring Security 集成测试指南

## 1. 测试环境准备

1. 确保应用已启动，默认端口为8080
2. 默认管理员账号已自动创建：
   - 用户名: admin
   - 密码: admin123
   - 租户ID: test-tenant

## 2. API测试方法

### 2.1 用户注册

```bash
POST http://localhost:8080/shu/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "test123",
  "tenantId": "test-tenant",
  "realName": "测试用户"
}
```

### 2.2 用户登录

```bash
POST http://localhost:8080/shu/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "tenantId": "test-tenant"
}
```

响应示例：
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "username": "admin",
    "realName": "系统管理员"
  }
}
```

### 2.3 访问受保护接口

使用获取到的token访问API：

```bash
GET http://localhost:8080/shu/user/list
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
X-Tenant: test-tenant
```

## 3. 权限说明

- 管理员权限(ADMIN): 可以访问所有接口
- 普通用户权限(USER): 只能访问查询接口

## 4. 租户路由规则

所有API请求需要在Header中添加：
```
X-Tenant: [租户ID]
```

系统会根据租户配置自动路由到对应环境。

## 5. 测试账号

| 用户名 | 密码 | 租户ID | 角色 | 权限 |
|--------|------|--------|------|------|
| admin | admin123 | test-tenant | ADMIN | 全部权限 |
| testuser | test123 | test-tenant | USER | 只读权限 |