package com.shinetvbox.vod.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.shinetvbox.vod.utils.SqlHandle;
import com.shinetvbox.vod.utils.StringUtil;


public class SingerQuery extends BaseQuery implements Parcelable {
	public final static String SELECT_SINGER = "singer_id,singer_name,singer_sex,singer_region_new, local_click_rank";
	public final static String SELECT_SINGER_SMART_PINYIN = "spell_first_letter_abbreviation";
	public final static String SELECT_SINGER_SMART_HANDWRITE = "singer_name";

	/* shine view element*/
	public int number;//查询的个数
	public String singer_id;
	public String singer_name;
	public String singer_region_new;
	public String spell_first_letter_abbreviation;
	public String singer_name_word_count;
	public String singer_introduction;
	public String singer_sex;
	public String singer_region;
	public int offset;//查询的偏移量
	public int limit;//默认的个数
	public String tablename;
	public String local_click_rank;

	public SingerQuery(){
		reset();
	}

	public SingerQuery(Parcel in){
		readFromParcel(in);
	}

	public void reset(){
		number = 0;
		singer_id = "";
		singer_name = "";
		singer_region_new = "";
		spell_first_letter_abbreviation = "";
		singer_name_word_count = "";
		singer_introduction = "";
		singer_sex = "";
		singer_region = "";
		offset = 0;
		limit = 0;
		tablename = "";
		local_click_rank = "";
	}

	public SingerQuery clone(){
		SingerQuery newQuery = new SingerQuery();
		newQuery.singer_id = singer_id;
		newQuery.singer_name = singer_name;
		newQuery.singer_region_new = singer_region_new;
		newQuery.spell_first_letter_abbreviation = spell_first_letter_abbreviation;
		newQuery.singer_name_word_count = singer_name_word_count;
		newQuery.singer_introduction = singer_introduction;
		newQuery.singer_sex = singer_sex;
		newQuery.singer_region = singer_region;
		newQuery.offset = offset;
		newQuery.limit = limit;
		newQuery.tablename = tablename;
		newQuery.local_click_rank = local_click_rank;

		return newQuery;
	}

	public String toString(){
//		return "query  spell="+spell+" zs="+zs+" songBM"+songBM+" songName="+songName+" singer="+singer+
//				" songType="+songType+" lang="+lang+" movie="+movie+"  offset="+
//				offset+"  number="+number+" isNewSong="+isNewSong+"  cloud="+cloud+"  cloudDown="+cloudDown
//				+"  sortType="+sortType+"  mediaInfo="+mediaInfo +
//				"  singerType="+singerType+"  publicSongFlag="+publicSongFlag+"  user="+user;
		return "";
	}

	public String makepagesql()
	{
		SqlHandle sqlhandle = null;
		String emptystring = new String("");

		if(!tablename.equals(emptystring)) {
			sqlhandle = new SqlHandle(tablename);
		}
		else
		{
			sqlhandle = new SqlHandle("singer");
		}

		sqlhandle.FIELD("count(*)");

		if(!spell_first_letter_abbreviation.equals(emptystring)) {
			sqlhandle.CONDITION("spell_first_letter_abbreviation", ">=", spell_first_letter_abbreviation);
			sqlhandle.CONDITION("spell_first_letter_abbreviation", "<=", spell_first_letter_abbreviation+"z");
		}

		if(!singer_id.equals(emptystring)) {
			sqlhandle.CONDITION("singer_id", "=", singer_id);
		}

		if(!singer_name.equals(emptystring)) {
			sqlhandle.CONDITION("singer_name", "like", singer_name + "%");
		}

		if(!singer_name_word_count.equals(emptystring)) {
			sqlhandle.CONDITION("singer_name_word_count", "=", singer_name_word_count);
		}

		if(!singer_region_new.equals(emptystring)) {
			sqlhandle.CONDITION("singer_region_new", "=", singer_region_new);
		}

		if(!singer_introduction.equals(emptystring)) {
			sqlhandle.CONDITION("singer_introduction", "=", singer_introduction);
		}

		if(!singer_sex.equals(emptystring)) {
			sqlhandle.CONDITION("singer_sex", "=", singer_sex);
		}

		if(!singer_region.equals(emptystring)) {
			sqlhandle.CONDITION("singer_region", "=", singer_region);
		}

		return sqlhandle.toString();
	}

