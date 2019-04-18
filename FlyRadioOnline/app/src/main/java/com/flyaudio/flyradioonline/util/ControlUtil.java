package com.flyaudio.flyradioonline.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;


import com.flyaudio.flyradioonline.Constant;
import com.flyaudio.flyradioonline.presenter.FmDataPresenter;
import com.flyaudio.flyradioonline.task.play.activity.PlayingActivity;
import com.flyaudio.flyradioonline.task.play.fragment.PlayListFragment;
import com.flyaudio.flyradioonline.task.play.service.FmVoiceService;
import com.flyaudio.flyradioonline.ui.IActionCallback;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmDataCallback;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzehao on 18-5-7.
 */

public class ControlUtil {

    private static final String TAG = "ControlUtil";
    private static ControlUtil controlUtil;
    private PlayingActivity fmPlayActivity;
    private Context fmContext;
    private PlayListFragment fmPlayFragment;
    private FmVoiceService fmVoiceService;
    private XmPlayerManager mPlayerManager;
    private IActionCallback actionCallback;

    public static ControlUtil getInstance(){
        if(controlUtil == null){
            controlUtil = new ControlUtil();
        }

        return controlUtil;
    }


    public void setFmPlayActivity(PlayingActivity fmPlayActivity, XmPlayerManager xmPlayerManager){
        this.fmPlayActivity = fmPlayActivity;
        this.mPlayerManager = xmPlayerManager;
        initFmPlayer();
    }

    public void setContext(Context context){
        this.fmContext = context;
    }

    public void setPlayerManager(XmPlayerManager mPlayerManager){
        this.mPlayerManager = mPlayerManager;
        initFmPlayer();
    }

    public void setFmPlayFragment(PlayListFragment fmPlayFragment){
        this.fmPlayFragment = fmPlayFragment;
    }

    public void setPlayService(FmVoiceService fmVoiceService){
        this.fmVoiceService = fmVoiceService;
    }

    public void setActionCallback(IActionCallback actionCallback){
        this.actionCallback = actionCallback;
    }

    public void releasePlayer(){
        if(mPlayerManager != null){
            removeListener();
        }
        XmPlayerManager.release();
        CommonRequest.release();
    }

    public void resetSession(){
        if(fmVoiceService != null){
            fmVoiceService.resetSession();
        }
    }

    private void removeListener(){
        mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.removeAdsStatusListener(mAdsListener);
        mPlayerManager.removeOnConnectedListerner(mIConnectListener);
    }

    private void addListener(){
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.addAdsStatusListener(mAdsListener);
        mPlayerManager.addOnConnectedListerner(mIConnectListener);
    }

    private void playIcon(){
        if(actionCallback != null){
            actionCallback.startIcon();
        }
    }

    private void stopIcon(){
        if(actionCallback != null){
            actionCallback.stopIcon();
        }
    }

    public void resetProgress(Radio radio){
        if(fmPlayActivity != null){
            fmPlayActivity.resetPlayProgress(radio);
        }
    }

    public void resetFmPlayer(List<Track> playList, int playPos){
        Flog.e("liuzehao", "resetFmPlayer//playPos//"+playPos+"//"+playList.toString());
        mPlayerManager.init();
        initFmPlayer();
        mPlayerManager.playList(playList, playPos);
    }

    public void getSchedule(final Radio radio, Context context, int weekday){
        if (!isOpenNetwork(context)) {
            return;
        }
        if (radio == null) {
            return;
        }
        List<Object> paramList = new ArrayList<>();
        paramList.add(weekday);
        paramList.add(radio);
        FmDataPresenter.getInstance().getFmData(Constant.MAIN_SCHEDULE_TOKEN, paramList);
    }

    public void playRaido(Object param){
        List<Object> paramList = (List<Object>) param;
        if(paramList != null && !paramList.isEmpty()){
            List<Schedule> scheduleList = (List<Schedule>) paramList.get(1);
            if(fmPlayActivity != null && !fmPlayActivity.isDestroyed()){
                fmPlayActivity.setPlayList(scheduleList);
            }
            playRadio(scheduleList, -1);
        }else{

        }
    }

    public void stopPlay(){
        if(fmPlayActivity != null && !fmPlayActivity.isDestroyed()){
            fmPlayActivity.finish();
        }
        mPlayerManager.stop();
        initFmPlayer();

        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.HOME");
        fmContext.startActivity(intent);
    }

    public void initRadioUI(){
        if(fmPlayActivity != null && !fmPlayActivity.isDestroyed()){
            fmPlayActivity.initPlayerUI();
        }
    }

    public void playRadio(List<Schedule> scheduleList, int startIndex){
        Flog.e("ttt", "ControlUtil//playRadio//"+startIndex);
        for(Schedule schedule:scheduleList){
            Flog.e("ttt", "ControlUtil//playRadio//"+schedule.getEndTime()+"//"+schedule.getStartTime()+"//"+schedule.getRelatedProgram().getProgramName());
        }
        mPlayerManager.playSchedule(scheduleList ,startIndex);
    }

    public void initFmPlayer(){
        Flog.e("ttt", "ControlUtil//initFmPlayer");
        mPlayerManager.init();
        removeListener();
        addListener();
        mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
        mPlayerManager.setPlayListChangeListener(new IXmDataCallback() {
            @Override
            public void onDataReady(List<Track> list, boolean hasMorePage, boolean isNextPage) {
                Flog.e("ttt", "ControlUtil//setPlayListChangeListener//onDataReady//" + list.size());

            }

            @Override
            public void onError(int code, String message, boolean isNextPage) throws RemoteException {
                Flog.e("ttt", "ControlUtil//setPlayListChangeListener//onError//" + code);
            }

            @Override
            public IBinder asBinder() {
                return null;
            }
        });
        //ActivityCompat.requestPermissions(fmPlayActivity ,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE} ,0);
    }

