package com.flyaudio.flyradioonline.util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetWorkUtil {

	private static final String TAG = "NetWorkUtil";

	private static NetWorkUtil mNetWorkUtil;

	private BroadcastReceiver mBroadcastReciver ;
	private Context mContext;

	private ConnectivityManager cm;

	private boolean isNetworkConnected = false;

	/**
	 * 枚举网络状态 NET_NO：没有网络 NET_2G:2g网络 NET_3G：3g网络 NET_4G：4g网络 NET_WIFI：wifi
	 * NET_UNKNOWN：未知网络
	 */
	public enum NetState {
		NET_NO, NET_2G, NET_3G, NET_4G, NET_WIFI, NET_UNKNOWN
	};

/*	public static synchronized NetWorkUtil getInstance( Context context,BroadcastReceiver mBroadcastReciver) {
		if (mNetWorkUtil == null) {
			mNetWorkUtil = new NetWorkUtil( context,mBroadcastReciver);
		}
		return mNetWorkUtil;
	}*/

	public NetWorkUtil(Context context , BroadcastReceiver mBroadcastReciver) {
		// TODO Auto-generated constructor stub
		mContext = context;
		isNetworkConnected = getNetworkConnectState();
		this.mBroadcastReciver = mBroadcastReciver;
	}
	public NetWorkUtil(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		isNetworkConnected = getNetworkConnectState();
	}

	public void registerNetWorkReceiver() {
		if (mContext != null) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
			mContext.registerReceiver(mBroadcastReciver, intentFilter);
		}
	}

	public void ungisterNetWorkReceiver(){
		if(mContext != null){
			mContext.unregisterReceiver(mBroadcastReciver);
		}
	}

	public boolean getNetworkConnectState() {
		// return mReceiver.isNetConn;
		NetState state = getNetWorkState();
		if (state == NetState.NET_NO) {
			return false;
		}
		return true;
	}

	public boolean isNetworkConnected() {
		return isNetworkConnected;
	}

	public boolean isWifiConn() {
		NetworkInfo networkInfo = getConnectivityManager().getNetworkInfo(
				ConnectivityManager.TYPE_WIFI);
		return networkInfo.isConnected();
	}

	public boolean isMobileConn() {
		NetworkInfo networkInfo = getConnectivityManager().getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE);
		return networkInfo.isConnected();
	}

	public static boolean isOpenNetwork(Context context) {
		if(context != null){
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connManager.getActiveNetworkInfo() != null) {
				return connManager.getActiveNetworkInfo().isAvailable();
			}
		}
		return false;
	}

	public NetState getNetWorkState() {
		Flog.d(TAG, " MobileDataControllerImpl getNetWorkState ");

		NetworkInfo info = getConnectivityManager().getActiveNetworkInfo();
		NetState stateCode = NetState.NET_NO;
		if (info != null && info.isConnectedOrConnecting()) {
			switch (info.getType()) {
			case ConnectivityManager.TYPE_WIFI:
				Flog.d(TAG, "ConnectivityManager.TYPE_WIFI");
				stateCode = NetState.NET_WIFI;
				break;
			case ConnectivityManager.TYPE_MOBILE:
				Flog.d(TAG, "ConnectivityManager.TYPE_MOBILE");
				switch (info.getSubtype()) {
				case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
				case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
				case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN:
					stateCode = NetState.NET_2G;
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
				case TelephonyManager.NETWORK_TYPE_EHRPD:
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					stateCode = NetState.NET_3G;
					break;
				case TelephonyManager.NETWORK_TYPE_LTE:
					stateCode = NetState.NET_4G;
					break;
				default:
					stateCode = NetState.NET_UNKNOWN;
				}
				break;
			case ConnectivityManager.TYPE_BLUETOOTH:
				Flog.d(TAG, "ConnectivityManager.TYPE_BLUETOOTH");
				break;

			default:
				stateCode = NetState.NET_UNKNOWN;
				break;
			}
		}
		return stateCode;
	}

	public ConnectivityManager getConnectivityManager() {
		cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm;
	}

	public boolean isAvailable() {
		NetworkInfo networkInfo = getConnectivityManager()
				.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			Flog.d(TAG,
					"-----网络可用"+ networkInfo.isAvailable());
			return true;
		}
		
		return false;
	}
}
