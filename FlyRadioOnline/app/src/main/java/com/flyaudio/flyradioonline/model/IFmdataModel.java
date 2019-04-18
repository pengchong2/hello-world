package com.flyaudio.flyradioonline.model;

import com.flyaudio.flyradioonline.presenter.IFmDataPresenter;
import com.flyaudio.flyradioonline.task.play.service.FmVoiceService;

public interface IFmdataModel {
    void setFmDataPresenter(IFmDataPresenter fmDataPresenter);
    void setFmService(FmVoiceService fmService);
    void requestFmData(int tag, Object params);
}
