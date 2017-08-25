package com.xdja.cache.common;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

import okhttp3.OkCacheOperation;

/**
 * Created by ldy on 2017/8/24.
 */

public class OkCache {
    private static final long DEFAULT_CACHE_SIZE = 1024 * 1024 * 50;
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


    public static OkCacheOperation getCacheOperation(){
        assertInitialization();
        if (okCacheOperation==null){
            synchronized (OkCache.class){
                if (okCacheOperation==null){
                    okCacheOperation = new OkCacheOperation(new File(initParams.cacheDir),initParams.maxCacheSize);
                }
            }
        }
        return okCacheOperation;
    }

    public static void assertInitialization() {
        if (initParams == null) {
            throw new IllegalStateException("Do you forget to initialize OkCache?");
        }
    }

    public static class InitParams {
        private String cacheDir;
        private long maxCacheSize;
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
