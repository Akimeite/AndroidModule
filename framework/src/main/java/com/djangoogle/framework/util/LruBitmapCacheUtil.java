package com.djangoogle.framework.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;

import com.blankj.utilcode.util.LogUtils;

/**
 * Bitmap内存缓存管理器
 * Created by Djangoogle on 2018/11/30 09:07 with Android Studio.
 */
public class LruBitmapCacheUtil {

	private static volatile LruBitmapCacheUtil instance = null;

	public static LruBitmapCacheUtil getInstance() {
		if (null == instance) {
			synchronized (LruBitmapCacheUtil.class) {
				if (null == instance) {
					instance = new LruBitmapCacheUtil();
				}
			}
		}
		return instance;
	}

	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * 初始化
	 */
	public void initialize() {
		int MAXMEMONRY = (int) (Runtime.getRuntime().maxMemory() / 1024);
		if (null == mMemoryCache) {
			mMemoryCache = new LruCache<String, Bitmap>(MAXMEMONRY / 8) {
				@Override
				protected int sizeOf(@NonNull String key, @NonNull Bitmap bitmap) {
					//重写此方法来衡量每张图片的大小，默认返回图片数量。
					return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
				}

				@Override
				protected void entryRemoved(boolean evicted, @NonNull String key, @NonNull Bitmap oldValue, Bitmap newValue) {}
			};
		}
	}

	/**
	 * 清空缓存
	 */
	public void clearCache() {
		if (null != mMemoryCache) {
			if (mMemoryCache.size() > 0) {
				LogUtils.d("mMemoryCache.size = " + mMemoryCache.size());
				mMemoryCache.evictAll();
				LogUtils.d("mMemoryCache.size = " + mMemoryCache.size());
			}
		}
	}

	/**
	 * 添加Bitmap
	 *
	 * @param key
	 * @param bitmap
	 */
	public synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (null == mMemoryCache.get(key)) {
			if (null != key && null != bitmap) {
				mMemoryCache.put(key, bitmap);
			}
		} else {
			LogUtils.w("the res is aready exits");
		}
	}

	/**
	 * 获取Bitmap
	 *
	 * @param key
	 * @return
	 */
	public synchronized Bitmap getBitmapFromMemoryCache(String key) {
		Bitmap bitmap = mMemoryCache.get(key);
		if (null != key) {
			return bitmap;
		}
		return null;
	}

	/**
	 * 移除缓存
	 *
	 * @param key
	 */
	public synchronized void removeImageCache(String key) {
		if (null != key) {
			if (null != mMemoryCache) {
				Bitmap bitmap = mMemoryCache.remove(key);
				if (null != bitmap) {
					bitmap.recycle();
				}
			}
		}
	}
}
