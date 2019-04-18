package com.flyaudio.flyradioonline.entity

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio

data class CanSelectRadioItem(var isSelect:Boolean) : Radio(), MultiItemEntity {
    override fun getItemType(): Int {
        return 0
    }
}