package com.penjin.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penjin.android.R;

import org.json.JSONObject;


/**
 * 个人考勤界面的ListView中的Item
 * Created by maotiancai on 2016/1/6.
 */
public class PenjinKaoqinListItem extends LinearLayout {

    TextView idText;
    TextView kaoqinType;
    TextView kaoqinTime;
    TextView dakaTime;
    ImageView dakaImage;
    TextView dakaResult;
    TextView youxiaoTime;

    public PenjinKaoqinListItem(Context context) {
        super(context);
    }

    public PenjinKaoqinListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PenjinKaoqinListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public PenjinKaoqinListItem(Context context, JSONObject jo, int id) {
        super(context);
        initView(context, jo, id);
    }

    private void initView(Context context, JSONObject jo, int id) {
        LayoutInflater.from(context).inflate(R.layout.layout_list_item_kaoqin, this);
        idText = (TextView) findViewById(R.id.idText);
        kaoqinType = (TextView) findViewById(R.id.kaoqinType);
        kaoqinTime = (TextView) findViewById(R.id.kaoqinTime);
        dakaTime = (TextView) findViewById(R.id.dakaTime);
        dakaImage = (ImageView) findViewById(R.id.dakaImage);
        dakaResult = (TextView) findViewById(R.id.dakaResult);
        youxiaoTime = (TextView) findViewById(R.id.youxiaoTime);

        idText.setText(id + "");
        kaoqinType.setText(jo.optString("type"));
        kaoqinTime.setText(jo.optString("needworktime"));
        String time = jo.optString("signintime", null);
        dakaTime.setText(time);
        String status = jo.optString("status");
        dakaResult.setText(status);
        /**
         * 更换打卡结果提示图标
         *//*
        if (status != null && status.contains("正常")) {

        } else if (status != null && status.contains("异常")) {
            dakaResult.setText(jo.optString("考勤异常"));
        } else {
            dakaResult.setText(jo.optString("未打卡"));
        }*/

        //解析考勤时间段
        String needstarttime = jo.optString("needstarttime");
        String needendtime = jo.optString("needendtime");
        youxiaoTime.setText(needstarttime + "至" + needendtime + "打卡有效");
    }

}
