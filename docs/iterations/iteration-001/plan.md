# 企业级会议室预约系统 — 二期迭代技术方案

| 文档版本 | 1.0 |
|----------|------|
| 依据文档 | `specs/spec.md`、`specs/plan.md`、`docs/iterations/iteration-001/spec.md` |
| 目标读者 | 开发、测试、任务拆分、代码生成 Agent |
| 状态 | 可执行 |

---

## 1. 设计原则

- **增量改动**：不破坏一期已有的 8 个模块功能。
- **复用一期代码**：注册复用 `AdminUserService.createUser` 的密码加密与唯一性校验逻辑；修改密码复用 `PasswordEncoder`。
- **前端复用**：注册表单复用 `UserFormDialog.vue` 的样式与校验模式。

---

## 2. API 设计

**Base**：`/api/v1`（与一期一致）

### 2.1 新增/修改接口

| 方法 | 路径 | 角色 | 说明 |
|------|------|------|------|
| POST | `/auth/register` | 公开（无需认证） | 自助注册 EMPLOYEE 账号 |
| PUT | `/auth/profile` | 登录用户 | 修改当前用户的显示姓名 |
| POST | `/auth/change-password` | 登录用户 | 修改当前用户的登录密码 |

### 2.2 接口详情

#### POST /auth/register

请求体：

```json
{
  "username": "zhangsan",
  "password": "123456",
  "confirmPassword": "123456",
  "displayName": "张三"
}
```

成功响应（与 login 一致，注册即登录）：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "eyJ...",
    "user": { "id": 42, "username": "zhangsan", "displayName": "张三", "role": "EMPLOYEE" }
  }
}
```

错误码：

| code | 说明 |
|------|------|
| 40007 | 密码与确认密码不一致 |
| 40904 | 账号已存在（复用一期 `USERNAME_ALREADY_EXISTS`） |
| 40008 | 密码不符合长度要求 |

#### PUT /auth/profile

请求体：

```json
{
  "displayName": "新显示名"
}
```

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": { "id": 1, "username": "admin", "displayName": "新显示名", "role": "ADMIN" }
}
```

校验：

- `displayName` 不能为空，最长 128 字符。

#### POST /auth/change-password

请求体：

```json
{
  "currentPassword": "old123",
  "newPassword": "new456",
  "confirmPassword": "new456"
}
```

成功响应：`{ "code": 0, "message": "密码修改成功，请重新登录" }`

错误码：

| code | 说明 |
|------|------|
| 40103 | 当前密码错误 |
| 40007 | 新密码与确认密码不一致 |
| 40008 | 新密码不符合长度要求（6-64） |

---

## 3. 后端改动

### 3.1 新增文件

| 文件 | 说明 |
|------|------|
| `auth/dto/RegisterRequest.java` | 注册请求 DTO |
| `auth/dto/ChangePasswordRequest.java` | 修改密码请求 DTO |
| `auth/dto/UpdateProfileRequest.java` | 修改资料请求 DTO |

### 3.2 修改文件

| 文件 | 改动 |
|------|------|
| `auth/AuthController.java` | 新增 register、updateProfile、changePassword 三个端点 |
| `auth/AuthService.java` | 新增 register、updateProfile、changePassword 三个方法 |
| `config/SecurityConfig.java` | 放行 POST `/auth/register`（permitAll） |
| `common/ErrorCodes.java` | 如有需要新增错误码常量 |

### 3.3 AuthService 新增方法说明

#### register(RegisterRequest)

1. 校验 `password` 与 `confirmPassword` 一致
2. 校验 `password` 长度 6-64
3. 调用 `sysUserMapper.selectByUsername` 检查唯一性
4. 创建 `SysUser`，角色固定为 `EMPLOYEE`，`enabled = 1`
5. `passwordHash` = `passwordEncoder.encode(password)`
6. `displayName` 默认等于 `username`（若未提供）
7. 调用 `sysUserMapper.insert`
8. 签发 JWT，返回 token + user（与 login 返回结构一致）

