package com.penjin.android.activity;

import java.util.HashMap;
import java.util.Map;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.util.EMLog;
import com.penjin.android.R;
import com.penjin.android.fragment.GerenFragment;
import com.penjin.android.fragment.ContactsFragment;
import com.penjin.android.fragment.GongzuoFragment;
import com.penjin.android.fragment.KaoqinFragment;
import com.penjin.android.message.Constant;
import com.penjin.android.message.EMChatHelper;
import com.penjin.android.message.chat.EMChatActivity;
import com.penjin.android.message.chat.EMConversationListFragment;
import com.penjin.android.message.chat.EMConversationListFragment.EaseConversationListItemClickListener;
import com.penjin.android.message.db.InviteMessgeDao;
import com.penjin.android.message.db.UserDao;
import com.penjin.android.message.domain.InviteMessage;
import com.penjin.android.message.utils.EMConstant;
import com.penjin.android.message.utils.EaseConstant;
import com.penjin.android.service.UserService;
import com.penjin.android.view.CommonGestureListener;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * 主界面Activity
 * 主要是管理 各个fragment，以及维护一个同步参数，与全局的消息传递
 */
public class MainActivity extends BaseActivity implements OnClickListener, EMEventListener {

    private static String TAG = "MainActivity";

    private Map<String, Fragment> fragments = new HashMap<String, Fragment>();
    private int fragmentIndex = 1;

    private RelativeLayout xiaoxiWrapper;
    private RelativeLayout huodongWrapper;
    private RelativeLayout dingWrapper;
    private RelativeLayout gongzuoWrapper;
    private RelativeLayout gerenWrapper;

    private ImageButton tongxunImg;
    private TextView tongxunText;
    private ImageButton xiaoxiImg;
    private TextView xiaoxiText;
    private ImageButton dingImage;
    private ImageButton gongzuoImage;
    private TextView gongzuoText;
    private ImageButton gerenImage;
    private TextView gerenText;

    private TextView unreadContact;
    private TextView unreadChat;
    private int unreadContactCount;
    private int unreadChatCount;

    /*
     * ---------------------------------------分割行--------------------------------
     * --------
     */
    /*
     * 装载Fragment的Activity必须的属性
	 */
    public boolean isConflict = false;// 账号在别处登录
    private boolean isCurrentAccountRemoved = false;// 账号被移除
    private boolean isConflictDialogShow = false;
    private boolean isAccountRemovedDialogShow = false;
    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;

    private BroadcastReceiver internalDebugReceiver;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;

    /*
     * 主界面的5个Fragment
     */
    private ContactsFragment contactsFragment; //联系人
    private EMConversationListFragment conversationListFragment;//最近消息
    private GongzuoFragment gongzuoFragment;
    private GerenFragment gerenFragment;
    private KaoqinFragment kaoqinFragment;


