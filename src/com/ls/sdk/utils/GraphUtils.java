package com.ls.sdk.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.MeasureSpec;

/**
 * 图形的工具类
 * 
 * @author ls
 * 
 */
public class GraphUtils {

	/**
	 * bitmap转成byte数组
	 * 
	 * @param avatar
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap avatar) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		avatar.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();

	}

	/**
	 * 把一个资源文件中的图片变成bitmap
	 * 
	 * @param ct
	 * @param fileName
	 * @return
	 */
	public static Bitmap getImageFromAssetsFile(Context ct, String fileName) {
		Bitmap image = null;
		AssetManager am = ct.getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * 获取一个环形的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.RGB_565);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 20;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 获取一个环形的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.RGB_565);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		// final float roundPx = 20;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 获取一个环形的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx,
			int width, int height) {
		Bitmap bm = Bitmap.createScaledBitmap(bitmap, width, height, true);
		Bitmap output = Bitmap.createBitmap(width, height, Config.RGB_565);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bm, rect, rect, paint);

		return output;
	}

	/**
	 * 获取一个图片压缩后的宽
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap decodeFile(String path) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);
			// The new size we want to scale to
			final int REQUIRED_SIZE = 70;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeFile(path, o2);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 获取一个压缩后的图片宽度
	 * 
	 * @param path
	 * @param width
	 * @return
	 */
	public static Bitmap decodeFile(String path, int width) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);
			// Find the correct scale value. It should be the power of 2.
			int scale = o.outWidth / width;
			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeFile(path, o2);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 用于从相册获取图片
	 * 
	 * @param activity
	 * @param uri
	 * @return
	 */
	public static Bitmap decodeUri(Activity activity, Uri uri) {
		ParcelFileDescriptor parcelFD = null;
		try {
			parcelFD = activity.getContentResolver().openFileDescriptor(uri,
					"r");
			FileDescriptor imageSource = parcelFD.getFileDescriptor();

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(imageSource, null, o);

			// the new size we want to scale to
			final int REQUIRED_SIZE = 1024;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource,
					null, o2);
			// 此处对bitmap处理
			return bitmap;

		} catch (FileNotFoundException e) {
			// handle errors
		} catch (IOException e) {
			// handle errors
		} finally {
			if (parcelFD != null)
				try {
					parcelFD.close();
				} catch (IOException e) {
					// ignored
				}
		}
		return null;
	}

	/**
	 * 获取文件夹的绝对路径目录
	 * 
	 * @param activity
	 * @return
	 */
	public static String genMuLu(Activity activity) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				&& Environment.getExternalStorageDirectory().exists()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			return activity.getApplication().getFilesDir().getAbsolutePath();
		}
	}

	/**
	 * 保存图片到指定目录自己写路径
	 * 
	 * @param bitmap
	 */
	public static String saveBitmap(Context context, Bitmap mBitmap) {
		// File mFile = new File(Environment.getRootDirectory()+"/oAimages");
		Date mDate = new Date();
		String imgName = Long.toString(mDate.getTime()) + ".jpg";
		File mFile = new File("/image/");
		if (!mFile.exists()) {
			mFile.mkdirs();
		}
		try {
			FileOutputStream out = new FileOutputStream(mFile.getAbsolutePath()
					+ "/" + imgName);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mFile = new File(mFile.getAbsolutePath() + "/" + imgName);
		return mFile.getAbsolutePath();
	}

	private File file;

	/**
	 * 对图片进行裁剪，就是你在手机的相册中选择了一张图片需要对图片自带的剪切功能就可以调用这个方法
	 * getPicPath(),得到路劲后你就可以在通过意图让他裁剪
	 * 
	 * @param 上下文
	 *            和图片的绝对路径
	 */
	private void startPhotoZoom(Context con, String filePath) {
		Uri uri = Uri.fromFile(new File(filePath));
		Intent intent = new Intent();
		intent.setAction("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("return-data", true);

		// intent .putExtra("outputFormat",
		// Bitmap.CompressFormat.JPEG.toString());

		// if (Utils.hasSDCard()) {
		file = new File("/image/thumb_avatar.png");// 图片的路
		if (file.exists()) {
			file.delete();
		} else {
			file.getParentFile().mkdirs();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		// startActivityForResult(intent, REQUEST_CODE_CROP);
		// } else {
		// Toast.makeText(con, "未找到存储卡",0).show();
		// }
	}

	/**
	 * 从图库中获取图片路径，你可以通过意图打开一个图片，当用户打开后选择了图片，你就可以在onActivityResult()中getPicPath(
	 * act,data.getData()
	 * 
	 * @param uri
	 * @return
	 */
	private String getPicPath(Activity act, Uri uri) {
		// 获得图片的uri
		Uri originalUri = uri;
		String[] proj = { MediaStore.Images.Media.DATA };
		// 好像是android多媒体数据库的封装接口，具体的看Android文档

		Cursor cursor = act.managedQuery(originalUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		// 将光标移至开头 ，这个很重要，不小心很容易引起越界
		cursor.moveToFirst();
		// 最后根据索引值获取图片路径
		String path = cursor.getString(column_index);
		return path;
	}

	/**
	 * 图片变灰
	 **/
	private static Bitmap graph2Greys(Bitmap old) {
		int width, height;
		height = old.getHeight();
		width = old.getWidth();
		Bitmap news = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(old);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(news, 0, 0, paint);
		return news;
	}

	/**
	 * 得到一个View的图片
	 * 
	 * @param views
	 * @return
	 */
	public static Bitmap convertViewToBitmap(View views) {
		views.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		views.layout(0, 0, views.getMeasuredWidth(), views.getMeasuredHeight());
		views.buildDrawingCache();
		Bitmap bitmap = views.getDrawingCache();
		return bitmap;
	}

	/**
	 * 图片变成灰色
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap graph2Grey(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Bitmap faceIconGreyBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(faceIconGreyBitmap);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
				colorMatrix);
		paint.setColorFilter(colorMatrixFilter);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return faceIconGreyBitmap;
	}
}
