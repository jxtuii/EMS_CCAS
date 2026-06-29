# 04 — 审批流程（投影验证 + 状态推进 + updateById 时序修复）

## 功能描述

两级审批流转模块，实现教研室主任初审 → 学院管理员终审的完整流程。

核心特性：
- **投影验证 revalidateBeforeApproval**：审批前预计算教师工作量，超限则拦截
- **两级状态推进**：教研室通过(status=1→2) → 学院通过(status=2→4)
- **自动创建教学任务**：学院终审通过后自动生成 TeachingTask
- **updateById 时序修复**：先更新申请状态再创建审批记录，防状态丢失（BUG-002 修复）
- **去重投影**：otherApps 排除 taskCovered 课程（BUG-005 修复）

## 文件清单

| 文件路径 | 说明 |
|----------|------|
| `service/ApprovalService.java` | 审批服务接口。approve() + getRecordsByApplicationId() |
| `service/impl/ApprovalServiceImpl.java` | ★ 核心实现。投影验证 + 两级审批 + 自动创建任务 + 教学计划状态推进 |
| `controller/admin/ApprovalController.java` | REST API。POST（审核）+ GET /records/{id}（审核记录） |
| `entity/ApprovalRecord.java` | 审批记录实体。applicationId + approverId + action + comment |
| `entity/TeachingApplication.java` | 授课申报实体。含 status 状态机(0=草稿→1=待教研室→2=待学院→3=驳回→4=通过→5=拒绝) |
| `mapper/ApprovalRecordMapper.java` | 审批记录 Mapper |
| `mapper/TeachingApplicationMapper.java` | 授课申报 Mapper |
| `dto/ApprovalDTO.java` | 审批请求 DTO。applicationId + approved + comment |

## 投影验证公式

```
projectedHours    = taskHours + otherAppHours + 本申请学时
projectedCourses  = taskCourseIds + otherAppCourseIds + 1
任一超限 → 拦截并抛出 IllegalArgumentException
```
