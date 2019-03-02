

#include <stdio.h>
#include <sys/stat.h>
#include "../sqlite3_method.h"
#include "UpdateDBVersion.h"
#include "../ShineLog.h"
#include "../ShinePath.h"
#include "../cloudserver/CloudAddSong.h"

//extern  sqlite3 *g_Handle_KTVDB_Mirror;
//extern char db_local_version_frm_txt[128];
//extern char db_net_version_frm_txt[128];
//get net_version

//extern char db_net_version_frm_txt[128];

//extern char db_local_version_frm_txt[128];

extern int mWriteDBCnt;

char *strrpc(char *str,char *oldstr,char *newstr) {
    int i = 0;
    char str1[strlen(str)];//存放结果的字符串
    char s[strlen(oldstr)];//存放头部字符串

    memset(str1, 0, sizeof(str1));//设置存放结果字符串为0

    for (; i < strlen(str); i++) {
        memset(s, 0, sizeof(s));
        strncpy(s, str + i, strlen(oldstr));//将头部字符放入数组
        if (!strncmp(s, oldstr, strlen(oldstr))) {//判断该位置前头部字符是否是被替换者
            strcat(str1, newstr);
            i += strlen(oldstr);
        }
        strncat(str1, str + i, 1);//存入一字节到结果数组
    }
    strcpy(str, str1);
    return str;
}

//初始化版本表
int DB_Init_Version()
{
    char sql[1024];
    int result = 0;

    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='version'");
    result = KTVDB_mirror_row_count(sql);
    //printf("\n\n-----------------create version result = %d-----------------\n\n", result);
    if(result == 0)
    {
        //create the version table
        KTVDB_mirror_execute_songSql("CREATE TABLE version (version text)");
        KTVDB_mirror_execute_songSql("insert into  version(version) values('0')");
        printf("\n\n-----------------create version version sussess-----------------\n\n");
    }
    else{
        int result = KTVDB_mirror_row_count("select count(*) from version;");
        if( result == 0 ) {
            KTVDB_mirror_execute_songSql("insert into  version(version) values('0')");
        }
    }
    return 0;
}




