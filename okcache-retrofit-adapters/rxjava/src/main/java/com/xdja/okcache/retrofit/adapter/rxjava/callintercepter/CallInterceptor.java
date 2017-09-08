package com.xdja.okcache.retrofit.adapter.rxjava.callintercepter;

import com.xdja.okcache.retrofit.adapter.rxjava.CallArbiter;

import java.util.List;

import retrofit2.Call;

/**
 * Created by ldy on 2017/9/4.
 */

public interface CallInterceptor<T> {
    List<Call<T>> getCallList(Call<T> call);
    void execute(CallArbiter<T> callArbiter, boolean isAsync);
}
