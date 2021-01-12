package com.example.myapplication;

import android.graphics.Bitmap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.util.Log;

import java.util.concurrent.Callable;

public class MeasuringWebViewClient extends WebViewClient {
    public long rootResourceOnLoadNs = 0;
    public long pageStartedNs = 0;
    public long pageCommitVisibleNs = 0;
    public long pageFinishedNs = 0;
    public String originalUrl = null;
    private Callable<Void> onPageFinishedCallback = null;

    public void setOnPageFinishedCallback(Callable<Void> onPageFinishedCallback) {
        this.onPageFinishedCallback = onPageFinishedCallback;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.i("FIRST_FRAG", "shouldOverrideUrlLoading: " + request.getUrl());
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.i("FIRST_FRAG", "onPageStarted: " + url);
        if (url.equals(originalUrl)) {
            pageStartedNs = System.nanoTime();
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (url.equals(originalUrl) && pageFinishedNs == 0) {
            Log.i("FIRST_FRAG", "onPageFinished: " + url);
            pageFinishedNs = System.nanoTime();
            if (this.onPageFinishedCallback != null) {
                try {
                    this.onPageFinishedCallback.call();
                } catch (Exception e) {
                    Log.e("FIRST_FRAG", "Caught error while invoking onPageFinished callback", e);
                }
            }
        }
        super.onPageFinished(view, url);
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        Log.i("FIRST_FRAG", "onPageCommitVisible: " + url);
        pageCommitVisibleNs = System.nanoTime();
        super.onPageCommitVisible(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (originalUrl == null) {
            Log.i("FIRST_FRAG", "onLoadResource: " + url);
            originalUrl = url;
            rootResourceOnLoadNs = System.nanoTime();
        }
        super.onLoadResource(view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//        Log.i("FIRST_FRAG", "shouldInterceptRequest: " + request.getUrl());
        return super.shouldInterceptRequest(view, request);
    }


}
