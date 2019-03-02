
#ifndef SHINEKTV_ANDROID_UpdateBDVERSION_H_H
#define SHINEKTV_ANDROID_UpdateBDVERSION_H_H

#include "../sqlite3.h"
#include <stdbool.h>
#ifdef __cplusplus

extern "C" {
#endif

//本地，网络数据库版本
char db_net_version_frm_txt[128];

char db_local_version_frm_txt[128];

int mWriteDBCnt;

static int sCountframe;
static long sCountTime;

//static bool sUpdateDBStop;

//int startserver();

int MainFuncUpdatedbVersion();

int KTVDBMirrorInitNewTable();

int sqlite3_execute_sql(sqlite3 *sql_handler, const char *sql);

int database_song_callback(void * para, int n_column, char ** column_value, char ** column_name);
int database_singer_callback(void * para, int n_column, char ** column_value, char ** column_name);
int database_topsong_callback(void * para, int n_column, char ** column_value, char ** column_name);

//解析服务器发送的数据库更新文件
int download_parse_database_update();


int update_test();


#ifdef __cplusplus

}
#endif
#endif