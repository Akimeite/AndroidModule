package com.djangoogle.framework.util

import java.util.*

/**
 * 自定义工具
 * Created by Djangoogle on 2018/10/16 13:26 with Android Studio.
 */
object DjangoUtils {

	/**
	 * 将List按指定大小分割
	 *
	 * @param list 集合
	 * @param len  分割长度
	 * @return 分割后的集合
	 */
	fun splitList(list: List<*>?, len: Int): List<List<*>>? {
		if (list == null || list.isEmpty() || len < 1) {
			return null
		}

		val result = ArrayList<List<*>>()

		val size = list.size
		val count = (size + len - 1) / len

		for (i in 0 until count) {
			val subList = list.subList(i * len, if ((i + 1) * len > size) size else len * (i + 1))
			result.add(subList)
		}
		return result
	}
}
