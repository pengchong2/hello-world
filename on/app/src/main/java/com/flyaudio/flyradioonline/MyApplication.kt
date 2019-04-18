package com.flyaudio.flyradioonline

import android.app.Application
import com.flyaudio.flyradioonline.util.Flog
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.util.BaseUtil
import com.ximalaya.ting.android.opensdk.util.Logger
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager
import com.ximalaya.ting.android.sdkdownloader.http.RequestParams
import com.ximalaya.ting.android.sdkdownloader.http.app.RequestTracker
import com.ximalaya.ting.android.sdkdownloader.http.request.UriRequest
import org.xutils.x
import kotlin.properties.Delegates

class MyApplication : Application() {

    companion object {
        var instance: MyApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Flog.e("FmVoiceService", "MyApplication//onCreate")
        initSDK()
    }

    private fun initSDK() {
        x.Ext.init(this)

        val mp3 = getExternalFilesDir("mp3")!!.absolutePath


        val mXimalaya = CommonRequest.getInstanse()
        val mAppSecret = "89340f608c399d61d2203769a3a21127"
        mXimalaya.setAppkey("7c0961c9cfe45482054dcbff68b77cc0")
        mXimalaya.setPackid("com.flyaudio.flyradioonline")
        mXimalaya.init(this, mAppSecret)

        if (BaseUtil.isMainProcess(this)) {
            XmDownloadManager.Builder(this)
                    .maxDownloadThread(1)            // 最大的下载个数 默认为1 最大为3
                    .maxSpaceSize(java.lang.Long.MAX_VALUE)    // 设置下载文件占用磁盘空间最大值，单位字节。不设置没有限制
                    .connectionTimeOut(15000)        // 下载时连接超时的时间 ,单位毫秒 默认 30000
                    .readTimeOut(15000)                // 下载时读取的超时时间 ,单位毫秒 默认 30000
                    .fifo(false)                    // 等待队列的是否优先执行先加入的任务. false表示后添加的先执行(不会改变当前正在下载的音频的状态) 默认为true
                    .maxRetryCount(3)                // 出错时重试的次数 默认2次
                    .progressCallBackMaxTimeSpan(1000)//  进度条progress 更新的频率 默认是800
                    .requestTracker(requestTracker)    // 日志 可以打印下载信息
                    .savePath(mp3)    // 保存的地址 会检查这个地址是否有效
                    .create()
        }

    }

    private val requestTracker = object : RequestTracker {
        override fun onWaiting(params: RequestParams) {
            Logger.log("TingApplication : onWaiting $params")
        }

        override fun onStart(params: RequestParams) {
            Logger.log("TingApplication : onStart $params")
        }

        override fun onRequestCreated(request: UriRequest) {
            Logger.log("TingApplication : onRequestCreated $request")
        }

        override fun onSuccess(request: UriRequest, result: Any) {
            Logger.log("TingApplication : onSuccess $request   result = $result")
        }

        override fun onRemoved(request: UriRequest) {
            Logger.log("TingApplication : onRemoved $request")
        }

        override fun onCancelled(request: UriRequest) {
            Logger.log("TingApplication : onCanclelled $request")
        }

        override fun onError(request: UriRequest, ex: Throwable, isCallbackError: Boolean) {
            Logger.log("TingApplication : onError $request   ex = $ex   isCallbackError = $isCallbackError")
        }

        override fun onFinished(request: UriRequest) {
            Logger.log("TingApplication : onFinished $request")
        }
    }
}