package com.ls.sdk.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 自己手动设置照相的布局
 * 需要照相的权限
 * @author ls
 * 
 */
public class CameraUsering {
	private Camera cam;

	// 检查照相机是否存在
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else { // no camera on this device
			return false;
		}
	}

	// 获取照相机对象
	private static Camera getCameraInstance(int position) {
		Camera c = null;
		try {
			// 如果open中写1代表前置摄像头,0代表后置
			c = Camera.open(position); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c;
	}

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param view
	 *            要显示的View
	 * @param position
	 *            调用系统那个摄像头,0是后置，1代表前置
	 * @return 照相机实例
	 */
	public Camera getInstance(Context context, ViewGroup view, int position) {
		if (context == null || view == null) {
			return null;
		}
		if (position != 0 && position != 1) {
			position = 0;
		}
		if (checkCameraHardware(context)) {
			cam = getCameraInstance(position);
			if (cam != null) {
				CameraPreview c = new CameraPreview(context, cam);
				view.addView(c);
			} else {
				Toast.makeText(context, "摄像头不可用", 0).show();
			}
		} else {
			Toast.makeText(context, "照相机不存在", 0).show();
		}
		return cam;
	}
}
