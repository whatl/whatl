package com.ls.sdk.utils;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;

/**
 * Activity的工具类
 * 
 * @author ls
 * 
 */
public class ActivityUtils {
	/**
	 * 开启一个activiry后关闭
	 * 
	 * @param activity
	 * @param cls
	 */
	public static void startActivityAndFinish(Activity activity, Class<?> cls) {
		Intent intent = new Intent(activity, cls);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 开启一个activity
	 * 
	 * @param activity
	 * @param cls
	 */
	public static void startActivity(Activity activity, Class<?> cls) {
		Intent intent = new Intent(activity, cls);
		activity.startActivity(intent);
	}

	/**
	 * 开启一个activity携带一个int参数
	 * 
	 * @param activity
	 * @param cls
	 * @param data
	 */
	public static void startActivityForIntData(Activity activity, Class<?> cls,
			int data) {
		Intent intent = new Intent(activity, cls);
		intent.putExtra("data", data);
		activity.startActivity(intent);
	}

	/**
	 * 开启activity并携带一个字符串
	 * 
	 * @param activity
	 * @param cls
	 * @param data
	 */
	public static void startActivityForStringData(Activity activity,
			Class<?> cls, String data) {
		Intent intent = new Intent(activity, cls);
		intent.putExtra("data", data);
		activity.startActivity(intent);
	}

	/**
	 * 开启activity并返回一个结果
	 * 
	 * @param activity
	 * @param cls
	 * @param data
	 *            需要携带的数据
	 * @param flag
	 */
	public static void startActivityForResult(Activity activity, Class<?> cls,
			String data, int flag) {
		Intent intent = new Intent(activity, cls);
		intent.putExtra("data", data);
		intent.setFlags(flag);
		activity.startActivityForResult(intent, flag);
	}

	/**
	 * 开启一个activity携带一组集合
	 * 
	 * @param activity
	 * @param cls
	 * @param data
	 */
	public static void startActivityForSerializable(Activity activity,
			Class<?> cls, Serializable data) {
		Intent intent = new Intent(activity, cls);
		intent.putExtra("Serializable", data);
		activity.startActivity(intent);
	}
}
