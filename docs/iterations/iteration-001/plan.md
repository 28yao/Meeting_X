# 企业级会议室预约系统 — 二期迭代技术方案

| 文档版本 | 1.2 |
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

### 2.2 列表接口分页（破坏性变更）

以下接口 `GET` 的 `data` 由 **数组** 改为 **分页对象** `PageResult<T>`（字段：`items`、`page`、`pageSize`、`total`）。

| 方法 | 路径 | 角色 | 查询参数 | 说明 |
|------|------|------|----------|------|
| GET | `/notifications` | 登录用户 | `page`（默认 1） | 当前用户通知，按创建时间降序 |
| GET | `/admin/users` | ADMIN | `page`（默认 1） | 用户列表，按 ID 升序 |
| GET | `/admin/rooms` | ADMIN | `page`（默认 1） | 会议室列表，按名称升序 |

**分页常量**（与 `BookingQueryService.DEFAULT_PAGE_SIZE` 一致）：

- `page`：从 1 开始，小于 1 按 1 处理
- `pageSize`：固定 20

**响应示例**（以通知为例）：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "items": [{ "id": 1, "title": "预约成功", "read": false }],
    "page": 1,
    "pageSize": 20,
    "total": 3
  }
}
```

**公共类型**：将 `booking.dto.PageResult` 迁至 `common.dto.PageResult`，供通知、用户、会议室、预约模块共用。

### 2.3 接口详情（认证）

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
| 选择时段后立即展示可用会议室 | 同一页面中时间选择器下方出现房间列表 |
| 变更时间后列表自动刷新 | 无需点击「下一步」，房间列表随时间选择变化 |
| 选中房间后进入确认步骤 | 选中房间后点下一步到达确认页 |
| GET `/notifications?page=1` | `data.items` 为数组，`data.total` 为总数 |
| GET `/admin/users?page=2` | 第二页用户列表 |
| GET `/admin/rooms` 缺省 page | 等同 `page=1` |

---

## 8. 预约界面优化（时间与会议室选择合并）

### 8.1 改动说明

将一期三步流程（选时间 → 选会议室 → 确认）简化为两步（选时间+选会议室 → 确认）。后端无变动，仅涉及前端 `BookView.vue` 与 `AvailableRoomsStep.vue` 的布局与交互调整。

### 8.2 修改文件

| 文件 | 改动 |
|------|------|
| `views/BookView.vue` | `el-steps` 从三步改为两步；Step0 中嵌入时间表单 + 会议室列表；选择时间后自动加载房间 |
| `views/book/AvailableRoomsStep.vue` | 无功能改动（仅移除 `min-height` 约束，适应内嵌布局） |

**不移除现有组件**，仅在 `BookView.vue` 中调整布局：将 `AvailableRoomsStep` 内嵌到 Step0 的时间表单下方。

### 8.3 新布局（BookView Step0）

```
┌─────────────────────────────────┐
│ 选择日期  [日期选择器]           │
│ 开始时间  [下拉选择]             │
│ 结束时间  [下拉选择]             │
├─────────────────────────────────┤
│ 可选会议室                      │
│ ┌──────┐ ┌──────┐ ┌──────┐     │
│ │ 房A  │ │ 房B  │ │ 房C  │     │
│ └──────┘ └──────┘ └──────┘     │
└─────────────────────────────────┘
          [上一步] [下一步]
```

### 8.4 交互逻辑

| 场景 | 行为 |
|------|------|
| 页面加载 | 显示时间选择器（默认明天+10:00-10:30），下方自动加载对应房间列表 |
| 用户变更日期/时间 | 自动重新查询并刷新房间列表（防抖 300ms） |
| 房间列表加载中 | 显示 `v-loading` |
| 选中房间 | 卡片高亮，`selectedRoom` 赋值 |
| 点击「下一步」 | 未选房间提示「请选择一间会议室」；已选则进入确认步 |
| 确认步 | 不变（填主题 + 提交） |

### 8.5 核心逻辑变更

**改前**（BookView.vue）：
```
activeStep: 0=选时间 → 1=选房间 → 2=确认
goNext(step0): loadRooms() → activeStep=1
goNext(step1): selectedRoom check → activeStep=2
```

**改后**：
```
activeStep: 0=选时间+选房间 → 1=确认
watch(date/startTime/endTime): loadRooms() 自动
goNext(step0): selectedRoom check → activeStep=1
```

---

## 9. 列表分页（通知 · 用户 · 会议室）

### 9.1 后端改动

| 文件 | 改动 |
|------|------|
| `common/dto/PageResult.java` | 从 `booking.dto` 迁入（booking 模块改 import） |
| `common/PagingDefaults.java` | `DEFAULT_PAGE_SIZE = 20`、`safePage(int)` |
| `notification/mapper/NotificationMapper.java` | 新增 `countByUserId`、`selectPageByUserId`（LIMIT/OFFSET） |
| `notification/NotificationService.java` | `listByUser(userId, page)` 返回 `PageResult` |
| `notification/NotificationController.java` | `GET` 增加 `@RequestParam page` |
| `user/AdminUserService.java` | `listUsers(page)` 使用 MyBatis-Plus `Page` |
| `user/AdminUserController.java` | 返回 `PageResult<AdminUserDto>` |
| `room/MeetingRoomAdminService.java` | `listRooms(page)` 使用 MyBatis-Plus `Page` |
| `room/AdminRoomController.java` | 返回 `PageResult<MeetingRoomDto>` |

### 9.2 前端改动

| 文件 | 改动 |
|------|------|
| `src/types/paging.ts` | 公共 `PageResult<T>` 类型（可选，或继续从 `api/booking` 导出） |
| `src/api/notification.ts` | `listNotifications(page)` |
| `src/api/adminUser.ts` | `listAdminUsers(page)` |
| `src/api/adminRoom.ts` | `listAdminRooms(page)` |
| `src/views/NotificationsView.vue` | 分页状态 + `el-pagination` |
| `src/views/admin/AdminUsersView.vue` | 同上 |
| `src/views/admin/AdminRoomsView.vue` | 同上 |

### 9.3 测试改动

| 文件 | 改动 |
|------|------|
| `NotificationIntegrationTest.java` | `$.data[0]` → `$.data.items[0]` |
| `AdminUserIntegrationTest.java` | `$.data` isArray → `$.data.items` isArray |
| `AdminRoomIntegrationTest.java` | 同上；`cannotDeleteRoomWithFutureBooking` 取 `data.items[0]` |

---

## 10. 修订记录

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-05-21 | 初版 |
| 1.1 | 2026-05-21 | 新增 §8 预约界面优化（时间与会议室选择合并） |
| 1.2 | 2026-05-20 | 新增 §2.2、§9 列表分页；`PageResult` 迁至 common |
