package com.ls.sdk.utils;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.ls.sdk.bean.ContactInfo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * 联系人的工具类
 * @author Administrator
 *
 */
public class ContactUtils {
	/**
	 * 获取系统里面全部的联系人信息
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static List<ContactInfo> getAllContacts(Context context) {
		List<ContactInfo> contanctInfos = new ArrayList<ContactInfo>();
		// 1. 查询raw_contacts 把 联系人的 id获取出来
		ContentResolver resolver = context.getContentResolver();
		// 获取raw_contacts表的uri
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		// 获取data表的uri
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("contact_id"));
			System.out.println("联系人的id：" + id);
			if (id != null) {
				ContactInfo contactInfo = new ContactInfo();
				// 2.根据id查询data表 获取联系人的数据。
				Cursor datacursor = resolver.query(datauri, null,
						"raw_contact_id=?", new String[] { id }, null);
				while (datacursor.moveToNext()) {
					String data1 = datacursor.getString(datacursor
							.getColumnIndex("data1"));
					String mimetype = datacursor.getString(datacursor
							.getColumnIndex("mimetype"));
					if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
						contactInfo.setPhone(data1);
					} else if ("vnd.android.cursor.item/email_v2"
							.equals(mimetype)) {
						contactInfo.setEmail(data1);
					} else if ("vnd.android.cursor.item/name".equals(mimetype)) {
						contactInfo.setName(data1);
					}
				}
				datacursor.close();
				contanctInfos.add(contactInfo);
			}
		}
		cursor.close();
		return contanctInfos;
	}
	/**
	 * 添加联系人到数据库
	 * @return
	 */
	  public boolean  putContactPeople(){
		  
		  return false;
	  }
}
