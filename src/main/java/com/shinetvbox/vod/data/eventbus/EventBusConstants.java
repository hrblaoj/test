package com.shinetvbox.vod.data.eventbus;

public class EventBusConstants {
    /** 初始化网络相关服务 */
    public static final int INIT_NETWORK_RELATED_SERVICES = 0x00001;
    /** 进入屏切 */
    public static final int VIDEO_SHOW = 0x00002;
    /** 退出屏切 */
    public static final int VIDEO_HIDE = 0x00003;
    /** 请求权限成功 */
    public static final int REQUEST_PERMISSION_SUCCESS = 0x00004;
    /** 请求权限失败 */
    public static final int REQUEST_PERMISSION_FAILURE = 0x00005;

    /** 返回 */
    public static final int PAGE_BACK = 0x10000;
    /** 进入首页 */
    public static final int PAGE_GOTO_HOME = 0x10001;
    /** 进入首页-点歌台 */
    public static final int PAGE_GOTO_HOME_DIANGETAI = 0x10002;
    /** 进入首页-影视 */
    public static final int PAGE_GOTO_HOME_YINGSHI = 0x10003;
    /** 进入首页-娱乐 */
    public static final int PAGE_GOTO_HOME_YULE = 0x10004;
    /** 进入首页-应用 */
    public static final int PAGE_GOTO_HOME_YINGYONG = 0x10005;
    /** 进入-影视金曲 */
    public static final int PAGE_GOTO_YINGSHIJINQU = 0x10006;
    /** 进入-排行榜 */
    public static final int PAGE_GOTO_PAIHANGBANG = 0x10007;
    /** 进入-拼音点歌 */
    public static final int PAGE_GOTO_PINYINDIANGE = 0x10008;
    /** 进入-歌星点歌 */
    public static final int PAGE_GOTO_GEXINGDIANGE = 0x10009;
    /** 进入-分类点歌 */
    public static final int PAGE_GOTO_FENLEIDIANGE = 0x10010;
    /** 进入-新歌推荐 */
    public static final int PAGE_GOTO_XINGGETUIJIAN = 0x10011;
    /** 进入-已选歌曲 */
    public static final int PAGE_GOTO_YIXUANGEQU = 0x10012;
    /** 进入-已选歌曲 */
    public static final int PAGE_GOTO_XIAZAIGEQU = 0x10013;
    /** 进入-已唱歌曲 */
    public static final int PAGE_GOTO_YICHANGGEQU = 0x10014;
    /** 进入-语言选择 */
    public static final int PAGE_GOTO_LANGUAGE = 0x10015;
    /** 进入-排行榜-歌曲列表 */
    public static final int PAGE_GOTO_PAIHANGBANG_LIST = 0x10016;
    /** 进入-歌星点歌-歌星列表 */
    public static final int PAGE_GOTO_GEXINGDIANGE_LIST = 0x10017;
    /** 进入-歌星点歌-歌星歌曲列表 */
    public static final int PAGE_GOTO_GEXINGDIANGE_GEQU_LIST = 0x10018;
    /** 进入-分类点歌-歌曲列表 */
    public static final int PAGE_GOTO_FENLEIDIANGE_LIST = 0x10019;
    /** 进入-分类点歌-歌曲列表 */
    public static final int PAGE_GOTO_UPDATEAPP = 0x10020;
    /** 进入-支付页面 */
    public static final int PAGE_GOTO_PAY = 0x10021;
    /** 进入-支付页面-兑换码 */
    public static final int PAGE_GOTO_PAY_CDKEY = 0x10022;


    /** 小键盘设置回调 */
    public static final int KEYBOARD_SET_LISTENER = 0x20001;
    /** 小键盘设置文本 */
    public static final int KEYBOARD_SET_INPUTTEXT = 0x20002;


    /** 歌曲下载列表刷新 */
    public static final int SONG_DOWNLOAD_REFRESH = 0x30001;
    /** 歌曲下载成功 */
    public static final int SONG_DOWNLOAD_SUCCESS = 0x30002;
    /** 歌曲下载失败 */
    public static final int SONG_DOWNLOAD_FAILURE = 0x30003;
    /** 歌曲下载进度 */
    public static final int SONG_DOWNLOAD_PROGRESS = 0x30004;
    /** 歌曲下载数量改变 */
    public static final int SONG_DOWNLOAD_NUMBER_CHANGE = 0x30005;

    /** 播放歌曲改变 */
    public static final int SONG_PLAY_CHANGE = 0x40001;
    /** 清空查询条件 */
    public static final int SONG_CLEAR_QUERY = 0x40002;
    /** 已选歌曲数量改变 */
    public static final int SONG_SELECT_NUMBER_CHANGE = 0x40003;

    public static final int CLOUD_UI_PROC = 0x40004;

    public static final int FLOAT_WINDOW_CHANGE = 0x40005;

    public static final int WINDOWN_TOAST = 0x40006;
    /** 软件更新进度 */
    public static final int APP_UPDATE_PROGRESS = 0x50001;

    /** 语言切换 */
    public static final int LANGUAGE_CHANGE = 0x60001;
    /** 会员信息更新 */
    public static final int MEMBER_INFO_REFRESH = 0x60002;

}
