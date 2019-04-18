package com.flyaudio.flyradioonline.task.more

import android.support.v7.widget.RecyclerView
import com.flyaudio.flyradioonline.entity.CanSelectRadioItem
import com.flyaudio.flyradioonline.task.main.HistoryRadioAdapter

class MoreHistoryRadioAdapter(radioList: MutableList<CanSelectRadioItem>, onClick: (CanSelectRadioItem?) -> Unit, onLongClick: () -> Unit) : HistoryRadioAdapter(radioList, onClick, onLongClick) {

    override fun getItemCount(): Int {
        return radioList.size
    }

    override fun getItemViewType(position: Int): Int {
        return defaultType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindData(radioList[position], position)
    }
}