package com.xdja.cache.common;

import android.support.annotation.Nullable;

import com.xdja.cache.common.strategy.custom.OnlyCacheStrategy;
import com.xdja.cache.common.strategy.custom.OnlyNetworkStrategy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CacheTest {
    private String bodyContent = "{'name':'ldy'}";
    private String url = "http://www.test.com/";
    private Request request = new Request.Builder().url(url).method("GET",null).build();
    private Interceptor.Chain chain = new Interceptor.Chain() {
        @Override
        public Request request() {
            return request;
        }

        @Override
        public Response proceed(Request request) throws IOException {
            ResponseBody body = ResponseBody.create(MediaType.parse("text"), bodyContent);

            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .body(body)
                    .message("ok")
//                        .sentRequestAtMillis(-1L)
//                        .receivedResponseAtMillis(System.currentTimeMillis())
                    .build();
        }

        @Nullable
        @Override
        public Connection connection() {
            return null;
        }
    };;

    @Before
    public void init(){
        OkCache.init(RuntimeEnvironment.application,null);
    }

    @Test
    public void testGet() throws Exception {
        request = new Request.Builder().url(url).method("GET",null).build();

        OnlyNetworkStrategy onlyNetworkStrategy = new OnlyNetworkStrategy();
        Response response = onlyNetworkStrategy.request(chain);

        assertEquals(bodyContent,response.body().string());

        OnlyCacheStrategy onlyCacheStrategy = new OnlyCacheStrategy();
        Response onlyCacheResponse = onlyCacheStrategy.request(chain);
        assertEquals(bodyContent,onlyCacheResponse.body().string());

    }

    @Test
    public void testPost() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text"), "{'method':'testPost'}");

        request = new Request.Builder().url(url).method("POST",requestBody).build();

        OnlyNetworkStrategy onlyNetworkStrategy = new OnlyNetworkStrategy();
        Response response = onlyNetworkStrategy.request(chain);
        assertEquals(bodyContent,response.body().string());

        OnlyCacheStrategy onlyCacheStrategy = new OnlyCacheStrategy();
        Response onlyCacheResponse = onlyCacheStrategy.request(chain);

        assertEqualsResponse(response,onlyCacheResponse);
        assertEquals(bodyContent,onlyCacheResponse.body().string());
    }

    private void assertEqualsResponse(Response netResponse,Response cacheResponse) throws Exception{
        assertEquals(netResponse.message(),cacheResponse.message());
        assertEquals(netResponse.request().method(),netResponse.request().method());
        assertEquals(netResponse.request().body().contentLength(),cacheResponse.request().body().contentLength());
    }
}