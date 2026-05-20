# meeting-booking-web

会议室预约系统前端（Vue 3 + Vite + Element Plus）。

## 本地开发

```bash
npm install
npm run dev
```

浏览器访问：`http://localhost:5173`

- `/` 自动跳转到 `/login`
- `/login` 脚手架占位页（会尝试调用后端 `/health`）

## 环境变量

| 文件 | 变量 | 默认值 |
|------|------|--------|
| `.env.development` | `VITE_API_BASE_URL` | `http://localhost:8080/api/v1` |

## 常用命令

```bash
npm run dev      # 开发服务器
npm run build    # 生产构建
npm run preview  # 预览构建结果
```
