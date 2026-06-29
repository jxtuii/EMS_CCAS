蒋祥涛（前端开发/后端bug修改）
================================
负责模块：
1. 三门户前端页面开发（管理端15个+教师端7个+学生端5个+登录页）
2. 课表网格组件（ScheduleGrid.vue：5x11+rowspan合并+三时段分区）
3. 教师任务书卡片展示（MySchedule.vue：合并时间描述渲染）
4. 后端bug修改：BUG-004(rowspan修复)、BUG-008(节次连续检查)、
   BUG-001(双计数去重)、BUG-005(投影验证去重)、BUG-007(状态更新)
5. 前后端接口联调

周有俊（前端/后端开发）
================================
负责模块：
1. 教师端/学生端前端页面开发
2. 一键重置功能（PlanController.resetAllToDraft + TeachingPlanServiceImpl）
3. 退回排课功能（PlanController.rollbackScheduling + TeachingPlanServiceImpl）
4. 教师申报校验增强（TeachingApplicationServiceImpl.create强制校验）
5. IntentController预校验+提交双保险
6. BUG-006修复（未配规则时回退required_title_level）
7. 前后端联调
