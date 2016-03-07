package com.penjin.android.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by maotiancai on 2015/12/11.
 */
public class PenjinCalendar extends GridView {


    public PenjinCalendar(Context context) {
        super(context);
    }

    public PenjinCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PenjinCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
