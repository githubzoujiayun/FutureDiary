package com.qiuyongchen.futurediary;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Chronometer;
import android.widget.TextView;

public class LearningTimeActivity extends Activity {

	/** ����ؼ�����ر��� */
	private ProgressWheel count_time_spinner;
	private Chronometer chronometer_learning_time;
	private TextView textview_the_time_had_gone;

	final int FREQUENCY = 4;

	/** ����ʱ������ر��� */
	private long date_delayed = 0;
	private Date date_begin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.e("Activity_Learning_time", "before super.onCreate");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_learning_time);

		count_time_spinner = (ProgressWheel) findViewById(R.id.spinner_time_spin);
		chronometer_learning_time = (Chronometer) findViewById(R.id.chronometer_learning_time);
		textview_the_time_had_gone = (TextView) findViewById(R.id.textView_the_time_had_gone);

		Log.e("Activity_Learning_time", "count_time_spinner.spin");

		/** һ�л����������棬�����м�Ľ�������ʼ��ת */
		count_time_spinner.spin();

		/** �����ϵ�chronometer�ؼ���ʼ��ת����ʼ��ʱ */
		chronometer_learning_time.setBase(SystemClock.elapsedRealtime()); // ����

		chronometer_learning_time.start(); // ��ʼ

		/** �л�����Activityʱ��ʱ�� */
		date_begin = getCurrentDate();

		Log.e("Activity_Learning_time", "before get new dbhelper_LT");

		/** �����ݿ��л�ȡ����ʷ�ۼơ�ʱ��  */
		DatabaseHelper_LearningTime dbhelper_LT = new DatabaseHelper_LearningTime(
				getBaseContext());

		SQLiteDatabase mSqliteDatabase = dbhelper_LT.getWritableDatabase();

		Cursor cursor_maxid = mSqliteDatabase.rawQuery("select * from DATE",
				null);

		int date_sum_delayed = 0;

		/** ����ע�����ݿ��Ƿ�Ϊ�� */
		if (cursor_maxid.getCount() > 0) {

			cursor_maxid.moveToLast();

			date_sum_delayed = cursor_maxid.getInt(cursor_maxid
					.getColumnIndex("DATE_SUM_DELAYED"));

		}

		dbhelper_LT.close(); // �ر����ݿ�����ӣ���Ȼ�����ݿ��һֱ��������

		/** �л�����Activityʱ����Ҫ��ʾ��ʷ�ۼ�ʱ�� */
		textview_the_time_had_gone.setText(Integer
				.toString(date_sum_delayed / 3600)
				+ "Сʱ"
				+ Integer.toString((date_sum_delayed % 3600) / 60) + "����");

	}

	/** ����ϵͳ���ذ�ť��������ʱ */
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			/** ������µ��Ƿ��ؼ����ǣ�û�����ˣ�ֻ�ܽ����ʱ��д�����ݿ��ˣ�˭����С�������� */
			Date date_end = getCurrentDate(); // ���ñ�����ʱ��ʱ��
			
			date_delayed = getDateDelayedFrom(date_begin, date_end);
			
			Log.e("LearningTimeActivity", "press the back button");
			
			/** �����ͳ�Ƶ�ʱ��д�����ݿ� */
			saveToDatabase("LearningTime_Database", "DATE", date_begin,
					date_end, date_delayed);

			/** ʹchronometer�ؼ�ֹͣ */
			chronometer_learning_time.stop(); // ֹͣ

			/** ʹProgressWheelֹͣ��ת */
			count_time_spinner.stopSpinning();

			/** ���������ٿ��ǵ���Ӧ������л�Activity */
			Intent intent = new Intent(LearningTimeActivity.this,
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

	/**
	 * �����ĳ��ʱ�俪ʼ�����ñ�����Ϊֹ��ʱ�� ����date_begin��ʾ��ʱ��ʼ��ʱ�� ������date_end��ʾ��ʱ������ʱ�䣬
	 * ���ص����Ժ�������λ��ʱ���
	 */
	public long getDateDelayedFrom(Date date_begin, Date date_end) {

		long delayed = 0;

		delayed = date_end.getTime() - date_begin.getTime();

		return delayed;
	}

	/**
	 * �����ʱ�����ڴ���Sqlite���ݿ��� ��һ������String:���ݿ������ �ڶ��ز���String:���ݿ�ı�����˵���ˣ��������ݿ���������ݿ�
	 * ����������Date:��ʼ���� ���ĸ�����Date:�������� ���������long:��ʼ���ںͽ�������֮���ʱ���ú�������λ��
	 */
	public void saveToDatabase(String database, String table, Date date_begin,
			Date date_end, long date_delayed) {

		DatabaseHelper_LearningTime dbhelper_LT = new DatabaseHelper_LearningTime(
				getBaseContext());

		// �������ݿ�
		SQLiteDatabase mSqliteDatabase = dbhelper_LT.getWritableDatabase();

		// ��ȡ���ݿ������һ������
		Cursor cursor_maxid = mSqliteDatabase.rawQuery("select * from DATE",
				null);

		long date_sum_delayed = 0;

		Log.e("Activity_learinTime", Integer.toString(cursor_maxid.getCount()));

		/** ע�⣬���ݿ����Ϊ�� */
		if (cursor_maxid.getCount() > 0) {

			cursor_maxid.moveToLast();

			date_sum_delayed = cursor_maxid.getInt(cursor_maxid
					.getColumnIndex("DATE_SUM_DELAYED"))
					+ (date_delayed / 1000);

			Log.e("Activity_learinTime", "cursor_getCount_biggerthanzero");
		}

		// ����ContentValues����
		ContentValues values = new ContentValues();
		// ��������ʱ���롱��ʽ�ĸ�ʽ��
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		// ��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��
		values.put("Date_Begin", dateformat.format(date_begin));
		values.put("Date_End", dateformat.format(date_end));
		// �������ݿ��ʱ����������λ
		values.put("Date_Delayed", date_delayed / 1000);
		values.put("DATE_SUM_DELAYED", date_sum_delayed);

		// ����insert�������Ϳ��Խ����ݲ��뵽���ݿ⵱��
		// ��һ������:������
		// �ڶ���������SQl������һ�����У����ContentValues�ǿյģ���ô��һ�б���ȷ��ָ��ΪNULLֵ
		// ������������ContentValues����
		mSqliteDatabase.insert(table, null, values);

		dbhelper_LT.close(); // �ر����ݿ�����ӣ���Ȼ�����ݿ��һֱ��������

	}

}
