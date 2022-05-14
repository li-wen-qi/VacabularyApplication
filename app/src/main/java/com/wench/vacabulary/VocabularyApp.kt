package com.wench.vacabulary

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import com.tencent.mmkv.MMKV

/**
 * CreateTime:5/13/2022
 * Creator: liwenqi 328073180@qq.com
 * Description:
 */
class VocabularyApp:Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        MMKV.initialize(this)
        Log.d("路径-外", "${appContext?.getExternalFilesDir(null)}")
        Log.d("路径-内", "${appContext?.filesDir}")
    }

    companion object {
        var appContext: Context? = null
    }
}