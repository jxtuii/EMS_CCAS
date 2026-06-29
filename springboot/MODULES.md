# 王锦禄 — 后端模块文件清单

## 7 大核心模块

### 01 - 技术架构设计（四层架构 + JWT 安全 + RBAC 权限）
- `pom.xml` — Maven 构建配置
- `src/main/resources/application.yml` — 应用配置
- `src/main/resources/logback-spring.xml` — 日志配置
- `common/Result.java` — 统一 API 响应
- `common/GlobalExceptionHandler.java` — 全局异常处理
- `common/JwtUtil.java` — JWT 令牌工具
- `common/SemesterUtils.java` — 学期推算
- `config/CorsConfig.java` — 跨域配置
- `config/Knife4jConfig.java` — API 文档
- `config/MyBatisPlusConfig.java` — MyBatis-Plus 配置
- `config/SecurityConfig.java` — Spring Security RBAC
- `security/*.java` — JWT 认证过滤器 + 用户加载

### 02 - ⭐ 排课算法（55 时间片贪心 + 晚间优先）
- `service/SchedulingService.java` — 排课接口
- `service/impl/SchedulingServiceImpl.java` — ★ 55 时间片四层贪心 + 晚间优先
- `controller/admin/SchedulingController.java` — 排课 API
- `entity/Schedule.java` — 课表实体
- `entity/TimeSlot.java` — 时间片实体
- `dto/SchedulingResultDTO.java` — 排课结果

### 03 - ⭐ 规则引擎（DB 驱动 + 学院课程规则 + 三层去重）
- `service/TeachingRuleService.java` — 规则引擎接口
- `service/impl/TeachingRuleServiceImpl.java` — ★ DB 驱动 4 规则 + 三维度 + 三层去重
- `entity/TeachingRule.java` — 教学规则实体
- `entity/TeachingAccident.java` — 教学事故实体
- `dto/CourseRuleConfigDTO.java` — 课程规则配置

### 04 - ⭐ 审批流程（投影验证 + 状态推进）
- `service/ApprovalService.java` — 审批接口
- `service/impl/ApprovalServiceImpl.java` — ★ 投影验证 + 两级审批
- `controller/admin/ApprovalController.java` — 审批 API
- `entity/ApprovalRecord.java` — 审批记录
- `entity/TeachingApplication.java` — 授课申报
- `dto/ApprovalDTO.java` — 审批请求

### 05 - 课表导出（节次合并 + 网格构建）
- `service/ScheduleExportService.java` — 导出接口
- `service/impl/ScheduleExportServiceImpl.java` — ★ 节次合并 + 网格
- `controller/admin/ScheduleExportController.java` — 导出 API
- `dto/TeacherScheduleDTO.java` / `TeacherScheduleItem.java` — 教师课表
- `dto/ClassScheduleGrid.java` / `GridCell.java` — 班级网格

### 06 - 教学计划管理（六阶段状态机 + 一键重置）
- `service/TeachingPlanService.java` — 计划接口
- `service/impl/TeachingPlanServiceImpl.java` — ★ 计划管理 + 重置 + 退回
- `controller/admin/PlanController.java` — 计划 API
- `entity/TeachingPlan.java` — 教学计划实体
- `entity/TeachingTask.java` — 教学任务实体

### 07 - 数据库设计（20 张表 + 55 时间片）
- `sql/newemscss.sql` — 完整建库建表
- `sql/newemscss_reset.sql` — 数据重置
- `sql/migrate_time_slots.sql` — 时间片迁移

### 配套服务
- `service/TeachingApplicationService.java` / `impl` — 授课申报
- `service/AssignmentService.java` / `impl` — 教师分配
- `controller/admin/AssignmentController.java` — 分配 API
- `service/TaskPoolService.java` — 任务池
- `controller/admin/ReportsController.java` — 报表 API
