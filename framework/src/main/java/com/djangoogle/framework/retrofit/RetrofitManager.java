package com.djangoogle.framework.retrofit;

import android.content.Context;

import com.djangoogle.framework.constants.DjangoConst;
import com.tencent.mmkv.MMKV;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Djangoogle on 2019/04/28 11:14 with Android Studio.
 */
public class RetrofitManager {

	private static volatile RetrofitManager instance = null;

	public static RetrofitManager getInstance() {
		if (null == instance) {
			synchronized (RetrofitManager.class) {
				if (null == instance) {
					instance = new RetrofitManager();
				}
			}
		}
		return instance;
	}

	//网络框架超时时间
	private static final long OK_HTTP_TIME_OUT = 10L;

	/**
	 * 初始化
	 *
	 * @param context   上下文
	 * @param serverUrl 服务器地址
	 */
	public void initialize(Context context, String serverUrl) {
		//初始化OkHttpClient
		initOkHttpClient(context);
		//初始化Retrofit
		initRetrofit(serverUrl);
	}

	private OkHttpClient mOkHttpClient = null;
	private Retrofit mRetrofit = null;

	/**
	 * 初始化OkHttpClient
	 *
	 * @param context 上下文
	 */
	private void initOkHttpClient(Context context) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		//配置log
		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor("Djangoogle");
		//log打印级别，决定了log显示的详细程度
		httpLoggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
		//log颜色级别，决定了log在控制台显示的颜色
		httpLoggingInterceptor.setColorLevel(Level.INFO);
		builder.addInterceptor(httpLoggingInterceptor);
		//全局的读取超时时间
		builder.readTimeout(OK_HTTP_TIME_OUT, TimeUnit.SECONDS);
		//全局的写入超时时间
		builder.writeTimeout(OK_HTTP_TIME_OUT, TimeUnit.SECONDS);
		//全局的连接超时时间
		builder.connectTimeout(OK_HTTP_TIME_OUT, TimeUnit.SECONDS);
		builder.retryOnConnectionFailure(true);
		//使用数据库保持cookie，如果cookie不过期，则一直有效
//		builder.cookieJar(new CookieJarImpl(new DBCookieStore(context)));
		mOkHttpClient = builder.build();
	}

	/**
	 * 初始化Retrofit
	 *
	 * @param serverUrl 服务器地址
	 */
	private void initRetrofit(String serverUrl) {
		MMKV.defaultMMKV().encode(DjangoConst.SERVER_URL, serverUrl);
		mRetrofit = new Retrofit.Builder()
				.client(mOkHttpClient)
				.baseUrl(serverUrl)
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build();
	}

	public Retrofit getRetrofit() {
		return mRetrofit;
	}
}
