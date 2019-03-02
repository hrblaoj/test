package com.shinetvbox.vod.utils;

import android.util.Log;

public class ConversionsUtil {
    public static int stringToInteger(String str){
        int a = -1;
        try {
            a = Integer.parseInt( str.split( "\\." )[0] );
        }catch (Exception e){

        }
        return a;
    }
    public static float stringToFloat(String str){
        float a = -1;
        try {
            a = Float.parseFloat( str );
        }catch (Exception e){

        }
        return a;
    }
}
