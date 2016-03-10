package com.penjin.android.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.List;

/**
 * Created by maotiancai on 2016/1/8.
 */
public class HttpService {

    private static HttpService instance;
    private AsyncHttpClient httpClient;
    private Context mContext;
    private PersistentCookieStore cookieStore;
    private String sn;

    private HttpService(Context context) {
        httpClient = new AsyncHttpClient();
        httpClient.setTimeout(5000);
        httpClient.setResponseTimeout(5000);
        this.mContext = context;
        cookieStore = new PersistentCookieStore(mContext);
        httpClient.setCookieStore(cookieStore);
    }

    public static HttpService getInstance(Context context) {
        synchronized (HttpService.class) {
            if (instance == null) {
                instance = new HttpService(context);
            }
        }
        return instance;
    }

    public void setCookie(String key, String value) {
        BasicClientCookie cookie = new BasicClientCookie(key, value);
        cookie.setDomain(HttpConstant.HOST);
        this.cookieStore.addCookie(cookie);
    }

    /**
     * 执行POST请求
     */
    public void postRequest(Context context, String url, RequestParams requestParams, ResponseHandlerInterface handlerInterface) throws Exception {
        this.httpClient.post(context, url, requestParams, handlerInterface);
    }

    /**
     * 在非UI现场中处理结果，需要通过handler等机制来通知UI
     */
    public void postRequestAsync(String url, RequestParams requestParams, ResponseHandlerInterface handlerInterface) throws Exception {
        this.httpClient.post(url, requestParams, handlerInterface);
    }

    public String getSn() {
        if (this.sn == null) {
            SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("PenjinUser", 1);
            this.sn = sharedPreferences.getString("sn", "-1");
        }
        return this.sn;

    }

    public RequestHandle postRequestWithHandle(Context context,String url,RequestParams params,ResponseHandlerInterface responseHandlerInterface) throws Exception{
        return this.httpClient.post(context,url,params,responseHandlerInterface);
    }

    public void setSn(String sn) {
        this.sn = sn;
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("PenjinUser", 1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sn", sn);
        editor.commit();
    }

    /**
     * 获得浏览器实例
     *
     * @return
     */
    public AsyncHttpClient getHttpClient() {
        return this.httpClient;
    }

    /**
     * 获得浏览器Cookie存储单元
     */
    public PersistentCookieStore getCookieStore() {
        return cookieStore;
    }


}
