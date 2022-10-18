maven配置文件的标签顺序

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.hellozj</groupId>
        <artifactId>hellozj-service</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
 
    <artifactId>hellozj-service-goods</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
 
    <name>hellozj-service-goods</name>
    <description>Demo project for Spring Boot</description>
 
    <modules>
        <module>hellozj-api</module>
        <module>hellozj-service</module>
        <module>hellozj-common</module>
    </modules>
     
    <properties>
        <!-- Common Properties -->
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <java.version>1.8</java.version>
    </properties>
 
    <dependencies>
        <dependency>
            <groupId>com.hellozj</groupId>
            <artifactId>hellozj-common</artifactId>
        </dependency>
    </dependencies>
 
 
    <dependencyManagement>
        <dependencies>
            <!-- 项目模块开始-->
            <dependency>
                <groupId>com.hellozj</groupId>
                <artifactId>hellozj-api-oss</artifactId>
                <version>${hellozj.platform.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
             
    <profiles>
        <profile>
            <id>local</id>
            <properties>
                <profiles.active>local</profiles.active>
            </properties>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
    </profiles>
 
    <build>
        <finalName>${project.artifactId}</finalName>

        <resources>
            <resource>
                <!--引入所需环境的配置文件-->
                <directory>src/main/resources/env/${profiles.active}</directory>
                <filtering>true</filtering>
                <includes>
                    <include>*.*</include>
                </includes>
            </resource>
 
            <resource>
                <!--打包之后不包含env文件夹-->
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
                <excludes>
                    <exclude>**/env/**</exclude>
                </excludes>
            </resource>
        </resources>
        
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
     
    <distributionManagement>
        <repository>
            <!--这里的id需要和settings.xml中的server的id一致-->
            <id>nexus-release</id>
            <name>Nexus release Repository</name>
            <!--releases仓库-->
            <url>http://192.168.1.252:8081/repository/maven-releases/</url>
        </repository>
        <snapshotRepository>
            <id>nexus-snapshots</id>
            <name>Nexus snapshots Repository</name>
            <!--snapshots仓库-->
            <url>http://192.168.1.252:8081/repository/maven-snapshots/</url>
        </snapshotRepository>
    </distributionManagement>
</project>
```