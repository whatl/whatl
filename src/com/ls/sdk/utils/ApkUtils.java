package com.ls.sdk.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * apk
 * 
 * @author ls
 * 
 */
public class ApkUtils {
	/**
	 * 安装一个apk
	 * @param context
	 * @param path
	 */
	public static void InstallApk(Context context, String path) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 卸载一个apk
	 * @param context
	 * @param pkName
	 */
	public static void UnInstallApk(Context context, String pkName) {
		Uri packageURI = Uri.parse(pkName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		context.startActivity(uninstallIntent);
	}

	/**
	 * 启动一个已安装apk
	 * @param context
	 * @param pkName
	 */
	public static void OpenApp(Context context, String pkName) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(
				pkName);
		context.startActivity(intent);
	}


	/**
	 * 获取到apk的包名
	 * @param context
	 * @param appPath
	 * @return
	 */
	public static String getPageNameByApk(Context context, String appPath) {
		if (appPath != null) {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(appPath,
					PackageManager.GET_ACTIVITIES);
			ApplicationInfo appInfo = info.applicationInfo;
			String packageName = appInfo.packageName;
			// String version=info.versionName; 
			// Drawable icon = pm.getApplicationIcon(appInfo);
			return packageName;
		} else {
			return null;
		}
	}

	/**
	 * 变成一个系统的app
	 * @param context
	 * @param name
	 */
	public static void toSystemApp(Context context, String name) {
		String packageName = context.getPackageName();
		String[] commands = {
				"busybox mount -o remount,rw /system",
				"busybox cp /data/data/" + packageName + "/files/" + name
						+ " /system/app/" + name,
				"busybox rm /data/data/" + packageName + "/files/" + name };
		Process process = null;
		DataOutputStream dataOutputStream = null;
		try {
			process = Runtime.getRuntime().exec("su");
			dataOutputStream = new DataOutputStream(process.getOutputStream());
			int length = commands.length;
			for (int i = 0; i < length; i++) {
				dataOutputStream.writeBytes(commands[i] + "\n");
			}
			dataOutputStream.writeBytes("exit\n");
			dataOutputStream.flush();
			process.waitFor();
		} catch (Exception e) {
		} finally {
			try {
				if (dataOutputStream != null) {
					dataOutputStream.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 变成一个数据的app
	 * @param context
	 * @param name
	 */
	public static void toDataApp(Context context, String name) {
		String packageName = context.getPackageName();
		String[] commands = {
				"busybox mount -o remount,rw /system",
				"busybox cp /data/data/" + packageName + "/files/" + name
						+ " /data/app/" + name,
				"busybox rm /data/data/" + packageName + "/files/" + name };
		Process process = null;
		DataOutputStream dataOutputStream = null;
		try {
			process = Runtime.getRuntime().exec("su");
			dataOutputStream = new DataOutputStream(process.getOutputStream());
			int length = commands.length;
			for (int i = 0; i < length; i++) {
				dataOutputStream.writeBytes(commands[i] + "\n");
			}
			dataOutputStream.writeBytes("exit\n");
			dataOutputStream.flush();
			process.waitFor();
		} catch (Exception e) {
		} finally {
			try {
				if (dataOutputStream != null) {
					dataOutputStream.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
	}
}
