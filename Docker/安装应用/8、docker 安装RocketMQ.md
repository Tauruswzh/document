docker 安装RocketMQ


基于docker-compose安装
[注意：启动 RocketMQ Server + Broker + Console 至少需要 2G 内存]

1.创建rockerMQ文件夹
```text
mkdir rocketmq
```

2.在rockerMQ文件夹下,创建[docker-compose.yml]
[docker-compose.yml](file/docker-compose.yml)
```text
cd rocketmq/
vi docker-compose.yml
粘贴配置, esc
保存 :wq!
```

3.在rockerMQ文件夹下,创建配置[broker.conf]
[broker.conf](file/broker.conf)
```text
mkdir data
cd data/
mkdir brokerconf           [docker-compose.yml中配置的数据卷]
cd brokerconf/
vi broker.conf
粘贴配置, esc
保存 :wq!
```

4.返回至在rockerMQ文件夹,使用docker-compose命令执行yml
启动
[docker-compose up -d]
停止
[docker-compose down]

5.测试访问
执行docker-compose up -d 之后，docker ps 查看是否已经启动容器
前端访问: localhost:8080

[踩坑]
项目连接报错：connect to 172.19.0.4:10911 failed 或者：页面打开Cluster中地址：172.19.0.4:10911
原因：ip限制
解决：修改配置：broker.conf
添加配置：ip为本地ip
namesrvAddr=10.16.191.100:9876
brokerIP1=10.16.191.100