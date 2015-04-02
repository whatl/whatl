package com.ls.sdk.mview.pullrefreshview;


import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 这个是最上面一个可以缩放图片，下面是listVIew的条目，当你下啦时最上面的head图片可以放大缩小
 * 
 * listView = (PullToZoomListView)findViewById(R.id.listview); adapterData = new
 * String[] {
 * "Activity","Service","Content Provider","Intent","BroadcastReceiver"
 * ,"ADT","Sqlite3","HttpClient","DDMS","Android Studio","Fragment","Loader" };
 * 
 * listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
 * android.R.layout.simple_list_item_1, adapterData));
 * listView.getHeaderView().setImageResource(R.drawable.splash01);
 *必须要加缩放类型，不然没效果CENTER_CROP，代表中心放大
 * listView.getHeaderView().setScaleType(ImageView.ScaleType.CENTER_CROP);
 * FIT_START 这种缩放有的类似谷歌日历，当你上滑的时候这个图片也会上滑要比listView条目慢一拍的节奏 
 * 
 * @author ls
 * 
 */
public class PullToZoomListView extends ListView implements
		AbsListView.OnScrollListener {
	private static final int INVALID_VALUE = -1;
	private static final String TAG = "PullToZoomListView";
	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float paramAnonymousFloat) {
			float f = paramAnonymousFloat - 1.0F;
			return 1.0F + f * (f * (f * (f * f)));
		}
	};
	int mActivePointerId = -1;
	private FrameLayout mHeaderContainer;
	private int mHeaderHeight;
	private ImageView mHeaderImage;
	float mLastMotionY = -1.0F;
	float mLastScale = -1.0F;
	float mMaxScale = -1.0F;
	private AbsListView.OnScrollListener mOnScrollListener;
	private ScalingRunnalable mScalingRunnalable;
	private int mScreenHeight;
	private ImageView mShadow;

	public PullToZoomListView(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public PullToZoomListView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	public PullToZoomListView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext);
	}

	private void endScraling() {
		if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight)
			Log.d("mmm", "endScraling");
		this.mScalingRunnalable.startAnimation(200L);
	}

	/**
	 * 这个方法大概实现的功能是，创建了一个imageViEW和阴影的IMageView，和一个容器帧布局，new了一个缩放线程设置了滚动的监听事件
	 * 
	 * @param paramContext
	 */
	private void init(Context paramContext) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		((Activity) paramContext).getWindowManager().getDefaultDisplay()
				.getMetrics(localDisplayMetrics);
		this.mScreenHeight = localDisplayMetrics.heightPixels;
		this.mHeaderContainer = new FrameLayout(paramContext);
		this.mHeaderImage = new ImageView(paramContext);
		int i = localDisplayMetrics.widthPixels;
		// 设置了这个布局frameLayout的大小
		setHeaderViewSize(i, (int) (9.0F * (i / 16.0F)));
		this.mShadow = new ImageView(paramContext);
		FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
				-1, -2);
		localLayoutParams.gravity = 80;
		this.mShadow.setLayoutParams(localLayoutParams);
		this.mHeaderContainer.addView(this.mHeaderImage);
		this.mHeaderContainer.addView(this.mShadow);
		addHeaderView(this.mHeaderContainer);
		this.mScalingRunnalable = new ScalingRunnalable();
		super.setOnScrollListener(this);
	}

	private void onSecondaryPointerUp(MotionEvent paramMotionEvent) {
		int i = (paramMotionEvent.getAction()) >> 8;
		if (paramMotionEvent.getPointerId(i) == this.mActivePointerId)
			if (i != 0) {
				int j = 1;
				this.mLastMotionY = paramMotionEvent.getY(0);
				this.mActivePointerId = paramMotionEvent.getPointerId(0);
				return;
			}
	}

	private void reset() {
		this.mActivePointerId = -1;
		this.mLastMotionY = -1.0F;
		this.mMaxScale = -1.0F;
		this.mLastScale = -1.0F;
	}

	public ImageView getHeaderView() {
		return this.mHeaderImage;
	}

	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		return super.onInterceptTouchEvent(paramMotionEvent);
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
		if (this.mHeaderHeight == 0)
			this.mHeaderHeight = this.mHeaderContainer.getHeight();
	}

	@Override
	public void onScroll(AbsListView paramAbsListView, int paramInt1,
			int paramInt2, int paramInt3) {
		Log.d("mmm", "onScroll");
		float f = this.mHeaderHeight - this.mHeaderContainer.getBottom();
		Log.d("mmm", "f|" + f);
		if ((f > 0.0F) && (f < this.mHeaderHeight)) {
			Log.d("mmm", "1");
			int i = (int) (0.65D * f);
			this.mHeaderImage.scrollTo(0, -i);
		} else if (this.mHeaderImage.getScrollY() != 0) {
			Log.d("mmm", "2");
			this.mHeaderImage.scrollTo(0, 0);
		}
		if (this.mOnScrollListener != null) {
			this.mOnScrollListener.onScroll(paramAbsListView, paramInt1,
					paramInt2, paramInt3);
		}
	}

	public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
		if (this.mOnScrollListener != null)
			this.mOnScrollListener.onScrollStateChanged(paramAbsListView,
					paramInt);
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		Log.d("mmm", "" + (0xFF & paramMotionEvent.getAction()));
		switch (0xFF & paramMotionEvent.getAction()) {
		case 4:
		case 0:// 按下
			if (!this.mScalingRunnalable.mIsFinished) {
				this.mScalingRunnalable.abortAnimation();
			}
			this.mLastMotionY = paramMotionEvent.getY();
			this.mActivePointerId = paramMotionEvent.getPointerId(0);
			this.mMaxScale = (this.mScreenHeight / this.mHeaderHeight);
			this.mLastScale = (this.mHeaderContainer.getBottom() / this.mHeaderHeight);
			System.out.println("lgs======0==" + paramMotionEvent.getAction()
					+ "==" + (0xFF & MotionEvent.ACTION_DOWN) + "=="
					+ MotionEvent.ACTION_DOWN + "=="
					+ Integer.toBinaryString(255));
			break;
		case 2:// 移动
			Log.d("mmm", "mActivePointerId" + mActivePointerId);
			System.out.println("lgs======2==" + paramMotionEvent.getAction());
			int j = paramMotionEvent.findPointerIndex(this.mActivePointerId);
			if (j == -1) {
				Log.e("PullToZoomListView", "Invalid pointerId="
						+ this.mActivePointerId + " in onTouchEvent");
			} else {
				if (this.mLastMotionY == -1.0F)
					this.mLastMotionY = paramMotionEvent.getY(j);
				if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight) {
					ViewGroup.LayoutParams localLayoutParams = this.mHeaderContainer
							.getLayoutParams();
					float f = ((paramMotionEvent.getY(j) - this.mLastMotionY + this.mHeaderContainer
							.getBottom()) / this.mHeaderHeight - this.mLastScale)
							/ 2.0F + this.mLastScale;
					if ((this.mLastScale <= 1.0D) && (f < this.mLastScale)) {
						localLayoutParams.height = this.mHeaderHeight;
						this.mHeaderContainer
								.setLayoutParams(localLayoutParams);
						return super.onTouchEvent(paramMotionEvent);
					}
					this.mLastScale = Math.min(Math.max(f, 1.0F),
							this.mMaxScale);
					localLayoutParams.height = ((int) (this.mHeaderHeight * this.mLastScale));
					if (localLayoutParams.height < this.mScreenHeight)
						this.mHeaderContainer
								.setLayoutParams(localLayoutParams);
					this.mLastMotionY = paramMotionEvent.getY(j);
					return true;
				}
				this.mLastMotionY = paramMotionEvent.getY(j);
			}
			break;
		case 1:// 抬起
			reset();
			endScraling();
			System.out.println("lgs======1==" + paramMotionEvent.getAction());
			break;
		case 3:
			int i = paramMotionEvent.getActionIndex();
			this.mLastMotionY = paramMotionEvent.getY(i);
			this.mActivePointerId = paramMotionEvent.getPointerId(i);
			System.out.println("lgs======3==" + paramMotionEvent.getAction());
			break;
		case 5:
			onSecondaryPointerUp(paramMotionEvent);
			this.mLastMotionY = paramMotionEvent.getY(paramMotionEvent
					.findPointerIndex(this.mActivePointerId));
			System.out.println("lgs======5==" + paramMotionEvent.getAction());
			break;
		case 6:
			System.out.println("lgs======6==" + paramMotionEvent.getAction());
		}
		return super.onTouchEvent(paramMotionEvent);
	}

	public void setHeaderViewSize(int paramInt1, int paramInt2) {
		Object localObject = this.mHeaderContainer.getLayoutParams();
		if (localObject == null)
			localObject = new AbsListView.LayoutParams(paramInt1, paramInt2);
		((ViewGroup.LayoutParams) localObject).width = paramInt1;
		((ViewGroup.LayoutParams) localObject).height = paramInt2;
		this.mHeaderContainer
				.setLayoutParams((ViewGroup.LayoutParams) localObject);
		this.mHeaderHeight = paramInt2;
	}

	public void setOnScrollListener(
			AbsListView.OnScrollListener paramOnScrollListener) {
		this.mOnScrollListener = paramOnScrollListener;
	}

	public void setShadow(int paramInt) {
		this.mShadow.setBackgroundResource(paramInt);
	}

	class ScalingRunnalable implements Runnable {
		long mDuration;
		boolean mIsFinished = true;
		float mScale;
		long mStartTime;

		ScalingRunnalable() {
		}

		public void abortAnimation() {
			this.mIsFinished = true;
		}

		public boolean isFinished() {
			return this.mIsFinished;
		}

		public void run() {
			float f2;
			ViewGroup.LayoutParams localLayoutParams;
			if ((!this.mIsFinished) && (this.mScale > 1.0D)) {
				float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) this.mStartTime)
						/ (float) this.mDuration;
				f2 = this.mScale - (this.mScale - 1.0F)
						* PullToZoomListView.sInterpolator.getInterpolation(f1);
				localLayoutParams = PullToZoomListView.this.mHeaderContainer
						.getLayoutParams();
				if (f2 > 1.0F) {
					Log.d("mmm", "f2>1.0");
					localLayoutParams.height = PullToZoomListView.this.mHeaderHeight;
					;
					localLayoutParams.height = ((int) (f2 * PullToZoomListView.this.mHeaderHeight));
					PullToZoomListView.this.mHeaderContainer
							.setLayoutParams(localLayoutParams);
					PullToZoomListView.this.post(this);
					return;
				}
				this.mIsFinished = true;
			}
		}

		public void startAnimation(long paramLong) {
			this.mStartTime = SystemClock.currentThreadTimeMillis();
			this.mDuration = paramLong;
			this.mScale = ((float) (PullToZoomListView.this.mHeaderContainer
					.getBottom()) / PullToZoomListView.this.mHeaderHeight);
			this.mIsFinished = false;
			PullToZoomListView.this.post(this);
		}
	}
}
