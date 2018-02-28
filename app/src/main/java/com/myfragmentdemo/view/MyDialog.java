package com.myfragmentdemo.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.myfragmentdemo.R;

/**
 * 类描述：
 * 创建人：NM-127
 * 创建时间：2017/11/20 18:53
 */

public class MyDialog extends Dialog {
    private TextView tv_test;
    private Context context;
    protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        init();
    }

    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init();
    }

    public MyDialog(Context context) {
        super(context);
        this.context = context;
        init();
    }
    private void testValueAnimation(){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1,100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tv_test.setText( animation.getAnimatedValue()+"");
                Log.i("onAnimationUpdate","---animation.getAnimatedValue()--->>"+animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(5000).start();
    }
    private void init() {
        View view = getLayoutInflater().inflate(R.layout.dia_layout, null);
        setContentView(view);
        tv_test = (TextView) view.findViewById(R.id.tv_test);
        tv_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testValueAnimation();
            }
        });
//        tv_test.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add("hahaha");
//                menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(context, "用户点击了菜单", 0).show();
//                        return true;
//                    }
//                });
//            }
//        });
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.add("hahaha");
//    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i("onContextItemSelected", "dialog----onContextItemSelected");
        return true;
    }
}
