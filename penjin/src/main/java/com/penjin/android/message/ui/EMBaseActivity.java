package com.penjin.android.message.ui;

import com.penjin.android.message.EMChatHelper;

import android.app.Activity;
import android.view.View;

public class EMBaseActivity extends Activity {

	@Override
	protected void onResume() {
		super.onResume();
		// onresume时，取消notification显示
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

}
