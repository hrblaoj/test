package com.shinetvbox.vod.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;

public class FileUtil {

	static {
		System.loadLibrary("native-lib");
	}
	//by Bati 2018-06-13, from Internet
	/**
	 * 复制单个文件
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
					//System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				fs.flush();
				fs.close();
				inStream.close();
			}
		}
		catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}
	}


	/** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
   public void copyFolder(String oldPath, String newPath) {

       try { 
           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
           File a=new File(oldPath);
           String[] file=a.list();
           File temp=null;
           for (int i = 0; i < file.length; i++) { 
               if(oldPath.endsWith(File.separator)){
                   temp=new File(oldPath+file[i]);
               } 
               else{ 
                   temp=new File(oldPath+ File.separator+file[i]);
               } 

               if(temp.isFile()){ 
                   FileInputStream input = new FileInputStream(temp);
                   FileOutputStream output = new FileOutputStream(newPath + "/" +
                           (temp.getName()).toString()); 
                   byte[] b = new byte[1024 * 5]; 
                   int len; 
                   while ( (len = input.read(b)) != -1) { 
                       output.write(b, 0, len); 
                   } 
                   output.flush(); 
                   output.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory()){//如果是子文件夹 
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); 
               } 
           } 
       } 
       catch (Exception e) {
           System.out.println("复制整个文件夹内容操作出错");
           e.printStackTrace(); 

       } 

   }
   
    /**截取.之后的字符串*/
	public static String getSuffix(String filename){
		int dix = filename.lastIndexOf('.');
		if(dix<0){
			return "";
		}else{
			return filename.substring(dix+1);
		}
	}
	
	public static boolean isMusicFile(String path)
	{
		if(path == null){
			return false;
		}
		try
		{
		String ext = getSuffix(path).toLowerCase();
		if(ext.equalsIgnoreCase("mp3") ||
/*				ext.equalsIgnoreCase("ogg") ||*/
				ext.equalsIgnoreCase("wav") ||
				ext.equalsIgnoreCase("wma") ||
/*				ext.equalsIgnoreCase("m4a") ||
                ext.equalsIgnoreCase("ape") ||*/
                ext.equalsIgnoreCase("dts") ||
/*                ext.equalsIgnoreCase("flac") ||
                ext.equalsIgnoreCase("mp1") ||
                ext.equalsIgnoreCase("mp2") ||*/
                ext.equalsIgnoreCase("aac") ||
                ext.equalsIgnoreCase("ac3") ||
                ext.equalsIgnoreCase("midi") ||
                ext.equalsIgnoreCase("mid")/* ||
                ext.equalsIgnoreCase("mp5") ||
                ext.equalsIgnoreCase("mpga") ||
                ext.equalsIgnoreCase("mpa") ||
				ext.equalsIgnoreCase("m4p") ||
				ext.equalsIgnoreCase("amr") ||
				ext.equalsIgnoreCase("m4r")*/)
		{
			return true;
		}
		}
		catch (IndexOutOfBoundsException e)
		{
			return false;
		}
		return false;
	}
	