    private XmPlayerManager.IConnectListener mIConnectListener = new XmPlayerManager.IConnectListener() {
        @Override
        public void onConnected() {
            Flog.e("ttt", "ControlUtil//addOnConnectedListerner//onConnected");
            mPlayerManager.removeOnConnectedListerner(this);
            mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
        }
    };

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
            Flog.e("ttt", "ControlUtil//onSoundPrepared");
            fmPlayActivity.setBarEnble();
        }

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            Flog.e("ttt", "ControlUtil//onSoundSwitch//"+mPlayerManager.getCurrentIndex());
            PlayableModel model = mPlayerManager.getCurrSound();
            if (model != null) {
                String title = null;
                String coverUrl = null;
                String source = null;
                if (model instanceof Schedule) {
                    Flog.e("ttt", "ControlUtil//onSoundSwitch//Schedule");
                    Schedule schedule = (Schedule) model;
                    source = schedule.getRadioName();
                    title = schedule.getRelatedProgram().getProgramName();
                    coverUrl = schedule.getRelatedProgram().getBackPicUrl();

                    fmPlayActivity.setFmPlayTitle(title, source);
                    fmPlayActivity.setFmPlayType(ToolUtil.getProgramStatus(schedule));
                    fmPlayActivity.setCover(coverUrl);
                    fmPlayActivity.initRadio(schedule);
                    fmPlayActivity.setPlayIndex(mPlayerManager.getCurrentIndex());
                    mPlayerManager.seekTo(0);

                    if(fmPlayFragment != null && !fmPlayFragment.isDestroy()){
                        fmPlayFragment.notifyData();
                    }
                }
                //fmPlayActivity.showLoading();
                Flog.e("ttt", "ControlUtil//onSoundSwitch//"+title+"//"+mPlayerManager.getCurrentIndex());
            }
            fmPlayActivity.updateButtonStatus();
        }

        @Override
        public void onPlayStop() {
            Flog.e("ttt", "ControlUtil//onPlayStop//"+mPlayerManager.getPlayerStatus());
            fmPlayActivity.setBtnStop();
            //fmPlayActivity.stopLiveProgress();
            stopIcon();
        }

        @Override
        public void onPlayStart() {
            Flog.e("ttt", "ControlUtil//onPlayStart//"+mPlayerManager.getCurrPlayType()+"//"
                    +mPlayerManager.getCurrentIndex() +"//"+mPlayerManager.getPlayerStatus());
            fmPlayActivity.setBtnStart();
            //fmPlayActivity.startLiveProgress();
            playIcon();
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            Flog.e("keke", "ControlUtil//onPlayProgress//"+currPos+"//"+duration);
            fmPlayActivity.setPlayProgress(currPos, duration);
        }

        @Override
        public void onPlayPause() {
            Flog.e("ttt", "ControlUtil//onPlayPause//"+mPlayerManager.getPlayerStatus());
            fmPlayActivity.setBtnStop();
            stopIcon();
            //fmPlayActivity.stopLiveProgress();
        }

        @Override
        public void onSoundPlayComplete() {
            Flog.e("ttt", "ControlUtil//onSoundPlayComplete//"+mPlayerManager.getPlayerStatus());
            if(mPlayerManager.hasNextSound()){
                Flog.e("ttt", "ControlUtil//onSoundPlayComplete//hasNext");
                fmPlayActivity.playNext();
            }else{
            }
            fmPlayActivity.setBtnStop();
            stopIcon();
        }

        @Override
        public boolean onError(XmPlayerException exception) {
            Flog.e("ttt", "ControlUtil//onError//"+exception.getMessage());
            fmPlayActivity.setBtnStop();
            stopIcon();
            //fmPlayActivity.stopLiveProgress();
            //fmPlayActivity.stopLoading();
            return false;
        }

        @Override
        public void onBufferProgress(int position) {
            Flog.e("ttt", "ControlUtil//onBufferProgress//"+position);
        }

        @Override
        public void onBufferingStart() {
            Flog.e("ttt", "ControlUtil//onBufferingStart");
            //fmPlayActivity.showLoading();

        }

        @Override
        public void onBufferingStop() {
            Flog.e("ttt", "ControlUtil//onBufferingStop");
            //fmPlayActivity.stopLoading();
        }

    };


    private IXmAdsStatusListener mAdsListener = new IXmAdsStatusListener() {

        @Override
        public void onStartPlayAds(Advertis ad, int position) {

        }

        @Override
        public void onStartGetAdsInfo() {
            Flog.e(TAG, "ControlUtil//onStartGetAdsInfo");
            //fmPlayActivity.startGetAdsInfo();
        }

        @Override
        public void onGetAdsInfo(final AdvertisList ads) {
        }

        @Override
        public void onError(int what, int extra) {
            Flog.e(TAG, "ControlUtil//onError what:" + what + ", extra:" + extra);
        }

        @Override
        public void onCompletePlayAds() {
            Flog.e(TAG, "ControlUtil//onCompletePlayAds");
            //fmPlayActivity.completePlayAds();
        }

        @Override
        public void onAdsStopBuffering() {
            Flog.e(TAG, "ControlUtil//onAdsStopBuffering");
        }

        @Override
        public void onAdsStartBuffering() {
            Flog.e(TAG, "ControlUtil//onAdsStartBuffering");
        }
    };

    public static boolean isOpenNetwork(Context context) {
        if(context != null){
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connManager.getActiveNetworkInfo() != null) {
                return connManager.getActiveNetworkInfo().isAvailable();
            }
        }
        return false;
    }
}
