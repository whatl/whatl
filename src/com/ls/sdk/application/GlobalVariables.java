package com.ls.sdk.application;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 全局变量
 */
public class GlobalVariables extends Application {

	public static RequestQueue mQueue;
	private String tag = "MyApplication";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public static RequestQueue getRequestQueue(Context c) {
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(c);
		}
		return mQueue;
	}
}
