# Git 管理方案 B：结构化 Monorepo + Git Flow 分支策略

> 远程仓库：`https://gitcode.com/springcloudlab/A2503-tea-culture-exhibition.git`

---

## 1 方案概述

将所有代码（前端、后端、文档）统一存放在一个 Git 仓库中，通过精心设计的 `.gitignore` 排除非必要文件，采用 `main` / `develop` / `feature/*` 分支模型管理开发流程。

**核心理念**：茶文化平台是一个完整产品，前后端共享业务语义，不应割裂为多个仓库。

---

## 2 仓库结构

```
tea-platform/                          # Git 仓库根目录
│
├── .gitignore                         # Git 忽略规则（核心配置文件）
├── README.md                          # 项目说明
│
├── frontend/                          # 前端
│   ├── admin-web/                     # 管理端 (Vue 3 + Vite :5174)
│   │   ├── src/
│   │   │   ├── api/                   #   API 接口封装
│   │   │   ├── components/            #   公共组件
│   │   │   ├── layout/               #   布局组件
│   │   │   ├── router/               #   路由与导航守卫
│   │   │   ├── stores/               #   Pinia 状态管理
│   │   │   ├── utils/                #   工具函数
│   │   │   └── views/                #   页面组件
│   │   ├── public/
│   │   ├── .env.development
│   │   ├── .env.production
│   │   ├── index.html
│   │   ├── package.json
│   │   └── vite.config.js
│   │
│   └── client-web/                    # 用户端 (Vue 3 + Vite :5173)
│       └── (同 admin-web 结构)
│
├── backend/                           # 后端
│   └── tea-backend/                   # Maven 父工程 (pom.xml)
│       ├── pom.xml                    # 父 POM（版本/依赖统一管理）
│       ├── tea-common/                # 公共模块 (JAR)
│       │   └── src/main/java/com/tea/common/
│       │       ├── result/Result.java
│       │       ├── exception/BusinessException.java
│       │       ├── util/JwtUtil.java
│       │       └── config/JacksonConfig.java
│       ├── gateway-service/           # 网关服务 :10010
│       │   └── src/
│       │       ├── main/java/com/tea/gateway/
│       │       │   ├── GatewayApplication.java
│       │       │   ├── filter/AuthGlobalFilter.java
│       │       │   ├── controller/HomeController.java
│       │       │   ├── service/HomeAggregationService.java
│       │       │   ├── ov/HomeVO.java
│       │       │   └── config/
│       │       │       ├── WebClientConfig.java
│       │       │       ├── GlobalExceptionHandler.java
│       │       │       ├── SentinelGatewayConfig.java
│       │       │       └── GatewayBlockHandler.java
│       │       └── test/java/com/tea/gateway/
│       │           └── ContentServiceGatewayTest.java
│       ├── user-service/              # 用户服务 :20001
│       │   └── src/
│       │       ├── main/java/com/tea/user/
│       │       │   ├── UserApplication.java
│       │       │   ├── controller/
│       │       │   │   ├── UserController.java
│       │       │   │   ├── AdminUserController.java
│       │       │   │   └── AddressController.java
│       │       │   ├── entity/ (User / Address)
│       │       │   ├── dto/ (LoginDTO / RegisterDTO / PasswordDTO / AddressDTO)
│       │       │   ├── mapper/ (UserMapper / AddressMapper)
│       │       │   └── config/ (MybatisPlusConfig / RedisConfig / GlobalExceptionHandler)
│       │       └── test/java/com/tea/user/controller/
│       │           └── UserControllerTest.java
│       ├── content-service/           # 内容服务 :20002
│       │   └── src/
│       │       ├── main/java/com/tea/content/
│       │       │   ├── ContentApplication.java
│       │       │   ├── controller/
│       │       │   │   ├── ContentController.java
│       │       │   │   ├── AdminContentController.java
│       │       │   │   └── UploadController.java
│       │       │   ├── entity/ (Article / ArticleCategory)
│       │       │   ├── dto/ (ArticlePublishDTO)
│       │       │   ├── mapper/ (ArticleMapper / ArticleCategoryMapper)
│       │       │   └── config/ (MinioConfig / MybatisPlusConfig / GlobalExceptionHandler)
│       │       └── test/java/com/tea/content/Controller/
│       │           └── ContentControllerTest.java
│       ├── exhibition-service/        # 展览服务 :20003
│       │   └── src/
│       │       ├── main/java/com/tea/exhibition/
│       │       │   ├── ExhibitionApplication.java
│       │       │   ├── controller/
│       │       │   │   ├── ExhibitionController.java
│       │       │   │   └── AdminExhibitionController.java
│       │       │   ├── entity/ (Exhibition / ExhibitionSignup)
│       │       │   ├── dto/ (ExhibitionSignupDTO / ExhibitionSaveDTO)
│       │       │   ├── mapper/ (ExhibitionMapper / ExhibitionSignupMapper)
│       │       │   └── config/ (MybatisPlusConfig / GlobalExceptionHandler)
│       │       └── test/ (...)
│       ├── product-service/           # 商品服务 :20004
│       │   └── src/
│       │       ├── main/java/com/tea/product/
│       │       │   ├── ProductApplication.java
│       │       │   ├── controller/
│       │       │   │   ├── ProductController.java
│       │       │   │   ├── AdminProductController.java
│       │       │   │   ├── OrderController.java
│       │       │   │   └── CartController.java
│       │       │   ├── entity/ (Product / Cart / Order / OrderItem)
│       │       │   ├── dto/ (ProductPublishDTO / CreateOrderDTO / CartItemDTO)
│       │       │   ├── ov/ (CartVO)
│       │       │   ├── mapper/ (ProductMapper / CartMapper / OrderMapper / OrderItemMapper)
│       │       │   └── config/ (MybatisPlusConfig / GlobalExceptionHandler)
│       │       └── test/ (...)
│       └── sql/                       # 数据库脚本
│           └── 茶文平台-DDL建表SQL.sql
│
├── docs/                              # 项目文档
│   ├── 项目报告.md                    # 项目综合报告
│   ├── 项目系统设计图.md              # Mermaid 系统设计图
│   ├── 开发操作文档.md                # 开发环境操作指南
│   └── Sentinel_1.8.9_集成方案.md    # Sentinel 集成技术文档
│
└── logs/                              # 运行日志 (不提交，已 gitignore)
```

