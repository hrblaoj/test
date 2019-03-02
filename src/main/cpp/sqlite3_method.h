#ifndef SQLITE3_METHOD_INCLUDE_H
#define SQLITE3_METHOD_INCLUDE_H

#include "sqlite3.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

//#define USE_SATA_MODE_OPENDB

static sqlite3 *g_sqlite3_handle=NULL;
static sqlite3 *g_sqlite3_repair_handle=NULL;
static sqlite3 *Tape_Handle=NULL;
static sqlite3 *g_Handle_KTVDB_Mirror = NULL;

char MirrorDBPath[1024];

int row_callback(void* argv,int argc,char **value,char **name);
sqlite3 *get_handle();
sqlite3 *get_tapehandle();
int sqlite3_row_count(const char *sql);
int sqlite3_open_DB(const char *file_path);
int Creat_TapeDB(const char *path);
int Creat_TapeDBbyArg(const char *path, int del);
int sqlite3_execute_TapeSql(const char *sql);
int sqlite3_row__tape_count(const char *sql);
int sqlite3_execute_songSql(const char *sql);

//by Bati 2018-06-13
sqlite3 *get_ktvdb_mirror_handle();
int sqlite3_open_KTVDB_Mirror(const char *file_path, bool isMemoryMode);
int sqlite3_close_KTVDB_Mirror(bool isMemoryMode);
int KTVDB_mirror_execute_songSql(const char *sql);
int KTVDB_mirror_row_count(const char *sql);
int KTVDB_mirror_get_field_data(sqlite3 *sqlhandle_, const char *sql,char *result_str);
int loadOrSaveDb(sqlite3 *pInMemeory, const char *zFilename, int isSave);


sqlite3 *sqlite_opendb(const char *file_path);
int sqlite3_closedbByHandle(sqlite3 *handle);
int sqlite3_execute_songSqlbyHandle(const char *sql, sqlite3 *handle);
int sqlite3_execute_getrowByHandle(const char *sql, sqlite3 *sqlite3_handle);


#ifdef __cplusplus
}
#endif


#endif
