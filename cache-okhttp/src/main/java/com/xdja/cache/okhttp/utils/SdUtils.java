package com.xdja.cache.okhttp.utils;

import android.os.Environment;

import com.xdja.cache.okhttp.AppContext;


/**
 */
public class SdUtils {

    public static String getDiskCacheDir(){
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = AppContext.getContext().getExternalCacheDir().getPath();
        } else {
            cachePath = AppContext.getContext().getCacheDir().getPath();
        }
        return cachePath;
    }
}
