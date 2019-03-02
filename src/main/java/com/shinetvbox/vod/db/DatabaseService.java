package com.shinetvbox.vod.db;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;


import com.shinetvbox.vod.utils.KtvLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shinetvbox.vod.MyApplication.configIni;


public class DatabaseService extends Service {
	private Object LOCK = new Object();

	private Object LOCKDBMirror = new Object();

	private static boolean isInit = false;
	private MyBinder mIBinder;

	private Map<BaseQuery,Integer> mapCachePageCount = new HashMap<>();

	public DatabaseService(){
		super();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mIBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mIBinder = new MyBinder(this);
		isInit = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    //通知栏消息有什么作用？？？ 8.0报错注释掉
//		Notification notification = new Notification();
//		notification.flags = Notification.FLAG_ONGOING_EVENT;
//		notification.flags |= Notification.FLAG_NO_CLEAR;
//		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
//		this.startForeground(1, notification);
		return START_NOT_STICKY;
//		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	@Override
	public void unbindService(ServiceConnection conn) {
		super.unbindService(conn);
	}

	public class MyBinder extends IDatabaseService.Stub {
		private String cacheDbPath;
		private char[] singerNameChar = new char[10];
		private char[] singerSpellChar = new char[26* 2];
		private char[] songNameChar = new char[10];
		private char[] songSpellChar = new char[26];
		public MyBinder(Context context){
			cacheDbPath = "/data/data/" + context.getPackageName() + "/DialogManagerUtil.db";
		}

		@Override
		public List<TapeQuery> getTapeDate(TapeQuery mTapeQuery) throws RemoteException {
			synchronized(LOCK){
				KtvLog.d("Server getTapeDate");

				String sql = mTapeQuery.maketapedatasql();

				ArrayList<TapeQuery> aTapeQuery = null;
				//aTapeQuery = KtvDbNative.getTapeDate(sql);
				KtvLog.d("getTapeDate make sql " + sql);
				return aTapeQuery;
			}
		}
		@Override
		public int getTapeCount(TapeQuery mTapeQuery) throws RemoteException {
			synchronized(LOCK){
				KtvLog.d("Server getTapeCount");
				int conunt = 0;

				String sql = mTapeQuery.maketapepagesql();


				//conunt = KtvDbNative.getTapeCount(sql);
				KtvLog.d("getTapeCount make sql " + sql + " count is " + conunt);
				return conunt;
			}
		}

		@Override
		public void delTapeSql(TapeQuery mTapeQuery) throws RemoteException {
			synchronized (LOCK) {
				KtvLog.d("delTapeSql mp3path is " + mTapeQuery.mp3path);
				String sql = mTapeQuery.makedelsql();
				KtvLog.d("delTapeSql sql " + sql);
				//KtvDbNative.execteTapeSql(sql);

			}
		}

		@Override
		public void insertTapeSql(TapeQuery mTapeQuery) throws RemoteException {
			synchronized (LOCK) {
				KtvLog.d("insertTapeSql id is " + mTapeQuery.song_id);
				String sql = mTapeQuery.makeinsertsql();
				KtvLog.d("insertTapeSql sql " + sql);
				//KtvDbNative.execteTapeSql(sql);

			}
		}

		@Override
		public void updateTapeSql(TapeQuery mTapeQuery) throws RemoteException {
			synchronized (LOCK) {
				String sql = mTapeQuery.makeupdatesql();
				KtvLog.d("updateTapeSql sql " + sql);
				//KtvDbNative.execteTapeSql(sql);
			}
		}

		@Override
		public void opentapedb(String path) throws RemoteException {
			synchronized(LOCK){
				KtvLog.d("Server opendb");
				//KtvDbNative.opentapedb(path);
			}
		}

		@Override
		public void opentapedbbyarg(String path, int del) throws RemoteException {
			synchronized(LOCK){
				KtvLog.d("Server opentapedbbyarg");
				//KtvDbNative.opentapedbbyarg(path, del);
			}
		}

		@Override
		public void opendb(String path) throws RemoteException {
			synchronized(LOCK){
				mapCachePageCount.clear();
				KtvLog.d("Server opendb");
				isInit = KtvDbNative.opendb(path);
				if(isInit) {
					KtvLog.d("Server init db success!");
					checkDb();
				}else{
					KtvLog.d("Server init db success failure!");
				}
			}
		}

		@Override
		public void opendbAndSetTemp(String path, String tempPath) throws RemoteException {
			synchronized(LOCK){
				KtvLog.d("Server opendb");
				isInit = KtvDbNative.opendbAndSetTemp(path, tempPath);
				if(isInit) {
					KtvLog.d("Server init tempDb success!");
					checkDb();
				}else{
					KtvLog.d("Server init tempDb success failure!");
				}
			}
		}

        @Override
        public void checkDb() throws RemoteException {
            synchronized(LOCK){
                KtvLog.d("Server checkDb");
                KtvDbNative.checkDb();
            }
        }


		@Override
		public boolean openrepairdb(String path) throws RemoteException {
			synchronized(LOCK){
				boolean ret = false;
//				ret = KtvDbNative.openrepairdb(path);
//				if(ret) {
//					KtvLog.d("openrepairdb 1111");;
//				}
//				else
//					KtvLog.d("openrepairdb 2222");

				return ret;
			}
		}

		@Override
		public void closedb() throws RemoteException {
			synchronized(LOCK){
				KtvDbNative.closedb();
			}
		}

		@Override
		public void closereapirdb() throws RemoteException {
			synchronized(LOCK){
				//KtvDbNative.closereapirdb();
			}
		}

		@Override
		public int getSingerCount(SingerQuery singerQuery) throws RemoteException {
			synchronized(LOCK){
				int conunt = 0;
				KtvLog.d("Server getSingerCount");
				String sql = singerQuery.makepagesql();
				KtvLog.d("getSingerCount make sql " + sql);
				conunt = KtvDbNative.getSingerCount(sql);
				return conunt;
			}
		}

		@Override
		public List<Singer> querySinger(SingerQuery singerQuery) throws RemoteException {
			// TODO Auto-generated method stub
			if(!isInit){
				return new ArrayList<Singer>();
			}
			synchronized(LOCK){
				String sql = singerQuery.makedatasql(SingerQuery.SELECT_SINGER);
				KtvLog.d("querySingerquerySinger make sql " + sql);

				ArrayList<Singer> aSinger = KtvDbNative.querySinger(sql);
				//ArrayList<Song> aSong = new ArrayList<Song>();
				KtvLog.d("querySingerquerySinger " + singerQuery.toString() + " size is " + aSinger.size());
				return aSinger;

			}
		}

		@Override
		public List<String> querySingerSmartPinyin(SingerQuery singerQuery) throws RemoteException {
//			if(singerQuery.spell_first_letter_abbreviation == null || singerQuery.spell_first_letter_abbreviation.length() == 0) return null;
			if(!isInit){
				return new ArrayList<String>(  );
			}
			synchronized(LOCK){
				String sql = singerQuery.makedatasql(SingerQuery.SELECT_SINGER_SMART_PINYIN );
				String[] strings = KtvDbNative.querySingerSmartPinyin(sql);
				List<String> list = new ArrayList<>(  );
				int len = singerQuery.spell_first_letter_abbreviation.length();
				for(String str:strings){
					if(str.length()>len){
						if(str.startsWith( singerQuery.spell_first_letter_abbreviation )){
							String key = str.substring( len,len+1 );
							if(!list.contains( key ) && !key.equals( "" )){
								list.add( key );
							}
						}
					}
				}
				return list;
			}
		}
		@Override
		public List<String> querySingerSmartHandwrite(SingerQuery songQuery) throws RemoteException {
			// TODO Auto-generated method stub
			if(!isInit){
				return new ArrayList<String>(  );
			}
			synchronized(LOCK){
				String sql = songQuery.makedatasql(SingerQuery.SELECT_SINGER_SMART_HANDWRITE );
				String[] strings = KtvDbNative.querySingerSmartHandwrite(sql);
				List<String> list = new ArrayList<>(  );
				int len = songQuery.singer_name.length();
				for(String str:strings){
					if(str.length()>len){
						String key = str.substring( len,len+1 );
						if(!list.contains( key ) && !key.equals( "" )){
							list.add( key );
							if(list.size()>=9) return list;
						}
					}
				}

				return list;
			}
		}
		@Override
		public int getSongCount(Query songQuery) throws RemoteException {
			synchronized(LOCK){
				int conunt = getCachePageCount( songQuery );
				if(conunt == -1){
					KtvLog.d("Server getSongCount");
					String sql = songQuery.makesql(Query.SELECT_SONG_COUNT);
					KtvLog.d("getSongCount make sql " + sql);
					conunt = KtvDbNative.getSongCount(sql);
					mapCachePageCount.put( songQuery.clone(),conunt );
				}
				return conunt;
			}
		}
		private int getCachePageCount(BaseQuery query){
			if(query == null || mapCachePageCount.size()==0) return -1;
			for(Map.Entry entry:mapCachePageCount.entrySet()){
				if(entry.getKey().equals( query )) return (int) entry.getValue();
			}
			return -1;
		}
        @Override
        public List<String> querySongSmartPinyin(Query songQuery) throws RemoteException {
			if(songQuery.spell_first_letter_abbreviation == null || songQuery.spell_first_letter_abbreviation.length() == 0) return null;
            // TODO Auto-generated method stub
            if(!isInit){
                return new ArrayList<String>(  );
            }
            synchronized(LOCK){
                String sql = songQuery.makesql(Query.SELECT_SONG_SMART_PINYIN );
                String[] strings = KtvDbNative.querySongSmartPinyin(sql);
				List<String> list = new ArrayList<>(  );
				int len = songQuery.spell_first_letter_abbreviation.length();
				for(String str:strings){
					if(str.length()>len){
						if(str.startsWith( songQuery.spell_first_letter_abbreviation )){
							String key = str.substring( len,len+1 );
							if(!list.contains( key ) && !key.equals( "" )){
								list.add( key );
							}
						}
					}
				}
                return list;
            }
        }
		@Override
		public List<String> querySongSmartHandwrite(Query songQuery) throws RemoteException {
			// TODO Auto-generated method stub
			if(!isInit){
				return new ArrayList<String>(  );
			}
			synchronized(LOCK){
				String sql = songQuery.makesql(Query.SELECT_SONG_SMART_HANDWRITE );
				String[] strings = KtvDbNative.querySongSmartHandwrite(sql);
				List<String> list = new ArrayList<>(  );
				int len = songQuery.song_name.length();
				for(String str:strings){
					if(str.length()>len){
						String key = str.substring( len,len+1 );
						if(!list.contains( key ) && !key.equals( "" )){
							list.add( key );
							if(list.size()>=9) return list;
						}
					}
				}

				return list;
			}
		}

		public void setConfigIni(String section, String key, String value) {
			configIni.set(section, key, value);
		}

		@Override
		public List<Song> querySong(Query songQuery) throws RemoteException {
			// TODO Auto-generated method stub
			if(!isInit){
				return new ArrayList<Song>();
			}
			synchronized(LOCK){
				String sql = songQuery.makesql(Query.SELECT_SONG);
				KtvLog.d("querySongquerySong make sql " + sql);
				ArrayList<Song> aSong = KtvDbNative.querySong(sql);
				//ArrayList<Song> aSong = new ArrayList<Song>();
				//KtvLog.d("querySongquerySong " + songQuery.toString() + " size is " + aSong.size());
				return aSong;

			}
		}

		@Override
		public int updateSetSql(Query songQuery, String key, String value) throws RemoteException {
			// TODO Auto-generated method stub
			if(!isInit){
				return -1;
			}
			synchronized(LOCK){
				String sql = songQuery.updatedatasql( key, value );
				//KtvLog.d("updatedata make sql " + sql);
//				int result = KtvDbNative.execteSongSql(sql);
//				//KtvLog.d("querySongquerySong " + songQuery.toString() + " size is " + result);
//				return result;
				return 0;

			}
		}

		@Override
		public int executeSqlStatement(String parStatement) throws RemoteException {
			if(!isInit){
				return -1;
			}
			synchronized(LOCK){
				KtvLog.d("executeSqlStatement make sql " + parStatement);

				int result = KtvDbNative.execteSongSql(parStatement);
				KtvLog.d("executeSqlStatement make sql result is" + " size is " + result);
				return result;
//				return 0;
			}
		}

		@Override
		public void testout(String[] list){
			list[0]="hellllllllo";
		}

		@Override
		public void initnew() throws RemoteException {
			synchronized(LOCK){
				KtvLog.d("Server init");
//				isInit = false;
//				File f = new File(path);
//				cacheDbPath = f.getParent()+"/cache.db";
//				KtvLog.d("cacheDbPath="+cacheDbPath);
//				if(reset) {
//					if(NativeTools.fileExist(cacheDbPath)) {
//						NativeTools.fileRemove(cacheDbPath);
//					}
//				}
//				KtvDbNative.init(path, songType, langs);
//				KtvDbNative.initSinger(path, nationalType);
//				//KtvDbNative.initSinger(path);
//				isInit = true;
			}
		}
		@Override
		public void init(String path, boolean reset, String[] songType, String[] langs, String[] nationalType) throws RemoteException {
			synchronized(LOCK){
				KtvLog.d("Server init");
//				isInit = false;
//				File f = new File(path);
//				cacheDbPath = f.getParent()+"/cache.db";
//				KtvLog.d("cacheDbPath="+cacheDbPath);
//				if(reset) {
//					if(NativeTools.fileExist(cacheDbPath)) {
//						NativeTools.fileRemove(cacheDbPath);
//					}
//				}
//				KtvDbNative.init(path, songType, langs);
//				KtvDbNative.initSinger(path, nationalType);
//				//KtvDbNative.initSinger(path);
//				isInit = true;
			}
		}

		//by Bati
		@Override
		public String gSqliteHdlGetData(String statment_){
			synchronized (LOCK) {
				return KtvDbNative.gSqliteHdlGetData(statment_);
			}

//			return "";
		}
		//gSqliteHdlGetData


		//by Bati
		@Override
		public void openDBMirror(String path){
			synchronized (LOCKDBMirror ) {
				//KtvDbNative.OpenKTVDBMirror(path, true);
			}
		}

		@Override
		public int executeDBMirrorStatement(String parStatement){
			synchronized (LOCKDBMirror ) {
//				int result = KtvDbNative.KTVDBMirrorExecuteSongSql(parStatement);
//				return result;
				return 0;
			}
		}

		@Override
		public void closeDBMirror(){
			synchronized (LOCKDBMirror ) {
				//KtvDbNative.CloseKTVDBMirror(true);
			}
		}

		@Override
		public void KTVDBMirrorInitNewTable(){
			synchronized (LOCKDBMirror ) {
				//KtvDbNative.KTVDBMirrorInitNewTable();
			}
		}

		@Override
		public void KTVDBMirrorGetData(String parStatement){
			synchronized (LOCKDBMirror ) {
				//KtvDbNative.KTVDBMirrorGetData(parStatement);
			}
		}
	}
}
