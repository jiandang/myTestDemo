package com.myfragmentdemo.utils;

/**
 * 工具类
 *
 * @author Administrator
 */
public class Utils {
    private static long lastClickTime;
    private static long lastClickFlowerTime;

    /**
     * 防止多次点击
     *
     * @return
     */
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        if (timeD < 400) {
            return true;
        }
        return false;
    }

}
