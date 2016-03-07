package com.penjin.android.http;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.cookie.Cookie;

import java.net.CookieStore;
import java.util.List;

/**
 * Created by maotiancai on 2016/1/8.
 */
public class CookieUtil {

    private static CookieUtil instance;
    private SharedPreferences sharedPreferences;
    private List<Cookie> cookies;

    private CookieUtil(Context context) {
        sharedPreferences = context.getSharedPreferences("PenjinCookie", 1);
    }

    public static CookieUtil getInstance(Context context) {
        synchronized (CookieUtil.class) {
            if (instance == null) {
                instance = new CookieUtil(context);
            }
        }
        return instance;
    }


    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }
}
