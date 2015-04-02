package com.ls.sdk.http;

/**
 * 
 * 回调的标示
 * @author ls
 *
 */
public class HttpStaticApi {
	/**成功从服务端获取到数据*/
	public static final int SUCCESS_HTTP = 20000;
	/**没有成功获取到想要的数据，可能原因是网络没连接上，或则是访问服务器成功，但服务器返回数据不是你想要的*/
	public static final int FAILURE_HTTP = 	50000;
}
