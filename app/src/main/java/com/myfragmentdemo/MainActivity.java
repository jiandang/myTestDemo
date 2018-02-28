package com.myfragmentdemo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.myfragmentdemo.view.ClickControlledSpinner;
import com.myfragmentdemo.view.MCircleView;
import com.myfragmentdemo.view.MTouchView;
import com.myfragmentdemo.view.MyDialog;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private TextView tv_title;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private MTouchView mTouchView;
    private ClickControlledSpinner spinner;
    private ArrayAdapter<String> adapter;
    private boolean isExpand;
    private MCircleView circle_v;
    private MyDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();
    }

    private void createNotify() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.btn_show_talk_bg, "来了一个通知", System.currentTimeMillis());
        manager.notify(1, notification);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i("onContextItemSelected", "onContextItemSelected");
        return super.onContextItemSelected(item);
    }

    private static class ViewWrapper {
        private View target;

        public ViewWrapper(View view) {
            this.target = view;
        }

        public float getWidth() {
            return target.getLayoutParams().width;
        }

        public void setWidth(float width) {
            target.getLayoutParams().width = (int) width;
            target.requestLayout();

        }
    }

    private void testValueAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
        valueAnimator.start();
    }

    private void init() {
        circle_v = (MCircleView) findViewById(R.id.circle_v);
        circle_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewWrapper wrapper = new ViewWrapper(v);
                ObjectAnimator.ofFloat(wrapper, "width", v.getWidth() * 1.2f).setDuration(2000).start();
            }
        });
//        circle_v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add("测试");
//            }
//        });
//        manager = getSupportFragmentManager();
//        transaction = manager.beginTransaction();
//        tv_title = (TextView) findViewById(R.id.tv_title);
//        transaction.replace(R.id.contain, new LeftFragment()).addToBackStack(null).commit();
//        tv_title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//              //  manager.beginTransaction().replace(R.id.contain, new RightFragment()).addToBackStack(null).commit();
////                send();
////                mTouchView.smoothScroller(500,500);
//                ObjectAnimator.ofFloat(mTouchView,"translationY",0,100).setDuration(1000).start();
//            }
//        });
//        mTouchView = (MTouchView) findViewById(R.id.touv_test);
//        mTouchView.setText("woshishishi");
//        findViewById(R.id.tv_talk_number).setVisibility(View.VISIBLE);
//        tv_title.setVisibility(View.VISIBLE);
    }

    private void send() {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra("tag", "main");
        startActivity(intent);
    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Toast.makeText(MainActivity.this, "onKeyDown", 0).show();
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
