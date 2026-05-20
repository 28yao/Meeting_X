# 企业级会议室预约系统 — 开发任务清单

| 文档版本 | 1.0 |
|----------|-----|
| 依据 | `specs/spec.md` v1.0、`specs/plan.md` v1.0、`AGENTS.md` |
| 状态图例 | 未开始 · 进行中 · 已完成 · 阻塞 · 待确认 |

**使用说明：** 每条任务尽量只改/建一个主文件；按「前置依赖」顺序执行；完成后将 `[ ]` 改为 `[x]` 并更新模块「当前状态」。

---

## 模块总览

| 模块 | 优先级 | 当前状态 |
|------|--------|----------|
| 1. 项目脚手架与基础设施 | P0 | 已完成 |
| 2. 用户登录认证 | P0 | 已完成 |
| 3. 会议室预约（员工） | P0 | 已完成 |
| 4. 我的预约 | P0 | 已完成 |
| 5. 站内通知 | P1 | 已完成 |
| 6. 管理员 — 用户管理 | P0 | 已完成 |
| 7. 管理员 — 会议室管理 | P0 | 已完成 |
| 8. 管理员 — 预约管理 | P0 | 已完成 |

---

# 模块 1：项目脚手架与基础设施

## 1. 模块目标

搭建可运行的前后端工程、数据库迁移、统一 API 响应格式与全局异常处理，为所有业务模块提供基础。**当前阶段用户看不到具体业务功能**；主要用于支撑后续功能开发。可通过启动项目、访问默认页面或健康检查接口验证。

## 2. 用户可见内容

- 当前阶段：无完整业务流程。
- 脚手架完成后：浏览器可打开前端（可能为空白或占位页）；后端健康检查或 Swagger（若配置）可访问。

## 3. 用户操作流程

本阶段无业务操作。验证流程：启动 MySQL → 启动后端 → 启动前端 → 确认无启动报错。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M1-F-01 | 初始化 Vue3+Vite+Element Plus 工程 | `frontend/meeting-booking-web/package.json` | 无 | `npm install` 与 `npm run dev` 成功 | 已完成 |
| M1-F-02 | 配置 Vue Router 与基础布局壳 | `frontend/meeting-booking-web/src/router/index.ts` | M1-F-01 | 访问根路径不白屏报错 | 已完成 |
| M1-F-03 | 配置 Axios 实例与 API 基址环境变量 | `frontend/meeting-booking-web/src/api/http.ts` | M1-F-01 | 请求可发到配置的 baseURL | 已完成 |
| M1-F-04 | 添加占位首页/重定向到 login | `frontend/meeting-booking-web/src/views/HomePlaceholder.vue` | M1-F-02 | 打开 `/` 有可见内容或跳转 | 已完成 |

## 5. Controller 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M1-C-01 | 健康检查接口 GET /actuator/health 或 /api/v1/health | `backend/meeting-booking-api/src/main/java/com/meeting/booking/controller/HealthController.java` | M1-B-02 | GET 返回 200 | 已完成 |

## 6. Service 任务

本模块无业务 Service。

## 7. Mapper 任务

本模块无业务 Mapper。

## 8. Repository / 数据保存任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M1-D-01 | Flyway V1 建表 sys_user/meeting_room/booking/notification | `backend/meeting-booking-api/src/main/resources/db/migration/V1__init.sql` | M1-B-02 | 启动后四表存在 | 已完成 |
| M1-D-02 | Flyway 种子：admin 用户与示例会议室 | `backend/meeting-booking-api/src/main/resources/db/migration/V2__seed.sql` | M1-D-01 | admin 可查到；至少 1 间示例房 | 已完成 |

## 9. 后端其他（归入 Service 层配套）

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M1-B-01 | 初始化 Spring Boot 2.7 父工程与 pom | `backend/meeting-booking-api/pom.xml` | 无 | `mvn compile` 成功 | 已完成 |
| M1-B-02 | application.yml 数据源与 Flyway 配置 | `backend/meeting-booking-api/src/main/resources/application.yml` | M1-B-01 | 连上 MySQL 并执行迁移 | 已完成 |
| M1-B-03 | 统一响应体 ApiResponse | `backend/meeting-booking-api/src/main/java/com/meeting/booking/common/ApiResponse.java` | M1-B-01 | 单元或手工封装测试 | 已完成 |
| M1-B-04 | 全局异常处理 GlobalExceptionHandler | `backend/meeting-booking-api/src/main/java/com/meeting/booking/common/GlobalExceptionHandler.java` | M1-B-03 | 抛业务异常返回统一 JSON | 已完成 |
| M1-B-05 | docker-compose：MySQL+后端+前端 | `docker-compose.yml` | M1-B-02, M1-F-01 | `docker-compose up` 三服务健康 | 已完成 |

