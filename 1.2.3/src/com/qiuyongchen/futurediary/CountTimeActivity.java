package com.qiuyongchen.futurediary;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class CountTimeActivity extends Activity {

	/** 界面控件的相关变量 */
	private ProgressWheel count_time_spinner;
	private Button button_stop;

	private int FREQUENCY = 4;
	private int TIME_PER_CHANGE = 1000 / FREQUENCY;

	/** 计算时间差的相关变量 */
	private long date_delayed = 0;
	private Date date_begin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_count_time);

		count_time_spinner = (ProgressWheel) findViewById(R.id.spinner_time_spin);

		button_stop = (Button) findViewById(R.id.button_stop);

		/** 一切换到阅历界面，就让中间的进度条开始旋转 */
		count_time_spinner.spin();

		/*
		 * final Handler handler = new Handler(); Runnable
		 * runnable_progresswheel_increse_one_degree = new Runnable() { public
		 * void run() { count_time_spinner.incrementProgress();
		 * handler.postDelayed(this, TIME_PER_CHANGE); } };
		 * handler.post(runnable_progresswheel_increse_one_degree);
		 */

		date_begin = getCurrentDate();

		/** 设置按钮“stop”的监听器 */
		button_stop.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				date_delayed = getDateDelayedFrom(date_begin);

				button_stop.setText("持续了" + Float.toString(date_delayed / 1000)
						+ "s");

			}
		});
	}

	/** 当“系统返回按钮”被按下时 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			Intent intent = new Intent(CountTimeActivity.this,
					MainActivity.class);

			setResult(RESULT_OK, intent);

			finish();

			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);

			return false;
		}

		return false;
	}

	/** 获取“调用此函数时的系统时间” */
	public Date getCurrentDate() {

		Date d = new Date();

		return d;
	}

	/** 计算从某个时间开始到调用本函数为止的时间差，参数lastDate表示计时开始的时间， 返回的是以毫秒做单位的时间差 */
	public long getDateDelayedFrom(Date lastDate) {

		Date date_end = getCurrentDate(); // 调用本函数时的时间

		long delayed = 0;

		delayed = date_end.getTime() - lastDate.getTime();

		return delayed;
	}
}
