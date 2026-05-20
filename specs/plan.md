# 企业级会议室预约系统 — 技术实现方案（plan）

| 文档版本 | 1.0 |
|----------|-----|
| 依据文档 | `specs/spec.md` v1.0、`AGENTS.md` |
| 目标读者 | 开发、测试、任务拆分、代码生成 Agent |
| 状态 | 可执行（含 spec §14 缺省决策） |

---

## 1. 文档目的与使用方式

本文档将 `spec.md` 中的产品需求映射为**可落地的技术方案**，用于：

- 任务拆分（见 §12，任务 ID 可直链 spec 章节/US）
- REST 接口设计与联调（§8）
- 页面与路由设计（§9）
- 测试用例设计（§13）
- 后端/前端实现与代码生成（§7、§10、§11）

**追踪约定**：文中 `[SPEC-x.y]` 表示 `spec.md` 对应章节；`[US-xx]` 表示用户故事。

---

## 2. Spec 追踪矩阵（需求 → 实现落点）

| Spec 条目 | 产品要点 | 本方案落点 |
|-----------|----------|------------|
| §2.2 目标 | 利用率、可见性 | 空闲会议室查询 API、占用片段展示 §8.3 |
| §3.1 MVP | 单站点、Web、无审批 | 单体应用、无 workflow 引擎 §5 |
| §4 角色 | 员工、管理员 | `Role` 枚举、RBAC 拦截 §8.1 |
| §5 会议室 | 属性、维护状态 | `meeting_room` 表、状态机 §6 |
| §6 预约规则 | 15min、30天、冲突、并发 | `BookingValidator` + 事务 §7 |
| §7 可见性 | 他人可见主题+组织者 | 占用查询 DTO §8.3 |
| §8 功能清单 | P0/P1 | 任务 T-001～T-020 §12 |
| §9 通知 | 站内 P1 | `notification` 表 §8.5 |
| §10 US-01～05 | 验收标准 | 测试用例 TC-xxx §13 |
| §11 边缘场景 | 各类禁止/失败 | 错误码 §8.6、校验 §7 |
| §12 NFR | 并发不重 | 事务 + 重叠检测 §7.3 |
| §14 待确认 | 4 项 | **ADR-01～04** §3 |

---

## 3. 架构缺省决策（闭合 spec §14）

### ADR-01：会议室进入「维护中」时已有未来预约

- **策略**：已有未来预约继续有效；仅禁止新预约。
- **实现**：`status=MAINTENANCE` 时 `GET /rooms/available` 排除该室。

### ADR-02：删除仍有未来预约的会议室

- **策略**：禁止删除，返回未来预约数量提示。

### ADR-03：「我的预约」列表

- **展示**：含已结束历史。
- **排序**：未开始按开始时间升序；已结束/已取消按开始时间降序。
- **分页**：pageSize=20。

### ADR-04：管理员创建用户最小字段

| 字段 | 管理员填写 | 默认值 / 说明 |
|------|------------|----------------|
| username | 是 | 登录账号 |
| password | 否 | 固定默认 `123456`，用户登录后自行修改（一期暂无自助改密页） |
| displayName | 否 | 默认等于 `username`，用户自行修改（一期暂无自助改资料页） |
| role | 是 | EMPLOYEE / ADMIN |

**重置密码**：管理员一键恢复为默认密码 `123456`，无需输入新密码。

---

## 4. 技术栈与约束

| 层级 | 选型 |
|------|------|
| 运行时 | Java 8（`AGENTS.md`） |
| 后端 | Spring Boot 2.7.x |
| 数据库 | MySQL 8 + Flyway |
| ORM | MyBatis-Plus |
| 前端 | Vue 3 + Vite + Element Plus |
| 认证 | JWT + BCrypt |
| API | REST JSON，`/api/v1` |

**AGENTS.md**：所有类/接口 JavaDoc；禁止嵌套循环；用 Stream/SQL 批量处理。

---

## 5. 系统架构

单体 Spring Boot + Vue SPA + MySQL。

**后端包**：`auth`、`user`、`room`、`booking`、`notification`、`common`、`config`。

**前端路由**：

| 路由 | 页面 | 角色 |
|------|------|------|
| `/login` | 登录 | 公开 |
| `/book` | 预约向导 | 登录用户 |
| `/my-bookings` | 我的预约 | 登录用户 |
| `/notifications` | 通知 | 登录用户 |
| `/admin/users` | 用户管理 | ADMIN |
| `/admin/rooms` | 会议室管理 | ADMIN |
| `/admin/bookings` | 预约管理 | ADMIN |

---

## 6. 领域模型

### 6.1 实体

- **sys_user**：username, password_hash, display_name, role, enabled
- **meeting_room**：name, capacity, floor, room_type, equipment, status(NORMAL/MAINTENANCE), deleted
- **booking**：room_id, organizer_id, title, start_time, end_time, status(CONFIRMED/CANCELLED)
- **notification**：user_id, type, title, content, related_booking_id, read_flag

### 6.2 预约状态（查询推导）

- DB 存：`CONFIRMED`、`CANCELLED`
- 展示推导：`未开始` / `进行中` / `已结束`（按当前时间与 start/end 比较）
- **仅未开始且 CONFIRMED 可取消** [SPEC-11]

---

## 7. 核心业务逻辑

### 7.1 时间校验

