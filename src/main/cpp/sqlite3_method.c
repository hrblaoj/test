//
// Created by hrblaoj on 2018/3/27.
//
#include <sys/types.h>
#include <stdio.h>
#include "sqlite3.h"
#include "sqlite3_method.h"
#include "cloudserver/ShineLog.h"
#include <android/log.h>
#include <stdbool.h>


#define printf(...) ((void)__android_log_print(ANDROID_LOG_INFO, "HTTPSERVER", __VA_ARGS__))

extern char MirrorDBPath[1024];

sqlite3 *get_handle()
{
    return g_sqlite3_handle;
}

sqlite3 *get_tapehandle()
{
    return Tape_Handle;
}

int row_callback(void* argv,int argc,char **value,char **name)
{
    //�ѵõ�����������
    int row_count = atoi(value[0]);

    memcpy(argv,&row_count,sizeof(int));

    return 0;
}

int sqlite3_row__tape_count(const char *sql)
{
    char *error_msg=NULL;
    int result=0;
    int row_count=0;

    if (NULL==Tape_Handle)
    {
        return -1;
    }
    result = sqlite3_exec(Tape_Handle,sql,row_callback,&row_count,&error_msg);
    sqlite3_free(error_msg);
    if (SQLITE_OK==result)
    {
        return row_count;
    }

    return -1;
}


int sqlite3_row_count(const char *sql)
{
    char *error_msg=NULL;
    int result=0;
    int row_count=0;

    if (NULL==g_sqlite3_handle)
    {
        return -1;
    }
    result = sqlite3_exec(g_sqlite3_handle,sql,row_callback,&row_count,&error_msg);
    sqlite3_free(error_msg);
    if (SQLITE_OK==result)
    {
        return row_count;
    }

    return -1;
}

sqlite3 *sqlite_opendb(const char *file_path){
    sqlite3 *handle=NULL;
    int result=0;
    result = sqlite3_open(file_path,&handle);
    if (SQLITE_OK!=result)
    {
        printf(" sqlite_opendb db fail result is %d db name is %s++++\r\n", result, file_path);
        return 0;
    }

    return handle;
}

int sqlite3_closedbByHandle(sqlite3 *handle){
    int result=0;
    if (NULL==handle)
    {
        return 1;
    }

    result = sqlite3_close(handle);

    if (SQLITE_OK!=result)
    {
        printf(" sqlite3_closedbByHandle db fail result is %d \r\n", result);
        return 0;
    }
    else
    {

        printf(" sqlite3_closedbByHandle success\r\n");
    }

    return 1;
}

int sqlite3_execute_songSqlbyHandle(const char *sql, sqlite3 *handle)
{
    char *error_msg=NULL;
    int result = 0;

    if (NULL==handle)
    {
        printf("sqlite3_execute_songSqlbyHandle ull");
        return 0;
    }
    if(sql == NULL)
    {
        return -1;
    }

    result = sqlite3_exec(handle,sql,0,0,&error_msg);
    if(result != 0)
    {
//        printf("==============%s %d: sql = %s\n", __FILE__, __LINE__, sql);
        printf("--------------sqlite3_execute_songSqlbyHandle failed %s-------------result is %d, sql is %s\n", error_msg,result, sql);
    } else{
        printf("sqlite3_execute_songSqlbyHandle success sql is %s \n", sql);
    }

    //错误消息目前没有使用
    sqlite3_free(error_msg);
    if (SQLITE_OK==result)
    {
        return 1;
    }

    return 0;
}

int sqlite3_open_DB(const char *file_path)
{
    int result=0;
    //���ݿ��Ѿ��򿪾Ͳ����ڴ���
    if (NULL!=g_sqlite3_handle)
    {
        return 1;
    }
#if 1
    //�ı����ݿ�
    result = sqlite3_open(file_path,&g_sqlite3_handle);
#else
    //�ڴ����ݿ� ,����7229 ȡ���ڴ����ݿ⡣
    result = sqlite3_open(":memory:", &g_sqlite3_handle);
    result = loadOrSaveDb(g_sqlite3_handle, file_path, 0); //�ļ����ݿ⵼�뵽�ڴ����ݿ�
#endif
    if (SQLITE_OK!=result)
    {
        printf(" sqlite3_open_DB db fail result is %d db name is %s++++\r\n", result, file_path);
        return 0;
    }
    else
    {

        printf(" sqlite3_open_DB success\r\n");
    }

    return 1;
}