## 10. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 在 `frontend/meeting-booking-web` 执行 `npm run dev` | 终端无报错 |
| 2 | 浏览器打开开发地址（通常 localhost:5173） | 页面能打开，无整页崩溃 |
| 3 | 打开浏览器开发者工具 Network | 无持续失败的静态资源请求 |

## 11. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 启动 MySQL 与后端 | 日志显示 Flyway 迁移成功 |
| 2 | 用浏览器或 curl 访问健康检查 URL | HTTP 200，body 表示服务正常 |
| 3 | 查数据库 | 存在 sys_user、meeting_room、booking、notification 表 |

## 12. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 故意配错数据库密码后启动后端 | 启动失败，日志有明确数据库连接错误 |
| 2 | 不启动后端，只开前端 | 页面仍可打开（业务接口会失败，属预期） |

## 13. 当前状态

**已完成。** 失败排查：启动报错看后端日志；页面打不开看前端终端；数据库看 Flyway 日志。

### 数据与接口（本模块）

| 项 | 说明 |
|----|------|
| 后端接口 | GET `http://localhost:8080/api/v1/health` |
| 数据保存 | Flyway 脚本写入 MySQL |
| 失败层级 | 配置层 / 数据库连接层 / 依赖未安装 |

---

# 模块 2：用户登录认证

## 1. 模块目标

实现管理员分配账号后的登录能力，JWT 认证，区分 EMPLOYEE 与 ADMIN 角色，支撑后续所有需登录接口。[US-05 前提][SPEC-4.2]

## 2. 用户可见内容

- 登录页：账号、密码输入框，「登录」按钮。
- 登录成功：跳转到预约页或首页；顶部显示当前用户显示名。
- 登录失败：页面上有错误提示（账号密码错误等）。

## 3. 用户操作流程

1. 打开 `/login`。
2. 输入管理员创建的账号、密码，点击「登录」。
3. 成功 → 进入系统，Token 保存在浏览器（localStorage 或 session，与实现一致）。
4. 失败 → 停留在登录页，显示错误信息。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M2-F-01 | 登录页 UI 与表单校验 | `frontend/meeting-booking-web/src/views/LoginView.vue` | M1-F-02, M1-F-03 | 空账号提交有前端提示 | 已完成 |
| M2-F-02 | auth API：login、getMe | `frontend/meeting-booking-web/src/api/auth.ts` | M2-F-01 | 能调通 POST login | 已完成 |
| M2-F-03 | Pinia/存储 Token 与用户信息 | `frontend/meeting-booking-web/src/stores/auth.ts` | M2-F-02 | 登录后刷新仍保持（若用 localStorage） | 已完成 |
| M2-F-04 | 路由守卫：未登录跳转 login | `frontend/meeting-booking-web/src/router/guards.ts` | M2-F-03 | 未登录访问 /book 跳转登录 | 已完成 |
| M2-F-05 | Axios 请求拦截器附加 Bearer Token | `frontend/meeting-booking-web/src/api/http.ts` | M2-F-03 | 受保护接口带 Authorization | 已完成 |
| M2-F-06 | 布局：显示用户名与退出 | `frontend/meeting-booking-web/src/layouts/MainLayout.vue` | M2-F-03 | 点击退出回到登录页 | 已完成 |

