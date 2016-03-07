package com.penjin.android.message.chat;

import com.penjin.android.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class EMChatActivity  extends Activity{
	
	public static EMChatActivity activityInstance;
	private static String TAG=EMChatActivity.class.getName();
	
	private EMChatFragment emChatFragment;
	
	String toChatUsername;
	int chatType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activityInstance=this;
		setContentView(R.layout.layout_activity_chat);
		
		toChatUsername=getIntent().getExtras().getString("userId");
		
		initFragment();
	}
	
	private void initFragment()
	{
		FragmentTransaction ft=getFragmentManager().beginTransaction();
		emChatFragment=new EMChatFragment();
		emChatFragment.setArguments(getIntent().getExtras());
		ft.add(R.id.chatframent_container, emChatFragment);
		ft.commit();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		//super.onNewIntent(intent);
		String username=intent.getStringExtra("userId");
		if(username!=null&&username.equals(toChatUsername))
		{
			super.onNewIntent(intent);
		}else if(username!=null)
		{
			finish();
			startActivity(intent);
		}
	}
	
}
