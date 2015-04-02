package com.ls.sdk.http;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.v4.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.ls.sdk.R;
import com.ls.sdk.utils.LogUtils;
import com.ls.sdk.utils.ToastUtils;

/**
 * 异步数据请求网络框架Volley
 */
public class AnsynHttpRequest {
	public static final int POST = 1; // post 提交
	public static final int GET = 2; // get 提交
	public LogUtils logUtils = LogUtils.jLog();

	/***
	 * get和post请求方法
	 * 
	 * @param sendType
	 *            请求类型：get和post AnsynHttpRequest.GET
	 * @param context
	 *            上下文
	 * @param url
	 *            请求地址
	 * @param map
	 *            post使用到的
	 * @param callBack
	 *            异步回调
	 * @param mQueue
	 *            volly最终请求类
	 * @param i
	 *            请求的方法对应的int值（整个项目中唯一不重复的）
	 * @param obj
	 *            此参数用于一些http请求结果回调里,需要请求前传递一些参数的方法.不需要时,可以传空,不处理即可.
	 */
	public static void requestGetOrPost(final int sendType,
			final Context context, String url, final Map<String, String> map,
			final ObserverCallBack callBack, RequestQueue mQueue, final int i,
			final Object obj) {
		url = Utf8URLencode(url);
		switch (sendType) {
		case POST:
			StringRequest stringRequest = new StringRequest(Method.POST, url,
					new Response.Listener<String>() {// 成功回调
						@Override
						public void onResponse(String response) {
							callBack.back(response, HttpStaticApi.FAILURE_HTTP,
									i, obj);
						}
					}, new Response.ErrorListener() { // 请求失败
						@Override
						public void onErrorResponse(VolleyError error) {
							ToastUtils.showShort(context, "请检查网络连接");
							callBack.back(null, HttpStaticApi.FAILURE_HTTP, i,
									obj);
						}
					}) {
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					return map;
				}

				@Override
				protected Response<String> parseNetworkResponse(
						NetworkResponse response) {
					try {

						String jsonString = new String(response.data, "UTF-8");
						return Response.success(jsonString,
								HttpHeaderParser.parseCacheHeaders(response));
					} catch (UnsupportedEncodingException e) {
						return Response.error(new ParseError(e));
					} catch (Exception je) {
						return Response.error(new ParseError(je));
					}
				}
			};

			mQueue.add(stringRequest);
			break;
		case GET:
			Response.Listener<String> listener = new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					callBack.back(response, HttpStaticApi.SUCCESS_HTTP, i, obj);
				}
			};
			Response.ErrorListener errorListener = new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					ToastUtils.showShort(context, "请检查网络连接");
					callBack.back(null, HttpStaticApi.FAILURE_HTTP, i, obj);
				}
			};
			StringRequest stringRequest2 = new StringRequest(url, listener,
					errorListener) {
				@Override
				protected Response<String> parseNetworkResponse(
						NetworkResponse response) {
					try {

						String jsonString = new String(response.data, "UTF-8");
						return Response.success(jsonString,
								HttpHeaderParser.parseCacheHeaders(response));
					} catch (UnsupportedEncodingException e) {
						return Response.error(new ParseError(e));
					} catch (Exception je) {
						return Response.error(new ParseError(je));
					}
				}
			};
			mQueue.add(stringRequest2);
			break;
		default:
			break;
		}

	}

	/**
	 * Utf8URL编码
	 * 
	 * @param s
	 * @return
	 */
	public static String Utf8URLencode(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c >= 0 && c <= 255) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}

	static PopupWindow winDialog;
	private static RequestQueue mQueue;

	private static void showWinDialog(final Context context) {
		final View view = LayoutInflater.from(context).inflate(
				R.layout.main_weixin_top_rights, null);
		winDialog = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		TextView btn_game_win_check_price = (TextView) view
				.findViewById(R.id.btn_game_win_check_price);// 确定
		TextView btn_game_win_share = (TextView) view
				.findViewById(R.id.btn_game_win_share);// 取消

		TextView textView2 = (TextView) view.findViewById(R.id.textView2);
		textView2.setText("您的账户在其他地方登录\n是否重新进行登录?");

		btn_game_win_check_price.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		});
		btn_game_win_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		winDialog.setOutsideTouchable(true);
		winDialog.setFocusable(true);
		winDialog.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					winDialog.dismiss();
					return true;
				}
				return false;
			}
		});
		// // 此行代码可以在返回键按下的时候,使CancelDialog消失.
		// winDialog.setBackgroundDrawable(new BitmapDrawable());
		try {
			winDialog.showAtLocation(((Activity) context).getWindow()
					.getDecorView(), Gravity.CENTER, 0, 0);
		} catch (Exception e) {
		}
	}

	public static void readBitmapViaVolley(String imgUrl,
			final ImageView imageView, RequestQueue mQueue) {
		ImageRequest imgRequest = new ImageRequest(imgUrl,
				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap arg0) {
						imageView.setBackgroundResource(0);
						imageView.setImageBitmap(arg0);
					}
				}, 300, 200, Config.ARGB_8888, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {

					}
				});
		mQueue.add(imgRequest);
	}

	/**
	 * 请求网络图片，可以吧这个队列变成application
	 * 
	 * @param imgUrl
	 * @param imageView
	 * @param context
	 */
	public static void readBitmapVolley(String imgUrl,
			final ImageView imageView, Context context) {
		ImageLoader mImageLoader = new ImageLoader(getRequestQueue(context),
				new BitmapCache());
		ImageListener listener = ImageLoader.getImageListener(imageView,
				R.drawable.pictureloading, R.drawable.pictureloading);
		mImageLoader.get(imgUrl, listener, 0, 0);
	}

	public static class BitmapCache implements ImageCache {
		private LruCache<String, Bitmap> mCache;

		public BitmapCache() {
			int maxSize = 10 * 1024 * 1024;
			mCache = new LruCache<String, Bitmap>(maxSize) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getRowBytes() * value.getHeight();
				}

			};
		}

		@Override
		public Bitmap getBitmap(String url) {
			return mCache.get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			mCache.put(url, bitmap);
		}

	}

	public static RequestQueue getRequestQueue(Context c) {
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(c);
		}
		return mQueue;
	}

}
