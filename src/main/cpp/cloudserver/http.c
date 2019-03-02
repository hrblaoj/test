#include "curl/curl.h"
#include <string.h>

#include "ShineLog.h"

typedef struct MemoryStruct
{
    char *memory;
    size_t length;
    size_t size;
} MemoryStruct;

static size_t callback_get_data(void *ptr, size_t size, size_t nmemb, void *userp)
{
    size_t realsize = size * nmemb;

    struct MemoryStruct *mem = (struct MemoryStruct *)userp;
    if (mem->length > (mem->size +realsize))
    {
        memcpy(&(mem->memory[mem->size]), ptr, realsize);
        mem->size += realsize;
        mem->memory[mem->size] = 0;
    }

    return realsize;
}

static size_t write_data(char *buffer,size_t size, size_t nitems,void *outstream)
{
    int written = fwrite(buffer, size, nitems, (FILE*)outstream);
    return written;
}
static size_t write_buff_data(char *buffer,size_t size, size_t nitems,void *outstream)
{
    memcpy(outstream,buffer,nitems*size);
    return nitems*size;
}
/*
�����ļ�����Э��֧��
url���ص�ַ
file������ļ�����
�ɹ�����0
*/
/* CURLOPT_PROGRESSFUNCTION */
static int prog_cb2 (void *p, double dltotal, double dlnow, double ult, double uln)
{
    int *conn = (int *)p;
    int pro=((dlnow/dltotal)*100);
    if(pro != *conn)
    {
        *conn = pro;
        printf("========%s %d:Progress: %d	\n", __FILE__, __LINE__, pro);
        fflush(NULL);
    }
    //printf("Progress:%d    %s (%g/%g) \n", pro,conn, dlnow, dltotal);
    pro++;
    return 0;
}
int download_buff(char *url,char *file)
{
    CURL *curl;
    CURLcode res=-1;
    curl = curl_easy_init();
    if(curl)
    {
        //	int temp=0;
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void*)file );
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_buff_data);
        curl_easy_setopt(curl, CURLOPT_URL,url);
        curl_easy_setopt(curl, CURLOPT_LOW_SPEED_TIME, 10L);
        curl_easy_setopt(curl, CURLOPT_LOW_SPEED_LIMIT, 50L);
        curl_easy_setopt(curl, CURLOPT_MAX_RECV_SPEED_LARGE, 2000000L);/*��������ٶ�*/
        res = curl_easy_perform(curl);
        curl_easy_cleanup(curl);
        if(res == CURLE_OK)
        {
            return res;
        }
    }
    return res;
}
//http://192.169.8.143/GetServerInfo/GetFootball
int down_load_file(char *url,char *file, int timeout)
{
    LOGD("dfdf down_load_file %s, %s, %d", url, file, timeout);
    LOGD("dfdf down_load_file %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    CURL *curl;
    CURLcode res=-1;
    long  contype;
    if(timeout <= 0)
    {
        timeout = 10;
    }

    LOGD("dfdf down_load_file %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    curl = curl_easy_init();
    if(curl == NULL)
    {
        return res;
    }

    LOGD("dfdf down_load_file file%s %s, %d, %s", file, __FILE__,__LINE__,__FUNCTION__);
    FILE* pFile = fopen( file, "wb" );
    if(pFile)
    {LOGD("dfdf down_load_file %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
        int temp=0;LOGD("dfdf down_load_file %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);

        //curl_easy_setopt( curl, CURLOPT_CONNECTTIMEOUT, 5);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void*)pFile );
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
        curl_easy_setopt(curl, CURLOPT_URL,url);
        curl_easy_setopt(curl, CURLOPT_PROGRESSDATA,&temp);
        curl_easy_setopt(curl, CURLOPT_NOPROGRESS, 0L);
        curl_easy_setopt(curl, CURLOPT_PROGRESSFUNCTION, prog_cb2);
        curl_easy_setopt(curl, CURLOPT_LOW_SPEED_TIME, 10L);
        curl_easy_setopt(curl, CURLOPT_LOW_SPEED_LIMIT, 10L);
        curl_easy_setopt(curl, CURLOPT_MAX_RECV_SPEED_LARGE, 3000000L);/*��������ٶ�*/
        curl_easy_setopt(curl, CURLOPT_TIMEOUT, timeout);
        res = curl_easy_perform(curl);
        curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE , &contype);
        curl_easy_cleanup(curl);
        fclose(pFile);
        if(res == CURLE_OK&&200 == contype)
        {

            printf("download %s success,res = %d,contype is %ld\n",file, res,contype);
            printf("dfdf ========%s %d", __FILE__, __LINE__);

            return res;
        }
        else
        {
            remove(file);
            printf("download fail,res = %d,contype is %ld\n", res,contype);
            res = -1;
        }
    }
    else
    {
        curl_easy_cleanup(curl);
        printf("open file failed file is %s\n", file);
        res=-2;
    }

    LOGD("dfdf down_load_file %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    printf("%s download %s fail:%d\n\r",url,file,res);
    return res;
}

//post����,���ص�����д�뵽�ļ��
int post_replydata_to_file(char *url, char *data, int data_size, char *file, int https /* = 0 */)
{LOGD("dfdf down_load_file %s, %d, %s", __FILE__,__LINE__,__FUNCTION__);
    CURL *curl_handle = NULL;
    CURLSH* share_handle = NULL;
    CURLcode ret = CURLE_OK;
    FILE* pFile = NULL;
    //�õ�url
    if (NULL == url || 0 == strcmp(url, "") || NULL == data)
    {
        return -2;
    }

    curl_handle = curl_easy_init();
    if (NULL == curl_handle)
    {
        perror("error message: ");
        return -3;
    }
    share_handle = curl_share_init();
    if(share_handle == NULL)
    {
        curl_easy_cleanup(curl_handle);
        return -1;
    }
    pFile = fopen( file, "wb" );
    if(pFile == NULL)
    {
        printf("====================%s %d: open %s failed\n", __FILE__, __LINE__, file);
        curl_easy_cleanup(curl_handle);
        curl_share_cleanup(share_handle);
        return -1;
    }

    if (1 == https)
    {
        //�����https
        curl_easy_setopt(curl_handle, CURLOPT_SSL_VERIFYPEER, 0L);
        curl_easy_setopt(curl_handle, CURLOPT_SSL_VERIFYHOST, 0L);
        curl_easy_setopt(curl_handle, CURLOPT_AUTOREFERER, 1L);
        //curl_easy_setopt(curl_handle, CURLOPT_HEADER, 1L);
    }

    curl_share_setopt(share_handle, CURLSHOPT_SHARE, CURL_LOCK_DATA_DNS);

    curl_easy_setopt(curl_handle, CURLOPT_SHARE, share_handle);
    curl_easy_setopt(curl_handle, CURLOPT_DNS_CACHE_TIMEOUT, 60 * 5);

    curl_easy_setopt(curl_handle, CURLOPT_URL, url);
    curl_easy_setopt(curl_handle, CURLOPT_POST, 1L);
    curl_easy_setopt(curl_handle, CURLOPT_POSTFIELDS, data);
    curl_easy_setopt(curl_handle, CURLOPT_POSTFIELDSIZE, data_size);
    curl_easy_setopt(curl_handle, CURLOPT_WRITEDATA, (void*)pFile);
    curl_easy_setopt(curl_handle, CURLOPT_WRITEFUNCTION, write_data);
    curl_easy_setopt(curl_handle, CURLOPT_CONNECTTIMEOUT_MS, 5000);
    curl_easy_setopt(curl_handle, CURLOPT_TIMEOUT_MS, 10000);
    curl_easy_setopt(curl_handle, CURLOPT_NOSIGNAL, 1);
    curl_easy_setopt(curl_handle, CURLOPT_FOLLOWLOCATION, 1);

    ret = curl_easy_perform(curl_handle);
    curl_easy_cleanup(curl_handle);
    curl_share_cleanup(share_handle);

    //�������https
    if (1 != https)
    {
        //curl_slist_free_all(slist);
    }
    fclose(pFile);

    if(ret != CURLE_OK)
    {
        remove(file);
        printf("================%s %d: post_replydata_to_file failed ret = %d\n", __FILE__, __LINE__, ret);
    }

    return ret;
}

//post����
int post_data(char *url, char *data, int data_size, char *reply_data, int reply_len, int https /* = 0 */)
{LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    CURL *curl_handle = NULL;
    CURLcode ret = CURLE_OK;
    CURLSH* share_handle = NULL;
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    struct MemoryStruct chunk;
    chunk.memory = reply_data;
    chunk.length = reply_len;
    chunk.size = 0;
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    //�õ�url
    if (NULL == url || 0 == strcmp(url, "") || NULL == data)
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        return -2;
    }
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    curl_handle = curl_easy_init();
    if (NULL == curl_handle)
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        return -3;
    }
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    share_handle = curl_share_init();
    LOGD("dfdf startserver share_handle %d %s, %d, %s", share_handle, __FILE__,__LINE__,__FUNCTION__);
    if(share_handle == NULL)
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        curl_easy_cleanup(curl_handle);
        return -1;
    }
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if (1 == https)
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        //�����https
        curl_easy_setopt(curl_handle, CURLOPT_SSL_VERIFYPEER, 0L);
        curl_easy_setopt(curl_handle, CURLOPT_SSL_VERIFYHOST, 0L);
        curl_easy_setopt(curl_handle, CURLOPT_AUTOREFERER, 1L);
        //curl_easy_setopt(curl_handle, CURLOPT_HEADER, 1L);
    }
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    curl_share_setopt(share_handle, CURLSHOPT_SHARE, CURL_LOCK_DATA_DNS);

    curl_easy_setopt(curl_handle, CURLOPT_SHARE, share_handle);
    curl_easy_setopt(curl_handle, CURLOPT_DNS_CACHE_TIMEOUT, 60 * 5);

    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    curl_easy_setopt(curl_handle, CURLOPT_URL, url);
    curl_easy_setopt(curl_handle, CURLOPT_POST, 1L);
    curl_easy_setopt(curl_handle, CURLOPT_POSTFIELDS, data);
    curl_easy_setopt(curl_handle, CURLOPT_POSTFIELDSIZE, data_size);
    curl_easy_setopt(curl_handle, CURLOPT_WRITEDATA, (void *)&chunk);
    curl_easy_setopt(curl_handle, CURLOPT_WRITEFUNCTION, callback_get_data);
    curl_easy_setopt(curl_handle, CURLOPT_CONNECTTIMEOUT_MS, 5000);
    curl_easy_setopt(curl_handle, CURLOPT_TIMEOUT_MS, 10000);
    curl_easy_setopt(curl_handle, CURLOPT_NOSIGNAL, 1);
    curl_easy_setopt(curl_handle, CURLOPT_FOLLOWLOCATION, 1);
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    ret = curl_easy_perform(curl_handle);
    curl_easy_cleanup(curl_handle);
    curl_share_cleanup(share_handle);
    //�������https
    if (1 != https)
    {
        //curl_slist_free_all(slist);
    }

    if (CURLE_OK != ret)
    {
        printf("================%s %d: post_data failed ret = %d\n", __FILE__, __LINE__, ret);
        return ret;
    }

    return 0;
}



