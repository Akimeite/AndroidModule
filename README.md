[![Build Status](https://img.shields.io/travis/Djangoogle/Djangoogle/master.svg?style=flat-square)](https://travis-ci.org/Djangoogle/Djangoogle)
[![Platform support](https://img.shields.io/badge/platform-android-f44336.svg?style=flat-square)](https://github.com/Djangoogle/Djangoogle/blob/master/LICENSE)
[![Language](https://img.shields.io/badge/language-java-ff9800.svg?style=flat-square)](https://github.com/Djangoogle/Djangoogle/blob/master/LICENSE)
[![Apache License 2.0](https://img.shields.io/badge/license-Apache%202.0-607d8b.svg?style=flat-square)](https://github.com/Djangoogle/Djangoogle/blob/master/LICENSE)

Banner:
[![Banner](https://img.shields.io/bintray/v/djangoogle/maven/banner.svg?style=flat-square)](https://github.com/Djangoogle/Djangoogle/tree/master/banner)

Framework:
[![Framework](https://img.shields.io/bintray/v/djangoogle/maven/framework.svg?style=flat-square)](https://github.com/Djangoogle/Djangoogle/tree/master/framework)

Player:
[![Player](https://img.shields.io/bintray/v/djangoogle/maven/player.svg?style=flat-square)](https://github.com/Djangoogle/Djangoogle/tree/master/player)

# Android基础框架

## 用法
>implementation 'com.djangoogle.support:framework:2.0.0-alpha2'

## AndroidManifest.xml
>清单文件
* 已添加下列权限：
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
* 请在程序启动时调用申请权限代码：
```java
AndPermission.with(this).runtime().permission(Permission.Group.STORAGE).start();
```

## BaseActivity
### DjangoogleActivity
>所有Activity的基类
* 基类默认创建了一个基础布局，根布局为CoordinatorLayout，包含了一个定制的Toolbar标题栏、左下角与右下角各一个FloatingActionButton浮动按钮
* 通用组件：
```java
protected CoordinatorLayout clBaseRootView;//根View
protected AppBarLayout ablCommonToolBar;//通用ToolBar根布局
protected Toolbar tbCommon;//通用ToolBar
protected AppCompatImageButton acibToolBarBackBtn;//返回键
protected AppCompatImageView acivToolBarAvatar;//头像
protected AppCompatTextView actvToolBarTitle;//标题
protected AppCompatEditText acetToolBarInput;//输入框
protected AppCompatTextView actvToolBarRightTextBtn;//右侧文字按钮
protected AppCompatImageButton acibToolBarRightImgBtn;//右侧图标按钮
protected FrameLayout flBaseBodyView;//bodyview
protected FloatingActionButton fabBaseBottomRightBtn;//右下角浮动按钮
protected FloatingActionButton fabBaseBottomLeftBtn;//左下角浮动按钮
protected Activity mActivity;//通用Activity
```

* **mUseBaseLayoutFlag** 通用标识符，判断是否使用基础布局
* **mNoDoubleClickFlag** 通用标识符，判断是否使用防重复打开Activity

* 通常情况下，直接在 **initLayout()** 方法的返回值中直接设置布局文件Id即可：
```java
@Override
protected int initLayout() {
	return R.layout.*;
}
```

* 特殊情况下，若不想继承基础的布局文件，完全使用自定义的布局，则需要保持上方的initLayout()方法代码不变，并同时在onCreate的super.onCreate(savedInstanceState)之前调用useCustomLayout()方法：
```java
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
	useCustomLayout();
	super.onCreate(savedInstanceState);
}
```

* 布局初始化完毕之后，会依次调用三个抽象方法，请在对应的方法中处理相应的事件，并注意调用顺序：
```java
/**
 * 设置界面
 */
protected abstract void initGUI();
/**
 * 设置事件
 */
protected abstract void initAction();
/**
 * 设置数据
 */
protected abstract void initData();
```

* 关于ButterKnife

ButterKnife只能支持自己依赖的Module，因此在基类中保留了ButterKnife的绑定事件：
```java
protected abstract void initButterKnife();
```

然后在主项目中依赖ButterKnife，并新建一个BaseActivity抽象类继承DjangoogleActivity，绑定ButterKnife，请注意，主项目的Activity都要继承此BaseActivity，不要继承DjangoogleActivity：
```java
public abstract class BaseActivity extends AgesunBaseActivity {
	@Override
	protected void initButterKnife() {
		ButterKnife.bind(this);
	}
}
```

* 通用方法
```java
/**
 * 隐藏返回键
 */
protected void hideBackBtn() {
	if (mUseBaseLayoutFlag) {
		acibToolBarBackBtn.setVisibility(View.GONE);
	}
}
/**
 * 显示头像
 */
protected void showAvatar() {
	if (mUseBaseLayoutFlag) {
		acivToolBarAvatar.setVisibility(View.VISIBLE);
	}
}
/**
 * 设置标题
 *
 * @param title 标题文字
 */
protected void setTitle(String title) {
	if (mUseBaseLayoutFlag) {
		actvToolBarTitle.setVisibility(View.VISIBLE);
		actvToolBarTitle.setText(title);
	}
}
/**
 * 设置输入框
 *
 * @param hint 提示文字
 */
protected void setInput(String hint) {
	if (mUseBaseLayoutFlag) {
		acetToolBarInput.setVisibility(View.VISIBLE);
		acetToolBarInput.setHint(hint);
	}
}
/**
 * 设置右侧文字按钮
 *
 * @param visibility 可见度
 * @param text       按钮文字
 * @param listener   点击事件
 */
protected void setRightTextBtn(int visibility, String text, View.OnClickListener listener) {
	if (mUseBaseLayoutFlag) {
		actvToolBarRightTextBtn.setVisibility(visibility);
		actvToolBarRightTextBtn.setText(text);
		if (null != listener) {
			actvToolBarRightTextBtn.setOnClickListener(listener);
		}
	}
}
/**
 * 设置右侧图标按钮
 *
 * @param resId    图标资源文件
 * @param listener 点击事件
 */
protected void setRightImgBtn(int resId, View.OnClickListener listener) {
	if (mUseBaseLayoutFlag) {
		acibToolBarRightImgBtn.setVisibility(View.VISIBLE);
		acibToolBarRightImgBtn.setImageResource(resId);
		if (null != listener) {
			acibToolBarRightImgBtn.setOnClickListener(listener);
		}
	}
}
/**
 * 设置右下角浮动按钮
 *
 * @param resId    图标资源文件
 * @param listener 点击事件
 */
protected void setBottomRightFloatingBtn(int resId, View.OnClickListener listener) {
	if (mUseBaseLayoutFlag) {
		fabBaseBottomRightBtn.show();
		fabBaseBottomRightBtn.setImageResource(resId);
		if (null != listener) {
			fabBaseBottomRightBtn.setOnClickListener(listener);
		}
	}
}
/**
 * 设置左下角浮动按钮
 *
 * @param resId    图标资源文件
 * @param listener 点击事件
 */
protected void setBottomLeftFloatingBtn(int resId, View.OnClickListener listener) {
	if (mUseBaseLayoutFlag) {
		fabBaseBottomLeftBtn.setImageResource(resId);
		if (null != listener) {
			fabBaseBottomLeftBtn.setOnClickListener(listener);
		}
	}
}
/**
 * 显示Loading
 */
protected void showLoading() {
	LoadingManager.getInstance().show(mActivity);
}
/**
 * 隐藏Loading
 */
protected void hideLoading() {
	LoadingManager.getInstance().hide();
}
/**
 * 打开Activity，并防止重复连续点击
 *
 * @param intent 意图
 */
public void startActivity(Intent intent) {
	if (mNoDoubleClickFlag && NoDoubleClickUtils.isDoubleClick()) {
		LogUtils.d("重复调用startActivity()，点击间隔时间不得小于" + NoDoubleClickUtils.INTERVAL + "ms");
		return;
	}
	super.startActivity(intent);
}
/**
 * 设置防重复打开Activity
 *
 * @param noDoubleClickFlag
 */
public void setNoDoubleClickFlag(boolean noDoubleClickFlag) {
	mNoDoubleClickFlag = noDoubleClickFlag;
}
```

* 关于EventBus

在onStart()和onStop()中分别注册和销毁了EventBus，并且添加了默认的EventBus事件，不添加的话程序会崩溃，所以请勿删除：
```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onBaseMessageEvent(Object event) {}
```

### DjangoogleGalleryActivity
>相册选取基类
* 继承自DjangoogleActivity，所有通用组件与方法参考DjangoogleActivity，并添加了两个方法用于弹出相册选择框：
```java
/**
 * 在屏幕底部弹出选择框并指定任意ViewGroup背景变暗
 *
 * @param viewGroup     指定的View
 * @param mimeType      媒体类型 MimeType.ofAll().ofImage().ofVideo().ofAudio()
 * @param maxPickNumber 最多可以选择的媒体数量
 */
protected void popupGallery(ViewGroup viewGroup, int mimeType, int maxPickNumber)

/**
 * 在屏幕底部弹出选择框
 *
 * @param mimeType      媒体类型 MimeType.ofAll().ofImage().ofVideo().ofAudio()
 * @param maxPickNumber 最多可以选择的媒体数量
 */
protected void popupGallery(int mimeType, int maxPickNumber)
```

* 关于ButterKnife

与DjangoogleActivity相同

## BaseFragment
### DjangoogleFragment
>所有Fragment的基类
基本方法与DjangoogleActivity相同，以下是不同点：
* DjangoogleFragment没有基础布局，initLayout()的返回值就是根布局
* 因Fragment的特殊性，initData()方法做了懒加载处理，即当前Fragment处于可见状态时，才会执行initData()方法，因此如果在使用了ViewPager等会缓存Fragment的组件的时候，必须在initData()里面处理数据，否则可能会影响性能，甚至出现bug
* 关于ButterKnife，参考上方的BaseActivity，新建一个BaseFragment抽象类继承DjangoogleFragment，绑定ButterKnife，以后主项目中需要使用弹窗选择相册的Fragment都继承此BaseFragment，不要继承DjangoogleFragment，注意bind()方法的写法与Activity不同：
```java
public abstract class BaseFragment extends DjangoogleFragment {
	@Override
	protected void initButterKnife(View view) {
		ButterKnife.bind(this, view);
	}
}
```

## DjangoogleApplication
>自定义基础Application
* 对所有依赖库进行了必要的初始化操作，请将主项目自定义的Application继承此DjangoogleApplication
* 因考虑到不同项目可能存在的差异，请在主项目自定义的Application中初始化OkGo，具体的参数酌情修改：
```java
//网络框架超时时间
private static final long OK_HTTP_TIME_OUT = 10L;

/**
 * 初始化网络请求框架
 */
private void initOkGo() {
	OkHttpClient.Builder builder = new OkHttpClient.Builder();
	HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor("OkGo");
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
	//使用数据库保持cookie，如果cookie不过期，则一直有效
	builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
	//配置log
	OkGo.getInstance().init(this)//必须调用初始化
	    .setOkHttpClient(builder.build())//建议设置OkHttpClient，不设置将使用默认的
	    .setRetryCount(0);//全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
}
```

## BaseModel
>序列化的基础实体类，主项目中除数据库实体类以外所有实体类都推荐继承此类

## BaseLitePalSupport
>序列化的数据库基础实体类，主项目中所有使用LitePal工具的数据库实体类都推荐继承此类

## MarqueeTextView
>自定义的跑马灯控件

继承自android.support.v7.widget.AppCompatTextView，使用时必须在布局中加上以下属性才能生效：
```xml
<style name="marquee_txt_style">
	<item name="android:marqueeRepeatLimit">marquee_forever</item>
	<item name="android:ellipsize">marquee</item>
	<item name="android:singleLine">true</item>
	<item name="android:focusableInTouchMode">true</item>
	<item name="android:focusable">true</item>
</style>
```

## LruBitmapCacheUtil
>Bitmap内存缓存管理器

## 颜色库
>参见res/values/colors.xml

常用颜色推荐使用material design推荐的配色：
```xml
<!--material配色-->
<!--https://material.io/collections/color/#-->
<color name="material_red">#f44336</color>
<color name="material_pink">#e91e63</color>
<color name="material_purple">#9c27b0</color>
<color name="material_deep_purple">#673ab7</color>
<color name="material_indigo">#3f51b5</color>
<color name="material_blue">#2196f3</color>
<color name="material_light_blue">#03a9f4</color>
<color name="material_cyan">#00bcd4</color>
<color name="material_teal">#009688</color>
<color name="material_green">#4caf50</color>
<color name="material_light_green">#8bc34a</color>
<color name="material_lime">#cddc39</color>
<color name="material_yellow">#ffeb3b</color>
<color name="material_amber">#ffc107</color>
<color name="material_orange">#ff9800</color>
<color name="material_deep_orange">#ff5722</color>
<color name="material_brown">#795548</color>
<color name="material_grey">#9e9e9e</color>
<color name="material_blue_grey">#607d8b</color>
```

## Glide
>[Glide](https://github.com/bumptech/glide)
>>已使用 **@GlideModule** 注解将GlideModle注入到GeneratedAppGlideModuleImpl中，在实际使用时请调用GlideApp进行操作，而不是直接调用Glide，GlideUtils中已经创建好了几个基本方法，可以直接调用

## 相册/拍照
>[Phoenix](https://github.com/guoxiaoxing/phoenix)

## 网络请求
>[OkGo](https://github.com/jeasonlzy/okhttp-OkGo)

## 通用工具类库
>[AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)（[文档](https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/README-CN.md)）
* 日志管理

日志管理请使用 **LogUtils** 统一管理

## 数据库
>[LitePal](https://github.com/LitePalFramework/LitePal)

## 滚轮控件
>[PickerView](https://github.com/Bigkoo/Android-PickerView)

## 通用RecyclerView适配器
>[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)（[文档](https://www.jianshu.com/p/b343fcff51b0)）

## 列表侧滑控件
>[EasySwipeMenuLayout](https://github.com/anzaizai/EasySwipeMenuLayout)
>>请结合上方的BaseRecyclerViewAdapterHelper使用

## 通用列表分割线
>[RecyclerView-FlexibleDivider](https://github.com/yqritc/RecyclerView-FlexibleDivider)
>>仅限RecyclerView

## 通用Popup
>[EasyPopup](https://github.com/zyyoona7/EasyPopup)

## 通用权限管理
>[AndPermission](https://github.com/yanzhenjie/AndPermission)

## 通用key-value组件
>[MMKV](https://github.com/Tencent/MMKV)（[文档](https://github.com/Tencent/MMKV/blob/master/readme_cn.md)）

## 通用Loading动画
>[Android-SpinKit](https://github.com/ybq/Android-SpinKit)

## 通用消息传递
>[EventBus](https://github.com/greenrobot/EventBus)
