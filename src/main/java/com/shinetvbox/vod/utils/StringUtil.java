package com.shinetvbox.vod.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	public static String skipChar(String str) {
		char[] cha = str.toCharArray();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < cha.length; i++) {
			char c = cha[i];
			if((c >= 'A' && c<= 'Z') || (c >= 'a' && c<= 'z')){
				buf.append(c);// 如果不是数字就追加到buf里
			}
		}
		return buf.toString();
	}
	
	public static String byte2hex(byte [] buffer, int size ){
        String h = "";
          
        for(int i = 0; i < size; i++){  
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if(temp.length() == 1){  
                temp = "0" + temp;  
            }  
            h = h + temp;  
        }  
        android.util.Log.d("peter", "byte2hex h="+h);
        return h;  
   
    }
	
	public static String binaryString2hexString(String bString)
	{
		if (bString == null || bString.equals("") || bString.length() % 8 != 0)
			return null;
		StringBuffer tmp = new StringBuffer();
		int iTmp = 0;
		for (int i = 0; i < bString.length(); i += 4)
		{
			iTmp = 0;
			for (int j = 0; j < 4; j++)
			{
				String s = bString.substring(i + j, i + j + 1);
				if(Hanzi2PinYin4j.isNumeric(s)) {
					iTmp += Integer.parseInt(s) << (4 - j - 1);
				}
			}
			tmp.append(Integer.toHexString(iTmp));
		}
		return tmp.toString();
	}
	
	/**数据库转义*/
	public static String sqliteEscape(String keyWord){
	    keyWord = keyWord.replace("/", "//");
	    keyWord = keyWord.replace("'", "''");
	    keyWord = keyWord.replace("[", "/[");
	    keyWord = keyWord.replace("]", "/]");
	    keyWord = keyWord.replace("%", "/%");
	    keyWord = keyWord.replace("&","/&");
	    keyWord = keyWord.replace("_", "/_");
	    keyWord = keyWord.replace("(", "/(");
	    keyWord = keyWord.replace(")", "/)");
	    return keyWord;
	}
	
	public static boolean compareString(String str1, String str2) {
		if((str1 == null || "".equals(str1)) && (str2 == null || "".equals(str2))) {
			return true;
		}
		
		if(str1 != null && str1.equals(str2)) {
			return true;
		} else {
			return false;
		}
	}
	
	//1用JAVA自带的函数
	public static boolean isNumeric(String str){
	  for (int i = str.length();--i>=0;){   
	   if (str.length() > 0 && !Character.isDigit(str.charAt(i))){
	    return false;
	   }
	  }
	  return true;
	 }

	//2用正则表达式
	public static boolean isNumericPt(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();    
	 } 

	
	/**去后缀*/
	public static String getStrSubstSuffix(String str){
		int num = str.indexOf('.');
		if(num>0){
			return str.substring(0, num);
		}
		return str;
	}
	/**获取后缀*/
	public static String getStrSuffix(String str){
		str = str.toUpperCase();
		int num = str.indexOf('.');
		if(str.length() > num){
			return str.substring(num+1);
		}
		return str;
	}
	/**删除换行符*/
	public static String replaceBlank(String src) {
		String dest = "";
		if (src != null) {
			Pattern pattern = Pattern.compile("\t|\r|\n|\\s*");
			Matcher matcher = pattern.matcher(src);
			dest = matcher.replaceAll("");
		}
		return dest;
	}

    /**
     * 数组反转
     * @param strs
     */
	public static void inverseString(String[] strs){
		for (int i = 0; i < strs.length; i++) {
			String temp=null;
			int j=strs.length-1-i;
			if(i<=j){
				temp=strs[i];
				strs[i]=strs[j];
				strs[j]=temp;
			}
		}
	}


	public static String getUTF8StringFromGBKString(String gbkStr) {
		try {
			return new String(getUTF8BytesFromGBKString(gbkStr), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new InternalError();
		}
	}

	public static byte[] getUTF8BytesFromGBKString(String gbkStr) {
		int n = gbkStr.length();
		byte[] utfBytes = new byte[3 * n];
		int k = 0;
		for (int i = 0; i < n; i++) {
			int m = gbkStr.charAt(i);
			if (m < 128 && m >= 0) {
				utfBytes[k++] = (byte) m;
				continue;
			}
			utfBytes[k++] = (byte) (0xe0 | (m >> 12));
			utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
			utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
		}
		if (k < utfBytes.length) {
			byte[] tmp = new byte[k];
			System.arraycopy(utfBytes, 0, tmp, 0, k);
			return tmp;
		}
		return utfBytes;
	}
}
