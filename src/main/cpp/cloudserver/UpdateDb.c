#include <pthread.h>
#include <stdio.h>
#include <sys/stat.h>
#include "CloudAddSong.h"
#include "DownloadService.h"
#include "../sqlite3_method.h"
#include "../ShineLog.h"
#include "cJSON.h"

static char g_UpdatePath[256];
static char g_ParaPath[256];
static sqlite3 * g_UseingDb = NULL;
static sqlite3 * g_UpdatingngDb = NULL;
static char versionlast_cArray[32];
static char versionLocal_cArray[32] = "";
static int g_RankDownRet = 1;
typedef struct versionTag
{
    int version_cnt;
    int *version_arry;
} Version_st;
Version_st db_version_arry = {0, NULL};
static pthread_mutex_t UpdateDbThread = PTHREAD_MUTEX_INITIALIZER;
void upDateDb_ParseJson(char *json)
{
    cJSON * JsonRoot2 = NULL;
    memset(versionlast_cArray,0, sizeof(versionlast_cArray));
    JsonRoot2 = cJSON_GetObjectItem(json, "newversion");
    if(JsonRoot2 != NULL)
    {
        sprintf(versionlast_cArray, "%d", JsonRoot2->valueint);
    }
    JsonRoot2 = NULL;


    if(db_version_arry.version_arry != NULL)
    {
        db_version_arry.version_cnt = 0;
        free(db_version_arry.version_arry);
        db_version_arry.version_arry = NULL;
    }

    JsonRoot2 = cJSON_GetObjectItem(json, "result");
    if(JsonRoot2 != NULL)
    {
        int arry_cnt = 0;
        int i = 0;

        arry_cnt = cJSON_GetArraySize(JsonRoot2);
        if(arry_cnt != 0)
        {
            db_version_arry.version_cnt = arry_cnt;
            db_version_arry.version_arry = (int *)malloc(sizeof(int) * arry_cnt);
            for(i = 0; i < arry_cnt; i++)
            {
                db_version_arry.version_arry[i] = cJSON_GetArrayItem(JsonRoot2, i)->valueint;
                    LOGD("================%s %d: db_version_arry.version_arry[%d] = %d, versionlast_cArray is %s\n", __FILE__, __LINE__, i, db_version_arry.version_arry[i], versionlast_cArray);
            }
        }

    }
    return;
}


int upDateDb_DownDb(){
    if(0 == db_version_arry.version_cnt)
    {
        return -1;
    }

    char dbListFile[256] = "";
    FILE *fp = NULL;
    sprintf(dbListFile, "%s%s",g_UpdatePath, DB_LIST_TXT);
    if((fp = fopen(dbListFile, "w")) == NULL)
    {
        return  -1;
    }

    int i = 0;
    int ret = 0;
    char requestVersion[64] = "";
    char downLoadFile[64] = "";
    for(i = 0; i < db_version_arry.version_cnt; i++){
        memset(requestVersion, 0, sizeof(requestVersion));
        memset(downLoadFile, 0, sizeof(downLoadFile));
        sprintf(requestVersion, "%d", db_version_arry.version_arry[i]);
        sprintf(downLoadFile, "%s%s", g_UpdatePath, requestVersion);
        if(0 != download_file_from_http(downLoadFile, requestVersion, GET_DATABASE_SINGLE)){
            fclose(fp);
            return -1;
        }

        ret = fputs(downLoadFile, fp);

        fputs("\n", fp);
    }

    char rankPath[128] = "";
    sprintf(rankPath, "%s%s", g_UpdatePath, DB_RANK);
    g_RankDownRet = request_KfunQrCode(TYPE_RANKDB, rankPath);

    LOGD("upDateDb_DownDb success\n");
    fclose(fp);
    return 0;
}

