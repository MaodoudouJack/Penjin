package com.penjin.android.utils;

/**
 * Created by maotiancai on 2016/1/15.
 */
public class JsonUtils {

    public static boolean isJsonNull(String s) {
        if (s == null || s.equals("null") || s.equals("NULL") || s.equals("Null") || s.equals("")) {
            return true;
        } else
            return false;
    }



}
