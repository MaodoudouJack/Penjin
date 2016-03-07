package com.penjin.android.activity.gongzuo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.util.NetUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.activity.PenjinBaseActivity;
import com.penjin.android.activity.photo.util.FileUtils;
import com.penjin.android.constants.HttpConstants;
import com.penjin.android.constants.PenjinConstants;
import com.penjin.android.domain.BillSubType;
import com.penjin.android.domain.BillType;
import com.penjin.android.domain.PenjinBillMan;
import com.penjin.android.domain.PenjinFlowNode;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.http.HttpConstant;
import com.penjin.android.http.HttpService;
import com.penjin.android.service.UserService;
import com.penjin.android.view.ContextAlterPopupWindow;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.PjChuchaiItem;
import com.penjin.android.view.TitleBarView;
import com.penjin.android.view.penjin.FlowLine;
import com.penjin.android.view.penjin.FlowMan;
import com.penjin.android.widget.CustomEditDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.me.lewisdeane.ldialogs.BaseDialog;
import uk.me.lewisdeane.ldialogs.CustomListDialog;

/**
 * 出差申请
 * Created by maotiancai on 2016/1/13.
 */
public class ChuchaiApplyActivity extends PenjinBaseActivity implements View.OnClickListener {

    private CustomProgressDialog progressDialog;
    LinearLayout chuchaiList;
    LinearLayout flowWrapper;
    int currentPickedItem;
    int datePickType = -1;
    View add;
    View startDateWrapper;
    View endDateWrapper;
    TextView startDate;
    TextView endDate;
    EditText remark;
    Button apply;
    CustomEditDialog dialog;
    CustomEditDialog.CustomEditDialogListner customEditDialogListner;

    CustomEditDialog modifyDialog;
    CustomEditDialog.CustomEditDialogListner modifyListner;

    ContextAlterPopupWindow contextAlterPopupWindow;
    ContextAlterPopupWindow.ContextAlertPopupWindowInterface contextAlertPopupWindowInterface;

    DatePickerDialog datePicker;
    com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener onDateSetListener;

    HttpService httpService;
    PenjinUser user;
    private TitleBarView titleBarView;

    /**
     * 单据部分
     */
    private boolean isBillTypeLoaded = false;
    private Map<String, BillType> billTypes = new HashMap<String, BillType>();
    private Map<String, BillSubType> billSubTypes = new HashMap<String, BillSubType>();
    private BillType currentBillType;
    private BillSubType currentBillSubType;
    private View billTypeWrapper;
    private View billSubTypeWrapper;
    private CustomListDialog billTypeDialog;
    private CustomListDialog billSubTypeDialog;
    private CustomListDialog.ListClickListener billTypeListener;
    private CustomListDialog.ListClickListener billSubTypeListener;
    private TextView billType;
    private TextView billSubType;

    private void initBillType() {

        billType = (TextView) findViewById(R.id.danjuType);
        billSubType = (TextView) findViewById(R.id.chuchaiType);
        billTypeWrapper = findViewById(R.id.billTypeWrapper);
        billSubTypeWrapper = findViewById(R.id.billSubTypeWrapper);
        billSubTypeWrapper.setOnClickListener(this);
        billTypeWrapper.setOnClickListener(this);

        billTypeListener = new CustomListDialog.ListClickListener() {
            @Override
            public void onListItemSelected(int i, String[] strings, String s) {
                System.out.println(s);
                currentBillType = billTypes.get(s);
                billType.setText(currentBillType.typeName);
                //这里显示工作流
                if (currentBillType != null) {
                    flowWrapper.removeAllViews();
                    addFlowMan(currentBillType);
                }
            }
        };
        billSubTypeListener = new CustomListDialog.ListClickListener() {
            @Override
            public void onListItemSelected(int i, String[] strings, String s) {
                System.out.println(s);
                currentBillSubType = billSubTypes.get(s);
                billSubType.setText(currentBillSubType.typeName);
            }
        };
    }

