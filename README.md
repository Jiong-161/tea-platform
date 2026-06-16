
<p align="center">
  <h1 align="center">🍵 茶文 · Tea Platform</h1>
  <p align="center"><strong>茶文化品鉴购销一站式服务平台</strong></p>
  <p align="center">
    <img src="https://img.shields.io/badge/JDK-17-orange?logo=openjdk" alt="JDK 17">
    <img src="https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen?logo=springboot" alt="Spring Boot 3.3.4">
    <img src="https://img.shields.io/badge/Spring_Cloud-2023.0.3-blue?logo=spring" alt="Spring Cloud 2023.0.3">
    <img src="https://img.shields.io/badge/Vue-3.5-4FC08D?logo=vuedotjs" alt="Vue 3.5">
    <img src="https://img.shields.io/badge/Vite-8.x-646CFF?logo=vite" alt="Vite 8">
    <img src="https://img.shields.io/badge/License-Apache_2.0-blue" alt="Apache 2.0">
  </p>
</p>

---

## 📖 项目简介

**茶文** 是一个聚焦茶文化领域的垂直综合型 Web 平台，集**茶文化内容传播**、**茶事展览活动**、**茶叶电商交易**三大核心能力于一体。平台采用前后端分离架构，后端基于 Spring Cloud Alibaba 微服务生态，前端包含面向消费者的 C 端门户和面向管理员的后台管理系统。

### 核心业务模块

| 模块 | 说明 |
|------|------|
| 👤 **用户系统** | 注册/登录（JWT 无状态认证）、个人信息管理、收货地址管理、角色权限控制 |
| 📝 **内容系统** | 茶文化文章发布与管理、分类体系、富文本编辑、封面图上传、浏览计数 |
| 🎪 **展览系统** | 茶博会/展览活动管理、在线报名、展览状态管控 |
| 🛒 **电商系统** | 茶叶商品管理、购物车、订单创建与查询 |
| 🛡️ **网关系统** | 统一入口、路由转发、跨域处理、Sentinel 流量控制与熔断降级 |

---

## 🛠️ 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| JDK | 17 | 运行环境 |
| Spring Boot | 3.3.4 | 应用框架 |
| Spring Cloud | 2023.0.3 | 微服务治理 |
| Spring Cloud Alibaba | 2023.0.3.2 | 服务注册/配置/流控 |
| Spring Cloud Gateway | — | API 网关 |
| Nacos | 2.x | 服务注册与发现 |
| Sentinel | 1.8.9 | 流量控制 & 熔断降级 |
| MyBatis-Plus | 3.5.7 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存 & Token 存储 |
| MinIO | 2024+ | 对象存储（图片/文件） |
| JJWT | 0.11.5 | JWT 令牌签发与校验 |
| BCrypt | — | 密码加密 |
| Maven | 3.8+ | 构建管理 |

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5 | UI 框架 |
| Vite | 8.x | 构建工具 |
| Element Plus | 2.14 | UI 组件库 |
| Pinia | 3.x | 状态管理 |
| Vue Router | 5.x | 路由管理 |
| Axios | 1.x | HTTP 客户端 |
| Sass | 1.x | CSS 预处理 |

---

## 📂 项目结构

```
tea-platform/
├── backend/tea-backend/          # 后端微服务根目录
│   ├── gateway-service/          # API 网关（端口 10010）
│   ├── user-service/             # 用户服务
│   ├── content-service/          # 内容服务（文章/资讯）
│   ├── exhibition-service/       # 展览服务
│   ├── product-service/          # 商品/订单服务
│   ├── tea-common/               # 公共模块（JWT/Hutool/工具类）
│   ├── sql/                      # 数据库初始化脚本
│   └── pom.xml                   # Maven 父 POM（统一版本管理）
├── frontend/
│   ├── client-web/               # C 端用户前台（端口 5173）
│   └── admin-web/                # 后台管理系统（端口 5174）
├── docs/                         # 项目文档
│   ├── 开发操作文档.md             # 开发环境搭建 & 规范
│   ├── 茶文平台-DDL建表SQL.sql     # 完整建表脚本（MySQL 8.0+）
│   ├── Sentinel_1.8.9_集成方案.md  # Sentinel 集成指南
│   └── 方案B.md                   # 技术方案文档
├── logs/                         # 日志目录
├── LICENSE                       # Apache-2.0
└── README.md                     # 项目说明
```

### 微服务端口规划

| 服务 | 端口 | 说明 |
|------|------|------|
| `gateway-service` | 10010 | 统一入口网关 |
| `user-service` | — | 用户 & 地址管理（Nacos 动态分配） |
| `content-service` | — | 文章 & 上传 |
| `exhibition-service` | — | 展览 & 报名 |
| `product-service` | — | 商品 & 购物车 & 订单 |

---

## 🚀 快速开始

### 环境要求

| 软件 | 最低版本 | 推荐版本 |
|------|---------|---------|
| JDK | 17 | 17.0.x |
| Maven | 3.8+ | 3.9.x |
| Node.js | 18+ | 20.x LTS |
| npm | 9+ | 10.x |
| MySQL | 8.0 | 8.0.x |
| Redis | 6.0 | 7.x |
| Nacos | 2.2 | 2.3.x |
| MinIO | 2024-01 | 最新稳定版 |

> ⚠️ 推荐开发机内存 **16 GB** 以上（需同时运行 MySQL、Redis、Nacos、MinIO 及多个微服务进程）。

