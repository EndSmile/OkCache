package com.xdja.okcache;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;

import com.xdja.okcache.constant.HeaderParams;
import com.xdja.okcache.constant.QueryParams;

import java.io.File;

import okhttp3.OkCacheOperation;
import okhttp3.Request;

/**
 * Created by ldy on 2017/8/24.
 */

public class OkCache {
    private static final long DEFAULT_CACHE_SIZE = 1024 * 1024 * 50;
    private static final long DEFAULT_MAX_STALE = 60 * 60 * 24 * 30;//过期时间为30天
    private static Context context;
    private static InitParams initParams;
    private static String cacheDirPath;
    private static OkCacheOperation okCacheOperation;

    public static void init(Context context, InitParams initParams) {
        if (context == null) {
            throw new IllegalStateException("Context can't be null");
        }
        if (OkCache.context != null) {
            throw new IllegalStateException("OkCache is already initialized, do not initialize again");
        }
        OkCache.context = context.getApplicationContext();
        if (initParams == null) {
            initParams = new InitParams();
        }
        if (initParams.maxCacheSize <= 0) {
            initParams.maxCacheSize = DEFAULT_CACHE_SIZE;
        }
        OkCache.initParams = initParams;
    }

    public static String getCacheDirPath() {
        if (cacheDirPath != null) {
            return cacheDirPath;
        }
        cacheDirPath = initParams.cacheDir;
        if (cacheDirPath != null) {
            File file = new File(cacheDirPath);
            if (!file.exists()) {
                if (file.mkdirs()) {
                    return cacheDirPath;
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (dir != null) {
                File xelogFile = new File(dir, "okcache");
                if (xelogFile.exists()) {
                    cacheDirPath = xelogFile.getPath();
                    return cacheDirPath;
                } else {
                    if (xelogFile.mkdirs()) {
                        cacheDirPath = xelogFile.getPath();
                        return cacheDirPath;
                    }
                }
            }
        }
        cacheDirPath = new File(Environment.getExternalStorageDirectory(), "okcache").getPath();
        return cacheDirPath;
    }


    public static OkCacheOperation getCacheOperation() {
        assertInitialization();
        if (okCacheOperation == null) {
            synchronized (OkCache.class) {
                if (okCacheOperation == null) {
                    okCacheOperation = new OkCacheOperation(new File(getCacheDirPath()), initParams.maxCacheSize);
                }
            }
        }
        return okCacheOperation;
    }

    public static long getMaxStale() {
        assertInitialization();
        if (initParams.maxStale <= 0) {
            return DEFAULT_MAX_STALE;
        } else {
            return initParams.maxStale;
        }
    }

    public static void putCacheTime(String key) {
        assertInitialization();
        SharedPreferences preferences = context.getSharedPreferences("OkCache", Context.MODE_PRIVATE);
        preferences.edit().putLong(key, System.currentTimeMillis()).apply();
    }

    public static long getCacheTime(String key) {
        assertInitialization();
        SharedPreferences preferences = context.getSharedPreferences("OkCache", Context.MODE_PRIVATE);
        return preferences.getLong(key, 0);
    }

    public static Request stripSelfParams(Request request) {
        Request.Builder builder = request.newBuilder();
        builder.url(QueryParams.stripUrlQuery(request.url()));
        return HeaderParams.stripHeader(builder).build();
    }

    public static boolean isEnableCache(Request request) {
        assertInitialization();
        String enableCacheStr = request.header(QueryParams.ENABLE_CACHE);
        if ("true".equals(enableCacheStr)) {
            return true;
        }
        if ("false".equals(enableCacheStr)) {
            return false;
        }
        if (request.method().equals("GET")) {
            return initParams.enableGetCache;
        } else if (request.method().equals("POST")) {
            return initParams.enablePostCache;
        } else {
            return false;
        }
    }

    private static void assertInitialization() {
        if (initParams == null) {
            throw new IllegalStateException("Do you forget to initialize OkCache?");
        }
    }

    public static class InitParams {
        private String cacheDir;
        private long maxCacheSize;
        private long maxStale;
        private boolean enableGetCache;
        private boolean enablePostCache;

        public InitParams setCacheDir(String cacheDir) {
            this.cacheDir = cacheDir;
            return this;
        }

        public InitParams setMaxCacheSize(long maxCacheSize) {
            this.maxCacheSize = maxCacheSize;
            return this;
        }

        public InitParams enableGetCache() {
            enableGetCache = true;
            return this;
        }

        public InitParams enablePostCache() {
            enablePostCache = true;
            return this;
        }
    }

}
