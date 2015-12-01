package com.example.qqslidemenu;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SlideMenu extends FrameLayout {

	private View menuView;
	private View mainView;
	private int menuWidth;// 菜单宽
	private int mainWidth;// 主界面宽
	private int width;// 当前slidemenu宽度
	private float dragRange;
	private ViewDragHelper viewDragHelper;
	private FloatEvaluator floatEvaluator;// 浮点计算器
	private DragState mState = DragState.Close;

	enum DragState {
		Open, Close
	}

	public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideMenu(Context context) {
		super(context);
		init();
	}

	private void init() {
		floatEvaluator = new FloatEvaluator();
		viewDragHelper = ViewDragHelper.create(this, callback);

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (getChildCount() != 2) {
			throw new IllegalArgumentException(
					"Slidemenu can only have two child");
		}
		menuView = getChildAt(0);
		mainView = getChildAt(1);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		menuWidth = menuView.getMeasuredWidth();
		mainWidth = mainView.getMeasuredWidth();
		width = getMeasuredWidth();
		dragRange = width * 0.6f;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		return result;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	private ViewDragHelper.Callback callback = new Callback() {

		private String tag;

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// TODO Auto-generated method stub
			return child == menuView || child == mainView;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == mainView) {
				if (left < 0) {
					left = 0;// 限制左边
				} else if (left > dragRange) {
					left = (int) dragRange;
				}
			}
			return left;
		}

		@Override
		public int getViewHorizontalDragRange(View child) {

			return (int) dragRange;
		}

		/**
		 * 实现伴随移动
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if (changedView == menuView) {
				// 手动固定住menuView，不让他移动
				menuView.layout(0, 0, menuWidth, menuView.getMeasuredHeight());
				// 让mainView作伴随移动
				Log.e(tag, "dx:" + dx);
				// 让mainView作移动，并且需要再次限制边界
				int newLeft = mainView.getLeft() + dx;
				if (newLeft < 0) {
					newLeft = 0;
				}
				if (newLeft > dragRange) {
					newLeft = (int) dragRange;
				}
				mainView.layout(newLeft, mainView.getTop(),
						newLeft + mainWidth, mainView.getBottom());
			}
			// 1、计算移动的百分比
			float fraction = mainView.getLeft() / dragRange;
			// 2、根据移动的百分比执行伴随动画
			executeAnim(fraction);
			// 3、根据fraction判断当前状态
			if (fraction == 0f && mState != DragState.Close) {
				mState = DragState.Close;
				if (listener != null) {
					listener.onClose();
				}

			} else if (fraction == 1f && mState != DragState.Open) {
				mState = DragState.Open;
				if (listener != null) {
					listener.onOpen();
				}
			}

			// 回调onDragging方法
			if (listener != null) {
				listener.onDraging(fraction);
			}
		}

		/**
		 * 执行伴随动画
		 * 
		 * @param fraction
		 */
		private void executeAnim(float fraction) {
			// fraction 0-1
			// 缩小mainView
			// float scaleValue=0.8f+0.2f*(1-fraction);//1-0.8
			ViewHelper.setScaleX(mainView,
					floatEvaluator.evaluate(fraction, 1f, 0.8f));
			ViewHelper.setScaleY(mainView,
					floatEvaluator.evaluate(fraction, 1f, 0.8f));
			// 移动、放大,透明menuView
			ViewHelper.setTranslationX(menuView,
					floatEvaluator.evaluate(fraction, -menuWidth / 2, 0));
			ViewHelper.setScaleX(menuView,
					floatEvaluator.evaluate(fraction, 0.5f, 1f));
			ViewHelper.setScaleY(menuView,
					floatEvaluator.evaluate(fraction, 0.5f, 1f));
			ViewHelper.setAlpha(menuView,
					floatEvaluator.evaluate(fraction, 0.3f, 1f));

			// 给SlideMenu的背景覆盖一层遮罩效果
			getBackground().setColorFilter(
					(Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,
							Color.TRANSPARENT), Mode.SRC_OVER);
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if (mainView.getLeft() < dragRange / 2) {
				// 在左边
				close();
			} else {
				// 在右半边
				open();
			}
			if (xvel > 100 && mState == DragState.Close) {
				// 如果向右滑动速度大于100，就打开
				open();
			}
			if (xvel < -100 && mState == DragState.Open) {
				// 如果向左滑动速度大于100，就打关闭
				close();
			}
		}

	};

	/**
	 * 打开slideMenu
	 */
	public void open() {
		viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange,
				mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}

	/**
	 * 关闭slideMenu
	 */
	public void close() {
		viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}

	public void computeScroll() {
		// 如果当前动画没有结束，那么继续刷新
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		}
	}

	private OnDragStateChangedListener listener;

	public void setOnDragStateChangedListener(
			OnDragStateChangedListener listener) {
		this.listener = listener;
	}

	/**
	 * 定义接口回调
	 */
	public interface OnDragStateChangedListener {
		/**
		 * 打开的回调
		 */
		void onOpen();

		/**
		 * 关闭的回调
		 */
		void onClose();

		/**
		 * 拖拽过程中的回调
		 */
		void onDraging(float fraction);
	}

	/**
	 * 
	 * @return
	 */
	public DragState getCurrentState() {
		return mState;
	}

}
