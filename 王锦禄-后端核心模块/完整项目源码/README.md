# 完整项目源码（可编译运行）

## 说明

本目录包含完整的 Spring Boot Maven 项目，可直接编译运行。

## 目录结构

```
springboot/
├── pom.xml                     # Maven 构建文件
├── src/main/java/com/ems/
│   ├── EmsApplication.java     # 启动入口
│   ├── common/                  # 通用工具（Result/JwtUtil/异常处理）
│   ├── config/                  # 配置类（CORS/Security/MyBatis-Plus/Knife4j）
│   ├── security/                # JWT 认证过滤器 + RBAC
│   ├── entity/                  # 数据库实体
│   ├── mapper/                  # MyBatis-Plus Mapper
│   ├── service/                 # 业务接口
│   ├── service/impl/            # 业务实现
│   ├── dto/                     # 数据传输对象
│   └── controller/admin/        # REST API 控制器
└── src/main/resources/
    ├── application.yml          # 应用配置
    └── logback-spring.xml       # 日志配置
sql/
├── newemscss.sql               # 完整建库建表脚本
├── newemscss_reset.sql          # 数据重置脚本
└── migrate_time_slots.sql       # 时间片迁移脚本
```

## 启动方式

```bash
cd springboot
mvn spring-boot:run
# 服务默认端口 9090
```

数据库需先执行 `sql/newemscss.sql` 初始化。
