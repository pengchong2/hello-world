package com.flyaudio.flyradioonline.task.main

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.flyaudio.flyradioonline.R
import com.flyaudio.flyradioonline.entity.AllRadioFirstItem
import com.flyaudio.flyradioonline.entity.AllRadioSecondItem

class AllRadioAdapter(data: List<MultiItemEntity>) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {

    init {
        addItemType(TYPE_LEVEL_0, R.layout.layout_all_radio_item)
        addItemType(TYPE_LEVEL_1, R.layout.layout_all_radio_item)
    }

    override fun getItemCount(): Int {
        return if (data.size == 0) 6 else super.getItemCount()
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        when (helper.itemViewType) {
            TYPE_LEVEL_0 -> {
                with(item as AllRadioFirstItem) {
                    helper.setText(R.id.tvContent, content)
                    helper.setBackgroundRes(R.id.rlAllRadioItemContainer, if (radioId.toInt() == 0) R.drawable.all_radio_item_background_d else R.drawable.all_radio_item_background_l)
                }
            }
            TYPE_LEVEL_1 -> {
                helper.setText(R.id.tvContent, (item as AllRadioSecondItem).content)
                helper.setBackgroundRes(R.id.rlAllRadioItemContainer, R.drawable.all_radio_item_background_l)
            }
        }
    }

    companion object {
        const val TYPE_LEVEL_0 = 0
        const val TYPE_LEVEL_1 = 1
    }
}