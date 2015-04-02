package com.ls.sdk.utils;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * 字符串的工具类
 * 
 * @author ls
 * 
 */
public class StringUtils {
	/**
	 * 把一个字符串编码
	 * 
	 * @param path
	 * @return
	 */
	public static String getUrlEncodePath(String path) {
		try {
			String substring1 = path.substring(0, path.lastIndexOf("/") + 1);
			String substring = path.substring(path.lastIndexOf("/") + 1);
			path = substring1 + URLEncoder.encode(substring, "utf-8");
			return path;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 校验是否是数字,英文字母和中文
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isValidTagAndAlias(String s) {
		Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
		Matcher m = p.matcher(s);
		return m.matches();
	}

	/**
	 * 传进来一个字符串名字，截取文件的最后一位小数点后面的内容
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileType(String path) {
		try {
			String substring = path.substring(path.lastIndexOf("."));
			return substring;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// get the sub string for large string
	public static String getSubString(final String str, int start, int end) {
		return new String(str.substring(start, end));
	}

	/**
	 * 从字符串的0角标开始截取直达字符串最后一位小数点
	 * 
	 * @param name
	 * @return
	 */
	public static String getFileNamesss(String name) {
		try {
			if (name.lastIndexOf(".") != -1) {
				String substring = name.substring(0, name.lastIndexOf("."));
				return substring;
			} else {
				return name;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Utf8URL编码
	 * 
	 * @param s
	 * @return
	 */
	public static String Utf8URLencode(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c >= 0 && c <= 255) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}

	/**
	 * 获取他的Md5
	 * 
	 * @param paramString
	 * @return
	 */
	public static String getMdFive(String paramString) {
		String returnStr;
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramString.getBytes());
			returnStr = byteToHexString(localMessageDigest.digest());
			return returnStr;
		} catch (Exception e) {
			return paramString;
		}
	}

	/**
	 * 将指定byte数组转换成16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

	// 将数字转化为汉字的数组,因为各个实例都要使用所以设为静态
	private static final char[] cnNumbers = { '一', '二', '三', '四', '五', '六',
			'七', '八', '九' };

	// 供分级转化的数组,因为各个实例都要使用所以设为静态
	private static final char[] series = { '0', '十', '百', '千', '万' };

	/**
	 * 构造函数,通过它将阿拉伯数字形式的字符串传入 把一个数字转换成大写，不能超过Interger，否则会出异常
	 * 
	 * @param original
	 *            return string
	 */
	public static String cnToUpperCaser(String original) {
		return getCnString(original);
	}

	/**
	 * 取得大写形式的字符串
	 * 
	 * @return
	 */
	private static String getCnString(String original) {
		// 因为是累加所以用StringBuffer
		StringBuffer sb = new StringBuffer();
		int size = original.length() - 1;
		boolean flag = false;
		// 整数部分处理
		for (int i = size; i >= 0; i--) {
			int number = getNumber(original.charAt(i));
			if (i == size && number == 0) {
				flag = true;
			}
			if (number == 0) {
				flag = false;
			}
			if (!flag) {
				if (number != 0) {
					if (i != size) {// 个位不执行
						sb.append(series[size - i]);
					}
				}

				if (number != 0) {
					sb.append(cnNumbers[number - 1]);
				} else {
					if (!"零".contains(sb.toString())) {
						sb.append("零");
					}
				}
			}

		}
		sb.reverse();
		// 返回拼接好的字符串
		return sb.toString();
	}

	/**
	 * 将字符形式的数字转化为整形数字 因为所有实例都要用到所以用静态修饰
	 * 
	 * @param c
	 * @return
	 */
	private static int getNumber(char c) {
		String str = String.valueOf(c);
		return Integer.parseInt(str);
	}
	/**
	 * 把name中的末尾含有endText这个文本后面的字符串截取出来
	 * @param name
	 * @param endText
	 * @return 返回null或则“”代表没有截取到，否则返回endtext后的文字
	 * example; http:\\www.baidu.com\indext.java
	 * 			endText=\,返回
	 */
	public static String  getPathEndFileName(String name,String endText){
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
