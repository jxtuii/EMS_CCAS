package com.ems.security;

import com.ems.common.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(userId);
            loginUser.setUsername(username);
            loginUser.setRoles(roles);
            // Compute roleLevel from roles
            java.util.Map<String, Integer> roleLevelMap = java.util.Map.of(
                    "ADMIN_SCHOOL", 0, "ADMIN_COLLEGE", 1, "DIRECTOR_DEPT", 2, "TEACHER", 3);
            loginUser.setRoleLevel(roles.stream()
                    .mapToInt(r -> roleLevelMap.getOrDefault(r, 99))
                    .min().orElse(99));
            if (claims.get("teacherId") != null) {
                loginUser.setTeacherId(claims.get("teacherId", Long.class));
            }
            if (claims.get("studentId") != null) {
                loginUser.setStudentId(claims.get("studentId", Long.class));
            }
            if (claims.get("classId") != null) {
                loginUser.setClassId(claims.get("classId", Long.class));
            }
            if (claims.get("collegeId") != null) {
                loginUser.setCollegeId(claims.get("collegeId", Long.class));
            }
            if (claims.get("departmentId") != null) {
                loginUser.setDepartmentId(claims.get("departmentId", Long.class));
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
