package com.example.qqslidemenu;

import com.example.qqslidemenu.SlideMenu.DragState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
/**
 * 在SlideMenu处于打开的状态下，应该拦截所有的触摸事件
 * @author Administrator
 *
 */
public class MyLinearLayout extends LinearLayout {

	public MyLinearLayout(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public SlideMenu slideMenu;
	public void setSlideMenu(SlideMenu slideMenu) {
		this.slideMenu = slideMenu;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (isSlideMenuOpen()) {
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isSlideMenuOpen()) {
			if (event.getAction()==MotionEvent.ACTION_UP) {
				//需要关闭SlideMenu
				slideMenu.close();
			}
			//需要消费掉事件
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	public boolean isSlideMenuOpen(){
		return slideMenu!=null && slideMenu.getCurrentState()==DragState.Open;
	}
}
