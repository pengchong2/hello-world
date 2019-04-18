package com.flyaudio.flyradioonline.task.play.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyaudio.flyradioonline.Constant;
import com.flyaudio.flyradioonline.R;
import com.flyaudio.flyradioonline.data.db.RadioDb;
import com.flyaudio.flyradioonline.presenter.FmDataPresenter;
import com.flyaudio.flyradioonline.task.main.MainActivity;
import com.flyaudio.flyradioonline.task.play.fragment.PlayListFragment;
import com.flyaudio.flyradioonline.task.play.service.FmVoiceService;
import com.flyaudio.flyradioonline.util.ControlUtil;
import com.flyaudio.flyradioonline.util.Flog;
import com.flyaudio.flyradioonline.util.ToolUtil;
import com.flyaudio.flyradioonline.view.VoicePlayingIcon;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;

import java.util.List;

public class PlayingActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "liuxiaosheng";

    private static final int PROGRESS_SHOW = 0x001;

    private Button fmPlayBack;
    private Button fmListBack;
    private Button fmPlayTable;
    private Button fmPlayControl;
    private Button fmPlayPrevious;
    private Button fmPlayNext;
    private Button fmPlayCollect;
    private RadioGroup dayGroup;
    private RadioButton lastDay;
    private RadioButton today;
    private RadioButton tomorrow;
    private TextView fmPlayTitle;
    private TextView fmPlayResource;
    private TextView fmPlayType;
    private TextView fmPlayCur;
    private TextView fmPlayDuration;
    private ImageView fmPlayingCover;
    private SeekBar fmPlayBar;
    private RelativeLayout mainPlayRe;
    private FrameLayout playlistFl;
    private LinearLayout pagePlayll;
    private XmPlayerManager mPlayerManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment mCurentFragment;
    private PlayListFragment todayFragment;
    private PlayListFragment lastdayFragment;
    private PlayListFragment tomorrowFragment;
    private Radio radio;
    private String radioId;
    private String radioName;
    private String radioURL;
    private String mCurrentTitle;
    private int mPlayIndex;
    private long mCurrentTime;
    private long mLastTime;
    private int mCurrentPosition;
    private List<Schedule> scheduleList;
    private boolean mUpdateProgress = true;
    private boolean isFirstLive = true;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == PROGRESS_SHOW){
                mCurrentPosition = (int) msg.obj;
                fmPlayCur.setText(ToolUtil.updateTime(mCurrentPosition));
                fmPlayBar.setProgress(mCurrentPosition * 1000);
                if((mCurrentPosition * 1000) >= fmPlayBar.getMax()){
                    mHandler.removeMessages(PROGRESS_SHOW);
                }else{
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(PROGRESS_SHOW, mCurrentPosition + 1), 1000);
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        initView();
        initData();
    }

    private void initView(){
        playlistFl = (FrameLayout) findViewById(R.id.fm_list_container);
        mainPlayRe = (RelativeLayout) findViewById(R.id.fm_list_play_re);
        pagePlayll = (LinearLayout) findViewById(R.id.fm_main_play_ll);
        fmListBack = (Button) findViewById(R.id.fm_list_back);
        fmPlayBack = (Button) findViewById(R.id.fm_img_back);
        fmPlayTable = (Button) findViewById(R.id.fm_img_list);
        fmPlayControl = (Button) findViewById(R.id.fm_img_play_state);
        fmPlayPrevious = (Button) findViewById(R.id.fm_img_previous);
        fmPlayNext = (Button) findViewById(R.id.fm_img_next);
        fmPlayCollect = (Button) findViewById(R.id.fm_img_collection);
        dayGroup = (RadioGroup) findViewById(R.id.fm_playday_rg);
        lastDay = (RadioButton) findViewById(R.id.fm_lastday_rb);
        today = (RadioButton) findViewById(R.id.fm_today_rb);
        tomorrow = (RadioButton) findViewById(R.id.fm_tomorrow_rb);
        fmPlayTitle = (TextView) findViewById(R.id.fm_tv_program_name);
        fmPlayResource = (TextView) findViewById(R.id.fm_tv_show_name);
        fmPlayType = (TextView) findViewById(R.id.fm_tv_playing_type);
        fmPlayCur = (TextView) findViewById(R.id.fm_tv_playing_playtime);
        fmPlayDuration = (TextView) findViewById(R.id.fm_tv_playing_alltime);
        fmPlayingCover = (ImageView) findViewById(R.id.fm_img_album_picture);
        fmPlayBar = (SeekBar) findViewById(R.id.fm_sb_playing_slider);

    }

    private void initData(){
        Intent intent = getIntent();
        radio = intent.getParcelableExtra(Constant.RADIO_DATA);
        if(radio != null){
            RadioDb radioDb = RadioDb.Companion.getInstance();
            radioDb.saveRadioHistory(radio);
            radioId = radio.getDataId() + "";
            radioName = radio.getRadioName();
            radioURL = radio.getCoverUrlLarge();
        }else{
            radioId = "-1";
        }

        Flog.e(TAG, "PlayingActivity//"+radioId+"//"+radioName+"//"+radioURL);

        preferences = getSharedPreferences("liveTime", Context.MODE_PRIVATE);
        editor = preferences.edit();

        todayFragment = new PlayListFragment();
        lastdayFragment = new PlayListFragment();
        tomorrowFragment = new PlayListFragment();

        fmListBack.setOnClickListener(this);
        fmPlayBack.setOnClickListener(this);
        fmPlayTable.setOnClickListener(this);
        fmPlayControl.setOnClickListener(this);
        fmPlayPrevious.setOnClickListener(this);
        fmPlayNext.setOnClickListener(this);
        fmPlayCollect.setOnClickListener(this);


        mPlayerManager = XmPlayerManager.getInstance(this);
        FmDataPresenter.getInstance().setActivity(this);
        ControlUtil.getInstance().setFmPlayActivity(this, mPlayerManager);
        ControlUtil.getInstance().resetSession();

        initProgressListener();
        initChosenListener();
        initPlayerUI();
        playRadioData();
    }

    public void initPlayerUI(){
        PlayableModel model = mPlayerManager.getCurrSound();
        if(!FmVoiceService.isStop && model != null && (radio == null || (radio.getDataId() == ((Schedule)model).getRadioId()))){
            Flog.e(TAG, "PlayingActivity//initPlayerUI//1");
            Schedule schedule = (Schedule) mPlayerManager.getCurrSound();
            Program program = schedule.getRelatedProgram();
            mPlayIndex = mPlayerManager.getCurrentIndex();
            fmPlayResource.setText(schedule.getRadioName());
            fmPlayTitle.setText(program.getProgramName());
            fmPlayType.setText(ToolUtil.getProgramStatus(schedule));
            initRadio(schedule);
            setCover(program.getBackPicUrl());
            if(mPlayerManager.isPlaying()){
                setBtnStart();
            }else{
                setBtnStop();
            }
            if(mPlayerManager.getDuration() > 0){
                fmPlayCur.setText(ToolUtil.formatTime(mPlayerManager.getPlayCurrPositon()));
                fmPlayDuration.setText(ToolUtil.formatTime(mPlayerManager.getDuration()));
            }
        }else{
            Flog.e(TAG, "PlayingActivity//initPlayerUI//2//"+(model == null ? "null" : ((Schedule) model).getRadioId()));
            FmVoiceService.CURRENT_PLAYDAY = Constant.TODAY;
        }
    }

    public void initRadio(Schedule schedule){
        if(this != null && !this.isDestroyed()){
            mCurentFragment = null;
            radioName = schedule.getRadioName();
            radioURL = schedule.getRelatedProgram().getBackPicUrl();
            radioId = String.valueOf(schedule.getRadioId());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fm_list_back){
            playlistFl.setVisibility(View.GONE);
            mainPlayRe.setVisibility(View.GONE);
            pagePlayll.setVisibility(View.VISIBLE);
        }else if(v.getId() == R.id.fm_img_back){
            finish();
        }else if(v.getId() == R.id.fm_img_list){
            showPlayList();
        }else if(v.getId() == R.id.fm_img_play_state){
            if (mPlayerManager.isPlaying() && mPlayerManager.getPlayerStatus() != PlayerConstants.STATE_IDLE) {
                mPlayerManager.pause();
            } else if(mPlayerManager.getPlayerStatus() != PlayerConstants.STATE_IDLE){
                mPlayerManager.play();
            }
        }else if(v.getId() == R.id.fm_img_previous){
            playPrevious();
        }else if(v.getId() == R.id.fm_img_next){
            playNext();
        }else if(v.getId() == R.id.fm_img_collection){

        }
    }

    public void showData(int tag, Object param){
        ControlUtil.getInstance().playRaido(param);
    }

    private void showPlayList(){
        todayFragment = new PlayListFragment();
        dayGroup.clearCheck();
        lastDay.setChecked(false);
        tomorrow.setChecked(false);
        today.setChecked(true);
        mainPlayRe.setVisibility(View.VISIBLE);
        pagePlayll.setVisibility(View.GONE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playlistFl.setVisibility(View.VISIBLE);
            }
        }, 300);
    }

    private void playRadioData(){
        PlayableModel model = mPlayerManager.getCurrSound();
        if(radio != null && (FmVoiceService.isStop || model == null || (model != null && radio.getDataId() != ((Schedule) model).getRadioId()))){
            Flog.e(TAG, "PlayingActivity//playRadioData");
            FmVoiceService.isStop = false;
            ControlUtil.getInstance().getSchedule(radio, PlayingActivity.this, -1);
        }
    }

    public void setCover(String programeUrl) {
        try {
            if(!isDestroyed()){
                Glide.with(this).load(programeUrl)
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.mp3_play_no_album_picture)
                                .error(R.mipmap.mp3_play_no_album_picture))
                        .into(fmPlayingCover);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startLiveProgress(){
        PlayableModel model = mPlayerManager.getCurrSound();
        if(model != null && "直播".equals(ToolUtil.getProgramStatus((Schedule) model))){
            //processLiveTime(((Schedule) model).getRelatedProgram().getProgramId(), ToolUtil.getCurrent(0));
            if(isFirstLive){
                mHandler.removeMessages(PROGRESS_SHOW);
                editor.putLong("id", ((Schedule) model).getRelatedProgram().getProgramId());
                editor.putLong("lastTime", ToolUtil.getCurrent(0));
                fmPlayBar.setMax((int) ToolUtil.getDuration((Schedule) model));
                fmPlayBar.setEnabled(false);
                fmPlayCur.setText(ToolUtil.updateTime(0));
                fmPlayDuration.setText(ToolUtil.updateTime((int) (((ToolUtil.getDuration((Schedule) model)))/1000)));
                mHandler.sendMessageDelayed(mHandler.obtainMessage(PROGRESS_SHOW, mCurrentPosition), 1000);
                isFirstLive = false;
            }else{
                mHandler.sendMessage(mHandler.obtainMessage(PROGRESS_SHOW, mCurrentPosition));
            }

        }

    }

    public void stopLiveProgress(){
        mCurrentPosition = 0;
        fmPlayBar.setEnabled(true);
        mHandler.removeMessages(PROGRESS_SHOW);
    }

    private void processLiveTime(long currentId, long currentTime){
        long id = preferences.getLong("id", -1);
        if(id == currentId){
            long lastTime = preferences.getLong("lastTime", -1);
            mCurrentPosition = (int) ToolUtil.getCurrent(lastTime);
        }else{
            editor.clear().commit();
        }
    }

    public void setPlayIndex(int index){
        this.mPlayIndex = index;
    }

    private void initProgressListener() {

        fmPlayBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public int position;
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Flog.e("hehe","/////111111");
                mPlayerManager.seekTo(position);
                mUpdateProgress = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUpdateProgress = false;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                position = progress;
            }



        });
    }

    private void initChosenListener(){
        dayGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.fm_lastday_rb:
                        Flog.e(TAG, "setOnCheckedChangeListener//fm_lastday_rb");
                        if(lastDay.isChecked()){
                            setCurentFragment(lastdayFragment, Constant.LASTDAY);
                        }
                        break;
                    case R.id.fm_today_rb:
                        Flog.e(TAG, "setOnCheckedChangeListener//fm_today_rb");
                        if(today.isChecked()){
                            setCurentFragment(todayFragment, Constant.TODAY);
                        }
                        break;
                    case R.id.fm_tomorrow_rb:
                        Flog.e(TAG, "setOnCheckedChangeListener//fm_tomorrow_rb");
                        if(tomorrow.isChecked()){
                            setCurentFragment(tomorrowFragment, Constant.NEXTDAY);
                        }
                        break;
                    default:
                        break;

                }

            }
        });
    }

    //设置fragment
    private void setCurentFragment(Fragment fragment, int week){
        if(fragment != null && mCurentFragment != fragment){
            fragmentTransaction = getFragmentManager().beginTransaction();
            mCurentFragment = fragment;
            Bundle bundle = new Bundle();
            Radio radio = new Radio();
            radio.setRadioName(radioName);
            radio.setCoverUrlLarge(radioURL);
            radio.setDataId(Long.parseLong(radioId));
            Flog.e(TAG, "setCurentFragment//"+radioName+"//"+radioId);
            bundle.putParcelable("radio", radio);
            if(week == Constant.LASTDAY){
                bundle.putString("week", ToolUtil.getLastDay());
                bundle.putInt("day", Constant.LASTDAY);
            }else if(week == Constant.TODAY){
                bundle.putString("week", "-1");
                bundle.putInt("day", Constant.TODAY);
            }else if(week == Constant.NEXTDAY){
                bundle.putString("week", ToolUtil.getNextDay());
                bundle.putInt("day", Constant.NEXTDAY);
            }
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fm_list_container, fragment).commitAllowingStateLoss();
        }
    }

    public void setPlayList(List<Schedule> scheduleList){
        this.scheduleList = scheduleList;
    }

    public void playPrevious(){
        mCurrentTime = System.currentTimeMillis();
        if((mCurrentTime - mLastTime) >= 1200 && mPlayerManager.getPlayerStatus() != PlayerConstants.STATE_IDLE){
            mLastTime = mCurrentTime;
            if(mPlayIndex == 0){
                fmPlayPrevious.setEnabled(false);
            }else if(mPlayIndex > 0){
                mPlayIndex = mPlayIndex -1;
                mPlayerManager.playPre();
                fmPlayNext.setEnabled(true);
            }
        }
    }

    public void playNext(){
        mCurrentTime = System.currentTimeMillis();
        if((mCurrentTime - mLastTime) >= 1200 && mPlayerManager.getPlayerStatus() != PlayerConstants.STATE_IDLE){
            mLastTime = mCurrentTime;
            if("直播".equals(ToolUtil.getProgramStatus((Schedule) mPlayerManager.getCurrSound()))){
                fmPlayNext.setEnabled(false);
            }else if(mPlayIndex >= 0){
                mPlayIndex = mPlayIndex +1;
                mPlayerManager.playNext();
                fmPlayPrevious.setEnabled(true);
            }
        }
    }

    public void updateButtonStatus() {
        if(!isDestroyed()){
            if (mPlayerManager.hasPreSound()) {
                fmPlayPrevious.setEnabled(true);
            } else {
                fmPlayPrevious.setEnabled(false);
            }
            if (mPlayerManager.hasNextSound() && !"直播".equals(ToolUtil.getProgramStatus((Schedule) mPlayerManager.getCurrSound()))) {
                fmPlayNext.setEnabled(true);
            } else {
                fmPlayNext.setEnabled(false);
            }
        }

    }

    public void setFmPlayTitle(String title, String source){
        if(!isDestroyed()){
            fmPlayTitle.setText(title);
            fmPlayResource.setText(source);
            mCurrentTitle = title;
        }
    }

    public void setFmPlayType(String type){
        if(!isDestroyed()){
            fmPlayType.setText(type);
            if(!"直播".equals(type)){
                isFirstLive = true;
                //stopLiveProgress();
            }else{
                fmPlayCur.setText("00:00");
                fmPlayDuration.setText("00:00");
            }
        }
    }

    public void setBtnStart(){
        if(!isDestroyed()){
            fmPlayControl.setBackgroundResource(R.drawable.seletor_pause);
        }
    }

    public void setBtnStop(){
        if(!isDestroyed()){
            fmPlayControl.setBackgroundResource(R.drawable.seletor_play);
        }
    }

    public void setPlayProgress(int currPos, int duration){
        if(!isDestroyed() /*&& duration != 0*/){
            fmPlayCur.setText(ToolUtil.formatTime(currPos));
            fmPlayDuration.setText(ToolUtil.formatTime(duration));
            fmPlayBar.setMax(duration);
            if (mUpdateProgress && duration != 0) {
                Flog.e("hao","///"+currPos);
                fmPlayBar.setProgress(currPos);
            }
        }
    }

    public void resetPlayProgress(Radio radio){
        fmPlayCur.setText(ToolUtil.formatTime(0));
        fmPlayDuration.setText(ToolUtil.formatTime(0));
        fmPlayResource.setText(radio.getRadioName());
        fmPlayTitle.setText(radio.getProgramName());
        fmPlayBar.setProgress(0);
    }

    public void setBarEnble(){
        if(!isDestroyed()){
            fmPlayBar.setEnabled(true);
        }
    }

    public void completePlayAds(){
        if(!isDestroyed()){
            fmPlayControl.setEnabled(true);
            fmPlayBar.setEnabled(true);
        }
    }

    public void startGetAdsInfo(){
        if(!isDestroyed()){
            fmPlayControl.setEnabled(false);
            fmPlayBar.setEnabled(false);
        }
    }


    public void showDisNetwork(){

    }

    public void showHasNetwork(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Flog.e("liuxiaosheng", "PlayingActivity//onDestroy");
    }
}










/*Map<String ,String> maps = new HashMap<String, String>();

        maps.put(DTransferConstants.RADIO_CATEGORY_ID ,param);
        CommonRequest.getRadiosByCategory(maps, new IDataCallBack<RadioListByCategory>() {

            @Override
            public void onSuccess(final RadioListByCategory object) {
                Flog.e(TAG,"fmRadio///"+(object.getRadios() == null ? "null" : object.getRadios().get(0).getRadioName()));
                if(object.getRadios() != null){
                    radioId = object.getRadios().get(0).getDataId() + "";
                    radioURL = object.getRadios().get(0).getCoverUrlLarge();
                    radioName = object.getRadios().get(0).getRadioName();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ControlUtil.getInstance().getSchedule(object.getRadios().get(0), PlayingActivity.this, -1);
                        }
                    }, 2000);
                }
            }

            @Override
            public void onError(int code, String message) {
                Flog.e(TAG,"fmRadio//onError//"+message);
            }

        });*/