package com.xdja.okcache.okhttp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.retrofitsample.common</p>
 * <p>Author:yusenkui</p>
 * <p>Date:2017/7/31</p>
 * <p>Time:11:01</p>
 */


public class CacheOkHttpUtil {
    /**
     * 是否联网
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
