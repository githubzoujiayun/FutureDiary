/**
 * ע����������������������������������������������������������������������������������������������
 * �˴����ԭ��
 * ע����������������������������������������������������������������������������������������������
 */

package com.qiuyongchen.futurediary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * SQLiteOpenHelper��һ�������࣬�����������ݿ�Ĵ����Ͱ汾�������ṩ��������Ĺ���
 * ��һ��getReadableDatabase()��getWritableDatabase
 * ()���Ի��SQLiteDatabase����ͨ���ö�����Զ����ݿ���в���
 * �ڶ����ṩ��onCreate()��onUpgrade()�����ص����������������ٴ������������ݿ�ʱ�������Լ��Ĳ���
 */
public class DatabaseHelper_LearningTime extends SQLiteOpenHelper {
	private static final int VERSION = 1;

	public DatabaseHelper_LearningTime(Context context) {
		// �̳������࣬�������ݿ�����Ҫ��д����ز������������ݿ����ƺͰ汾��
		super(context, "DATA_LEARNING_TIME.db", null, 1);
		// TODO �Զ����ɵĹ��캯�����
	}

	/**
	 * ��SQLiteOpenHelper�����൱�У������иù��캯��
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param name
	 *            ���ݿ�����
	 * @param factory
	 * @param version
	 *            ��ǰ���ݿ�İ汾��ֵ���������������ǵ�����״̬
	 */
	public DatabaseHelper_LearningTime(Context context, String name,
			CursorFactory factory, int version) {
		// ����ͨ��super���ø��൱�еĹ��캯��
		super(context, name, factory, version);
	}

	public DatabaseHelper_LearningTime(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DatabaseHelper_LearningTime(Context context, String name) {
		this(context, name, VERSION);
	}

	// �ú������ڵ�һ�δ������ݿ��ʱ��ִ��
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("create a database");
		// execSQL����ִ��SQL���
		db.execSQL("create table DATE(ID integer primary key autoincrement,DATE_BEGIN TEXT,DATE_END TEXT,DATE_DELAYED integer,DATE_SUM_DELAYED integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		System.out.println("upgrade a database");
	}
}