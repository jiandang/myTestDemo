package com.myfragmentdemo.right;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myfragmentdemo.R;
import com.myfragmentdemo.SecondActivity;
import com.myfragmentdemo.base.BaseContract;
import com.myfragmentdemo.base.MVPFragment;
import com.myfragmentdemo.view.MCircleView;

/**
 * 类描述：
 * 创建人：NM-127
 * 创建时间：2016/11/16 15:50
 */

public class RightFragment extends MVPFragment<RightPresenter> implements RightViewImpl {
    private ProgressDialog progressDialog;
    private MCircleView mCircleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, null);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在加载中...");
        progressDialog.show();
        attachMVPView();
        iBasePresenter.getData();
        mCircleView = (MCircleView) v.findViewById(R.id.circle_v);
        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SecondActivity.class));
            }
        });
        return v;
    }

    private void attachMVPView() {
        iBasePresenter = new RightPresenter();
        iBasePresenter.attachView(this);
    }

    @Override
    public void setPresenter(BaseContract.BasePresenter presenter) {
        iBasePresenter = (RightPresenter) presenter;
    }

    @Override
    public void showData(String data) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
    }
}
