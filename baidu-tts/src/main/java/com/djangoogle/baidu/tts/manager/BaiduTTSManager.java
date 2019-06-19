package com.djangoogle.baidu.tts.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.djangoogle.baidu.tts.config.BaiduInitConfig;
import com.djangoogle.baidu.tts.constants.Constants;
import com.djangoogle.baidu.tts.impl.MessageListener;
import com.djangoogle.baidu.tts.util.AutoCheck;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度TTS管理器
 * Created by Djangoogle on 2019/03/20 15:11 with Android Studio.
 * Copyright (C) 2019 Agesun, Ltd. All Rights Reserved.
 * 注意：本内容仅限于安徽时旭智能科技有限公司内部传阅，禁止外泄以及用于其他的商业目的。
 */
public class BaiduTTSManager {

	// ================选择TtsMode.ONLINE  不需要设置以下参数; 选择TtsMode.MIX 需要设置下面2个离线资源文件的路径
	private static final String TEMP_DIR = "/sdcard/baiduTTS"; // 重要！请手动将assets目录下的3个dat 文件复制到该目录
	// 请确保该PATH下有这个文件
	private static final String TEXT_FILENAME = TEMP_DIR + "/" + "bd_etts_text.dat";
	// 请确保该PATH下有这个文件 ，m15是离线男声
	private static final String MODEL_FILENAME = TEMP_DIR + "/" + "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
	private static volatile BaiduTTSManager instance = null;
	// TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
	private TtsMode ttsMode = TtsMode.MIX;
	private SpeechSynthesizer mSpeechSynthesizer;

	// ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

