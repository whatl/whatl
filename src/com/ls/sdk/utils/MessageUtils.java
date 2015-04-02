package com.ls.sdk.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * 这个是短信的工具类
 * 
 * @author ls
 */
public class MessageUtils {
	public void putMessage(Context context, int phone, String body) {
		// 1.得到内容解析器。
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		ContentValues values = new ContentValues();
		values.put("address", phone + "");
		values.put("type", 1);// 1 代表接受的短信 2代表的是发送的短信
		values.put("date", System.currentTimeMillis());
		values.put("body", body);
		resolver.insert(uri, values);
	}

	public void sendMessage() {

	}

	public void receiverMessage() {

	}

	public void readMessage(Context context) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String body = cursor.getString(cursor.getColumnIndex("body"));
			String date = cursor.getString(cursor.getColumnIndex("date"));
			String type = cursor.getString(cursor.getColumnIndex("type"));
			System.out.println(address + ":" + body + ":" + date + ":" + type);
		}
		cursor.close();
	}
}
