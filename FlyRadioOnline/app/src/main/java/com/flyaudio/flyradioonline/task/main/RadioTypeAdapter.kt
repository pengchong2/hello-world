package com.flyaudio.flyradioonline.task.main

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.flyaudio.flyradioonline.R
import com.flyaudio.flyradioonline.entity.RadioType

class RadioTypeAdapter(radioTypeList: List<RadioType>) : BaseQuickAdapter<RadioType, BaseViewHolder>(R.layout.layout_all_radio_type_item, radioTypeList) {

    override fun convert(holder: BaseViewHolder, item: RadioType) {
        with(item) {
            holder.setText(R.id.tvRadioType, name)
            holder.setBackgroundRes(R.id.rlRadioTypeItemContainer,
                    if (isCheck) R.drawable.radio_type_item_background_l
                    else R.drawable.radio_type_item_background_d)
        }
    }
}