package com.hellozj.order.service;

import com.hellozj.order.entity.dto.OrderLogDto;


/**
* 文件名: AsynOrderLogService.java
* 作者: xiahao
* 时间: 2020/5/28 15:19
* 描述: 异步订单日志
*/
public interface AsynOrderLogService {
    /**
    * 方法名:  setOrderLog
    * 作者/时间: xiahao-2020/5/28
    * 描述: 插入订单日志
    * 参数: ul
    * 返回:
    */
    void setOrderLog(OrderLogDto orderLog);
}
