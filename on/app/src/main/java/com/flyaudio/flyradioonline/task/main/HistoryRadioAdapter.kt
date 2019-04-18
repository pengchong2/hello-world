package com.flyaudio.flyradioonline.task.main

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyaudio.flyradioonline.Constant
import com.flyaudio.flyradioonline.R
import com.flyaudio.flyradioonline.data.db.RadioDb
import com.flyaudio.flyradioonline.entity.CanSelectRadioItem
import com.flyaudio.flyradioonline.task.more.MoreActivity
import com.flyaudio.flyradioonline.util.Flog
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.layout_radio_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity

open class HistoryRadioAdapter(protected var radioList: MutableList<CanSelectRadioItem>, val onClick: (CanSelectRadioItem?) -> Unit, val onLongClick: () -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //历史列表更多item
    private val historyFootType = 1
    protected val defaultType = 0

    var isEdit = false
    var isAllSelect = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == defaultType) ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_radio_item, parent, false), onClick)
        else FootViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_radio_history_foot_item, parent, false))
    }

    override fun getItemCount(): Int {
        return if (radioList.size < Constant.LIST_HIS_NUM) radioList.size else Constant.LIST_HIS_NUM
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == Constant.LIST_HIS_FOOT_POS) historyFootType else defaultType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (position == Constant.LIST_HIS_FOOT_POS) {
            Flog.e("liuxiaosheng", "onBindViewHolder//1//"+radioList.hashCode()+"//"+radioList.toString())
            (holder as FootViewHolder).bindData(holder.itemView.context)
        } else {
            Flog.e("liuxiaosheng", "onBindViewHolder//2//"+radioList.hashCode()+"//"+radioList.toString())
            (holder as ViewHolder).bindData(radioList[position], position)
        }
    }

    fun setNewData(list: MutableList<CanSelectRadioItem>) {
        radioList = list
        Flog.e("liuxiaosheng", "setNewData//"+hasObservers())
        notifyDataSetChanged()
    }

    fun deleteSelect() {
        val selectList = radioList.filter { it.isSelect }
        if (selectList.isEmpty()) return
        selectList.forEach { radioList.remove(it) }
        notifyDataSetChanged()
        doAsync { RadioDb.instance.deleteHistory(selectList) }
    }

    fun setAllSelect() {
        radioList.forEach {
            it.isSelect = !isAllSelect
        }
        isAllSelect = !isAllSelect
        onClick(null)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View, val onClick: (CanSelectRadioItem?) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bindData(radio: CanSelectRadioItem, position: Int) {
            Flog.e("liuxiaosheng", "bindData//" + position)
            with(radio) {
                Glide.with(itemView).load(coverUrlLarge)
                        .apply(RequestOptions().placeholder(R.drawable.radio_item_default_picture))
                        .into(itemView.ivRadioPicture)
                itemView.tvRadioName.text = radioName
                itemView.setOnClickListener {
                    if (isEdit) {
                        isSelect = !isSelect
                        notifyItemChanged(position)
                    }
                    isAllSelect = (radioList.all { item -> item.isSelect })
                    onClick(this)
                }
                itemView.setOnLongClickListener {
                    if (!isEdit) {
                        if (!isSelect) {
                            isSelect = true
                        }
                        isEdit = true
                        notifyDataSetChanged()
                        onLongClick()
                    }
                    true
                }
                if (isEdit) {
                    if (isSelect) {
                        itemView.ivRadioItemSurface.setBackgroundResource(R.mipmap.radio_item_edit_select)
                    } else {
                        itemView.ivRadioItemSurface.setBackgroundResource(R.mipmap.radio_item_edit_not_select)
                    }
                } else itemView.ivRadioItemSurface.setBackgroundResource(R.mipmap.fm_radio_item_mengban)
            }
        }
    }

    inner class FootViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(context: Context) {
            itemView.setOnClickListener { context.startActivity<MoreActivity>() }
        }
    }
}