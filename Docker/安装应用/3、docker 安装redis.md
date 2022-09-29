docker 安装redis

1.查询镜像
[docker search  redis]
2.拉取镜像
[docker pull redis:latest]
3.查看镜像
[docker images]
4.启动镜像生成容器
[docker run -itd --name witredis -p 19003:6379 redis --requirepass witroot]

```text
docker run  -p 主机端口:容器内部的端口 --name 容器名  -d  镜像名
-p 6379:6379：映射容器服务的 6379 端口到宿主机的 6379 端口。外部可以直接通过宿主机ip:6379 访问到 Redis 的服务。
--name：自定义容器名，可以自定义，不指定则自动命名
-d：	后台运行容器，不交互
-i:		以交互模式运行容器，通常与-t同时使用	//-it：前台启动，相当于打开一个命令窗口，并进入当前的容器中
-t:		为容器重新分配一个伪输入终端
-v:		指定挂载主机目录到容器目录（数据卷），默认为rw读写模式，ro表示只读
--requirepass: 设置密码
```

5.链接访问
[docker exec -it witredis /bin/bash]
回车
[redis-cli]
redis-cli -h ip -p 端口 -a 密码
回车

6.避坑 [Redis显示中文]
配置了key和value的序列化后，中文依然乱码：
只需要在启动redis-cli时在其后面加上--raw参数即可启动后，再显示就正常了
[redis-cli --raw]

[redis默认16个库,单节点需要指定数据库，数据库之间隔离不共享，切换：select 数字]

[集群](https://baijiahao.baidu.com/s?id=1730440988136689035&wfr=spider&for=pc)