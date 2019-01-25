package cn.onestravel.bottomview.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * @author onestravel
 * @version 1.0.0
 * @name cn.onestravel.bottomview.utils.DensityUtils
 * @description //TODO
 * @createTime 2019/1/22 11:11
 */
public class DensityUtils {

    /**
     * dp转换为px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dpToPx(Context context, float dpValue) {//dp转换为px
        float scale = context.getResources().getDisplayMetrics().density;//获得当前屏幕密度
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转换为dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int pxToDp(Context context, float pxValue) {//
        float scale = context.getResources().getDisplayMetrics().density;//获得当前屏幕密度
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转换为px
     *
     * @param resources
     * @param dpValue
     * @return
     */
    public static int dpToPx(Resources resources, float dpValue) {
        float scale = resources.getDisplayMetrics().density;//获得当前屏幕密度
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * px转换为dp
     *
     * @param resources
     * @param pxValue
     * @return
     */
    public static int pxToDp(Resources resources, float pxValue) {//
        float scale = resources.getDisplayMetrics().density;//获得当前屏幕密度
        return (int) (pxValue / scale + 0.5f);
    }

}
