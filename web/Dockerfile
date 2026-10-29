# =====================================================================
# web/Dockerfile —— 计算器前端（静态页面 + 反向代理）镜像（Lab 7）
#
# 前端是纯静态页面，不需要 Node/编译步骤：直接用 nginx:alpine 作为
# 基础镜像，把页面文件和自定义 nginx 配置拷进去即可。
# =====================================================================
FROM nginx:1.27-alpine

# 用自定义配置替换默认站点配置：其中 /api/ 会反向代理到 backend:8000
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 拷贝静态资源（index.html / app.js / style.css）到 nginx 默认站点目录
COPY index.html app.js style.css /usr/share/nginx/html/

# 前端服务监听 80 端口，compose 中映射为宿主机 8080
EXPOSE 80