---

## 3 .gitignore 配置

```gitignore
# ============================================================
# IDE & 编辑器配置
# ============================================================
.idea/
.vscode/
*.iml
*.swp
*.swo
*~

# ============================================================
# Claude Code
# ============================================================
# 保留项目级配置 (.claude/settings.json)，排除会话缓存
.claude/sessions/
.claude/worktrees/
.claude/transcripts/
.claude/scheduled_tasks.json

# ============================================================
# Maven 构建产物
# ============================================================
target/
!.mvn/wrapper/maven-wrapper.jar

# ============================================================
# Node.js 生态
# ============================================================
node_modules/
dist/
.vite/
*.local

# ============================================================
# 日志文件
# ============================================================
logs/
*.log
*.log.*

# ============================================================
# 操作系统自动生成
# ============================================================
.DS_Store
Thumbs.db
Desktop.ini
ehthumbs.db

# ============================================================
# 敏感信息 / 环境覆盖
# ============================================================
.env.local
.env.*.local
*.password
application-local.yml
```

### 关键说明

| 规则 | 原因 |
|------|------|
| `.idea/` 完全排除 | IntelliJ IDEA 配置包含本地路径、JDK 路径等环境信息，提交会导致团队冲突 |
| `.claude/` 部分保留 | 保留 `settings.json`（项目共享的 hook/权限配置），排除会话缓存和临时 worktree |
| `target/` 排除 | Maven 编译输出，体积大且可随时重新构建 |
| `node_modules/` 排除 | 前端依赖，通过 `package.json` + `npm install` 恢复 |
| `logs/` 排除 | 运行时日志，包含本地时间戳和环境信息 |

