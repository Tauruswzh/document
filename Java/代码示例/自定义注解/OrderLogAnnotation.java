package com.hellozj.order.annotation;


/**
* 文件名: OrderLogAnnotation.java
* 作者: xiahao
* 时间: 2020/5/27 19:20
* 描述: 订单日志自定义注解
*/

/**
*@Documented - 标记这些注解是否包含在用户文档中
 *
*@Target - 标记这个注解应该是哪种 Java 成员
 * *		ElementType.METHOD 可以应用于方法级注释
 * *		ElementType.FIELD 可以应用于字段或属性
 * *		ElementType.PARAMETER 可以应用于方法的参数
 * *		ElementType.CONSTRUCTOR 可以应用于构造函数
 * *		ElementType.LOCAL_VARIABLE 可以应用于局部变量
 *
 *@Retention - 标识这个注解怎么保存，是只在代码中，还是编入class文件中，或者是在运行时可以通过反射访问
 * * 		RetentionPolicy.SOURCE - 标记的注释仅保留在源级别中，并由编译器忽略
 * *		RetentionPolicy.CLASS - 标记的注释在编译时由编译器保留，但Java虚拟机（JVM）会忽略
 * *		RetentionPolicy.RUNTIME - 标记的注释由JVM保留，因此运行时环境可以使用它
*
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrderLogAnnotation {

}