int post_file(char *url, char *path, int num, char (*name)[128], char (*value)[128], int (*callback)( void *, int, int, void *))
{
    CURL *curl = NULL;
    CURLcode res=-1;
    int i = 0;

    struct curl_httppost *formpost=NULL;
    struct curl_httppost *lastptr=NULL;
    struct curl_slist *headerlist=NULL;
    char buf[] = "Expect:";

    curl_formadd(&formpost, &lastptr,CURLFORM_COPYNAME,"FileData",CURLFORM_FILE,path, CURLFORM_END);

    for(i = 0; i < num; i++)
    {
        curl_formadd(&formpost, &lastptr,CURLFORM_COPYNAME,*(name + i),CURLFORM_COPYCONTENTS, *(value + i),CURLFORM_END);
    }

    curl = curl_easy_init();
    headerlist = curl_slist_append(headerlist, buf);
    if(curl)
    {
        curl_easy_setopt(curl, CURLOPT_URL, url);
        curl_easy_setopt(curl, CURLOPT_HTTPPOST, formpost);
        curl_easy_setopt(curl, CURLOPT_LOW_SPEED_LIMIT,10);
        curl_easy_setopt(curl, CURLOPT_LOW_SPEED_TIME,10);
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, callback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, path);
        curl_easy_setopt(curl, CURLOPT_NOSIGNAL, 1);
        res = curl_easy_perform(curl);
        if(res != CURLE_OK)
        {
            fprintf(stderr, "curl_easy_perform() failed: %s\n",curl_easy_strerror(res));
        }
        curl_easy_cleanup(curl);
    }
    if(formpost)
    {
        curl_formfree(formpost);
    }
    if(headerlist)
    {
        curl_slist_free_all (headerlist);
    }

    return res;
}

