package com.penjin.android.activity.gongzuo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.util.NetUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.activity.photo.PenjinGalleryActivity;
import com.penjin.android.activity.photo.util.Bimp;
import com.penjin.android.activity.photo.util.FileUtils;
import com.penjin.android.activity.photo.util.ImageItem;
import com.penjin.android.constants.HttpConstants;
import com.penjin.android.constants.PenjinConstants;
import com.penjin.android.domain.BillSubType;
import com.penjin.android.domain.BillType;
import com.penjin.android.domain.PenjinBillMan;
import com.penjin.android.domain.PenjinFlowNode;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.http.HttpService;
import com.penjin.android.service.UserService;
import com.penjin.android.utils.BitmapUtils;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.TitleBarView;
import com.penjin.android.view.penjin.FlowLine;
import com.penjin.android.view.penjin.FlowMan;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.me.lewisdeane.ldialogs.BaseDialog;
import uk.me.lewisdeane.ldialogs.CustomListDialog;

/**
 * Created by maotiancai on 2016/1/14.
 */
public class BukaApplyActivity extends Activity implements View.OnClickListener {
    private CustomProgressDialog progressDialog;
    LinearLayout flowWrapper;
    int datePickType = -1;
    View startDateWrapper;
    TextView startDate;
    View startTimeWrapper;
    TextView startTime;
    EditText remark;
    Button apply;
    HttpService httpService;
    PenjinUser user;
    TitleBarView titleBarView;

    private Uri currentBitmapUri;
    private GridView noScrollgridview;
    private GridAdapter adapter;

    DatePickerDialog datePicker;
    com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener onDateSetListener;
    private String currentBitmapPath;


    /**
     * 单据部分
     */
    private boolean isBillTypeLoaded = false;
    private Map<String, BillType> billTypes = new HashMap<String, BillType>();
    private BillType currentBillType;
    private View billTypeWrapper;
    private CustomListDialog billTypeDialog;
    private CustomListDialog.ListClickListener billTypeListener;
    private TextView billType;

    private void initBillType() {

        billType = (TextView) findViewById(R.id.danjuType);
        billTypeWrapper = findViewById(R.id.billTypeWrapper);
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
    }

