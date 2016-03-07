package com.penjin.android.activity;

import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;
import com.penjin.android.R;
import com.penjin.android.message.EMChatHelper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistUserActivity extends Activity implements OnClickListener {
	
	private static final int REGIST_SUCCESS=1;
	private static final int REGIST_FAILURE_WEB=2;
	private static final int REGIST_FAILURE_EXIST=3;
	private static final int REGIST_FAILURE_RIGHT=4;
	private static final int RIGIST_FAILURE_INNER=5;
	
	Handler handler=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REGIST_SUCCESS:
				Toast.makeText(RegistUserActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case REGIST_FAILURE_WEB:
				Toast.makeText(RegistUserActivity.this, "注册失败:网络异常", Toast.LENGTH_SHORT).show();
				break;
			case REGIST_FAILURE_EXIST:
				Toast.makeText(RegistUserActivity.this, "注册失败:用户已存在", Toast.LENGTH_SHORT).show();
				break;
			case REGIST_FAILURE_RIGHT:
				Toast.makeText(RegistUserActivity.this, "注册失败：权限不足", Toast.LENGTH_SHORT).show();
				break;
			case RIGIST_FAILURE_INNER:
				Toast.makeText(RegistUserActivity.this, "注册失败：内部异常", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
		
	};

	EditText username;
	EditText password;
	TextView regist;
	TextView xieyi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_regist);
		initView();
	}

	private void initView() {
		username = (EditText) findViewById(R.id.editUsername);
		password = (EditText) findViewById(R.id.editPassword);
		regist = (TextView) findViewById(R.id.regist);
		xieyi = (TextView) findViewById(R.id.xieyi);

		// 添加下划线
		String str1 = xieyi.getText().toString();
		xieyi.setText(Html.fromHtml("<u>" + str1 + "</u>"));
		
		xieyi.setOnClickListener(this);
		regist.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.regist:
			if(!NetUtils.hasNetwork(this))
			{
				Toast.makeText(this, "网络断开，请检查您的网络", Toast.LENGTH_SHORT).show();
				return ;
			}
			if(!checkRegist())
			{
				Toast.makeText(this, "请输入正确的手机号和密码", Toast.LENGTH_SHORT).show();
				return ;
			}
			registUser();
			break;
		default:
			break;
		}
	}
	
	private void registUser() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance().createAccountOnServer(
							username.getEditableText().toString(),
							password.getEditableText().toString());
					handler.sendEmptyMessage(REGIST_SUCCESS);
				} catch (final EaseMobException e) {
					// 注册失败
					int errorCode = e.getErrorCode();
					if (errorCode == EMError.NONETWORK_ERROR) {
						handler.sendEmptyMessage(REGIST_FAILURE_WEB);
					} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
						// Toast.makeText(getApplicationContext(), "用户已存在！",
						// Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(REGIST_FAILURE_EXIST);
					} else if (errorCode == EMError.UNAUTHORIZED) {
						// Toast.makeText(getApplicationContext(), "注册失败，无权限！",
						// Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(REGIST_FAILURE_RIGHT);
					} else {
						// sToast.makeText(getApplicationContext(), "注册失败: " +
						// e.getMessage(), Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(RIGIST_FAILURE_INNER);
					}
				}
			}
		});
		thread.start();
	}
	
	private boolean checkRegist() {
		String uname = username.getEditableText().toString();
		String upwd = username.getEditableText().toString();
		if (uname == null || upwd == null||uname.isEmpty()||upwd.isEmpty()) {
			return false;
		}
		String telephoneRegex="[1][3458]\\d{9}";
		if(uname.matches(telephoneRegex))
			return true;
		return false;
	}
	
}