//by Bati 2018-5-25
int sqlite3_execute_songSql(const char *sql)
{
    char *error_msg=NULL;
    int result = 0;

    if (NULL==g_sqlite3_handle)
    {
        printf("g_sqlite3_handleg_sqlite3_handle ull");
        return 0;
    }
    if(sql == NULL)
    {
        return -1;
    }

    result = sqlite3_exec(g_sqlite3_handle,sql,0,0,&error_msg);
    if(result != 0)
    {
        printf("==============%s %d: sql = %s\n", __FILE__, __LINE__, sql);
        printf("--------------%s-------------result is %d\n", error_msg,result);
    }

    //错误消息目前没有使用
    sqlite3_free(error_msg);
    if (SQLITE_OK==result)
    {
        return 1;
    }

    return 0;
}

int sqlite3_execute_TapeSql(const char *sql)
{
    char *error_msg=NULL;
    int result = 0;

    if (NULL==Tape_Handle)
    {
        return 0;
    }
    if(sql == NULL)
    {
        return -1;
    }

    result = sqlite3_exec(Tape_Handle,sql,0,0,&error_msg);
    if(result != 0)
    {
        printf("==============%s %d: sql = %s\n", __FILE__, __LINE__, sql);
        printf("--------------%s-------------\n", error_msg);
    }

    //错误消息目前没有使用
    sqlite3_free(error_msg);
    if (SQLITE_OK==result)
    {
        return 1;
    }

    return 0;
}
int Creat_TapeDB(const char *path)
{
    char sqlcommand[512] = "";
    char* record = "record";
    char *errmsg = NULL;

    if(sqlite3_open(path,&Tape_Handle) !=0)
    {
        printf("error :%s \n",sqlite3_errmsg(Tape_Handle));
        return -1;
    }

    //strcpy(sqlcommand,"CREATE TABLE IF NOT EXISTS record(song_id text, song_name text, singer_name text, mp3path text, url text, state INTEGER, id INTEGER PRIMARY KEY AUTOINCREMENT)");

    strcpy(sqlcommand,"CREATE TABLE IF NOT EXISTS record(song_id text, song_name text, singer_name text, mp3path text, url text, state INTEGER)");
    sqlite3_execute_TapeSql(sqlcommand);

    memset(sqlcommand, '\0', sizeof(sqlcommand));
    strcpy(sqlcommand,"delete from record");
    //sqlite3_execute_TapeSql(sqlcommand);

    return 0;
}

int Creat_TapeDBbyArg(const char *path, int del)
{
    char sqlcommand[512] = "";
    char* record = "record";
    char *errmsg = NULL;

    if(sqlite3_open(path,&Tape_Handle) !=0)
    {
        printf("error :%s \n",sqlite3_errmsg(Tape_Handle));
        return -1;
    }

    //strcpy(sqlcommand,"CREATE TABLE IF NOT EXISTS record(song_id text, song_name text, singer_name text, mp3path text, url text, state INTEGER, id INTEGER PRIMARY KEY AUTOINCREMENT)");

    strcpy(sqlcommand,"CREATE TABLE IF NOT EXISTS record(song_id text, song_name text, singer_name text, mp3path text, url text, state INTEGER)");
    sqlite3_execute_TapeSql(sqlcommand);

    memset(sqlcommand, '\0', sizeof(sqlcommand));
    strcpy(sqlcommand,"delete from record");


    if(del)
        sqlite3_execute_TapeSql(sqlcommand);

    return 0;
}

