# iteration-ui-theme — 变更记录

按时间倒序记录；每次发布或合并后更新「关联提交」。

---

## 2026-05-20 — 全站大地色主题（初版）

### 动机

一期界面沿用 Element Plus 默认蓝色（顶栏 `#409eff`、登录页蓝灰渐变、未读通知蓝底等），与「企业会议室 · 简约稳重」气质不符。在**不改变业务行为**的前提下统一为大地色系视觉。

### 变更摘要

- 新增全局主题 `frontend/meeting-booking-web/src/styles/theme.css`，覆盖 Element Plus 主色、卡片、按钮、表单、步骤条、表格、标签、对话框、徽章等。
- 扩展 `src/style.css`：Inter 字体、全站 `.page-title` / `.page-header` / `.page-shell` 工具类。
- 顶栏改为深褐背景，导航链接增加悬停/激活态（无下划线蓝链）。
- 登录/注册页去除蓝紫渐变，统一米灰背景与暖褐链接色。
- 预约流程：会议室卡片选中边框、悬停阴影；步骤区分割线改用主题边框色。
- 通知列表：未读背景改为主色浅档（`--el-color-primary-light-9`），非 `#ecf5ff`。
- 管理端与用户端列表页：移除 scoped 内 `#303133` 等硬编码，依赖全局标题样式。

### 未改动

- 所有 `src/api/**`、Pinia store 业务逻辑、`router/**`、`router/guards.ts`
- 后端 `backend/**`
- 各页面 `template` 结构、`@click` / API 调用

### 涉及文件

| 路径 | 变更类型 |
|------|----------|
| `frontend/meeting-booking-web/src/styles/theme.css` | 新增 |
| `frontend/meeting-booking-web/src/style.css` | 修改 |
| `frontend/meeting-booking-web/src/main.ts` | 修改（引入 theme） |
| `frontend/meeting-booking-web/index.html` | 修改（标题、字体） |
| `frontend/meeting-booking-web/src/layouts/MainLayout.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/components/NotificationBadge.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/LoginView.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/RegisterView.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/BookView.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/book/AvailableRoomsStep.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/MyBookingsView.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/NotificationsView.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/admin/AdminUsersView.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/admin/AdminRoomsView.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/admin/AdminBookingsView.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/LoginPlaceholder.vue` | 修改（样式） |
| `frontend/meeting-booking-web/src/views/HomePlaceholder.vue` | 修改（样式） |

### 验证

```bash
cd frontend/meeting-booking-web
npm run build
```

- 构建结果：**通过**（2026-05-20 本地执行）
- 人工建议：登录页、顶栏导航、预约三步、通知未读态、三个管理端列表各扫一眼

### 关联提交

> 待代码与本文档一并提交后，在此填写 Git commit hash，例如：`abc1234` — `style(web): earth-tone theme (iteration-ui-theme)`

| 类型 | Commit | 说明 |
|------|--------|------|
| 代码 + 文档 | _待补_ | 建议 message：`style(web): earth-tone theme; docs(iteration-ui-theme)` |

---

## 后续变更模板

```markdown
## YYYY-MM-DD — 简短标题

### 动机
…

### 变更摘要
…

### 涉及文件
…

### 验证
…

### 关联提交
`hash` — message
```
