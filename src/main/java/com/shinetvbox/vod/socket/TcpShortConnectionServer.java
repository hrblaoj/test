package com.shinetvbox.vod.socket;


import com.shinetvbox.vod.utils.KtvLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.shinetvbox.vod.socket.MessageType.GBK_CODE;


/**
 * Created by hrblaoj on 2018/11/1.
 * 没有边界处理，数据是否完整完全靠运气，以后改吧
 */

public class TcpShortConnectionServer  extends MessageProcss{
    public static Map<Integer, TcpShortConnectionServer> connectionMap = new HashMap<Integer, TcpShortConnectionServer>();
    myServerSocket mServerSocket = null;
    public static int threadsize = 1;
    ExecutorService mScoketProcThread = Executors.newFixedThreadPool(threadsize);
    int mPort;

    interface eventProcess{
        void onRevice();
        void onSend();
    }


    public TcpShortConnectionServer mIntance = null;

    public static TcpShortConnectionServer getIntanceByPort(int port){
        return connectionMap.get(port);
    }
    class myServerSocket extends ServerSocket {

        public myServerSocket(int port) throws IOException {
            super(port);
        }

        @Override
        public void close(){
            try {
                super.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addMessageType(MessageType m){
        this.MessageTypeList.add(m);
    }

    public TcpShortConnectionServer(int port){
        KtvLog.d("TcpShortConnectionServer");
        mPort = port;
        mIntance = this;
        connectionMap.put(port, this);
//        MessageTypeList.add(new MessageType("", GBK_CODE, new MessageType.onProcess(){
//
//            @Override
//            public void onProcessHead() {
//
//            }
//
//            @Override
//            public void onProcessBody(String str) {
//
//            }
//        }));
    }

    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    mServerSocket = new myServerSocket(mPort);
                    mServerSocket.setReuseAddress(true);

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                finally {
                    if(null == mServerSocket)
                        return;
                }

                Socket acSocket = null;

                while (true){
                    try {
                        acSocket = mServerSocket.accept();
                        KtvLog.d("acSocket accept");
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    if(null != acSocket){
                        mScoketProcThread.execute(new socketProcessRunnable(acSocket));
                    }
                }

            }
        } ).start();

    }

    public class socketProcessRunnable implements Runnable {
        int soTime = 50000;
        Socket mSocket;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        public socketProcessRunnable(Socket msocket){
            mSocket = msocket;
            try {
                mSocket.setSoTimeout(soTime);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        public boolean sendAndRecv(byte[] sendbuf){
            try {
                outputStream.write(sendbuf, 0, sendbuf.length);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            }
            finally {
//                if(null != outputStream)
//                    try {
//                        outputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
            }

            byte buffer[] = new byte[sendbuf.length];
            int temp = 0;
            try {
                if((temp = inputStream.read(buffer)) != -1) {
                    KtvLog.d("yyyy222 temp is " + temp);
                    if(temp == sendbuf.length){
                        return true;
                    }
                }
            } catch (IOException e) {
                KtvLog.d("yyyy333 temp is " + temp);
                e.printStackTrace();
            }

            return false;
        }

        public void close(){
            if(null != reader)
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            if(null != inputStreamReader)
                try {
                    inputStreamReader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            if(null != inputStream)
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            if(null != outputStream)
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            if(null != mSocket)
                try {
                    mSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
        @Override
        public void run() {
            try {
                    //可能有问题，不用，貌似需要对端close，readLine才能返回，以后有机会研究明白这一套的机制
//                KtvLog.d("服务端tcp 11");
//                reader = new BufferedReader(inputStreamReader = (new InputStreamReader(inputStream = mSocket.getInputStream(),"GBK")));
//                String str = null;
//                KtvLog.d("服务端tcp 22");
//                while((str = reader.readLine()) != null) {
//                    KtvLog.d("服务端tcp is " + str+ " str len is " + str.length());
//                }
//                KtvLog.d("服务端tcp 33");


                //字节流
                inputStream = mSocket.getInputStream();
                outputStream = mSocket.getOutputStream();
                byte buffer[] = new byte[1024 * 4];
                int temp = 0;
                // 从InputStream当中读取客户端所发送的数据
                KtvLog.d("服务端tcp 11111");
//                while ((temp = inputStream.read(buffer)) != -1) {
                if((temp = inputStream.read(buffer)) != -1){
                    KtvLog.d("服务端tcp 0.50.5");
                    KtvLog.d("yyyyy is " + temp);
                    //System.out.println(new String(buffer, 0, temp));
                    MessageType type = dispatchTask(buffer);
                    if(null != type && null != type.monProcess) {
                        if(type.processType.equals(type.STRING_TYPE) )
                            type.monProcess.onProcessBody(new String(buffer, 0, temp, type.messageCode), this);
                        else
                            type.monProcess.onProcessBody(buffer, this);
                    }
                }
                KtvLog.d("服务端tcp 22222");

                //字符流
//                char chars[] = new char[64];
//                inputStreamReader = (new InputStreamReader(inputStream = mSocket.getInputStream(),"GBK"));
//                inputStreamReader.read(chars);
//                KtvLog.d("服务端tcp 22222 is " + new String(chars));

            }
            catch (IOException e) {
                e.printStackTrace();
                KtvLog.d("服务端tcp 444");
            }
            finally {
                close();
            }

        }
    }
}
