package com.penjin.android.view.penjin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.penjin.android.R;
import com.penjin.android.utils.BitmapUtils;
import com.penjin.android.view.CircleImageView;

/**
 * Created by maotiancai on 2016/1/21.
 */
public class ApplyListItem extends RelativeLayout {

    Context mContext;
    CircleImageView typeImage;
    TextView typeName;
    TextView typeTime;
    TextView typeDate;

    public ApplyListItem(Context context) {
        super(context);
        mContext = context;
        initView(context, null);
    }

    public ApplyListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context, attrs);
    }

    public ApplyListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.layout_item_apply_list, this);
        typeImage = (CircleImageView) findViewById(R.id.typeImage);
        typeName = (TextView) findViewById(R.id.typeName);
        typeTime = (TextView) findViewById(R.id.typeTime);
        typeDate = (TextView) findViewById(R.id.typeDate);
    }

    public void setTypeImage(int id) {
        typeImage.setImageBitmap(BitmapUtils.readBitMap(mContext, id));
    }

    public void setTypeName(String name) {
        this.typeName.setText(name);
    }

    public void setTypeTime(String time) {
        this.typeTime.setText(time);
    }

    public void setTypeDate(String date) {
        this.typeDate.setText(date);
    }
}
