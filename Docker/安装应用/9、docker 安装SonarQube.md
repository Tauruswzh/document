docker 安装sonarQube


####1.创建文件夹
```shell script
mkdir -p /opt/sonar/postgres/{postgresql,data}
mkdir -p  /opt/sonar/sonarqube/{extensions,logs,data,conf}
chmod -R 777 /opt/sonar/*

docker pull postgres:12.3
docker pull sonarqube:lts-community
```

####2.创建docker-compose.yml
cd /opt/sonar
[sonar-docker-compose](file/sonar-docker-compose.yml)
[注意：去除docker-compose.yml中的注释]

```shell script
#启动
docker-compose up -d
浏览器输入http://ip:19005,ip为sonar服务器ip,账号密码为:admin/admin
```

####3.汉化
配置->应用市场->Chinese Pack
点击homepage 到github中下载相应的汉化包：
sonarqube:7.9.2-community -> 1.29.jar

将汉化jar包复制到 /opt/sonar/sonarqube/extensions/downloads
重新启动容器：docker restart sonar

[参考](http://t.zoukankan.com/longronglang-p-14164549.html)