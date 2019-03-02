#include "CloudAddSong.h"
#include "stdio.h"
#include "unistd.h"
#include "netinet/tcp.h" //For TCP_NODELAY
#include <sys/select.h>
#include <sys/socket.h>
#include <string.h>
/* According to earlier standards */
#include <sys/time.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <math.h>
#include	 <sys/statfs.h>
#include	 <pthread.h>
#include	 <sys/socket.h>
#include	 <netinet/in.h>
#include     <unistd.h>     /*Unix标准函数定义*/
#include     <termios.h>    /*PPSIX终端控制定义*/
#include	 <arpa/inet.h>
#include	 <netdb.h>
#include	 <sys/ioctl.h>
#include	 <net/if.h>
#include	 <sys/types.h>
#include     <dirent.h>
#include     <sys/wait.h>
#include     <sys/time.h>
#include     <sys/un.h>
#include     <sys/ipc.h>
//#include     <openssl/rsa.h>0
#include	 <pthread.h>
#include <stdlib.h>
#include <strings.h>
#include	 "SH_DownloadFile.h"
#include "openssl/md5.h"
#include "cJSON.h"
#include "ShineLog.h"
#include "DownloadService.h"
#include "../ShinePath.h"
#include "UpdateDb.h"

#define AXELLL


#define CONFIGFILENAME           "config.ini"   //包房配置文件名
#define TOUCHCONFIGUREFILE       "touch.ini"    //触摸屏配置文件
#define LOCALMACHINE			 "localmachine.ini" //单机配置文件

#define OPEN_DEBUG
#ifdef OPEN_DEBUG
#define DEBUG(A)	printf(A)
#define DEBUG2(A,B)	printf(A,B)
#define DEBUG3(A,B,C)	printf(A,B,C)
#define DEBUG4(A,B,C,D)	printf(A,B,C,D)
#define DEBUG5(A,B,C,D,E)	printf(A,B,C,D,E)
#else
#define DEBUG(A)
#define DEBUG2(A,B)
#define DEBUG3(A,B,C)
#define DEBUG4(A,B,C,D)
#define DEBUG5(A,B,C,D,E)
#endif

#define POST_FILE_END	"\r\n--AaB03x--\r\n\r\n"
#define ACCEPT_LANGUAGE "Accept-Language: zh-CN,zh;q=0.8\r\n"
#define CACHE_CONTROL "Cache-Control: no-cache\r\n"
#define CONNECTION "Connection: close\r\n\r\n"
#define CONTENT_TYPE "Content-Type: multipart/form-data; boundary=AaB03x\r\n"

#define RANDOM_LEN 8

#define HTTP_GET_END " HTTP/1.1\r\nAccept: */*\r\nCache-Control: no-store,no-cache\r\nPragma: no-cache\r\nConnection: Close\r\nHost: "

#define SEND_UDP(sockfd,buff,len,filefd) {if(send(sockfd,buff,len,MSG_NOSIGNAL) < 0)\
		{\
			fclose(filefd);\

static char mStrCPU[512];
static char mStrMac[512];
static char mStrSubject[512];
static char mStrDownloadUrl[1024];

static char mStrDownSongError[1024] = "";

//CPU ID，Mac
static char cpu_id[64];
static char mac[64];
//服务器传送过来的云加歌到期日期和付费连接
static char cloud_out_date[1024];

//云加歌服务器ip，端口
static char server_http[512];



typedef struct db_version
{
    int version_cnt;
    int *version_arry;
} DB_VERSION;
DB_VERSION server_db_version = {0, NULL};

static int server_num;		//服务器个数
static int downfile_faile_cnt;		//下载测试文件失败次数,各线程失败一个加一
pthread_mutex_t	mutex_down_file;	//下载测试文件互斥锁
pthread_cond_t cond_down_file=PTHREAD_COND_INITIALIZER;		//下载测试文件条件变量
static unsigned long down_file_time;	//下载文件所用时间
static int down_song_faile_cnt;		//下歌失败计数
static int server_use_suffix;	//当前下载歌曲使用的ip下标
//可用磁盘设备
static char disk[8][64]; //暂定位不会超过8个
static int disk_counts=0;

//网络数据库版本
static char db_net_version[32];

//网络程序版本号
static char program_net_version[32];

//是否开始运行
static int download_thread_running=0;
//下载更新程序正常运行
static int thread_update_running;
//计算MD5的buffer
#define MD5_PACKAGE_SIZE 1024*1024

//================================================================
void *download_song_thread(void *args);
//从INI文件读取字符串类型数据
int GetIniKeyString(char *AppName,char *KeyName,char *ReturnValue,char *filename);

extern int SH_GetPasswdUser(char *User,char *Passwd);

extern int SH_initHI3719Code(char *code);

//================================================================
//存放数据库更新文件的目录是否存在1：存在，0：不存在
static int have_directory;
static SH_DOWN_SEEP down_load_info;//正在下载歌曲的下载信息

char download_song_id[16] = "";	//下载的歌曲id

static pthread_mutex_t DownSongThreadLock = PTHREAD_MUTEX_INITIALIZER;

void SetCpuIDAndSubjectID(const char *_cpu_id, const char *_mac, const char *_subject){
    memset(mStrCPU, 0, sizeof(mStrCPU));
    memset(mStrMac, 0, sizeof(mStrMac));
    memset(mStrSubject, 0, sizeof(mStrSubject));
    strcpy(mStrCPU, _cpu_id);
    strcpy(mStrMac, _mac);
    strcpy(mStrSubject, _subject);
}
void SetServerDownloadUrl(const char *_url){
    memset(mStrDownloadUrl, 0, sizeof(mStrDownloadUrl));
    strcpy(mStrDownloadUrl, _url);
}

int encrypt_Md5(char *in_data, int in_len, char *out_data, int out_len)
{
    MD5_CTX c;
    unsigned char md5[32]= {0};
    int i;
    char md5_str[128] = "";
    char tmp[64] = "";

    if(in_data == NULL || out_data == NULL)
    {
        return -1;
    }

    MD5_Init(&c);
    MD5_Update(&c, in_data, in_len);
    MD5_Final(md5,&c);

    memset(tmp,0,sizeof(tmp));
    memset(md5_str,0,sizeof(md5_str));

    for(i=0; i<16; i++)
    {
        sprintf(tmp,"%02X",md5[i]);
        strcat(md5_str,tmp);
    }
    if(out_len > strlen(md5_str))
    {
        strcpy(out_data, md5_str);
    }
    else
        return -1;

    return 0;
}

//产生长度为length的随机字符串
int getRandomString(int length, char *string)
{
    int flag, i;

    if(string == NULL)
    {
        return -1;
    }

    srand((unsigned) time(NULL ));

    for (i = 0; i < length; i++)
    {
        flag = rand() % 3;
        switch (flag)
        {
        case 0:
            string[i] = 'A' + rand() % 26;
            break;
        case 1:
            string[i] = 'a' + rand() % 26;
            break;
        case 2:
            string[i] = '0' + rand() % 10;
            break;
        default:
            string[i] = 'x';
            break;
        }
    }

    return 0;
}


int SystemInstead(char* cmd)
{
//    LOGD("dfdf startserver cmd %s %s, %d, %s",cmd,__FILE__,__LINE__,__FUNCTION__);
    int status = 0;
    pid_t pid;
    //char * environ[ ]={"PATH=/bin",0};
    //vfork();
    //create a new process ,  the process is over, the old process will be back to run
    if ((pid = vfork()) <0)
    {
//        LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
//        printf("vfork process error! \n");
        status = -1;
    }
    else if (pid==0)
    {
//        LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        //const char *new_argv[4];
        struct sigaction sa_cld;
        sa_cld.sa_handler = SIG_DFL;
        sa_cld.sa_flags = 0;

//        LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);

        /* 在子进程中放开SIGINT信号 */
        sigemptyset(&sa_cld.sa_mask);
        sigaction (SIGINT, &sa_cld, NULL);
        sigaction (SIGQUIT, &sa_cld, NULL);

        LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);

        execl("/system/bin/sh", "sh", "-c", cmd, (char *)0);
//        LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        exit(0);
    }
    else if(pid>0)
    {
//        LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        waitpid(pid,&status,0);
    }
//    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    return status;
}


/*
	从域名解析
*/
void domain_to_ip_sss(char *domain_str,char *ip)
{
//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
#ifndef WIN32
#include <netdb.h>
    struct hostent *addr;
//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    addr=gethostbyname(domain_str);
//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    if(addr != NULL)
    {
//        LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        inet_ntop( addr->h_addrtype,addr->h_addr_list[0],ip,16);
    }
#else
    struct sockaddr_in addr;
    struct hostent *h;
    h=gethostbyname(domain_str);
    if(h != NULL)
    {
        addr.sin_addr = *(struct in_addr*) h->h_addr;
        strcpy(ip,inet_ntoa(addr.sin_addr));
    }
#endif
}


/*--------------------------------------------------------------------------
	GET 文件
url:url信息
filename:保存地址信息
aimip:目标ip
aimport:目标端口
返回0成功
--------------------------------------------------------------------------*/
int http_get_file_shine(char *url,char *filename,char *aimip,unsigned short aimport)
{
    int len = 0;
    int relen;
    int ret=-1;
    char *str;
    char buff[1024];
    struct sockaddr_in name;
    int stock= socket(PF_INET, SOCK_STREAM, 0);/*TCP协议*/
    if(stock == -1)
    {
//        printf("\n-------------stock-----failed-------\n");
        return ret;
    }
    sprintf(buff,"GET /%s HTTP/1.1\r\nHost: %s:%d\r\n" \
            CONTENT_TYPE"User-Agent: eidows\r\n"	\
            ACCEPT_LANGUAGE CACHE_CONTROL CONNECTION,url,aimip,aimport);
    memset(&name,0,sizeof(struct sockaddr_in));
    name.sin_family = AF_INET;
    name.sin_port = htons(aimport);
    name.sin_addr.s_addr= inet_addr(aimip);
#ifndef WIN32
    struct timeval tv_out;
    tv_out.tv_sec = 0;
    tv_out.tv_usec = 1000000;
    setsockopt(stock, SOL_SOCKET, SO_RCVTIMEO, &tv_out, sizeof(tv_out));
    setsockopt(stock, SOL_SOCKET, SO_SNDTIMEO, &tv_out, sizeof(tv_out));
#endif
    if(connect(stock,(struct sockaddr *)&(name),sizeof(name)) < 0)
    {
        close(stock);
//        printf("\n-------------connect-----failed-------\n");

        return ret;
    }
    LOGD("dfdf startserver %s, %d, %s ",__FILE__,__LINE__,__FUNCTION__);
    send(stock,buff,strlen(buff),MSG_NOSIGNAL);
    LOGD("dfdf startserver %s, %d, %s ",__FILE__,__LINE__,__FUNCTION__);
    char rebuff[4096];//printf("请求下载\n");
    len=recv(stock,&rebuff[0],sizeof(rebuff), 0);
    LOGD("dfdf startserver %s, %d, %s ",__FILE__,__LINE__,__FUNCTION__);
    //printf("%s",buff);
    if(str=strstr(rebuff,"Content-Length: "))
    {
        char *no;
        FILE *fb;
        str+=sizeof("Content-Length: ")-1;
        no=str;
        if(!strstr(rebuff,"HTTP/1.1 200 OK"))
            return -1;
        while((*no>='0')&&(*no<='9'))
            no++;
        *no=0;
        no+=4;
        relen=atoi(str);
        len-=no-rebuff;
        if(fb=fopen(filename,"wb"))
        {
            relen-=fwrite(no,1,len,fb);
            ret=0;
            while(relen>0)
            {
                int writelen=0;
                len=recv(stock,rebuff,sizeof(rebuff), 0);
                if(len<=0)
                {
                    ret=-1;
                    break;
                }
                writelen=fwrite(rebuff,1,len,fb);
                if(writelen>0)
                    relen-=writelen;
                else
                {
                    ret=-1;
                    break;
                }
            }
            fclose(fb);
        }
        else
        {
            ret=-1;
        }

    }
    close(stock);
    return ret;
}

int get_cpu_id()
{
    memset(cpu_id, '\0', sizeof(cpu_id));
    strcpy(cpu_id, "qingk3.0");
#if 0
    int fd=0;
    int ret = 0;

    fd=open("/dev/shm/hareware.info",O_RDONLY);
    if(fd>0)
    {
        ret=read(fd,cpu_id,sizeof(cpu_id));
        if(ret<=0)
        {
            DEBUG("\nRead CPU ID Error\n");
        }
        close(fd);
    }
    memset(cpu_id, "\0", sizeof(cpu_id));
    memcpy(cpu_id, "HAISITEST", sizeof("HAISITEST"));
#endif

#if 0
    int ret = 1;
    int num = 0;
    char savecode[64] = {0};
    memset(savecode, '\0', sizeof(savecode));
    GetIniKeyString("REGISTER", "SAVEREGISTERCODE",savecode,REGISTERFILENAME);
    SH_initHI3719Code(savecode);
    while(1)
    {
        ret = SH_GetPasswdUser(mac, cpu_id);
        if(ret == 0)
            break;
        if(num > 5)
        {
            //printf("===========%s %d: ret = %d, mac = %s, cpu_id = %s\n", __FILE__, __LINE__, ret, mac, cpu_id);
            memset(mac,0,sizeof(mac));
            memset(cpu_id,0,sizeof(cpu_id));
            break;
        }

        usleep(1000);
        num++;
    }
#endif

    return 0;
}

int get_mac_shine()
{
#if 1
    int fd=0;
    char tmp_mac[64] = "";
    int ret = 0;

    fd=open("/sys/class/net/eth0/address",O_RDONLY);
    if(fd>0)
    {


        ret=read(fd,tmp_mac,sizeof(tmp_mac));
        LOGD("======= mac is", tmp_mac);
        if(ret<=0)
        {
            DEBUG("\nRead MAC Address Error\n");
        }
        close(fd);
    }
    else{
        LOGD("=======471 mac is", tmp_mac);
        fd=open("/sys/class/net/wlan0/address",O_RDONLY);
        if( fd > 0 ) {
            ret = read(fd, tmp_mac, sizeof(tmp_mac));
            LOGD("=======475 mac is", tmp_mac);
            if (ret <= 0) {
                DEBUG("\nRead MAC Address Error\n");
            }
            close(fd);
        }
    }

    if(strlen(tmp_mac)>0)
    {
        int i=0;
        int j=0;
        for(i=0; i<strlen(tmp_mac); i++)
        {
            if(tmp_mac[i] != ':' && tmp_mac[i] != '\r' && tmp_mac[i] != '\n')
            {
                mac[j]=tmp_mac[i];
                j++;
            }
        }
    }

#else
    char savecode[64] = {0};
    memset(savecode, '\0', sizeof(savecode));
    GetIniKeyString("REGISTER", "SAVEREGISTERCODE",savecode,REGISTERFILENAME);
    SH_initHI3719Code(savecode);

    SH_GetPasswdUser(mac, cpu_id);

#endif
    
	
	return 0;
}

