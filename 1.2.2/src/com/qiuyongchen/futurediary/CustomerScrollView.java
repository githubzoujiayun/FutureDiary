package com.qiuyongchen.futurediary;

/*
 * 抄袭网络上的一段代码，仿ios阻尼效果的scrollview
 * 不会急速回弹，也不会不符合逻辑的可以下拉一大段
 * 是同类仿ios中最相似的，就用这一段了吧
 */
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class CustomerScrollView extends ScrollView {

	Context mContext;
	private View mView;
	private float touchY;
	private int scrollY = 0;
	private boolean handleStop = false;
	private int eachStep = 0;

	private static final int MAX_SCROLL_HEIGHT = 200;// 最大滑动距离
	private static final float SCROLL_RATIO = 0.4f;// 阻尼系数,越小阻力就越大

	public CustomerScrollView(Context context) {
		super(context);
		this.mContext = context;
	}

	public CustomerScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public CustomerScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			this.mView = getChildAt(0);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
			touchY = arg0.getY();
		}
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mView == null) {
			return super.onTouchEvent(ev);
		} else {
			commonOnTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	}

	private void commonOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			if (mView.getScrollY() != 0) {
				handleStop = true;
				animation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float nowY = ev.getY();
			int deltaY = (int) (touchY - nowY);
			touchY = nowY;
			if (isNeedMove()) {
				int offset = mView.getScrollY();
				if (offset < MAX_SCROLL_HEIGHT && offset > -MAX_SCROLL_HEIGHT) {
					mView.scrollBy(0, (int) (deltaY * SCROLL_RATIO));
					handleStop = false;
				}
			}

			break;
		default:
			break;
		}
	}

	private boolean isNeedMove() {
		int viewHight = mView.getMeasuredHeight();
		int srollHight = getHeight();
		int offset = viewHight - srollHight;
		int scrollY = getScrollY();
		if (scrollY == 0 || scrollY == offset) {
			return true;
		}
		return false;
	}

	private void animation() {
		scrollY = mView.getScrollY();
		eachStep = scrollY / 10;
		resetPositionHandler.sendEmptyMessage(0);
	}

	Handler resetPositionHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (scrollY != 0 && handleStop) {
				scrollY -= eachStep;
				if ((eachStep < 0 && scrollY > 0)
						|| (eachStep > 0 && scrollY < 0)) {
					scrollY = 0;
				}
				mView.scrollTo(0, scrollY);
				this.sendEmptyMessageDelayed(0, 5);
			}
		};
	};

}