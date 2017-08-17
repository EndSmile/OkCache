package com.xdja.cache;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xdja.cache.common.bean.Contributor;
import com.xdja.cache.common.bean.ResponseBodys;
import com.xdja.cache.common.interceptor.CacheType;
import com.xdja.cache.okhttp.request.IAsyncCallBack;
import com.xdja.cache.okhttp.request.NameValuePair;
import com.xdja.cache.okhttp.request.OkHttpCacheUtils;
import com.xdja.cache.retrofitApi.ApiFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView textView;
    private Button okhttp, okhttpAsync, retrofit;
    private static String[] responseBody = {""};
    private int mCurrentCacheType = CacheType.NETWORK_ELSE_CACHE;
    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        okhttp = (Button) findViewById(R.id.okhttp);
        okhttpAsync = (Button) findViewById(R.id.okhttp2);
        retrofit = (Button) findViewById(R.id.retrofit);
        textView = (TextView) findViewById(R.id.text);
        okhttpAsync.setOnClickListener(this);
        okhttp.setOnClickListener(this);
        retrofit.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.only_cache:
                mCurrentCacheType = CacheType.ONLY_CACHE;
                break;
            case R.id.only_network:
                mCurrentCacheType = CacheType.ONLY_NETWORK;
                break;
            case R.id.network_cache:
                mCurrentCacheType = CacheType.NETWORK_ELSE_CACHE;
                break;
            case R.id.cache_network:
                mCurrentCacheType = CacheType.CACHE_ELSE_NETWORK;
                break;
            case R.id.clean_data:
                textView.setText("");
                break;
            default:
                break;
        }
        return true;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        private MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        activity.textView.setText(responseBody[0]);
                        break;
                }
                super.handleMessage(msg);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okhttp:
                requestByOkhttp();
                break;
            case R.id.retrofit:
                requestByRetrofit();
                break;
            case R.id.okhttp2:
                asyncRequestByOkhttp();
                break;
            default:
                break;
        }
    }


    private void asyncRequestByOkhttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://api.github.com/repos/square/okhttp/issues/3490";
                OkHttpCacheUtils.getInstance().okHttpASyncGet(url, null, null, mCurrentCacheType, new IAsyncCallBack() {
                    @Override
                    public void onFailure(Call arg0, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) {
                        Log.i(TAG, "onResponse: " + Thread.currentThread().getId());
                        try {
                            String result = response.body().string();
                            Gson gson = new Gson();
                            ResponseBodys responseBodys = gson.fromJson(result, ResponseBodys.class);
                            String url1 = responseBodys.getUrl();
                            textView.setText(url1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }).start();
    }

    private void requestByRetrofit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiFactory apiFactory = new ApiFactory();
                try {
                    List<Contributor> contributors = apiFactory.getCacheApi().contributors("square", "retrofit").execute().body();
                    if (contributors != null && contributors.size() != 0) {
                        Log.d("ysk", contributors.get(0).getLogin());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void requestByOkhttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://api.github.com/repos/square/okhttp/issues/3490";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    NameValuePair nameValuePair = new NameValuePair("isCache", "false");
                    NameValuePair nameValuePair2 = new NameValuePair("cacheTime", "30");
                    params.add(nameValuePair);
                    params.add(nameValuePair2);

                    String result = OkHttpCacheUtils.getInstance().okhttpGetByCacheType(url, null, null, mCurrentCacheType);
                    Gson gson = new Gson();
                    ResponseBodys responseBodys = gson.fromJson(result, ResponseBodys.class);
                    responseBody[0] = responseBodys.getUrl();
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpCacheUtils.getInstance().client.dispatcher().cancelAll();
    }

}
