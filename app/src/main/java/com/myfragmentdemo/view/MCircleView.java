package com.myfragmentdemo.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.myfragmentdemo.R;

/**
 * 类描述：
 * 创建人：NM-127
 * 创建时间：2017/11/16 17:26
 */

public class MCircleView extends View {
    private Paint mPaint;
    private int mcolor = Color.RED;
    public MCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.mycircle);
        mcolor = array.getColor(R.styleable.mycircle_circle_corlor,Color.BLUE);
        array.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mcolor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;
        canvas.drawCircle(width/2, height/2, radius, mPaint);
    }
}
