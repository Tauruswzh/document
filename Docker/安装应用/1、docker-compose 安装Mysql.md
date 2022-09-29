docker-compose 安装mysql

###1.环境
docker
docker-compose
mkdir -p /opt/mysql
mkdir -p /opt/mysql/data


###2.编写docker-compose.yml
cd /opt/mysql
vim docker-compose.yml
```yaml
version: '3.1'
services:
  mysql:
    restart: always
    image: mysql:5.7.22
    container_name: mysql
    ports:
      - 19001:3306
    environment:
      TZ: Asia/Shanghai
      MYSQL_ROOT_PASSWORD: witroot
    command:
      --character-set-server=utf8mb4
      --collation-server=utf8mb4_general_ci
      --explicit_defaults_for_timestamp=true
      --lower_case_table_names=1
      --max_allowed_packet=128M
      --sql-mode="STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO"
    volumes:
      - ./data:/var/lib/mysql
```
:wq


###3.启动
启动
[docker-compose up -d]
停止
[docker-compose down]


###4.验证
```shell script
docker ps
docker exec -it mysql /bin/bash

mysql -h localhost -u root -p
witroot

create database 数据库名;
show databases;
use 库名;
show tables;
desc 表名;                //查看表结构
show create table 表名;   //查看表的创建语句
drop table 表名;          //删除表
```

退出容器：exit


进入mysql容器并创建用户账户密码均为sonar
```shell script
#进入容器
docker exec -it mysql bash
#登陆
mysql -u root -p
#密码
witroot
#创建数据库
create database sonar;

#创建用户和密码
CREATE USER 'sonar'@'%' IDENTIFIED WITH mysql_native_password BY 'sonar';
#授权
GRANT ALL PRIVILEGES ON *.* TO 'sonar'@'%';
```