    /*
     * 用于滑动监听事件
     */
    private CommonGestureListener gestureListener;

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    private EaseConversationListItemClickListener conversationListItemClickListener = new EaseConversationListItemClickListener() {

        @SuppressWarnings("static-access")
        @Override
        public void onListItemClicked(EMConversation conversation) {
            String username = conversation.getUserName();
            if (username != null && !username.equals(EMChatManager.getInstance().getCurrentUser())) {
                Intent intent = new Intent(MainActivity.this, EMChatActivity.class);
                intent.putExtra("userId", username);
                EMConversationType chatType = conversation.getType();
                if (chatType == EMConversationType.Chat) {
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE);
                } else if (chatType == EMConversationType.GroupChat) {
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                }
                startActivity(intent);
            }
        }
    };

    public EaseConversationListItemClickListener getConversationListItemClickListener() {
        return conversationListItemClickListener;
    }

    public void setConversationListItemClickListener(
            EaseConversationListItemClickListener conversationListItemClickListener) {
        this.conversationListItemClickListener = conversationListItemClickListener;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)
                && !isConflictDialogShow) {
            showConflictDialog();
        } else if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
                && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }
    }

    /**
     * 检查当前用户是否被删除
     */
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        //这个对话框，其实是有EMChatHelper发起的一个startActivit(Intent)，在intent里携带了用户推出去的消息
        isConflictDialogShow = true;
        EMChatHelper.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                UserService.getInstance(null).clearUser();
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
        String st = "账号已退出";
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(
                            MainActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage("账号在别地登陆");
                conflictBuilder.setPositiveButton("好的",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                conflictBuilder = null;
                                setResult(RESULT_OK);
                                finish();
                               /* startActivity(new Intent(MainActivity.this,
                                        LoginActivity.class));*/
                            }
                        });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG,
                        "---------color conflictBuilder error" + e.getMessage());
            }
        }
    }

    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        EMChatHelper.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                UserService.getInstance(null).clearUser();
                ;
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
        String st5 = "账号被移除";
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(
                            MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage("账号被移除");
                accountRemovedBuilder.setPositiveButton("好的",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                accountRemovedBuilder = null;
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e(TAG,
                        "---------color userRemovedBuilder error"
                                + e.getMessage());
            }
        }
    }

    /*
     * 消息监听器
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                System.out.println("有新消息....");
                EMMessage message = (EMMessage) event.getData();
                System.out.println("消息laizi:" + message.getUserName());
                // 提示新消息
                EMChatHelper.getInstance().getNotifier().onNewMsg(message);
                refreshUIWithMessage();
                break;
            }

            case EventOfflineMessage: {
                refreshUIWithMessage();
                break;
            }

            case EventConversationListChanged: {
                refreshUIWithMessage();
                break;
            }

            default:
                break;
        }
    }

    /*
     * 用广播接收器来接受， 用户的群组和好友变化广播
     */
    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("新消息来了：" + intent.getAction());
                // updateUnreadLabel();
                // updateUnreadAddressLable();
                /*
                 * if (currentTabIndex == 0) { // 当前页面如果为聊天历史页面，刷新此页面 if
				 * (conversationListFragment != null) {
				 * conversationListFragment.refresh(); } } else if
				 * (currentTabIndex == 1) { if(contactListFragment != null) {
				 * contactListFragment.refresh(); } }
				 */
                // 群组变动消息
                /*
                 * String action = intent.getAction();
				 * if(action.equals(Constant.ACTION_GROUP_CHANAGED)){ if
				 * (EaseCommonUtils
				 * .getTopActivity(MainActivity.this).equals(GroupsActivity
				 * .class.getName())) { GroupsActivity.instance.onResume(); } }
				 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactsFragment.refreshUnread();
                        refreshUnread();
                    }
                });
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastReceiver() {
        if (broadcastReceiver != null) {
            broadcastManager.unregisterReceiver(broadcastReceiver);
        }

    }

    /*
     * 这是EMEvent消息监听器里使用的。因为ConversationListFragment自动刷新消息的机制放在onResume中
     * 如果当前屏幕是这个Fragment，若不注册消息监听，则不能刷新
     * 同时，在MainActivity的EventListener里，还要去管理地步小红点的刷新
     */
    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                // updateUnreadLabel();
				/*
				 * if (currentTabIndex == 0) { // 当前页面如果为聊天历史页面，刷新此页面 if
				 * (conversationListFragment != null) {
				 * conversationListFragment.refresh(); } }
				 */
                if (fragments.get("2") != null) {
                    ((EMConversationListFragment) fragments.get("2")).refresh();
                    refreshUnread();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConflict && !isCurrentAccountRemoved) {
            // updateUnreadLabel();刷新未读消息数
            // updateUnreadAddressLable();刷新申请与通知消息数
        }

        refreshUnread();

        // unregister this event listener when this activity enters the
        // background
        EMChatHelper sdkHelper = EMChatHelper.getInstance();
        sdkHelper.pushActivity(this);

        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventConversationListChanged});
    }

    @Override
    protected void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        EMChatHelper sdkHelper = EMChatHelper.getInstance();
        sdkHelper.popActivity(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }
        unregisterBroadcastReceiver();

        try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
    }

    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        EMChatHelper.getInstance().getNotifier().viberateAndPlayTone(null);

        // 刷新bottom bar消息未读数
        // updateUnreadAddressLable();
        // 刷新好友页面ui
		/*
		 * if (currentTabIndex == 1) contactListFragment.refresh();
		 */
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        // 保存未读数，这里没有精确计算
        inviteMessgeDao.saveUnreadMessageCount(1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
        //这里需要仔细看ACITIVITY的生命周期和onNewIntent的设计机制
        //demo里面说的很清楚了，EMChatHelper用startActivity发送下线消息的时候，会出弹出对话框，同时会把这个Activity里的isConfict熟悉改成true
        //如果用户此时没有点击对话框的确认按钮，返回home了，此时Activity有被销毁的风险
        //用户再次进来，有可能是重新创建的MainActivity
        //只有用这个保存的状态，让系统为我们保存一些必要的控制信息，才能正确的初始化这个Activity
    }

    private void refreshUnread() {
        unreadContactCount = inviteMessgeDao.getUnreadMessagesCount();
        if (unreadContactCount > 0) {
            unreadContact.setText(unreadContactCount + "");
            unreadContact.setVisibility(View.VISIBLE);
        } else {
            unreadContact.setVisibility(View.GONE);
        }
        unreadChatCount = EMChatManager.getInstance().getUnreadMsgsCount();
        if (unreadChatCount > 0) {
            unreadChat.setText(unreadChatCount + "");
            unreadChat.setVisibility(View.VISIBLE);
        } else {
            unreadChat.setVisibility(View.GONE);
        }

    }
	
	/*
	 * ---------------------------------------分割行--------------------------------
	 * --------
	 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.getBoolean(EMConstant.ACCOUNT_REMOVED,
                false)) {
            // 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            EMChatHelper.getInstance().logout(true, null);
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        } else if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setContentView(R.layout.layout_main_drawer);
        initView();
        if (getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)
                && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)
                && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }

        inviteMessgeDao = new InviteMessgeDao(this);
        userDao = new UserDao(this);
        showFragment(this.fragmentIndex);

        // 注册群组和联系人监听
        EMChatHelper.getInstance().registerGroupAndContactListener();

        registerBroadcastReceiver();
    }

    private void initView() {
        xiaoxiWrapper = (RelativeLayout) findViewById(R.id.wrapper_xiaoxi);
        huodongWrapper = (RelativeLayout) findViewById(R.id.wrapper_huodong);
        dingWrapper = (RelativeLayout) findViewById(R.id.wrapper_ding);
        gongzuoWrapper = (RelativeLayout) findViewById(R.id.wrapper_gongzuo);
        gerenWrapper = (RelativeLayout) findViewById(R.id.wrapper_geren);

        tongxunImg = (ImageButton) findViewById(R.id.img_xiaoxi);
        tongxunText = (TextView) findViewById(R.id.tongxunText);
        xiaoxiImg = (ImageButton) findViewById(R.id.img_huodong);
        xiaoxiText = (TextView) findViewById(R.id.xiaoxiText);
        dingImage = (ImageButton) findViewById(R.id.img_ding);
        gongzuoImage = (ImageButton) findViewById(R.id.img_gongzuo);
        gongzuoText = (TextView) findViewById(R.id.gongzuoText);
        gerenImage = (ImageButton) findViewById(R.id.img_geren);
        gerenText = (TextView) findViewById(R.id.gerenText);

        unreadContact = (TextView) findViewById(R.id.unread_contact);
        unreadChat = (TextView) findViewById(R.id.unread_chat);

        xiaoxiWrapper.setOnClickListener(this);
        huodongWrapper.setOnClickListener(this);
        gerenWrapper.setOnClickListener(this);
        gongzuoWrapper.setOnClickListener(this);
        dingWrapper.setOnClickListener(this);

    }

    private void showFragment(int fragmentIndex) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        hideOthers(ft, fragmentIndex);
        if (!fragments.containsKey(fragmentIndex + "")) {
            switch (this.fragmentIndex) {
                case 1:
                    contactsFragment = new ContactsFragment();
                    addFragment(fragmentIndex, contactsFragment, ft);
                    break;
                case 2:
                    conversationListFragment = new EMConversationListFragment();
                    addFragment(2, conversationListFragment, ft);
                    break;
                case 3:
                    kaoqinFragment = new KaoqinFragment();
                    addFragment(3, kaoqinFragment, ft);
                    break;
                case 4:
                    gongzuoFragment = new GongzuoFragment();
                    addFragment(4, gongzuoFragment, ft);
                    break;
                case 5:
                    gerenFragment = new GerenFragment();
                    addFragment(5, gerenFragment, ft);
                    break;
                default:
                    break;
            }
        }
        ft.show(fragments.get(this.fragmentIndex + ""));
        ft.commit();
    }

    private void addFragment(int index, Fragment fragment, FragmentTransaction ft) {
        fragments.put(index + "", fragment);
        ft.add(R.id.fragment_container, fragment, index + "");
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.wrapper_xiaoxi:
                switchFragment(1);
                break;
            case R.id.wrapper_huodong:
                switchFragment(2);
                break;
            case R.id.wrapper_ding:
                switchFragment(3);
                break;
            case R.id.wrapper_gongzuo:
                switchFragment(4);
                break;
            case R.id.wrapper_geren:
                switchFragment(5);
                break;
            default:
                break;
        }
    }

    private void switchFragment(int index) {
        if (fragmentIndex != index) {
            switchIcon(index);
            fragmentIndex = index;
            showFragment(fragmentIndex);
        }
    }

    /*
     * 这个有可能是引起小米切换fragment卡顿的原因！！！！
     * 后面再处理
     */
    private void switchIcon(int index) {
        switch (fragmentIndex) {
            case 1:
//                tongxunImg.setImageBitmap(BitmapUtils.readBitMap(this, R.drawable.tongxun_icon));
                tongxunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.tongxun_icon));
                tongxunText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case 2:
