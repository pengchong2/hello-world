package com.flyaudio.flyradioonline;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzehao on 18-4-25.
 */

public class Constant {
    public static final String POPULAR_TOKEN = "popularRadio";
    public static final String SORT_TOKEN = "sortRadio";
    public static final int MAIN_SCHEDULE_TOKEN = 0x0001;
    public static final int PAGE_SCHEDULE_TOKEN = 0x0002;
    public static final int RAIDO_DATA_TOKEN = 0x0006;
    public static final int RADIO_SEARCH_TOKEN = 0x0007;
    public static final int SEARCH_RADIO_TOKEN = 0x0008;
    public static final int SEARCH_WORDS_TOKEN = 0X0009;

    public static final int LASTDAY = 0x0003;
    public static final int TODAY = 0x0004;
    public static final int NEXTDAY = 0x0005;

    public static final int LIST_NUM = 0x0005;
    public static final int LIST_HIS_NUM = 0x000A;
    public static final int LIST_HIS_FOOT_POS = 0x0009;



    public static final int COLLECTION_TOKEN = 0x0003;
    public static final int SEARCH_TOKEN = 0x0004;
    public static final int TRCK_TOKEN = 0x0005;
    public static final int ADD_COLLECT_TOKEN = 0x0006;
    public static final int DEL_COLLECT_TOKEN = 0x0007;
    public static final int SEARCH_WORD_TOKEN = 0x0008;
    public static final int SEARCH_RECOMMEND_TOKEN = 0x000a;
    public static final int RECENT_PLAY_TOKEN = 0x000b;
    public static final int SEND_HISTORY_TOKEN = 0x0015;
    public static final int DELETE_HISTORY_TOKEN = 0x0016;
    public static final int OUT_LOGIN_TOKEN = 0x001b;
    public static final int BASE_USERINFO_TOKEN = 0x001c;
    public static final int USERINFO_TOKEN = 0x001d;
    public static final int LOGIN_COLLECTION_TOKEN = 0x001e;
    public static final String AREA_COUNTRY_RADIO_TOKEN ="countryRadio";
    public static final String AREA_PROVINCE_RADIO_TOKEN ="provinceRadio";
    public static final String PROVINCE_TOKEN="province";
    public static final String HISTORY_TOKEN="history";



    public static final int SHOW_KEYWORD_LAYOUT = 0x000c;
    public static final int SHOW_HISTORY_LAYOUT = 0x000d;
    public static final int SHOW_RESULT_LAYOUT = 0x000e;
    public static final int SHOW_FAIL_LAYOUT = 0x000f;
    public static final int SHOW_DISNET_LAYOUT = 0x0010;


    public static String FM_UID = "default";

    public static final String ARTIST = "search_artist";
    public static final String KEYWORDS = "search_keywords";
    public static final String TEXT = "search_text";

    public static final String FLYFM_DB_NAME = "flyaudiofm.db";
    public static final String TABLE_SEARCH_NAME = "fm_search";
    public static final String SEARCH_ID = "_id";
    public static final String SEARCH_UID = "search_uid";
    public static final String SEARCH_NAME = "search_content";

    public static final String TABLE_PLAY_NAME = "fm_play";
    public static final String PLAY_ID = "_id";
    public static final String PLAY_INDEX = "play_index";
    public static final String PLAY_STATE = "play_state";
    public static final String PLAY_ALBUM_ID = "play_album_id";
    public static final String PLAY_TYPE = "play_type";

    public static final String TABLE_POPULAR_NAME = "fm_popular";
    public static final String POPULAR_ID = "_id";
    public static final String POPULAR_TITLE = "popular_title";
    public static final String POPULAR_TAG = "popular_tag";
    public static final String POPULAR_COVER_URL = "popular_cover_url";
    public static final String POPULAR_ALBUM_ID = "popular_album_id";

