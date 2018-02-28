package com.myfragmentdemo.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 类描述：
 * 创建人：王建党
 * 创建时间：2018/2/8 16:39
 */

public abstract class MVPFragment<P extends IBasePresent> extends Fragment{
    public P iBasePresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void attachMVPView(BaseContract.BaseView baseView) {
        if (iBasePresenter != null) {
            iBasePresenter.attachView(baseView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iBasePresenter != null) {
            iBasePresenter.detachView();
        }
    }
}
