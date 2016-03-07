package com.penjin.android.message.contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.penjin.android.R;
import com.penjin.android.message.EMChatHelper;
import com.penjin.android.message.db.InviteMessgeDao;
import com.penjin.android.message.db.UserDao;
import com.penjin.android.message.domain.EaseUser;
import com.penjin.android.message.ui.EMBaseFragment;
import com.penjin.android.message.utils.EaseCommonUtils;
import com.penjin.android.message.view.contact.ContactItemView;
import com.penjin.android.message.view.contact.EaseContactList;
import com.penjin.android.view.AddFriendDialog;
import com.penjin.android.view.AddFriendPopupWindow;
import com.penjin.android.view.TitleBarView;
import com.penjin.android.view.AddFriendDialog.AddFriendDialogListenr;
import com.penjin.android.view.AddFriendPopupWindow.AddFriendPopupWindowInterface;
import com.penjin.android.view.TitleBarView.TitleBarListener;

/*
 * 所有好友的联系人列表
 */
public class EMContactListFragment extends EMBaseFragment implements OnClickListener {

	// 这里有一个FrameLayout来显示好友列表是否加载完成，给EMChatHelper注册一个监听器
	//
	private ContactSyncListener contactSyncListener;
	// private BlackListSyncListener blackListSyncListener;

	private static final String TAG = "EMContactListFragment";

	private int unreadContactCount=0;
	
