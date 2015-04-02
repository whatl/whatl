package com.ls.sdk.activity;

import com.ls.sdk.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseActivity extends Activity implements OnClickListener{
	/**
	 * 标记标题左右两边的类型:文字
	 */
	protected final int TITLE_TYPE_TEXT = 0;
	/**
	 * 标记标题左右两边的类型:图片
	 */
	protected final int TITLE_TYPE_IMG = 1;

	protected ProgressDialog waitDialog;
	private long firstTime = 0;

	/**
	 * 需在setContentView方法之后调用. 设置后,如果不对左侧的事件进行监听,默认的点击事件是结束当前界面.
	 * <p>
	 * 标题传资源id和字符串皆可.
	 * <p>
	 * 如果某一侧显示的是图片,则那一侧只能传对应的图片资源id.如果是文字,则资源id和字符串皆可.
	 * 
	 * @param title
	 *            标题
	 * @param left
	 *            是否显示左侧的部分
	 * @param leftType
	 *            左侧的类型
	 * @param l
	 *            左侧部分内容
	 * @param right
	 *            是否显示右侧的部分
	 * @param rightType
	 *            右侧的类型
	 * @param r
	 *            右侧部分的内容
	 */
	protected void setTitle(Object title, boolean left, int leftType, Object l,
			boolean right, int rightType, Object r) {
		try {
			TextView tvTitle = (TextView) findViewById(R.id.tv_title);
			TextView tvLeft = (TextView) findViewById(R.id.tv_title_left);
			LinearLayout llLeft = (LinearLayout) findViewById(R.id.ll_title_left);
			ImageView ivLeft = (ImageView) findViewById(R.id.iv_title_left);
			TextView tvRight = (TextView) findViewById(R.id.tv_title_right);
			ImageView ivRight = (ImageView) findViewById(R.id.iv_title_right);
			LinearLayout llRight = (LinearLayout) findViewById(R.id.ll_title_right);
			if (title != null && title instanceof String) {
				if (!TextUtils.isEmpty((String) title)) {
					tvTitle.setVisibility(View.VISIBLE);
					tvTitle.setText((String) title);
				} else {
					tvTitle.setVisibility(View.INVISIBLE);
				}
			} else if (title != null && title instanceof Integer) {
				if (((Integer) title) > 0) {
					tvTitle.setVisibility(View.VISIBLE);
					tvTitle.setText((Integer) title);
				} else {
					tvTitle.setVisibility(View.INVISIBLE);
				}

			}
			if (left) {
				llLeft.setVisibility(View.VISIBLE);
				if (leftType == TITLE_TYPE_TEXT) {
					ivLeft.setVisibility(View.GONE);
					tvLeft.setVisibility(View.VISIBLE);
					if (l instanceof String) {
						tvLeft.setText((String) l);
					} else if (l instanceof Integer) {
						tvLeft.setText((Integer) l);
					}
				} else if (leftType == TITLE_TYPE_IMG) {
					ivLeft.setVisibility(View.VISIBLE);
					tvLeft.setVisibility(View.GONE);
					if (l instanceof Integer) {
						ivLeft.setImageResource((Integer) l);
					}
				}
			} else {
				llLeft.setVisibility(View.INVISIBLE);
			}
			if (right) {
				llRight.setVisibility(View.VISIBLE);
				if (rightType == TITLE_TYPE_TEXT) {
					ivRight.setVisibility(View.GONE);
					tvRight.setVisibility(View.VISIBLE);
					if (r instanceof String) {
						tvRight.setText((String) r);
					} else if (r instanceof Integer) {
						tvRight.setText((Integer) r);
					}
				} else if (rightType == TITLE_TYPE_IMG) {
					ivRight.setVisibility(View.VISIBLE);
					tvRight.setVisibility(View.GONE);
					if (r instanceof Integer) {
						ivRight.setImageResource((Integer) r);
					}
				}
			} else {
				llRight.setVisibility(View.INVISIBLE);
			}

		} catch (Exception e) {

		}
	}

	/**
	 * 设置点击左上角的返回事件.默认是finish界面
	 */
	protected void registerBack() {
		LinearLayout llLeft = (LinearLayout) findViewById(R.id.ll_title_left);
		llLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				BaseActivity.this.finish();
			}
		});
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.onCreate(savedInstanceState,this);
		this.init(this);
		this.initListener(this);
	}
	/**
	 * 重写此方法,不需要重写onCreate
	 * @param savedInstanceState
	 * @param context
	 */
	public abstract void onCreate(Bundle savedInstanceState,Context context);
	/***
	 * 初始化布局
	 * @param context
	 */
	public abstract void init(Context context);
	/**
	 * 初始化监听事件
	 * @param context
	 */
	public abstract void initListener(Context context);
	/**
	 * 全局等待对话框
	 */
	public void showWaitDialog() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null || !waitDialog.isShowing()) {
					waitDialog = new ProgressDialog(BaseActivity.this);
					waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					waitDialog.setCanceledOnTouchOutside(false);
					ImageView view = new ImageView(BaseActivity.this);
					view.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					Animation loadAnimation = AnimationUtils.loadAnimation(
							BaseActivity.this, R.anim.rotate);
					view.startAnimation(loadAnimation);
					loadAnimation.start();
					view.setImageResource(R.drawable.loading);
					// waitDialog.setCancelable(false);
					waitDialog.show();
					waitDialog.setContentView(view);
				}

			}
		});

	}

	public void dismissWaitDialog() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog != null && waitDialog.isShowing()) {
					waitDialog.dismiss();
					waitDialog = null;
				}
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissWaitDialog();
		// unregisterReceiver(receiver);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * 连续按两次退出当前的界面
	 * 
	 * @param act
	 */
	public void onBackPressed(Activity act) {
		long secondTime = System.currentTimeMillis();
		if (secondTime - firstTime > 1000) { // 如果两次按键时间间隔大于2秒，则不退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			firstTime = secondTime;// 更新firstTime
		} else { // 两次按键小于2秒时，退出应用
			firstTime = 0;
			act.finish();
		}
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
