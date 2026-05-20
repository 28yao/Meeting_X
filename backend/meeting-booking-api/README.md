# meeting-booking-api

企业级会议室预约系统后端（Spring Boot 2.7 + Java 8 + MyBatis-Plus + Flyway + MySQL）。

## 快速启动

### 1. 准备 MySQL

**方式 A（推荐）：** 项目根目录启动 Docker MySQL

```bash
docker compose up -d mysql
```

本机默认：`localhost:3306`，`root` / `123`，库名 `meeting_booking`（与 `application.yml` 一致）。

**方式 B：** 使用本机 MySQL，创建库 `meeting_booking`，并通过环境变量覆盖连接信息。

### 2. 启动后端

```bash
cd backend/meeting-booking-api
mvn spring-boot:run
```

### 3. 验证健康检查

浏览器或 curl 访问：

```
GET http://localhost:8080/api/v1/health
```

预期 JSON：

```json
{"code":0,"message":"success","data":{"status":"UP","timestamp":...}}
```

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| DB_HOST | localhost | 数据库主机 |
| DB_PORT | 3306 | 端口 |
| DB_NAME | meeting_booking | 库名 |
| DB_USERNAME | root | 用户名 |
| DB_PASSWORD | 123 | 密码 |

## 认证接口（模块 2）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 登录，body: `{username,password}` |
| GET | `/auth/me` | 当前用户（Header: `Authorization: Bearer <token>`） |

## 预约接口（模块 3，需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/rooms/available` | 查询空闲会议室。Query：`date`（yyyy-MM-dd）、`startTime`、`endTime`（HH:mm，15 分钟对齐） |
| GET | `/rooms/{id}/occupancy` | 某日占用片段。Query：`date` |
| POST | `/bookings` | 创建预约。Body：`{roomId,title,startTime,endTime}`，时间为 ISO `YYYY-MM-DDTHH:mm:ss` |

### 业务错误码（节选）

| code | 含义 |
|------|------|
| 40001 | 结束时间不晚于开始时间 |
| 40002 | 跨天预约 |
| 40003 | 时间未按 15 分钟对齐 |
| 40004 | 预约过去时间 |
| 40005 | 超过提前 30 天 |
| 40901 | 时段冲突 |
| 40902 | 会议室维护中 |

## 种子数据

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | ADMIN |

另含示例会议室「一号会议室」。

## 常用命令

```bash
mvn compile          # 编译
mvn test             # 单元/集成测试（需本机 MySQL meeting_booking）
mvn spring-boot:run  # 启动（需 MySQL）
```
