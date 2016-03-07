package com.penjin.android.view;

import com.penjin.android.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddFriendDialog extends Dialog implements android.view.View.OnClickListener {
	
	
	Context mContext;
	EditText editText;
	TextView confirm;
	
	private AddFriendDialogListenr listenr;
	public AddFriendDialog(Context context) {
		super(context);
	}

	public AddFriendDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public AddFriendDialog(Context context, int theme) {
		super(context, theme);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dialog_addfriend1);
		editText=(EditText)findViewById(R.id.friend_id);
		confirm=(TextView)findViewById(R.id.add_friend_confirm);
		confirm.setOnClickListener(this);
	}




	public interface AddFriendDialogListenr
	{
		public void confirm(String username);
	}


	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.add_friend_confirm:
			//valid
			String userId=editText.getEditableText().toString();
			if(listenr!=null)
			{
				listenr.confirm(userId);
			}
			this.dismiss();
			break;
		default:
			break;
		}
	}
	public void setListenr(AddFriendDialogListenr listenr) {
		this.listenr = listenr;
	}
	
	
}
