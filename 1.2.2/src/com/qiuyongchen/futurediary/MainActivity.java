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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private CustomerScrollView customerscrollview_show_top_text;
	private LinearLayout linearlayout_show_top_text;
	private TextView textview_show_top_text;
	private EditText edittext_edit_text;
	private Button button_save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MobclickAgent.updateOnlineConfig(getBaseContext());

		customerscrollview_show_top_text = (CustomerScrollView) findViewById(R.id.customerscrollview_show_top_text);
		linearlayout_show_top_text = (LinearLayout) findViewById(R.id.linearLayout_show_top_text);
		textview_show_top_text = (TextView) findViewById(R.id.textView_show_top_text);
		edittext_edit_text = (EditText) findViewById(R.id.editText_edit_text);
		button_save = (Button) findViewById(R.id.button_save);

		// ���ü�����
		button_save.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// ���ı��༭��������ݴ����ļ�
				save();
				// ˢ���ı��༭��ʹ��Ϊ��
				edittext_edit_text.setText("");
				try {
					// ˢ���ı���ʾ���������
					updateTextView();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// ʹ�ı���ʾ��������ײ�
				scrollToBottom(customerscrollview_show_top_text,
						linearlayout_show_top_text);
			}
		});

		customerscrollview_show_top_text.setVerticalScrollBarEnabled(false);
		/*
		 * ScrollView���ع�����  
		 */
		
		customerscrollview_show_top_text.setOverScrollMode(View.OVER_SCROLL_NEVER);
		/*
		 * ������֪��android��scrollview�������ײ��򶥲�ʱ�������ֺܰ������������ɫ�����֣� Ϊ�˺ÿ�һЩ���Ҿ�ȥ����������ɫ��
		 * ����api��
		 * 
		 * public void setOverScrollMode (int mode)
		 * 
		 * Ϊ��ͼ����over-scrollģʽ����Ч��over-scrollģʽ��OVER_SCROLL_ALWAYS��ȱʡֵ����
		 * OVER_SCROLL_IF_CONTENT_SCROLLS
		 * ��ֻ��������ͼ���ݴ������ʱ������over-scrolling����OVER_SCROLL_NEVER
		 * ��ֻ�е���ͼ���Թ���ʱ���������ò������á�
		 * 
		 * ������ע�����������2.3 r1 �������ģ�API Level
		 * 9������over-scroll������Ϊ���Թ����������μ����ӣ�����iPhone�ĵ���ListView������
		 * 
		 * ����
		 * 
		 * mode The new over-scroll mode for this view.
		 * 
		 * 
		 * ���ֻ����2.3��ϵͳ�����ã�2.3���µľ�ֻ�ܼ��������Ƿ�ͷ���ù��� �¼�ʧЧ��
		 */

		try {
			// �ڳ���ʼ���е�ʱ��Ͷ��ı���ʾ���е����ݽ���ˢ��
			updateTextView();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ����տ�ʼ����ʱ���滹û��������ʾ��������ʱ������scrollTo��õ��������������ӳ�500ms�ŵ���scrollTo
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				// �ڲ�߶ȳ������
				int offset = linearlayout_show_top_text.getMeasuredHeight()
						- customerscrollview_show_top_text.getMeasuredHeight();
				if (offset < 0) {
					offset = 0;
				}
				customerscrollview_show_top_text.scrollTo(0, offset);
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

			// MODE_APPEND �� ��������ʽд���Ѵ����ļ�
			FileOutputStream fileOutputStream = this.openFileOutput(
					getFileName(), MODE_APPEND);
			byte[] b = (getTime() + "\r\n"
					+ edittext_edit_text.getText().toString() + "\r\n\r\n\r\n")
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

	String getFileName() {
		// get the filename���õ�������Ϊ�����ļ�����
		// format : mm_dd_yyyy.redleaf����ʽ��
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
		// get the time���õ��ı������ݱ�����ʱ��ʱ�䣩
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
				// �ڲ�߶ȳ������
				int offset = inner.getMeasuredHeight()
						- scroll.getMeasuredHeight();
				if (offset < 0) {
					System.out.println("��λ...");
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}
}