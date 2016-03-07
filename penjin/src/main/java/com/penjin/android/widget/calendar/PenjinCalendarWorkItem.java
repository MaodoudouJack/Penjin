package com.penjin.android.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.penjin.android.R;

import java.util.Random;

/**
 * Created by maotiancai on 2015/12/12.
 * 代表一个工作日的信息
 */
public class PenjinCalendarWorkItem extends FrameLayout {

    private int day = 0;//日期
    private int banci = 3;//划分的班次数目
    private int[] banciColors = new int[]{R.color.pj_workday_color, R.color.pj_qingjia_color, R.color.pj_xiuxi_color};//班次对应的颜色

    private LinearLayout work_item_background;
    private TextView work_item_day;

    public DateEntity dateEntity;

    public PenjinCalendarWorkItem(Context context) {
        super(context);
        initView(context);
    }

    public PenjinCalendarWorkItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PenjinCalendarWorkItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public PenjinCalendarWorkItem(Context context, int banci, int day) {
        super(context);
        this.banci = banci;
        this.day = day;
        initView(context);
    }

    public PenjinCalendarWorkItem(Context context, int banci, DateEntity dateEntity) {
        super(context);
        this.banci = banci;
        this.dateEntity = dateEntity;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_work_day, this);
        work_item_background = (LinearLayout) getRootView().findViewById(R.id.work_item_background);
        work_item_day = (TextView) findViewById(R.id.work_item_day);

        //判断日期是否是当月的
        if (dateEntity.isSelfMonthDate) {
            for (int i = 0; i < banci; i++) {
                LinearLayout ll = new LinearLayout(context);
                ll.setBackgroundColor(context.getResources().getColor(banciColors[new Random().nextInt(3)]));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                lp.weight = 1;
                ll.setLayoutParams(lp);
                work_item_background.addView(ll);
            }
            work_item_day.setText(dateEntity.day + "");
        } else {

        }
    }
}
