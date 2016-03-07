package com.penjin.android.activity;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.NetUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.constants.HttpConstants;
import com.penjin.android.constants.PenjinConstants;
import com.penjin.android.domain.PenjinCompany;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.http.HttpService;
import com.penjin.android.message.EMChatHelper;
import com.penjin.android.service.UserService;
import com.penjin.android.view.CircleImageView;
import com.penjin.android.view.CustomProgressDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class LoginActivity extends Activity implements OnClickListener {

    private static final int EMCHAT_LOGIN_SUCCESS = 0;
    private static final int EMCHAT_LOGIN_FAILURE = 1;
    private static final int SERVER_LOGIN_SUCCESS = 2;
    private static final int SERVER_LOGIN_FAILURE = 3;
    private static final int LOGOUT_SUCCESS = 4;
    private static final int LOGUOUT_FAILURE = 5;
    private static final int EMCHATE_LOGOUT_SUCCESS = 6;
    private static final int EMCHATE_LOGOUT_FAILURE = 7;

    private boolean isEMChatLogin;

    boolean isGo = false;
    PenjinUser user;
    HttpService httpService;

    CustomProgressDialog customProgressDialog;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            super.handleMessage(msg);
            switch (msg.what) {
                case EMCHAT_LOGIN_SUCCESS:
                    break;
                case EMCHAT_LOGIN_FAILURE:
                    break;
                case SERVER_LOGIN_SUCCESS:
                    customProgressDialog.dismiss();
                    Set<String> tags = new HashSet<>();
                    tags.add("0000000008");
                    TagAliasCallback callback = new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {
                            System.out.println("jpush tags inited!!!!!!!!!!!!!");
                        }
                    };
                    JPushInterface.setTags(LoginActivity.this, tags, callback);
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT)
                            .show();
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivityForResult(intent, 100);
                    break;
                case SERVER_LOGIN_FAILURE:
                    customProgressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "登陆失败：账户密码错误!",
                            Toast.LENGTH_SHORT).show();
                    if (EMChatHelper.getInstance().isLoggedIn()) {
                        EMChatHelper.getInstance().logout(true, emchatLogoutCallback);
                    }
                    user = null;
                    username.setText(null);
                    password.setText(null);
                    pwdWrapper.setVisibility(View.VISIBLE);
                    login.setText("登     陆");
                    break;
                case LOGOUT_SUCCESS:
                    Toast.makeText(LoginActivity.this, "账号以安全退出，请重新登陆", Toast.LENGTH_SHORT).show();
                    UserService.getInstance(LoginActivity.this.getApplicationContext()).clearUser();
                    user = null;
                    username.setText(null);
                    password.setText(null);
                    pwdWrapper.setVisibility(View.VISIBLE);
                    login.setText("登     陆");
                    break;
                case LOGUOUT_FAILURE:
                    Toast.makeText(LoginActivity.this, "网络不稳定，请重试~",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

    };

    View pwdWrapper;

    EditText username;
    EditText password;
    TextView login;
    TextView regist;

    TextView relogin;
    TextView forgetPwd;
    CircleImageView avatar;

    private EMCallBack emchatLogoutCallback = new EMCallBack() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(int i, String s) {

        }

        @Override
        public void onProgress(int i, String s) {

        }
    };

    private EMCallBack logoutCallBack = new EMCallBack() {

        @Override
        public void onSuccess() {
            handler.sendEmptyMessage(LOGOUT_SUCCESS);
        }

        @Override
        public void onProgress(int arg0, String arg1) {

        }

        @Override
        public void onError(int arg0, String arg1) {
            handler.sendEmptyMessage(LOGUOUT_FAILURE);
        }
    };

    private EMCallBack loginCallBack = new EMCallBack() {

        @Override
        public void onSuccess() {
            EMChatHelper.getInstance().setCurrentUserName(
                    username.getText().toString());
            EMChatHelper.getInstance().registerGroupAndContactListener();
            // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
            // ** manually load all local groups and
            EMGroupManager.getInstance().loadAllGroups();
            EMChatManager.getInstance().loadAllConversations();
            handler.sendEmptyMessage(EMCHAT_LOGIN_SUCCESS);
        }

        @Override
        public void onProgress(int arg0, String arg1) {

        }

        @Override
        public void onError(int arg0, String arg1) {
            handler.sendEmptyMessage(EMCHAT_LOGIN_FAILURE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_login);
        initHttpModule();
        initData();
        initView();
    }

    private void initHttpModule() {
        httpService = HttpService.getInstance(this.getApplicationContext());
    }

    private void initData() {
        user = UserService.getInstance(this.getApplicationContext()).getCurrentUser();
    }

    private void initView() {
        customProgressDialog = CustomProgressDialog.createDialog(this);
        customProgressDialog.setCanceledOnTouchOutside(false);
        customProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        username = (EditText) findViewById(R.id.editUsername);
        password = (EditText) findViewById(R.id.editPassword);
        login = (TextView) findViewById(R.id.login);
        regist = (TextView) findViewById(R.id.regist);
        relogin = (TextView) findViewById(R.id.relogin);
        forgetPwd = (TextView) findViewById(R.id.forget_pwd);
        pwdWrapper = findViewById(R.id.password_wrapper);
        avatar = (CircleImageView) findViewById(R.id.login_avatar);
        // 添加下划线
        String str1 = relogin.getText().toString();
        relogin.setText(Html.fromHtml("<u>" + str1 + "</u>"));
        str1 = forgetPwd.getText().toString();
        forgetPwd.setText(Html.fromHtml("<u>" + str1 + "</u>"));

        forgetPwd.setOnClickListener(this);
        login.setOnClickListener(this);
        relogin.setOnClickListener(this);
        regist.setOnClickListener(this);

        if (user != null) {
            if (!PenjinConstants.isCurrentUserDirInited()) {
                PenjinConstants.createUserDir(user.getPhone());
            }
            username.setText(user.getPhone());
            pwdWrapper.setVisibility(View.GONE);
            login.setText("自 动 登 陆");
            try {
                Bitmap bitmap = UserService.getInstance(this.getApplicationContext()).getUserAvatar();
                if (bitmap != null) {
                    avatar.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.login:
                doLogin();
                break;
            case R.id.regist:
                Intent intent = new Intent(this, RegistUserActivity.class);
                startActivity(intent);
                break;
            case R.id.relogin:
                if (EMChat.getInstance().isLoggedIn()) {
                    EMChatHelper.getInstance().logout(true, logoutCallBack);
                }
                break;
            default:
                break;
        }
    }

    private boolean checkLogin() {
        String uname = username.getEditableText().toString();
        String upwd = username.getEditableText().toString();
        if (uname == null || upwd == null || uname.isEmpty() || upwd.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private void doLogin() {
        if (!NetUtils.hasNetwork(this)) {
            Toast.makeText(this, "网络异常...,请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null) {
        } else {
            if (!checkLogin()) {
                Toast.makeText(this, "账号、密码不能为空", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            user = new PenjinUser();
            user.setPhone(username.getEditableText().toString());
            user.setPassword(password.getEditableText().toString());
        }
        loginServer();
        loginEMChat();
    }

    //登陆系统账号
    private void loginServer() {
        customProgressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.put("UserName", user.getPhone());
        requestParams.put("Password", user.getPassword());
        try {
            httpService.postRequest(LoginActivity.this.getApplicationContext(), HttpConstants.HOST + HttpConstants.LoginInterface, requestParams, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                /*    throwable.printStackTrace();
                    System.out.println(s);*/
                    customProgressDialog.dismiss();
                    throwable.printStackTrace();
                    if (s == null) {
                        Toast.makeText(LoginActivity.this, "服务器连接超时", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println(s);
                    }
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    try {
                        JSONObject jo = new JSONObject(s);
                        if (jo.optBoolean("result")) {
                            JSONObject data = jo.optJSONObject("data");
                            JSONObject staff = jo.getJSONObject("staff");
                            JSONObject person = jo.getJSONArray("personal").getJSONObject(0);
                            String sn = jo.getString("sn");

                            //保存公司信息
                            String department = staff.getString("department");
                            String positon = staff.getString("position");
                            String realname = staff.getString("name");
                            PenjinCompany penjinCompany = new PenjinCompany();
                            if (realname == null || realname.equals("null") || realname.equals(""))
                                penjinCompany.setName(null);
                            else {
                                penjinCompany.setName(realname);
                            }
                            if (department == null || department.equals("null") || department.equals("")) {
                                penjinCompany.setDepartment(null);
                            } else {
                                penjinCompany.setDepartment(department);
                            }
                            if (positon == null || positon.equals("null") || positon.equals("")) {
                                penjinCompany.setPosition(null);
                            } else {
                                penjinCompany.setPosition(positon);
                            }
                            UserService.getInstance(LoginActivity.this.getApplicationContext()).saveCompany(penjinCompany);

                            //个人基本信息
                            String staffNum = data.getString("staffNumber");
                            String companyId = data.getString("companyId");
                            String nickName = person.getString("nickname");
                            System.out.println("nickName=" + nickName);
                            String address = person.getString("address");
                            String region = person.getString("strict");
                            String sex = person.getString("gender");
                            String qianming = person.getString("personal");
                            user.setCompanyId(companyId);
                            user.setStaffNum(staffNum);
                            if (nickName == null || nickName.equals("null") || nickName.equals("")) {
                                user.setUsername(null);
                            } else {
                                user.setUsername(nickName);
                            }
                            if (address == null || address.equals("null") || address.equals("")) {
                                user.setAddress(null);
                            } else {
                                user.setAddress(address);
                            }
                            if (region == null || region.equals("null") || region.equals("")) {
                                user.setRegion(null);
                            } else {
                                user.setRegion(region);
                            }
                            if (sex == null || sex.equals("null") || sex.equals("")) {
                                user.setSex(null);
                            } else {
                                user.setSex(sex);
                            }
                            if (qianming == null || qianming.equals("null") || qianming.equals("")) {
                                user.setQianming(null);
                            } else {
                                user.setQianming(qianming);
                            }
                            //同步信息
                            UserService.getInstance(LoginActivity.this.getApplicationContext()).saveUser(user);
                            httpService.setSn(sn);
                            PenjinConstants.createUserDir(user.getPhone());
                            handler.sendEmptyMessage(SERVER_LOGIN_SUCCESS);
                        } else {
                            handler.sendEmptyMessage(SERVER_LOGIN_FAILURE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(s);
                }
            });
        } catch (Exception e) {
            customProgressDialog.dismiss();
            Toast.makeText(this, "网络连接异常!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //登陆环信账号
    private void loginEMChat() {
        System.out.println("使用用户名：" + user.getPhone() + "  密码：" + user.getPassword() + "登陆");
        if (!EMChat.getInstance().isLoggedIn())
            EMChatManager.getInstance().login(
                    user.getPhone(),
                    user.getPassword(), loginCallBack);
       /* else//如果已经登陆，判断当前的用户与环信已登录的账户是否一致
        {
            String currentChatId = EMChatHelper.getInstance().getCurrentUsernName();
            if (currentChatId.equals(user.getUsername())) {
                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                isGo = true;
                EMChatHelper.getInstance().logout(true, logoutCallBack);
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                user = UserService.getInstance(this.getApplicationContext()).getCurrentUser();
                username.setText(null);
                password.setText(null);
                pwdWrapper.setVisibility(View.VISIBLE);
                login.setText("登     陆");
            }
        }
    }
}
