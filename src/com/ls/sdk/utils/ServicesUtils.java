package com.ls.sdk.utils;

import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * 服务的工具类
 * @author ls
 *
 */
public class ServicesUtils {
	/**
	 * 服务是否在运行
	 * @param con
	 * @param classname全路径服务类名
	 * @return
	 */
	public static boolean isRuningServices(Context con,String classname){
		ActivityManager  act = (ActivityManager) con.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> run = act.getRunningServices(200);//200随便写，代表这个集合只能放200个服务
		for (RunningServiceInfo runServices : run) {
			String className = runServices.service.getClassName();
			if(classname.equalsIgnoreCase((className))){
				return true;
			}
		}
		return false;
	}
}
