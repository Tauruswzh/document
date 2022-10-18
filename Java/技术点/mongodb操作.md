mongodb操作


###1.连接
```shell script
mongo -u ⽤户名 -p 密码 --port 端⼝号 --host IP 数据库名
#或者
mongo 192.168.0.1:27017/库名  -u ⽤户名 -p 密码

#docker安装没有账号密码
mongo
```

###2.基本操作
查询数据库
show databases
切换数据库
use test
查询当前数据库下面的集合
show collections
创建集合
db.createCollection("集合名称")
删除集合
db.集合名称.drop()
删除数据库
db.dropDatabase() //首先要通过use切换到当前的数据库


###3.Mongodb增删改查(CURD)
id 系统会自动加一个时间戳+机器码 生成

####3.1. 增（insert）
```shell script
#新增一条
db.userinfo.insert({name:"贾宝玉",age:25,gander:"男",address:'贾府'})
#新增多条
db.userinfo.insert([{name:,address:},{name:"林黛玉",age:16,gander:"女",address:'林府'}])
#可不可以快速插入10条数据
for(var i=1;i<=10;i++)
{
"clay"+i,age:i})
}
```
####3.2. 查（find）
```shell script
#查询所有的数据
db.集合名称.find({})
#查询top条数
db.集合名称.find({}).limit(条数)
#条件查询
db.userinfo.find({name:"clay1",age:1},{name:1,_id:0})

#排序&分页
db.c1.insert({_id:1,name:"a",sex:1,age:1})
db.c1.insert({_id:2,name:,sex:1,age:2})
db.c1.insert({_id:3,name:"b",sex:2,age:3})
db.c1.insert({_id:4,name:"c",sex:2,age:4})
db.c1.insert({_id:5,name:"d",sex:2,age:5})

db.c1.find()
#正序
db.c1.find({}).sort({age:1})
#降序
db.c1.find({}).sort({age:-1})
#分页查询 跳过两条查询两条
db.c1.find({}).sort({age:1}).skip(2).limit(2)

#运算符
#年龄大于1
db.c1.find({age:{$gt:1}})
#年龄是 3,4,5的
$in:[3,4,5]}})
```
####3.3. 改（update）
db.集合名.update（条件， 新数据） {修改器: {键:值}}

```shell script
db.c1.insert({name:"8888",age:1,addr:'address',flag:true})

db.c1.update({name:}, {name:"99"})
},
{
$set:{name: "zs44"},
$inc:{age:10},
$rename:{addr:"address"} ,
$unset:{flag:""}
}
)
db.c1.find({name:})
```
####3.4. 删（delete）
```shell script
#全部移除
db.userinfo.deleteMany({})
db.userinfo.deleteMany({age:1})
```
####3.5. 聚合查询
顾名思义就是把数据聚起来，然后统计
语法
db.集合名称.aggregate([
{管道:{表达式}}
....
])

常用管道
$group 将集合中的文档分组，用于统计结果
$match 过滤数据，只要输出符合条件的文档
$sort 聚合数据进一步排序
$skip 跳过指定文档数
$limit 限制集合数据返回文档数

常用表达式
$sum 总和 $sum:1同count表示统计
$avg 平均
$min 最小值
$max 最大值