## 5. Controller 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M2-C-01 | POST /api/v1/auth/login | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/AuthController.java` | M2-S-02 | 正确账号返回 token | 已完成 |
| M2-C-02 | GET /api/v1/auth/me | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/AuthController.java` | M2-S-03, M2-S-04 | 带 token 返回用户信息 | 已完成 |

## 6. Service 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M2-S-01 | User 实体与 SysUserMapper | `backend/meeting-booking-api/src/main/java/com/meeting/booking/user/entity/SysUser.java` | M1-D-01 | 能按 username 查询 | 已完成 |
| M2-S-02 | AuthService：校验密码、签发 JWT | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/AuthService.java` | M2-S-01, M2-S-05 | 错密抛认证失败 | 已完成 |
| M2-S-03 | JwtTokenProvider 生成与解析 | `backend/meeting-booking-api/src/main/java/com/meeting/booking/auth/JwtTokenProvider.java` | M1-B-02 | 单测 token 往返 | 已完成 |
| M2-S-04 | SecurityConfig：放行 login，其余需认证 | `backend/meeting-booking-api/src/main/java/com/meeting/booking/config/SecurityConfig.java` | M2-S-03 | 无 token 访问 /bookings 返回 401 | 已完成 |
| M2-S-05 | BCrypt 密码编码器 Bean | `backend/meeting-booking-api/src/main/java/com/meeting/booking/config/PasswordEncoderConfig.java` | M1-B-01 | 种子 admin 密码可验证 | 已完成 |

## 7. Mapper 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M2-M-01 | SysUserMapper 接口与 XML/注解 | `backend/meeting-booking-api/src/main/java/com/meeting/booking/user/mapper/SysUserMapper.java` | M1-D-01 | selectByUsername 有结果 | 已完成 |

## 8. Repository / 数据保存任务

数据已存在于 `sys_user`（M1-D-02 种子）；本模块只读校验，登录不写库。

## 9. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 打开 `/login` | 看到账号、密码、登录按钮 |
| 2 | 输入错误密码点登录 | 页面提示失败，不进入系统 |
| 3 | 用种子 admin 正确登录 | 进入系统，能看到用户名 |
| 4 | 点击退出后再访问 /book | 自动跳回登录页 |

## 10. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | POST `/api/v1/auth/login` body `{username,password}` | 200 + token |
| 2 | GET `/api/v1/auth/me` 无 Header | 401 |
| 3 | GET `/api/v1/auth/me` Header `Bearer <token>` | 200 + displayName、role |

## 11. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 禁用用户登录（若实现 enabled 校验） | 拒绝登录并有提示 |
| 2 | Token 过期后访问受保护接口 | 401，前端跳转登录 |

## 12. 当前状态

**已完成。**

| 项 | 说明 |
|----|------|
| 后端接口 | POST `/auth/login`；GET `/auth/me` |
| 数据保存 | `sys_user` 表（管理员预先创建） |
| 失败可能层级 | 前端校验 / 网络 / 认证 Service / 数据库无用户 |

---

# 模块 3：会议室预约（员工）

## 1. 模块目标

实现员工主流程：选日期时段 → 展示空闲会议室 → 选房间填主题 → 提交生效。[US-01][SPEC-6.1]

## 2. 用户可见内容

- 预约向导 `/book`：步骤条（选时间 / 选房间 / 确认）。
- Step1：日期选择器、开始/结束时间（15 分钟步进）。
- Step2：卡片列表展示空闲房间（名称、容量、楼层、类型、设备）。
- Step3：会议主题输入、所选房间与时间摘要、「确认预约」按钮。
- 成功/失败：Toast 或消息框；冲突、维护、过去时间等有明确中文提示。

## 3. 用户操作流程

1. 登录后进入「预约会议室」。
2. 选未来 30 天内某日，开始/结束时间（同一天，15 分钟对齐）。
3. 下一步 → 系统请求空闲列表 → 用户点选一间房。
4. 下一步 → 填写会议主题 → 确认提交。
5. 成功 → 提示成功（后续模块跳转「我的预约」）；失败 → 停留当前步并提示原因。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M3-F-01 | BookView 步骤1：日期时段表单 | `frontend/.../views/BookView.vue` | M2-F-04 | 仅选时间可下一步 | 已完成 |
| M3-F-02 | BookView 步骤2：空闲房间列表 | `frontend/.../views/book/AvailableRoomsStep.vue` | M3-F-03 | 有数据时展示房间卡片 | 已完成 |
| M3-F-03 | room API：getAvailableRooms | `frontend/.../api/room.ts` | M3-B-C-01 | 参数 date/start/end 正确 | 已完成 |
| M3-F-04 | BookView 步骤3：主题与确认 | `frontend/.../views/book/ConfirmStep.vue` | M3-F-02 | 主题必填校验 | 已完成 |
| M3-F-05 | booking API：createBooking | `frontend/.../api/booking.ts` | M3-C-01 | POST 成功跳转或提示 | 已完成 |
| M3-F-06 | 错误码映射为用户可读文案 | `frontend/.../utils/errorMessages.ts` | M1-B-04 | 40901 等显示中文 | 已完成 |

