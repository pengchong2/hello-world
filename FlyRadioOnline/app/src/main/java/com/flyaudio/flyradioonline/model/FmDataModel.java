package com.flyaudio.flyradioonline.model;


import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.flyaudio.flyradioonline.Constant;
import com.flyaudio.flyradioonline.presenter.IFmDataPresenter;
import com.flyaudio.flyradioonline.task.play.service.FmVoiceService;
import com.flyaudio.flyradioonline.util.Flog;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.live.schedule.ScheduleList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuzehao on 18-4-25.
 */

public class FmDataModel implements IFmdataModel {
    private static final String TAG = "FmDataModel";
    private static FmDataModel fmDataModel;
    private IFmDataPresenter fmDataPresenter;
    private FmVoiceService fmVoiceService;

    public static synchronized IFmdataModel getInstance() {
        if (fmDataModel == null) {
            fmDataModel = new FmDataModel();
        }
        return fmDataModel;
    }

    @Override
    public void setFmDataPresenter(IFmDataPresenter fmDataPresenter){
        this.fmDataPresenter = fmDataPresenter;
    }

    @Override
    public void setFmService(FmVoiceService fmService) {
        this.fmVoiceService = fmService;
    }

    @Override
    public void requestFmData(int tag, Object params) {
        switch (tag){
            case Constant.MAIN_SCHEDULE_TOKEN:
                requestScheduleData(tag, params);
                break;
            case Constant.PAGE_SCHEDULE_TOKEN:
                requestScheduleData(tag, params);
                break;
            case Constant.RAIDO_DATA_TOKEN:
                requestRadioData(tag, params);
                break;
            case Constant.RADIO_SEARCH_TOKEN:
                requestSearchData(tag, params);
                break;
            case Constant.SEARCH_RADIO_TOKEN:
                requestSearchData(tag, params);
                break;
            case Constant.SEARCH_WORDS_TOKEN:
                requestSearchWords(tag, params);
                break;
            default:
                break;
        }
    }

