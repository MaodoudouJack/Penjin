package com.penjin.android.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.penjin.android.R;
import com.penjin.android.utils.ScreenUtils;

/**
 * Created by maotiancai on 2016/1/13.
 */
public class ContextAlterPopupWindow implements View.OnTouchListener, View.OnClickListener {

    private PopupWindow popupWindow;

    private ContextAlertPopupWindowInterface contextAlertPopupWindowInterface;

    private int popHeight;

    public ContextAlterPopupWindow(Context context) {
        popHeight = ScreenUtils.dp2px(context, 40);
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_popwindow_context_alert, null);
        rootView.findViewById(R.id.modify).setOnClickListener(this);
        rootView.findViewById(R.id.delete).setOnClickListener(this);
        popupWindow = new PopupWindow(rootView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.PopupAnimationAlpha);
        popupWindow.setTouchInterceptor(this);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify:
                if (this.contextAlertPopupWindowInterface != null) {
                    this.contextAlertPopupWindowInterface.onModify();
                }
                break;
            case R.id.delete:
                if (this.contextAlertPopupWindowInterface != null) {
                    this.contextAlertPopupWindowInterface.onDelete();
                }
                break;
            default:
                break;
        }
    }

    public void setContextAlertPopupWindowInterface(ContextAlertPopupWindowInterface contextAlertPopupWindowInterface) {
        this.contextAlertPopupWindowInterface = contextAlertPopupWindowInterface;
    }

    public interface ContextAlertPopupWindowInterface {
        public void onModify();

        public void onDelete();
    }

    public void showAtView(View view) {
        this.popupWindow.showAsDropDown(view, view.getWidth() / 2, -(view.getHeight() + popHeight));
    }

    public void dismiss() {
        if (this.popupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }
}
