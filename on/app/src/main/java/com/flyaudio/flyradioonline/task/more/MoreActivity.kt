package com.flyaudio.flyradioonline.task.more

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.flyaudio.flyradioonline.*
import com.flyaudio.flyradioonline.data.db.RadioDb
import com.flyaudio.flyradioonline.entity.CanSelectRadioItem
import com.flyaudio.flyradioonline.task.play.activity.PlayingActivity
import com.flyaudio.flyradioonline.ui.IActionCallback
import com.flyaudio.flyradioonline.util.ControlUtil
import com.flyaudio.flyradioonline.util.Flog
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_more.*
import kotlinx.android.synthetic.main.layout_radio_history_edit_bar.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity

class MoreActivity : Activity(), View.OnClickListener, IActionCallback {

    private var historyRadioAdapter: MoreHistoryRadioAdapter? = null
    private val historyRadioList: MutableList<CanSelectRadioItem> = mutableListOf()
    private val xmPlayerManager by lazy { XmPlayerManager.getInstance(ctx) }
    private val controlUtil by lazy { ControlUtil.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)
        initView()
        getRadioHistory()
    }

    private fun initView() {
        setOnclickListener(this, ivBack, vpiMorePlayPage, btnAllSelect, btnCancel, btnClear)
        rvHistory.setItemViewCacheSize(0)
        rvHistory.layoutManager = StaggeredGridLayoutManager(Constant.LIST_NUM, StaggeredGridLayoutManager.VERTICAL)
        historyRadioAdapter = MoreHistoryRadioAdapter(historyRadioList, onClick = {
            if (!historyRadioAdapter!!.isEdit) {
                startActivity<PlayingActivity>(Constant.RADIO_DATA to it)
            } else {
                if (historyRadioAdapter!!.isAllSelect) btnAllSelect.setText(R.string.cancel_all_select)
                else btnAllSelect.setText(R.string.all_select)
            }
        }, onLongClick = {
            setViewVisible(llRadioHistoryEditBar)
        })
        rvHistory.adapter = historyRadioAdapter
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> finish()
            vpiMorePlayPage -> startActivity<PlayingActivity>()
            btnAllSelect -> historyRadioAdapter?.setAllSelect()
            btnClear -> {
                historyRadioAdapter?.deleteSelect()
                if (historyRadioAdapter!!.isAllSelect)
                    setViewGone(llRadioHistoryEditBar)
            }
            btnCancel -> {
                historyRadioAdapter?.isEdit = false
                historyRadioAdapter?.notifyDataSetChanged()
                setViewGone(llRadioHistoryEditBar)
            }
        }
    }

    override fun stopIcon() {
        if(vpiMorePlayPage.isPlaying){
            Flog.e("liuxiaosheng", "MainActivity//stopIcon")
            vpiMorePlayPage.stop()
        }

    }

    override fun startIcon() {
        if(!vpiMorePlayPage.isPlaying()){
            Flog.e("liuxiaosheng", "MainActivity//startIcon")
            vpiMorePlayPage.start()
        }
    }

    private fun getRadioHistory() {
        doAsync {
            val radioHistoryList = RadioDb.instance.getRadioHistoryListFromDb()
            radioHistoryList.let {
                val list: MutableList<CanSelectRadioItem> = mutableListOf()
                it.forEach {
                    val canSelectRadioItem = CanSelectRadioItem(false)
                    canSelectRadioItem.dataId = it.dataId
                    canSelectRadioItem.radioName = it.radioName
                    canSelectRadioItem.coverUrlLarge = it.coverUrlLarge
                    list.add(canSelectRadioItem)
                }
                historyRadioAdapter?.setNewData(list)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        controlUtil.setActionCallback(this)
        if (xmPlayerManager.isPlaying && !vpiMorePlayPage.isPlaying()){
            Flog.e("liuxiaosheng", "MainActivity//onResume//start")
            vpiMorePlayPage.start()
        }else if(!xmPlayerManager.isPlaying && vpiMorePlayPage.isPlaying()){
            Flog.e("liuxiaosheng", "MainActivity//onResume//stop")
            vpiMorePlayPage.stop()
        }
    }
}
