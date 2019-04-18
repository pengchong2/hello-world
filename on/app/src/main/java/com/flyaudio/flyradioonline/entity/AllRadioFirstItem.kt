package com.flyaudio.flyradioonline.entity

import com.chad.library.adapter.base.entity.AbstractExpandableItem
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.flyaudio.flyradioonline.task.main.AllRadioAdapter.Companion.TYPE_LEVEL_0
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio
import java.io.Serializable

class AllRadioFirstItem(val content: String, val radioId: Long = 0, val provinceCode: Long = -1) : AbstractExpandableItem<AllRadioSecondItem>(), MultiItemEntity ,Serializable{

    var radio: Radio? =null

    override fun getItemType(): Int {
        return TYPE_LEVEL_0
    }

    override fun getLevel(): Int {
        return 0
    }

}