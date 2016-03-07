package com.penjin.android.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.penjin.android.activity.MainActivity;
import com.penjin.android.message.db.DBManager;
import com.penjin.android.message.db.DBModel;
import com.penjin.android.message.db.InviteMessgeDao;
import com.penjin.android.message.db.UserDao;
import com.penjin.android.message.domain.EaseUser;
import com.penjin.android.message.domain.InviteMessage;
import com.penjin.android.message.domain.InviteMessage.InviteMesageStatus;
import com.penjin.android.message.parse.UserProfileManager;
import com.penjin.android.message.reciever.CallReceiver;
import com.penjin.android.message.utils.EaseCommonUtils;
import com.penjin.android.message.utils.EaseNotifier;
import com.penjin.android.message.utils.EaseNotifier.EaseNotificationInfoProvider;
import com.penjin.android.message.utils.EaseUI;
import com.penjin.android.message.utils.EaseUI.EaseSettingsProvider;
import com.penjin.android.message.utils.PreferenceManager;
import com.penjin.android.service.UserService;

public class EMChatHelper {

    /*
     * UI 接口
     */
    private EaseUI easeUI;

    /*
     * 内部静态接口，供使用者注册自己的业务监听器
     */
    public static interface DataSyncListener {
        /**
         * 同步完毕
         *
         * @param success true：成功同步到数据，false失败
         */
        public void onSyncComplete(boolean success);
    }

    /*
     * 内部静态数据
     */

    private UserProfileManager userProManager;
    private static String TAG = "EMChatHelper";
    private boolean sdkInited = false;
    private static EMChatHelper intstance = null;
    private Context appContext;

    public boolean isVoiceCalling;
    public boolean isVideoCalling;

    /*
     * 数据服务层与Dao层
     */
    private DBModel dbModel;
    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    /*
     * 监听器
     */
    private EMConnectionListener connectionListener;
    private LocalBroadcastManager broadcastManager;
    private CallReceiver callReceiver;
    protected EMEventListener eventListener = null;

    private List<DataSyncListener> syncGroupsListeners;
    private List<DataSyncListener> syncContactsListeners;
    private List<DataSyncListener> syncBlackListListeners;

    private boolean alreadyNotified = false;
    private boolean isGroupAndContactListenerRegisted = false;

    /*
     * 每次连接成功之后，去服务器同步一次数据
     */
    private boolean isSyncingGroupsWithServer = false;
    private boolean isSyncingContactsWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;

    /*
     * 内存缓存
     */
    private Map<String, EaseUser> contactList;
    private String username;

    private EMChatHelper() {
    }

    public synchronized static EMChatHelper getInstance() {
        if (intstance == null) {
            intstance = new EMChatHelper();
        }
        return intstance;
    }

    /*
     * 全局的初始化动作
     */
    public synchronized void init(Context context) {
        if (sdkInited) {
            return;
        }
        appContext = context;
        EMChat.getInstance().setDebugMode(true);// 设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
        EMChat.getInstance().setAutoLogin(true);//设置是否自动登陆
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        Log.d(TAG, "process app name : " + processAppName);
        if (processAppName == null
                || !processAppName
                .equalsIgnoreCase(appContext.getPackageName())) {
            Log.e(TAG, "enter the service process!");
            return;
        }

        // 初始化数据接口
        dbModel = new DBModel(context);

        EMChat.getInstance().init(context);
        setChatoptions();

        // 初始化UI接口
        easeUI = EaseUI.getInstance();
        easeUI.init(context);
        setEaseUIProviders();

        // 初始化SharedPreferenced
        PreferenceManager.init(context);
        //初始化用户管理类
        getUserProfileManager().init(context);

        // 初始化全局监听
        setGlobalListeners();
        broadcastManager = LocalBroadcastManager.getInstance(appContext);

        // 初始化数据层
        initDbDao();

        sdkInited = true;
    }

