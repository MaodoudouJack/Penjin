package com.penjin.android.view;

import com.penjin.android.R;
import com.penjin.android.utils.ScreenUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

public class AddFriendPopupWindow  implements OnClickListener, OnTouchListener {

	private PopupWindow popupWindow;
	
	private AddFriendPopupWindowInterface listener;
	
	public AddFriendPopupWindow(Context context)
	{
		
		View view=LayoutInflater.from(context).inflate(R.layout.layout_popwindow_addfriend, null);
		view.findViewById(R.id.popwin_addfriend).setOnClickListener(this);
		view.findViewById(R.id.popwin_saoyisao).setOnClickListener(this);
		int width=ScreenUtils.getScreenWidth(context);
		popupWindow=new PopupWindow(view, width/3, LayoutParams.WRAP_CONTENT,true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.popwin_anim_style1);
		popupWindow.setTouchInterceptor(this);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
	}
	
	public void showAt(View view)
	{
		popupWindow.showAsDropDown(view,view.getWidth()-popupWindow.getWidth(),0);
	}

	public interface AddFriendPopupWindowInterface
	{
		public void addFriend();
		
		public void sao();
	}

	@Override
	public void onClick(View arg0) {
		System.out.println("点击事件");
		switch (arg0.getId()) {
		case R.id.popwin_addfriend:
			if(this.listener!=null)
			{
				listener.addFriend();
				if(this.popupWindow.isShowing())
				{
					popupWindow.dismiss();
				}
			}
			break;
		case R.id.popwin_saoyisao:
			if(this.listener!=null)
			{
				listener.sao();
				if(this.popupWindow.isShowing())
				{
					popupWindow.dismiss();
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		return false;
	}

	public void setListener(AddFriendPopupWindowInterface listener) {
		this.listener = listener;
	}
	
	

}
