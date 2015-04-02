package com.ls.sdk.utils;

import com.ls.sdk.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.view.ViewGroup.MarginLayoutParams;

/**
 * 该类的主要的作用是界面展示通知的对话框集合..
 * 
 * @author ls
 * 
 */
public class NotifactionsViewUtils {
	private Dialog rotateDialog;
	private ProgressDialog RotateDialogLoading;
	private Object mAttrs;
	private LinearLayout mPanel;
	private View mView;

	/**
	 * 展示一个旋转的动画，大概效果是左边一个圈，右边一个文字加载中
	 * 
	 * @param context
	 */
	public void showRotateDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialogview, null);
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.rotate);
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		rotateDialog = new Dialog(context, R.style.FullHeightDialog);
		rotateDialog.setCancelable(true);
		rotateDialog.show();
		rotateDialog.setContentView(layout, new LinearLayout.LayoutParams(180,
				LinearLayout.LayoutParams.WRAP_CONTENT));
	}

	/**
	 * 关闭一个旋转动画
	 * 
	 * @param context
	 */
	public void closeRotateDialog(Context context) {
		if (rotateDialog != null && rotateDialog.isShowing()) {
			rotateDialog.dismiss();
		}
	}

	/**
	 * 安卓再带的进度条加载dialog，,最上面是标题，如果没有标题可以为空，为空他不会显示，一个选装的圆形和文字居中显示
	 * 效果同showRotateDialog（this）
	 */
	public void showRotateDialogLoading(Context context, String title,
			String desc) {
		RotateDialogLoading = new ProgressDialog(context);
		if (!TextUtils.isEmpty(title)) {
			RotateDialogLoading.setTitle(title);
		}
		RotateDialogLoading.setMessage(desc);
		RotateDialogLoading.show();
	}

	public void closeRotateDialogLoading() {
		if (RotateDialogLoading != null && RotateDialogLoading.isShowing()) {
			RotateDialogLoading.dismiss();
		}
	}

	/**
	 * 安卓自带的单选按钮，左边一个文本描述，右边一个圆形的当前状态
	 * 
	 * @param context
	 * @param title
	 *            标题，如果为空他就不会显示标题
	 * @param desc
	 *            要显示的文本
	 * @param callback
	 *            单选按钮点击后的回调
	 */
	public void showDialogSingleSelectorButton(Context context, String title,
			final String[] desc, final CallBackSelector callback) {
		AlertDialog.Builder bu = new Builder(context);
		boolean empty = TextUtils.isEmpty(title);
		if (empty) {
		} else {
			bu.setTitle(title);
		}
		bu.setSingleChoiceItems(desc, 0, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (callback != null) {
					callback.onlickCallBack(dialog, which, null);
				}
			}
		});
		bu.show();
	}

	public interface CallBackSelector {
		/**
		 * 
		 * @param dialog
		 * @param which
		 * @param selector
		 *            如果为null时没有作用
		 */
		public void onlickCallBack(DialogInterface dialog, int which,
				boolean[] selector);
	}

	/**
	 * 安卓自带多选按钮对话框，同单选按钮相反，左边一个文本，右边一个checkbox，上面一个标题（为空不显示），下面一个提交按钮
	 * 
	 * @param context
	 * @param title
	 *            标题可以为空
	 * @param desc
	 *            内容
	 * @param selector
	 *            默认选中和不选中选中为true，与desc相对应
	 * @param commit
	 *            提交按钮文本
	 * @param callback
	 *            回调接口当点击提交按钮时
	 */
	public void showDialogMultipleSelectorButton(Context context, String title,
			final String[] desc, final boolean[] selector, String commit,
			final CallBackSelector callback) {
		AlertDialog.Builder bu = new Builder(context);
		boolean empty = TextUtils.isEmpty(title);
		if (empty) {
		} else {
			bu.setTitle(title);
		}
		bu.setMultiChoiceItems(desc, selector,
				new OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						selector[which] = isChecked;
					}
				});
		bu.setPositiveButton(commit, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onlickCallBack(dialog, which, selector);
			}
		});
		bu.show();
	}

	/**
	 * 安卓自带的弹出对话框，上面一个标题，中间一个描述，下面一个确认和一个取消
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题可以为空
	 * @param ok
	 *            确认的字符串
	 * @param giveup
	 *            取消的字符串
	 * @param desc
	 *            描述
	 * @param callback
	 *            确认和取消的回调，当which为11时代表确认的回调，当which为22时代表取消的回调
	 */
	public void showDialogMessage(Context context, String title, String ok,
			String giveup, String desc, final CallBackSelector callback) {
		AlertDialog.Builder bu = new Builder(context);
		boolean empty = TextUtils.isEmpty(title);
		if (empty) {
		} else {
			bu.setTitle(title);
		}
		bu.setMessage(desc);
		bu.setPositiveButton(ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				which = 11;
				callback.onlickCallBack(dialog, which, null);
			}
		});
		bu.setNegativeButton(giveup, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				which = 22;
				callback.onlickCallBack(dialog, which, null);
			}
		});
		bu.show();
	}

	/**
	 * 安卓自带的一个水平进度条，他会显示当前的进度，默认进度为0，最大进度为100 样式：上面一个标题，中间一个描述，下面一个水平进度条
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param desc
	 *            内容
	 * @return
	 */
	public ProgressDialog showDialogProgressLoading(Context context,
			String title, String desc) {
		final ProgressDialog pro = new ProgressDialog(context);
		boolean empty = TextUtils.isEmpty(title);
		if (empty) {
		} else {
			pro.setTitle(title);
		}
		pro.setMessage(desc);
		pro.setMax(100);
		pro.setProgress(0);
		pro.setProgressStyle(1);
		pro.setCanceledOnTouchOutside(false);
		pro.show();
		return pro;
	}

	/**
	 * 安卓自带的通知消息样式最普通的样式
	 * 
	 * @param context
	 * @param title
	 *            显示在上面的文本
	 * @param desc
	 *            显示在标题下面的文本
	 * @param id
	 *            要显示的图片id
	 */
	public void showNotifactionMessage(Context context, String title,
			String desc, int id) {
		NotificationManager nfim = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		android.app.Notification.Builder build = new Notification.Builder(
				context);
		build.setContentTitle(title)
				.setContentText(desc)
				.setSmallIcon(id)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(), id));
		Notification build2 = build.build();
		nfim.notify(1, build2);
	}

	/**
	 * 展示一个仿ios版的popUp样式
	 * 
	 * @param context
	 *            上下文
	 * @param titles
	 *            标题数组
	 * @param cancelTitle
	 *            取消按钮
	 * @param id
	 *            传一个View的Id只要当前布局有这个View就可以
	 * @return PopupWindow
	 */
	public PopupWindow showIosPopUpStyle(Context context, String[] titles,
			String cancelTitle, View id) {
		// 修改文字尺寸，等于0代表系统默认
		int textSize = 0;
		// 修改文字颜色，等于0代表系统默认
		int textColor = 0xff1E82FF;
		FrameLayout parent = new FrameLayout(context);
		final PopupWindow pop = new PopupWindow(parent);
		pop.setOutsideTouchable(true);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.MATCH_PARENT);
		parent.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (pop != null && pop.isShowing()) {
					pop.dismiss();
				}
				if (listener != null) {
					listener.outSideonclick(arg0);
				}
			}
		});
		parent.startAnimation(createAlphaInAnimation());
		// 设置背景色
		parent.setBackgroundColor(Color.argb(136, 0, 0, 0));
		// 填充父窗体
		parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		// 线性布局中装条目
		LinearLayout mPanel = new LinearLayout(context);
		// 包裹内容
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 在fragment底部
		params.gravity = Gravity.BOTTOM;
		mPanel.setOrientation(LinearLayout.VERTICAL);
		mPanel.setLayoutParams(params);
		parent.addView(mPanel);
		if (titles != null && titles.length > 1) {
			for (int i = 0; i < titles.length; i++) {
				Button bt = new Button(context);
				bt.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (listener != null) {
							listener.itemonclick(arg0);
						}
					}
				});
				bt.setId(i);
				if (i == 0) {
					bt.setBackgroundResource(R.drawable.slt_as_ios7_other_bt_top);
				} else if (i == titles.length - 1) {
					bt.setBackgroundResource(R.drawable.slt_as_ios7_other_bt_bottom);
				} else {
					bt.setBackgroundResource(R.drawable.slt_as_ios7_other_bt_middle);
				}

				LinearLayout.LayoutParams parambtn = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				parambtn.leftMargin = 20;
				parambtn.rightMargin = 20;
				if (textSize != 0) {
					bt.setTextSize(textSize);
				}
				// 设置颜色
				if (textColor != 0) {
					bt.setTextColor(textColor);
				}
				bt.setText(titles[i]);
				bt.setLayoutParams(parambtn);
				// 线性布局中添加button
				mPanel.addView(bt);
			}
			if (TextUtils.isEmpty(cancelTitle)) {
				pop.showAtLocation(id, Gravity.BOTTOM, 0, 0);
				return pop;
			}
			// 创建取消按钮
			Button bt = new Button(context);
			if (textSize != 0) {
				bt.setTextSize(textSize);
			}
			// 设置颜色
			if (textColor != 0) {
				bt.setTextColor(textColor);
			}
			bt.setText(cancelTitle);
			// 设置取消按钮的边距
			LinearLayout.LayoutParams parambtn2 = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			parambtn2.topMargin = 20;
			parambtn2.leftMargin = 20;
			parambtn2.rightMargin = 20;
			parambtn2.bottomMargin = 20;
			bt.setOnClickListener(new android.view.View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (listener != null) {
						listener.cancelonclick(arg0);
					}
				}
			});
			// 设置按钮的状态选择器
			bt.setBackgroundResource(R.drawable.slt_as_ios7_cancel_bt);
			bt.setLayoutParams(parambtn2);
			// 添加取消按钮
			mPanel.addView(bt);
			// 开启一个位移动画
			mPanel.startAnimation(createTranslationInAnimation());
		}
		pop.showAtLocation(id, Gravity.BOTTOM, 0, 0);
		return pop;
	}

	private Animation createTranslationInAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				1, type, 0);
		an.setDuration(200);
		return an;
	}

	private Animation createAlphaInAnimation() {
		AlphaAnimation an = new AlphaAnimation(0, 1);
		an.setDuration(250);
		return an;
	}

	private onPopupClickCallBack listener = null;

	/** 设置popup的回调接口 */
	public void setOnPopUpClickListener(onPopupClickCallBack listener) {
		this.listener = listener;
	}

	interface onPopupClickCallBack {
		/** 外置触摸关闭popup的事件 */
		public void outSideonclick(View arg0);

		/** 取消按钮的点击事件 */
		public void cancelonclick(View arg0);

		/** 可以通过ID来区分序号是数组的循序从0开始 */
		public void itemonclick(View arg0);
	}
}