    protected void setEaseUIProviders() {

        //需要easeui库显示用户头像和昵称设置此provider easeUI.setUserProfileProvider(new
        easeUI.setEaseUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });

        // 设置聊天参数,比如声音、震动等 不设置，则使用easeui默认的
        easeUI.setSettingsProvider(new EaseSettingsProvider() {

            @Override
            public boolean isSpeakerOpened() {
                return dbModel.getSettingMsgSpeaker();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return dbModel.getSettingMsgVibrate();
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return dbModel.getSettingMsgSound();
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                if (message == null) {
                    return dbModel.getSettingMsgNotification();
                }
                if (!dbModel.getSettingMsgNotification()) {
                    return false;
                } else {
                    // 如果允许新消息提示
                    // 屏蔽的用户和群组不提示用户
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // 获取设置的不提示新消息的用户或者群组ids
                    if (message.getChatType() == ChatType.Chat) {
                        chatUsename = message.getFrom();
                        notNotifyIds = dbModel.getDisabledIds();
                    } else {
                        chatUsename = message.getTo();
                        notNotifyIds = dbModel.getDisabledGroups();
                    }

                    if (notNotifyIds == null
                            || !notNotifyIds.contains(chatUsename)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });

        // 设置Notifier inten跳转 不设置，则使用easeui默认的
        easeUI.getNotifier().setNotificationInfoProvider(
                new EaseNotificationInfoProvider() {

                    @Override
                    public String getTitle(EMMessage message) {
                        // 修改标题,这里使用默认
                        return "测试消息";
                    }

                    @Override
                    public int getSmallIcon(EMMessage message) {
                        // 设置小图标，这里为默认
                        return 0;
                    }

                    @Override
                    public String getDisplayedText(EMMessage message) {
                        // 设置状态栏的消息提示，可以根据message的类型做相应提示
                        // String ticker =
                        // EaseCommonUtils.getMessageDigest(message,
                        // appContext);
                        String ticker = "测试标题栏";
                        if (message.getType() == Type.TXT) {
                            ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                        }
                        EaseUser user = getUserInfo(message.getFrom());
                        if (user != null) {
                            return getUserInfo(message.getFrom()).getNick()
                                    + ": " + ticker;
                        } else {
                            return message.getFrom() + ": " + ticker;
                        }
                    }

                    @Override
                    public String getLatestText(EMMessage message,
                                                int fromUsersNum, int messageNum) {
                        return null;
                        // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
                    }

                    @Override
                    public Intent getLaunchIntent(EMMessage message) {
                        // 设置点击通知栏跳转事件
                        /*
                         * Intent intent = new Intent(appContext,
						 * ChatActivity.class); //有电话时优先跳转到通话页面
						 * if(isVideoCalling){ intent = new Intent(appContext,
						 * VideoCallActivity.class); }else if(isVoiceCalling){
						 * intent = new Intent(appContext,
						 * VoiceCallActivity.class); }else{ ChatType chatType =
						 * message.getChatType(); if (chatType == ChatType.Chat)
						 * { // 单聊信息 intent.putExtra("userId",
						 * message.getFrom()); intent.putExtra("chatType",
						 * Constant.CHATTYPE_SINGLE); } else { // 群聊信息 //
						 * message.getTo()为群聊id intent.putExtra("userId",
						 * message.getTo()); if(chatType == ChatType.GroupChat){
						 * intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
						 * }else{ intent.putExtra("chatType",
						 * Constant.CHATTYPE_CHATROOM); }
						 * 
						 * } } return intent;
						 */
                        Intent intent = new Intent(appContext,
                                MainActivity.class);
                        return intent;
                    }
                });
    }

    /*
     * 监听器 初始化
     */
    private void setGlobalListeners() {
        syncGroupsListeners = new ArrayList<DataSyncListener>();
        syncContactsListeners = new ArrayList<DataSyncListener>();
        syncBlackListListeners = new ArrayList<DataSyncListener>();

        isGroupsSyncedWithServer = dbModel.isGroupsSynced();
        isContactsSyncedWithServer = dbModel.isContactSynced();
        isBlackListSyncedWithServer = dbModel.isBacklistSynced();

        // 注册EMChat连接监听 EMConnectListener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                System.out.println("没有连接。。。");
                if (error == EMError.USER_REMOVED) {
                    onCurrentAccountRemoved();
                } else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                }// 网络不稳定的消息交由Fragment来处理
            }

            @Override
            public void onConnected() {
                // in case group and contact were already synced, we supposed to
                // notify sdk we are ready to receive the events

                System.out.println("EMChatHelper上的连接上了");
                if (isGroupsSyncedWithServer && isContactsSyncedWithServer) {
                    new Thread() {
                        @Override
                        public void run() {
                            EMChatHelper.getInstance()
                                    .notifyForRecevingEvents();
                        }
                    }.start();
                } else {
                    if (!isGroupsSyncedWithServer) {
                        asyncFetchGroupsFromServer(null);
                    }

                    if (!isContactsSyncedWithServer) {
                        asyncFetchContactsFromServer(null);
                    }

                    if (!isBlackListSyncedWithServer) {
                        asyncFetchBlackListFromServer(null);
                    }
                }
            }
        };
        EMChatManager.getInstance().addConnectionListener(connectionListener);

        // 注册语言通话广播接收者
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance()
                .getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }
        appContext.registerReceiver(callReceiver, callFilter);

        // 注册群组和联系人监听
        registerGroupAndContactListener();

        // 注册消息事件监听
        registerEventListener();
    }

    /*
     * 环信聊天设置
     */
    private void setChatoptions() {
        Log.d(TAG, "init HuanXin Options");
        // 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(true);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);
        // 设置从db初始化加载时, 每个conversation需要加载msg的个数
        options.setNumberOfMessagesLoaded(1);
        options.allowChatroomOwnerLeave(getDBModel()
                .isChatroomOwnerLeaveAllowed());
    }

    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(appContext);
        userDao = new UserDao(appContext);
    }

    /**
     * 账号在别的设备登录 设计模式：MainActivity保持单例模式 singleTask
     * MainActivity用onNewIntent来接受这个intent消息 intent消息里面携带一个字段来表示账户有冲突
     */
    protected void onConnectionConflict() {

        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_CONFLICT, true);
        appContext.startActivity(intent);
    }

    /**
     * 账号被移除
     */
    protected void onCurrentAccountRemoved() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }

    /**
     * 同步操作，从服务器获取群组列表 该方法会记录更新状态，可以通过isSyncingGroupsFromServer获取是否正在更新
     * 和isGroupsSyncedWithServer获取是否更新已经完成
     *
     * @throws EaseMobException
     */
    public synchronized void asyncFetchGroupsFromServer(
            final EMCallBack callback) {
        if (isSyncingGroupsWithServer) {
            return;
        }

        isSyncingGroupsWithServer = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    /*
                     * 从服务器端获取当前用户的所有群组
					 * （此操作只返回群组列表，并不获取群组的所有成员信息，如果要更新某个群组包括成员的全部信息， 需要再调用
					 * getGroupFromServer(String groupId),，一般来说取到后需要保存一下，
					 * 调用createOrUpdateLocalGroup(EMGroup)） this api will get
					 * groups from remote server and update local groups
					 */
                    EMGroupManager.getInstance().getGroupsFromServer();

                    // in case that logout already before server returns, we
                    // should return immediately
                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }

                    dbModel.setGroupsSynced(true);

                    isGroupsSyncedWithServer = true;
                    isSyncingGroupsWithServer = false;

                    // 通知listener同步群组完毕
                    noitifyGroupSyncListeners(true);
                    if (isContactsSyncedWithServer()) {
                        notifyForRecevingEvents();
                    }
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (EaseMobException e) {
                    dbModel.setGroupsSynced(false);
                    isGroupsSyncedWithServer = false;
                    isSyncingGroupsWithServer = false;
                    noitifyGroupSyncListeners(false);
                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void noitifyGroupSyncListeners(boolean success) {
        for (DataSyncListener listener : syncGroupsListeners) {
            listener.onSyncComplete(success);
        }
    }

    public void asyncFetchContactsFromServer(
            final EMValueCallBack<List<String>> callback) {
        if (isSyncingContactsWithServer) {
            return;
        }

        isSyncingContactsWithServer = true;

        new Thread() {//注意这里已经开启一个线程去获取用户的个人信息了
            @Override
            public void run() {
                List<String> usernames = null;
                try {
                    usernames = EMContactManager.getInstance()
                            .getContactUserNames();// 这里一次拿全部的联系人列表
                    // in case that logout already before server returns, we
                    // should return immediately
                    System.out.println("contac list" + usernames);
                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }

                    Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                    for (String username : usernames) {
                        EaseUser user = new EaseUser(username);
                        EaseCommonUtils.setUserInitialLetter(user);
                        userlist.put(username, user);
                    }
                    // 存入内存
                    getContactList().clear();
                    getContactList().putAll(userlist);
                    // 存入db
                    UserDao dao = new UserDao(appContext);
                    List<EaseUser> users = new ArrayList<EaseUser>(
                            userlist.values());
                    dao.saveContactList(users);

                    dbModel.setContactSynced(true);
                    EMLog.d(TAG, "set contact syn status to true");

                    isContactsSyncedWithServer = true;
                    isSyncingContactsWithServer = false;

                    // 通知listeners联系人同步完毕
                    notifyContactsSyncListener(true);
                    if (isGroupsSyncedWithServer()) {
                        notifyForRecevingEvents();
                    }

                    // 档案管理方面暂时不看

                    getUserProfileManager().asyncFetchContactInfosFromServer(
                            usernames, new EMValueCallBack<List<EaseUser>>() {

                                @Override
                                public void onSuccess(List<EaseUser> uList) {
                                    updateContactList(uList);
                                    getUserProfileManager()
                                            .notifyContactInfosSyncListener(true);
                                }

                                @Override
                                public void onError(int error, String errorMsg) {
                                }
                            });
                    if (callback != null) {
                        callback.onSuccess(usernames);
                    }

                } catch (EaseMobException e) {
                    dbModel.setContactSynced(false);
                    isContactsSyncedWithServer = false;
                    isSyncingContactsWithServer = false;
                    noitifyGroupSyncListeners(false);
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void notifyContactsSyncListener(boolean success) {
        for (DataSyncListener listener : syncContactsListeners) {
            listener.onSyncComplete(success);
        }
    }

    public void asyncFetchBlackListFromServer(
            final EMValueCallBack<List<String>> callback) {

        if (isSyncingBlackListWithServer) {
            return;
        }

        isSyncingBlackListWithServer = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    List<String> usernames = EMContactManager.getInstance()
                            .getBlackListUsernamesFromServer();

                    // in case that logout already before server returns, we
                    // should return immediately
                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }

                    dbModel.setBlacklistSynced(true);

                    isBlackListSyncedWithServer = true;
                    isSyncingBlackListWithServer = false;

                    EMContactManager.getInstance().saveBlackList(usernames);
                    notifyBlackListSyncListener(true);
                    if (callback != null) {
                        callback.onSuccess(usernames);
                    }
                } catch (EaseMobException e) {
                    dbModel.setBlacklistSynced(false);

                    isBlackListSyncedWithServer = false;
                    isSyncingBlackListWithServer = true;
                    e.printStackTrace();

                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void notifyBlackListSyncListener(boolean success) {
        for (DataSyncListener listener : syncBlackListListeners) {
            listener.onSyncComplete(success);
        }
    }

    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    public boolean isSyncingBlackListWithServer() {
        return isSyncingBlackListWithServer;
    }

    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
        return isContactsSyncedWithServer;
    }

    public boolean isBlackListSyncedWithServer() {
        return isBlackListSyncedWithServer;
    }

    public synchronized void notifyForRecevingEvents() {
        if (alreadyNotified) {
            return;
        }

        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();// 这个是用于sdk内部的操作，让sdk内部可以发送相应的消息到回调函数
        alreadyNotified = true;
    }

    public void addSyncGroupListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncGroupsListeners.contains(listener)) {
            syncGroupsListeners.add(listener);
        }
    }

    public void removeSyncGroupListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncGroupsListeners.contains(listener)) {
            syncGroupsListeners.remove(listener);
        }
    }

    public void addSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncContactsListeners.contains(listener)) {
            syncContactsListeners.add(listener);
        }
    }

    public void removeSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncContactsListeners.contains(listener)) {
            syncContactsListeners.remove(listener);
        }
    }

    public void addSyncBlackListListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncBlackListListeners.contains(listener)) {
            syncBlackListListeners.add(listener);
        }
    }

    public void removeSyncBlackListListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncBlackListListeners.contains(listener)) {
            syncBlackListListeners.remove(listener);
        }
    }

    /*
     * 注册群组和联系人监听，由于logout的时候会被sdk清除掉，再次登录的时候需要再注册一下
     */
    public void registerGroupAndContactListener() {
        if (!isGroupAndContactListenerRegisted) {
            // 注册群组变动监听
            EMGroupManager.getInstance().addGroupChangeListener(
                    new MyGroupChangeListener());
            // 注册联系人变动监听
            EMContactManager.getInstance().setContactListener(
                    new MyContactListener());
            isGroupAndContactListenerRegisted = true;
        }
    }

    /**
     * 全局事件监听 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理 activityList.size()
     * <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        eventListener = new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onEvent(EMNotifierEvent event) {
                System.out.println("EMChatHelper:获取到新消息...");
                EMMessage message = null;
                if (event.getData() instanceof EMMessage) {
                    message = (EMMessage) event.getData();
                    EMLog.d(TAG, "receive the event : " + event.getEvent()
                            + ",id : " + message.getMsgId());
                }
                switch (event.getEvent()) {
                    case EventNewMessage:
                        // 应用在后台，不需要刷新UI,通知栏提示新消息
                        if (!easeUI.hasForegroundActivies()) {
                            getNotifier().onNewMsg(message);
                        }
                        break;
                    case EventOfflineMessage:
                        if (!easeUI.hasForegroundActivies()) {
                            EMLog.d(TAG, "received offline messages");
                            List<EMMessage> messages = (List<EMMessage>) event
                                    .getData();
                            getNotifier().onNewMesg(messages);
                        }
                        break;
                    // below is just giving a example to show a cmd toast, the app
                    // should not follow this
                    // so be careful of this
                    case EventNewCMDMessage: {

                        EMLog.d(TAG, "收到透传消息");
                        // 获取消息body
                        CmdMessageBody cmdMsgBody = (CmdMessageBody) message
                                .getBody();
                        final String action = cmdMsgBody.action;// 获取自定义action

                        // 获取扩展属性 此处省略
                        // message.getStringAttribute("");
                        EMLog.d(TAG, String.format("透传消息：action:%s,message:%s",
                                action, message.toString()));
                        // final String str =
                        // appContext.getString(R.string.receive_the_passthrough);
                        final String str = "收到透传消息";

                        final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                        IntentFilter cmdFilter = new IntentFilter(
                                CMD_TOAST_BROADCAST);

                        if (broadCastReceiver == null) {
                            broadCastReceiver = new BroadcastReceiver() {

                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(appContext,
                                            intent.getStringExtra("cmd_value"),
                                            Toast.LENGTH_SHORT).show();
                                }
                            };

                            // 注册广播接收者
                            appContext.registerReceiver(broadCastReceiver,
                                    cmdFilter);
                        }

                        Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                        broadcastIntent.putExtra("cmd_value", str + action);
                        appContext.sendBroadcast(broadcastIntent, null);

                        break;
                    }
                    case EventDeliveryAck:
                        message.setDelivered(true);
                        break;
                    case EventReadAck:
                        message.setAcked(true);
                        break;
                    // add other events in case you are interested in
                    default:
                        break;
                }

            }
        };

        EMChatManager.getInstance().registerEventListener(eventListener);
    }

    /**
     * 数据库中存储邀请信息 保存并提示消息的邀请消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessgeDao(appContext);
        }
        inviteMessgeDao.saveMessage(msg);
        // 保存未读数，这里没有精确计算
        inviteMessgeDao.saveUnreadMessageCount(1);
        // 提示有新消息
        getNotifier().viberateAndPlayTone(null);
    }

    private EaseUser getUserInfo(String username) {
        // 获取user信息，demo是从内存的好友列表里获取，
        // 实际开发中，可能还需要从服务器获取用户信息,
        // 从服务器获取的数据，最好缓存起来，避免频繁的网络请求
        EaseUser user = null;
        if (username.equals(EMChatManager.getInstance().getCurrentUser()))
            return getUserProfileManager().getCurrentUserInfo();
        user = getContactList().get(username);
        return user;
    }

    /*
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, EaseUser> contactList) {
        this.contactList = contactList;
    }

    /*
     * 获取好友list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        if (isLoggedIn() && contactList == null) {
            contactList = dbModel.getContactList();
        }
        return contactList;
    }

    public String getCurrentUsernName() {
        if (username == null) {
            username = dbModel.getCurrentUsernName();
        }
        return username;
    }

    public DBModel getDBModel() {
        return this.dbModel;
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
                    .next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm
                            .getApplicationInfo(info.processName,
                                    PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /*
     * 获取消息通知类
     *
     * @return
     */
    public EaseNotifier getNotifier() {
        return easeUI.getNotifier();
    }

    /**
     * 是否登录成功过
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMChat.getInstance().isLoggedIn();
    }

    synchronized void reset() {
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;

        dbModel.setGroupsSynced(false);
        dbModel.setContactSynced(false);
        dbModel.setBlacklistSynced(false);

        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;

        alreadyNotified = false;
        isGroupAndContactListenerRegisted = false;

        setContactList(null);
        // setRobotList(null);
        // getUserProfileManager().reset();
        DBManager.getInstance().closeDB();
    }

    /**
     * 群组变动监听
     */
    public class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName,
                                         String inviter, String reason) {

            boolean hasGroup = false;
            for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    break;
                }
            }
            if (!hasGroup)
                return;

            // 被邀请
            // String st3 =
            // appContext.getString(R.string.Invite_you_to_join_a_group_chat);
            String st3 = "被邀请";
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(inviter + " " + st3));
            // 保存邀请消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            getNotifier().viberateAndPlayTone(msg);
            // 发送local广播
            broadcastManager.sendBroadcast(new Intent(
                    Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter,
                                        String reason) {
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee,
                                         String reason) {
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            // TODO 提示用户被T了，demo省略此步骤
            broadcastManager.sendBroadcast(new Intent(
                    Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {
            // 群被解散
            // TODO 提示用户群被解散,demo省略
            broadcastManager.sendBroadcast(new Intent(
                    Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationReceived(String groupId, String groupName,
                                          String applyer, String reason) {

            // 用户申请加入群聊
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
            msg.setStatus(InviteMesageStatus.BEAPPLYED);
            notifyNewIviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(
                    Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName,
                                        String accepter) {

			/*
             * String st4 = appContext
			 * .getString(R.string.Agreed_to_your_group_chat_application);
			 */
            String st4 = "加群被同意...";
            // 加群申请被同意
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(accepter + " " + st4));
            // 保存同意消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            // getNotifier().viberateAndPlayTone(msg);
            broadcastManager.sendBroadcast(new Intent(
                    Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName,
                                          String decliner, String reason) {
            // 加群申请被拒绝，demo未实现
        }
    }

    /*
     * 好友变化listener
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {
            // 保存增加的联系人
            System.out.println("新增了联系人:" + usernameList);
            Map<String, EaseUser> localUsers = getContactList();
            System.out.println(localUsers);
            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            for (String username : usernameList) {
                EaseUser user = new EaseUser(username);
                // 添加好友时可能会回调added方法两次
                if (!localUsers.containsKey(username)) {
                    userDao.saveContact(user);
                }
                toAddUsers.put(username, user);
            }
            localUsers.putAll(toAddUsers);
            // 发送好友变动广播
            broadcastManager.sendBroadcast(new Intent(
                    Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除
            Map<String, EaseUser> localUsers = EMChatHelper.getInstance()
                    .getContactList();
            for (String username : usernameList) {
                localUsers.remove(username);
                userDao.deleteContact(username);
                inviteMessgeDao.deleteMessage(username);
            }
            broadcastManager.sendBroadcast(new Intent(
                    Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactInvited(String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null
                        && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(
                    Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "同意了你的好友请求");
            msg.setStatus(InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(
                    Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }

    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }

    /**
     * 退出登录
     *
     * @param unbindDeviceToken 是否解绑设备token(使用GCM才有)
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        EMChatManager.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    private void endCall() {
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置当前用户的环信id
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        dbModel.setCurrentUserName(username);
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }

    /**
     * update user list to cach And db
     *
     * @param contactList
     */
    public void updateContactList(List<EaseUser> contactInfoList) {
        for (EaseUser u : contactInfoList) {
            contactList.put(u.getUsername(), u);
        }
        ArrayList<EaseUser> mList = new ArrayList<EaseUser>();
        mList.addAll(contactList.values());
        dbModel.saveContactList(mList);
    }
}
