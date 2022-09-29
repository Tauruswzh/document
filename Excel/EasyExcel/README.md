EasyExcel 导入导出

1.添加依赖
```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>2.2.6</version>
</dependency>
<!-- 工具包 -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.3.7</version>
</dependency>
```

2.实体修改
2.1.继承标准类 BaseRowModel
2.2.添加注解 @ExcelProperty、@ContentStyle、@ColumnWidth、@ExcelIgnore
[ExcelModelDto](ExcelModelDto.java)

3.导入
[导入](./导入/ImportOrderController.java)

4.导出
[导出](./导出/OrderExportController.java)


5.Excel导出功能超时解决方案
原因：导出excel会出现超时的情况，初步判断是数据增长太快，数据量太大，请求时间超过了设置的超时时间

尝试：
1.有考虑直接更改该请求的超时时长，可是治标不治本
2.采用多线程的方式，出现了内存溢出的情况。

解决：[异步处理]　
整体思路是，后端在开始处理请求之后，维护一个请求的状态用来标记文件是否已经生成，如果文件已经生成，
将文件上传到云端返回下载地址，将地址记录，直到下次请求时将地址返回，供前端下载。

具体流程如下
1.前端第一次请求，后端接到请求之后开始生成文件，并返回前端正在处理，用redis记录该次请求，标记状态为正在创建文件
2.前端需要定时轮询接口，查看文件链接是否生成，可在页面上设置loading效果（直到get到文件的下载url）
3.文件生成之后，修改redis记录状态为已生成，且记录地址url，下次接口请求时返回该url。

[流程图](./image/异步导出.jpg)

总结
1.重点是在于采用了[异步的思想取代原来同步的流程]，这样请求就不会出现超时的情况，无论处理多久都木有关系　　　
2.这里采用redis来记录也是考虑到下载会有一个过期时间，一个请求可能过一段时间就无效了