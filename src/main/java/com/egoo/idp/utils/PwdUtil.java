package com.egoo.idp.utils;

import java.util.regex.Pattern;

public class PwdUtil {

    public static boolean con_contiunity(String password) {//方法冗余
        String content = password;
        String pattern = "(123456|234567|345678|456789|567890|012345|987654|876543|765432|654321|543210)";
        boolean isMatch = Pattern.matches(pattern, content);
        return isMatch;
    }

    /*
     * 相同的6位数字
     */
    public static boolean con_AAAAAA(String password) {//方法冗余
        String content = password;
        String pattern = "^(.)\\1*$";
        boolean isMatch = Pattern.matches(pattern, content);
        return isMatch;
    }

    /*
     * AAABBB类型
     */
    public static boolean con_AAABBB(String password) {
        String content = password;
        String pattern = "(\\d)\\1{2}(\\d)\\2{2}";
        boolean isMatch = Pattern.matches(pattern, content);
        return isMatch;
    }

    /*
     * AABBCC类型
     */
    public static boolean con_AABBCC(String password) {
        String content = password;
        String pattern = "^(\\w)\\1(\\w)\\2(\\w)\\3$";
        boolean isMatch = Pattern.matches(pattern, content);
        return isMatch;
    }

    /*
     * ABCABC类型
     */
    public static boolean con_ABCABC(String password) {
        String content = password;
        String pattern = "(\\w+?)\\1+";
        boolean isMatch = Pattern.matches(pattern, content);
        return isMatch;
    }

    /**
     * knownmsg:已知变量
     * return -1 不匹配……
     */
    public static boolean con_string(String password, String knownmsg) {
        if(knownmsg.indexOf(password)>=0)
            return true;
        else
            return false;
    }

}

