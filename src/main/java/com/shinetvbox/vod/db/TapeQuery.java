package com.shinetvbox.vod.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.shinetvbox.vod.utils.SqlHandle;


/**
 * Created by hrblaoj on 2018/4/27.
 */


public class TapeQuery extends BaseQuery implements Parcelable {
    /* shine view element*/
    public static final String SELECT_TAPE= "song_id,song_name,singer_name,mp3path,url,state,rowid";
    static final int LISTLIMIT = 9;
    static final int DOWNLOADING = 0;
    static final int VALID = 1;
    static final int INVALID = 2;

    public static final int SELECT = 0;
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;

    public int limit = 9;
    public int offset = 0;

    /* shine element*/
    public int querytype = SELECT;
    public String song_id;
    public String song_name;
    public String singer_name;
    public String url;
    public String mp3path;
    public int satate = DOWNLOADING;

    public void setQuerySelect(){
        querytype = SELECT;
    }

    public void setQueryInsert(){
        querytype = INSERT;
    }

    public void setQueryDel(){
        querytype = DELETE;
    }

    public void setQueryUpdate(){
        querytype = UPDATE;
    }

    public void setStateDownLoadIng(){
        satate = DOWNLOADING;
    }

    public void setStateValid(){
        satate = VALID;
    }

    public void setStateInValid(){
        satate = INVALID;
    }

    public TapeQuery(){
        reset();
    }

    public TapeQuery(String song_id, String song_name, String singer_name, String mp3path, String url, int satate){
        this.song_id = song_id;
        this.song_name = song_name;
        this.singer_name = singer_name;
        this.mp3path = mp3path;
        this.url = url;
        this.satate = satate;
        this.limit = 0;
        this.offset = 0;
    }

    public TapeQuery(Parcel in){
        readFromParcel(in);
    }

    public void reset(){
        querytype = SELECT;
        song_id = "";
        song_name = "";
        singer_name = "";
        url = "";
        mp3path = "";
        satate = DOWNLOADING;
        this.limit = 0;
        this.offset = 0;

    }

    public BaseQuery clone(){
        TapeQuery newQuery = new TapeQuery();
        newQuery.querytype = querytype;
        newQuery.song_id = song_id;
        newQuery.song_name = song_name;
        newQuery.singer_name = singer_name;
        newQuery.url = url;
        newQuery.mp3path = mp3path;
        newQuery.satate = satate;

        return newQuery;
    }

    public String toString(){
        return "";
    }

    public String maketapepagesql()
    {
        SqlHandle sqlhandle = null;
        String emptystring = new String("");

        sqlhandle = new SqlHandle("record");
        sqlhandle.FIELD("count(*)");


        return sqlhandle.toString();
    }

    public String maketapedatasql()
    {
        SqlHandle sqlhandle = null;
        String emptystring = new String("");

        sqlhandle = new SqlHandle("record");

        sqlhandle.FIELD(SELECT_TAPE);

        sqlhandle.LIMIT(offset, limit);

        return sqlhandle.toString();
    }

    public String makedelsql()
    {
        SqlHandle sqlhandle = null;
        String emptystring = new String("");

        sqlhandle = new SqlHandle("record");
        sqlhandle.OPERATE("delete");

        sqlhandle.CONDITION("mp3path", "=", mp3path);

        return sqlhandle.toString();
    }

    public String makeinsertsql()
    {
        SqlHandle sqlhandle = null;
        String emptystring = new String("");

        sqlhandle = new SqlHandle("record");
        sqlhandle.OPERATE("insert");

        sqlhandle.OPERATEFILED(song_id);
        sqlhandle.OPERATEFILED(song_name);
        sqlhandle.OPERATEFILED(singer_name);
        sqlhandle.OPERATEFILED(mp3path);
        sqlhandle.OPERATEFILED(url);
        sqlhandle.OPERATEFILED(satate);

        return sqlhandle.toString();
    }

    public String makeupdatesql()
    {
        SqlHandle sqlhandle = null;
        String emptystring = new String("");

        sqlhandle = new SqlHandle("record");
        sqlhandle.OPERATE("update");

//        sqlhandle.OPERATEFILED(song_id);
//        sqlhandle.OPERATEFILED(song_name);
//        sqlhandle.OPERATEFILED(singer_name);
//        sqlhandle.OPERATEFILED(mp3path);
//        sqlhandle.OPERATEFILED(url);
//        sqlhandle.OPERATEFILED(satate);


//        sqlhandle.OPERATEFILED("state", satate);
//        sqlhandle.CONDITION("mp3path", "=", mp3path);

        sqlhandle.OPERATEFILED("url", url);
        sqlhandle.CONDITION("song_id", "=", song_id);




        return sqlhandle.toString();
    }

    public boolean equals(Object obj){

        return true;
    }

    public static final Creator<TapeQuery> CREATOR = new Creator<TapeQuery>() {

        @Override
        public TapeQuery createFromParcel(Parcel in) {
            // TODO Auto-generated method stub
            return new TapeQuery(in);
        }

        @Override
        public TapeQuery[] newArray(int size) {
            // TODO Auto-generated method stub
            return new TapeQuery[size];
        }

    };
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel in, int arg1) {
        // TODO Auto-generated method stub
        in.writeInt(this.querytype);
        in.writeString(this.song_id);
        in.writeString(this.song_name);
        in.writeString(this.singer_name);
        in.writeString(this.url);
        in.writeString(this.mp3path);
        in.writeInt(this.satate);
        in.writeInt(this.offset);
        in.writeInt(this.limit);
    }

    public void readFromParcel(Parcel in) {
        this.querytype = in.readInt();
        this.song_id = in.readString();
        this.song_name = in.readString();
        this.singer_name = in.readString();
        this.url = in.readString();
        this.mp3path = in.readString();
        this.satate = in.readInt();
        this.offset = in.readInt();
        this.limit = in.readInt();

    }
}