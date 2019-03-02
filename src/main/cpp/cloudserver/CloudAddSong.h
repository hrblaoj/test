#ifndef _CLOUDADDSONG_H_
#define _CLOUDADDSONG_H_

#include <sys/statfs.h>
//#include <openssl/md5.h>
#include "netinet/tcp.h" //For TCP_NODELAY
#include <sys/select.h>



/* According to earlier standards */
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>
#include <fcntl.h>
#include <pthread.h>

//程序版本号存放目录
#ifndef FILE_VERSION_TXT
#define FILE_VERSION_TXT	"/root/version.txt"
#endif


#ifndef FILE_PROGRAM_TXT
#define FILE_PROGRAM_TXT	"/media/C/version_program.txt"
#endif

#ifndef FILE_DELETE_TXT
#define FILE_DELETE_TXT		"delete_list.txt"
#endif

#define OPEN_CLOUD_ADD_SONG //云加歌服务

#define DOWNLOAD_SERVICE_RECV_PORT		4444 //下载服务程序接收命令端口
#define DOWNLOAD_SERVICE_SEND_PORT		7777 //下载服务程序发送命令端口

#define DB_UPDATE_DIR_ 	"/sdcard/testdbupdate"
#define	DB_RANK			"shinerank.db"
#define DB_LIST_TXT 	"db_list.txt"
#define DB_UPDATE_OK 	"db_canUpdate"
#define WEB_SERVER_IP_TXT 	"http://www.joyk.com.cn/file/severip.txt"

#define FILENAME_SERVERIP "DownloadService_serverip.txt"

pthread_mutex_t mutex;

char CLOUDSERVE_DOWNLOADSONGPATH[512];
char CLOUDSERVE_DOWNLOADDBPATH[512];

char CLOUDSERVE_DOWNLOAD_SERVERIP[512];
char CLOUDSERVER_KTVDB_PATH[512];


typedef enum
{
    NORMAL_MODE=0,
    SELECT_MODE,
} SELECT_TYPE;

//命令类型
typedef enum
{
    GET_NULL=0,
    //获取数据库版本
    GET_DB_ALL,
    GET_DB_VERSION,
    GET_DB_VERSION_OK,
    GET_DB_VERSION_FAILED,

    //下载数据库
    GET_DATABASE_SINGLE,
    GET_DATABASE,
    GET_DATABASE_OK,
    GET_DATABASE_FAILED,
    GET_DB_FINISH, //下载成功后删除数据库
    GET_DATABASE_DISK_FULL, //磁盘空间已满
    //下载歌曲
    GET_DOWNSONGTHREAD_IDLE,
    GET_SONG_IDLE,
    GET_SONG_NOTIDLE,
    GET_SONG,
    GET_SONG_START,
    GET_SONG_OK,
    GET_SONG_FAILED,

    //设置CPU ID地址
    SET_CPUID,
    SET_DISK,
    GET_DISK,
    GET_DISK_FAILED,

    //获取在线更新包版本
    GET_ONLINE_UPDATE_VERSION,
    GET_ONLINE_UPDATE_VERSION_OK,
    GET_ONLINE_UPDATE_VERSION_FAILED,

    //下载在线更新包
    GET_ONLINE_UPDATE,
    GET_ONLINE_UPDATE_OK,
    GET_ONLINE_UPDATE_FAILED,
    GET_ONLINE_UPDATE_DISK_FULL, //磁盘空间已满

    //设置下载数据库的百分比
    SET_DOWNLOAD_DB_PERCENT,

    //设置下载歌曲进度百分比
    SET_DOWNLOAD_SONG_PERCENT,

    SONG_SORT,

    //查询云加歌过期时间
    CLOUD_OUT_OF_DATE,
    SET_CLOUD_OUT_OF_DATE,

    GET_SONG_EXIT,
    CLOUD_SERVICE_OK,



} DOWNLOAD_CMD;

//通信结构体
typedef struct
{
    DOWNLOAD_CMD cmd;
    char content[1024];
} DOWNLOAD_STRUCT;

typedef struct
{
    int id;
    char content[1024];
} JAVA_DOWNLOAD_STRUCT;