	public static BaiduTTSManager getInstance() {
		if (null == instance) {
			synchronized (BaiduTTSManager.class) {
				if (null == instance) {
					instance = new BaiduTTSManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 注意此处为了说明流程，故意在UI线程中调用。
	 * 实际集成中，该方法一定在新线程中调用，并且该线程不能结束。具体可以参考NonBlockSyntherizer的写法
	 */
	@SuppressLint("HandlerLeak")
	public void initialize(Context context) {
		LoggerProxy.printable(true); // 日志打印在logcat中
		boolean isMix = ttsMode.equals(TtsMode.MIX);
		boolean isSuccess;
		if (isMix) {
			//检查2个离线资源是否可读
			isSuccess = checkOfflineResources();
			if (!isSuccess) {
				ResourceUtils.copyFileFromAssets("bd_etts_text.dat", TEXT_FILENAME);
				ResourceUtils.copyFileFromAssets("bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat", MODEL_FILENAME);
			} else {
				LogUtils.i("离线资源存在并且可读, 目录：" + TEMP_DIR);
			}
		}
		// 日志更新在UI中，可以换成MessageListener，在logcat中查看日志
		SpeechSynthesizerListener listener = new MessageListener();

		// 1. 获取实例
		mSpeechSynthesizer = SpeechSynthesizer.getInstance();
		mSpeechSynthesizer.setContext(context);

		// 2. 设置listener
		mSpeechSynthesizer.setSpeechSynthesizerListener(listener);

		// 3. 设置appId，appKey.secretKey
		int result = mSpeechSynthesizer.setAppId(Constants.BAIDU_TTS_APP_ID);
		checkResult(result, "setAppId");
		result = mSpeechSynthesizer.setApiKey(Constants.BAIDU_TTS_APP_KEY, Constants.BAIDU_TTS_SECRET_KEY);
		checkResult(result, "setApiKey");

		// 4. 支持离线的话，需要设置离线模型
		if (isMix) {
			// 检查离线授权文件是否下载成功，离线授权文件联网时SDK自动下载管理，有效期3年，3年后的最后一个月自动更新。
			isSuccess = checkAuth();
			if (!isSuccess) {
				return;
			}
			// 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
			// 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
		}

		// 5. 以下setParam 参数选填。不填写则默认值生效
//		// 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
//		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
//		// 设置合成的音量，0-9 ，默认 5
//		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
//		// 设置合成的语速，0-9 ，默认 5
//		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
//		// 设置合成的语调，0-9 ，默认 5
//		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");

		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5");
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI);
		// 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
		// MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
		// MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
		// MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
		// MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

		mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);

		// x. 额外 ： 自动so文件是否复制正确及上面设置的参数
		Map<String, String> params = new HashMap<>();
		// 复制下上面的 mSpeechSynthesizer.setParam参数
		// 上线时请删除AutoCheck的调用
		if (isMix) {
			params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
			params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
		}
		BaiduInitConfig initConfig = new BaiduInitConfig(Constants.BAIDU_TTS_APP_ID, Constants.BAIDU_TTS_APP_KEY,
				Constants.BAIDU_TTS_SECRET_KEY, ttsMode, params, listener);
		AutoCheck.getInstance(context).check(initConfig, new Handler() {
			@Override
			/**
			 * 开新线程检查，成功后回调
			 */
			public void handleMessage(Message msg) {
				if (msg.what == 100) {
					AutoCheck autoCheck = (AutoCheck) msg.obj;
					synchronized (autoCheck) {
						String message = autoCheck.obtainDebugMessage();
						LogUtils.i(message); // 可以用下面一行替代，在logcat中查看代码
					}
				}
			}
		});

		// 6. 初始化
		result = mSpeechSynthesizer.initTts(ttsMode);
		checkResult(result, "initTts");
	}

	/**
	 * 检查appId ak sk 是否填写正确，另外检查官网应用内设置的包名是否与运行时的包名一致。本demo的包名定义在build.gradle文件中
	 *
	 * @return
	 */
	private boolean checkAuth() {
		AuthInfo authInfo = mSpeechSynthesizer.auth(ttsMode);
		if (!authInfo.isSuccess()) {
			// 离线授权需要网站上的应用填写包名。本demo的包名是com.baidu.tts.sample，定义在build.gradle中
			String errorMsg = authInfo.getTtsError().getDetailMessage();
			LogUtils.i("【error】鉴权失败 errorMsg=" + errorMsg);
			return false;
		} else {
			LogUtils.i("验证通过，离线正式授权文件存在。");
			return true;
		}
	}

	/**
	 * 检查 TEXT_FILENAME, MODEL_FILENAME 这2个文件是否存在，不存在请自行从assets目录里手动复制
	 *
	 * @return
	 */
	private boolean checkOfflineResources() {
		String[] filenames = {TEXT_FILENAME, MODEL_FILENAME};
		for (String path : filenames) {
			File f = new File(path);
			if (!f.canRead()) {
				LogUtils.i("[ERROR] 文件不存在或者不可读取，请从assets目录复制同名文件到：" + path);
				LogUtils.i("[ERROR] 初始化失败！！！");
				return false;
			}
		}
		return true;
	}

	public void speak(String text, String utteranceId, MessageListener messageListener) {
		stop();

		/* 以下参数每次合成时都可以修改
		 *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
		 *  设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
		 *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); 设置合成的音量，0-9 ，默认 5
		 *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5"); 设置合成的语速，0-9 ，默认 5
		 *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); 设置合成的语调，0-9 ，默认 5
		 *
		 *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
		 *  MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
		 *  MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
		 *  MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
		 *  MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
		 */

		if (null == mSpeechSynthesizer) {
			LogUtils.i("[ERROR], 初始化失败");
			return;
		}
		if (null != messageListener) {
			mSpeechSynthesizer.setSpeechSynthesizerListener(messageListener);
		}
		int result = mSpeechSynthesizer.speak(text, utteranceId);
		LogUtils.i("合成并播放 按钮已经点击");
		checkResult(result, "speak");
	}

	public void stop() {
		LogUtils.i("停止合成引擎 按钮已经点击");
		int result = mSpeechSynthesizer.stop();
		checkResult(result, "stop");
	}

	public void release() {
		if (mSpeechSynthesizer != null) {
			mSpeechSynthesizer.stop();
			mSpeechSynthesizer.release();
			mSpeechSynthesizer = null;
			LogUtils.i("释放资源成功");
		}
	}

	private void checkResult(int result, String method) {
		if (result != 0) {
			LogUtils.i("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
		}
	}
}
