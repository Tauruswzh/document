package com.hellozj.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
* 文件名: BigDecimalUtil.java
* 作者:  wangxiushen
* 时间:  2020/5/18 18:42
* 描述:  BigDecimal工具类
*/
public class BigDecimalUtil {
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
	public static boolean isGreater(BigDecimal a, BigDecimal b) {
		int r = a.compareTo(b);

		return r == 1;
	}
	
	public static void main(String[] args) {
	}

	/**
	 * a >= b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isGreaterEqual(BigDecimal a, BigDecimal b) {
		int r = a.compareTo(b);

		return r == 1 || r == 0;
	}

	/**
	 * a = b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isEqual(BigDecimal a, BigDecimal b) {
		int r = a.compareTo(b);

		return r == 0;
	}

	/**
	 * a < b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isLess(BigDecimal a, BigDecimal b) {
		int r = a.compareTo(b);

		return r == -1;
	}

	/**
	 * a <= b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isLessEqual(BigDecimal a, BigDecimal b) {
		int r = a.compareTo(b);

		return r == -1 || r == 0;
	}

	/**
	 * 提供精确的加法运算。
	 *
	 * @param value1 被加数
	 * @param value2 加数
	 * @return 两个参数的和
	 */
	public static BigDecimal add(BigDecimal value1, BigDecimal value2) {
		return value1.add(value2);
	}

	/**
	 *
	 * 提供精确的减法运算。
	 *
	 * @param value1 被减数
	 * @param value2 减数
	 * @return 两个参数的差
	 */
	public static BigDecimal sub(BigDecimal value1, BigDecimal value2) {
		return value1.subtract(value2);
	}

	/**
	 * 提供精确的乘法运算。
	 *
	 * @param value1 被乘数
	 * @param value2 乘数
	 * @return 两个参数的积
	 */
	public static BigDecimal mul(BigDecimal value1, BigDecimal value2) {
		return round(value1.multiply(value2),2);
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时， 精确到小数点以后10位，以后的数字四舍五入。
	 *
	 * @param dividend 被除数
	 * @param divisor 除数
	 * @return 两个参数的商
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		return divide(dividend, divisor, DEF_DIV_SCALE);
	}

	/**
	 *
	 * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
	 *
	 * @param dividend 被除数
	 * @param divisor 除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, Integer scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return dividend.divide(divisor, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 提供指定数值的（精确）小数位四舍五入处理。
	 *
	 * @param value 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static BigDecimal round(BigDecimal value, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal one = new BigDecimal("1");
		return value.divide(one, scale, RoundingMode.HALF_UP);
	}
}
