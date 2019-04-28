package com.djangoogle.framework.util;

import android.content.Context;

import com.blankj.utilcode.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义工具
 * Created by Djangoogle on 2018/10/16 13:26 with Android Studio.
 */
public class DjangoogleUtils {

	//拍摄/选取 照片或视频
	public static final int TAKE_MEDIA = 5188;

	/**
	 * 将List按指定大小分割
	 *
	 * @param list 集合
	 * @param len  分割长度
	 * @return 分割后的集合
	 */
	public static List<List<?>> splitList(List<?> list, int len) {
		if (list == null || list.size() == 0 || len < 1) {
			return null;
		}

		List<List<?>> result = new ArrayList<>();

		int size = list.size();
		int count = (size + len - 1) / len;

		for (int i = 0; i < count; i++) {
			List<?> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
			result.add(subList);
		}
		return result;
	}

	/**
	 * 获取/sdcard/Android/data/包名/files/dir文件夹路径
	 *
	 * @param context 上下文
	 * @param dir     文件夹名
	 * @return /sdcard/Android/data/包名/files/dir
	 */
	public static String getExternalFilesDir(Context context, String dir) {
		String path = context.getApplicationContext().getExternalFilesDir(dir).getAbsolutePath();
		FileUtils.createOrExistsDir(path);
		return path;
	}
}