//提示类型
typedef enum
{
    NO_HINT,					//没有提示
    HINT_NEW_DB,				//有新数据库
    HINT_DB_UPDATE_OK,			//更新完成
    HINT_OUT_DISK_FREE_SPACE,	//没有硬盘空间
    HINT_ONLINE_UPDATE_OK,			//在线更新成功
    HINT_ONLINE_UPDATE_FAILED,		//更新失败
    HINT_PROGRAM_VERSION_ALREADY_NEW,	//已经是最新版本
    HINT_USER_MAC_NOT_REGISTER,			//用户注册失败
    HINT_USER_MAC_NOT_THROUGH,			//用户验证不通过
    HINT_GET_PROGRAM_VERSION_FAILED, //获取最新版本失败
    HINT_UPDATE_DB_PERCENT,		//更新数据库显示百分比
    HINT_DOWNLOAD_DB_PERCENT,	//下载数据库的显示百分比
    HINT_WHETHER_SORT_DB,		//提示用户是否对数据库重新进行排序
    HINT_NEW_DB_ALREADY_NEW,	//数据库版本已经是最新
    HINT_NEW_DB_FAILED,			//获取数据库版本失败
    HINT_ONLINE_UPDATE_DISK_FULL,	//在线更新的时候磁盘满
    HINT_DATABASE_DISK_FULL,		//更新数据库的时候磁盘满
} HINT_TYPE;

//当前正在下载的歌曲信息song_id和下载进度
typedef struct
{
    char song_id[16];		//歌曲id
    char percent_song[16];	//下载进度
    int refresh;			//是否刷新
} CURRENT_DOWNLOAD_SONG;

//初始化
int cloud_init_proc();

//获取系统磁盘 zhenyubin 2014-04-25
int cloud_get_disk();

//设置可用磁盘,到下载进程 zhenyubin 2014-04-25
//下载进程，自己有时获取不到
int cloud_set_disk();


//解析加歌服务器
int cloud_start_add_song();

//判断服务器是添加的域名还是IP，域名，进行解析
int cloud_parse_domain(char *address,int len);

//获取网络数据库版本信息
int cloud_get_dbnetversion();
//通知下载监听程序停止下载
int cloud_inform_download();

//设置CPU ID type:0 只设置CPU ID 1：发送到下载服务
int cloud_set_cpuid(int type,char *cpu);

//备份数据库
int bak_db();
//数据库排序
//int Re_Sort_DB();
//解析服务器发送的数据库更新文件 zhenyubin 2014-04-17
//int download_parse_database_update(char *file_name);
//int download_parse_database_update();

//网络检测线程
void *cloud_start_listen_download_thread(void *args);
//解析并处理下载服务端消息
int cloud_analysis_download_cmd();


//发送命令到下载服务器downloadservice
int cloud_sendto_download_service(DOWNLOAD_STRUCT cmd);
//建立TCP socket连接,发送接收超时，毫秒级，返回连接成功的socket
int cloud_connect_by_tcp(char *ip,int port,int send_timeout,int recv_timeout);

//获得wifi模式
int get_wifi_mode();
//设置wifi模式
int set_wifi_mode(int flag);
//设置获取数据库版本标志
int set_db_version_flag(HINT_TYPE flag);
//得到获取数据库版本的标志
int get_db_version_flag(HINT_TYPE *flag);
//请求下载数据库
int request_download_service_database();
//请求下载在线更新程序包
int request_download_update_online();
//请求在线更新程序包的版本号
int request_version_update_online();
//设置重新下载标志
int set_reload_database_flag(int flag);
//获取重新下载标志
int get_reload_database_flag(int *flag);
//下载歌曲
int cloud_download_song();
//获取得到的可以使用的磁盘信息。
int get_disk_info( char info[8][64], int *cnt);
//获得更新数据库的百分比
int get_update_db_percent(int *db_percent);
//得到当前更新数据库的百分比
int get_download_db_percent(int *db_percent);

//更新数据库中的local_path字段值
int update_db_local_path(int local_path, char *song_id);

//得到当前下载的歌曲信息
int get_cur_down_song_info(CURRENT_DOWNLOAD_SONG *down_song_info);

//系统仍可大约存储的歌曲数目
int remain_store_song_num(unsigned long *hard_disk);


#endif
