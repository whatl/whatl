package com.ls.sdk.http;
/**
 * 
 * 访问网络后的回调接口
 * @author ls
 *
 */
public interface ObserverCallBack {
	/**
	 * 
	 * @param data 服务端返回的资源
	 * @param encoding 访问服务器的状态是否成功（判断是否访问成功）
	 * @param method 来自哪个url接口的标示（区分，唯一）
	 * @param obj 是你请求接口时携带的数据可能为null
	 */
	public void back(String data, int encoding, int method, Object obj);
}