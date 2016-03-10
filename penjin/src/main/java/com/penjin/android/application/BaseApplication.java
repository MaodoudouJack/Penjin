package com.penjin.android.application;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.penjin.android.activity.photo.util.Res;
import com.penjin.android.constants.PenjinConstants;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.message.EMChatHelper;
import com.penjin.android.service.UserService;
import com.penjin.android.utils.AppUtils;

import android.app.Application;
import android.content.Context;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class BaseApplication extends Application {

    private static Context context;
    private static BaseApplication instance;
    private static String TAG = BaseApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = this;
        initPush();
        initEMChat();
        initDB();
        init();
    }

    private void initPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    private void initEMChat() {
        EMChatHelper.getInstance().init(this);
    }

    private void init() {
        String packageName = AppUtils.getCurrentProcessName(this);
        if (packageName != null && packageName.equals("com.penjin.android")) {
            //SDKInitializer.initialize(getApplicationContext());
            Res.init(this);
        }
    }

    private void initDB() {
        // ActiveAndroid.initialize(this);
        //insert test Data
        PenjinConstants.createAppDir();
    }

    public static Context getContext() {
        return context;
    }

    public static BaseApplication getInstance() {
        return instance;
    }

}
