package com.myfragmentdemo.home;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.myfragmentdemo.R;
import com.myfragmentdemo.base.BaseContract;
import com.myfragmentdemo.base.MVPActivity;
import com.myfragmentdemo.base.MVPFragment;
import com.myfragmentdemo.left.LeftFragment;
import com.myfragmentdemo.right.RightFragment;
import com.myfragmentdemo.service.TaskService;
import com.myfragmentdemo.utils.Utils;

/**
 * 类描述：
 * 创建人：王建党
 * 创建时间：2018/2/8 14:46
 */

public class HomeActivity extends MVPActivity<HomePresenter> implements View.OnClickListener {
    private View home_bottom_tab1, home_bottom_tab2;
    private final String LEFT = "left", RIGHT = "right";
    private FragmentManager mFragmentManager;
    private TaskService.MyBinder myBinder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (TaskService.MyBinder) service;
            myBinder.startBind();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(myBinder!=null){
                myBinder.stopBind();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);
        initview();
    }

    private void initview() {
        Log.i("tag","---->>"+getTaskId());
        mFragmentManager = getSupportFragmentManager();
        home_bottom_tab1 = findViewById(R.id.home_bottom_tab1);
        home_bottom_tab2 = findViewById(R.id.home_bottom_tab2);
        home_bottom_tab1.findViewById(R.id.iv_icon).setBackgroundResource(
                R.drawable.home_bottom_show_iv_selector);
        home_bottom_tab2.findViewById(R.id.iv_icon).setBackgroundResource(
                R.drawable.home_bottom_me_iv_selector);
        ((TextView) home_bottom_tab1.findViewById(R.id.tv_text)).setText("左边");
        ((TextView) home_bottom_tab2.findViewById(R.id.tv_text)).setText("右边");
        home_bottom_tab1.setOnClickListener(this);
        home_bottom_tab2.setOnClickListener(this);
        home_bottom_tab1.setSelected(true);
        switchContent(LEFT);
    }

    private void bottomChangeSelected(View selectedView) {
        home_bottom_tab1.setSelected(false);
        home_bottom_tab2.setSelected(false);
        selectedView.setSelected(true);
    }

    private void switchContent(String tag) {
        MVPFragment fragment = null;
        if (LEFT.equals(tag)) {
            bottomChangeSelected(home_bottom_tab1);
            fragment = (MVPFragment) mFragmentManager.findFragmentByTag(tag);
            if (fragment == null) {
                fragment = new LeftFragment();
            }
        } else if (RIGHT.equals(tag)) {
            bottomChangeSelected(home_bottom_tab2);
            fragment = (MVPFragment) mFragmentManager.findFragmentByTag(tag);
            if (fragment == null) {
                fragment = new RightFragment();
            }
        }
        if (mFragmentManager != null && fragment != null) {
            if (fragment.isAdded()) {
                return;
            }
            mFragmentManager.beginTransaction()
                    .replace(R.id.home_container, fragment, tag).addToBackStack(tag)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (Utils.isFastClick())
            return;
        switch (v.getId()) {
            case R.id.home_bottom_tab1:
                if (!home_bottom_tab1.isSelected()) {
                    switchContent(LEFT);
                }
//                Intent intent = new Intent(HomeActivity.this, TaskService.class);
//                startService(intent);
//                bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                break;
            case R.id.home_bottom_tab2:
                if (!home_bottom_tab2.isSelected()) {
                    switchContent(RIGHT);
                }
//                Intent intent2 = new Intent(HomeActivity.this, TaskService.class);
//                stopService(intent2);
//                unbindService(serviceConnection);
                break;

            default:
                break;
        }
    }

    @Override
    public void setPresenter(BaseContract.BasePresenter presenter) {
        iBasePresent = (HomePresenter) presenter;
    }
}