int parse_serverip_file(char *file_name)
{
	FILE *fp_serverip = NULL;
	char *tmp_str = NULL;
	char tmp_buf[512] = "";
	char rd_buf[512] = "";
	int i = 0;

	if(file_name == NULL)
	{
		return -1;
	}

	if((fp_serverip = fopen(file_name, "r")) == NULL)
	{
//		printf("-------------------open %s failed-----------\n", file_name);
		
		return -1;
	}
	
	while(fgets(rd_buf, sizeof(rd_buf),fp_serverip) != NULL)
	{		
		tmp_str = strstr(rd_buf, "new_jinshanplat_server=");//new_jinshanplat_server=http://news.joyk.com.cn:6518
		if(tmp_str != NULL)
		{
			strcpy(server_http, tmp_str+strlen("new_jinshanplat_server="));
			for(i = strlen(server_http) - 1; i >= 0; i--)
			{
				if((server_http[i] == ' ') || (server_http[i] == '\n')|| (server_http[i] == '\r'))
				{
					server_http[i] = '\0';					
				}
				else
				{
					break;
				}
			}			
		}
		
		memset(rd_buf, '\0', sizeof(rd_buf));
	}
	fclose(fp_serverip);

	return 0;
}


int download_init_proc()
{
//    LOGD("dfdf download_init_proc start");
    int i=0;
//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    int fd=0;
    int ret=0;
    char tmp_mac[64] = "";
//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
	FILE *fp_serverip = NULL;
    char rd_buf[1024] = "";
    char *tmp_str = NULL;
    char server_www[512] = WEB_SERVER_IP_TXT;
    char domainip[64] = "";

    memset(mac,0,sizeof(mac));
//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    memset(tmp_mac,0,sizeof(tmp_mac));
//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

    get_cpu_id();
//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

    get_mac_shine();

//    strcpy(mac, "08d40c48245d");//测试mac
//    LOGD("mymymac is %s", mac);
    while(1)
    {
        memset(domainip, '\0', sizeof(domainip));
//        LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        domain_to_ip_sss("www.baidu.com", domainip);
//        LOGD("dfdf download_init_proc %s  %s, %d, %s",domainip, __FILE__,__LINE__,__FUNCTION__);

        if(strcmp(domainip, "") == 0)
        {
            domain_to_ip_sss("www.126.com", domainip);
//            LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            if(strcmp(domainip, "") != 0)
            {
//                LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                break;;
            }
        }
        else
        {
//            LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            break;
        }
//        LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        usleep(500000);
    }

//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

again:
    ret = down_load_file(server_www, CLOUDSERVE_DOWNLOAD_SERVERIP, 300);
    if(ret != 0)
    {
//        LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
		ret = down_load_file(server_www, CLOUDSERVE_DOWNLOAD_SERVERIP, 300);
		if(ret != 0)
		{
			ret = down_load_file(server_www, CLOUDSERVE_DOWNLOAD_SERVERIP, 300);
		}		        
    }

//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
	if(ret == 0)
	{
//        LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        //SystemInstead("cp /sdcard/down/DownloadService_serverip.txt /media/C/DownloadService_serverip_c.txt  -rf");
	}
	else
	{
//        LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
		if(access(CLOUDSERVE_DOWNLOAD_SERVERIP, 0) == 0)
        {
//            LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            //SystemInstead("cp /sdcard/down/DownloadService_serverip.txt /sdcard/down/DownloadService_serverip.txt -rf");
        }
        else
        {
//            LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            sleep(2);
            goto again;
        }
	}
//    LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
	parse_serverip_file(CLOUDSERVE_DOWNLOAD_SERVERIP);
//    LOGD("dfdf download_init_proc %s  %s, %d, %s", server_http , __FILE__,__LINE__,__FUNCTION__);
	if(strlen(server_http) == 0)
	{
//        LOGD("dfdf download_init_proc  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        
		parse_serverip_file("/root/company_serverip.txt.txt");
		if(strlen(server_http) == 0)
		{
			sleep(2);
			goto again;
		}
	}
		

    DEBUG("\n================================================\n");
    DEBUG("    DownloadService Start !\n");
    DEBUG2("  (user)  MAC:%s\n",mac);
    DEBUG("================================================\n");

    return 0;
}


//获取系统磁盘 zhenyubin 2014-04-25
int download_get_disk()
{
    FILE *fp = NULL;
    char line[1024] = "";
    char *chrstr1=NULL;
    int i=0;
    struct statfs diskInfo;
    char cmd[256] = "";
	int flag = 0;

    //查找挂载点设备
    //if((fp = fopen("/proc/mounts", "r")) == NULL)
    if(1)
    {
        perror("Error:::::[CloudAddSong.c Open /proc/mounts failed!]");
        return -1;
    }

    for(i=0; i<8; i++)
    {
        memset(disk[i],0,sizeof(disk[i]));
    }
    disk_counts = 0;

    while(!feof(fp))
    {
        memset(line,0,sizeof(line));
        if(fgets(line,sizeof(line),fp) != NULL)
        {
            if ((chrstr1=strstr(line,"/media/C")) != NULL)
            {
				flag = 1;
                continue;
            }
            if( ((chrstr1=strstr(line,"/media/D")) != NULL) || ((chrstr1=strstr(line,"/media/E")) != NULL) \
                    || ((chrstr1=strstr(line,"/media/F")) != NULL)) //查找挂载点设备 UsbDevice
            {
                if(disk_counts<8 && (strstr(line,"/media/usb") == NULL))
                {
                    //拷贝 /mnt/disk0 共10个字符
                    strncpy(disk[disk_counts],chrstr1,strlen("/media/D"));
                    sprintf(cmd, "rm %s/*.st -rf", disk[disk_counts]);
                    SystemInstead(cmd);
                    memset(cmd, '\0', sizeof(cmd));
                    sprintf(cmd, "rm %s/*.mpg_tmp -rf", disk[disk_counts]);
                    SystemInstead(cmd);
//                    printf("\n[ downloadservice.c Disk %d]:%s\n",disk_counts+1,disk[disk_counts]);
                    disk_counts++;
                }
            }
        }
    }

    //关闭文件
    fclose(fp);

    memset(cmd, '\0', sizeof(cmd));
    strcpy(cmd, "rm /media/C/*.st -rf");
    SystemInstead(cmd);
    memset(cmd, '\0', sizeof(cmd));
    strcpy(cmd, "rm /media/C/*.mpg_tmp -rf");
    SystemInstead(cmd);

	if(disk_counts == 0 && flag == 1)	//只有一个分区的时候，歌曲可以下载到C盘
	{
		strcpy(disk[disk_counts],"/media/C");		
//		printf("\n[ downloadservice.c Disk %d]:%s\n",disk_counts+1,disk[disk_counts]);
		disk_counts++;
	}

    return 0;
}


//查找是否有大于need_size空间的磁盘，并返回索引,都是以M为单位
int download_get_disk_free(int need_size)
{
    int i=0;
    long size=0;
    struct statfs diskInfo;

    for(i=0; i<disk_counts; i++)
    {
//        printf("--------------disk[%d]=%s\n", i, disk[i]);

        if(statfs(disk[i],&diskInfo) == 0)
        {
            //size = diskInfo.f_bavail*diskInfo.f_bsize/1024/1024; //计算剩余的空间大小 M
//            printf("--------------f_bavail=%d,f_bsize=%d\n", diskInfo.f_bavail, diskInfo.f_bsize);
            size = diskInfo.f_bavail*diskInfo.f_bsize/1000/1000; //计算剩余的空间大小 M
//            printf("--------------%s %d: size = %ld\n", __FUNCTION__, __LINE__, size);
			
            if(strcmp(disk[i], CLOUDSERVE_DOWNLOADSONGPATH) == 0)
			{
				if(size > 500)
	            {
	                return i;
	            }
			}
			else
			{
				if(size > 50)
	            {
	                return i;
	            }
			}
			
        }
        else
        {
            perror("statfs");
        }
    }
    return -1;
}

//查找歌曲是否存在，存在需要读出当前大小，需要断点续传
int download_get_file_size(char *song_name)
{
    int result=0;
    struct stat song_info;

    result=stat(song_name,&song_info);
    if(result == 0)
    {
        return (song_info.st_size);
    }

    return 0;
}

//设置Get Http头
int download_set_get_http(char *head,char *cmd,char *ip,int port)
{
    char buff[255];

    if (NULL==head)
    {
        return -1;
    }

    if(port == 0)
    {
        port = 80;	//如果没有端口号，默认为80
    }

    //获取歌曲信息
    memset(buff,0x0,sizeof(buff));
    sprintf(head,"GET /%s HTTP/1.1\r\n",cmd);
    sprintf(buff,"Host: %s:%d\r\n",ip,port);
    strcat(head,buff);
    strcat(head,"Cache-Control: no-cache\r\n");
    strcat(head,"User-Agent: Mozilla/5.0 (Windows NT 6.1)\r\n");
    strcat(head,"Accept-Language: zh-CN,zh;q=0.8\r\n");
    strcat(head,"Connection: close\r\n\r\n");

    return 0;
}

//建立TCP socket连接
//发送接收超时，毫秒级，返回连接成功的socket
int download_connect_by_tcp(char *ip,int port,int send_timeout,int recv_timeout)
{
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    int result=0;
    int socket_fd = 0;
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    //地址，发送，接收超时
    struct sockaddr_in server_addr;
    struct timeval timeout_send;
    struct timeval timeout_recv;

    // 创建套节字
    socket_fd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if(socket_fd <= 0)
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        perror("Error::::::[DownloadService.c create socket Failed]");
        return -1;
    }

    timeout_send.tv_sec = send_timeout;
    timeout_send.tv_usec = (int)(send_timeout*1000);

    timeout_recv.tv_sec = send_timeout;
    timeout_recv.tv_usec = (int)(recv_timeout*1000);

    bzero(&server_addr,sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);
    if(strcmp(ip,"") != 0)
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        inet_pton(AF_INET,ip,&server_addr.sin_addr);
    }
    else //连接本地地址服务，即与下载服务通信
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    }

    //socket可以重复bind
    //setsockopt(socket_fd,SOL_SOCKET,SO_REUSEADDR,&reuse,sizeof(reuse));

    //获得发送缓冲区的大小
    //send_buf_len=sizeof(int);
    //getsockopt(socket_fd,SOL_SOCKET,SO_SNDBUF,&send_buf_size,&send_buf_len);
    //DEBUG("[--Create Socket Send Buff Size:%d--]\n",send_buf_size);
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    result=connect(socket_fd,(struct sockaddr *)&server_addr, sizeof(server_addr));
    if(result<0)
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//        LOGD("Error::::::[download_connect_by_tcp Connect socket Failed]");
        close(socket_fd);
        socket_fd=0;
        return -1;
    }
    //屏蔽NEEG算法
    //setsockopt(socket_fd,IPPROTO_TCP,TCP_NODELAY,(char *)&reuse, sizeof(reuse) );

    //非阻塞方式
    //ioctl(socket_fd,FIONBIO,(unsigned long *)&ul);
    setsockopt(socket_fd,SOL_SOCKET,SO_SNDTIMEO,&timeout_send,sizeof(struct timeval));
    setsockopt(socket_fd,SOL_SOCKET,SO_RCVTIMEO,&timeout_recv,sizeof(struct timeval));
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    return socket_fd;
}

//获取http中content-length
int download_get_http_contentlength(char *buffer)
{
    unsigned long len=0;
    char *chstr1=NULL;

    if(buffer == NULL)
    {
        return -1;
    }

    chstr1=strstr(buffer,"Content-Length: ");
    if(chstr1 && chstr1+16)
    {
        len=atoi(chstr1+16);
    }

    return len;
}

//获取Http中的MD5
int download_get_http_md5(char *buffer,char *result)
{
    char *chstr1=NULL;
    char *chstr2=NULL;

    if(buffer == NULL || result == NULL)
    {
        return -1;
    }

    chstr1=strstr(buffer,"Content-MD5: ");
    if(chstr1)
    {
        chstr2=strstr(chstr1,"\r\n");
        if(chstr2)
        {
            strncpy(result,chstr1+13,chstr2-(chstr1+13));
            chstr1=NULL;
            chstr2=NULL;
            return 0;
        }
    }

    chstr1=NULL;
    chstr2=NULL;
    return -1;
}

//计算文件的MD5 zhenyubin 2014-04-16
char *hexstr(unsigned char *buf,int len)
{
    const char *set = "0123456789abcdef";
    static char str[65],*tmp;
    unsigned char *end;

    if(buf == NULL)
    {
        return NULL;
    }

    if (len > 32)
    {
        len = 32;
    }
    end = buf + len;
    tmp = &str[0];
    while (buf < end)
    {
        *tmp++ = set[ (*buf) >> 4 ];
        *tmp++ = set[ (*buf) & 0xF ];
        buf ++;
    }
    *tmp = ' ';

    return str;
}

int download_cal_file_md5(char *file_path,char *content_md5)
{
    int i=0;
    FILE *fd;
    MD5_CTX c;
    unsigned char md5[32]= {0};
    int len;
    char md5_str[128] = "";
    char tmp[64] = "";
    unsigned char *md5_buffer = NULL;

    if(file_path == NULL)
    {
        return -1;
    }

    md5_buffer = (unsigned char *)malloc(MD5_PACKAGE_SIZE);
    if(md5_buffer == NULL)
    {
//        printf("===========%s %d: malloc failed\n", __FILE__, __LINE__);
        return 0;
    }

    fd=fopen(file_path,"r");
    if(fd == NULL)
    {
//        printf("\nError:::::::[cal file md5] Open File Failed:%s",file_path);
        perror("");
        free(md5_buffer);
        return -1;
    }

    MD5_Init(&c);
    memset(md5_buffer,0,MD5_PACKAGE_SIZE);
    while( 0 != (len = fread(md5_buffer, 1, MD5_PACKAGE_SIZE, fd) ) )
    {
        MD5_Update(&c, md5_buffer, len);
        memset(md5_buffer,0,MD5_PACKAGE_SIZE);
    }
    MD5_Final(md5,&c);

    fclose(fd);
    free(md5_buffer);

    memset(tmp,0,sizeof(tmp));
    memset(md5_str,0,sizeof(md5_str));
    for(i=0; i<16; i++)
    {
        sprintf(tmp,"%02x",md5[i]);
        strcat(md5_str,tmp);
    }

    if(strcasecmp(md5_str,content_md5) == 0)
    {
        return 0;
    }
    else
    {
//        printf("\n[Download Service Compare Md5 Error]:File: %s,Line:%d\n",__FILE__,__LINE__);
//        printf("Content-MD5:%s\n",content_md5);
//        printf("Calcula-MD5:%s\n",md5_str);

        return -1;
    }
}
//发送get请求,返回发送成功的socket
int download_send_get_request(char *ip,int port,char *cmd)
{
    char send_buffer[1024];
    int socket_fd=0;
    int result=0;

    memset(send_buffer,0,sizeof(send_buffer));

    //封装get头
    download_set_get_http(send_buffer,cmd,ip,port);

    //建立连接，发送请求
    socket_fd=download_connect_by_tcp(ip,port,1,1);
    if(socket_fd < 0)
    {
        return -1;
    }

    //printf("\n\n--------------------Send Start---------------------------\n%s\n--------------------Send End------------------------\n\n",send_buffer);

    result=send(socket_fd,send_buffer,strlen(send_buffer),MSG_NOSIGNAL);
    if(result<=0)
    {
        perror("Error::::::[DownloadService.c Send Get Request Failed]");
        close(socket_fd);
        socket_fd=0;
        return -1;
    }

    return socket_fd;
}

