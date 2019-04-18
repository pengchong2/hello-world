package com.flyaudio.flyradioonline.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.flyaudio.flyradioonline.Constant;


public class FlyaudioFmHelper extends SQLiteOpenHelper {
	//原始版本为1
	private static final int VERSION = 2;
	private static final String TAG = "database";

	public FlyaudioFmHelper(Context context) {
		this(context, Constant.FLYFM_DB_NAME, VERSION);
	}

	public FlyaudioFmHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public FlyaudioFmHelper(Context context, String name,
                            CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "sqlite==start===");
		createSearchTable(db);
		createPlayTable(db);
		createPopularTable(db);
		createCollectTable(db);
		createRadioTable(db);
		createHistoryTable(db);
		createLocalTable(db);
		createAdminTable(db);
		createRadioTxTable(db);
		createCollectNumTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (int i = oldVersion; i < newVersion; i++) {
			switch (i) {
			case 1:
				createSearchTable(db);
				createPlayTable(db);
				createPopularTable(db);
				createCollectTable(db);
				createRadioTable(db);
				createHistoryTable(db);
				createLocalTable(db);
				createAdminTable(db);
				createRadioTxTable(db);
				createCollectNumTable(db);
				break;
			default:
				break;
			}
		}
	}

	private void createSearchTable(SQLiteDatabase db){
		String CREATE_TABLE_SQL = "create table " + Constant.TABLE_SEARCH_NAME
				+ "(" + Constant.SEARCH_ID + " INTEGER PRIMARY KEY, "
				+ Constant.SEARCH_UID + " VARCHAR(32), "
				+ Constant.SEARCH_NAME + " VARCHAR(64))";
		db.execSQL(CREATE_TABLE_SQL);
	}

	private void createPlayTable(SQLiteDatabase db){
		String CREATE_TABLE_SQL = "create table " + Constant.TABLE_PLAY_NAME
				+ "(" + Constant.PLAY_ID + " INTEGER PRIMARY KEY, "
				+ Constant.PLAY_ALBUM_ID + " INTEGER, "
				+ Constant.PLAY_INDEX + " INTEGER, "
				+ Constant.PLAY_STATE + " INTEGER, "
				+ Constant.PLAY_TYPE + " VARCHAR(32))";
		db.execSQL(CREATE_TABLE_SQL);
	}

	private void createPopularTable(SQLiteDatabase db){
		String CREATE_TABLE_SQL = "create table " + Constant.TABLE_POPULAR_NAME
				+ "(" + Constant.POPULAR_ID + " INTEGER PRIMARY KEY, "
				+ Constant.POPULAR_ALBUM_ID + " INTEGER, "
				+ Constant.POPULAR_TITLE + " VARCHAR(64), "
				+ Constant.POPULAR_TAG + " VARCHAR(64), "
				+ Constant.POPULAR_COVER_URL + " VARCHAR(64))";
		db.execSQL(CREATE_TABLE_SQL);
	}

	private void createCollectTable(SQLiteDatabase db){
		String CREATE_TABLE_SQL = "create table " + Constant.TABLE_COLLECT_NAME
				+ "(" + Constant.COLLECT_ID + " INTEGER PRIMARY KEY, "
				+ Constant.COLLECT_ALBUM_ID + " INTEGER, "
				+ Constant.COLLECT_TITLE + " VARCHAR(64), "
				+ Constant.COLLECT_TAG + " VARCHAR(64), "
				+ Constant.COLLECT_COVER_URL + " VARCHAR(64))";
		db.execSQL(CREATE_TABLE_SQL);
	}

	private void createHistoryTable(SQLiteDatabase db){
		String CREATE_TABLE_SQL = "create table " + Constant.TABLE_HISTORY_NAME
				+ "(" + Constant.HISTORY_ID + " INTEGER PRIMARY KEY, "
				+ Constant.HISTORY_ALBUM_ID + " INTEGER, "
				+ Constant.HISTORY_TRACK_ID + " INTEGER, "
				+ Constant.HISTORY_CONTENT_TYPE + " INTEGER, "
				+ Constant.HISTORY_COVER_URL + " VARCHAR(64), "
				+ Constant.HISTORY_LARGE_COVER_URL + " VARCHAR(64), "
				+ Constant.HISTORY_TITLE + " VARCHAR(32))";
		db.execSQL(CREATE_TABLE_SQL);
	}

	private void createLocalTable(SQLiteDatabase db){
		String CREATE_TABLE_SQL = "create table " + Constant.TABLE_LOCAL_NAME
				+ "(" + Constant.LOCAL_ID + " INTEGER PRIMARY KEY, "
				+ Constant.LOCAL_ALBUM_ID + " INTEGER, "
				+ Constant.LOCAL_TITLE + " VARCHAR(64), "
				+ Constant.LOCAL_LARGE_COVER_URL + " VARCHAR(64), "
				+ Constant.LOCAL_COVER_URL + " VARCHAR(64))";
		db.execSQL(CREATE_TABLE_SQL);
	}

	private void createRadioTable(SQLiteDatabase db){
		String CREATE_TABLE_SQL = "create table " + Constant.TABLE_RADIO_NAME
				+ "(" + Constant.RADIO_ID + " INTEGER PRIMARY KEY, "
				+ Constant.RADIO_SCHEDULE_ID + " INTEGER, "
				+ Constant.RADIO_STARTTIME + " INTEGER, "
				+ Constant.RADIO_ENDTIME + " INTEGER, "
				+ Constant.RADIO_ACTIVITY_ID + " INTEGER, "
				+ Constant.RADIO_DATA_ID + " INTEGER, "
				+ Constant.RADIO_UPDATE_AT + " INTEGER, "
				+ Constant.RADIO_PLAYCOUNT + " INTEGER, "
				+ Constant.RADIO_PROGRAME_ID + " INTEGER, "
				+ Constant.RADIO_KIND + " VARCHAR(32), "
				+ Constant.RADIO_TYPE + " VARCHAR(32), "
				+ Constant.RADIO_NAME + " VARCHAR(64), "
				+ Constant.RADIO_DESC + " VARCHAR(64), "
				+ Constant.RADIO_PROGRAME_NAME + " VARCHAR(64), "
				+ Constant.RADIO_RATE24AAC_URL + " VARCHAR(64), "
				+ Constant.RADIO_RATE24TS_URL + " VARCHAR(64), "
				+ Constant.RADIO_RATE64AAC_URL + " VARCHAR(64), "
				+ Constant.RADIO_RATE64TS_URL + " VARCHAR(64), "
				+ Constant.RADIO_COVERURLSMALL + " VARCHAR(64), "
				+ Constant.RADIO_COVERURLLARGE + " VARCHAR(64), "
				+ Constant.RADIO_SHARE_URL + " VARCHAR(64), "
				+ Constant.RADIO_ISACTIVITYLIVE + " VARCHAR(32))";
		db.execSQL(CREATE_TABLE_SQL);
	}

	private void createAdminTable(SQLiteDatabase db){
		String CREATE_TABLE_SQL = "create table " + Constant.TABLE_ADMIN_NAME
				+ "(" + Constant.ADMIN_ID + " INTEGER PRIMARY KEY, "
				+ Constant.ADMIN_COVER_URL + " VARCHAR(64), "
				+ Constant.ADMIN_INTRODUCE + " VARCHAR(64), "
				+ Constant.ADMIN_NAME + " VARCHAR(32))";
		db.execSQL(CREATE_TABLE_SQL);
	}

	private void createRadioTxTable(SQLiteDatabase db){
		String CREATE_TABLE_SQL = "create table " + Constant.TABLE_RAIDO_TX_NAME
				+ "(" + Constant.RADIO_TX_ID + " INTEGER PRIMARY KEY, "
				+ Constant.RADIO_TX_NAME + " VARCHAR(32), "
				+ Constant.RADIO_TX_CONTENT + " text)";
		db.execSQL(CREATE_TABLE_SQL);
	}

	private void createCollectNumTable(SQLiteDatabase db){
		String CREATE_TABLE_SQL = "create table " + Constant.TABLE_COLLECT_NUM_NAME
				+ "(" + Constant.COLLECT_NUM_ID + " INTEGER PRIMARY KEY, "
				+ Constant.COLLECT_NUM_POS + " INTEGER)";
		db.execSQL(CREATE_TABLE_SQL);
	}
}
