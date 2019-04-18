package com.flyaudio.flyradioonline.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.flyaudio.flyradioonline.util.Flog;

import java.util.concurrent.atomic.AtomicInteger;


public class FlyaudioFmManager {
	private static final int VERSION = 1;

	private static final String TAG = "FlyaudioFmManager";
	
	private static FlyaudioFmManager dbManager;
	private static FlyaudioFmHelper mHelper;
	private SQLiteDatabase mDatabase;
	private AtomicInteger mOpenCounter = new AtomicInteger();
	
	public static synchronized FlyaudioFmManager getInstance(){
		if (dbManager == null) {
			throw new IllegalStateException(FlyaudioFmManager.class.getSimpleName() +
	                   " is not initialized, call initializeInstance(..) method first.");
		}
		return dbManager;
	}
	
	public static synchronized void initializeInstance(Context context) {
	       if (dbManager == null && context != null) {
	    	   dbManager = new FlyaudioFmManager();
	           mHelper = new FlyaudioFmHelper(context);
	           Flog.d(TAG, "initializeInstance");
	       }
	   }
	 
	   public synchronized SQLiteDatabase openDatabase() {
		   Flog.e(TAG, "openDatabase mOpenCounter.get() = " + mOpenCounter.get());
	       if(mOpenCounter.incrementAndGet() == 1 || mDatabase == null) {
	           // Opening new database
	           mDatabase = mHelper.getWritableDatabase();
	       }
	       return mDatabase;
	   }
	 
	   public synchronized void closeDatabase() {
		   Flog.e(TAG, "closeDatabase get = " + mOpenCounter.get());
	       if(mOpenCounter.decrementAndGet() == 0) {
	           // Closing database
	           mDatabase.close();
	           mDatabase = null;
	       }
	   }
}
