package com.ls.sdk.utils;

import java.io.File;
import java.util.Date;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 安卓意图的工具类
 * 
 * @author ls
 * 
 */
public class IntentUtils {
	static Intent intent = null;

	/**
	 * 开启一个打开安卓系统图片的意图，如果要缩放可以参考GraphUtils工具类
	 */
	public static void startIntent2Photo(Activity activity, int requestCode) {
		intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intent, requestCode);
	}

	/***
	 * 开启一个照相机的意图
	 */
	public static void startIntent2TakePhoto(Activity activity, int requestCode) {
		intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri(activity));
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 获取一个保存图片的uri
	 * 
	 * @param activity
	 * @return uri
	 */
	public static Uri setImageUri(Activity activity) {
		File file = new File(AplicationUtils.getMuLu(activity) + "/DCIM/",
				"image" + new Date().getTime() + ".png");
		Uri imgUri = Uri.fromFile(file);
		return imgUri;
	}
	/**
	 * 开启一个超级管理员的意图
	 * @param activity
	 * @param admin 继承了DeviceAdminReceiver 
	 */
	public static void startIntent2Admin(Activity activity,Class admin){
		ComponentName  sp = new ComponentName(activity, admin);
		 DevicePolicyManager dev = (DevicePolicyManager) activity.getSystemService(activity.DEVICE_POLICY_SERVICE);
		   if (dev.isAdminActive(sp)) {
		   dev.lockNow();
		   }
		   intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		  intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, sp); 
		  intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "不开启扣500");
		  activity.startActivity(intent);
	}
	
}
