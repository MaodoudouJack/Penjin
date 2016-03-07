package com.penjin.android.widget.calendar;

import java.util.List;

/**
 * Created by maotiancai on 2016/1/19.
 */
public class PenjinDateEntityList {

    public List<DateEntity> mDateEntityList;
    public int startIndex;
    public int endIndex;

    public PenjinDateEntityList(List<DateEntity> mDateEntityList, int startIndex, int endIndex) {
        this.mDateEntityList = mDateEntityList;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
}
