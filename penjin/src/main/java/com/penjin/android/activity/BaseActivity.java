package com.penjin.android.activity;

import com.penjin.android.message.EMChatHelper;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.view.View;


/*
 * 基类Activity
 */
public class BaseActivity extends FragmentActivity{
	
	
	@Override
    protected void onResume() {
        super.onResume();
        EMChatHelper.getInstance().getNotifier().reset();
    }

    /**
     * 返回
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
