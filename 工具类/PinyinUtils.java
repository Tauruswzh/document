package com.hellozj.common.util;


import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.tinypinyin.lexicons.java.cncity.CnCityDict;
import org.springframework.util.Assert;

/**
 * 文件名: PinyinUtils.java
 * 作者: zhuxiang
 * 时间: 2020/5/26 19:54
 * 描述: 中文转拼音工具类
 */
public class PinyinUtils {

    /**
     * 方法名: toPinyin
     * 作者/时间: zhuxiang-2020/5/26
     * 描述: 汉字转拼音，英文字符不变
     * 参数: str
     * 返回: java.lang.String
     * 异常场景:
     */
    public static String toPinyin(String str) {
        Assert.notNull(str, "param 'str' not null");
        StringBuilder sb = new StringBuilder();
        char[] chars = str.trim().toCharArray();
        for (char aChar : chars) {
            sb.append(Pinyin.toPinyin(aChar));
        }
        return sb.toString();
    }

    /**
     * 方法名: toPinyinCity
     * 作者/时间: zhuxiang-2020/5/26
     * 描述: 将输入的中国城市名转为拼音
     * 参数: str
     * 返回: java.lang.String
     * 异常场景:
     */
    public static String toPinyinCity(String str) {
        Assert.notNull(str, "param 'str' not null");
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance()));
        return Pinyin.toPinyin(str.trim(), "");
    }

    /**
     * 方法名: getFirstLetter
     * 作者/时间: zhuxiang-2020/5/26
     * 描述: 获取中文字符串的首字母缩写，英文字符不变
     * 参数: str
     * 返回: java.lang.String
     * 异常场景:
     */
    public static String getFirstSpell(String str) {
        Assert.notNull(str, "param 'str' not null");
        StringBuilder sb = new StringBuilder();
        char[] chars = str.trim().toCharArray();
        for (char aChar : chars) {
            sb.append(Pinyin.toPinyin(aChar).charAt(0));
        }
        return sb.toString();
    }

    /**
     * 方法名: getFirstLetter
     * 作者/时间: zhuxiang-2020/5/26
     * 描述: 获取中文字符串的首字母
     * 参数: str
     * 返回: java.lang.String
     * 异常场景:
     */
    public static String getFirstLetter(String str) {
        Assert.notNull(str, "param 'str' not null");
        char[] chars = str.trim().toCharArray();
        char c = Pinyin.toPinyin(chars[0]).charAt(0);
        return String.valueOf(c);
    }

}
