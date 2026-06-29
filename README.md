# NEWEMSCCS — 高校教务排课管理系统 V2.0.0

> 基于 Spring Boot 3.2.5 + Vue 3 + MySQL 8.0 的全流程教务排课平台。
> 从教学计划发布 → 课程申报 → 审批 → 自动排课 → 课表导出，覆盖教务管理完整闭环。

---

## 📋 目录

- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [核心业务流程](#核心业务流程)
- [核心功能模块](#核心功能模块)
- [快速启动](#快速启动)
- [项目数据](#项目数据)
- [团队](#团队)
- [相关文档](#相关文档)

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **后端框架** | Spring Boot | 3.2.5 |
| **JDK** | OpenJDK | 17 |
| **ORM** | MyBatis-Plus | 3.5.7 |
| **安全认证** | Spring Security + JJWT | 0.12.5 |
| **API 文档** | Knife4j (Swagger) | 4.5.0 |
| **数据库** | MySQL | 8.0 |
| **连接池** | Druid | — |
| **前端框架** | Vue 3 + Vite | 5.x |
| **UI 组件** | Element Plus | — |
| **构建工具** | Maven / npm | — |

---

## 项目结构

```
EMS_CCAS/
├── README.md                    # 本文件 — 项目总说明
├── CLAUDE.md                    # Claude Code 项目配置
├── DESIGN.md                    # 设计规范（Supabase 风格）
├── DATABASE_REFERENCE.md        # 数据库参考文档
├── nginx.conf                   # Nginx 反向代理配置（8088 → 9090/5173）
├── springboot/                  # 后端 Maven 项目
│   ├── pom.xml                  # 构建配置 → ems-server-2.0.0.jar
│   ├── README.md                # 后端说明
│   ├── MODULES.md               # 后端模块与文件功能介绍
│   ├── sql/                     # 数据库脚本
│   └── src/                     # Java 源码
└── vue/                         # 前端 Vue 项目
    ├── package.json
    ├── vite.config.js
    └── src/                     # Vue 组件/路由/状态
```

---

## 核心业务流程

```
教学计划发布（教务处管理员）
       ↓
课程申报与教师分配（学院管理员 / 教研室主任）
       ↓
授课申报（教师）
       ↓
两级审批（教研室主任 → 学院管理员）
       ↓
自动排课（55 时间片贪心算法）
       ↓
课表查询与导出（教师课表 / 班级网格）
```

### 角色权限

| 角色 | 门户 | URL 前缀 |
|------|------|----------|
| ADMIN_SCHOOL（教务处管理员） | 管理端 | `/api/admin/**` |
| ADMIN_COLLEGE（学院管理员） | 管理端 | `/api/admin/**` |
| DIRECTOR_DEPT（教研室主任） | 管理端 | `/api/admin/**` |
| TEACHER（教师） | 教师端 | `/api/teacher/**` |
| STUDENT（学生） | 学生端 | `/api/student/**` |

---

## 核心功能模块

### 后端（`springboot/` — 详见 [MODULES.md](springboot/MODULES.md)）

| # | 模块 | 核心亮点 |
|---|------|----------|
| 01 | 技术架构设计 | 四层架构 + JWT + RBAC 三门户权限控制 |
| 02 | ⭐ 排课算法 | 55 时间片 × 四层贪心（晚间优先 + 连续节次拼凑） |
| 03 | ⭐ 规则引擎 | DB 驱动 4 条全局规则 + 学院课程三维度 + 三层去重 |
| 04 | ⭐ 审批流程 | 投影验证（预计算工作量）+ 两级审批状态推进 |
| 05 | 课表导出 | 教师任务书（节次合并）+ 班级网格（rowspan 构建） |
| 06 | 教学计划管理 | 六阶段状态机 + 一键重置 + 退回排课 |
| 07 | 数据库设计 | 20 张表 + 55 时间片 + 种子数据 |

### 前端（`vue/`）

| 模块 | 说明 |
|------|------|
| 管理端 | 教学计划管理、课程规则配置、教师分配、审批、排课控制、课表导出 |
| 教师端 | 授课申报、我的课表（卡片式展示）、通知公告 |
| 学生端 | 班级课表（5×11 网格 + rowspan 合并）、通知公告 |

---

## 快速启动

### 后端

```bash
# 1. 创建数据库并导入
mysql -u root -p < springboot/sql/newemscss.sql

# 2. 启动服务
cd springboot
mvn spring-boot:run
# 服务启动于 http://localhost:9090
# API 文档 http://localhost:9090/doc.html
```

### 前端

```bash
cd vue
npm install
npm run dev
# 启动于 http://localhost:5173
```

### 生产部署

```bash
# 后端打包
cd springboot && mvn package
# 生成 target/ems-server-2.0.0.jar

# 前端构建
cd vue && npm run build
# 生成 dist/

# Nginx 代理（参考 nginx.conf）
# 8088 端口统一入口
```

---

## 项目数据

| 指标 | 数量 |
|------|------|
| 数据库表 | 20 张 |
| REST Controller | 7 个 |
| Service 接口/实现 | 14 / 9 个 |
| 时间片 | 55 个（5 天 × 11 节） |
| 全局规则 | 4 条（DB 驱动） |
| Vue 页面 | 管理端 15+ / 教师端 7 / 学生端 5 |

---

## 团队

| 成员 | 角色 | 职责 |
|------|------|------|
| 王锦禄 | 项目组长 / 后端开发 | 后端 64+ 个 Java 文件 |
| 蒋祥涛 | 前端开发 / Bug 修复 | Vue 页面开发、ScheduleGrid.vue 课表组件 |
| 周有俊 | 前端开发 / 后端协作 | 前后端接口联调、Bug 修复 |
| 王聪 | 测试 | 运维 + 测试 |

---

## 相关文档

- [后端模块文件清单](springboot/MODULES.md) — 每个 Java 文件的详细功能说明
- [设计规范](DESIGN.md) — Supabase 风格设计系统
- [数据库参考](DATABASE_REFERENCE.md)