int debug_info(char *info, int displaynumb)
{
    DOWNLOAD_STRUCT out_struct;
    char str_percent[64] = "";	//百分比字符串
    static char percent_song_bak[64] = "";		//下载歌曲的百分比备份

    strcpy(str_percent, info);
    if(strcmp(percent_song_bak, str_percent) != 0)
    {
        //printf("===================%s %d: str_percent = %s, percent_song_bak = %s=========================\n", __FILE__, __LINE__, str_percent, percent_song_bak);
        strcpy(percent_song_bak, str_percent);
        strcpy(out_struct.content,download_song_id);
        strcat(out_struct.content,":");
        strcat(out_struct.content,str_percent);
        out_struct.cmd=SET_DOWNLOAD_SONG_PERCENT;
        download_send_client(out_struct);
    }
    return 0;
}


/***************************
	从金山云上下载歌曲，如果是多服务器，挑选响应最快的下载。
****************************/

/*
	把下载地址添加到服务队列
*/
void InputServerQue(SERCERIP **tmp,char *url)
{
    SERCERIP *link=*tmp;
    SERCERIP *SRC;
    SRC=malloc(sizeof(SERCERIP));
    if(!SRC)
        return ;
    strcpy(SRC->url,url);
    SRC->next=NULL;
    if(!link)
    {
        *tmp=SRC;
        return ;
    }
    while(link->next)
    {
        link=link->next;
    }
    link->next=SRC;
}

int InitDownInfo(SH_DOWN_SEEP *distmp)
{
    pthread_mutexattr_init(&(distmp->mutex));
    distmp->total = 0;
    distmp->done = 0;
    distmp->speed = 0;
    distmp->enable = 0;
    memset(distmp->name, '\0', sizeof(distmp->name));
    distmp->fun = debug_info;

    return 0;
}

int dowload_song_from_kingsoftcloud(char *file_name)
{
//    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    FILE *fp_cloud = NULL;
    char tmp_str[1024] = "";
    int num_server = 0;
    SERCERIP *url_serve = NULL;
    int ret = 0;
    char MD5[1024] = "";
    char file_path_bak[256] = "";
    char cmd[512] = "";
    InitDownInfo(&down_load_info);
//    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);

    char http_addr_path[512] = {};
    sprintf(http_addr_path, "%s/song_http_addr", CLOUDSERVE_DOWNLOADSONGPATH);
    if((fp_cloud = fopen(http_addr_path, "r")) == NULL)
    {
        return -1;
    }
//    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    while(!feof(fp_cloud))
    {
//        LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        memset(tmp_str, '\0', sizeof(tmp_str));
        fgets(tmp_str, sizeof(tmp_str), fp_cloud);
        if((strncmp(tmp_str, "http", strlen("http")) == 0) || (strncmp(tmp_str, "ftp", strlen("ftp")) == 0))
        {
//            LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
            num_server++;
            int j = 0;
            for(j = strlen(tmp_str) - 1; j >= 0; j--)
            {
//                LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
                if((tmp_str[j] == '\n') || (tmp_str[j] == '\r'))
                {
                    tmp_str[j] = '\0';
                }
                else
                {
                    break;
                }
            }
//            LOGD("dfdf startserver tmp_str %s %s, %d, %s",tmp_str,__FILE__,__LINE__,__FUNCTION__);
            //memset(tmp_str, '\0', sizeof(tmp_str));
            //strcpy(tmp_str, "http://rznetwork.platupdate.cloudsong.kss.ksyun.com/Bco01.zip?AccessKeyId=nXLz3axA9cpFXPsrs0fS&Expires=1485082957&Signature=acPoSdZb39tV0Yok0fGvVyXA7qg%3D&");
            //printf("==================%s %d: server_url = %s\n", __FILE__, __LINE__, tmp_str);
#ifdef AXELLL
            InputServerQue(&url_serve, tmp_str);
#endif
        }
    }

//    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    fclose(fp_cloud);

    if(num_server == 0)
    {
//        printf("===============%s %d: don't have server, tmp_str = %s\n", __FILE__, __LINE__, tmp_str);
        return -1;
    }

//    printf("start Download return Download return Download return file_name=%s\n ", file_name);

    if(strstr(file_name, ".mpg") != NULL)
    {
//        LOGD("dfdf startserver filename %s %s, %d, %s",file_name,__FILE__,__LINE__,__FUNCTION__);
        strncpy(file_path_bak, file_name, strlen(file_name) - 4);
        strcat(file_path_bak, ".mpg_tmp");
    }
#ifndef AXELLL
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    ret = down_load_file(tmp_str,file_name, 30 * 60);LOGD("dfdf startserver ret %d %s, %d, %s",ret,__FILE__,__LINE__,__FUNCTION__);
    if(0 != ret)
        return -1;

#else
//    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    ret = SH_DownloadFileHttpFtp(url_serve, file_path_bak, debug_info, 1, MD5, 1);
    if(ret != 0)
    {
        sprintf(cmd, "rm %s -rf", file_path_bak);
        SystemInstead(cmd);
        memset(cmd, '\0', sizeof(cmd));
        sprintf(cmd, "rm %s.st -rf", file_path_bak);
        SystemInstead(cmd);
        return -1;
    }
//    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if(strlen(MD5) > 0)		//check MD5
    {
        if(download_cal_file_md5(file_path_bak, MD5) != 0)
        {
//            LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
            char temp_cmd[100] = "";
            sprintf(temp_cmd,"rm %s -rf",file_path_bak);
            SystemInstead(temp_cmd);
            return -1;
        }
    }
//    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
#endif
    memset(cmd, '\0', sizeof(cmd));
    sprintf(cmd, "mv %s %s", file_path_bak, file_name);
//    LOGD("dfdf startserver cmd %s %s, %d, %s",cmd,__FILE__,__LINE__,__FUNCTION__);
    SystemInstead(cmd);
    SystemInstead("sync");
//    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    return 0;
}


