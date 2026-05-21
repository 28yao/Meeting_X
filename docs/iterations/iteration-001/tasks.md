# 企业级会议室预约系统 — 二期迭代开发任务清单

| 文档版本 | 1.0 |
|----------|------|
| 依据 | `docs/iterations/iteration-001/spec.md`、`docs/iterations/iteration-001/plan.md` |
| 状态图例 | 未开始 · 进行中 · 已完成 · 阻塞 · 待确认 |

**使用说明：** 按「前置依赖」顺序执行；完成后将 `[ ]` 改为 `[x]` 并更新模块「当前状态」。

---

## 模块总览

| 模块 | 优先级 | 当前状态 |
|------|--------|----------|
| 9. 登录页优化（注册 + 去除测试账号） | P0 | 已完成 |
| 10. 员工自助信息修改 | P0 | 未开始 |
| 11. 预约界面优化（时间与会议室合并） | P0 | 已完成 |
| 12. 列表分页（通知 · 用户 · 会议室） | P0 | 已完成 |

---

# 模块 9：登录页优化（注册 + 去除测试账号）

## 1. 模块目标

在登录页增加自助注册入口与功能，允许新用户自行创建 EMPLOYEE 账号。同时移除一期遗留的测试账号提示文本。

## 2. 用户可见内容

- 登录页底部「测试账号：admin / admin123」文本已移除。
- 登录页「登录」按钮下方新增「注册账号」链接。
- `/register` 注册页：账号、密码、确认密码、显示姓名（可选）。

## 3. 用户操作流程

1. 打开登录页 → 看不到测试账号文本。
2. 点击「注册账号」→ 跳转注册页。
3. 填写信息提交 → 自动登录进入系统。
4. 或注册失败 → 页面显示错误提示。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M9-F-01 | 去除登录页测试账号文本 | `frontend/meeting-booking-web/src/views/LoginView.vue` | 无 | 登录页底部无测试账号文本 | 已完成 |
| M9-F-02 | 登录页添加「注册账号」链接 | `frontend/meeting-booking-web/src/views/LoginView.vue` | 无 | 登录按钮下方可见可点击 | 已完成 |
| M9-F-03 | 注册页 UI 与表单校验 | `frontend/meeting-booking-web/src/views/RegisterView.vue` | M9-F-02 | 字段校验：必填、密码一致、密码长度 | 已完成 |
| M9-F-04 | auth API 新增 register 方法 | `frontend/meeting-booking-web/src/api/auth.ts` | 无 | 可调通 POST /auth/register | 已完成 |
| M9-F-05 | 路由注册 `/register`（公开） | `frontend/meeting-booking-web/src/router/index.ts` | M9-F-03 | 未登录可访问 /register | 已完成 |

## 5. Controller 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M9-C-01 | POST /auth/register 端点 | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/AuthController.java` | M9-S-01 | 注册成功返回 token+user | 已完成 |

