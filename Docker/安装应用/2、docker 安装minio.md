docker 安装minio


###1.拉取镜像
```shell script
docker search minio
docker pull minio
```


###2.运行
```shell script
mkdir -p /opt/minio/config
mkdir -p /opt/minio/data
```

```shell script
docker run -p 9000:9000 -p 19002:19002 --name minio \
-d --restart=always \
-e "MINIO_ACCESS_KEY=root" \
-e "MINIO_SECRET_KEY=witrootroot" \
-v /opt/minio/data:/data \
-v /opt/minio/config:/root/.minio \
minio/minio server /data \
--console-address ":19002" -address ":9000"
```
MINIO_ACCESS_KEY:账号
MINIO_SECRET_KEY:密码

[踩坑]
1.指定端口启动，这里有个坑，一定要指定api端口和console端口，否则无法访问
一定要注意最后两个参数
console端口是给后台页面使用的，address端口则是我们需要集成到后台项目中用到的，比如集成到springboot中，配置里面用到的端口就是address


2.前端直接根据fileKey预览图片：
前提：将桶的权限[Summary -> Access Policy]设置成：public 
使用：公网IP+内网端口/fileKey
