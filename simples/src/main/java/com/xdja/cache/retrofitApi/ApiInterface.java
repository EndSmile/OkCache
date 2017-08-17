package com.xdja.cache.retrofitApi;

import com.xdja.cache.common.bean.Contributor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ApiInterface {

    @Headers("requestCacheType: 1")
    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> contributors(@Path("owner") String owner,
                                         @Path("repo") String repo);

}
