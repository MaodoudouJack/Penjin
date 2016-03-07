package com.penjin.android.activity.gongzuo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.penjin.android.R;
import com.penjin.android.activity.PenjinBaseActivity;
import com.penjin.android.activity.apply.ChuchaiApplyDetailActivity;
import com.penjin.android.activity.apply.MyApplyDetailActivity;
import com.penjin.android.view.TitleBarView;

/**
 * 工作台：审批管理
 * Created by maotiancai on 2016/1/6.
 */
public class PenjinApplyActivity extends PenjinBaseActivity implements View.OnClickListener {


    private View chuchai;
    private View daka;
    private View jiaban;
    private View buka;
    private View qingjia;
    private View tiaoxiu;
    private TitleBarView titleBarView;

    private View appling;
    private View applyDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_penjin_apply);
        initView();
        initTitleBar();
    }

    private void initView() {
        applyDone = findViewById(R.id.applyDone);
        applyDone.setOnClickListener(this);
        daka = findViewById(R.id.daka);
        chuchai = findViewById(R.id.chuchai);
        daka.setOnClickListener(this);
        chuchai.setOnClickListener(this);
        jiaban = findViewById(R.id.jiaban);
        jiaban.setOnClickListener(this);
        buka = findViewById(R.id.buka);
        buka.setOnClickListener(this);
        qingjia = findViewById(R.id.qingjia);
        qingjia.setOnClickListener(this);
        tiaoxiu = findViewById(R.id.tiaoxiu);
        tiaoxiu.setOnClickListener(this);
        appling = findViewById(R.id.appling);
        appling.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.applyDone:
                intent = new Intent(this, MyApplyDetailActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            case R.id.appling:
                intent = new Intent(this, MyApplyDetailActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.daka:
                intent = new Intent(this, YidiKaoqinApplyActivity.class);
                startActivity(intent);
                break;
            case R.id.chuchai:
                intent = new Intent(this, ChuchaiApplyActivity.class);
                startActivity(intent);
                break;
            case R.id.jiaban:
                intent = new Intent(this, JiabanApplyActivity.class);
                startActivity(intent);
                break;
            case R.id.buka:
                intent = new Intent(this, BukaApplyActivity.class);
                startActivity(intent);
                break;
            case R.id.qingjia:
                intent = new Intent(this, QingjiaApplyActivity.class);
                startActivity(intent);
                break;
            case R.id.tiaoxiu:
                intent = new Intent(this, TiaoxiuApplyActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