void *download_db_thread(void *arg){

    pthread_mutex_lock(&UpdateDbThread);
    int ret = 0;
    DOWNLOAD_STRUCT temp_struct;
    temp_struct.cmd = 0;
    memset(temp_struct.content, 0, sizeof( temp_struct.content ));

    LOGD("upDateDb download_db_thread start\n");
    if(0 != upDateDb_OpenUpteDb()){
        SendServiceCreateOK();
        pthread_mutex_unlock(&UpdateDbThread);
        LOGD("upDateDb upDateDb_OpenDb return\n");
        return NULL;
    }

    memset(versionLocal_cArray, 0, sizeof(versionLocal_cArray));
    if(-1 == KTVDB_mirror_get_field_data(g_UpdatingngDb,"select version from version", versionLocal_cArray)){
        LOGD("upDateDb KTVDB_mirror_get_field_data return\n");
        sqlite3_close(g_UpdatingngDb);
        g_UpdatingngDb = NULL;
        SendServiceCreateOK();
        pthread_mutex_unlock(&UpdateDbThread);
        return NULL;
    }



    char praSql[256] = "";
//    sprintf(praSql, "PRAGMA temp_store_directory = \'%s\'", g_ParaPath);
    sprintf(praSql, "PRAGMA temp_store = 2");
    printf("upDateDb praSql is %s\n" , praSql);

//            sqlite3_execute_songSqlbyHandle(praSql, g_UseingDb);
    sqlite3_execute_songSqlbyHandle(praSql, g_UpdatingngDb);

    if((ret = download_comm_to_cloud_parsebyout(versionLocal_cArray,GET_DB_VERSION, upDateDb_ParseJson)) == 0)
    {
        if(atoi(versionLocal_cArray) >= atoi(versionlast_cArray) && (0 != atoi(versionLocal_cArray))){
            printf("upDateDb versionLocal_cArray >= return ver is %s\n", versionLocal_cArray);
            sqlite3_close(g_UpdatingngDb);
            g_UpdatingngDb = NULL;
            SendServiceCreateOK();
            pthread_mutex_unlock(&UpdateDbThread);
            return NULL;
        }


//        char updateDbName[256] = "";
//        sprintf(updateDbName, "%sktv10.db_bak", g_UpdatePath);
//        unlink(updateDbName);
////        loadOrSaveDb(g_UseingDb, updateDbName, 1);
//        char useDbName[256] = "";
//        sprintf(useDbName, "%sktv10.db", g_UpdatePath);
//        char cmd[256] = "";
//        sprintf(cmd, "cp %s %s", useDbName, updateDbName);
//        SystemInstead(cmd);
//        sqlite3_close(g_UseingDb);
//        g_UseingDb = NULL;

    }
    else
    {
        sqlite3_close(g_UpdatingngDb);
        g_UpdatingngDb = NULL;
        SendServiceCreateOK();
        pthread_mutex_unlock(&UpdateDbThread);
        return NULL;
    }

    SendServiceCreateOK();
    if(0 != upDateDb_DownDb())
    {
        sqlite3_close(g_UpdatingngDb);
        g_UpdatingngDb = NULL;
        pthread_mutex_unlock(&UpdateDbThread);
        return NULL;
    }

//    if(0 != upDateDb_OpenUpteDb()){
//        pthread_mutex_unlock(&UpdateDbThread);
//        LOGD("upDateDb upDateDb_OpenDb return\n");
//        return NULL;
//    }

//    sqlite3_execute_songSqlbyHandle(praSql, g_UpdatingngDb);
    upDateDb_InitTable();
    upDateDb_DatabaseUpdate();
    FILE *fp = NULL;
    char isUpdateSuFlag[256] = "";
    sprintf(isUpdateSuFlag, "%s%s",g_UpdatePath, DB_UPDATE_OK);
    if((fp = fopen(isUpdateSuFlag, "w")) == NULL)
    {
        sqlite3_close(g_UpdatingngDb);
        g_UpdatingngDb = NULL;
        pthread_mutex_unlock(&UpdateDbThread);
        return  -1;
    }
    fclose(fp);
    sqlite3_close(g_UpdatingngDb);
    g_UpdatingngDb = NULL;
    pthread_mutex_unlock(&UpdateDbThread);
    return NULL;
}

int DB_Init_Version()
{
    char sql[1024];
    int result = 0;

    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='version'");
    result = sqlite3_execute_getrowByHandle(sql, g_UpdatingngDb);
    //printf("\n\n-----------------create version result = %d-----------------\n\n", result);
    if(result == 0)
    {
        //create the version table
        sqlite3_execute_songSqlbyHandle("CREATE TABLE version (version text)", g_UpdatingngDb);
        sqlite3_execute_songSqlbyHandle("insert into  version(version) values('0')", g_UpdatingngDb);
        printf("\n\n-----------------create version version sussess-----------------\n\n");
    }
    else{
        int result = sqlite3_execute_getrowByHandle("select count(*) from version", g_UpdatingngDb);
        if( result == 0 ) {
            sqlite3_execute_songSqlbyHandle("insert into  version(version) values('0')", g_UpdatingngDb);
        }
    }
    return 0;
}

int DB_Init_Top_Song()
{
    char sql[1024] = "";
    int result = 0;
    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='top_song'");
    result = sqlite3_execute_getrowByHandle(sql, g_UpdatingngDb);
    if(result == 0)
    {
        sqlite3_execute_songSqlbyHandle("CREATE TABLE [top_song] (\
                                               [song_id] text,\
                                               [accompany_sing_track] text,\
                                               [karaoke_track] text,\
                                               [guid_track] text,\
                                               [song_name] text,\
                                               [movie_name] text,\
                                               [show_movie_name] text,\
                                               [song_name_sort] INTEGER,\
                                               [accompany_volume] INTEGER,\
                                               [karaoke_volume] INTEGER,\
                                               [language] text,\
                                               [song_type] text,\
                                               [song_name_word_count] INTEGER,\
                                               [singer_number] INTEGER,\
                                               [singer_name] text,\
                                               [singer_sex] text,\
                                               [spell_first_letter_abbreviation] text,\
                                               [spell_first_letter_traditional] text,\
                                               [moive_spell_first_letter] text,\
                                               [sing_number] INTEGER DEFAULT 0,\
                                               [song_version] text,\
                                               [light_control_set] INTEGER,\
                                               [audio_effect_code] INTEGER,\
                                               [file_format] text,\
                                               [song_bit_rate] real,\
                                               [song_theme] text,\
                                               [new_song_theme] text,\
                                               [first_word_stroke_number] INTEGER,\
                                               [local_path] text,\
                                               [server_path1] ,\
                                               [server_path2] ,\
                                               [server_path3] ,\
                                               [server_path4] ,\
                                               [server_path5] ,\
                                               [server_path6] ,\
                                               [song_relative_path] ,\
                                               [file_size] INTEGER,\
                                               [video_saturation] INTEGER,\
                                               [video_luminance] INTEGER,\
                                               [video_contrast] INTEGER,\
                                               [lyric] ,\
                                               [word_head_code] ,\
                                               [culture_code] ,\
                                               [issue_year] ,\
                                               [new_song_date] ,\
                                               [preview_path] ,\
                                               [singer_id1] TEXT,\
                                               [singer_id2] TEXT,\
                                               [singer_id3] TEXT,\
                                               [singer_id4] TEXT,\
                                               [file_version] TEXT,\
                                               CONSTRAINT [sqlite_autoindex_song_1] PRIMARY KEY ([song_id]));", g_UpdatingngDb);
        sqlite3_execute_songSqlbyHandle("CREATE INDEX top_song_language on top_song(language)", g_UpdatingngDb);
        printf("\n\n-----------------create top_song sussess-----------------\n\n");
    }

    //清空表， 后面排序后填充
//    sqlite3_execute_songSqlbyHandle("delete from top_song", g_UpdatingngDb);

    //不能在这里排序， 可能song表里， 没有对应歌曲
    //Re_Sort_Top_Song_From_Net_Rank();

    return 0;
}

