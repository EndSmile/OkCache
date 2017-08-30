package com.xdja.okcache.okhttp.request;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.xdja.okcache.constant.CacheStrategy;
import com.xdja.okcache.constant.HeaderParams;
import com.xdja.okcache.okhttp.util.CacheOkHttpUtil;
import com.xdja.okcache.okhttp.util.SdUtils;
import com.xdja.okcache.okhttp.exception.NetworkException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Author:yusenkui</p>
 * <p>Date:2017/7/29</p>
 * <p>Time:14:59</p>
 */


public class OkHttpCacheUtils {

    public OkHttpClient client = null;
    private static String TAG = "OkHttpCacheUtils";
    private Handler mainHanlder;
    private static Context mContext;

    private OkHttpCacheUtils() {
        initOkHttp();
        mainHanlder = new Handler(Looper.getMainLooper());
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static OkHttpCacheUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     * 加载OkHttpCacheUtils 类时不会初始化instance 只有在调用getInstance 方法时，才会导致instance 被初始化,
     * 这个方法不仅能够确保线程安全，也能够保证 单例对象的唯一性,同时也延迟了单例的实例化
     */
    private static class SingletonHolder {
        private static final OkHttpCacheUtils INSTANCE = new OkHttpCacheUtils();
    }

    /**
     * 初始化okhttp
     * 定义缓存大小和路径以及拦截器
     */
    private void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //设置缓存路径
        File cacheFile = new File(SdUtils.getDiskCacheDir(mContext), "httpCache");
        //设置缓存对象
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.cache(cache)
                //// TODO: 2017/8/28 待修复
//                .addInterceptor(new CacheInterceptor())
                .addInterceptor(loggingInterceptor);
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连 关于这个错误有三种 1.无法访问ip地址 2.过时的连接池连接 3.无法连接代理服务器
        builder.retryOnConnectionFailure(true);
        client = builder.build();
    }

    /**
     * get同步请求带缓存
     *
     * @param url       请求地址
     * @param params    参数
     * @param headers   请求头
     * @param cacheType 缓存类型
     * @return 请求体
     * @throws NetworkException
     */
    public String okhttpGetByCacheType(String url, List<NameValuePair> params
            , List<NameValuePair> headers, int cacheTime, int cacheType) throws NetworkException {
        return getResult(url, params, headers, cacheTime, cacheType);
    }

    private String getResult(String url, List<NameValuePair> params, List<NameValuePair> headers, int cacheTime, int cacheType) {
        String result = "";
        Response response = null;

        try {
            response = okHttpGet(url, params, headers, cacheTime, cacheType);
            if (response != null && response.isSuccessful()) {
                result = response.body().string();
            } else {
                throw new NetworkException("服务器不稳定或发生错误,响应错误(响应代码为" + response.code() + ")" + ",请重试!");
            }
        } catch (NetworkException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Response okHttpGet(String url, List<NameValuePair> params, List<NameValuePair> headers, int cacheTime, int cacheType) throws NetworkException {

        try {
            Request request = getRequest(url, params, headers, cacheTime, cacheType);
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * GET请求参数拼接，使用UTF编码
     */
    private static String getRequestParameters(List<NameValuePair> params) throws NetworkException {
        String requestParameters = "";
        try {
            StringBuilder paramSb = new StringBuilder();
            for (NameValuePair nameValuePair : params) {
                if (nameValuePair.value == null) {
                    nameValuePair.value = "";
                }
                paramSb.append(nameValuePair.key).append("=").append(URLEncoder.encode(nameValuePair.value, "UTF-8")).append("&");
            }
            if (paramSb.length() > 0) {
                requestParameters = paramSb.substring(0, paramSb.length() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            throw new NetworkException("客户端请求参数编码异常", e);
        }
        return requestParameters;
    }

    private Headers getRequestHeaders(List<NameValuePair> headers) {
        Headers.Builder headerBuilder = new Headers.Builder();
        for (NameValuePair header : headers) {
            headerBuilder.add(header.key, header.value);
        }
        return headerBuilder.build();
    }


    /**
     * get异步请求带缓存
     *
     * @param url       请求地址
     * @param params    参数
     * @param headers   请求头
     * @param cacheType 缓存类型
     * @return 请求体
     * @throws NetworkException
     */
    public void okHttpASyncGet(String url, List<NameValuePair> params, List<NameValuePair> headers, int cacheTime, int cacheType, IAsyncCallBack IAsyncCallBack) {
        try {
            Request request = getRequest(url, params, headers, cacheTime, cacheType);
            Call call = client.newCall(request);
            startRequest(call, IAsyncCallBack);
        } catch (NetworkException e) {
            e.printStackTrace();
        }

    }

    private Request getRequest(String url, List<NameValuePair> params, List<NameValuePair> headers, int cacheTime, int cacheType) throws NetworkException {
        if (params == null) {
            params = new ArrayList<>();
        }
        int currentCacheType;
        //网络无效的话指定读取缓存策略
        if (!CacheOkHttpUtil.isNetworkConnected(mContext)) {
            currentCacheType = CacheStrategy.ONLY_CACHE;
        } else {
            currentCacheType = cacheType;
        }
        if (cacheTime != 0) {
            NameValuePair nameValuePair = new NameValuePair("cacheTime", String.valueOf(cacheTime));
            params.add(nameValuePair);
        }
        String parameters = getRequestParameters(params);
        Uri uri = Uri.parse(url);
        if (TextUtils.isEmpty(uri.getQuery())) {
            url = url + "?" + parameters;
        } else {
            url = url + "&" + parameters;
        }
        Request.Builder builder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            builder.headers(getRequestHeaders(headers));
        }

        return builder.url(url).
                addHeader(HeaderParams.CACHE_STRATEGY, String.valueOf(currentCacheType)).build();
    }

    private void startRequest(Call call, final IAsyncCallBack IAsyncCallBack) {
        try {

            call.enqueue(new Callback() {

                @Override
                public void onFailure(@NonNull final Call arg0, @NonNull final IOException arg1) {
                    //请求失败
                    Log.d(TAG, "onFailure" + arg1.getMessage(), arg1);
                    mainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            IAsyncCallBack.onFailure(arg0, arg1);
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call arg0, @NonNull final Response response) throws IOException {
                    Log.i(TAG, "onResponse: " + Thread.currentThread().getId());
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    mainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            IAsyncCallBack.onResponse(response);
                        }
                    });

                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
