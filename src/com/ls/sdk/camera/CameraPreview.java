package com.ls.sdk.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 * 创建了一个预览类
 * 是用来显示照相
 * @author Administrator
 *
 */
class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	//用来打印log
	private static final String TAG = "CameraPreview";
	//显示容器
	private SurfaceHolder mHolder;
	//照相机对象
	private Camera mCamera;
	//构造函数传进上下文和照相设备
	public CameraPreview(Context context, Camera camera) {
		super(context);
		System.out.println("3--构造");
		mCamera = camera;
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		//得到容器
		mHolder = getHolder();
		System.out.println("4--得到照相机的holder容器");
		//当容器创建好的时候就会掉
		mHolder.addCallback(this);
		System.out.println("5--当4ok时创建的回调");
		// deprecated setting, but required on Android versions prior to 3.0
		//设置这个加载的反冲
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		System.out.println("6--把资源反冲竟来");
	}
	//当容器创建好的时候调用的方法
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			//设置设备显示的地方
			System.out.println("7--回调中调用创建好的方法");
			mCamera.setPreviewDisplay(holder);
			//开启设备预览
			System.out.println("8--把预览显示在容器中");
			mCamera.startPreview();
			System.out.println("9--开启预览界面");
		} catch (IOException e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}
	//当容器死亡最小化的时候调用的方法
	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		System.out.println("9--容器最小化的时候或者死亡调用的方法");
	}
	//当容器改变大小的时候调用的方法
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			System.out.println("10--容器改变的时候");
			return;
		}
		// stop preview before making changes
		try {
			mCamera.stopPreview();
			System.out.println("11--容器改变的时候停止了容器的预览");
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
		// set preview size and make any resize, rotate or
		// reformatting changes here
		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			System.out.println("12--重新显示预览");
			mCamera.startPreview();
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}
}
