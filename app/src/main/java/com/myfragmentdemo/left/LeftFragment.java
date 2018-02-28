package com.myfragmentdemo.left;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.myfragmentdemo.R;
import com.myfragmentdemo.base.MVPFragment;


/**
 * 类描述：
 * 创建人：NM-127
 * 创建时间：2016/11/16 15:46
 */

public class LeftFragment extends MVPFragment<LeftPresenter> {
    private WebView my_webv;
    private View mContentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView != null && mContentView.getParent() != null) {
            ((ViewGroup) mContentView.getParent()).removeView(mContentView);
        } else {
            mContentView = inflater.inflate(R.layout.frag_layout, null);
            my_webv = (WebView) mContentView.findViewById(R.id.my_webv);
            my_webv.getSettings().setJavaScriptEnabled(true);
            my_webv.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });
            my_webv.loadUrl("https://www.baidu.com/");
        }
        return mContentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(my_webv!=null){
            my_webv.removeAllViews();
        }
    }
}
