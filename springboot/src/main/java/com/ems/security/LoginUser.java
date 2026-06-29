package com.ems.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LoginUser implements UserDetails {
    private Long userId;
    private String username;
    private String password;
    private List<String> roles;
    /** 角色级别：全校=0,本院=1,本室=2,自己=3 */
    private Integer roleLevel;
    /** 关联的teacher_id（教师角色时有效） */
    private Long teacherId;
    /** 关联的student_id（学生角色时有效） */
    private Long studentId;
    /** 所属学院ID */
    private Long collegeId;
    /** 所属教研室ID */
    private Long departmentId;
    /** 学生班级ID */
    private Long classId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public List<String> getRoleCodes() {
        return roles;
    }
}
