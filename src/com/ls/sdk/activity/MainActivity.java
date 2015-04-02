package com.ls.sdk.activity;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import com.ls.sdk.R;
import com.ls.sdk.mview.scrollviewpager.ScrollViewPager;
import com.ls.sdk.mview.scrollviewpager.ScrollViewPager.ViewOnClickCallBack;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ScrollViewPager vPager;
	private List<ImageView> list = new ArrayList<ImageView>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	private void init() {
		vPager = (ScrollViewPager) findViewById(R.id.vp);
		addImageView();
		vPager.setAdapterStart(list, vPager, true);
		vPager.setIsScroll(false);
		vPager.setOnClickListenerView(new ViewOnClickCallBack() {

			@Override
			public boolean onClickCallBack(View curr, MotionEvent event) {
				// Toast.makeText(MainActivity.this, "当前点击了",0).show();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					break;

				case MotionEvent.ACTION_UP:
					switch (curr.getId()) {
					case 0:
						Toast.makeText(MainActivity.this, "当前点击了/0", 0).show();
						break;
					case 1:
						Toast.makeText(MainActivity.this, "当前点击了/1", 0).show();
						break;
					case 3:
						Toast.makeText(MainActivity.this, "当前点击了/3", 0).show();
						break;
					}

					break;
				}
				return true;
			}
		});
	}

	@Override
	protected void onStart() {
		if (vPager != null) {
			vPager.onStart();
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (vPager != null) {
			vPager.onStop();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (vPager != null) {
			vPager.onDestroy();
		}
		super.onDestroy();
	}

	public void addImageView() {

		for (int i = 0; i < 4; i++) {
			ImageView iv = new ImageView(this);
			if (i == 0) {
				iv.setId(0);
				iv.setBackgroundResource(R.drawable.a);
			} else if (i == 1) {
				iv.setId(1);
				iv.setBackgroundResource(R.drawable.b);
			} else if (i == 2) {
				iv.setId(2);
				iv.setBackgroundResource(R.drawable.d);
			} else if (i == 3) {
				iv.setId(3);
				iv.setBackgroundResource(R.drawable.e);
			} else if (i == 4) {
				iv.setId(4);
				iv.setBackgroundResource(R.drawable.f);
			}
			list.add(iv);
		}
	}

}