int post_file_form_header(char *url, char *path, int num, char (*name)[128], char (*value)[128], int num_header, char (*value_header)[256],int (*callback)( void *, int, int, void *))
{
    CURL *curl = NULL;
    CURLcode res = -1;
    int i = 0;

    struct curl_httppost *formpost=NULL;
    struct curl_httppost *lastptr=NULL;
    struct curl_slist *headerlist=NULL;
    char buf[] = "Expect:";

    curl_formadd(&formpost, &lastptr,CURLFORM_COPYNAME,"FileData",CURLFORM_FILE,path, CURLFORM_CONTENTTYPE, "application/octet-stream", CURLFORM_END);

    for(i = 0; i < num; i++)
    {
        curl_formadd(&formpost, &lastptr,CURLFORM_COPYNAME,*(name + i),CURLFORM_COPYCONTENTS, *(value + i),CURLFORM_END);
    }

    curl = curl_easy_init();

    headerlist = curl_slist_append(headerlist, buf);
    headerlist = curl_slist_append(headerlist, "Content-Type: multipart/form-data");
    for(i = 0; i < num_header; i++)
    {
        curl_slist_append(headerlist, *(value_header + i));
    }

    if(curl)
    {
        curl_easy_setopt(curl, CURLOPT_URL, url);
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerlist);
        curl_easy_setopt(curl, CURLOPT_HTTPPOST, formpost);
        curl_easy_setopt(curl, CURLOPT_LOW_SPEED_LIMIT,10);
        curl_easy_setopt(curl, CURLOPT_LOW_SPEED_TIME,10);
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, callback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, path);
        curl_easy_setopt(curl, CURLOPT_NOSIGNAL, 1);
        res = curl_easy_perform(curl);
        if(res != CURLE_OK)
        {
            printf("================%s %d: post_data failed ret = %d\n", __FILE__, __LINE__, res);
            fprintf(stderr, "curl_easy_perform() failed: %s\n",curl_easy_strerror(res));
        }

        curl_easy_cleanup(curl);
    }
    if(formpost)
    {
        curl_formfree(formpost);
    }
    if(headerlist)
    {
        curl_slist_free_all (headerlist);
    }

    return res;
}


