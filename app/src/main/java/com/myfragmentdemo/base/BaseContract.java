package com.myfragmentdemo.base;

/**
 * 类描述：
 * 创建人：王建党
 * 创建时间：2018/2/8 15:00
 */

public interface BaseContract {
    interface BaseView<P extends BasePresenter> {
        void setPresenter(P presenter);
    }

    interface BasePresenter<BaseView> {

        void attachView(BaseView view);

        void detachView();

        boolean isAttachView();
    }

}