## 5. Controller 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M3-C-01 | POST /api/v1/bookings | `backend/.../booking/BookingController.java` | M3-S-03 | 合法 body 返回 201/200 | 已完成 |
| M3-C-02 | GET /api/v1/rooms/available | `backend/.../room/RoomController.java` | M3-S-04 | 返回空闲房列表 | 已完成 |
| M3-C-03 | GET /api/v1/rooms/{id}/occupancy | `backend/.../room/RoomController.java` | M3-S-05 | 返回当日占用片段 | 已完成 |

## 6. Service 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M3-S-01 | TimeValidator（TR-01～05） | `backend/.../booking/TimeValidator.java` | 无 | 单测覆盖 5 条规则 | 已完成 |
| M3-S-02 | BookingConflictChecker overlap 查询 | `backend/.../booking/BookingConflictChecker.java` | M3-M-02 | 单测重叠/相邻不冲突 | 已完成 |
| M3-S-03 | BookingService.create 事务+并发 | `backend/.../booking/BookingService.java` | M3-S-01,M3-S-02 | 并发测试仅 1 成功 | 已完成 |
| M3-S-04 | RoomAvailabilityService 空闲列表 | `backend/.../room/RoomAvailabilityService.java` | M3-M-01 | 维护中房间不出现 | 已完成 |
| M3-S-05 | RoomOccupancyService 占用片段 DTO | `backend/.../room/RoomOccupancyService.java` | M3-M-02 | 他人预约含主题+组织者 | 已完成 |

## 7. Mapper 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M3-M-01 | MeetingRoomMapper | `backend/.../room/mapper/MeetingRoomMapper.java` | M1-D-01 | 查 NORMAL 房间 | 已完成 |
| M3-M-02 | BookingMapper 含 overlap 查询 | `backend/.../booking/mapper/BookingMapper.java` | M1-D-01 | SQL 参数化 overlap | 已完成 |

## 8. Repository / 数据保存任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M3-R-01 | Booking 实体与插入 | `backend/.../booking/entity/Booking.java` | M1-D-01 | 创建后 booking 表有 CONFIRMED 行 | 已完成 |

## 9. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 员工登录，打开 `/book` | 看到选日期时间界面 |
| 2 | 选明天 10:00-10:30，下一步 | 列出空闲会议室 |
| 3 | 选一间，填主题「周会」，提交 | 成功提示 |
| 4 | 用另一账号预约同一房间重叠时段 | 失败并提示已被占用 |

## 10. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | GET `/rooms/available?date&startTime&endTime` 带 token | 200 列表 |
| 2 | POST `/bookings` 合法 body | 200/201 + bookingId |
| 3 | POST 重叠时段同房间 | 409 + code 40901 |
| 4 | POST 维护中房间 | 409 + code 40902 |

## 11. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 结束时间早于开始时间 | 40001，前端有提示 |
| 2 | 跨天时段 | 40002 |
| 3 | 非 15 分钟对齐 | 40003 |
| 4 | 预约过去时间 | 40004 |
| 5 | 超过 30 天 | 40005 |

## 12. 当前状态

**已完成。** `mvn test`（含 `TimeValidatorTest`、`BookingIntegrationTest`）与 `npm run build` 已通过。

| 项 | 说明 |
|----|------|
| 后端接口 | GET `/rooms/available`；GET `/rooms/{id}/occupancy`；POST `/bookings` |
| 数据保存 | `booking` 表 INSERT |
| 失败层级 | 前端校验 / TimeValidator / 冲突检测 / 房间状态 / 数据库 |
| 前端路由 | `/book` 三步预约向导（`BookView.vue`） |

---

# 模块 4：我的预约

## 1. 模块目标

员工查看本人全部预约（含历史），取消未开始且状态为 CONFIRMED 的预约。[US-02][ADR-03]

## 2. 用户可见内容

- `/my-bookings` 列表：主题、会议室名、开始/结束时间、状态（未开始/进行中/已结束/已取消）。
- 未开始的预约有「取消」按钮；进行中/已结束无取消按钮。

## 3. 用户操作流程

