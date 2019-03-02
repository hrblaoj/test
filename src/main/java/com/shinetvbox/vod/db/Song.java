package com.shinetvbox.vod.db;

import android.os.Parcel;
import android.os.Parcelable;


public class Song implements Parcelable {
	public Song(){
		reset();
	}
	public Song(Parcel in) {
		readFromParcel(in);
	}
	
	public Song(String singer_id1, String singer_id2, String singer_id3, String singer_id4, String song_id, String accompany_track, String karaoke_track, String song_name, String show_movie_name,
                String accompany_volume, String karaoke_volume, String language, String song_type, String singer_name,
                String singer_sex, String song_version, String local_path, String light_control_set, String song_theme,
                String new_song_theme, String pingfen, String word_head_code){
		this.singer_id1 = singer_id1;
		this.singer_id2 = singer_id2;
		this.singer_id3 = singer_id3;
		this.singer_id4 = singer_id4;
		this.song_id = song_id;
		this.accompany_track = accompany_track;
		this.karaoke_track = karaoke_track;
		this.song_name = song_name;
		this.show_movie_name = show_movie_name;
		this.accompany_volume = accompany_volume;
		this.karaoke_volume = karaoke_volume;
		this.language = language;
		this.song_type = song_type;
		this.singer_name = singer_name;
		this.singer_sex = singer_sex;
		this.song_version = song_version;
		this.local_path = local_path;
		this.light_control_set = light_control_set;
		this.song_theme = song_theme;
		this.new_song_theme = new_song_theme;
		this.pingfen = pingfen;
		this.word_head_code = word_head_code;


	}

	public String songBM;
	public String songName;
	public int zs;
	public String singer;
	public String songType;
	public String lang;
	public String path;
	public String fileName;
	public String spell;
	public int volume;
	public int musicTrack;
	public int stroke;
	public int orderTimes;
	public int isNewSong;
	public int cloud;
	public int cloudDown;
	public int isMovie;
	public String serviceName;

	/* shine element*/
	public String singer_id1;
	public String singer_id2;
	public String singer_id3;
	public String singer_id4;
	public String song_id;
	public String accompany_track;
	public String karaoke_track;
	public String song_name;
	public String show_movie_name;
	public String accompany_volume;
	public String karaoke_volume;
	public String language;
	public String song_type;
	public String singer_name;
	public String singer_sex;
	public String song_version;
	public String local_path;
	public String light_control_set;
	public String song_theme;
	public String new_song_theme;
	public String pingfen;
	public String word_head_code;

	public String toString(){
		return "songBM="+songBM+"  songName="+songName+"  zs="+zs+"  singer="+singer+
				"  songType=="+songType+"  lang="+lang+"  path="+path+"  fileName="+fileName+
				"  spell="+spell+"  volume="+volume+"  musicTrack="+musicTrack+
				"  stroke="+stroke+"  orderTimes="+orderTimes+" isNewSong="+isNewSong+
				"  cloud="+cloud+"  cloudDown="+cloudDown+"  isMovie="+isMovie+" serviceName="+serviceName;
	}
	
	public void reset(){
		this.singer_id1 = "";
		this.singer_id2 = "";
		this.singer_id3 = "";
		this.singer_id4 = "";
		this.song_id = "";
		this.accompany_track = "";
		this.karaoke_track = "";
		this.song_name = "";
		this.show_movie_name = "";
		this.accompany_volume = "";
		this.karaoke_volume = "";
		this.language = "";
		this.song_type = "";
		this.singer_name = "";
		this.singer_sex = "";
		this.song_version = "";
		this.local_path = "";
		this.light_control_set = "";
		this.song_theme = "";
		this.new_song_theme = "";
		this.pingfen = "";
		this.word_head_code= "";
	}
	
	public static final Parcelable.Creator<Song> CREATOR = new Creator<Song>() {

		@Override
		public Song createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Song(source);
		}

		@Override
		public Song[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Song[size];
		}
		
	};
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(this.singer_id1==null?"":this.singer_id1);
		dest.writeString(this.singer_id2==null?"":this.singer_id2);
		dest.writeString(this.singer_id3==null?"":this.singer_id3);
		dest.writeString(this.singer_id4==null?"":this.singer_id4);
		dest.writeString(this.song_id==null?"":this.song_id);
		dest.writeString(this.accompany_track==null?"":this.accompany_track);
		dest.writeString(this.karaoke_track==null?"":this.karaoke_track);
		dest.writeString(this.song_name==null?"":this.song_name);
		dest.writeString(this.show_movie_name==null?"":this.show_movie_name);
		dest.writeString(this.accompany_volume==null?"":this.accompany_volume);
		dest.writeString(this.karaoke_volume==null?"":this.karaoke_volume);
		dest.writeString(this.language==null?"":this.language);
		dest.writeString(this.song_type==null?"":this.song_type);
		dest.writeString(this.singer_name==null?"":this.singer_name);
		dest.writeString(this.singer_sex==null?"":this.singer_sex);
		dest.writeString(this.song_version==null?"":this.song_version);
		dest.writeString(this.local_path==null?"":this.local_path);
		dest.writeString(this.light_control_set==null?"":this.light_control_set);
		dest.writeString(this.song_theme==null?"":this.song_theme);
		dest.writeString(this.new_song_theme==null?"":this.new_song_theme);
		dest.writeString(this.pingfen==null?"":this.pingfen);
		dest.writeString(this.word_head_code==null?"":this.word_head_code);

	}
	
	public void readFromParcel(Parcel dest){
		this.singer_id1 = dest.readString();
		this.singer_id2 = dest.readString();
		this.singer_id3 = dest.readString();
		this.singer_id4 = dest.readString();
		this.song_id = dest.readString();
		this.accompany_track = dest.readString();
		this.karaoke_track = dest.readString();
		this.song_name = dest.readString();
		this.show_movie_name = dest.readString();
		this.accompany_volume = dest.readString();
		this.karaoke_volume = dest.readString();
		this.language = dest.readString();
		this.song_type = dest.readString();
		this.singer_name = dest.readString();
		this.singer_sex = dest.readString();
		this.song_version = dest.readString();
		this.local_path = dest.readString();
		this.light_control_set = dest.readString();
		this.song_theme = dest.readString();
		this.new_song_theme = dest.readString();
		this.pingfen = dest.readString();
		this.word_head_code = dest.readString();
	}
	
//	public static Song buildSong(Ksongs ksong){
//		Song s = new Song();
//		s.songBM = ksong.getSONGBM();
//		s.songName = ksong.getSONGNAME();
//		s.zs = (int) ksong.getZS();
//		s.singer = ksong.getSINGER();
//		s.songType = ksong.getSONGTYPE();
//		s.lang = ksong.getLANG();
//		s.path = ksong.getPATH();
//		s.fileName = ksong.getFILENAME();
//		s.spell = ksong.getSPELL();
//		s.serviceName = ksong.getServiceName();
//
//		s.volume = (int) ksong.getVOLUME();
//		s.musicTrack = (int) ksong.getMUSICTRACK();
//		s.orderTimes = (int) ksong.getORDERTIMES();
//		s.isNewSong = (int) ksong.getNEWSONG();
//		s.cloud = ksong.getCLOUD();
//		s.cloudDown = -1;
//		s.stroke = -1;
//		s.isMovie = (int) ksong.getMOVIE();
//		return s;
//	}
}
