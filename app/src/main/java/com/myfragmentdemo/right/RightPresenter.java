package com.myfragmentdemo.right;

import android.os.Handler;

import com.myfragmentdemo.base.IBasePresent;


/**
 * 类描述：
 * 创建人：王建党
 * 创建时间：2018/2/9 11:09
 */

public class RightPresenter extends IBasePresent<RightViewImpl> {

    public void getData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mvpView.showData("获取数据成功");
            }
        }, 3000);
    }
}