1. 点击「我的预约」菜单。
2. 浏览列表（未开始按开始时间升序，已结束/已取消降序）。
3. 对未开始预约点「取消」→ 确认 → 列表更新为已取消，时段释放。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M4-F-01 | MyBookingsView 列表与分页 | `frontend/.../views/MyBookingsView.vue` | M4-F-02 | 展示至少一条预约 | 已完成 |
| M4-F-02 | booking API：listMine、cancel | `frontend/.../api/booking.ts` | M4-C-01,M4-C-02 | 取消后刷新列表 | 已完成 |
| M4-F-03 | 前端状态标签映射（未开始/进行中等） | `frontend/.../utils/bookingStatus.ts` | M4-F-01 | 时间与状态显示正确 | 已完成 |

## 5. Controller 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M4-C-01 | GET /api/v1/bookings/mine | `backend/.../booking/BookingController.java` | M4-S-01 | 分页 pageSize=20 | 已完成 |
| M4-C-02 | POST /api/v1/bookings/{id}/cancel | `backend/.../booking/BookingController.java` | M4-S-02 | 仅组织者且未开始 | 已完成 |

## 6. Service 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M4-S-01 | listMine 排序与展示状态推导 | `backend/.../booking/BookingQueryService.java` | M3-M-02 | 单测排序逻辑 | 已完成 |
| M4-S-02 | cancel 校验未开始 + 更新 CANCELLED | `backend/.../booking/BookingCancelService.java` | M3-R-01 | 已开始返回 40302 | 已完成 |

## 7. Mapper 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M4-M-01 | selectByOrganizerId 分页查询 | `backend/.../booking/mapper/BookingMapper.java` | M3-M-02 | 参数化 organizerId | 已完成 |
| M4-M-02 | updateStatusToCancelled | `backend/.../booking/mapper/BookingMapper.java` | M4-M-01 | 取消后 status=CANCELLED | 已完成 |

## 8. Repository / 数据保存任务

取消操作 UPDATE `booking.status`；列表为 SELECT。

## 9. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 先完成一次预约 | 我的预约有一条未开始 |
| 2 | 点击取消并确认 | 状态变已取消 |
| 3 | 再预约并开始时间设为过去（或等开始） | 无取消按钮或点击失败 |

## 10. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | GET `/bookings/mine?page=1` | 含本人记录 |
| 2 | POST `/bookings/{id}/cancel` 未开始 | 200 |
| 3 | 对已开始 id cancel | 403 + 40302 |

## 11. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 取消他人预约 id | 403 无权限 |
| 2 | 重复取消同一预约 | 合理错误提示 |

## 12. 当前状态

**已完成。** `mvn test`（含 `BookingDisplayStatusTest`、`BookingMineIntegrationTest`）与 `npm run build` 已通过。

| 项 | 说明 |
|----|------|
| 后端接口 | GET `/bookings/mine`；POST `/bookings/{id}/cancel` |
| 数据保存 | `booking` 表 UPDATE status |
| 失败层级 | 权限 / 状态校验 Service / Mapper |
| 前端路由 | `/my-bookings` 列表与取消 |

---

# 模块 5：站内通知

## 1. 模块目标

预约成功、用户取消、管理员修改/取消他人预约时，向相关用户写入并展示站内通知。[SPEC-9] P1

## 2. 用户可见内容

- `/notifications` 通知列表：标题、内容、时间、已读/未读。
- 顶栏未读角标（数字）。

## 3. 用户操作流程

1. 预约成功或预约被取消后，用户点击「通知」。
2. 查看列表，点击单条标为已读，或「全部已读」。
3. 角标数字减少。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M5-F-01 | NotificationsView 列表 | `frontend/.../views/NotificationsView.vue` | M5-F-02 | 展示通知 | 已完成 |
| M5-F-02 | notification API 四个接口封装 | `frontend/.../api/notification.ts` | M5-C-01 | 未读数、已读生效 | 已完成 |
| M5-F-03 | 顶栏未读角标组件 | `frontend/.../components/NotificationBadge.vue` | M5-F-02 | 预约成功后角标+1 | 已完成 |

## 5. Controller 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M5-C-01 | NotificationController 四个端点 | `backend/.../notification/NotificationController.java` | M5-S-02 | GET list/unread-count | 已完成 |

