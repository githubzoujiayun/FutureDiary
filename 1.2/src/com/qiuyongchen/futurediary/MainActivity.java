package com.qiuyongchen.futurediary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.http.util.EncodingUtils;

import com.qiuyongchen.futurediary.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ScrollView scrollview_show_top_text;
	private LinearLayout linearlayout_show_top_text;
	private TextView textview_show_top_text;
	private EditText edittext_edit_text;
	private Button button_save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MobclickAgent.updateOnlineConfig(getBaseContext());

		scrollview_show_top_text = (ScrollView) findViewById(R.id.scrollView_show_top_text);
		linearlayout_show_top_text = (LinearLayout) findViewById(R.id.linearLayout_show_top_text);
		textview_show_top_text = (TextView) findViewById(R.id.textView_show_top_text);
		edittext_edit_text = (EditText) findViewById(R.id.editText_edit_text);
		button_save = (Button) findViewById(R.id.button_save);

		// 设置监听器
		button_save.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 将文本编辑框里的内容存入文件
				save();
				// 刷新文本编辑框，使其为空
				edittext_edit_text.setText("");
				try {
					// 刷新文本显示框里的内容
					updateTextView();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 使文本显示框滚动到底部
				scrollToBottom(scrollview_show_top_text,
						linearlayout_show_top_text);
			}
		});

		try {
			// 在程序开始运行的时候就对文本显示框中的内容进行刷新
			updateTextView();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 程序刚开始运行时界面还没有立刻显示出来，这时若调用scrollTo会得到错误结果，所以延迟500ms才调用scrollTo
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				// 内层高度超过外层
				int offset = linearlayout_show_top_text.getMeasuredHeight()
						- scrollview_show_top_text.getMeasuredHeight();
				if (offset < 0) {
					offset = 0;
				}
				scrollview_show_top_text.scrollTo(0, offset);
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
		// save the file
		try {

			// MODE_APPEND ： 以增量方式写入已存在文件
			FileOutputStream fileOutputStream = this.openFileOutput(
					getFileName(), MODE_APPEND);
			byte[] b = (getTime() + "\r\n"
					+ edittext_edit_text.getText().toString() + "\r\n\r\n\r\n")
					.getBytes();

			// 将内容写入文件
			try {
				fileOutputStream.write(b);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 关闭文件
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
		// get the filename（得到以日期为名单文件名）
		// format : mm_dd_yyyy.redleaf（格式）
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
		// get the time（得到文本框内容被保存时的时间）
		// format : hh:mm:ss
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
		/*
		 * read text from the file
		 */

		FileInputStream localFileInputStream = this.openFileInput(getFileName());
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
				// 内层高度超过外层
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
