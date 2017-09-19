package com.xdja.okcache;

import android.support.annotation.Nullable;

import java.io.EOFException;
import java.nio.charset.Charset;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.ByteString;

/**
 * Created by ldy on 2017/9/18.
 */

public class KeyUtil {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * 根据request获取一个key值，如果失败则返回null
     */
    @Nullable
    public static String getKey(Request request) {
        if (request.method().equals("POST")) {
            try {
                RequestBody requestBody = request.body();
                if (requestBody == null) {
                    return null;
                }

                String requestBodyStr = getRequestBodyStr(requestBody);
                if (requestBodyStr != null) {
                    return key(request.url() + "_" + requestBodyStr);
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return key(request.url());
    }

    @Nullable
    public static String getRequestBodyStr(RequestBody requestBody) {
        return getRequestBodyStr(requestBody, true);
    }

    @Nullable
    public static String getRequestBodyStr(RequestBody requestBody, boolean isPlainVerify) {
        if (requestBody == null) {
            return null;
        }

        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (!isPlainVerify || isPlaintext(buffer)) {
                return buffer.readString(charset);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static String key(HttpUrl url) {
        return key(url.toString());
    }

    public static String key(String string) {
        return ByteString.encodeUtf8(string).md5().hex();
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    public static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