void upDateDb_InitTable(){
    DB_Init_Song();

    sqlite3_execute_songSqlbyHandle("CREATE INDEX IF NOT EXISTS song_local_path on song(local_path)", g_UpdatingngDb);//insert index
    sqlite3_execute_songSqlbyHandle("CREATE INDEX IF NOT EXISTS song_word_head_code on song(word_head_code)", g_UpdatingngDb);//insert index
    //去除联合索引
    sqlite3_execute_songSqlbyHandle("CREATE INDEX IF NOT EXISTS date_theme on song (new_song_date, song_theme)", g_UpdatingngDb);
    sqlite3_execute_songSqlbyHandle("PRAGMA synchronous = OFF", g_UpdatingngDb);//synchronous, wait for the operate over then synchronous
    LOGD("upDateDb_InitTable MainFuncUpdatedbVersion  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

    //歌星点击率创建列
    sqlite3_execute_songSqlbyHandle("alter table singer add column local_click_rank TEXT Default \"0\"", g_UpdatingngDb);

    DB_Init_Version();
    //初始化金曲排行表
    DB_Init_Top_Song();//热门歌曲

    DB_Init_Song_Bak();//该函数创建一个备份表， 主要用于排序后， 替换数据

    DB_Init_Singer();//该函数创建一个备份表， 主要用于排序后， 替换数据

    DB_Init_Singer_Bak();//创建歌星排序表

    sqlite3_execute_songSqlbyHandle("PRAGMA synchronous = NORMAL", g_UpdatingngDb);
    LOGD("upDateDb_InitTable MainFuncUpdatedbVersion  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    return ;
}

//
// Created by hrblaoj on 2019/1/18.
//
void upDateDb_StartThread(){
    pthread_t download_thread_t=0;
    pthread_attr_t  attr;
    size_t stacksize = 2048*1024;
    struct sched_param schedling_value;
    pthread_attr_init(&attr);
    pthread_attr_getschedparam(&attr, &schedling_value);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
    pthread_attr_setschedpolicy(&attr, SCHED_OTHER);
    pthread_attr_setstacksize(&attr, stacksize);
    pthread_attr_setschedparam(&attr, &schedling_value);

    ////创建发送线程
    if(pthread_create(&download_thread_t,&attr,download_db_thread,NULL))
    {
        SendServiceCreateOK();
        perror("Error::::::::[upDateDb_StartThread.c Create Thread Failed]");
    }
}



void setUpdateDbPath(const char *path, const char *path2){
    memset(g_UpdatePath, 0, sizeof(g_UpdatePath));
    memset(g_ParaPath, 0, sizeof(g_ParaPath));

    strlcpy(g_UpdatePath, path, sizeof(g_UpdatePath));
    strlcpy(g_ParaPath, path2, sizeof(g_ParaPath));
}


int upDateDb_OpenDb(){
    int result=0;
    char useDbName[256] = "";
    sprintf(useDbName, "%sktv10.db", g_UpdatePath);

    if(g_UseingDb != NULL){
        sqlite3_close(g_UseingDb);
    }
    result = sqlite3_open(useDbName, &g_UseingDb);

//    result = sqlite3_open(":memory:", &g_sqlite3_handle);
//    result = loadOrSaveDb(g_sqlite3_handle, file_path, 0); //�ļ����ݿ⵼�뵽�ڴ����ݿ�

    if (SQLITE_OK!=result)
    {
        printf(" upDateDb_OpenDb db fail result is %d db name is %s++++\r\n", result, useDbName);
        return -1;
    }
    else
    {

        return 0;
    }

}
int upDateDb_OpenUpteDb(){
    int result=0;
    char updateDbName[256] = "";
    sprintf(updateDbName, "%sktv10.db_bak", g_UpdatePath);

    if(g_UpdatingngDb != NULL){
        sqlite3_close(g_UpdatingngDb);
        g_UpdatingngDb = NULL;
    }
    result = sqlite3_open(updateDbName, &g_UpdatingngDb);

//    result = sqlite3_open(":memory:", &g_sqlite3_handle);
//    result = loadOrSaveDb(g_sqlite3_handle, file_path, 0); //�ļ����ݿ⵼�뵽�ڴ����ݿ�

    if (SQLITE_OK!=result)
    {
        printf(" upDateDb_OpenUpteDb db fail result is %d db name is %s++++\r\n", result, updateDbName);
        return -1;
    }
    else
    {
        return 0;
    }

}

int upDateDb_DatabaseUpdate()
{

    FILE *fp = NULL;
    char line[1024] = "";
    char str_song_id[128] = "";
    char tmp_song_id[128] = "";
    int ret = 0;
    char *tmp_str = NULL;
    int i = 0;
    int file_exist;
    int line_counts = 0;
    sqlite3 *sqlite3_handle=NULL;
    char *errmsg = NULL;
    //这里需要做一定修改
    char *select_songtable = "select song_id,accompany_sing_track,karaoke_track,song_name,movie_name,show_movie_name,accompany_volume,karaoke_volume,language,song_type,song_name_word_count,singer_name,\
							singer_sex,spell_first_letter_abbreviation,moive_spell_first_letter,sing_number,song_version,light_control_set,song_theme,new_song_theme,first_word_stroke_number,local_path,new_song_date,singer_id1,singer_id2,singer_id3,singer_id4 from song";
    char *select_singer =  "select singer_id,singer_name,singer_sex,singer_region,singer_region_new,spell_first_letter_abbreviation,singer_name_word_count,singer_hot_rank from singer ";
    char *select_bansong = "select songid from bansong";
    char *select_bansinger = "select singername from bansinger";

    char file_name[256] = "";

    sprintf(file_name, "%s%s", g_UpdatePath, DB_LIST_TXT);
    LOGD("-----------------file_name = %s--------------\n\n", file_name);

    fp = fopen(file_name, "r");
    if (fp == NULL) {
        LOGD("Open datebase update file Failed: ");
        return -1;
    }


    sqlite3_execute_songSqlbyHandle("PRAGMA synchronous = OFF", g_UpdatingngDb);


//开始插表
    //不断检测文件是否结束
    while (!feof(fp)) {

        memset(line, '\0', sizeof(line));

        if (fgets(line, sizeof(line), fp) != NULL) {
            if (line[strlen(line) - 1] == '\n') {
                line[strlen(line) - 1] = '\0';
            }
            LOGD("============%s %d: line = %s\n", __FILE__, __LINE__, line);

#ifdef USE_SATA_MODE_OPENDB
    ret = sqlite3_open(line, &sqlite3_handle);
#else
            //�ڴ����ݿ� ,����7229 ȡ���ڴ����ݿ⡣
            ret = sqlite3_open(":memory:", &sqlite3_handle);
            ret = loadOrSaveDb(sqlite3_handle, line, 0); //�ļ����ݿ⵼�뵽�ڴ����ݿ�
#endif


            if (SQLITE_OK != ret) {
                LOGD("============%s %d: sqlite open %s db fail\n", __FILE__, __LINE__, line);
                continue;
            }

            char praSql[256] = "";
//            sprintf(praSql, "PRAGMA temp_store_directory = \'%s\'", g_ParaPath);
            sprintf(praSql, "PRAGMA temp_store = 2");
            sqlite3_execute_songSqlbyHandle(praSql, sqlite3_handle);
            //由于一次读表大表会内存
            //更新song表
            sCountTime = 0;
            sCountframe  = 0;
            struct timeval tv;
            gettimeofday(&tv,NULL);
            sCountTime = tv.tv_sec * 1000 + tv.tv_usec / 1000;
            sqlite3_exec(sqlite3_handle, select_songtable, database_song_callback, NULL, NULL);
            //更新singer表

            sqlite3_exec(sqlite3_handle, select_singer, database_singer_callback, NULL, NULL);

            if (NULL != sqlite3_handle) {
                sqlite3_close(sqlite3_handle);
            }
            tmp_str = strstr(line, g_UpdatePath);

            //设立将数据库版本设置为最新
            if (tmp_str != NULL) {
                if (atoi(tmp_str + strlen(g_UpdatePath)) == 0) {
                    sprintf(file_name, "update version set version=\"%s\"",
                            versionlast_cArray);
                    strcpy(versionLocal_cArray, versionlast_cArray);
                } else {
                    //把本地的数据库拷贝成当前文件版本
                    sprintf(file_name, "update version set version=\"%s\"",
                            tmp_str + strlen(g_UpdatePath));
                    strcpy(versionLocal_cArray,
                           tmp_str + strlen(g_UpdatePath));
                }

                sqlite3_execute_songSqlbyHandle(file_name, g_UpdatingngDb);
            }
        }
    }

    fclose(fp);

    if(0 == g_RankDownRet){
        upDateDb_ReSortTopSongFromNetRank();
    }

    upDateDb_Grooming();

    return 0;
}


int database_topsong_callback(void * para, int n_column, char ** column_value, char ** column_name)
{
//    if( sUpdateDBStop ){
//        LOGD("sUpdateDBStop true, return database_grooming");
//        return 0;
//    }

    int i = 0;
    char sql_cmd[1024] = "";

    for( i = 0 ; i < n_column; i++ )
    {
        if(strcmp(column_name[i],"songid")==0)
        {
            if(column_value[i] != NULL)
            {
                memset(sql_cmd, '\0', sizeof(sql_cmd));
                sprintf(sql_cmd, "insert or replace into top_song select * from song where song_id = '%s'", column_value[i]);
                sqlite3_execute_songSqlbyHandle(sql_cmd, g_UpdatingngDb);
                //printf("============sql_cmd = %s\n", sql_cmd);
            }
            break;
        }
    }

    return 0;
}

int upDateDb_ReSortTopSongFromNetRank(){


    sqlite3 *sqlite3_handleNetRandkDB=NULL;
    char *select_topsong = "select songid from month order by count desc";

    char rank_db[256] = "";
    sprintf(rank_db, "%s%s", g_UpdatePath, DB_RANK);

    int ret = sqlite3_open(rank_db,&sqlite3_handleNetRandkDB);

    if (SQLITE_OK!=ret)
    {
        printf(" upDateDb_ReSortTopSongFromNetRank sqlite3_open_DB db fail result is %d filenae is %s\r\n", ret, rank_db);
        return -1;
    }
    char praSql[256] = "";
//    sprintf(praSql, "PRAGMA temp_store_directory = \'%s\'", g_ParaPath);
    sprintf(praSql, "PRAGMA temp_store = 2");
    sqlite3_execute_songSqlbyHandle(praSql, sqlite3_handleNetRandkDB);
    sqlite3_execute_songSqlbyHandle("delete from top_song", g_UpdatingngDb);
    sqlite3_exec(sqlite3_handleNetRandkDB,select_topsong, database_topsong_callback, NULL,NULL);


    if (NULL!=sqlite3_handleNetRandkDB)
    {
        sqlite3_close(sqlite3_handleNetRandkDB);
        sqlite3_handleNetRandkDB = NULL;
    }

}
void upDateDb_Grooming()
{
    char new_song_theme[256] = "";
    char sql[1024] = "";
    struct stat file_buf;

    LOGD("=========================upDateDb_Grooming update song=========================\n");
    //如果新歌日期为“空格”的话，设置为“空”
    sqlite3_execute_songSqlbyHandle(
            "update song set new_song_date = '' where new_song_date = ' '", g_UpdatingngDb);

    LOGD("=========================upDateDb_Grooming DB_Init_Top_Song=========================\n");
    //重新初始化排行
    LOGD("=========================upDateDb_Grooming PRAGMA synchronous = NORMAL=========================\n");
    sqlite3_execute_songSqlbyHandle("PRAGMA synchronous = NORMAL", g_UpdatingngDb);


    LOGD("=========================upDateDb_Grooming VACUUM=========================\n");
    sqlite3_execute_songSqlbyHandle("VACUUM", g_UpdatingngDb);    //压缩数据库


    if(1)
    {
        LOGD("=========================upDateDb_Grooming Re_Sort_DB=========================\n");
        //重新对数据库进行排序

        upDateDb_ReSortDb();

        LOGD("=========================upDateDb_Grooming PRAGMA synchronous = NORMAL=========================\n");
        sqlite3_execute_songSqlbyHandle("PRAGMA synchronous = NORMAL", g_UpdatingngDb);


        LOGD("=========================upDateDb_Grooming VACUUM=========================\n");
        sqlite3_execute_songSqlbyHandle("VACUUM", g_UpdatingngDb);	//压缩数据库
        //压缩完后需要重新开启
    }

    return ;
}

void *ttttttt(void *arg){
    sleep(3);
//    int result=0;
//    char updateDbName[256] = "";
//    sprintf(updateDbName, "%sktv10.db_bak", g_UpdatePath);
//
//sqlite3 * dbbb = NULL;
//    result = sqlite3_open(updateDbName, &dbbb);
//
////    result = sqlite3_open(":memory:", &g_sqlite3_handle);
////    result = loadOrSaveDb(g_sqlite3_handle, file_path, 0); //�ļ����ݿ⵼�뵽�ڴ����ݿ�
//
//    if (SQLITE_OK!=result)
//    {
//        printf(" ttttttt db fail result is %d db name is %s++++\r\n", result, updateDbName);
//        return -1;
//    }
//    else
    {
        char aaa[64] = "";
        KTVDB_mirror_get_field_data(g_UpdatingngDb,"select version from version", aaa);
        printf("ttttttt KTVDB_mirror_get_field_data aaa is %s\n", aaa);
        return 0;
    }
}

int upDateDb_ReSortDb()
{
    sqlite3_execute_songSqlbyHandle("delete from song_bak", g_UpdatingngDb);
    //设置culture_code字段值。1：国语/粤语/闽南，2：英语，3：日语，4：韩语，5：其他。
    sqlite3_execute_songSqlbyHandle(
            "update song set culture_code = '1' where language = '1' or language = '2' or language = '3'", g_UpdatingngDb);

    sqlite3_execute_songSqlbyHandle("update song set culture_code = '2' where language = '4'", g_UpdatingngDb);

    sqlite3_execute_songSqlbyHandle("update song set culture_code = '3' where language = '6'", g_UpdatingngDb);

    sqlite3_execute_songSqlbyHandle("update song set culture_code = '4' where language = '5'", g_UpdatingngDb);

    sqlite3_execute_songSqlbyHandle(
            "update song set culture_code = '5' where language not in ('1','2','3','4','5','6')", g_UpdatingngDb);

//    pthread_t download_thread_t=0;
//    pthread_attr_t  attr;
//    size_t stacksize = 2048*1024;
//    struct sched_param schedling_value;
//    pthread_attr_init(&attr);
//    pthread_attr_getschedparam(&attr, &schedling_value);
//    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
//    pthread_attr_setschedpolicy(&attr, SCHED_OTHER);
//    pthread_attr_setstacksize(&attr, stacksize);
//    pthread_attr_setschedparam(&attr, &schedling_value);
//
//    ////创建发送线程
//    if(pthread_create(&download_thread_t,&attr,ttttttt,NULL))
//    {
//
//        perror("Error::::::::[upDateDb_StartThread.c Create Thread Failed]");
//    }
    sqlite3_execute_songSqlbyHandle("insert into song_bak select * from song	order by song_name_word_count,culture_code,spell_first_letter_abbreviation,song_name,lyric,singer_name", g_UpdatingngDb);

    //清空song表
    sqlite3_execute_songSqlbyHandle("delete from song", g_UpdatingngDb);

    sqlite3_execute_songSqlbyHandle("insert into song select * from song_bak", g_UpdatingngDb);

    sqlite3_execute_songSqlbyHandle("delete from song_bak", g_UpdatingngDb);

    sqlite3_execute_songSqlbyHandle("insert into singer_bak select * from singer order by singer_hot_rank desc",g_UpdatingngDb);

    sqlite3_execute_songSqlbyHandle("delete from singer", g_UpdatingngDb);

    sqlite3_execute_songSqlbyHandle("insert into singer select * from singer_bak", g_UpdatingngDb);

    sqlite3_execute_songSqlbyHandle("delete from singer_bak", g_UpdatingngDb);

    return 0;
}

int database_song_callback(void * para, int n_column, char ** column_value, char ** column_name)
{
//    if( sUpdateDBStop ){
//        return 0;
//    }

    int i = 0;
    int j = 0;
    int file_exist = 0;
    char song_id[32] = "";
    char tmp_str[128] = "";

    int result = 0;

    char sql_cmd[2*1024] = "insert or replace into song(song_id,accompany_sing_track,karaoke_track,song_name,movie_name,show_movie_name,accompany_volume,karaoke_volume,language,song_type,song_name_word_count,singer_name,singer_sex,spell_first_letter_abbreviation,moive_spell_first_letter,sing_number,song_version,light_control_set,song_theme,new_song_theme,first_word_stroke_number,local_path,new_song_date,singer_id1,singer_id2,singer_id3,singer_id4) values( ";

    for( i = 0 ; i < n_column; i++ )
    {
        //如果存在"，将内容设为""
        char tempColumn[4096] = {};

        char *p = 0;
        char *pos = 0;

        if( column_value[i] != 0 && strlen(column_value[i] ) > 0 ) {
            strcpy(tempColumn, column_value[i]);
            p = tempColumn;
            pos = 0;

            char buff[1024] = {};
            //插入“”
            while( (pos = strstr( p, "\"" )) != 0  ){
                strcpy(buff, pos+1  );

                strcpy( pos+1, "\"");
                int size = strlen("\"");
                strcpy( pos+1+size, buff );
                char* end = pos+1+size+strlen(buff)+1;
                end = "\0";
                p= pos+1+size;
            }

        }




        strcat(sql_cmd, "\"");

        switch(i)
        {
            case 0:
                if(strcmp(column_name[i], "song_id") == 0)
                {
                    strcpy(song_id, tempColumn);
                    strcat(sql_cmd, tempColumn);
                }
                break;
            case 20:
                if(strcmp(column_name[i], "first_word_stroke_number") == 0)
                {
                    if(strlen(song_id) > 0)
                    {
//                        //sprintf(tmp_str, "%sbco/%s.bco", MAIN_ROOT_PATH, song_id);
//                        // strrpc(tmp_str, MAIN_ROOT_PATH, MAIN_ROOT_PATH_2ACCESS);
//                        if(isBcoSong(song_id))
//                        {
//                            strcat(sql_cmd, "1");
//                        }
//                        else
//                        {
//                            strcat(sql_cmd, "0");
//                        }
                        strcat(sql_cmd, "0");

                    }
                }
                break;
            case 21:
                if(strcmp(column_name[i], "local_path") == 0)
                {
                    if(strlen(song_id) > 0)
                    {
                        //本地歌曲

                        //for(j=0; j<disk_counts; j++)
                        //{
                        //sprintf(tmp_str,"%s%s.mpg",CLOUDSERVE_DOWNLOADSONGPATH, song_id);
                        //LOGD(" shinektv stop is %s", tmp_str);
//                        if(strcmp(CLOUDSERVE_DOWNLOADSONGPATH, MAIN_ROOT_PATH2)==0)
//                            sprintf(tmp_str,"%s%s.mpg",MAIN_ROOT_PATH2_2ACCESS, song_id);
//                        //strrpc(tmp_str, MAIN_ROOT_PATH2, MAIN_ROOT_PATH2_2ACCESS);
//                        //LOGD(" shinektv stop is copy over %s", tmp_str);
//                        if(access(tmp_str,0) == 0)
//                        {
//                            file_exist = 1;
//                            //break;
//                        }
//
//                        if(strcmp(CLOUDSERVE_DOWNLOADSONGPATH, MAIN_ROOT_PATH2)==0)
//                            sprintf(tmp_str,"%s%s.MPG",MAIN_ROOT_PATH2_2ACCESS, song_id);
//                        //strrpc(tmp_str, MAIN_ROOT_PATH2, MAIN_ROOT_PATH2_2ACCESS);
//
//                        //LOGD("accccccccc is %s", tmp_str);
//                        if(access(tmp_str,0) == 0)
//                        {
//                            file_exist = 1;
//                            //break;
//                        }
//                        //}
//
//                        if(file_exist)
//                        {
//                            strcat(sql_cmd, "0");
//                        }
//                        else
//                        {
//                            strcat(sql_cmd, "1");
//                        }
                            strcat(sql_cmd, "1");
                    }
                }
                break;
            default:
                if(tempColumn != NULL)
                {
                    strcat(sql_cmd, tempColumn);
                }
                break;
        }


        strcat(sql_cmd, "\",");
    }

    if(i != 0)
    {
        sql_cmd[strlen(sql_cmd) - 1] = ')';

        //printf("======================%s %d: sql_cmd = %s", __FILE__, __LINE__, sql_cmd);
        //pthread_mutex_lock(&mutex_db_write);
        //DB_exec_sql(sql_cmd);
        struct timeval tv;
        gettimeofday(&tv,NULL);
        long btime = tv.tv_sec * 1000 + tv.tv_usec / 1000;


        result = sqlite3_execute_songSqlbyHandle(sql_cmd, g_UpdatingngDb);
        gettimeofday(&tv,NULL);


        sCountframe++;

        if(  (tv.tv_sec * 1000 + tv.tv_usec / 1000 ) - sCountTime > 1000 ){
            LOGD("dfdf database_song_callback run frame is %d", sCountframe);
            sCountframe = 0;
            sCountTime = tv.tv_sec * 1000 + tv.tv_usec / 1000;
        }


        LOGD("after exec sqllllllllll time process is %ld", tv.tv_sec * 1000 + tv.tv_usec / 1000 - btime);

        //pthread_mutex_unlock(&mutex_db_write);
    }


    if( result != 1 ){
        LOGD("dfdf startserver length ========= sql write wrong %s %s, %d, %s",sql_cmd, __FILE__, __LINE__, __FUNCTION__);
    }
    else {
        LOGD("dfdf startserver length ========= sql write success %s %s, %d, %s",sql_cmd, __FILE__, __LINE__, __FUNCTION__);
    }

    return 0;
}

//"select singer_name,singer_sex,singer_region,singer_region_new,spell_first_letter_abbreviation,singer_name_word_count,singer_hot_rank from singer";
int database_singer_callback(void * para, int n_column, char ** column_value, char ** column_name)
{
//    if( sUpdateDBStop ){
//        return 0;
//    }

    int i = 0;
    char sql_cmd[2*1024] = "insert or replace into singer(singer_id,singer_name,singer_sex,singer_region,singer_region_new,spell_first_letter_abbreviation,singer_name_word_count,singer_hot_rank) values( ";

    int result = 0;

    for( i = 0 ; i < n_column; i++ )
    {
        strcat(sql_cmd, "\"");
        if(column_value[i] != NULL)
        {
            strcat(sql_cmd, column_value[i]);
        }
        strcat(sql_cmd, "\",");
    }

    if(i != 0)
    {
        sql_cmd[strlen(sql_cmd) - 1] = ')';
        printf("======================%s %d: sql_cmd = %s", __FILE__, __LINE__, sql_cmd);

        result = sqlite3_execute_songSqlbyHandle(sql_cmd, g_UpdatingngDb);

    }

    if(result != 1){
        LOGD("dfdf startserver length ========= sql write wrong %s %s, %d, %s",sql_cmd, __FILE__, __LINE__, __FUNCTION__);
    }
    else{
        LOGD("dfdf startserver length ========= sql write wrong %s %s, %d, %s",sql_cmd, __FILE__, __LINE__, __FUNCTION__);
    }

    return 0;
}

int DB_Init_Song_Bak()
{
    char sql[1024];
    char cnt = 0;
    int result = 0;

    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='song_bak'");
    result = sqlite3_execute_getrowByHandle(sql, g_UpdatingngDb);
    //printf("\n\n-----------------create song_bak result = %d-----------------\n\n", result);
    if(result == 0)
    {
        sqlite3_execute_songSqlbyHandle("CREATE TABLE [song_bak] (\
                                               [song_id] text,\
                                               [accompany_sing_track] text,\
                                               [karaoke_track] text,\
                                               [guid_track] text,\
                                               [song_name] text,\
                                               [movie_name] text,\
                                               [show_movie_name] text,\
                                               [song_name_sort] INTEGER,\
                                               [accompany_volume] INTEGER,\
                                               [karaoke_volume] INTEGER,\
                                               [language] text,\
                                               [song_type] text,\
                                               [song_name_word_count] INTEGER,\
                                               [singer_number] INTEGER,\
                                               [singer_name] text,\
                                               [singer_sex] text,\
                                               [spell_first_letter_abbreviation] text,\
                                               [spell_first_letter_traditional] text,\
                                               [moive_spell_first_letter] text,\
                                               [sing_number] INTEGER DEFAULT 0,\
                                               [song_version] text,\
                                               [light_control_set] INTEGER,\
                                               [audio_effect_code] INTEGER,\
                                               [file_format] text,\
                                               [song_bit_rate] real,\
                                               [song_theme] text,\
                                               [new_song_theme] text,\
                                               [first_word_stroke_number] INTEGER,\
                                               [local_path] text,\
                                               [server_path1] ,\
                                               [server_path2] ,\
                                               [server_path3] ,\
                                               [server_path4] ,\
                                               [server_path5] ,\
                                               [server_path6] ,\
                                               [song_relative_path] ,\
                                               [file_size] INTEGER,\
                                               [video_saturation] INTEGER,\
                                               [video_luminance] INTEGER,\
                                               [video_contrast] INTEGER,\
                                               [lyric] ,\
                                               [word_head_code] ,\
                                               [culture_code] ,\
                                               [issue_year] ,\
                                               [new_song_date] ,\
                                               [preview_path] ,\
                                               [singer_id1] TEXT,\
                                               [singer_id2] TEXT,\
                                               [singer_id3] TEXT,\
                                               [singer_id4] TEXT,\
                                               [file_version] TEXT,\
                                               CONSTRAINT [sqlite_autoindex_song_1] PRIMARY KEY ([song_id]));", g_UpdatingngDb);
        LOGD("\n-----------------DB_Init_Song_Bak create song_bak sussess-----------------\n");
    }
    sqlite3_execute_songSqlbyHandle("delete from song_bak", g_UpdatingngDb);
  ;
    return 0;
}

int DB_Init_Song()
{
    char sql[1024];
    char cnt = 0;
    int result = 0;

    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='song'");
    result = sqlite3_execute_getrowByHandle(sql, g_UpdatingngDb);
    //printf("\n\n-----------------create song_bak result = %d-----------------\n\n", result);
    if(result == 0)
    {
        sqlite3_execute_songSqlbyHandle("CREATE TABLE [song] (\
                                               [song_id] text,\
                                               [accompany_sing_track] text,\
                                               [karaoke_track] text,\
                                               [guid_track] text,\
                                               [song_name] text,\
                                               [movie_name] text,\
                                               [show_movie_name] text,\
                                               [song_name_sort] INTEGER,\
                                               [accompany_volume] INTEGER,\
                                               [karaoke_volume] INTEGER,\
                                               [language] text,\
                                               [song_type] text,\
                                               [song_name_word_count] INTEGER,\
                                               [singer_number] INTEGER,\
                                               [singer_name] text,\
                                               [singer_sex] text,\
                                               [spell_first_letter_abbreviation] text,\
                                               [spell_first_letter_traditional] text,\
                                               [moive_spell_first_letter] text,\
                                               [sing_number] INTEGER DEFAULT 0,\
                                               [song_version] text,\
                                               [light_control_set] INTEGER,\
                                               [audio_effect_code] INTEGER,\
                                               [file_format] text,\
                                               [song_bit_rate] real,\
                                               [song_theme] text,\
                                               [new_song_theme] text,\
                                               [first_word_stroke_number] INTEGER,\
                                               [local_path] text,\
                                               [server_path1] ,\
                                               [server_path2] ,\
                                               [server_path3] ,\
                                               [server_path4] ,\
                                               [server_path5] ,\
                                               [server_path6] ,\
                                               [song_relative_path] ,\
                                               [file_size] INTEGER,\
                                               [video_saturation] INTEGER,\
                                               [video_luminance] INTEGER,\
                                               [video_contrast] INTEGER,\
                                               [lyric] ,\
                                               [word_head_code] ,\
                                               [culture_code] ,\
                                               [issue_year] ,\
                                               [new_song_date] ,\
                                               [preview_path] ,\
                                               [singer_id1] TEXT,\
                                               [singer_id2] TEXT,\
                                               [singer_id3] TEXT,\
                                               [singer_id4] TEXT,\
                                               [file_version] TEXT,\
                                               CONSTRAINT [sqlite_autoindex_song_1] PRIMARY KEY ([song_id]));", g_UpdatingngDb);
        LOGD("\n-----------------DB_Init_Song create song_bak sussess-----------------\n");
    }
    //sqlite3_execute_songSqlbyHandle("delete from song", g_UpdatingngDb);

    return 0;
}


int DB_Init_Singer_Bak(){
    char sql[1024] = "";
    int result = 0;

    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='singer_bak'");
    result = sqlite3_execute_getrowByHandle(sql, g_UpdatingngDb);
    if( result == 0 ){
        sqlite3_execute_songSqlbyHandle("CREATE TABLE singer_bak(singer_id text PRIMARY KEY,singer_name text ,singer_sex,singer_region,singer_region_new,\
					popular_singer,spell_first_letter_abbreviation,singer_name_word_count INTEGER,\
					singer_hot_rank INTEGER default 0,singer_introduction  INTEGER default 0,\
                  singer_region_qy2 INTEGER default 0, local_click_rank text Default 0)", g_UpdatingngDb);

        sqlite3_execute_songSqlbyHandle(
                "CREATE INDEX singer_spell_first_letter_abbreviation on singer_bak(spell_first_letter_abbreviation)", g_UpdatingngDb);
        sqlite3_execute_songSqlbyHandle(
                "CREATE INDEX singer_singer_region_new on singer_bak(singer_region_new)", g_UpdatingngDb);
        sqlite3_execute_songSqlbyHandle("CREATE INDEX singer_singer_name on singer_bak(singer_name)", g_UpdatingngDb);
    } else{
        sqlite3_execute_songSqlbyHandle("alter table singer_bak add column local_click_rank TEXT Default \"0\"", g_UpdatingngDb);
    }
    sqlite3_execute_songSqlbyHandle("delete from singer_bak", g_UpdatingngDb);

    return 0;
}

int DB_Init_Singer() {
    char sql[1024] = "";
    int result = 0;
    char singer_region_new[256] = "";
    char cnt = 0;

    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='singer'");
    result = sqlite3_execute_getrowByHandle(sql, g_UpdatingngDb);
    if (result == 0) {
        sqlite3_execute_songSqlbyHandle("CREATE TABLE singer(singer_id text PRIMARY KEY,singer_name text ,singer_sex,singer_region,singer_region_new,\
					popular_singer,spell_first_letter_abbreviation,singer_name_word_count INTEGER,\
					singer_hot_rank INTEGER default 0,singer_introduction  INTEGER default 0,\
                  singer_region_qy2 INTEGER default 0, local_click_rank text Default 0)", g_UpdatingngDb);

        sqlite3_execute_songSqlbyHandle(
                "CREATE INDEX singer_spell_first_letter_abbreviation on singer(spell_first_letter_abbreviation)", g_UpdatingngDb);

        sqlite3_execute_songSqlbyHandle(
                "CREATE INDEX singer_singer_region_new on singer(singer_region_new)", g_UpdatingngDb);

        sqlite3_execute_songSqlbyHandle("CREATE INDEX singer_singer_name on singer(singer_name)", g_UpdatingngDb);


        LOGD("\n\n-----------------DB_Init_Singer create singer sussess-----------------\n\n");
    }


    return 0;
}