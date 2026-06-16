# A2503-tea-culture-exhibition

## 基于 Vue + Spring Cloud 的茶文化品鉴购销服务系统设计与实现

**茶文化 + 展览互动 + 电商交易 + 内容运营综合型微服务平台**

---

## 📖 项目简介

本项目是一个聚焦茶文化领域的垂直综合型 Web 平台，集**茶文化内容传播**、**茶事展览活动**、**茶叶电商交易**三大核心业务于一体。系统采用前后端分离架构，后端基于 Spring Cloud Alibaba 微服务生态，前端包含面向消费者的 C 端门户和面向管理员的后台管理系统。

### 核心功能

| 模块 | 功能 |
|------|------|
| 👤 用户系统 | 注册/登录（JWT 无状态认证）、个人信息管理、收货地址管理、管理员角色权限 |
| 📝 内容系统 | 茶文化文章发布与管理、分类体系、富文本编辑、浏览量统计、封面图上传 |
| 🎪 展览系统 | 茶博会/展览活动管理、在线报名与审核、展览状态全生命周期管控 |
| 🛒 电商系统 | 茶叶商品管理、购物车、订单创建与查询 |
| 🛡️ 网关系统 | 统一入口、路由转发、跨域处理、Sentinel 流量控制与熔断降级 |

---

## 🛠️ 技术栈总览

| 层级 | 技术选型 | 版本 |
| :--- | :--- | :--- |
| 后端框架 | Spring Boot | 3.3.4 |
| 微服务 | Spring Cloud + Spring Cloud Alibaba | 2023.0.3 / 2023.0.3.2 |
| API 网关 | Spring Cloud Gateway | — |
| 服务注册 | Nacos | 2.x |
| 流量控制 | Sentinel | 1.8.9 |
| ORM | MyBatis-Plus | 3.5.7 |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | 6.0+ |
| 对象存储 | RustFS | — |
| 认证安全 | JJWT + BCrypt | 0.11.5 |
| JDK | OpenJDK / Oracle JDK | 17 |
| 前端框架 | Vue | 3.5+ |
| 构建工具 | Vite | 8.x |
| UI 组件库 | Element Plus | 2.14+ |
| 状态管理 | Pinia | 3.x |
| HTTP 客户端 | Axios | 1.x |
| 路由 | Vue Router | 5.x |

---

## 📂 项目结构

```
tea-platform/
├── backend/tea-backend/
│   ├── gateway-service/          # API 网关（端口 10010）
│   ├── user-service/             # 用户服务
│   ├── content-service/          # 内容服务（文章/资讯）
│   ├── exhibition-service/       # 展览服务
│   ├── product-service/          # 商品/订单服务
│   ├── tea-common/               # 公共模块（JWT/工具类）
│   ├── sql/                      # 数据库初始化脚本
│   └── pom.xml                   # Maven 父 POM
├── frontend/
│   ├── client-web/               # C 端用户前台（端口 5173）
│   └── admin-web/                # 后台管理系统（端口 5174）
├── docs/                         # 项目文档
│   ├── 开发操作文档.md
│   ├── 茶文平台-DDL建表SQL.sql
│   ├── Sentinel_1.8.9_集成方案.md
│   └── 方案B.md
├── logs/                         # 日志目录
├── LICENSE                       # Apache-2.0
└── README.md
```

### 微服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| gateway-service | 10010 | 统一入口网关 |
| user-service | 动态分配 | 用户 & 地址管理 |
| content-service | 动态分配 | 文章 & 文件上传 |
| exhibition-service | 动态分配 | 展览 & 报名管理 |
| product-service | 动态分配 | 商品 & 购物车 & 订单 |

---

## 🚀 快速开始

### 环境要求

| 软件 | 最低版本 | 推荐版本 |
|------|---------|---------|
| JDK | 17 | 17.0.x |
| Maven | 3.8+ | 3.9.x |
| Node.js | 18+ | 20.x LTS |
| MySQL | 8.0 | 8.0.x |
| Redis | 6.0 | 7.x |
| Nacos | 2.2 | 2.3.x |
| RustFS | — | 最新稳定版 |

