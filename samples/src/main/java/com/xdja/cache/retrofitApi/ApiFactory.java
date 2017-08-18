package com.xdja.cache.retrofitApi;


import com.xdja.cache.retrofit.generator.RetrofitCacheGenerator;

public class ApiFactory {

    private RetrofitCacheGenerator retrofitCacheGenerator;

    public ApiFactory() {
        this.retrofitCacheGenerator = new RetrofitCacheGenerator();
    }


    public ApiInterface getCacheApi() {
        return this.retrofitCacheGenerator.createService(ApiInterface.class);
    }
}
