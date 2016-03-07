package com.penjin.android.activity.apply;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.util.NetUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.constants.HttpConstants;
import com.penjin.android.domain.PenjinBill;
import com.penjin.android.domain.PenjinCompany;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.http.HttpConstant;
import com.penjin.android.http.HttpService;
import com.penjin.android.service.UserService;
import com.penjin.android.utils.CalendarUtil;
import com.penjin.android.utils.JsonUtils;
import com.penjin.android.view.CircleImageView;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.TitleBarView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 出差申请详情
 * Created by maotiancai on 2016/1/20.
 */
public class ChuchaiApplyDetailActivity extends Activity {

    CustomProgressDialog progressDialog;

    TitleBarView titleBarView;//标题栏

    TextView billNumber;//单据编号
    TextView billTypeName;//单据类型
    TextView type;//子种类

    TextView name;//用户公司真实姓名
    TextView department;//部门
    TextView zhiwu;//用户职务
    TextView passDay;//表单已经经过的天数

    TextView timeName;//申请单时间描述
    TextView timeDays;//申请单已经经历过的时间
    TextView startTime;
    TextView endTime;
    TextView detail;
    TextView fujian;
    LinearLayout flowLineWrapper; //审批流程的父控件

    PenjinUser user;
    PenjinCompany company;
    CircleImageView avatar;
    HttpService httpService;
    String billSort;
    String billDate;

    PenjinBill currentBill;
    private boolean isApplyDetailLoaded = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    refreshUI();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_bill_detail);
        initData();
        initDialog();
        initTitleBar();
        initView();
        initUserData();
        initHttpModule();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isApplyDetailLoaded) {
            getApplyDetail();
        }
    }

    private void initDialog() {
        progressDialog = CustomProgressDialog.createDialog(this);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        billSort = bundle.getString("BillSort");
        billDate = bundle.getString("Date");
    }

    private void initHttpModule() {
        httpService = HttpService.getInstance(this.getApplicationContext());
    }

    private void initUserData() {
        UserService userService = UserService.getInstance(this.getApplicationContext());
        user = userService.getCurrentUser();
        company = userService.getCurrentCompany();
        name.setText(company.getName());
        department.setText(company.getDepartment());
        zhiwu.setText(company.getPosition());
        try {
            Bitmap bitmap = userService.getUserAvatar();
            avatar.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        avatar = (CircleImageView) findViewById(R.id.avatar);
        billNumber = (TextView) findViewById(R.id.billNumber);
        billTypeName = (TextView) findViewById(R.id.billTypeName);
        type = (TextView) findViewById(R.id.type);
        name = (TextView) findViewById(R.id.name);
        department = (TextView) findViewById(R.id.department);
        zhiwu = (TextView) findViewById(R.id.zhiwu);
        passDay = (TextView) findViewById(R.id.passDay);
        timeName = (TextView) findViewById(R.id.timeName);
        timeDays = (TextView) findViewById(R.id.timeDays);
        startTime = (TextView) findViewById(R.id.startTime);
        endTime = (TextView) findViewById(R.id.endTime);
        detail = (TextView) findViewById(R.id.detail);
        fujian = (TextView) findViewById(R.id.fujian);
        flowLineWrapper = (LinearLayout) findViewById(R.id.flowLineWrapper);
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
        titleBarView.setCenterText("出差申请");
    }

    private void getApplyDetail() {
        if (NetUtils.hasNetwork(this)) {
            try {
                progressDialog.show();
                RequestParams params = new RequestParams();
                params.put("staffNumber", user.getStaffNum());
                params.put("companyId", user.getCompanyId());
                params.put("billSort", billSort);
                params.put("date", billDate);
                params.put("sn", httpService.getSn());
                httpService.postRequestAsync(HttpConstants.HOST + HttpConstants.SpecificDetail, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        progressDialog.dismiss();
                        if (s != null) {
                            System.out.println(s);
                        }
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        progressDialog.dismiss();
                        System.out.println(s);
                        try {
                            JSONObject result = new JSONObject(s);
                            if (result.getBoolean("result")) {
                                JSONArray datas = result.getJSONArray("data");
                                JSONObject data = datas.getJSONObject(0);
                                currentBill = new PenjinBill();
                                currentBill.typeName = data.getString("billtype");
                                currentBill.subTypeName = data.getString("type");
                                currentBill.startTime = data.getString("starttime");
                                currentBill.endTime = data.getString("endtime");
                                currentBill.reason = data.getString("reason");
                                currentBill.billNumber = data.getString("billsort");
                                JSONArray auditstep = result.getJSONArray("auditstep");
                                //加载审批流节点
                                mHandler.sendEmptyMessage(1);
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
            progressDialog.dismiss();
            Toast.makeText(this, "无法访问网络~", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshUI() {
        if (currentBill != null) {
            billNumber.setText(currentBill.billNumber);
            billTypeName.setText(currentBill.typeName);
            type.setText(currentBill.subTypeName);
            passDay.setText(billDate);//表示订单发起时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                Date date1 = simpleDateFormat.parse(currentBill.startTime);
                Date date2 = simpleDateFormat.parse(currentBill.endTime);
                String betweenDays = CalendarUtil.daysBetween(date1, date2);
                timeDays.setText(betweenDays);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startTime.setText(currentBill.startTime);
            endTime.setText(currentBill.endTime);
            if (JsonUtils.isJsonNull(currentBill.reason))
                detail.setText("未填写");
            else
                detail.setText(currentBill.reason);
        }
    }

}
