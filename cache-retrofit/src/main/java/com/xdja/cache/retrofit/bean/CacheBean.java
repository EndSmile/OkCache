package com.xdja.cache.retrofit.bean;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.cache.retrofit.bean</p>
 * <p>Author:yusenkui</p>
 * <p>Date:2017/8/8</p>
 * <p>Time:19:50</p>
 */


public class CacheBean {
    private boolean isUseCache;
    private int cacheTime;

    public boolean isUseCache() {
        return isUseCache;
    }

    public void setUseCache(boolean useCache) {
        isUseCache = useCache;
    }

    public int getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(int cacheTime) {
        this.cacheTime = cacheTime;
    }
}
