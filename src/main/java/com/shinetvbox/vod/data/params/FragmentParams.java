package com.shinetvbox.vod.data.params;

import android.view.View;

import com.shinetvbox.vod.db.Query;
import com.shinetvbox.vod.db.SingerQuery;

import java.util.List;

public class FragmentParams extends Object {
    /***
     * 当前页面
     */
    public int pageIndex = -1;
    /***
     * 当前页面目标焦点
     */
    public View viewFocus = null;
    /***
     * 是否显示已选歌曲按钮
     */
    public boolean isShowBtnSelectedSong = true;
    /***
     * 是否是页面返回操作
     */
    public boolean isPageBack = false;
    /***
     * 小键盘参数<br>
     * 是否显示小键盘，标题，提示语，输入文本，智能拼音
     */
    public Keyboard keyboard = new Keyboard();
    /***
     * 歌曲列表信息<br>
     * 标题，缩略图，提示语
     */
    public SongListInfo songListInfo = new SongListInfo();
    /***
     * 歌星歌曲列表信息<br>
     * 歌星名，缩略图，提示语
     */
    public SingerListInfo singerListInfo = new SingerListInfo();

    /***
     * 软件更新版本信息<br>
     * 版本号
     */
    public SoftUpdateInfo softUpdateInfo = new SoftUpdateInfo();


    public class Keyboard{
        public boolean isShow = false;
        public int titleId = 0;
        public int hintId = 0;
        public String inputText = "";
        public List<String> smartPinyin = null;
    }
    public class SongListInfo{
        public int titleId = 0;
        public int thumbnailId = 0;
        public int hintId = 0;
        public int hintContentId = 0;
        public String thumbnailPath = null;
        public Query query = null;
        public int totalPage = 1;
        public int curPage = 1;
        public int totalSong = 0;
    }
    public class SingerListInfo{
        public String singerName = "";
        public int hintContentId = 0;
        public String thumbnailPath = "";
        public SingerQuery singerQuery = null;
        public int totalPage = 1;
        public int curPage = 1;
        public int totalSong = 0;
    }
    public class SoftUpdateInfo{
        public String version = "";
        public String describe = "";
    }
}
