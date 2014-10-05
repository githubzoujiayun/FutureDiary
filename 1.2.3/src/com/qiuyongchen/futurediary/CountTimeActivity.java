package com.qiuyongchen.futurediary;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class CountTimeActivity extends Activity {

	/** ����ؼ�����ر��� */
	private ProgressWheel count_time_spinner;
	private Button button_stop;

	private int FREQUENCY = 4;
	private int TIME_PER_CHANGE = 1000 / FREQUENCY;

	/** ����ʱ������ر��� */
	private long date_delayed = 0;
	private Date date_begin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_count_time);

		count_time_spinner = (ProgressWheel) findViewById(R.id.spinner_time_spin);

		button_stop = (Button) findViewById(R.id.button_stop);

		/** һ�л����������棬�����м�Ľ�������ʼ��ת */
		count_time_spinner.spin();

		/*
		 * final Handler handler = new Handler(); Runnable
		 * runnable_progresswheel_increse_one_degree = new Runnable() { public
		 * void run() { count_time_spinner.incrementProgress();
		 * handler.postDelayed(this, TIME_PER_CHANGE); } };
		 * handler.post(runnable_progresswheel_increse_one_degree);
		 */

		date_begin = getCurrentDate();

		/** ���ð�ť��stop���ļ����� */
		button_stop.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				date_delayed = getDateDelayedFrom(date_begin);

				button_stop.setText("������" + Float.toString(date_delayed / 1000)
						+ "s");

			}
		});
	}

	/** ����ϵͳ���ذ�ť��������ʱ */
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

	/** ��ȡ�����ô˺���ʱ��ϵͳʱ�䡱 */
	public Date getCurrentDate() {

		Date d = new Date();

		return d;
	}

	/** �����ĳ��ʱ�俪ʼ�����ñ�����Ϊֹ��ʱ������lastDate��ʾ��ʱ��ʼ��ʱ�䣬 ���ص����Ժ�������λ��ʱ��� */
	public long getDateDelayedFrom(Date lastDate) {

		Date date_end = getCurrentDate(); // ���ñ�����ʱ��ʱ��

		long delayed = 0;

		delayed = date_end.getTime() - lastDate.getTime();

		return delayed;
	}
}
