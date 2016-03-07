package com.penjin.android.activity.gongzuo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.easemob.util.NetUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.activity.PenjinBaseActivity;
import com.penjin.android.activity.photo.PenjinGalleryActivity;
import com.penjin.android.activity.photo.util.Bimp;
import com.penjin.android.activity.photo.util.FileUtils;
import com.penjin.android.activity.photo.util.ImageItem;
import com.penjin.android.activity.photo.util.Res;
import com.penjin.android.constants.PenjinConstants;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.http.HttpService;
import com.penjin.android.service.UserService;
import com.penjin.android.utils.BitmapUtils;
import com.penjin.android.utils.CalendarUtil;
import com.penjin.android.view.CircleImageView;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.TitleBarView;

import org.apache.http.Header;

/**
 * 异地考勤申请
 * Created by maotiancai on 2016/1/9.
 */
public class YidiKaoqinApplyActivity extends PenjinBaseActivity implements View.OnClickListener {

    private CustomProgressDialog progressDialog;
    private Uri currentBitmapUri;
    private String currentBitmapPath;

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private PenjinUser user;
    private View submit;
    private CircleImageView avatar;
    private TextView currentDate;
    private TextView name;
    private boolean isReadyToRequestForm = false;//用于判断执行完定位之后是否需要提交表单

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private TextView address;
    private TextView addressDetail;
    private TextView refreshLocation;
    private View add;
    private View minus;
    private boolean isLocated = false;
    private Date lastLocateDate;
    private int loacationSpan = 60 * 6000;
    private int locationRetryTimes = 5;
    private BDLocation bdLocation;
    private LocationClient mLocationClient = null;

