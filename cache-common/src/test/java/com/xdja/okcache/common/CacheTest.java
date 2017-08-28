package com.xdja.okcache.common;

import android.support.annotation.Nullable;

import com.xdja.okcache.common.strategy.custom.OnlyCacheStrategy;
import com.xdja.okcache.common.strategy.custom.OnlyNetworkStrategy;

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
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.io.FileSystem;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

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
    private Request request = new Request.Builder().url(url).method("GET", null).build();
    private String message = "ok";
    private Interceptor.Chain chain = new Interceptor.Chain() {
        @Override
        public Request request() {
            return request;
        }

        @Override
        public Response proceed(Request request) throws IOException {

            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .body(ResponseBody.create(MediaType.parse("text"), bodyContent))
                    .message(message)
//                        .sentRequestAtMillis(-1L)
//                        .receivedResponseAtMillis(System.currentTimeMillis())
                    .build();
        }

        @Nullable
        @Override
        public Connection connection() {
            return null;
        }
    };
    ;

    @Before
    public void init() {
        OkCache.init(RuntimeEnvironment.application, null);
    }

    @Test
    public void testGet() throws Exception {
        request = new Request.Builder().url(url).method("GET", null).build();

        OnlyNetworkStrategy onlyNetworkStrategy = new OnlyNetworkStrategy();
        Response response = onlyNetworkStrategy.request(chain);

        assertEquals(bodyContent, response.body().string());

        OnlyCacheStrategy onlyCacheStrategy = new OnlyCacheStrategy();
        Response onlyCacheResponse = onlyCacheStrategy.request(chain);
        assertEquals(bodyContent, onlyCacheResponse.body().string());

    }

    @Test
    public void testPost() throws Exception {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text"), "{'method':'testPost'}");

        request = new Request.Builder().url(url).method("POST", requestBody).build();

        OnlyNetworkStrategy onlyNetworkStrategy = new OnlyNetworkStrategy();
        Response netResponse = onlyNetworkStrategy.request(chain);
        assertEquals(bodyContent, netResponse.body().string());
        assertEquals(bodyContent, new OnlyCacheStrategy().request(chain).body().string());

//        OkCache.getCacheOperation().remove(chain.request());
//        bodyContent = "{'name':'ldy2'}";
//        message = "ok2";
//        netResponse = new OnlyNetworkStrategy().request(chain);
//
//        Response onlyCacheResponse = new OnlyCacheStrategy().request(chain);
//
//        assertEquals(bodyContent, onlyCacheResponse.body().string());
//        assertEqualsResponse(netResponse, onlyCacheResponse);
    }

    @Test
    public void testLruCache() throws IOException {
        DiskLruCache cache = DiskLruCache.create(FileSystem.SYSTEM, RuntimeEnvironment.application.getCacheDir(), 1, 1, 1024 * 1024 * 50);

        DiskLruCache.Editor editor = cache.edit("ldy");
        BufferedSink sink = Okio.buffer(editor.newSink(0));
        sink.writeUtf8("1111").writeByte('\n');
        sink.close();
        editor.commit();

        DiskLruCache.Snapshot snapshot = cache.get("ldy");
        Source in = snapshot.getSource(0);
        BufferedSource source = Okio.buffer(in);
        String value = source.readUtf8LineStrict();

        assertEquals("1111", value);

        editor = cache.edit("ldy");
        sink = Okio.buffer(editor.newSink(0));
        sink.writeUtf8("1112").writeByte('\n');
        sink.close();
        editor.commit();

        snapshot = cache.get("ldy");
        in = snapshot.getSource(0);
        source = Okio.buffer(in);
        value = source.readUtf8LineStrict();

        assertEquals("1112", value);
    }

    private void assertEqualsResponse(Response netResponse, Response cacheResponse) throws Exception {
        assertEquals(netResponse.message(), cacheResponse.message());
        assertEquals(netResponse.request().method(), netResponse.request().method());
        assertEquals(netResponse.request().body().contentLength(), cacheResponse.request().body().contentLength());
    }
}