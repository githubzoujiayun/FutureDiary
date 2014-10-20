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

	/** 界面控件的相关变量 */
	private ProgressWheel count_time_spinner;
	private Chronometer chronometer_learning_time;
	private TextView textview_the_time_had_gone;

	final int FREQUENCY = 4;

	/** 计算时间差的相关变量 */
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

		/** 一切换到阅历界面，就让中间的进度条开始旋转 */
		count_time_spinner.spin();

		/** 界面上的chronometer控件开始运转，开始计时 */
		chronometer_learning_time.setBase(SystemClock.elapsedRealtime()); // 清零

		chronometer_learning_time.start(); // 开始

		/** 切换到本Activity时的时间 */
		date_begin = getCurrentDate();

		Log.e("Activity_Learning_time", "before get new dbhelper_LT");

		/** 从数据库中获取“历史累计”时间  */
		DatabaseHelper_LearningTime dbhelper_LT = new DatabaseHelper_LearningTime(
				getBaseContext());

		SQLiteDatabase mSqliteDatabase = dbhelper_LT.getWritableDatabase();

		Cursor cursor_maxid = mSqliteDatabase.rawQuery("select * from DATE",
				null);

		int date_sum_delayed = 0;

		/** 必须注意数据库是否为空 */
		if (cursor_maxid.getCount() > 0) {

			cursor_maxid.moveToLast();

			date_sum_delayed = cursor_maxid.getInt(cursor_maxid
					.getColumnIndex("DATE_SUM_DELAYED"));

		}

		dbhelper_LT.close(); // 关闭数据库的连接，不然该数据库会一直被锁死的

		/** 切换到本Activity时，需要显示历史累计时间 */
		textview_the_time_had_gone.setText(Integer
				.toString(date_sum_delayed / 3600)
				+ "小时"
				+ Integer.toString((date_sum_delayed % 3600) / 60) + "分钟");

	}

	/** 当“系统返回按钮”被按下时 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			/** 如果按下的是返回键，那，没方法了，只能将这段时间写入数据库了，谁叫你小子命不好 */
			Date date_end = getCurrentDate(); // 调用本函数时的时间
			
			date_delayed = getDateDelayedFrom(date_begin, date_end);
			
			Log.e("LearningTimeActivity", "press the back button");
			
			/** 把这次统计的时间写入数据库 */
			saveToDatabase("LearningTime_Database", "DATE", date_begin,
					date_end, date_delayed);

			/** 使chronometer控件停止 */
			chronometer_learning_time.stop(); // 停止

			/** 使ProgressWheel停止运转 */
			count_time_spinner.stopSpinning();

			/** 接下来，再考虑到底应该如何切换Activity */
			Intent intent = new Intent(LearningTimeActivity.this,
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

	/**
	 * 计算从某个时间开始到调用本函数为止的时间差， 参数date_begin表示计时开始的时间 参数，date_end表示计时结束的时间，
	 * 返回的是以毫秒做单位的时间差
	 */
	public long getDateDelayedFrom(Date date_begin, Date date_end) {

		long delayed = 0;

		delayed = date_end.getTime() - date_begin.getTime();

		return delayed;
	}

	/**
	 * 把相关时间日期存入Sqlite数据库中 第一个参数String:数据库的名称 第二关参数String:数据库的表名，说白了，就是数据库里的子数据库
	 * 第三个参数Date:开始日期 第四个参数Date:结束日期 第五个参数long:开始日期和结束日期之间的时间差（用毫秒作单位）
	 */
	public void saveToDatabase(String database, String table, Date date_begin,
			Date date_end, long date_delayed) {

		DatabaseHelper_LearningTime dbhelper_LT = new DatabaseHelper_LearningTime(
				getBaseContext());

		// 创建数据库
		SQLiteDatabase mSqliteDatabase = dbhelper_LT.getWritableDatabase();

		// 获取数据库里最后一行数据
		Cursor cursor_maxid = mSqliteDatabase.rawQuery("select * from DATE",
				null);

		long date_sum_delayed = 0;

		Log.e("Activity_learinTime", Integer.toString(cursor_maxid.getCount()));

		/** 注意，数据库可能为空 */
		if (cursor_maxid.getCount() > 0) {

			cursor_maxid.moveToLast();

			date_sum_delayed = cursor_maxid.getInt(cursor_maxid
					.getColumnIndex("DATE_SUM_DELAYED"))
					+ (date_delayed / 1000);

			Log.e("Activity_learinTime", "cursor_getCount_biggerthanzero");
		}

		// 创建ContentValues对象
		ContentValues values = new ContentValues();
		// “年月日时分秒”格式的格式器
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		// 向该对象中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("Date_Begin", dateformat.format(date_begin));
		values.put("Date_End", dateformat.format(date_end));
		// 存入数据库的时长以秒做单位
		values.put("Date_Delayed", date_delayed / 1000);
		values.put("DATE_SUM_DELAYED", date_sum_delayed);

		// 调用insert方法，就可以将数据插入到数据库当中
		// 第一个参数:表名称
		// 第二个参数：SQl不允许一个空列，如果ContentValues是空的，那么这一列被明确的指明为NULL值
		// 第三个参数：ContentValues对象
		mSqliteDatabase.insert(table, null, values);

		dbhelper_LT.close(); // 关闭数据库的连接，不然该数据库会一直被锁死的

	}

}