### 1. 克隆项目

```bash
git clone <repository-url>
cd tea-platform
```

### 2. 初始化数据库

```bash
# 使用 MySQL 客户端执行建表脚本
mysql -u root -p < docs/茶文平台-DDL建表SQL.sql
```

### 3. 启动基础设施

确保以下服务已启动并可访问：

- **Nacos**：`http://127.0.0.1:8848/nacos`（默认账号 `nacos/nacos`）
- **Redis**：`localhost:6379`
- **MinIO**：控制台 `http://127.0.0.1:9001`，API `http://127.0.0.1:9000`
- **Sentinel Dashboard**：`http://localhost:8089`（默认账号 `sentinel/sentinel`）

### 4. 配置本地环境

项目已提供 `backend/tea-backend/application-local.yml` 模板，包含 MySQL、Redis、Nacos、MinIO 的连接配置。使用方式：

```bash
# 复制到每个子模块的 resources 目录（推荐）
cp backend/tea-backend/application-local.yml backend/tea-backend/user-service/src/main/resources/
cp backend/tea-backend/application-local.yml backend/tea-backend/content-service/src/main/resources/
cp backend/tea-backend/application-local.yml backend/tea-backend/exhibition-service/src/main/resources/
cp backend/tea-backend/application-local.yml backend/tea-backend/product-service/src/main/resources/
```

### 5. 启动后端服务

```bash
cd backend/tea-backend

# 编译全部模块
mvn clean install -DskipTests

# 按顺序启动（每个服务在新终端窗口）：
# ① 网关（必须最先启动）
cd gateway-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

# ② 业务服务（可并行启动）
cd ../user-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

cd ../content-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

cd ../exhibition-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

cd ../product-service
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

### 6. 启动前端应用

```bash
# 用户端（C 端前台 → http://localhost:5173）
cd frontend/client-web
npm install
npm run dev

# 管理后台（→ http://localhost:5174）
cd frontend/admin-web
npm install
npm run dev
```

> 前端已配置 Vite 代理，开发模式下 API 请求自动转发至 `localhost:10010` 网关。

---

## 🔌 API 概览

所有请求统一经过网关 `http://localhost:10010`，按路径前缀路由至对应微服务。

### 用户端接口

| 路径前缀 | 服务 | 典型功能 |
|----------|------|---------|
| `/user/**` | user-service | 注册、登录、个人信息、地址管理 |
| `/content/**` | content-service | 文章列表、文章详情、分类查询、文件上传 |
| `/exhibition/**` | exhibition-service | 展览列表、展览详情、在线报名 |
| `/product/**` | product-service | 商品列表、商品详情、购物车、下单 |
| `/home/**` | gateway-service | 首页聚合数据 |

### 管理端接口

| 路径前缀 | 服务 | 典型功能 |
|----------|------|---------|
| `/admin/user/**` | user-service | 用户管理、角色变更、账号启停 |
| `/admin/content/**` | content-service | 文章发布、审核、分类管理 |
| `/admin/exhibition/**` | exhibition-service | 展览发布、报名管理 |
| `/admin/product/**` | product-service | 商品发布、订单管理 |

> 📘 服务启动后可通过 Swagger/Knife4j 查看完整 API 文档（开发环境自动启用）。

---

## 🏗️ 构建部署

### 前端构建

```bash
# 用户端
cd frontend/client-web
npm run build        # 产物输出至 dist/

# 管理后台
cd frontend/admin-web
npm run build        # 产物输出至 dist/
```

### 后端构建

```bash
cd backend/tea-backend
mvn clean package -DskipTests
# 各模块 JAR 包位于各自 target/ 目录下
```

### 生产部署

1. 将前端 `dist/` 部署至 Nginx，配置反向代理至网关 `10010` 端口
2. 后端 JAR 通过 `java -jar` 启动，指定生产环境配置
3. 确保 Nacos、Sentinel、Redis、MinIO 等基础设施使用生产环境地址
4. 建议通过 systemd 或 Docker 进行进程守护

---

## 📚 文档索引

| 文档 | 说明 |
|------|------|
| [开发操作文档](docs/开发操作文档.md) | 环境搭建、编码规范、Git 流程、调试技巧 |
| [DDL 建表 SQL](docs/茶文平台-DDL建表SQL.sql) | 完整数据库建表脚本（可直接执行） |
| [Sentinel 集成方案](docs/Sentinel_1.8.9_集成方案.md) | Sentinel 1.8.9 流控/熔断接入指南 |
| [方案 B](docs/方案B.md) | 技术方案文档 |

---

## 🤝 贡献指南

1. Fork 本仓库
2. 从 `master` 分支创建功能分支：`git checkout -b feature/your-feature`
3. 提交变更：`git commit -m "feat: 添加xxx功能"`
4. 推送分支：`git push origin feature/your-feature`
5. 创建 Pull Request 至 `master` 分支

### Commit 规范

推荐使用 [Conventional Commits](https://www.conventionalcommits.org/) 格式：

- `feat:` — 新功能
- `fix:` — 缺陷修复
- `chore:` — 构建/工具/依赖变更
- `docs:` — 文档更新
- `refactor:` — 重构
- `style:` — 代码格式（不影响逻辑）

---

## 📄 许可证

本项目基于 [Apache License 2.0](LICENSE) 开源。

---

<p align="center">
  <sub>Made with ☕ by the Tea Platform Team</sub>
</p>