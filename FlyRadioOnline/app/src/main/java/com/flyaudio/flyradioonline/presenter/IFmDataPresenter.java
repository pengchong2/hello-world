package com.flyaudio.flyradioonline.presenter;

import com.flyaudio.flyradioonline.task.play.activity.PlayingActivity;
import com.flyaudio.flyradioonline.task.play.fragment.PlayListFragment;
import com.flyaudio.flyradioonline.task.play.service.FmVoiceService;
import com.flyaudio.flyradioonline.task.search.FmSearchActivity;

public interface IFmDataPresenter {

    void getFmData(int tag, Object params);
    void setFmFragment(PlayListFragment fmFragment);
    void setActivity(PlayingActivity activity);
    void setSearchActivity(FmSearchActivity searchActivity);
    void setService(FmVoiceService service);
    void setNetMonitor();
    void disNetMonitor();
    void showFmData(int tag, Object object);
}
