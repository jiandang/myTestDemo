package com.myfragmentdemo.base;

/**
 * 类描述：
 * 创建人：王建党
 * 创建时间：2018/2/8 15:03
 */

public class IBasePresent<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<BaseContract.BaseView> {
    public T mvpView;

    @Override
    public void attachView(BaseContract.BaseView view) {
        this.mvpView = (T) view;
        mvpView.setPresenter(this);//给p的子类赋值
    }

    @Override
    public void detachView() {
        if (mvpView != null)
            mvpView = null;
    }

    @Override
    public boolean isAttachView() {
        return mvpView != null;
    }
}
