package com.penjin.android.activity.kaoqin;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.penjin.android.R;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.service.UserService;
import com.penjin.android.view.PenjinAvatar;
import com.penjin.android.view.TitleBarView;

/**
 * 个人月考勤详情
 * Created by maotiancai on 2016/1/5.
 */
public class MonthKaoqinActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private String zongheRankPrefix = "个人考勤综合排名:";
    private String gongsiRankPrefix = "全公司排名:";

    private PenjinUser user;

    /**
     * 个人信息部分
     */
    PenjinAvatar avatar;
    TextView name;
    TextView department;
    TextView zhiwu;

    /**
     * 上班情况部分
     */
    TextView zhengchangChuqin;
    TextView jiaban;
    TextView qingjiaTianshu;


    /**
     * 日期部分
     */
    TextView year;
    TextView monthAndDay;
    TextView leftDayBtn;
    TextView rightDatyBtn;

    /**
     * 排名部分
     */
    TextView zongheRank;
    TextView gongsiRank;
    View paimingDetailBtn;

    /**
     * 数据区域部分
     */

    TextView cidaoTimes;
    View cidaoTimesBtn;

    TextView zaotuiTimes;
    View zaotuiTimesBtn;

    TextView jiabanTimes;
    View jiabanTimesBtn;

    TextView qingjiaTimes;
    View qingjiaTimesBtn;

    TextView kuanggongTimes;
    View kuanggongTimesBtn;

    TextView qiankaTimes;
    View qiankaTimesBtn;

    TextView waiqinTimes;
    View waiqinTimesBtn;

    TextView yingchuTimes;
    View yingchuTimesBtn;

    SwipeRefreshLayout swipeLayout;
    TitleBarView titleBar;
    private TitleBarView titleBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_month_kaoqin);
        initDate();
        initView();
        initTitleBar();
    }

    private void initDate() {
        user = UserService.getInstance(this.getApplicationContext()).getCurrentUser();
        if (user == null) {
            Toast.makeText(MonthKaoqinActivity.this, "用户尚未登陆！", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 初始化网络接口
     */
    private void initHttpModule() {

    }

    private void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        avatar = (PenjinAvatar) findViewById(R.id.avatar);
        name = (TextView) findViewById(R.id.name);
        department = (TextView) findViewById(R.id.department);
        zhiwu = (TextView) findViewById(R.id.zhiwu);

        zhengchangChuqin = (TextView) findViewById(R.id.zhengchangChuqin);
        jiaban = (TextView) findViewById(R.id.jiaban);
        qingjiaTianshu = (TextView) findViewById(R.id.qingjiaTianshu);

        year = (TextView) findViewById(R.id.year);
        monthAndDay = (TextView) findViewById(R.id.month_day);
        leftDayBtn = (TextView) findViewById(R.id.leftDayBtn);
        rightDatyBtn = (TextView) findViewById(R.id.rightDayBtn);

        zongheRank = (TextView) findViewById(R.id.zongheRank);
        gongsiRank = (TextView) findViewById(R.id.gongsiRank);
        paimingDetailBtn = findViewById(R.id.paimingDetailBtn);

        cidaoTimes = (TextView) findViewById(R.id.cidaoTimes);
        cidaoTimesBtn = findViewById(R.id.cidaoTimesBtn);
        zaotuiTimes = (TextView) findViewById(R.id.zaotuiTimes);
        zaotuiTimesBtn = findViewById(R.id.zaotuiTimesBtn);
        qingjiaTimes = (TextView) findViewById(R.id.qingjiaTimes);
        qingjiaTimesBtn = findViewById(R.id.qingjiaTimesBtn);
        kuanggongTimes = (TextView) findViewById(R.id.kuanggongTimes);
        kuanggongTimesBtn = findViewById(R.id.kuanggongTimesBtn);
        waiqinTimes = (TextView) findViewById(R.id.waiqinTimes);
        waiqinTimesBtn = findViewById(R.id.waiqinTimesBtn);
        yingchuTimes = (TextView) findViewById(R.id.yingchuTimes);
        yingchuTimesBtn = findViewById(R.id.yingchuTimesBtn);

        titleBar = (TitleBarView) findViewById(R.id.titleBar);

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
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cidaoTimesBtn:
                break;
            default:
                break;
        }
    }
}
