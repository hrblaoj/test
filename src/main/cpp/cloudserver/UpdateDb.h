
#ifndef UPDATEDB_H
#define UPDATEDB_H

#include "../sqlite3.h"
#include <stdbool.h>
typedef void(*parseCallBack)(char *json);
#define DB_LIST_TXT 	"db_list.txt"
#define USE_SATA_MODE_OPENDB 1
#ifdef __cplusplus

extern "C" {
#endif

////本地，网络数据库版本
//char db_net_version_frm_txt[128];
//
//char db_local_version_frm_txt[128];
//
//int mWriteDBCnt;
//
//static int sCountframe;
//static long sCountTime;
//
////static bool sUpdateDBStop;
//
////int startserver();
//
//int MainFuncUpdatedbVersion();
//
//int KTVDBMirrorInitNewTable();
//
//int sqlite3_execute_sql(sqlite3 *sql_handler, const char *sql);
//
//int database_song_callback(void * para, int n_column, char ** column_value, char ** column_name);
//int database_singer_callback(void * para, int n_column, char ** column_value, char ** column_name);
//int database_topsong_callback(void * para, int n_column, char ** column_value, char ** column_name);
//
////解析服务器发送的数据库更新文件
//int download_parse_database_update();
//
//
//int update_test();

static int sCountframe;
static long sCountTime;
int database_song_callback(void * para, int n_column, char ** column_value, char ** column_name);
int database_singer_callback(void * para, int n_column, char ** column_value, char ** column_name);
int upDateDb_OpenDb();
void setUpdateDbPath(const char *path, const char *path2);
void upDateDb_StartThread();
int upDateDb_DatabaseUpdate();
void upDateDb_Grooming();
void upDateDb_InitTable();
int upDateDb_ReSortDb();
//void *download_db_thread(void *arg);
#ifdef __cplusplus

}
#endif
#endif