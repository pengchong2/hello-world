package com.flyaudio.flyradioonline.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.util.Log
import com.flyaudio.flyradioonline.MyApplication

class NetworkUtil {
    companion object {
        enum class NetState {
            NET_NO, NET_2G, NET_3G, NET_4G, NET_WIFI, NET_UNKNOWN
        }

        private val connectivityManager = MyApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isOpenNetwork: Boolean
            get() {
                return if (connectivityManager.activeNetworkInfo != null) {
                    connectivityManager.activeNetworkInfo.isAvailable
                } else false
            }
        val networkState: Boolean
            get() {
                return getNetWorkState() != NetState.NET_NO
            }

        private fun getNetWorkState(): NetState {
            Log.d("yuan", " MobileDataControllerImpl getNetWorkState ")
            val info = connectivityManager.activeNetworkInfo
            var stateCode = NetState.NET_NO
            if (info != null && info.isConnectedOrConnecting) {
                when (info.type) {
                    ConnectivityManager.TYPE_WIFI -> {
                        Log.d("yuan", "ConnectivityManager.TYPE_WIFI")
                        stateCode = NetState.NET_WIFI
                    }
                    ConnectivityManager.TYPE_MOBILE -> {
                        Log.d("yuan", "ConnectivityManager.TYPE_MOBILE")
                        stateCode = when (info.subtype) {
                            TelephonyManager.NETWORK_TYPE_GPRS // 联通2g
                                , TelephonyManager.NETWORK_TYPE_CDMA // 电信2g
                                , TelephonyManager.NETWORK_TYPE_EDGE // 移动2g
                                , TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NetState.NET_2G
                            TelephonyManager.NETWORK_TYPE_EVDO_A // 电信3g
                                , TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NetState.NET_3G
                            TelephonyManager.NETWORK_TYPE_LTE -> NetState.NET_4G
                            else -> NetState.NET_UNKNOWN
                        }
                    }
                    ConnectivityManager.TYPE_BLUETOOTH -> Log.d("yuan", "ConnectivityManager.TYPE_BLUETOOTH")
                    else -> stateCode = NetState.NET_UNKNOWN
                }
            }
            return stateCode
        }

        private var networkListener: ((Boolean) -> Unit)? = null
        private val networkReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (networkState) networkListener?.invoke(true) else networkListener?.invoke(false)
            }
        }

        fun setNetworkListener(listener: ((Boolean) -> Unit)) {
            networkListener = listener
            MyApplication.instance.registerReceiver(networkReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        }

        fun clearNetworkListener() {
            networkListener = null
            MyApplication.instance.unregisterReceiver(networkReceiver)
        }
    }


}
