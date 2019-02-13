package ru.zagulin.twitterlikeaccountpage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * Simple cache powered by shared preferences
 */
public class SimpleImageCache {

    private final SharedPreferences mSharedPreferences;

    /**
     *
     * @param context - to get shared preferences
     */
    SimpleImageCache(Context context) {
        mSharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
    }

    /**
     * Save image path to cache
     * @param key - unique key of cached image (user uid using for profile images)
     * @param path - absolute path to image
     */
    public void saveToCache(String key, String path) {
        mSharedPreferences.edit().putString(key, path).commit();
    }

    /**
     * Get bitmap image from cache
     * Remove path from cache if no bitmap in cached path
     * @param key - unique key of cached image (user uid using for profile images)
     * @return - bitmap from cache, null - if nothing in cache or no image in cached path
     */
    public Bitmap getBitmapFromCache(String key) {
        String path = mSharedPreferences.getString(key, null);
        if (path == null) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null) {
            removeFromCache(key);
        }
        return bitmap;
    }

    /**
     * Remove record from cache by key
     * @param key - unique key of cached image (user uid using for profile images)
     */
    public void removeFromCache(String key) {
        mSharedPreferences.edit().remove(key).commit();
    }
}
