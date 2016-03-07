package com.penjin.android.service;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 手机设置服务类
 * Created by maotiancai on 2016/1/8.
 */
public class SettingService {

    private SharedPreferences sharedPreferences;
    private static SettingService instance;

    private SettingService(Context context) {
        context.getSharedPreferences("PenjinSetting", 1);
    }

    public static SettingService getInstance(Context context) {
        synchronized (SettingService.class) {
            if (instance == null) {
                instance = new SettingService(context);
            }
        }
        return instance;
    }


    public void setAutoLogin(boolean isAutoLogin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("autoLogin", isAutoLogin);
        editor.commit();
    }

    public boolean isAutoLogin() {
        return this.sharedPreferences.getBoolean("autoLogin", false);
    }

}
