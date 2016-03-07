package com.penjin.android.widget.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by maotiancai on 2015/12/11.
 */
public class PenjinCalendarAdapter extends BaseAdapter {


    private Context mContext;
    private Resources mRes;

    /**
     * 日期实体集合
     */
    private List<DateEntity> mDataList;
    /**
     * 因为position是从0开始的，所以用当做一个中间者，用来加1.以达到判断除数时，为哪个星期
     */
    private int temp;

    public PenjinCalendarAdapter(Context context, Resources resources) {
        mContext = context;
        mRes = resources;
    }

    /**
     * 设置日期数据
     */
    public void setDateList(List<DateEntity> dataList) {
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        if (mDataList == null)
            return 0;
        else return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PenjinCalendarWorkItem item = new PenjinCalendarWorkItem(mContext, 3, mDataList.get(position));
        return item;
    }
}
