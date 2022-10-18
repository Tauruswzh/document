mybatis操作


1.条件判断
```sql
from export_center
where 1 = 1
<if test="fileName != null and fileName != ''"></if>
```

```xml
 <if test="orderTabState != null">
    <choose>
      <when test="orderTabState == 0"><!--待支付 -->
        and os.order_state in (0)
      </when>
      <when test="orderTabState == 1"><!-- 待分配 -->
        and os.order_state = 2
      </when>
      <when test="orderTabState == 2"><!-- 进行中 -->
        and os.order_state in (10,14,20,24,28,32)
      </when>
      <when test="orderTabState == 3"><!-- 待评价-->
        and os.order_state = 50 and os.eval_state = 0
      </when>
      <when test="orderTabState == 4"><!-- 已完成-->
        and (os.order_state = 50 and os.eval_state = 1 or os.order_state in (51,52,53))
      </when>
    </choose>
  </if>
```

2.模糊查询
```xml
<if test="name != null and name != ''">
	name like "%"#{name}"%"
</if>
```

```sql
enginner_name LIKE  concat('%',#{engineerName},'%')
```

3.循环
```xml
<select id="queryByIds" resultType="witpdp.entity.CalcTemplate" parameterType="java.util.Map">
    select
    id, name, type, IFNULL(status,-1) status, liable_id, liable_name
    from pdp_calc_template
    where delete_flag = 0
    and id in
    <foreach collection="ids" item="item" separator="," open="(" close=")">
        #{item}
    </foreach>
</select>

<!-- 多语句拼接 需要在sql配置中加入allowMultiQueries=true -->
<update id="batchUpdateBeforeTaskId">
    <foreach collection="list" item="item" index="index" separator=";">
        update pdp_product_task set
        before_task_id = #{item.beforeTaskId,jdbcType=BIGINT},
        update_id = #{item.updateId,jdbcType=VARCHAR},
        update_time = #{item.updateTime,jdbcType=TIMESTAMP}
        where id = #{item.id,jdbcType=BIGINT}
    </foreach>
</update>
```

4.时间比较
```sql
<if test="requestBeginTime != null">
  and request_time  &gt;=  #{requestBeginTime}
</if>
<if test="requestEndTime != null">
  and request_time  &lt;= #{requestEndTime}
</if>

create_time <![CDATA[ >= ]]> #{beginTime}

最新时间
max(create_time) 
```

5.不等于
```text
remain_stock_num <![CDATA[<=]]> #{remainStockNum}

DATE_FORMAT(create_time,'%Y-%m-%d')   &gt;= #{beginTime} 	//>=
and DATE_FORMAT(create_time,'%Y-%m-%d')  &lt;= #{endTime}	//<=
```


6.批量
```text
void insertList(@Param("list") List<OrderCompany> orderCompanies);
```
```sql
<insert id="insertList" parameterType="java.util.List">
        insert into order_company
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="list[0].id != null">
                id,
            </if>
            <if test="list[0].orderName != null">
                order_name,
            </if>
            <if test="list[0].orderId != null">
                order_id,
            </if>
            <if test="list[0].orderNum != null">
                order_num,
            </if>
        </trim>
        values
        <foreach collection="list" index="index" item="item" separator=",">
            <trim prefix=" (" suffix=")" suffixOverrides=",">
                <if test="item.id != null">
                    #{item.id,jdbcType=BIGINT},
                </if>
                <if test="item.orderName != null">
                    #{item.orderName,jdbcType=VARCHAR},
                </if>
                <if test="item.orderId != null">
                    #{item.orderId,jdbcType=BIGINT},
                </if>
                <if test="item.orderNum != null">
                    #{item.orderNum,jdbcType=VARCHAR},
                </if>
            </trim>
        </foreach>
    </insert>
```

7.时间格式化比较
```sql
and DATE_FORMAT(create_time,'%Y-%m-%d')  &gt;= #{beginTime}
and DATE_FORMAT(create_time,'%Y-%m-%d')  &lt;= #{endTime}
```

8.无则插入有则更新
```sql
INSERT INTO order_engineer
(order_id,order_num,engineer_id,work_type,dispatch_time,create_time,update_time)
VALUES
(#{orderId},#{orderNum},#{engineerId},#{workType},#{dispatchTime},#{createTime},#{updateTime})
ON DUPLICATE KEY UPDATE
engineer_id=#{engineerId} ,update_time=#{updateTime},work_type=#{workType},dispatch_time=#{dispatchTime}
```

***这种方法有个前提条件，就是，需要插入的约束，需要是主键或者唯一约束***（在你的业务中那个要作为唯一的判断就将那个字段设置为唯一约束也就是unique key）

```sql
INSERT INTO order_price_discount
(order_id, order_num, discount_price, discount_type, create_time, update_time)
SELECT
#{orderId}, #{orderNum}, #{discountPrice}, #{discountType}, #{createTime}, #{updateTime}
FROM DUAL
WHERE
NOT EXISTS ( SELECT 1 FROM order_price_discount WHERE order_id = #{orderId});
UPDATE order_price_discount SET discount_price = #{discountPrice} WHERE order_id = #{orderId};
  
```

***这种方法其实就是使用了mysql的一个临时表的方式，但是里面使用到了子查询，效率也会有一点点影响，如果能使用上面的就不使用这个***

