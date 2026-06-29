# 01 — 技术架构设计（四层架构 + JWT 安全 + RBAC 权限）

## 功能描述

本模块是整个系统的技术底座，负责提供：

- **四层 B/S 架构**：表现层(Vue) → 接入层(Nginx) → 应用层(Spring Boot) → 数据层(MySQL)
- **JWT 令牌认证**：用户登录后签发 JWT，后续请求通过 Token 鉴权
- **RBAC 权限控制**：基于 URL 前缀 + 角色等级的五角色权限模型
- **统一异常处理**：全局异常捕获并返回统一格式响应
- **跨域配置**：前后端分离开发跨域请求支持
- **API 文档**：Knife4j 自动生成 Swagger 文档
- **MyBatis-Plus 集成**：简化数据库操作

## 文件清单

| 文件路径 | 说明 |
|----------|------|
| `pom.xml` | Maven 构建配置。JDK 17、Spring Boot 3.2.5、MyBatis-Plus 3.5.7、JJWT 0.12.5、Knife4j 4.5.0 |
| `src/main/resources/application.yml` | 应用核心配置。MySQL 数据源(druid)、JWT 密钥/过期时间、端口 9090 |
| `src/main/resources/logback-spring.xml` | Logback 日志配置。控制台彩色输出、文件滚动策略 |
| `src/main/java/com/ems/EmsApplication.java` | Spring Boot 启动入口。@SpringBootApplication + @MapperScan |
| **common/** | |
| `common/Result.java` | 统一 API 响应封装。泛型 `Result<T>`(code+message+data)，静态工厂 success()/error() |
| `common/GlobalExceptionHandler.java` | 全局异常处理器。@ControllerAdvice 捕获参数校验/业务/运行时异常 |
| `common/JwtUtil.java` | JWT 令牌工具。JJWT 0.12.5 实现生成(generateToken)/解析(parseToken)/校验(validateToken)，HMAC-SHA 算法 |
| `common/SemesterUtils.java` | 学期推算工具。根据输入学期推算下学期（如 2025-2026-1 → 2025-2026-2） |
| **config/** | |
| `config/CorsConfig.java` | 跨域配置。WebMvcConfigurer 允许所有来源/方法/头 |
| `config/Knife4jConfig.java` | API 文档配置。分组扫描 controller 包，OpenAPI 3.0 |
| `config/MyBatisPlusConfig.java` | MyBatis-Plus 配置。@MapperScan + 分页插件 PaginationInnerInterceptor |
| `config/SecurityConfig.java` | Spring Security 核心。三门户 URL 前缀权限控制 + 无状态 Session + BCrypt 密码 |
| **security/** | |
| `security/CustomUserDetailsService.java` | 用户加载服务。根据用户名查库构建登录用户 |
| `security/LoginUser.java` | 登录用户上下文。含 userId/roles/collegeId/roleLevel/teacherId/studentId |
| `security/JwtAuthenticationFilter.java` | JWT 认证过滤器。OncePerRequestFilter 提取 Token → 解析 → 设置 SecurityContext |

## 核心架构

### 四层架构
```
表现层(Vue3) → 接入层(Nginx :80) → 应用层(Spring Boot :9090) → 数据层(MySQL 8.0)
```

### RBAC 权限模型
| 角色 | 等级 | 门户 | URL 前缀 |
|------|------|------|----------|
| ADMIN_SCHOOL(教务处) | 0 | 管理端 | /api/admin/** |
| ADMIN_COLLEGE(学院) | 1 | 管理端 | /api/admin/** |
| DIRECTOR_DEPT(教研室) | 2 | 管理端 | /api/admin/** |
| TEACHER(教师) | 3 | 教师端 | /api/teacher/** |
| STUDENT(学生) | 4 | 学生端 | /api/student/** |
