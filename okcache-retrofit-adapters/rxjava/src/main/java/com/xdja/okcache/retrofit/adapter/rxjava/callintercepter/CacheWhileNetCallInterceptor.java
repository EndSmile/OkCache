package com.xdja.okcache.retrofit.adapter.rxjava.callintercepter;

import android.support.annotation.Nullable;

import com.xdja.okcache.constant.CacheStrategy;
import com.xdja.okcache.constant.HeaderParams;
import com.xdja.okcache.retrofit.adapter.rxjava.CallArbiter;
import com.xdja.okcache.retrofit.adapter.rxjava.ModifyCallUtil;

import java.util.Arrays;
import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.HookOkHttpCall;
import retrofit2.Response;
import rx.exceptions.Exceptions;

/**
 * Created by ldy on 2017/9/7.
 * <p>
 * 先获取缓存数据并返回给上层(如果没有或错误则不返回)，同时请求网络并将结果反馈
 */

public class CacheWhileNetCallInterceptor<T> implements CallInterceptor<T> {

    private Call<T> cacheCall;
    private Call<T> netCall;

    @Override
    public List<Call<T>> getCallList(Call<T> call) {
        cacheCall = ModifyCallUtil.getStrategyCall(call.clone(), CacheStrategy.ONLY_CACHE);
        netCall = ModifyCallUtil.getStrategyCall(call.clone(), CacheStrategy.ONLY_NETWORK);
        //// TODO: 2017/9/7 空处理
        return Arrays.asList(cacheCall, netCall);
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
            response = netCall.execute();
            callArbiter.emitResponse(response, true);
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            callArbiter.emitError(t);
        }
    }
}
