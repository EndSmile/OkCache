/*
 * Copyright (C) 2016 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xdja.okcache.retrofit.adapter.rxjava;

import com.xdja.okcache.retrofit.adapter.rxjava.callintercepter.CallInterceptor;
import com.xdja.okcache.retrofit.adapter.rxjava.callintercepter.CallInterceptorContainer;
import com.xdja.okcache.retrofit.adapter.rxjava.callintercepter.DefaultCallInterceptor;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

final class CallExecuteOnSubscribe<T> implements OnSubscribe<Response<T>> {
  private final Call<T> originalCall;
  private CallInterceptorContainer container;

  CallExecuteOnSubscribe(Call<T> originalCall, CallInterceptorContainer container) {
    this.originalCall = originalCall;
    this.container = container;
  }

  @Override public void call(Subscriber<? super Response<T>> subscriber) {
    // Since Call is a one-shot type, clone it for each new subscriber.
    Call<T> call = originalCall.clone();
    CallArbiter<T> arbiter = new CallArbiter<>(call,container, subscriber);
    subscriber.add(arbiter);
//    subscriber.setProducer(arbiter);

    CallInterceptor<T> interceptor = container.getCallInterceptor();
    if (interceptor==null){
      interceptor = new DefaultCallInterceptor<T>();
    }
    interceptor.execute(arbiter,false);
//    Response<T> response;
//    try {
//      response = call.execute();
//    } catch (Throwable t) {
//      Exceptions.throwIfFatal(t);
//      arbiter.emitError(t);
//      return;
//    }
//    arbiter.emitResponse(response);
  }
}