    private void requestSearchWords(final int tag, Object params){
        Flog.e(TAG, "requestSearchData");
        final String searchWords = (String) params;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, searchWords);
        CommonRequest.getSuggestWord(map, new IDataCallBack<SuggestWords>(){

            @Override
            public void onSuccess(SuggestWords suggestWords) {
                Flog.e(TAG, "requestSearchData//"+suggestWords.getKeyWordList().size());
                List<Object> params = new ArrayList<>();
                params.add(suggestWords);
                fmDataPresenter.showFmData(tag, params);
            }

            @Override
            public void onError(int i, String s) {
                Flog.e(TAG, "requestSearchData//onError");
                fmDataPresenter.showFmData(tag, null);
            }
        });
    }

    private synchronized void requestSearchData(final int tag, Object object){
        Map<String ,String> map = new HashMap<String, String>();
        final List<Object> params = (List<Object>) object;
        map.put(DTransferConstants.SEARCH_KEY, (String) params.get(0));
        CommonRequest.getSearchedRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(@Nullable RadioList radioList) {
                if(Constant.RADIO_SEARCH_TOKEN == tag){
                    Flog.e(TAG, "requestSearchData//1//"+((String) params.get(0)));
                    if(radioList.getRadios().isEmpty()) {
                        Flog.e(TAG, "requestSearchData//2");
                        switch ((String) params.get(1)) {
                            case Constant.ARTIST:
                                fmVoiceService.searchVoice((String) params.get(2), Constant.KEYWORDS);
                                break;
                            case Constant.KEYWORDS:
                                fmVoiceService.searchVoice((String) params.get(2), Constant.TEXT);
                                break;
                            case Constant.TEXT:
                                fmDataPresenter.showFmData(tag, radioList.getRadios());
                                break;
                        }
                    }else{
                        String searchJson = JSON.toJSONString(radioList.getRadios());
                        fmVoiceService.sendVoiceData(searchJson);
                        Flog.e(TAG, "requestSearchData//3//"+searchJson);
                    }
                }else{
                    Flog.e(TAG, "requestSearchData//4//"+((String) params.get(0)));
                    fmDataPresenter.showFmData(tag, radioList.getRadios());
                }
            }

            @Override
            public void onError(int i, String s) {
                Flog.e(TAG, "requestSearchData//onError//"+s);
            }
        });
    }

    private synchronized void requestRadioData(final int tag, Object object){
        Map<String ,String> map = new HashMap<String, String>();
        final String radioIds = (String) object;
        map.put(DTransferConstants.RADIO_IDS, radioIds);
        CommonRequest.getRadiosByIds(map, new IDataCallBack<RadioListById>() {
            @Override
            public void onSuccess(@Nullable RadioListById radioListById) {
                List<Radio> radios = radioListById.getRadios();
                Flog.e(TAG, "requestRadioData//onSuccess//"+(radios == null || radios.isEmpty()));
                fmDataPresenter.showFmData(tag, radios);
            }

            @Override
            public void onError(int i, String s) {
                Flog.e(TAG, "requestRadioData//onError//"+s);
                fmDataPresenter.showFmData(tag, null);
            }
        });

    }


    private synchronized void requestScheduleData(final int tag, Object object) {
        Map<String ,String> map = new HashMap<String, String>();
        List<Object> params = (List<Object>) object;
        int weekday = (int) params.get(0);
        final Radio radio = (Radio) (params.get(1));
        map.put(DTransferConstants.RADIOID ,radio.getDataId() + "");
        if(weekday >= 0) {
            map.put(DTransferConstants.WEEKDAY ,weekday + "");
        }
        Calendar c = Calendar.getInstance();
        final boolean isToday = (weekday == (c.get(Calendar.DAY_OF_WEEK) - 1));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy:MM:dd");
        Calendar calendar = Calendar.getInstance();
        if(weekday >= 0) {
            calendar.set(Calendar.DAY_OF_WEEK ,weekday + 1);
        }
        final String currTime = simpleDateFormat.format(calendar.getTime());
        CommonRequest.getSchedules(map, new IDataCallBack<ScheduleList>() {
            @Override
            public void onSuccess(ScheduleList object) {
                if(object != null && object.getmScheduleList() != null && !object.getmScheduleList().isEmpty()) {
                    List<Schedule> schedules = object.getmScheduleList();
                    for (Schedule schedule : schedules) {
                        schedule.setStartTime(currTime + ":" + schedule.getStartTime());
                        schedule.setEndTime(currTime + ":" + schedule.getEndTime());
                        Program program = schedule.getRelatedProgram();
                        if (program == null) {
                            program = new Program();
                            schedule.setRelatedProgram(program);
                        }
                        program.setBackPicUrl(radio.getCoverUrlLarge());
                        schedule.setRadioId(radio.getDataId());
                        schedule.setRadioName(radio.getRadioName());
                        schedule.setRadioPlayCount(radio.getRadioPlayCount());
                        if(isToday) {
                            if (BaseUtil.isInTime(schedule.getStartTime() + "-" + schedule.getEndTime()) == 0) {
                                program.setRate24AacUrl(radio.getRate24AacUrl());
                                program.setRate24TsUrl(radio.getRate24TsUrl());
                                program.setRate64AacUrl(radio.getRate64AacUrl());
                                program.setRate64TsUrl(radio.getRate64TsUrl());
                                break;
                            }
                        }
                        Flog.e(TAG, "playRadio//"+schedule.getRelatedProgram().getProgramName()+"//"+schedule.getPlayType());
                    }

                    List<Object> paramList = new ArrayList<>();
                    paramList.add(tag);
                    paramList.add(schedules);
                    fmDataPresenter.showFmData(tag, paramList);
                }else{
                    List<Object> paramList = new ArrayList<>();
                    fmDataPresenter.showFmData(tag, paramList);
                    Flog.e(TAG, "playRadio//无数据");
                }
            }
            @Override
            public void onError(int code, String message) {
                Flog.e(TAG, "playRadio//onError//"+message);
                List<Object> paramList = new ArrayList<>();
                fmDataPresenter.showFmData(tag, paramList);
            }
        });
    }

}
