#!/bin/bash

#停止防火墙
systemctl stop firewalld

#启动Mysql
cd /opt/mysql
chkconfig --add mysql
service mysql stop
service mysql start

#启动Redis
ps aux | grep redis | grep -v grep | awk '{print $2}' | xargs kill -9
cd /opt/redis
./bin/redis-server ./redis.conf

#启动Minio
ps aux | grep minio | grep -v grep | awk '{print $2}' | xargs kill -9
cd /home/minio/
./minio.sh