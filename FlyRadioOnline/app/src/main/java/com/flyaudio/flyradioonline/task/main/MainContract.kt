package com.flyaudio.flyradioonline.task.main

import com.flyaudio.flyradioonline.base.BasePresenter
import com.flyaudio.flyradioonline.base.IView

object MainContract {

    interface MainView : IView {
        fun getDataSuccess(tag:String,data:Any?)
        fun getDataFail(error: String?)
    }

    abstract class MainPresenter : BasePresenter<MainView>() {
        abstract fun requestFmData(tag: String, params: Any?)
    }
}