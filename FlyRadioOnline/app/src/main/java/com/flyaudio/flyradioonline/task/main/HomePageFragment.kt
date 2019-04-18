package com.flyaudio.flyradioonline.task.main

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.flyaudio.flyradioonline.*
import com.flyaudio.flyradioonline.Constant.HISTORY_TOKEN
import com.flyaudio.flyradioonline.Constant.POPULAR_TOKEN
import com.flyaudio.flyradioonline.data.db.RadioDb
import com.flyaudio.flyradioonline.entity.CanSelectRadioItem
import com.flyaudio.flyradioonline.task.play.activity.PlayingActivity
import com.flyaudio.flyradioonline.util.Flog
import com.flyaudio.flyradioonline.view.ObservableScrollView
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.layout_radio_history_edit_bar.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.ctx
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

@Suppress("UNCHECKED_CAST", "DeferredResultUnused")
class HomePageFragment : Fragment(), MainContract.MainView, ObservableScrollView.ScrollViewListener, View.OnClickListener {

    private val mainPresenter by lazy { MainPresenter() }
    private val popularRadioList: List<Radio> = listOf()
    private val popularRadioAdapter by lazy {
        PopularRadioAdapter(popularRadioList) {
            ctx.startActivity<PlayingActivity>(Constant.RADIO_DATA to it)
        }
    }
    private val historyRadioList: MutableList<CanSelectRadioItem> = mutableListOf()
    private var historyRadioAdapter: HistoryRadioAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_page, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Flog.e("liuxiaosheng", "onViewCreated")
        mainPresenter.attachView(this)
        initView()
        initData()
    }

    private fun initData() {
        getRadioPopular()
    }

    private fun initView() {
        setOnclickListener(this, btnAllSelect, btnCancel, btnClear)
        osvContainer.setScrollViewListener(this)
        rvHistory?.setItemViewCacheSize(0)
        rvHistory?.layoutManager = getLayoutManager()
        Flog.e("liuxiaosheng", "initView//" + historyRadioList.hashCode() + "//"+rvHistory.hashCode())
        historyRadioAdapter = HistoryRadioAdapter(historyRadioList, onClick = {
            if (!historyRadioAdapter!!.isEdit) {
                startActivity<PlayingActivity>(Constant.RADIO_DATA to it)
            } else {
                if (historyRadioAdapter!!.isAllSelect) btnAllSelect.setText(R.string.cancel_all_select)
                else btnAllSelect.setText(R.string.all_select)
            }
        }, onLongClick = {
            setViewVisible(llRadioHistoryEditBar)
        })
        rvHistory?.adapter = historyRadioAdapter
        rvPopular.setItemViewCacheSize(0)
        rvPopular.layoutManager = getLayoutManager()
        rvPopular.adapter = popularRadioAdapter
        srlTopRefresh.setOnRefreshListener { requestPopularRadio() }
    }

    override fun onClick(v: View?) {
        when (v) {
            btnAllSelect -> historyRadioAdapter?.setAllSelect()
            btnClear -> { historyRadioAdapter?.deleteSelect()
                if (historyRadioAdapter!!.isAllSelect|| historyRadioAdapter!!.itemCount==0) {
                    setViewGone(tvHistoryTitle, llRadioHistoryEditBar)
                    historyRadioAdapter!!.isEdit=false
                }
            }
            btnCancel -> {
                historyRadioAdapter?.isEdit = false
                historyRadioAdapter?.notifyDataSetChanged()
                setViewGone(llRadioHistoryEditBar)
            }
        }
    }

    private fun getLayoutManager(): StaggeredGridLayoutManager {
        return object : StaggeredGridLayoutManager(Constant.LIST_NUM, StaggeredGridLayoutManager.VERTICAL) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Flog.e("liuxiaosheng", "onResume")
        mainPresenter.attachView(this)
        getRadioHistory()
    }

    private fun getRadioPopular() = async(UI) {
        bg { RadioDb.instance.getRadioFromDb(POPULAR_TOKEN) }.await().also {
            if (it.isNotEmpty()) popularRadioAdapter.setNewData(it)
            else requestPopularRadio()
        }
    }

    private fun requestPopularRadio() {
        mainPresenter.requestFmData(POPULAR_TOKEN, null)
    }

    private fun getRadioHistory() {
        Flog.e("liuxiaosheng", "getRadioHistory//"+this.hashCode())
        mainPresenter.requestFmData(HISTORY_TOKEN, null)
    }

    override fun getDataSuccess(tag: String, data: Any?) {
        Flog.e("liuxiaosheng", "getDataSuccess//" + tag + "//" + data)
        runOnUiThread {
            when (tag) {
                POPULAR_TOKEN -> {
                    if (srlTopRefresh.isRefreshing) srlTopRefresh.isRefreshing = false
                    data?.let { popularRadioAdapter.setNewData(data as List<Radio>) }
                }
                HISTORY_TOKEN -> {
                    (data as List<Radio>?)?.let {
                        if (it.isEmpty()) {
                            setViewGone(tvHistoryTitle)
                        } else {
                            setViewVisible(tvHistoryTitle)

                            val list: MutableList<CanSelectRadioItem> = mutableListOf()
                            it.forEachIndexed { index, radio ->
                                val canSelectRadioItem = CanSelectRadioItem(false)
                                canSelectRadioItem.dataId = radio.dataId
                                canSelectRadioItem.radioName = radio.radioName
                                canSelectRadioItem.coverUrlLarge = radio.coverUrlLarge
                                list.add(canSelectRadioItem)
                            }
                            Flog.e("liuxiaosheng", "getDataSuccess//" + list.hashCode())
                            historyRadioAdapter?.setNewData(list)

                        }
                    }
                }
            }
        }
    }

    override fun getDataFail(error: String?) {
        runOnUiThread {
            if (srlTopRefresh.isRefreshing) srlTopRefresh.isRefreshing = false
            toast("数据请求失败，请检查网络后重新刷新！")
            Log.d("yuan", "error info$error")
        }
    }

    override fun onScrollChanged(scrollView: ObservableScrollView?, x: Int, y: Int, oldx: Int, oldy: Int) {
        val location = IntArray(2)
        tvPopularTitle.getLocationOnScreen(location)
        val locationY = location[1] - 110
        val topHeight = (activity as MainActivity).topHeight
        Log.d("yuan", "locationY=$locationY  topHeight=$topHeight")
        if (locationY <= topHeight && (tvRecommendTopPanel.visibility == View.GONE || tvRecommendTopPanel.visibility == View.INVISIBLE)) {
            tvRecommendTopPanel.visibility = View.VISIBLE
        }
        if (locationY > topHeight && tvRecommendTopPanel.visibility == View.VISIBLE) {
            tvRecommendTopPanel.visibility = View.GONE
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) initData()
    }
}