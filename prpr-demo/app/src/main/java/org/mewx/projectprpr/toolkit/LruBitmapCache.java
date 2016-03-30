package org.mewx.projectprpr.toolkit;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * For bitmap image cache.
 * Created by MewX on 3/31/2016.
 */
public class LruBitmapCache extends LruCache<String,Bitmap> implements ImageCache {
    public static int getDefaultLruCacheSize(){
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        return maxMemory / 8; // cache size
    }

    public LruBitmapCache(){
        this(getDefaultLruCacheSize());
    }

    public LruBitmapCache(int sizeInKB){
        super(sizeInKB);
    }

    @Override
    public int sizeOf(String key,Bitmap value){
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url){
        return get(url);
    }

    @Override
    public void putBitmap(String url,Bitmap bitmap){
        put(url,bitmap);
    }
}
