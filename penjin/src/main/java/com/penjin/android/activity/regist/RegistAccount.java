package com.penjin.android.activity.regist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.http.HttpService;
import com.penjin.android.message.view.EaseSwitchButton;
import com.penjin.android.utils.HttpUtil;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.TitleBarView;

import org.apache.http.Header;

/**
 * 用户注册最后一步
 * Created by Administrator on 2016/3/9.
 */
public class RegistAccount extends Activity implements View.OnClickListener {

    private String phoneNum;
    private String code;

    private TitleBarView titleBarView;
    private TextView phone;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private CustomProgressDialog progressDialog;
    private EaseSwitchButton switchButton;
    private View next;

    private HttpService httpService;
    private TextHttpResponseHandler registHandler;
    private RequestHandle registHandle;

    private boolean showPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData(savedInstanceState);
        initView();
        initHttp();
    }

    private void initData(Bundle savedInstance) {
        Intent intent = getIntent();
        if (intent != null) {
            this.phoneNum = intent.getStringExtra("phoneNum");
            this.code = intent.getStringExtra("code");
        }
        if (savedInstance != null) {
            String tmp1 = savedInstance.getString("phoneNum");
            String tmp2 = savedInstance.getString("code");
            if (tmp1 != null)
                this.phoneNum = tmp1;
            if (tmp2 != null)
                this.code = tmp2;
        }
    }

    private void initView() {
        progressDialog = CustomProgressDialog.createDialog(this);
        progressDialog.setMessage("请稍后");
        titleBarView = (TitleBarView) findViewById(R.id.titleBarView);
        titleBarView.setTitleBarListener(new TitleBarView.TitleBarListener() {
            @Override
            public void left(View view) {
                RegistAccount.this.finish();
            }

            @Override
            public void center(View view) {

            }

            @Override
            public void right(View view) {

            }
        });
        phone = (TextView) findViewById(R.id.phone);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        switchButton = (EaseSwitchButton) findViewById(R.id.switch_btn);
        next = findViewById(R.id.next);
        switchButton.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    private void initHttp() {
        this.httpService = HttpService.getInstance(this.getApplicationContext());
        this.registHandler = new TextHttpResponseHandler() {
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
                Toast.makeText(RegistAccount.this, "账户注册成功", Toast.LENGTH_SHORT).show();

                //login
            }
        };
    }

    private void showPassword(boolean show) {
        if (show)
            this.password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        else
            this.password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("phoneNum", this.phoneNum);
        outState.putString("code", code);
    }

    @Override
    protected void onDestroy() {
        if (this.registHandle != null)
            registHandle.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_btn:
                showPassword(this.showPassword);
                if (this.showPassword == true) {
                    this.showPassword = false;
                } else {
                    this.showPassword = true;
                }
                break;
            case R.id.next:
                if (HttpUtil.isNetworkAvailable(this)) {
                    String username = this.username.getEditableText().toString();
                    int a = checkUsername(username);
                    if (a != 0) {
                        return;
                    }
                    String pwd = this.password.getEditableText().toString();
                    String confirmPwd = this.confirmPassword.getEditableText().toString();
                    int result = checkPassword(pwd, confirmPwd);
                    if (result == 0) {
                        RequestParams params = new RequestParams();
                        params.put("phoneNum", this.phoneNum);
                        params.put("code", this.code);
                        params.put("username", username);
                        params.put("password", pwd);
                        try {
                            this.registHandle = this.httpService.postRequestWithHandle(this, "", params, this.registHandler);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private int checkPassword(String pwd, String confirmPwd) {
        if (pwd == null || pwd.equals("")) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return 1;
        }
        if (pwd.length() < 6) {
            Toast.makeText(this, "密码长度至少为6位", Toast.LENGTH_SHORT).show();
            return 2;
        }

        if (!pwd.equals(confirmPwd)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return 3;
        }
        return 0;
    }

    private int checkUsername(String username) {
        if (username == null || username.equals("")) {
            Toast.makeText(this, "请设置用户昵称~", Toast.LENGTH_SHORT).show();
            return 1;
        }
        if (username.length() > 4) {
            Toast.makeText(this, "昵称长度请少于4个字符", Toast.LENGTH_SHORT).show();
            return 2;
        }
        return 0;
    }
}