## 6. Service 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M9-S-01 | AuthService.register 方法 | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/AuthService.java` | M2-M-01（SysUserMapper） | 创建 EMPLOYEE 用户并签发 JWT | 已完成 |
| M9-S-02 | RegisterRequest DTO | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/dto/RegisterRequest.java` | 无 | 含 username/password/confirmPassword/displayName | 已完成 |

## 7. Mapper 任务

本模块无新增 Mapper，复用 `SysUserMapper.selectByUsername` 与 `insert`。

## 8. 安全配置任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M9-SEC-01 | 放行 POST /auth/register | `backend/meeting-booking-api/src/main/java/com/meeting/booking/config/SecurityConfig.java` | M9-C-01 | 未登录可访问注册接口 | 已完成 |

## 9. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 访问登录页 | 底部无测试账号文本，有注册链接 |
| 2 | 点击「注册账号」 | 跳转 /register |
| 3 | 填写新账号信息并提交 | 自动登录进入系统 |
| 4 | 退出后用新账号登录 | 登录成功 |

## 10. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | POST `/auth/register` 合法 body | 200 + token |
| 2 | 重复注册同一账号 | 409 + 账号已存在 |
| 3 | 密码与确认密码不一致 | 400 + 错误提示 |

## 11. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 密码长度 < 6 | 400 + 提示 |
| 2 | 注册时用户名超长 | 400 + 提示 |
| 3 | 注册时所有字段为空 | 前端校验拦截 |

## 12. 当前状态

**已完成。**

| 项 | 说明 |
|----|------|
| 后端接口 | POST `/auth/register` |
| 数据保存 | `sys_user` 表 INSERT |
| 失败层级 | 前端校验 / 后端校验 / 数据库唯一约束 |
| 前端路由 | `/register`（公开） |

---

# 模块 10：员工自助信息修改

## 1. 模块目标

员工可在系统内修改自己的显示姓名和登录密码。

## 2. 用户可见内容

- 顶部导航栏用户名变为下拉菜单，含「个人设置」选项。
- 个人设置弹窗/页：两个区域——修改显示姓名、修改密码。
- 修改姓名后顶部导航栏立即更新。
- 修改密码后需重新登录。

## 3. 用户操作流程

1. 点击顶部导航栏用户名 → 下拉菜单出现。
2. 点击「个人设置」→ 弹出设置对话框。
3. 修改显示姓名 → 保存 → 关闭弹窗 → 顶部已更新。
4. 修改密码 → 输入当前密码、新密码、确认密码 → 保存 → 提示重新登录 → 跳转登录页。
5. 点击「退出登录」（仍在下拉菜单中）。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M10-F-01 | 顶部导航用户名改为下拉菜单（含个人设置、退出登录） | `frontend/meeting-booking-web/src/layouts/MainLayout.vue` | 无 | 点击用户名出现下拉菜单 | 未开始 |
| M10-F-02 | 个人设置弹窗（修改显示姓名 + 修改密码） | `frontend/meeting-booking-web/src/components/ProfileDialog.vue` | M10-F-01 | 修改姓名后顶部更新；改密后跳转登录 | 未开始 |
| M10-F-03 | auth API 新增 updateProfile 和 changePassword | `frontend/meeting-booking-web/src/api/auth.ts` | 无 | 可调通后端接口 | 未开始 |
| M10-F-04 | auth store 新增 updateUser 方法 | `frontend/meeting-booking-web/src/stores/auth.ts` | 无 | 修改姓名后 store 同步更新 | 未开始 |

## 5. Controller 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M10-C-01 | PUT /auth/profile 端点 | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/AuthController.java` | M10-S-01 | 更新 displayName 成功 | 未开始 |
| M10-C-02 | POST /auth/change-password 端点 | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/AuthController.java` | M10-S-02 | 改密后旧 token 失效（需重新登录） | 未开始 |

## 6. Service 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M10-S-01 | AuthService.updateProfile 方法 | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/AuthService.java` | M2-M-01 | 更新 displayName 并返回新用户信息 | 未开始 |
| M10-S-02 | AuthService.changePassword 方法 | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/AuthService.java` | M2-M-01 | 当前密码校验通过后更新 passwordHash | 未开始 |
| M10-S-03 | UpdateProfileRequest DTO | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/dto/UpdateProfileRequest.java` | 无 | 含 displayName | 未开始 |
| M10-S-04 | ChangePasswordRequest DTO | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/dto/ChangePasswordRequest.java` | 无 | 含 currentPassword/newPassword/confirmPassword | 未开始 |

## 7. Mapper 任务

本模块无新增 Mapper，复用 `SysUserMapper.selectById` 与 `updateById`。

## 8. Repository / 数据保存任务

UPDATE `sys_user.display_name` 或 `sys_user.password_hash`。

## 9. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 登录后点击顶部用户名 | 出现下拉菜单 |
| 2 | 点击「个人设置」→ 修改显示姓名 | 保存后顶部姓名更新 |
| 3 | 在个人设置中修改密码 | 提示重新登录，跳转登录页 |
| 4 | 用旧密码登录 | 失败 |
| 5 | 用新密码登录 | 成功 |

## 10. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | PUT `/auth/profile` 合法 body | 200 + 新用户信息 |
| 2 | POST `/auth/change-password` 正确当前密码 | 200 |
| 3 | POST `/auth/change-password` 错误当前密码 | 40103 |

## 11. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 修改密码：新密码与确认密码不一致 | 400 |
| 2 | 修改密码：新密码长度 < 6 | 400 |
| 3 | 修改姓名：传空值 | 400 |
| 4 | 无 token 访问 profile 或 change-password | 401 |

## 12. 当前状态

**未开始。**

| 项 | 说明 |
|----|------|
| 后端接口 | PUT `/auth/profile`；POST `/auth/change-password` |
| 数据保存 | `sys_user` 表 UPDATE display_name / password_hash |
| 失败层级 | 认证 / 参数校验 / 当前密码校验 |
| 前端入口 | 顶部导航栏用户名下拉菜单 → 个人设置 |

---

# 模块 11：预约界面优化（时间与会议室选择合并）

## 1. 模块目标

将预约流程中「选择时间」与「选择会议室」两步合并为一步，用户选择时段后即时看到可用会议室列表，减少操作步骤，提升预约效率。

## 2. 用户可见内容

- 预约页面步骤条从三步改为两步（选择时间与会议室 → 确认预约）。
- 第一步中上方为时间选择器，下方为对应时段的会议室列表。
- 更改时间后列表自动刷新。
- 会议室卡片选中后高亮。

## 3. 用户操作流程

1. 进入预约页，默认显示时间选择器与加载中的会议室列表。
2. 选择日期、开始/结束时间 → 列表自动刷新。
3. 点击一间会议室卡片选中（高亮）。
4. 点击「下一步」进入确认页。
5. 填写会议主题，点击「确认预约」。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M11-F-01 | BookView 改为两步步骤条 | `frontend/meeting-booking-web/src/views/BookView.vue` | 无 | 步骤条显示 2 步 | 未开始 |
| M11-F-02 | Step0 内嵌时间表单 + 会议室列表 | `frontend/meeting-booking-web/src/views/BookView.vue` | M11-F-01 | 时间选择器下方显示房间列表 | 未开始 |
| M11-F-03 | 时间变更自动加载房间 | `frontend/meeting-booking-web/src/views/BookView.vue` | M11-F-02 | 切换时间后列表自动刷新 | 未开始 |
| M11-F-04 | 删除旧三步相关逻辑 | `frontend/meeting-booking-web/src/views/BookView.vue` | M11-F-03 | 无残留的三步逻辑 | 未开始 |

## 5. Controller 任务

本模块无变更。

## 6. Service 任务

本模块无变更。

## 7. Mapper 任务

本模块无变更。

## 8. Repository / 数据保存任务

无变更。

## 9. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 进入预约页 `/book` | 看到时间选择器和下方会议室列表（可加载中或空） |
| 2 | 选择明天 10:00-10:30 | 列表出现可用会议室 |
| 3 | 将时间改为 10:00-11:00 | 列表自动刷新 |
| 4 | 点击一间会议室 | 卡片高亮选中 |
| 5 | 点击「下一步」 | 进入确认页，显示选中房间信息 |
| 6 | 点击「确认预约」 | 预约成功 |

## 10. 接口测试方法

本模块无新增接口。

## 11. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 不选房间直接点下一步 | 提示「请选择一间会议室」 |

## 12. 当前状态

**已完成。**

| 项 | 说明 |
|----|------|
| 变更范围 | 仅前端 `BookView.vue`、`AvailableRoomsStep.vue` |
| 失败层级 | 前端交互逻辑 |

---

# 模块 12：列表分页（通知 · 用户管理 · 会议室管理）

## 1. 模块目标

为通知中心、管理员用户列表、管理员会议室列表增加与「我的预约」一致的后端分页与前端翻页组件，避免一次加载全量数据。

## 2. 用户可见内容

- 三个页面底部在数据超过 20 条时出现翻页器（上一页 / 页码 / 下一页）。
- 默认进入第 1 页；切换页码后列表刷新。

## 3. 用户操作流程

1. 打开通知中心 / 用户管理 / 会议室管理 → 看到第 1 页数据。
2. 点击第 2 页 → 列表加载第 2 页内容。
3. 在当前页完成删除或编辑 → 列表刷新，仍停留在当前页（若当前页为空且非第 1 页则回退一页）。

## 4. 后端任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M12-B-01 | PageResult 迁至 common.dto | `common/dto/PageResult.java`；booking 改 import | 无 | 编译通过 | 已完成 |
| M12-B-02 | PagingDefaults 工具 | `common/PagingDefaults.java` | M12-B-01 | page&lt;1 归 1 | 已完成 |
| M12-B-03 | 通知列表分页 | NotificationMapper/Service/Controller | M12-B-01 | GET `?page=1` 返回 items+total | 已完成 |
| M12-B-04 | 用户列表分页 | AdminUserService/Controller | M12-B-01 | GET `?page=1` 返回 PageResult | 已完成 |
| M12-B-05 | 会议室列表分页 | MeetingRoomAdminService/Controller | M12-B-01 | GET `?page=1` 返回 PageResult | 已完成 |
| M12-B-06 | 集成测试更新 | 三个 IntegrationTest | M12-B-03～05 | jsonPath 已改 | 已完成 |

## 5. 前端任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M12-F-01 | API 传 page 参数 | `notification.ts`、`adminUser.ts`、`adminRoom.ts` | M12-B-03～05 | 请求带 `?page=` | 已完成 |
| M12-F-02 | 通知中心分页 UI | `NotificationsView.vue` | M12-F-01 | 翻页可用 | 已完成 |
| M12-F-03 | 用户管理分页 UI | `AdminUsersView.vue` | M12-F-01 | 翻页可用 | 已完成 |
| M12-F-04 | 会议室管理分页 UI | `AdminRoomsView.vue` | M12-F-01 | 翻页可用 | 已完成 |

## 6. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 通知超过 20 条时打开 `/notifications` | 底部出现分页，切换页码列表变化 |
| 2 | 用户管理 `/admin/users` | 同上 |
| 3 | 会议室管理 `/admin/rooms` | 同上 |
| 4 | 通知「全部标为已读」 | 当前页刷新，未读角标更新 |

## 7. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | GET `/notifications?page=1` | `data.items` 数组，`data.pageSize`=20 |
| 2 | GET `/admin/users?page=1` | 同上 |
| 3 | GET `/admin/rooms` 无 page | 等同第 1 页 |

## 8. 当前状态

**已完成。**

| 项 | 说明 |
|----|------|
| 依据 | `spec.md` §3.6、`plan.md` §9 |
| 破坏性 | 三个列表 GET 的 `data` 结构变更，前后端须同版本发布 |
| 备注 | 改约下拉使用 `listAllAdminRooms()` 合并多页；`GET /rooms` 仍返回全量列表 |

---

# 修订记录

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-05-21 | 初版，依据二期迭代 spec/plan 拆分 |
| 1.1 | 2026-05-21 | 新增模块 11 预约界面优化 |
| 1.2 | 2026-05-20 | 新增模块 12 列表分页 |
