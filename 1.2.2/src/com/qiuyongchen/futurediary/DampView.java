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
		 * mTouchSlop��һ�����룬��ʾ������ʱ���ֵ��ƶ�Ҫ�����������ſ�ʼ�ƶ��ؼ�
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
		 * onLayout��������l
		 * ,t,r,b�ֱ��Ƿ��ø��ؼ��ľ��ο��ÿռ䣨��ȥmargin��padding�Ŀռ䣩�����Ͻǵ�left��top�Լ����½�right
		 * ��bottomֵ��
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
				 * �÷�����View�ķ��÷�������View��ʵ�֡����ø÷�����Ҫ�������View�ľ��οռ����Ͻ�left��topֵ�����½�right
				 * ��bottomֵ�����ĸ�ֵ������ڸ��ؼ����Եġ����紫����ǣ�10, 10, 100,
				 * 100�������View�ھ��븸�ؼ������Ͻ�λ��(10,
				 * 10)����ʾ����ʾ�Ĵ�С�ǿ����90(����r,b��������Ͻǵ�)�����е�����Բ��֡�
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
		 * ����event������eventΪ�ֻ���Ļ�����¼���װ��Ķ������з�װ�˸��¼���������Ϣ�����紥����λ�á������������Լ�������ʱ��ȡ�
		 * �ö�������û������ֻ���Ļʱ��������
		 * 
		 * ����ֵ���÷����ķ���ֵ�����������Ӧ�¼�����ͬ��ͬ���ǵ��Ѿ������ش����˸��¼��Ҳ�ϣ�������ص������ٴδ���ʱ����true�����򷵻�false
		 * ��
		 * 
		 * �÷���������֮ǰ���ܹ��ķ���ֻ����һ���¼���һ���������������������¼�ȫ����onTouchEvent��������ֻ����������еĶ���ֵ��ͬ��
		 * 
		 * ��Ļ�����£�����Ļ������ʱ�����Զ����ø÷����������¼�����ʱMotionEvent.getAction()��ֵΪMotionEvent.
		 * ACTION_DOWN�������Ӧ�ó�������Ҫ������Ļ�����µ��¼���ֻ�����¸ûص�������Ȼ���ڷ����н��ж������жϼ��ɡ�
		 * 
		 * ��Ļ��̧�𣺵����ر��뿪��Ļʱ�������¼������¼�ͬ����ҪonTouchEvent��������׽��Ȼ���ڷ����н��ж����жϡ���MotionEvent
		 * .getAction()��ֵΪMotionEvent.ACTION_UPʱ����ʾ����Ļ��̧����¼���
		 * 
		 * ����Ļ���϶����÷������������ر�����Ļ�ϻ������¼���ͬ���ǵ���MotionEvent.getAction()
		 * �������ж϶���ֵ�Ƿ�ΪMotionEvent.ACTION_MOVE�ٽ��д���
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
		 * getX()�Ǳ�ʾWidget������������Ͻǵ�x����,��getRawX()�Ǳ�ʾ�������Ļ���Ͻǵ�x����ֵ(ע��:�����Ļ���Ͻ����ֻ���Ļ���Ͻ�
		 * ,����activity�Ƿ���titleBar���Ƿ�ȫ��Ļ),getY(),getRawY()һ���ĵ���
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
