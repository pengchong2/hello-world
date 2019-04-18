package com.flyaudio.flyradioonline.entity

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.flyaudio.flyradioonline.task.main.AllRadioAdapter.Companion.TYPE_LEVEL_1
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio

class AllRadioSecondItem(val content: String, val radioId: Long) : MultiItemEntity {

    var radio: Radio? =null

    override fun getItemType(): Int {
        return TYPE_LEVEL_1
    }
}