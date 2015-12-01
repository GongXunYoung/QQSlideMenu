package com.example.qqslidemenu.demo;

import com.example.qqslidemenu.ColorUtil;
import com.example.qqslidemenu.R;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DragLayout extends FrameLayout {
	private View redView;
	private View blueView;
	private int redWidth;
	private ViewDragHelper viewDragHelper;
	private int width;
	private int height;
	public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		
		super(context, attrs, defStyleAttr);
		init();
	}

	public DragLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragLayout(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		viewDragHelper = ViewDragHelper.create(this,callback);
	}

	/**
	 * 当DragLayout在布局文件中的结束标签被读取完毕的时候回调该方法
	 * 当回调该方法的时候，viewGroup就能知道当前有几个子View，所以一般用来初始化子View的引用
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		redView=getChildAt(0);
		blueView=getChildAt(1);
	}

//	/**
//	 * 实现对子View的测量
//	 */
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		//测量子View
//		int size=redView.getLayoutParams().width;
////		int size=getResources().getDimensionPixelSize(R.dimen.dp100);
//		int measureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
//		redView.measure(measureSpec, measureSpec);
//	}
	/**
	 * 该方法在onMeasure方法执行完毕之后才执行，所以可以再该方法中获取子View的宽高
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		redWidth = redView.getMeasuredWidth();//100dp
		width=getMeasuredWidth();//当前draglayout的宽度
		height=getMeasuredHeight();//当前draglayout的高度
	}
	
	/**
	 * 摆放子View的位置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int top, int right,
			int bottom) {
//		int left=getMeasuredWidth()/2-redWidth/2;
		int left=0;
		redView.layout(left, 0, redWidth+left, redWidth);
		blueView.layout(left, redView.getBottom(), redWidth+left, redView.getBottom()+redWidth);
		
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//让ViewDragHelper帮助我们判断是否需要拦截
		return viewDragHelper.shouldInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;
	}
	
	private ViewDragHelper.Callback callback=new Callback() {
		private String tag="ViewDragHelper.Callback";

		/**
		 * 用来判断是否需要开始捕获触摸事件
		 * child:当前所触摸的子View
		 * return true:需要捕获,false：忽略不处理
		 */
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			
			return child==blueView||child==redView;
		}
		/**
		 * 当开始捕获view
		 * capturedChild：当前被捕获的view
		 */
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			Log.e(tag, "onViewCaptured");
			super.onViewCaptured(capturedChild, activePointerId);
		}
		/**
		 * 获取view水平方向拖拽范围,然后并不能限制view的拖拽距离,目前该方法的返回值
		 * 会用在计算view进行平移动画的时间上面，最好不要返回0
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			
			return width-redWidth;
		}
		@Override
		public int getViewVerticalDragRange(View child) {
			
			return height-redWidth;
			
		};
		
		/**
		 * 用来控制view水平方向移动
		 * left:ViewDragHelper任务你想让当前child的left变成的值，left=child.getleft()+dx
		 * dx:本次水平方向移动的距离
		 * return :表示我们真正想让child的left变成的值
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (left<0) {
				left=0;
			}else if (left>width-redWidth) {
				left=width-redWidth;
			}
			return left;
		}
		
		/**
		 * 用来控制view垂直方向移动
		 * left:ViewDragHelper任务你想让当前child的top变成的值，top=child.getTop()+dy
		 * dx:本次水平方向移动的距离
		 * return :表示我们真正想让child的top变成的值
		 */
		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			if (top<0) {
				top=0;
			}else if (top>height-redWidth) {
				top=height-redWidth;//限制底部
			}
			return top;
		}
		/**
		 * 当View位置改变时调用,一般在该方法中作伴随移动的逻辑
		 * changeView:当前改变的view
		 * left:changeView最新的left
		 * top:changeView最新的top
		 * dx:本次水平移动的距离
		 * dy:本次垂直移动的距离
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if (changedView==blueView) {
				//需要让redview跟随移动
				redView.layout(redView.getLeft()+dx, redView.getTop()+dy, redView.getRight()+dx, redView.getBottom()+dy);
			}else if (changedView==redView) {
				//需要让blueview跟随移动
				blueView.layout(blueView.getLeft()+dx, blueView.getTop()+dy, blueView.getRight()+dx, blueView.getBottom()+dy);
			}
			//1、计算view移动的百分比
			float fraction=changedView.getLeft()*1f/(width-redWidth);
			Log.e(tag, "fraction="+fraction);
			//2、执行一系列伴随动画
			executeAnim(fraction);
		}
		
		/**
		 * 当手指抬起的时候会执行该方法，一般来实现平移动画
		 * releasedChild:当前抬起的View
		 * xvel:水平方向移动的速度
		 * yvel:垂直方向移动的速度
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			int centerleft=width/2-redWidth/2;
			if (releasedChild.getLeft()<centerleft) {
				//在左半边
				viewDragHelper.smoothSlideViewTo(releasedChild, 0, releasedChild.getTop());
				//刷新,不停的调用computeScroll()方法
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}else{
				//在右半边
				viewDragHelper.smoothSlideViewTo(releasedChild, width-redWidth, releasedChild.getTop());
				//刷新,不停的调用computeScroll()方法
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
				
			}
		}
	};
	
	public void computeScroll() {
		//如果当前动画没有结束，那么继续刷新
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(DragLayout.this);
		}
	}
	
	/**
	 * 执行伴随动画,属性动画
	 * @param fraction
	 */
	protected void executeAnim(float fraction) {
		// fraction:0-1
//		ViewHelper.setScaleX(redView, 1+0.5f*fraction);
//		ViewHelper.setScaleY(redView, 1+0.5f*fraction);
//		ViewHelper.setRotation(redView, 720*fraction);
		ViewHelper.setRotationX(redView, 720*fraction);
//		ViewHelper.setRotationX(blueView, 720*fraction);
//		ViewHelper.setTranslationX(redView, 80*fraction);
		//透明
//		ViewHelper.setAlpha(redView, 1-fraction);
		
		//设置过度颜色的渐变
		redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED, Color.GREEN));
//		setBackgroundColor((int) ColorUtil.evaluateColor(fraction, Color.RED, Color.GREEN));
	};
}
