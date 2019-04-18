package com.flyaudio.flyradioonline.task.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyaudio.flyradioonline.Constant;
import com.flyaudio.flyradioonline.R;
import com.flyaudio.flyradioonline.database.FmCacheDAO;
import com.flyaudio.flyradioonline.presenter.FmDataPresenter;
import com.flyaudio.flyradioonline.task.play.activity.PlayingActivity;
import com.flyaudio.flyradioonline.util.Flog;
import com.flyaudio.flyradioonline.util.SpacesItemDecoration;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzehao on 18-4-28.
 */

public class FmSearchActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "haozi";
    private Button searchBack;
    private Button searchReset;
    private TextView searchDelete;
    private EditText searchInput;
    private LinearLayout searchOccsiate;
    private LinearLayout searchHistory;
    private LinearLayout searchSuccess;
    private RelativeLayout searchHisRe;
    private RelativeLayout searchRe;
    private RelativeLayout searchFailRe;
    private RelativeLayout searchDisNetRe;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView searchHisRv;
    private HistoryAdapter searchHisAdapter;
    private RecyclerView searchSucRv;
    private SearchSucAdapter searchSucAdapter;

    private FmDataPresenter fmDataPresenter;
    private List<QueryResult> wordsResultList;
    private List<Radio> resultList;
    private List<String> historyList;

    private boolean isHisItemClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_search_input);
        initView();
        initData();

    }


    private void initView() {
        searchBack = (Button) findViewById(R.id.fm_search_etback);
        searchReset = (Button) findViewById(R.id.fm_search_reset_btn);
        searchDelete = (TextView) findViewById(R.id.fm_search_hisdelete);
        searchInput = (EditText) findViewById(R.id.fm_search_input_et);
        searchOccsiate = (LinearLayout) findViewById(R.id.fm_search_associ_container);
        searchHistory = (LinearLayout) findViewById(R.id.fm_search_hiscontainer);
        searchSuccess = (LinearLayout) findViewById(R.id.fm_searchsuc_container);
        searchHisRe = (RelativeLayout) findViewById(R.id.fm_search_history_re);
        searchRe = (RelativeLayout) findViewById(R.id.fm_search_re);
        searchFailRe = (RelativeLayout) findViewById(R.id.fm_searchfail_re);
        searchDisNetRe = (RelativeLayout) findViewById(R.id.fm_search_disnetwork_re);
        searchHisRv = (RecyclerView) findViewById(R.id.fm_search_history_rv);
        searchSucRv = (RecyclerView) findViewById(R.id.fm_search_success_rv);
    }

    private void initData() {
        FmCacheDAO.initFmCacheDAO(this);
        XmPlayerManager.getInstance(this).init();

        searchBack.setOnClickListener(this);
        searchReset.setOnClickListener(this);
        searchDelete.setOnClickListener(this);
        searchInput.setOnClickListener(this);

        recyclerView = new RecyclerView(this);
        searchOccsiate.addView(recyclerView);
        recyclerView.setItemViewCacheSize(0);
        recyclerView.addItemDecoration(new SpacesItemDecoration(0,0, 1));
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        searchHisRv.setItemViewCacheSize(0);
        searchHisRv.addItemDecoration(new SpacesItemDecoration(0,0, 1));
        searchHisRv.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        LinearLayoutManager historyLayoutManager = new LinearLayoutManager(this);
        searchHisRv.setLayoutManager(historyLayoutManager);
        historyList = FmCacheDAO.getInstance().getSearchRecord();
        Flog.e("ttt", "///rrr///"+historyList.toString());
        searchHisAdapter = new HistoryAdapter(this, historyList);
        searchHisRv.setAdapter(searchHisAdapter);

        searchSucRv.setItemViewCacheSize(0);
        searchSucRv.addItemDecoration(new SpacesItemDecoration(0,0, 1));
        searchSucRv.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        LinearLayoutManager SuccessLayoutManager = new LinearLayoutManager(this);
        searchSucRv.setLayoutManager(SuccessLayoutManager);

        fmDataPresenter = FmDataPresenter.getInstance();
        fmDataPresenter.setSearchActivity(this);

        addInputListener();
        addTouchListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fm_search_etback:
                finish();
                break;
            case R.id.fm_search_reset_btn:
                searchInput.setText("");
                break;
            case R.id.fm_search_hisdelete:
                FmCacheDAO.getInstance().deleteSearchRecord();
                historyList.clear();
                searchHisAdapter.notifychange(historyList);
                break;
            case R.id.fm_search_input_et:
                Flog.e(TAG, "点击输入框");
                searchInput.setFocusable(true);
                searchInput.setFocusableInTouchMode(true);
                searchInput.requestFocus();
                searchInput.requestFocusFromTouch();
                showLayout(Constant.SHOW_KEYWORD_LAYOUT);
                InputMethodManager inputManager =
                        (InputMethodManager)searchInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(searchInput, 0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Flog.e(TAG, "FmSearchActivity//onResume");
        if(TextUtils.isEmpty(searchInput.getText().toString())){
            showLayout(Constant.SHOW_HISTORY_LAYOUT);
            wordsResultList = null;
        }
    }


    private void addTouchListener() {
        searchRe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) FmSearchActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                searchInput.setFocusable(false);
                searchInput.setFocusableInTouchMode(false);
                return true;
            }
        });

    }

    private void addInputListener(){

        searchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    Flog.e(TAG, "onFocusChange//失去焦点");
                    if(TextUtils.isEmpty(searchInput.getText().toString())){
                        Flog.e(TAG, "onFocusChange//输入内容为空");
                        showLayout(Constant.SHOW_HISTORY_LAYOUT);
                    }
                }else{
                    Flog.e(TAG, "onFocusChange//获得焦点");
                }
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
                Flog.e(TAG, "addInputListener//onTextChanged//"+s.toString());
                if(!TextUtils.isEmpty(s.toString())){
                    searchReset.setVisibility(View.VISIBLE);
                    if(!isHisItemClick){
                        fmDataPresenter.getFmData(Constant.SEARCH_WORDS_TOKEN, s.toString());
                    }
                }else{
                    searchReset.setVisibility(View.GONE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 输入前的监听
                Flog.e(TAG, "addInputListener//beforeTextChanged");

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听
                Flog.e(TAG, "addInputListener//afterTextChanged//");
                if(TextUtils.isEmpty(s.toString())){
                    historyList = FmCacheDAO.getInstance().getSearchRecord();
                    searchHisAdapter.notifychange(historyList);
                    showLayout(Constant.SHOW_HISTORY_LAYOUT);
                }else{
                    showLayout(Constant.SHOW_KEYWORD_LAYOUT);
                }

            }
        });
    }

    public void showData(int tag, Object object) {
        Flog.e(TAG, "FmSearchActivity//loadFmData");
        isHisItemClick = false;
        if(object != null){
            if(Constant.SEARCH_WORDS_TOKEN == tag){
                List<Object> parmas = (List<Object>) object;
                SuggestWords suggestWords = (SuggestWords) parmas.get(0);
                if(suggestWords != null){
                    showLayout(Constant.SHOW_KEYWORD_LAYOUT);
                    wordsResultList = suggestWords.getKeyWordList();
                    recyclerAdapter = new RecyclerAdapter(this, wordsResultList);
                    recyclerView.setAdapter(recyclerAdapter);
                    Flog.e(TAG, "FmSearchActivity//loadFmData1//不为空//"+wordsResultList.size());

                }
            }else if(Constant.SEARCH_RADIO_TOKEN == tag){
                resultList = (List<Radio>) object;
                if(!resultList.isEmpty()){
                    showLayout(Constant.SHOW_RESULT_LAYOUT);
                    searchSucAdapter = new SearchSucAdapter(this, resultList);
                    searchSucRv.setAdapter(searchSucAdapter);
                    Flog.e(TAG, "FmSearchActivity//loadFmData2//不为空//"+resultList.size());
                }else{
                    showLayout(Constant.SHOW_FAIL_LAYOUT);
                }
            }
        }else{

        }
    }

    private void showLayout(int tag){
        switch (tag){
            case Constant.SHOW_FAIL_LAYOUT:
                searchOccsiate.setVisibility(View.GONE);
                searchHisRe.setVisibility(View.GONE);
                searchSuccess.setVisibility(View.GONE);
                searchDisNetRe.setVisibility(View.GONE);
                searchFailRe.setVisibility(View.VISIBLE);
                break;
            case Constant.SHOW_HISTORY_LAYOUT:
                searchOccsiate.setVisibility(View.GONE);
                searchHisRe.setVisibility(View.VISIBLE);
                searchSuccess.setVisibility(View.GONE);
                searchFailRe.setVisibility(View.GONE);
                searchDisNetRe.setVisibility(View.GONE);
                break;
            case Constant.SHOW_KEYWORD_LAYOUT:
                searchOccsiate.setVisibility(View.VISIBLE);
                searchHisRe.setVisibility(View.GONE);
                searchSuccess.setVisibility(View.GONE);
                searchFailRe.setVisibility(View.GONE);
                searchDisNetRe.setVisibility(View.GONE);
                break;
            case Constant.SHOW_RESULT_LAYOUT:
                searchOccsiate.setVisibility(View.GONE);
                searchHisRe.setVisibility(View.GONE);
                searchSuccess.setVisibility(View.VISIBLE);
                searchFailRe.setVisibility(View.GONE);
                searchDisNetRe.setVisibility(View.GONE);
                break;
            case Constant.SHOW_DISNET_LAYOUT:
                searchDisNetRe.setVisibility(View.VISIBLE);
                searchOccsiate.setVisibility(View.GONE);
                searchHisRe.setVisibility(View.GONE);
                searchSuccess.setVisibility(View.GONE);
                searchFailRe.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder>{
        private List<QueryResult> wordsResultList = null;
        private Context context;

        public RecyclerAdapter(Context context, List<QueryResult> wordsResultList) {
            this.wordsResultList = wordsResultList;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fm_searchkeywords_item,viewGroup,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.setPosition(position);
            String keywords = wordsResultList.get(position).getKeyword();
            Flog.e(TAG, "onBindViewHolder//"+keywords);
            if(!TextUtils.isEmpty(keywords)){
                viewHolder.keywordsTv.setText(keywords);
            }
        }

        @Override
        public int getItemCount() {
            return wordsResultList == null ? 0 : wordsResultList.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView keywordsTv;
        private RelativeLayout keywordsRe;
        private int position;
        public ViewHolder(View view) {
            super(view);

            keywordsRe = (RelativeLayout) view.findViewById(R.id.fm_search_keyword_re);
            keywordsTv = (TextView) view.findViewById(R.id.fm_search_keyword_tv);
            keywordsRe.setOnClickListener(this);
        }

        public void setPosition(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            String result = wordsResultList.get(getAdapterPosition()).getKeyword();
            boolean isHasRecord = FmCacheDAO.getInstance().isHasRecord(result);
            if(!isHasRecord){
                FmCacheDAO.getInstance().insertSearchRecord(result);
                historyList = FmCacheDAO.getInstance().getSearchRecord();
                searchHisAdapter.notifychange(historyList);
            }
            List<Object> params = new ArrayList<>();
            params.add(result);
            fmDataPresenter.getFmData(Constant.SEARCH_RADIO_TOKEN, params);
        }
    }

    public class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder>{
        private List<String> searchList = null;
        private Context context;

        public HistoryAdapter(Context context, List<String> searchList) {
            this.searchList = searchList;
            this.context = context;
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fm_searchhistory_item,viewGroup,false);
            HistoryHolder vh = new HistoryHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(HistoryHolder viewHolder, int position) {
            Flog.e(TAG, "HistoryAdapter//onBindViewHolder");
            viewHolder.setPosition(position);
            viewHolder.historyTv.setText(searchList.get(position));
        }

        @Override
        public int getItemCount() {
            return searchList == null ? 0 : searchList.size();
        }

        public void notifychange(List<String> searchList){
            this.searchList = searchList;
            notifyDataSetChanged();
        }
    }

    public class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private int position;
        private TextView historyTv;
        private Button hisItemDel;
        private RelativeLayout hisRe;
        public HistoryHolder(View view) {
            super(view);
            hisRe = (RelativeLayout) view.findViewById(R.id.fm_searchhistory_item_re);
            hisItemDel = (Button) view.findViewById(R.id.fm_searchhistory_item_delete);
            historyTv = (TextView) view.findViewById(R.id.fm_searchhistory_item_tv);
            hisRe.setOnClickListener(this);
            hisRe.setOnLongClickListener(this);
            hisItemDel.setOnClickListener(this);
        }

        public void setPosition(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.fm_searchhistory_item_re){
                isHisItemClick = true;
                searchInput.setText(historyTv.getText().toString());
                List<Object> params = new ArrayList<>();
                params.add(historyTv.getText().toString());
                fmDataPresenter.getFmData(Constant.SEARCH_RADIO_TOKEN, params);
            }else if(v.getId() ==  R.id.fm_searchhistory_item_delete){
                FmCacheDAO.getInstance().deleteItemSearch(historyList.get(getAdapterPosition()));
                searchHisAdapter.notifyItemChanged(getAdapterPosition());
                //historyList = FmCacheDAO.getInstance().getSearchRecord();
                //searchHisAdapter.notifychange(historyList);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(hisItemDel.getVisibility() == View.VISIBLE){
                hisItemDel.setVisibility(View.GONE);
                hisRe.setBackgroundResource(R.drawable.fm_search_history_item_selector);
            }else{
                hisRe.setBackgroundResource(R.mipmap.fm_search_history_item_delete);
                hisItemDel.setVisibility(View.VISIBLE);
            }
            return false;
        }
    }

    public class SearchSucAdapter extends RecyclerView.Adapter<SearchSucHolder>{
        private List<Radio> resultList = null;
        private Context context;

        public SearchSucAdapter(Context context, List<Radio> resultList) {
            this.resultList = resultList;
            this.context = context;
        }

        @Override
        public SearchSucHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fm_searchsuccess_item,viewGroup,false);
            SearchSucHolder vh = new SearchSucHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(SearchSucHolder viewHolder, int position) {
            viewHolder.setPosition(position);
            String keywords = resultList.get(position).getRadioName();
            viewHolder.resultTv.setText(keywords);
        }

        @Override
        public int getItemCount() {
            return resultList == null ? 0 : resultList.size();
        }
    }

    public class SearchSucHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private int position;
        private TextView resultTv;
        private RelativeLayout resultRe;
        public SearchSucHolder(View view) {
            super(view);
            resultRe = (RelativeLayout) view.findViewById(R.id.fm_search_success_re);
            resultTv = (TextView) view.findViewById(R.id.fm_search_success_tv);
            resultRe.setOnClickListener(this);
        }

        public void setPosition(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            startFmPlay(resultList.get(getAdapterPosition()));
        }

        private void startFmPlay(Radio radio){
            Intent intent = new Intent(FmSearchActivity.this, PlayingActivity.class);
            intent.putExtra(Constant.RADIO_DATA, radio);
            startActivity(intent);
        }
    }

}