/*****************************************
	http下载文件，支持断点续传
	file_dir: 文件路径
	file_name:文件名字
	flag: 0，下载歌曲文件 1：下载数据库 2:下载在线更新包
******************************************/
int download_file_from_http(char *file_dir,char *file_name,DOWNLOAD_CMD type)
{
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    char md5[128] = "";		//MD5信息
    FILE *fp = NULL;	//打开文件
    char file_path[512] = ""; //文件全部路径
    int i=0;
    char cmd[512] = "";

    char url[1024] = "";
    char recv_buffer[2*1024] = "";
    char send_data[1024] = "";
    char send_tmp[1024] = "";
    char sign[128] = "";

    int result=0;
    char *chstr1=NULL;
    char *chstr2=NULL;
    unsigned long current_time = 0;
    char RandomString[RANDOM_LEN*2] = "abcd0123";
    cJSON *json = NULL;
    char *p_str = NULL;
    cJSON * sub_json = NULL;
    cJSON * JsonRoot2 = NULL;
    int ret = 0;
    int down_ok = 0;

    if( file_name == NULL)
    {
        return -1;
    }
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    current_time = time(NULL);
    getRandomString(RANDOM_LEN, RandomString);
//    LOGD("dfdf startserver type %d  %s, %d, %s", type, __FILE__,__LINE__,__FUNCTION__);
    switch(type)
    {
        case GET_DATABASE_SINGLE:
        {
//            sprintf(url, "%s%s", server_http, "/KfunCloud/DownVersion"); 	//http链接
            sprintf(url, "%s", "http://ks3.cloud.joyk.com.cn/App/DownVersion");

            //for(i = 0; i < server_db_version.version_cnt; i++)
            {

                memset(send_tmp, '\0', sizeof(send_tmp));
                memset(send_data, '\0', sizeof(send_data));
                memset(recv_buffer, '\0', sizeof(recv_buffer));

                LOGD("mymymy mac = %s", mac);

                sprintf(send_tmp, "cpuid=%s&mac=%s&noncestr=%s&subject=%s&timestamp=%ld&version=%d&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", mStrCPU, mStrMac, RandomString, mStrSubject,current_time, atoi(file_name));
                encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

                sprintf(send_data, "cpuid=%s&mac=%s&noncestr=%s&subject=%s&timestamp=%ld&version=%d&sign=%s", mStrCPU, mStrMac, RandomString,mStrSubject, current_time, atoi(file_name), sign);

                printf("=============%s %d: url = %s\n", __FILE__, __LINE__, url);
                printf("=============%s %d: send_data = %s\n", __FILE__, __LINE__, send_data);
                result = post_data(url, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
                if(result != 0)
                {
                    result = post_data(url, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
                    if(result != 0)
                    {
                        printf("================%s %d: post_data failed result = %d\n", __FILE__, __LINE__, result);
                        return -1;
                    }
                }

                json = cJSON_Parse(recv_buffer);
                if(json == NULL)
                {
//                printf("=================%s %d: json format error\n", __FILE__, __LINE__);
                    return -1;
                }

                sub_json = cJSON_GetObjectItem(json, "code");
                if(sub_json != NULL && (p_str = sub_json->valuestring) != NULL)
                {
                    if(atoi(p_str) == 0)
                    {
                        sub_json = cJSON_GetObjectItem(json, "result");
                        if(sub_json != NULL && (p_str = sub_json->valuestring) != NULL)
                        {
                            sprintf(file_path, "%s", file_dir);

                            result = down_load_file(p_str, file_path, 60*60);
                            if(result != 0)
                            {
                                result = down_load_file(p_str, file_path, 60*60);
                            }
                            if(result == 0)
                            {
//                                fputs(file_path, fp);
//                                fputs("\n", fp);
                                cJSON_Delete(json);
                                return 0;
                            }
                        }
                        else
                        {
                            printf("==========%s %d: result == NULL\n", __FILE__, __LINE__);
                        }
                    }
                    else
                    {
                        printf("==========%s %d: code = %d\n", __FILE__, __LINE__, atoi(p_str));
                        printf("==========%s %d: description = %s\n", __FILE__, __LINE__, cJSON_GetObjectItem(json, "description")->valuestring);
                    }
                }
                else
                {
                    printf("==========%s %d: code == NULL\n", __FILE__, __LINE__);
                }


                cJSON_Delete(json);
                return -1;


            }

            break;
    }
    case GET_DATABASE:
        if(server_db_version.version_cnt == 0)
        {
            return -1;
        }

        sprintf(cmd, "%s/%s", CLOUDSERVE_DOWNLOADDBPATH,DB_LIST_TXT);
        if((fp = fopen(cmd, "w")) == NULL)
        {
//            printf("=================%s %d: fopen %s failed\n", __FILE__, __LINE__, cmd);
            return  -1;
        }

        sprintf(url, "%s%s", server_http, "/KfunCloud/DownVersion"); 	//http链接

        for(i = 0; i < server_db_version.version_cnt; i++)
        {

            memset(send_tmp, '\0', sizeof(send_tmp));
            memset(send_data, '\0', sizeof(send_data));
            memset(recv_buffer, '\0', sizeof(recv_buffer));

            LOGD("mymymy mac = %s", mac);

            sprintf(send_tmp, "cpuid=%s&mac=%s&noncestr=%s&timestamp=%ld&version=%d&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", cpu_id, mac, RandomString, current_time, server_db_version.version_arry[i]);
            encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

            sprintf(send_data, "cpuid=%s&mac=%s&noncestr=%s&timestamp=%ld&version=%d&sign=%s", cpu_id, mac, RandomString, current_time, server_db_version.version_arry[i], sign);

            printf("=============%s %d: url = %s\n", __FILE__, __LINE__, url);
            printf("=============%s %d: send_data = %s\n", __FILE__, __LINE__, send_data);
            result = post_data(url, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
            if(result != 0)
            {               
                result = post_data(url, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
                if(result != 0)
                {
                    printf("================%s %d: post_data failed result = %d\n", __FILE__, __LINE__, result);
                    break;
                }
            }

            json = cJSON_Parse(recv_buffer);
            if(json == NULL)
            {
//                printf("=================%s %d: json format error\n", __FILE__, __LINE__);
                break;
            }

            sub_json = cJSON_GetObjectItem(json, "code");
            if(sub_json != NULL && (p_str = sub_json->valuestring) != NULL)
            {
                if(atoi(p_str) == 0)
                {
                    sub_json = cJSON_GetObjectItem(json, "result");
                    if(sub_json != NULL && (p_str = sub_json->valuestring) != NULL)
                    {
                        sprintf(file_path, "%s/%d", CLOUDSERVE_DOWNLOADDBPATH,server_db_version.version_arry[i]);

                        result = down_load_file(p_str, file_path, 60*60);
                        if(result != 0)
                        {
                            result = down_load_file(p_str, file_path, 60*60);
                        }
                        if(result == 0)
                        {
                            fputs(file_path, fp);
                            fputs("\n", fp);
                            down_ok = 1;
                        }
                    }
                    else
                    {
                        printf("==========%s %d: result == NULL\n", __FILE__, __LINE__);
                    }
                }
                else
                {
                    printf("==========%s %d: code = %d\n", __FILE__, __LINE__, atoi(p_str));
                    printf("==========%s %d: description = %s\n", __FILE__, __LINE__, cJSON_GetObjectItem(json, "description")->valuestring);
                }
            }
            else
            {
                printf("==========%s %d: code == NULL\n", __FILE__, __LINE__);
            }

            cJSON_Delete(json);
            printf("==========%s %d: code == NULL\n", __FILE__, __LINE__);
            if(down_ok == 0)
            {
                break;
            }
        }
        printf("==========%s %d: code == NULL\n", __FILE__, __LINE__);
        if(fp != NULL)
        {
            fclose(fp);
        }
        printf("==========%s %d: code == NULL i= %d \n", __FILE__, __LINE__,i);
        if(i != 0)
        {
            return 0;
        }
        else
        {
            return -1;
        }
        printf("==========%s %d: code == NULL\n", __FILE__, __LINE__);
        break;
    case GET_ONLINE_UPDATE:
        sprintf(url, "%s%s", server_http, "/KfunCloud/DownSoftFile"); 	//http链接
        sprintf(send_tmp, "cpuid=%s&mac=%s&noncestr=%s&soft_type=1213&timestamp=%ld&version=%s&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", cpu_id, mac, RandomString, current_time, file_name);
        encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

        sprintf(send_data, "cpuid=%s&mac=%s&noncestr=%s&soft_type=1213&timestamp=%ld&version=%s&sign=%s", cpu_id, mac, RandomString, current_time, file_name, sign);

//        printf("=============%s %d: url = %s\n", __FILE__, __LINE__, url);
//        printf("=============%s %d: send_data = %s\n", __FILE__, __LINE__, send_data);

        result = post_data(url, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
        if(result != 0)
        {            
            result = post_data(url, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
            if(result != 0)
            {
//                printf("================%s %d: post_data failed result = %d\n", __FILE__, __LINE__, result);
                return -1;
            }
        }

        json = cJSON_Parse(recv_buffer);
        if(json == NULL)
        {
//            printf("=================%s %d: json format error\n", __FILE__, __LINE__);
            return -1;
        }

        JsonRoot2 = cJSON_GetObjectItem(json, "code");
        if(JsonRoot2 != NULL && (p_str = JsonRoot2->valuestring) != NULL)
        {
            if(atoi(p_str) == 0)
            {
                sub_json = cJSON_GetObjectItem(json, "result");
                if(sub_json != NULL)
                {
                    JsonRoot2 = NULL;
                    JsonRoot2 = cJSON_GetObjectItem(sub_json, "downurl");
                    if(JsonRoot2 != NULL && ((p_str = JsonRoot2->valuestring) != NULL))
                    {
                        sprintf(file_path,"%s/%s",file_dir,file_name);

                        result = down_load_file(p_str, file_path, 60*60);
                        if(result == 0)
                        {
                            down_ok = 1;
                        }
                    }
                }
                else
                {
//                    printf("==========%s %d: result == NULL\n", __FILE__, __LINE__);
                }
            }
            else
            {
//                printf("==========%s %d: code = %d\n", __FILE__, __LINE__,atoi(p_str));
//                printf("==========%s %d: description = %s\n", __FILE__, __LINE__, cJSON_GetObjectItem(json, "description")->valuestring);
            }
        }
        else
        {
//            printf("==========%s %d: code == NULL\n", __FILE__, __LINE__);
        }

        cJSON_Delete(json);

        if(down_ok == 1)
        {
            return 0;
        }
        else
        {
            return -1;
        }

        break;
    case GET_SONG:
        //查找歌曲是否曾经传过，并找出位置
//        LOGD("dfdf startserver file_name:%s %s, %d, %s", file_name, __FILE__,__LINE__,__FUNCTION__);
        //for(i=0; i<disk_counts; i++)
        {
            memset(file_path,0,sizeof(file_path));
            //sprintf(file_path,"%s/%s",disk[i], file_name);
            sprintf(file_path,"%s/%s",CLOUDSERVE_DOWNLOADSONGPATH, file_name);

            //if(strcmp(disk[i], "/sdcard/testclouddown") == 0)
            {
                result= 1;
            }
//            LOGD("dfdf startserver file_path %s  %s, %d, %s", file_path, __FILE__,__LINE__,__FUNCTION__);
            if(access(file_path,0) == 0)
            {
                //have_this_file=1;		//断点续传的歌曲也判断磁盘空间大小
//                printf("----------------DownloadService %s have_this_file=1---------------------\n", file_path);
                sprintf(cmd, "rm %s -rf", file_path);	//删除原有文件
                SystemInstead(cmd);
                usleep(100);
            }
        }
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        if(result == 0)
        {
            //sprintf(file_path,"/media/C/%s",file_name);
            sprintf(file_path,"%s/%s",CLOUDSERVE_DOWNLOADSONGPATH, file_name);
            if(access(file_path,0) == 0)
            {
//                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//                printf("----------------DownloadService %s have_this_file=1---------------------\n", file_path);
                sprintf(cmd, "rm %s -rf", file_path);	//删除原有文件
                SystemInstead(cmd);
                usleep(100);
            }
        }
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        result=0;//download_get_disk_free(0);
        if(result<0)
        {
//            printf("Error::::::[DownloadService.c No Enough Disk Space Need\n");
            return -2;
        }
        memset(file_path,0,sizeof(file_path));
        //sprintf(file_path,"%s/%s",disk[result],file_name);
            sprintf(file_path,"%s/%s",CLOUDSERVE_DOWNLOADSONGPATH,file_name);
//        printf("====================%s %d: file_path = %s\n", __FILE__, __LINE__, file_path);

        char tmp_cmd[512] = "";
        sprintf(tmp_cmd, "rm -f %s.st", file_path);
        SystemInstead(tmp_cmd);
        memset(tmp_cmd, '\0', sizeof(tmp_cmd));
        sprintf(tmp_cmd, "rm -f %s_tmp", file_path);
        SystemInstead(tmp_cmd);



        memset(download_song_id, '\0', sizeof(download_song_id));
        strncpy(download_song_id, file_name, strlen(file_name) - 4);

//        LOGD("dfdf startserver download_song_id %s  %s, %d, %s", download_song_id, __FILE__,__LINE__,__FUNCTION__);

//        sprintf(url, "%s%s", server_http, "/KfunCloud/DownSongFile");		//http链接
        sprintf(send_tmp, "cpuid=%s&mac=%s&noncestr=%s&songid=%s&subject=%s&timestamp=%ld&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", mStrCPU, mStrMac, RandomString, download_song_id,mStrSubject, current_time);
        encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

        sprintf(send_data, "cpuid=%s&mac=%s&noncestr=%s&songid=%s&subject=%s&timestamp=%ld&sign=%s", mStrCPU, mStrMac, RandomString, download_song_id,mStrSubject, current_time, sign);

//        printf("=============%s %d: url = %s\n", __FILE__, __LINE__, url);
//        printf("=============%s %d: send_tmp = %s\n", __FILE__, __LINE__, download_song_id);
        printf("=============%s %d: send_data = %s\n", __FILE__, __LINE__, send_data);

        char a[1024];
        char b[1024];
        char c[1024];
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        result = post_data(mStrDownloadUrl, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
        if(result != 0)
        {
            LOGD("dfdf startserver send_data %d %s  %s, %d, %s", result,  send_data, __FILE__,__LINE__,__FUNCTION__);
            result = post_data(mStrDownloadUrl, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
            if(result != 0)
            {
                printf("================%s %d: post_data failed result = %d\n", __FILE__, __LINE__, result);
                return -1;
            }
        }
        printf("=============%s %d: recv_buffer = %s\n", __FILE__, __LINE__, recv_buffer);
        json = cJSON_Parse(recv_buffer);
        if(json == NULL)
        {
            printf("=================%s %d: json format error\n", __FILE__, __LINE__);
            return -1;
        }
        LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        sub_json = cJSON_GetObjectItem(json, "code");
        if(sub_json != NULL && (p_str = sub_json->valuestring) != NULL)
        {
            LOGD("dfdf startserver sub_json%s %s, %d, %s",sub_json->valuestring,__FILE__,__LINE__,__FUNCTION__);
            if(atoi(p_str) == 0)
            {
                sub_json = cJSON_GetObjectItem(json, "result");
                if(sub_json != NULL && (p_str = sub_json->valuestring) != NULL)
                {
                    char http_addr_path[512] = {};
                    sprintf(http_addr_path, "%s/song_http_addr", CLOUDSERVE_DOWNLOADSONGPATH);
                    if((fp = fopen(http_addr_path, "w")) != NULL)
                    {
                        fputs(p_str, fp);
                        LOGD("dfdf startserver %s %s, %d, %s",p_str,__FILE__,__LINE__,__FUNCTION__);
                        down_ok = 1;
                        fclose(fp);
                    }
                    else
                    {
                        printf("=============%s %d: fopen song_http_addr failed\n", __FILE__, __LINE__);
                    }
                }
                else{
                    printf("==========%s %d: result == NULL\n", __FILE__, __LINE__);
                }
            }
            else
            {
                printf("==========%s %d: code = %d\n", __FILE__, __LINE__, atoi(p_str));
                printf("==========%s %d: description = %s\n", __FILE__, __LINE__, cJSON_GetObjectItem(json, "description")->valuestring);
            }
        }
        else
        {
//            printf("==========%s %d: code == NULL\n", __FILE__, __LINE__);
        }

            if(cJSON_GetObjectItem(json, "description")->valuestring!=NULL && strlen(cJSON_GetObjectItem(json, "description")->valuestring)!=0)
            {
                memset(mStrDownSongError, '\0', sizeof(mStrDownSongError));
                strcpy(mStrDownSongError, cJSON_GetObjectItem(json, "code")->valuestring);
            }

//            LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        cJSON_Delete(json);

        if(down_ok == 0)
        {
//            LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
            return -1;
        }
        LOGD("dfdf startserver %s, %d, %s,  file_path %s",__FILE__,__LINE__,__FUNCTION__, file_path);
        result = dowload_song_from_kingsoftcloud(file_path);
        if(result != 0)
        {
//            LOGD("dfdf startserver result %d %s, %d, %s",result,__FILE__,__LINE__,__FUNCTION__);
            return -1;
        }

        break;
    default:
        break;
    }

    return 0;
}

//获得数据库版本信息 //download_get_db_net_version
int download_comm_to_cloud(char *arg,DOWNLOAD_CMD type)
{
    char url[1024] = "";
    char recv_buffer[2*1024] = "";
    char send_data[1024] = "";
    char send_tmp[1024] = "";
    char sign[128] = "";

    int result=0;
    char *chstr1=NULL;
    char *chstr2=NULL;
    char temp[256] = "";
    unsigned long current_time = 0;
    char RandomString[RANDOM_LEN*2] = "abcd0123";
    cJSON *json = NULL;
    char *p_str = NULL;
    cJSON * sub_json = NULL;
    cJSON * JsonRoot2 = NULL;
    int ret = 0;

    if(arg == NULL)
    {
//        printf("arg_NULL error!!\n");
        return -1;
    }

    current_time = time(NULL);
    getRandomString(RANDOM_LEN, RandomString);

    switch(type)
    {
    case GET_DB_VERSION:
        memset(db_net_version,0,sizeof(db_net_version));
        sprintf(url, "%s%s", server_http, "/KfunCloud/GetVersion");		//http链接
        sprintf(send_tmp, "cpuid=%s&mac=%s&noncestr=%s&timestamp=%ld&version=%s&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", cpu_id, mac, RandomString, current_time, arg);
        encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

        sprintf(send_data, "cpuid=%s&mac=%s&noncestr=%s&timestamp=%ld&version=%s&sign=%s", cpu_id, mac, RandomString, current_time, arg, sign);
        break;
    case GET_ONLINE_UPDATE_VERSION:
        sprintf(url, "%s%s", server_http, "/KfunCloud/GetSoftVersion");		//http链接
        sprintf(send_tmp, "cpuid=%s&mac=%s&noncestr=%s&soft_type=1213&timestamp=%ld&version=%s&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", cpu_id, mac, RandomString, current_time, arg);
        encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

        sprintf(send_data, "cpuid=%s&mac=%s&noncestr=%s&soft_type=1213&timestamp=%ld&version=%s&sign=%s", cpu_id, mac, RandomString, current_time, arg, sign);

        break;
    case CLOUD_OUT_OF_DATE:
        sprintf(url, "%s%s", server_http, "/KfunCloud/GetUserDetail");		//http链接
        sprintf(send_tmp, "mac=%s&noncestr=%s&timestamp=%ld&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", mac, RandomString, current_time);
        encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

        sprintf(send_data, "mac=%s&noncestr=%s&timestamp=%ld&sign=%s", mac, RandomString, current_time, sign);

        break;
    default:
        break;
    }

//    printf("=============%s %d: url = %s\n", __FILE__, __LINE__, url);
//    printf("=============%s %d: send_data = %s\n", __FILE__, __LINE__, send_data);

    result = post_data(url, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
    if(result != 0)
    {        
        result = post_data(url, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
        if(result != 0)
        {
//            printf("================%s %d: post_data failed result = %d\n", __FILE__, __LINE__, result);
            return -1;
        }
    }
		
    json = cJSON_Parse(recv_buffer);
    if(json)
    {
        if(type != CLOUD_OUT_OF_DATE)
        {
            sub_json = cJSON_GetObjectItem(json, "code");
            if(sub_json != NULL && (p_str = sub_json->valuestring) != NULL)
            {
                ret = atoi(p_str);
                switch(ret)
                {
                case 1:
                case 2:
                case 4:
                    ret = 1;
//                    printf("==========%s %d: code = %d\n", __FILE__, __LINE__, atoi(p_str));
//                    printf("==========%s %d: description = %s\n", __FILE__, __LINE__, cJSON_GetObjectItem(json, "description")->valuestring);
                    goto version_end;
                    break;
                case 0:
                    break;
                default:
                    ret = -1;
//                    printf("==========%s %d: code = %d\n", __FILE__, __LINE__, atoi(p_str));
//                    printf("==========%s %d: description = %s\n", __FILE__, __LINE__, cJSON_GetObjectItem(json, "description")->valuestring);
                    goto version_end;
                    break;
                }
            }
        }
    }
    else
    {		
//		printf("=================%s %d: json format error\n", __FILE__, __LINE__);
        return -1;
    }


    switch(type)
    {
    case GET_DB_VERSION:
        JsonRoot2 = cJSON_GetObjectItem(json, "newversion");
        if(JsonRoot2 != NULL)
        {
            sprintf(db_net_version, "%d", JsonRoot2->valueint);
        }
        JsonRoot2 = NULL;
        if(server_db_version.version_arry != NULL)
        {
            server_db_version.version_cnt = 0;
            free(server_db_version.version_arry);
            server_db_version.version_arry = NULL;
        }

        JsonRoot2 = cJSON_GetObjectItem(json, "result");
        if(JsonRoot2 != NULL)
        {
            int arry_cnt = 0;
            int i = 0;

            arry_cnt = cJSON_GetArraySize(JsonRoot2);
            if(arry_cnt != 0)
            {
                server_db_version.version_cnt = arry_cnt;
                server_db_version.version_arry = (int *)malloc(sizeof(int) * arry_cnt);
                for(i = 0; i < arry_cnt; i++)
                {
                    server_db_version.version_arry[i] = cJSON_GetArrayItem(JsonRoot2, i)->valueint;
//                    printf("================%s %d: server_db_version.version_arry[%d] = %d\n", __FILE__, __LINE__, i, server_db_version.version_arry[i]);
                }
            }

        }
        break;
    case GET_ONLINE_UPDATE_VERSION:
        sub_json = cJSON_GetObjectItem(json, "result");
        if(sub_json != NULL)
        {
            JsonRoot2 = cJSON_GetObjectItem(sub_json, "sermaxversion");
            if(JsonRoot2 != NULL)
            {
                sprintf(program_net_version, "%d", JsonRoot2->valueint);
            }
        }

        break;
    case CLOUD_OUT_OF_DATE:
        memset(cloud_out_date, '\0', sizeof(cloud_out_date));
        strcpy(cloud_out_date, recv_buffer);
//        printf("============%s %d: cloud_out_date = %s\n", __FILE__, __LINE__, cloud_out_date);
        break;
    default:
        break;
    }

version_end:
    cJSON_Delete(json);

    return ret;
}


int download_comm_to_cloud_parsebyout(char *arg,DOWNLOAD_CMD type, parseCallBack callback)
{
    char url[1024] = "";
    char recv_buffer[2*1024] = "";
    char send_data[1024] = "";
    char send_tmp[1024] = "";
    char sign[128] = "";

    int result=0;
    char *chstr1=NULL;
    char *chstr2=NULL;
    char temp[256] = "";
    unsigned long current_time = 0;
    char RandomString[RANDOM_LEN*2] = "abcd0123";
    cJSON *json = NULL;
    char *p_str = NULL;
    cJSON * sub_json = NULL;
    cJSON * JsonRoot2 = NULL;
    int ret = 0;

    if(arg == NULL)
    {
//        printf("arg_NULL error!!\n");
        return -1;
    }

    current_time = time(NULL);
    getRandomString(RANDOM_LEN, RandomString);

    switch(type)
    {
        case GET_DB_VERSION:
            memset(db_net_version,0,sizeof(db_net_version));
//            sprintf(url, "%s%s", server_http, "/KfunCloud/GetVersion");		//http链接
            sprintf(url, "%s", "http://ks3.cloud.joyk.com.cn/App/GetVersion");
            sprintf(send_tmp, "cpuid=%s&mac=%s&noncestr=%s&subject=%s&timestamp=%ld&version=%s&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", mStrCPU, mStrMac, RandomString, mStrSubject, current_time, arg);
            encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

            sprintf(send_data, "cpuid=%s&mac=%s&noncestr=%s&subject=%s&timestamp=%ld&version=%s&sign=%s", mStrCPU, mStrMac, RandomString, mStrSubject, current_time, arg, sign);
            break;
        case GET_ONLINE_UPDATE_VERSION:
            sprintf(url, "%s%s", server_http, "/KfunCloud/GetSoftVersion");		//http链接
            sprintf(send_tmp, "cpuid=%s&mac=%s&noncestr=%s&soft_type=1213&timestamp=%ld&version=%s&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", cpu_id, mac, RandomString, current_time, arg);
            encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

            sprintf(send_data, "cpuid=%s&mac=%s&noncestr=%s&soft_type=1213&timestamp=%ld&version=%s&sign=%s", cpu_id, mac, RandomString, current_time, arg, sign);

            break;
        case CLOUD_OUT_OF_DATE:
            sprintf(url, "%s%s", server_http, "/KfunCloud/GetUserDetail");		//http链接
            sprintf(send_tmp, "mac=%s&noncestr=%s&timestamp=%ld&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", mac, RandomString, current_time);
            encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

            sprintf(send_data, "mac=%s&noncestr=%s&timestamp=%ld&sign=%s", mac, RandomString, current_time, sign);

            break;
        default:
            break;
    }

//    printf("=============%s %d: url = %s\n", __FILE__, __LINE__, url);
//    printf("=============%s %d: send_data = %s\n", __FILE__, __LINE__, send_data);

    result = post_data(url, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
    if(result != 0)
    {
        result = post_data(url, send_data, strlen(send_data), recv_buffer, sizeof(recv_buffer),0);
        if(result != 0)
        {
//            printf("================%s %d: post_data failed result = %d\n", __FILE__, __LINE__, result);
            return -1;
        }
    }

    json = cJSON_Parse(recv_buffer);
    if(json)
    {
        if(type != CLOUD_OUT_OF_DATE)
        {
            sub_json = cJSON_GetObjectItem(json, "code");
            if(sub_json != NULL && (p_str = sub_json->valuestring) != NULL)
            {
                ret = atoi(p_str);
                switch(ret)
                {
                    case 1:
                    case 2:
                    case 4:
                        ret = 1;
//                    printf("==========%s %d: code = %d\n", __FILE__, __LINE__, atoi(p_str));
//                    printf("==========%s %d: description = %s\n", __FILE__, __LINE__, cJSON_GetObjectItem(json, "description")->valuestring);
                        goto version_end;
                        break;
                    case 0:
                        break;
                    default:
                        ret = -1;
//                    printf("==========%s %d: code = %d\n", __FILE__, __LINE__, atoi(p_str));
//                    printf("==========%s %d: description = %s\n", __FILE__, __LINE__, cJSON_GetObjectItem(json, "description")->valuestring);
                        goto version_end;
                        break;
                }
            }
        }
    }
    else
    {
//		printf("=================%s %d: json format error\n", __FILE__, __LINE__);
        return -1;
    }


    switch(type)
    {
        case GET_DB_VERSION:
            callback(json);
//            JsonRoot2 = cJSON_GetObjectItem(json, "newversion");
//            if(JsonRoot2 != NULL)
//            {
//                sprintf(db_net_version, "%d", JsonRoot2->valueint);
//            }
//            JsonRoot2 = NULL;
//            if(server_db_version.version_arry != NULL)
//            {
//                server_db_version.version_cnt = 0;
//                free(server_db_version.version_arry);
//                server_db_version.version_arry = NULL;
//            }
//
//            JsonRoot2 = cJSON_GetObjectItem(json, "result");
//            if(JsonRoot2 != NULL)
//            {
//                int arry_cnt = 0;
//                int i = 0;
//
//                arry_cnt = cJSON_GetArraySize(JsonRoot2);
//                if(arry_cnt != 0)
//                {
//                    server_db_version.version_cnt = arry_cnt;
//                    server_db_version.version_arry = (int *)malloc(sizeof(int) * arry_cnt);
//                    for(i = 0; i < arry_cnt; i++)
//                    {
//                        server_db_version.version_arry[i] = cJSON_GetArrayItem(JsonRoot2, i)->valueint;
////                    printf("================%s %d: server_db_version.version_arry[%d] = %d\n", __FILE__, __LINE__, i, server_db_version.version_arry[i]);
//                    }
//                }
//
//            }
            break;
        case GET_ONLINE_UPDATE_VERSION:
            sub_json = cJSON_GetObjectItem(json, "result");
            if(sub_json != NULL)
            {
                JsonRoot2 = cJSON_GetObjectItem(sub_json, "sermaxversion");
                if(JsonRoot2 != NULL)
                {
                    sprintf(program_net_version, "%d", JsonRoot2->valueint);
                }
            }

            break;
        case CLOUD_OUT_OF_DATE:
            memset(cloud_out_date, '\0', sizeof(cloud_out_date));
            strcpy(cloud_out_date, recv_buffer);
//        printf("============%s %d: cloud_out_date = %s\n", __FILE__, __LINE__, cloud_out_date);
            break;
        default:
            break;
    }

    version_end:
    cJSON_Delete(json);

    return ret;
}


/**********************************
	读写socket
	fd:socket文件描述符
	data：数据内容
	len：数据长度
	mode：是否启用select模式,非select模式直接读取数据
	timeout：超时时长，毫秒级,0是非阻塞
	返回值：成功返回字节数，超时返回-2，失败返回-1
***********************************/
int download_recv(int sockfd,char *data,int len,SELECT_TYPE mode,int timeout)
{
    int read_size=0;
    int result=0;
    fd_set fds;
    struct timeval out; //默认100毫秒等待，要非阻塞就置0
    int can_read=0;

    //容错判断
    if((data == NULL) || (sockfd <= 0))
    {
        return -1;
    }

    //超时时间
    if(mode == SELECT_MODE)
    {
        out.tv_sec=10;
        out.tv_usec=timeout*1000; //毫秒级
    }

    //读取数据
    while(read_size<len)
    {
        if(mode == SELECT_MODE) //select模式
        {
            can_read=0;
            FD_ZERO(&fds); //每次循环都要清空集合，否则不能检测描述符变化
            FD_SET(sockfd,&fds); //添加描述符

            switch(select(sockfd+1,&fds,NULL,NULL,&out))   //select使用
            {
            case -1:  //select错误，退出程序
            {
                if(read_size>0) //之前已经读到数据
                {
                    return read_size;
                }
                else
                {
                    return -1; //error
                }
                break;
            }
            case 0: //超时
            {
                if(read_size>0) //之前已经读到数据
                {
                    return read_size;
                }
                else
                {
                    return -2; //超时
                }
                break;
            }
            default:
            {
                if(FD_ISSET(sockfd,&fds)) //测试sock是否可读，即是否网络上有数据
                {
                    can_read=1;
                }
                else //返回0，即socket描述符不在字符集中
                {
                    if(read_size>0)
                    {
                        return read_size;
                    }
                    else
                    {
                        return -1;
                    }
                }
                break;
            }
            }//switch End
        }
        else //非select模式，直接读取数据
        {
            can_read=1;
        }

        //可以读取数据
        if(can_read == 1)
        {
            result=recv(sockfd,data+read_size,len-read_size,0);
            if(result>0)
            {
                read_size+=result;
            }
            else
            {
                if(read_size>0)
                {
                    return read_size; //未读到指定长度数据，返回实际读到的字节数
                }
                else
                {
                    return -3; //recv 错误肯能是-1,0 -3直接代表是recv而非select错误
                }
            }
        }
    }//while End

    return read_size;
}

//向客户端发送命令
int download_send_client(DOWNLOAD_STRUCT out_struct)
{
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    int fd_out=0;
    fd_set fds;
    struct timeval out; //默认100毫秒等待，要非阻塞就置0
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    fd_out=download_connect_by_tcp("",DOWNLOAD_SERVICE_SEND_PORT,10,10);
    if(fd_out <= 0)
    {
        return -1;
    }
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    out.tv_sec=10;
    out.tv_usec=10*1000; //毫秒级
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    FD_ZERO(&fds); //每次循环都要清空集合，否则不能检测描述符变化
    FD_SET(fd_out,&fds); //添加描述符
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    switch(select(fd_out+1,NULL,&fds,NULL,&out))   //select使用
    {
    case -1:  //select错误，退出程序
    case 0: //超时
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        close(fd_out);
        return -1;
        break;
    }
    default:
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        if(FD_ISSET(fd_out,&fds)) //测试sock是否可读，即是否网络上有数据
        {
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

            //这里将 文件转换
            //int strTempLen = strlen(out_struct.content)+4;LOGD("dfdf startserver%d %s %s, %d, %s",out_struct.cmd, out_struct.content,__FILE__,__LINE__,__FUNCTION__);
            //byte* sendByte = malloc(sizeof(byte)*strTempLen);LOGD("dfdf startserver %d  %s, %d, %s",sizeof(byte)*strTempLen, __FILE__,__LINE__,__FUNCTION__);
            //memset(sendByte, 0, sizeof(byte)*strTempLen);LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            //int cmd = (int)out_struct.cmd;LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            //memcpy(sendByte, &cmd, sizeof(cmd));LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            //memcpy(sendByte+4, out_struct.content, strlen(out_struct.content));LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            if(send(fd_out,&out_struct,sizeof(DOWNLOAD_STRUCT),MSG_NOSIGNAL)>0)
            //if(send(fd_out,&sendByte,strTempLen,MSG_NOSIGNAL)>0)
            {
//                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                close(fd_out);
                //free(sendByte);
                return 0;
            }
            //free(sendByte);
        }
        break;
    }
    }//switch End
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    close(fd_out);
    return -1;
}


int download_send_client2(DOWNLOAD_STRUCT out_struct)
{
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    int fd_out=0;
    fd_set fds;
    struct timeval out; //默认100毫秒等待，要非阻塞就置0
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    fd_out=download_connect_by_tcp("",DOWNLOAD_SERVICE_SEND_PORT,20,20);
    if(fd_out <= 0)
    {
        return -1;
    }
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    out.tv_sec=10;
    out.tv_usec=10*1000; //毫秒级
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    FD_ZERO(&fds); //每次循环都要清空集合，否则不能检测描述符变化
    FD_SET(fd_out,&fds); //添加描述符
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    switch(select(fd_out+1,NULL,&fds,NULL,&out))   //select使用
    {
        case -1:  //select错误，退出程序
        case 0: //超时
        {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            close(fd_out);
            return -1;
            break;
        }
        default:
        {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            if(FD_ISSET(fd_out,&fds)) //测试sock是否可读，即是否网络上有数据
            {
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

                //这里将 文件转换
                //int strTempLen = strlen(out_struct.content)+4;LOGD("dfdf startserver%d %s %s, %d, %s",out_struct.cmd, out_struct.content,__FILE__,__LINE__,__FUNCTION__);
                //byte* sendByte = malloc(sizeof(byte)*strTempLen);LOGD("dfdf startserver %d  %s, %d, %s",sizeof(byte)*strTempLen, __FILE__,__LINE__,__FUNCTION__);
                //memset(sendByte, 0, sizeof(byte)*strTempLen);LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                //int cmd = (int)out_struct.cmd;LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                //memcpy(sendByte, &cmd, sizeof(cmd));LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                //memcpy(sendByte+4, out_struct.content, strlen(out_struct.content));LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                if(send(fd_out,&out_struct,sizeof(DOWNLOAD_STRUCT),MSG_NOSIGNAL)>0)
                    //if(send(fd_out,&sendByte,strTempLen,MSG_NOSIGNAL)>0)
                {
//                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                    char response[8]= "";
                    int ret = 0;
                    ret = recv(fd_out,response,sizeof(response),MSG_WAITALL);
                    download_thread_running=0;
                    send(fd_out,response,sizeof(response),MSG_NOSIGNAL);
                    printf("yyyyy send %d, ret is %d\n", sizeof(response), ret);
                    close(fd_out);
                    //free(sendByte);
                    if(ret > 0)
                        return 0;
                    else
                        return -1;
                }
                //free(sendByte);
            }
            break;
        }
    }//switch End
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    close(fd_out);
    return -1;
}

void *idlePortCount_thread(void *arg){
    while (1) {
        sleep(30);
        DOWNLOAD_STRUCT out_struct = {};
        memset(out_struct.content, 0, sizeof(out_struct.content));

        if (0 == download_thread_running) {
            out_struct.cmd = GET_SONG_IDLE;
            download_send_client(out_struct);

        } else {
            out_struct.cmd = GET_SONG_NOTIDLE;
            download_send_client(out_struct);
        }
    }
}

int idlePortCount = 0;
void idlePortCount_start(){
    pthread_attr_t  attr;
    size_t stacksize = 1024*1024;
    struct sched_param schedling_value;
    pthread_t count_thread_t=0;

    //设置线程为分离属性，以便于线程内部退出时，释放资源
    pthread_attr_init(&attr);
    pthread_attr_getschedparam(&attr, &schedling_value);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
    pthread_attr_setschedpolicy(&attr, SCHED_OTHER);
    pthread_attr_setstacksize(&attr, stacksize);
    pthread_attr_setschedparam(&attr, &schedling_value);


    ////创建发送线程
    if(pthread_create(&count_thread_t,&attr,idlePortCount_thread,NULL))
    {

    }
    else
    {

    }
}
void axel_SetExitFlag(int i);
void download_song_thread2(DOWNLOAD_STRUCT *args);
//int main(int argc,char *argv[])
int startserver()
{
//    LOGD("dfdf startserver %s length %d %s, %d, %s",CLOUDSERVE_DOWNLOADDBPATH, strlen(CLOUDSERVE_DOWNLOADDBPATH),__FILE__,__LINE__,__FUNCTION__);
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    int result=0;
    int temp_socket=0;
    //DOWNLOAD_STRUCT temp_struct;
    pthread_t download_thread_t=0;
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    //socket server
    struct sockaddr_in addrClient;
    int	AddrLen=sizeof(addrClient);
    int Listen;
    int reuse=1;
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    struct timeval timeout_send;
    struct timeval timeout_recv;
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    DIR * dir;
    struct dirent * ptr;
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    dir = opendir(CLOUDSERVE_DOWNLOADDBPATH);
    if(dir == NULL)
    {
        //SystemInstead("mkdir /media/C/update_db");
        pthread_mutex_lock(&mutex);
        char cTempMk[512] = {};
//        LOGD("dfdf startserver id %s  %s, %d, %s",CLOUDSERVE_DOWNLOADDBPATH, __FILE__,__LINE__,__FUNCTION__);
        sprintf(cTempMk, "mkdir %s", CLOUDSERVE_DOWNLOADDBPATH);
//        LOGD("dfdf startserver id %s  %s, %d, %s",cTempMk, __FILE__,__LINE__,__FUNCTION__);
        int id = SystemInstead(cTempMk);
//        LOGD("dfdf startserver id %d  %s, %d, %s",id, __FILE__,__LINE__,__FUNCTION__);
        usleep(1000);
        dir = opendir(CLOUDSERVE_DOWNLOADDBPATH);
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        pthread_mutex_unlock(&mutex);
        if(dir == NULL)
        {
//            printf("=================== DownloadService mkdir %s failed\n", CLOUDSERVE_DOWNLOADDBPATH);
        }
        else
        {
            have_directory = 1;
            closedir(dir);
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        }
    }
    else
    {
        have_directory = 1;
        closedir(dir);
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        //如果目录存在，删除目录中的所有临时文件
        char cTempCmd[512] = {};
        sprintf(cTempCmd, "rm %s/* -rf", CLOUDSERVE_DOWNLOADDBPATH);

        SystemInstead(cTempCmd);
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    }
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    /************************************************
    	屏蔽管道异常信号
    	否则管道写的时候会给程序发一个异常退出的信号
    ************************************************/
    signal(SIGPIPE, SIG_IGN);
//    printf("****************************************\n");
//    printf("  DownloadService %s\n", __DATE__);
//    printf("*****************************************\n");
//    LOGD("dfdf 1928");

    //加载Mac Cpu，创建下载线程
    download_init_proc();

//    printf("***********************DownloadService start*************************\n");

//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    pthread_mutex_init(&mutex_down_file, 0);
    pthread_cond_init(&cond_down_file, NULL);
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    //初始化socket
    bzero(&addrClient,sizeof(addrClient));
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    addrClient.sin_family=AF_INET;
    addrClient.sin_port=htons(DOWNLOAD_SERVICE_RECV_PORT);
    addrClient.sin_addr.s_addr=htonl(INADDR_ANY);

    //初始化监听socket
    Listen=socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    if(Listen < 0)
    {
        perror("OsDefine Create Http Socket Fail !");
        return -1;
    }

    //可重复绑定
    setsockopt(Listen,SOL_SOCKET,SO_REUSEADDR,&reuse,sizeof(reuse));
//    setsockopt(Listen,SOL_SOCKET,SO_REUSEPORT,&reuse,sizeof(reuse));

//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    if(bind(Listen,(struct sockaddr *)&addrClient,sizeof(struct sockaddr)) != 0)
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        close(Listen);
        printf("DownloadService Bind Error !\n");
        return 0;
    }
    ;
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    //监听
    if(listen(Listen,10) != 0)
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        close(Listen);
        perror("DownloadService Listen Error !");
        return 0;
    }

    //发送启动消息
//    SendServiceCreateOK();
//    download_file_from_http("","198404.mpg",GET_SONG);

//    LOGD("dfdf 1966");

    timeout_recv.tv_sec = 50;
    timeout_recv.tv_usec = 100000;
    upDateDb_StartThread();
//    idlePortCount_start();

    while(1)
    {
        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        temp_socket=accept(Listen,(struct sockaddr*)&addrClient,&AddrLen);
        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        if(temp_socket <0 )
        {
            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            perror("OsDefine Accept Error !");
            continue;
        }
        //printf("\nAccept Listen:%d;\n",temp_socket);
        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

        timeout_send.tv_sec = 0;
        timeout_send.tv_usec = 100000;


        setsockopt(temp_socket,SOL_SOCKET,SO_RCVTIMEO,&timeout_recv,sizeof(timeout_recv));
//        setsockopt(temp_socket,SOL_SOCKET,SO_SNDTIMEO,&timeout_send,sizeof(timeout_send));

		// 接收到数据才更新 进入 控制结构体
        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        

        char buff[1024];
        //清空
        memset(buff, 0, sizeof(1024));





        //memset(&temp_struct,0,sizeof(DOWNLOAD_STRUCT));
        //int rlt = recv(temp_socket,&temp_struct,sizeof(DOWNLOAD_STRUCT),0);
        int rlt = recv(temp_socket,buff,  sizeof(buff), 0);
        LOGD("dfdf startserver rlt %d %s, %d, %s", rlt, __FILE__,__LINE__,__FUNCTION__);
        if(rlt>0)
        {
            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            //printf("\nRCV RUNNING:%d\n",download_thread_running);

//            char* c = malloc(4);
//            int i = (int)(*c);
//            free(c);
            int i = 0;
            memcpy( &i, buff, sizeof(i) );
            LOGD("dfdf startserver i %d  %s, %d, %s", i,  __FILE__,__LINE__,__FUNCTION__);






            char bef[512];
            memset(bef, 0, sizeof(bef));
            //buff += 4;
            LOGD("dfdf startserver bef %s  %s, %d, %s", bef,  __FILE__,__LINE__,__FUNCTION__);
            memcpy( bef, buff+4,  rlt-4);


            LOGD("dfdf startserver bef %s  %s, %d, %s", bef,  __FILE__,__LINE__,__FUNCTION__);
            //printf("\n\n\----------in_struct.cmd = %d in_struct.content = %s-----------\n\n",in_struct.cmd, in_struct.content);
            //switch(temp_struct.cmd)
            switch(i)
            {
            case GET_DB_ALL:
            {
                upDateDb_StartThread();
                break;
            }
            case GET_DB_VERSION:
            case SET_CPUID:
            case SET_DISK:
            case GET_ONLINE_UPDATE_VERSION:
            case CLOUD_OUT_OF_DATE:
                //case GET_ONLINE_UPDATE:
            {
                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                /*in_struct.cmd=temp_struct.cmd;
                memset(in_struct.content,0,sizeof(in_struct.content));
                strcpy(in_struct.content,temp_struct.content);
                */
                //download_song_thread(&temp_struct);
//                DOWNLOAD_STRUCT *temp_struct = malloc(sizeof(DOWNLOAD_STRUCT));
//                temp_struct->cmd = 0;
//                memset(temp_struct->content, 0, sizeof( temp_struct->content ));
//                temp_struct->cmd = i;
//                if( sizeof(buff) >= sizeof(bef) ){
//                    memcpy(temp_struct->content, bef, sizeof(bef));
//                }
//                else{
//                    memcpy(temp_struct->content, bef, sizeof(buff));
//                }
//                download_song_thread2(temp_struct);
//                free(temp_struct);
                download_threadStart2Out(i, bef, sizeof(bef));

                break;
            }
            case GET_DOWNSONGTHREAD_IDLE: {
                DOWNLOAD_STRUCT out_struct = {};
                memset(out_struct.content, 0, sizeof(out_struct.content));
                strcpy(out_struct.content, cloud_out_date);

//                if(0 == pthread_mutex_trylock(&DownSongThreadLock))
                if(0 == download_thread_running)
                {
                    out_struct.cmd = GET_SONG_IDLE;
                    download_send_client(out_struct);
//                    pthread_mutex_unlock(&DownSongThreadLock);
                }
                else{
                    out_struct.cmd=GET_SONG_NOTIDLE;
                    download_send_client(out_struct);
                }

                break;
            }
//            case GET_ONLINE_UPDATE:
//            {
////                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//                if(thread_update_running == 1)
//                {
//                    continue;
//                }
////                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//                /*in_struct.cmd=temp_struct.cmd;
//                memset(in_struct.content,0,sizeof(in_struct.content));
//                strcpy(in_struct.content,temp_struct.content);
//                */
//                pthread_attr_t  attr;
//                size_t stacksize = 2048*1024;
//                struct  sched_param schedling_value;
//                pthread_t download_thread_update=0;
////                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//                //设置线程为分离属性，以便于线程内部退出时，释放资源
//                pthread_attr_init(&attr);
//                pthread_attr_getschedparam(&attr, &schedling_value);
//                pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
//                pthread_attr_setschedpolicy(&attr, SCHED_OTHER);
//                pthread_attr_setstacksize(&attr, stacksize);
//                pthread_attr_setschedparam(&attr, &schedling_value);
////                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//                ////创建发送线程
//                if(pthread_create(&download_thread_update,&attr,download_song_thread,&temp_struct))
//                {
//                    perror("Error::::::::[DownloadService.c Create Thread Failed]");
//                    continue;
//                }
//                else
//                {
//                    thread_update_running = 1;
//                }
//            }
//            break;
//            case SONG_SORT:
//                // 排序不需要操作 [5/13/2015 陈林]
//                /*in_struct.cmd=temp_struct.cmd;
//                memset(in_struct.content,0,sizeof(in_struct.content));
//                strcpy(in_struct.content,temp_struct.content);
//                */
////                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//                if(download_thread_running == 1)
//                {
//                    download_thread_running = 0;
//
//                    //pthread_cancel(download_thread_t);
//                    /* 暂时注释掉，后期修改 */
//                    //pthread_cancel(download_thread_t);
//                    int result = pthread_kill(download_thread_t, SIGUSR2);
////                    LOGD("dfdf startserver pt_kl rlt %d %s, %d, %s",result,__FILE__,__LINE__,__FUNCTION__);
////                    printf("**************************pthread_cancel download_thread_t************************\n");
//
//                }
//                break;
            case GET_DATABASE:
                /*in_struct.cmd=temp_struct.cmd;
                memset(in_struct.content,0,sizeof(in_struct.content));
                strcpy(in_struct.content,temp_struct.content);
                */
                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//                if(download_thread_running == 1)
//                {
//                    download_thread_running = 0;
//                    //pthread_cancel(download_thread_t);
//                    int result = pthread_kill(download_thread_t, SIGUSR2);
////                    LOGD("dfdf startserver pt_kl rlt %d %s, %d, %s",result,__FILE__,__LINE__,__FUNCTION__);
////                    printf("**************************pthread_cancel download_thread_t************************\n");
//                }
//                    DOWNLOAD_STRUCT *temp_struct = malloc(sizeof(DOWNLOAD_STRUCT));
//                    temp_struct->cmd = 0;
//                    memset(temp_struct->content, 0, sizeof( temp_struct->content ));
//                    temp_struct->cmd = i;
//                    if( sizeof(buff) >= sizeof(bef) ){
//                        memcpy(temp_struct->content, bef, sizeof(bef));
//                    }
//                    else{
//                        memcpy(temp_struct->content, bef, sizeof(buff));
//                    }
//                download_song_thread2(temp_struct);
                    download_threadStart2Out(i, bef, sizeof(bef));

                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                break;
            case GET_SONG_EXIT:
            {
                if(download_thread_running == 1){
                    axel_SetExitFlag(1);
                }
                break;
            }
            default:
            {
                //没有启动下载线程，重新启动
//                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                if(download_thread_running == 0)
                {
                    char reponseBuf[8] = "";
//                    close(temp_socket);
//                    continue;
                    send(temp_socket, reponseBuf, sizeof(reponseBuf), MSG_NOSIGNAL);
                    int recvnum = 0;
                    recvnum = recv(temp_socket, reponseBuf, sizeof(reponseBuf), MSG_WAITALL);
                    printf("recvnumrecvnum is %d\n", recvnum);



                    /*in_struct.cmd=temp_struct.cmd;
                    memset(in_struct.content,0,sizeof(in_struct.content));
                    strcpy(in_struct.content,temp_struct.content);
                    */
                    pthread_attr_t  attr;
                    size_t stacksize = 2048*1024;
                    struct sched_param schedling_value;

                    //设置线程为分离属性，以便于线程内部退出时，释放资源
                    pthread_attr_init(&attr);
                    pthread_attr_getschedparam(&attr, &schedling_value);
                    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
                    pthread_attr_setschedpolicy(&attr, SCHED_OTHER);
                    pthread_attr_setstacksize(&attr, stacksize);
                    pthread_attr_setschedparam(&attr, &schedling_value);

                    DOWNLOAD_STRUCT *temp_struct = malloc(sizeof(DOWNLOAD_STRUCT));
                    temp_struct->cmd = 0;
                    memset(temp_struct->content, 0, sizeof( temp_struct->content ));
                    temp_struct->cmd = i;
                    if( sizeof(buff) >= sizeof(bef) ){
                        memcpy(temp_struct->content, bef, sizeof(bef));
                    }
                    else{
                        memcpy(temp_struct->content, bef, sizeof(buff));
                    }
                    ////创建发送线程
                    if(pthread_create(&download_thread_t,&attr,download_song_thread,temp_struct))
                    {
                        perror("Error::::::::[DownloadService.c Create Thread Failed]");
                        free(temp_struct);
                        continue;
                    }
                    else
                    {
                        //download_thread_running=1;
                        pthread_detach(download_thread_t) ;//这里进行线程分离，这样线程退出时系统才会释放线程占用的内存
                    }
                }
                break;
            } //End Default:
            }//End Switch
        }//End if

        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        close(temp_socket);
        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    }
    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    return 0;
}

void download_threadStart2Out(int cmd , char *content, int len){
    pthread_attr_t  attr;
    size_t stacksize = 2048*1024;
    struct sched_param schedling_value;
    pthread_t download_thread_t=0;

    //设置线程为分离属性，以便于线程内部退出时，释放资源
    pthread_attr_init(&attr);
    pthread_attr_getschedparam(&attr, &schedling_value);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
    pthread_attr_setschedpolicy(&attr, SCHED_OTHER);
    pthread_attr_setstacksize(&attr, stacksize);
    pthread_attr_setschedparam(&attr, &schedling_value);

    DOWNLOAD_STRUCT *temp_struct = malloc(sizeof(DOWNLOAD_STRUCT));
    temp_struct->cmd = 0;
    memset(temp_struct->content, 0, sizeof( temp_struct->content ));
    temp_struct->cmd = cmd;
    if( sizeof(temp_struct->content) >= len ){
        memcpy(temp_struct->content, content, len);
    }

    ////创建发送线程
    if(pthread_create(&download_thread_t,&attr,download_song_thread2,temp_struct))
    {
        perror("Error::::::::[DownloadService.c Create Thread Failed]");
        free(temp_struct);
    }
    else
    {
        //download_thread_running=1;
    }
}

//处理下载线程
void *download_song_thread(void *args)
{
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    printf("download_song_thread will lock");
//    pthread_mutex_lock(&DownSongThreadLock);
    download_thread_running=1;
    axel_SetExitFlag(0);
    struct sigaction sigact;
    sigact.sa_sigaction = CusturmHandleCloseThread;
    sigact.sa_flags = SA_SIGINFO;
    sigemptyset(&sigact.sa_mask);
    sigaction(SIGUSR2, &sigact, NULL);

    int result=0;
    char cmd[256] = "";
    int ret = 0;
    int index = 0;
    DOWNLOAD_STRUCT in_struct = {};
    DOWNLOAD_STRUCT out_struct = {};
    //JAVA_DOWNLOAD_STRUCT java_struct = {};
    //printf("-------------in_struct.cmd = %d, in_struct = %s-------------\n", in_struct.cmd,in_struct.content);
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

    if(strlen(cpu_id) == 0)
    {
        get_cpu_id();
        //printf("****************************downloadservice get_cpu_id = %s\n", cpu_id);
    }
    if(strlen(disk[0]) == 0)
    {
        download_get_disk();
    }
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    //in_struct = *((DOWNLOAD_STRUCT *)args);
    DOWNLOAD_STRUCT *argg = args;
    in_struct.cmd = argg->cmd;
    memcpy(in_struct.content, argg->content, sizeof(in_struct.content));
    free(argg);


//    LOGD("dfdf startserver %d %s, %d, %s", in_struct.cmd, __FILE__,__LINE__,__FUNCTION__);
    
    switch(in_struct.cmd)
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    case CLOUD_OUT_OF_DATE:
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        
        /*请求云加歌到期日期和付费连接*/
        memset(cloud_out_date, '\0', sizeof(cloud_out_date));
        if((ret = download_comm_to_cloud(in_struct.content,CLOUD_OUT_OF_DATE)) == 0)
        {
            memset(out_struct.content,0,sizeof(out_struct.content));
            strcpy(out_struct.content,cloud_out_date);
            out_struct.cmd=SET_CLOUD_OUT_OF_DATE;
            download_send_client(out_struct);
        }
        break;
    case GET_ONLINE_UPDATE_VERSION:
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        if((ret = download_comm_to_cloud(in_struct.content,GET_ONLINE_UPDATE_VERSION)) == 0)
        {
            memset(out_struct.content,0,sizeof(out_struct.content));
            strcpy(out_struct.content,program_net_version);
            out_struct.cmd=GET_ONLINE_UPDATE_VERSION_OK;
            download_send_client(out_struct);
//            DEBUG2("\nDOWN LOAD GET ONLINE_UPDATE_VERSION: --- --- --- %s\n",program_net_version);
        }
        else
        {
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//            printf("\n\n---------------DOWN LOAD GET ONLINE_UPDATE_VERSION FAILED-------------\n\n");
            memset(out_struct.content, '\0', sizeof(out_struct.content));
            switch(ret)
            {
            case 1:
                strcpy(out_struct.content, "1");
                break;
            case 2:
                strcpy(out_struct.content, "2");
                break;
            default:
                strcpy(out_struct.content, "4");
                break;
            }
            out_struct.cmd=GET_ONLINE_UPDATE_VERSION_FAILED;
            download_send_client(out_struct);
        }
        break;
    case GET_ONLINE_UPDATE:
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        char file_path[64] = "/media/C";

//        DEBUG2("\nDOWN LOAD GET ONLINE UPDATE : --- --- --- %s\n",in_struct.content);
        result=download_file_from_http(file_path,in_struct.content,GET_ONLINE_UPDATE);
        if(result == 0)
        {
            out_struct.cmd=GET_ONLINE_UPDATE_OK;
            memset(out_struct.content,0,sizeof(out_struct.content));
            sprintf(out_struct.content,"%s/%s", file_path, in_struct.content);
            SystemInstead("sync"); //同步硬盘数据
            download_send_client(out_struct);
        }
        else
        {
            //下载失败，删除指定文件
            memset(cmd,0,sizeof(cmd));
            sprintf(cmd, "rm -rf %s/%s", file_path, in_struct.content);
            SystemInstead(cmd);

            out_struct.cmd=GET_ONLINE_UPDATE_FAILED;
            download_send_client(out_struct);
        }
        //下载程序线程退出
        thread_update_running = 0;
    }

    break;

    case GET_DB_VERSION:
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        ret = 0;
        if((ret = download_comm_to_cloud(in_struct.content,GET_DB_VERSION)) == 0)
        {
            memset(out_struct.content,0,sizeof(out_struct.content));
            strcpy(out_struct.content,db_net_version);
            out_struct.cmd=GET_DB_VERSION_OK;
            download_send_client(out_struct);
//            DEBUG2("\nDOWN LOAD GET DBVERSION: --- --- --- %s\n",db_net_version);
        }
        else
        {
//            printf("\n\n---------------DOWN LOAD GET GET_DB_VERSION_FAILED -------------\n\n");
            out_struct.cmd=GET_DB_VERSION_FAILED;
            if(ret == 1)	//没有注册
            {
                strcpy(out_struct.content,"no register");
            }
            download_send_client(out_struct);
        }
        break;
    }
    case GET_DATABASE://下载数据库
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        char file_path_new_android[64] = "";
        sprintf(file_path_new_android, "%supdate_db", MAIN_ROOT_PATH);

//        DEBUG2("\nDOWN LOAD GET DATABASE : --- --- --- %s\n",in_struct.content);
        result=download_file_from_http(CLOUDSERVE_DOWNLOADDBPATH,in_struct.content,GET_DATABASE);
        if(result == 0) {
            printf("DownLoadSucceed[%s:%d]", __FILE__, __LINE__);
            //download_thread_running=0;
            out_struct.cmd=GET_DATABASE_OK;
            memset(out_struct.content,0,sizeof(out_struct.content));
            sprintf(out_struct.content,"%s/%s",file_path_new_android, in_struct.content);
            SystemInstead("sync"); //同步硬盘数据
            download_send_client(out_struct);
            printf("DownLoadSucceed[%s:%d]", __FILE__, __LINE__);
        }
        else
        {
            //下载失败，删除指定文件
            printf("DownLoadSucceed[%s:%d]", __FILE__, __LINE__);
            memset(cmd,0,sizeof(cmd));
            sprintf(cmd,"rm %s/%s -rf",file_path_new_android, in_struct.content);
            SystemInstead(cmd);

            //download_thread_running=0;
            out_struct.cmd=GET_DATABASE_FAILED;
            download_send_client(out_struct);
            printf("DownLoadSucceed[%s:%d]", __FILE__, __LINE__);
        }
        break;
    }
    case GET_SONG: //下载文件
    case GET_SONG_START:
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        get_cpu_id();
        //printf("****************************downloadservice get_cpu_id = %s\n", cpu_id);
//        printf("\n-----------------------\nDownload Service Get Song %s\n", in_struct.content);
//        static char lastsong[1024]="";
//        static time_t lasttime=0;
//        if(0 == strcmp(lastsong, in_struct.content) && ((time(NULL) - lasttime) < 1)){
//            if(1 == download_thread_running)
//                download_thread_running=0;
//        }
        result=download_file_from_http("",in_struct.content,GET_SONG);
//        memset(lastsong, 0, sizeof(lastsong));
//        strlcpy(lastsong, in_struct.content, sizeof(lastsong));
//        lasttime = time(NULL);
        if(result == 0)
        {
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            down_song_faile_cnt = 0;		// 清除记录下载失败的歌曲计数 [7/24/2015 陈林]
            out_struct.cmd=GET_SONG_OK;
            memset(out_struct.content,0,sizeof(out_struct.content));
            strcpy(out_struct.content,in_struct.content);
//            LOGD("dfdf startserver song%s  %s, %d, %s",in_struct.content, __FILE__,__LINE__,__FUNCTION__);
            SystemInstead("sync"); //同步硬盘数据
            printf("addDownSongaddDownSong ok song is %s\n", in_struct.content);

            while(download_send_client2(out_struct) != 0)
            {
//                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//                DEBUG("\n-----------------------\nDownload Service GET_SONG_OK send_client failed\n");
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if(result == -2)
        {
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            out_struct.cmd=GET_DISK_FAILED;
            while (download_send_client2(out_struct) != 0)
            {
//                DEBUG("\n-----------------------\nDownload Service GET_DISK_FAILED send_client failed\n");
            }
        }
        else
        {
            printf("addDownSongaddDownSong failed song is %s\n", in_struct.content);

//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            down_song_faile_cnt++;		// 歌曲下载失败计数 [7/24/2015 陈林]
            if(down_song_faile_cnt % 2 == 0)
            {
                down_song_faile_cnt = 0;
            }
            out_struct.cmd=GET_SONG_FAILED;

            char temp[1024] = "";
            sprintf(temp, "%s||code=%s", in_struct.content, mStrDownSongError);
            strcpy(out_struct.content,temp);
            //strcat(out_struct.content, mStrDownSongError);

//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            while (download_send_client2(out_struct) != 0)
            {
//                DEBUG("\n-----------------------\nDownload Service GET_SONG_FAILED send_client failed\n");
            }
        }

        if(1 == download_thread_running)
            download_thread_running=0;

        break;
    }
    case SET_CPUID:
    {
        //DEBUG2("\n----------------------Download Service Set CPUID:%s\n",in_struct.content);
        if(strcmp(in_struct.content,"") != 0)
        {
            memset(cpu_id,0,sizeof(cpu_id));
            strcpy(cpu_id,in_struct.content);
        }
        break;
    }
    case SET_DISK:
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        
        //应用程序发送可用磁盘，以 , 分割
//        DEBUG2("\n----------------------Download Service Set DISK:%s\n",in_struct.content);
        if(strcmp(in_struct.content,"") != 0)
        {
            char *delim=",";
            char *p;
            int i = 0;

            for(i=0; i<8; i++)
            {
                memset(disk[i],0,sizeof(disk[i]));
            }
            disk_counts = 0;

            p=strtok(in_struct.content,delim);
            do
            {
                if(disk_counts < 8)
                {
                    memset(disk[disk_counts],0,sizeof(disk[disk_counts]));
                    if (strncmp(p, "/media/C", strlen("/media/C")) == 0)   //跳过系统盘
                    {
                        continue;
                    }
                    strcpy(disk[disk_counts],p);
                    disk_counts++;
                }

            }
            while (p=strtok(NULL,delim));
        }
        break;
    }
    default:
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        download_thread_running=0;
        break;
    }
    }//End switch
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//    printf("Song Thread End: download_thread_running = %d, thread_update_running = %d\n",download_thread_running, thread_update_running);
    printf("download_song_thread will unlock");
//    pthread_mutex_unlock(&DownSongThreadLock);
    return NULL;
}

int request_KfunQrCode(WEB_INFO_TYPE type, char * downFilePath)
{
    char url[1024] = "";
    unsigned long current_time = 0;
    char RandomString[RANDOM_LEN*2] = "abcd0123";
    char send_tmp[1024] = "";
    char sign[128] = "";
    char send_buf[1024] = "";
    char reply_data[2048] = "";
    cJSON *json = NULL;
    char *p_str = NULL;
    char *p_str2 = NULL;

    cJSON * sub_json = NULL;
    cJSON * JsonRoot2 = NULL;
    int ret = 0;
    char weixin_code[24] = "";
    int cnt = 0;
    int again_flag = 0;
    int code = -1;

    memset(url, '\0', sizeof(url));
    memset(send_buf, '\0', sizeof(send_buf));
    memset(send_tmp, '\0', sizeof(send_tmp));
    memset(reply_data, '\0', sizeof(reply_data));
    memset(sign, '\0', sizeof(sign));

    strlcpy(url, server_http, sizeof(url));
    if(strlen(url) == 0)
    {
        printf("===================%s %d: no server\n", __FILE__, __LINE__);
        return -1;
    }

    switch(type)
    {
        case TYPE_PHONE:	//΢�Ŷ�ά��
            strcat(url, "/KfunCloud/GetKfunQrCode");
            break;
        case TYPE_XIUXIU_MV:	//����MV��ά��
            strcat(url, "/KfunCloud/GetKfunMVQrCode");
            break;
        case TYPE_DANMU: //��Ļ��ά��
            strcat(url, "/KfunCloud/GetKfunDmQrCode");
            break;
        case TYPE_RANKDB:	//�������ݿ�
            strlcat(url, "/KfunCloud/ShineDemand", sizeof(url));
            break;
        case TYPE_COPYRIGHT:	//��Ȩ����
            strcat(url, "/KfunCloud/ShineCopyrightSong");
            break;
        default:
            return -1;
            break;
    }



    current_time = time(NULL);
    getRandomString(RANDOM_LEN, RandomString);
    switch (type)
    {
        case TYPE_RANKDB:
        case TYPE_COPYRIGHT:
            sprintf(send_tmp, "mac=%s&noncestr=%s&timestamp=%ld&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", mac, RandomString, current_time);	//
            encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));

            sprintf(send_buf, "mac=%s&noncestr=%s&timestamp=%ld&sign=%s", mac, RandomString, current_time, sign);
            ret = post_data(url, send_buf, strlen(send_buf), reply_data, sizeof(reply_data),0);
            if(ret != 0)
            {
                ret = post_data(url, send_buf, strlen(send_buf), reply_data, sizeof(reply_data),0);
            }
            break;
        default:
//            get_weichat_code(weixin_code);
//            if(strlen(weixin_code) == 0)
//            {
//                printf("===================%s %d: no weixin_code\n", __FILE__, __LINE__);
//                return -1;
//            }

//            sprintf(send_tmp, "mac=%s&noncestr=%s&roomid=%s&timestamp=%ld&key=platZP6SNVHJoaTJOq4bon6tXL2HWc5R", mac, RandomString, weixin_code, current_time);	//
//            encrypt_Md5(send_tmp, strlen(send_tmp), sign, sizeof(sign));
//
//
//            sprintf(send_buf, "mac=%s&noncestr=%s&roomid=%s&timestamp=%ld&sign=%s", mac, RandomString, weixin_code, current_time, sign);
//            ret = post_data(url, send_buf, strlen(send_buf), reply_data, sizeof(reply_data),0);
//            if(ret != 0)
//            {
//                ret = post_data(url, send_buf, strlen(send_buf), reply_data, sizeof(reply_data),0);
//            }
            break;
    }
    printf("==================%s %d: http_head = %s\n", __FILE__, __LINE__, url);
    printf("==================%s %d: send_buf = %s\n", __FILE__, __LINE__, send_buf);
    printf("==================%s %d: reply_data = %s\n", __FILE__, __LINE__, reply_data);

    json = cJSON_Parse(reply_data);
    if(json == NULL)
    {
        return -1;
    }

    sub_json = cJSON_GetObjectItem(json, "code");
    if(sub_json != NULL && (p_str = sub_json->valuestring) != NULL)
    {
        code = atoi(p_str);
        if(code == 0)
        {
            sub_json = cJSON_GetObjectItem(json, "result");
            if(sub_json != NULL && (p_str = sub_json->valuestring) != NULL)
            {
                memset(url, '\0', sizeof(url));
                switch(type)
                {
                    case TYPE_PHONE:	//΢�Ŷ�ά��
//                        strcpy(url, "/root/wechat_code.png");
//
//                        web_info[0].PlatCode = TYPE_PHONE;
//                        strcpy(web_info[0].PlatUrls, p_str);

                        break;
                    case TYPE_XIUXIU_MV:	//����MV��ά��
//                        strcpy(url, "/root/xiuxiumv_code.png");
//                        web_info[1].PlatCode = TYPE_XIUXIU_MV;
//                        strcpy(web_info[1].PlatUrls, p_str);

                        break;
                    case TYPE_DANMU: //��Ļ��ά��
//                        strcpy(url, "/root/danmu_code.png");
//                        web_info[2].PlatCode = TYPE_DANMU;
//                        strcpy(web_info[2].PlatUrls, p_str);
                        break;
                    case TYPE_RANKDB:
                        memset(url, 0, sizeof(url));
                        strlcpy(url, downFilePath, sizeof(url));
                        break;
                    case TYPE_COPYRIGHT:
                    {
                        /*
                        char local_version[64] = "";
                        sqlite3_get_field_data("select version from version_copyright",local_version);

                        JsonRoot2 = cJSON_GetObjectItem(json, "lastmodify");
                        if(JsonRoot2 != NULL && (p_str2 = JsonRoot2->valuestring) != NULL)
                        {
                            if(strcmp(p_str2, local_version) != 0)
                            {
                                sprintf(url, "%s/%s", DB_UPDATE_DIR, COPYRIGHT_ZIP);
                            }
                        }
                        if(strlen(url) == 0)
                        {
                            cJSON_Delete(json);
                            return -1;
                        }
                        */
                        //sprintf(url, "%s/%s", DB_UPDATE_DIR, COPYRIGHT_ZIP);
                    }
                        break;
                    default:
                        cJSON_Delete(json);
                        return -1;
                        break;
                }
            }

            ret = down_load_file(p_str, url, 30);
            if(ret != 0)
            {
                ret = down_load_file(p_str, url, 30);
            }
        }
        else
        {
            sub_json = cJSON_GetObjectItem(json, "description");
            if(sub_json && (p_str = sub_json->valuestring) != NULL)
            {
                printf("===============%s %d: description = %s\n", __FILE__, __LINE__, p_str);
            }
            if(code == 1 || code == 2)
            {
                ret = -2;
            }
            else
            {
                ret = -1;
            }
        }
    }
    else
    {
        ret = -1;
    }
    cJSON_Delete(json);

    return ret;
}


