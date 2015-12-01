package com.example.qqslidemenu;

import java.util.Random;

import com.example.qqslidemenu.SlideMenu.OnDragStateChangedListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 初始化view
		SlideMenu slideMenu=(SlideMenu) findViewById(R.id.slideMenu);
		final ListView menuListView = (ListView) findViewById(R.id.menu_listview);
		final ListView mainListView = (ListView) findViewById(R.id.main_listview);
		final ImageView iv_head=(ImageView) findViewById(R.id.iv_head);
		MyLinearLayout layout=(MyLinearLayout) findViewById(R.id.my_layout);
		
		layout.setSlideMenu(slideMenu);
		// 设置数据
		menuListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Constant.sCheeseStrings){
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				TextView textView=(TextView) super.getView(position, convertView, parent);
				textView.setTextColor(Color.WHITE);
				return textView;
			}
		});
		mainListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Constant.NAMES));
		
	
		slideMenu.setOnDragStateChangedListener(new OnDragStateChangedListener() {
			
			@Override
			public void onOpen() {
				menuListView.smoothScrollToPosition(new Random().nextInt(menuListView.getCount()));
			}
			
			@Override
			public void onDraging(float fraction) {
				ViewHelper.setAlpha(iv_head, 1-fraction);
			}
			
			@Override
			public void onClose() {
				//让小人在抖动
				ViewPropertyAnimator
				.animate(iv_head)
				.translationXBy(50)
				.setInterpolator(new CycleInterpolator(4))
				.setDuration(500)
				.start();
			}
		});
	}

}
