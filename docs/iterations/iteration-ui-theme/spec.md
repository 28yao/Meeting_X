# 全站视觉主题 — 需求与边界说明

| 文档版本 | 1.0 |
|----------|------|
| 迭代代号 | `iteration-ui-theme` |
| 状态     | 已完成（已提交 `1d526de`） |
| 完成日期 | 2026-05-20 |
| 设计依据 | `ui-ux-pro-max` 技能（企业 SaaS · 简约 · 大地色系） |

---

## 1. 文档目的

记录一期 MVP 完成后的**横切视觉改造**，便于与 `iteration-001`（功能二期）区分追溯，避免功能验收与样式验收混在一起。

---

## 2. 范围

### 2.1 包含

| 类别 | 说明 |
|------|------|
| 全局主题 | `frontend/meeting-booking-web/src/styles/theme.css`：CSS 变量、Element Plus 主色与组件覆盖 |
| 全局排版 | `src/style.css`：字体、`.page-title`、`.page-shell` 等工具类 |
| 入口 | `main.ts` 引入主题；`index.html` 标题与字体 preconnect |
| 布局与页面样式 | `MainLayout.vue`、`NotificationBadge.vue` 及各业务 View 的 `<style scoped>` |
| 交互细节 | 导航悬停/激活、卡片选中态、未读通知背景、按钮/表单/表格视觉统一 |

### 2.2 明确不包含（禁止改动）

- 接口地址、请求参数、响应数据处理逻辑
- 路由配置、`router/guards.ts`、权限判断
- 后端代码、数据库表结构及字段
- Vue `template` 的 DOM 结构、`:key`、事件绑定与业务脚本逻辑
- 新功能需求（注册、改密、预约合并等见 `iteration-001`）

### 2.3 与 iteration-001 的关系

- **iteration-001**：功能迭代（自助注册、去测试账号提示、员工改密/改名、预约步骤合并等）。
- **iteration-ui-theme**：仅视觉；登录/注册页的**配色与排版**在本迭代完成，**是否显示测试账号、注册入口**仍按 `iteration-001` 验收。

`iteration-001` 的 `spec.md` §2.2 已引用本文档，避免范围重叠。

---

## 3. 视觉硬性规则

| 规则 | 说明 |
|------|------|
| 禁用色 | 不得使用蓝色、紫色及蓝紫混合渐变（含 Element 默认 `#409eff` 等） |
| 主色 | 暖褐 `#6b5d52`；顶栏深褐 `#4a4238`；点缀 `#b8956a` |
| 中性色 | 背景 `#f6f4f1`、表面 `#ffffff`、正文 `#2c2825` |
| 风格 | 简洁大气；柔和阴影；无花哨动效；过渡约 150–300ms |
| 字体 | Inter + 系统中文字体栈 |
| 无障碍 | 正文对比度 ≥ 4.5:1；支持 `prefers-reduced-motion` |

设计 token 实现位置：`frontend/meeting-booking-web/src/styles/theme.css`（`:root` 与 Element 变量映射）。

---

## 4. 影响页面（路由）

| 路由 | 页面 | 样式改动要点 |
|------|------|----------------|
| `/login` | 登录 | 米灰背景、卡片边框、链接色 |
| `/register` | 注册 | 同登录 |
| `/book` | 预约向导 | 步骤条主题色、房间卡片选中态 |
| `/my-bookings` | 我的预约 | 标题与表格沿用全局主题 |
| `/notifications` | 通知中心 | 未读项暖色浅底（非蓝） |
| `/admin/users` | 用户管理 | 页头与表格 |
| `/admin/rooms` | 会议室管理 | 同上 |
| `/admin/bookings` | 预约管理 | 同上 |
| 布局 | 顶栏 | 深褐背景、导航悬停/激活、退出按钮悬停 |

占位页（`LoginPlaceholder`、`HomePlaceholder`）仅同步背景与文字色，非生产主路径。

---

## 5. 验收标准（摘要）

完整勾选清单见同目录 [`acceptance.md`](./acceptance.md)。

- `npm run build` 通过
- 源码中无 `#409eff`、`#e8f4ff`、`#ecf5ff` 等一期蓝系硬编码
- 顶栏、主按钮、步骤条、分页激活态均为大地色系
- 未改 API / 路由 / 后端 / template 业务逻辑

---

## 6. 文档索引

| 文档 | 用途 |
|------|------|
| [`changelog.md`](./changelog.md) | 变更记录与文件清单、Git 提交关联 |
| [`acceptance.md`](./acceptance.md) | 验收自检清单 |
| `PROJECT_STATUS.md` §4.1 | 项目总览索引 |
| `docs/iterations/iteration-001/` | 功能二期（不含本主题细节） |
