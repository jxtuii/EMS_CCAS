package com.ems.common;

/**
 * 学期工具类：处理学期格式转换。
 *
 * 学期格式：YYYY-YYYY-S
 *   - YYYY-YYYY: 学年，如 2025-2026 表示 2025-2026 学年
 *   - S: 学期，1=秋季学期，2=春季学期
 *
 * 转换规则：
 *   2025-2026-1 → 2025-2026-2（同一学年，秋季→春季）
 *   2025-2026-2 → 2026-2027-1（下一学年，春季→秋季）
 */
public class SemesterUtils {

    private SemesterUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 根据当前学期计算下一学期。
     *
     * @param current 当前学期，格式如 "2025-2026-1"
     * @return 下一学期，格式如 "2025-2026-2" 或 "2026-2027-1"
     * @throws IllegalArgumentException 如果格式不合法
     */
    public static String nextSemester(String current) {
        if (current == null || current.isBlank()) {
            throw new IllegalArgumentException("学期不能为空");
        }

        String[] parts = current.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException("学期格式错误，应为 YYYY-YYYY-S，实际: " + current);
        }

        int year1, year2, semester;
        try {
            year1 = Integer.parseInt(parts[0]);
            year2 = Integer.parseInt(parts[1]);
            semester = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("学期格式错误，数字部分无法解析: " + current, e);
        }

        if (year2 != year1 + 1) {
            throw new IllegalArgumentException("学年级数不合法，year2 应 = year1 + 1，实际: " + current);
        }
        if (semester != 1 && semester != 2) {
            throw new IllegalArgumentException("学期编号不合法，应为 1(秋季)或 2(春季)，实际: " + semester);
        }

        if (semester == 1) {
            // 2025-2026-1 → 2025-2026-2
            return year1 + "-" + year2 + "-2";
        } else {
            // 2025-2026-2 → 2026-2027-1
            return year2 + "-" + (year2 + 1) + "-1";
        }
    }
}
