package com.qiuyongchen.futurediary;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class DampView extends ViewGroup {

	private Scroller mScroller;

	private VelocityTracker mVelocityTracker;

	private static final int TOUCH_STATE_REST = 0;

	private static final int TOUCH_STATE_SCROLLING = 1;

	private int mTouchState = TOUCH_STATE_REST;

	private int mTouchSlop;

	private float mLastMotionY;

	public DampView(Context context, AttributeSet attrs) {

		this(context, attrs, 0);

		mScroller = new Scroller(context);

		/**
		 * mTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件
		 */

		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

	}

	public DampView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

		mScroller = new Scroller(context);

		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
		 * onLayout传下来的l
		 * ,t,r,b分别是放置父控件的矩形可用空间（除去margin和padding的空间）的左上角的left、top以及右下角right
		 * 、bottom值。
		 */

		int childLeft = 0;

		final int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {

			final View childView = getChildAt(i);

			if (childView.getVisibility() != View.GONE) {

				final int childWidth = childView.getMeasuredWidth();

				childView.layout(childLeft, 0, childLeft + childWidth,
						childView.getMeasuredHeight());

				/*
				 * 该方法是View的放置方法，在View类实现。调用该方法需要传入放置View的矩形空间左上角left、top值和右下角right
				 * 、bottom值。这四个值是相对于父控件而言的。例如传入的是（10, 10, 100,
				 * 100），则该View在距离父控件的左上角位置(10,
				 * 10)处显示，显示的大小是宽高是90(参数r,b是相对左上角的)，这有点像绝对布局。
				 */

				childLeft += childWidth;
			}

		}

	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int count = getChildCount();

		for (int i = 0; i < count; i++) {

			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);

		}

		scrollTo(0, 0);

	}

	public void snapToDestination() {

		final int delta = -getScrollY();

		mScroller.startScroll(0, getScrollY(), 0, delta, Math.abs(delta) * 6);

		invalidate();

	}

	public void computeScroll() {

		if (mScroller.computeScrollOffset()) {

			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

			postInvalidate();

		}

	}

	public boolean onTouchEvent(MotionEvent event) {

		/*
		 * 参数event：参数event为手机屏幕触摸事件封装类的对象，其中封装了该事件的所有信息，例如触摸的位置、触摸的类型以及触摸的时间等。
		 * 该对象会在用户触摸手机屏幕时被创建。
		 * 
		 * 返回值：该方法的返回值机理与键盘响应事件的相同，同样是当已经完整地处理了该事件且不希望其他回调方法再次处理时返回true，否则返回false
		 * 。
		 * 
		 * 该方法并不像之前介绍过的方法只处理一种事件，一般情况下以下三种情况的事件全部由onTouchEvent方法处理，只是三种情况中的动作值不同。
		 * 
		 * 屏幕被按下：当屏幕被按下时，会自动调用该方法来处理事件，此时MotionEvent.getAction()的值为MotionEvent.
		 * ACTION_DOWN，如果在应用程序中需要处理屏幕被按下的事件，只需重新该回调方法，然后在方法中进行动作的判断即可。
		 * 
		 * 屏幕被抬起：当触控笔离开屏幕时触发的事件，该事件同样需要onTouchEvent方法来捕捉，然后在方法中进行动作判断。当MotionEvent
		 * .getAction()的值为MotionEvent.ACTION_UP时，表示是屏幕被抬起的事件。
		 * 
		 * 在屏幕中拖动：该方法还负责处理触控笔在屏幕上滑动的事件，同样是调用MotionEvent.getAction()
		 * 方法来判断动作值是否为MotionEvent.ACTION_MOVE再进行处理。
		 */

		if (mVelocityTracker == null) {

			mVelocityTracker = VelocityTracker.obtain();

		}

		mVelocityTracker.addMovement(event);

		final int action = event.getAction();

		final float y = event.getY();

		switch (action) {

		case MotionEvent.ACTION_DOWN:

			if (!mScroller.isFinished()) {

				mScroller.abortAnimation();

			}

			mLastMotionY = y;

			break;

		case MotionEvent.ACTION_MOVE:

			int deltaY = (int) (mLastMotionY - y);

			mLastMotionY = y;

			scrollBy(0, (int) (deltaY / 2.5));

			break;

		case MotionEvent.ACTION_UP:

			snapToDestination();

			mTouchState = TOUCH_STATE_REST;

			break;

		case MotionEvent.ACTION_CANCEL:

			mTouchState = TOUCH_STATE_REST;

			break;

		}

		return true;

	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();

		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {

			return true;

		}

		final float y = ev.getY();
		/*
		 * getX()是表示Widget相对于自身左上角的x坐标,而getRawX()是表示相对于屏幕左上角的x坐标值(注意:这个屏幕左上角是手机屏幕左上角
		 * ,不管activity是否有titleBar或是否全屏幕),getY(),getRawY()一样的道理
		 */

		switch (action) {

		case MotionEvent.ACTION_MOVE:

			final int yDiff = (int) Math.abs(mLastMotionY - y);

			if (yDiff > mTouchSlop) {

				mTouchState = TOUCH_STATE_SCROLLING;

			}

			break;

		case MotionEvent.ACTION_DOWN:

			mLastMotionY = y;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;

			break;

		case MotionEvent.ACTION_CANCEL:

		case MotionEvent.ACTION_UP:

			mTouchState = TOUCH_STATE_REST;

			break;

		}

		return mTouchState != TOUCH_STATE_REST;

	}

	protected void dispatchDraw(Canvas canvas) {

		try {

			super.dispatchDraw(canvas);

		} catch (Exception e) {

		}

	}

}
