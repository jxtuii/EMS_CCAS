package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.dto.CourseRuleConfigDTO;
import com.ems.entity.*;
import com.ems.mapper.*;
import com.ems.service.TeachingRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeachingRuleServiceImpl implements TeachingRuleService {

    private static final Logger log = LoggerFactory.getLogger(TeachingRuleServiceImpl.class);

    @Autowired
    private TeachingTaskMapper taskMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private TeachingAccidentMapper accidentMapper;
    @Autowired
    private TeachingRuleMapper ruleMapper;
    @Autowired
    private TeachingApplicationMapper applicationMapper;
    @Autowired
    private TeachingPlanMapper planMapper;

    // ──────────────────────────── 职称等级名称映射 ────────────────────────────
    private static String titleName(int level) {
        return switch (level) {
            case 1 -> "助教";
            case 2 -> "讲师";
            case 3 -> "副教授";
            case 4 -> "教授";
            default -> "未知(" + level + ")";
        };
    }

    // ──────────────────────────── 全局规则值读取 ────────────────────────────
    private int getGlobalRuleInt(String ruleType, int defaultValue) {
        List<TeachingRule> rules = ruleMapper.selectList(
                new LambdaQueryWrapper<TeachingRule>()
                        .eq(TeachingRule::getRuleType, ruleType)
                        .eq(TeachingRule::getEnabled, 1));
        if (rules != null && !rules.isEmpty()) {
            try {
                return Integer.parseInt(rules.get(0).getRuleValue());
            } catch (NumberFormatException e) {
                log.warn("全局规则[{}]的值[{}]无法解析为整数，使用默认值{}", ruleType,
                        rules.get(0).getRuleValue(), defaultValue);
            }
        }
        return defaultValue;
    }

    // ── ★ 统计教师已确认的周学时（教学任务 + 待审批/已通过的申报）──
    private int totalWeeklyHours(Long teacherId, String semester) {
        return totalWeeklyHours(teacherId, semester, null);
    }

    private int totalWeeklyHours(Long teacherId, String semester, Long excludeAppId) {
        int hours = 0;

        // 来自已创建的教学任务（学院已审批通过 or 手动分配）
        List<TeachingTask> tasks = taskMapper.selectByTeacherAndSemester(teacherId, semester);
        Set<Long> taskCourseIds = new HashSet<>();
        for (TeachingTask t : tasks) {
            if (t.getWeeklyHours() != null) hours += t.getWeeklyHours();
            if (t.getCourseId() != null) taskCourseIds.add(t.getCourseId());
        }

        // 来自待审批申报（排除已有任务的课程 + 排除自身）
        List<TeachingApplication> pendingApps = applicationMapper.selectList(
                new LambdaQueryWrapper<TeachingApplication>()
                        .eq(TeachingApplication::getTeacherId, teacherId)
                        .in(TeachingApplication::getStatus, 1, 2));
        for (TeachingApplication app : pendingApps) {
            if (excludeAppId != null && app.getId().equals(excludeAppId)) continue;
            TeachingPlan p = planMapper.selectById(app.getTeachingPlanId());
            if (p != null && p.getWeeklyHours() != null && p.getSemester().equals(semester)) {
                // 该课程的学时已通过 teaching_task 计入，跳过
                if (p.getCourseId() != null && taskCourseIds.contains(p.getCourseId())) continue;
                hours += p.getWeeklyHours();
            }
        }

        return hours;
    }

    // ── ★ 统计教师已确认的主讲课程门数（任务 + 待审批申报）──
    private int totalMainCourseCount(Long teacherId, String semester) {
        return totalMainCourseCount(teacherId, semester, null);
    }

    private int totalMainCourseCount(Long teacherId, String semester, Long excludeAppId) {
        // 已创建的教学任务（含手动分配）
        List<TeachingTask> tasks = taskMapper.selectByTeacherAndSemester(teacherId, semester);
        int count = (int) tasks.stream()
                .filter(t -> "主讲".equals(t.getRoleType()))
                .count();
        Set<Long> taskCourseIds = new HashSet<>();
        for (TeachingTask t : tasks) {
            if (t.getCourseId() != null) taskCourseIds.add(t.getCourseId());
        }

        // 待审批申报（排除已有任务的课程 + 排除自身）
        List<TeachingApplication> pendingApps = applicationMapper.selectList(
                new LambdaQueryWrapper<TeachingApplication>()
                        .eq(TeachingApplication::getTeacherId, teacherId)
                        .in(TeachingApplication::getStatus, 1, 2));
        long pendingCourseCount = pendingApps.stream()
                .filter(app -> excludeAppId == null || !app.getId().equals(excludeAppId))
                .map(app -> planMapper.selectById(app.getTeachingPlanId()))
                .filter(p -> p != null && p.getSemester().equals(semester))
                .filter(p -> p.getCourseId() != null && !taskCourseIds.contains(p.getCourseId()))
                .map(TeachingPlan::getCourseId)
                .distinct()
                .count();
        count += (int) pendingCourseCount;

        return count;
    }

    // ──────────────────────────── 四大全局规则校验 ────────────────────────────
    @Override
    public List<String> validateAll(Long teacherId, Long courseId, String semester,
                                     String roleType, Integer weeklyHours) {
        List<String> errors = new ArrayList<>();

        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            errors.add("教师不存在 (ID=" + teacherId + ")");
            return errors;
        }

        Course course = null;
        if (courseId != null) {
            course = courseMapper.selectById(courseId);
        }

        // ── 规则①：主讲课程门数上限（从DB读取，默认2）──
        int maxCourseLimit = getGlobalRuleInt("MAX_COURSE_LIMIT", 2);
        if ("主讲".equals(roleType)) {
            // ★ 统计已确认的教学任务 + 待审批的申报（去重按课程计数）
            int mainCount = totalMainCourseCount(teacherId, semester);
            if (mainCount >= maxCourseLimit) {
                errors.add("规则①失败：该教师本学期已有 " + mainCount
                        + " 门主讲课程（含已提交申报），每学期最多主讲 " + maxCourseLimit + " 门");
            }
        }

        // ── 规则②：主干课程职称要求（从DB读取，默认3=副教授） ──
        int coreTitleMin = getGlobalRuleInt("CORE_COURSE_TITLE", 3);
        if ("主讲".equals(roleType) && course != null
                && course.getIsCore() != null && course.getIsCore() == 1) {
            if (teacher.getTitleLevel() != null && teacher.getTitleLevel() < coreTitleMin) {
                errors.add("规则②失败：" + titleName(coreTitleMin) + "及以上职称方可承担主干课程主讲"
                        + " (当前职称=" + titleName(teacher.getTitleLevel())
                        + ", 要求≥" + titleName(coreTitleMin)
                        + ", 课程=" + course.getCourseName() + ")");
            }
        }

        // ── 规则③：中层干部周学时上限（从DB读取，默认4）──
        int leaderHourLimit = getGlobalRuleInt("LEADER_HOUR_LIMIT", 4);
        if (teacher.getIsMiddleLeader() != null && teacher.getIsMiddleLeader() == 1
                && weeklyHours != null && weeklyHours > 0) {
            // ★ 统计已确认的教学任务 + 待审批申报的总学时
            int currentWeeklyHours = totalWeeklyHours(teacherId, semester);
            if (currentWeeklyHours + weeklyHours > leaderHourLimit) {
                errors.add("规则③失败：中层干部每周主讲学时不能超过 " + leaderHourLimit
                        + " 学时，当前已排(含已提交申报) " + currentWeeklyHours
                        + " 学时，本课程 " + weeklyHours + " 学时");
            }
        }

        // ── 规则④：严重教学事故禁止主讲（从DB读取，默认1=启用） ──
        int accidentBan = getGlobalRuleInt("ACCIDENT_BAN", 1);
        if ("主讲".equals(roleType) && accidentBan == 1) {
            int seriousCount = accidentMapper.countSeriousByTeacherAndSemester(teacherId, semester);
            if (seriousCount > 0) {
                errors.add("规则④失败：该教师本学期有 " + seriousCount
                        + " 条严重教学事故记录，禁止主讲");
            }
        }

        // ── ★ 学院管理员设置的本课程级别限制条件（新增）★ ──
        if (courseId != null) {
            List<String> courseErrors = validateCourseRules(teacherId, courseId, semester, weeklyHours);
            errors.addAll(courseErrors);
        }

        if (errors.isEmpty()) {
            log.info("教学规则全部通过: teacherId={}, courseId={}, semester={}", teacherId, courseId, semester);
        } else {
            log.warn("教学规则校验失败: teacherId={}, errors={}", teacherId, errors);
        }
        return errors;
    }

    /**
     * 校验规则（排除指定申请ID，用于审批/分配时避免重复计算自身学时）
     */
    public List<String> validateAllExcluding(Long teacherId, Long courseId, String semester,
                                              String roleType, Integer weeklyHours, Long excludeApplicationId) {
        List<String> errors = new ArrayList<>();

        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            errors.add("教师不存在 (ID=" + teacherId + ")");
            return errors;
        }

        Course course = null;
        if (courseId != null) {
            course = courseMapper.selectById(courseId);
        }

        // ── 规则① ──
        int maxCourseLimit = getGlobalRuleInt("MAX_COURSE_LIMIT", 2);
        if ("主讲".equals(roleType)) {
            int mainCount = totalMainCourseCount(teacherId, semester, excludeApplicationId);
            if (mainCount >= maxCourseLimit) {
                errors.add("规则①失败：该教师本学期已有 " + mainCount
                        + " 门主讲课程，每学期最多主讲 " + maxCourseLimit + " 门");
            }
        }

        // ── 规则② ──
        int coreTitleMin = getGlobalRuleInt("CORE_COURSE_TITLE", 3);
        if ("主讲".equals(roleType) && course != null
                && course.getIsCore() != null && course.getIsCore() == 1) {
            if (teacher.getTitleLevel() != null && teacher.getTitleLevel() < coreTitleMin) {
                errors.add("规则②失败：" + titleName(coreTitleMin) + "及以上职称方可承担主干课程主讲"
                        + " (当前职称=" + titleName(teacher.getTitleLevel())
                        + ", 要求≥" + titleName(coreTitleMin)
                        + ", 课程=" + course.getCourseName() + ")");
            }
        }

        // ── 规则③ ──
        int leaderHourLimit = getGlobalRuleInt("LEADER_HOUR_LIMIT", 4);
        if (teacher.getIsMiddleLeader() != null && teacher.getIsMiddleLeader() == 1
                && weeklyHours != null && weeklyHours > 0) {
            int currentWeeklyHours = totalWeeklyHours(teacherId, semester, excludeApplicationId);
            if (currentWeeklyHours + weeklyHours > leaderHourLimit) {
                errors.add("规则③失败：中层干部每周主讲学时不能超过 " + leaderHourLimit
                        + " 学时，当前已排(含已提交申报) " + currentWeeklyHours
                        + " 学时，本课程 " + weeklyHours + " 学时");
            }
        }

        // ── 规则④ ──
        int accidentBan = getGlobalRuleInt("ACCIDENT_BAN", 1);
        if ("主讲".equals(roleType) && accidentBan == 1) {
            int seriousCount = accidentMapper.countSeriousByTeacherAndSemester(teacherId, semester);
            if (seriousCount > 0) {
                errors.add("规则④失败：该教师本学期有 " + seriousCount
                        + " 条严重教学事故记录，禁止主讲");
            }
        }

        // ── 学院课程规则 ──
        if (courseId != null) {
            List<String> courseErrors = validateCourseRules(teacherId, courseId, semester, weeklyHours);
            errors.addAll(courseErrors);
        }

        return errors;
    }

    @Override
    public List<String> validateTeacher(Long teacherId, String semester, String roleType) {
        return validateAll(teacherId, null, semester, roleType, null);
    }

    @Override
    public List<TeachingRule> getEnabledRules() {
        return ruleMapper.selectList(
                new LambdaQueryWrapper<TeachingRule>()
                        .eq(TeachingRule::getEnabled, 1));
    }

    @Override
    public boolean isTeacherBanned(Long teacherId, String semester) {
        return accidentMapper.countSeriousByTeacherAndSemester(teacherId, semester) > 0;
    }

    // ──────────────────── 学院级课程限制规则（新增） ────────────────────

    @Override
    public List<TeachingRule> getCourseRules(Long courseId) {
        return ruleMapper.selectList(
                new LambdaQueryWrapper<TeachingRule>()
                        .eq(TeachingRule::getRuleName, "COURSE_" + courseId)
                        .eq(TeachingRule::getEnabled, 1));
    }

    @Override
    public List<String> validateCourseRules(Long teacherId, Long courseId, String semester,
                                             Integer weeklyHours) {
        List<String> errors = new ArrayList<>();

        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) return errors;

        Course course = courseMapper.selectById(courseId);
        String courseLabel = course != null ? course.getCourseName() : "ID=" + courseId;

        List<TeachingRule> courseRules = getCourseRules(courseId);

        // ── ★ 课程自身的最低职称要求：始终生效（无论学院管理员是否配了规则）──
        int courseMinTitle = course != null && course.getRequiredTitleLevel() != null
                ? course.getRequiredTitleLevel() : 0;

        // ── 学院管理员配置的 MIN_TITLE（如果配了，取学院要求与课程要求的较高者）──
        Integer collegeMinTitle = null;
        Integer collegeMaxHours = null;
        Integer collegeMinHours = null;
        for (TeachingRule rule : courseRules) {
            switch (rule.getRuleType()) {
                case "MIN_TITLE" -> collegeMinTitle = Integer.parseInt(rule.getRuleValue());
                case "MAX_HOURS" -> collegeMaxHours = Integer.parseInt(rule.getRuleValue());
                case "MIN_HOURS" -> collegeMinHours = Integer.parseInt(rule.getRuleValue());
            }
        }

        // ── 最终生效的最低职称 = max(课程自身要求, 学院管理员要求) ──
        int effectiveMinTitle = Math.max(courseMinTitle,
                collegeMinTitle != null ? collegeMinTitle : 0);
        if (effectiveMinTitle > 0 && teacher.getTitleLevel() != null
                && teacher.getTitleLevel() < effectiveMinTitle) {
            String source = collegeMinTitle != null && collegeMinTitle >= courseMinTitle
                    ? "学院设置" : "课程要求";
            errors.add(source + "：课程[" + courseLabel
                    + "]要求教师职称不低于" + titleName(effectiveMinTitle)
                    + "，当前教师职称为" + titleName(teacher.getTitleLevel()));
        }

        // ── 最高周学时（仅学院管理员可配置）──
        if (collegeMaxHours != null && weeklyHours != null && weeklyHours > 0) {
            List<TeachingTask> tasks = taskMapper.selectByTeacherAndSemester(teacherId, semester);
            int currentHours = 0;
            for (TeachingTask t : tasks) {
                if (t.getWeeklyHours() != null) currentHours += t.getWeeklyHours();
            }
            if (currentHours + weeklyHours > collegeMaxHours) {
                errors.add("学院限制：教师周学时总计不得超过" + collegeMaxHours
                        + "学时，当前已排" + currentHours
                        + "学时，本课程" + weeklyHours + "学时，合计"
                        + (currentHours + weeklyHours) + "学时");
            }
        }

        // ── 最低周学时（仅学院管理员可配置）──
        if (collegeMinHours != null && weeklyHours != null && weeklyHours < collegeMinHours) {
            errors.add("学院限制：课程[" + courseLabel
                    + "]周学时不得低于" + collegeMinHours + "学时");
        }

        if (!errors.isEmpty()) {
            log.warn("课程规则校验失败: teacherId={}, courseId={}, courseMinTitle={}, collegeMinTitle={}, errors={}",
                    teacherId, courseId, courseMinTitle, collegeMinTitle, errors);
        }
        return errors;
    }

    @Override
    public List<CourseRuleConfigDTO> getCourseRuleConfigsByCollegeId(Long collegeId) {
        // 获取学院下所有课程
        List<Course> courses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .inSql(Course::getDepartmentId,
                                "SELECT id FROM department WHERE college_id = " + collegeId));

        return courses.stream().map(course -> {
            CourseRuleConfigDTO dto = new CourseRuleConfigDTO();
            dto.setCourseId(course.getId());
            dto.setCourseName(course.getCourseName());

            List<TeachingRule> rules = getCourseRules(course.getId());
            for (TeachingRule rule : rules) {
                switch (rule.getRuleType()) {
                    case "MIN_TITLE" -> dto.setMinTitleLevel(Integer.parseInt(rule.getRuleValue()));
                    case "MAX_HOURS" -> dto.setMaxWeeklyHours(Integer.parseInt(rule.getRuleValue()));
                    case "MIN_HOURS" -> dto.setMinWeeklyHours(Integer.parseInt(rule.getRuleValue()));
                }
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
