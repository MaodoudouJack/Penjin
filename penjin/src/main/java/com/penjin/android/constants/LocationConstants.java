package com.penjin.android.constants;

/**
 * Created by maotiancai on 2016/1/12.
 */
public class LocationConstants {
    public final static int LOCATION_OK = 61; //GPS定位结果，GPS定位成功。
    public final static int LOCATION_FAILURE = 62;//无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。
    public final static int LOCATION_NETWORK_ERROR = 63; //网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
    public final static int LOCATION_ON_CACHE = 65;//定位缓存的结果。
    public final static int LOCATION_OFFLINE_OK = 66;//离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
    public final static int LOCATION_OFFLINE_FALURE = 67;//离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
    /*68 //网络连接失败时，查找本地离线定位时对应的返回结果。
            161//网络定位结果，网络定位定位成功。
            162//请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件。
            167//服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
            502//key参数错误，请按照说明文档重新申请KEY。
            505//key不存在或者非法，请按照说明文档重新申请KEY。
            601//key服务被开发者自己禁用，请按照说明文档重新申请KEY。
            602//key mcode不匹配*/

}