#### updateProfile(Long userId, UpdateProfileRequest)

1. 通过 userId 查询用户
2. 更新 `displayName`
3. 调用 `sysUserMapper.updateById`
4. 返回更新后的 `UserInfoResponse`

#### changePassword(Long userId, ChangePasswordRequest)

1. 通过 userId 查询用户
2. 校验 `passwordEncoder.matches(currentPassword, user.passwordHash)`
3. 校验 `newPassword` 与 `confirmPassword` 一致
4. 校验 `newPassword` 长度 6-64
5. 更新 `passwordHash` = `passwordEncoder.encode(newPassword)`
6. 调用 `sysUserMapper.updateById`

---

## 4. 前端改动

### 4.1 新增文件

| 文件 | 说明 |
|------|------|
| `src/api/auth.ts` | 新增 register、updateProfile、changePassword API（追加至现有文件） |
| `src/views/RegisterView.vue` | 注册页 |
| `src/components/ProfileDialog.vue` | 个人设置对话框（修改显示姓名 + 修改密码） |

### 4.2 修改文件

| 文件 | 改动 |
|------|------|
| `src/views/LoginView.vue` | 去除测试账号文本；新增「注册账号」链接 |
| `src/router/index.ts` | 新增 `/register` 路由（公开） |
| `src/layouts/MainLayout.vue` | 用户名处新增下拉菜单，含「个人设置」入口 |
| `src/stores/auth.ts` | 新增 `updateUser` 方法用于局部更新 store 中的 user 对象 |

### 4.3 路由

| 路由 | 页面 | 角色 |
|------|------|------|
| `/register` | 注册页 | 公开（新增） |

### 4.4 前端页面说明

#### RegisterView.vue

- 独立注册页，风格与登录页一致。
- 表单字段：账号、显示姓名（可选）、密码、确认密码。
- 校验：账号必填、密码必填且 ≥6 位、确认密码须与密码一致。
- 成功：自动登录并跳转 `/book`。
- 失败：表单上方显示错误消息。

#### ProfileDialog.vue

- 由顶部导航栏用户名下拉菜单「个人设置」触发。
- 两个卡片或 Tab：
  - **修改显示姓名**：输入框 + 保存按钮。
  - **修改密码**：当前密码 + 新密码 + 确认新密码 + 保存按钮。
- 修改姓名：成功后更新顶部显示，关闭弹窗。
- 修改密码：成功后提示重新登录，清除 Token 跳转 `/login`。

#### MainLayout.vue

- 用户名显示由纯文本改为下拉菜单（`el-dropdown`）。
- 菜单项：「个人设置」、「退出登录」。

---

## 5. 安全配置

`SecurityConfig.java` 新增放行：

```java
.antMatchers(HttpMethod.POST, "/auth/register").permitAll()
```

同时保持已有放行规则（`/health`、`/auth/login`）不变。

---

## 6. 数据库

本期无需新增或修改表结构。所有变更均基于已有字段与表：
- `sys_user`：`display_name`、`password_hash`（一期已建）
- `meeting_room`：`status`（一期已建，NORMAL / MAINTENANCE）

---

## 7. 测试要点

| 测试场景 | 预期 |
|----------|------|
| 注册新账号 → 用该账号登录 | 成功 |
| 注册已存在的账号 | 返回重复提示 |
| 注册时密码与确认密码不一致 | 前端/后端报错 |
| 修改显示姓名 → 刷新页面 | 显示名保持为新值 |
| 修改密码 → 用旧密码登录 | 失败 |
| 修改密码 → 用新密码登录 | 成功 |
| 修改密码时当前密码错误 | 拒绝修改 |
| 登录页无测试账号文本 | 页面底部无该文本 |

---

## 8. 修订记录

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-05-21 | 初版 |
