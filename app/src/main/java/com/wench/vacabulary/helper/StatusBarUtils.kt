package com.wench.vacabulary.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.view.WindowManager
// https://github.com/mikepenz/MaterialDrawer/issues/254
// https://github.com/laobie/StatusBarUtil/blob/master/library/src/main/java/com/jaeger/library/StatusBarUtil.java
object StatusBarUtils {
    private const val SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 0x00002000
    private const val SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR = 0x00000010

    /**
    * 修改 MIUI V6  以上状态栏颜色
    */
    @SuppressLint("PrivateApi")
    private fun setMIUIStatusBarDarkIcon(
          activity: android.app.Activity,
          darkIcon: Boolean
    ) {
    val clazz = activity.window.javaClass
    try {
      val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
      val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
      val darkModeFlag = field.getInt(layoutParams)
      val extraFlagField =
              clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
      extraFlagField.invoke(activity.window, if (darkIcon) darkModeFlag else 0, darkModeFlag)
    } catch (e: Exception) {
    }
    }

    private fun setWindowFlag(
            activity: android.app.Activity,
            bits: Int,
            on: Boolean
    ) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }


    /**
     * 修改魅族状态栏字体颜色 Flyme 4.0
     */
    private fun setMeizuStatusBarDarkIcon(
            activity: android.app.Activity,
            darkIcon: Boolean
    ) {
        try {
            val lp = activity.window.attributes
            val darkFlag =
                    WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = meizuFlags.getInt(lp)
            value = if (darkIcon) {
                value or bit
            } else {
                value and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            activity.window.attributes = lp
        } catch (e: Exception) {
        }
    }

      @SuppressLint("ObsoleteSdkInt")
      fun update(
              activity: Activity,
              statusBarTranslucent: Boolean,
              navigationBarTranslucent: Boolean,
              statusBarLightMode: Boolean, // lightMode -> darkIcon
              navigationBarLightMode: Boolean
      ) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT &&
            android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP
        ) {
          if (statusBarTranslucent) {
              setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
          }
          if (navigationBarTranslucent) {
              setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true)
          }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
          if (statusBarTranslucent) {
              setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
          }
          activity.window.statusBarColor = android.graphics.Color.TRANSPARENT

          if (navigationBarTranslucent) {
              setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false)
          }
          activity.window.navigationBarColor = android.graphics.Color.TRANSPARENT
          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            activity.window.navigationBarDividerColor = android.graphics.Color.TRANSPARENT
          }
        }

          setMIUIStatusBarDarkIcon(activity, statusBarLightMode)
          setMeizuStatusBarDarkIcon(activity, navigationBarLightMode)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
          activity.window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_VISIBLE.let {
            var result = it
            if (statusBarTranslucent) result = result or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (navigationBarTranslucent) result = result or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            if (statusBarLightMode) result = result or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
              if (statusBarLightMode) result = result or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
              if (navigationBarLightMode) result = result or android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            result
          }
        }
      }

}

fun Activity.useTranslucentStatusBar(statusBarLightMode: Boolean = false) {
    StatusBarUtils.update(
            this, statusBarTranslucent = true, navigationBarTranslucent = false,
            statusBarLightMode = statusBarLightMode, navigationBarLightMode = true
    )
}
