package com.hellozj.order.service.impl;

import com.hellozj.order.annotation.OrderLogAnnotation;
import com.hellozj.order.entity.dto.OrderLogDto;
import com.hellozj.order.service.AsynOrderLogService;
import org.springframework.stereotype.Service;

/**
* 文件名: AsynOrderLogServiceImpl.java
* 作者: xiahao
* 时间: 2020/5/28 15:20
* 描述: 异步订单日志实现
*/
@Service
public class AsynOrderLogServiceImpl implements AsynOrderLogService{

    @Override
    @OrderLogAnnotation
    public void setOrderLog(OrderLogDto orderLog) {
    }
}
