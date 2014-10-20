/**
 * 注！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
 * 此代码非原创
 * 注！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
 */

package com.qiuyongchen.futurediary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * SQLiteOpenHelper是一个辅助类，用来管理数据库的创建和版本他，它提供两个方面的功能
 * 第一，getReadableDatabase()、getWritableDatabase
 * ()可以获得SQLiteDatabase对象，通过该对象可以对数据库进行操作
 * 第二，提供了onCreate()、onUpgrade()两个回调函数，允许我们再创建和升级数据库时，进行自己的操作
 */
public class DatabaseHelper_LearningTime extends SQLiteOpenHelper {
	private static final int VERSION = 1;

	public DatabaseHelper_LearningTime(Context context) {
		// 继承至父类，创建数据库所需要填写的相关参数、包括数据库名称和版本等
		super(context, "DATA_LEARNING_TIME.db", null, 1);
		// TODO 自动生成的构造函数存根
	}

	/**
	 * 在SQLiteOpenHelper的子类当中，必须有该构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param name
	 *            数据库名称
	 * @param factory
	 * @param version
	 *            当前数据库的版本，值必须是整数并且是递增的状态
	 */
	public DatabaseHelper_LearningTime(Context context, String name,
			CursorFactory factory, int version) {
		// 必须通过super调用父类当中的构造函数
		super(context, name, factory, version);
	}

	public DatabaseHelper_LearningTime(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DatabaseHelper_LearningTime(Context context, String name) {
		this(context, name, VERSION);
	}

	// 该函数是在第一次创建数据库的时候执行
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("create a database");
		// execSQL用于执行SQL语句
		db.execSQL("create table DATE(ID integer primary key autoincrement,DATE_BEGIN TEXT,DATE_END TEXT,DATE_DELAYED integer,DATE_SUM_DELAYED integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		System.out.println("upgrade a database");
	}
}