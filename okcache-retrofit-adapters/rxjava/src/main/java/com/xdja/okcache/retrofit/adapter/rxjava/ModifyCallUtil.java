package com.xdja.okcache.retrofit.adapter.rxjava;

import android.support.annotation.Nullable;

import com.xdja.okcache.constant.HeaderParams;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.HookOkHttpCall;

/**
 * Created by ldy on 2017/9/19.
 */

public class ModifyCallUtil {
    @Nullable
    public static <T> Call<T> getStrategyCall(Call<T> call, int strategy) {
        try {
            HookOkHttpCall cacheHookCall = new HookOkHttpCall(call);
            Request request = cacheHookCall.getRequest();
            if (request != null) {
                Request.Builder requestBuilder = request.newBuilder();
                HeaderParams.setCacheStrategy(requestBuilder, strategy);
                cacheHookCall.hookRequest(requestBuilder.build());
            }
            return call;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
