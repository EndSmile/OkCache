package com.xdja.okcache.retrofit.adapter.rxjava.callintercepter;

import com.xdja.okcache.constant.CacheStrategy;
import com.xdja.okcache.retrofit.adapter.rxjava.CallArbiter;
import com.xdja.okcache.retrofit.adapter.rxjava.ModifyCallUtil;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import rx.exceptions.Exceptions;

/**
 * Created by ldy on 2017/9/7.
 *
 * 先获取缓存数据并返回给上层(如果没有或错误则不返回)，同时请求网络如果上次成功则不返回，否则返回
 */

public class CacheWhileHideNetCallInterceptor<T> implements CallInterceptor<T> {

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
            //如果缓存访问成功则发送缓存并结束事件，然后重新执行一次网络请求
            callArbiter.emitResponse(response, true);
            try {
                netCall.execute();
            } catch (Throwable ignore) {
            }
        }else {
            //如果缓存访问失败则按照正常的流程重新访问
            try {
                response = netCall.execute();
                callArbiter.emitResponse(response, true);
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                callArbiter.emitError(t);
            }
        }

    }
}
