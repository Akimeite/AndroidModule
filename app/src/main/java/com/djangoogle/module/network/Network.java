package com.djangoogle.module.network;

/**
 * Created by Djangoogle on 2019/05/18 15:37 with Android Studio.
 */
public class Network {

	private static ZhuangbiApi zhuangbiApi;

	public static ZhuangbiApi getZhuangbiApi() {
//		if (zhuangbiApi == null) {
//			zhuangbiApi = RetrofitManager.Companion.getINSTANCE().getRetrofit("http://www.zhuangbi.info/").create(ZhuangbiApi.class);
//		}
		return zhuangbiApi;
	}
}