	public static boolean isImageFile(String path)
	{
		try
		{
			String name = getSuffix(path).toLowerCase();
			if(
					//bmp,jpg,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw
					name.equalsIgnoreCase("bmp")||name.equalsIgnoreCase("jpg")||
					name.equalsIgnoreCase("tiff")||name.equalsIgnoreCase("gif")||
					name.equalsIgnoreCase("pcx")||name.equalsIgnoreCase("tga")||
					name.equalsIgnoreCase("exif")||name.equalsIgnoreCase("fpx")||
					name.equalsIgnoreCase("svg")||name.equalsIgnoreCase("psd")||
					name.equalsIgnoreCase("cdr")||name.equalsIgnoreCase("pcd")||
					name.equalsIgnoreCase("pcd")||name.equalsIgnoreCase("dxf")||
					name.equalsIgnoreCase("ufo")||name.equalsIgnoreCase("eps")||
					name.equalsIgnoreCase("ai")||name.equalsIgnoreCase("raw")
					
					)
			{
				return true;
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			return false;
		}
		
		return false;
	}
	
	public static boolean isMidiFile(String path){
		if(path==null) {
			return false;
		}
		return path.endsWith("mid") || path.endsWith("midi");
	}
	public static boolean isMovieFile(String path)
	{
		try
		{
			String ext = getSuffix(path).toLowerCase();
			if(ext.equalsIgnoreCase("avi") ||
					ext.equalsIgnoreCase("wmv") ||
					ext.equalsIgnoreCase("rmvb") ||
					ext.equalsIgnoreCase("mkv") ||
					ext.equalsIgnoreCase("okf") ||
					ext.equalsIgnoreCase("iso") ||
/*					ext.equalsIgnoreCase("m4v") ||
	                ext.equalsIgnoreCase("mov") ||*/
					ext.equalsIgnoreCase("mpg") ||
					ext.equalsIgnoreCase("rm")  ||
/*	                ext.equalsIgnoreCase("flv") ||
	                ext.equalsIgnoreCase("pmp") ||*/
					ext.equalsIgnoreCase("vob") ||
					ext.equalsIgnoreCase("vod") ||
/*	                ext.equalsIgnoreCase("dat") ||
	                ext.equalsIgnoreCase("asf") ||
	                ext.equalsIgnoreCase("psr") ||*/
					/*	                ext.equalsIgnoreCase("3gp") ||*/
					ext.equalsIgnoreCase("mpeg")||
/*	                ext.equalsIgnoreCase("ram") ||
	                ext.equalsIgnoreCase("divx") ||*/
/*	                ext.equalsIgnoreCase("m4p") ||
	                ext.equalsIgnoreCase("m4b") ||*/
					ext.equalsIgnoreCase("mp4")
				/*					ext.equalsIgnoreCase("f4v") ||*/
/*					ext.equalsIgnoreCase("3gpp") ||
					ext.equalsIgnoreCase("3g2") ||
					ext.equalsIgnoreCase("3gpp2") ||
					ext.equalsIgnoreCase("webm") ||
					ext.equalsIgnoreCase("ts") ||
					ext.equalsIgnoreCase("tp") ||
					ext.equalsIgnoreCase("m2ts") ||*/
/*					ext.equalsIgnoreCase("3dv") ||
					ext.equalsIgnoreCase("3dm") ||
					ext.equalsIgnoreCase("zip")*/)
			{
				return true;
			}
		}
		catch(IndexOutOfBoundsException e)
		{
			return false;
		}
		return false;
	}


	//by Bati 2018-06-15 from Internet, 创建文件夹， 创建文件并写入数据
	// 将字符串写入到文本文件中
	public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
		//生成文件夹之后，再生成文件，不然会出错
		makeFilePath(filePath, fileName);

		String strFilePath = filePath+fileName;
		// 每次写入时，都换行写
		String strContent = strcontent + "\r\n";
		try {
			File file = new File(strFilePath);
			if (!file.exists()) {
				Log.d("TestFile", "Create the file:" + strFilePath);
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rwd");
			raf.seek(file.length());
			raf.write(strContent.getBytes());
			raf.close();
		} catch (Exception e) {
			Log.e("TestFile", "Error on write File:" + e);
		}
	}

	// 生成文件
	public static File makeFilePath(String filePath, String fileName) {
		File file = null;
		makeRootDirectory(filePath);
		try {
			file = new File(filePath + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	public static void makeFileAndWriteData(String path, String filename, String data){
	    Log.i("222222222222222222",data+"_+++++++++++++"+path+filename );
		if (!data.equals( "" )) {
			try {
				FileWriter fw = new FileWriter(path+filename);
				fw.write(data);
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 生成文件夹
	public static void makeRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {
			Log.i("error:", e+"");
		}
	}

	/**
	 * 删除文件夹内所有文件
	 * @param dir
	 */
	public static void deleteAllFile(File dir, boolean inclThis) {
		if (dir == null || !dir.exists() || !dir.isDirectory() || dir.list() == null)
			return;
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteAllFile(file,true); // 递规的方式删除
		}
		if(inclThis){
			dir.delete();
		}
	}
	/**
	 * 删除单个文件
	 *
	 * @param fileName
	 *            要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

	public static ArrayList<String> ReadTxtFile(String strFilePath)
	{
		String path = strFilePath;
		ArrayList<String> newList=new ArrayList<String>();
		//打开文件
		File file = new File(path);
		//如果path是传递过来的参数，可以做一个非目录的判断
		if (file.isDirectory())
		{
			Log.d("TestFile", "The File doesn't not exist.");
		}
		else
		{
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null)
				{
					InputStreamReader inputreader = new InputStreamReader(instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					//分行读取
					while (( line = buffreader.readLine()) != null) {
						newList.add(line+"\n");
					}
					instream.close();
				}
			}
			catch (java.io.FileNotFoundException e)
			{
				Log.d("TestFile", "The File doesn't not exist.");
			}
			catch (IOException e)
			{
				Log.d("TestFile", e.getMessage());
			}
		}
		return newList;
	}

    public static String readTxtFileToString(String strFilePath){
	    String string = "";
        try {
            File file = new File(strFilePath);
            InputStream instream = new FileInputStream(file);
            if (instream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                while ((line = buffreader.readLine()) != null) {
					string += line;
                }
                instream.close();
            }
        } catch (Exception e) {
        }
        return string;
    }

	//判断文件是否存在
	static public boolean fileIsExists(String strFile)
	{
		try
		{
			File f=new File(strFile);
			if(!f.exists())
			{
				return false;
			}

		}
		catch (Exception e)
		{
			return false;
		}

		return true;
	}

	/**
	 * 字符串保存到手机内存设备中
	 *
	 * @param str
	 */
	public static void removeAndSaveFile(String str, String fileName) {
		// 创建String对象保存文件名路径
		try {
			// 创建指定路径的文件
			File file = new File(fileName);
			// 如果文件不存在
			if (file.exists()) {
				// 创建新的空文件
				file.delete();
			}
			file.createNewFile();
			// 获取文件的输出流对象
			FileOutputStream outStream = new FileOutputStream(file);
			// 获取字符串对象的byte数组并写入文件流
			outStream.write(str.getBytes());
			// 最后关闭文件输出流
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/*
	 * 获取文件扩展名
	 * */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	public static native boolean GetIniKeyString(String typeName, String KeyName, byte[] ReturnValue, String filename);


	//文件重命名
	public static boolean RenameFile(String _pSrcPath, String _pDstPath ){
		File file = new File(_pSrcPath);
		return file.renameTo(new File(_pDstPath));
	}


	//获取随机码 ， 包括数字,字母getRandomString(RANDOM_LEN, RandomString);
	static public String GetRandomString(int _pLength ){
		String randStr = "";

		Random random = new Random();

		for (int i = 0; i < _pLength; i++)
		{
			int flag = random.nextInt(2);
			switch (flag)
			{
				case 0: {
					int index = random.nextInt(25);
					randStr += (char) ('A' + index);
				}
				break;
				case 1: {
					int index = random.nextInt(25);
					randStr += (char) ('a' + index);
				}
				break;
				case 2: {
					int index = random.nextInt(9);
					randStr += (char)('0' + index);
				}
				break;
				default:
					randStr+='x';
					break;
			}
		}

		return randStr;
	}

	public static String GetValueFromFile(String strFilePath, String key, String operator){
		String value = "";

		if( !fileIsExists(strFilePath)){
			return value;
		}

		ArrayList<String> nArr = new ArrayList<String>();
		nArr = FileUtil.ReadTxtFile(strFilePath);

		for (  String ncontent: nArr   ) {
			if( ncontent.indexOf(key) >= 0){
				value = ncontent.substring(key.length()+ operator.length(), ncontent.length());
			}
		}
		if( value.endsWith("\r") || value.endsWith("\n") || value.endsWith(" ") ){
			value=value.substring(0, value.length()-"\n".length());
		}
		return value;
	}
	/**
	 * 获取cache路径
	 * @param context
	 * @return
	 */
	public static String getDiskCachePath(Context context) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			return context.getExternalCacheDir().getPath();
		} else {
			return context.getCacheDir().getPath();
		}
	}
}
