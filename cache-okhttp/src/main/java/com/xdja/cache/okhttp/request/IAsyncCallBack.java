package com.xdja.cache.okhttp.request;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Author:yusenkui</p>
 * <p>Date:2017/8/14</p>
 * <p>Time:10:04</p>
 */


public interface IAsyncCallBack {
    /**
     * 请求失败
     *
     * @param call call
     * @param e    异常
     */
    void onFailure(Call call, IOException e);

    /**
     * 请求成功
     *
     * @param response 响应
     */
    void onResponse(Response response);
}
