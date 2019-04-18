package com.flyaudio.flyradioonline.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flyaudio.flyradioonline.Constant;
import com.flyaudio.flyradioonline.model.FmDataModel;
import com.flyaudio.flyradioonline.model.IFmdataModel;
import com.flyaudio.flyradioonline.task.play.activity.PlayingActivity;
import com.flyaudio.flyradioonline.task.play.fragment.PlayListFragment;
import com.flyaudio.flyradioonline.task.play.service.FmVoiceService;
import com.flyaudio.flyradioonline.task.search.FmSearchActivity;
import com.flyaudio.flyradioonline.util.Flog;
import com.flyaudio.flyradioonline.util.NetWorkUtil;

public class FmDataPresenter implements IFmDataPresenter{
    private static final String TAG = "FmDataPresenter";
    private static FmDataPresenter fmDataPresenter;
    private IFmdataModel fmDataModel;
    private PlayListFragment fmFragment;
    private PlayingActivity mActivity;
    private FmSearchActivity mSearchActivity;
    private FmVoiceService mService;
    private Context fmContext;
    private NetWorkUtil netWorkUtil;

    public static synchronized FmDataPresenter getInstance() {
        if (fmDataPresenter == null) {
            fmDataPresenter = new FmDataPresenter();
        }
        return fmDataPresenter;
    }

    public FmDataPresenter() {

        fmDataModel = FmDataModel.getInstance();
        fmDataModel.setFmDataPresenter(this);
    }


    @Override
    public void getFmData(int tag, Object params) {
        fmDataModel.requestFmData(tag, params);
    }

    @Override
    public void setFmFragment(PlayListFragment fmFragment) {
        this.fmFragment = fmFragment;
    }

    @Override
    public void setActivity(PlayingActivity activity) {
        this.mActivity = activity;
        this.fmContext = activity;
    }

    @Override
    public void setSearchActivity(FmSearchActivity searchActivity) {
        this.mSearchActivity = searchActivity;
    }

    @Override
    public void setService(FmVoiceService service) {
        this.mService = service;
        fmDataModel.setFmService(mService);
    }

    @Override
    public void setNetMonitor() {
        netWorkUtil = new NetWorkUtil(fmContext, mBroadcastReciver);
        netWorkUtil.registerNetWorkReceiver();//注册网络连接广播
    }

    @Override
    public void disNetMonitor() {
        if(netWorkUtil != null){
            netWorkUtil.ungisterNetWorkReceiver();
        }
    }

    @Override
    public void showFmData(int tag, Object object) {
        switch (tag){
            case Constant.MAIN_SCHEDULE_TOKEN:
                FmVoiceService.isStop = false;
                if(mActivity != null && !mActivity.isDestroyed()){
                    mActivity.showData(tag, object);
                }else{
                    mService.showData(tag, object);
                }
                break;
            case Constant.PAGE_SCHEDULE_TOKEN:
                if(fmFragment.getClass().equals(PlayListFragment.class)){
                    fmFragment.showData(tag, object);
                }
                break;
            case Constant.RAIDO_DATA_TOKEN:
                mService.showData(tag, object);
                break;
            case Constant.SEARCH_WORDS_TOKEN:
                mSearchActivity.showData(tag, object);
                break;
            case Constant.SEARCH_RADIO_TOKEN:
                mSearchActivity.showData(tag, object);
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver mBroadcastReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            boolean temp = netWorkUtil.getNetworkConnectState();
            if (!temp) {
                Flog.e(TAG, "onReceive(),网络不可用");
                mActivity.showDisNetwork();
            }
            if(temp){
                Flog.e(TAG, "onReceive(),网络可用");
                mActivity.showHasNetwork();
            }

        }
    };

}
