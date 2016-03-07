package com.penjin.android.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Calendar;

/**
 * Created by maotiancai on 2016/1/12.
 */
public class TimeView extends TextView {

    private boolean isShowOnWindow = false;

    private TimeViewHandler hanler;

    public TimeView(Context context) {
        super(context);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        hanler = new TimeViewHandler(this);
    }

    private static class TimeViewHandler extends Handler {
        private WeakReference<TimeView> timeViewWeakReference;

        public TimeViewHandler(TimeView timeView) {
            super();
            timeViewWeakReference = new WeakReference<TimeView>(timeView);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                TimeView timeView = timeViewWeakReference.get();
                if (timeView != null) {
                    timeView.refreshTime();
                    this.sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    }

    private void refreshTime() {
        Calendar calendar = Calendar.getInstance();
        this.setText(String.format("%d:%d:%d",
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isShowOnWindow)
            this.hanler.sendEmptyMessage(1);
    }

}
