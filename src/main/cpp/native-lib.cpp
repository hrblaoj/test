//
// Created by hrblaoj on 2018/12/26.
//

#include <jni.h>
#include "sqlite3_method.h"
#include "cloudserver/CloudAddSong.h"
#include "cloudserver/ShineLog.h"
#include "cloudserver/DownloadService.h"
#include "cloudserver/UpdateDb.h"


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_shinetvbox_vod_service_cloudserver_KtvCloudDownNative_SetServerDownloadSongPath(JNIEnv *env,
                                                                                 jclass type,
                                                                                 jstring pPath_) {
    const char *pPath = env->GetStringUTFChars(pPath_, 0);

    memset(CLOUDSERVE_DOWNLOADSONGPATH, 0, sizeof(CLOUDSERVE_DOWNLOADSONGPATH));
    // TODO
    LOGD("edfdf startservr length %d %s, %d, %s", sizeof(CLOUDSERVE_DOWNLOADSONGPATH), __FILE__,
         __LINE__, __FUNCTION__);
    strcpy(CLOUDSERVE_DOWNLOADSONGPATH, pPath);
    LOGD("dfdf startserver %s length %d %s, %d, %s", CLOUDSERVE_DOWNLOADSONGPATH,
         strlen(CLOUDSERVE_DOWNLOADSONGPATH), __FILE__, __LINE__, __FUNCTION__);
    env->ReleaseStringUTFChars(pPath_, pPath);

    return true;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_shinetvbox_vod_service_cloudserver_KtvCloudDownNative_SetServerDownloadDBPath(JNIEnv *env,
                                                                               jclass type,
                                                                               jstring pPath_) {
    const char *pPath = env->GetStringUTFChars(pPath_, 0);

    if (pthread_mutex_init(&mutex, NULL) < 0) {
        LOGD("dfdf startserver sem_init error");
        env->ReleaseStringUTFChars(pPath_, pPath);
        return false;
    }
    pthread_mutex_lock(&mutex);
    // TODO
    memset(CLOUDSERVE_DOWNLOADDBPATH, 0, strlen(CLOUDSERVE_DOWNLOADDBPATH));
    // TODO
    LOGD("dfdf startserver length %d %s, %d, %s", sizeof(CLOUDSERVE_DOWNLOADDBPATH), __FILE__,
         __LINE__, __FUNCTION__);
    strcpy(CLOUDSERVE_DOWNLOADDBPATH, pPath);
    LOGD("dfdf startserver %s length %d %s, %d, %s", CLOUDSERVE_DOWNLOADDBPATH,
         strlen(CLOUDSERVE_DOWNLOADDBPATH), __FILE__, __LINE__, __FUNCTION__);

    //set string serverip
    memset(CLOUDSERVE_DOWNLOAD_SERVERIP, 0, strlen(CLOUDSERVE_DOWNLOAD_SERVERIP));
    sprintf(CLOUDSERVE_DOWNLOAD_SERVERIP, "%s/%s", CLOUDSERVE_DOWNLOADDBPATH, FILENAME_SERVERIP);

    pthread_mutex_unlock(&mutex);

    env->ReleaseStringUTFChars(pPath_, pPath);

    return true;
}extern "C"
JNIEXPORT jboolean JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_opendb(JNIEnv *env, jclass type, jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);

    // TODO


    jboolean tRet = false;
    int ret = 0;
    ret = sqlite3_open_DB(path);


    if(1 == ret)
    {
        tRet = true;
    }
    else
    {
        tRet = false;
    }

    // TODO
    env->ReleaseStringUTFChars(path_, path);
    return tRet;
}extern "C"
JNIEXPORT jboolean JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_opendbAndSetTemp(JNIEnv *env, jclass type, jstring path_,
                                                        jstring tempPath_) {
    const char *path = env->GetStringUTFChars(path_, 0);
    const char *tempPath = env->GetStringUTFChars(tempPath_, 0);

    // TODO
    jboolean tRet = false;
    int ret = 0;
    ret = sqlite3_open_DB(path);


    if(1 == ret)
    {
        tRet = true;
        char sql[256] = "";
//        sprintf(sql, "PRAGMA temp_store_directory = \'%s\'", tempPath);
        sprintf(sql, "PRAGMA temp_store = 2");
        printf("Java_com_shinetvbox_vod_jnimethod_jniMethod_opendbAndSetTemp sql is %s\n", sql);
        sqlite3_execute_songSql(sql);
        printf("sqlite3_threadsafe is %d\n", sqlite3_threadsafe());
    }
    else
    {
        tRet = false;
    }


    env->ReleaseStringUTFChars(path_, path);
    env->ReleaseStringUTFChars(tempPath_, tempPath);

    return tRet;
}extern "C"
JNIEXPORT void JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_checkDb(JNIEnv *env, jclass type) {
    //联合查询
    sqlite3_execute_songSql("CREATE INDEX IF NOT EXISTS song_local_path on song(local_path)");
    sqlite3_execute_songSql("CREATE INDEX IF NOT EXISTS song_word_head_code on song(word_head_code)");
    //去除索引
    sqlite3_execute_songSql("CREATE INDEX IF NOT EXISTS date_theme on song (new_song_date, song_theme);");

    //歌星点击率创建列
    int result = sqlite3_execute_songSql("alter table singer add column local_click_rank TEXT Default \"0\"");
    LOGD("check Db , result is %d", result);

    // TODO

}extern "C"
JNIEXPORT jobject JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_querySong(JNIEnv *env, jclass type, jstring sql_) {
    const char *sql = env->GetStringUTFChars(sql_, 0);

    // TODO

    jclass list_cls = env->FindClass("java/util/ArrayList");//获得ArrayList类引用
    jmethodID list_costruct = env->GetMethodID(list_cls , "<init>","()V"); //获得得构造函数Id
    jobject list_obj = env->NewObject(list_cls , list_costruct); //创建一个Arraylist集合对象
    jmethodID list_add  = env->GetMethodID(list_cls,"add","(Ljava/lang/Object;)Z");


    jclass song_cls = env->FindClass("com/shinetvbox/vod/db/Song");//获得Song类引用


    jmethodID song_costruct = env->GetMethodID(song_cls , "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");


    /* java需要的成员*/
    jstring singer_id1;
    jstring singer_id2;
    jstring singer_id3;
    jstring singer_id4;
    jstring song_id;
    jstring accompany_track;
    jstring karaoke_track;
    jstring song_name;
    jstring show_movie_name;
    jstring accompany_volume;
    jstring karaoke_volume;
    jstring language;
    jstring song_type;
    jstring singer_name;
    jstring singer_sex;
    jstring song_version;
    jstring local_path;
    jstring light_control_set;
    jstring song_theme;
    jstring new_song_theme;
    jstring pingfen;
    jstring word_head_code;


    /* c需要的变量 */
    char **get_result=NULL;
    char *error_msg=NULL;
    int row=0;
    int column=0;
    int result=0;
    int suffix=0;
    int i=0;


    if (NULL==get_handle())
    {
        return NULL;
    }

    result = sqlite3_get_table(get_handle(),sql,&get_result,&row,&column,&error_msg);

    LOGD("error_msg is %s, result is %d column %d\n", error_msg, result, column);
    sqlite3_free(error_msg);

    if (SQLITE_OK==result)
    {
        for (i=column; i<(row+1)*column; i+= column)
        {
            if (NULL!=get_result[i])
            {
                if(get_result[i])
                {
                    song_id = env->NewStringUTF(get_result[i]);
                }
                else{
                    song_id = env->NewStringUTF("");
                }
                if(get_result[i+1])
                {
                    accompany_track = env->NewStringUTF(get_result[i+1]);
                }
                else{
                    accompany_track = env->NewStringUTF("");
                }
                if(get_result[i+2])
                {
                    karaoke_track = env->NewStringUTF(get_result[i+2]);
                } else
                    karaoke_track = env->NewStringUTF("");
                if(get_result[i+3])
                {
                    song_name = env->NewStringUTF(get_result[i+3]);
                } else
                    song_name = env->NewStringUTF("");
                if(get_result[i+4])
                {
                    show_movie_name = env->NewStringUTF(get_result[i+4]);
                } else
                    show_movie_name = env->NewStringUTF("");
                if(get_result[i+5])
                {
                    accompany_volume = env->NewStringUTF(get_result[i+5]);
                } else
                    accompany_volume = env->NewStringUTF("");
                if(get_result[i+6])
                {
                    karaoke_volume = env->NewStringUTF(get_result[i+6]);
                } else
                    karaoke_volume = env->NewStringUTF("");
                if(get_result[i+7])
                {
                    language = env->NewStringUTF(get_result[i+7]);
                } else
                    language = env->NewStringUTF("");
                if(get_result[i+8])
                {
                    song_type = env->NewStringUTF(get_result[i+8]);
                } else
                    song_type = env->NewStringUTF("");
                if(get_result[i+9])
                {
                    singer_name = env->NewStringUTF(get_result[i+9]);
                } else
                    singer_name = env->NewStringUTF("");
                if(get_result[i+10])
                {
                    singer_sex = env->NewStringUTF(get_result[i+10]);
                } else
                    singer_sex = env->NewStringUTF("");
                if(get_result[i+11])
                {
                    song_version = env->NewStringUTF(get_result[i+11]);
                } else
                    song_version = env->NewStringUTF("");
                if(get_result[i+12])
                {
                    local_path = env->NewStringUTF(get_result[i+12]);
                } else
                    local_path = env->NewStringUTF("");
                if(get_result[i+13])
                {
                    light_control_set = env->NewStringUTF(get_result[i+13]);
                } else
                    light_control_set = env->NewStringUTF("");
                if(get_result[i+14])
                {
                    song_theme = env->NewStringUTF(get_result[i+14]);
                } else
                    song_theme = env->NewStringUTF("");
                if(get_result[i+15])
                {
                    new_song_theme = env->NewStringUTF(get_result[i+15]);
                } else
                    new_song_theme = env->NewStringUTF("");
                if(get_result[i+16])
                {
                    pingfen = env->NewStringUTF(get_result[i+16]);
                } else
                    pingfen = env->NewStringUTF("");
                if(get_result[i+17])
                {
                    singer_id1 = env->NewStringUTF(get_result[i+17]);
                } else
                    singer_id1 = env->NewStringUTF("");
                if(get_result[i+18])
                {
                    singer_id2 = env->NewStringUTF(get_result[i+18]);
                } else
                    singer_id2 = env->NewStringUTF("");
                if(get_result[i+19])
                {
                    singer_id3 = env->NewStringUTF(get_result[i+19]);
                } else
                    singer_id3 = env->NewStringUTF("");
                if(get_result[i+20])
                {
                    singer_id4 = env->NewStringUTF(get_result[i+20]);
                } else
                    singer_id4 = env->NewStringUTF("");
                if(get_result[i+21])
                {
                    word_head_code = env->NewStringUTF(get_result[i+21]);
                } else
                    word_head_code = env->NewStringUTF("");

            }


            jobject song_obj = env->NewObject(song_cls ,song_costruct ,singer_id1,singer_id2,singer_id3,singer_id4,song_id, accompany_track, karaoke_track, song_name, show_movie_name,\
accompany_volume, karaoke_volume, language, song_type, singer_name, singer_sex, song_version, local_path, light_control_set,song_theme,\
new_song_theme, pingfen,word_head_code); //构造一个对象
            env->CallBooleanMethod(list_obj , list_add , song_obj);
            env->DeleteLocalRef(song_obj);
            env->DeleteLocalRef(song_id);
            env->DeleteLocalRef(accompany_track);
            env->DeleteLocalRef(karaoke_track);
            env->DeleteLocalRef(song_name);
            env->DeleteLocalRef(show_movie_name);
            env->DeleteLocalRef(accompany_volume);
            env->DeleteLocalRef(karaoke_volume);
            env->DeleteLocalRef(language);
            env->DeleteLocalRef(song_type);
            env->DeleteLocalRef(singer_name);
            env->DeleteLocalRef(singer_sex);
            env->DeleteLocalRef(song_version);
            env->DeleteLocalRef(local_path);
            env->DeleteLocalRef(light_control_set);
            env->DeleteLocalRef(song_theme);
            env->DeleteLocalRef(new_song_theme);
            env->DeleteLocalRef(pingfen);
            env->DeleteLocalRef(singer_id1);
            env->DeleteLocalRef(singer_id2);
            env->DeleteLocalRef(singer_id3);
            env->DeleteLocalRef(singer_id4);
            env->DeleteLocalRef(word_head_code);
        }

    }
    else
    {
        //printf("sqlite3_get_table() fail,sql:%s   result=%d\r\n",sql,result);
    }

    sqlite3_free_table(get_result);

    env->DeleteLocalRef(list_cls);
    env->DeleteLocalRef(song_cls);

    env->ReleaseStringUTFChars(sql_, sql);

    return list_obj;
}extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_querySongSmartPinyin(JNIEnv *env, jclass type,
                                                            jstring sql_) {
    const char *sql = env->GetStringUTFChars(sql_, 0);

//  spell_first_letter_abbreviation
// song_name
    jstring str;
    char **get_result=NULL;
    char *error_msg=NULL;
    int row=0;
    int column=0;
    int result=0;
    int i=0;
    jobjectArray ret = 0;


    if (NULL==get_handle())
    {
        return NULL;
    }

    result = sqlite3_get_table(get_handle(),sql,&get_result,&row,&column,&error_msg);
    sqlite3_free(error_msg);
    ret = (jobjectArray)(env->NewObjectArray(row*column, env->FindClass("java/lang/String"), 0));

    if (SQLITE_OK==result)
    {
        for (i=column; i<(row+1)*column; i++)
        {
            if (NULL!=get_result[i])
            {
                if(get_result[i])
                {
                    str = env->NewStringUTF(get_result[i]);
                    env->SetObjectArrayElement(ret, i-column, str);
                    env->DeleteLocalRef(str);
                }
            }
            else{
                str = env->NewStringUTF("");
                env->SetObjectArrayElement(ret, i-column, str);
                env->DeleteLocalRef(str);
            }
        }
    }
    else
    {
        //printf("sqlite3_get_table() fail,sql:%s   result=%d\r\n",sql,result);
    }
    LOGD("error_msg is %s, getSmartPinyin result: %d row: %d column: %d, sql: %s\n", error_msg, result, row, column, sql);

    sqlite3_free_table(get_result);
    env->ReleaseStringUTFChars(sql_, sql);
    return ret;
}extern "C"
JNIEXPORT jint JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_getSongCount(JNIEnv *env, jclass type, jstring sql_) {
    const char *sql = env->GetStringUTFChars(sql_, 0);

    int conunt = 0;
    conunt = sqlite3_row_count(sql);
    env->ReleaseStringUTFChars(sql_, sql);

    return conunt;
}extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_querySingerSmartHandwrite(JNIEnv *env, jclass type,
                                                                 jstring sql_) {
    const char *sql = env->GetStringUTFChars(sql_, 0);

    jstring str;
    char **get_result=NULL;
    char *error_msg=NULL;
    int row=0;
    int column=0;
    int result=0;
    int i=0;
    jobjectArray ret = 0;


    if (NULL==get_handle())
    {
        return NULL;
    }

    result = sqlite3_get_table(get_handle(),sql,&get_result,&row,&column,&error_msg);
    sqlite3_free(error_msg);
    ret = (jobjectArray)(env->NewObjectArray(row*column, env->FindClass("java/lang/String"), 0));

    if (SQLITE_OK==result)
    {
        for (i=column; i<(row+1)*column; i+=column)
        {
            if (NULL!=get_result[i])
            {
                if(get_result[i])
                {
                    str = env->NewStringUTF(get_result[i]);
                    env->SetObjectArrayElement(ret, i-column, str);
                    env->DeleteLocalRef(str);
                }
            }
        }
    }
    else
    {
        //printf("sqlite3_get_table() fail,sql:%s   result=%d\r\n",sql,result);
    }

    LOGD("error_msg is %s, getSmartHandwrite result: %d row: %d column: %d, sql: %s\n", error_msg, result, row, column, sql);

    sqlite3_free_table(get_result);
    env->ReleaseStringUTFChars(sql_, sql);
    return ret;
}extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_querySongSmartHandwrite(JNIEnv *env, jclass type,
                                                               jstring sql_) {
    const char *sql = env->GetStringUTFChars(sql_, 0);

//  spell_first_letter_abbreviation
// song_name
    jstring str;
    char **get_result=NULL;
    char *error_msg=NULL;
    int row=0;
    int column=0;
    int result=0;
    int i=0;
    jobjectArray ret = 0;


    if (NULL==get_handle())
    {
        return NULL;
    }

    result = sqlite3_get_table(get_handle(),sql,&get_result,&row,&column,&error_msg);
    sqlite3_free(error_msg);
    ret = (jobjectArray)(env->NewObjectArray(row*column, env->FindClass("java/lang/String"), 0));

    if (SQLITE_OK==result)
    {
        for (i=column; i<(row+1)*column; i+=column)
        {
            if (NULL!=get_result[i])
            {
                if(get_result[i])
                {
                    str = env->NewStringUTF(get_result[i]);
                    env->SetObjectArrayElement(ret, i-column, str);
                    env->DeleteLocalRef(str);
                }
            }
        }
    }
    else
    {
        //printf("sqlite3_get_table() fail,sql:%s   result=%d\r\n",sql,result);
    }

    LOGD("error_msg is %s, getSmartHandwrite result: %d row: %d column: %d, sql: %s\n", error_msg, result, row, column, sql);

    sqlite3_free_table(get_result);
    env->ReleaseStringUTFChars(sql_, sql);
    return ret;
}
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_querySingerSmartPinyin(JNIEnv *env, jclass type,
                                                              jstring sql_) {
    const char *sql = env->GetStringUTFChars(sql_, 0);

    jstring str;
    char **get_result=NULL;
    char *error_msg=NULL;
    int row=0;
    int column=0;
    int result=0;
    int i=0;
    jobjectArray ret = 0;


    if (NULL==get_handle())
    {
        return NULL;
    }

    result = sqlite3_get_table(get_handle(),sql,&get_result,&row,&column,&error_msg);
    sqlite3_free(error_msg);
    ret = (jobjectArray)(env->NewObjectArray(row*column, env->FindClass("java/lang/String"), 0));

    if (SQLITE_OK==result)
    {
        for (i=column; i<(row+1)*column; i++)
        {
            if (NULL!=get_result[i])
            {
                if(get_result[i])
                {
                    str = env->NewStringUTF(get_result[i]);
                    env->SetObjectArrayElement(ret, i-column, str);
                    env->DeleteLocalRef(str);
                }
            }
            else{
                str = env->NewStringUTF("");
                env->SetObjectArrayElement(ret, i-column, str);
                env->DeleteLocalRef(str);
            }
        }
    }
    else
    {
        //printf("sqlite3_get_table() fail,sql:%s   result=%d\r\n",sql,result);
    }
    LOGD("error_msg is %s, getSmartPinyin result: %d row: %d column: %d, sql: %s\n", error_msg, result, row, column, sql);

    sqlite3_free_table(get_result);
    env->ReleaseStringUTFChars(sql_, sql);
    return ret;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_querySinger(JNIEnv *env, jclass type, jstring sql_) {
    const char *sql = env->GetStringUTFChars(sql_, 0);

    // TODO
    jclass list_cls = env->FindClass("java/util/ArrayList");//获得ArrayList类引用
    jmethodID list_costruct = env->GetMethodID(list_cls , "<init>","()V"); //获得得构造函数Id
    jobject list_obj = env->NewObject(list_cls , list_costruct); //创建一个Arraylist集合对象
    jmethodID list_add  = env->GetMethodID(list_cls,"add","(Ljava/lang/Object;)Z");

    jclass song_cls = env->FindClass("com/shinetvbox/vod/db/Singer");//获得Song类引用


    jmethodID song_costruct = env->GetMethodID(song_cls , "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");

    /* java需要的成员*/
    jstring singer_id;
    jstring singer_name;
    jstring singer_sex;
    jstring singer_region;
    jstring local_click_rank;


    /* c需要的变量 */
    char **get_result=NULL;
    char *error_msg=NULL;
    int row=0;
    int column=0;
    int result=0;
    int suffix=0;
    int i=0;


    if (NULL==get_handle())
    {
        return NULL;
    }

    result = sqlite3_get_table(get_handle(),sql,&get_result,&row,&column,&error_msg);

    LOGD("error_msg is %s, get singer result is %d column %d\n", error_msg, result, column);
    sqlite3_free(error_msg);

    if (SQLITE_OK==result)
    {
        for (i=column; i<(row+1)*column; i+= column)
        {
            if (NULL!=get_result[i])
            {
                if(get_result[i])
                {
                    singer_id = env->NewStringUTF(get_result[i]);
                }
                if(get_result[i+1])
                {
                    singer_name = env->NewStringUTF(get_result[i+1]);
                }
                if(get_result[i+2])
                {
                    singer_sex = env->NewStringUTF(get_result[i+2]);
                }
                if(get_result[i+3])
                {
                    singer_region = env->NewStringUTF(get_result[i+3]);
                }
                if(get_result[i+4])
                {
                    local_click_rank = env->NewStringUTF(get_result[i+4]);
                }
                else{
                    singer_region = env->NewStringUTF("");
                }
            }

            jobject song_obj = env->NewObject(song_cls ,song_costruct ,singer_id,singer_name, singer_sex, singer_region, local_click_rank); //构造一个对象
            env->CallBooleanMethod(list_obj , list_add , song_obj);
            env->DeleteLocalRef(song_obj);
            env->DeleteLocalRef(singer_id);
            env->DeleteLocalRef(singer_name);
            env->DeleteLocalRef(singer_sex);
            env->DeleteLocalRef(singer_region);
            env->DeleteLocalRef(local_click_rank);
        }

    }
    else
    {
        //printf("sqlite3_get_table() fail,sql:%s   result=%d\r\n",sql,result);
    }

    sqlite3_free_table(get_result);

    env->DeleteLocalRef(list_cls);
    env->DeleteLocalRef(song_cls);

    env->ReleaseStringUTFChars(sql_, sql);

    return list_obj;
}extern "C"
JNIEXPORT jint JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_getSingerCount(JNIEnv *env, jclass type, jstring sql_){
    const char *sql = env->GetStringUTFChars(sql_, 0);

    // TODO
    int conunt = 0;
    conunt = sqlite3_row_count(sql);
    env->ReleaseStringUTFChars(sql_, sql);

    return conunt;
}extern "C"
JNIEXPORT jint JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_closedb(JNIEnv *env, jclass type) {

    // TODO
    if (NULL!=g_sqlite3_handle)
    {
        return 0;
    }

    int result=0;

    result = sqlite3_close(g_sqlite3_handle);

    if (SQLITE_OK!=result)
    {
        return 0;
    }
    else
    {
        g_sqlite3_handle = NULL;
        printf(" sqlite3_close_DB g_sqlite3_handle success\r\n");
    }

    return 1;
}

extern "C" void SH_StdioAddSongPath(char *path);
extern "C" void CreateHTTPStream(unsigned short lport);
extern "C" int SH_VideoStream();
extern "C"
JNIEXPORT jint JNICALL
Java_com_shinetvbox_vod_MainActivity_GetShineHttpServerPort(JNIEnv *env, jobject instance) {

    signal(SIGPIPE, SIG_IGN);
    // TODO
//    SH_StdioAddSongPath("");
    int i = 18080;
#if 0

    SH_StdioAddSongPath("");
    return SH_VideoStream();



#else

 LOGD("will CreateHTTPStream");
//    srand((unsigned)time( NULL ) );

//    i = rand()%100+18080;

    CreateHTTPStream(i);

    return i;
#endif

}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_jni_1GetPlayMd5Key(JNIEnv *env, jclass type) {

    // TODO


    return env->NewStringUTF("shine667788gkfjhdsegsagsh");
}extern "C"
JNIEXPORT jint JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_execteSongSql(JNIEnv *env, jclass type, jstring sql_) {
    const char *sql = env->GetStringUTFChars(sql_, 0);

    // TODO
    int result = sqlite3_execute_songSql(sql);
    env->ReleaseStringUTFChars(sql_, sql);
    return result;
}extern "C"
JNIEXPORT jstring JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_gSqliteHdlGetData(JNIEnv *env, jclass type,
                                                         jstring parStatement_)  {
    const char *parStatement = env->GetStringUTFChars(parStatement_, 0);
    // TODO
    char strT[1024] = "";
    KTVDB_mirror_get_field_data(  get_handle(), parStatement, strT );

    env->ReleaseStringUTFChars(parStatement_, parStatement);
    return env->NewStringUTF(strT);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_shinetvbox_vod_service_cloudserver_KtvCloudDownNative_startccloudserver(JNIEnv *env, jclass type) {

    // TODO
    LOGD("dfdf %s, %d, %s", __FILE__, __LINE__, __FUNCTION__);
    int result = 0;
    result = startserver();

    //testpthread();

    LOGD("dfdf result%d %s, %d, %s", result, __FILE__, __LINE__, __FUNCTION__);

    return true;

}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_shinetvbox_vod_service_cloudserver_KtvCloudDownNative_SetServerDownloadUpdateDBPath(JNIEnv *env,
                                                                                     jclass type,
                                                                                     jstring pPath_,
                                                                                     jstring para_) {
    const char *pPath = env->GetStringUTFChars(pPath_, 0);
    const char *para = env->GetStringUTFChars(para_, 0);

    // TODO
    setUpdateDbPath(pPath, para);

    env->ReleaseStringUTFChars(pPath_, pPath);
    env->ReleaseStringUTFChars(para_, para);

    return true;
}extern "C"
JNIEXPORT jint JNICALL
Java_com_shinetvbox_vod_db_KtvDbNative_getSongCountByDbName(JNIEnv *env, jclass type,
                                                            jstring name_) {
    const char *name = env->GetStringUTFChars(name_, 0);

    // TODO
    sqlite3 * handle = NULL;
    int ret = 0;
    ret = sqlite3_open(name, &handle);
    if (SQLITE_OK!=ret)
    {
        printf(" Java_com_shinetvbox_vod_db_KtvDbNative_getSongCountByDbName db fail result is %d db name is %s++++\r\n", ret, name);
        return -1;
    }

    ret = sqlite3_execute_getrowByHandle("select count(song_id) from song", handle);
    sqlite3_close(handle);

    env->ReleaseStringUTFChars(name_, name);

    return ret;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_shinetvbox_vod_service_cloudserver_KtvCloudDownNative_setServerCpuIdAndSubject(JNIEnv *env,
                                                                                jclass type,
                                                                                jstring cpuid_,
                                                                                jstring mac_,
                                                                                jstring subject_) {
    const char *cpuid = env->GetStringUTFChars(cpuid_, 0);
    const char *mac = env->GetStringUTFChars(mac_, 0);
    const char *subject = env->GetStringUTFChars(subject_, 0);

    SetCpuIDAndSubjectID( cpuid,mac, subject );

    env->ReleaseStringUTFChars(cpuid_, cpuid);
    env->ReleaseStringUTFChars(mac_, mac);
    env->ReleaseStringUTFChars(subject_, subject);

    return true;

}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_shinetvbox_vod_service_cloudserver_KtvCloudDownNative_setServerDwonloadUrl(JNIEnv *env,
                                                                            jclass type,
                                                                            jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);

    SetServerDownloadUrl( url );

    env->ReleaseStringUTFChars(url_, url);

    return true;
}
