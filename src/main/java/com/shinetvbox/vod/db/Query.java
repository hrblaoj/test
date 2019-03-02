package com.shinetvbox.vod.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


import com.shinetvbox.vod.utils.SqlHandle;
import com.shinetvbox.vod.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Query extends BaseQuery implements Parcelable {

	public final static String SELECT_SONG = "song_id,accompany_sing_track,karaoke_track,song_name,show_movie_name,accompany_volume,karaoke_volume,language,song_type,singer_name,singer_sex,song_version,local_path,light_control_set,song_theme,new_song_theme,first_word_stroke_number,singer_id1,singer_id2,singer_id3,singer_id4,word_head_code";

    public final static String SELECT_SONG_COUNT = "count(*)";

	public final static String SELECT_SONG_SMART_PINYIN = "spell_first_letter_abbreviation,moive_spell_first_letter";

	public final static String SELECT_SONG_SMART_HANDWRITE = "song_name";



	/* shine view element*/
	public List<String> recordStepList = new ArrayList<String>();//记录查询条件
	public int cloud = -1;//是否是云端 1代表云端

	/* shine select element*/
	public String new_song_date;//新歌推荐
	public String singer_id1;
	public String singer_id2;
	public String singer_id3;
	public String singer_id4;
	public String song_id;
	public String spell_first_letter_abbreviation;
	public String spell_first_letter_traditional;
	public String song_name_word_count;
	public String song_name;//歌名
	public String word_head_code;
	public String language;
	public String first_word_stroke_number;
	public String song_theme;//主题类型
	public String sing_number;
	public String singer_name;
	public String song_version;
	public String member_name;
	public String song_type;//歌曲分类（京剧、梆子...）
	public String new_song_theme;//娱乐节目
	public String movie_spell_first_letter;
	public String movie_name;
	public String local_path;
	public int offset;//查询的偏移量
	public int limit;//默认的个数
	public String tablename;
	public boolean isNativeSong;//本地歌曲

	public ArrayList<String> mOrderList = new ArrayList<String>();
	public ArrayList<String> mCustormCndtnList = new ArrayList<String>();


	public Query(){
		reset();
	}

	public Query(Parcel in){
		readFromParcel(in);
	}
	
	public void reset(){
		cloud = -1;
		new_song_date = "";
		singer_id1 = "";
		singer_id2 = "";
		singer_id3 = "";
		singer_id4 = "";
		song_id = "";
		spell_first_letter_abbreviation = "";
		spell_first_letter_traditional = "";
		song_name_word_count = "";
		song_name = "";
		word_head_code = "";
		language = "";
		first_word_stroke_number = "";
		song_theme = "";
		sing_number = "";
		singer_name = "";
		song_version = "";
		member_name = "";
		song_type = "";
		new_song_theme = "";
		movie_spell_first_letter = "";
		movie_name = "";
		local_path = "";
		offset = 0;
		limit = 0;
		tablename = "";
		isNativeSong = false;
        recordStepList.clear();

        mOrderList.clear();
        mCustormCndtnList.clear();
	}
	
	public Query clone(){
		Query newQuery = new Query();
		newQuery.new_song_date = new_song_date;
		newQuery.singer_id1 = singer_id1;
		newQuery.singer_id2 = singer_id2;
		newQuery.singer_id3 = singer_id3;
		newQuery.singer_id4 = singer_id4;
		newQuery.song_id = song_id;
		newQuery.spell_first_letter_abbreviation = spell_first_letter_abbreviation;
		newQuery.spell_first_letter_traditional = spell_first_letter_traditional;
		newQuery.song_name_word_count = song_name_word_count;
		newQuery.song_name = song_name;
		newQuery.word_head_code = word_head_code;
		newQuery.language = language;
		newQuery.first_word_stroke_number = first_word_stroke_number;
		newQuery.song_theme = song_theme;
		newQuery.sing_number = sing_number;
		newQuery.singer_name = singer_name;
		newQuery.song_version = song_version;
		newQuery.member_name = member_name;
		newQuery.song_type = song_type;
		newQuery.new_song_theme = new_song_theme;
		newQuery.movie_spell_first_letter = movie_spell_first_letter;
		newQuery.movie_name = movie_name;
		newQuery.local_path = local_path;
		newQuery.offset = offset;
		newQuery.limit = limit;
		newQuery.tablename = tablename;
        newQuery.recordStepList=recordStepList;
		newQuery.isNativeSong = isNativeSong;

		for( String n : mOrderList ){
			newQuery.mOrderList.add(n);
		}

		for( String n : mCustormCndtnList ){
            newQuery.mCustormCndtnList.add(n);
        }

		return newQuery;
	}
	
	public String toString(){
//		return "query  spell="+spell+" zs="+zs+" songBM"+songBM+" songName="+songName+" singer="+singer+
//				" songType="+songType+" lang="+lang+" movie="+movie+"  offset="+
//				offset+"  number="+number+" isNewSong="+isNewSong+"  cloud="+cloud+"  cloudDown="+cloudDown
//				+"  sortType="+sortType+"  mediaInfo="+mediaInfo +
//				"  singerType="+singerType+"  publicSongFlag="+publicSongFlag+"  user="+user;
        return "\r\ncloud="+cloud+"//song_id="+song_id+"//spell_first_letter_abbreviation="+spell_first_letter_abbreviation
				+"\r\n//spell_first_letter_traditional="+spell_first_letter_traditional+"//song_name_word_count="+song_name_word_count
				+"\r\n//song_name="+song_name+"//word_head_code="+word_head_code+"//language="+language
				+"\r\n//first_word_stroke_number="+first_word_stroke_number+"//song_theme="+song_theme+"//sing_number="+sing_number
				+"\r\n//singer_name="+singer_name+"//song_version="+song_version+"//member_name="+member_name
				+"\r\n//song_type="+song_type+"//new_song_theme="+new_song_theme+"//movie_spell_first_letter="+ movie_spell_first_letter
				+"\r\n//movie_name="+movie_name+"//local_path="+local_path+"//offset="+offset+"//limit="+limit;
	}

	public String makesql(String field)
	{
		SqlHandle sqlhandle = null;
		String emptystring = "";

		if(!tablename.equals(emptystring)) {
			sqlhandle = new SqlHandle(tablename);
		}
		else
		{
			sqlhandle = new SqlHandle("song");
		}

		sqlhandle.FIELD(field);

		if(field.equals( SELECT_SONG )){
			sqlhandle.LIMIT(offset, limit);
		}

		if(!new_song_date.equals(emptystring)) {
			sqlhandle.CONDITION("new_song_date", ">=", new_song_date);
			if(field.equals( SELECT_SONG )){
				sqlhandle.ORDERBY_CUSTOM("new_song_date desc,song_name_word_count asc");
			}
		}
		//设置-本地歌曲 按本地点击率升序，语言降序排列
		if(isNativeSong){
			sqlhandle.ORDERBY_CUSTOM("word_head_code desc,language desc");
		}
		//迪曲加密开启时，过滤其他歌曲中的迪曲（主题-劲爆迪曲 除外）
//		if(configIni.get("CONFIG", "DJPasswd", "0").equals( "1" )){
//			if(!song_theme.equals( "1" )){
//				sqlhandle.CONDITION("song_theme", "!=", "1");
//			}
//		}
		//影视金曲增加电影名称搜索
		if(!spell_first_letter_abbreviation.equals(emptystring)) {
			if(!song_theme.equals( "8" )){
				sqlhandle.CONDITION("spell_first_letter_abbreviation", ">=", spell_first_letter_abbreviation);
				sqlhandle.CONDITION("spell_first_letter_abbreviation", "<=", spell_first_letter_abbreviation+"z");
			}else{
				String condition = "((spell_first_letter_abbreviation >='"+spell_first_letter_abbreviation+
						"' and spell_first_letter_abbreviation <= '"+spell_first_letter_abbreviation+
						"z') or (moive_spell_first_letter >='"+spell_first_letter_abbreviation+
						"' and moive_spell_first_letter <= '"+spell_first_letter_abbreviation+"z'))";
				sqlhandle.CONDITION_CUSTOM( condition );
			}
		}

		if(!singer_id1.equals(emptystring)) {
			sqlhandle.CONDITION("singer_id1", "=", singer_id1);
		}

		if(!singer_id2.equals(emptystring)) {
			sqlhandle.CONDITION("singer_id2", "=", singer_id2);
		}

		if(!singer_id3.equals(emptystring)) {
			sqlhandle.CONDITION("singer_id3", "=", singer_id3);
		}

		if(!singer_id4.equals(emptystring)) {
			sqlhandle.CONDITION("singer_id4", "=", singer_id4);
		}
		if(!singer_name.equals(emptystring)) {
			sqlhandle.CONDITION("singer_name", "like", "%"+singer_name+"%");
		}
		if(!song_theme.equals(emptystring)) {
			sqlhandle.CONDITION("song_theme", "=", song_theme);
		}

		if(!new_song_theme.equals(emptystring)) {
			sqlhandle.CONDITION("new_song_theme", "=", new_song_theme);
		}

		if(!song_name_word_count.equals(emptystring)) {
			if(song_name_word_count.equals( "8" ))
				sqlhandle.CONDITION("song_name_word_count", ">=", song_name_word_count);
			else
				sqlhandle.CONDITION("song_name_word_count", "=", song_name_word_count);
		}
		if(!song_type.equals(emptystring)) {
			sqlhandle.CONDITION("song_type", "=", song_type);
		}
		byte[] maxCode={(byte)0xFD,(byte)0xBF,(byte)0xBF,(byte)0xBF,(byte)0xBF,(byte)0xBF};
		if(!song_name.equals(emptystring)) {
			//sqlhandle.CONDITION("song_name", "like",song_name + "%");
			sqlhandle.CONDITION("song_name", ">=", song_name);
			sqlhandle.CONDITION("song_name", "<=", song_name + new String(maxCode));
		}
		if(!movie_name.equals(emptystring)) {
			//sqlhandle.CONDITION("song_name", "like",song_name + "%");
			sqlhandle.CONDITION("movie_name", ">=", movie_name);
			sqlhandle.CONDITION("movie_name", "<=", movie_name + new String(maxCode));
		}
		/**数据库字段movie拼错为moive_spell_first_letter*/
		if(!movie_spell_first_letter.equals(emptystring)) {
			sqlhandle.CONDITION("moive_spell_first_letter", ">=", movie_spell_first_letter);
			sqlhandle.CONDITION("moive_spell_first_letter", "<=", movie_spell_first_letter+"z");
		}

		if(!language.equals(emptystring)) {
			if(language.equals( "39" ))
				sqlhandle.CONDITION("language", ">=", "7");
			else
				sqlhandle.CONDITION("language", "=", language);
		}

		if( !song_id.equals(emptystring) ){
			sqlhandle.CONDITION("song_id", "=", song_id);
		}

		//记录本地点播率
		if( !word_head_code.equals(emptystring) ){
			if( word_head_code.equals("desc") ){
				//降序排列
				sqlhandle.ORDERBY( "desc" , "word_head_code" );
			}
			else if( word_head_code.equals("asc") ){
				//升序排列
				sqlhandle.ORDERBY( "asc" , "word_head_code" );
			}

		}

		//是否显示云歌
//		if(configIni.get("CONFIG", "DISPLAY_CLOUD_SONG", "1").equals("0")){
//			sqlhandle.CONDITION("local_path", "=", "0");
//		}else{
//			if(!local_path.equals(emptystring)) {
//				sqlhandle.CONDITION("local_path", "=", local_path);
//			}
//		}

		Log.i( "select_sql_setence",sqlhandle.toString() );

		if( mOrderList.size() > 0 ){
			for( String nStr : mOrderList){
				sqlhandle.ORDERBY_CUSTOM(nStr);
			}
		}

		if( mCustormCndtnList.size() > 0 ){
            for( String nStr : mCustormCndtnList){
                sqlhandle.CONDITION_CUSTOM(nStr);
            }
        }
		return sqlhandle.toString();
	}

	public String updatedatasql(String modifyKey, String modifyValue )
	{
		SqlHandle sqlhandle = null;
		String emptystring = new String("");

		if(!tablename.equals(emptystring)) {
			sqlhandle = new SqlHandle(tablename);
		}
		else
		{
			sqlhandle = new SqlHandle("song");
		}

		sqlhandle.OPERATE("update");

		sqlhandle.OPERATEFILED(modifyKey, modifyValue);
		if(!song_id.equals(emptystring)) {
			sqlhandle.CONDITION("song_id", "=", song_id);
		}

		return sqlhandle.toString();

	}




	public boolean equals(Object obj){
		Query query = null;
		if(obj instanceof Query) {
			query = (Query)obj;
		} else {
			return false;
		}

		if(this.cloud != query.cloud) {
			return false;
		}
		if(!StringUtil.compareString(this.song_id, query.song_id)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_id1, query.singer_id1)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_id2, query.singer_id2)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_id3, query.singer_id3)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_id4, query.singer_id4)){
			return false;
		}
		if(!StringUtil.compareString(this.spell_first_letter_abbreviation, query.spell_first_letter_abbreviation)){
			return false;
		}
		if(!StringUtil.compareString(this.tablename, query.tablename)){
			return false;
		}
		if(!StringUtil.compareString(this.spell_first_letter_traditional, query.spell_first_letter_traditional)){
			return false;
		}
		if(!StringUtil.compareString(this.song_name_word_count, query.song_name_word_count)){
			return false;
		}
		if(!StringUtil.compareString(this.song_name, query.song_name)){
			return false;
		}
		if(!StringUtil.compareString(this.word_head_code, query.word_head_code)){
			return false;
		}
		if(!StringUtil.compareString(this.first_word_stroke_number, query.first_word_stroke_number)){
			return false;
		}
		if(!StringUtil.compareString(this.language, query.language)){
			return false;
		}
		if(!StringUtil.compareString(this.song_theme, query.song_theme)){
			return false;
		}
		if(!StringUtil.compareString(this.sing_number, query.sing_number)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_name, query.singer_name)){
			return false;
		}
		if(!StringUtil.compareString(this.song_version, query.song_version)){
			return false;
		}
		if(!StringUtil.compareString(this.member_name, query.member_name)){
			return false;
		}
		if(!StringUtil.compareString(this.song_type, query.song_type)){
			return false;
		}
		if(!StringUtil.compareString(this.new_song_theme, query.new_song_theme)){
			return false;
		}
		if(!StringUtil.compareString(this.movie_spell_first_letter, query.movie_spell_first_letter )){
			return false;
		}
		if(!StringUtil.compareString(this.movie_name, query.movie_name)){
			return false;
		}
		if(!StringUtil.compareString(this.local_path, query.local_path)){
			return false;
		}
		if(this.offset != query.offset){
			return false;
		}
		if(this.limit != query.limit){
			return false;
		}
		return true;
	}
	
	public static final Parcelable.Creator<Query> CREATOR = new Creator<Query>() {

		@Override
		public Query createFromParcel(Parcel in) {
			return new Query(in);
		}

		@Override
		public Query[] newArray(int size) {
			return new Query[size];
		}
		
	};
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel in, int arg1) {
		in.writeInt(this.cloud);
		in.writeString(this.new_song_date);
		in.writeString(this.singer_id1);
		in.writeString(this.singer_id2);
		in.writeString(this.singer_id3);
		in.writeString(this.singer_id4);
		in.writeString(this.song_id);
		in.writeString(this.spell_first_letter_abbreviation);
		in.writeString(this.spell_first_letter_traditional);
		in.writeString(this.song_name_word_count);
		in.writeString(this.song_name);
		in.writeString(this.word_head_code);
		in.writeString(this.language);
		in.writeString(this.first_word_stroke_number);
		in.writeString(this.song_theme);
		in.writeString(this.sing_number);
		in.writeString(this.singer_name);
		in.writeString(this.song_version);
		in.writeString(this.member_name);
		in.writeString(this.song_type);
		in.writeString(this.new_song_theme);
		in.writeString(this.movie_spell_first_letter );
		in.writeString(this.movie_name);
		in.writeString(this.local_path);
		in.writeInt(this.offset);
		in.writeInt(this.limit);
		in.writeString(this.tablename);
		in.writeByte( (byte) (this.isNativeSong?1:0) );
		in.writeStringList( this.mOrderList );
		in.writeStringList( this.mCustormCndtnList );
	}
	
	public void readFromParcel(Parcel in) {
		this.cloud = in.readInt();
		this.new_song_date = in.readString();
		this.singer_id1 = in.readString();
		this.singer_id2 = in.readString();
		this.singer_id3 = in.readString();
		this.singer_id4 = in.readString();
		this.song_id = in.readString();
		this.spell_first_letter_abbreviation = in.readString();
		this.spell_first_letter_traditional = in.readString();
		this.song_name_word_count = in.readString();
		this.song_name = in.readString();
		this.word_head_code = in.readString();
		this.language = in.readString();
		this.first_word_stroke_number = in.readString();
		this.song_theme = in.readString();
		this.sing_number = in.readString();
		this.singer_name = in.readString();
		this.song_version = in.readString();
		this.member_name = in.readString();
		this.song_type = in.readString();
		this.new_song_theme = in.readString();
		this.movie_spell_first_letter = in.readString();
		this.movie_name = in.readString();
		this.local_path = in.readString();
		this.offset = in.readInt();
		this.limit = in.readInt();
		this.tablename = in.readString();
		this.isNativeSong = in.readByte()!=0;

		in.readStringList(this.mOrderList );
		in.readStringList(this.mCustormCndtnList );

	}
}
