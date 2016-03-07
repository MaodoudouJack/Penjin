package com.penjin.android.message.contact;

import com.penjin.android.R;
import com.penjin.android.message.chat.EMChatActivity;
import com.penjin.android.message.contact.EMContactListFragment.EaseContactListItemClickListener;
import com.penjin.android.message.domain.EaseUser;
import com.penjin.android.message.utils.EaseConstant;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class EMContactActivity extends Activity{
	
	
	EMContactListFragment contactFragment;
	EaseContactListItemClickListener clickListener=new EaseContactListItemClickListener() {
		
		@Override
		public void onListItemClicked(EaseUser user) {
			Intent intent =new Intent(EMContactActivity.this,EMContactDetailActivity.class);
			Bundle extras=new Bundle();
			extras.putString(EaseConstant.EXTRA_USER_ID, user.getUsername());
			extras.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
			intent.putExtras(extras);
			startActivity(intent);
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_geren_friends);
		contactFragment=new EMContactListFragment();
		contactFragment.setContactListItemClickListener(clickListener);
		FragmentTransaction tf=getFragmentManager().beginTransaction();
		tf.add(R.id.fragment_container,contactFragment);
		tf.commit();
	}
	
}