    public static final String TABLE_COLLECT_NAME = "fm_collect";
    public static final String COLLECT_ID = "_id";
    public static final String COLLECT_TITLE = "collect_title";
    public static final String COLLECT_TAG = "collect_tag";
    public static final String COLLECT_COVER_URL = "collect_cover_url";
    public static final String COLLECT_ALBUM_ID = "collect_album_id";


    public static final String TABLE_HISTORY_NAME = "fm_history";
    public static final String HISTORY_ID = "_id";
    public static final String HISTORY_TITLE = "history_title";
    public static final String HISTORY_COVER_URL = "history_cover_url";
    public static final String HISTORY_LARGE_COVER_URL = "history_large_cover_url";
    public static final String HISTORY_ALBUM_ID = "history_album_id";
    public static final String HISTORY_TRACK_ID = "history_track_id";
    public static final String HISTORY_CONTENT_TYPE = "history_content_type";
    public static final String HOMEPAGE_HISTORY_CACHE= "homepage_history_cache";


    public static final String TABLE_LOCAL_NAME = "fm_local";
    public static final String LOCAL_ID = "_id";
    public static final String LOCAL_TITLE = "local_title";
    public static final String LOCAL_COVER_URL = "local_cover_url";
    public static final String LOCAL_LARGE_COVER_URL = "local_large_cover_url";
    public static final String LOCAL_ALBUM_ID = "local_album_id";

    public static final String RADIO_DATA="radio_data";
    public static final String TABLE_RADIO_NAME = "fm_radio";
    public static final String RADIO_ID = "radio_id";
    public static final String SCHEDULE_ID = "schedule_id";
    public static final String RADIO_TYPE= "radio_type";
    public static final String RADIO_NAME= "radio_name";
    public static final String RADIO_DESC = "radio_desc";
    public static final String RADIO_PROGRAME_NAME = "radio_programe_name";
    public static final String RADIO_SCHEDULE_ID = "radio_schedule_id";
    public static final String RADIO_STARTTIME = "radio_starttime";
    public static final String RADIO_ENDTIME = "radio_endtime";
    public static final String RADIO_RATE24AAC_URL = "radio_rate24AacUrl";
    public static final String RADIO_RATE24TS_URL = "radio_rate24TsUrl";
    public static final String RADIO_RATE64AAC_URL = "radio_rate64AacUrl";
    public static final String RADIO_RATE64TS_URL = "radio_rate64TsUrl";
    public static final String RADIO_PLAYCOUNT = "radio_radioPlayCount";
    public static final String RADIO_COVERURLSMALL = "radio_coverUrlSmall";
    public static final String RADIO_COVERURLLARGE = "radio_coverUrlLarge";
    public static final String RADIO_PROGRAME_ID = "radio_programId";
    public static final String RADIO_UPDATE_AT = "radio_updateAt";
    public static final String RADIO_SHARE_URL = "radio_shareUrl";
    public static final String RADIO_ISACTIVITYLIVE = "radio_isActivityLive";
    public static final String RADIO_ACTIVITY_ID = "radio_activityId";
    public static final String RADIO_DATA_ID = "radio_dataId";
    public static final String RADIO_KIND = "radio_kind";

    public static final String TABLE_ADMIN_NAME = "fm_admin";
    public static final String ADMIN_ID = "_id";
    public static final String ADMIN_NAME = "admin_name";
    public static final String ADMIN_COVER_URL = "admin_cover_url";
    public static final String ADMIN_INTRODUCE = "admin_introduce";

    public static final String TABLE_RAIDO_TX_NAME = "fm_radio_tx";
    public static final String RADIO_TX_ID = "_id";
    public static final String RADIO_TX_NAME = "radio_tx_name";
    public static final String RADIO_TX_CONTENT = "radio_tx_content";

    public static final String TABLE_COLLECT_NUM_NAME = "fm_collect_num";
    public static final String COLLECT_NUM_ID = "_id";
    public static final String COLLECT_NUM_POS = "rcollect_num_pos";
}
