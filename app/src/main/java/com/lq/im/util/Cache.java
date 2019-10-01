package com.lq.im.util;

import android.util.LruCache;

public class Cache {
    private static LruCache<String, String> mMemoryCache;

    public static synchronized void init() {
        if (mMemoryCache!=null){
            return;
        }
        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 16;
        mMemoryCache = new LruCache<>(cacheSize);
    }

    public synchronized static void addToMemoryCache(String key, String value) {
        if (mMemoryCache==null){
            return;
        }
        mMemoryCache.put(key, value);
    }

    public synchronized static String getFromMemCache(String key) {
        if (mMemoryCache==null){
            return "";
        }
        return mMemoryCache.get(key);
    }

    public static void clear(){
        if (mMemoryCache==null){
            return;
        }
        mMemoryCache.evictAll();
    }
}
