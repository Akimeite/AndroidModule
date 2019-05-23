package com.djangoogle.framework.manager

import android.graphics.Bitmap
import com.blankj.utilcode.util.LogUtils

/**
 * Bitmap内存缓存管理器
 * Created by Djangoogle on 2018/11/30 09:07 with Android Studio.
 */
class LruBitmapCacheManager private constructor() {

	companion object {
		val INSTANCE: LruBitmapCacheManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
			LruBitmapCacheManager()
		}
	}

	private var mMemoryCache: androidx.collection.LruCache<String, Bitmap>? = null

	/**
	 * 初始化
	 */
	fun initialize() {
		val maxMemonry = (Runtime.getRuntime().maxMemory() / 1024L).toInt()
		if (null == mMemoryCache) {
			mMemoryCache = object : androidx.collection.LruCache<String, Bitmap>(maxMemonry / 8) {
				override fun sizeOf(key: String, bitmap: Bitmap): Int {
					//重写此方法来衡量每张图片的大小，默认返回图片数量。
					return bitmap.rowBytes * bitmap.height / 1024
				}

				override fun entryRemoved(evicted: Boolean, key: String, oldValue: Bitmap, newValue: Bitmap?) {}
			}
		}
	}

	/**
	 * 清空缓存
	 */
	fun clearCache() {
		if (null != mMemoryCache) {
			if (mMemoryCache!!.size() > 0) {
				LogUtils.dTag("clearCache", "mMemoryCache.size = " + mMemoryCache?.size())
				mMemoryCache!!.evictAll()
				LogUtils.dTag("clearCache", "mMemoryCache.size = " + mMemoryCache?.size())
			}
		}
	}

	/**
	 * 添加Bitmap
	 *
	 * @param key
	 * @param bitmap
	 */
	@Synchronized
	fun addBitmapToMemoryCache(key: String?, bitmap: Bitmap?) {
		if (null == key) {
			LogUtils.dTag("addBitmapToMemoryCache", "key为空")
			return
		}
		if (null != mMemoryCache?.get(key)) {
			LogUtils.dTag("addBitmapToMemoryCache", "资源已存在")
			return
		}
		if (null != bitmap) {
			mMemoryCache?.put(key, bitmap)
			LogUtils.dTag("addBitmapToMemoryCache", "资源已添加", "key: $key", "value: ${bitmap.width} * ${bitmap.height}")
		}
	}

	/**
	 * 获取Bitmap
	 *
	 * @param key
	 * @return
	 */
	@Synchronized
	fun getBitmapFromMemoryCache(key: String?): Bitmap? {
		return if (null != key) {
			mMemoryCache?.get(key)
		} else null
	}

	/**
	 * 移除缓存
	 *
	 * @param key
	 */
	@Synchronized
	fun removeImageCache(key: String?) {
		if (null != key && null != mMemoryCache) {
			val bitmap = mMemoryCache?.remove(key)
			bitmap?.recycle()
		}
	}
}
