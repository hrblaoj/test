package com.shinetvbox.vod.socket;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by hrblaoj on 2018/11/2.
 */

public class MessageProcss {
    ArrayList<MessageType> MessageTypeList = new java.util.ArrayList<MessageType>();

    synchronized void addTypeList(MessageType type){
        MessageTypeList.add(type);
    }

    synchronized MessageType dispatchTask(byte[] recvBuf){
        for(MessageType t:MessageTypeList){
            byte[] b = t.messageHead.getBytes();
            byte[] tmp = new byte[b.length];
            System.arraycopy(recvBuf , 0, tmp, 0, tmp.length);
            if(Arrays.equals(b, tmp))
                return t;
        }

        return null;
    }
}
