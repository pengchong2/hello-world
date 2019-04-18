package com.flyaudio.flyradioonline.task.play.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyaudio.flyradioonline.Constant;
import com.flyaudio.flyradioonline.presenter.FmDataPresenter;
import com.flyaudio.flyradioonline.util.ControlUtil;
import com.flyaudio.flyradioonline.util.Flog;
import com.flyaudio.flyradioonline.util.ToolUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.receive.WireControlReceiver;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class FmVoiceService extends Service {
    private static final String TAG = "FmVoiceService";
    private XmPlayerManager mPlayerManager;
    private FmDataPresenter fmDataPresenter;
    private AudioManager audioManager;
    private MediaSession mMediaSession;
    private MediaSession mSession;
    private MyOnAudioFocusChangeListener onAudioFocusChangeListener;
    private Handler mHandler = new Handler();
    private List<String> searchList;
    public static int CURRENT_PLAYDAY = Constant.TODAY;
    public static boolean isStop = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Flog.e(TAG, "FmVoiceService//onCreate");
        registerNetWorkReceiver();
        searchList = new ArrayList<>();
        mPlayerManager = XmPlayerManager.getInstance(getApplicationContext());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        fmDataPresenter = FmDataPresenter.getInstance();
        fmDataPresenter.setService(this);
        resetSession();
        setAppExit();
        ControlUtil.getInstance().setPlayerManager(mPlayerManager);
        ControlUtil.getInstance().setPlayService(this);
        ControlUtil.getInstance().setContext(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void registerNetWorkReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.flyaudio.flyradioonline.action");
        getApplicationContext().registerReceiver(mVoiceReceiver, intentFilter);
    }

    private void ungisterNetWorkReceiver(){
        getApplicationContext().unregisterReceiver(mVoiceReceiver);
    }

    public void showData(int tag, Object param){
        Flog.e("liuxiaosheng", "FmVoiceService//showData//"+tag);
        if(Constant.RAIDO_DATA_TOKEN == tag && param != null){
            final List<Radio> radios = (List<Radio>) param;
            if(!radios.isEmpty()){
                Flog.e("liuxiaosheng", "FmVoiceService//showData//"+radios.get(0).getRadioName());
                ControlUtil.getInstance().getSchedule(radios.get(0), getApplicationContext(), -1);
            }
        }else if(Constant.MAIN_SCHEDULE_TOKEN == tag && param != null){
            Flog.e("liuxiaosheng", "FmVoiceService//playRaido//1");
            ControlUtil.getInstance().playRaido(param);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Flog.e("liuxiaosheng", "FmVoiceService//playRaido//2");
                    ControlUtil.getInstance().initRadioUI();
                }
            }, 500);
        }
    }

    public void sendVoiceData(String data){
        Intent intent = new Intent();
        intent.setAction("cn.flyaudio.flyradioonline.action");
        intent.putExtra("result", data);
        getApplicationContext().sendBroadcast(intent);
    }

    private void setAppExit(){
        SystemProperties.set("fly.txz.getversion", "1");
    }

    public void getFoucs(){
        onAudioFocusChangeListener = new MyOnAudioFocusChangeListener();
        int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);//获取焦点
        Flog.e(TAG, "FmPlayService//焦点//"+result);
    }

    public void resetSession(){

        if (android.os.Build.VERSION.SDK_INT >= 21){
            Flog.e(TAG, "抢占注册");
            mSession = new MediaSession(getApplicationContext(), "MusicService");
            mSession.setCallback(new MediaSession.Callback() {

                @Override
                public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                    Flog.e(TAG, "onMediaButtonEvent//"+mediaButtonIntent.getAction());
                    return super.onMediaButtonEvent(mediaButtonIntent);
                }

                @Override
                public void onSkipToNext() {
                    super.onSkipToNext();
                    Flog.e(TAG, "onSkipToNext");
                    if(mPlayerManager.hasNextSound()){
                        mPlayerManager.playNext();
                    }
                }

                @Override
                public void onSkipToPrevious() {
                    super.onSkipToPrevious();
                    Flog.e(TAG, "onSkipToPrevious");
                    if(mPlayerManager.hasPreSound()){
                        mPlayerManager.playPre();
                    }
                }
            });

            mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
                    | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

            Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            ComponentName mediaButtonReceiverComponent = new ComponentName(getApplicationContext(), WireControlReceiver.class);
            mediaButtonIntent.setComponent(mediaButtonReceiverComponent);
            PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);
            mSession.setMediaButtonReceiver(mediaPendingIntent);

            AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder();
            audioAttributesBuilder.setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);

            mSession.setPlaybackToLocal(audioAttributesBuilder.build());
            mSession.setActive(true);

            PlaybackState state = new PlaybackState.Builder()
                    .setActions(
                            PlaybackState.ACTION_PLAY
                                    | PlaybackState.ACTION_PLAY_PAUSE
                                    | PlaybackState.ACTION_PLAY_FROM_MEDIA_ID
                                    | PlaybackState.ACTION_PAUSE
                                    | PlaybackState.ACTION_SKIP_TO_NEXT
                                    | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                    .setState(PlaybackState.STATE_PLAYING, 0, 1,
                            SystemClock.elapsedRealtime()).build();
            mSession.setPlaybackState(state);
        }
    }

    class MyOnAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch(focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN: // 重新获得焦点, 可做恢复播放，恢复后台音量的操作
                    Flog.e("keke","AUDIOFOCUS_GAIN1//"+mPlayerManager.getPlayerStatus());
                    if(mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PAUSED){
                        mPlayerManager.play();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS: // 永久丢失焦点除非重新主动获取，这种情况是被其他播放器抢去了焦点，为避免与其他播放器混音，可将音乐暂停
                    Flog.e("keke","AUDIOFOCUS_LOSS//"+mPlayerManager.getPlayerStatus());
                    if(mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_STARTED){
                        mPlayerManager.pause();
                    }

                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: // 暂时丢失焦点，这种情况是被其他应用申请了短暂的焦点，可压低后台音量
                    Flog.e("keke","AUDIOFOCUS_LOSS_TRANSIENT//"+mPlayerManager.getPlayerStatus());
                    if(mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_STARTED){
                        mPlayerManager.pause();
                    }

                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: // 短暂丢失焦点，这种情况是被其他应用申请了短暂的焦点希望其他声音能压低音量（或者关闭声音）凸显这个声音（比如短信提示音），
                    Flog.e("keke","AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    break;
                default:
                    break;
            }
        }
    }

    private BroadcastReceiver mVoiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int radioId = intent.getIntExtra("radio_id", -1);
            int playState = intent.getIntExtra("play_state", -1);
            String search = intent.getStringExtra("search");
            String control = intent.getStringExtra("action");
            Flog.e(TAG, "VoiceReceiver//"+radioId+"//"+playState+"//"+search+"//"+control);
            if(!TextUtils.isEmpty(control)){
                if("prev".equals(control) && mPlayerManager.hasPreSound()){
                    Flog.e(TAG, "VoiceReceiver//playPre");
                    mPlayerManager.playPre();
                }else if("next".equals(control) && mPlayerManager.hasNextSound()
                        && !"直播".equals(ToolUtil.getProgramStatus((Schedule) mPlayerManager.getCurrSound()))){
                    Flog.e(TAG, "VoiceReceiver//playNext");
                    mPlayerManager.playNext();
                }
            }
            if(playState != -1){
                switch (playState){
                    case 0:
                        Flog.e(TAG, "VoiceReceiver//stopPlay");
                        ControlUtil.getInstance().stopPlay();
                        isStop = true;
                        break;
                    case 1:
                        Flog.e(TAG, "VoiceReceiver//pause");
                        mPlayerManager.pause();
                        break;
                    case 2:
                        if(!mPlayerManager.isPlaying() && mPlayerManager.getPlayerStatus() != PlayerConstants.STATE_IDLE){
                            mPlayerManager.play();
                        }
                        break;
                    case 3:
                        if(radioId != -1){
                            Flog.e("liuxiaosheng", "FmVoiceService//mVoiceReceiver//"+radioId);
                            fmDataPresenter.getFmData(Constant.RAIDO_DATA_TOKEN, radioId + "");
                        }
                        break;
                    case 4:

                        break;
                    case 5:
                        break;
                    default:
                        break;
                }
            }else if(!TextUtils.isEmpty(search)){
                searchVoice(search, "");
            }

        }
    };

    public void searchVoice(String search, String searchType){
        List<String> searchResult = getRealSearch(search, searchType);
        List<Object> parmas = new ArrayList<>();
        parmas.add(searchResult.get(0));
        parmas.add(searchResult.get(1));
        parmas.add(search);
        fmDataPresenter.getFmData(Constant.RADIO_SEARCH_TOKEN, parmas);
    }


    private List<String> getRealSearch(String search, String searchType){
        if(!TextUtils.isEmpty(search)) {
            searchList.clear();
            JSONObject jsonObject1 = JSONObject.parseObject(search);
            String artistData = jsonObject1.getString("artist");
            List<String> artistList = JSON.parseArray(artistData, String.class);
            if(!artistList.isEmpty() && (Constant.ARTIST.equals(searchType) || TextUtils.isEmpty(searchType))){
                Flog.e(TAG, "getRealSearch//1//" + artistList.get(0));
                searchList.add(artistList.get(0));
                searchList.add(Constant.ARTIST);
                return searchList;
            }else {
                String wordsData = jsonObject1.getString("keywords");
                List<String> wordsList = JSON.parseArray(wordsData, String.class);
                if(!wordsList.isEmpty() && (Constant.KEYWORDS.equals(searchType) || TextUtils.isEmpty(searchType))){
                    Flog.e(TAG, "getRealSearch//2//" + wordsList.get(0));
                    searchList.add(wordsList.get(0));
                    searchList.add(Constant.KEYWORDS);
                    return searchList;
                }else{
                    String txtData = jsonObject1.getString("text");
                    if (!TextUtils.isEmpty(txtData)) {
                        Flog.e(TAG, "getRealSearch//3//" + txtData);
                        searchList.add(txtData);
                        searchList.add(Constant.TEXT);
                        return searchList;
                    }
                }
            }
            Flog.e(TAG, "getRealSearch//5//null//");
        }
        return null;
    }


    private void releaseMediaSession() {
        if (mMediaSession != null) {
            mMediaSession.setCallback(null);
            mMediaSession.setActive(false);
            mMediaSession.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Flog.e(TAG, "FmVoiceService//onDestroy");
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        ungisterNetWorkReceiver();
        releaseMediaSession();
    }
}