> 推荐内存 **16 GB** 以上（需同时运行 MySQL、Redis、Nacos、RustFS 及多个微服务）。

### 1. 克隆项目

```bash
git clone https://gitcode.com/springcloudlab/A2503-tea-culture-exhibition.git
cd tea-platform
```

### 2. 初始化数据库

```bash
mysql -u root -p < docs/茶文平台-DDL建表SQL.sql
```

### 3. 启动基础设施

- **Nacos**：`http://127.0.0.1:8848/nacos`（默认 `nacos/nacos`）
- **Redis**：`localhost:6379`
- **RustFS**：控制台 `http://127.0.0.1:9001`，API `http://127.0.0.1:9000`
- **Sentinel Dashboard**：`http://localhost:8089`（默认 `sentinel/sentinel`）

### 4. 配置本地环境

```bash
# 复制公共配置到各子模块
for module in user-service content-service exhibition-service product-service; do
  cp backend/tea-backend/application-local.yml backend/tea-backend/$module/src/main/resources/
done
```

### 5. 启动后端

```bash
cd backend/tea-backend
mvn clean install -DskipTests

# 依次启动各服务（每个在新终端）
cd gateway-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
cd ../user-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
cd ../content-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
cd ../exhibition-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
cd ../product-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

### 6. 启动前端

```bash
# C 端前台 → http://localhost:5173
cd frontend/client-web && npm install && npm run dev

# 后台管理 → http://localhost:5174
cd frontend/admin-web && npm install && npm run dev
```

---

## 🔌 API 路由规划

所有请求统一经网关 `http://localhost:10010` 路由至对应微服务。

### 用户端

| 路径 | 服务 | 功能 |
|------|------|------|
| `/user/**` | user-service | 注册、登录、个人信息、地址管理 |
| `/content/**` | content-service | 文章列表/详情、分类、上传 |
| `/exhibition/**` | exhibition-service | 展览列表/详情、在线报名 |
| `/product/**` | product-service | 商品列表/详情、购物车、下单 |
| `/home/**` | gateway-service | 首页聚合数据 |

### 管理端

| 路径 | 服务 | 功能 |
|------|------|------|
| `/admin/user/**` | user-service | 用户管理、角色变更 |
| `/admin/content/**` | content-service | 文章发布/审核、分类管理 |
| `/admin/exhibition/**` | exhibition-service | 展览发布、报名管理 |
| `/admin/product/**` | product-service | 商品管理、订单管理 |

---

## 🏗️ 构建部署

### 前端构建

```bash
cd frontend/client-web && npm run build    # 产物 → dist/
cd frontend/admin-web && npm run build     # 产物 → dist/
```

### 后端构建

```bash
cd backend/tea-backend
mvn clean package -DskipTests
# 各模块 JAR 位于各自 target/ 目录
```

### 生产部署建议

1. 前端 `dist/` 部署至 Nginx，反向代理至网关 `10010`
2. 后端 JAR 通过 `java -jar` 启动，指定生产配置
3. Nacos、Sentinel、Redis、RustFS 使用生产地址
4. 推荐通过 systemd 或容器化进行进程守护

---

## 📚 文档索引

| 文档 | 说明 |
|------|------|
| [开发操作文档](docs/开发操作文档.md) | 环境搭建、编码规范、Git 流程、调试技巧 |
| [DDL 建表 SQL](docs/茶文平台-DDL建表SQL.sql) | 完整数据库建表脚本（MySQL 8.0+） |
| [Sentinel 集成方案](docs/Sentinel_1.8.9_集成方案.md) | Sentinel 流控/熔断接入指南 |
| [方案 B](docs/方案B.md) | 技术方案文档 |

---

## 📄 许可证

本项目基于 [Apache License 2.0](LICENSE) 开源。
