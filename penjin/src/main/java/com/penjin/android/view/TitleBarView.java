package com.penjin.android.view;

import com.penjin.android.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBarView extends RelativeLayout implements OnClickListener {

    private Drawable leftImage;
    private Drawable centerImage;
    private String centerStr;
    private Drawable rightImage;
    private Drawable rightExtraImage;

    private ImageView leftView;
    private TextView centerView;
    private ImageView rightView;
    private ImageView rightExtraView;

    private View leftWrapper;
    private View centerWrapper;
    private View rightWrapper;
    private View rightExtraWrapper;

    private int backGroudColor;
    private int textColor;

    private TitleBarRightExtraListener rightExtraListener;
    private TitleBarListener titleBarListener;

    public TitleBarView(Context context) {
        super(context);
        initView(context, null);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);

    }

    @SuppressLint("NewApi")
    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.TitleBarView);
            leftImage = ta.getDrawable(R.styleable.TitleBarView_leftImage);
            centerImage = ta.getDrawable(R.styleable.TitleBarView_centerImage);
            centerStr = ta.getString(R.styleable.TitleBarView_centerText);
            rightImage = ta.getDrawable(R.styleable.TitleBarView_rightImage);
            rightExtraImage = ta.getDrawable(R.styleable.TitleBarView_rightExtraImage);
            backGroudColor = ta.getColor(R.styleable.TitleBarView_titleBarBackGroudColor, context.getResources().getColor(R.color.titlebar_backgroud_blue));
            textColor = ta.getColor(R.styleable.TitleBarView_titleBarTextColor, context.getResources().getColor(R.color.white));
            ta.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.layout_titlebar, this);

        findViewById(R.id.app_title_bar).setBackgroundColor(backGroudColor);//设置背景颜色
        leftView = (ImageView) findViewById(R.id.titlebar_left_img);
        centerView = (TextView) findViewById(R.id.titlebar_center_text);
        centerView.setTextColor(textColor);//设置中间字体颜色
        rightView = (ImageView) findViewById(R.id.titlebar_right_img);

        leftWrapper = findViewById(R.id.titlebar_left);
        centerWrapper = findViewById(R.id.titlebar_center);
        rightWrapper = findViewById(R.id.titlebar_right);

        rightExtraView = (ImageView) findViewById(R.id.titlebar_right_extra_img);
        rightExtraWrapper = findViewById(R.id.titlebar_right_extra);

        if (rightExtraImage != null) {
            rightExtraView.setImageDrawable(rightExtraImage);
            rightExtraWrapper.setVisibility(View.VISIBLE);
            rightExtraWrapper.setOnClickListener(this);
        }

        if (leftImage != null) {
            leftView.setImageDrawable(leftImage);
        }
        if (centerImage != null) {
            centerView.setBackground(centerImage);
        }
        if (rightImage != null) {
            rightView.setImageDrawable(rightImage);
        }
        if (centerStr != null) {
            centerView.setText(centerStr);
        }
        leftWrapper.setOnClickListener(this);
        centerWrapper.setOnClickListener(this);
        rightWrapper.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.titlebar_left:
                if (this.titleBarListener != null) {
                    titleBarListener.left(arg0);
                }
                break;
            case R.id.titlebar_center:
                if (this.titleBarListener != null) {
                    titleBarListener.center(arg0);
                }
                break;
            case R.id.titlebar_right:
                if (this.titleBarListener != null) {
                    titleBarListener.right(arg0);
                }
                break;
            case R.id.titlebar_right_extra:
                if (this.rightExtraListener != null) {
                    rightExtraListener.click(arg0);
                }
                break;
            default:
                break;
        }
    }

    public void setTitleBarListener(TitleBarListener titleBarListener) {
        this.titleBarListener = titleBarListener;
    }


    public void setRightExtraListener(TitleBarRightExtraListener rightExtraListener) {
        this.rightExtraListener = rightExtraListener;
    }


    public interface TitleBarListener {
        public void left(View view);

        public void center(View view);

        public void right(View view);
    }

    public void setCenterText(String str) {
        if (centerView != null) {
            centerView.setText(str);
        }
    }

    public interface TitleBarRightExtraListener {
        public void click(View view);
    }
}
