package com.penjin.android.view.penjin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.penjin.android.R;
import com.penjin.android.utils.ScreenUtils;

/**
 * Created by maotiancai on 2016/1/14.
 */
public class FlowMan extends RelativeLayout {

    private int blue = R.color.pj_text_blue;//进行中的颜色
    private int gray = R.color.gray_8f;//未完成的颜色
    private int green = R.color.cpb_green;//完成了的颜色

    private int doneIcon = R.drawable.arrow_8_right;//完成了的图标id
    private int unDoneIcon = R.drawable.arrow_8_right;//未完成的图标id

    private int doneBackground = R.drawable.common_editor_bg_gray_border;
    private int unDoneBackground = R.drawable.common_editor_bg_blue_border;

    private TextView name;
    private boolean isDone = false;

    public FlowMan(Context context) {
        super(context);
        init(context, null);
    }

    public FlowMan(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlowMan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet as) {
        LayoutInflater.from(context).inflate(R.layout.layout_item_pj_flow_man, this);
        name = (TextView) findViewById(R.id.flow_name);
    }

    /**
     * 为该item绑定数据
     */
    public void setData() {

    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
        if (isDone) {
            this.name.setTextColor(getContext().getResources().getColor(blue));
            //((GradientDrawable) this.getBackground()).setStroke(ScreenUtils.dp2px(getContext(), 1), getContext().getResources().getColor(blue));
        } else {
            this.name.setTextColor(getContext().getResources().getColor(gray));
            // ((GradientDrawable) this.getBackground()).setStroke(ScreenUtils.dp2px(getContext(), 1), getContext().getResources().getColor(gray));
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
