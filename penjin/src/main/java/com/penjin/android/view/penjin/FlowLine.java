package com.penjin.android.view.penjin;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.penjin.android.R;

/**
 * 绘制流程中间的细线
 * Created by maotiancai on 2016/1/14.
 */
public class FlowLine extends RelativeLayout {

    private int gray = R.color.gray_8f;
    private int blue = R.color.pj_text_blue;
    private LinearLayout line;

    public FlowLine(Context context) {
        super(context);
        init(context, null);
    }

    public FlowLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlowLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.layout_item_pj_flow_line, this);
        line = (LinearLayout) findViewById(R.id.flow_line);
    }


    public void setIsDone(boolean isDone) {
        if (isDone) {
            line.setBackgroundColor(getContext().getResources().getColor(blue));
        } else {
            line.setBackgroundColor(getContext().getResources().getColor(gray));
        }

    }


}
