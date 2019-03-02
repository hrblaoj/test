package com.shinetvbox.vod.socket;

/**
 * Created by hrblaoj on 2018/11/2.
 */

public class MessageType {
    public static String GBK_CODE = "GBK";
    public static String UTF8_CODE = "UTF-8";
    public static String STRING_TYPE = "string";
    public static String BYTE_TYPE = "byte";
    String messageHead;
    String messageCode;
    String messageTag;
    String processType = STRING_TYPE;


    onProcess monProcess;

    public interface onProcess{
        void onProcessHead();
        void onReponse(byte[] sendbuf);
        void onProcessBody(String str, TcpShortConnectionServer.socketProcessRunnable socketProcessRunnable);
        void onProcessBody(byte[] bytes, TcpShortConnectionServer.socketProcessRunnable socketProcessRunnable);
    }
    public MessageType(String mMessageHead, String mMessageCode){
        messageHead = mMessageHead;
        messageCode = mMessageCode;
    }

    public MessageType(String mMessageHead, String mMessageCode, onProcess mOnProcess){
        messageHead = mMessageHead;
        messageCode = mMessageCode;
        monProcess = mOnProcess;
    }

    public MessageType(String mMessageType, String mMessageTag, String mMessageHead, String mMessageCode, onProcess mOnProcess, MessageProcss mMessageProcss){
        processType = mMessageType;
        messageTag = mMessageTag;
        messageHead = mMessageHead;
        messageCode = mMessageCode;
        monProcess = mOnProcess;
        mMessageProcss.addTypeList(this);
    }

    void setMessageProcess(onProcess m){
        monProcess = m;
    }


}
