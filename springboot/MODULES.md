# 王锦禄 — 后端模块文件清单

## 完整文件目录结构

```
springboot/
├── pom.xml                          # Maven 构建配置
├── README.md                        # 项目说明
├── MODULES.md                       # 本文件 — 模块文件清单
├── src/
│   └── main/
│       ├── java/com/ems/
│       │   ├── EmsApplication.java  # 应用启动入口
│       │   ├── common/              # 通用工具
│       │   ├── config/              # 框架配置
│       │   ├── security/            # JWT 认证
│       │   ├── controller/admin/    # REST API 控制器
│       │   ├── dto/                 # 数据传输对象
│       │   ├── entity/              # 数据库实体
│       │   ├── mapper/              # MyBatis-Plus 映射
│       │   ├── service/             # 业务接口
│       │   └── service/impl/        # 业务实现
│       └── resources/
│           ├── application.yml      # 应用配置
│           └── logback-spring.xml   # 日志配置
└── sql/                             # 数据库脚本
    ├── newemscss.sql
    ├── newemscss_reset.sql
    └── migrate_time_slots.sql
```

---

## 01 — 技术架构（四层架构 + JWT + RBAC）

### Maven 项目构建

| 文件 | 说明 |
|------|------|
| `pom.xml` | Maven 构建：JDK 17, Spring Boot 3.2.5, MyBatis-Plus 3.5.7, JJWT 0.12.5, Knife4j 4.5.0 |

### 应用配置

| 文件 | 说明 |
|------|------|
| `src/main/resources/application.yml` | 应用配置：MySQL(druid)、JWT密钥/过期时间、端口9090、日志级别 |
| `src/main/resources/logback-spring.xml` | Logback日志：控制台彩色输出、文件滚动策略 |

### 启动入口

| 文件 | 说明 |
|------|------|
| `EmsApplication.java` | Spring Boot启动类。@SpringBootApplication + @MapperScan 扫描 Mapper 包 |

### common/ — 通用组件

| 文件 | 说明 |
|------|------|
| `common/Result.java` | 统一 API 响应封装。code(200=成功)+message+data，提供 success()/error() 静态工厂 |
| `common/GlobalExceptionHandler.java` | 全局异常处理。@RestControllerAdvice 捕获参数异常、权限异常、运行时异常 |
| `common/JwtUtil.java` | JWT 令牌工具。HMAC-SHA 算法生成/解析/校验 Token，含 userId+roles+collegeId |
| `common/SemesterUtils.java` | 学期推算工具。格式 YYYY-YYYY-S，根据当前学期推算下学期(1→2, 2→下学年1) |

### config/ — 框架配置

| 文件 | 说明 |
|------|------|
| `config/CorsConfig.java` | 跨域配置。允许所有来源/方法/头，支持前端5173端口开发 |
| `config/Knife4jConfig.java` | API 文档配置。Knife4j + OpenAPI 3.0，自动生成 Swagger 接口文档 |
| `config/MyBatisPlusConfig.java` | MyBatis-Plus 配置。分页插件 PaginationInnerInterceptor |
| `config/SecurityConfig.java` | Spring Security 核心。三门户URL权限控制，无状态Session，BCrypt加密 |

### security/ — JWT 认证

| 文件 | 说明 |
|------|------|
| `security/LoginUser.java` | 登录用户上下文。含 userId/username/roles/collegeId/roleLevel/teacherId/studentId |
| `security/CustomUserDetailsService.java` | 用户加载服务。从数据库查用户信息+角色+教师/学生关联，构造LoginUser |
| `security/JwtAuthenticationFilter.java` | JWT 认证过滤器。OncePerRequestFilter 提取Token→解析→设置SecurityContext |

---

## 02 — 排课算法（55时间片 + 四层贪心）

| 文件 | 说明 |
|------|------|
| `service/SchedulingService.java` | 排课服务接口：schedule()执行排课，publishTasks()发布教学任务 |
| `service/impl/SchedulingServiceImpl.java` | [核心] 55时间片(5天x11节) + findSlots()四层贪心：冲突过滤→周三晚间优先→连续节次/跨天拼凑→容错保留 |
| `controller/admin/SchedulingController.java` | 排课API。POST /run执行排课，POST /publish发布任务(自动推算下学期) |
| `entity/Schedule.java` | 课表实体。关联班级x课程x教师x时间片，含联表字段 className/courseName/teacherName |
| `entity/TimeSlot.java` | 时间片实体。55条：5天x11节，含 weekday/sectionNo/startTime/endTime |
| `mapper/ScheduleMapper.java` | 课表 Mapper。按班级/教师/学期联表查询(LEFT JOIN 6张表) |
| `mapper/TimeSlotMapper.java` | 时间片 Mapper。按 weekday 查询时间片 |
| `dto/SchedulingResultDTO.java` | 排课结果 DTO：successCount + conflictCount + conflicts列表 |

