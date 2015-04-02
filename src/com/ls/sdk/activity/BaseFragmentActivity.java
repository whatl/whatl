package com.ls.sdk.activity;

import com.ls.sdk.R;
import com.ls.sdk.fragment.IosItemClickFragment;
import com.ls.sdk.fragment.IosItemClickFragment.IosItemListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BaseFragmentActivity extends FragmentActivity implements
		IosItemListener {
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

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	/**
	 * 设置点击左上角的返回事件.默认是finish界面
	 */
	protected void registerBack() {
		LinearLayout llLeft = (LinearLayout) findViewById(R.id.ll_title_left);
		llLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				BaseFragmentActivity.this.finish();
			}
		});
	}

	/**
	 * 全局等待对话框
	 */
	public void showWaitDialog() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null || !waitDialog.isShowing()) {
					waitDialog = new ProgressDialog(BaseFragmentActivity.this);
					waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					waitDialog.setCanceledOnTouchOutside(false);
					ImageView view = new ImageView(BaseFragmentActivity.this);
					view.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					Animation loadAnimation = AnimationUtils.loadAnimation(
							BaseFragmentActivity.this, R.anim.rotate);
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
	 * 弹出一个Fragment界面,好比就是popup
	 */
	public void showActionSheet() {
		setTheme(R.style.ActionSheetStyleIOS7);
		com.ls.sdk.fragment.IosItemClickFragment
				.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("Cancel")
				.setOtherButtonTitles("Item2", "Item2", "Item3", "Item4")
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	@Override
	public void onDismiss(IosItemClickFragment actionSheet, boolean isCancel) {
		Toast.makeText(getApplicationContext(),
				"dismissed isCancle = " + isCancel, 0).show();
	}

	@Override
	public void onOtherButtonClick(IosItemClickFragment actionSheet, int index) {
		Toast.makeText(getApplicationContext(),
				"dismissed isCancle = " + index, 0).show();
	}
}