//sqlite3 *get_ktvdb_mirror_handle() int sqlite3_open_KTVDB_Mirror(const char *file_path) int sqlite3_close_KTVDB_Mirror() int KTVDB_mirror_execute_songSql(const char *sql)
//by Bati 2018-06-13
sqlite3 *get_ktvdb_mirror_handle()
{
    return g_Handle_KTVDB_Mirror;
}
//by Bati 2018-06-13
int sqlite3_open_KTVDB_Mirror(const char *file_path, bool isMemoryMode)
{
    int result=0;

    if( strlen(MirrorDBPath) == 0 && strlen(file_path) != 0 ){
        strcpy(MirrorDBPath, file_path);
    }

    //���ݿ��Ѿ��򿪾Ͳ����ڴ���
    if (NULL!=g_Handle_KTVDB_Mirror)
    {
        return 1;
    }

    if( !isMemoryMode ) {
        //�ı����ݿ�
        result = sqlite3_open(MirrorDBPath, &g_Handle_KTVDB_Mirror);
        //KTVDB_mirror_execute_songSql("PRAGMA synchronous = OFF");
        //KTVDB_mirror_execute_songSql("PRAGMA temp_store = MEMORY;");
        //g_Handle_KTVDB_Mirror
    }
    else {
        //�ڴ����ݿ� ,����7229 ȡ���ڴ����ݿ⡣
        result = sqlite3_open(":memory:", &g_Handle_KTVDB_Mirror);
        result = loadOrSaveDb(g_Handle_KTVDB_Mirror, MirrorDBPath, 0); //�ļ����ݿ⵼�뵽�ڴ����ݿ�
    }
    if (SQLITE_OK!=result)
    {
        printf(" sqlite3_open_DB db fail result is %d db name is %s++++\r\n", result, file_path);
        return 0;
    }
    else
    {

        printf(" sqlite3_open_DB success\r\n");
    }

    return 1;
}
//by Bati 2018-06-13
int sqlite3_close_KTVDB_Mirror( bool isMemoryMode )
{
    int result=0;
    //���ݿ��Ѿ��򿪾Ͳ����ڴ���
    if (NULL==g_Handle_KTVDB_Mirror)
    {
        return 1;
    }
    if(!isMemoryMode) {
        //�ı����ݿ�
        result = sqlite3_close(g_Handle_KTVDB_Mirror);
    }
    else {
        //�ڴ����ݿ� ,����7229 ȡ���ڴ����ݿ⡣
        //result = sqlite3_open(":memory:", &g_Handle_KTVDB_Mirror);
        result = loadOrSaveDb(g_Handle_KTVDB_Mirror, MirrorDBPath, 1); //�ļ����ݿ⵼�뵽�ڴ����ݿ�

        result = sqlite3_close(g_Handle_KTVDB_Mirror);
    }

    //result = sqlite3_close(g_Handle_KTVDB_Mirror);
    g_Handle_KTVDB_Mirror = NULL;
    if (SQLITE_OK!=result)
    {
        printf(" sqlite3_open_DB db fail result is %d db name is ktv10.db_bak ++++\r\n", result);
        return 0;
    }
    else
    {

        printf(" sqlite3_close_DB success\r\n");
    }

    return 1;
}

//by Bati 2018-06-13
int KTVDB_mirror_execute_songSql(const char *sql)
{
    char *error_msg=NULL;
    int result = 0;

    if (NULL==g_Handle_KTVDB_Mirror)
    {
        return 0;
    }
    if(sql == NULL)
    {
        return -1;
    }

    result = sqlite3_exec(g_Handle_KTVDB_Mirror,sql,0,0,&error_msg);
    if(result != 0)
    {
        printf("==============%s %d: sql = %s\n", __FILE__, __LINE__, sql);
        printf("--------------%s-------------\n", error_msg);
    }

    //错误消息目前没有使用
    sqlite3_free(error_msg);
    if (SQLITE_OK==result)
    {
        return 1;
    }

    return 0;
}

