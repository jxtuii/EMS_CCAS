# 王锦禄 — 后端模块文件清单

## 7 大核心模块

### 01 - 技术架构设计（四层架构 + JWT 安全 + RBAC 权限）

| 文件 | 说明 |
|------|------|
| `pom.xml` | Maven 构建配置 |
| `src/main/resources/application.yml` | 应用配置（MySQL/JWT/端口） |
| `src/main/resources/logback-spring.xml` | 日志配置 |
| `src/main/java/com/ems/EmsApplication.java` | Spring Boot 启动入口 |
| `src/main/java/com/ems/common/Result.java` | 统一 API 响应封装 |
| `src/main/java/com/ems/common/GlobalExceptionHandler.java` | 全局异常处理 |
| `src/main/java/com/ems/common/JwtUtil.java` | JWT 令牌工具 |
| `src/main/java/com/ems/common/SemesterUtils.java` | 学期推算工具 |
| `src/main/java/com/ems/config/CorsConfig.java` | 跨域配置 |
| `src/main/java/com/ems/config/Knife4jConfig.java` | API 文档配置 |
| `src/main/java/com/ems/config/MyBatisPlusConfig.java` | MyBatis-Plus 配置 |
| `src/main/java/com/ems/config/SecurityConfig.java` | Spring Security RBAC |
| `src/main/java/com/ems/security/CustomUserDetailsService.java` | 用户加载服务 |
| `src/main/java/com/ems/security/LoginUser.java` | 登录用户上下文 |
| `src/main/java/com/ems/security/JwtAuthenticationFilter.java` | JWT 认证过滤器 |

### 02 - ⭐ 排课算法（55 时间片贪心 + 晚间优先）

| 文件 | 说明 |
|------|------|
| `src/main/java/com/ems/service/SchedulingService.java` | 排课服务接口 |
| `src/main/java/com/ems/service/impl/SchedulingServiceImpl.java` | ★ 核心算法：55 时间片四层贪心 + 晚间优先 |
| `src/main/java/com/ems/controller/admin/SchedulingController.java` | 排课 REST API |
| `src/main/java/com/ems/entity/Schedule.java` | 课表实体 |
| `src/main/java/com/ems/entity/TimeSlot.java` | 时间片实体（55 条）|
| `src/main/java/com/ems/mapper/ScheduleMapper.java` | 课表 Mapper |
| `src/main/java/com/ems/mapper/TimeSlotMapper.java` | 时间片 Mapper |
| `src/main/java/com/ems/dto/SchedulingResultDTO.java` | 排课结果 DTO |

### 03 - ⭐ 规则引擎（DB 驱动 + 学院课程规则 + 三层去重）

| 文件 | 说明 |
|------|------|
| `src/main/java/com/ems/service/TeachingRuleService.java` | 规则引擎接口 |
| `src/main/java/com/ems/service/impl/TeachingRuleServiceImpl.java` | ★ DB 驱动 4 规则 + 学院课程三维度 + 三层去重 |
| `src/main/java/com/ems/entity/TeachingRule.java` | 教学规则实体 |
| `src/main/java/com/ems/entity/TeachingAccident.java` | 教学事故实体 |
| `src/main/java/com/ems/mapper/TeachingRuleMapper.java` | 教学规则 Mapper |
| `src/main/java/com/ems/mapper/TeachingAccidentMapper.java` | 教学事故 Mapper |
| `src/main/java/com/ems/dto/CourseRuleConfigDTO.java` | 课程规则配置 DTO |

### 04 - ⭐ 审批流程（投影验证 + 状态推进）

| 文件 | 说明 |
|------|------|
| `src/main/java/com/ems/service/ApprovalService.java` | 审批服务接口 |
| `src/main/java/com/ems/service/impl/ApprovalServiceImpl.java` | ★ 投影验证 + 两级审批 + 自动创建任务 |
| `src/main/java/com/ems/controller/admin/ApprovalController.java` | 审批 REST API |
| `src/main/java/com/ems/entity/ApprovalRecord.java` | 审批记录实体 |
| `src/main/java/com/ems/entity/TeachingApplication.java` | 授课申报实体 |
| `src/main/java/com/ems/mapper/ApprovalRecordMapper.java` | 审批记录 Mapper |
| `src/main/java/com/ems/mapper/TeachingApplicationMapper.java` | 授课申报 Mapper |
| `src/main/java/com/ems/dto/ApprovalDTO.java` | 审批请求 DTO |

### 05 - 课表导出（节次合并 + 网格构建）

| 文件 | 说明 |
|------|------|
| `src/main/java/com/ems/service/ScheduleExportService.java` | 课表导出接口 |
| `src/main/java/com/ems/service/impl/ScheduleExportServiceImpl.java` | ★ 节次合并 + 网格构建 |
| `src/main/java/com/ems/controller/admin/ScheduleExportController.java` | 课表导出 REST API |
| `src/main/java/com/ems/dto/TeacherScheduleDTO.java` | 教师课表 DTO |
| `src/main/java/com/ems/dto/TeacherScheduleItem.java` | 课表明细 DTO |
| `src/main/java/com/ems/dto/ClassScheduleGrid.java` | 班级网格 DTO |
| `src/main/java/com/ems/dto/GridCell.java` | 网格单元格 DTO |

### 06 - 教学计划管理（六阶段状态机 + 一键重置）

| 文件 | 说明 |
|------|------|
| `src/main/java/com/ems/service/TeachingPlanService.java` | 教学计划接口 |
| `src/main/java/com/ems/service/impl/TeachingPlanServiceImpl.java` | ★ 计划管理 + 重置 + 退回排课 |
| `src/main/java/com/ems/controller/admin/PlanController.java` | 教学计划 REST API |
| `src/main/java/com/ems/entity/TeachingPlan.java` | 教学计划实体 |
| `src/main/java/com/ems/entity/TeachingTask.java` | 教学任务实体 |
| `src/main/java/com/ems/mapper/TeachingPlanMapper.java` | 教学计划 Mapper |
| `src/main/java/com/ems/mapper/TeachingTaskMapper.java` | 教学任务 Mapper |

### 07 - 数据库设计（20 张表 + 55 时间片）

| 文件 | 说明 |
|------|------|
| `sql/newemscss.sql` | 完整建库建表脚本 |
| `sql/newemscss_reset.sql` | 数据重置脚本 |
| `sql/migrate_time_slots.sql` | 时间片迁移脚本 |

### 配套服务

| 文件 | 说明 |
|------|------|
| `src/main/java/com/ems/service/TeachingApplicationService.java` | 授课申报接口 |
| `src/main/java/com/ems/service/impl/TeachingApplicationServiceImpl.java` | 授课申报实现 |
| `src/main/java/com/ems/service/AssignmentService.java` | 教师分配接口 |
| `src/main/java/com/ems/service/impl/AssignmentServiceImpl.java` | 教师分配实现 |
| `src/main/java/com/ems/controller/admin/AssignmentController.java` | 分配 REST API |
| `src/main/java/com/ems/service/TaskPoolService.java` | 任务池接口 |
| `src/main/java/com/ems/controller/admin/ReportsController.java` | 报表 REST API |
| `src/main/java/com/ems/dto/PublishResultDTO.java` | 发布结果 DTO |
| `src/main/java/com/ems/dto/AssignTeacherDTO.java` | 教师分配 DTO |
