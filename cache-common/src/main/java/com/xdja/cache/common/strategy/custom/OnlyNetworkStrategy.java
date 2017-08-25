package com.xdja.cache.common.strategy.custom;


import com.xdja.cache.common.OkCache;
import com.xdja.cache.common.strategy.IRequestStrategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkCacheOperation;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.cache.CacheRequest;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static okhttp3.internal.Util.discard;

/**
 * 仅仅请求网络策略
 */
public class OnlyNetworkStrategy implements IRequestStrategy {

    @Override
    public Response request(Interceptor.Chain chain) throws IOException {
        Request request = OkCache.stripSelfParams(chain.request());

        Response networkResponse = chain.proceed(request);
        if (networkResponse.isSuccessful()){
            OkCacheOperation cacheOperation = OkCache.getCacheOperation();

            Response cacheResponse = cacheOperation.get(request);
            Response response = networkResponse.newBuilder()
                    .cacheResponse(stripBody(cacheResponse))
                    .networkResponse(stripBody(networkResponse))
                    .build();

            CacheRequest cacheRequest = cacheOperation.put(response);
            OkCache.putCacheTime(OkCacheOperation.getKey(request));
            return cacheWritingResponse(cacheRequest,response);
        }

        return networkResponse;
    }

    private static Response stripBody(Response response) {
        return response != null && response.body() != null
                ? response.newBuilder().body(null).build()
                : response;
    }

    /**
     * Returns a new source that writes bytes to {@code cacheRequest} as they are read by the source
     * consumer. This is careful to discard bytes left over when the stream is closed; otherwise we
     * may never exhaust the source stream and therefore not complete the cached response.
     */
    private Response cacheWritingResponse(final CacheRequest cacheRequest, Response response)
            throws IOException {
        // Some apps return a null body; for compatibility we treat that like a null cache request.
        if (cacheRequest == null) return response;
        Sink cacheBodyUnbuffered = cacheRequest.body();
        if (cacheBodyUnbuffered == null) return response;

        final BufferedSource source = response.body().source();
        final BufferedSink cacheBody = Okio.buffer(cacheBodyUnbuffered);

        Source cacheWritingSource = new Source() {
            boolean cacheRequestClosed;

            @Override public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead;
                try {
                    bytesRead = source.read(sink, byteCount);
                } catch (IOException e) {
                    if (!cacheRequestClosed) {
                        cacheRequestClosed = true;
                        cacheRequest.abort(); // Failed to write a complete cache response.
                    }
                    throw e;
                }

                if (bytesRead == -1) {
                    if (!cacheRequestClosed) {
                        cacheRequestClosed = true;
                        cacheBody.close(); // The cache response is complete!
                    }
                    return -1;
                }

                sink.copyTo(cacheBody.buffer(), sink.size() - bytesRead, bytesRead);
                cacheBody.emitCompleteSegments();
                return bytesRead;
            }

            @Override public Timeout timeout() {
                return source.timeout();
            }

            @Override public void close() throws IOException {
                if (!cacheRequestClosed
                        && !discard(this, HttpCodec.DISCARD_STREAM_TIMEOUT_MILLIS, MILLISECONDS)) {
                    cacheRequestClosed = true;
                    cacheRequest.abort();
                }
                source.close();
            }
        };

        return response.newBuilder()
                .body(new RealResponseBody(response.headers(), Okio.buffer(cacheWritingSource)))
                .build();
    }
}
