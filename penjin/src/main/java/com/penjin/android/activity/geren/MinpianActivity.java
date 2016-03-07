package com.penjin.android.activity.geren;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.penjin.android.R;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.service.UserService;
import com.penjin.android.utils.BitmapUtils;
import com.penjin.android.view.TitleBarView;


/**
 * Created by maotiancai on 2015/12/15.
 */
public class MinpianActivity extends Activity implements View.OnClickListener {

    PenjinUser currentUser;

    private TitleBarView titleBarView;
    private ImageView qrImage;
    private TextView userNameJianchen;
    private TextView userName;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1)
            {
                Bitmap qrBitmap = BitmapUtils.create2DCoderBitmap(currentUser.getPhone(), qrImage.getWidth(), qrImage.getHeight());
                qrImage.setImageBitmap(qrBitmap);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_minpian);
        initView();
    }

    private void initView() {
        userNameJianchen = (TextView) findViewById(R.id.userNameJianchen);
        userName = (TextView) findViewById(R.id.userName);
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
        qrImage = (ImageView) findViewById(R.id.qrImage);

        UserService userService = UserService.getInstance(getApplicationContext());
        currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getUsername();
            if (username != null) {
                if (username.length() < 3) {
                    userNameJianchen.setText(username);
                } else if (username.length() == 3) {
                    String jianStr = username.substring(1);
                    userNameJianchen.setText(jianStr);
                } else if (username.length() > 3) {
                    String jianStr = username.substring(2);
                    userNameJianchen.setText(jianStr);
                }
                userName.setText(username);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessageDelayed(1,500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.qrImage:
                Bitmap qrBitmap = BitmapUtils.create2DCoderBitmap("www.baidu.com", v.getWidth(), v.getHeight());
                ((ImageView) v).setImageBitmap(qrBitmap);*/
            default:
                break;
        }
    }
}
