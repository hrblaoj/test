package com.shinetvbox.vod.db;

/**
 * Created by hrblaoj on 2018/4/2.
 */

public class SongInfo {

    public SongInfo(){
        reset();
    }

    /* shine element*/
    public int index;
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
    public String yuanchang;
    public String gaoqing;

    public String singer_id1;
    public String singer_id2;
    public String singer_id3;

    public String word_head_code;

    public int downStatus = 0;//0 ing 1 ok 2 failed

    public void setsongid(String song_id) {
        this.song_id = song_id;
    }

    public void set_accompany_track(String accompany_track) {
        this.accompany_track = accompany_track;
    }
    public void set_karaoke_track(String karaoke_track) {
        this.karaoke_track = karaoke_track;
    }

    public void set_songname(String song_name) {
        this.song_name = song_name;
    }

    public void set_show_movie_name(String show_movie_name) {
        this.show_movie_name = show_movie_name;
    }

    public void set_language(String language) {
        this.language = language;
    }

    public void set_singer_name(String singer_name) {
        this.singer_name = singer_name;
    }

    public void set_singer_sex(String singer_sex) {
        this.singer_sex = singer_sex;
    }

    public void set_song_version(String song_version) {
        this.song_version = song_version;
    }

    public void set_local_path(String local_path) {
        this.local_path = local_path;
    }

    public void set_light_control_set(String light_control_set) {
        this.light_control_set = light_control_set;
    }

    public void set_song_theme(String song_theme) {
        this.song_theme = song_theme;
    }

    public void set_new_song_theme(String new_song_theme) {
        this.new_song_theme = new_song_theme;
    }

    public void set_pingfen(String pingfen) {
        this.pingfen = pingfen;
    }

    public void set_singer_id1( String _id ){
        this.singer_id1 = _id;
    }

    public String get_songname() {
        return song_name;
    }

    public String toString(){
        return "";
    }

    public void reset(){
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
        this.yuanchang = "";
        this.gaoqing = "";

        this.singer_id1 = "";
        this.singer_id2 = "";
        this.singer_id3 = "";

        this.word_head_code = "";
    }
    public static SongInfo buildKsong(Song song) {
        SongInfo songInfo = new SongInfo();

        songInfo.song_id = song.song_id;;
        songInfo.accompany_track = song.accompany_track;
        songInfo.karaoke_track = song.karaoke_track;
        songInfo.song_name = song.song_name;
        songInfo.show_movie_name = song.show_movie_name;
        songInfo.accompany_volume = song.accompany_volume;
        songInfo.karaoke_volume = song.karaoke_volume;
        songInfo.language = song.language;
        songInfo.song_type = song.song_type;
        if(song.singer_name.endsWith( "/" )){
            songInfo.singer_name = song.singer_name.substring( 0,song.singer_name.length()-1 );
        }else{
            songInfo.singer_name = song.singer_name;
        }
        songInfo.singer_sex = song.singer_sex;
        songInfo.song_version = song.song_version;
        songInfo.local_path = song.local_path;
        songInfo.light_control_set = song.light_control_set;
        songInfo.song_theme = song.song_theme;
        songInfo.new_song_theme = song.new_song_theme;
        songInfo.pingfen = song.pingfen;
        songInfo.singer_id1 = song.singer_id1;
        songInfo.singer_id2 = song.singer_id2;
        songInfo.singer_id3 = song.singer_id3;
        songInfo.word_head_code = song.word_head_code;

        return songInfo;
    }
    public SongInfo clone(){
        SongInfo mSongInfo = new SongInfo();

        mSongInfo.song_id = song_id;
        mSongInfo.accompany_track = accompany_track;
        mSongInfo.karaoke_track = karaoke_track;
        mSongInfo.song_name = song_name;
        mSongInfo.show_movie_name = show_movie_name;
        mSongInfo.accompany_volume = accompany_volume;
        mSongInfo.karaoke_volume = karaoke_volume;
        mSongInfo.language = language;
        mSongInfo.song_type = song_type;
        mSongInfo.singer_name = singer_name;
        mSongInfo.singer_sex = singer_sex;
        mSongInfo.song_version = song_version;
        mSongInfo.local_path = local_path;
        mSongInfo.light_control_set = light_control_set;
        mSongInfo.song_theme = song_theme;
        mSongInfo.new_song_theme = new_song_theme;
        mSongInfo.pingfen = pingfen;

        mSongInfo.singer_id1 = singer_id1;
        mSongInfo.singer_id2 = singer_id2;
        mSongInfo.singer_id3 = singer_id3;

        mSongInfo.word_head_code = word_head_code;

        return mSongInfo;
    }

}
