package com.shinetvbox.vod.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Util {

	 /**
	   * 获取单个文件的MD5值！
	  * @param file
	   * @return
	   */

	 public static String getFileMD5(File file) {
		   if (!file.isFile()) {
		    return null;
		   }
		   MessageDigest digest = null;
		   FileInputStream in = null;
		   byte buffer[] = new byte[1024];
		   int len;
		   try {
			    digest = MessageDigest.getInstance("MD5");
			    in = new FileInputStream(file);
			    while ((len = in.read(buffer, 0, 1024)) != -1) {
			    	digest.update(buffer, 0, len);
			    }
			    in.close();
		   } catch (Exception e) {
			    e.printStackTrace();
			    return null;
		   }
		   BigInteger bigInt = new BigInteger(1, digest.digest());
		   return bigInt.toString(16);
	  }


	    /*** 
	     * MD5加码 生成32位md5码 
	     */  
	    public static String string2MD5(String inStr){
	    	if (inStr == null) return "";
	        MessageDigest md5 = null;
	        try{  
	            md5 = MessageDigest.getInstance("MD5");
	        }catch (Exception e){
	            System.out.println(e.toString());
	            e.printStackTrace();  
	            return "";  
	        }  
	        char[] charArray = inStr.toCharArray();  
	        byte[] byteArray = new byte[charArray.length];  
	  
	        for (int i = 0; i < charArray.length; i++)  
	            byteArray[i] = (byte) charArray[i];  
	        byte[] md5Bytes = md5.digest(byteArray);  
	        StringBuffer hexValue = new StringBuffer();
	        for (int i = 0; i < md5Bytes.length; i++){  
	            int val = ((int) md5Bytes[i]) & 0xff;  
	            if (val < 16)  
	                hexValue.append("0");  
	            hexValue.append(Integer.toHexString(val));
	        }  
	        return hexValue.toString();  
	  
	    }  
	  
	    /** 
	     * 加密解密算法 执行一次加密，两次解密 
	     */   
	    public static String convertMD5(String inStr){
	        char[] a = inStr.toCharArray();  
	        for (int i = 0; i < a.length; i++){  
	            a[i] = (char) (a[i] ^ 't');  
	        }  
	        String s = new String(a);
	        return s;  
	    }  
	  
	    // 测试主函数  
	    public static void main(String args[]) {
	        String s = new String("tangfuqiang");
	        System.out.println("原始：" + s);
	        System.out.println("MD5后：" + string2MD5(s));
	        System.out.println("加密的：" + convertMD5(s));
	        System.out.println("解密的：" + convertMD5(convertMD5(s)));
	  
	    }  
	    
}
