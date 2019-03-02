package com.shinetvbox.vod.data.constants;

public class Constants {
    //动画类型
    //默认类型，也就是滑动
    public static final int ANIMATION_DEFAULT = 0;
    //中间翻阅动画
    public static final int ANIMATION_FLIP = 1;
    //旋转动画
    public static final int ANIMATION_ROTATION = 2;
    //歌曲每页数量
    public static final int SONG_LIST_LIMIT = 7;
    //歌星每页数量
    public static final int SINGER_LIST_LIMIT = 12;
    //焦点动画X轴缩放值
    public static final float FOCUS_XSCALE = (float) 1.05;
    //焦点动画Y轴缩放值
    public static final float FOCUS_YSCALE = (float) 1.05;
    //焦点动画播放时长
    public static final int FOCUS_DURATION  = 200;

    //歌曲左侧信息图片大小
    public static final int SONG_INFO_THUMB_SIZE  = 303;

    //歌星图片路径
    public static final String SINGER_IMAGE_PATH = "";
    //微信点歌二维码路径
    public static String WECHAT_ARCODE_PATH = "";

    //微信点歌服务器IP
    public static final String WECHAT_IP = "121.42.47.32";
    //微信点歌服务器端口
    public static final int WECHAT_PORT = 80;

    //微信相关信息（如二维码）
    public static final String HTTP_WECHAT_INFO = "http://ks3.cloud.joyk.com.cn:80/KfunCloud/GetKfunQrCode";
}
