package com.shinetvbox.vod.socket;

import com.shinetvbox.vod.utils.KtvLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hrblaoj on 2019/1/11.
 */

public class TcpShortConnectionClient {
    public static int threadsize = 1;
    ExecutorService mScoketProcThread = Executors.newFixedThreadPool(threadsize);
    public TcpShortConnectionClient mIntance = null;
    public static Map<Integer, TcpShortConnectionClient> connectionMap = new HashMap<Integer, TcpShortConnectionClient>();
    int mPort;
    public static TcpShortConnectionClient getIntanceByPort(int port){
        return connectionMap.get(port);
    }

    public TcpShortConnectionClient(int port){
        mPort = port;
        mIntance = this;
        connectionMap.put(port, this);
    }

    public interface onSendProcess{
        void onFailed();
    }

    int soTime = 50000;
    public class SendRunnable implements Runnable{
        byte[] send;
        String ip = "";
        onSendProcess tSendProcess = null;
        public SendRunnable(byte[] sendBuf, String connectIp){
            send = sendBuf;
            ip = connectIp;
        }

        public SendRunnable(byte[] sendBuf, String connectIp, onSendProcess sendProcess){
            send = sendBuf;
            ip = connectIp;
            tSendProcess = sendProcess;
        }

        @Override
        public void run() {
            Socket so =  new Socket();
            OutputStream outputStream = null;
            InputStream inputStream = null;
            if(null == so)
                return;
            try {
                so.connect(new InetSocketAddress(ip, mPort), 50000);
                so.setSoTimeout(soTime);
                outputStream = so.getOutputStream();
                outputStream.write(send, 0, send.length);
                outputStream.flush();
                if(null == tSendProcess)
                    return;
                inputStream = so.getInputStream();
                byte[] recv = new byte[8];
                int recvnum = 0;

                if(recv.length == (recvnum = inputStream.read(recv))){
                    KtvLog.d("length recvnumrecvnum is " + recvnum );
                    outputStream.write(recv, 0, recv.length);
                    outputStream.flush();
                }
                else {
                    KtvLog.d("recvnumrecvnum is " + recvnum);
//                    outputStream.write(recv, 0, recv.length);
//                    outputStream.flush();
                    tSendProcess.onFailed();
                }


            } catch (IOException e) {
                e.printStackTrace();
                if(null == tSendProcess)
                    tSendProcess.onFailed();

            }
            finally {
                if(null != outputStream)
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if(null != inputStream)
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if(null != so)
                    try {
                        so.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return;
        }
    }

    public void execute(SendRunnable run){
        mScoketProcThread.execute(run);
    }
}
