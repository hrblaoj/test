/*
#include "debug_info.h"
	���ڴ�ӡ������Ϣѡ������Ӵ�ӡ
*/
#ifndef __DIALOGUE_H__
#define __DIALOGUE_H__

#define TX_WARNING_V	1
#define TX_ERROR_V 1
#define TX_DEBUG_V 1
#include <stdio.h>
//������ӡ��Ϣ
#define PRINT_SHINE(fmt,...) printf("[%s][%s][%d]\t"fmt"",__FILE__,__FUNCTION__,__LINE__,##__VA_ARGS__);
//��ӡ������Ϣ
#if (TX_WARNING_V > 0)
#define WARN_SHINE(fmt,...) printf("warning[%s][%s][%d]\t"fmt,__FILE__,__FUNCTION__,__LINE__,##__VA_ARGS__);
#else
#define WARN_SHINE(fmt,...)
#endif

//��ӡ������Ϣ
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
								printf("���ļ�����\n");\
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
/*д�뵽���Կ�*/
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
							}}}  //��ӡ������Ϣ
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
//��ӡUTF8��ʽ�ַ���
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