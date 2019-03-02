package com.shinetvbox.vod.service.cloudserver;

import static com.shinetvbox.vod.service.cloudserver.CloudMessageProc.DOWNLOAD_CMD.GET_NULL;

/**
 * Created by Administrator on 2018/5/14.
 */

public class CloudDownloadSongStruce {



        public CloudDownloadSongStruce(CloudMessageProc.DOWNLOAD_CMD cmd, String content){
                if(!cmd.equals(this.cmd))
                    this.cmd = cmd;
                if(!cmd.equals(this.content))
                    this.content = content;
        }

        public String getContent(){
            return content;
        }

        public CloudMessageProc.DOWNLOAD_CMD getCmd(){
            return cmd;
        }
        //public DOWNLOAD_CMD cmd;
    public CloudMessageProc.DOWNLOAD_CMD cmd = GET_NULL;
    public String content = "";
}
