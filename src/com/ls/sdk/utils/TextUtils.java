package com.ls.sdk.utils;

import android.R.integer;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

/**
 * TextView的工具类
 * 
 * @author ls
 * 
 */
public class TextUtils {
	/**
	 * 设置TextView的左边的图标
	 * 
	 * @param activity
	 * @param id
	 *            图片的id
	 * @param tv
	 *            textView
	 */
	public void setTextViewLeftIcon(Activity activity, int id, TextView tv) {
		Drawable img_off;
		Resources res = activity.getResources();
		img_off = res.getDrawable(id);// 女孩子图标
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		img_off.setBounds(0, 0, img_off.getMinimumWidth(),
				img_off.getMinimumHeight());
		tv.setCompoundDrawables(img_off, null, null, null);
	}

	/**
	 * 设置右边的图标
	 * 
	 * @param activity
	 * @param id
	 * @param tv
	 */
	public void setTextViewRightIcon(Activity activity, int id, TextView tv) {
		Drawable img_off;
		Resources res = activity.getResources();
		img_off = res.getDrawable(id);// 女孩子图标
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		img_off.setBounds(0, 0, img_off.getMinimumWidth(),
				img_off.getMinimumHeight());
		tv.setCompoundDrawables(null, img_off, null, null); // 设置左图标
	}
}
