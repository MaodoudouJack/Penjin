package com.penjin.android.activity.gongzuo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.penjin.android.R;
import com.penjin.android.activity.PenjinBaseActivity;
import com.penjin.android.view.TitleBarView;
import com.penjin.android.widget.PenjinCircleNumber;

/**
 * Created by maotiancai on 2016/1/7.
 */
public class PenjinBaobiaoActivity extends PenjinBaseActivity {

    PenjinCircleNumber penjinCircleNumber;
    private TitleBarView titleBarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_penjin_baobiao);
        initView();
        initTitleBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        penjinCircleNumber.start();
    }

    private void initView() {
        penjinCircleNumber = (PenjinCircleNumber) findViewById(R.id.chidaoCircle);
        penjinCircleNumber.initData(11);
    }

    private void initTitleBar() {
        titleBarView = (TitleBarView) findViewById(R.id.titleBar);
        titleBarView.setTitleBarListener(new TitleBarView.TitleBarListener() {
            @Override
            public void left(View view) {
                finish();
            }

            @Override
            public void center(View view) {

            }

            @Override
            public void right(View view) {

            }
        });
    }
}