void download_song_thread2(DOWNLOAD_STRUCT *args)
{
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    struct sigaction sigact;
    sigact.sa_sigaction = CusturmHandleCloseThread;
    sigact.sa_flags = SA_SIGINFO;
    sigemptyset(&sigact.sa_mask);
    sigaction(SIGUSR2, &sigact, NULL);

    int result=0;
    char cmd[256] = "";
    int ret = 0;
    int index = 0;
    DOWNLOAD_STRUCT in_struct = {};
    DOWNLOAD_STRUCT out_struct = {};
    //JAVA_DOWNLOAD_STRUCT java_struct = {};
    //printf("-------------in_struct.cmd = %d, in_struct = %s-------------\n", in_struct.cmd,in_struct.content);
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

    if(strlen(cpu_id) == 0)
    {
        get_cpu_id();
        //printf("****************************downloadservice get_cpu_id = %s\n", cpu_id);
    }
    if(strlen(disk[0]) == 0)
    {
        download_get_disk();
    }
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

    DOWNLOAD_STRUCT *argg = args;
    in_struct.cmd = argg->cmd;
    memcpy(in_struct.content, argg->content, sizeof(in_struct.content));
    free(argg);
//    LOGD("dfdf startserver %d %s, %d, %s", in_struct.cmd, __FILE__,__LINE__,__FUNCTION__);

    switch(in_struct.cmd)
    {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        case CLOUD_OUT_OF_DATE:
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

            /*请求云加歌到期日期和付费连接*/
            memset(cloud_out_date, '\0', sizeof(cloud_out_date));
            if((ret = download_comm_to_cloud(in_struct.content,CLOUD_OUT_OF_DATE)) == 0)
            {
                memset(out_struct.content,0,sizeof(out_struct.content));
                strcpy(out_struct.content,cloud_out_date);
                out_struct.cmd=SET_CLOUD_OUT_OF_DATE;
                download_send_client(out_struct);
            }
            break;
        case GET_ONLINE_UPDATE_VERSION:
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            if((ret = download_comm_to_cloud(in_struct.content,GET_ONLINE_UPDATE_VERSION)) == 0)
            {
                memset(out_struct.content,0,sizeof(out_struct.content));
                strcpy(out_struct.content,program_net_version);
                out_struct.cmd=GET_ONLINE_UPDATE_VERSION_OK;
                download_send_client(out_struct);
//            DEBUG2("\nDOWN LOAD GET ONLINE_UPDATE_VERSION: --- --- --- %s\n",program_net_version);
            }
            else
            {
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//            printf("\n\n---------------DOWN LOAD GET ONLINE_UPDATE_VERSION FAILED-------------\n\n");
                memset(out_struct.content, '\0', sizeof(out_struct.content));
                switch(ret)
                {
                    case 1:
                        strcpy(out_struct.content, "1");
                        break;
                    case 2:
                        strcpy(out_struct.content, "2");
                        break;
                    default:
                        strcpy(out_struct.content, "4");
                        break;
                }
                out_struct.cmd=GET_ONLINE_UPDATE_VERSION_FAILED;
                download_send_client(out_struct);
            }
            break;
        case GET_ONLINE_UPDATE:
        {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            char file_path[64] = "/media/C";

//        DEBUG2("\nDOWN LOAD GET ONLINE UPDATE : --- --- --- %s\n",in_struct.content);
            result=download_file_from_http(file_path,in_struct.content,GET_ONLINE_UPDATE);
            if(result == 0)
            {
                out_struct.cmd=GET_ONLINE_UPDATE_OK;
                memset(out_struct.content,0,sizeof(out_struct.content));
                sprintf(out_struct.content,"%s/%s", file_path, in_struct.content);
                SystemInstead("sync"); //同步硬盘数据
                download_send_client(out_struct);
            }
            else
            {
                //下载失败，删除指定文件
                memset(cmd,0,sizeof(cmd));
                sprintf(cmd, "rm -rf %s/%s", file_path, in_struct.content);
                SystemInstead(cmd);

                out_struct.cmd=GET_ONLINE_UPDATE_FAILED;
                download_send_client(out_struct);
            }
            //下载程序线程退出
            thread_update_running = 0;
        }

            break;

        case GET_DB_VERSION:
        {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            ret = 0;
            if((ret = download_comm_to_cloud(in_struct.content,GET_DB_VERSION)) == 0)
            {
                memset(out_struct.content,0,sizeof(out_struct.content));
                strcpy(out_struct.content,db_net_version);
                out_struct.cmd=GET_DB_VERSION_OK;
                download_send_client(out_struct);
//            DEBUG2("\nDOWN LOAD GET DBVERSION: --- --- --- %s\n",db_net_version);
            }
            else
            {
//            printf("\n\n---------------DOWN LOAD GET GET_DB_VERSION_FAILED -------------\n\n");
                out_struct.cmd=GET_DB_VERSION_FAILED;
                if(ret == 1)	//没有注册
                {
                    strcpy(out_struct.content,"no register");
                }
                download_send_client(out_struct);
            }
            break;
        }
        case GET_DATABASE://下载数据库
        {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            char file_path_new_android[64] = "";
            //sprintf(file_path_new_android, "%supdate_db", MAIN_ROOT_PATH);

//        DEBUG2("\nDOWN LOAD GET DATABASE : --- --- --- %s\n",in_struct.content);
            result=download_file_from_http(CLOUDSERVE_DOWNLOADDBPATH,in_struct.content,GET_DATABASE);
            if(result == 0) {
                printf("DownLoadSucceed[%s:%d]", __FILE__, __LINE__);
                //download_thread_running=0;
                out_struct.cmd=GET_DATABASE_OK;
                memset(out_struct.content,0,sizeof(out_struct.content));
                sprintf(out_struct.content,"%s/%s",CLOUDSERVE_DOWNLOADDBPATH, in_struct.content);
                SystemInstead("sync"); //同步硬盘数据
                download_send_client(out_struct);
                printf("DownLoadSucceed[%s:%d]", __FILE__, __LINE__);
            }
            else
            {
                //下载失败，删除指定文件
                printf("DownLoadSucceed[%s:%d]", __FILE__, __LINE__);
                memset(cmd,0,sizeof(cmd));
                sprintf(cmd,"rm %s/%s -rf",file_path_new_android, in_struct.content);
                SystemInstead(cmd);

                //download_thread_running=0;
                out_struct.cmd=GET_DATABASE_FAILED;
                download_send_client(out_struct);
                printf("DownLoadSucceed[%s:%d]", __FILE__, __LINE__);
            }
            break;
        }
        case GET_SONG: //下载文件
        case GET_SONG_START:
        {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            get_cpu_id();
            //printf("****************************downloadservice get_cpu_id = %s\n", cpu_id);
//        printf("\n-----------------------\nDownload Service Get Song %s\n", in_struct.content);
            result=download_file_from_http("",in_struct.content,GET_SONG);
            if(result == 0)
            {
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                down_song_faile_cnt = 0;		// 清除记录下载失败的歌曲计数 [7/24/2015 陈林]
                out_struct.cmd=GET_SONG_OK;
                memset(out_struct.content,0,sizeof(out_struct.content));
                strcpy(out_struct.content,in_struct.content);
//            LOGD("dfdf startserver song%s  %s, %d, %s",in_struct.content, __FILE__,__LINE__,__FUNCTION__);
                SystemInstead("sync"); //同步硬盘数据
                if(download_send_client(out_struct) < 0)
                {
//                LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//                DEBUG("\n-----------------------\nDownload Service GET_SONG_OK send_client failed\n");
                }
            }
            else if(result == -2)
            {
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                out_struct.cmd=GET_DISK_FAILED;
                if(download_send_client(out_struct) < 0)
                {
//                DEBUG("\n-----------------------\nDownload Service GET_DISK_FAILED send_client failed\n");
                }
            }
            else
            {
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                down_song_faile_cnt++;		// 歌曲下载失败计数 [7/24/2015 陈林]
                if(down_song_faile_cnt % 2 == 0)
                {
                    down_song_faile_cnt = 0;
                }
                out_struct.cmd=GET_SONG_FAILED;
                strcpy(out_struct.content,in_struct.content);
//            LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
                if(download_send_client(out_struct) < 0)
                {
//                DEBUG("\n-----------------------\nDownload Service GET_SONG_FAILED send_client failed\n");
                }
            }

            //download_thread_running=0;

            break;
        }
        case SET_CPUID:
        {
            //DEBUG2("\n----------------------Download Service Set CPUID:%s\n",in_struct.content);
            if(strcmp(in_struct.content,"") != 0)
            {
                memset(cpu_id,0,sizeof(cpu_id));
                strcpy(cpu_id,in_struct.content);
            }
            break;
        }
        case SET_DISK:
        {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

            //应用程序发送可用磁盘，以 , 分割
//        DEBUG2("\n----------------------Download Service Set DISK:%s\n",in_struct.content);
            if(strcmp(in_struct.content,"") != 0)
            {
                char *delim=",";
                char *p;
                int i = 0;

                for(i=0; i<8; i++)
                {
                    memset(disk[i],0,sizeof(disk[i]));
                }
                disk_counts = 0;

                p=strtok(in_struct.content,delim);
                do
                {
                    if(disk_counts < 8)
                    {
                        memset(disk[disk_counts],0,sizeof(disk[disk_counts]));
                        if (strncmp(p, "/media/C", strlen("/media/C")) == 0)   //跳过系统盘
                        {
                            continue;
                        }
                        strcpy(disk[disk_counts],p);
                        disk_counts++;
                    }

                }
                while (p=strtok(NULL,delim));
            }
            break;
        }
        default:
        {
//        LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
            //download_thread_running=0;
            break;
        }
    }//End switch
//    LOGD("dfdf startserver  %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
//    printf("Song Thread End: download_thread_running = %d, thread_update_running = %d\n",download_thread_running, thread_update_running);
    return ;
}

