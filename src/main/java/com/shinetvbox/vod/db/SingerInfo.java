package com.shinetvbox.vod.db;

/**
 * 歌星数据
 * @date 2018-12-28
 */

public class SingerInfo{

	public String singer_id;
	public String singer_name;
	public String singer_sex;
	public String singer_region_new;

	public static SingerInfo buildKsinger(Singer singer) {
		SingerInfo ksinger = new SingerInfo();

		ksinger.singer_id = singer.singer_id;
		ksinger.singer_name = singer.singer_name;
		ksinger.singer_sex = singer.singer_sex;
		ksinger.singer_region_new = singer.singer_region_new;
		return ksinger;
	}

	public SingerInfo clone(){
		SingerInfo newSingerInfo = new SingerInfo();

		newSingerInfo.singer_id = this.singer_id;
		newSingerInfo.singer_name = this.singer_name;
		newSingerInfo.singer_sex = this.singer_sex;
		newSingerInfo.singer_region_new = this.singer_region_new;

		return newSingerInfo;
	}
}
