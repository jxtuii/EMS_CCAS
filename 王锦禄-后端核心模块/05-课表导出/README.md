# 05 — 课表导出（节次合并 + 网格构建）

## 功能描述

课表展示与导出模块，提供教师和学生双视角课表输出。

核心特性：
- **教师教学任务书**：按课程×班级分组，合并连续节次为可读描述
- **班级课表网格**：5×11 网格视图，rowspan 合并多节课程
- **节次合并算法**：按天分组 → 连续节次合并（BUG-008 修复：严格检查 sectionNo 连续性）
- **双端 API**：教师端任务书 + 学生端网格课表

## 文件清单

| 文件路径 | 说明 |
|----------|------|
| `service/ScheduleExportService.java` | 课表导出服务接口。getTeacherSchedule() + getClassScheduleGrid() |
| `service/impl/ScheduleExportServiceImpl.java` | ★ 核心实现。教师任务书节次合并 + 班级课表网格 rowspan 构建 |
| `controller/admin/ScheduleExportController.java` | REST API。GET /teacher-schedule + GET /class-grid |
| `dto/TeacherScheduleDTO.java` | 教师课表 DTO。含教师名 + 学期 + 课程任务列表 |
| `dto/TeacherScheduleItem.java` | 教师课表项。含课程名 + 班级 + 周学时 + 时间描述列表 |
| `dto/ClassScheduleGrid.java` | 班级网格 DTO。课表头 + 节次列表 + grid[weekday][sectionNo] |
| `dto/GridCell.java` | 网格单元格。courseName + teacherName + sectionSpan(rowspan) |

## 节次合并示例

输入：周一第1节 + 周一第2节 + 周三第1节
输出："周一 第1-2节 08:00-09:35；周三 第1-2节 08:00-09:35"
