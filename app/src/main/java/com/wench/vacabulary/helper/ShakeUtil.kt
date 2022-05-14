package com.wench.vacabulary.helper

import android.app.Service
import android.content.Context
import android.os.Vibrator

object ShakeUtil {
    fun vibrator(context: Context?, duration: Long) {
        if (context == null) return
        val vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(duration)
    }
}