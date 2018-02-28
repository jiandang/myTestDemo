package com.myfragmentdemo.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Spinner;

/**
 * 类描述：
 * 创建人：NM-127
 * 创建时间：2017/11/10 18:20
 */

public class ClickControlledSpinner extends Spinner {

    public ClickControlledSpinner(Context context) {
        super(context);
    }

    public ClickControlledSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ClickControlledSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isMoved = false;
    private Point touchedPoint = new Point();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchedPoint.x = x;
                touchedPoint.y = y;
                break;
            case MotionEvent.ACTION_MOVE:
                isMoved = true;
                break;
            case MotionEvent.ACTION_UP:
                if (isMoved) {
                    // 从上向下滑动
                    if (y - touchedPoint.y > 20) {
                    }
                    // 从下向上滑动
                    else if (touchedPoint.y - y > 20) {
                    }
                    // 滑动幅度小时，当作点击事件
                    else {
                        onClick();
                    }
                    isMoved = false;
                } else {
                    onClick();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void onClick() {
        if (onClickMyListener != null && isEnabled()) {
            // use a thread to do something which maybe spend many times.
            new Thread() {
                public void run() {
                    onClickMyListener.onClick();
//                    performClick();
                }
            }.start();
        }
    }


    private OnClickMyListener onClickMyListener;

    /**
     * 注册自定义的点击事件监听
     * Register the click event self-fulfilling listener.
     * @param onClickMyListener
     */
    public void setOnClickMyListener(OnClickMyListener onClickMyListener) {
        this.onClickMyListener = onClickMyListener;
    }

    /**
     * 自定义点击事件监听.
     * Click event self-fulfilling listener.
     * @author Wison Xu
     */
    public interface OnClickMyListener {
        /**
         * 点击时触发
         * 警告：该方法在非UI线程中执行
         *
         * Triggers when click event occurs.
         * Warning: this method does not run in UI thread.
         */
        public void onClick() ;
    }
}
