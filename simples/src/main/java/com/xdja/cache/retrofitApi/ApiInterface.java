package com.xdja.cache.retrofitApi;

import com.xdja.cache.common.bean.Contributor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> contributors(@Path("owner") String owner
                                        ,@Path("repo") String repo
                                        ,@Header("requestCacheType") int requestCacheType);

}
