# 王锦禄 — 后端核心模块（NEWEMSCCS V2.0）

## 项目说明

本目录是 NEWEMSCCS（高校教务排课管理系统 V2.0）的 **后端 Spring Boot 项目**，由 **王锦禄（项目组长/后端负责人）** 独立完成大部分 Java 源码。

系统实现 **学院课程安排** 的全流程自动化，从教学计划发布 → 课程申报审批 → 自动排课 → 课表导出。

## 7 大核心模块

详见 [MODULES.md](./MODULES.md) — 每个文件的完整功能说明。

| # | 模块 | 核心文件 | 说明 |
|---|------|----------|------|
| 01 | 技术架构设计 | SecurityConfig.java / JwtUtil.java | 四层架构 + JWT 认证 + RBAC 三门户 |
| 02 | ⭐ 排课算法 | SchedulingServiceImpl.java | 55 时间片 × 四层贪心（晚间优先） |
| 03 | ⭐ 规则引擎 | TeachingRuleServiceImpl.java | DB 驱动 4 全局规则 + 三维度配置 |
| 04 | ⭐ 审批流程 | ApprovalServiceImpl.java | 投影验证 + 两级审批状态推进 |
| 05 | 课表导出 | ScheduleExportServiceImpl.java | 教师任务书 / 班级网格 rowspan |
| 06 | 教学计划管理 | TeachingPlanServiceImpl.java | 六阶段状态机 + 一键重置 |
| 07 | 数据库设计 | newemscss.sql | 20 张表 + 55 时间片 + 种子数据 |

## 技术栈

- **后端框架**：Spring Boot 3.2.5 + JDK 17
- **ORM**：MyBatis-Plus 3.5.7
- **安全**：Spring Security + JJWT 0.12.5
- **数据库**：MySQL 8.0（20 张业务逻辑表）
- **API 文档**：Knife4j 4.5.0（Swagger）

## 项目数据

- 20 张数据表 · 7 个 Controller · 14 个 Service 接口 + 9 个实现
- 55 时间片 · 4 条全局规则 · 3 层去重校验
- 8 个 DTO · 11+ 个数据库实体

## 构建

```bash
mvn package -DskipTests
# 生成 target/ems-server-2.0.0.jar
```

## 配套服务

| 服务 | 说明 |
|------|------|
| `TeachingApplicationService` | 授课申报（含规则校验） |
| `AssignmentService` | 教师分配（含去重校验） |
| `TaskPoolService` | 待分配任务池 |
| `ReportsController` | 报表查询 API |
