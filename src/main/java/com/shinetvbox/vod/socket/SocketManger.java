package com.shinetvbox.vod.socket;

import com.shinetvbox.vod.MyApplication;

/**
 * Created by hrblaoj on 2019/1/11.
 */

public class SocketManger {
    public static int Port7777 = 7777;
    public static int Port4444 = 4444;

    public static   MyApplication.InitCallBack initCallBack = new MyApplication.InitCallBack() {
            @Override
            public void init() {
                new TcpShortConnectionServer(7777).start();
                new TcpShortConnectionClient(4444);
            }
        };
}
