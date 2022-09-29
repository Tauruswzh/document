Docker下gitlab安装配置使用


[参考](https://blog.csdn.net/BThinker/article/details/124097795?spm=1001.2101.3001.6661.1&utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-124097795-blog-123816629.pc_relevant_aa_2&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-124097795-blog-123816629.pc_relevant_aa_2&utm_relevant_index=1)

####1.安装
```shell script
#1:拉取gitlab镜像
docker pull gitlab/gitlab-ce  

#2:生成挂载目录
# 配置文件
mkdir -p /home/gitlab/etc
# 数据文件
mkdir -p /home/gitlab/data
# 日志文件
mkdir -p /home/gitlab/logs

#3:启动容器（用的时候调整下命令，为了便于查看，有换行符）
docker run --name='gitlab' -d \
--publish 8881:443 --publish 8882:80 --publish 8883:22 \   
--restart always \
-v /home/gitlab/etc:/etc/gitlab \
-v /home/gitlab/data:/var/opt/gitlab \
-v /home/gitlab/logs:/var/log/gitlab \
--privileged=true   \
gitlab/gitlab-ce:latest   


# -d：后台运行
# -p：将容器内部端口向外映射
# --name：命名容器名称
# -v：将容器内数据文件夹或者日志、配置等文件夹挂载到宿主机指定目录
# --privileged=true  让容器获取宿主机root权限
# -p 8443:443      #将容器内部端口向外映射
# -p 8090:80       #将容器内80端口映射至宿主机8090端口，这是访问gitlab的端口
# -p 8022:22       #将容器内22端口映射至宿主机8022端口，这是访问ssh的端口

# 查看启动日志
docker logs -f gitlab
```
[–privileged=true 要加上，不然可能因为权限问题导致启动失败]
这里不要着急 稍等一会
此时访问 ip:8882 是有界面了，如果网络不可用或者502，就再等个几分钟，此时容器尚未启动完全

环境处理
```shell script
#1:防火墙是否开启，开启了可以直接关闭，如果不能关闭，就把所需的端口进行开放
systemctl status firewalld
#2:开放指定端口（因为22一般被sshd服务占据，所以这里用222端口）
firewall-cmd --zone=dmz --add-port=80/tcp --permanent
firewall-cmd --zone=dmz --add-port=443/tcp --permanent
firewall-cmd --zone=dmz --add-port=22/tcp --permanent
firewall-cmd --zone=dmz --add-port=222/tcp --permanent
firewall-cmd --reload #刷新
firewall-cmd --zone=dmz --list-ports
```


####2.配置
按上面的方式，gitlab容器运行没问题，但在gitlab上创建项目的时候，生成项目的URL访问地址是按容器的hostname来生成的，也就是容器的id。
作为gitlab服务器，我们需要一个固定的URL访问地址，于是需要配置gitlab.rb

接下来的配置请在容器内进行修改，不要在挂载到宿主机的文件上进行修改。
否则可能出现配置更新不到容器内，或者是不能即时更新到容器内，导致gitlab启动成功，但是无法访问

```yaml
#进入容器
docker exec -it gitlab /bin/bash

#配置域名或IP
#修改gitlab.rb
vi /etc/gitlab/gitlab.rb

#添加配置
#配置http协议所使用的访问地址,不加端口号默认为80，也可以使用域名，
#external_url需要IP+端口，但是你创建该容器时已经映射过，所以此处只配置IP即可
external_url 'http://192.168.1.1'
 
# 配置ssh协议所使用的访问地址和端口
gitlab_rails['gitlab_ssh_host'] = '192.168.1.1'
gitlab_rails['gitlab_shell_ssh_port'] = 8883 # 此端口是run时22端口映射的端口

#配置超时设置
#gitlab_rails['webhook_timeout'] = 90 
#gitlab_rails['git_timeout']=90

#保存
:wq

#刷新配置
gitlab-ctl reconfigure

注意事项：
external_url 和gitlab_rails这两个ip参数建议固定操作系统的静态不变的IP或说是域名进行配置，
假设IP变得的话在GitLab新建项目的时候，生成的IP还是原来的IP，此时就无法推送代码在Gitlab里面


#注意不要重启
#/etc/gitlab/gitlab.rb文件的配置会映射到gitlab.yml这个文件，
#解决clone的http路径缺少端口

#配置gitlab.yml
vi /opt/gitlab/embedded/service/gitlab-rails/config/gitlab.yml

gitlab:
	host: 192.168.1.1
	port: 8882
	https: false


#重启gitlab 
gitlab-ctl restart
#退出容器 
exit
```
[注意：不要刷新，配置会还原，docker重启也会]


[踩坑]
1.机器配置要大于4g，否则很容易启动不了，报502

2.问题：修改 external_url 后gitlab无法正常访问
原因：修改了内部gitlab的external_url后，其docker的内部访问端口不再是默认的80端口，而是你配置的external_url端口



####3. 初始化密码
gitlab默认管理用户是root
登录：http://101.34.130.39:8882/登录修改root的密码

[修改root密码]
需要进入容器修改密码：
docker exec -it 容器ID /bin/bash
1、要重置您的root密码，请首先使用root特权登录到服务器。使用以下命令启动Ruby on Rails控制台
gitlab-rails console -e production
2、等待控制台加载完毕，有多种找到用户的方法，您可以搜索电子邮件或用户名
user = User.where(id: 1).first
或者
user = User.find_by(email: 'admin@example.com')
3、现在，您可以更改密码
user.password = 'root123456'
user.password_confirmation = 'root123456'
4、重要的是，您必须同时更改密码和password_confirmation才能使其正常工作，别忘了保存更改
user.save!


[忘记密码找回]
获取密码命令行：docker exec -it gitlab grep 'Password:' /etc/gitlab/initial_root_password


####4.创建项目
在工程根目录创建.gitignore，忽略掉无用的代码
```text
.idea/
target/
*.iml
```


####5.安装git
```text
# 安装
yum install -y git
# 查看版本
git version
```