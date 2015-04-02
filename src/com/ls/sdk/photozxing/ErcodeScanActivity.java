package com.ls.sdk.photozxing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.ls.sdk.R;
import com.ls.sdk.http.AnsynHttpRequest;
import com.ls.sdk.http.HttpStaticApi;
import com.ls.sdk.utils.ToastUtils;
/**
 * 这个是二维码扫描的入口,布局文件都在这里
 * @author ls
 *
 */
public class ErcodeScanActivity extends Activity implements Callback {
	public final static int PURCHASEADD = 200;
	private Context mContext;
	private CaptureActivityHandler handler;
	private ErcodeScanView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private SurfaceView surfaceView;
	private ImageView mBack;
	private Button mShowLight;
	private String requestUrl="http://www.baidu.com";

	private String resultString = "";
	private int screenWidth;
	private int request_code;
	private RelativeLayout ll_title, rl_btn;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_zxing_scan);
		mContext = this;
		CameraManager.init(getApplication());
		initControl();
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		request_code = getIntent().getFlags();

	}

	private void initControl() {
		mShowLight = (Button) findViewById(R.id.scan_light);
		viewfinderView = (ErcodeScanView) findViewById(R.id.viewfinder_view);
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		ll_title = (RelativeLayout) findViewById(R.id.ll_title);
		rl_btn = (RelativeLayout) findViewById(R.id.rl_btn);
		ll_title.getBackground().setAlpha(100);
		rl_btn.getBackground().setAlpha(100);
		mBack = (ImageView) findViewById(R.id.back);

		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mShowLight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CameraManager.get().turnLightOnOrOff();
			}
		});

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;

	}

	@Override
	protected void onResume() {
		super.onResume();
		start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	public void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * @param result
	 * @param barcode
	 *当扫描成功或者失败的处理消息
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		resultString = result.getText();//扫描成功后的字符串
		if (resultString.equals("")) {
			ToastUtils.showShort(ErcodeScanActivity.this, "扫描失败");
		} else {
			//扫描成功请求后台服务数据,
			scanRequestServer(this,requestUrl,callBack,Volley.newRequestQueue(this));
		}
	}
	/**
	 * 开始扫描
	 */
	private void start() {
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	/**
	 * 停止扫描
	 */
	private void stop() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ErcodeScanView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	/**
	 * 扫描正确后的震动声音,如果感觉apk大了,可以删除
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}
	private static final long VIBRATE_DURATION = 200L;
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	/**
	 * Get请求服务端数据
	 * @param context 上下问
	 * @param url 访问服务端的地址
	 * @param callBack	访问后的回调，失败或者成功
	 * @param mQueue 访问的对象
	 */
	public void scanRequestServer( final Context context, String url,
			final CallBack callBack,
			RequestQueue mQueue) {
		Response.Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				callBack.back(response, HttpStaticApi.SUCCESS_HTTP);
			}
		};
		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callBack.back(error.toString(), HttpStaticApi.FAILURE_HTTP);
			}
		};
		StringRequest stringRequest2 = new StringRequest(AnsynHttpRequest.Utf8URLencode(url), listener,
				errorListener);
		mQueue.add(stringRequest2);
	}
	/**
	 * 扫描到二维码需要从服务端返回数据的回调接口
	 * @author Administrator
	 */
	public interface CallBack{
		/**
		 * @param s 回调后的数据
		 * @param response 会调失败成功的区分码
		 */
		public void back(String data,int response);
	}
	CallBack callBack = new CallBack() {
		@Override
		public void back(String data, int response) {
			switch (response) {
			case HttpStaticApi.FAILURE_HTTP://访问网络失败
				ToastUtils.showShort(ErcodeScanActivity.this, "没连接网络，请求数据失败"
						);
				break;
			case HttpStaticApi.SUCCESS_HTTP://访问网络服务端成功返回数据
				ToastUtils.showShort(ErcodeScanActivity.this, "访问网络成功的回调"+ data
						);
				break;
			}
		}
	};
}

