package com.qiuyongchen.futurediary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

public class MainActivity extends Activity {
	private DampingScrollView dampingscrollview_show_top_text;
	private LinearLayout linearlayout_show_top_text;
	private TextView textview_show_top_text;
	private EditText edittext_edit_text;
	private Button button_save;
	private Button button_learning_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.e("MainActivity", "nothing more before this statement");

		super.onCreate(savedInstanceState);

		/** 慢慢地加载布局文件 */
		setContentView(R.layout.activity_main);

		/** 拼命的载人“友盟的统计模块” */
		MobclickAgent.updateOnlineConfig(getBaseContext());

		Log.e("MainActivity", "before find view");
		/** 从布局文件里找出相应的控件实例 */
		dampingscrollview_show_top_text = (DampingScrollView) findViewById(R.id.dampingscrollview_show_top_text);
		linearlayout_show_top_text = (LinearLayout) findViewById(R.id.linearLayout_show_top_text);
		textview_show_top_text = (TextView) findViewById(R.id.textView_show_top_text);
		edittext_edit_text = (EditText) findViewById(R.id.editText_edit_text);
		button_save = (Button) findViewById(R.id.button_save);
		button_learning_time = (Button) findViewById(R.id.button_learing_time);

		Log.e("MainActivity", "before the button of save listener");

		/** 设置按钮“圣者度人”的监听器 */
		button_save.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				/** 将文本编辑框里的内容存入文件 */
				save();

				/** 刷新文本编辑框，使其为空 */
				edittext_edit_text.setText("");

				try {

					/** 刷新文本显示框里的内容 */
					updateTextView();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/** 使文本显示框滚动到底部 */
				scrollToBottom(dampingscrollview_show_top_text,
						linearlayout_show_top_text);

			}
		});

		Log.e("MainActivity", "before the button of learning time");

		/** 设置按钮“学习时长”的监听器 */
		button_learning_time.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				Intent it = new Intent(MainActivity.this,
						LearningTimeActivity.class);
				startActivityForResult(it, 0);

				/** 设置两个activity间的切换方式 */
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			}
		});

		Log.e("MainActivity", "before the setVertical");

		/** ScrollView隐藏滚动条 */
		dampingscrollview_show_top_text.setVerticalScrollBarEnabled(false);

		Log.e("MainActivity", "before the scrollview");

		/**
		 * 众所周知，android的scrollview滚动到底部或顶部时，会那种很碍眼讨人厌的颜色栏出现， 为了好看一些，我就去掉了这种颜色栏
		 * 贴出api：
		 * 
		 * public void setOverScrollMode (int mode)
		 * 
		 * 为视图设置over-scroll模式。有效的over-scroll模式有OVER_SCROLL_ALWAYS（缺省值），
		 * OVER_SCROLL_IF_CONTENT_SCROLLS
		 * （只允许当视图内容大过容器时，进行over-scrolling）和OVER_SCROLL_NEVER
		 * 。只有当视图可以滚动时，此项设置才起作用。
		 * 
		 * （译者注：这个函数是2.3 r1 中新增的，API Level
		 * 9。关于over-scroll这里译为弹性滚动，即，参见帖子：类似iPhone的弹性ListView滚动）
		 * 
		 * 参数
		 * 
		 * mode The new over-scroll mode for this view.
		 * 
		 * 
		 * 这个只能在2.3的系统才有用，2.3以下的就只能监听滚动是否到头来让滚动 事件失效了
		 */

		dampingscrollview_show_top_text
				.setOverScrollMode(View.OVER_SCROLL_NEVER);

		try {

			/** 在程序开始运行的时候就对文本显示框中的内容进行刷新 */
			updateTextView();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.e("MainActivity", "before the handler");

		/** 程序刚开始运行时界面还没有立刻显示出来，这时若调用scrollTo会得到错误结果，所以延迟500ms才调用scrollTo */
		Handler handler = new Handler();

		handler.postDelayed(new Runnable() {
			public void run() {

				/** 内层高度超过外层 */
				int offset = linearlayout_show_top_text.getMeasuredHeight()
						- dampingscrollview_show_top_text.getMeasuredHeight();

				if (offset < 0) {
					offset = 0;
				}

				dampingscrollview_show_top_text.scrollTo(0, offset);

			}

		}, 500);

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	void save() {
		/** save the file */
		try {

			// MODE_APPEND ： 以增量方式写入已存在文件
			FileOutputStream fileOutputStream = this.openFileOutput(
					getFileName(), MODE_APPEND);
			byte[] b = (getTime() + "\r\n"
					+ edittext_edit_text.getText().toString() + "\r\n\r\n\r\n")
					.getBytes();

			/** 将内容写入文件 */
			try {
				fileOutputStream.write(b);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/** 关闭文件 */
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	String getFileName() {

		/**
		 * get the filename（得到以日期为名单文件名） format : mm_dd_yyyy.redleaf（格式）
		 */
		Calendar calendar = Calendar.getInstance();
		String filename = "";

		int mMonth = calendar.get(Calendar.MONTH) + 1;
		int mDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
		int mYear = calendar.get(Calendar.YEAR);

		if (mMonth <= 9) {
			filename += "0";
		}

		filename += Integer.toString(mMonth) + "_";

		if (mDay <= 9) {
			filename += "0";
		}

		filename += Integer.toString(mDay) + "_";
		filename += Integer.toString(mYear) + ".redleaf";

		return filename;
	}

	String getTime() {
		/** get the time（得到文本框内容被保存时的时间） format : hh:mm:ss */
		Calendar calendar = Calendar.getInstance();

		String time = "";
		int mHour = calendar.get(Calendar.HOUR_OF_DAY);
		int mMinute = calendar.get(Calendar.MINUTE);
		int mSecond = calendar.get(Calendar.SECOND);

		if (mHour < 10) {
			time += "0";
		}
		time += Integer.toString(mHour) + ":";
		if (mMinute < 10) {
			time += "0";
		}
		time += Integer.toString(mMinute) + ":";
		if (mSecond < 10) {
			time += "0";
		}
		time += Integer.toString(mSecond);

		return time;

	}

	void updateTextView() throws IOException {

		/** read text from the file */
		FileInputStream localFileInputStream = this
				.openFileInput(getFileName());

		int j = localFileInputStream.available();
		byte[] arrayOfByte = new byte[j];

		localFileInputStream.read(arrayOfByte);
		String str = EncodingUtils.getString(arrayOfByte, "UTF-8");
		this.textview_show_top_text.setText(str);
	}

	void scrollToBottom(final ScrollView scroll, final View inner) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (scroll == null || inner == null) {
					return;
				}

				/** 内层高度超过外层 */
				int offset = inner.getMeasuredHeight()
						- scroll.getMeasuredHeight();
				if (offset < 0) {
					System.out.println("定位...");
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}
}
