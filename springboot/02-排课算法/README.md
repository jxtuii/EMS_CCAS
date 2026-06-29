# 02 — 排课算法（55 时间片贪心 + 晚间优先）

## 功能描述

自动排课核心模块，基于 **55 时间片 × 四层贪心策略** 实现智能排课。

核心特性：
- **55 时间片体系**：5 天 × 11 节（上午 4 + 下午 4 + 晚上 3），V1.0 的 25 片 → V2.0 扩展至 55 片
- **四层贪心算法 findSlots()**：冲突计算 → 晚间优先 → 连续节次 → 跨天拼凑
- **周 3 学时晚间优先**：3 学时的课程优先分配到晚间连续 3 节（19:00-21:25）
- **自动清理旧数据**：排课前清空目标学期旧课表
- **状态推进**：排课成功后自动将教学计划状态更新为 5（已排课）

## 文件清单

| 文件路径 | 说明 |
|----------|------|
| `service/SchedulingService.java` | 排课服务接口。定义 schedule() 和 publishTasks() 方法 |
| `service/impl/SchedulingServiceImpl.java` | ★ 核心算法实现。55 时间片四层贪心分配 + 晚间优先策略 + 教学任务发布 |
| `controller/admin/SchedulingController.java` | REST API。POST /run（执行排课）+ POST /publish（发布任务书） |
| `entity/Schedule.java` | 课表实体。关联班级×课程×教师×时间片 |
| `entity/TimeSlot.java` | 时间片实体。55 条记录（weekday+sectionNo+startTime+endTime） |
| `mapper/ScheduleMapper.java` | 课表 MyBatis-Plus Mapper |
| `mapper/TimeSlotMapper.java` | 时间片 Mapper |
| `dto/SchedulingResultDTO.java` | 排课结果 DTO。successCount + conflictCount + conflicts 列表 |

## 核心算法：findSlots() 四层贪心

```
Layer 1 — 冲突计算：
  计算班级已占时间片(classOcc) + 教师已占时间片(teacherOcc)，过滤得到可用时间片

Layer 2 — 周3学时晚间优先：
  ① 同天连续晚间 9-10-11 节 → ② 同天任意 3 节晚间 → ③ 全部晚间 + 白天补齐

Layer 3 — 通用策略（非 3 学时）：
  ① 同天连续 2 节 + 同日补齐 → ② 同天凑够 → ③ 跨天随机拼凑

Layer 4 — 容错：
  分配失败保留 status=4，可重新排课
```