---

## 4 分支策略（Git Flow 简化版）

### 4.1 分支模型

```
                        hotfix/xxx ─────────────┐
                       /                          \
main ──────●────────────────────●─────────────────●──────  [生产分支]
            \                  / \                /
             \    feature/A   /   \  feature/C   /
              \   /           /     \   /        /
develop ──────●──●──●───●───●───────●──●───●───●───────  [开发主线]
                            \
                             feature/B (进行中)
```

### 4.2 分支定义

| 分支名 | 用途 | 来源 | 合并目标 | 保护规则 |
|--------|------|------|----------|----------|
| `main` | 生产就绪代码，tag 标记版本 | — | — | 禁止直接 push，仅接受 PR/MR |
| `develop` | 日常开发集成，始终可运行 | main 初始化 | — | 需 CI 通过后才能合并 feature |
| `feature/*` | 功能开发，短期存在 | develop | develop | 无，合并后建议删除 |
| `hotfix/*` | 紧急修复 | main | main + develop | 修复后合并到两个分支 |

### 4.3 分支命名规范

```
feature/user-login          # 功能分支
feature/product-sku         # 功能分支
feature/exhibition-v2       # 功能分支
hotfix/order-stock-bug      # 热修复分支
hotfix/sentinel-degrade     # 热修复分支
```

格式：`{type}/{简短描述}`，使用小写字母和连字符。

### 4.4 典型工作流

```bash
# 1. 开始新功能
git checkout develop
git pull origin develop
git checkout -b feature/user-address-crud

# 2. 开发 & 小步提交
git add -A
git commit -m "feat(user): 新增地址增删改查接口"

# 3. 推送并创建 PR/MR
git push -u origin feature/user-address-crud
# → 在 GitCode 上创建 Merge Request → develop

# 4. 合并后清理
git checkout develop
git pull origin develop
git branch -d feature/user-address-crud
```

---

## 5 Commit 规范

### 5.1 格式

```
<type>(<scope>): <subject>

[body]        # 可选，详细描述
[footer]      # 可选，关联 issue
```

### 5.2 Type 类型

| Type | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(user): 新增用户登录接口` |
| `fix` | Bug 修复 | `fix(order): 修复库存扣减并发问题` |
| `docs` | 文档变更 | `docs: 更新系统设计图` |
| `style` | 格式（不影响代码运行） | `style: 统一缩进为 4 空格` |
| `refactor` | 重构（非新功能、非修 Bug） | `refactor(common): Result.error 改为泛型` |
| `perf` | 性能优化 | `perf(gateway): WebClient 连接池优化` |
| `test` | 测试相关 | `test(user): 新增 UserController 10 个测试` |
| `chore` | 构建/工具变更 | `chore: 升级 Spring Boot 至 3.3.4` |
| `revert` | 回滚 | `revert: 回退 feat(user) 提交` |

### 5.3 Scope 范围

```
feat(user)       # 用户服务
feat(content)    # 内容服务
feat(product)    # 商品服务
feat(exhibition) # 展览服务
feat(gateway)    # 网关服务
feat(common)     # 公共模块
feat(frontend)   # 前端
fix(order)       # 订单
docs             # 无 scope（全项目文档）
```

---

## 6 初始提交计划

将当前全部未跟踪文件按业务模块分 10 次提交，形成清晰的项目历史。

| # | Commit Message | 包含内容 |
|---|---------------|----------|
| 1 | `chore: 项目初始化 — .gitignore 与 README` | `.gitignore`、`README.md` |
| 2 | `feat(common): 后端公共模块 — Result / JwtUtil / JacksonConfig / BusinessException` | `backend/tea-backend/tea-common/` |
| 3 | `feat(gateway): 网关服务 — 路由转发 / JWT+Redis鉴权 / 首页聚合 / Sentinel三层容错` | `backend/tea-backend/gateway-service/` |
| 4 | `feat(user): 用户服务 — 注册/登录/登出/密码修改/地址管理` | `backend/tea-backend/user-service/` |
| 5 | `feat(content): 内容服务 — 文章CRUD/分类管理/MinIO文件上传` | `backend/tea-backend/content-service/` |
| 6 | `feat(exhibition): 展览服务 — 展览管理/报名系统/防重复报名` | `backend/tea-backend/exhibition-service/` |
| 7 | `feat(product): 商品服务 — 商品管理/购物车/订单创建/库存扣减` | `backend/tea-backend/product-service/` |
| 8 | `feat(sql): 数据库建表脚本 — 10张业务表 DDL` | `backend/tea-backend/sql/` |
| 9 | `feat(frontend): 前端双端 — client-web 用户端 + admin-web 管理端` | `frontend/` |
| 10 | `docs: 项目文档 — 项目报告 / 系统设计图 / 开发手册 / Sentinel集成方案` | `docs/` |

### 为什么按这个顺序？

1. **基础设施先行**：`.gitignore` 是仓库根基，`tea-common` 是所有微服务的依赖
2. **网关居中**：gateway 是系统流量入口，早提交便于理解架构
3. **业务按域拆分**：user → content → exhibition → product，由简到繁
4. **SQL 独立**：数据库脚本是基础设施，独立提交便于 DBA 审查
5. **前端最后**：前端体积最大，独立提交避免冲淡后端历史
6. **文档收尾**：文档随代码演进，最后提交最准确

---

## 7 执行步骤（完整脚本）

> ⚠️ **以下命令仅供参考，请勿直接执行**。实际执行前需确认工作目录状态。

```bash
# ============================================================
# 步骤 0：准备工作
# ============================================================
cd G:/JavaProject/tea-platform

