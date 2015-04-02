package com.ls.sdk.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.ls.sdk.R;
import com.ls.sdk.http.UploadFileProductPic;
import com.ls.sdk.http.UploadFileProductPic.OnUploadFileForResultListener;
import com.ls.sdk.utils.SDCardUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * 该类的主要作用是用来通过意图打开相册或则打开照相机后，当成功获取到图片后，可以做一些处理，比如上传到服务器
 * 
 * @author Administrator
 * 
 */
public class UploadPicActivity extends BaseActivity {

	private View view;
	private Button take_photo;
	private Button from_album;
	private Button cancel;
	private PopupWindow pw;
	private Intent intent;
	private Context context;
	final private int PICK_IMAGE = 1;
	final private int CAPTURE_IMAGE = 2;
	private String pic_path;
	private int uploadimageurl = 1;
	public static final int REQUEST_MODIFY_CODE = 8000;
	public static final int REQUEST_CODE_CROP = 9000;
	private final int REQUESTSGETSUCCESS = 10001;
	private final int REQUESTSGETFAILURE = 10002;
	private File file;
	/***/
	private String url;
	private File files;

	@Override
	public void init(Context context) {

	}

	@Override
	public void initListener(Context context) {

	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.take_photo) {
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
			startActivityForResult(intent, CAPTURE_IMAGE);
		} else if (id == R.id.from_album) {
			intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, PICK_IMAGE);
			pw.dismiss();
		} else if (id == R.id.cancel) {
			pw.dismiss();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState, Context context) {
		this.context = context;
		setContentView(R.layout.main_activity);
		intentPopUp();
	}

	private void intentPopUp() {
		view = LayoutInflater.from(this).inflate(R.layout.popup_intent_getpic,
				null);
		take_photo = (Button) view.findViewById(R.id.take_photo);
		take_photo.setOnClickListener(this);
		from_album = (Button) view.findViewById(R.id.from_album);
		from_album.setOnClickListener(this);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		pw = new PopupWindow(view);
		pw.setBackgroundDrawable(new BitmapDrawable());// 没有此句点击外部不会消失
		pw.setOutsideTouchable(true);
		pw.setFocusable(true);
		pw.setAnimationStyle(R.style.MyPopupAnimation);
		pw.setWidth(LayoutParams.MATCH_PARENT);
		pw.setHeight(LayoutParams.WRAP_CONTENT);
		pw.showAtLocation(findViewById(R.id.ll_layout), Gravity.BOTTOM, 0, 0);
	}

	public Uri setImageUri() {
		File file = new File(genMuLu(this) + "/DCIM/", "image"
				+ new Date().getTime() + ".png");
		Uri imgUri = Uri.fromFile(file);
		this.pic_path = file.getAbsolutePath();
		return imgUri;
	}

	/**
	 * 获取绝对路径
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
	 * 照片获取后，缩放，缩放后， 上传传入网址
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 当用户点返回键时--系统自动返回这个RESULT_CANCELED
		if (resultCode != Activity.RESULT_CANCELED) {
			UploadFileProductPic uploadpic = new UploadFileProductPic();
			// uploadimageurl 和 message 做识别
			uploadpic.setListener(new OnUploadFileForResultListener() {

				private Message msg;

				@Override
				public void onResultListener(boolean isUploadSuccess,
						String message, int i) {
					if (isUploadSuccess) {// 上传成功后
						switch (i) {
						case 1:
							msg.what = REQUESTSGETSUCCESS;
							msg.obj = message;
							handlerPic.sendMessage(msg);
							break;
						}
					} else {
						msg.what = REQUESTSGETFAILURE;
						msg.obj = message;
						handlerPic.sendMessage(msg);
					}
				}
			}, uploadimageurl);

			if (requestCode == PICK_IMAGE && data != null) {// 相册的意图
				// 相册
				// showWaitDialog();
				try {
					File file = new File(getPicPath(data.getData()));
					files = file;
					startPhotoZoom(files.getAbsolutePath());
				} catch (Exception e) {
					dismissWaitDialog();
					e.printStackTrace();
				}

			} else if (requestCode == CAPTURE_IMAGE) {// 拍照的意图
				try {

					File file = new File(pic_path);
					files = file;
					startPhotoZoom(files.getAbsolutePath());
				} catch (Exception e) {
					dismissWaitDialog();
					e.printStackTrace();
				}
			} else if (requestCode == REQUEST_CODE_CROP && data != null) {// startPhotoZoom（）　---》　图片的缩放的意图，缩放后，就上传到服务器
				if (SDCardUtils.isSDCardEnable()) {
					Uri photoUri = data.getData();
					Bitmap photo = null;
					if (photoUri != null) {
						photo = BitmapFactory.decodeFile(photoUri.getPath());
					}
					if (photo == null) {// 333
						Bundle extra = data.getExtras();
						if (extra != null) {
							photo = (Bitmap) extra.get("data");
						}
					}
					File file = saveBitmap(context, photo, getAppDir(this)+"/image/thumb_avatar.png");
					MultipartEntity entity = new MultipartEntity();
					if (file.exists()) {

						FileBody fileBody = new FileBody(file);
						StringBody uidBody = null;
						StringBody tokenBody = null;
						try {
							// 需要传到服务器的参数
							// uidBody = new StringBody(uid);
							// tokenBody = new StringBody(token);
						} catch (Exception e) {
							e.printStackTrace();
						}
						entity.addPart("uid", uidBody);
						entity.addPart("token", tokenBody);
						entity.addPart("logo", fileBody);
						uploadpic.uploadImg(url, entity);
					}
				}
			}

		}

	}

	/**
	 * 从图库中获取图片路径
	 * 
	 * @param uri
	 * @return
	 */
	private String getPicPath(Uri uri) {
		// 获得图片的uri
		Uri originalUri = uri;
		String[] proj = { MediaStore.Images.Media.DATA };
		// 好像是android多媒体数据库的封装接口，具体的看Android文档

		Cursor cursor = managedQuery(originalUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		// 将光标移至开头 ，这个很重要，不小心很容易引起越界
		cursor.moveToFirst();
		// 最后根据索引值获取图片路径
		String path = cursor.getString(column_index);
		return path;
	}

	/**
	 * 请求码 ：　REQUEST_CODE_CROP 开启缩放的意图
	 */
	private void startPhotoZoom(String Selected) {
		Uri uri = Uri.fromFile(new File(Selected));
		Intent intent = new Intent();
		intent.setAction("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		if (uploadimageurl == 0) {
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
		} else {
			intent.putExtra("aspectX", 2);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 100);
		}

		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("return-data", true);

		// intent .putExtra("outputFormat",
		// Bitmap.CompressFormat.JPEG.toString());

		if (SDCardUtils.isSDCardEnable()) {
			file = new File(getAppDir(this), "/image/thumb_avatar.png");
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
			//这个方法增对其他手机不是很管用
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(intent, REQUEST_CODE_CROP);
		} else {
			// ToastUtil.makeShortText(this, "未找到存储卡");
		}
	}

	/**
	 * 获取app的存储目录
	 * <p>
	 * 一般情况下是这样的/storage/emulated/0/Android/data/包名/
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppDir(Context context) {
		return (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? (Environment
				.getExternalStorageDirectory().getPath() + "/Android/data/")
				: (context.getCacheDir().getPath()))
				+ context.getPackageName();
	}

	Handler handlerPic = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REQUESTSGETSUCCESS:// 上传成功
				Toast.makeText(context, "成功", 0).show();

				dismissWaitDialog();
				break;
			case REQUESTSGETFAILURE:
				// 失败后业务处理
				Toast.makeText(context, "失败", 0).show();
				dismissWaitDialog();
				break;
			}

		}
	};
	/**
	 * 保存图片到指定目录自己写路径
	 * path 文件路径
	 * @param bitmap
	 */
	public File saveBitmap(Context context, Bitmap mBitmap,String path) {
		if (mBitmap == null) {
			return null;
		}
		File mfile = new File(path);
		if (mfile.exists()) {
			mfile.delete();
		} else {
			mfile.getParentFile().mkdirs();
		}
		try {
			mfile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileOutputStream out = new FileOutputStream(mfile.getAbsolutePath());
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mfile;
	}
}
