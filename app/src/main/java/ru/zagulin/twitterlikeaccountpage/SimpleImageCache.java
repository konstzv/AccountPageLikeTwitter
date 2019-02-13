package ru.zagulin.twitterlikeaccountpage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SimpleImageCache {

    private final SharedPreferences mSharedPreferences;

    SimpleImageCache(Context context) {
        mSharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
    }

    public void saveToCache(String key, String path) {
        mSharedPreferences.edit().putString(key, path).commit();
    }

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

    public void removeFromCache(String key) {
        mSharedPreferences.edit().remove(key).commit();
    }
}
