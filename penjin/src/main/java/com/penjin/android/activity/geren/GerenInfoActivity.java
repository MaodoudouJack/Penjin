package com.penjin.android.activity.geren;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.util.NetUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.activity.LoginActivity;
import com.penjin.android.constants.HttpConstants;
import com.penjin.android.constants.PenjinConstants;
import com.penjin.android.domain.PenjinCompany;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.http.HttpConstant;
import com.penjin.android.http.HttpService;
import com.penjin.android.service.UserService;
import com.penjin.android.utils.BitmapUtils;
import com.penjin.android.utils.SDCardUtils;
import com.penjin.android.view.CircleImageView;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.TitleBarView;
import com.penjin.android.widget.CustomEditDialog;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by maotiancai on 2015/12/19.
 */
public class GerenInfoActivity extends Activity implements View.OnClickListener {
    private String[] items = new String[]{"选择本地图片", "拍照"};
    TitleBarView titleBarView;
    PenjinUser currentUser;
    PenjinCompany currentCompany;
    UserService userService;

    private int currentType = -1;
    private boolean isModified = false;
    View avatarWrapper;
    CircleImageView pjAvatar;
    View nickNameWrapper;
    TextView nickName; //1
    View realNameWrapper;
    TextView realName;//2
    View qrWrapper;
    View addressWrapper;
    TextView address;//3
    View sexWrapper;
    TextView sex;//4
    View diquWrapper;
    TextView diqu;//5
    View qianmingWrapper;
    TextView qianming;//6
    View updateWrapper;

    CustomProgressDialog customProgressDialog;

    CustomEditDialog customEditDialog;
    CustomEditDialog.CustomEditDialogListner customEditDialogListner;
    private HttpService httpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate()");
        setContentView(R.layout.layout_activity_gereninfo);
        userService = UserService.getInstance(getApplicationContext());
        currentUser = userService.getCurrentUser();
        currentCompany = userService.getCurrentCompany();

