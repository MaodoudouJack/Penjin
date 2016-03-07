package com.penjin.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penjin.android.R;

/**
 * Created by maotiancai on 2016/1/13.
 */
public class PjChuchaiItem extends LinearLayout {

    public int itemId;//序列标号
    public TextView didianDetail;//地点详情

    public PjChuchaiItem(Context context) {
        super(context);
        init(context);
    }

    public PjChuchaiItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PjChuchaiItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_item_pj_chuchai, this);
        didianDetail = (TextView) findViewById(R.id.didianDetail);
    }

    public void setDidianDetail(String detail) {
        this.didianDetail.setText(detail);
    }

    public String getDidianDetail() {
        return this.didianDetail.getText().toString();
    }

    public void setItemId(int id) {
        this.itemId = id;
    }

    public int getId() {
        return this.itemId;
    }
}
