# 06 — 教学计划管理（课程规则配置 + 一键重置 + 退回排课）

## 功能描述

教学计划全生命周期管理，从创建到发布的完整流程控制。

核心特性：
- **六阶段状态机**：0=草稿 → 1=已发布 → 2=待申报 → 3=待审核 → 4=待排课 → 5=已排课
- **一键重置**：将所有计划状态清零 → 0（清空下游所有数据）
- **退回排课**：已排课(5) → 退回待排课(4)，仅清空课表不丢失申报数据
- **批量发布**：一次发布多个教学计划
- **课程规则配置**：为每门课程配置学院级限制条件

## 文件清单

| 文件路径 | 说明 |
|----------|------|
| `service/TeachingPlanService.java` | 教学计划服务接口。CRUD + 发布/重置/批量操作 |
| `service/impl/TeachingPlanServiceImpl.java` | ★ 核心实现。计划生命周期管理 + 一键重置 + 退回排课 + 规则配置 |
| `controller/admin/PlanController.java` | REST API。`/api/admin/plans/**` 完整 CRUD + 发布/重置/退回 |
| `entity/TeachingPlan.java` | 教学计划实体。含六阶段状态机 + 课程/班级/学时/学分字段 |
| `mapper/TeachingPlanMapper.java` | 教学计划 Mapper。含 selectAllWithNames() 联表查询 |
| `entity/TeachingTask.java` | 教学任务实体。记录教师×课程×班级×学时的教学任务 |
| `mapper/TeachingTaskMapper.java` | 教学任务 Mapper。含 selectByTeacherAndSemester() |
| `dto/CourseRuleConfigDTO.java` | 课程规则配置 DTO |

## 六阶段状态机

```
0(草稿) → 1(已发布/待学院配置) → 2(待教师申报) → 3(待学院审核)
  → 4(待教务处排课) → 5(已排课)
↓ 一键重置：全部 → 0
↓ 退回排课：5 → 4
```
