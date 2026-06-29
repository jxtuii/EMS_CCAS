# 王锦禄 — 后端核心模块（NEWEMSCCS V2.0）

## 项目说明

本项目是 NEWEMSCCS（高校教务排课管理系统 V2.0）中由 **王锦禄（项目组长/后端核心开发）** 负责的后端核心模块。
系统实现**学院课程安排**的全流程自动化：从教学计划下发 → 课程限制配置 → 教师授课申报 → 两级审批 → 自动排课 → 课表导出。

## 7 大核心模块

| 模块 | 目录 | 核心文件 |
|------|------|----------|
| 01-技术架构设计 | [01-技术架构设计](./01-技术架构设计) | SecurityConfig.java / JwtUtil.java |
| 02-排课算法 ★ | [02-排课算法](./02-排课算法) | SchedulingServiceImpl.java（55 时间片贪心）|
| 03-规则引擎 ★ | [03-规则引擎](./03-规则引擎) | TeachingRuleServiceImpl.java（DB 驱动 4 规则）|
| 04-审批流程 ★ | [04-审批流程](./04-审批流程) | ApprovalServiceImpl.java（投影验证）|
| 05-课表导出 | [05-课表导出](./05-课表导出) | ScheduleExportServiceImpl.java（节次合并）|
| 06-教学计划管理 | [06-教学计划管理](./06-教学计划管理) | TeachingPlanServiceImpl.java（六阶段状态机）|
| 07-数据库设计 | [07-数据库设计](./07-数据库设计) | newemscss.sql（20 张表 + 55 时间片）|

## 技术栈

- **后端框架**：Spring Boot 3.2.5 + JDK 17
- **ORM**：MyBatis-Plus 3.5.7
- **安全**：Spring Security + JJWT 0.12.5
- **数据库**：MySQL 8.0（20 张表，逻辑外键）
- **API 文档**：Knife4j 4.5.0（Swagger）

## 项目数据

- 20 张数据表 · 27 个 Controller · 20 个 Service
- 55 时间片 · 4 条全局规则 · 3 层去重
- 8 个缺陷闭环 · 11 周开发周期
