package com.flyaudio.flyradioonline.task.play.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyaudio.flyradioonline.Constant;
import com.flyaudio.flyradioonline.R;
import com.flyaudio.flyradioonline.presenter.FmDataPresenter;
import com.flyaudio.flyradioonline.task.play.service.FmVoiceService;
import com.flyaudio.flyradioonline.ui.IActionCallback;
import com.flyaudio.flyradioonline.util.ControlUtil;
import com.flyaudio.flyradioonline.util.Flog;
import com.flyaudio.flyradioonline.util.SpacesItemDecoration;
import com.flyaudio.flyradioonline.util.ToolUtil;
import com.flyaudio.flyradioonline.view.VoicePlayingIcon;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.LiveAnnouncer;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.ArrayList;
import java.util.List;

public class PlayListFragment extends Fragment implements IActionCallback {
    private static final String TAG = "PlayListFragment";
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private FmDataPresenter fmDataPresenter;
    private List<Schedule> scheduleList;
    private XmPlayerManager mPlayerManager;
    private RelativeLayout nodataList;
    private Radio radio;
    private String week;
    private int day;
    private boolean isDestroy = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        initData(view);
        return view;
    }

    private void initData(View view) {
        isDestroy = false;
        fmDataPresenter = FmDataPresenter.getInstance();
        mPlayerManager = XmPlayerManager.getInstance(getActivity());
        nodataList = (RelativeLayout) view.findViewById(R.id.fm_playlist_nodata_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.fm_playlist_rv);
        recyclerView.setItemViewCacheSize(0);
        recyclerView.addItemDecoration(new SpacesItemDecoration(0, 0, 1));
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        fmDataPresenter.setFmFragment(this);

        ControlUtil.getInstance().setFmPlayFragment(this);
        ControlUtil.getInstance().setActionCallback(this);

        Bundle bundle = getArguments();
        radio = bundle.getParcelable("radio");
        week = bundle.getString("week");
        day = bundle.getInt("day");
        Flog.e(TAG, "PlayListFragment//"+radio.getRadioName()+"//"+week);
        if("6".equals(ToolUtil.getToDay()) && "0".equals(week)){
            week = "-2";
        }
        List<Object> params = new ArrayList<>();
        params.add(Integer.parseInt(week));
        params.add(radio);

        fmDataPresenter.getFmData(Constant.PAGE_SCHEDULE_TOKEN, params);

    }

    public void showData(int tag, Object param){
        List<Object> paramList = (List<Object>) param;
        if(paramList != null && !paramList.isEmpty()){
            nodataList.setVisibility(View.GONE);
            scheduleList = (List<Schedule>) paramList.get(1);
            recyclerAdapter = new RecyclerAdapter(getActivity(), scheduleList);
            recyclerView.setAdapter(recyclerAdapter);
        }else{
            nodataList.setVisibility(View.VISIBLE);
        }
    }

    public void notifyData(){
        if(FmVoiceService.CURRENT_PLAYDAY == day && recyclerAdapter != null){
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void stopIcon() {
        notifyData();
    }

    @Override
    public void startIcon() {
        notifyData();
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder>{
        private List<Schedule> scheduleList = null;
        private Context context;

        public RecyclerAdapter(Context context, List<Schedule> scheduleList) {
            this.scheduleList = scheduleList;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_playlist_item,viewGroup,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Flog.e(TAG,"onBindViewHolder//"+position);
            StringBuilder builder = new StringBuilder();
            Schedule schedule = scheduleList.get(position);
            PlayableModel model = mPlayerManager.getCurrSound();
            Program program = schedule.getRelatedProgram();
            String startTime = schedule.getStartTime().substring(9);
            String endTime = schedule.getEndTime().substring(9);
            viewHolder.setPosition(position);
            viewHolder.itemTitle.setText(program.getProgramName());
            viewHolder.itemTime.setText(startTime + " - " + endTime);
            List<LiveAnnouncer> announcers = program.getAnnouncerList();
            if(announcers != null && !announcers.isEmpty()){
                for(LiveAnnouncer announcer : announcers){
                    builder.append(announcer.getNickName() + " ");
                }
                viewHolder.itemNickName.setText(builder.toString());
            }else{
                viewHolder.itemNickName.setText("无数据");
            }
            viewHolder.itemType.setText(ToolUtil.getProgramStatus(schedule));
            if("未开始".equals(ToolUtil.getProgramStatus(schedule))){
                viewHolder.itemRe.setEnabled(false);
            }else{
                viewHolder.itemRe.setEnabled(true);
            }
            Flog.e("");
            if(model != null && ((Schedule)model).getDataId() == schedule.getDataId()
                    && FmVoiceService.CURRENT_PLAYDAY == day){
                viewHolder.itemPlayIv.setVisibility(View.VISIBLE);
                if(mPlayerManager.isPlaying()){
                    viewHolder.itemPlayIv.start();
                }else{
                    viewHolder.itemPlayIv.stop();
                }
            }else{
                viewHolder.itemPlayIv.stop();
                viewHolder.itemPlayIv.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return scheduleList == null ? 0 : scheduleList.size();
        }

        public void notifychange(List<Schedule> scheduleList){
            this.scheduleList = scheduleList;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView itemTitle;
        private TextView itemNickName;
        private TextView itemTime;
        private TextView itemType;
        private VoicePlayingIcon itemPlayIv;
        private LinearLayout itemRe;
        private int position;
        public ViewHolder(View view) {
            super(view);
            itemTitle = (TextView) view.findViewById(R.id.fm_playlist_item_title);
            itemNickName = (TextView) view.findViewById(R.id.fm_playlist_item_nickname);
            itemTime = (TextView) view.findViewById(R.id.fm_playlist_item_time);
            itemType = (TextView) view.findViewById(R.id.fm_playlist_item_type);
            itemPlayIv = (VoicePlayingIcon) view.findViewById(R.id.fm_playlist_icon);
            itemRe = (LinearLayout) view.findViewById(R.id.fm_playlist_item_re);
            itemRe.setOnClickListener(this);
        }

        public void setPosition(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            startFmPlay(getAdapterPosition());
        }

        private void startFmPlay(int position){
            FmVoiceService.CURRENT_PLAYDAY = day;
            mPlayerManager.playSchedule(scheduleList, position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

    public boolean isDestroy(){
        return isDestroy;
    }
}
