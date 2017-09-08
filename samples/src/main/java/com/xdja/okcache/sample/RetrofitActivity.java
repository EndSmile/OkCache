package com.xdja.okcache.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.xdja.okcache.retrofit.adapter.rxjava.callintercepter.CacheWhileNetCallInterceptor;
import com.xdja.okcache.sample.bean.Contributor;
import com.xdja.okcache.sample.retrofit.RetrofitGenerator;
import com.xdja.okcache.sample.retrofit.retrofitApi.ApiInterface;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RetrofitActivity extends AppCompatActivity {

    private RetrofitGenerator generator;
    private ApiInterface service;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        tvContent = (TextView) findViewById(R.id.content);
        generator = new RetrofitGenerator(this);
        service = generator.createService(ApiInterface.class);
    }

    public void request(View view) {
        service.contributorsObservable("square","retrofit")
                .setCallInterceptor(new CacheWhileNetCallInterceptor<List<Contributor>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contributor>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(RetrofitActivity.this, "完成", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Contributor> contributors) {
                        tvContent.setText(contributors.toString());
                    }
                });
    }
}
