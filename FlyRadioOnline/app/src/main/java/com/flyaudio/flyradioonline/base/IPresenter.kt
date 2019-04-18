package com.flyaudio.flyradioonline.base

interface IPresenter<in V: IView> {

    /**
     * 注入View，使之能够与View相互响应
     */
    fun attachView(view:V)

    /**
     * 释放资源
     */
    fun detachView()
}