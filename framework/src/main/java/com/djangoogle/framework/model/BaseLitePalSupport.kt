package com.djangoogle.framework.model

import org.litepal.crud.LitePalSupport

import java.io.Serializable

/**
 * 序列化的数据库基础实体类
 * Created by Djangoogle on 2018/10/18 14:50 with Android Studio.
 */
class BaseLitePalSupport : LitePalSupport(), Serializable
