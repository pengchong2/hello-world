package com.flyaudio.flyradioonline.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class FlyaudioFmDB {

	private static FlyaudioFmDB mCollectDataDB = null;

	private FlyaudioFmHelper mHelper;

	private final String TAG = "FlyaudioFmDB";

	public static FlyaudioFmDB getInstance(Context context) {
		if (mCollectDataDB == null) {
			mCollectDataDB = new FlyaudioFmDB(context);
		}
		return mCollectDataDB;
	}

	private FlyaudioFmDB(Context context) {
		FlyaudioFmManager.initializeInstance(context);
	}

	/**
	 * 判断是否连接
	 * 
	 * @return
	 */
	private SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = FlyaudioFmManager.getInstance().openDatabase();
		} catch (Exception e) {
		}
		return sqliteDatabase;
	}

}