int MainFuncUpdatedbVersion(){
    LOGD("dfdf MainFuncUpdatedbVersion  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    KTVDB_mirror_execute_songSql("CREATE INDEX IF NOT EXISTS song_local_path on song(local_path)");//insert index
    KTVDB_mirror_execute_songSql("CREATE INDEX IF NOT EXISTS song_word_head_code on song(word_head_code)");//insert index
    //去除联合索引
    KTVDB_mirror_execute_songSql("CREATE INDEX IF NOT EXISTS date_theme on song (new_song_date, song_theme);");
    KTVDB_mirror_execute_songSql("PRAGMA synchronous = OFF");//synchronous, wait for the operate over then synchronous
    LOGD("dfdf MainFuncUpdatedbVersion  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);


    DB_Init_Version();
    LOGD("dfdf MainFuncUpdatedbVersion  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);


    KTVDB_mirror_execute_songSql("PRAGMA synchronous = NORMAL");
    LOGD("dfdf MainFuncUpdatedbVersion  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    return 0 ;
}

int KTVDBMirrorInitNewTable(){
    LOGD("dfdf MainFuncUpdatedbVersion  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

    KTVDB_mirror_execute_songSql("CREATE INDEX IF NOT EXISTS song_local_path on song(local_path)");//insert index
    //KTVDB_mirror_execute_songSql("CREATE TABLE version (version text)");//test insert index
    KTVDB_mirror_execute_songSql("CREATE INDEX IF NOT EXISTS song_word_head_code on song(word_head_code)");//insert index
    //去除联合索引
    KTVDB_mirror_execute_songSql("CREATE INDEX IF NOT EXISTS date_theme on song (new_song_date, song_theme);");
    KTVDB_mirror_execute_songSql("PRAGMA synchronous = OFF");//synchronous, wait for the operate over then synchronous
    LOGD("dfdf MainFuncUpdatedbVersion  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

    //歌星点击率创建列
    KTVDB_mirror_execute_songSql("alter table singer add column local_click_rank TEXT Default \"0\"");

    DB_Init_Version();
    //初始化金曲排行表
    DB_Init_Top_Song();//热门歌曲
    //已唱列表
    //DB_Init_Sing_Song();//暂时应该不需要
    //初始化新歌表

    //初始会员歌曲 Member_Song

    DB_Init_Song_Bak();//该函数创建一个备份表， 主要用于排序后， 替换数据

    DB_Init_Singer();//该函数创建一个备份表， 主要用于排序后， 替换数据

    DB_Init_Singer_Bak();//创建歌星排序表

    KTVDB_mirror_execute_songSql("PRAGMA synchronous = NORMAL");
    LOGD("dfdf MainFuncUpdatedbVersion  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    return 0 ;
}

int LogGetCountErr(){
    int result = KTVDB_mirror_row_count("select count(song_id) from song");
    if( result == 0 ){
        LOGD("dfdf check the db count is 0 %s, %d, %s", __FILE__, __LINE__, __FUNCTION__);
    }
    int resultSinger = KTVDB_mirror_row_count("select count(singer_id) from singer");
    if( resultSinger == 0 ){
        LOGD("dfdf check the db count is 0 %s, %d, %s", __FILE__, __LINE__, __FUNCTION__);
    }
}

int update_test(){
    //sqlite3 *sqlite3_handle=NULL;
    //sqlite3_open("/mnt/sata/update_db/1", &sqlite3_handle);

    DB_Init_Song_Bak();
    KTVDB_mirror_execute_songSql("insert into song_bak select * from song	order by song_name_word_count,culture_code,spell_first_letter_abbreviation,song_name,lyric,singer_name");
    KTVDB_mirror_execute_songSql("VACUUM");
    //KTVDB_mirror_execute_songSql("delete from song_bak");
}

//解析服务器发送的数据库更新文件
int download_parse_database_update()
{
    //update_test();
    //return 0;

//    if( sUpdateDBStop ){
//        return  0;
//    }

    mWriteDBCnt = 0;

    FILE *fp = NULL;
    //FILE *fp_delete = NULL;
    //char sql_cmd[2*1024] = ";
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

    //在这里重新读取数据库版本， 数据库版本存储在 updateversion.txt,并且该文件是判断是否更新的依据
    //
    //FILE *fpVersionTxt = NULL;
    //这里先写固定值
    //char pathVersionTxt[1024] = "";
    //sprintf(pathVersionTxt, "%s/%s",CLOUDSERVE_DOWNLOADDBPATH, "upodateversion.txt");
    //fpVersionTxt = fopen(pathVersionTxt,"r");

    //char cDataVersion[1024] = "";

    /*
    if(db_net_version_frm_txt == NULL){ //没有对应的值， 这里本不该执行到这里
        LOGD("dfdf error wrong excute  %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);

        return -1;
    }
    else{
        int result = fread(cDataVersion, sizeof(cDataVersion), 1, fpVersionTxt);
        if( result > 0 ){
            strcpy(db_net_version_frm_txt, cDataVersion);
        }
        else{
            return -1;
        }
    }
    */



        sprintf(file_name, "%s/%s", CLOUDSERVE_DOWNLOADDBPATH, DB_LIST_TXT);
        printf("-----------------file_name = %s--------------\n\n", file_name);

        fp = fopen(file_name, "r");
        if (fp == NULL) {
            perror("Open datebase update file Failed: ");
            return -1;
        }


        KTVDB_mirror_execute_songSql("PRAGMA synchronous = OFF");

        //KTVDB_mirror_execute_songSql("PRAGMA auto_vacuum = 1");

        LogGetCountErr();

//开始插表
        //不断检测文件是否结束
        while (!feof(fp)) {
//            if( sUpdateDBStop ){
//                break;
//            }

            memset(line, '\0', sizeof(line));

            if (fgets(line, sizeof(line), fp) != NULL) {
                if (line[strlen(line) - 1] == '\n') {
                    line[strlen(line) - 1] = '\0';
                }
                printf("============%s %d: line = %s\n", __FILE__, __LINE__, line);

#ifdef USE_SATA_MODE_OPENDB
                //�ı����ݿ�
    ret = sqlite3_open(line, &sqlite3_handle);
#else
                //�ڴ����ݿ� ,����7229 ȡ���ڴ����ݿ⡣
                ret = sqlite3_open(":memory:", &sqlite3_handle);
                ret = loadOrSaveDb(sqlite3_handle, line, 0); //�ļ����ݿ⵼�뵽�ڴ����ݿ�
#endif

                //ret = sqlite3_open(line, &sqlite3_handle);

                if (SQLITE_OK != ret) {
                    printf("============%s %d: sqlite open %s db fail\n", __FILE__, __LINE__, line);
                    continue;
                }

                sqlite3_execute_songSqlbyHandle("PRAGMA temp_store_directory = '/data/data/com.shinektv.vod/'", sqlite3_handle);
                //由于一次读表大表会内存
                //更新song表
                LogGetCountErr();
                sCountTime = 0;
                sCountframe  = 0;
                struct timeval tv;
                gettimeofday(&tv,NULL);
                sCountTime = tv.tv_sec * 1000 + tv.tv_usec / 1000;
                sqlite3_exec(sqlite3_handle, select_songtable, database_song_callback, NULL, NULL);
                //更新singer表
                LogGetCountErr();
                sqlite3_exec(sqlite3_handle, select_singer, database_singer_callback, NULL, NULL);
                LogGetCountErr();
                //禁播暂时不用
                //删除禁播歌曲
                //if(fp_delete != NULL)
                {
                    //    sqlite3_exec(sqlite3_handle,select_bansong, database_bansong_callback, fp_delete,NULL);
                }

                //禁播暂时不用
                //删除禁播歌星
                //sqlite3_exec(sqlite3_handle,select_bansinger, database_bansinger_callback,NULL,NULL);
                LogGetCountErr();
                if (NULL != sqlite3_handle) {
                    sqlite3_close(sqlite3_handle);
                }
                LogGetCountErr();
                tmp_str = strstr(line, CLOUDSERVE_DOWNLOADDBPATH);

                //设立将数据库版本设置为最新
                if (tmp_str != NULL) {
                    if (atoi(tmp_str + strlen(CLOUDSERVE_DOWNLOADDBPATH) + 1) == 0) {
                        sprintf(file_name, "update version set version=\"%s\"",
                                db_net_version_frm_txt);
                        strcpy(db_local_version_frm_txt, db_net_version_frm_txt);
                    } else {
                        //把本地的数据库拷贝成当前文件版本
                        sprintf(file_name, "update version set version=\"%s\"",
                                tmp_str + strlen(CLOUDSERVE_DOWNLOADDBPATH) + 1);
                        strcpy(db_local_version_frm_txt,
                               tmp_str + strlen(CLOUDSERVE_DOWNLOADDBPATH) + 1);
                    }

                    //pthread_mutex_lock(&mutex_db_write);
                    //sqlite3_execute_sql(file_name);
                    //pthread_mutex_unlock(&mutex_db_write);
                    KTVDB_mirror_execute_songSql(file_name);
                    LogGetCountErr();
                }
            }
        }

        fclose(fp);

        /*
        if(strcmp(db_local_version, "0") == 0 )|| get_quanku_flag() == 1)
        {
            memset(file_name, '\0', sizeof(file_name));
            sprintf(file_name,"update version set version=\"%s\"",db_net_version);
            strcpy(db_local_version, db_net_version);
            pthread_mutex_lock(&mutex_db_write);
            sqlite3_execute_sql(file_name);
            pthread_mutex_unlock(&mutex_db_write);
        }
         */


    //数据库整理
    LogGetCountErr();
    database_grooming();
    LogGetCountErr();
    //更新数据库版本，并删除临时文件
    //当前版本暂时不增加版本服务
    /*
    if(fp_delete != NULL)
    {
        fclose(fp_delete);

        pthread_t thread__delete_t = 0;
        if(pthread_create(&thread__delete_t,NULL,delete_song_id_list,0L))
        {
            perror("================");
            return -1;
        }
        else
        {
            pthread_detach(thread__delete_t) ;//这里进行线程分离，这样线程退出时系统才会释放线程占用的内存
        }
    }
    */

    return 0;
}

int isBcoSong(char * songid){
    char songPath[128] = "";
    sprintf(songPath, "%s%s.MPG", MAIN_ROOT_PATH2_2ACCESS,songid);
    if(access(songPath, 0) != 0)
    {
        memset(songPath, 0, sizeof(songPath));
        sprintf(songPath, "%s%s.mpg", MAIN_ROOT_PATH2_2ACCESS,songid);
        if(access(songPath, 0) != 0){
            printf("isBcoSong 1111 songPath is %s\n", songPath);
            return 0;
        }
    }

    FILE *fp = NULL;
    fp = fopen(songPath, "r");
    if(NULL == fp){
        printf("isBcoSong 2222\n");
        return 0;
    }

    int ret = 0;
    ret = fseeko(fp,-(2048 + 2048 + 100* 1024 + 500*1024 + 500*1024 + 50*1024 + 50*1024 + 256),SEEK_END);
    if(0 != ret){
        fclose(fp);
        printf("isBcoSong 33333\n");
        return 0;
    }

    char readBuf[24] = "";
    char readString[64] = "";
    fread(readBuf, 1, 24, fp);
    int i = 0;
    int j = 0;
    for(i = 0;i<24;i++){
        if(0 != readBuf[i]){
            readString[j] = readBuf[i];
            j++;
        }
    }
    if(0 != strcmp(readString, "SHINEKTV3.0:") && 0 != strcmp(readString, "SHINEKTV2.0:")){
        fclose(fp);
        printf("isBcoSong 44444 readString is %s\n",  readString);
        return 0;
    }

    ret = fseeko(fp,-(500*1024 + 500*1024 + 50*1024 + 50*1024 + 256),SEEK_END);
    if(0 != ret){
        fclose(fp);
        printf("isBcoSong 555555\n");
        return 0;
    }

    memset(readBuf, 0, sizeof(readBuf));
    memset(readString, 0, sizeof(readString));
    fread(readBuf, 1, 16, fp);
    j = 0;
    for(i = 0;i<16;i++){
        if(0 != readBuf[i]){
            if(readBuf[i] < 48 || readBuf[i] > 57){
                fclose(fp);
                return 0;
            }
            readString[j] = readBuf[i];
            j++;
        }
    }

    ret = atoi(readString);
    fclose(fp);
    //printf("isBcoSong len is %d\n", ret);
    return ret;
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
                        //sprintf(tmp_str, "%sbco/%s.bco", MAIN_ROOT_PATH, song_id);
                       // strrpc(tmp_str, MAIN_ROOT_PATH, MAIN_ROOT_PATH_2ACCESS);
                        if(isBcoSong(song_id))
                        {
                            strcat(sql_cmd, "1");
                        }
                        else
                        {
                            strcat(sql_cmd, "0");
                        }

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
                            if(strcmp(CLOUDSERVE_DOWNLOADSONGPATH, MAIN_ROOT_PATH2)==0)
                                sprintf(tmp_str,"%s%s.mpg",MAIN_ROOT_PATH2_2ACCESS, song_id);
                            //strrpc(tmp_str, MAIN_ROOT_PATH2, MAIN_ROOT_PATH2_2ACCESS);
                            //LOGD(" shinektv stop is copy over %s", tmp_str);
                            if(access(tmp_str,0) == 0)
                            {
                                file_exist = 1;
                                //break;
                            }

                        if(strcmp(CLOUDSERVE_DOWNLOADSONGPATH, MAIN_ROOT_PATH2)==0)
                            sprintf(tmp_str,"%s%s.MPG",MAIN_ROOT_PATH2_2ACCESS, song_id);
                            //strrpc(tmp_str, MAIN_ROOT_PATH2, MAIN_ROOT_PATH2_2ACCESS);

                        //LOGD("accccccccc is %s", tmp_str);
                            if(access(tmp_str,0) == 0)
                            {
                                file_exist = 1;
                                //break;
                            }
                        //}

                        if(file_exist)
                        {
                            strcat(sql_cmd, "0");
                        }
                        else
                        {
                            strcat(sql_cmd, "1");
                        }
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


        result = sqlite3_execute_sql( get_ktvdb_mirror_handle(), sql_cmd );
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


    if( result == 0 ){
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
        //pthread_mutex_lock(&mutex_db_write);
        result = sqlite3_execute_sql(get_ktvdb_mirror_handle() ,sql_cmd);
        //pthread_mutex_unlock(&mutex_db_write);
    }

    if(result == 0){
        LOGD("dfdf startserver length ========= sql write wrong %s %s, %d, %s",sql_cmd, __FILE__, __LINE__, __FUNCTION__);
    }
    else{
        LOGD("dfdf startserver length ========= sql write wrong %s %s, %d, %s",sql_cmd, __FILE__, __LINE__, __FUNCTION__);
    }

    return 0;
}

int sqlite3_execute_sql(sqlite3 *sql_handler, const char *sql)
{
    char *error_msg=NULL;
    int result = 0;

    if (NULL==sql_handler)
    {
        return 0;
    }
    //操作的是备份数据库， 暂时不用锁， 避免java与c++之间用线程锁是否会产生问题
    //pthread_mutex_lock(&mutex_sqlite_handle);

    result = sqlite3_exec(sql_handler,sql,0,0,&error_msg);
    if(result != 0)
    {
        printf("==============%s %d: sql = %s\n", __FILE__, __LINE__, sql);
        printf("--------------%s-------------\n", error_msg);
    }

    //暂时不用
    //pthread_mutex_unlock(&mutex_sqlite_handle);

    //错误消息目前没有使用
    sqlite3_free(error_msg);
    if (SQLITE_OK==result)
    {
        return 1;
    }
    return 0;
}

int database_grooming()
{
//    if( sUpdateDBStop ){
//        LOGD("sUpdateDBStop true, return database_grooming");
//        return 0;
//    }

    char new_song_theme[256] = "";
        char sql[1024] = "";
        struct stat file_buf;

        //set_update_db_percent(82);
        //set_db_version_flag(HINT_UPDATE_DB_PERCENT);

        printf("=========================update song=========================\n");
        //如果新歌日期为“空格”的话，设置为“空”
        KTVDB_mirror_execute_songSql(
                "update song set new_song_date = '' where new_song_date = ' '");
        LogGetCountErr();
        //set_db_version_flag(HINT_UPDATE_DB_PERCENT);
        //如果有歌词，清空，单机不需要歌词，节省空间
        /*
        KTVDB_mirror_execute_songSql("update song set lyric = '' where lyric <> ''");
        LogGetCountErr();
        printf("=========================insert into new_song=========================\n");
        //重新更新新歌
        KTVDB_mirror_execute_songSql("delete from new_song");
        LogGetCountErr();
        KTVDB_mirror_execute_songSql("insert into new_song select * from song where new_song_date <> '' order by new_song_date desc,song_name_word_count asc limit 900");
        */
        LogGetCountErr();
        //set_update_db_percent(90);
        //set_db_version_flag(HINT_UPDATE_DB_PERCENT);

        printf("=========================DB_Init_Top_Song=========================\n");
        //重新初始化排行
        Re_Sort_Top_Song_From_Net_Rank();
        LogGetCountErr();

        //set_update_db_percent(93);
        //set_db_version_flag(HINT_UPDATE_DB_PERCENT);

        printf("=========================PRAGMA synchronous = NORMAL=========================\n");
        KTVDB_mirror_execute_songSql("PRAGMA synchronous = NORMAL");
        LogGetCountErr();
        char cKTVPath[1024] = {};
        sprintf(cKTVPath, "%s%s/%s", MAIN_ROOT_PATH, "KTV_VOD/shinedb", "ktv10.db_bak");
//    sprintf(cKTVPath, "/data/data/com.shinektv.vod/ktv10.db_bak");

        //stat("/media/C/ktv10db", &file_buf);
        stat(cKTVPath, &file_buf);
        if(file_buf.st_size > 60 * 1024 * 1024){
        //该函数如果判定过大文件， 会出错
        //if (1) {
            printf("=========================VACUUM=========================\n");
            KTVDB_mirror_execute_songSql("VACUUM");    //压缩数据库
            //压缩完要重新关开一次

            LogGetCountErr();
        }

        //set_update_db_percent(98);
        //set_db_version_flag(HINT_UPDATE_DB_PERCENT);

        //修改查询结果, 这里不需要
        //total_number_record(); //重新查询各个分类的记录数。
        //printf("==2===set_db_version_flag====db_version_flag==%d==\n",db_version_flag);

        //删除备份的数据库
        //SystemInstead("rm /media/C/db_bak/ktv10db -rf");

        //删除歌曲
        //Disk_deleteSong(200);
        //Disk_DeleteLocalSong();

        //set_db_version_flag(HINT_WHETHER_SORT_DB);



    if(1)
    //if(user_whether_sort_db == 1)		//排序
    {
        printf("=========================Re_Sort_DB=========================\n");
        //重新对数据库进行排序

        Re_Sort_DB();
        LogGetCountErr();

        printf("=========================PRAGMA synchronous = NORMAL=========================\n");
        KTVDB_mirror_execute_songSql("PRAGMA synchronous = NORMAL");

        char cKTVPath[1024] = {};
        sprintf(cKTVPath, "%s%s/%s",  MAIN_ROOT_PATH, "KTV_VOD/shinedb","ktv10.db_bak");
//        sprintf(cKTVPath, "/data/data/com.shinektv.vod/ktv10.db_bak");

        //该函数如果判定过大文件， 会出错
        stat(cKTVPath, &file_buf);
        if(file_buf.st_size > 60 * 1024 * 1024)
        //if(1)
        {
            printf("=========================VACUUM=========================\n");
            KTVDB_mirror_execute_songSql("VACUUM");	//压缩数据库
            //压缩完后需要重新开启

            LogGetCountErr();
        }

        //删除备份的数据库
        //SystemInstead("rm /media/C/db_bak/ktv10db -rf");
    }
    LogGetCountErr();
    return 0;
}

//当前版本， 不添加删除禁播歌曲
//在线程里删除禁播歌曲
/*
void *delete_song_id_list(void *args)
{
    FILE *fp_delete = NULL;
    char buf_read[32] = "";
    char cmd[128] = "";

    if((fp_delete = fopen(FILE_DELETE_TXT, "r")) == NULL)
    {
        perror("====================");
        return NULL;
    }

    while(!feof(fp_delete))
    {
        usleep(1);
        memset(buf_read, '\0', sizeof(buf_read));
        fgets(buf_read, sizeof(buf_read), fp_delete);
        if(strlen(buf_read) > 0)
        {
            //printf("=================%s %d: strlen(buf_read) = %d\n", __FILE__, __LINE__, strlen(buf_read));
            if(buf_read[strlen(buf_read) - 1] == '\n')
            {
                buf_read[strlen(buf_read) - 1] = '\0';
            }
            DeleteLocalSong_by_song_id(buf_read);
        }
    }

    fclose(fp_delete);

}
*/


int Re_Sort_DB()
{
//    if( sUpdateDBStop ){
//        LOGD("sUpdateDBStop true, return database_grooming");
//        return 0;
//    }


    //清空song_bak
    KTVDB_mirror_execute_songSql("delete from song_bak");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;

    //设置culture_code字段值。1：国语/粤语/闽南，2：英语，3：日语，4：韩语，5：其他。
    KTVDB_mirror_execute_songSql(
            "update song set culture_code = '1' where language = '1' or language = '2' or language = '3'");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    KTVDB_mirror_execute_songSql("update song set culture_code = '2' where language = '4'");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    KTVDB_mirror_execute_songSql("update song set culture_code = '3' where language = '6'");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    KTVDB_mirror_execute_songSql("update song set culture_code = '4' where language = '5'");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    KTVDB_mirror_execute_songSql(
            "update song set culture_code = '5' where language not in ('1','2','3','4','5','6')");
    LogGetCountErr();
    //for( int i = 10500; i < 11000; i++ ){


    //将song表重新排序后插入到song_bak表中
    //数据量过大， 数据库会打不开, 静默线程操作
    //int nCntsong_id = KTVDB_mirror_row_count("select count(song_id) from song order by song_name_word_count,culture_code,spell_first_letter_abbreviation,song_name,lyric,singer_name");

//    if( sUpdateDBStop ) return 0;
    KTVDB_mirror_execute_songSql("insert into song_bak select * from song	order by song_name_word_count,culture_code,spell_first_letter_abbreviation,song_name,lyric,singer_name");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    //清空song表
    KTVDB_mirror_execute_songSql("delete from song");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    //拷贝重新排序的song_bak到song
    KTVDB_mirror_execute_songSql("insert into song select * from song_bak");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    //再次清空song_bak
    KTVDB_mirror_execute_songSql("delete from song_bak");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    //order by singer_hot_rank desc
    KTVDB_mirror_execute_songSql("insert into singer_bak select * from singer order by singer_hot_rank desc");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    //清空song表
    KTVDB_mirror_execute_songSql("delete from singer");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    //拷贝重新排序的song_bak到song
    KTVDB_mirror_execute_songSql("insert into singer select * from singer_bak");
    LogGetCountErr();

//    if( sUpdateDBStop ) return 0;
    //再次清空song_bak
    KTVDB_mirror_execute_songSql("delete from singer_bak");
    LogGetCountErr();

    return 0;
}


//初始化金曲排行表 zhenyubin 2014-01-06
int DB_Init_Top_Song()
{
    LogGetCountErr();
    char sql[1024] = "";
    int result = 0;
    LogGetCountErr();
    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='top_song'");
    result = KTVDB_mirror_row_count(sql);
    if(result == 0)
    {
        KTVDB_mirror_execute_songSql("CREATE TABLE [top_song] (\
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
                                               CONSTRAINT [sqlite_autoindex_song_1] PRIMARY KEY ([song_id]));");
        LogGetCountErr();
        KTVDB_mirror_execute_songSql("CREATE INDEX top_song_language on top_song(language)");
        LogGetCountErr();
        printf("\n\n-----------------create top_song sussess-----------------\n\n");
    }

    //清空表， 后面排序后填充
    KTVDB_mirror_execute_songSql("delete from top_song");
    LogGetCountErr();

    //不能在这里排序， 可能song表里， 没有对应歌曲
    //Re_Sort_Top_Song_From_Net_Rank();

    LogGetCountErr();
    return 0;
}

//通过网络榜单更新热门榜单
int Re_Sort_Top_Song_From_Net_Rank(){
//    if( sUpdateDBStop ){
//        LOGD("sUpdateDBStop true, return database_grooming");
//        return 0;
//    }

    sqlite3 *sqlite3_handleNetRandkDB=NULL;
    char *select_topsong = "select songid from month order by count desc";

    char rank_db[256] = "";
    sprintf(rank_db, "%s%s/%s", MAIN_ROOT_PATH, "update_db", "shinerank.db");

    int ret = sqlite3_open(rank_db,&sqlite3_handleNetRandkDB);
    sqlite3_exec(sqlite3_handleNetRandkDB,select_topsong, database_topsong_callback, NULL,NULL);


    if (NULL!=sqlite3_handleNetRandkDB)
    {
        sqlite3_close(sqlite3_handleNetRandkDB);
        sqlite3_handleNetRandkDB = NULL;
    }

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
                KTVDB_mirror_execute_songSql(sql_cmd);
                //printf("============sql_cmd = %s\n", sql_cmd);
            }
            break;
        }
    }

    return 0;
}

int Re_Sort_Top_Song()
{
    char sql[1024] = "";
    char cnt = 0;
    int result = 0;
    LogGetCountErr();
    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='top_song_bak'");
    LogGetCountErr();
    result = KTVDB_mirror_row_count(sql);
    LogGetCountErr();
    if(result == 0)
    {
        KTVDB_mirror_execute_songSql("CREATE TABLE top_song_bak (\
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
                                               CONSTRAINT [sqlite_autoindex_song_1] PRIMARY KEY ([song_id]));");
        LogGetCountErr();
        printf("\n\n-----------------create top_song_bak sussess-----------------\n\n");
    }

    //清空song_bak
    KTVDB_mirror_execute_songSql("delete from top_song_bak");
    LogGetCountErr();
    KTVDB_mirror_execute_songSql("insert into top_song_bak select * from song where song_theme != '1' and language = '1' and sing_number > 0 order by sing_number desc limit 500 offset 0");
    LogGetCountErr();
    KTVDB_mirror_execute_songSql("insert into top_song_bak select * from song where song_theme != '1' and language = '2' and sing_number > 0 order by sing_number desc limit 100 offset 0");
    LogGetCountErr();
    KTVDB_mirror_execute_songSql("insert into top_song_bak select * from song where song_theme != '1' and language = '3' and sing_number > 0 order by sing_number desc limit 100 offset 0");
    LogGetCountErr();
    KTVDB_mirror_execute_songSql("insert into top_song_bak select * from song where song_theme != '1' and language = '4' and sing_number > 0 order by sing_number desc limit 100 offset 0");
    LogGetCountErr();
    KTVDB_mirror_execute_songSql("insert into top_song_bak select * from song where song_theme != '1' and language = '5' and sing_number > 0 order by sing_number desc limit 100 offset 0");
    LogGetCountErr();
    KTVDB_mirror_execute_songSql("insert into top_song_bak select * from song where song_theme != '1' and language = '6' and sing_number > 0 order by sing_number desc limit 100 offset 0");
    LogGetCountErr();
    KTVDB_mirror_execute_songSql("insert into top_song_bak select * from song where song_theme != '1' and language not in ('1','2','3','4','5','6') and sing_number > 0 order by sing_number desc limit 100 offset 0");
    LogGetCountErr();

    KTVDB_mirror_execute_songSql("insert into top_song select * from top_song_bak order by sing_number desc");
    LogGetCountErr();
    KTVDB_mirror_execute_songSql("delete from top_song_bak");
    LogGetCountErr();
    return 0;
}

int DB_Init_Song_Bak()
{
    char sql[1024];
    char cnt = 0;
    int result = 0;

    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='song_bak'");
    LogGetCountErr();
    result = KTVDB_mirror_row_count(sql);
    //printf("\n\n-----------------create song_bak result = %d-----------------\n\n", result);
    if(result == 0)
    {
        KTVDB_mirror_execute_songSql("CREATE TABLE [song_bak] (\
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
                                               CONSTRAINT [sqlite_autoindex_song_1] PRIMARY KEY ([song_id]));");
        LogGetCountErr();
        printf("\n-----------------create song_bak sussess-----------------\n");
    }
    KTVDB_mirror_execute_songSql("delete from song_bak");
    LogGetCountErr();
    return 0;
}

int DB_Init_Singer_Bak(){
    char sql[1024] = "";
    int result = 0;

    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='singer_bak'");
    result = KTVDB_mirror_row_count(sql);
    if( result == 0 ){
        KTVDB_mirror_execute_songSql("CREATE TABLE singer_bak(singer_id text PRIMARY KEY,singer_name text ,singer_sex,singer_region,singer_region_new,\
					popular_singer,spell_first_letter_abbreviation,singer_name_word_count INTEGER,\
					singer_hot_rank INTEGER default 0,singer_introduction  INTEGER default 0,\
                  singer_region_qy2 INTEGER default 0, local_click_rank text Default 0)");
        LogGetCountErr();
        KTVDB_mirror_execute_songSql(
                "CREATE INDEX singer_spell_first_letter_abbreviation on singer_bak(spell_first_letter_abbreviation)");
        LogGetCountErr();
        KTVDB_mirror_execute_songSql(
                "CREATE INDEX singer_singer_region_new on singer_bak(singer_region_new)");
        LogGetCountErr();
        KTVDB_mirror_execute_songSql("CREATE INDEX singer_singer_name on singer_bak(singer_name)");
    } else{
        KTVDB_mirror_execute_songSql("alter table singer_bak add column local_click_rank TEXT Default \"0\"");
    }
    KTVDB_mirror_execute_songSql("delete from singer_bak");
    LogGetCountErr();

    return 0;
}

int DB_Init_Singer() {
    char sql[1024] = "";
    int result = 0;
    char singer_region_new[256] = "";
    char cnt = 0;

    sprintf(sql, "select count(*) from sqlite_master where type='table' and name='singer'");
    result = KTVDB_mirror_row_count(sql);
    if (result == 0) {
        KTVDB_mirror_execute_songSql("CREATE TABLE singer(singer_id text PRIMARY KEY,singer_name text ,singer_sex,singer_region,singer_region_new,\
					popular_singer,spell_first_letter_abbreviation,singer_name_word_count INTEGER,\
					singer_hot_rank INTEGER default 0,singer_introduction  INTEGER default 0,\
                  singer_region_qy2 INTEGER default 0, local_click_rank text Default 0)");
        LogGetCountErr();
        KTVDB_mirror_execute_songSql(
                "CREATE INDEX singer_spell_first_letter_abbreviation on singer(spell_first_letter_abbreviation)");
        LogGetCountErr();
        KTVDB_mirror_execute_songSql(
                "CREATE INDEX singer_singer_region_new on singer(singer_region_new)");
        LogGetCountErr();
        KTVDB_mirror_execute_songSql("CREATE INDEX singer_singer_name on singer(singer_name)");
        LogGetCountErr();

        printf("\n\n-----------------create singer sussess-----------------\n\n");
    }

    return 0;
}