	protected List<EaseUser> contactList;
	protected ListView listView;
	protected boolean hidden=false;
	protected List<String> blackList;
	protected ImageButton clearSearch;
	protected EditText query;
	protected Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(getActivity(), "好友请求发送成功~~", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(getActivity(), "好友请求发送失败", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
		
	};
	protected EaseUser toBeProcessUser;
	protected String toBeProcessUsername;
	protected EaseContactList contactListLayout;
	protected boolean isConflict;
	protected FrameLayout contentContainer;
	private EaseContactListItemClickListener listItemClickListener;
	
	private ContactItemView  applicationIitem;
	private InviteMessgeDao inviteMessgeDao;
	
	private TitleBarView titleBar;
	private TitleBarListener titleBarListener;

	// 好友列表
	private Map<String, EaseUser> contactsMap;
	
	private AddFriendPopupWindow addFriendPopupWindow;
	private AddFriendPopupWindowInterface addPopwinListener = new AddFriendPopupWindowInterface() {

		@Override
		public void sao() {
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
			if(EaseCommonUtils.isNetWorkConnected(getActivity())){
				Thread thread=new Thread(){
					@Override
					public void run() {
							try {
								EMContactManager.getInstance().addContact(username, "添加好友测试...");
								handler.sendEmptyMessage(0);
							} catch (EaseMobException e) {
								e.getErrorCode();
								e.printStackTrace();
								handler.sendEmptyMessage(1);
							}
					}
				};
				thread.start();
			}else
			{
				handler.sendEmptyMessage(1);
			}
			
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.ease_fragment_contact_list, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false))
			return;
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected void initView() {
		addFriendPopupWindow=new AddFriendPopupWindow(getActivity());
		addFriendPopupWindow.setListener(addPopwinListener);
		addFriendDialog = new AddFriendDialog(getActivity(),R.style.AddFriendDialog);
		addFriendDialog.setListenr(addFriendDialogListenr);
		applicationIitem=(ContactItemView)getView().findViewById(R.id.application_item);
		titleBar = (TitleBarView) getView().findViewById(R.id.title_bar);
		inviteMessgeDao=new InviteMessgeDao(getActivity());
		contentContainer = (FrameLayout) getView().findViewById(
				R.id.content_container);
		contactListLayout = (EaseContactList) getView().findViewById(
				R.id.contact_list);
		listView = contactListLayout.getListView();
		//为listView注册上下文菜单
		registerForContextMenu(listView);
		// 搜索框
		query = (EditText) getView().findViewById(R.id.query);
		clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
	}

	@Override
	protected void setUpView() {
		titleBarListener = new TitleBarListener() {

			@Override
			public void right(View view) {
				addFriendPopupWindow.showAt(titleBar);
			}

			@Override
			public void left(View view) {
				getActivity().finish();
			}

			@Override
			public void center(View view) {

			}
		};
		titleBar.setTitleBarListener(titleBarListener);
		EMChatManager.getInstance().addConnectionListener(connectionListener);

		// 黑名单列表
		blackList = EMContactManager.getInstance().getBlackListUsernames();
		// 普通好友列表
		setContactsMap(EMChatHelper.getInstance().getContactList());
		contactList = new ArrayList<EaseUser>();
		// 获取设置contactlist
		getContactList();
		// init list
		contactListLayout.init(contactList);

		if (listItemClickListener != null) {
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					EaseUser user = (EaseUser) listView
							.getItemAtPosition(position);
					listItemClickListener.onListItemClicked(user);
					// itemClickLaunchIntent.putExtra(EaseConstant.USER_ID,
					// username);
				}
			});
		}

		query.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				contactListLayout.filter(s);
				if (s.length() > 0) {
					clearSearch.setVisibility(View.VISIBLE);
				} else {
					clearSearch.setVisibility(View.INVISIBLE);

				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		clearSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				query.getText().clear();
				hideSoftKeyboard();
			}
		});

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				hideSoftKeyboard();
				return false;
			}
		});
		/*
		 * 控制器 if (!EMChatHelper.getInstance().isContactsSyncedWithServer()) {
		 * loadingView.setVisibility(View.VISIBLE); } else {
		 * loadingView.setVisibility(View.GONE); }
		 */
	}

	/*
	 * 手动管理fragment的时候，都是用show和hide方式，这样避免fragment重新初始化。
	 * onHiddenChanged回调就是来响应show和hide事件的。
	 * show方法调用时，传给该回调函数一个false值
	 */
	/*@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}*/

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * 把user移入到黑名单
	 */
	protected void moveToBlacklist(final String username) {
		final ProgressDialog pd = new ProgressDialog(getActivity());
		String st1 = getResources().getString(R.string.Is_moved_into_blacklist);
		final String st2 = getResources().getString(
				R.string.Move_into_blacklist_success);
		final String st3 = getResources().getString(
				R.string.Move_into_blacklist_failure);
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					// 加入到黑名单
					EMContactManager.getInstance().addUserToBlackList(username,
							false);
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st2, 0).show();
							refresh();
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st3, 0).show();
						}
					});
				}
			}
		}).start();

	}

	// 刷新ui
	public void refresh() {
		System.out.println("refresh ui。。。。");
		unreadContactCount= inviteMessgeDao.getUnreadMessagesCount();
		if(unreadContactCount>0)
		{
			System.out.println("邀请联系人个数:"+unreadContactCount);
			applicationIitem.showUnreadMsgView();
			applicationIitem.showAlert();
			applicationIitem.setOnClickListener(this);
		}else
		{
			System.out.println("没有额外的邀请信息...");
			applicationIitem.hideUnreadMsgView();
			applicationIitem.setOnClickListener(null);
		}
		getContactList();
		contactListLayout.refresh();
	}
	
	@Override
	public void onDestroy() {
		
		EMChatManager.getInstance()
				.removeConnectionListener(connectionListener);

		super.onDestroy();
	}

	/**
	 * 获取联系人列表，并过滤掉黑名单和排序
	 */
	protected void getContactList() {
		contactList.clear();
		synchronized (contactList) {
			// 获取联系人列表
			if (contactsMap == null) {
				return;
			}
			Iterator<Entry<String, EaseUser>> iterator = contactsMap.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, EaseUser> entry = iterator.next();
				// 兼容以前的通讯录里的已有的数据显示，加上此判断，如果是新集成的可以去掉此判断
				if (!entry.getKey().equals("item_new_friends")
						&& !entry.getKey().equals("item_groups")
						&& !entry.getKey().equals("item_chatroom")
						&& !entry.getKey().equals("item_robots")) {
					if (!blackList.contains(entry.getKey())) {
						// 不显示黑名单中的用户
						EaseUser user = entry.getValue();
						EaseCommonUtils.setUserInitialLetter(user);
						contactList.add(user);
					}
				}
			}
			// 排序
			Collections.sort(contactList, new Comparator<EaseUser>() {

				@Override
				public int compare(EaseUser lhs, EaseUser rhs) {
					if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
						return lhs.getNick().compareTo(rhs.getNick());
					} else {
						if ("#".equals(lhs.getInitialLetter())) {
							return 1;
						} else if ("#".equals(rhs.getInitialLetter())) {
							return -1;
						}
						return lhs.getInitialLetter().compareTo(
								rhs.getInitialLetter());
					}

				}
			});
		}

	}

	protected EMConnectionListener connectionListener = new EMConnectionListener() {

		@Override
		public void onDisconnected(int error) {
			if (error == EMError.USER_REMOVED
					|| error == EMError.CONNECTION_CONFLICT) {
				isConflict = true;
			} else {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						onConnectionDisconnected();
					}

				});
			}
		}

		@Override
		public void onConnected() {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					onConnectionConnected();
				}

			});
		}
	};

	protected void onConnectionDisconnected() {

	}

	protected void onConnectionConnected() {

	}

	/**
	 * 设置需要显示的数据map，key为环信用户id
	 * 
	 * @param contactsMap
	 */
	public void setContactsMap(Map<String, EaseUser> contactsMap) {
		this.contactsMap = contactsMap;
	}

	public interface EaseContactListItemClickListener {
		/**
		 * 联系人listview item点击事件
		 * 
		 * @param user
		 *            被点击item所对应的user对象
		 */
		void onListItemClicked(EaseUser user);
	}

	/**
	 * 设置listview item点击事件
	 * 
	 * @param listItemClickListener
	 */
	public void setContactListItemClickListener(
			EaseContactListItemClickListener listItemClickListener) {
		this.listItemClickListener = listItemClickListener;
	}

	public void refreshContactCount()
	{
		
	}
	
	/**
	 * 删除联系人
	 * 
	 * @param toDeleteUser
	 */
	public void deleteContact(final EaseUser tobeDeleteUser) {
		String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.Delete_failed);
		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
					// 删除db和内存中此用户的数据
					UserDao dao = new UserDao(getActivity());
					dao.deleteContact(tobeDeleteUser.getUsername());
					EMChatHelper.getInstance().getContactList().remove(tobeDeleteUser.getUsername());
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							contactList.remove(tobeDeleteUser);
							contactListLayout.refresh();

						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st2 + e.getMessage(), 1).show();
						}
					});

				}

			}
		}).start();

	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (isConflict) {
			outState.putBoolean("isConflict", true);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		toBeProcessUser = (EaseUser) listView
				.getItemAtPosition(((AdapterContextMenuInfo) menuInfo).position);
		toBeProcessUsername = toBeProcessUser.getUsername();
		getActivity().getMenuInflater().inflate(R.menu.em_context_contact_list,
				menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
			try {
				// 删除此联系人
				deleteContact(toBeProcessUser);
				// 删除相关的邀请消息
				InviteMessgeDao dao = new InviteMessgeDao(getActivity());
				dao.deleteMessage(toBeProcessUser.getUsername());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (item.getItemId() == R.id.add_to_blacklist) {
			moveToBlacklist(toBeProcessUsername);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private class ContactSyncListener implements EMChatHelper.DataSyncListener {

		@Override
		public void onSyncComplete(boolean success) {

		}

	}


	@Override
	public void onClick(View arg0) {
		Intent intent=new Intent();
		switch (arg0.getId()) {
		case R.id.application_item:
			intent.setClass(getActivity(), NewFriendsMsgActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
