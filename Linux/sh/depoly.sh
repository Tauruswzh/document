#! /bin/sh


#刷新环境
source /etc/profile


#删除日志
rm -f /opt/xxljob/log/*


#干掉进程
ps aux | grep xxl-job-admin-2.3.0.jar | grep -v grep | awk '{print $2}' | xargs kill -9

#启动
nohup java -jar /opt/xxljob/xxl-job-admin-2.3.0.jar  > /opt/xxljob/log/admin.log 2>&1 &



