package com.shinetvbox.vod.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * ini文件工具类<br>
 * example:<br>
 * IniFile file = new IniFile(new File("D:/dev/idressworkmobile/ztest/src/ztest/FacePositive.ini"));<br>
 * System.out.println(file.get("Config0", "PoinX0"));  <br>
 * //      file.save(new File("D:/c.ini"));  <br>
 * file.remove("ModelFace");  <br>
 * file.save();<br>
 * IniFile file2 = new IniFile();  <br>
 * file2.set("Config", "属性1", "值1");  <br>
 * file2.set("Config", "属性2", "值2");  <br>
 * file2.save(new File("d:/d.ini"));  <br>
 */
public class IniUtil {
    /**
     * 点节
     */
    public class Section {

        private String name;
        private Map<String, Object> values = new LinkedHashMap<String, Object>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        public void setDescribe(String key) {
            values.put(key, "describe");
        }
        public void set(String key, Object value) {
            values.put(key, value);
        }

        public Object get(String key) {
            return values.get(key);
        }

        public Map<String, Object> getValues() {
            return values;
        }

    }

    /**换行符*/
    private String line_separator = null ;
    /**编码*/
    private String charSet = "GBK";
    /**存储数据*/
    private Map<String, Section> sections = new LinkedHashMap<String, Section>();

    /**
     * 指定换行符
     * @param line_separator
     */
    public synchronized void setLineSeparator(String line_separator){
        this.line_separator = line_separator;
    }

    /**
     * 指定编码
     * @param charSet
     */
    public synchronized void setCharSet(String charSet){
        this.charSet = charSet;
    }

    /**
     * 设置值
     * @param section 节点
     * @param key 属性名
     * @param value 属性值
     */
    public synchronized void set(String section, String key, Object value) {
        Section sectionObject = sections.get(section);
        if (sectionObject == null){
            sectionObject = new Section();
        }
        sectionObject.name = section;
        sectionObject.set(key, value);
        sections.put(section, sectionObject);
    }

    /**
     * 获取节点
     * @param section 节点名称
     * @return
     */
    public synchronized Section get(String section){
        return sections.get(section);
    }

    /**
     * 获取值
     * @param section 节点名称
     * @param key 属性名称
     * @return
     */
    public synchronized Object get(String section, String key) {
        return get(section, key, "");
    }

    /**
     * 获取值
     * @param section 节点名称
     * @param key 属性名称
     * @param defaultValue 如果为空返回默认值
     * @return
     */
    public synchronized Object get(String section, String key, String defaultValue) {
        Section sectionObject = sections.get(section);
        if (sectionObject != null) {
            Object value = sectionObject.get(key);
            if (value == null || value.toString().trim().equals("")){
                return defaultValue;
            }
            return value;
        }
        return defaultValue;
    }

    /**
     * 删除节点
     * @param section 节点名称
     */
    public synchronized void remove(String section){
        sections.remove(section);
    }

    /**
     * 删除属性
     * @param section 节点名称
     * @param key 属性名称
     */
    public synchronized void remove(String section, String key){
        Section sectionObject = sections.get(section);
        if(sectionObject!=null)sectionObject.getValues().remove(key);
    }

    /** 当前操作的文件对像 */
    private File file = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public IniUtil(){

    }

    public IniUtil(String filePath) {
        load(filePath);
    }

    public IniUtil(InputStream inputStream) {
        load(inputStream);
    }
    /**
     * 加载一个ini文件
     * @param filePath
     */
    public synchronized void load(String filePath) {
        this.file = new File( filePath );
        try {
            inputStream = new FileInputStream( file );
            initFromInputStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 加载一个ini文件
     * @param inputStream
     */
    public synchronized void load(InputStream inputStream) {
        initFromInputStream(inputStream);
    }
    /**
     * 保存到当前文件
     */
    public synchronized void save(){
        saveFile(this.file);
    }
    /**
     * 保存到文件
     * @param filePath
     */
    public synchronized void save(String filePath){
        saveFile( new File( filePath ) );
    }
    private void saveFile(File saFile){
        try {
            outputStream = new FileOutputStream( saFile );
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,charSet));
            saveConfig(bufferedWriter);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从输入流初始化IniFile
     * @param inputStream
     */
    private void initFromInputStream(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,charSet));
            toIniFile(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从BufferedReader 初始化IniFile
     * @param bufferedReader
     */
    private void toIniFile(BufferedReader bufferedReader) {
        String strLine;
        Section section = null;
        Pattern p = Pattern.compile("^\\[.*\\]$");
        try {
            while ((strLine = bufferedReader.readLine()) != null) {
                if (p.matcher((strLine)).matches()) {
                    strLine = strLine.trim();
                    section = new Section();
                    section.name = strLine.substring(1, strLine.length() - 1);
                    sections.put(section.name, section);
                } else {
                    if(section != null){
                        if (strLine.indexOf( ";" ) != 0 && strLine.indexOf( "#" ) != 0 && strLine.indexOf( "=" ) != -1) {
                            String[] keyValue = strLine.split("=");
                            if(keyValue.length >= 2){
                                String str = keyValue[1];
                                for (int i = 2; i < keyValue.length; i++) {
                                    str += "="+keyValue[i];
                                }
                                section.set(keyValue[0].trim(), str);
                            }else{
                                section.set(keyValue[0].trim(), "");
                            }
                        }else{
                            section.setDescribe(strLine);
//                            Log.i( "shinektv","---"+strLine );
                        }
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存Ini文件
     * @param bufferedWriter
     */
    private void saveConfig(BufferedWriter bufferedWriter){
        try {
            boolean line_spe = false;
            if(line_separator == null || line_separator.trim().equals(""))line_spe = false;
            for (Section section : sections.values()) {
                bufferedWriter.write("["+section.getName()+"]");
                if(line_spe)
                    bufferedWriter.write(line_separator);
                else
                    bufferedWriter.newLine();
                for (Map.Entry<String, Object> entry : section.getValues().entrySet()) {
                    if(entry.getValue().toString().equals( "describe" )){
                        bufferedWriter.write(entry.getKey());
                    }else{
                        bufferedWriter.write(entry.getKey());
                        bufferedWriter.write("=");
                        bufferedWriter.write(entry.getValue().toString());
                    }
                    if(line_spe)
                        bufferedWriter.write(line_separator);
                    else
                        bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void clear(){
        sections.clear();
    }
}