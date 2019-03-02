package com.shinetvbox.vod.db;

import java.util.ArrayList;

public class KtvDbNative {

	static {
		System.loadLibrary("native-lib");
	}

	public static native boolean opendb(String path);
	public static native boolean opendbAndSetTemp(String path, String tempPath);

	public static native void checkDb();

	public static native ArrayList<Song> querySong(String sql);

	public static native String[] querySongSmartPinyin(String sql);

	public static native int getSongCount(String sql);

	public static native String[] querySingerSmartHandwrite(String sql);

	public static native String[] querySongSmartHandwrite(String sql);

	public static native String[] querySingerSmartPinyin(String sql);

	public static native ArrayList<Singer> querySinger(String sql);

	public static native int getSingerCount(String sql);

	public static native int closedb();
//	public static native void checkDb();

//	public static native boolean openrepairdb(String path);
//
//	public static native int closedb();
//
//	public static native int closereapirdb();
//
//	public static native void execteTapeSql(String sql);
//
//	public static native void opentapedb(String path);
//
//	public static native void opentapedbbyarg(String path, int delarg);
//
//	public static native int getSongCount(String sql);
//
//	public static native int getSingerCount(String sql);
//
//	public static native ArrayList<Song> querySong(String sql);
//
//	public static native String[] querySongSmartHandwrite(String sql);
//
//	public static native ArrayList<Singer> querySinger(String sql);
//
//	public static native String[] querySingerSmartHandwrite(String sql);
//
//	public static native int getTapeCount(String sql);
//
//    public static native ArrayList<TapeQuery> getTapeDate(String sql);
//
//    //by Bati , update 数据库
	public static native int execteSongSql(String sql);
//	//by Bati , ktv10.db直接获取数据
	public static native String gSqliteHdlGetData(String parStatement);
//
//	//by Bati, 数据库版本更新
//	public static native void MainFuncUpdateDB();
//
//	//by Bati, 备份数据库操作相关
//	public static native int CloseKTVDBMirror(boolean isMemoryMode);
//	public static native int OpenKTVDBMirror(String filePath, boolean isMemoryMode);
//	public static native int KTVDBMirrorExecuteSongSql(String sql);
//	public static native void KTVDBMirrorInitNewTable();
//	public static native String KTVDBMirrorGetData(String parStatement);
//	public static native int KTVDBMirrorUpdateFromBakVersion();
//	public static native int KTVDB_mirror_row_count(String parStatement);
//	public static native  int KTVDB_SetStopUpdating( boolean boolStop );


	public  static native String  jni_GetPlayMd5Key();

    public static native int getSongCountByDbName(String name);
}
