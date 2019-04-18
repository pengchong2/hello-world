package com.flyaudio.flyradioonline.task.main

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.flyaudio.flyradioonline.Constant.*
import com.flyaudio.flyradioonline.R
import com.flyaudio.flyradioonline.data.ACache
import com.flyaudio.flyradioonline.data.db.RadioDb
import com.flyaudio.flyradioonline.entity.AllRadioFirstItem
import com.flyaudio.flyradioonline.entity.AllRadioSecondItem
import com.flyaudio.flyradioonline.entity.RadioType
import com.flyaudio.flyradioonline.task.play.activity.PlayingActivity
import com.flyaudio.flyradioonline.task.search.FmSearchActivity
import com.flyaudio.flyradioonline.util.Flog
import com.ximalaya.ting.android.opensdk.model.live.provinces.Province
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio
import kotlinx.android.synthetic.main.fragment_all.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

@Suppress("IMPLICIT_CAST_TO_ANY", "DeferredResultUnused", "UNCHECKED_CAST")
class AllFragment : Fragment(), MainContract.MainView {

    private val mainPresenter by lazy { MainPresenter() }
    private val aCache by lazy { ACache.get(ctx) }
    private val radioTypeList = listOf(RadioType("国家台", true), RadioType("省市台"),
            RadioType("新闻台"), RadioType("交通台"), RadioType("文艺台"), RadioType("经济台"))
    private var provinceList: List<Province>? = listOf()
    private var dataList: MutableList<MultiItemEntity> = mutableListOf()
    private var currentType = 0
    private var currentProvincePosition = -1

    private val radioTypeAdapter: RadioTypeAdapter by lazy { RadioTypeAdapter(radioTypeList) }
    private val allRadioAdapter: AllRadioAdapter by lazy { AllRadioAdapter(dataList) }
    private var isRequestFail = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_all, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Flog.e("liuxiaosheng", "AllFragment//onViewCreated")
        mainPresenter.attachView(this)
        initView()
        getCountryRadio()
    }

    override fun onResume() {
        super.onResume()
        Flog.e("liuxiaosheng", "AllFragment//onResume")
        mainPresenter.attachView(this)
    }

    private fun initView() {
        tvSearch.setOnClickListener { startActivity<FmSearchActivity>() }
        radioTypeAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
            if (currentType != position) {
                radioTypeList[currentType].isCheck = false
                radioTypeList[position].isCheck = true
                adapter.notifyDataSetChanged()
                when (position) {
                    0 -> getCountryRadio()
                    1 -> getProvinceList()
                    2 -> getRadioWithCategory("5")
                    3 -> getRadioWithCategory("1")
                    4 -> getRadioWithCategory("4")
                    5 -> getRadioWithCategory("11")
                }
            }
            currentType = position
        }
        rvRadioType.layoutManager = LinearLayoutManager(ctx)
        rvRadioType.adapter = radioTypeAdapter

        allRadioAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
            val item = adapter.getItem(position)
            if (adapter.getItemViewType(position) == AllRadioAdapter.TYPE_LEVEL_0) {
                with(item as AllRadioFirstItem) {
                    if (radioId.toInt() == 0) {
                        if (isExpanded) {
                            adapter.collapse(position)
                        } else {
                            if (subItems != null && subItems.size != 0) {
                                adapter.expand(position)
                            } else {
                                currentProvincePosition = position
                                getProvinceRadio(provinceCode.toString())
                            }
                        }
                    } else startActivity<PlayingActivity>(RADIO_DATA to item.radio)
                }
            } else {
                startActivity<PlayingActivity>(RADIO_DATA to (item as AllRadioSecondItem).radio)
            }
        }
        rvAllRadio.layoutManager = LinearLayoutManager(ctx)
        rvAllRadio.adapter = allRadioAdapter
    }


    private fun getCountryRadio() = async(UI) {
        val itemList = getDataFromDataBase(AREA_COUNTRY_RADIO_TOKEN)
        if (itemList.isNotEmpty()) allRadioAdapter.setNewData(getItemListWithRadioList(itemList))
        else mainPresenter.requestFmData(AREA_COUNTRY_RADIO_TOKEN, listOf("1"))
    }

    private suspend fun getDataFromDataBase(radioType: String): List<Radio> {
        val result = bg { RadioDb.instance.getRadioFromDb(radioType) }
        return result.await()
    }

    private fun getItemListWithRadioList(data: Any?): List<MultiItemEntity> {
        val list = mutableListOf<AllRadioFirstItem>()
        (data as List<Radio>?)?.forEach {
            val item = AllRadioFirstItem(it.radioName, it.dataId)
            item.radio = it
            list.add(item)
        }
        return list
    }

    private fun getProvinceList() {
        val list = aCache.getAsObject(PROVINCE_TOKEN) as List<AllRadioFirstItem>?
        if (list != null) setDataForAllRadioAdapter(list)
        else mainPresenter.requestFmData(PROVINCE_TOKEN, null)
    }

    private fun getProvinceRadio(provinceId: String) = async(UI) {
        val itemList = getDataFromDataBase(AREA_PROVINCE_RADIO_TOKEN + provinceId)
        if (itemList.isNotEmpty()) getDataSuccess(AREA_PROVINCE_RADIO_TOKEN, itemList)
        else mainPresenter.requestFmData(AREA_PROVINCE_RADIO_TOKEN, listOf("2", provinceId))
    }

    private fun getRadioWithCategory(categoryId: String) = async(UI) {
        val itemList = getDataFromDataBase(SORT_TOKEN + categoryId)
        if (itemList.isNotEmpty()) allRadioAdapter.setNewData(getItemListWithRadioList(itemList))
        else mainPresenter.requestFmData(SORT_TOKEN, categoryId)
    }

    override fun getDataSuccess(tag: String, data: Any?) {
        when (tag) {
            AREA_COUNTRY_RADIO_TOKEN, SORT_TOKEN -> {

                val list = getItemListWithRadioList(data)
                allRadioAdapter.setNewData(list as List<MultiItemEntity>?)
            }
            AREA_PROVINCE_RADIO_TOKEN -> {
                val list = data as List<Radio>?
                val allRadioFirstItem = dataList[currentProvincePosition] as AllRadioFirstItem
                list?.forEachWithIndex { i, radio ->
                    if (i < 3) {
                        val item = AllRadioSecondItem(radio.radioName, radio.dataId)
                        item.radio = radio
                        allRadioFirstItem.addSubItem(item)
                    }
                }
                allRadioAdapter.expand(currentProvincePosition)
                allRadioAdapter.setNewData(dataList)
            }
            PROVINCE_TOKEN -> {
                provinceList = data as List<Province>?
                val list = mutableListOf<AllRadioFirstItem>()
                provinceList?.forEach {
                    list.add(AllRadioFirstItem(it.provinceName, 0, it.provinceCode))
                }
                setDataForAllRadioAdapter(list)
                aCache.put(tag, list as ArrayList<AllRadioFirstItem>)
            }
        }
    }

    private fun setDataForAllRadioAdapter(list: List<MultiItemEntity>, isClear: Boolean = true) {
        if (isClear) dataList.clear()
        dataList.addAll(list)
        allRadioAdapter.setNewData(dataList)
    }

    override fun getDataFail(error: String?) {
        isRequestFail = true
        Log.d("yuan", "getData fail $error")
    }

}