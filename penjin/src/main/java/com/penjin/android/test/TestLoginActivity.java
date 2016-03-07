package com.penjin.android.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.activity.kaoqin.GerenKaoqinActivity;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.http.HttpService;
import com.penjin.android.service.UserService;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by maotiancai on 2016/1/8.
 */
public class TestLoginActivity extends Activity {

    EditText username;
    EditText password;
    Button login;
    Button go;
    HttpService httpService;
    PenjinUser user = new PenjinUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_testlogin);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        go = (Button) findViewById(R.id.go);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(username);
                String uname = username.getEditableText().toString();
                String pwd = password.getEditableText().toString();
                user.setPhone(uname);
                user.setPassword(pwd);
                httpService = HttpService.getInstance(TestLoginActivity.this.getApplicationContext());
                RequestParams requestParams = new RequestParams();
                requestParams.put("UserName", uname);
                requestParams.put("Password", pwd);
                try {
                    httpService.postRequest(TestLoginActivity.this, "http://192.168.0.19/Account/LoginInterface", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            throwable.printStackTrace();
                            System.out.println(s);
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                if (jo.optBoolean("result")) {
                                    String sn = jo.getString("sn");
                                    httpService.setCookie("sn", sn);
                                    String staffNum = jo.getString("staffNumber");
                                    String companyId = jo.getString("companyId");
                                    user.setCompanyId(companyId);
                                    user.setStaffNum(staffNum);
                                    UserService.getInstance(TestLoginActivity.this.getApplicationContext()).saveUser(user);
                                    Toast.makeText(TestLoginActivity.this, s, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TestLoginActivity.this, "用户密码错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(s);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestLoginActivity.this, GerenKaoqinActivity.class);
                startActivity(intent);
               /* try {
                    httpService.postRequest(TestLoginActivity.this, "http://192.168.0.23:8080/CookieTest/test", new RequestParams(), new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });
    }
}
