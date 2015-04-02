package com.ls.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * * 获得屏幕相关的辅助类,截屏的时候有的时候会报空指针的原因是如果以你上来就直接在oncreat（）中就截屏他就会报空指针
 * ，可能是没有加载完成你就开始截屏了
 * 
 * @author ls
 * 
 */
public class ScreenUtils {
	private ScreenUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 获得屏幕款度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {

		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}

	/**
	 * 获取当前屏幕截图，包含状态栏
	 * 如果要保存图片可以参考GrapUtils工具类
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		//下面三句代码可以拿到控件的形状
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;

	}

	/***
	 * 判断当前是横屏还是竖屏，如果是竖屏返回true
	 * 
	 * @param con
	 */
	public boolean isLandscape(Context con) {
		int orientation = con.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			return false;
		}
		return false;
	}

	/**
	 * 窗口背景变暗的效果0.0f-1.0f，透明度1.0完全不透明
	 * 
	 * @param activity
	 *            float f
	 */
	public void setScreenAlpha(Activity activity, float f) {
		// 产生背景变暗效果
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = f;
		activity.getWindow().setAttributes(lp);
	}
	
	/**
	 *  算出按下的时候和抬起来的时候偏差 ，如果为0.0代表毫无偏差，如果上了double返回了30以上就有偏差了
	 *  0-30可以当做按下时候的点和抬起来的时候点没有多大偏差，可以接受
	 * @param rawX 按下屏幕的x点
	 * @param rawY 按下屏幕的Y点
	 * @param uprawX  抬起来的x点
	 * @param uprawY 抬起来的Y点
	 * @return
	 */
	private double pointDistance(float rawX, float rawY, float uprawX, float uprawY) {
		double sqrt = Math.sqrt(Math.abs((rawX - uprawX) * (rawX - uprawX)
				+ Math.abs((rawY - uprawY) * (rawY - uprawY))));
		return sqrt;
	}

}