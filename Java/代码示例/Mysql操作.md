Mysql操作


###1、按天分组 不足补0
```sql
 SELECT
    a.createTime AS pointTime,
    IFNULL(a.patientNum+b.patientNum,0) AS patientNum

    FROM
    (SELECT @cdate := DATE_ADD( @cdate, INTERVAL - 1 DAY ) createTime, 0 AS patientNum FROM
    (SELECT @cdate := DATE_ADD(#{endTime}, INTERVAL + 1 DAY ) FROM area_info ) as t1 
      WHERE @cdate &gt; #{beginTime}
    )a

    LEFT JOIN

    (SELECT COUNT(*) AS patientNum,DATE_FORMAT(create_time,'%Y-%m-%d') AS createTime FROM patient_info
    WHERE delete_flag = 0 and company_id = #{companyId}
    and DATE_FORMAT(create_time,'%Y-%m-%d')  &gt;= #{beginTime}
    and DATE_FORMAT(create_time,'%Y-%m-%d')  &lt;= #{endTime}
    GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d')
    )b

    ON b.createTime = a.createTime
    ORDER BY a.createTime
```

注：时间的间隔与查询表中的数据有关联，时间间隔必须是表的数据范围内，不然间隔显示不全，完善：使用库中的常量表作为查询基表[area_info]



###2、时间格式
```sql
%Y-%m-%d
```


###3、时间
```sql
interval(),为比较函数
当关键词使用时,表示为设置时间间隔,常用在date_add()与date_sub()函数里
如: interval 1 day ,解释为将时间间隔设置为1天

#本月第一天
date_add(curdate(), interval - day(curdate()) + 1 day)
concat(date_format(LAST_DAY(now()),'%Y-%m-'),'01')

#本月最后一天
last_day(curdate())
LAST_DAY(now())

#今天是当月的第几天
SELECT DAYOFMONTH( NOW())

#本月数据
DATE_FORMAT(字段,'%Y-%m') = DATE_FORMAT(CURDATE(),'%Y-%m')

#上月第一天
date_add(curdate()-day(curdate())+1,interval -1 month)
concat(date_format(LAST_DAY(now() - interval 1 month),'%Y-%m-'),'01')

#上月最后一天
last_day(date_sub(now(),interval 1 month))

#下月第一天
date_add(curdate()-day(curdate())+1,interval 1 month)

#下月最后一天
last_day(date_sub(now(),interval -1 month))
LAST_DAY(now() - interval 1 month)

#本月天数
day(last_day(curdate()))

#上月今天的当前日期
date_sub(curdate(), interval 1 month)

#上月今天的当前时间 (时间戳)
unix_timestamp(date_sub(now(),interval 1 month))

#获取当前时间与上个月之间的天数
datediff(curdate(), date_sub(curdate(), interval 1 month))

#今天
SELECT DATE_FORMAT(NOW(),'%Y-%m-%d 00:00:00') AS '今天开始';
SELECT DATE_FORMAT(NOW(),'%Y-%m-%d 23:59:59') AS '今天结束';

#昨天
SELECT DATE_FORMAT( DATE_SUB(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d 00:00:00') AS '昨天开始';
SELECT DATE_FORMAT( DATE_SUB(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d 23:59:59') AS '昨天结束';

#上周
SELECT DATE_FORMAT( DATE_SUB( DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 1 WEEK), '%Y-%m-%d 00:00:00') AS '上周一';
SELECT DATE_FORMAT( SUBDATE(CURDATE(), WEEKDAY(CURDATE()) + 1), '%Y-%m-%d 23:59:59') AS '上周末';

#本周
SELECT DATE_FORMAT( SUBDATE(CURDATE(),DATE_FORMAT(CURDATE(),'%w')-1), '%Y-%m-%d 00:00:00') AS '本周一';
SELECT DATE_FORMAT( SUBDATE(CURDATE(),DATE_FORMAT(CURDATE(),'%w')-7), '%Y-%m-%d 23:59:59') AS '本周末';
 
-- 上面的本周算法会有问题,因为mysql是按照周日为一周第一天,如果当前是周日的话,会把时间定为到下一周.
SELECT DATE_FORMAT( DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), '%Y-%m-%d 00:00:00') AS '本周一';
SELECT DATE_FORMAT( DATE_ADD(SUBDATE(CURDATE(), WEEKDAY(CURDATE())), INTERVAL 6 DAY), '%Y-%m-%d 23:59:59') AS '本周末';

#上月
SELECT DATE_FORMAT( DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-01 00:00:00') AS '上月初';
SELECT DATE_FORMAT( LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)), '%Y-%m-%d 23:59:59') AS '上月末';

#本月
SELECT DATE_FORMAT( CURDATE(), '%Y-%m-01 00:00:00') AS '本月初';
SELECT DATE_FORMAT( LAST_DAY(CURDATE()), '%Y-%m-%d 23:59:59') AS '本月末';

#本年第一天
SELECT DATE_SUB(CURDATE(),INTERVAL dayofyear(now())-1 DAY)  
#或
concat(year(now()),'-01-01')//当前年份的第一天
concat(year(now()),'-12-31')//当前年份的最后一天


#日期增加年,月,天,小时,分,秒
select date_add(日期, interval 1 day); 日期加天
select date_add(日期, interval 1 hour); 日期加小时
select date_add(日期, interval 1 minute); 日期加分
select date_add(日期, interval 1 second);日期加秒
select date_add(日期, interval 1 microsecond); 日期加微秒
select date_add(日期, interval 1 week); 日期加周
select date_add(日期, interval 1 month); 日期加月
select date_add(日期, interval 1 quarter); 日期加季度
select date_add(日期, interval 1 year); 日期加年

   
 #当前week的第一天:  
 select date_sub(curdate(),INTERVAL WEEKDAY(curdate()) + 1 DAY);  
   
 #当前week的最后一天: 
 select date_sub(curdate(),INTERVAL WEEKDAY(curdate()) - 5 DAY);  
   
 #前一week的第一天:
 select date_sub(curdate(),INTERVAL WEEKDAY(curdate()) + 8 DAY);  
   
 #前一week的最后一天:
 select date_sub(curdate(),INTERVAL WEEKDAY(curdate()) + 2 DAY);
```


###4、存储过程 插入表数据，日前增加
```sql
DELIMITER //  --将语句的结束符号从分号;临时改为两个$$(可以是自定义)
CREATE PROCEDURE test1(X INT(10),Y INT(10))
BEGIN
DECLARE i INT DEFAULT X;
DECLARE DTime DATETIME DEFAULT '2020-11-10 00:00:00';
WHILE i< Y DO
	INSERT INTO `hellozj_report`.`report_statistics_distribution`
	( `amount_num_total`, `order_num_total`, `store_num_total`, `eng_num_total`, `amount_num`, `order_num`, `store_num`, `eng_num`, `create_time`, `update_time`) 
	VALUES 
	( 0.00, 0, 0, 0, 0.00, 0, 0, 0, DTime, DTime);
SET i = i +1;
SET DTime = DATE_ADD(DTime,INTERVAL 1 DAY);
END WHILE;
END //
DELIMITER; --将语句的结束符号恢复为分号


CALL test1(1,10);  --执行存储过程
```


