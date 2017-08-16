package com.xdja.cache.okhttp.request;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.cache.okhttp.request</p>
 * <p>Author:yusenkui</p>
 * <p>Date:2017/8/14</p>
 * <p>Time:10:04</p>
 */


public interface IAsyncCallBack {
    void onFailure(Call arg0, IOException e);

    void onResponse(Response response);
}
