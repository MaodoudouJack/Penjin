package com.penjin.android.activity.kaoqin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.penjin.android.R;
import com.penjin.android.domain.PenjinCompany;
import com.penjin.android.domain.PenjinDate;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.service.UserService;
import com.penjin.android.view.CircleImageView;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.TitleBarView;
import com.penjin.android.widget.calendar.CalendarTool;
import com.penjin.android.widget.calendar.DateEntity;
import com.penjin.android.widget.calendar.PenjinCalendar;
import com.penjin.android.widget.calendar.PenjinCalendarAdapter;
import com.penjin.android.widget.calendar.PenjinCalendarPoint;


import java.util.Calendar;
import java.util.List;

/**
 * 个人排班
 * Created by maotiancai on 2016/1/19.
 */
public class GerenPaibanActivity extends Activity implements View.OnClickListener {

    PenjinUser user;
    PenjinCompany company;

    private List<DateEntity> mDateEntityList;
    private PenjinCalendarPoint mNowCalendarPoint;
    private CalendarTool mCalendarTool;
    private PenjinCalendar penjinCalendar;
    private PenjinCalendarAdapter calendarAdapter;

    TitleBarView titleBar;
    CircleImageView avatar;
    TextView name;
    TextView department;
    TextView zhiwu;
    TextView workDays;//该月需要上的班数

    TextView year;
    TextView month_day;

    View leftDayBtn;
    View rightDayBtn;

    private CustomProgressDialog progressDialog;
    private PenjinDate todayDate;
    private PenjinDate pickDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_geren_paiban);
        initData();
        initView();
        initTitleBar();
        initDate();
    }

    private void initData() {
        user = UserService.getInstance(this.getApplicationContext()).getCurrentUser();
        if (user == null) {
            Toast.makeText(GerenPaibanActivity.this, "用户尚未登陆！", Toast.LENGTH_SHORT).show();
            finish();
        }
        Toast.makeText(this, user.getPhone() + " " + user.getStaffNum(), Toast.LENGTH_SHORT).show();
    }

    private void initView() {

        penjinCalendar = (PenjinCalendar) findViewById(R.id.calendar);
        progressDialog = CustomProgressDialog.createDialog(this);
        avatar = (CircleImageView) findViewById(R.id.avatar);
        name = (TextView) findViewById(R.id.name);
        department = (TextView) findViewById(R.id.department);
        zhiwu = (TextView) findViewById(R.id.zhiwu);

        year = (TextView) findViewById(R.id.year);
        month_day = (TextView) findViewById(R.id.month_day);
        leftDayBtn = (TextView) findViewById(R.id.leftDayBtn);
        rightDayBtn = (TextView) findViewById(R.id.rightDayBtn);

        titleBar = (TitleBarView) findViewById(R.id.titleBar);

        leftDayBtn.setOnClickListener(this);
        rightDayBtn.setOnClickListener(this);
        String companyId = user.getCompanyId();
        if (companyId != null && !companyId.equals("") && !companyId.equals("null")) {
            company = UserService.getInstance(this.getApplicationContext()).getCurrentCompany();
            name.setText(company.getName());
            department.setText(company.getDepartment());
            zhiwu.setText(company.getPosition());
        }
        if (user != null) {
            try {
                avatar.setImageBitmap(UserService.getInstance(this.getApplicationContext()).getUserAvatar());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initTitleBar() {
        titleBar = (TitleBarView) findViewById(R.id.titleBar);
        titleBar.setTitleBarListener(new TitleBarView.TitleBarListener() {
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

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        todayDate = new PenjinDate(calendar.getTime());//当前日期
        System.out.println(todayDate.year + " " + todayDate.month + " " + todayDate.date);
        pickDate = todayDate;//当前选择的日期
        setDateRegion(pickDate);
        mCalendarTool = new CalendarTool(this);
        mNowCalendarPoint = mCalendarTool.getNowCalendar();
        mDateEntityList = mCalendarTool.getDateEntityList(mNowCalendarPoint.x,
                mNowCalendarPoint.y);
        System.out.println(mDateEntityList.size());
        for (DateEntity dateEntity : mDateEntityList) {
            System.out.println(dateEntity.year + " " + dateEntity.month + " " + dateEntity.day);
        }
        calendarAdapter = new PenjinCalendarAdapter(this, this.getResources());
        calendarAdapter.setDateList(mDateEntityList);
        penjinCalendar.setAdapter(calendarAdapter);
    }

    //设置显示日期的区域
    private void setDateRegion(PenjinDate penjinDate) {
        year.setText(penjinDate.year + "年");
        month_day.setText(penjinDate.month + "月" + penjinDate.date + "日" + "(" + penjinDate.weekDay + ")");
    }

    @Override
    public void onClick(View v) {

    }
}