| ID | 规则 | 错误码 |
|----|------|--------|
| TR-01 | start < end | INVALID_TIME_RANGE |
| TR-02 | 同一天 | CROSS_DAY_NOT_ALLOWED |
| TR-03 | 15 分钟对齐 | TIME_SLOT_NOT_ALIGNED |
| TR-04 | start >= now | PAST_TIME_NOT_ALLOWED |
| TR-05 | 最多提前 30 天 | EXCEED_ADVANCE_LIMIT |

### 7.2 冲突判定

```text
overlap: b.start_time < newEnd AND b.end_time > newStart
status = CONFIRMED
```

相邻时段（09:00-09:15 与 09:15-09:30）**不冲突**。

### 7.3 并发

`@Transactional` + 插入前 overlap 查询；竞态时二次查询，失败返回 `ROOM_SLOT_OCCUPIED` (40901)。

### 7.4 员工不可改预约

员工端无 PATCH；仅取消后重新预约。

---

## 8. API 设计

**Base**：`/api/v1`  
**认证**：`Authorization: Bearer <token>`

### 8.1 认证与用户

| 方法 | 路径 | 角色 |
|------|------|------|
| POST | `/auth/login` | 公开 |
| GET | `/auth/me` | 登录 |
| GET | `/admin/users` | ADMIN |
| POST | `/admin/users` | ADMIN |
| PUT | `/admin/users/{id}` | ADMIN |
| POST | `/admin/users/{id}/reset-password` | ADMIN |

### 8.2 会议室

| 方法 | 路径 | 角色 |
|------|------|------|
| GET | `/rooms/available?date&startTime&endTime` | 登录 |
| GET | `/rooms/{id}/occupancy?date` | 登录 |
| GET | `/rooms` | ADMIN |
| POST | `/admin/rooms` | ADMIN |
| PUT | `/admin/rooms/{id}` | ADMIN |
| PATCH | `/admin/rooms/{id}/status` | ADMIN |
| DELETE | `/admin/rooms/{id}` | ADMIN |

### 8.3 预约

| 方法 | 路径 | 角色 |
|------|------|------|
| POST | `/bookings` | 登录 |
| GET | `/bookings/mine` | 登录 |
| POST | `/bookings/{id}/cancel` | 组织者 |
| GET | `/admin/bookings` | ADMIN |
| PUT | `/admin/bookings/{id}` | ADMIN |
| POST | `/admin/bookings/{id}/cancel` | ADMIN |

**创建体**：`{ roomId, title, startTime, endTime }`

### 8.4 通知

| 方法 | 路径 |
|------|------|
| GET | `/notifications` |
| GET | `/notifications/unread-count` |
| POST | `/notifications/{id}/read` |
| POST | `/notifications/read-all` |

### 8.5 错误码

| code | 说明 |
|------|------|
| 40001 | 时间范围无效 |
| 40002 | 不允许跨天 |
| 40003 | 非 15 分钟粒度 |
| 40004 | 过去时间 |
| 40005 | 超过 30 天 |
| 40901 | 时段已被预约 |
| 40902 | 会议室维护中 |
| 40903 | 有未来预约不可删房间 |
| 40302 | 已开始/结束不可取消 |

---

## 9. 前端页面要点

- **`/book`**：Step1 日期+时段 → Step2 空闲房间列表 → Step3 主题+确认
- **`/my-bookings`**：列表+取消（无编辑）
- **管理端**：用户/会议室/全公司预约 CRUD

---

## 10. 数据库（Flyway V1）

表：`sys_user`、`meeting_room`、`booking`、`notification`  
索引：`booking(room_id, start_time, end_time)`、`booking(organizer_id, start_time)`

种子：管理员 `admin` + 示例会议室。

---

## 11. 代码规范（AGENTS.md）

- 每个类/接口完整 JavaDoc（含 `@author liuxinsi`、`@date`）
- 禁止嵌套 for；空闲房间用 `NOT EXISTS` 单 SQL

---

## 12. 任务拆分

| ID | 标题 | Spec |
|----|------|------|
| T-001 | 脚手架 + 统一响应 | §4 |
| T-002 | DDL + 种子 | §10 |
| T-003 | JWT + Security | §4.2 |
| T-004 | 用户管理 API | US-05 |
| T-005 | 会议室 CRUD | US-03 |
| T-006 | TimeValidator 单测 | §6.2 |
| T-007 | 冲突检测 | §6.3 |
| T-008 | 空闲房间 API | US-01 |
| T-009 | 占用片段 API | §7.1 |
| T-010 | 创建预约 + 并发测试 | US-01 |
| T-011 | 我的预约 + 取消 | US-02 |
| T-012 | 管理员预约管理 | US-04 |
| T-013 | 通知 API | §9 |
| T-014～T-018 | 前端各页 | §8 |
| T-019 | 集成测试 | §12 |
| T-020 | docker-compose | - |

---

## 13. 测试用例（节选）

| ID | 场景 |
|----|------|
| TC-01 | 合法预约成功 |
| TC-06 | 重叠 → 40901 |
| TC-07 | 相邻 15min 成功 |
| TC-08 | 并发仅 1 成功 |
| TC-11 | 取消未开始 |
| TC-12 | 取消已开始 → 40302 |
| TC-15 | 删房间有预约 → 40903 |

---

## 14. 仓库目录

```
Meeting/
├── AGENTS.md
├── specs/
│   ├── spec.md
│   └── plan.md    ← 本文件
├── backend/meeting-booking-api/
├── frontend/meeting-booking-web/
└── docker-compose.yml
```

---

## 15. 修订记录

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-05-20 | 初版 |