# 确保在正确的目录且仓库为空状态
git status

# ============================================================
# 步骤 1：创建 .gitignore
# ============================================================
# 手动创建 .gitignore 文件（内容见本文档第 3 节），然后：

git add .gitignore
git commit -m "chore: 项目初始化 — .gitignore 与 README"

# ============================================================
# 步骤 2-10：按模块分批提交
# ============================================================

# Commit 2: 公共模块
git add backend/tea-backend/tea-common/
git commit -m "feat(common): 后端公共模块 — Result / JwtUtil / JacksonConfig / BusinessException"

# Commit 3: 网关服务
git add backend/tea-backend/gateway-service/
git commit -m "feat(gateway): 网关服务 — 路由转发 / JWT+Redis鉴权 / 首页聚合 / Sentinel三层容错"

# Commit 4: 用户服务
git add backend/tea-backend/user-service/
git commit -m "feat(user): 用户服务 — 注册/登录/登出/密码修改/地址管理"

# Commit 5: 内容服务
git add backend/tea-backend/content-service/
git commit -m "feat(content): 内容服务 — 文章CRUD/分类管理/MinIO文件上传"

# Commit 6: 展览服务
git add backend/tea-backend/exhibition-service/
git commit -m "feat(exhibition): 展览服务 — 展览管理/报名系统/防重复报名"

# Commit 7: 商品服务
git add backend/tea-backend/product-service/
git commit -m "feat(product): 商品服务 — 商品管理/购物车/订单创建/库存扣减"

# Commit 8: 数据库脚本
git add backend/tea-backend/sql/
git commit -m "feat(sql): 数据库建表脚本 — 10张业务表 DDL"

# Commit 9: 前端
git add frontend/
git commit -m "feat(frontend): 前端双端 — client-web 用户端 + admin-web 管理端"

# Commit 10: 文档
git add docs/
git commit -m "docs: 项目文档 — 项目报告 / 系统设计图 / 开发手册 / Sentinel集成方案"

# ============================================================
# 步骤 11：连接远程仓库并推送
# ============================================================
git remote add origin https://gitcode.com/springcloudlab/A2503-tea-culture-exhibition.git
git branch -M main
git push -u origin main

# ============================================================
# 步骤 12：创建 develop 分支
# ============================================================
git checkout -b develop
git push -u origin develop