       /* if (currentUser == null) {
            Toast.makeText(this, "尚未登陆!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }*/
        initView();
        initData();
        initDialog();
        initHttpModule();
    }

    private void initHttpModule() {
        httpService = HttpService.getInstance(this.getApplicationContext());
    }

    private void initDialog() {
        customProgressDialog = CustomProgressDialog.createDialog(this);
        customEditDialog = new CustomEditDialog(this, R.style.PenjinCommonDialog);
        customEditDialogListner = new CustomEditDialog.CustomEditDialogListner() {
            @Override
            public void ok(String msg, View view) {
                customEditDialog.dismiss();
                if (msg != null && !msg.equals("")) {
                    switch (currentType) {
                        case 1:
                            nickName.setText(msg);
                            currentUser.setUsername(msg);
                            isModified = true;
                            break;
                        case 2:
                            realName.setText(msg);
                            currentCompany.setName(msg);
                            isModified = true;
                            break;
                        case 3:
                            address.setText(msg);
                            currentUser.setAddress(msg);
                            isModified = true;
                            break;
                        case 4:
                            sex.setText(msg);
                            currentUser.setSex(msg);
                            isModified = true;
                            break;
                        case 5:
                            diqu.setText(msg);
                            currentUser.setRegion(msg);
                            isModified = true;
                            break;
                        case 6:
                            qianming.setText(msg);
                            currentUser.setQianming(msg);
                            isModified = true;
                            break;
                    }
                }
            }

            @Override
            public void cancel(String msg) {
                if (customEditDialog != null)
                    if (customEditDialog.isShowing()) {
                        customEditDialog.dismiss();
                    }
            }
        };
        customEditDialog.setCustomEditDialogListnerlistner(customEditDialogListner);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {

            switch (requestCode) {
                case PenjinConstants.IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case PenjinConstants.CAMERA_REQUEST_CODE:
                    if (SDCardUtils.isSDCardEnable()) {
                        File tempFile = new File(
                                Environment.getExternalStorageDirectory() + File.separator + PenjinConstants.CAPTURE_TEMP);//这里获取相机拍摄之后缓存的文件
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(this, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_LONG).show();
                    }

                    break;
                case PenjinConstants.RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PenjinConstants.RESULT_REQUEST_CODE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            try {
                if (!PenjinConstants.isCurrentUserDirInited()) {
                    PenjinConstants.createUserDir(currentUser.getPhone());
                }
                UserService.getInstance(this.getApplicationContext()).saveUserAvatar(photo);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(GerenInfoActivity.this, "保存头像失败", Toast.LENGTH_SHORT).show();
            }
            Drawable drawable = new BitmapDrawable(photo);
            pjAvatar.setImageDrawable(drawable);
        }
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.titlebar);
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
        avatarWrapper = findViewById(R.id.avatarWrapper);
        pjAvatar = (CircleImageView) findViewById(R.id.pjAvatar);
        avatarWrapper.setOnClickListener(this);
        nickNameWrapper = findViewById(R.id.nickNameWrapper);
        nickNameWrapper.setOnClickListener(this);
        nickName = (TextView) findViewById(R.id.nickName);
        realNameWrapper = findViewById(R.id.realNameWrapper);
        realNameWrapper.setOnClickListener(this);
        realName = (TextView) findViewById(R.id.realName);
        qrWrapper = findViewById(R.id.qrWrapper);
        qrWrapper.setOnClickListener(this);
        addressWrapper = findViewById(R.id.addressWrapper);
        addressWrapper.setOnClickListener(this);
        address = (TextView) findViewById(R.id.address);
        sexWrapper = findViewById(R.id.sexWrapper);
        sexWrapper.setOnClickListener(this);
        sex = (TextView) findViewById(R.id.sex);
        diquWrapper = findViewById(R.id.diquWrapper);
        diquWrapper.setOnClickListener(this);
        diqu = (TextView) findViewById(R.id.diqu);
        qianmingWrapper = findViewById(R.id.qianmingWrapper);
        qianmingWrapper.setOnClickListener(this);
        qianming = (TextView) findViewById(R.id.qianming);
        updateWrapper = findViewById(R.id.updateWrapper);
        updateWrapper.setOnClickListener(this);

        try {
            Bitmap avatarBitmap = UserService.getInstance(this.getApplicationContext()).getUserAvatar();
            pjAvatar.setImageBitmap(avatarBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        if (currentUser != null) {
            if (currentUser.getUsername() != null) {
                nickName.setText(currentUser.getUsername());
            } else {
                nickName.setText("添加昵称");
            }
            if (currentCompany != null) {
                realName.setText(currentCompany.getName());
            } else {
                realName.setText("绑定公司");
            }

            if (currentUser.getSex() != null) {
                sex.setText(currentUser.getSex());
            } else {
                sex.setText("修改性别");
            }
            if (currentUser.getAddress() != null) {
                address.setText(currentUser.getAddress());
            } else {
                address.setText("添加地址");
            }
            if (currentUser.getRegion() != null) {
                diqu.setText(currentUser.getRegion());
            } else {
                diqu.setText("修改区域");
            }

            if (currentUser.getQianming() != null) {
                qianming.setText(currentUser.getQianming());
            } else {
                qianming.setText("添加签名信息");
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatarWrapper:
                showDialog();
                break;
            case R.id.nickNameWrapper:
                currentType = 1;
                customEditDialog.setTitle("修改昵称");
                customEditDialog.setHint("填写您的昵称");
                customEditDialog.show();
                break;
            case R.id.addressWrapper:
                currentType = 3;
                customEditDialog.setTitle("修改住址");
                customEditDialog.setHint("填写您的住址");
                customEditDialog.show();
                break;
            case R.id.sexWrapper:
                currentType = 4;
                customEditDialog.setTitle("修改性别");
                customEditDialog.setHint("填写您的性别");
                customEditDialog.show();
                break;
            case R.id.diquWrapper:
                currentType = 5;
                customEditDialog.setTitle("修改区域");
                customEditDialog.setHint("填写您的工作区域");
                customEditDialog.show();
                break;
            case R.id.qianmingWrapper:
                currentType = 6;
                customEditDialog.setTitle("修改签名");
                customEditDialog.setHint("填写您的个性签名");
                customEditDialog.show();
                break;
            case R.id.updateWrapper:
                launchRequest();
                break;
            default:
                break;
        }
    }

    /**
     * 发起更新用户信息的网络请求
     */
    private void launchRequest() {
        if (isModified) {
            if (NetUtils.hasNetwork(this)) {
                try {
                    customProgressDialog.show();
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("sn", httpService.getSn());
                    requestParams.put("userName", currentUser.getPhone());
                    requestParams.put("nickname", currentUser.getUsername());
                    requestParams.put("address", currentUser.getAddress());
                    requestParams.put("strict", currentUser.getRegion());
                    requestParams.put("gender", currentUser.getSex());
                    requestParams.put("personal", currentUser.getQianming());
                    try {
                        File file = userService.getUserAvatarFile();
                        if (file.exists()) {
                            requestParams.put("photo", userService.getUserAvatarFile());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    httpService.postRequest(this, HttpConstants.HOST + HttpConstants.PesonInfoSet, requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            if (s == null) {
                                Toast.makeText(GerenInfoActivity.this, "服务器内部错误！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(GerenInfoActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                            }
                            customProgressDialog.dismiss();
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            System.out.println(s);
                            try {
                                JSONObject jo = new JSONObject(s);
                                if (jo.getBoolean("result")) {
                                    isModified = false;
                                    userService.saveUser(currentUser);
                                    Toast.makeText(GerenInfoActivity.this, "用户信息更新成功！", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(GerenInfoActivity.this, "用户信息修改失败！", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            customProgressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "已经为最新数据~亲~", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示选择对话框
     */
    private void showDialog() {
        if (!SDCardUtils.isSDCardEnable()) {
            Toast.makeText(this, "手机没有外接SD卡，不能更改用户头像...", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*"); // 设置文件类型
                                intentFromGallery
                                        .setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery,
                                        PenjinConstants.IMAGE_REQUEST_CODE);
                                break;
                            case 1:
                                Intent intentFromCapture = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (SDCardUtils.isSDCardEnable()) {
                                    System.out.println("外部sd可用");
                                    File dir = new File(Environment.getExternalStorageDirectory() + File.separator + PenjinConstants.ROOT);
                                    if (!dir.exists()) {
                                        dir.mkdir();
                                    }
                                    File tempImage = new File(Environment.getExternalStorageDirectory() + File.separator + PenjinConstants.CAPTURE_TEMP);
                                    if (tempImage.exists()) {
                                        tempImage.delete();
                                    }
                                    intentFromCapture.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(tempImage));
                                    startActivityForResult(intentFromCapture,
                                            PenjinConstants.CAMERA_REQUEST_CODE);
                                } else {
                                    Toast.makeText(GerenInfoActivity.this, "未安装sd卡，请从相册中选择图片", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isModified) {
                new AlertDialog.Builder(this).setTitle("提示").setMessage("您的个人信息发生变更，是否更新到服务器")
                        .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchRequest();
                        dialog.dismiss();
                    }
                }).show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
