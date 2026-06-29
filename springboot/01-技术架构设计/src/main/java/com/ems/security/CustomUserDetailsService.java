package com.ems.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.SysRole;
import com.ems.entity.SysUser;
import com.ems.entity.Teacher;
import com.ems.entity.Student;
import com.ems.mapper.SysRoleMapper;
import com.ems.mapper.SysUserMapper;
import com.ems.mapper.TeacherMapper;
import com.ems.mapper.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private StudentMapper studentMapper;

    private static final Map<String, Integer> ROLE_LEVEL_MAP = new HashMap<>();
    static {
        ROLE_LEVEL_MAP.put("ADMIN_SCHOOL", 0);
        ROLE_LEVEL_MAP.put("ADMIN_COLLEGE", 1);
        ROLE_LEVEL_MAP.put("DIRECTOR_DEPT", 2);
        ROLE_LEVEL_MAP.put("TEACHER", 3);
        ROLE_LEVEL_MAP.put("STUDENT", 4);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            log.warn("登录失败: 用户[{}]不存在", username);
            throw new UsernameNotFoundException("用户不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("登录失败: 用户[{}]账号已禁用", username);
            throw new UsernameNotFoundException("账号已禁用");
        }

        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getId());
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setRoles(roleCodes);
        loginUser.setRoleLevel(roleCodes.stream()
                .mapToInt(r -> ROLE_LEVEL_MAP.getOrDefault(r, 99))
                .min().orElse(99));

        // 教师角色：查询关联teacher信息
        if (roleCodes.contains("TEACHER") || roleCodes.contains("DIRECTOR_DEPT")) {
            Teacher teacher = teacherMapper.selectOne(
                    new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, user.getId()));
            if (teacher != null) {
                loginUser.setTeacherId(teacher.getId());
                loginUser.setCollegeId(teacher.getCollegeId());
                loginUser.setDepartmentId(teacher.getDepartmentId());
            }
        }

        // 学院管理员：查询学院关系
        if (roleCodes.contains("ADMIN_COLLEGE")) {
            Teacher teacher = teacherMapper.selectOne(
                    new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, user.getId()));
            if (teacher != null) {
                loginUser.setCollegeId(teacher.getCollegeId());
            }
        }

        // 学生角色：查询关联学生信息
        if (roleCodes.contains("STUDENT")) {
            Student student = studentMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Student>()
                            .eq(Student::getUserId, user.getId()));
            if (student != null) {
                loginUser.setStudentId(student.getId());
                loginUser.setCollegeId(student.getCollegeId());
                loginUser.setClassId(student.getClassId());
            }
        }

        log.info("用户[{}]加载成功, 角色={}, 教师ID={}, 学生ID={}",
                username, roleCodes, loginUser.getTeacherId(), loginUser.getStudentId());
        return loginUser;
    }
}
