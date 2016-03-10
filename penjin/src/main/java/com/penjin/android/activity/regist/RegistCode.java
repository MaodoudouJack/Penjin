package com.penjin.android.activity.regist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.jungly.gridpasswordview.GridPasswordView;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.http.HttpService;
import com.penjin.android.utils.HttpUtil;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.TitleBarView;

import org.apache.http.Header;

/**
 * 填写手机验证码
 * Created by Administrator on 2016/3/9.
 */
public class RegistCode extends Activity implements View.OnClickListener {

    TitleBarView titleBarView;
    TextView phone;
    TextView resendCode;
    TextView phoneCode;
    View next;
    GridPasswordView gridPasswordView;

    HttpService httpService;
    TextHttpResponseHandler checkCodeHandler;
    TextHttpResponseHandler resendCodeHandler;
    RequestHandle checkCodeHandle;
    RequestHandle resendCodeHandle;
    CustomProgressDialog progressDialog;

    String phoneNum;
    String checkCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activiti_regist_code);
        Intent intent = getIntent();
        if (intent != null)
            this.phoneNum = intent.getStringExtra("phoneNum");
        String temp = null;
        if (savedInstanceState != null)
            temp = savedInstanceState.getString("phoneNum");
        if (temp != null) {
            phoneNum = temp;
        }
        initView();
        initHttpService();
    }

    private void initView() {
        progressDialog = CustomProgressDialog.createDialog(this);
        progressDialog.setMessage("请稍后...");
        titleBarView = (TitleBarView) findViewById(R.id.titleBarView);
        phone = (TextView) findViewById(R.id.phone);
        phone.setText(this.phoneNum);
        next = findViewById(R.id.next);
        gridPasswordView = (GridPasswordView) findViewById(R.id.pswView);
        resendCode = (TextView) findViewById(R.id.resendCode);
        phoneCode = (TextView) findViewById(R.id.phoneCode);

        gridPasswordView.setPasswordVisibility(true);
        next.setOnClickListener(this);
        titleBarView.setTitleBarListener(new TitleBarView.TitleBarListener() {
            @Override
            public void left(View view) {
                RegistCode.this.finish();
            }

            @Override
            public void center(View view) {

            }

            @Override
            public void right(View view) {

            }
        });
        resendCode.setOnClickListener(this);
        phoneCode.setOnClickListener(this);
    }

    private void initHttpService() {
        httpService = HttpService.getInstance(this.getApplicationContext());
        resendCodeHandler = new TextHttpResponseHandler() {
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
                Toast.makeText(RegistCode.this, "注册码已经发送，请注意查收", Toast.LENGTH_SHORT).show();
            }
        };
        checkCodeHandler = new TextHttpResponseHandler() {
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
                goNext(phoneNum, checkCode);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                if (HttpUtil.isNetworkAvailable(this)) {
                    checkCode = this.gridPasswordView.getPassWord();
                    RequestParams params = new RequestParams();
                    params.put("checkCode", checkCode);
                    try {
                        checkCodeHandle = this.httpService.postRequestWithHandle(this, "", params, checkCodeHandler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.resendCode:
                if (HttpUtil.isNetworkAvailable(this)) {
                    RequestParams params = new RequestParams();
                    params.put("phoneNum", this.phoneNum);
                    try {
                        resendCodeHandle = this.httpService.postRequestWithHandle(this, "", params, resendCodeHandler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.phoneCode:
                Toast.makeText(this, "当前仅支持短信验证码注册！", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void goNext(String phoneNum, String checkCode) {
        Intent intent = new Intent();
        intent.setClass(this, RegistAccount.class);
        intent.putExtra("phoneNum", phoneNum);
        intent.putExtra("code", checkCode);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("phoneNum", phoneNum);
        outState.putString("checkCode", checkCode);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (this.checkCodeHandle != null)
            checkCodeHandle.cancel(true);
        if (this.resendCodeHandle != null)
            resendCodeHandle.cancel(true);
        super.onDestroy();
    }
}
