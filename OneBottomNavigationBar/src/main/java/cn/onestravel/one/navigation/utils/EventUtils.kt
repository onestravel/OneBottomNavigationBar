package cn.onestravel.one.navigation.utils

import android.graphics.Rect
import android.view.View

/**
 * @author onestravel
 * @version 1.0.0
 * @name EventUtils
 * @description //TODO
 * @createTime 2019/1/23 10:50
 */
object EventUtils {
    /**
     * 判断点击事件是否在view内
     * @param view
     * @param x
     * @param y
     * @return
     */
    fun isInViewZone(view: View, x: Double, y: Double): Boolean {
        val mChangeImageBackgroundRect = Rect()
        view.getDrawingRect(mChangeImageBackgroundRect)
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        mChangeImageBackgroundRect.left = location[0]
        mChangeImageBackgroundRect.top = location[1]
        mChangeImageBackgroundRect.right = mChangeImageBackgroundRect.right + location[0]
        mChangeImageBackgroundRect.bottom = mChangeImageBackgroundRect.bottom + location[1]
        return mChangeImageBackgroundRect.contains(x.toInt(), y.toInt())
    }
}
