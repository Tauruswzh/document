package com.xx.common.util;

import com.xx.common.exception.XXException;

/**
* 文件名: PwdUtil.java
* 作者: dxwang
* 时间: 2021/5/18 10:06
* 描述: 是否包含3个及以上相同或字典连续字符
*/
public class PwdUtil {

    /**
     * 是否包含3个及以上相同或字典连续字符
     */
    public static boolean isContinuousChar(String password) {
        String regex = "^.{6,18}$";
        boolean b = password.matches(regex);
        if(!b){
            throw new XXException("密码长度为6~18");
        }
        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            int n1 = chars[i];
            int n2 = chars[i + 1];
            int n3 = chars[i + 2];
            // 判断重复字符
            if (n1 == n2 && n1 == n3) {
                return true;
            }
            // 判断连续字符： 正序 + 倒序
            if ((n1 + 1 == n2 && n1 + 2 == n3) || (n1 - 1 == n2 && n1 - 2 == n3)) {
                return true;
            }
        }
        return false;
    }
}
