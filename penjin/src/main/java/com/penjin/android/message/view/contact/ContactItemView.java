package com.penjin.android.message.view.contact;

import com.penjin.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * 这个是联系人Activity里的：公司联系人，个人好友的选项
 */
public class ContactItemView extends LinearLayout{

	private Animation alertAnimation;
	
    private TextView unreadMsgView;

    public ContactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ContactItemView(Context context) {
        super(context);
        init(context, null);
    }
    
    protected void init(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ContactItemView);
        String name = ta.getString(R.styleable.ContactItemView_contactItemName);
        Drawable image = ta.getDrawable(R.styleable.ContactItemView_contactItemImage);
        ta.recycle();
        LayoutInflater.from(context).inflate(R.layout.em_widget_contact_item, this);//inflater默认会把渲染好的view放在根目录里，而这个根View就是继承了ViewGroup的ContactItemView即第三个参数默认是true
        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        unreadMsgView = (TextView) findViewById(R.id.unread_msg_number);
        TextView nameView = (TextView) findViewById(R.id.name);
        if(image != null){
            avatar.setImageDrawable(image);
        }
        nameView.setText(name);
        alertAnimation=new RotateAnimation(-10.0f, +10.0f, 170f, 170f);
        alertAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alertAnimation.setRepeatCount(10);
        alertAnimation.setDuration(400);
    }
    
    public void setUnreadCount(int unreadCount){
        unreadMsgView.setText(String.valueOf(unreadCount));
    }
    
    public void showUnreadMsgView(){
        unreadMsgView.setVisibility(View.VISIBLE);
    }
    public void hideUnreadMsgView(){
        unreadMsgView.setVisibility(View.INVISIBLE);
    }
    
    
    public void setAlertAnimator(Animation animation)
    {
    	this.alertAnimation=animation;
    }
    
    public void showAlert()
    {
    	if(this.alertAnimation!=null)
    	{
    		unreadMsgView.startAnimation(alertAnimation);
    	}
    }
    
    public void stopAlert()
    {
    	if(this.alertAnimation!=null);
    	unreadMsgView.startAnimation(alertAnimation);
    }
}