## 6. Service 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M5-S-01 | Notification 实体与 Mapper | `backend/.../notification/entity/Notification.java` | M1-D-01 | 插入可读 | 已完成 |
| M5-S-02 | NotificationQueryService 列表/未读/已读 | `backend/.../notification/NotificationService.java` | M5-S-01 | 标已读后不统计未读 | 已完成 |
| M5-S-03 | NotificationPublisher 预约成功/取消 | `backend/.../notification/NotificationPublisher.java` | M3-S-03,M4-S-02 | 创建预约后 notification 有行 | 已完成 |
| M5-S-04 | 管理员改约/取消时通知组织者 | `backend/.../notification/NotificationPublisher.java` | M8-S-02 | 管理员取消后组织者收到 | 已完成 |

## 7. Mapper 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M5-M-01 | NotificationMapper CRUD | `backend/.../notification/mapper/NotificationMapper.java` | M1-D-01 | 参数化 userId | 已完成 |

## 8. Repository / 数据保存任务

INSERT `notification`；UPDATE `read_flag`。

## 9. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 完成一次预约 | 通知页有一条「预约成功」类消息 |
| 2 | 取消该预约 | 有取消相关通知（若产品启用） |
| 3 | 标已读 | 未读角标减少 |

## 10. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | GET `/notifications` | 当前用户通知 |
| 2 | GET `/notifications/unread-count` | 数字正确 |
| 3 | POST `/notifications/{id}/read` | 该条已读 |

## 11. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 读他人通知 id | 403 或 404 |

## 12. 当前状态

**已完成。** `mvn test`（含 `NotificationIntegrationTest`）与 `npm run build` 已通过。M5-S-04 发布方法已就绪，待模块 8 管理员改约/取消时调用。

| 项 | 说明 |
|----|------|
| 后端接口 | GET/POST 见 plan §8.4 |
| 数据保存 | `notification` 表 |
| 失败层级 | 业务未触发发布 / 查询 Service |
| 前端路由 | `/notifications` + 顶栏未读角标 |

---

# 模块 6：管理员 — 用户管理

## 1. 模块目标

管理员创建、编辑用户，分配 EMPLOYEE/ADMIN 角色，重置密码。[US-05]

## 2. 用户可见内容

- `/admin/users`：用户表格（账号、显示名、角色、是否启用）。
- 「新建用户」对话框：仅需填写 username、role（初始密码默认 `123456`，显示名默认等于账号）。
- 行操作：编辑、重置密码（恢复默认密码 `123456`）、删除（不可删自己/末位管理员/有预约用户）。

## 3. 用户操作流程

1. 管理员登录，进入用户管理。
2. 新建用户 → 填必填项 → 保存 → 列表出现新用户。
3. 将新账号交给员工，员工可在登录页登录（模块 2）。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M6-F-01 | AdminUsersView 表格与分页 | `frontend/.../views/admin/AdminUsersView.vue` | M6-F-02 | 仅 ADMIN 可访问 | 已完成 |
| M6-F-02 | admin user API | `frontend/.../api/adminUser.ts` | M6-C-01 | CRUD 调通 | 已完成 |
| M6-F-03 | 新建/编辑用户对话框 | `frontend/.../components/admin/UserFormDialog.vue` | M6-F-01 | 必填校验 | 已完成 |
| M6-F-04 | 路由与菜单 ADMIN 权限 | `frontend/.../router/index.ts` | M2-F-04 | 员工访问 /admin/users 被拒绝 | 已完成 |

## 5. Controller 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M6-C-01 | AdminUserController CRUD+reset | `backend/.../user/AdminUserController.java` | M6-S-01 | @PreAuthorize ADMIN | 已完成 |

## 6. Service 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M6-S-01 | AdminUserService 创建加密密码 | `backend/.../user/AdminUserService.java` | M2-M-01 | 新用户可登录 | 已完成 |

## 7. Mapper 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M6-M-01 | SysUserMapper insert/update | `backend/.../user/mapper/SysUserMapper.java` | M2-M-01 | 参数化写入 | 已完成 |

## 8. Repository / 数据保存任务

INSERT/UPDATE `sys_user`（password_hash BCrypt）。

## 9. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | admin 登录进用户管理 | 看到用户列表 |
| 2 | 新建员工账号（仅填账号与角色） | 列表多一行，默认密码 123456 |
| 3 | 用新账号登录（123456） | 成功进入员工界面 |
| 4 | 重置密码 | 确认后可用 123456 登录 |
| 5 | 删除无预约的新建用户 | 列表中消失 |

