package com.junkchen.wxlogin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private WebView web_wx_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        web_wx_login = (WebView) findViewById(R.id.web_wx_login);

        WebViewClient viewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i(TAG, "shouldOverrideUrlLoading: url: " + request.getUrl().toString());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "onPageFinished: url: " + url);
//                WebView.HitTestResult result = view.getHitTestResult();
//                Log.i(TAG, "onPageFinished: Extra: " + result.getExtra() + ", type: " + result.getType());

//                view.loadUrl("javascript:window.java_obj.getSource('<head>'+" +
//                        "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
//                view.loadUrl("javascript:window.java_obj.getSource(document.getElementsByTagName('html')[0].innerHTML);");
                //http://test.lkyiliao.com/wx/webCallBack?code=061KyYXO0vMaS82quHZO0mEHXO0KyYXi&state=null
//                String regex = "http://test\\.lkyiliao\\.com/wx/webCallBack\\?code=+";
//                if (url.matches(regex)) {
//                if (url.startsWith("http://test.lkyiliao.com/wx/webCallBack?code=")) {
//                    Log.i(TAG, "onPageFinished: success...");
//                    view.loadUrl("javascript:window.java_obj.getSource(document.body.innerHTML);");
//                }
//                view.loadUrl("javascript:window.js_obj.toString();");
//                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if (url.startsWith("http://test.lkyiliao.com/wx/webCallBack?code=")) {
                    Log.i(TAG, "onLoadResource: user user agree login.");
                }else if (url.contains("&last=404&_")) {
                    Log.i(TAG, "onLoadResource: user had scan qr code");
                }else if (url.contains("&last=403&_")) {
                    Log.i(TAG, "onLoadResource: user cancel.");
                }
                super.onLoadResource(view, url);
                //https://long.open.weixin.qq.com/connect/l/qrconnect?uuid=001oAUrrkrIR9jk2&last=403&_=1508220696121
                //https://long.open.weixin.qq.com/connect/l/qrconnect?uuid=001oAUrrkrIR9jk2&last=404&_=1508220696110
                //https://long.open.weixin.qq.com/connect/l/qrconnect?uuid=071QWzTZhFgIalnW&last=404&_=1508221339497
                //https://long.open.weixin.qq.com/connect/l/qrconnect?uuid=071QWzTZhFgIalnW&last=403&_=1508221339498
                //http://test.lkyiliao.com/wx/webCallBack?code=0214BVkW1Z34CS0WwxkW1fwRkW14BVk4&state=login
                view.loadUrl("javascript:window.java_obj.getSource(document.body.innerHTML);");
                Log.i(TAG, "onLoadResource: url: " + url);
            }
        };

        web_wx_login.setWebViewClient(viewClient);

        //声明WebSettings子类
        WebSettings webSettings = web_wx_login.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
//        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        web_wx_login.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
//        web_wx_login.addJavascriptInterface(new JsObject(), "js_obj");

//        web_wx_login.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//
//            }
//        });
    }

    public void wxLogin(View view) {
        web_wx_login.loadUrl("http://test.lkyiliao.com/wx/toOauth");
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void getSource(String html) throws JSONException {
//            Log.i("html=", html);
            String jsonText = html.substring(html.indexOf("{"), html.indexOf("}") + 1);
//            JSONObject jsonObject = new JSONObject(jsonText);
//            boolean result = jsonObject.getBoolean("result");
//            int errorCode = jsonObject.getInt("errorCode");
//            Log.i(TAG, "getSource: result: " + result + ", errorCode: " + errorCode);
            Log.i(TAG, "getSource:" + jsonText);
        }
    }

    final class JsObject {
        @JavascriptInterface
        public String toString() {
            return "injectedObject";
        }
    }
}
