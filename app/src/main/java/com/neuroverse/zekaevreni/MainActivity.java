package com.neuroverse.zekaevreni;
import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.webkit.ConsoleMessage;
import android.webkit.RenderProcessGoneDetail;
import android.widget.TextView;
import android.widget.FrameLayout;

public final class MainActivity extends Activity {
    private WebView web;
    private FrameLayout root;
    private static final String TAG="Neuroverse";
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(8,12,32));
        getWindow().setNavigationBarColor(Color.rgb(8,12,32));
        root=new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(8,12,32));
        setContentView(root);
        try {
            web=new WebView(this);
            WebSettings settings=web.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setAllowFileAccess(true);
            settings.setDefaultTextEncodingName("UTF-8");
            web.setBackgroundColor(Color.rgb(8,12,32));
            web.setWebChromeClient(new WebChromeClient() {
                @Override public boolean onConsoleMessage(ConsoleMessage c) {
                    Log.i(TAG, c.messageLevel()+": "+c.message()+" at "+c.sourceId()+":"+c.lineNumber()); return true;
                }
            });
            web.setWebViewClient(new WebViewClient() {
                @Override public void onReceivedError(WebView v, int code, String description, String url) {
                    Log.e(TAG,"Page error "+code+" "+description+" "+url);
                }
                @Override public boolean onRenderProcessGone(WebView v, RenderProcessGoneDetail info) {
                    Log.e(TAG,"WebView process lost, crashed: "+info.didCrash());
                    root.removeView(v); v.destroy(); web=null;
                    showError("Görüntüleme işlemi kapandı. Android System WebView uygulamasını güncelle.");
                    return true;
                }
            });
            root.addView(web,new FrameLayout.LayoutParams(-1,-1));
            web.loadUrl("file:///android_asset/index.html");
        } catch(Throwable e) {
            Log.e(TAG,"Startup failed",e);
            showError("NEUROVERSE açılış hatası: "+e);
        }
    }
    private void showError(String msg) {
        root.removeAllViews();
        TextView text=new TextView(this);
        text.setTextColor(Color.WHITE);
        text.setTextSize(16);
        text.setPadding(24,52,24,16);
        text.setText(msg);
        root.addView(text);
    }
    @Override public void onBackPressed() {
        if(web!=null) web.evaluateJavascript("document.dispatchEvent(new KeyboardEvent('keydown',{key:'Escape',bubbles:true}));",null);
        else super.onBackPressed();
    }
    @Override protected void onPause(){ if(web!=null) web.onPause(); super.onPause(); }
    @Override protected void onResume(){ super.onResume(); if(web!=null) web.onResume(); }
    @Override protected void onDestroy(){ if(web!=null) {root.removeView(web);web.destroy();web=null;} super.onDestroy(); }
}