    private void requestBillType() {
        if (!isBillTypeLoaded) {
            RequestParams requestParams = new RequestParams();
            requestParams.put("companyId", user.getCompanyId());
            requestParams.put("billSort", HttpConstants.chuchaiBillSort);
            requestParams.put("type", HttpConstants.chuchaiType);
            requestParams.put("sn", HttpService.getInstance(this.getApplicationContext()).getSn());
            try {
                httpService.postRequestAsync(HttpConstants.HOST + HttpConstants.BillProperty, requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        if (s != null)
                            System.out.println(s);
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        System.out.println(s);
                        if (s != null) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                if (jo.getBoolean("result")) {
                                    JSONArray billTypeArray = jo.getJSONArray("data");
                                    JSONArray billSubTypeArray = jo.getJSONArray("data1");
                                    //处理第一类型
                                    if (billTypeArray != null && billTypeArray.length() > 0 && billSubTypeArray != null && billSubTypeArray.length() > 0) {
                                        billTypes.clear();
                                        for (int a = 0; a < billTypeArray.length(); a++) {
                                            JSONObject billTypeJO = billTypeArray.getJSONObject(a);
                                            BillType billType = new BillType();
                                            billType.type = billTypeJO.getString("type");
                                            billType.typeName = billTypeJO.getString("typename");
                                            billType.flowNodes = new ArrayList<PenjinFlowNode>();//初始化列表
                                            billTypes.put(billTypeJO.getString("typename"), billType);
                                            //处理申请单对应的审批流
                                            JSONArray processes = billTypeJO.getJSONArray("auditprocess");
                                            if (processes != null && processes.length() > 0) {
                                                for (int m = 0; m < processes.length(); m++) {
                                                    JSONObject process = processes.getJSONObject(m);
                                                    PenjinFlowNode flowNode = new PenjinFlowNode();
                                                    flowNode.nodeName = process.getString("name");
                                                    flowNode.billManList = new ArrayList<PenjinBillMan>();
                                                    String[] billMans = process.getString("phone").split(";");
                                                    for (int n = 0; n < billMans.length; n++) {
                                                        String[] infos = billMans[n].split("-");
                                                        PenjinBillMan billMan = new PenjinBillMan();
                                                        billMan.name = infos[1];
                                                        billMan.phone = infos[0];
                                                        flowNode.billManList.add(billMan);
                                                    }
                                                    billType.flowNodes.add(m, flowNode);
                                                }
                                            }
                                        }
                                        handler.sendEmptyMessage(1);
                                        //处理子类型
                                        billSubTypes.clear();
                                        for (int b = 0; b < billSubTypeArray.length(); b++) {
                                            BillSubType billSubType = new BillSubType();
                                            billSubType.type = billSubTypeArray.getJSONObject(b).getString("type");
                                            billSubType.typeName = billSubTypeArray.getJSONObject(b).getString("typename");
                                            billSubTypes.put(billSubTypeArray.getJSONObject(b).getString("typename"), billSubType);
                                        }
                                        handler.sendEmptyMessage(2);
                                        isBillTypeLoaded = true;
                                    }
                                } else {
                                    isBillTypeLoaded = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                System.out.println("共有" + billTypes.size() + "种出差单据");
                String[] items = new String[billTypes.size()];
                Set<String> keySets = billTypes.keySet();
                Iterator<String> keys = keySets.iterator();
                int i = 0;
                while (i < billTypes.size()) {
                    items[i] = new String(keys.next().toString());
                    System.out.println(items[i]);
                    i++;
                }
                CustomListDialog.Builder builder = new CustomListDialog.Builder(ChuchaiApplyActivity.this, "单据类型", items);
                builder.titleAlignment(BaseDialog.Alignment.CENTER); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
                builder.itemAlignment(BaseDialog.Alignment.LEFT); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
                builder.titleColor(R.color.common_bottom_bar_selected_bg); // int res, or int colorRes parameter versions available as well.
                builder.itemColor(R.color.top_bar_normal_bg); // int res, or int colorRes parameter versions available as well.
                builder.titleTextSize(18);
                builder.itemTextSize(14);
                billTypeDialog = builder.build();
                billTypeDialog.setListClickListener(billTypeListener);
                billType.setText("点击选择");
            } else if (msg.what == 2) {
                String[] items = new String[billSubTypes.size()];
                Set<String> keySets = billSubTypes.keySet();
                Iterator<String> keys = keySets.iterator();
                int i = 0;
                while (i < billSubTypes.size()) {
                    items[i] = keys.next();
                    i++;
                }
                CustomListDialog.Builder builder = new CustomListDialog.Builder(ChuchaiApplyActivity.this, "出差类型", items);
                builder.titleAlignment(BaseDialog.Alignment.CENTER); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
                builder.itemAlignment(BaseDialog.Alignment.LEFT); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
                builder.titleColor(R.color.common_bottom_bar_selected_bg); // int res, or int colorRes parameter versions available as well.
                builder.itemColor(R.color.top_bar_normal_bg); // int res, or int colorRes parameter versions available as well.
                builder.titleTextSize(18);
                builder.itemTextSize(14);
                billSubTypeDialog = builder.build();
                billSubTypeDialog.setListClickListener(billSubTypeListener);
                billSubType.setText("点击选择");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_chuchai_apply);
        user = UserService.getInstance(this.getApplicationContext()).getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "用户尚未登陆~", Toast.LENGTH_SHORT).show();
            finish();
        }
        initView();
        initTitleBar();
        initHttpModule();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestBillType();
    }

    @Override
    protected void onDestroy() {
        PenjinConstants.currentBitmaps = null;
        super.onDestroy();
    }

    private void initView() {
        initBillType();
        initDate();
        progressDialog = CustomProgressDialog.createDialog(this);
        initModifyDialog();
        initPopupWindow();
        initCreateDialog();
        flowWrapper = (LinearLayout) findViewById(R.id.flowLineWrapper);
        apply = (Button) findViewById(R.id.apply);
        apply.setOnClickListener(this);
        chuchaiList = (LinearLayout) findViewById(R.id.chuchaiList);
        add = findViewById(R.id.add);
        add.setOnClickListener(this);
        remark = (EditText) findViewById(R.id.remark);
    }

    private void initDate() {
        this.onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                if (datePickType == 1) {
                    startDate.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                } else if (datePickType == 2) {
                    endDate.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                }

            }
        };
        Date date = new Date();
        int year = date.getYear() + 1900;
        int month = date.getMonth();
        int day = date.getDate();
        datePicker = DatePickerDialog.newInstance(this.onDateSetListener, year, month, day);
        startDateWrapper = findViewById(R.id.startDateWrapper);
        endDateWrapper = findViewById(R.id.endDateWapper);
        startDate = (TextView) findViewById(R.id.startDate);
        endDate = (TextView) findViewById(R.id.endDate);
        startDateWrapper.setOnClickListener(this);
        endDateWrapper.setOnClickListener(this);
        startDate.setText(year + "年" + (month + 1) + "月" + day + "日");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, +1);
        endDate.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日");
    }

    private void initTitleBar() {
        titleBarView = (TitleBarView) findViewById(R.id.titleBar);
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
    }

    private void initModifyDialog() {
        modifyDialog = new CustomEditDialog(this, R.style.PenjinCommonDialog, "修 改 地 点", null);
        modifyListner = new CustomEditDialog.CustomEditDialogListner() {
            @Override
            public void ok(String msg, View view) {
                if (currentPickedItem != -1) {
                    if (msg != null && !msg.equals("")) {
                        ((PjChuchaiItem) chuchaiList.getChildAt(currentPickedItem)).setDidianDetail(msg);
                    }
                    currentPickedItem = -1;
                }
                modifyDialog.dismiss();
            }

            @Override
            public void cancel(String msg) {
                currentPickedItem = -1;
                modifyDialog.dismiss();
            }
        };
        modifyDialog.setCustomEditDialogListnerlistner(modifyListner);
    }

    private void initPopupWindow() {
        contextAlterPopupWindow = new ContextAlterPopupWindow(this);
        contextAlertPopupWindowInterface = new ContextAlterPopupWindow.ContextAlertPopupWindowInterface() {
            @Override
            public void onModify() {
                contextAlterPopupWindow.dismiss();
                if (currentPickedItem != -1) {
                    System.out.println("共有" + chuchaiList.getChildCount() + "个孩子");
                    View child = chuchaiList.getChildAt(currentPickedItem);
                    if (child == null) {
                        System.out.println("child 为空");
                    }
                    String hint = ((PjChuchaiItem) chuchaiList.getChildAt(currentPickedItem)).getDidianDetail();
                    System.out.println("hint" + hint);
                    modifyDialog.setHint(hint);
                    modifyDialog.show();
                }
            }

            @Override
            public void onDelete() {
                if (currentPickedItem != -1) {
                    chuchaiList.removeViewAt(currentPickedItem);
                }
                contextAlterPopupWindow.dismiss();
            }
        };
        contextAlterPopupWindow.setContextAlertPopupWindowInterface(contextAlertPopupWindowInterface);
    }

    private void initCreateDialog() {
        dialog = new CustomEditDialog(this, R.style.PenjinCommonDialog);
        customEditDialogListner = new CustomEditDialog.CustomEditDialogListner() {
            @Override
            public void ok(String msg, View view) {
                if (msg != null && !msg.equals("")) {
                    int count = ChuchaiApplyActivity.this.chuchaiList.getChildCount();
                    PjChuchaiItem item = new PjChuchaiItem(ChuchaiApplyActivity.this);
                    item.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    item.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            LinearLayout parent = (LinearLayout) v.getParent();
                            int count = parent.getChildCount();
                            int i = 0;
                            for (; i < count; i++) {
                                if (parent.getChildAt(i) == v) {
                                    break;
                                }
                            }
                            currentPickedItem = i;
                            contextAlterPopupWindow.showAtView(v);
                            return true;
                        }
                    });
                    item.setItemId(count);
                    item.setDidianDetail(msg);
                    chuchaiList.addView(item, count);
                    chuchaiList.invalidate();
                }
                dialog.dismiss();
            }

            @Override
            public void cancel(String msg) {
                dialog.dismiss();
            }
        };
        dialog.setCustomEditDialogListnerlistner(this.customEditDialogListner);
    }

    private void initHttpModule() {
        httpService = HttpService.getInstance(this.getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.billTypeWrapper:
                if (billTypeDialog != null) {
                    billTypeDialog.show();
                }
                break;
            case R.id.billSubTypeWrapper:
                if (billSubTypeDialog != null) {
                    billSubTypeDialog.show();
                }
                break;
            case R.id.add:
                dialog.show();
                break;
            case R.id.startDateWrapper:
                datePickType = 1;//选择开始日期
                datePicker.show(getFragmentManager(), "hello");
                break;
            case R.id.endDateWapper:
                datePickType = 2;//选择结束日期
                datePicker.show(getFragmentManager(), "hello");
                break;
            case R.id.apply:
                if (!NetUtils.hasNetwork(this)) {
                    Toast.makeText(this, "亲，网络有问题哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentBillType == null || currentBillSubType == null) {
                    Toast.makeText(this, "请选择表单类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (chuchaiList.getChildCount() == 0) {
                    Toast.makeText(this, "请添加出差地点", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {
                        launchRequest();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void addFlowMan(JSONArray ja) {
        FlowMan flowMan = new FlowMan(this);
        flowMan.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        flowMan.setName("王经理");
        addFlowItemAtLast(flowMan);
        FlowLine flowLine = new FlowLine(this);
        flowLine.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        addFlowItemAtLast(flowLine);
        FlowMan flowMan1 = new FlowMan(this);
        flowMan1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        flowMan1.setName("人事经理");
        addFlowItemAtLast(flowMan1);
        FlowLine flowLine1 = new FlowLine(this);
        flowLine1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        addFlowItemAtLast(flowLine1);
    }

    private void addFlowMan(BillType billType) {
        FlowMan tijiao = new FlowMan(this);
        tijiao.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        tijiao.setName("提交");
        tijiao.setIsDone(true);
        this.flowWrapper.addView(tijiao);
        addFlowLine();

        if (billType.flowNodes != null && billType.flowNodes.size() > 0) {
            for (int i = 0; i < billType.flowNodes.size(); i++) {
                PenjinFlowNode newNode = new PenjinFlowNode(billType.flowNodes.get(i));
                FlowMan flowMan = new FlowMan(this);
                flowMan.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                flowMan.setName(newNode.billManList.get(0).name);
                flowMan.setTag(newNode);
                this.flowWrapper.addView(flowMan);
                addFlowLine();
            }
        }
        FlowMan wanchen = new FlowMan(this);
        wanchen.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        wanchen.setName("完成");
        this.flowWrapper.addView(wanchen);
    }

    private void addFlowLine() {
        FlowLine flowLine = new FlowLine(this);
        flowLine.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        this.flowWrapper.addView(flowLine);
    }

    //在最后一个孩子之前添加
    private void addFlowItemAtLast(View view) {
        int count = flowWrapper.getChildCount();
        flowWrapper.addView(view, count - 1);
    }

    private List<String> getChuchaiList() {
        List<String> list = new ArrayList<>();
        int count = chuchaiList.getChildCount();
        for (int i = 0; i < count; i++) {
            list.add(((PjChuchaiItem) (chuchaiList.getChildAt(i))).getDidianDetail());
        }
        return list;
    }

    private RequestParams generateRequestParams() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("staffNumber", user.getStaffNum());
        requestParams.put("companyId", user.getCompanyId());
        requestParams.put("billType", currentBillType.type);
        requestParams.put("chuChaiType", currentBillSubType.type);
        requestParams.put("sn", httpService.getSn());
        List<String> addressList = getChuchaiList();
        requestParams.put("addressCount", addressList.size());
        int i = 1;
        for (; i <= addressList.size(); i++) {
            requestParams.put("address" + i, addressList.get(i - 1));
        }
        requestParams.put("startDate", startDate.getText().toString());
        requestParams.put("endDate", endDate.getText().toString());
        requestParams.put("remark", remark.getEditableText().toString());
        return requestParams;
    }

    private void launchRequest() throws Exception {
        progressDialog.show();
        RequestParams requestParams = generateRequestParams();
        httpService.postRequest(this, HttpConstants.HOST + HttpConstants.ChuchaiApply, requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (s != null) {
                    Toast.makeText(ChuchaiApplyActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(ChuchaiApplyActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
