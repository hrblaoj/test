/*
#include "debug_info.h"
	用于打印调试信息选择性添加打印
*/
#ifndef __DIALOGUE_H__
#define __DIALOGUE_H__

#define TX_WARNING_V	1
#define TX_ERROR_V 1
#define TX_DEBUG_V 1
#include <stdio.h>
//正常打印信息
#define PRINT_SHINE(fmt,...) printf("[%s][%s][%d]\t"fmt"",__FILE__,__FUNCTION__,__LINE__,##__VA_ARGS__);
//打印警告信息
#if (TX_WARNING_V > 0)
#define WARN_SHINE(fmt,...) printf("warning[%s][%s][%d]\t"fmt,__FILE__,__FUNCTION__,__LINE__,##__VA_ARGS__);
#else
#define WARN_SHINE(fmt,...)
#endif

//打印错误信息
#if (TX_ERROR_V > 0)
#define ERROR_SHINE(fmt,...) printf("error[%s][%s][%d]\t"fmt,__FILE__,__FUNCTION__,__LINE__,##__VA_ARGS__);
#else
#define ERROR_SHINE(fmt,...)
#endif
#define DEBUG_ERROR_TXT "/mnt/usb/addsong/cloud_song_log.txt"
#if 0
#define LOG_SHINE(fmt,...) {\
							FILE* fid = fopen(DEBUG_ERROR_TXT, "a+");\
							if(!fid){\
								printf("打开文件错误\n");\
							}\
							time_t timep;\
							struct tm *p;\
							time(&timep);\
							p=gmtime(&timep);\
							sprintf(fid,"error[%s][%s][%d][%d-%d-%d  %d:%d:%d]"__FILE__,__FUNCTION__,__LINE__,(1900+p->tm_year),(1+p->tm_mon),p->tm_mday,p->tm_hour,p->tm_min,p->tm_sec);\
							/*sprintf(fid,"error[%s][%s][%d][%d-%d-%d  %d:%d:%d]"fmt,__FILE__,__FUNCTION__,__LINE__,(1900+p->tm_year),(1+p->tm_mon),p->tm_mday,p->tm_hour,p->tm_min,p->tm_sec,##__VA_ARGS__);*/\
							fclose(fid);\
							}
#else
#define LOG_SHINE(fmt,...) {{\
							FILE* fid = fopen(DEBUG_ERROR_TXT, "a+");\
							if(fid){\
								time_t timep;\
								struct tm *p;\
								time(&timep);\
								p=gmtime(&timep);\
								fprintf(fid,"log[%s][%s][%d][%d-%d-%d  %d:%d:%d]\t"fmt,__FILE__,__FUNCTION__,__LINE__,(1900+p->tm_year),(1+p->tm_mon),p->tm_mday,p->tm_hour,p->tm_min,p->tm_sec,##__VA_ARGS__);\
								fclose(fid);\
							}}}
#endif
/*写入到调试框*/
#define SEND_SHINE_SPEED_FILE(fmt,...)   {{\
							FILE* fid = fopen("/tmp/addsong_speed", "w");\
							if(fid){\
								fprintf(fid,fmt,##__VA_ARGS__);\
								fclose(fid);\
							}}}

#define SEND_SHINE_SPEED_ALL_FILE(fmt,...)   {{\
							FILE* fid = fopen("/tmp/addsong_speed_all", "w");\
							if(fid){\
								fprintf(fid,fmt,##__VA_ARGS__);\
								fclose(fid);\
							}}}

#define SEND_SHINE_DOWNING_FILE(fmt,...)   {{\
							FILE* fid = fopen("/tmp/addsong_download", "w");\
							if(fid){\
								fprintf(fid,fmt,##__VA_ARGS__);\
								fclose(fid);\
							}}}
#define SEND_SHINE_COMPLETE_FILE(fmt,...)   {{\
							FILE* fid = fopen("/tmp/addsong_complete", "w");\
							if(fid){\
								fprintf(fid,fmt,##__VA_ARGS__);\
								fclose(fid);\
							}}}  //打印调试信息
#define SEND_SHINE_WAIT_FILE(fmt,...)   {{\
							FILE* fid = fopen("/tmp/addsong_waiting", "w");\
							if(fid){\
								fprintf(fid,fmt,##__VA_ARGS__);\
								fclose(fid);\
							}}}
#if (TX_DEBUG_V > 0)
#define DEBUG_SHINE(fmt,...) printf("debug[%s][%s][%d]\t"fmt,__FILE__,__FUNCTION__,__LINE__,##__VA_ARGS__);
#else
#define DEBUG_SHINE(fmt,...)
#endif
//打印UTF8格式字符串
#if (TX_DEBUG_V > 0)
#define DEBUG_UTF8_SHINE(buff_printf)\
{\
	char *buff_UTF8;\
	buff_UTF8=(char *)malloc(sizeof(char)*strlen(buff_printf)*2);\
	utf8_to_gb(buff_printf,strlen(buff_printf),buff_UTF8,(sizeof(char)*strlen(buff_printf)+10));\
	printf("[%s][%s][%d]:   %s\n",__FILE__,__FUNCTION__,__LINE__,buff_UTF8);\
	free(buff_UTF8);\
}
#else
#define DEBUG_UTF8_SHINE(buff_printf)
#endif

#define astoerrp(tmp,str) if(tmp){printf(str); goto ERR;}
#define astoerr(tmp) if(tmp) goto ERR;
#define astoerr2(tmp) if(tmp) goto ERR2;
#endif