## 10. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 员工 token 调 POST `/admin/users` | 403 |
| 2 | admin POST 创建 | 200 |
| 3 | PUT 改 displayName | 200 |

## 11. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 重复 username 创建 | 明确错误提示 |

## 12. 当前状态

**已完成。** `mvn test`（含 `AdminUserIntegrationTest`）与 `npm run build` 已通过。

| 项 | 说明 |
|----|------|
| 后端接口 | GET/POST/PUT/DELETE `/admin/users`；POST reset-password |
| 数据保存 | `sys_user` |
| 失败层级 | 权限 / 唯一约束 / Service 校验 |
| 前端路由 | `/admin/users`（仅 ADMIN） |

**待确认：** spec §14 是否扩展用户字段（工号、部门等）；当前 plan ADR-04 管理员仅需填账号与角色，密码/显示名由系统默认。

---

# 模块 7：管理员 — 会议室管理

## 1. 模块目标

管理员维护会议室信息与维护状态；维护中禁止新预约；有未来预约禁止删除。[US-03][ADR-01][ADR-02]

## 2. 用户可见内容

- `/admin/rooms`：会议室列表（名称、容量、楼层、类型、设备、状态）。
- 新增/编辑表单；「设为维护中」「恢复正常」；删除按钮。

## 3. 用户操作流程

1. 进入会议室管理 → 新增会议室 → 保存。
2. 编辑某会议室信息 → 保存。
3. 将会议室设为维护中 → 员工预约流程中不可选。
4. 删除无未来预约的房间 → 成功；有未来预约 → 提示先处理。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M7-F-01 | AdminRoomsView 列表 | `frontend/.../views/admin/AdminRoomsView.vue` | M7-F-02 | 展示房间 | 已完成 |
| M7-F-02 | admin room API | `frontend/.../api/adminRoom.ts` | M7-C-01 | CRUD+status | 已完成 |
| M7-F-03 | 会议室表单对话框 | `frontend/.../components/admin/RoomFormDialog.vue` | M7-F-01 | 必填 name | 已完成 |

## 5. Controller 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M7-C-01 | AdminRoomController + RoomController GET /rooms | `backend/.../room/AdminRoomController.java` | M7-S-01 | DELETE 有预约返回 40903 | 已完成 |

## 6. Service 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M7-S-01 | MeetingRoomAdminService CRUD+status+delete | `backend/.../room/MeetingRoomAdminService.java` | M3-M-01 | 维护中房间 excluded from available | 已完成 |

## 7. Mapper 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M7-M-01 | MeetingRoomMapper 管理端 CRUD | `backend/.../room/mapper/MeetingRoomMapper.java` | M3-M-01 | 逻辑删除 deleted 标志 | 已完成 |
| M7-M-02 | countFutureBookingsByRoomId | `backend/.../booking/mapper/BookingMapper.java` | M3-M-02 | 删除前计数 | 已完成 |

## 8. Repository / 数据保存任务

INSERT/UPDATE `meeting_room`；status PATCH；删除为逻辑删除或物理删除（与 plan 一致，待实现时统一）。

## 9. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 新增「测试会议室」 | 列表可见 |
| 2 | 设为维护中 | 员工 `/book` 选时段后列表无此室 |
| 3 | 对有未来预约的房间点删除 | 失败并提示 |

## 10. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | POST `/admin/rooms` | 201 |
| 2 | PATCH status MAINTENANCE | 200 |
| 3 | DELETE 有未来预约 | 40903 |

## 11. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 员工调用 POST `/admin/rooms` | 403 |

## 12. 当前状态

**已完成。** `AdminRoomIntegrationTest` 与 `npm run build` 已通过。

| 项 | 说明 |
|----|------|
| 后端接口 | GET/POST/PUT/PATCH/DELETE `/admin/rooms`；GET `/rooms`（ADMIN） |
| 数据保存 | `meeting_room` 逻辑删除 |
| 失败层级 | 权限 / 未来预约 40903 / 校验 |

---

# 模块 8：管理员 — 预约管理

## 1. 模块目标

管理员查看全公司预约，修改时间或会议室，取消任意预约；组织者收到通知。[US-04]

## 2. 用户可见内容

