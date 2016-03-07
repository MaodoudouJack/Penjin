package com.penjin.android.message.contact;

import com.easemob.chat.EMChatManager;
import com.penjin.android.R;
import com.penjin.android.message.chat.EMChatActivity;
import com.penjin.android.message.utils.EaseConstant;
import com.penjin.android.message.vedio.VoiceCallActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class EMContactDetailActivity extends Activity {

	private Bundle extras;
	private TextView usernameTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		extras=getIntent().getExtras();
		setContentView(R.layout.layout_activity_contact_detail);
		initView();
	}
	
	private  void initView()
	{
		usernameTextView=(TextView)findViewById(R.id.background_name);
		usernameTextView.setText(extras.getString(EaseConstant.EXTRA_USER_ID));
	}
	
	
	public  void sendMsg(View view)
	{
		Intent intent =new Intent(this,EMChatActivity.class);
		intent.putExtras(extras);
		startActivity(intent);
		finish();
	}
	
	public void sendVoice(View view)
	{
		if (!EMChatManager.getInstance().isConnected()) {
			Toast.makeText(this, R.string.not_connect_to_server, Toast.LENGTH_SHORT)
					.show();
		} else {
			startActivity(new Intent(this, VoiceCallActivity.class)
					.putExtra("username", extras.getString(EaseConstant.EXTRA_USER_ID)).putExtra(
							"isComingCall", false));
		}
	}
	
	public void sendDing(View view)
	{
		
	}
	
	public void back(View view)
	{
		finish();
	}
	
}
