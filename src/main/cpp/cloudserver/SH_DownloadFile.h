#ifndef __SH_DOWNLOAD_FILE_H__
#define __SH_DOWNLOAD_FILE_H__
#include <pthread.h>


typedef struct
{
    char cwd[1024];
    char *message;
    int status;
    int fd;
    int data_fd;
    int ftp_mode;
    char *local_if;
} ftp_t;
typedef struct
{
    char host[1024];
    char auth[1024];
    char request[2048];
    char headers[2048];
    int proto;			/* FTP through HTTP proxies	*/
    int proxy;
    long long int firstbyte;
    long long int lastbyte;
    int status;
    int fd;
    char *local_if;
} http_t;
typedef struct
{
    void *next;
    char text[1024];
} message_t;
typedef struct
{
    char default_filename[1024];
    char http_proxy[1024];
    char no_proxy[1024];
    int strip_cgi_parameters;
    int save_state_interval;
    int connection_timeout;
    int reconnect_delay;
    int num_connections;
    int buffer_size;
    int max_speed;
    int verbose;
    int alternate_output;

    message_t *interfaces;

    int search_timeout;
    int search_threads;
    int search_amount;
    int search_top;

    int add_header_count;
    char add_header[10][1024];

    char user_agent[1024];
} conf_t;

typedef struct
{
    conf_t *conf;

    int proto;
    int port;
    int proxy;
    char host[1024];/*��������ַ*/
    char dir[1024];
    char file[1024];
    char user[1024];
    char pass[1024];

    ftp_t ftp[1];
    http_t http[1];
    long long int size;		/* File size, not 'connection size'..	*/
    long long int currentbyte;
    long long int lastbyte;
    int fd;
    int enabled;
    int supported;
    int last_transfer;
    char *message;
    char *local_if;

    int state;
    pthread_t setup_thread[1];
} conn_t;
typedef struct SERCERIP
{
    char url[1024];/*url*/
    char addr[512];/*��������ַ*/
    double time;
    struct SERCERIP *next;
} SERCERIP;
typedef struct
{
    pthread_mutex_t mutex ;
    long long int total;
    long long int done;
    int speed;
    int enable;
    char name[256];
    void *fun;
} SH_DOWN_SEEP;
/*
	��url��������ַ
*/
int SH_GetConvertURL(conn_t *conn,char *set_url);
/*
	�Ӷ��url�������ļ�
	�������3M
*/
int SH_DownloadFileHttpFtp(SERCERIP *head,char *filename,void *fun,int displaynumb, char *MD5, int pthread_num);
int SH_DownloadFileSizeHttpFtp(SERCERIP *head,char *filename,void *fun,unsigned int size,int displaynumb);
/*
	�����ļ�
	�����3M
*/
int SH_DownloadFileFromUrl(char *url,char *pathname,void *fun,int displaynumb, char *MD5, int pthread_num);
/*
	�����ص�ַ��ӵ��������
*/
void InputServerQue(SERCERIP **tmp,char *url);

#endif
