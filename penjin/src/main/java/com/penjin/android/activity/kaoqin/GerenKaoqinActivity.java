package com.penjin.android.activity.kaoqin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.util.NetUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.adapter.KaoqinListAdapter;
import com.penjin.android.constants.HttpConstants;
import com.penjin.android.domain.PenjinCompany;
import com.penjin.android.domain.PenjinDate;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.http.HttpService;
import com.penjin.android.service.UserService;
import com.penjin.android.utils.CalendarUtil;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.PenjinAvatar;
import com.penjin.android.view.TitleBarView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 个人考勤详情
 * Created by maotiancai on 2016/1/5.
 */
public class GerenKaoqinActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static String TAG = GerenKaoqinActivity.class.getName();

    private String zongheRankPrefix = "个人考勤综合排名:";
    private String gongsiRankPrefix = "全公司排名:";
    private CustomProgressDialog progressDialog;

    private PenjinUser user;
    private PenjinCompany company;
    private final static int REFRESH_LIST = 1;

    /**
     * 个人信息部分
     */
    PenjinAvatar avatar;
    TextView name;
    TextView department;
    TextView zhiwu;

    /**
     * 班次部分
     */
    TextView todayBanci;
    TextView jihuaTime;
    TextView hasTime;
    TextView workTime;


    /**
     * 日期部分
     */
    TextView year;
    TextView monthAndDay;
    TextView leftDayBtn;
    TextView rightDatyBtn;
    PenjinDate todayDate; //今天日期
    PenjinDate pickDate;//选择的日期
    Calendar calendar;

    /**
     * 排名部分
     */
    TextView zongheRank;
    TextView gongsiRank;
    View paimingDetailBtn;

    ListView workTimeList;
    KaoqinListAdapter adapter;
    SwipeRefreshLayout swipeLayout;
    TitleBarView titleBar;

    boolean isLoaded = false;
    boolean isLoadingInfo = false;
    HttpService httpService;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_LIST:
                    workTimeList.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };
    private TitleBarView titleBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_geren_kaoqin);
        initData();
        initView();
        initTitleBar();
        initDate();
        initHttpModule();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLoaded) {
            progressDialog.show();
            if (user.isInCompany()) {//判断员工是否绑定公司
                //判断网络是否正常
                if (NetUtils.isWifiConnection(this)) {
                    isLoadingInfo = true;
                    try {
                        progressDialog.show();
                        RequestParams requestParams = new RequestParams();
                        requestParams.put("staffNumber", user.getStaffNum());
                        requestParams.put("companyId", user.getCompanyId());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                        String date = dateFormat.format(todayDate.javaDate);
                        requestParams.put("date", todayDate.year + "-" + todayDate.month + "-" + todayDate.date);
                        requestParams.put("sn", HttpService.getInstance(this.getApplicationContext()).getSn());
                        System.out.println(user.getStaffNum() + " " + user.getCompanyId() + " " + date + " " + HttpService.getInstance(this.getApplicationContext()).getSn());
                        httpService.postRequest(this, HttpConstants.HOST + HttpConstants.KaoQinInfo, requestParams, new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                isLoadingInfo = false;
                                if (s == null) {
                                    Toast.makeText(GerenKaoqinActivity.this, "服务器内部错误", Toast.LENGTH_SHORT).show();
                                } else {
                                    System.out.println(s);
                                    Toast.makeText(GerenKaoqinActivity.this, "资源路径出错", Toast.LENGTH_SHORT).show();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, String s) {
                                isLoadingInfo = false;
                                try {
                                    JSONObject jo = new JSONObject(s);
                                    if (jo.getBoolean("result")) {
                                        JSONArray ja = jo.getJSONArray("data");
                                        if (ja != null && ja.length() > 0) {
                                            refreshList(ja);
                                            isLoaded = true;
                                        } else {
                                            Toast.makeText(GerenKaoqinActivity.this, "暂无数据信息", Toast.LENGTH_SHORT).show();
                                            isLoaded = true;
                                        }
                                    } else {
                                        isLoaded = false;
                                    }
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "网络不可用...", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "尚未绑定公司", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void initData() {
        user = UserService.getInstance(this.getApplicationContext()).getCurrentUser();
        if (user == null) {
            Toast.makeText(GerenKaoqinActivity.this, "用户尚未登陆！", Toast.LENGTH_SHORT).show();
            finish();
        }
        Toast.makeText(this, user.getPhone() + " " + user.getStaffNum(), Toast.LENGTH_SHORT).show();
    }

    private void initDate() {
        calendar = Calendar.getInstance();
        todayDate = new PenjinDate(calendar.getTime());//当前日期
        System.out.println(todayDate.year + " " + todayDate.month + " " + todayDate.date);
        pickDate = todayDate;//当前选择的日期
        setDateRegion(pickDate);
    }

    /**
     * 初始化网络接口
     */
    private void initHttpModule() {
        httpService = HttpService.getInstance(this.getApplicationContext());

    }

    private void initView() {
        progressDialog = CustomProgressDialog.createDialog(this);
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

        todayBanci = (TextView) findViewById(R.id.todayBanci);
        jihuaTime = (TextView) findViewById(R.id.jihuaTime);
        hasTime = (TextView) findViewById(R.id.hasTime);
        workTime = (TextView) findViewById(R.id.workTime);

        year = (TextView) findViewById(R.id.year);
        monthAndDay = (TextView) findViewById(R.id.month_day);
        leftDayBtn = (TextView) findViewById(R.id.leftDayBtn);
        rightDatyBtn = (TextView) findViewById(R.id.rightDayBtn);
        zongheRank = (TextView) findViewById(R.id.zongheRank);
        gongsiRank = (TextView) findViewById(R.id.gongsiRank);
        paimingDetailBtn = findViewById(R.id.paimingDetailBtn);

        titleBar = (TitleBarView) findViewById(R.id.titleBar);
        workTimeList = (ListView) findViewById(R.id.workTimeList);

        leftDayBtn.setOnClickListener(this);
        rightDatyBtn.setOnClickListener(this);
        String companyId = user.getCompanyId();
        if (companyId != null && !companyId.equals("") && !companyId.equals("null")) {
            company = UserService.getInstance(this.getApplicationContext()).getCurrentCompany();
            name.setText(company.getName());
            department.setText(company.getDepartment());
            zhiwu.setText(company.getPosition());
        }
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

    private void refreshList(JSONArray ja) throws Exception {
        JSONObject jo = ja.getJSONObject(0);
        String time = jo.optString("work");
        workTime.setText(time);
        adapter = new KaoqinListAdapter(ja, GerenKaoqinActivity.this);
        workTimeList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftDayBtn:
                if (!isLoadingInfo) {
                    if (pickDate.isSameDay(todayDate)) {
                        rightDatyBtn.setText("后一天");
                    }
                    pickDate = PenjinDate.getPreDay(pickDate);
                    setDateRegion(pickDate);
                    getGerenKaoqin();
                }
                break;
            case R.id.rightDayBtn:
                if (!isLoadingInfo) {
                    if (todayDate.isSameDay(pickDate)) {
                        //什么也不错
                    } else {
                        pickDate = PenjinDate.getNextDay(pickDate);
                        setDateRegion(pickDate);
                        if (pickDate.isSameDay(todayDate)) {
                            rightDatyBtn.setText("今  天");
                        } else {
                            rightDatyBtn.setText("后一天");
                        }
                        getGerenKaoqin();
                    }
                }
            default:
                break;
        }
    }

    //设置显示日期的区域
    private void setDateRegion(PenjinDate penjinDate) {
        year.setText(penjinDate.year + "年");
        monthAndDay.setText(penjinDate.month + "月" + penjinDate.date + "日" + "(" + penjinDate.weekDay + ")");
    }

    //根据当前选择的日期查询个人日考勤
    private void getGerenKaoqin() {
        if (NetUtils.isWifiConnection(this)) {
            isLoadingInfo = true;
            try {
                if (progressDialog != null)
                    progressDialog.show();
                RequestParams requestParams = new RequestParams();
                requestParams.put("staffNumber", user.getStaffNum());
                requestParams.put("companyId", user.getCompanyId());
                requestParams.put("date", pickDate.year + "-" + pickDate.month + "-" + pickDate.date);
                requestParams.put("sn", HttpService.getInstance(this.getApplicationContext()).getSn());
                httpService.postRequest(this, "http://192.168.0.19/App/KaoQinInfo", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        isLoadingInfo = false;
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        if (s == null) {
                            Toast.makeText(GerenKaoqinActivity.this, "服务器内部错误", Toast.LENGTH_SHORT).show();
                        } else {
                            System.out.println(s);
                            Toast.makeText(GerenKaoqinActivity.this, "资源路径出错", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        isLoadingInfo = false;
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        try {
                            JSONObject jo = new JSONObject(s);
                            if (jo.getBoolean("result")) {
                                JSONArray data = jo.getJSONArray("data");
                                if (data != null && data.length() > 0) {
                                    refreshList(data);
                                    isLoaded = true;
                                } else {
                                    Toast.makeText(GerenKaoqinActivity.this, "暂无数据信息", Toast.LENGTH_SHORT).show();
                                    isLoaded = false;
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                isLoaded = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "网络不可用...", Toast.LENGTH_SHORT).show();
        }
    }

}
