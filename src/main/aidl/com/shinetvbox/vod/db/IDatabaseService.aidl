// IDatabaseService.aidl
package com.shinetvbox.vod.db;
import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.db.Song;
import com.shinetvbox.vod.db.Singer;
import com.shinetvbox.vod.db.TapeQuery;
import com.shinetvbox.vod.db.SingerQuery;

// Declare any non-default types here with import statements

interface IDatabaseService {
    /**初始化数据库*/
    	void init(String path, boolean reset, in String[] songType, in String[] langs, in String [] nationalType);

    	void initnew();

    	void testout(out String[] list);

        List<TapeQuery> getTapeDate(in TapeQuery mTapeQuery);

    	int getTapeCount(in TapeQuery mTapeQuery);

    	void updateTapeSql(in TapeQuery mTapeQuery);

    	void insertTapeSql(in TapeQuery mTapeQuery);

    	void delTapeSql(in TapeQuery mTapeQuery);

    	void opentapedb(String path);

    	void opentapedbbyarg(String path, int del);

        void opendbAndSetTemp(String path, String tempPath);
    	void opendb(String path);
    	void checkDb();

    	int getSongCount(in Query songQuery);

    	int getSingerCount(in SingerQuery singerQuery);

        List<String> querySingerSmartPinyin(in SingerQuery singerQuery);

        List<String> querySingerSmartHandwrite(in SingerQuery singerQuery);

    	/**根据查询条件，获取歌曲*/
        List<Song> querySong(in Query songQuery);

        List<String> querySongSmartPinyin(in Query songQuery);

        List<String> querySongSmartHandwrite(in Query songQuery);

        List<Singer> querySinger(in SingerQuery singerQuery);

        //by Bati 将数据写入song表
        int updateSetSql(in Query songQuery, String key, String value);
        //by Bati 在song表上直接写入
        int executeSqlStatement(String parStatement);

        //by Bati , 数据库备份操作 2018-06-13
        void openDBMirror(String path);
        int executeDBMirrorStatement(String parStatement);
        void closeDBMirror();
        void KTVDBMirrorInitNewTable();
        void KTVDBMirrorGetData(String parStatement);
        String gSqliteHdlGetData(String statment_);

        void setConfigIni(String section,String key,String value);
        boolean openrepairdb(String path);
        void closedb();
        void closereapirdb();

}