# ============================================================
# 步骤 13（可选）：设置 GitCode 保护规则
# ============================================================
# 在 GitCode 网页端操作：
# 1. Settings → Repository → Protected Branches
# 2. 保护 main 分支：禁止直接 push，要求 MR 合并
# 3. 保护 develop 分支：要求 CI 通过
```

---

## 8 日常开发工作流

### 8.1 新功能开发

```bash
# 1. 从 develop 切出功能分支
git checkout develop
git pull origin develop
git checkout -b feature/add-tea-comment

# 2. 开发过程中小步提交
git add backend/tea-backend/content-service/src/main/java/...
git commit -m "feat(content): 新增评论实体与 Mapper"

git add backend/tea-backend/content-service/src/main/java/...
git commit -m "feat(content): 实现评论发布与列表查询接口"

# 3. 推送
git push -u origin feature/add-tea-comment

# 4. 在 GitCode 创建 Merge Request → develop
# 5. Code Review → 合并 → 删除远程分支
# 6. 本地清理
git checkout develop
git pull origin develop
git branch -d feature/add-tea-comment
```

### 8.2 Bug 修复

```bash
# 从 develop 切出修复分支
git checkout develop
git checkout -b fix/order-null-price

# 修改代码并提交
git add -A
git commit -m "fix(order): 修复订单创建时 price 为 null 导致 NPE 的问题

将 safeDecimal() 调用替换为内联 null 检查，与 safeInt() 保持一致的防御性编程风格。

Closes #42"

git push -u origin fix/order-null-price
# → 创建 MR → develop
```

### 8.3 紧急热修复

```bash
# 从 main 切出
git checkout main
git checkout -b hotfix/sentinel-api-version

# 修复
git add -A
git commit -m "fix(gateway): 修复 Sentinel 1.8.9 API 兼容性问题

setBlockRequestHandler → setBlockHandler
GatewayBlockHandler 返回值适配 BlockRequestHandler 接口"

# 合并到 main 和 develop
git checkout main
git merge hotfix/sentinel-api-version
git push origin main

git checkout develop
git merge hotfix/sentinel-api-version
git push origin develop

# 清理
git branch -d hotfix/sentinel-api-version
```

---

## 9 常见问题处理

### Q1：不小心提交了 target/ 目录？

```bash
# 从 Git 历史中移除但保留本地文件
git rm -r --cached backend/tea-backend/*/target/
git commit -m "chore: 从仓库移除 target 构建产物"
```

### Q2：.idea/ 已经被跟踪了？

```bash
git rm -r --cached .idea/
git commit -m "chore: 从仓库移除 IDE 配置文件"
```

### Q3：如何查看哪些文件被 gitignore 忽略？

```bash
git status --ignored
```

### Q4：需要临时提交一个还在开发中的功能？

```bash
# 使用 WIP (Work In Progress) 标记
git add -A
git commit -m "wip(product): 商品 SKU 管理 — 存储过程未完成"
```

---

## 10 方案对比回顾

| 维度 | 方案 A 简单 Mono | **方案 B 结构化 Mono** | 方案 C Submodule |
|------|:--:|:--:|:--:|
| 设置难度 | ⭐ | ⭐⭐ | ⭐⭐⭐⭐ |
| 多人协作 | 差 | **好** | 一般 |
| 跨前后端改动 | 简单 | **简单** | 复杂 (3 仓库) |
| 历史可追溯性 | 差 | **好** (语义化 commit) | 差 (指针丢失) |
| CI/CD 复杂度 | 低 | **低** | 高 |
| IDE 环境兼容 | 差 (冲突) | **好** (.idea 排除) | 好 |
| 适合团队规模 | 1 人 | **2-8 人** | 10+ 人 |

---

> **结论**：方案 B 是当前项目的最优选择 — 单一仓库保持前后端一致性，完善的 .gitignore 与分支策略支撑团队协作，语义化初始提交提供清晰的项目历史。未来如需拆分，从 Monorepo 迁移到 Submodule 是单向可逆的。