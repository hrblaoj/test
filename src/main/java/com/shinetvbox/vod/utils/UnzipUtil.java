package com.shinetvbox.vod.utils;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUtil {

    private static final int BUFFER = 4096; //这里缓冲区我们使用4KB，
    /**解压完成*/
    public static final int UNZIP_OVER = 0;
    /**解压错误*/
    public static final int UNZIP_ERROR = 1;

    public static void UnzipMainThread(String zipFile, String targetDir) {
        {
            String strEntry; //保存每个zip的条目名称
            try {
                BufferedOutputStream dest = null; //缓冲输出流
                FileInputStream fis = new FileInputStream(zipFile);
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
                ZipEntry entry; //每个zip条目的实例

                while ((entry = zis.getNextEntry()) != null) {
                    try {
                        //Log.i("Unzip: ","Unzip list name = "+ entry);
                        int count;
                        byte data[] = new byte[BUFFER];
                        strEntry = entry.getName();

                        File entryFile = new File(targetDir + strEntry);
                        File entryDir = new File(entryFile.getParent());
                        if (!entryDir.exists()) {
                            entryDir.mkdirs();
                        }

                        FileOutputStream fos = new FileOutputStream(entryFile);
                        dest = new BufferedOutputStream(fos, BUFFER);
                        while ((count = zis.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                        fos.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                zis.close();
                fis.close();
            } catch (Exception cwj) {
                cwj.printStackTrace();
            }
        }
    }
    public static void Unzip(final String zipFile, final String targetDir) {
        Unzip(zipFile,targetDir,null);
    }
    public static void Unzip(final String zipFile, final String targetDir, final Handler handler) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                String strEntry; //保存每个zip的条目名称
                try {
                    BufferedOutputStream dest = null; //缓冲输出流
                    FileInputStream fis = new FileInputStream(zipFile);
                    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
                    ZipEntry entry; //每个zip条目的实例

                    while ((entry = zis.getNextEntry()) != null) {
                        try {
//                            Log.i("Unzip: ","Unzip list name = "+ entry);
                            int count;
                            byte data[] = new byte[BUFFER];
                            strEntry = entry.getName();

                            File entryFile = new File(targetDir + strEntry);
                            File entryDir = new File(entryFile.getParent());
                            if (!entryDir.exists()) {
                                entryDir.mkdirs();
                            }

                            FileOutputStream fos = new FileOutputStream(entryFile);
                            dest = new BufferedOutputStream(fos, BUFFER);
                            while ((count = zis.read(data, 0, BUFFER)) != -1) {
                                dest.write(data, 0, count);
                            }

                            dest.flush();
                            dest.close();
                            fos.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    zis.close();
                    fis.close();
                    if(handler!=null){
                        handler.sendEmptyMessage( UNZIP_OVER );
                    }
                } catch (Exception cwj) {
                    cwj.printStackTrace();
                    if(handler!=null){
                        handler.sendEmptyMessage( UNZIP_ERROR );
                    }
                }
            }
        } ).start();
    }
    public static List<String> getNameList(String fileStr) {
        List<String> list = new ArrayList<>(  );
        try {
            FileInputStream fis = new FileInputStream(fileStr);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; //每个zip条目的实例
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    list.add( entry.getName() );
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();
            fis.close();
        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
        return list;
    }
    public static List<String> getPathList(String fileName, String path) {
        List<String> list = new ArrayList<>(  );
        try {
            FileInputStream fis = new FileInputStream(path+fileName);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; //每个zip条目的实例
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    list.add( path+entry.getName() );
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();
            fis.close();
        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
        return list;
    }
}