//by Bati 2018-06-13
int KTVDB_mirror_row_count(const char *sql)
{
    char *error_msg=NULL;
    int result=0;
    int row_count=0;

    if (NULL==g_Handle_KTVDB_Mirror)
    {
        return -1;
    }
    result = sqlite3_exec(g_Handle_KTVDB_Mirror,sql,row_callback,&row_count,&error_msg);
    sqlite3_free(error_msg);
    if (SQLITE_OK==result)
    {
        return row_count;
    }

    return -1;
}

int sqlite3_execute_getrowByHandle(const char *sql, sqlite3 *sqlite3_handle)
{
    char *error_msg=NULL;
    int result=0;
    int row_count=0;

    if (NULL==sqlite3_handle)
    {
        return -1;
    }
    result = sqlite3_exec(sqlite3_handle,sql,row_callback,&row_count,&error_msg);
    sqlite3_free(error_msg);
    if (SQLITE_OK==result)
    {
        LOGD("sqlite3_execute_getrowByHandle success sql is %s row_count is %d\n", sql, row_count);
        return row_count;
    }

    LOGD("sqlite3_execute_getrowByHandle fail sql is %s\n", sql);
    return -1;
}

//by Bati 2018-06-15
int KTV_mirror_get_field_data(const char *sql,char *result_str)
{
    char **get_result=NULL;
    char *error_msg=NULL;
    int row=0; //行数
    int column=0; //列数
    int result=0;

    if (NULL == g_Handle_KTVDB_Mirror || NULL == result_str)
    {
        return -1;
    }
    result = sqlite3_get_table(g_Handle_KTVDB_Mirror,sql,&get_result,&row,&column,&error_msg);
    sqlite3_free(error_msg);
    if (SQLITE_OK==result)
    {
        //执行成功
        if(NULL != get_result[column])
        {
            if(row != 0)
            {
                strcpy(result_str, get_result[column]);
            }
        }

        sqlite3_free_table(get_result);
        return row;
    }

    sqlite3_free_table(get_result);
    return -1;
}

int KTVDB_mirror_get_field_data(sqlite3 *sqlhandle_, const char *sql,char *result_str)
{
    char **get_result=NULL;
    char *error_msg=NULL;
    int row=0; //行数
    int column=0; //列数
    int result=0;

    if (NULL == sqlhandle_ || NULL == result_str)
    {
        return -1;
    }
    result = sqlite3_get_table(sqlhandle_,sql,&get_result,&row,&column,&error_msg);

    if (SQLITE_OK==result)
    {
        //执行成功
        if(NULL != get_result[column])
        {
            if(row != 0)
            {
                strcpy(result_str, get_result[column]);
            }
        }

        sqlite3_free_table(get_result);
        sqlite3_free(error_msg);
        return row;
    } else{
        printf("KTVDB_mirror_get_field_data is serror ret is %d, error is %s\n", result, error_msg);
    }

    sqlite3_free(error_msg);
    sqlite3_free_table(get_result);
    return -1;
}


int loadOrSaveDb(sqlite3 *pInMemeory, const char *zFilename, int isSave)
{
    int rc=0;
    sqlite3 *pFile=NULL;
    sqlite3_backup *pBackup;
    sqlite3 *pTo=NULL;
    sqlite3 *pFrom=NULL;

    //打开文本数据库
    rc = sqlite3_open(zFilename, &pFile);
    if(SQLITE_OK==rc)
    {
        pFrom = (isSave?pInMemeory:pFile);
        pTo = (isSave?pFile:pInMemeory);

        //备份数据库
        pBackup = sqlite3_backup_init(pTo,"main",pFrom,"main");
        if(pBackup)
        {
            //执行备份
            (void)sqlite3_backup_step(pBackup,-1);
            (void)sqlite3_backup_finish(pBackup);
        }
        else
        {
            printf("sqlite3_backup_init() fail\r\n");
        }
        rc = sqlite3_errcode(pTo);
    }
    else
    {
        printf("sqlite open local db fail++++++\r\n");
    }
    sqlite3_close(pFile);

    return rc;
}