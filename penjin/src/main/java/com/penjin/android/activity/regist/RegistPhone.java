package com.penjin.android.activity.regist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.http.HttpService;
import com.penjin.android.utils.CommonStrUtil;
import com.penjin.android.utils.HttpUtil;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.TitleBarView;

import org.apache.http.Header;

/**
 * Created by Administrator on 2016/3/9.
 * 注册页面：填写手机号码
 */
public class RegistPhone extends Activity implements View.OnClickListener {

    TitleBarView titleBarView;
    TextView region;
    EditText phone;
    View next;
    CheckBox confirm;
    TextView law;
    HttpService httpService;
    TextHttpResponseHandler httpHandler;
    RequestHandle requestHandle;

    CustomProgressDialog progressDialog;

    String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String temp = savedInstanceState.getString("phoneNum");
            if (temp != null) {
                this.phoneNum = null;
            }
        }
        setContentView(R.layout.layout_activiti_regist_phone);
        initView();
        initHttpModule();
    }

    private void initHttpModule() {
        httpService = HttpService.getInstance(this.getApplicationContext());
        httpHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (s != null) {
                    System.out.println(s);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                System.out.println(s);
                progressDialog.dismiss();
                Toast.makeText(RegistPhone.this, "注册码已经发送，请注意查收", Toast.LENGTH_SHORT).show();
                goNext(phoneNum);
            }
        };

    }

    private void initView() {
        progressDialog = CustomProgressDialog.createDialog(this);
        progressDialog.setMessage("请稍后...");
        titleBarView = (TitleBarView) findViewById(R.id.titleBarView);
        region = (TextView) findViewById(R.id.region);
        phone = (EditText) findViewById(R.id.phone);
        if (this.phoneNum != null) {
            this.phone.setText(phoneNum);
        }
        next = findViewById(R.id.next);
        confirm = (CheckBox) findViewById(R.id.confirm);
        law = (TextView) findViewById(R.id.law);
        region.setOnClickListener(this);
        next.setOnClickListener(this);
        confirm.setOnClickListener(this);
        law.setOnClickListener(this);
        titleBarView.setTitleBarListener(new TitleBarView.TitleBarListener() {
            @Override
            public void left(View view) {
                RegistPhone.this.finish();
            }

            @Override
            public void center(View view) {

            }

            @Override
            public void right(View view) {

            }
        });

    }

    /**
     * 显示使用条款和隐私政策
     */
    private void showLaw() {

    }

    private void goNext(String phoneNum) {
        Intent intent = new Intent();
        intent.setClass(this, RegistCode.class);
        intent.putExtra("phoneNum", phoneNum);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.law:
                showLaw();
                break;
            case R.id.next:
                if (confirm.isChecked()) {
                    if (HttpUtil.isNetworkAvailable(this)) {
                        phoneNum = phone.getEditableText().toString();
                        if (CommonStrUtil.isPhoneNum(phoneNum)) {
                            RequestParams params = new RequestParams();
                            params.put("phone", phoneNum);
                            try {
                                /*progressDialog.show();
                                requestHandle=httpService.postRequestWithHandle(this,"", params, this.httpHandler);*/
                                goNext(phoneNum);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "网络连接异常!,请重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "隐私政策确认", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.region:
                Toast.makeText(this, "目前仅支持中国地区用户注册", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("phoneNum", this.phoneNum);
    }

    @Override
    protected void onDestroy() {
        if (requestHandle != null)
            requestHandle.cancel(true);
        super.onDestroy();
    }
}
