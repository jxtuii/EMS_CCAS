/*
 * 高校排课管理系统 - 数据库初始化脚本（重置版）
 *
 * 与 newemscss.sql 的区别：
 *   教师、学生、角色、学院、课程等基础数据不变
 *   所有教学计划状态重置为 0（草稿，未发布）
 *   清空所有下游业务数据（申报、审批、排课、考试等）
 *   保留全局教学规则和教学事故记录
 *
 * Date: 2026-06-14
 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 第一部分：组织结构（不变）
-- ============================================================

-- ----------------------------
-- 学院表
-- ----------------------------
DROP TABLE IF EXISTS `college`;
CREATE TABLE `college` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学院名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学院编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_college_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学院表' ROW_FORMAT = Dynamic;

INSERT INTO `college` VALUES (1, '计算机与网络工程学院', 'CS');
INSERT INTO `college` VALUES (2, '商学院', 'BUS');
INSERT INTO `college` VALUES (3, '外国语学院', 'FL');

-- ----------------------------
-- 教研室/系表
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教研室名称',
  `college_id` bigint NOT NULL COMMENT '所属学院 逻辑外键: college.id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教研室/专业方向表' ROW_FORMAT = Dynamic;

INSERT INTO `department` VALUES (1, '计算机科学系', 1);
INSERT INTO `department` VALUES (2, '软件工程系', 1);
INSERT INTO `department` VALUES (3, '会计系', 2);
INSERT INTO `department` VALUES (4, '市场营销系', 2);
INSERT INTO `department` VALUES (5, '英语系', 3);
INSERT INTO `department` VALUES (6, '大学外语教学部', 3);

-- ----------------------------
-- 班级表
-- ----------------------------
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `student_count` int NULL DEFAULT 0 COMMENT '学生人数',
  `college_id` bigint NOT NULL COMMENT '所属学院 逻辑外键: college.id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级表' ROW_FORMAT = Dynamic;

INSERT INTO `class` VALUES (1, '软件工程2201', '软件工程', '2022', 45, 1);
INSERT INTO `class` VALUES (2, '软件工程2202', '软件工程', '2022', 42, 1);
INSERT INTO `class` VALUES (3, '计算机2201', '计算机科学与技术', '2022', 40, 1);
INSERT INTO `class` VALUES (4, '计算机2202', '计算机科学与技术', '2022', 38, 1);
INSERT INTO `class` VALUES (5, '网络工程2201', '网络工程', '2022', 35, 1);
INSERT INTO `class` VALUES (6, '会计2201', '会计学', '2022', 48, 2);
INSERT INTO `class` VALUES (7, '会计2202', '会计学', '2022', 45, 2);
INSERT INTO `class` VALUES (8, '市营2201', '市场营销', '2022', 35, 2);
INSERT INTO `class` VALUES (9, '英语2201', '英语', '2022', 30, 3);
INSERT INTO `class` VALUES (10, '英语2202', '英语', '2022', 28, 3);

-- ----------------------------
-- 课程表
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程代码',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `department_id` bigint NOT NULL COMMENT '所属教研室 逻辑外键: department.id',
  `total_hours` int NULL DEFAULT NULL COMMENT '总学时',
  `weekly_hours` int NULL DEFAULT NULL COMMENT '周学时',
  `credit` decimal(5, 1) NULL DEFAULT NULL COMMENT '学分',
  `course_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程类型',
  `is_core` tinyint NULL DEFAULT 0 COMMENT '是否主干课程 0-否 1-是',
  `required_title_level` int NULL DEFAULT NULL COMMENT '主讲最低职称等级要求',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_course_code`(`course_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

INSERT INTO `course` VALUES (1, 'CS1001', 'Java程序设计', 2, 64, 4, 4.0, '专业课', 0, 2);
INSERT INTO `course` VALUES (2, 'CS1002', '数据结构', 1, 48, 3, 3.0, '专业核心课', 1, 3);
INSERT INTO `course` VALUES (3, 'CS2001', '计算机网络', 1, 48, 3, 3.0, '专业课', 0, 2);
INSERT INTO `course` VALUES (4, 'CS2002', '数据库原理', 1, 48, 3, 3.0, '专业核心课', 1, 3);
INSERT INTO `course` VALUES (5, 'CS1003', '操作系统', 1, 48, 3, 3.0, '专业核心课', 1, 3);
INSERT INTO `course` VALUES (6, 'CS2003', '软件工程', 2, 48, 3, 3.0, '专业课', 0, 2);
INSERT INTO `course` VALUES (7, 'CS3001', '人工智能导论', 1, 32, 2, 2.0, '专业课', 0, 2);
INSERT INTO `course` VALUES (8, 'AC3001', '基础会计', 3, 48, 3, 3.0, '专业核心课', 1, 3);
INSERT INTO `course` VALUES (9, 'MK3001', '市场营销学', 4, 32, 2, 2.0, '专业课', 0, 2);
INSERT INTO `course` VALUES (10, 'AC3002', '财务管理', 3, 48, 3, 3.0, '专业核心课', 1, 3);
INSERT INTO `course` VALUES (11, 'EN4001', '大学英语', 6, 64, 4, 4.0, '公共课', 0, 2);
INSERT INTO `course` VALUES (12, 'EN4002', '英语口语', 5, 32, 2, 2.0, '公共课', 0, 2);

-- ----------------------------
-- 时间片表（不变）
-- ----------------------------
DROP TABLE IF EXISTS `time_slot`;
CREATE TABLE `time_slot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `weekday` int NOT NULL COMMENT '星期',
  `section_no` int NOT NULL COMMENT '节次',
  `start_time` time NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` time NULL DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '时间片表' ROW_FORMAT = Dynamic;

-- 周一 上午4节+下午4节+晚上3节
INSERT INTO `time_slot` VALUES (1, 1, 1, '08:00:00', '08:45:00');
INSERT INTO `time_slot` VALUES (2, 1, 2, '08:50:00', '09:35:00');
INSERT INTO `time_slot` VALUES (3, 1, 3, '09:50:00', '10:35:00');
INSERT INTO `time_slot` VALUES (4, 1, 4, '10:45:00', '11:30:00');
INSERT INTO `time_slot` VALUES (5, 1, 5, '14:00:00', '14:45:00');
INSERT INTO `time_slot` VALUES (6, 1, 6, '14:50:00', '15:35:00');
INSERT INTO `time_slot` VALUES (7, 1, 7, '15:45:00', '16:30:00');
INSERT INTO `time_slot` VALUES (8, 1, 8, '16:35:00', '17:20:00');
INSERT INTO `time_slot` VALUES (9, 1, 9, '19:00:00', '19:45:00');
INSERT INTO `time_slot` VALUES (10, 1, 10, '19:50:00', '20:35:00');
INSERT INTO `time_slot` VALUES (11, 1, 11, '20:40:00', '21:25:00');
-- 周二
INSERT INTO `time_slot` VALUES (12, 2, 1, '08:00:00', '08:45:00');
INSERT INTO `time_slot` VALUES (13, 2, 2, '08:50:00', '09:35:00');
INSERT INTO `time_slot` VALUES (14, 2, 3, '09:50:00', '10:35:00');
INSERT INTO `time_slot` VALUES (15, 2, 4, '10:45:00', '11:30:00');
INSERT INTO `time_slot` VALUES (16, 2, 5, '14:00:00', '14:45:00');
INSERT INTO `time_slot` VALUES (17, 2, 6, '14:50:00', '15:35:00');
INSERT INTO `time_slot` VALUES (18, 2, 7, '15:45:00', '16:30:00');
INSERT INTO `time_slot` VALUES (19, 2, 8, '16:35:00', '17:20:00');
INSERT INTO `time_slot` VALUES (20, 2, 9, '19:00:00', '19:45:00');
INSERT INTO `time_slot` VALUES (21, 2, 10, '19:50:00', '20:35:00');
INSERT INTO `time_slot` VALUES (22, 2, 11, '20:40:00', '21:25:00');
-- 周三
INSERT INTO `time_slot` VALUES (23, 3, 1, '08:00:00', '08:45:00');
INSERT INTO `time_slot` VALUES (24, 3, 2, '08:50:00', '09:35:00');
INSERT INTO `time_slot` VALUES (25, 3, 3, '09:50:00', '10:35:00');
INSERT INTO `time_slot` VALUES (26, 3, 4, '10:45:00', '11:30:00');
INSERT INTO `time_slot` VALUES (27, 3, 5, '14:00:00', '14:45:00');
INSERT INTO `time_slot` VALUES (28, 3, 6, '14:50:00', '15:35:00');
INSERT INTO `time_slot` VALUES (29, 3, 7, '15:45:00', '16:30:00');
INSERT INTO `time_slot` VALUES (30, 3, 8, '16:35:00', '17:20:00');
INSERT INTO `time_slot` VALUES (31, 3, 9, '19:00:00', '19:45:00');
INSERT INTO `time_slot` VALUES (32, 3, 10, '19:50:00', '20:35:00');
INSERT INTO `time_slot` VALUES (33, 3, 11, '20:40:00', '21:25:00');
-- 周四
INSERT INTO `time_slot` VALUES (34, 4, 1, '08:00:00', '08:45:00');
INSERT INTO `time_slot` VALUES (35, 4, 2, '08:50:00', '09:35:00');
INSERT INTO `time_slot` VALUES (36, 4, 3, '09:50:00', '10:35:00');
INSERT INTO `time_slot` VALUES (37, 4, 4, '10:45:00', '11:30:00');
INSERT INTO `time_slot` VALUES (38, 4, 5, '14:00:00', '14:45:00');
INSERT INTO `time_slot` VALUES (39, 4, 6, '14:50:00', '15:35:00');
INSERT INTO `time_slot` VALUES (40, 4, 7, '15:45:00', '16:30:00');
INSERT INTO `time_slot` VALUES (41, 4, 8, '16:35:00', '17:20:00');
INSERT INTO `time_slot` VALUES (42, 4, 9, '19:00:00', '19:45:00');
INSERT INTO `time_slot` VALUES (43, 4, 10, '19:50:00', '20:35:00');
INSERT INTO `time_slot` VALUES (44, 4, 11, '20:40:00', '21:25:00');
-- 周五
INSERT INTO `time_slot` VALUES (45, 5, 1, '08:00:00', '08:45:00');
INSERT INTO `time_slot` VALUES (46, 5, 2, '08:50:00', '09:35:00');
INSERT INTO `time_slot` VALUES (47, 5, 3, '09:50:00', '10:35:00');
INSERT INTO `time_slot` VALUES (48, 5, 4, '10:45:00', '11:30:00');
INSERT INTO `time_slot` VALUES (49, 5, 5, '14:00:00', '14:45:00');
INSERT INTO `time_slot` VALUES (50, 5, 6, '14:50:00', '15:35:00');
INSERT INTO `time_slot` VALUES (51, 5, 7, '15:45:00', '16:30:00');
INSERT INTO `time_slot` VALUES (52, 5, 8, '16:35:00', '17:20:00');
INSERT INTO `time_slot` VALUES (53, 5, 9, '19:00:00', '19:45:00');
INSERT INTO `time_slot` VALUES (54, 5, 10, '19:50:00', '20:35:00');
INSERT INTO `time_slot` VALUES (55, 5, 11, '20:40:00', '21:25:00');

-- ============================================================
-- 第二部分：人员与权限（不变）
-- ============================================================

-- ----------------------------
-- 系统角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统角色表' ROW_FORMAT = Dynamic;

INSERT INTO `sys_role` VALUES (1, '教务处管理员', 'ADMIN_SCHOOL');
INSERT INTO `sys_role` VALUES (2, '学院管理员', 'ADMIN_COLLEGE');
INSERT INTO `sys_role` VALUES (3, '教研室主任', 'DIRECTOR_DEPT');
INSERT INTO `sys_role` VALUES (4, '教师', 'TEACHER');
INSERT INTO `sys_role` VALUES (5, '学生', 'STUDENT');

-- ----------------------------
-- 系统用户表（密码均为 123456 的 BCrypt 哈希）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '系统管理员', '13800000000', 'admin@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (2, 'xy001', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '张明', '13800000001', 'zhangming@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (3, 'xy002', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '李丽', '13800000002', 'lili@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (4, 'xy003', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '王芳', '13800000003', 'wangfang@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (5, 'js001', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '王强', '13800000004', 'wangqiang@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (6, 'js002', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '赵芳', '13800000005', 'zhaofang@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (7, 'js003', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '刘伟', '13800000006', 'liuwei@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (8, 'js004', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '陈雪', '13800000007', 'chenxue@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (9, 'js005', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '吴磊', '13800000008', 'wulei@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (10, 'js006', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '周敏', '13800000009', 'zhoumin@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (11, 'js007', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '徐亮', '13800000010', 'xuliang@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (12, 'js008', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '李华', '13800000011', 'lihua@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (13, 'js009', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '王丽', '13800000012', 'wangli@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (14, 'js010', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '陈静', '13800000013', 'chenjing@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (15, 'js011', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '杨光', '13800000014', 'yangguang@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (16, 'js012', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '马超', '13800000015', 'machao@xtu.edu.cn', 1, NOW());
INSERT INTO `sys_user` VALUES (17, 'S001', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '张三', NULL, NULL, 1, NOW());
INSERT INTO `sys_user` VALUES (18, 'S002', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '李四', NULL, NULL, 1, NOW());
INSERT INTO `sys_user` VALUES (19, 'S003', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '王五', NULL, NULL, 1, NOW());
INSERT INTO `sys_user` VALUES (20, 'S004', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '赵六', NULL, NULL, 1, NOW());
INSERT INTO `sys_user` VALUES (21, 'S005', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '孙七', NULL, NULL, 1, NOW());
INSERT INTO `sys_user` VALUES (22, 'S006', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '周八', NULL, NULL, 1, NOW());
INSERT INTO `sys_user` VALUES (23, 'S007', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '吴九', NULL, NULL, 1, NOW());
INSERT INTO `sys_user` VALUES (24, 'S008', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '郑十', NULL, NULL, 1, NOW());
INSERT INTO `sys_user` VALUES (25, 'S009', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '钱十一', NULL, NULL, 1, NOW());
INSERT INTO `sys_user` VALUES (26, 'S010', '$2a$10$xUq4REdF/1RYfmZdQvNZ5OprQ8o/mrFFyhc1Qa1Cf/3HxBBLR9Qje', '冯十二', NULL, NULL, 1, NOW());

-- ----------------------------
-- 用户-角色关联表（身份不变）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID 逻辑外键: sys_user.id',
  `role_id` bigint NOT NULL COMMENT '角色ID 逻辑外键: sys_role.id',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色多对多关联表' ROW_FORMAT = Dynamic;

INSERT INTO `sys_user_role` VALUES (1, 1);   -- admin       → ADMIN_SCHOOL
INSERT INTO `sys_user_role` VALUES (2, 2);   -- xy001 张明   → ADMIN_COLLEGE (计算机学院管理员)
INSERT INTO `sys_user_role` VALUES (2, 4);   -- xy001 张明   → TEACHER
INSERT INTO `sys_user_role` VALUES (3, 3);   -- xy002 李丽   → DIRECTOR_DEPT (计算机科学系主任)
INSERT INTO `sys_user_role` VALUES (3, 4);   -- xy002 李丽   → TEACHER
INSERT INTO `sys_user_role` VALUES (4, 3);   -- xy003 王芳   → DIRECTOR_DEPT (会计系主任，商学院)
INSERT INTO `sys_user_role` VALUES (4, 4);   -- xy003 王芳   → TEACHER
INSERT INTO `sys_user_role` VALUES (5, 4);   -- js001 王强   → TEACHER
INSERT INTO `sys_user_role` VALUES (6, 4);   -- js002 赵芳   → TEACHER
INSERT INTO `sys_user_role` VALUES (7, 4);   -- js003 刘伟   → TEACHER
INSERT INTO `sys_user_role` VALUES (8, 4);   -- js004 陈雪   → TEACHER
INSERT INTO `sys_user_role` VALUES (9, 4);   -- js005 吴磊   → TEACHER
INSERT INTO `sys_user_role` VALUES (10, 4);  -- js006 周敏   → TEACHER
INSERT INTO `sys_user_role` VALUES (11, 4);  -- js007 徐亮   → TEACHER
INSERT INTO `sys_user_role` VALUES (12, 4);  -- js008 李华   → TEACHER
INSERT INTO `sys_user_role` VALUES (13, 4);  -- js009 王丽   → TEACHER
INSERT INTO `sys_user_role` VALUES (14, 4);  -- js010 陈静   → TEACHER
INSERT INTO `sys_user_role` VALUES (15, 4);  -- js011 杨光   → TEACHER
INSERT INTO `sys_user_role` VALUES (16, 4);  -- js012 马超   → TEACHER
INSERT INTO `sys_user_role` VALUES (17, 5);  -- S001 张三   → STUDENT
INSERT INTO `sys_user_role` VALUES (18, 5);  -- S002 李四   → STUDENT
INSERT INTO `sys_user_role` VALUES (19, 5);  -- S003 王五   → STUDENT
INSERT INTO `sys_user_role` VALUES (20, 5);  -- S004 赵六   → STUDENT
INSERT INTO `sys_user_role` VALUES (21, 5);  -- S005 孙七   → STUDENT
INSERT INTO `sys_user_role` VALUES (22, 5);  -- S006 周八   → STUDENT
INSERT INTO `sys_user_role` VALUES (23, 5);  -- S007 吴九   → STUDENT
INSERT INTO `sys_user_role` VALUES (24, 5);  -- S008 郑十   → STUDENT
INSERT INTO `sys_user_role` VALUES (25, 5);  -- S009 钱十一 → STUDENT
INSERT INTO `sys_user_role` VALUES (26, 5);  -- S010 冯十二 → STUDENT

-- ----------------------------
-- 教师表（身份、职称、学院归属不变）
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '关联用户 逻辑外键: sys_user.id',
  `teacher_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工号',
  `title_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职称名称',
  `title_level` int NULL DEFAULT NULL COMMENT '职称等级 1-助教 2-讲师 3-副教授 4-教授',
  `college_id` bigint NULL DEFAULT NULL COMMENT '所属学院 逻辑外键: college.id',
  `is_middle_leader` tinyint NULL DEFAULT 0 COMMENT '是否中层干部 0-否 1-是',
  `max_hours` int NULL DEFAULT NULL COMMENT '最大学时',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_teacher_no`(`teacher_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教师表' ROW_FORMAT = Dynamic;

-- === 计算机与网络工程学院 (college_id=1) ===
INSERT INTO `teacher` VALUES (1, 2, 'XY001', '教授', 4, 1, 1, 14, 1);   -- 张明  学院管理员+教授+中层
INSERT INTO `teacher` VALUES (2, 3, 'XY002', '副教授', 3, 1, 0, 12, 1);  -- 李丽  教研室主任+副教授
INSERT INTO `teacher` VALUES (4, 5, 'JS001', '教授', 4, 1, 1, 12, 1);    -- 王强  教授+中层
INSERT INTO `teacher` VALUES (5, 6, 'JS002', '副教授', 3, 1, 0, 12, 1);   -- 赵芳  副教授
INSERT INTO `teacher` VALUES (6, 7, 'JS003', '讲师', 2, 1, 0, 10, 1);     -- 刘伟  讲师 ⚠️有教学事故
INSERT INTO `teacher` VALUES (7, 8, 'JS004', '讲师', 2, 1, 0, 10, 1);     -- 陈雪  讲师
INSERT INTO `teacher` VALUES (8, 9, 'JS005', '副教授', 3, 1, 1, 12, 1);   -- 吴磊  副教授+中层
INSERT INTO `teacher` VALUES (9, 10, 'JS006', '助教', 1, 1, 0, 8, 1);     -- 周敏  助教（唯一）
INSERT INTO `teacher` VALUES (10, 11, 'JS007', '讲师', 2, 1, 0, 10, 1);   -- 徐亮  讲师

-- === 商学院 (college_id=2) ===
INSERT INTO `teacher` VALUES (3, 4, 'XY003', '副教授', 3, 2, 0, 12, 1);   -- 王芳  教研室主任+副教授
INSERT INTO `teacher` VALUES (11, 12, 'JS008', '副教授', 3, 2, 0, 12, 1);  -- 李华  副教授
INSERT INTO `teacher` VALUES (12, 13, 'JS009', '讲师', 2, 2, 0, 10, 1);    -- 王丽  讲师
INSERT INTO `teacher` VALUES (15, 16, 'JS012', '助教', 1, 2, 0, 8, 1);     -- 马超  助教

-- === 外国语学院 (college_id=3) ===
INSERT INTO `teacher` VALUES (13, 14, 'JS010', '教授', 4, 3, 0, 12, 1);   -- 陈静  教授
INSERT INTO `teacher` VALUES (14, 15, 'JS011', '副教授', 3, 3, 1, 12, 1);  -- 杨光  副教授+中层

-- ----------------------------
-- 学生表（不变）
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '关联用户 逻辑外键: sys_user.id',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `class_id` bigint NULL DEFAULT NULL COMMENT '班级 逻辑外键: class.id',
  `college_id` bigint NULL DEFAULT NULL COMMENT '学院 逻辑外键: college.id',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_no`(`student_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_college_id`(`college_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生表' ROW_FORMAT = Dynamic;

INSERT INTO `student` VALUES (1, 17, 'S001', 1, 1, '2022', 1);
INSERT INTO `student` VALUES (2, 18, 'S002', 2, 1, '2022', 1);
INSERT INTO `student` VALUES (3, 19, 'S003', 3, 1, '2022', 1);
INSERT INTO `student` VALUES (4, 20, 'S004', 4, 1, '2022', 1);
INSERT INTO `student` VALUES (5, 21, 'S005', 5, 1, '2022', 1);
INSERT INTO `student` VALUES (6, 22, 'S006', 6, 2, '2022', 1);
INSERT INTO `student` VALUES (7, 23, 'S007', 7, 2, '2022', 1);
INSERT INTO `student` VALUES (8, 24, 'S008', 8, 2, '2022', 1);
INSERT INTO `student` VALUES (9, 25, 'S009', 9, 3, '2022', 1);
INSERT INTO `student` VALUES (10, 26, 'S010', 10, 3, '2022', 1);

-- ============================================================
-- 第三部分：全局规则与教学事故（保留，让规则校验仍然生效）
-- ============================================================

-- ----------------------------
-- 教学规则表（全局规则 + 可按学院/课程扩展）
-- ----------------------------
DROP TABLE IF EXISTS `teaching_rule`;
CREATE TABLE `teaching_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则名称',
  `rule_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规则类型',
  `rule_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则值',
  `enabled` tinyint NULL DEFAULT 1 COMMENT '是否启用 0-禁用 1-启用',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `config_json` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '灵活配置项(JSON)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教学规则表' ROW_FORMAT = Dynamic;

-- 全局四大规则
INSERT INTO `teaching_rule` VALUES (1, '主讲课程数量上限', 'MAX_COURSE_LIMIT', '2', 1, '每位教师每学期主讲课程不超过2门', NULL);
INSERT INTO `teaching_rule` VALUES (2, '中层干部周学时上限', 'LEADER_HOUR_LIMIT', '4', 1, '中层干部每周主讲学时不超过4学时', NULL);
INSERT INTO `teaching_rule` VALUES (3, '事故教师禁止主讲', 'ACCIDENT_BAN', '1', 1, '教学事故教师下学期禁止主讲', NULL);
INSERT INTO `teaching_rule` VALUES (4, '主干课程职称要求', 'CORE_COURSE_TITLE', '3', 1, '主干课程主讲教师职称不低于副教授', NULL);
-- 补充规则
INSERT INTO `teaching_rule` VALUES (5, '普通教师最大周学时', 'MAX_HOUR_NORMAL', '12', 1, '普通教师周学时不超过12', NULL);
INSERT INTO `teaching_rule` VALUES (6, '中层干部最大周学时', 'MAX_HOUR_MIDDLE', '14', 1, '中层干部周学时不超过14', NULL);
INSERT INTO `teaching_rule` VALUES (7, '主讲教师最低职称', 'TEACHER_LEVEL', '2', 1, '主讲教师最低职称为讲师', NULL);

-- 以下为 PLANT_MIN_TITLE 类型的规则，供学院管理员通过新接口覆盖为 COURSE_ 前缀规则时参考
INSERT INTO `teaching_rule` VALUES (8, 'Java程序设计-最低职称', 'PLAN_MIN_TITLE', '2', 1, 'Java程序设计最低讲师', NULL);
INSERT INTO `teaching_rule` VALUES (9, '数据结构-最低职称', 'PLAN_MIN_TITLE', '3', 1, '数据结构最低副教授', NULL);
INSERT INTO `teaching_rule` VALUES (10, '基础会计-最低职称', 'PLAN_MIN_TITLE', '3', 1, '基础会计最低副教授', NULL);
INSERT INTO `teaching_rule` VALUES (11, '计算机网络-最低职称', 'PLAN_MIN_TITLE', '2', 1, '计算机网络最低讲师', NULL);
INSERT INTO `teaching_rule` VALUES (12, '大学英语-最低职称', 'PLAN_MIN_TITLE', '2', 1, '大学英语最低讲师', NULL);

-- ----------------------------
-- 教学事故表（保留，让规则④正常生效）
-- ----------------------------
DROP TABLE IF EXISTS `teaching_accident`;
CREATE TABLE `teaching_accident` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `teacher_id` bigint NOT NULL COMMENT '教师 逻辑外键: teacher.id',
  `semester` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '事故等级',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `is_serious` tinyint NULL DEFAULT 0 COMMENT '是否严重 0-否 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教学事故表' ROW_FORMAT = Dynamic;

-- 刘伟（JS003, teacher_id=6）的3条严重事故 → 规则④会拦截他的主讲申请
INSERT INTO `teaching_accident` VALUES (1, 6, '2024-2025-2', '严重', '无故缺课', 1, '2025-01-15 10:00:00');
INSERT INTO `teaching_accident` VALUES (2, 6, '2025-2026-1', '严重', '期末考试漏题', 1, '2025-05-20 14:30:00');
INSERT INTO `teaching_accident` VALUES (3, 6, '2025-2026-1', '严重', '教学态度恶劣被学生投诉', 1, '2025-06-10 09:00:00');

-- ============================================================
-- 第四部分：公告（保留系统通知）
-- ============================================================
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '公告内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'SYSTEM' COMMENT '类型 SYSTEM-系统 COLLEGE-学院 DEPT-部门',
  `target_role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ALL' COMMENT '目标角色 ALL-全部 ADMIN-管理端 TEACHER-教师 STUDENT-学生',
  `publisher_id` bigint NULL DEFAULT NULL COMMENT '发布人 逻辑外键: sys_user.id',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_target_role`(`target_role` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

INSERT INTO `notice` VALUES (1, '2025-2026-1学期教学计划开始申报', '各位老师：2025-2026-1学期教学计划现已开始申报，请各学院在6月30日前完成申报工作。', 'SYSTEM', 'ALL', 1, NOW(), NOW());
INSERT INTO `notice` VALUES (2, '关于开展期中教学检查的通知', '学校将于第10周开展期中教学检查，请各位教师做好准备。', 'SYSTEM', 'TEACHER', 1, NOW(), NOW());
INSERT INTO `notice` VALUES (3, '计算机学院教研活动安排', '本周三下午2:30在计算中心会议室开展教研活动，请全体教师参加。', 'COLLEGE', 'TEACHER', 2, NOW(), NOW());
INSERT INTO `notice` VALUES (4, '商学院期末考试安排通知', '请各教研室主任于12月20日前提交期末考试命题。', 'COLLEGE', 'TEACHER', 4, NOW(), NOW());
INSERT INTO `notice` VALUES (5, '关于寒假实验室安全工作的通知', '各实验室请在放假前进行安全检查，确保水电门窗关闭。', 'SYSTEM', 'ADMIN', 1, NOW(), NOW());
INSERT INTO `notice` VALUES (6, '英语四六级考前辅导通知', '外国语学院将于6月10日举办英语四六级考前辅导讲座，欢迎同学们参加。', 'COLLEGE', 'STUDENT', 14, NOW(), NOW());

-- ============================================================
-- 第五部分：教学计划（★★★ 全部重置为 status=0 草稿状态 ★★★）
--   与原数据相比：所有 status 值清为 0
--   学期统一使用 2025-2026-1
--   覆盖全部 3 个学院、12 门课程、10 个班级
-- ============================================================
DROP TABLE IF EXISTS `teaching_plan`;
CREATE TABLE `teaching_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `semester` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `college_id` bigint NOT NULL COMMENT '学院 逻辑外键: college.id',
  `course_id` bigint NOT NULL COMMENT '课程 逻辑外键: course.id',
  `class_id` bigint NOT NULL COMMENT '班级 逻辑外键: class.id',
  `total_hours` int NULL DEFAULT NULL COMMENT '总学时',
  `weekly_hours` int NULL DEFAULT NULL COMMENT '周学时',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0-草稿 1-待学院配置 2-待教研室协调 3-待学院审核 4-待教务处排课 5-已发布',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人 逻辑外键: sys_user.id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教学计划表' ROW_FORMAT = Dynamic;

-- === 计算机与网络工程学院 (college_id=1) ===
-- Java程序设计 → 软件工程2201, 软件工程2202, 计算机2202
INSERT INTO `teaching_plan` VALUES (1, '2025-2026-1', 1, 1, 1, 64, 4, 0, 1);   -- Java → 软件2201 ★
INSERT INTO `teaching_plan` VALUES (9, '2025-2026-1', 1, 1, 4, 64, 4, 0, 1);   -- Java → 计算机2202

-- 数据结构 → 软件工程2201
INSERT INTO `teaching_plan` VALUES (2, '2025-2026-1', 1, 2, 1, 48, 3, 0, 1);   -- 数据结构 → 软件2201 ★核心课

-- 计算机网络 → 计算机2201, 网络工程2201
INSERT INTO `teaching_plan` VALUES (3, '2025-2026-1', 1, 3, 3, 48, 3, 0, 1);   -- 计算机网络 → 计算机2201
INSERT INTO `teaching_plan` VALUES (10, '2025-2026-1', 1, 3, 5, 48, 3, 0, 1);  -- 计算机网络 → 网络2201

-- 操作系统 → 计算机2201
INSERT INTO `teaching_plan` VALUES (4, '2025-2026-1', 1, 5, 3, 48, 3, 0, 1);   -- 操作系统 → 计算机2201 ★核心课

-- 数据库原理 → 软件工程2202
INSERT INTO `teaching_plan` VALUES (5, '2025-2026-1', 1, 4, 2, 48, 3, 0, 1);   -- 数据库原理 → 软件2202 ★核心课

-- 软件工程 → 软件工程2202
INSERT INTO `teaching_plan` VALUES (6, '2025-2026-1', 1, 6, 2, 48, 3, 0, 1);   -- 软件工程 → 软件2202

-- 人工智能导论 → 计算机2202, 网络工程2201
INSERT INTO `teaching_plan` VALUES (7, '2025-2026-1', 1, 7, 4, 32, 2, 0, 1);   -- 人工智能 → 计算机2202
INSERT INTO `teaching_plan` VALUES (8, '2025-2026-1', 1, 7, 5, 32, 2, 0, 1);   -- 人工智能 → 网络2201

-- === 商学院 (college_id=2) ===
INSERT INTO `teaching_plan` VALUES (11, '2025-2026-1', 2, 8, 6, 48, 3, 0, 1);   -- 基础会计 → 会计2201 ★核心课
INSERT INTO `teaching_plan` VALUES (12, '2025-2026-1', 2, 10, 7, 48, 3, 0, 1);  -- 财务管理 → 会计2202 ★核心课
INSERT INTO `teaching_plan` VALUES (13, '2025-2026-1', 2, 9, 8, 32, 2, 0, 1);   -- 市场营销学 → 市营2201

-- === 外国语学院 (college_id=3) ===
INSERT INTO `teaching_plan` VALUES (14, '2025-2026-1', 3, 11, 9, 64, 4, 0, 1);   -- 大学英语 → 英语2201
INSERT INTO `teaching_plan` VALUES (15, '2025-2026-1', 3, 12, 10, 32, 2, 0, 1);  -- 英语口语 → 英语2202

-- ============================================================
-- 第六部分：下游业务表（★ 全部清空 — 等待从头开始走流程 ★）
-- ============================================================

-- ----------------------------
-- 教师授课申请表（清空）
-- ----------------------------
DROP TABLE IF EXISTS `teaching_application`;
CREATE TABLE `teaching_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `teacher_id` bigint NOT NULL COMMENT '教师 逻辑外键: teacher.id',
  `teaching_plan_id` bigint NOT NULL COMMENT '教学计划 逻辑外键: teaching_plan.id',
  `priority_level` int NULL DEFAULT 0 COMMENT '优先级',
  `apply_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请说明',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0-草稿 1-已提交 2-教研室通过 3-驳回 4-学院通过 5-驳回',
  `submit_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教师授课申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 审核记录表（清空）
-- ----------------------------
DROP TABLE IF EXISTS `approval_record`;
CREATE TABLE `approval_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `application_id` bigint NOT NULL COMMENT '申请ID 逻辑外键: teaching_application.id',
  `approver_id` bigint NOT NULL COMMENT '审核人 逻辑外键: sys_user.id',
  `approver_role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核角色',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审核记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 教学任务表（清空）
-- ----------------------------
DROP TABLE IF EXISTS `teaching_task`;
CREATE TABLE `teaching_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `teacher_id` bigint NOT NULL COMMENT '教师 逻辑外键: teacher.id',
  `course_id` bigint NOT NULL COMMENT '课程 逻辑外键: course.id',
  `class_id` bigint NOT NULL COMMENT '班级 逻辑外键: class.id',
  `semester` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `role_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主讲/辅讲',
  `total_hours` int NULL DEFAULT NULL COMMENT '总学时',
  `weekly_hours` int NULL DEFAULT NULL COMMENT '周学时',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教学任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 课表表（清空）
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `semester` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `class_id` bigint NOT NULL COMMENT '班级 逻辑外键: class.id',
  `course_id` bigint NOT NULL COMMENT '课程 逻辑外键: course.id',
  `teacher_id` bigint NOT NULL COMMENT '教师 逻辑外键: teacher.id',
  `time_slot_id` bigint NOT NULL COMMENT '时间片 逻辑外键: time_slot.id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课表表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 考试安排表（清空）
-- ----------------------------
DROP TABLE IF EXISTS `exam_plan`;
CREATE TABLE `exam_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `course_id` bigint NOT NULL COMMENT '课程 逻辑外键: course.id',
  `class_id` bigint NOT NULL COMMENT '班级 逻辑外键: class.id',
  `exam_date` date NULL DEFAULT NULL COMMENT '考试日期',
  `start_time` time NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` time NULL DEFAULT NULL COMMENT '结束时间',
  `room` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '考试地点',
  `invigilator_id` bigint NULL DEFAULT NULL COMMENT '监考教师 逻辑外键: teacher.id',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0-未发布 1-已发布',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_semester`(`semester` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_invigilator_id`(`invigilator_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试安排表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 教室安排表（清空）
-- ----------------------------
DROP TABLE IF EXISTS `room_plan`;
CREATE TABLE `room_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `room_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教室名称',
  `capacity` int NULL DEFAULT NULL COMMENT '容量',
  `weekday` tinyint NULL DEFAULT NULL COMMENT '星期',
  `section_no` tinyint NULL DEFAULT NULL COMMENT '节次',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程 逻辑外键: course.id',
  `class_id` bigint NULL DEFAULT NULL COMMENT '班级 逻辑外键: class.id',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0-空闲 1-已占用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_semester`(`semester` ASC) USING BTREE,
  INDEX `idx_room_name`(`room_name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教室安排表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 任务池表（清空）
-- ----------------------------
DROP TABLE IF EXISTS `task_pool`;
CREATE TABLE `task_pool` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `course_id` bigint NOT NULL COMMENT '课程 逻辑外键: course.id',
  `class_id` bigint NOT NULL COMMENT '班级 逻辑外键: class.id',
  `total_hours` int NULL DEFAULT NULL COMMENT '总学时',
  `weekly_hours` int NULL DEFAULT NULL COMMENT '周学时',
  `required_title_level` int NULL DEFAULT NULL COMMENT '最低职称要求',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0-待分配 1-已分配 2-已完成',
  `assigned_teacher_id` bigint NULL DEFAULT NULL COMMENT '已分配教师 逻辑外键: teacher.id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_semester`(`semester` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_assigned_teacher`(`assigned_teacher_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务池表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
