package com.ls.sdk.download;

import java.io.File;

import com.ls.sdk.utils.LogUtils;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/***
 * 利用谷歌自带的下载作为工具类
 * 
 * 需要权限： <permission android:name="android.permission.ACCESS_DOWNLOAD_MANAGER"/>
 * 网络权限，外置存储写入权限 通知栏显示权限 <uses-permission
 * android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" /> 支持最低版本 9
 * 
 * @author ls
 * 
 */
public class MDownLoadManager {
	Context context;
	private DownloadManager downLoadManager;
	/**
	 * 下载文件的类
	 * @param context
	 * @param url 请求下载的路径
	 * @param flags 
	 * @param saveFile 保存文件的地址
	 * @param visibily 是否要显示通知栏
	 */
	private void downLoadFile(Context context, String url,boolean visibily,
			File saveFile) {
		File file = Environment.getExternalStorageDirectory();
		if(file==null){
			Log.i("MDownLoadManager","外部存储不存在");
		}
		this.context = context;
		downLoadManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(url);
		DownloadManager.Request request = new Request(uri);
		// 设置文件的保存地址
		request.setDestinationUri(Uri.fromFile(saveFile));
		/**
		 * 如果下载的这个文件是你的应用所专用的，你可能会希望把这个文件放在你的应用在外部存储中的一个专有文件夹中。注意这个文件夹不提供访问控制，
		 * 所以其他的应用也可以访问这个文件夹。在这种情况下，如果你的应用卸载了，那么在这个文件夹也会被删除。
		 * 下面的代码片段是指定存储文件的路径是应用在外部存储中的专用文件夹的方法：， 下载文件存放地值
		 */
		request.setDestinationInExternalFilesDir(context,
				Environment.DIRECTORY_DOWNLOADS, getPathEndFileName(url,"\\"));
		// 设置是否需要显示notifaction
		request.setShowRunningNotification(visibily);
		// 通知栏的显示文本，也可以注册一个通知栏下载完成后的广播事件
		request.setTitle("下载");
		request.setDescription("正在下载");
		// 设置是否显示下载界面
		request.setVisibleInDownloadsUi(false);
		/**
		 * 自定义nofitaction
		 * 比如标题，本来默认的就是下载的文件名字，默认也米描述，你可以这样： request.setTitle(“Earthquakes”);
		 * 
		 * request.setDescription(“Earthquake XML”);
		 * 
		 * 你还可以设置Notification的可见性使用setNotificationVisibility,下面列出几个可用标识
		 * 
		 * 1.Request.VISIBILITY_VISIBLE
		 * 
		 * 这个是默认的参数，下载期间会保持可见性，下载完成的时候自动消失。
		 * 
		 * 2.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
		 * 
		 * 基于1，但是下载完成后Notification不会消失，除非你点击了它。
		 * 
		 * 3.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION
		 * 
		 * 只在下载完成的时候触发Notification.
		 * 
		 * 4.Request.VISIBILITY_HIDDEN
		 * 
		 * 隐藏Notification.
		 */
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

		/***
		 * 如果下载的文件希望被其他的应用共享，特别是那些你下载下来希望被Media
		 * Scanner扫描到的文件（比如音乐文件），那么你可以指定你的下载路径在
		 * 外部存储的公共文件夹之下，下面的代码片段是将文件存放到外部存储中的公共音乐文件夹的方法：
		 */
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC,
				"Android_Rock.mp3");
		request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
		/**
		 * 在默认的情况下，通过Download Manager下载的文件是不能被Media
		 * Scanner扫描到的，进而这些下载的文件（音乐、视频等）就不会在Gallery和
		 */
		request.allowScanningByMediaScanner();
		// 希望下载的文件可以被系统的Downloads应用扫描到并管理
		request.setVisibleInDownloadsUi(true);
		// reference变量是系统为当前的下载请求分配的一个唯一的ID,只要网络可用就会执行下载
		IntentFilter filter = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		// 注册一个下载完成的广播
		context.registerReceiver(receiver, filter);
		/**
		 * 注册一个点击的广播
		 */
		IntentFilter filterCLcik = new IntentFilter(
				DownloadManager.ACTION_NOTIFICATION_CLICKED);
		context.registerReceiver(receiverCLick, filterCLcik);
		long reference = downLoadManager.enqueue(request);
		Log.i("MDownLoadManager", "lgs=执行队列的id=" + reference);
	}

	/**
	 * 下载完成的广播
	 */
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long reference = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			Log.i("MDownLoadManager", "lgs=下载的id=" + reference);
		}
	};
	/**
	 * 下载完成的广播
	 */
	BroadcastReceiver receiverCLick = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long reference = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			Log.i("MDownLoadManager", "lgs=下载的id=" + reference);
		}
	};

	/**
	 * 取消正在下载或则删除已下载的文件
	 * 
	 * @param 文件的
	 *            id
	 * @return 移除的个数
	 */
	public int removeDownLoad(long... id) {
		int remove = 0;
		if (downLoadManager != null) {
			remove = downLoadManager.remove(id);
		}
		return remove;
	}

	/**
	 * 查看下载的结果
	 * 
	 * @param cotnext
	 */
	public void seeDownLoading(Context cotnext) {
		Intent i = new Intent();
		i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
		cotnext.startActivity(i);
	}
	/**
	 * 把name中的末尾含有endText这个文本后面的字符串截取出来
	 * @param name
	 * @param endText
	 * @return 返回null或则“”代表没有截取到，否则返回endtext后的文字
	 * example; http:\\www.baidu.com\indext.java
	 * 			endText=\,返回
	 */
	public  String  getPathEndFileName(String name,String endText){
		if(TextUtils.isEmpty(name)||TextUtils.isEmpty(endText)){
			return null;
		}
		if(name.endsWith(endText)){
			return "";
		}
		int lastIndexOf = name.lastIndexOf(endText);
		if(lastIndexOf!=-1){
			//返回截取后的名字
			String newsName = name.substring(lastIndexOf+endText.length());
			return newsName;
		}
		return null;
	}
}
