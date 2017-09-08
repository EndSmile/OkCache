package com.xdja.okcache.retrofit.adapter.rxjava.callintercepter;

import com.xdja.okcache.retrofit.adapter.rxjava.CallArbiter;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import rx.exceptions.Exceptions;

/**
 * Created by ldy on 2017/9/4.
 */

public class DefaultCallInterceptor<T> implements CallInterceptor<T> {

    private Call<T> call;

    @Override
    public List<Call<T>> getCallList(Call<T> call) {
        this.call = call;
        return Collections.singletonList(this.call);
    }

    @Override
    public void execute(CallArbiter<T> arbiter, boolean isAsync) {
        Response<T> response;
        try {
            response = call.execute();
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            arbiter.emitError(t);
            return;
        }
        arbiter.emitResponse(response);
    }
}