//增加配置文件内键值
int AddConfigKeyValue(char *AppName,char *KeyName,char *filename,char *Value)
{
    FILE *fp=NULL;
    char Line[1024];
    char *Str=NULL;
    char App[512];
    char Key[1024];
    int FindApp=0;
    char fconfigure_content[10*1024];

    if(Value == NULL) return -1;

    fp=fopen(filename, "r");
    if(fp == NULL)
    {
        perror("\n\n[fconfigure.c] Open File Failed ");
        return -1;
    }

    memset(Key,0,sizeof(Key));
    memset(fconfigure_content,0,sizeof(fconfigure_content));
    memset(App,0,sizeof(App));

    strcpy(fconfigure_content,"");
    strcpy(App,"[");
    strcat(App,AppName);
    strcat(App,"]");

    strcpy(Key,KeyName);
    strcat(Key,"=");

    //不需要父节点，直接找节点名称
    if(strcmp(AppName,"NONE") == 0)
    {
        FindApp=1;
    }

    while(!feof(fp))
    {
        memset(Line,0,sizeof(Line));
        if(fgets(Line,sizeof(Line),fp) != NULL)
        {
            //printf("Line: %s\n",Line);
            //找到大节点 如： 【CONFIG】
            if(FindApp == 0)
            {
                Str=strstr(Line,App);
                if(Str != NULL)
                {
                    FindApp=1;
                    //保存所有内容
                    strcat(fconfigure_content,Line);
                    //添加字段内容
                    strcat(Key,Value);
                    strcat(Key,"\n");
                    strcat(fconfigure_content,Key);
                    FindApp=2;
                    continue;
                }
            }

            //保存所有内容
            strcat(fconfigure_content,Line);
        }
    }
    fclose(fp);

    //确认fconfigure_content有内容，否则有可能把touch.ini清空，导致开机不能启动
    if(strlen(fconfigure_content)>5)
    {
        //重新写入文件
        fp=fopen(filename,"w+");
        if(fp != NULL)
        {
            fwrite(fconfigure_content,strlen(fconfigure_content),1,fp);
            fclose(fp);
        }
    }

    SystemInstead("sync");
    usleep(100);
    return 0;
}
//从INI文件读取字符串类型数据
int GetIniKeyString(char *AppName,char *KeyName,char *ReturnValue,char *filename)
{
    FILE *fp=NULL;
    char szLine[1024];
    char tmpstr[1024];
    int rtnval=0;
    int i=0;
    int flag=0;
    char *tmp=NULL;

    memset(szLine,0x0,sizeof(szLine));
    memset(tmpstr,0x0,sizeof(tmpstr));

    if((fp=fopen(filename, "r"))==NULL)
    {
        printf("have   no   such   file <%s>  \n",filename);
        return -1;
    }
    while(!feof(fp))
    {
        rtnval = fgetc(fp);
        if(rtnval == EOF)
        {
            break;
        }
        else
        {
            szLine[i++]=rtnval;
        }
        if('\n'==rtnval)
        {
            i--;
            if('\r'==szLine[i-1])
                i--;
            szLine[i] = '\0';
            i = 0;
            //注释行
            if ((';'==szLine[0])||('#'==szLine[0]))
            {
                memset(szLine,0x0,sizeof(szLine));
                continue;
            }
            else if (('/'==szLine[0])&&('/'==szLine[1]))
            {
                memset(szLine,0x0,sizeof(szLine));
                continue;
            }
            tmp = strchr(szLine, '=');
            if(1==flag)
            {
                if ('['==szLine[0])   //找到下一组[title]表示结束
                {
                    fclose(fp);
                    return -1;
                }
                else if((tmp!=NULL)&&(NULL!=strstr(szLine,KeyName)))
                {
                    fclose(fp);
                    //找key对应变量
                    memset(tmpstr,0x0,sizeof(tmpstr));
                    strcpy(tmpstr,tmp+1);
                    if(0==strcmp(tmpstr,""))
                        return -1;
                    strcpy(ReturnValue,tmpstr);

                    return 0;
                }
            }
            else
            {
                sprintf(tmpstr,"[%s]",AppName);
                if(0==strncmp(tmpstr,szLine,strlen(tmpstr)))
                {
                    //找到title
                    flag = 1;
                }
            }
            memset(szLine,0x0,sizeof(szLine));
        }
    }
    fclose(fp);

    return -1;
}


