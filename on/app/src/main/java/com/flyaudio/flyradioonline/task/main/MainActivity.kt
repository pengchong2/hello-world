package com.flyaudio.flyradioonline.task.main

//import com.flyaudio.flyradioonline.task.play.activity.PlayingActivity
import android.app.Activity
import android.app.Fragment
import android.app.FragmentTransaction
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import com.flyaudio.flyradioonline.R
import com.flyaudio.flyradioonline.setViewGone
import com.flyaudio.flyradioonline.setViewVisible
import com.flyaudio.flyradioonline.startActivity
import com.flyaudio.flyradioonline.task.play.activity.PlayingActivity
import com.flyaudio.flyradioonline.task.play.service.FmVoiceService
import com.flyaudio.flyradioonline.task.search.FmSearchActivity
import com.flyaudio.flyradioonline.ui.IActionCallback
import com.flyaudio.flyradioonline.util.ControlUtil
import com.flyaudio.flyradioonline.util.Flog
import com.flyaudio.flyradioonline.util.NetworkUtil
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class MainActivity : Activity() , IActionCallback {
    private val mainPresenter by lazy { MainPresenter.instance }
    private val xmPlayerManager by lazy { XmPlayerManager.getInstance(ctx) }
    private val controlUtil by lazy { ControlUtil.getInstance() }
    private var transaction: FragmentTransaction? = null
    private var homePageFragment: HomePageFragment? = null
    private var allFragment: AllFragment? = null
    var topHeight: Int = 0
    private val mSearchImgView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        xmPlayerManager.init()
        initView()

    }


    override fun stopIcon() {
        if(vpiPlayPage.isPlaying){
            Flog.e("liuxiaosheng", "MainActivity//stopIcon")
            vpiPlayPage.stop()
        }

    }

    override fun startIcon() {
        if(!vpiPlayPage.isPlaying()){
            Flog.e("liuxiaosheng", "MainActivity//startIcon")
            vpiPlayPage.start()
        }
    }

    private fun initView() {
        homePageFragment = HomePageFragment()
        allFragment = AllFragment()

        transaction = fragmentManager.beginTransaction()
        transaction?.add(R.id.flFragmentContainer, allFragment)
        transaction?.add(R.id.flFragmentContainer, homePageFragment)
        transaction?.commit()


        vpiPlayPage.setOnClickListener {
            Flog.e("liuxiaosheng", "vpiPlayPage//setOnClickListener")
            startActivity<PlayingActivity>() }
        fm_main_to_search.setOnClickListener{startActivity<FmSearchActivity>()}
        rgTab.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbHomePage -> showHomeFragment()
                R.id.rbAll -> showAllFragment()
            }
        }
        rgTab.check(R.id.rbHomePage)
        NetworkUtil.setNetworkListener { showNetworkDisconnectInfoIfNeed(it) }
        startVoiceService()
        showHomeFragment()
    }

    private fun setCurrentFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().replace(R.id.flFragmentContainer, fragment).commitAllowingStateLoss()
    }

    private fun showNetworkDisconnectInfoIfNeed(isOpenNetwork: Boolean) {
        if (isOpenNetwork) {
            setViewGone(ivDisconnect, tvDisconnect)
            setViewVisible(flFragmentContainer)
        } else {
            setViewVisible(ivDisconnect, tvDisconnect)
            setViewGone(flFragmentContainer)
        }
    }

    private fun startVoiceService() {
        startService(Intent(this, FmVoiceService::class.java))
    }

    private fun hideFragments(transaction: FragmentTransaction?) {
        if (transaction != null) {
            if (homePageFragment != null) {
                transaction.hide(homePageFragment)
            }
            if (allFragment != null) {
                transaction.hide(allFragment)
            }

        }
    }

    private fun showHomeFragment() {
        //mainPresenter.attachView(this!!.homePageFragment!!)
        transaction = fragmentManager.beginTransaction()
        hideFragments(transaction)
        if (transaction != null && homePageFragment != null) {
            transaction?.show(homePageFragment)
            transaction?.commit()
        }
    }

    private fun showAllFragment() {
        //mainPresenter.attachView(this!!.allFragment!!)
        transaction = fragmentManager.beginTransaction()
        hideFragments(transaction)
        if (transaction != null && allFragment != null) {
            transaction?.show(allFragment)
            transaction?.commit()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val frame = Rect()
        window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top//状态栏高度
        val titleBarHeight = rlTabContainer.top//标题栏高度
        topHeight = titleBarHeight + statusBarHeight
    }

    override fun onResume() {
        super.onResume()
        showNetworkDisconnectInfoIfNeed(NetworkUtil.isOpenNetwork)
        controlUtil.setActionCallback(this)
        if (xmPlayerManager.isPlaying && !vpiPlayPage.isPlaying()){
            Flog.e("liuxiaosheng", "MainActivity//onResume//start")
            vpiPlayPage.start()
        }else if(!xmPlayerManager.isPlaying && vpiPlayPage.isPlaying()){
            Flog.e("liuxiaosheng", "MainActivity//onResume//stop")
            vpiPlayPage.stop()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtil.clearNetworkListener()
    }


}
