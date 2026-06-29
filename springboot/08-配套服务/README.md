# 08 — 配套服务（教师申报 + 教师分配 + 辅助功能）

## 功能描述

配合 7 大核心模块运行的辅助服务，提供教师授课申报、教师分配、报表统计等功能。

## 文件清单

| 文件路径 | 说明 |
|----------|------|
| `service/TeachingApplicationService.java` | 授课申报服务接口 |
| `service/impl/TeachingApplicationServiceImpl.java` | 教师授课申报实现。提交前调用规则引擎校验，状态机管理 |
| `service/AssignmentService.java` | 教师分配服务接口 |
| `service/impl/AssignmentServiceImpl.java` | 教师分配实现。含 validateAllExcluding 去重校验 |
| `controller/admin/AssignmentController.java` | REST API。教师分配 + 规则预校验 + 教师列表 |
| `service/TaskPoolService.java` | 任务池服务接口 |
| `controller/admin/ReportsController.java` | 报表 API。考试/教室/排课统计查询 |
| `dto/PublishResultDTO.java` | 发布结果 DTO |
| `dto/AssignTeacherDTO.java` | 教师分配请求 DTO |
