package com.penjin.android.message.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.easemob.chat.EMMessage;
import com.penjin.android.message.domain.EaseUser;

public class EaseUI {

    private boolean isInited = false;

    private static EaseUI instance;

    private EaseSettingsProvider settingsProvider;

    private EaseUserProfileProvider userProvider;

    private EaseNotifier notifier;

    /**
     * 用来记录注册了eventlistener的foreground Activity
     */
    private List<Activity> activityList = new ArrayList<Activity>();

    public void pushActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(0, activity);
        }
    }

    public void popActivity(Activity activity) {
        activityList.remove(activity);
    }

    private EaseUI() {
        settingsProvider = new DefaultSettingsProvider();
        notifier = new EaseNotifier();
    }

    public void init(Context context) {
        if (!isInited) {
            this.notifier.init(context);
        }
        isInited = true;
    }

    public synchronized static EaseUI getInstance() {
        if (instance == null) {
            instance = new EaseUI();
        }
        return instance;
    }

    public static interface EaseSettingsProvider {
        boolean isMsgNotifyAllowed(EMMessage message);

        boolean isMsgSoundAllowed(EMMessage message);

        boolean isMsgVibrateAllowed(EMMessage message);

        boolean isSpeakerOpened();
    }

    public static interface EaseUserProfileProvider {
        /**
         * 返回此username对应的user
         *
         * @param username 环信id
         * @return
         */
        EaseUser getUser(String username);
    }

    public EaseSettingsProvider getSettingsProvider() {
        return settingsProvider;
    }

    public void setSettingsProvider(EaseSettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    public EaseUserProfileProvider getUserProfileProvider() {
        return userProvider;
    }

    public void setEaseUserProfileProvider(EaseUserProfileProvider userProvider) {
        this.userProvider = userProvider;
    }

    public EaseNotifier getNotifier() {
        return notifier;
    }

    public void setNotifier(EaseNotifier notifier) {
        this.notifier = notifier;
    }

    public boolean hasForegroundActivies() {
        System.out.println("在前端的activity有" + activityList.size() + "个");
        return activityList.size() != 0;
    }

    protected class DefaultSettingsProvider implements EaseSettingsProvider {

        @Override
        public boolean isMsgNotifyAllowed(EMMessage message) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean isMsgSoundAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isMsgVibrateAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isSpeakerOpened() {
            return true;
        }


    }
}
