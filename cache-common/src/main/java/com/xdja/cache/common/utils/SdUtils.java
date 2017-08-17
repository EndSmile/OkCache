package com.xdja.cache.common.utils;

import android.content.Context;
import android.os.Environment;


/**
 */
public class SdUtils {

    public static String getDiskCacheDir(Context context){
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
}
