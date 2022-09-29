docker 安装XXLJob

[参考](https://blog.csdn.net/qq_34125999/article/details/125629267)


###1.数据库环境准备
新建数据库 xxl_job
[sql](./file/xxl_job.sql)


###2.安装 XXL-JOB
```shell script
#拉取镜像
docker pull xuxueli/xxl-job-admin:2.3.0

#运行
docker run -d --name xxl-job-admin -p 19008:8080 \
-e PARAMS="\
--spring.datasource.url=jdbc:mysql://192.168.70.116:3306/xxl_job?Unicode=true&characterEncoding=UTF-8 \
--spring.datasource.username=root \
--spring.datasource.password=123456" \
-v /opt/xxl-job/logs:/data/applogs \
--privileged=true \
xuxueli/xxl-job-admin:2.3.0

#访问控制台
#账号密码: admin、123456
http://ip:端口/xxl-job-admin
```