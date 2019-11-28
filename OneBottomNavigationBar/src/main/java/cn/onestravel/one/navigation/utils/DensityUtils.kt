package cn.onestravel.one.navigation.utils

import android.content.Context
import android.content.res.Resources

/**
 * @author onestravel
 * @version 1.0.0
 * @name DensityUtils
 * @description //TODO
 * @createTime 2019/1/22 11:11
 */
object DensityUtils {

    /**
     * dp转换为px
     *
     * @param context
     * @param dpValue
     * @return
     */
    fun dpToPx(context: Context, dpValue: Float): Int {//dp转换为px
        val scale = context.resources.displayMetrics.density//获得当前屏幕密度
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px转换为dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    fun pxToDp(context: Context, pxValue: Float): Int {//
        val scale = context.resources.displayMetrics.density//获得当前屏幕密度
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * dp转换为px
     *
     * @param resources
     * @param dpValue
     * @return
     */
    fun dpToPx(resources: Resources, dpValue: Float): Int {
        val scale = resources.displayMetrics.density//获得当前屏幕密度
        return (dpValue * scale + 0.5f).toInt()
    }


    /**
     * px转换为dp
     *
     * @param resources
     * @param pxValue
     * @return
     */
    fun pxToDp(resources: Resources, pxValue: Float): Int {//
        val scale = resources.displayMetrics.density//获得当前屏幕密度
        return (pxValue / scale + 0.5f).toInt()
    }


    /**
     * sp转换为px
     *
     * @param resources
     * @param spValue
     * @return
     */
    fun spToPx(resources: Resources, spValue: Float): Int {
        val scale = resources.displayMetrics.scaledDensity//获得当前屏幕密度
        return (spValue * scale + 0.5f).toInt()
    }


    /**
     * px转换为sp
     *
     * @param resources
     * @param pxValue
     * @return
     */
    fun pxToSp(resources: Resources, pxValue: Float): Int {//
        val scale = resources.displayMetrics.scaledDensity//获得当前屏幕密度
        return (pxValue / scale + 0.5f).toInt()
    }
}
