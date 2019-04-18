package com.flyaudio.flyradioonline.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Flog {

	public static String TAG = "FlyClientService";
	public static boolean DEBUG = false;
	static boolean INFO = false;
	static boolean ERROR = true;
	
	public Context mContext;
	private final String DEBUG_ACTION_ALL = "cn.flyaudio.debug";
	private String DEBUG_SHAREPREFERENCE_NAME = "debug";
	final String TELEPHONY_SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";

	public static void e(String Tag, String msg) {
		if (ERROR)
			Log.e(TAG + "-" + Tag, msg);
	}

	public static void e(String msg) {
		if (ERROR)
			Log.e(TAG, msg);
	}

	public static void d(String Tag, String msg) {
		if (DEBUG)
			Log.d(TAG + "-" + Tag, msg);
	}

	public static void d(String msg) {
		if (DEBUG)
			Log.d(TAG, msg);
	}

	public static void i(String msg) {
		if (INFO)
			Log.i(TAG, msg);
	}

	private boolean isRun = false;
	private void execCmd(String cmd) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmd);
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		while (null != (line = br.readLine())) {
			Log.e("Flog", line);
		}
	}
	
	public Flog() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * register the receiver of debug state change
	 * @param context
	 * @param action : please pass in the package name
	 */
	public void registerDebugBrocastReceiver(Context context, String action){
		mContext = context;
		DEBUG = INFO = restoreDebug();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(action);
		intentFilter.addAction(DEBUG_ACTION_ALL);
		mContext.registerReceiver(new DebugReceiver(action),intentFilter);
		
		IntentFilter filter_secret = new IntentFilter();
		filter_secret.addAction(TELEPHONY_SECRET_CODE_ACTION);
		filter_secret.addDataScheme("android_secret_code");
		mContext.registerReceiver(new DebugReceiver(action),filter_secret);
	}

	/**
	 * 
	 * @author qqm
	 * debug state receiver
	 */
	class DebugReceiver extends BroadcastReceiver {

		private String action = "";
		
		public DebugReceiver(String action) {
			// TODO Auto-generated constructor stub
			this.action = action;
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String intentAction = intent.getAction();
			if (intentAction.equals(action) || intentAction.equals(DEBUG_ACTION_ALL)) {
				setDebug(intent.getBooleanExtra("debug", false));
			}else if(intentAction.equals(TELEPHONY_SECRET_CODE_ACTION)){
				try {
					String secretHost = intent.getData().getHost();
					if (secretHost.equals("158001")) {
						setDebug(true);
					}else if(secretHost.equals("158003")){
						setDebug(false);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		
	}
	
	private void setDebug(boolean debug){
		this.DEBUG  = this.INFO = debug;
		storeDebug(debug);
	}
	
	private void storeDebug(boolean debug){
		if (mContext != null) {
			SharedPreferences preferences = mContext.getSharedPreferences(DEBUG_SHAREPREFERENCE_NAME, Context.MODE_PRIVATE);
			if (preferences != null) {
				Editor editor = preferences.edit();
				editor.putBoolean("debug", debug);
				editor.commit();
			}
		}
	}
	
	private boolean restoreDebug(){
		boolean debug = false;
		if (mContext != null) {
			SharedPreferences preferences = mContext.getSharedPreferences(DEBUG_SHAREPREFERENCE_NAME, Context.MODE_PRIVATE);
			if (preferences != null) {
				debug = preferences.getBoolean("debug", false);
			}
		}
		return debug;
	}

}