    private void addFlowLine() {
        FlowLine flowLine = new FlowLine(this);
        flowLine.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        this.flowWrapper.addView(flowLine);
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

    private void requestBillType() {
        if (!isBillTypeLoaded) {
            RequestParams requestParams = new RequestParams();
            requestParams.put("companyId", user.getCompanyId());
            requestParams.put("billSort", HttpConstants.qingjiaBillSort);
            requestParams.put("type", HttpConstants.qingjiaType);
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
                                    //处理第一类型
                                    if (billTypeArray != null && billTypeArray.length() > 0) {
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
                                                    System.out.println(process.getString("phone"));
                                                    for (int n = 0; n < billMans.length; n++) {
                                                        String[] infos = billMans[n].split("-");
                                                        System.out.println(infos);
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
                String[] items = new String[billTypes.size()];
                Set<String> keySets = billTypes.keySet();
                Iterator<String> keys = keySets.iterator();
                int i = 0;
                while (i < billTypes.size()) {
                    items[i] = new String(keys.next().toString());
                    i++;
                }
                CustomListDialog.Builder builder = new CustomListDialog.Builder(BukaApplyActivity.this, "单据类型", items);
                builder.titleAlignment(BaseDialog.Alignment.CENTER); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
                builder.itemAlignment(BaseDialog.Alignment.LEFT); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
                builder.titleColor(R.color.common_bottom_bar_selected_bg); // int res, or int colorRes parameter versions available as well.
                builder.itemColor(R.color.top_bar_normal_bg); // int res, or int colorRes parameter versions available as well.
                builder.titleTextSize(18);
                builder.itemTextSize(14);
                billTypeDialog = builder.build();
                billTypeDialog.setListClickListener(billTypeListener);
                billType.setText("点击选择");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PenjinConstants.currentBitmaps = null;
        PenjinConstants.currentBitmaps = new Bimp();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_buka_apply);
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
        if (adapter != null)
            adapter.update();
        super.onResume();
        if (!isBillTypeLoaded)
            requestBillType();
    }

    private void initView() {
        initBillType();
        initDate();
        progressDialog = CustomProgressDialog.createDialog(this);
        this.onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                if (datePickType == 1) {
                    startDate.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                } else if (datePickType == 2) {
                }

            }
        };
        flowWrapper = (LinearLayout) findViewById(R.id.flowLineWrapper);
        apply = (Button) findViewById(R.id.apply);
        apply.setOnClickListener(this);
        remark = (EditText) findViewById(R.id.remark);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (position == PenjinConstants.currentBitmaps.currentBitmaps.size()) {
                    capture();
                } else {
                    Intent intent = new Intent(BukaApplyActivity.this,
                            PenjinGalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", position);
                    startActivity(intent);
                }
            }
        });
    }

    private void initDate() {
        this.onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                if (datePickType == 1) {
                    startDate.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                } else if (datePickType == 2) {
                }

            }
        };
        Date date = new Date();
        int year = date.getYear() + 1900;
        int month = date.getMonth();
        int day = date.getDate();
        datePicker = DatePickerDialog.newInstance(this.onDateSetListener, year, month, day);
        startDateWrapper = findViewById(R.id.startDateWrapper);
        startDate = (TextView) findViewById(R.id.startDate);
        startDateWrapper.setOnClickListener(this);
        startDate.setText(year + "年" + (month + 1) + "月" + day + "日");
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
            case R.id.startDateWrapper:
                datePickType = 1;//选择开始日期
                datePicker.show(getFragmentManager(), "hello");
                break;
            case R.id.apply:
                if (!NetUtils.hasNetwork(this)) {
                    Toast.makeText(this, "亲，网络有问题哦~", Toast.LENGTH_SHORT).show();
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

    //在最后一个孩子之前添加
    private void addFlowItemAtLast(View view) {
        int count = flowWrapper.getChildCount();
        flowWrapper.addView(view, count - 1);
    }

    private RequestParams generateRequestParams() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("staffNumber", user.getStaffNum());
        requestParams.put("companyId", user.getCompanyId());
        requestParams.put("billType", currentBillType.type);
        requestParams.put("sn", httpService.getSn());
        requestParams.put("date", startDate.getText().toString());
        requestParams.put("remark", remark.getEditableText().toString());
        return requestParams;
    }

    private void launchRequest() throws Exception {
        progressDialog.show();
        RequestParams requestParams = generateRequestParams();
        httpService.postRequest(this, HttpConstants.HOST + HttpConstants.BuqianApply, requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (s != null) {
                    Toast.makeText(BukaApplyActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(BukaApplyActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void capture() {
        Intent intentPhote = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentPhote.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        try {
            currentBitmapUri = FileUtils.getTempPhotoUri();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentBitmapUri != null) {
            currentBitmapPath = currentBitmapUri.getPath();
            intentPhote.putExtra(MediaStore.EXTRA_OUTPUT, currentBitmapUri);
            startActivityForResult(intentPhote, PenjinConstants.PENJIN_CAPTURE_PICTURE);
        } else {
            Toast.makeText(this, "尚未安装sd卡,,~", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PenjinConstants.PENJIN_CAPTURE_PICTURE:
                if (currentBitmapPath != null) {
                    if (PenjinConstants.currentBitmaps.currentBitmaps.size() < 3 && resultCode == RESULT_OK) {
                        ImageItem takePhoto = new ImageItem();
                        String fileName = String.valueOf(System.currentTimeMillis());
                        fileName = user.getPhone() + "_" + fileName;
                        Bitmap bm = BitmapUtils.decodeSampledBitmapFromResource(currentBitmapPath, 230, 230);
                        takePhoto.setBitmap(bm);
                        FileUtils.saveBitmap(bm, fileName);
                        takePhoto.setImagePath(fileName);
                        PenjinConstants.currentBitmaps.currentBitmaps.add(takePhoto);
                        this.adapter.notifyDataSetChanged();
                        File file = new File(currentBitmapPath);//删除相机原图缓存
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                break;
        }
    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            this.notifyDataSetChanged();
            //loading();
        }

        public int getCount() {
            if (PenjinConstants.currentBitmaps.currentBitmaps.size() == 3) {
                return 3;
            }
            return (PenjinConstants.currentBitmaps.currentBitmaps.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == PenjinConstants.currentBitmaps.currentBitmaps.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 3) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(PenjinConstants.currentBitmaps.currentBitmaps.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("mao", newConfig.toString());
    }
}