- `/admin/bookings`：全公司预约表格（主题、房间、组织者、时间、状态）。
- 编辑：可改开始/结束/房间（员工端无此能力）。
- 取消：对任意 CONFIRMED 未开始（或按 spec 管理员可取消范围）预约执行取消。

## 3. 用户操作流程

1. 进入预约管理，筛选/浏览列表。
2. 点击编辑 → 改时间或换房 → 保存 → 冲突则失败提示。
3. 点击取消 → 确认 → 原组织者在通知中心收到消息（模块 5）。

## 4. 前端页面任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M8-F-01 | AdminBookingsView 列表 | `frontend/.../views/admin/AdminBookingsView.vue` | M8-F-02 | 显示全公司数据 | 已完成 |
| M8-F-02 | admin booking API | `frontend/.../api/adminBooking.ts` | M8-C-01 | list/update/cancel | 已完成 |
| M8-F-03 | 编辑预约对话框（时间+房间） | `frontend/.../components/admin/BookingEditDialog.vue` | M8-F-01 | 冲突显示错误 | 已完成 |

## 5. Controller 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M8-C-01 | GET/PUT/POST cancel admin bookings | `backend/.../booking/AdminBookingController.java` | M8-S-01,M8-S-02 | ADMIN 权限 | 已完成 |

## 6. Service 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M8-S-01 | AdminBookingQueryService 分页列表 | `backend/.../booking/AdminBookingQueryService.java` | M3-M-02 | 含组织者显示名 | 已完成 |
| M8-S-02 | AdminBookingUpdateService 改时/房+冲突检测 | `backend/.../booking/AdminBookingUpdateService.java` | M3-S-01,M3-S-02 | 重叠失败 40901 | 已完成 |
| M8-S-03 | AdminBookingCancelService | `backend/.../booking/AdminBookingCancelService.java` | M4-S-02 | 取消后通知组织者 | 已完成 |

## 7. Mapper 任务

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| M8-M-01 | selectAllBookings 分页 | `backend/.../booking/mapper/BookingMapper.java` | M3-M-02 | 参数化分页 | 已完成 |
| M8-M-02 | updateBookingTimeAndRoom | `backend/.../booking/mapper/BookingMapper.java` | M8-M-01 | UPDATE 参数化 | 已完成 |

## 8. Repository / 数据保存任务

UPDATE `booking` 时间/房间；取消 UPDATE status；触发 `notification`（模块 5）。

## 9. 页面测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 员工创建一条预约 | 管理员列表可见 |
| 2 | 管理员修改时间到不冲突时段 | 保存成功，员工我的预约时间已变 |
| 3 | 管理员取消该预约 | 员工收到通知，预约已取消 |

## 10. 接口测试方法

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | GET `/admin/bookings` | 200 列表 |
| 2 | PUT 修改为冲突时段 | 40901 |
| 3 | POST `/admin/bookings/{id}/cancel` | 200 |

## 11. 异常情况测试

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 员工 token 调 admin 接口 | 403 |
| 2 | 修改为维护中房间 | 40902 |

## 12. 当前状态

**已完成。** `AdminBookingIntegrationTest` 与 `npm run build` 已通过。

| 项 | 说明 |
|----|------|
| 后端接口 | GET/PUT `/admin/bookings`；POST cancel |
| 数据保存 | `booking` UPDATE；`notification` INSERT |
| 失败层级 | 权限 / 冲突 / 房间状态 / 通知发布 |

---

# 横切任务（可选，放在各模块完成后）

| ID | 任务 | 主文件 | 前置依赖 | 验收方式 | 状态 |
|----|------|--------|----------|----------|------|
| X-01 | 后端集成测试 TC-01～TC-15 节选 | `backend/.../test/BookingIntegrationTest.java` | M3,M4,M7,M8 | `mvn test` 通过 | 未开始 |
| X-02 | 前端 E2E 主流程（登录→预约→取消） | `frontend/.../e2e/booking.spec.ts` | 全部 P0 | 一条命令跑通 | 未开始 |
| X-03 | 更新 README 端口与默认密码 | `README.md` | M1-B-05 | 新人可按文档启动 | 未开始 |

---

# Bug 列表

| ID | 描述 | 模块 | 状态 |
|----|------|------|------|
| — | 暂无 | — | — |

---

# 修订记录

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-05-20 | 初版，依据 spec/plan/AGENTS 按功能模块拆分 |
