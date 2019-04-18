package com.flyaudio.flyradioonline.task.main

import com.flyaudio.flyradioonline.Constant
import com.flyaudio.flyradioonline.data.db.RadioDb
import com.flyaudio.flyradioonline.util.Flog
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants.*
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.live.provinces.ProvinceList
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListByCategory
import org.jetbrains.anko.doAsync
import java.util.*

open class MainPresenter : MainContract.MainPresenter() {

    companion object {
        val instance: MainPresenter by lazy { MainPresenter() }
    }

    override fun requestFmData(tag: String, params: Any?) {
        doAsync {
            Flog.e("liuxiaosheng", "requestFmData//"+tag)
            when (tag) {

                Constant.POPULAR_TOKEN -> requestPopularRadio(tag)
                Constant.AREA_COUNTRY_RADIO_TOKEN, Constant.AREA_PROVINCE_RADIO_TOKEN -> requestRadiosWithArea(tag, params)
                Constant.SORT_TOKEN -> requestRadiosWithCategory(tag, params)
                Constant.PROVINCE_TOKEN -> requestProvinces(tag)
                Constant.HISTORY_TOKEN-> requestHistory(tag)
            }
        }
    }

    private fun requestHistory(tag: String) {
        doAsync {
            Flog.e("liuxiaosheng", "requestHistory//"+view?.hashCode())
            view?.getDataSuccess(tag,RadioDb.instance.getRadioHistoryListFromDb())
        }
    }

    private fun getCallback(tag: String, category: String = ""): IDataCallBack<RadioList> {
        return object : IDataCallBack<RadioList> {
            override fun onSuccess(radioList: RadioList?) {
                radioList?.let {
                    view?.getDataSuccess(tag, radioList.radios)
                    doAsync { RadioDb.instance.saveRadio(radioList.radios, tag + category) }
                }
            }

            override fun onError(p0: Int, p1: String?) {
                view?.getDataFail(p1)
            }
        }
    }


    private fun requestPopularRadio(tag: String) {
        val map = HashMap<String, String>()
        map[RADIO_COUNT] = "28"
        CommonRequest.getRankRadios(map, getCallback(tag))
    }

    private fun requestProvinces(tag: String) {
        CommonRequest.getProvinces(null, object : IDataCallBack<ProvinceList> {
            override fun onSuccess(provinceList: ProvinceList?) {
                provinceList?.let { view?.getDataSuccess(tag, provinceList.provinceList) }
            }

            override fun onError(p0: Int, p1: String?) {
                view?.getDataFail(p1)
            }
        })
    }

    private fun requestRadiosWithArea(tag: String, params: Any?) {
        @Suppress("UNCHECKED_CAST")
        val paramList = params as List<String>?
        val map = HashMap<String, String>()
        if (paramList != null) {
            map[RADIOTYPE] = paramList[0]
            var area = ""
            if (paramList[0] == "2") {
                map[PROVINCECODE] = paramList[1]
                area = paramList[1]
            }
            CommonRequest.getRadios(map, getCallback(tag, area))
        }
    }

    private fun requestRadiosWithCategory(tag: String, params: Any?) {
        val map = HashMap<String, String>()
        if (params != null) {
            map[RADIO_CATEGORY_ID] = params as String
            CommonRequest.getRadiosByCategory(map, object : IDataCallBack<RadioListByCategory> {
                override fun onError(p0: Int, p1: String?) {
                    view?.getDataFail(p1)
                }

                override fun onSuccess(radioListByCategory: RadioListByCategory?) {
                    radioListByCategory?.let {
                        view?.getDataSuccess(tag, it.radios)
                        doAsync { RadioDb.instance.saveRadio(it.radios, tag + map[RADIO_CATEGORY_ID]) }
                    }
                }
            })
        }
    }
}