package com.hellozj.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
* 文件名: DoubleUtil.java
* 作者: xiahao
* 时间: 2020/5/18 18:42
* 描述: double工具类
*/
public class DoubleUtil {
	/** 默认除法运算精度*/
	private static final Integer DEF_DIV_SCALE = 2;
	// -1, 0, or 1 as
	// less than, equal to, or greater than

	/**
	 * a > b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isGreater(double a, double b) {
		BigDecimal data1 = new BigDecimal(a);
		BigDecimal data2 = new BigDecimal(b);
		int r = data1.compareTo(data2);

		return r == 1;
	}
	
	public static void main(String[] args) {
		double a = 2;
		double b = 1;
		System.out.println(a);
		System.out.println(sub(a, b));
		
		System.out.println(a);
	}

	/**
	 * a >= b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isGreaterEqual(double a, double b) {
		BigDecimal data1 = new BigDecimal(a);
		BigDecimal data2 = new BigDecimal(b);
		int r = data1.compareTo(data2);

		return r == 1 || r == 0;
	}

	/**
	 * a = b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isEqual(double a, double b) {
		BigDecimal data1 = new BigDecimal(a);
		BigDecimal data2 = new BigDecimal(b);
		int r = data1.compareTo(data2);

		return r == 0;
	}

	/**
	 * a < b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isLess(double a, double b) {
		BigDecimal data1 = new BigDecimal(a);
		BigDecimal data2 = new BigDecimal(b);
		int r = data1.compareTo(data2);

		return r == -1;
	}

	/**
	 * a <= b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isLessEqual(double a, double b) {
		BigDecimal data1 = new BigDecimal(a);
		BigDecimal data2 = new BigDecimal(b);
		int r = data1.compareTo(data2);

		return r == -1 || r == 0;
	}

	/**
	 * BigDecimal b1 = new BigDecimal(Double.toString(value1));
	 * 	   BigDecimal b2 = new BigDecimal(Double.toString(value2));
	 * 提供精确的加法运算。
	 *
	 * @param value1 被加数
	 * @param value2 加数
	 * @return 两个参数的和
	 */
	public static Double add(Double value1, Double value2) {
		BigDecimal b1 = BigDecimal.valueOf(value1);
		BigDecimal b2 = BigDecimal.valueOf(value2);
		return b1.add(b2).doubleValue();
	}

	/**
	 * BigDecimal b1 = new BigDecimal(Double.toString(value1));
	 * 	   BigDecimal b2 = new BigDecimal(Double.toString(value2));
	 *
	 * 提供精确的减法运算。
	 *
	 * @param value1 被减数
	 * @param value2 减数
	 * @return 两个参数的差
	 */
	public static double sub(Double value1, Double value2) {
		BigDecimal b1 = BigDecimal.valueOf(value1);
		BigDecimal b2 = BigDecimal.valueOf(value2);
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * * BigDecimal b1 = new BigDecimal(Double.toString(value1));
	 * 	 * 	   BigDecimal b2 = new BigDecimal(Double.toString(value2));
	 * 提供精确的乘法运算。
	 *
	 * @param value1 被乘数
	 * @param value2 乘数
	 * @return 两个参数的积
	 */
	public static Double mul(Double value1, Double value2) {
		BigDecimal b1 = BigDecimal.valueOf(value1);
		BigDecimal b2 = BigDecimal.valueOf(value2);
		return round(b1.multiply(b2).doubleValue(),2);
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时， 精确到小数点以后10位，以后的数字四舍五入。
	 *
	 * @param dividend 被除数
	 * @param divisor 除数
	 * @return 两个参数的商
	 */
	public static Double divide(Double dividend, Double divisor) {
		return divide(dividend, divisor, DEF_DIV_SCALE);
	}

	/**
	 * BigDecimal b1 = new BigDecimal(Double.toString(dividend));
	 * 		BigDecimal b2 = new BigDecimal(Double.toString(divisor));
	 *
	 * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
	 *
	 * @param dividend 被除数
	 * @param divisor 除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static Double divide(Double dividend, Double divisor, Integer scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = BigDecimal.valueOf(dividend);
		BigDecimal b2 = BigDecimal.valueOf(divisor);
		return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 提供指定数值的（精确）小数位四舍五入处理。
	 *
	 * @param value 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double value, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		/*BigDecimal b = new BigDecimal(Double.toString(value));*/
		BigDecimal b = BigDecimal.valueOf(value);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, RoundingMode.HALF_UP).doubleValue();
	}
}
