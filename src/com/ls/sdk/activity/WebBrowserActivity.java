package com.ls.sdk.activity;

import com.ls.sdk.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * 用于显示简单的html的界面.传递title和url即可. 4、设置WevView要显示的网页：
 * 互联网用：webView.loadUrl("http://www.google.com");
 * 本地文件用：webView.loadUrl("file:///android_asset/XX.html"); 本地文件存放在：assets文件中
 * 
 */
public class WebBrowserActivity extends BaseActivity {
	/**
	 * 显示的标题.
	 */
	public static final String ACTION_KEY_TITLE = "action_key_title";
	/**
	 * 加载的的url
	 */
	public static final String ACTION_KEY_URL = "action_key_url";
	private WebView webview;
	private ProgressBar progressbar;

	private void initView() {
		registerBack();
		Intent intent = getIntent();
		String t = intent.getStringExtra(ACTION_KEY_TITLE);
		String url = intent.getStringExtra(ACTION_KEY_URL);
		setTitle(t, true, TITLE_TYPE_IMG, R.drawable.back, false, -1, -1);
		webview = (WebView) findViewById(R.id.webview);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		webview.setWebChromeClient(new WebChromeClient());
		webview.setWebViewClient(new WebViewClient());
		webview.loadUrl(url);
	}

	public class WebChromeClient extends android.webkit.WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				progressbar.setVisibility(View.GONE);
			} else {
				if (progressbar.getVisibility() == View.GONE)
					progressbar.setVisibility(View.VISIBLE);
				progressbar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
			webview.goBack();
			return true;
		} else if (!webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();// 解决刚加载webview时快速点击返回键不卡死
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		webview.destroy();
	}

	@Override
	public void onClick(View arg0) {

	}

	@Override
	public void onCreate(Bundle savedInstanceState, Context context) {
		setContentView(R.layout.activity_browser);
	}

	@Override
	public void init(Context context) {
		initView();
	}

	@Override
	public void initListener(Context context) {

	}
}
