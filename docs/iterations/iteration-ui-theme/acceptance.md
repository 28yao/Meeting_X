# iteration-ui-theme — 验收自检清单

| 文档版本 | 1.0 |
|----------|------|
| 依据 | [`spec.md`](./spec.md) |
| 验收日期 | 2026-05-20 |

完成项将 `[ ]` 改为 `[x]`。

---

## 1. 构建与静态检查

- [x] `cd frontend/meeting-booking-web && npm run build` 无错误
- [x] 前端 `src` 下无 `#409eff`、`#e8f4ff`、`#ecf5ff`、`#d9ecff` 硬编码（`theme.css` 大地色变量除外）
- [x] 未修改 `src/api/**`、`src/router/**`、后端目录

---

## 2. 色彩与品牌

- [x] 顶栏背景为深褐系（`--app-primary-dark`），非 Element 蓝
- [x] 主按钮、链接、步骤条「进行中」为主色褐，非蓝
- [x] 登录/注册页背景为米灰 `var(--app-bg)`，无蓝紫渐变
- [x] 未读通知项背景为暖色浅底，非 `#ecf5ff`
- [x] 预约会议室卡片选中边框为 `var(--app-primary)`，非 `#409eff`

---

## 3. 布局与组件

- [x] 顶栏导航有悬停/激活反馈（背景高亮，非仅下划线）
- [x] 卡片圆角与浅阴影统一（`theme.css` 中 `.el-card`）
- [x] 表格表头为浅褐灰底，行悬停为 `--app-surface-muted`
- [x] 表单输入聚焦边框为主色，非默认蓝环
- [x] 分页当前页背景为主色

---

## 4. 交互与无障碍

- [x] 可点击区域（导航、房间卡片、通知项）具备 `cursor: pointer` 或等效样式
- [x] 过渡时长约 0.2s，无夸张动画
- [x] `theme.css` 含 `prefers-reduced-motion` 降级规则
- [x] 正文与背景对比度可接受（深褐字 on 米灰/白底）

---

## 5. 功能回归（行为不变）

- [ ] 登录、退出、路由守卫仍正常（需人工点验）
- [ ] 预约三步提交、取消预约、管理端 CRUD 仍正常（需人工点验）

> 注：§5 为运行时回归，文档记录时若未全量点验可保持未勾选；发布前建议补勾。

---

## 6. 文档追溯

- [x] `docs/iterations/iteration-ui-theme/spec.md` 已创建
- [x] `docs/iterations/iteration-ui-theme/changelog.md` 已创建
- [x] `PROJECT_STATUS.md` §4.1 已添加索引
- [x] `iteration-001/spec.md` §2.2 已注明不包含全站主题
- [ ] `changelog.md`「关联提交」已填写 commit hash（待 Git 提交后补）

---

## 签收

| 角色 | 姓名 | 日期 |
|------|------|------|
| 开发 | — | 2026-05-20 |
| 产品/验收 | — | _待填_ |
