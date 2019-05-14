package com.djangoogle.framework.glide;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * 自定义GlideModle，将注入到GeneratedAppGlideModuleImpl
 * Created by Djangoogle on 2018/10/11 15:05 with Android Studio.
 */
@GlideModule
public class DjangoGlideModule extends AppGlideModule {}
