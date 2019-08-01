package cn.onestravel.navigation.utils;

import android.graphics.Rect;
import android.view.View;

/**
 * @author onestravel
 * @version 1.0.0
 * @name EventUtils
 * @description //TODO
 * @createTime 2019/1/23 10:50
 */
public class EventUtils {
    /**
     * 判断点击事件是否在view内
     * @param view
     * @param x
     * @param y
     * @return
     */
    public static boolean isInViewZone(View view, double x, double y) {
        Rect mChangeImageBackgroundRect = new Rect();
        view.getDrawingRect(mChangeImageBackgroundRect);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mChangeImageBackgroundRect.left = location[0];
        mChangeImageBackgroundRect.top = location[1];
        mChangeImageBackgroundRect.right = mChangeImageBackgroundRect.right + location[0];
        mChangeImageBackgroundRect.bottom = mChangeImageBackgroundRect.bottom + location[1];
        return mChangeImageBackgroundRect.contains((int)(x), (int)y);
    }
}
