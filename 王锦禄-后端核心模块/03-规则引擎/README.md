# 03 — 规则引擎（DB 驱动 + 学院课程规则 + 三层去重）

## 功能描述

教务规则校验引擎，所有规则从 `teaching_rule` 表动态读取（改 DB 即生效，无需重新编译）。

核心特性：
- **4 条全局规则**：从 teaching_rule 表动态读取，修改即生效
- **学院课程三维度**：每门课可配置 MIN_TITLE/MAX_HOURS/MIN_HOURS 规则
- **三层去重机制**：申报 → 审批 → 分配，全链路防重复计数
- **统一校验入口**：validateAll() + validateAllExcluding() 两种模式

## 文件清单

| 文件路径 | 说明 |
|----------|------|
| `service/TeachingRuleService.java` | 规则引擎服务接口。定义全局/课程/三层去重的校验方法 |
| `service/impl/TeachingRuleServiceImpl.java` | ★ 核心实现。DB 驱动规则读取 + 4 条全局规则校验 + 学院课程三维度 + 三层去重 |
| `entity/TeachingRule.java` | 教学规则实体。ruleType + ruleValue + enabled（动态生效） |
| `entity/TeachingAccident.java` | 教学事故实体。记录教师事故信息 |
| `mapper/TeachingRuleMapper.java` | 教学规则 Mapper |
| `mapper/TeachingAccidentMapper.java` | 教学事故 Mapper（含 countSeriousByTeacherAndSemester） |
| `dto/CourseRuleConfigDTO.java` | 课程规则配置 DTO。minTitleLevel + maxWeeklyHours + minWeeklyHours |

## 全局规则（DB 驱动）

| 规则 | 默认值 | 说明 |
|------|--------|------|
| MAX_COURSE_LIMIT | 2 | 教师每学期主讲 ≤ 2 门 |
| CORE_COURSE_TITLE | 3 | 核心课程需副教授(≥3)以上职称 |
| LEADER_HOUR_LIMIT | 4 | 中层干部周学时 ≤ 4 |
| ACCIDENT_BAN | 1 | 严重教学事故教师禁主讲 |

## 三层去重机制

```
Layer 1（教师申报时）：
  tasks（已创建的教学任务）+ pendingApps（待审批的申报）去重

Layer 2（审批时）：
  taskCovered 排除已通过审批并创建了任务的课程

Layer 3（分配时）：
  validateAllExcluding 排除自身 applicationId，不重复计数
```
