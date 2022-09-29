docker 安装Yapi


###1.创建 MongoDB 数据卷
mkdir -p /opt/yapi/mongodb/mongo_data_yapi

###2.启动 MongoDB
docker run -d --name mongo-yapi -p 27017:27017 -v /opt/yapi/mongodb/mongo_data_yapi:/data/db mongo
[monogodb操作](../../Base/3.mongodb操作.md)

###3.获取 Yapi 镜像
版本信息可在 阿里云镜像仓库 查看 :
地址： https://dev.aliyun.com/detail.html?spm=5176.1972343.2.26.I97LV8&repoId=139034
docker pull registry.cn-hangzhou.aliyuncs.com/anoy/yapi

###4.初始化 Yapi 数据库索引及管理员账号
docker run -it --rm --link mongo-yapi:mongo  --entrypoint npm  --workdir /api/vendors  registry.cn-hangzhou.aliyuncs.com/anoy/yapi  run install-server

###5.启动 Yapi 服务
docker run -d  --name yapi --link mongo-yapi:mongo --workdir /api/vendors  -p 10001:3000/tcp  registry.cn-hangzhou.aliyuncs.com/anoy/yapi  server/app.js

###6.使用 Yapi
访问 http://ip:10001 
登录账号admin@admin.com，密码ymfe.org

进入容器：
docker exec -it yapi /bin/sh

[手册](https://yejingtao.blog.csdn.net/article/details/97315175?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1-97315175-blog-120814254.pc_relevant_default&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1-97315175-blog-120814254.pc_relevant_default&utm_relevant_index=2)