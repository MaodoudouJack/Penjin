package com.penjin.android.fragment;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.penjin.android.R;
import com.penjin.android.message.contact.EMContactActivity;
import com.penjin.android.message.db.InviteMessgeDao;
import com.penjin.android.message.group.GroupsActivity;
import com.penjin.android.message.utils.EaseCommonUtils;
import com.penjin.android.view.AddFriendDialog;
import com.penjin.android.view.AddFriendDialog.AddFriendDialogListenr;
import com.penjin.android.view.AddFriendPopupWindow;
import com.penjin.android.view.AddFriendPopupWindow.AddFriendPopupWindowInterface;
import com.penjin.android.view.CommonGestureListener;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.TitleBarView;
import com.penjin.android.view.TitleBarView.TitleBarListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import cn.hugo.android.scanner.CaptureActivity;

/*
 * 联系人Fragment，主界面的第一个fragment
 */
public class ContactsFragment extends BaseFragment implements OnRefreshListener {

    CustomProgressDialog customProgressDialog;
    private TextView unreadHaoyou;
    private TextView unreadGroup;
    private int unreadHaoyouCount;
    private int unreadGroupCount;

    private InviteMessgeDao inviteMessgeDao;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                customProgressDialog.dismiss();
                Toast.makeText(getActivity(), "好友请求发送成功!", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                Toast.makeText(getActivity(), "当前网络不可用~", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 3) {
                customProgressDialog.dismiss();
                Toast.makeText(getActivity(), "好友请求发送失败!", Toast.LENGTH_SHORT).show();
            }
        }

    };
    private TitleBarView titleBarView;
    private TitleBarListener barListener = new TitleBarListener() {

        @Override
        public void right(View view) {
            addFriendPopupWindow.showAt(titleBarView);
        }

        @Override
        public void left(View view) {

        }

        @Override
        public void center(View view) {

        }
    };

    private View rootView;

    private SwipeRefreshLayout swipeLayout;

    private View qiyehaoyou;
    private View qunzu;

    private AddFriendPopupWindow addFriendPopupWindow;
    private AddFriendPopupWindowInterface addPopwinListener = new AddFriendPopupWindowInterface() {

        @Override
        public void sao() {
            Intent intent = new Intent(getActivity(), CaptureActivity.class);
            startActivityForResult(intent, 1);
        }

        @Override
        public void addFriend() {
            addFriendDialog.show();
        }
    };

    private AddFriendDialog addFriendDialog;
    private AddFriendDialogListenr addFriendDialogListenr = new AddFriendDialogListenr() {

        @Override
        public void confirm(final String username) {
            Toast.makeText(getActivity(), username, Toast.LENGTH_SHORT).show();
            if (EaseCommonUtils.isNetWorkConnected(getActivity())) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            EMContactManager.getInstance().addContact(username, "添加好友测试...");
                        } catch (EaseMobException e) {
                            e.getErrorCode();
                            e.printStackTrace();
                            System.out.println("添加好友没有异常...");
                        }
                    }
                };
                thread.start();
            } else {
                handler.sendEmptyMessage(1);
            }

        }
    };

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        customProgressDialog = CustomProgressDialog.createDialog(getActivity());
        inviteMessgeDao = new InviteMessgeDao(getActivity());
        addFriendPopupWindow = new AddFriendPopupWindow(getActivity());
        addFriendPopupWindow.setListener(addPopwinListener);
        addFriendDialog = new AddFriendDialog(getActivity(),
                R.style.AddFriendDialog);
        addFriendDialog.setListenr(addFriendDialogListenr);
        rootView = inflater.inflate(R.layout.layout_fragment_xiaoxi, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        swipeLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        titleBarView = (TitleBarView) rootView.findViewById(R.id.title_bar);
        qiyehaoyou = rootView.findViewById(R.id.qiyehaoyou);
        qunzu = rootView.findViewById(R.id.qunzu);
        qiyehaoyou.setOnClickListener(this);
        qunzu.setOnClickListener(this);
        titleBarView.setTitleBarListener(barListener);

        unreadHaoyou = (TextView) rootView.findViewById(R.id.qiyehaoyou_num);
        unreadGroup = (TextView) rootView.findViewById(R.id.qunzu_num);
    }

    @Override
    public void onClick(View arg0) {
        Intent intent = new Intent();
        switch (arg0.getId()) {
            case R.id.qiyehaoyou:
                intent.setClass(getActivity(), EMContactActivity.class);
                startActivity(intent);
                break;
            case R.id.qunzu:
                intent.setClass(getActivity(), GroupsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void refreshUnread() {
        unreadHaoyouCount = inviteMessgeDao.getUnreadMessagesCount();
        if (unreadHaoyouCount > 0) {
            unreadHaoyou.setText(unreadHaoyouCount + "");
            unreadHaoyou.setVisibility(View.VISIBLE);
        } else {
            unreadHaoyou.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUnread();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 5000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == 100) {
                    final String message = data.getStringExtra("qrInfo");
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    if (EaseCommonUtils.isNetWorkConnected(getActivity())) {
                        customProgressDialog.show();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    EMContactManager.getInstance().addContact(message, "添加好友测试...");
                                    handler.sendEmptyMessage(0);
                                } catch (EaseMobException e) {
                                    e.getErrorCode();
                                    e.printStackTrace();
                                    handler.sendEmptyMessage(3);
                                }
                            }
                        };
                        thread.start();
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                }
            default:
                break;
        }
    }
}
