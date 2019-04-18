package com.flyaudio.flyradioonline.task.main

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.flyaudio.flyradioonline.R
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio
import kotlinx.android.synthetic.main.layout_radio_item.view.*

class PopularRadioAdapter(radioList: List<Radio>, val onClick: (Radio) -> Unit) :
        BaseQuickAdapter<Radio, BaseViewHolder>(R.layout.layout_radio_item, radioList) {

    override fun getItemCount(): Int {
        return 28
    }

    override fun convert(holder: BaseViewHolder, item: Radio) {
        with(item) {
            Glide.with(holder.itemView).load(coverUrlLarge)
                    .apply(RequestOptions().placeholder(R.mipmap.radio_item_default_picture))
                    .into(holder.itemView.ivRadioPicture)
            holder.itemView.tvRadioName.text = radioName
            holder.itemView.setOnClickListener { onClick(this) }
        }
    }
}