package com.wench.vacabulary.helper

import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

/**
 * CreateTime:5/13/2022
 * Creator: liwenqi 328073180@qq.com
 * Description:
 */
abstract class MultiTouchListener @JvmOverloads constructor(multiTouchInterval: Long = 250) :
    OnTouchListener {
    // 连续 touch 的最大间隔, 超过该间隔将视为一次新的 touch 开始
    private val multiTouchInterval: Long

    // 上次 onTouch 发生的时间
    private var lastTouchTime: Long

    // 已经连续 touch 的次数
    private var touchCount: Int
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val now = System.currentTimeMillis()
            lastTouchTime = now
            synchronized(this) { touchCount++ }
            Handler().postDelayed({ // 两个变量相等, 表示时隔 multiTouchInterval 之后没有新的 touch 产生, 触发事件并重置 touchCount
                if (now == lastTouchTime) {
                    synchronized(this@MultiTouchListener) {
                        onMultiTouch(
                            v,
                            event,
                            touchCount
                        )
                        touchCount = 0
                    }
                }
            }, multiTouchInterval)
        }
        return true
    }

    abstract fun onMultiTouch(v: View?, event: MotionEvent?, touchCount: Int): Boolean

    init {
        this.multiTouchInterval = multiTouchInterval
        lastTouchTime = 0
        touchCount = 0
    }
}