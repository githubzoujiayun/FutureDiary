package com.qiuyongchen.futurediary;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import com.qiuyongchen.futurediary.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private EditText edittext_m;
	private Button button_m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MobclickAgent.updateOnlineConfig(getBaseContext());
		
		edittext_m = (EditText) findViewById(R.id.editText1);
		button_m = (Button) findViewById(R.id.button1);

		// ���ü�����
		button_m.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				save();
				edittext_m.setText("");
			}
		});

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}


	
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	// save the file
	@SuppressWarnings("deprecation")
	void save() {
		try {

			// MODE_APPEND �� ��������ʽд���Ѵ����ļ�
			FileOutputStream fileOutputStream = this.openFileOutput(
					getFileName(), MODE_APPEND);
			byte[] b = (getTime() + "\r\n" + edittext_m.getText().toString() + "\r\n\r\n\r\n")
					.getBytes();

			// ������д���ļ�
			try {
				fileOutputStream.write(b);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// �ر��ļ�
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

	// get the filename���õ�������Ϊ�����ļ�����
	// format : mm_dd_yyyy.redleaf����ʽ��
	String getFileName() {
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

	// get the time���õ��ı������ݱ�����ʱ��ʱ�䣩
	// format : hh:mm:ss
	String getTime() {
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

}
