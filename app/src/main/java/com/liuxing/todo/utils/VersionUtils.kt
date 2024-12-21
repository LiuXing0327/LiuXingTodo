package com.liuxing.todo.utils

import android.content.Context
import android.os.Build

/**
 * Author：流星
 * DateTime：2024/12/21 17:24
 * Description：版本工具类
 */
object VersionUtils {

    /**
     * 获取版本名
     *
     * @param context 上下文
     * @return 版本名
     */
    fun getVersionName(context: Context): String? =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName

    /**
     * 获取版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    fun getVersionCode(context: Context): Long {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
    }

}