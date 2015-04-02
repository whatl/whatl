package com.ls.sdk.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

/**
 * 
 * @author ls
 * 
 */
public class AppInfoUtil {
	public static final String SDCARD_SOFT_STORE =Environment.getExternalStorageDirectory()+"/htjxsdk/";
	public static final File SDCARD =Environment.getExternalStorageDirectory();
	private static ProgressDialog mProgress = null;

	private static DisplayMetrics displayMetrics = new DisplayMetrics();
	private static int id;
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * 返回一个md5
	 * 
	 */
	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * apk是否已经安装
	 * @return true
	 */
	public static boolean isAppInstall(Context context, String packname) {
		PackageManager manager = context.getPackageManager();
		@SuppressWarnings("rawtypes")
		List pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = (PackageInfo) pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase(packname)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 从资源文件中获取名字让后写在本地
	 * 当写成功后返回true
	 * 
	 */
	public static boolean retrieveApkFromAssets(Context context,
			String fileName, String path) {
		boolean bRet = false;
		try {
			InputStream is = context.getAssets().open(fileName);

			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRet;
	}

	/**
	 * 返回apk信息 
	 * @param context
	 * @param archiveFilePath
	 * @return
	 */
	public static PackageInfo getApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath, 128);
		return apkInfo;
	}

	/**
	 * 获取电话的imsi
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String imsi = sp.getString("imsi", "");
		if (imsi == "") {
			TelephonyManager phone = (TelephonyManager) context
					.getSystemService("phone");
			imsi = phone.getSubscriberId();
			if (imsi != null && imsi != "") {
				Editor edit = sp.edit();
				edit.putString("imsi", imsi);
				edit.commit();
			} else {
				return "";
			}
		}
		return imsi;
	}
	/**
	 * imei锟斤拷mac锟斤拷址锟斤拷md5值
	 * @param context
	 * @return imei锟斤拷mac锟斤拷址锟斤拷md5值
	 */
	public static String getXid(Context context) {
		String xid = "";
		xid = md5(getIMEI(context) + getMacAddress(context));
		return xid;

	}
	/**
	 * 锟斤拷取锟斤拷锟斤拷
	 * @param context
	 * @return 锟斤拷取锟斤拷锟斤拷
	 */
	public static String getPkName(Context context) {
		String xid = "";
		xid = md5(getIMEI(context) + getMacAddress(context));
		return xid;

	}
	/**
	 * 获取当前的版本1 ，2 ，3依次叠加代表更新多少次了
	 * @param context
	 * @return 锟斤拷取锟芥本锟斤拷
	 */
	public static int getVersionCode(Context context) {

		PackageInfo packageInfo=null;
		try {
			packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
		
		
	}
	/**
	 * 获取当前的版本号比如1.02
	 * @param context
	 * @return 锟斤拷取锟芥本锟斤拷
	 */
	public static String getVersionName(Context context) {
		PackageInfo packageInfo=null;
		try {
			packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	/**
	 * 锟斤拷取Sim锟斤拷状态
	 * @param context 锟斤拷锟斤拷锟斤拷
	 * @return 锟斤拷取Sim锟斤拷状态锟斤拷
	 */
	public static int getSimState(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimState();
	}

	/**
	 * 得到手机名字
	 * @param context 锟斤拷锟斤拷锟斤拷
	 * @return 锟斤拷取锟界话锟斤拷锟斤拷
	 * @throws Exception
	 */
	public static String getPhoneNumber(Context context) throws Exception {
		String phoneNumber = "";
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			phoneNumber = tm.getLine1Number();
		} catch (Exception e) {
			throw new RuntimeException(
					"android.permission.READ_PHONE_STATE should be add to AndroidManifest.xml!");
		}
		return phoneNumber == null ? "" : phoneNumber;
	}

	/**
	 * 获取本机mac地址，需要权限<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />  
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String macAddr = sp.getString("mac", "");
		if (macAddr == "") {
			WifiManager wifi = (WifiManager) context.getSystemService("wifi");
			try {
				WifiInfo wifiInfo = wifi.getConnectionInfo();
				macAddr = wifiInfo.getMacAddress();
				if (macAddr != null && macAddr != "") {
					Editor edit = sp.edit();
					edit.putString("mac", macAddr);
					edit.commit();
				} else {
					return "";
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return macAddr;
	}
	/**
	 * 展示一个进度条
	 * @param context
	 * @param title
	 * @param message
	 * @param indeterminate
	 * @param cancelable
	 * @return
	 */
	public static ProgressDialog showProgress(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(false);
		dialog.setOnCancelListener(new OnCancelListener((Activity) context));
		dialog.show();
		mProgress = dialog;
		return dialog;
	}
	/**
	 * 关闭一个进度条
	 */
	public static void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 展示一个dialog
	 * @param context
	 * @param strTitle
	 * @param strText
	 * @param icon
	 * @param text
	 */
	public static void showDialog(Activity context, String strTitle,
			String strText, int icon, String text) {
		Builder tDialog = new Builder(context);
		tDialog.setIcon(icon);
		tDialog.setTitle(strTitle);
		tDialog.setMessage(strText);
		tDialog.setPositiveButton(text, null);
		tDialog.show();
	}

	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把一个流转换成字符串
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null)
				sb.append(line);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				is.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 更改一个文件的最后时间
	 * @param dir
	 * @param fileName
	 */
	public static void updateFileTime(String dir, String fileName) {
		File file = new File(dir, fileName);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}

	/**
	 * 获取文件的最后修改时间
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public static long getFileTime(String dir, String fileName) {
		File file = new File(dir, fileName);
		return file.lastModified();
	}

	static class OnCancelListener implements DialogInterface.OnCancelListener {
		Activity mcontext;

		OnCancelListener(Activity context) {
			this.mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			this.mcontext.onKeyDown(4, null);
		}
	}

	/**
	 * SD卡是否存在
	 * @return
	 */
	public static boolean isExistSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 获取屏幕宽度
	 * @param activity
	 * @return
	 */
	public static int getScreenWidth(Activity activity) {
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * @param activity
	 * @return
	 */
	public static int getScreenHeight(Activity activity) {
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	/**
	 * 获取手机的imei
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String imei = sp.getString("imei", "");
		if (imei == "") {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
			if (imei != null && imei != "") {
				Editor edit = sp.edit();
				edit.putString("imei", imei);
				edit.commit();
			} else {
				return "";
			}
		}

		return imei;
	}


	/**
	 * 开启一个通知
	 * @param context
	 * @param contentTitle
	 * @param contentText
	 * @param icon
	 * @param pkName
	 * @param className
	 */
	public static void stratNotification(Context context, String contentTitle,
			String contentText, int icon, String pkName,
			String className) {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence tickerText = contentTitle; 
		long when = System.currentTimeMillis(); 
		Notification notification = new Notification(icon, tickerText, when);
		Intent intent = new Intent(); 
		if(pkName==null){
			intent.setClassName(AppInfoUtil.getPkName(context),AppInfoUtil.getPkName(context)+".MainActivity.class");
		}else{
			intent.setClassName(pkName, className);
		}
		
		PendingIntent pedningIntent = PendingIntent.getActivity(context, 100,
				intent, PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				pedningIntent); 
		notification.flags = Notification.FLAG_AUTO_CANCEL; 
		// notification.sound = Uri.parse("file:///mnt/sdcard/download.mp3"); //
		manager.notify(id++, notification);
	}
	/**
	 *让一个数字变成mb
	 * @param context
	 * @param size
	 * @return
	 */
	public static String formatFileSize(Context context,long size) {
		return Formatter.formatFileSize(context, size);
	}

}
