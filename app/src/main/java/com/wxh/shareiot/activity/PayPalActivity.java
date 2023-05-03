package com.wxh.shareiot.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.LogUtils;
import com.wxh.basiclib.base.BaseNoModelActivity;
import com.wxh.shareiot.R;
import com.wxh.shareiot.databinding.ActivityPaypalBinding;
import com.wxh.shareiot.utils.Urls;
import com.wxh.shareiot.utils.UserUtil;

import java.util.HashMap;
import java.util.Map;

public class PayPalActivity extends BaseNoModelActivity<ActivityPaypalBinding> {
    private WebView mWebView;
    private String url = Urls.HTTP_URL + "pay/payPal/startPaypal";

    @Override
    protected int getLayout() {
        return R.layout.activity_paypal;
    }

    @Override
    protected void initView(Bundle bundle) {

        mWebView = new WebView(this, null);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            /**
             * 防止加载网页时调起系统浏览器
             */
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
      mWebView.setWebChromeClient(new WebChromeClient(){
          @Override
          public void onProgressChanged(WebView view, int newProgress) {
              if(newProgress==100){
                  dataBinding.pb.setProgress(0);
                  dataBinding.pb.setVisibility(View.GONE);
              }else{
                  dataBinding.pb.setProgress(newProgress);
                  dataBinding.pb.setVisibility(View.VISIBLE);
              }
          }
      });
        dataBinding.webView.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(this), "android");

        String orderId = getIntent().getStringExtra("orderId");
        url += "?orderId=" + orderId;
        LogUtils.e(url);
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", UserUtil.token);
        mWebView.loadUrl(url, headers);

    }

    @Override
    protected void initData() {

    }

    private class MyJavaScriptInterface {
        private Context mContext;

        public MyJavaScriptInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void exit() {
            finish();

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null)
            mWebView.destroy();
        super.onDestroy();
    }
}