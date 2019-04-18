package com.flyaudio.flyradioonline.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flyaudio.flyradioonline.Constant;
import com.flyaudio.flyradioonline.util.Flog;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.history.PlayHistory;
import com.ximalaya.ting.android.opensdk.model.history.PlayHistoryAlbum;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

import java.util.ArrayList;
import java.util.List;


public class FmCacheDAO {

	private static FmCacheDAO mFmCacheDAO = null;

	private final String TAG = "FmCacheDAO";

	public static synchronized void initFmCacheDAO(Context context) {
		if (mFmCacheDAO == null) {
			mFmCacheDAO = new FmCacheDAO(context);
		}
	}

	public static synchronized FmCacheDAO getInstance() {
		return mFmCacheDAO;
	}

	private FmCacheDAO(Context context) {
		FlyaudioFmManager.initializeInstance(context);
	}

	/**
	 * 获取数据库
	 *
	 * @return SQLiteDatabase
	 */
	private SQLiteDatabase getDatabase() {
		return FlyaudioFmManager.getInstance().openDatabase();
	}

	//添加热门缓存
	public synchronized void insertPopularCache(List<Album> albumList){
		SQLiteDatabase database = getDatabase();
		try {
			Flog.e("liuzehao", "insertPopularCache//"+albumList.size());
			for(Album album : albumList){
				ContentValues values = getPopularValues(album);
				database.insert(Constant.TABLE_POPULAR_NAME, null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "insertPopularCache()===" + e.getMessage());
		} finally {
			closeDataBase(database);
		}
	}

	public synchronized boolean isHasHistoryCache(long albumId){
		Cursor cursor = null;
		SQLiteDatabase database = getDatabase();

		String sql = "select * from " + Constant.TABLE_HISTORY_NAME
				+ " where " +Constant.HISTORY_ALBUM_ID
				+ "=?";
		try {
			cursor =database.rawQuery(sql, new String[] { String.valueOf(albumId) });
			if(cursor.moveToNext()) {
				Flog.e(TAG,"isHasHistoryCache//用户已经存在播放记录");
				return true;
			}

		}catch (Exception e){
			e.printStackTrace();
			Flog.d(TAG, "isHasHistoryCache()===" + e.getMessage());
		}finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return false;
	}

	//添加播放历史缓存
	public synchronized void insertHistoryCache(List<PlayHistory> historyList){
		SQLiteDatabase database = getDatabase();
		try {
			Flog.e("liuzehao", "insertHistoryCache//"+historyList.size());
			for(PlayHistory history : historyList){
				ContentValues values = getHistoryValues(history);
				database.insert(Constant.TABLE_HISTORY_NAME, null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "insertHistoryCache()===" + e.getMessage());
		} finally {
			closeDataBase(database);
		}
	}

	//删除播放历史缓存
	public synchronized void deleteHistoryCache() {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_HISTORY_NAME,
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	//删除特定的播放历史缓存
	public synchronized  void deletePointHistoryCache(long albumId){
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_HISTORY_NAME,
					Constant.HISTORY_ALBUM_ID + "=?", new String[]{String.valueOf(albumId)});
			Flog.e(TAG, "deletePointHistoryCache//"+albumId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	//获取播放历史缓存
	public synchronized List<PlayHistory> getHistoryCache() {
		Cursor cursor = null;
		List<PlayHistory> historyList = new ArrayList<>();
		SQLiteDatabase database = getDatabase();

		String sql = "select * from "
				+ Constant.TABLE_HISTORY_NAME;
		try {
			cursor = database.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					PlayHistory history = getPlayHistory(cursor);
					historyList.add(history);
					Flog.e("liuzehao","getHistoryCache//当前有播放历史缓存//"+history.toString());
				}
			}
		} catch (Exception e) {
			Flog.d("Exception==" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return historyList;
	}

	//删除热门缓存
	public synchronized void deletePopularCache() {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_POPULAR_NAME,
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	//获取热门缓存
	public synchronized List<Album> getPolularCache() {
		Cursor cursor = null;
		List<Album> albumList = new ArrayList<>();
		SQLiteDatabase database = getDatabase();

		String sql = "select * from "
				+ Constant.TABLE_POPULAR_NAME;
		try {
			cursor = database.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Album album = getPopularAlbum(cursor);
					albumList.add(album);
					Flog.e("liuzehao","getPolularCache//当前有热门缓存//"+album.toString());
				}
			}
		} catch (Exception e) {
			Flog.d("Exception==" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return albumList;
	}


	//获取收藏缓存
	public synchronized int getCollectCount(long albumId) {
		Cursor cursor = null;
		int count = -1;
		SQLiteDatabase database = getDatabase();

		String sql = "select count(*)  from " + Constant.TABLE_COLLECT_NAME
				+ " where " + Constant.COLLECT_ALBUM_ID + "=?";
		try {
			cursor = database.rawQuery(sql, new String[]{String.valueOf(albumId)});
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					count = cursor.getInt(0);
					Flog.e("liuzehao","getCollectCount//当前有指定收藏缓存//");
				}
			}
		} catch (Exception e) {
			Flog.d("Exception==" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return count;
	}

	//获取收藏缓存
	public synchronized List<Album> getCollectCache() {
		Cursor cursor = null;
		List<Album> albumList = new ArrayList<>();
		SQLiteDatabase database = getDatabase();

		String sql = "select * from "
				+ Constant.TABLE_COLLECT_NAME;
		try {
			cursor = database.rawQuery(sql, null);

			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Album album = getCollectAlbum(cursor);
					albumList.add(album);
					Flog.e("liuzehao","getCollectCache//当前有收藏缓存//"+album.toString());
				}
			}
		} catch (Exception e) {
			Flog.d("Exception==" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return albumList;
	}

	//获取电台缓存
	public synchronized List<Radio> getRadioCache(String radioType) {
		Cursor cursor = null;
		List<Radio> radioList = new ArrayList<>();
		SQLiteDatabase database = getDatabase();

		String sql = "select * from "
				+ Constant.TABLE_RADIO_NAME
				+ " where " +Constant.RADIO_TYPE
				+ "=?";
		try {
			cursor = database.rawQuery(sql, new String[]{ radioType });
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Radio radio = getRadio(cursor);
					radioList.add(radio);
					Flog.e("liuzehao","getRadioCache//当前有电台缓存//"+radio.getRadioName());
				}
			}
		} catch (Exception e) {
			Flog.d("Exception==" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return radioList;
	}

	public boolean isCollected(long albumId){
		int count = getCollectCount(albumId);
		if(count > 0){
			return true;
		}else{
			return false;
		}
	}

	//添加指定收藏缓存
	public synchronized void insertPointCollectCache(Album album){
		List<Album> albumList = new ArrayList<>();
		albumList.add(album);
		insertCollectCache(albumList);
		Flog.e(TAG, "insertPointCollectCache//"+album.getId());
	}

	//是否存在有没收藏的集合位置
	public synchronized boolean isHasCollectNumCache(int pos){
		Cursor cursor = null;
		SQLiteDatabase database = getDatabase();

		String sql = "select * from " + Constant.TABLE_COLLECT_NUM_NAME
				+ " where " +Constant.COLLECT_NUM_POS
				+ "=?";
		try {
			cursor =database.rawQuery(sql, new String[] { String.valueOf(pos) });
			if(cursor.moveToNext()) {
				Flog.e(TAG,"isHasCollectNumCache//已经存在");
				return true;
			}

		}catch (Exception e){
			e.printStackTrace();
			Flog.d(TAG, "isHasCollectNumCache()===" + e.getMessage());
		}finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return false;
	}

	//添加没收藏的集合位置
	public synchronized void insertCollectNumCache(int pos){
		SQLiteDatabase database = getDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(Constant.COLLECT_NUM_POS, pos);
			database.insert(Constant.TABLE_COLLECT_NUM_NAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "insertCollectNumCache()===" + e.getMessage());
		} finally {
			closeDataBase(database);
		}
	}


	//删除收藏的集合位置
	public synchronized void deleteCollectNumCache(int pos) {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_COLLECT_NUM_NAME,
					Constant.COLLECT_NUM_POS + "=?", new String[]{String.valueOf(pos)});
			Flog.e(TAG, "deleteCollectNumCache//"+pos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	//删除所有没收藏位置数据
	public synchronized void deleteCollectNumCache() {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_COLLECT_NUM_NAME,
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	//添加收藏缓存
	public synchronized void insertCollectCache(List<Album> albumList){
		SQLiteDatabase database = getDatabase();
		try {
			for(Album album : albumList){
				ContentValues values = getCollectValues(album);
				database.insert(Constant.TABLE_COLLECT_NAME, null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "insertCollectCache()===" + e.getMessage());
		} finally {
			closeDataBase(database);
		}
	}


	//删除指定收藏缓存
	public synchronized void deletePointCollectCache(long albumId) {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_COLLECT_NAME,
					Constant.COLLECT_ALBUM_ID + "=?", new String[]{String.valueOf(albumId)});
			Flog.e(TAG, "deletePointCollectCache//"+albumId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	//删除收藏缓存
	public synchronized void deleteCollectCache() {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_COLLECT_NAME,
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	//添加本地播放记录
	public synchronized void insertLocalPLayCache(Album album){
		SQLiteDatabase database = getDatabase();
		try {
			ContentValues values = getLocalPlayValues(album);
			database.insert(Constant.TABLE_LOCAL_NAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "insertLocalPLayCache()===" + e.getMessage());
		} finally {
			closeDataBase(database);
		}
	}

	//删除本地播放记录
	public synchronized void deletePlayLocalCache(long albumId) {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_LOCAL_NAME,
					Constant.LOCAL_ALBUM_ID + "=?", new String[]{String.valueOf(albumId)});
			Flog.e(TAG, "deleteLocalCache//"+albumId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	public synchronized List<PlayHistory> getLocalPlayCache(){
		Cursor cursor = null;
		List<PlayHistory> historyList = new ArrayList<>();
		SQLiteDatabase database = getDatabase();

		String sql = "select * from "
				+ Constant.TABLE_LOCAL_NAME;
		try {
			cursor = database.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					PlayHistory history = getLocalPlayHistory(cursor);
					historyList.add(history);
					Flog.e("liuzehao","getLocalPlayCache//当前有本地播放记录缓存//"+historyList.toString());
				}
			}
		} catch (Exception e) {
			Flog.d("Exception==" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return historyList;
	}

	public synchronized boolean isHasLocalPlayCache(long albumId){
		Cursor cursor = null;
		SQLiteDatabase database = getDatabase();

		String sql = "select * from " + Constant.TABLE_LOCAL_NAME
				+ " where " +Constant.LOCAL_ALBUM_ID
				+ "=?";
		try {
			cursor =database.rawQuery(sql, new String[] { String.valueOf(albumId) });
			if(cursor.moveToNext()) {
				Flog.e(TAG,"isHasLocalPlayCache//已经存在播放记录");
				return true;
			}

		}catch (Exception e){
			e.printStackTrace();
			Flog.d(TAG, "isHasLocalPlayCache()===" + e.getMessage());
		}finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return false;
	}

	public synchronized boolean isSameRadioTxCache(String tagName, String radioContent){
		Cursor cursor = null;
		SQLiteDatabase database = getDatabase();

		String sql = "select * from "
				+ Constant.TABLE_RAIDO_TX_NAME
				+ " where " +Constant.RADIO_TX_NAME
				+ "=?";
		try {
			cursor = database.rawQuery(sql, new String[] {tagName});
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String content = cursor.getString(cursor.getColumnIndex(Constant.RADIO_TX_CONTENT));
					Flog.e("liuzehao","isSameRadioTxCache//"+content);
					if(radioContent.equals(content)){
						return true;
					}
				}
			}
		} catch (Exception e) {
			Flog.d("Exception==" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return false;
	}

	public synchronized boolean isHasRadioTxCache(String tagName){
		Cursor cursor = null;
		SQLiteDatabase database = getDatabase();

		String sql = "select * from "
				+ Constant.TABLE_RAIDO_TX_NAME
				+ " where " +Constant.RADIO_TX_NAME
				+ "=?";
		try {
			cursor = database.rawQuery(sql, new String[] {tagName});
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					return true;
				}
			}
		} catch (Exception e) {
			Flog.d("Exception==" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return false;
	}

	//添加电台字符串缓存
	public synchronized void insertRadioTxCache(String tagName, String radioContent){
		SQLiteDatabase database = getDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(Constant.RADIO_TX_NAME, tagName);
			values.put(Constant.RADIO_TX_CONTENT, radioContent);
			database.insert(Constant.TABLE_RAIDO_TX_NAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "insertRadioTxCache()===" + e.getMessage());
		} finally {
			closeDataBase(database);
		}
	}

	//修改电台字符串缓存
	public synchronized void updateRadioTxCache(String tagName, String radioContent){
		SQLiteDatabase database = getDatabase();
		try {
			database.beginTransaction();
			ContentValues values = new ContentValues();
			values.put(Constant.RADIO_TX_CONTENT, radioContent);
			database.update(Constant.TABLE_RAIDO_TX_NAME, values, Constant.RADIO_TX_NAME + "=?", new String[]{tagName});
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "updateRadioTxCache()===" + e.getMessage());
		} finally {
			database.endTransaction();
			closeDataBase(database);
		}
	}


	//添加电台缓存
	public synchronized void insertRadioCache(List<Radio> radioList, String radioType){
		SQLiteDatabase database = getDatabase();
		try {
			for(Radio radio : radioList){
				ContentValues values = getRadioValues(radio);
				values.put(Constant.RADIO_TYPE, radioType);
				database.insert(Constant.TABLE_RADIO_NAME, null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "insertRadioCache()===" + e.getMessage());
		} finally {
			closeDataBase(database);
		}
	}

	//删除电台缓存
	public synchronized void deleteRadioCache(String radioType) {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_RADIO_NAME,
					Constant.RADIO_TYPE + "=?",
					new String[] { radioType });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}



	//添加搜索历史
	public synchronized void insertSearchRecord(String searchRecord){
		SQLiteDatabase database = getDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(Constant.SEARCH_UID, Constant.FM_UID);
			values.put(Constant.SEARCH_NAME, searchRecord);
			database.insert(Constant.TABLE_SEARCH_NAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "insertSearchRecord()===" + e.getMessage());
		} finally {
			closeDataBase(database);
		}
	}

	//删除特定搜索历史
	public synchronized void deleteItemSearch(String searchRecord) {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_SEARCH_NAME,
					Constant.SEARCH_UID + "=?"
							+ " and " + Constant.SEARCH_NAME
							+ "=?",
					new String[] { String.valueOf(Constant.FM_UID), searchRecord});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	//删除搜索历史
	public synchronized void deleteSearchRecord() {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_SEARCH_NAME,
					Constant.SEARCH_UID + "=?",
					new String[] { String.valueOf(Constant.FM_UID)});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	public boolean isHasRecord(String record){
		Cursor cursor = null;
		SQLiteDatabase database = getDatabase();

		String sql = "select * from " + Constant.TABLE_SEARCH_NAME
				+ " where " +Constant.SEARCH_UID
				+ "=?"
				+ " and " + Constant.SEARCH_NAME
				+ "=?";
		try {
			cursor =database.rawQuery(sql, new String[] { Constant.FM_UID, record});
			if(cursor.moveToNext()) {
				Flog.e(TAG,"isHasRecord//已经存在搜索记录");
				return true;
			}

		}catch (Exception e){
			e.printStackTrace();
			Flog.d(TAG, "isHasRecord()===" + e.getMessage());
		}finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return false;
	}

	//获取搜索历史记录
	public synchronized List<String> getSearchRecord() {
		Cursor cursor = null;
		List<String> searchList = new ArrayList<>();
		SQLiteDatabase database = getDatabase();

		String sql = "select * from "
				+ Constant.TABLE_SEARCH_NAME
				+ " where " +Constant.SEARCH_UID
				+ "=?";
		try {
			cursor = database.rawQuery(sql, new String[] { Constant.FM_UID});
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String record = cursor.getString(cursor.getColumnIndex(Constant.SEARCH_NAME));
					searchList.add(record);
					Flog.e("liuzehao","getSearchRecord//当前账号有搜索记录//"+record);
				}
			}
		} catch (Exception e) {
			Flog.d("Exception==" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return searchList;
	}

	//添加播放记录
	public synchronized void insertPlayRecord(int playIndex, int playState, long albumId, String playType){
		SQLiteDatabase database = getDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(Constant.PLAY_INDEX, playIndex);
			values.put(Constant.PLAY_STATE, playState);
			values.put(Constant.PLAY_ALBUM_ID, albumId);
			values.put(Constant.PLAY_TYPE, playType);
			database.insert(Constant.TABLE_PLAY_NAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "insertPlayRecord()===" + e.getMessage());
		} finally {
			closeDataBase(database);
		}
	}

	//修改播放记录
	public synchronized void updatePlayRecord(int playIndex, int playState){
		SQLiteDatabase database = getDatabase();
		try {
			database.beginTransaction();
			ContentValues values = new ContentValues();
			if(playIndex != -1){
				values.put(Constant.PLAY_INDEX, playIndex);
			}
			if(playState != -1){
				values.put(Constant.PLAY_STATE, playState);
			}
			database.update(Constant.TABLE_PLAY_NAME, values, Constant.PLAY_ALBUM_ID + "=?",null);
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "updatePlayRecord()===" + e.getMessage());
		} finally {
			database.endTransaction();
			closeDataBase(database);
		}
	}

	//修改播放记录
	public synchronized void updatePlayRecord(int playState){
		SQLiteDatabase database = getDatabase();
		try {
			database.beginTransaction();
			ContentValues values = new ContentValues();
			values.put(Constant.PLAY_STATE, playState);
			database.update(Constant.TABLE_PLAY_NAME, values, Constant.PLAY_ALBUM_ID + "=?",null);
		} catch (Exception e) {
			e.printStackTrace();
			Flog.d(TAG, "updatePlayRecord()===" + e.getMessage());
		} finally {
			database.endTransaction();
			closeDataBase(database);
		}
	}

	//获取播放类型
	public synchronized String getPlayType() {
		Cursor cursor = null;
		SQLiteDatabase database = getDatabase();

		String sql = "select * from "
				+ Constant.TABLE_PLAY_NAME
				+ " where " +Constant.PLAY_TYPE
				+ "=?";
		try {
			cursor = database.rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String type = cursor.getString(cursor.getColumnIndex(Constant.PLAY_TYPE));
					Flog.e("liuzehao","getPlayType//当前账号有播放类型//"+type);
					return type;
				}
			}
		} catch (Exception e) {
			Flog.d("Exception==" + e.getMessage());
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
			closeDataBase(database);
		}
		return null;
	}

	//删除播放记录
	public synchronized void deletePlayRecord() {
		SQLiteDatabase database = getDatabase();
		try {
			database.delete(Constant.TABLE_PLAY_NAME,
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDataBase(database);
		}
	}

	private PlayHistory getPlayHistory(Cursor cursor){
		PlayHistory history = null;
		PlayHistoryAlbum album = null;
		if (cursor != null) {
			history = new PlayHistory(null, null);
			album = new PlayHistoryAlbum();
			album.setAlbumCoverUrlSmall(cursor.getString(cursor.getColumnIndex(Constant.HISTORY_COVER_URL)));
			album.setAlbumCoverUrlLarge(cursor.getString(cursor.getColumnIndex(Constant.HISTORY_LARGE_COVER_URL)));
			album.setAlbumTitle(cursor.getString(cursor.getColumnIndex(Constant.HISTORY_TITLE)));
			album.setAlbumId(cursor.getLong(cursor.getColumnIndex(Constant.HISTORY_ALBUM_ID)));
			album.setTrackId(cursor.getLong(cursor.getColumnIndex(Constant.HISTORY_TRACK_ID)));
			history.setContentType(cursor.getInt(cursor.getColumnIndex(Constant.HISTORY_CONTENT_TYPE)));
			history.setHistoryAlbum(album);
		}
		return history;
	}



	private Album getPopularAlbum(Cursor cursor){
		Album album = null;
		if (cursor != null) {
			album = new Album();
			album.setAlbumTitle(cursor.getString(cursor.getColumnIndex(Constant.POPULAR_TITLE)));
			album.setCoverUrlSmall(cursor.getString(cursor.getColumnIndex(Constant.POPULAR_COVER_URL)));
			album.setCoverUrlMiddle(cursor.getString(cursor.getColumnIndex(Constant.POPULAR_COVER_URL)));
			album.setAlbumTags(cursor.getString(cursor.getColumnIndex(Constant.POPULAR_TAG)));
			album.setId(cursor.getLong(cursor.getColumnIndex(Constant.POPULAR_ALBUM_ID)));
		}
		return album;
	}

	private PlayHistory getLocalPlayHistory(Cursor cursor){
		PlayHistory history = null;
		PlayHistoryAlbum album = null;
		if (cursor != null) {
			history = new PlayHistory(null, null);
			album = new PlayHistoryAlbum();
			album.setAlbumTitle(cursor.getString(cursor.getColumnIndex(Constant.LOCAL_TITLE)));
			album.setAlbumCoverUrlSmall(cursor.getString(cursor.getColumnIndex(Constant.LOCAL_COVER_URL)));
			album.setAlbumCoverUrlLarge(cursor.getString(cursor.getColumnIndex(Constant.LOCAL_LARGE_COVER_URL)));
			album.setAlbumId(cursor.getLong(cursor.getColumnIndex(Constant.LOCAL_ALBUM_ID)));
			history.setContentType(1);
			history.setHistoryAlbum(album);
		}
		return history;
	}

	private Album getCollectAlbum(Cursor cursor){
		Album album = null;
		if (cursor != null) {
			album = new Album();
			album.setAlbumTitle(cursor.getString(cursor.getColumnIndex(Constant.COLLECT_TITLE)));
			album.setCoverUrlSmall(cursor.getString(cursor.getColumnIndex(Constant.COLLECT_COVER_URL)));
			album.setId(cursor.getLong(cursor.getColumnIndex(Constant.COLLECT_ALBUM_ID)));
		}
		return album;
	}

	private Radio getRadio(Cursor cursor){
		Radio radio = null;
		if (cursor != null) {
			radio = new Radio();
			radio.setRadioName(cursor.getString(cursor.getColumnIndex(Constant.RADIO_NAME)));
			radio.setRadioDesc(cursor.getString(cursor.getColumnIndex(Constant.RADIO_DESC)));
			radio.setProgramName(cursor.getString(cursor.getColumnIndex(Constant.RADIO_PROGRAME_NAME)));
			radio.setScheduleID(cursor.getLong(cursor.getColumnIndex(Constant.RADIO_SCHEDULE_ID)));
			radio.setStartTime(cursor.getLong(cursor.getColumnIndex(Constant.RADIO_STARTTIME)));
			radio.setEndTime(cursor.getLong(cursor.getColumnIndex(Constant.RADIO_ENDTIME)));
			radio.setRate24AacUrl(cursor.getString(cursor.getColumnIndex(Constant.RADIO_RATE24AAC_URL)));
			radio.setRate24TsUrl(cursor.getString(cursor.getColumnIndex(Constant.RADIO_RATE24TS_URL)));
			radio.setRate64AacUrl(cursor.getString(cursor.getColumnIndex(Constant.RADIO_RATE64AAC_URL)));
			radio.setRate64TsUrl(cursor.getString(cursor.getColumnIndex(Constant.RADIO_RATE64TS_URL)));
			radio.setRadioPlayCount(cursor.getInt(cursor.getColumnIndex(Constant.RADIO_PLAYCOUNT)));
			radio.setCoverUrlSmall(cursor.getString(cursor.getColumnIndex(Constant.RADIO_COVERURLSMALL)));
			radio.setCoverUrlLarge(cursor.getString(cursor.getColumnIndex(Constant.RADIO_COVERURLLARGE)));
			radio.setProgramId(cursor.getLong(cursor.getColumnIndex(Constant.RADIO_PROGRAME_ID)));
			radio.setUpdateAt(cursor.getLong(cursor.getColumnIndex(Constant.RADIO_UPDATE_AT)));
			radio.setKind(cursor.getString(cursor.getColumnIndex(Constant.RADIO_KIND)));
			radio.setShareUrl(cursor.getString(cursor.getColumnIndex(Constant.RADIO_SHARE_URL)));
			radio.setActivityLive(Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(Constant.RADIO_ISACTIVITYLIVE))));
			radio.setActivityId(cursor.getLong(cursor.getColumnIndex(Constant.RADIO_ACTIVITY_ID)));
			radio.setDataId(cursor.getLong(cursor.getColumnIndex(Constant.RADIO_DATA_ID)));
		}
		return radio;
	}


	private ContentValues getPopularValues(Album album) {
		ContentValues values = new ContentValues();
		values.put(Constant.POPULAR_TITLE, album.getAlbumTitle());
		values.put(Constant.POPULAR_TAG, album.getAlbumTags());
		values.put(Constant.POPULAR_COVER_URL, album.getCoverUrlMiddle());
		values.put(Constant.POPULAR_ALBUM_ID, album.getId());
		return values;
	}

	private ContentValues getCollectValues(Album album) {
		ContentValues values = new ContentValues();
		values.put(Constant.COLLECT_TITLE, album.getAlbumTitle());
		values.put(Constant.COLLECT_TAG, album.getAlbumTags());
		values.put(Constant.COLLECT_COVER_URL, album.getCoverUrlSmall());
		values.put(Constant.COLLECT_ALBUM_ID, album.getId());
		return values;
	}

	private ContentValues getLocalPlayValues(Album album) {
		ContentValues values = new ContentValues();
		values.put(Constant.LOCAL_TITLE, album.getAlbumTitle());
		values.put(Constant.LOCAL_COVER_URL, album.getCoverUrlSmall());
		values.put(Constant.LOCAL_LARGE_COVER_URL, album.getCoverUrlLarge());
		values.put(Constant.LOCAL_ALBUM_ID, album.getId());
		return values;
	}

	private ContentValues getHistoryValues(PlayHistory history) {
		ContentValues values = new ContentValues();
		values.put(Constant.HISTORY_TITLE, history.getHistoryAlbum().getAlbumTitle());
		values.put(Constant.HISTORY_COVER_URL, history.getHistoryAlbum().getAlbumCoverUrlSmall());
		values.put(Constant.HISTORY_LARGE_COVER_URL, history.getHistoryAlbum().getAlbumCoverUrlLarge());
		values.put(Constant.HISTORY_ALBUM_ID, history.getHistoryAlbum().getAlbumId());
		values.put(Constant.HISTORY_TRACK_ID, history.getHistoryAlbum().getTrackId());
		values.put(Constant.HISTORY_CONTENT_TYPE, history.getContentType());
		return values;
	}

	private ContentValues getRadioValues(Radio radio) {
		ContentValues values = new ContentValues();
		values.put(Constant.RADIO_NAME, radio.getRadioName());
		values.put(Constant.RADIO_DESC, radio.getRadioDesc());
		values.put(Constant.RADIO_PROGRAME_NAME, radio.getProgramName());
		values.put(Constant.RADIO_SCHEDULE_ID, String.valueOf(radio.getScheduleID()));
		values.put(Constant.RADIO_STARTTIME, String.valueOf(radio.getStartTime()));
		values.put(Constant.RADIO_ENDTIME, String.valueOf(radio.getEndTime()));
		values.put(Constant.RADIO_RATE24AAC_URL, radio.getRate24AacUrl());
		values.put(Constant.RADIO_RATE24TS_URL, radio.getRate24TsUrl());
		values.put(Constant.RADIO_RATE64AAC_URL, radio.getRate64AacUrl());
		values.put(Constant.RADIO_RATE64TS_URL, radio.getRate64TsUrl());
		values.put(Constant.RADIO_PLAYCOUNT, String.valueOf(radio.getRadioPlayCount()));
		values.put(Constant.RADIO_COVERURLSMALL, radio.getCoverUrlSmall());
		values.put(Constant.RADIO_COVERURLLARGE, radio.getCoverUrlLarge());
		values.put(Constant.RADIO_PROGRAME_ID, String.valueOf(radio.getProgramId()));
		values.put(Constant.RADIO_UPDATE_AT, String.valueOf(radio.getUpdateAt()));
		values.put(Constant.RADIO_SHARE_URL, radio.getShareUrl());
		values.put(Constant.RADIO_KIND, radio.getKind());
		values.put(Constant.RADIO_ISACTIVITYLIVE, String.valueOf(radio.isActivityLive()));
		values.put(Constant.RADIO_ACTIVITY_ID, String.valueOf(radio.getActivityId()));
		values.put(Constant.RADIO_DATA_ID, String.valueOf(radio.getDataId()));
		return values;
	}

	/**
	 * 关闭database
	 *
	 * @param database
	 */
	private void closeDataBase(SQLiteDatabase database) {
		try {
			FlyaudioFmManager.getInstance().closeDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭游标
	 *
	 * @param cursor
	 */
	private void closeCursor(Cursor cursor) {
		try {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}