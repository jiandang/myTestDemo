package com.myfragmentdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 类描述：
 * 创建人：王建党
 * 创建时间：2018/2/10 15:49
 */

public class TaskService extends Service {
    private MyBinder myBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("tag", "----onBind--->>");
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("tag", "----onCreate--->>");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("tag", "----onStartCommand--->>");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("tag", "----onDestroy--->>");
    }

   public class MyBinder extends Binder {
        public void startBind() {
            Log.i("tag", "----startBind--->>");
        }

        public void stopBind() {
            Log.i("tag", "----stopBind--->>");
        }
    }
}
