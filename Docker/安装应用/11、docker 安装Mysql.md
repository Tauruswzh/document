安装Mysql

###1.docker安装
docker search mysql
docker pull mysql
docker run -d -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=123456 mysql

命令参数：
* -p 3306:3306：将容器的3306端口映射到主机的3306端口
* -e MYSQL\_ROOT\_PASSWORD=123456：初始化root用户的密码

###2.验证
进入docker
docker exec -it mysql /bin/bash
后台登陆
mysql -h localhost -u root -p 回车
输入密码


###docker-compose安装
在opt文件夹下创建mysql文件夹，在Mysql文件夹下创建data和conf文件夹
将docker-compose.yml文件放到mysql文件夹下
将my.cnf文件放在conf文件夹下

docker-compose.yml
```yaml
version: '3.1'
services:
  mysql:
    image: mysql:5.7.22
    container_name: mysql
    privileged: true #一定要设置为true，不然数据卷可能挂载不了，启动不起
    ports: 
     - 19001:3306
    environment:
      MYSQL_ROOT_PASSWORD: witroot
      TZ: Asia/Shanghai
    command:
      --character-set-server=utf8mb4
      --collation-server=utf8mb4_general_ci
      --explicit_defaults_for_timestamp=true
      --lower_case_table_names=1
      --max_allowed_packet=128M
      --max_connections=2000
      --sync_binlog=1000
      --innodb_flush_log_at_trx_commit=2
      --innodb_buffer_pool_size=1G
      --sql-mode="STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO"   
    volumes:
      - /opt/mysql/data:/var/lib/mysql
      - /opt/mysql/conf/my.cnf:/etc/my.cnf
    restart: always
```
my.cnf文件
```text
[mysqld]
user=mysql
default-storage-engine=INNODB
character-set-server=utf8mb4
default-time_zone = '+8:00'
#添加约束
sql-mode=STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
[client]
default-character-set=utf8mb4
[mysql]
default-character-set=utf8mb4
```

查询sql-model
select @@GLOBAL.sql_mode

```text
4、常用sql_mode
①ONLY_FULL_GROUP_BY
对于GROUP BY聚合操作，如果在SELECT中的列，没有在GROUP BY中出现，那么这个SQL是不合法的，因为列不在GROUP BY从句中
②NO_AUTO_VALUE_ON_ZERO
该值影响自增长列的插入。默认设置下，插入0或NULL代表生成下一个自增长值。如果用户希望插入的值为0，而该列又是自增长的，那么这个选项就有用了。
③STRICT_TRANS_TABLES
如果一个值不能插入到一个事务中，则中断当前的操作，对非事务表不做限制
④NO_ZERO_IN_DATE
不允许日期和月份为零
⑤NO_ZERO_DATE
mysql数据库不允许插入零日期，插入零日期会抛出错误而不是警告
⑥ERROR_FOR_DIVISION_BY_ZERO
在insert或update过程中，如果数据被零除，则产生错误而非警告。如果未给出该模式，那么数据被零除时Mysql返回NULL
⑦NO_AUTO_CREATE_USER
禁止GRANT创建密码为空的用户
⑧NO_ENGINE_SUBSTITUTION
如果需要的存储引擎被禁用或未编译，那么抛出错误。不设置此值时，用默认的存储引擎替代，并抛出一个异常
⑨PIPES_AS_CONCAT
将"||"视为字符串的连接操作符而非或运算符，这和Oracle数据库是一样是，也和字符串的拼接函数Concat想类似
⑩ANSI_QUOTES
不能用双引号来引用字符串，因为它被解释为识别符
```