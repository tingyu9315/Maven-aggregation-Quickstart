package com.wd.maven.aggregation;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class AggregationProjectGenerator {

    private static final Logger LOG = Logger.getInstance(AggregationProjectGenerator.class);

    /**
     * 生成项目，支持单模块DDD架构和多模块架构
     * @param project 项目
     * @param groupId 组织ID
     * @param artifactId 项目ID
     * @param version 版本
     * @param modules 模块列表（多模块时使用）
     * @param javaVersion Java版本
     * @param addDependencies 是否添加常用依赖
     */
    public void generateProject(Project project, String groupId, String artifactId, String version, String[] modules, String javaVersion, boolean addDependencies) {
        // 默认使用多模块模式
        generateProject(project, groupId, artifactId, version, modules, javaVersion, addDependencies, false, "");
    }

    /**
     * 生成项目，支持单模块DDD架构和多模块架构
     * @param project 项目
     * @param groupId 组织ID
     * @param artifactId 项目ID
     * @param version 版本
     * @param modules 模块列表（多模块时使用）
     * @param javaVersion Java版本
     * @param addDependencies 是否添加常用依赖
     * @param isSingleModule 是否为单模块模式
     * @param domainName 领域名称（单模块DDD架构时使用）
     */
    public void generateProject(Project project, String groupId, String artifactId, String version, String[] modules, 
                               String javaVersion, boolean addDependencies, boolean isSingleModule, String domainName) {
        String baseDir = project.getBasePath();
        if ( baseDir == null || baseDir.isEmpty()) {
            LOG.warn("Project base directory not found.");
            return;
        }

        Path projectPath = Paths.get(baseDir);
        
        try {
            if (isSingleModule) {
                // 单模块DDD架构
                LOG.info("开始生成单模块DDD架构项目: " + artifactId);
                
                // 1. 创建DDD目录结构
                createDddDirectoryStructure(projectPath, groupId, domainName);
                
                // 2. 写入单模块POM
                writeSingleModulePom(projectPath, groupId, artifactId, version, javaVersion, addDependencies);
                
                // 3. 生成辅助文件
                writeDddReadme(projectPath, artifactId, domainName);
                writeGitignore(projectPath);
                
                LOG.info("单模块DDD架构项目生成完成: " + artifactId);
            } else {
                // 多模块架构（原有逻辑）
                LOG.info("开始生成Maven聚合项目: " + artifactId);
                
                // 1. 先创建所有目录结构
                createAllDirectoryStructures(projectPath, artifactId, modules);
                
                // 2. 写入父 POM（包含完整的依赖管理）
                writeParentPomWithDependencies(projectPath, groupId, artifactId, version, modules, javaVersion, addDependencies);

                // 3. 写入子模块POM（包含依赖关系）
                for (String module : modules) {
                    String moduleName = artifactId + "-" + module;
                    Path modulePath = projectPath.resolve(moduleName);
                    writeModulePomWithDependencies(modulePath, groupId, artifactId, version, moduleName, modules, addDependencies);
                }

                // 4. 生成辅助文件
                writeReadme(projectPath, artifactId, modules);
                writeGitignore(projectPath);
                
                LOG.info("Maven聚合项目生成完成: " + artifactId);
            }
        } catch (Exception e) {
            LOG.error("生成项目时发生错误", e);
        }
    }

    /**
     * 创建所有目录结构（多模块）
     */
    private void createAllDirectoryStructures(Path projectPath, String artifactId, String[] modules) {
        for (String module : modules) {
            String moduleName = artifactId + "-" + module;
            Path modulePath = projectPath.resolve(moduleName);
            createDirectoryStructure(modulePath.resolve("src/main/java"));
            createDirectoryStructure(modulePath.resolve("src/test/java"));
            createDirectoryStructure(modulePath.resolve("src/main/resources"));
        }
    }

    /**
     * 创建DDD目录结构（单模块）
     */
    private void createDddDirectoryStructure(Path projectPath, String groupId, String domainName) {
        // 基础路径
        String basePackagePath = groupId.replace('.', '/') + "/" + domainName;
        Path mainJavaPath = projectPath.resolve("src/main/java").resolve(basePackagePath);
        Path testJavaPath = projectPath.resolve("src/test/java").resolve(basePackagePath);
        Path resourcesPath = projectPath.resolve("src/main/resources");
        
        // 创建基础目录结构
        createDirectoryStructure(mainJavaPath);
        createDirectoryStructure(testJavaPath);
        createDirectoryStructure(resourcesPath);
        
        // 创建DDD目录结构 - 应用层
        createDirectoryStructure(mainJavaPath.resolve("application/service"));
        createDirectoryStructure(mainJavaPath.resolve("application/dto"));
        
        // 创建DDD目录结构 - 领域层
        createDirectoryStructure(mainJavaPath.resolve("domain/model"));
        createDirectoryStructure(mainJavaPath.resolve("domain/service"));
        createDirectoryStructure(mainJavaPath.resolve("domain/event"));
        createDirectoryStructure(mainJavaPath.resolve("domain/repository"));
        
        // 创建DDD目录结构 - 基础设施层
        createDirectoryStructure(mainJavaPath.resolve("infrastructure/persistence"));
        createDirectoryStructure(mainJavaPath.resolve("infrastructure/message"));
        createDirectoryStructure(mainJavaPath.resolve("infrastructure/external"));
        
        // 创建DDD目录结构 - 接口层
        createDirectoryStructure(mainJavaPath.resolve("interface/controller/web"));
        createDirectoryStructure(mainJavaPath.resolve("interface/controller/rpc"));
    }

    /**
     * 写入单模块POM文件
     */
    private void writeSingleModulePom(Path projectPath, String groupId, String artifactId, String version, String javaVersion, boolean addDependencies) {
        // 根据Java版本选择合适的依赖版本
        String lombokVersion;
        String hutoolVersion;
        String logbackVersion;
        String slf4jVersion;
        String swaggerVersion;
        String junitVersion;
        
        int javaVersionNum = Integer.parseInt(javaVersion);
        
        // 根据不同Java版本选择兼容的依赖版本
        if (javaVersionNum == 11) {
            // Java 11 兼容版本
            lombokVersion = "1.18.24";  // 兼容Java 8-17
            hutoolVersion = "5.8.16";   // 兼容Java 8-17
            logbackVersion = "1.2.13";  // 兼容Java 11
            slf4jVersion = "1.7.36";    // 兼容Java 8-17
            swaggerVersion = "3.0.0";   // SpringFox 3.0.0 兼容Java 11
            junitVersion = "5.9.3";     // JUnit 5.9.3 兼容Java 11
        } else if (javaVersionNum >= 17 && javaVersionNum < 21) {
            // Java 17-20 兼容版本
            lombokVersion = "1.18.32";  // 支持Java 17
            hutoolVersion = "5.8.25";   // 支持Java 17
            logbackVersion = "1.5.6";   // 支持Java 17
            slf4jVersion = "2.0.12";    // 支持Java 17
            swaggerVersion = "3.0.0";   // SpringFox 3.0.0 兼容Java 17
            junitVersion = "5.10.0";    // JUnit 5.10.0 支持Java 17
        } else if (javaVersionNum >= 21) {
            // Java 21及以上版本
            lombokVersion = "1.18.34";  // 最新兼容版本
            hutoolVersion = "5.8.25";   // 支持Java 21
            logbackVersion = "1.5.6";   // 支持Java 21
            slf4jVersion = "2.0.12";    // 支持Java 21
            swaggerVersion = "3.0.0";   // SpringFox 3.0.0 兼容Java 21
            junitVersion = "5.10.1";    // JUnit 5.10.1 支持Java 21
        } else {
            // Java 8 兼容版本
            lombokVersion = "1.18.24";  // 兼容Java 8-17
            hutoolVersion = "5.8.16";   // 兼容Java 8-17
            logbackVersion = "1.2.11";  // 兼容Java 8
            slf4jVersion = "1.7.36";    // 兼容Java 8
            swaggerVersion = "3.0.0";   // SpringFox 3.0.0 兼容Java 8
            junitVersion = "5.8.2";     // JUnit 5.8.2 兼容Java 8
        }
        
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
               .append("\n<project xmlns=\"http://maven.apache.org/POM/4.0.0\"")
               .append("\n         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"")
               .append("\n         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">")
               .append("\n    <modelVersion>4.0.0</modelVersion>")
               .append("\n\n    <groupId>").append(groupId).append("</groupId>")
               .append("\n    <artifactId>").append(artifactId).append("</artifactId>")
               .append("\n    <version>").append(version).append("</version>")
               .append("\n    <packaging>jar</packaging>")
               .append("\n\n    <properties>")
               .append("\n        <maven.compiler.source>").append(javaVersion).append("</maven.compiler.source>")
               .append("\n        <maven.compiler.target>").append(javaVersion).append("</maven.compiler.target>")
               .append("\n        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>")
               .append("\n    </properties>")
               .append("\n");
        
        if (addDependencies) {
            content.append("    <dependencies>")
                   .append("\n        <!-- 代码简化与工具依赖 -->")
                   .append("\n        <dependency>")
                   .append("\n            <groupId>org.projectlombok</groupId>")
                   .append("\n            <artifactId>lombok</artifactId>")
                   .append("\n            <version>").append(lombokVersion).append("</version>")
                   .append("\n            <scope>provided</scope>")
                   .append("\n        </dependency>")
                   .append("\n        <dependency>")
                   .append("\n            <groupId>cn.hutool</groupId>")
                   .append("\n            <artifactId>hutool-all</artifactId>")
                   .append("\n            <version>").append(hutoolVersion).append("</version>")
                   .append("\n        </dependency>")
                   .append("\n        <!-- 日志体系依赖 -->")
                   .append("\n        <!-- SLF4J API - 日志门面接口 -->")
                   .append("\n        <dependency>")
                   .append("\n            <groupId>org.slf4j</groupId>")
                   .append("\n            <artifactId>slf4j-api</artifactId>")
                   .append("\n            <version>").append(slf4jVersion).append("</version>")
                   .append("\n        </dependency>")
                   .append("\n        <!-- Logback - SLF4J 的具体实现 -->")
                   .append("\n        <dependency>")
                   .append("\n            <groupId>ch.qos.logback</groupId>")
                   .append("\n            <artifactId>logback-classic</artifactId>")
                   .append("\n            <version>").append(logbackVersion).append("</version>")
                   .append("\n        </dependency>")
                   .append("\n        <!-- Swagger - API文档生成工具 -->")
                   .append("\n        <dependency>")
                   .append("\n            <groupId>io.springfox</groupId>")
                   .append("\n            <artifactId>springfox-boot-starter</artifactId>")
                   .append("\n            <version>").append(swaggerVersion).append("</version>")
                   .append("\n        </dependency>")
                   .append("\n        <!-- JUnit - 单元测试框架 -->")
                   .append("\n        <dependency>")
                   .append("\n            <groupId>org.junit.jupiter</groupId>")
                   .append("\n            <artifactId>junit-jupiter-api</artifactId>")
                   .append("\n            <version>").append(junitVersion).append("</version>")
                   .append("\n            <scope>test</scope>")
                   .append("\n        </dependency>")
                   .append("\n        <dependency>")
                   .append("\n            <groupId>org.junit.jupiter</groupId>")
                   .append("\n            <artifactId>junit-jupiter-engine</artifactId>")
                   .append("\n            <version>").append(junitVersion).append("</version>")
                   .append("\n            <scope>test</scope>")
                   .append("\n        </dependency>")
                   .append("\n    </dependencies>");
        }
        
        content.append("\n</project>");
        
        writeToFile(projectPath.resolve("pom.xml"), content.toString());
    }

    /**
     * 写入DDD架构的README文件
     */
    private void writeDddReadme(Path projectPath, String artifactId, String domainName) {
        StringBuilder content = new StringBuilder();
        content.append("# ").append(artifactId).append(" 单模块DDD架构项目\n\n")
               .append("这是一个基于领域驱动设计(DDD)的单模块项目模板。\n\n")
               .append("## 项目结构\n\n")
               .append("```\n")
               .append(artifactId).append("/\n")
               .append("├── src/main/java/com/example/").append(domainName).append("\n")
               .append("│   ├── application  # 应用层（整体流程控制）\n")
               .append("│   │   ├── service  # 应用服务\n")
               .append("│   │   └── dto      # 数据传输对象\n")
               .append("│   ├── domain       # 领域层（核心）\n")
               .append("│   │   ├── model    # 实体、值对象\n")
               .append("│   │   ├── service  # 领域服务（逻辑核心）\n")
               .append("│   │   ├── event    # 领域事件\n")
               .append("│   │   └── repository # 仓储接口\n")
               .append("│   ├── infrastructure # 基础设施层\n")
               .append("│   │   ├── persistence # 数据库实现\n")
               .append("│   │   ├── message     # 消息队列\n")
               .append("│   │   └── external    # 外部服务调用\n")
               .append("│   └── interface/controller # 用户接口层\n")
               .append("│       ├── web     # REST API\n")
               .append("│       └── rpc     # gRPC/Dubbo\n")
               .append("└── pom.xml         # Maven配置文件\n")
               .append("```\n\n")
               .append("## DDD架构说明\n\n")
               .append("- **应用层(application)**：负责协调领域对象完成用例，处理跨领域模型的操作\n")
               .append("- **领域层(domain)**：核心业务逻辑，包含实体、值对象、领域服务等\n")
               .append("- **基础设施层(infrastructure)**：提供技术支持，实现仓储接口、消息队列等\n")
               .append("- **接口层(interface)**：处理外部请求，转换为应用服务调用\n")
               .append("\n")
               .append("## 开始使用\n\n")
               .append("1. 根据需要修改`pom.xml`文件添加额外依赖\n")
               .append("2. 在相应包中添加源代码\n")
               .append("3. 使用以下命令构建项目：\n\n")
               .append("```bash\n")
               .append("mvn clean install\n")
               .append("```\n");
        
        writeToFile(projectPath.resolve("README.md"), content.toString());
    }

    /**
     * 写入父POM，包含完整的依赖管理
     */
    private void writeParentPomWithDependencies(Path projectPath, String groupId, String artifactId, String version, String[] modules, String javaVersion, boolean addDependencies) {
        StringBuilder moduleXml = new StringBuilder();
        for (String module : modules) {
            String moduleName = artifactId + "-" + module;
            moduleXml.append("        <module>").append(moduleName).append("</module>\n");
        }

        StringBuilder dependencyManagementXml = new StringBuilder();
        for (String module : modules) {
            dependencyManagementXml.append("            <dependency>\n")
                    .append("                <groupId>").append(groupId).append("</groupId>\n")
                    .append("                <artifactId>").append(artifactId).append("-").append(module).append("</artifactId>\n")
                    .append("                <version>${project.version}</version>\n")
                    .append("            </dependency>\n");
        }

        // 根据Java版本选择合适的依赖版本
        String lombokVersion;
        String hutoolVersion;
        String logbackVersion;
        String slf4jVersion;
        String swaggerVersion;
        String junitVersion;
        
        int javaVersionNum = Integer.parseInt(javaVersion);
        
        // 根据不同Java版本选择兼容的依赖版本
        if (javaVersionNum == 11) {
            // Java 11 兼容版本
            lombokVersion = "1.18.24";  // 兼容Java 8-17
            hutoolVersion = "5.8.16";   // 兼容Java 8-17
            logbackVersion = "1.2.13";  // 兼容Java 11
            slf4jVersion = "1.7.36";    // 兼容Java 8-17
            swaggerVersion = "3.0.0";   // SpringFox 3.0.0 兼容Java 11
            junitVersion = "5.9.3";     // JUnit 5.9.3 兼容Java 11
        } else if (javaVersionNum >= 17 && javaVersionNum < 21) {
            // Java 17-20 兼容版本
            lombokVersion = "1.18.32";  // 支持Java 17
            hutoolVersion = "5.8.25";   // 支持Java 17
            logbackVersion = "1.5.6";   // 支持Java 17
            slf4jVersion = "2.0.12";    // 支持Java 17
            swaggerVersion = "3.0.0";   // SpringFox 3.0.0 兼容Java 17
            junitVersion = "5.10.0";    // JUnit 5.10.0 支持Java 17
        } else if (javaVersionNum >= 21) {
            // Java 21及以上版本
            lombokVersion = "1.18.34";  // 最新兼容版本
            hutoolVersion = "5.8.25";   // 支持Java 21
            logbackVersion = "1.5.6";   // 支持Java 21
            slf4jVersion = "2.0.12";    // 支持Java 21
            swaggerVersion = "3.0.0";   // SpringFox 3.0.0 兼容Java 21
            junitVersion = "5.10.1";    // JUnit 5.10.1 支持Java 21
        } else {
            // Java 8 兼容版本
            lombokVersion = "1.18.24";  // 兼容Java 8-17
            hutoolVersion = "5.8.16";   // 兼容Java 8-17
            logbackVersion = "1.2.11";  // 兼容Java 8
            slf4jVersion = "1.7.36";    // 兼容Java 8
            swaggerVersion = "3.0.0";   // SpringFox 3.0.0 兼容Java 8
            junitVersion = "5.8.2";     // JUnit 5.8.2 兼容Java 8
        }
        
        // 构建常用依赖
        StringBuilder commonDependenciesXml = new StringBuilder();
        if (addDependencies) {
            commonDependenciesXml.append("            <!-- 代码简化与工具依赖 -->\n");
            // Lombok - 提供代码简化注解如@Getter、@Setter、@Slf4j等
            commonDependenciesXml.append("            <dependency>\n");
            commonDependenciesXml.append("                <groupId>org.projectlombok</groupId>\n");
            commonDependenciesXml.append("                <artifactId>lombok</artifactId>\n");
            commonDependenciesXml.append("                <version>").append(lombokVersion).append("</version>\n");
            commonDependenciesXml.append("                <scope>provided</scope>\n");
            commonDependenciesXml.append("            </dependency>\n");
            
            // Hutool - 提供丰富的Java工具方法
            commonDependenciesXml.append("            <dependency>\n");
            commonDependenciesXml.append("                <groupId>cn.hutool</groupId>\n");
            commonDependenciesXml.append("                <artifactId>hutool-all</artifactId>\n");
            commonDependenciesXml.append("                <version>").append(hutoolVersion).append("</version>\n");
            commonDependenciesXml.append("            </dependency>\n");
            
            commonDependenciesXml.append("            <!-- 日志体系依赖 -->\n");
            commonDependenciesXml.append("            <!-- SLF4J API - 日志门面接口 -->\n");
            // 注意：logback-classic 已经包含了 slf4j-api，但这里显式声明以确保版本一致性
            commonDependenciesXml.append("            <dependency>\n");
            commonDependenciesXml.append("                <groupId>org.slf4j</groupId>\n");
            commonDependenciesXml.append("                <artifactId>slf4j-api</artifactId>\n");
            commonDependenciesXml.append("                <version>").append(slf4jVersion).append("</version>\n");
            commonDependenciesXml.append("            </dependency>\n");
            
            commonDependenciesXml.append("            <!-- Logback - SLF4J 的具体实现 -->\n");
            commonDependenciesXml.append("            <!-- Lombok 的 @Slf4j 注解会使用 SLF4J API，而 Logback 是具体的日志实现 -->\n");
            commonDependenciesXml.append("            <dependency>\n");
            commonDependenciesXml.append("                <groupId>ch.qos.logback</groupId>\n");
            commonDependenciesXml.append("                <artifactId>logback-classic</artifactId>\n");
            commonDependenciesXml.append("                <version>").append(logbackVersion).append("</version>\n");
            commonDependenciesXml.append("            </dependency>\n");
            
            // 添加Swagger依赖 - API文档生成工具
            commonDependenciesXml.append("            <!-- Swagger - API文档生成工具 -->\n");
            commonDependenciesXml.append("            <dependency>\n");
            commonDependenciesXml.append("                <groupId>io.springfox</groupId>\n");
            commonDependenciesXml.append("                <artifactId>springfox-boot-starter</artifactId>\n");
            commonDependenciesXml.append("                <version>").append(swaggerVersion).append("</version>\n");
            commonDependenciesXml.append("            </dependency>\n");
            
            // 添加JUnit依赖 - 单元测试框架
            commonDependenciesXml.append("            <!-- JUnit - 单元测试框架 -->\n");
            commonDependenciesXml.append("            <dependency>\n");
            commonDependenciesXml.append("                <groupId>org.junit.jupiter</groupId>\n");
            commonDependenciesXml.append("                <artifactId>junit-jupiter-api</artifactId>\n");
            commonDependenciesXml.append("                <version>").append(junitVersion).append("</version>\n");
            commonDependenciesXml.append("                <scope>test</scope>\n");
            commonDependenciesXml.append("            </dependency>\n");
            commonDependenciesXml.append("            <dependency>\n");
            commonDependenciesXml.append("                <groupId>org.junit.jupiter</groupId>\n");
            commonDependenciesXml.append("                <artifactId>junit-jupiter-engine</artifactId>\n");
            commonDependenciesXml.append("                <version>").append(junitVersion).append("</version>\n");
            commonDependenciesXml.append("                <scope>test</scope>\n");
            commonDependenciesXml.append("            </dependency>\n");
        }
        
        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <groupId>" + groupId + "</groupId>\n" +
                "    <artifactId>" + artifactId + "</artifactId>\n" +
                "    <version>" + version + "</version>\n" +
                "    <packaging>pom</packaging>\n" +
                "\n" +
                "    <modules>\n" +
                moduleXml.toString() +
                "    </modules>\n" +
                "\n" +
                "    <properties>\n" +
                "        <maven.compiler.source>" + javaVersion + "</maven.compiler.source>\n" +
                "        <maven.compiler.target>" + javaVersion + "</maven.compiler.target>\n" +
                "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
                "    </properties>\n" +
                "\n" +
                "    <dependencyManagement>\n" +
                "        <dependencies>\n" +
                "            <!-- 项目内部模块依赖 -->\n" +
                dependencyManagementXml.toString();
        
        if (addDependencies) {
            content += "            \n" + commonDependenciesXml.toString();
        }
        
        content += "        </dependencies>\n" +
                "    </dependencyManagement>\n" +
                "</project>";

        writeToFile(projectPath.resolve("pom.xml"), content);
    }

    /**
     * 写入子模块POM，包含依赖关系
     */
    private void writeModulePomWithDependencies(Path modulePath, String groupId, String parentArtifactId, String version, String moduleName, String[] allModules, boolean addDependencies) {
        String module = moduleName.substring(parentArtifactId.length() + 1); // 获取模块名
        
        // 确保目录存在
        if (!Files.exists(modulePath)) {
            try {
                Files.createDirectories(modulePath);
            } catch (IOException e) {
                LOG.error("Failed to create module directory: {}", e.getMessage());
                return;
            }
        }
        
        // 构建依赖关系
        StringBuilder dependenciesXml = new StringBuilder("    <dependencies>\n");
        boolean hasDependencies = false;
        
        // 添加模块间依赖
        if (module.equals("api") && containsModule(allModules, "service")) {
            dependenciesXml.append("        <dependency>\n")
                    .append("            <groupId>").append(groupId).append("</groupId>\n")
                    .append("            <artifactId>").append(parentArtifactId).append("-service</artifactId>\n")
                    .append("        </dependency>\n");
            hasDependencies = true;
        } else if (module.equals("service") && containsModule(allModules, "mapper")) {
            dependenciesXml.append("        <dependency>\n")
                    .append("            <groupId>").append(groupId).append("</groupId>\n")
                    .append("            <artifactId>").append(parentArtifactId).append("-mapper</artifactId>\n")
                    .append("        </dependency>\n");
            hasDependencies = true;
        } else if (module.equals("mapper") && containsModule(allModules, "pojo")) {
            dependenciesXml.append("        <dependency>\n")
                    .append("            <groupId>").append(groupId).append("</groupId>\n")
                    .append("            <artifactId>").append(parentArtifactId).append("-pojo</artifactId>\n")
                    .append("        </dependency>\n");
            hasDependencies = true;
        } else if (module.equals("pojo") && containsModule(allModules, "common")) {
            dependenciesXml.append("        <dependency>\n")
                    .append("            <groupId>").append(groupId).append("</groupId>\n")
                    .append("            <artifactId>").append(parentArtifactId).append("-common</artifactId>\n")
                    .append("        </dependency>\n");
            hasDependencies = true;
        }
        
        // 在common模块中添加常用依赖（如果用户选择了添加依赖）
        if (addDependencies && module.equals("common")) {
            dependenciesXml.append("        <!-- 代码简化与工具依赖 -->\n")
                    .append("        <dependency>\n")
                    .append("            <groupId>org.projectlombok</groupId>\n")
                    .append("            <artifactId>lombok</artifactId>\n")
                    .append("            <scope>provided</scope>\n")
                    .append("        </dependency>\n")
                    .append("        <dependency>\n")
                    .append("            <groupId>cn.hutool</groupId>\n")
                    .append("            <artifactId>hutool-all</artifactId>\n")
                    .append("        </dependency>\n")
                    .append("        <!-- 日志体系依赖 -->\n")
                    .append("        <!-- SLF4J API - 日志门面接口 -->\n")
                    .append("        <dependency>\n")
                    .append("            <groupId>org.slf4j</groupId>\n")
                    .append("            <artifactId>slf4j-api</artifactId>\n")
                    .append("        </dependency>\n")
                    .append("        <!-- Logback - SLF4J 的具体实现 -->\n")
                    .append("        <dependency>\n")
                    .append("            <groupId>ch.qos.logback</groupId>\n")
                    .append("            <artifactId>logback-classic</artifactId>\n")
                    .append("        </dependency>\n")
                    .append("        <!-- Swagger - API文档生成工具 -->\n")
                    .append("        <dependency>\n")
                    .append("            <groupId>io.springfox</groupId>\n")
                    .append("            <artifactId>springfox-boot-starter</artifactId>\n")
                    .append("        </dependency>\n")
                    .append("        <!-- JUnit - 单元测试框架 -->\n")
                    .append("        <dependency>\n")
                    .append("            <groupId>org.junit.jupiter</groupId>\n")
                    .append("            <artifactId>junit-jupiter-api</artifactId>\n")
                    .append("            <scope>test</scope>\n")
                    .append("        </dependency>\n")
                    .append("        <dependency>\n")
                    .append("            <groupId>org.junit.jupiter</groupId>\n")
                    .append("            <artifactId>junit-jupiter-engine</artifactId>\n")
                    .append("            <scope>test</scope>\n")
                    .append("        </dependency>\n");
            hasDependencies = true;
        }
        
        // 只在有依赖时添加闭合标签
        if (hasDependencies) {
            dependenciesXml.append("    </dependencies>\n");
        } else {
            // 如果没有依赖，清空dependenciesXml
            dependenciesXml = new StringBuilder();
        }

        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "    <parent>\n" +
                "        <groupId>" + groupId + "</groupId>\n" +
                "        <artifactId>" + parentArtifactId + "</artifactId>\n" +
                "        <version>" + version + "</version>\n" +
                "    </parent>\n" +
                "    <artifactId>" + moduleName + "</artifactId>\n" +
                "    <packaging>jar</packaging>\n" +
                dependenciesXml.toString() +
                "</project>";

        writeToFile(modulePath.resolve("pom.xml"), content);
    }

    private void writeParentPom(Path projectPath, String groupId, String artifactId, String version, String[] modules, String javaVersion) {
        StringBuilder moduleXml = new StringBuilder();
        for (String module : modules) {
            // 使用父项目名-子模块名的格式
            String moduleName = artifactId + "-" + module;
            moduleXml.append("        <module>").append(moduleName).append("</module>\n");
        }

        StringBuilder dependencyManagementXml = new StringBuilder();
        for (String module : modules) {
            dependencyManagementXml.append("            <dependency>\n")
                    .append("                <groupId>").append(groupId).append("</groupId>\n")
                    .append("                <artifactId>").append(artifactId).append("-").append(module).append("</artifactId>\n") // 使用正确的 artifactId
                    .append("                <version>${project.version}</version>\n")
                    .append("            </dependency>\n");
        }

        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <groupId>" + groupId + "</groupId>\n" +
                "    <artifactId>" + artifactId + "</artifactId>\n" +
                "    <version>" + version + "</version>\n" +
                "    <packaging>pom</packaging>\n" +
                "\n" +
                "    <modules>\n" +
                moduleXml.toString() +
                "    </modules>\n" +
                "\n" +
                "    <properties>\n" +
                "        <maven.compiler.source>" + javaVersion + "</maven.compiler.source>\n" +
                "        <maven.compiler.target>" + javaVersion + "</maven.compiler.target>\n" +
                "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
                "    </properties>\n" +
                "\n" +
                "    <dependencyManagement>\n" +
                "        <dependencies>\n" +
                "            <!-- 项目内部模块依赖 -->\n" +
                dependencyManagementXml.toString() +
                "        </dependencies>\n" +
                "    </dependencyManagement>\n" +
                "</project>";

        writeToFile(projectPath.resolve("pom.xml"), content);
    }




    private void writeReadme(Path projectPath, String projectName, String[] modules) {
        StringBuilder modulesDescription = new StringBuilder();
        for (String module : modules) {
            modulesDescription.append("- `").append(module).append("`: ");

            // 根据模块名称添加描述
            if (module.equals("api")) {
                modulesDescription.append("对外暴露的API接口模块\n");
            } else if (module.equals("common")) {
                modulesDescription.append("公共工具类和通用组件模块\n");
            } else if (module.equals("mapper")) {
                modulesDescription.append("数据访问层模块\n");
            } else if (module.equals("pojo")) {
                modulesDescription.append("实体类模块\n");
            } else if (module.equals("service")) {
                modulesDescription.append("业务逻辑模块\n");
            } else {
                modulesDescription.append("自定义模块\n");
            }
        }

        String content = "# " + projectName + " Maven聚合项目\n\n" +
                "这是一个多模块Maven项目模板，通过模块化设计组织Java应用程序。\n\n" +
                "## 项目结构\n\n" +
                "```\n" +
                projectName + "/\n";

        // 添加模块目录结构
        for (String module : modules) {
            content += "├── " + module + "/\n";
        }

        content += "```\n\n" +
                "## 模块说明\n\n" +
                modulesDescription.toString() + "\n" +
                "## 开始使用\n\n" +
                "1. 根据需要修改各模块的`pom.xml`文件\n" +
                "2. 在相应模块中添加源代码\n" +
                "3. 使用以下命令构建项目：\n\n" +
                "```bash\n" +
                "mvn clean install\n" +
                "```\n";

        writeToFile(projectPath.resolve("README.md"), content);
    }

    private void writeGitignore(Path projectPath) {
        String content = "# Maven\n" +
                "target/\n" +
                "pom.xml.tag\n" +
                "pom.xml.releaseBackup\n" +
                "pom.xml.versionsBackup\n" +
                "pom.xml.next\n" +
                "release.properties\n" +
                "dependency-reduced-pom.xml\n" +
                "buildNumber.properties\n" +
                ".mvn/timing.properties\n" +
                ".mvn/wrapper/maven-wrapper.jar\n\n" +
                "# IntelliJ IDEA\n" +
                ".idea/\n" +
                "*.iws\n" +
                "*.iml\n" +
                "*.ipr\n\n" +
                "# Eclipse\n" +
                ".classpath\n" +
                ".project\n" +
                ".settings/\n\n" +
                "# Mac\n" +
                ".DS_Store\n";

        writeToFile(projectPath.resolve(".gitignore"), content);
    }
    private void writeModulePom(Path modulePath, String groupId, String parentArtifactId, String version, String projectName) {
        // 获取模块名称
        String moduleName = modulePath.getFileName().toString();

        // 新的 artifactId: 父项目名称-子模块名
        //String newArtifactId = parentArtifactId + "-" + moduleName;
        String newArtifactId = moduleName;

        // 构建 pom.xml 内容
        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "    <parent>\n" +
                "        <groupId>" + groupId + "</groupId>\n" +
                "        <artifactId>" + parentArtifactId + "</artifactId>\n" +
                "        <version>" + version + "</version>\n" +
                "    </parent>\n" +
                "    <artifactId>" + newArtifactId + "</artifactId>\n" +
                "    <packaging>jar</packaging>\n" +
                "</project>";

        // 写入文件
        writeToFile(modulePath.resolve("pom.xml"), content);

        // 可选：手动验证 pom.xml 内容
        LOG.info("Generated pom.xml content for module " + moduleName + ":\n" + content);
    }

    private void addModuleDependencies(Path projectPath, String groupId, String version, String parentArtifactId, String[] modules) {
        // 这个方法已被优化，依赖关系现在在writeModulePomWithDependencies中直接生成
        // 保留此方法以保持向后兼容性，但不再使用
        LOG.info("模块间依赖关系已在POM生成时自动配置");
    }

    private void addDependencyToModule(Path modulePath, String groupId, String parentArtifactId, String moduleName, String version) {
        // 这个方法已被优化，不再需要动态修改POM文件
        // 依赖关系现在在POM生成时直接包含
        LOG.debug("依赖关系已在POM生成时自动配置，无需动态添加");
    }






    private void writeToFile(Path path, String content) {
        try {
            // 创建父目录（如果不存在）
            Files.createDirectories(path.getParent());

            // 写入文件内容
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(content);
            }
        } catch (IOException e) {
            LOG.warn("Failed to write file: " + path, e);
        }
    }

    private void createDirectoryStructure(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            LOG.warn("Failed to create directory: " + path, e);
        }
    }





    private boolean containsModule(String[] modules, String moduleName) {
        for (String module : modules) {
            if (module.equals(moduleName)) {
                return true;
            }
        }
        return false;
    }



}