//                xiaoxiImg.setImageBitmap(BitmapUtils.readBitMap(this, R.drawable.xiaoxin_icon));
                xiaoxiImg.setImageDrawable(getResources().getDrawable(R.drawable.xiaoxin_icon));
                xiaoxiText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case 3:
                break;
            case 4:
//			gongzuoImage.setImageResource(R.drawable.yingxiang);
//                gongzuoImage.setImageBitmap(BitmapUtils.readBitMap(this, R.drawable.kaoqin_icon));
                gongzuoImage.setImageResource(R.drawable.kaoqin_icon);
                gongzuoText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case 5:
//                gerenImage.setImageBitmap(BitmapUtils.readBitMap(this, R.drawable.geren_icon));
                gerenImage.setImageResource(R.drawable.geren_icon);
                gerenText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                break;
            default:
                break;
        }

        switch (index) {
            case 1:
//                tongxunImg.setImageBitmap(BitmapUtils.readBitMap(this, R.drawable.tongxun_blue_icon));
                tongxunImg.setImageResource(R.drawable.tongxun_blue_icon);
                tongxunText.setTextColor(getResources().getColor(R.color.narivar_bottom_text_blue));
                break;
            case 2:
//                xiaoxiImg.setImageBitmap(BitmapUtils.readBitMap(this, R.drawable.xiaoxin_blue_icon));
                xiaoxiImg.setImageResource(R.drawable.xiaoxin_blue_icon);
                xiaoxiText.setTextColor(getResources().getColor(R.color.narivar_bottom_text_blue));
                break;
            case 3:
                break;
            case 4:
//                gongzuoImage.setImageBitmap(BitmapUtils.readBitMap(this, R.drawable.kaoqin_blue_icon));
                gongzuoImage.setImageResource(R.drawable.kaoqin_blue_icon);
                gongzuoText.setTextColor(getResources().getColor(R.color.narivar_bottom_text_blue));
                break;
            case 5:
//                gerenImage.setImageBitmap(BitmapUtils.readBitMap(this, R.drawable.geren_blue_icon));
                gerenImage.setImageResource(R.drawable.geren_blue_icon);
                gerenText.setTextColor(getResources().getColor(R.color.narivar_bottom_text_blue));
                break;
            default:
                break;
        }
    }

    private void hideOthers(FragmentTransaction ft, int index) {
        for (int i = 1; i <= 5; i++) {
            if (i != index) {
                if (fragments.containsKey(i + "")) {
                    Fragment fragment = fragments.get(i + "");
                    if (fragment != null) {
                        ft.hide(fragment);
                    }
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // this.gestureListener.fireTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }


}