int down_load_file_https(char *url,char *file, int timeout)
{
    CURL *curl;
    CURLcode res=-1;
    long  contype;
    if(timeout <= 0)
    {
        timeout = 10;
    }

    curl = curl_easy_init();
    if(curl == NULL)
    {
        return res;
    }

    FILE* pFile = fopen( file, "wb" );
    if(pFile)
    {
        int temp=0;

        //curl_easy_setopt( curl, CURLOPT_CONNECTTIMEOUT, 5);
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0); // ����֤����
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, 1);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void*)pFile );
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
        curl_easy_setopt(curl, CURLOPT_URL,url);
        curl_easy_setopt(curl, CURLOPT_PROGRESSDATA,&temp);
        curl_easy_setopt(curl, CURLOPT_NOPROGRESS, 0L);
        curl_easy_setopt(curl, CURLOPT_PROGRESSFUNCTION, prog_cb2);
        curl_easy_setopt(curl, CURLOPT_LOW_SPEED_TIME, 10L);
        curl_easy_setopt(curl, CURLOPT_LOW_SPEED_LIMIT, 10L);
        curl_easy_setopt(curl, CURLOPT_MAX_RECV_SPEED_LARGE, 3000000L);/*��������ٶ�*/
        curl_easy_setopt(curl, CURLOPT_TIMEOUT, timeout);
        res = curl_easy_perform(curl);
        curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE , &contype);
        curl_easy_cleanup(curl);
        fclose(pFile);
        if(res == CURLE_OK&&200 == contype)
        {

            printf("download %s success,res = %d,contype is %ld\n",file, res,contype);
            printf("dfdf ========%s %d", __FILE__, __LINE__);
            return res;
        }
        else
        {
            remove(file);
            printf("download fail,res = %d,contype is %ld\n", res,contype);
            res = -1;
        }
    }
    else
    {
        curl_easy_cleanup(curl);
        printf("open file failed\n");
        res=-2;
    }
    printf("%s download %s fail:%d\n\r",url,file,res);
    return res;
}