void CusturmHandleCloseThread(int signal, siginfo_t *siginfo, void *u_contxt)
{
LOGD("\thandle exec for kill\n");
//exit(0);
pthread_exit(NULL);
return;
}


void SendServiceCreateOK(){
    DOWNLOAD_STRUCT out_struct;
    memset(out_struct.content,0,sizeof(out_struct.content));

    out_struct.cmd=CLOUD_SERVICE_OK;
    while(download_send_client(out_struct) != 0){
        //if too often, the android system will report exception that eof
        usleep(1000);
    }

    return;
}

void DownloadServer_SetDownSongPath(char *path){
    memset(CLOUDSERVE_DOWNLOADSONGPATH, 0, sizeof(CLOUDSERVE_DOWNLOADSONGPATH));
    // TODO

    strlcpy(CLOUDSERVE_DOWNLOADSONGPATH, path, sizeof(CLOUDSERVE_DOWNLOADSONGPATH));
}void DownloadServer_SetServerConfigPath(char *path){
    pthread_mutex_init(&mutex, NULL);
    pthread_mutex_lock(&mutex);
    // TODO
    memset(CLOUDSERVE_DOWNLOADDBPATH, 0, sizeof(CLOUDSERVE_DOWNLOADDBPATH));

    strlcpy(CLOUDSERVE_DOWNLOADDBPATH, path, sizeof(CLOUDSERVE_DOWNLOADDBPATH));


    memset(CLOUDSERVE_DOWNLOAD_SERVERIP, 0, sizeof(CLOUDSERVE_DOWNLOAD_SERVERIP));
    sprintf(CLOUDSERVE_DOWNLOAD_SERVERIP, "%s/%s", CLOUDSERVE_DOWNLOADDBPATH, FILENAME_SERVERIP);

    pthread_mutex_unlock(&mutex);
}