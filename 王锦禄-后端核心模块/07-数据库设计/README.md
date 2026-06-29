# 07 — 数据库设计（20 张表 + 55 时间片）

## 功能描述

系统的完整数据库设计与 SQL 脚本。

核心特性：
- **20 张数据表**：覆盖用户/权限/排课/审批/课表/基础数据六大模块
- **55 时间片**：5 天 × 11 节（V1.0 的 25 → V2.0 扩展至 55）
- **逻辑外键**：通过 COMMENT 标注关联关系，无物理 FOREIGN KEY
- **utf8mb4 字符集**：支持完整 Unicode
- **种子数据**：预置角色/用户/课程/班级/教师/时间片

## 文件清单

| 文件 | 说明 |
|------|------|
| `newemscss.sql` | 完整建库建表脚本。AUTO_INCREMENT 种子数据 + 20 张表 + 55 时间片 + 全部种子数据 |
| `newemscss_reset.sql` | 数据库重置脚本。清空所有业务表数据 + 重置自增 ID |
| `migrate_time_slots.sql` | 时间片迁移脚本。V1.0(25) → V2.0(55)，新增晚间 9-11 节 |

## 表结构概览

| 分组 | 表数 | 表名 |
|------|------|------|
| 用户与权限 | 5 | sys_user, sys_role, sys_user_role, teacher, student |
| 排课核心 | 7 | teaching_plan, teaching_application, approval_record, teaching_task, teaching_rule, teaching_accident, task_pool |
| 课表与资源 | 4 | time_slot(55条), schedule, room_plan, exam_plan |
| 基础数据 | 4 | college, department, course, class |
| 信息发布 | 1 | notice |
