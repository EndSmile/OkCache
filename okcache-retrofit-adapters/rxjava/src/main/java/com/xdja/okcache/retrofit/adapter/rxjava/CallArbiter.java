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

import java.util.concurrent.atomic.AtomicInteger;
import retrofit2.Call;
import retrofit2.Response;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.exceptions.OnCompletedFailedException;
import rx.exceptions.OnErrorFailedException;
import rx.exceptions.OnErrorNotImplementedException;
import rx.plugins.RxJavaPlugins;

public final class CallArbiter<T> implements Subscription {

  private final Call<T> call;
  private CallInterceptorContainer container;
  private final Subscriber<? super Response<T>> subscriber;

  CallArbiter(Call<T> call, CallInterceptorContainer container, Subscriber<? super Response<T>> subscriber) {
    this.call = call;
    this.container = container;
    this.subscriber = subscriber;
  }

  @Override public void unsubscribe() {
    CallInterceptor<T> callInterceptor = container.getCallInterceptor();
    if (callInterceptor!=null){
      for (Call tCall : callInterceptor.getCallList(call)) {
        tCall.cancel();
      }
    }
  }

  @Override public boolean isUnsubscribed() {
    CallInterceptor<T> callInterceptor = container.getCallInterceptor();
    if (callInterceptor!=null){
      for (Call<T> tCall : callInterceptor.getCallList(call)) {
        if (!tCall.isCanceled()){
          return false;
        }
      }
    }
    return true;
  }

  public void emitResponse(Response<T> response,boolean isComplete) {
    deliverResponse(response, isComplete);
  }

  public void emitResponse(Response<T> response) {
    emitResponse(response,true);
  }

  private void deliverResponse(Response<T> response,boolean isComplete) {
    try {
      if (!isUnsubscribed()) {
        subscriber.onNext(response);
      }
    } catch (OnCompletedFailedException
        | OnErrorFailedException
        | OnErrorNotImplementedException e) {
      RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
      return;
    } catch (Throwable t) {
      Exceptions.throwIfFatal(t);
      try {
        subscriber.onError(t);
      } catch (OnCompletedFailedException
          | OnErrorFailedException
          | OnErrorNotImplementedException e) {
        RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
      } catch (Throwable inner) {
        Exceptions.throwIfFatal(inner);
        CompositeException composite = new CompositeException(t, inner);
        RxJavaPlugins.getInstance().getErrorHandler().handleError(composite);
      }
      return;
    }
    if (isComplete){
      emitComplete();
    }
  }

  public void emitComplete() {
    try {
      if (!isUnsubscribed()) {
        subscriber.onCompleted();
      }
    } catch (OnCompletedFailedException
        | OnErrorFailedException
        | OnErrorNotImplementedException e) {
      RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
    } catch (Throwable t) {
      Exceptions.throwIfFatal(t);
      RxJavaPlugins.getInstance().getErrorHandler().handleError(t);
    }
  }

  public void emitError(Throwable t) {

    if (!isUnsubscribed()) {
      try {
        subscriber.onError(t);
      } catch (OnCompletedFailedException
          | OnErrorFailedException
          | OnErrorNotImplementedException e) {
        RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
      } catch (Throwable inner) {
        Exceptions.throwIfFatal(inner);
        CompositeException composite = new CompositeException(t, inner);
        RxJavaPlugins.getInstance().getErrorHandler().handleError(composite);
      }
    }
  }
}
