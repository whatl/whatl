package com.ls.sdk.mview.scrollviewpager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.ls.sdk.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 无线循环的ViewPager 使用方法: 1 找到当前控件的对象 2 调用一系列set方法，例如：是否需要滚动，是否需要点击事件 3
 * 调用setAdaPterStart(...)方法 注意事项: 1 不能自己调用setAdapter(); 2
 * 不允许实现onPagerChangerListener()接口，如果有需要可以调用setOnPagerChangerListenerCallBack();
 * 可以设置是否 要显示小圆点，可以关闭轮波，但却关闭不了无线循环
 * 
 * 当activity结束的时候建议调用该方法的生命周期
 * 
 * @author ls
 * 
 */
public class ScrollViewPager extends ViewPager {
	Context context;
	/** 为true需要滚动 */
	private boolean isScroll = true;
	private boolean roundDot = false;
	private ScheduledExecutorService scheduledExecutorService;
	private int currentIndex = 0;
	private int startTime = 4, gapTime = 3;
	/**
	 * 这个和isScroll连个的区别是：markScroll其实线程还在执行，但是当前页不会变，isScroll是线程结束导致ViewPager不轮询
	 */
	private boolean markScroll = true;
	private boolean flag = true;
	private OnPagerChangerListenerCallBack changerCallBack;
	private List<ImageView> lists;
	private ViewOnClickCallBack click;
	private boolean isRun = false;
	private LinearLayout ll_scroll;
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 00:// 当数据初始化的时候
				startScrollThread(startTime, gapTime);
				break;
			case 11:// 轮询的时候
				if (markScroll) {
					currentIndex++;
					setCurrentItem(currentIndex, true);
				}
				break;
			}
		};
	};
	public ScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ScrollViewPager(Context context) {
		super(context);
		init(context);
	}

	public void init(Context context) {
		this.context = context;
		this.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				currentIndex = position;
				if(roundDot&&ll_scroll!=null){
					int childCount = ll_scroll.getChildCount();
					if (lists.size() > 1) {// 页面改变改动小圆点
						for (int i = 0; i < childCount; i++) {
							View view = ll_scroll.getChildAt(i);
							view.setEnabled(false);
						}
						if (position == 0) {
							View views = ll_scroll.getChildAt(lists.size() - 3);
							if (views != null) {
								views.setEnabled(true);
							}
						} else if (position == lists.size() - 1) {
							View views = ll_scroll.getChildAt(0);
							if (views != null) {
								views.setEnabled(true);
							}
						} else {
							View views = ll_scroll.getChildAt(position - 1);
							if (views != null) {
								views.setEnabled(true);
							}
						}
					}
				}
				if (changerCallBack != null) {
					changerCallBack.onPageSelected(position);
				}
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (lists.size() < 2) {// 不需要轮询
					if (changerCallBack != null) {
						changerCallBack.onPageScrolled(arg0, arg1, arg2);
					}
					return;
				}
				// 这个地方是让他无线循环
				if (arg1 == 0.0) {
					if (arg0 == 0) {
						setCurrentItem(lists.size() - 2, false);
					} else if (arg0 == lists.size() - 1) {
						setCurrentItem(1, false);
					}
				}// 这个判断是为了防止他有的时候不执行上面的if，导致卡在当前的页面
				else if (arg1 > 0.98 && arg0 == lists.size() - 2) {
					setCurrentItem(1, false);
				} else if (arg0 == 0 && arg1 < 0.00999) {
					setCurrentItem(lists.size() - 2, false);
				}
				if (changerCallBack != null) {
					changerCallBack.onPageScrolled(arg0, arg1, arg2);
				}

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (changerCallBack != null) {
					changerCallBack.onPageScrollStateChanged(arg0);
				}
			}
		});
	}

	/**
	 * 设置当前是否滚动，如果isScroll设置了false，当前的设置无效
	 */
	public void setMarkScroll(boolean flag) {
		markScroll = flag;
	}

	/**
	 * 
	 * @param li
	 *            数据
	 * @param scr
	 *            对象
	 * @param roundDot
	 *            true就是显示小圆点
	 * @throws Exception
	 */
	public void setAdapterStart(List<ImageView> li, ScrollViewPager scr,boolean roundDot) {
		this.roundDot=roundDot;
		if(roundDot){//在父控件中添加了一个线性布局然后现形布局中添加几个小圆点
		 ll_scroll = new LinearLayout(context);
		ll_scroll.setId(10);
			ViewGroup parent = (ViewGroup) scr.getParent();
				parent.addView(ll_scroll);
				int child = parent.getChildCount();
				for (int i = 0; i <child; i++) {
					View view = parent.getChildAt(i);
					int id = view.getId();
					if(id==10){
						//默认是添加到图片的下边的,因为他的父控件是垂直的线性布局，这个地方决定了小圆点显示的位置
						ll_scroll = (LinearLayout) parent.getChildAt(i);
						RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) ll_scroll.getLayoutParams();
						layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.vp);//让当前的这个布局在ViewPager的上方
						layoutParams.bottomMargin=10;//距离底部有个dp
						layoutParams.width=layoutParams.MATCH_PARENT;
						ll_scroll.setGravity(Gravity.CENTER);//此处是让空间里面的东西居中对齐
						ll_scroll.setLayoutParams(layoutParams);
						
					}
				}
		}
		if (li.size() < 2) {
			lists = li;
			for (int i = 0; i < lists.size(); i++) {
				ImageView iv = lists.get(i);
				iv.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View curr, MotionEvent arg1) {
						if(click!=null){
							return click.onClickCallBack(curr, arg1);
						}
						return true;
					}
				});
			}
		} else {
				if (li != null && li.size() > 1) {
					ImageView ivStart = li.get(0);
					int ivStartId = ivStart.getId();
					Drawable ivStartbackground = ivStart.getBackground();// 有可能用户图片设置的背景是不同的数据类型
					Drawable ivStartDrawable = ivStart.getDrawable();
					ImageView ivEnd = li.get(li.size() - 1);
					int ivEndId = ivEnd.getId();
					Drawable ivEndbackground = ivEnd.getBackground();
					Drawable ivEndDrawable = ivEnd.getDrawable();
					lists = new ArrayList<ImageView>();
					for (int i = 0; i < li.size() + 2; i++) {
						ImageView iv = new ImageView(context);
						if (i == 0) {
							if (ivEndbackground != null) {// 设置的是最后一张要显示的图片
								iv.setBackgroundDrawable(ivEndbackground);
							} else if (ivEndDrawable != null) {
								iv.setBackgroundDrawable(ivEndDrawable);
							} else {
								Log.e("ScrollView",
										"没有获取到数据图片，图片不能正常显示,请直接在这里显示图片");
								ivEnd.setDrawingCacheEnabled(true);
								ivEnd.buildDrawingCache();
								Bitmap bitmap = ivEnd.getDrawingCache();
								iv.setImageBitmap(bitmap);
								
							}
							iv.setId(ivEndId);
							lists.add(iv);
							continue;
						} else if (i == li.size() + 1) {// 设置的是第一张要显示的图片
							if (ivStartbackground != null) {
								iv.setBackgroundDrawable(ivStartbackground);
							} else if (ivStartDrawable != null) {
								iv.setBackgroundDrawable(ivStartDrawable);
							} else {
								Log.e("ScrollView", "没有获取到数据图片，图片不能正常显示");
								ivStart.setDrawingCacheEnabled(true);
								ivStart.buildDrawingCache();
								Bitmap bitmap = ivStart.getDrawingCache();
								iv.setImageBitmap(bitmap);
							}
							iv.setId(ivStartId);
							lists.add(iv);
							continue;
						}
						if (i > 0 && i <= li.size() + 1) {
							ImageView ivs = li.get(i - 1);
							if (ivs.getBackground() != null) {
								iv.setBackgroundDrawable(ivs.getBackground());
							} else if (ivs.getDrawable() != null) {
								iv.setBackgroundDrawable(ivs.getDrawable());
							} else {// 设置所有要显示的图片
								Log.e("ScrollView", "没有获取到数据图片，图片不能正常显示");
								ivs.setDrawingCacheEnabled(true);
								ivs.buildDrawingCache();
								Bitmap bitmap = ivs.getDrawingCache();
								iv.setImageBitmap(bitmap);
							}
							iv.setClickable(true);
							iv.setOnTouchListener(new OnTouchListener() {
								@Override
								public boolean onTouch(View curr,
										MotionEvent event) {
									switch (event.getAction()) {
									case MotionEvent.ACTION_UP:
										markScroll = true;
										break;

									case MotionEvent.ACTION_DOWN:
										markScroll = false;
										break;
									case MotionEvent.ACTION_CANCEL:
										if (markScroll == false) {
											if (flag) {
											}
											new Thread(new Runnable() {

												@Override
												public void run() {
													try {
														flag = false;
														Thread.sleep(1000);
														markScroll = true;
														flag = true;
													} catch (InterruptedException e) {
														e.printStackTrace();
														markScroll = true;
														flag = true;
													}
												}
											}).start();
										}
										break;
									}
									if (click != null) {
										return click.onClickCallBack(curr,
												event);
									}
									return true;
								}
							});
							int id = ivs.getId();
							iv.setId(id);
							lists.add(iv);
							if(roundDot){//是否需要显示点
								setRoundDot();
							}
						}
					}
				}
				handler.sendEmptyMessage(00);
		}
		if (lists != null && lists.size() > 0) {
			scr.setAdapter(new HeaderAdapter(lists));
			scr.setCurrentItem((lists.size() == 1) ? 0 : 1);
		}
	}

	public void setOnPagerChangerListenerCallBack(
			OnPagerChangerListenerCallBack changerCallBack) {
		this.changerCallBack = changerCallBack;
	}

	/**
	 * 设置是否需要滚动,默认滚动,并且setadapter中unScrollEndless=true时
	 * 
	 */
	public void setIsScroll(boolean isScroll) {
		if (isScroll == false) {// 不需要滚动，让滚动的线程结束
			this.isScroll = isScroll;
			closeScrollThread();
			if (handler != null) {
				handler.removeCallbacksAndMessages(null);
			}
		} else {
			if (this.isScroll != true) {// 如果已经是滚动，就不需要在走此方法
				this.isScroll = isScroll;
				if (scheduledExecutorService == null) {
					startScrollThread(startTime, gapTime);
				}
			}
		}
	}

	/**
	 * 设置是否需要需要显示小圆点
	 * 
	 */
	public void setIsRoundDot(boolean roundDot) {
		this.roundDot = roundDot;
	}

	private class ViewPagerTask implements Runnable {

		@Override
		public void run() {
			handler.sendEmptyMessage(11);
		}
	}

	class HeaderAdapter extends PagerAdapter {

		public HeaderAdapter(List<ImageView> li) {
		}

		@Override
		public int getCount() {
			return lists.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);// 完全溢出view,避免数据多时出现重复现象
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(lists.get(position), 0);
			return lists.get(position);
		}
	}

	/**
	 * 让ImageView具备响应的点击事件
	 * 
	 * @param click
	 */
	public void setOnClickListenerView(ViewOnClickCallBack click) {
		this.click = click;
	}

	/***
	 * 设置滚动的时间
	 * 
	 * @param startTime
	 *            第一次开始执行轮询
	 * @param gapTime
	 *            每次轮询到下一张图片的时候间隙时间
	 */
	public void setScrollTime(int startTime, int gapTime) {
		this.startTime = startTime;
		this.gapTime = gapTime;
	}

	public interface ViewOnClickCallBack {
		/**
		 * 点击事件的返回值决定了当前的View的event up是否响应,区分View时用setId()区分所以一定要传setID
		 * 
		 * @param curr传进来的View
		 * @param event
		 *            当前的点击事件
		 * @return
		 */
		public boolean onClickCallBack(View curr, MotionEvent event);
	}

	/**
	 * 当前的activity死亡的时候，建议调用此方法，结束轮询的线程
	 */
	public void onDestroy() {
		closeScrollThread();
	}

	/**
	 * 建议调用此生命周期在onstart();
	 */
	public void onStart() {
		if (isRun) {// 第一次restart不需要执行
			if (isScroll) {
				startScrollThread(startTime, gapTime);
			}
		}
	}

	/**
	 * 在界面不可见的时候建议调用此生命周期
	 */
	public void onStop() {
		if (isScroll) {
			closeScrollThread();
			isRun = true;
		}
	}

	/**
	 * 关闭所有的线程
	 */
	private void closeScrollThread() {
		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
			scheduledExecutorService = null;
			if (handler != null) {
				handler.removeCallbacksAndMessages(null);
			}
		}
	}

	/**
	 * 开启轮询的线程
	 * 
	 * @param startTime
	 *            刚开始的时间
	 * @param gapTime
	 *            每次轮询的间隔时间
	 */
	private void startScrollThread(int startTime, int gapTime) {
		closeScrollThread();
		if (gapTime < 2) {
			gapTime = 2;
		}
		if (startTime < 0) {
			startTime = 2;
		}
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 设定执行线程计划,初始4s延迟,每次任务完成后延迟3s再执行一次任务
		scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(),
				startTime, gapTime, TimeUnit.SECONDS);
	}

	public interface OnPagerChangerListenerCallBack {
		public void onPageSelected(int currPager);

		public void onPageScrolled(int currPager, float arg1, int arg2);

		public void onPageScrollStateChanged(int arg0);
	}
	private void setRoundDot(){
		if (lists.size() > 1) {// 当图片dayu两张的时候
			View view = new View(context);
			android.widget.LinearLayout.LayoutParams lay = new android.widget.LinearLayout.LayoutParams(15,15);
			lay.leftMargin = 7;
			view.setBackgroundResource(R.drawable.select_radio);
			view.setLayoutParams(lay);
			view.setEnabled(false);
			ll_scroll.addView(view);
		}
	}

}
