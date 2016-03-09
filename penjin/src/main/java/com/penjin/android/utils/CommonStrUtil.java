package com.penjin.android.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 * Created by Administrator on 2016/3/9.
 */
public class CommonStrUtil {

    public static boolean isPhoneNum(String phoneNumber) {
        if(phoneNumber==null||phoneNumber.equals(""))
            return false;
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNumber);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

}