    private BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            boolean locationResult = false;
            if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                locationResult = true;
                lastLocateDate = new Date();
                bdLocation = location;
                refreshLocationUi();
            }
            if (locationResult == true) {
                mLocationClient.stop();
                isLocated = true;
            } else {
                locationRetryTimes--;
                System.out.println("定位重试次数剩余：" + locationRetryTimes);
                if (locationRetryTimes == 0) {
                    YidiKaoqinApplyActivity.this.isLocated = false;
                    mLocationClient.stop();//表示这次定位请求失败
                }
            }
        }
    };
    private HttpService httpService;
    private TitleBarView titleBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Res.init(this);
        PenjinConstants.currentBitmaps = null;
        PenjinConstants.currentBitmaps = new Bimp();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_yidikaoqin);
        user = UserService.getInstance(this.getApplicationContext()).getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "用户尚未登陆", Toast.LENGTH_SHORT).show();
            finish();
        }
        initView();
        initTitleBar();
        initMap();
        initLocation();
        initHttpModule();
        initDate();
    }

    private void initDate() {
        Date date = new Date();
        int yyyy = CalendarUtil.getYear(date);
        int mm = CalendarUtil.getMonth(date);
        int dd = CalendarUtil.getDate(date);
        String aa = CalendarUtil.getCnDay(date);
        this.currentDate.setText(aa + ":" + yyyy + "-" + mm + "-" + dd);
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

    private void initView() {
        name = (TextView) findViewById(R.id.name);
        name.setText(UserService.getInstance(this.getApplicationContext()).getCurrentCompany().getName());
        progressDialog = CustomProgressDialog.createDialog(this);
        address = (TextView) findViewById(R.id.address);
        addressDetail = (TextView) findViewById(R.id.addressDetail);
        refreshLocation = (TextView) findViewById(R.id.refreshLocation);
        refreshLocation.setOnClickListener(this);
        add = findViewById(R.id.add);
        minus = findViewById(R.id.minus);
        add.setOnClickListener(this);
        minus.setOnClickListener(this);
        currentDate = (TextView) findViewById(R.id.currentDate);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
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
                    Intent intent = new Intent(YidiKaoqinApplyActivity.this,
                            PenjinGalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", position);
                    startActivity(intent);
                }
            }
        });

        avatar = (CircleImageView) findViewById(R.id.avatar);
        try {
            Bitmap bitmap = UserService.getInstance(this.getApplicationContext()).getUserAvatar();
            avatar.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.map);
        mBaiduMap = mMapView.getMap();
        int childCount = mMapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {

            View child = mMapView.getChildAt(i);

            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        zoom.setVisibility(View.GONE);
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    private void initHttpModule() {
        httpService = HttpService.getInstance(this.getApplicationContext());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                //判断是否定位成功
                if (isLocated) {
                    launchRequest();
                } else {
                    Toast.makeText(YidiKaoqinApplyActivity.this, "尚未定位", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.refreshLocation:
                startLocation();
                break;
            case R.id.add:
                MapStatusUpdate zoomIn = MapStatusUpdateFactory.zoomIn();
                mBaiduMap.setMapStatus(zoomIn);
                break;
            case R.id.minus:
                MapStatusUpdate zoomOut = MapStatusUpdateFactory.zoomOut();
                mBaiduMap.setMapStatus(zoomOut);
                break;
            default:
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

    private void launchRequest() {
        if (!NetUtils.hasNetwork(this)) {
            Toast.makeText(this, "亲，当前网络不可用~", Toast.LENGTH_SHORT).show();
            return;
        }
        if (this.user == null) {
            Toast.makeText(this, "用户尚未登陆!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (PenjinConstants.currentBitmaps.currentBitmaps.size() == 0) {
            Toast.makeText(this, "亲，请选择1至3张相片哦~", Toast.LENGTH_SHORT).show();
            return;
        }
        Date currentDate = new Date();
        if ((currentDate.getTime() - lastLocateDate.getTime()) < loacationSpan) {
            progressDialog.show();
            RequestParams requestParams = generateRequestParams();
            try {
                httpService.postRequestAsync("http://192.168.0.19/App/YiDiKaoQin", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        if (s != null)
                            Log.d("http failure", s);
                        else {
                            System.out.println("链接中断....");
                        }
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(YidiKaoqinApplyActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {//表示距离上一次定位已经很久了 需要重新定位

        }
    }

    private RequestParams generateRequestParams() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("staffNumber", user.getStaffNum());
        requestParams.put("companyId", user.getCompanyId());
        requestParams.put("type", 3401);
        requestParams.put("latitude", bdLocation.getLatitude());
        requestParams.put("longitude", bdLocation.getLongitude());
        requestParams.put("address", bdLocation.getAddrStr());
        requestParams.put("reason", "异地打卡申请");
        requestParams.put("sn", httpService.getSn());
        int photoCount = PenjinConstants.currentBitmaps.currentBitmaps.size();
        requestParams.put("photoCount", photoCount);
        for (int i = 1; i <= photoCount; i++) {
            File file = new File(FileUtils.SDPATH + PenjinConstants.currentBitmaps.currentBitmaps.get(i - 1).getImagePath() + ".JPEG");
            System.out.println("张片路径：" + file.getAbsolutePath());
            try {
                requestParams.put("photo" + i, file, "image/jpeg");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return requestParams;
    }

    @Override
    protected void onResume() {
        if (mMapView != null)
            mMapView.onResume();
        if (adapter != null)
            adapter.update();
        if (mLocationClient != null && isLocated == false) {
            startLocation();
        }
        super.onResume();
    }

    private void startLocation() {
        if (!NetUtils.hasNetwork(this)) {
            Toast.makeText(this, "定位前，需开启您的网络~", Toast.LENGTH_SHORT).show();
            return;
        }
        isLocated = false;
        locationRetryTimes = 5;
        mLocationClient.start();
    }

    /**
     * 更新UI上的位置信息
     */
    private void refreshLocationUi() {
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .direction(100).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, null);
        mBaiduMap.setMyLocationConfigeration(config);
        mBaiduMap.clear();
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
        // 当不需要定位图层时关闭定位图层
        mBaiduMap.animateMapStatus(msu);
        addressDetail.setText(bdLocation.getAddrStr());
    }

    @Override
    protected void onPause() {
        if (mLocationClient != null)
            mLocationClient.stop();
        if (mMapView != null)
            mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        System.out.println("YidiActivity:onDestroy");
        PenjinConstants.currentBitmaps = null;
        if (mLocationClient != null) {
            if (mLocationListener != null)
                mLocationClient.unRegisterLocationListener(mLocationListener);
            mLocationClient = null;
        }
        if (mMapView != null)
            mMapView.onDestroy();
        super.onDestroy();
    }

}