---

## 03 — 规则引擎（DB驱动 + 三维度 + 三层去重）

| 文件 | 说明 |
|------|------|
| `service/TeachingRuleService.java` | 规则引擎接口：全局规则校验(validateAll)、学院课程规则(validateCourseRules)、排除式校验 |
| `service/impl/TeachingRuleServiceImpl.java` | [核心] 4条全局规则(DB读取)+学院课程三维度+三层去重(申报/审批/分配) |
| `entity/TeachingRule.java` | 教学规则实体。ruleType+ruleValue+enabled，改DB即生效无需重新编译 |
| `entity/TeachingAccident.java` | 教学事故实体。含 level/isSerious 字段，关联教师学期 |
| `mapper/TeachingRuleMapper.java` | 教学规则 Mapper(基础CRUD) |
| `mapper/TeachingAccidentMapper.java` | 教学事故 Mapper。countSeriousByTeacherAndSemester 查严重事故数 |
| `dto/CourseRuleConfigDTO.java` | 课程规则配置DTO：minTitleLevel + maxWeeklyHours + minWeeklyHours |

---

## 04 — 审批流程（投影验证 + 状态推进）

| 文件 | 说明 |
|------|------|
| `service/ApprovalService.java` | 审批接口：approve()审批/驳回，getRecordsByApplicationId()查询审批记录 |
| `service/impl/ApprovalServiceImpl.java` | [核心] revalidateBeforeApproval()投影验证(预计算工作量是否超限) + 两级审批状态推进 + 自动创建教学任务 |
| `controller/admin/ApprovalController.java` | 审批API。POST /api/admin/approvals 审批操作，角色自动识别(教研室主任/学院管理员) |
| `entity/ApprovalRecord.java` | 审批记录实体。applicationId+approverId+action+comment+createTime |
| `entity/TeachingApplication.java` | 授课申报实体。status状态机(0草稿→1已提交→2教研室通过→3驳回→4学院通过) |
| `mapper/ApprovalRecordMapper.java` | 审批记录 Mapper。selectByApplicationId联表查询审批人姓名 |
| `mapper/TeachingApplicationMapper.java` | 授课申报 Mapper。selectAllWithNames/selectByCollegeId联表查教师/课程/班级名 |
| `dto/ApprovalDTO.java` | 审批请求 DTO：applicationId + approved + comment |

---

## 05 — 课表导出（节次合并 + 网格构建）

| 文件 | 说明 |
|------|------|
| `service/ScheduleExportService.java` | 导出接口：getTeacherSchedule()教师任务书，getClassScheduleGrid()班级网格课表 |
| `service/impl/ScheduleExportServiceImpl.java` | [核心] 教师端：mergeTimeDescriptions()合并连续节次为可读描述；学生端：rowspan网格构建 |
| `controller/admin/ScheduleExportController.java` | 导出API。GET /teacher-schedule/{id} 教师课表，GET /class-grid/{id} 班级网格 |
| `dto/TeacherScheduleDTO.java` | 教师课表 DTO：teacherName + semester + tasks列表 |
| `dto/TeacherScheduleItem.java` | 教师课表明细：courseName + className + timeDescriptions(合并节次描述) |
| `dto/ClassScheduleGrid.java` | 班级网格 DTO：weekLabels + sections + grid(weekdayxsectionNo映射) |
| `dto/GridCell.java` | 网格单元格：courseName + teacherName + sectionSpan(rowspan合并) |

---

## 06 — 教学计划管理（六阶段状态机 + 一键重置）

