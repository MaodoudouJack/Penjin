package com.penjin.android.message.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public abstract class EMBaseFragment extends Fragment {

	protected InputMethodManager inputMethodManager;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		initView();
		setUpView();
	}

	/**
	 * 初始化控件
	 */
	protected abstract void initView();

	/**
	 * 设置属性，监听等
	 */
	protected abstract void setUpView();

	
	
	protected void hideSoftKeyboard() {
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
