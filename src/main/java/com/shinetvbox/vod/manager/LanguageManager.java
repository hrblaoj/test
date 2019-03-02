package com.shinetvbox.vod.manager;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LanguageManager {

	public static String currentLang = "zh_cn";
	//---------------语言名字key---------------
	public static final String ZH_CN = "zh_cn";
	public static final String ZH_CNF = "zh_tw";
	public static final String EN_US = "en_us";
	public static final String JA_JP = "ja_jp";
	public static final String KO_KR = "ko_kr";
	//柬埔寨km_KH
	public static final String KM_KH = "km_kh";
	//缅甸语my_MM( 用捷克语模拟cs_CZ)
	public static final String MY_MM = "my_mm";
	//印度尼西亚语in_ID
	public static final String IN_ID = "in_id";
	//泰国th_TH
	public static final String TH_TH = "th_th";
    //越南语vi_VN
    public static final String VI_VN = "vi_vn";
//	//法语fr_FR
//	public static final String FR_FR = "fr_fr";
//	//菲律宾语tl_PH
//	public static final String TL_PH = "tl_ph";
//	//马来西亚语ms_MY
//	public static final String MS_MY = "ms_my";
////	public static final String CS_CZ = "cs_cz";
//	//老挝语lo_LA
//	public static final String LO_LA = "lo_la";
//	//俄语ru_RU
//	public static final String RU_RU = "ru_ru";
//	//印度
//	public static final String HI_IN = "hi_in";
	
	private static Map<String, Locale> languageMap = new HashMap<String, Locale>();
	private static List<String> listValidLanaguage = new ArrayList<>(  );

	public static Locale getLocale(String lankey){
		return languageMap.get(lankey);
	}

	public static boolean isSupportLanaguage(String langKey){
		return listValidLanaguage.contains( langKey );
	}

	public static void initAllLocale(){
		languageMap.clear();
		listValidLanaguage.clear();
		final String[] locales = Resources.getSystem().getAssets().getLocales();

//		languageMap.put(ZH_CN, Locale.CHINA);
//		languageMap.put( ZH_CNF, Locale.TAIWAN);
//		languageMap.put(EN_US, Locale.ENGLISH);
//		languageMap.put(KO_KR, Locale.KOREA);
//		languageMap.put(JA_JP, Locale.JAPAN);
		languageMap.put(ZH_CN, getLan(ZH_CN, locales));
		languageMap.put( ZH_CNF, getLan(ZH_CNF, locales));
		languageMap.put(EN_US, getLan(EN_US, locales));
		languageMap.put(KO_KR, getLan(KO_KR, locales));
		languageMap.put(JA_JP, getLan(JA_JP, locales));
        languageMap.put(TH_TH, getLan(TH_TH, locales));
        languageMap.put(MY_MM, getLan(MY_MM, locales));
		languageMap.put(KM_KH, getLan(KM_KH, locales));
        languageMap.put(VI_VN, getLan(VI_VN, locales));
        languageMap.put(IN_ID, getLan(IN_ID, locales));

//		languageMap.put(FR_FR, getLan(FR_FR, locales));
//		languageMap.put(TL_PH, getLan(TL_PH, locales));
//		languageMap.put(MS_MY, getLan(MS_MY, locales));
//		languageMap.put(LO_LA, getLan(LO_LA, locales));
//		languageMap.put(RU_RU, getLan(RU_RU, locales));
//		languageMap.put(HI_IN, getLan(HI_IN, locales));
	}

	private static Locale getLan(String lanKey, String[] locales){
    	for (int i = 0 ; i < locales.length; i++ ) {
            final String s = locales[i];
            final int len = s.length();
            if (len == 5) {
                String language = s.substring(0, 5);
                String country = s.substring(3, 5);
                Locale l = new Locale(language, country);
                if(isLangEqualsKey(l.getLanguage(),lanKey)){
                	listValidLanaguage.add( lanKey );
                	 return new Locale(s.substring(0, 2), s.substring(3, 5));
                }
            }
    	}
    	return Locale.CHINA;
    }
    private static boolean isLangEqualsKey(String lang, String langKey){
		Map<String,String> map = new HashMap<>(  );
		String lank = langKey.replace( "_","" );
		String lan = lang.replace( "-","" ).replace( "_","" );
//        Log.i( "22222222222222l",(lank.equals( lan ))+"----------"+lang+"---------"+langKey );
		return lank.equals( lan );
	}
}