	public String makedatasql(String field)
	{
		SqlHandle sqlhandle = null;
		String emptystring = new String("");

		if(!tablename.equals(emptystring)) {
			sqlhandle = new SqlHandle(tablename);
		}
		else
		{
			sqlhandle = new SqlHandle("singer");
		}

//		sqlhandle.FIELD(SELECT_SINGER);
		sqlhandle.FIELD(field);

		//按照点击率排行， 这里不能用默认热度排行
		//sqlhandle.ORDERBY( "desc","singer_hot_rank" );

		if(!spell_first_letter_abbreviation.equals(emptystring)) {
			sqlhandle.CONDITION("spell_first_letter_abbreviation", ">=", spell_first_letter_abbreviation);
			sqlhandle.CONDITION("spell_first_letter_abbreviation", "<=", spell_first_letter_abbreviation+"z");
		}

		if(!singer_id.equals(emptystring)) {
			sqlhandle.CONDITION("singer_id", "=", singer_id);
		}

		if(!singer_name.equals(emptystring)) {
			sqlhandle.CONDITION("singer_name", "like", singer_name + "%");
		}

		if(!singer_name_word_count.equals(emptystring)) {
			sqlhandle.CONDITION("singer_name_word_count", "=", singer_name_word_count);
		}

		if(!singer_region_new.equals(emptystring)) {
			sqlhandle.CONDITION("singer_region_new", "=", singer_region_new);
		}

		if(!singer_introduction.equals(singer_introduction)) {
			sqlhandle.CONDITION("song_name", "=", singer_introduction);
		}

		if(!singer_sex.equals(emptystring)) {
			sqlhandle.CONDITION("singer_sex", "=", singer_sex);
		}

		if(!singer_region.equals(emptystring)) {
			sqlhandle.CONDITION("singer_region", "=", singer_region);
		}

		if(!local_click_rank.equals(emptystring)){
			sqlhandle.ORDERBY( "desc","local_click_rank" );
		}

		if(field.equals( SELECT_SINGER )){
			sqlhandle.LIMIT(offset, limit);
		}
		return sqlhandle.toString();
	}

	public boolean equals(Object obj){
		SingerQuery query = null;
		if(obj instanceof SingerQuery) {
			query = (SingerQuery)obj;
		} else {
			return false;
		}

		if(!StringUtil.compareString(this.singer_name, query.singer_name)){
			return false;
		}
		if(!StringUtil.compareString(this.spell_first_letter_abbreviation, query.spell_first_letter_abbreviation)){
			return false;
		}
		if(!StringUtil.compareString(this.tablename, query.tablename)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_region_new, query.singer_region_new)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_name_word_count, query.singer_name_word_count)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_introduction, query.singer_introduction)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_sex, query.singer_sex)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_region, query.singer_region)){
			return false;
		}
		if(!StringUtil.compareString(this.singer_id, query.singer_id)){
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

	public static final Creator<SingerQuery> CREATOR = new Creator<SingerQuery>() {

		@Override
		public SingerQuery createFromParcel(Parcel in) {
			// TODO Auto-generated method stub
			return new SingerQuery(in);
		}

		@Override
		public SingerQuery[] newArray(int size) {
			// TODO Auto-generated method stub
			return new SingerQuery[size];
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
		in.writeInt(this.number);
		in.writeString(this.singer_id);
		in.writeString(this.singer_name);
		in.writeString(this.singer_region_new);
		in.writeString(this.spell_first_letter_abbreviation);
		in.writeString(this.singer_name_word_count);
		in.writeString(this.singer_introduction);
		in.writeString(this.singer_sex);
		in.writeString(this.singer_region);;
		in.writeInt(this.offset);
		in.writeInt(this.limit);
		in.writeString(this.tablename);
		in.writeString(this.local_click_rank);
	}
	
	public void readFromParcel(Parcel in) {
		this.number = in.readInt();
		this.singer_id = in.readString();
		this.singer_name = in.readString();
		this.singer_region_new = in.readString();
		this.spell_first_letter_abbreviation = in.readString();
		this.singer_name_word_count = in.readString();
		this.singer_introduction = in.readString();
		this.singer_sex = in.readString();
		this.singer_region = in.readString();
		this.offset = in.readInt();
		this.limit = in.readInt();
		this.tablename = in.readString();
		this.local_click_rank = in.readString();
	}
}
