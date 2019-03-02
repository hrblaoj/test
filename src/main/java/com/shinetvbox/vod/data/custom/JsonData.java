package com.shinetvbox.vod.data.custom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonData {

    private Section mySection = new Section();
    private boolean isProtect = true;

    /**
     * 解析Json数据
     * @param data json数据
     */
    public JsonData(String data) {
        initData(data);
    }

    /**
     * 解析Json数据
     * @param data json数据
     * @param noProtect 是否保护程序不崩溃（默认true，当为false时未查找到数据时会崩溃）
     */
    public JsonData(String data,boolean noProtect) {
        isProtect = noProtect;
        initData(data);
    }
    private void initData(String data){
        if(data.equals( "" ) && !data.contains( "{" ) && !data.contains( "}" )) return;
        try {
            analyzeJson(new JSONObject( data ),mySection);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void analyzeJson(JSONObject jsonObject,Section section){
        while (jsonObject.keys().hasNext()){
            String key = jsonObject.keys().next();
            try {
                if(!jsonObject.get( key ).equals( null )){
                    if(jsonObject.get( key ) instanceof JSONObject){
                        Section sect = new Section();
                        section.set( key,sect );
                        analyzeJson( (JSONObject) jsonObject.get( key ),sect);
                    }else{
                        section.set( key,""+jsonObject.getString( key ));
                    }
                }else{
                    section.set( key,"");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonObject.remove( key );
        }
    }

    /**
     * 检测是否有此节点
     * @param key
     * @return
     */
    public boolean hasKey(String key){
        return mySection.hasKey( key );
    }
    /**
     * 获取节点
     * @param key
     * @return
     */
    public Section getSection(String key){
        return mySection.getSection( key );
    }
    /**
     * 获取字符串类型数据
     * @param key
     * @return
     */
    public String getValueString(String key){
        return mySection.getValueString( key );
    }
    /**
     * 获取Object数组
     * @param key
     * @return
     */
    public Object[] getValueArray(String key){
        return mySection.getValueArray( key);
    }
    /**
     * 获取String数组
     * @param key
     * @return
     */
    public String[] getValueStringArray(String key){
        return mySection.getValueStringArray( key );
    }
    /**
     * 获取数字类型数据
     * @param key
     * @return
     */
    public int getValueInt(String key){
        return mySection.getValueInt( key );
    }

    /**
     * 获取浮点类型数据
     * @param key
     * @return
     */
    public float getValueFloat(String key){
        return mySection.getValueFloat( key );
    }

    /**
     * 获取long类型数据
     * @param key
     * @return
     */
    public long getValueLong(String key){
        return mySection.getValueLong( key );
    }
    /**
     * 获取数字类型数据
     * @param key
     * @param radix (十六进制为16，二进制为2)
     * @return
     */
    public int getValueInt(String key,int radix){
        return mySection.getValueInt( key,radix );
    }

    /**
     * 获取布尔类型数据
     * @param key
     * @return
     */
    public boolean getValueBoolean(String key){
        return mySection.getValueBoolean( key );
    }

    /**
     * 点节
     */
    public class Section {

        private Map<String, Object> values = new HashMap<>();

        public void set(String key, Object value) {
            values.put(key, value);
        }

        /**
         * 获取节点
         * @param key
         * @return
         */
        public Section getSection(String key) {
            Section sect = new Section();
            if(!isProtect){
                sect = null;
            }
            if(containsKey( key ) && values.get(key) instanceof Section){
                sect = (Section) values.get(key);
            }
            return sect;
        }

        /**
         * 获取字符串类型数据
         * @param key
         * @return
         */
        public String getValueString(String key) {
            String str = "";
            if(containsKey( key ) && values.get(key) instanceof String){
                str = (String) values.get( key );
            }
            return str;
        }
        public boolean containsKey( String key ){
            return values.containsKey( key );
        }

        /**
         * 获取数字类型数据
         * @param key
         * @return
         */
        public int getValueInt(String key) {
            int in = 0;
            if(containsKey( key ) && values.get(key) instanceof String && !values.get(key).equals( "" )){
                try {
                    in = Integer.parseInt( (String) values.get( key ) );
                }catch (Exception e){}

            }
            return in;
        }

        /**
         * 获取数字类型数据
         * @param key
         * @return
         */
        public float getValueFloat(String key) {
            float in = 0;
            if(containsKey( key ) && values.get(key) instanceof String && !values.get(key).equals( "" )){
                try {
                    in = Float.parseFloat( (String) values.get( key ) );
                }catch (Exception e){}

            }
            return in;
        }

        /**
         * 获取long类型数据
         * @param key
         * @return
         */
        public long getValueLong(String key) {
            long in = 0;
            if(containsKey( key ) && values.get(key) instanceof String && !values.get(key).equals( "" )){
                try {
                    in = Long.parseLong( (String) values.get( key ) );
                }catch (Exception e){}
            }
            return in;
        }
        /**
         * 获取数字类型数据
         * @param key
         * @param radix (十六进制为16，二进制为2)
         * @return
         */
        public int getValueInt(String key,int radix) {
            int in = 0;
            if(containsKey( key ) && values.get(key) instanceof String && !values.get(key).equals( "" )){
                String str = (String) values.get( key );
                str = str.replace( "#","" );
                str = str.replace( "0x","" );
                try {
                    in = Integer.parseInt( str ,radix );
                }catch (Exception e){}
            }
            return in;
        }

        /**
         * 获取布尔类型数据
         * @param key
         * @return
         */
        public boolean getValueBoolean(String key) {
            boolean bo = false;
            if(containsKey( key ) && values.get(key) instanceof String && !values.get(key).equals( "" )){
                if(((String) values.get( key )).equals( "true" )){
                    bo = true;
                }
            }
            return bo;
        }
        /**
         * 获取Object数组
         * @param key
         * @return
         */
        public Object[] getValueArray(String key) {
            if(containsKey( key ) && values.get(key) instanceof String){
                try {
                    JSONArray jsonArray = new JSONArray( (String) values.get( key ) );
                    Object[] dd = new Object[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dd[i]=jsonArray.get( i );
                    }
                    return dd;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return new Object[0];
        }
        /**
         * 获取String数组
         * @param key
         * @return
         */
        public String[] getValueStringArray(String key) {
            if(containsKey( key ) && values.get(key) instanceof String){
                try {
                    JSONArray jsonArray = new JSONArray( (String) values.get( key ) );
                    String[] dd = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dd[i]=jsonArray.getString( i );
                    }
                    return dd;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return new String[0];
        }

        public boolean hasKey(String key) {
            return containsKey( key );
        }
    }
}