| 文件 | 说明 |
|------|------|
| `service/TeachingPlanService.java` | 计划接口：CRUD + 发布/批量发布 + 课程规则配置 + 一键重置 + 退回排课 |
| `service/impl/TeachingPlanServiceImpl.java` | [核心] 六阶段状态机(0→1→2→3→4→5)，configureCourseRules()学院课程三维度配置 |
| `controller/admin/PlanController.java` | 计划API。/plans/** 教学计划CRUD + 规则配置 + 重置/退回 |
| `entity/TeachingPlan.java` | 教学计划实体。status六阶段状态机 + semester/college/course/class/totalHours/weeklyHours |
| `entity/TeachingTask.java` | 教学任务实体。teacherId+courseId+classId+semester+roleType+hours |
| `mapper/TeachingPlanMapper.java` | 计划 Mapper。selectAllWithNames/selectByCollegeId联表查询 |
| `mapper/TeachingTaskMapper.java` | 任务 Mapper。按教师/班级/学期联表查询 |

---

## 07 — 数据库设计（20张表 + 55时间片）

| 文件 | 说明 |
|------|------|
| `sql/newemscss.sql` | 完整建库建表：20张表(sys_user/role/teacher/student/plan/application/schedule/rule等) + 55条time_slot + 种子数据 |
| `sql/newemscss_reset.sql` | 数据库重置：清空所有业务表数据，重置自增ID |
| `sql/migrate_time_slots.sql` | 时间片迁移：V1.0(25时间片) → V2.0(55时间片)，新增晚间9-11节 |

---

## 配套服务（跨模块辅助）

### REST API 控制器

| 文件 | 说明 |
|------|------|
| `controller/admin/AssignmentController.java` | 教师分配API。GET /teachers 教师列表，POST /validate 校验分配规则 |
| `controller/admin/ReportsController.java` | 报表查询API。查询教学任务/课表/学期列表 |

### 业务逻辑接口

| 文件 | 说明 |
|------|------|
| `service/AssignmentService.java` | 教师分配接口：validateRules()校验 + assign()执行分配 |
| `service/TeachingApplicationService.java` | 授课申报接口：教师提交申报，含规则校验调用 |
| `service/TaskPoolService.java` | 任务池接口：待分配任务管理 |
| `service/RecordService.java` | 综合查询接口：教学任务/课表/学期查询 |
| `service/AuthService.java` | 认证接口：登录 + 密码重置 |
| `service/ClazzService.java` | 班级基础数据查询 |
| `service/CollegeService.java` | 学院基础数据查询 |
| `service/CourseService.java` | 课程基础数据 CRUD |
| `service/DepartmentService.java` | 教研室基础数据查询 |
| `service/ExamPlanService.java` | 考试计划 CRUD + 发布 |
| `service/NoticeService.java` | 通知公告 CRUD |
| `service/RoomPlanService.java` | 教室计划 CRUD + 占用/释放 |
| `service/StudentService.java` | 学生基础数据 CRUD |
| `service/TeacherService.java` | 教师基础数据查询 |

### 业务逻辑实现

| 文件 | 说明 |
|------|------|
| `service/impl/AssignmentServiceImpl.java` | 分配实现。validateAllExcluding去重校验 + 统一入口调用规则引擎 |
| `service/impl/TeachingApplicationServiceImpl.java` | 申报实现。教师提交授课申请，调用规则引擎校验，状态机推进 |

### 数据传输对象

| 文件 | 说明 |
|------|------|
| `dto/AssignTeacherDTO.java` | 教师分配DTO：applicationId + teacherId + roleType |
| `dto/PublishResultDTO.java` | 发布结果DTO：taskCount + details列表 |

---

## 角色权限一览

| 角色 | 等级 | 门户 | URL前缀 | 预置账号 |
|------|------|------|----------|----------|
| ADMIN_SCHOOL(教务处管理员) | 0 | 管理端 | /api/admin/** | admin |
| ADMIN_COLLEGE(学院管理员) | 1 | 管理端 | /api/admin/** | xy001(张明) |
| DIRECTOR_DEPT(教研室主任) | 2 | 管理端 | /api/admin/** | xy002(李丽) |
| TEACHER(教师) | 3 | 教师端 | /api/teacher/** | js001-js012 |
| STUDENT(学生) | 4 | 学生端 | /api/student/** | S001-S010 |

## 技术栈

| 组件 | 版本 |
|------|------|
| JDK | 17 |
| Spring Boot | 3.2.5 |
| MyBatis-Plus | 3.5.7 |
| JJWT | 0.12.5 |
| Knife4j | 4.5.0 |
| MySQL | 8.0 |
| Druid | 连接池 |
