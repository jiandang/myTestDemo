package com.myfragmentdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * 类描述：
 * 创建人：NM-127
 * 创建时间：2017/11/2 15:11
 */

public class MTouchView extends TextView {
    private Scroller mScroller;
    public MTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }
    public void smoothScroller(int resx,int resy){
//        int scrollx = getScrollX();
//        int delta = resx-scrollx;
//        mScroller.startScroll(0,0,0,delta,1000);
        scrollTo((int)getX(),(int)getY()+100);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
    }
    private int start_x,start_y;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        float raw_x = event.getRawX();
        float raw_y = event.getRawY();
        Log.i("onTouchEvent","x="+x+",\ny="+y+",\nraw_x="+raw_x+",\nraw_y="+raw_y);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                start_x = x;
                start_y = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offset_x = x-start_x;
                int offset_y = y-start_y;
//                scrollTo(offset_x,offset_y);
                layout(getLeft()+offset_x,getTop()+offset_y,getRight()+offset_x,getBottom()+offset_y);

                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

}
