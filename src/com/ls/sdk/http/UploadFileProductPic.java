package com.ls.sdk.http;


import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.CharArrayBuffer;

import android.util.Log;

public class UploadFileProductPic {

	private static int CONNECTION_TIME_OUT = 30 * 1000;
	private static int SOCKET_TIME_OUT = 30 * 1000;
	private static final String UPLOAD_FILE_TAG = "upload_file_info";

	private OnUploadFileForResultListener listener;
	private int logoimage;

	public void setTimeOut(int CTO, int STO) {
		CONNECTION_TIME_OUT = CTO;
		SOCKET_TIME_OUT = STO;
	}

	/**
	 * 上传图片
	 * 
	 * @param url
	 * @param entity
	 */
	public void uploadImg(final String url, final MultipartEntity entity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				upload(url, entity);
			}
		}).start();
	}

	private void upload(String url, MultipartEntity entity) {
		final DefaultHttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(),
				CONNECTION_TIME_OUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), SOCKET_TIME_OUT);
		try {
			URI uri = null;
			uri = new URI(url);
			HttpPost request = new HttpPost(uri);
			request.setEntity(entity);
			final HttpResponse response = client.execute(request);
			final StatusLine status = response.getStatusLine();
			final int statusCode = status.getStatusCode();
			HttpEntity contentEntity = response.getEntity();
			if (statusCode == HttpStatus.SC_OK) {
				int i = (int) contentEntity.getContentLength();
				if (i < 0) {
					i = 4096;
				}
				final Reader reader = new InputStreamReader(
						contentEntity.getContent());
				final CharArrayBuffer buffer = new CharArrayBuffer(i);
				final char[] tmp = new char[1024];
				int l;
				while ((l = reader.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
				doWorkResult(buffer.toString(), true);
			} else {
				doWorkResult(null, false);
			}
		} catch (Exception e) {
			doWorkResult(null, false);
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	private void doWorkResult(String result, boolean isUploadSuccess) {

		if (listener != null) {
			listener.onResultListener(isUploadSuccess, result,logoimage);
		}
	}

	public interface OnUploadFileForResultListener {
		public abstract void onResultListener(boolean isUploadSuccess,
				String message,int i);
	}

	public void setListener(OnUploadFileForResultListener resultListener,int i) {
		listener = resultListener;
		this.logoimage=i;
	}

	public void removeListener() {
		listener = null;
		logoimage=0;
	}

}
