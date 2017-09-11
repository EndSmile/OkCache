package com.xdja.okcache.retrofit.adapter.rxjava.callintercepter;

import android.support.annotation.Nullable;

import com.xdja.okcache.constant.CacheStrategy;
import com.xdja.okcache.constant.HeaderParams;
import com.xdja.okcache.retrofit.adapter.rxjava.CallArbiter;

import java.util.Arrays;
import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.HookOkHttpCall;
import retrofit2.Response;
import rx.exceptions.Exceptions;

/**
 * Created by ldy on 2017/9/7.
 */

public class CacheWhileNetCallInterceptor<T> implements CallInterceptor<T> {

    private Call<T> cacheCall;
    private Call<T> netCall;

    @Override
    public List<Call<T>> getCallList(Call<T> call) {
        cacheCall = getStrategyCall(call.clone(), CacheStrategy.ONLY_CACHE);
        netCall = getStrategyCall(call.clone(), CacheStrategy.ONLY_NETWORK);
        //// TODO: 2017/9/7 空处理
        return Arrays.asList(cacheCall, netCall);
    }

    @Nullable
    private static <T> Call<T> getStrategyCall(Call<T> call, int strategy) {
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

    @Override
    public void execute(CallArbiter<T> callArbiter, boolean isAsync) {
        Response<T> response = null;
        try {
            response = cacheCall.execute();
        } catch (Throwable ignored) {
        }
        if (response != null && response.isSuccessful()) {
            callArbiter.emitResponse(response, false);
        }

        try {
            netCall.execute();
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            callArbiter.emitError(t);
            return;
        }
        callArbiter.emitResponse(response, true);
    }
}
