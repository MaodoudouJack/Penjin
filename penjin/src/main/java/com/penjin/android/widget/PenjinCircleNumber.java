package com.penjin.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.penjin.android.R;

import org.w3c.dom.Text;

import java.lang.reflect.Type;

/**
 * Created by maotiancai on 2016/1/9.
 */
public class PenjinCircleNumber extends RelativeLayout {

    private int number;
    private int strokeColor = Color.BLACK;
    private int backgroundColor = Color.GRAY;
    private float strokeWidth = getResources().getDimension(com.mikhaellopez.circularprogressbar.R.dimen.default_stroke_width);
    private float backgroundStrokeWidth = getResources().getDimension(com.mikhaellopez.circularprogressbar.R.dimen.default_background_stroke_width);

    CircularProgressBar circularProgressBar;
    RiseNumberTextView numberText;

    public PenjinCircleNumber(Context context) {
        super(context);
    }

    public PenjinCircleNumber(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PenjinCircleNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.layout_widget_penjin_circlenumber, this);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar);
        numberText = (RiseNumberTextView) findViewById(R.id.numberText);
        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.PenjinCircleNumber);
        try {
            strokeColor = ta.getInt(R.styleable.PenjinCircleNumber_pj_circlenum_stroke_color, strokeColor);
            backgroundColor = ta.getInt(R.styleable.PenjinCircleNumber_pj_circlenum_backgroud_color, backgroundColor);
            strokeWidth = ta.getDimension(R.styleable.PenjinCircleNumber_pj_circlenum_stroke_width, strokeWidth);
            backgroundStrokeWidth = ta.getDimension(R.styleable.PenjinCircleNumber_pj_circlenum_background_width, backgroundStrokeWidth);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ta.recycle();
        }
        numberText.setTextColor(strokeColor);
        circularProgressBar.setProgressBarWidth(strokeWidth);
        circularProgressBar.setBackgroundProgressBarWidth(backgroundStrokeWidth);
        circularProgressBar.setBackgroundColor(backgroundColor);
        circularProgressBar.setColor(strokeColor);

    }


    public void initData(int number) {
        this.number = number;
        this.numberText.withNumber(this.number);
        this.numberText.setDuration(3000);
    }


    public void start() {
        circularProgressBar.setProgressWithAnimation(100, 3000);
        this.numberText.start();
    }

}
