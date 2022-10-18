package com.hellozj.order.annotation;

import com.hellozj.order.entity.dao.OrderLog;
import com.hellozj.order.entity.dto.OrderLogDto;
import com.hellozj.order.send.MQMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
* 文件名: OrderLogAspect.java
* 作者: xiahao
* 时间: 2020/5/28 9:13
* 描述: 订单日志切面
*/
@Aspect
@Component
@Slf4j
public class OrderLogAspect {

	@Pointcut("@annotation(com.hellozj.order.annotation.OrderLogAnnotation)")
	public void orderLogCut() {
	}

	@Around("orderLogCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {

		// 方法返回结果
		Object result = point.proceed();

		// 保存日志
		sendOrderLogToMq(point);

		return result;
	}

	/**
	* 方法名:sendOrderLogToMq
	* 作者/时间: xiahao-2020/5/28
	* 描述: 切面增强方法
	* 参数: ul
	* 返回:
	*/
	private void sendOrderLogToMq(ProceedingJoinPoint joinPoint) {
		// 请求的参数
		Object[] args = joinPoint.getArgs();
		log.info("获取订单日志参数: {}", Arrays.toString(args));

		//封装参数
		OrderLog orderLog = new OrderLog();
		OrderLogDto orderLogDto = (OrderLogDto) args[0];
		BeanUtils.copyProperties(orderLogDto,orderLog);

		//发送MQ
		MQMessageHandler.orderLog(orderLog);
	}
}
