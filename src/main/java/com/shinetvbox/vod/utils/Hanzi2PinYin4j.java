package com.shinetvbox.vod.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hanzi2PinYin4j {
	public static boolean hasHanZi(String src){
		if(src.length() < getPinYin(src).length()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 将汉字转换为全拼
	 * 大写：toUpperCase()
	 * 小写：toLowerCase()
	 * @param src
	 * @return String
	 */
	public static String getPinYin(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		// 设置汉字拼音输出的格式
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断能否为汉字字符
				// System.out.println(t1[i]);
				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
					t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
				} else {
					// 如果不是汉字字符，间接取出字符并连接到字符串t4后
					t4 += Character.toString(t1[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return t4;
	}

	/**
	 * 提取每个汉字的首字母
	 * 大写：toUpperCase()
	 * 小写：toLowerCase()
	 * @param str
	 * @return String
	 */
	public static String getPinYinHeadChar(String str) {
		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			// 提取汉字的首字母
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert.toUpperCase();//改变为大写
	}

	/**
	 * 将字符串转换成ASCII码
	 * 
	 * @param cnStr
	 * @return String
	 */
	public static String getCnASCII(String cnStr) {
		StringBuffer strBuf = new StringBuffer();
		// 将字符串转换成字节序列
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			// 将每个字符转换成ASCII码
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}
	
	/**判断字符串是否全部是汉字*/
	public static boolean isAllHanZhi(String cnStr){
		char[] t1 = null;
		t1 = cnStr.toCharArray();
		for(char c : t1) {
			if (!Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
				return false;
			}
		}
		return true;
	}
	
	/**判断字符串是否全部是大写拼音*/
	public static boolean isAllSPELL(String cnStr){
		char[] t1 = null;
		t1 = cnStr.toCharArray();
		for(char c : t1) {
			if (!Character.isUpperCase(c)){
				return false;
		    }
		}
		return true;
	}
	
	/**计算所有拼音*/
	public static String countSpell(String cnStr){
		String spell = "";
		char[] t1 = null;
		t1 = cnStr.toCharArray();
		for(char c : t1) {
//			LogUtil.e("计算拼音："+Character.toString(c));
			if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {//是否汉字
				spell+=getPinYinHeadChar(Character.toString(c));
			}else if (Character.isUpperCase(c) || Character.isLowerCase(c) ){//获取大写和小写字母
				spell+=c;
			}
		}
//		LogUtil.e("计算拼音-结果："+spell);
		return spell;
	}
	
	/**计算首字母拼音*/
	public static String countInitialSpell(String cnStr){
		String spell = "";
		char[] t1 = null;
		t1 = cnStr.toCharArray();
		for(char c : t1) {
//			LogUtil.e("计算首字母拼音："+Character.toString(c));
			if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {//是汉字
				spell = getPinYinHeadChar(Character.toString(c));
				break;
			}else if (Character.isUpperCase(c) || Character.isLowerCase(c) ){//是大写和小写字母
				spell = Character.toString(c);
				break;
			}
		}
//		LogUtil.e("计算首字母拼音-结果："+spell);
		return spell;
	}
	
	
	
	/**去掉这些符号之后的*/
	public static String getSubst(String name){
		if(name.contains("-")){
			name = name.substring(0, name.indexOf("-"));
		}
		if(name.contains("[")){
			name = name.substring(0, name.indexOf("["));
		}
		if(name.contains("(")){
			name = name.substring(0, name.indexOf("("));
		}
		return name;
	}
	
	/**根据歌星名字或者歌曲名字获取拼音首字母*/
	public static String getSpell(String str){
		String name = getSubst(str);
		
		String spell = "";
		String[] names = name.split(" ");
		if(names.length>1){
			for (int i = 0; i < names.length; i++) {
				//获取首字拼音
				String string = names[i];
				if(string.length() >= 1){
					spell += countInitialSpell(string.substring(0, 1));
				}
			}
		}else{
			//获取一串拼音
			if(name.length() >= 1){
				spell = countSpell(name);
			}
		}
		return spell;
	}
	

	/**获取汉字数量*/
	public static int getHanZhiCount(String cnStr){
		int count = 0;
		char[] t1 = null;
		t1 = cnStr.toCharArray();
		for(char c : t1) {
			if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
				count++;
			}
		}
		return count;
	}
	
	/**获取大写字母数量*/
	public static int getUpperAcronymCount(String word){
		int count = 0;
		for(int i = 0; i < word.length(); i++){
		    char c = word.charAt(i);
		    if (Character.isUpperCase(c)){//统计大写字母
		        count++;
		    }
		}
		return count;
	}
	
	/**计算字数*/
	public static int countZS(String cnStr){
		int count = 0; 
		char[] t1 = null;
		t1 = cnStr.toCharArray();
		for(char c : t1) {
			if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {//是汉字
				count++;
				break;
			}else if (Character.isUpperCase(c) || Character.isLowerCase(c)){//是大写或小写字母
		        count++;
		        break;
			}
		}
		if(count == 0){
			count = cnStr.length();
		}
		return count;
	}
	
	/**根据歌星名或者歌曲名字，获取字数*/
	public static int getZS(String str){
		String name = getSubst(str);
		
		int zs = 0;
		String[] names = name.split(" ");
		if(names.length>1){
			//获取首字符数量
			for (int i = 0; i < names.length; i++) {
				//获取首字符累加数量
				String string = names[i];
				if(string.length() >= 1){
					zs += countZS(string.substring(0, 1));
				}
			}
		}else{
			//获取一串字符数量
			if(isAllHanZhi(name)){
				zs = name.length();
			}else{
				zs = countSpell(name).length();
			}
		}
		return zs;
	}
	
	/**
	 * 是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if(!isNum.matches() ){   
           return false;    
        }    
        return true;    
	}   
}