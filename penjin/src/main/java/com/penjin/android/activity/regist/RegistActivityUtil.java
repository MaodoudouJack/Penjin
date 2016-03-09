package com.penjin.android.activity.regist;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理注册页面和退出逻辑的工具类
 * Created by Administrator on 2016/3/9.
 */
public class RegistActivityUtil {

    public static List<Activity> list = new ArrayList<>(0);

    public static void push(Activity activity) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(0, activity);
    }

    public static void pop() {
        if (list == null) {
            list = new ArrayList<>();
        }
        if (list.size() > 0) {
            Activity activity = list.remove(0);
            activity.finish();
        }

    }

    public static void clear() {
        if (list == null) {
            list = new ArrayList<>();
        }
        for (int i = 0; i < list.size(); i++) {
            pop();
        }
    }

    public static void remove(){
        list.remove(0);
    }

}
