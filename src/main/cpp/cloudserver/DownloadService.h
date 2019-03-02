//
// Created by hrblaoj on 2018/5/2.
//

#ifndef SHINEKTV_ANDROID_DOWNLOADSERVICE_H_H
#define SHINEKTV_ANDROID_DOWNLOADSERVICE_H_H

#include "CloudAddSong.h"
#include "UpdateDb.h"


typedef enum
{
    TYPE_PHONE = 1001,	//�ֻ�
    TYPE_MEMBER_SONG,	//��Ա�ղ�
    TYPE_XIUXIU_MV,		//����MV
    TYPE_GAME,			//������Ϸ
    TYPE_TIAOZHAN_DAKA,	//��ս��
    TYPE_LEITAI_PK,		//��̨PK
    TYPE_KSONG,			//K�����
    TYPE_TOUSU_JIANYI,	//Ͷ�߽���
    TYPE_ZAIXIAN_CHAOSHI,//���߳���
    TYPE_QUEGE_DENGJI,	 //ȱ��Ǽ�
    TYPE_ZHANGDAN_CHAXUN,//�˵���ѯ
    TYPE_QIFEN_BIAOQING,//���ձ���
    TYPE_BAOFANG_YUDING,//����Ԥ��
    TYPE_WEIXIN_YONGHU,	//΢���û�����̨PKҳ��ʹ��
    TYPE_DANMU,		//��Ļ
    TYPE_RANKDB,		//�������ݿ�
    TYPE_COPYRIGHT,		//��Ȩ����
}WEB_INFO_TYPE;
#ifdef __cplusplus

extern "C" {
#endif
int startserver();

typedef   unsigned   char   byte;




int request_KfunQrCode(WEB_INFO_TYPE type, char * downFilePath);
void download_threadStart2Out(int cmd , char *content, int len);
void CusturmHandleCloseThread(int signal, siginfo_t *siginfo, void *u_contxt);
int download_file_from_http(char *file_dir,char *file_name,DOWNLOAD_CMD type);
void SendServiceCreateOK();
int encrypt_Md5(char *in_data, int in_len, char *out_data, int out_len);
int post_data(char *url, char *data, int data_size, char *reply_data, int reply_len, int https /* = 0 */);
int down_load_file(char *url,char *file, int timeout);
int SystemInstead(char* cmd);
int download_comm_to_cloud(char *arg,DOWNLOAD_CMD type);
int download_comm_to_cloud_parsebyout(char *arg,DOWNLOAD_CMD type, parseCallBack callback);
void DownloadServer_SetDownSongPath(char *path);
void DownloadServer_SetServerConfigPath(char *path);

void SetCpuIDAndSubjectID(const char *_cpu_id,const char *_mac, const char *_subject);
void SetServerDownloadUrl(const char *_url);
#ifdef __cplusplus
}

#endif

#endif //SHINEKTV_ANDROID_DOWNLOADSERVICE_H_H
