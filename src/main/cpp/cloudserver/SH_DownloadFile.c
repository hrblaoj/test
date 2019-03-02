#include "SH_DownloadFile.h"
#include <stdio.h>
#include <string.h>
#include <netdb.h>
#include <stdlib.h>
#include <netinet/in.h>

#include "ShineLog.h"

//extern int download_main(char *filename,int speed,char *threadnumb,char *url,void *fun);
extern int download_main(char *filename,int speed, int threadnumb,char *url,void *fun,int displaynumb, char *MD5);
//extern int download_main( int argc, char *argv[] );

static void http_encode( char *s )
{
    char t[1024];
    int i, j;

    for( i = j = 0; s[i]; i ++, j ++ )
    {
        /* Fix buffer overflow */
        if (j >= 1024 - 1)
        {
            break;
        }
        t[j] = s[i];
        if( s[i] == ' ' )
        {
            /* Fix buffer overflow */
            if (j >= 1024 - 3)
            {
                break;
            }
            strcpy( t + j, "%20" );
            j += 2;
        }
    }
    t[j] = 0;
    strcpy( s, t );
}
/*
	��url��������ַ
*/

int SH_GetConvertURL(conn_t *conn,char *set_url)
{
    char url[1024];
    char *i, *j;
    /* protocol://							*/
    if( ( i = strstr( set_url, "://" ) ) == NULL )
    {
        conn->proto = 1;/*FTP*/
        strncpy( url, set_url, 1024 );
    }
    else
    {
        if( set_url[0] == 'f' )
            conn->proto = 1;/*FTP*/
        else if( set_url[0] == 'h' )
            conn->proto = 2;
        else
        {
            return( 0 );
        }
        strncpy( url, i + 3, 1024 );
    }

    /* Split							*/
    if( ( i = strchr( url, '/' ) ) == NULL )
    {
        strcpy( conn->dir, "/" );
    }
    else
    {
        *i = 0;
        snprintf( conn->dir, 1024, "/%s", i + 1 );
        if( conn->proto == 2 )
            http_encode( conn->dir );
    }

    strncpy( conn->host, url, 1024 );

    j = strchr( conn->dir, '?' );
    if( j != NULL )
        *j = 0;
    i = strrchr( conn->dir, '/' );
    if( j != NULL )
        *j = '?';
    else
        *i = 0;
    if( i == NULL )
    {
        strncpy( conn->file, conn->dir, 1024 );
        strcpy( conn->dir, "/" );
    }
    else
    {
        strncpy( conn->file, i + 1, 1024 );
        strcat( conn->dir, "/" );
    }

    /* Check for username in host field				*/
    if( strrchr( conn->host, '@' ) != NULL )
    {
        strncpy( conn->user, conn->host, 1024 );
        i = strrchr( conn->user, '@' );
        *i = 0;
        strncpy( conn->host, i + 1, 1024 );
        *conn->pass = 0;
    }/* If not: Fill in defaults					*/else
    {
        if( conn->proto == 1 )/*FTP*/
        {
            /* Dash the password: Save traffic by trying
               to avoid multi-line responses		*/
            strcpy( conn->user, "anonymous" );
            strcpy( conn->pass, "mailto:axel-devel@lists.alioth.debian.org" );
        }
        else
        {
            *conn->user = *conn->pass = 0;
        }
    }
    /* Password?							*/
    if( ( i = strchr( conn->user, ':' ) ) != NULL )
    {
        *i = 0;
        strncpy( conn->pass, i + 1, 1024 );
    }
    /* Port number?							*/
    if( ( i = strchr( conn->host, ':' ) ) != NULL )
    {
        *i = 0;
        sscanf( i + 1, "%i", &conn->port );
    }/* Take default port numbers from /etc/services			*/else
    {
        struct servent *serv;
        if( conn->proto == 1 )/*ftp*/
            serv = getservbyname( "ftp", "tcp" );
        else
            serv = getservbyname( "www", "tcp" );
        if( serv ) {
            conn->port = ntohs(serv->s_port);
        }
        else if( conn->proto == 2 ) /*http*/
            conn->port = 80;
        else
            conn->port = 21;
    }
    printf("%s===\n",conn->host);
    return( conn->port > 0 );
}

/*
	�����ļ�
	�����3M
*/
int SH_DownloadFileFromUrl(char *url,char *pathname,void *fun,int displaynumb, char *MD5, int pthread_num)
{
    printf("============%s %d: begin download:\n", __FILE__, __LINE__);
    int tmp= download_main(pathname,50000000,pthread_num,url,fun,displaynumb, MD5);//50000000 1000000
    printf("============%s %d: return %d\n", __FILE__, __LINE__, tmp);
    return tmp;
#if 0
    if(pathname)
    {
        char *argv[8];
        argv[0]="shine";
        argv[1]= "-o";
        argv[2]=pathname;
        argv[3]="-s";
        argv[4]="3000000";
        argv[5]="-n";
        argv[6]="5";
        argv[7]=url;
        return download_main(8, argv);
    }
    char *argv[8];
    argv[0]="shine";
    argv[1]="-s";
    argv[2]="3000000";
    argv[3]="-n";
    argv[4]="5";
    argv[5]=url;
    return download_main(6, argv);
#endif
}


/*
	��ȡÿ�����������ٶ�
*/
static void GetServerSeep(SERCERIP *tmp)
{
    if(!tmp)
        return ;
    conn_t *conn=malloc(sizeof(conn_t));
    SERCERIP *link=tmp;
    while(link)
    {
        memset(conn,0,sizeof(conn_t));
        if(SH_GetConvertURL(conn,link->url))
        {
            strcpy(link->addr,conn->host);
            //link->time=GetPingTime(conn->host);
            link->time=1000*1000;
            //if(strstr(conn->host,"124.205.49.172")){
            //	link->time=1000*1000;
            //}
        }
        else
        {
            link->time=1000*1000;
        }
        link=link->next;
    }
    free(conn);
}
static int GetServerNumb(SERCERIP *tmp)
{
    int number=0;
    SERCERIP *link=tmp;
    while(link)
    {
        number++;
        link=link->next;
    }
    return number;
}
/*
	�ٶȷ������������
*/
static SERCERIP *SortLinkServer(SERCERIP *head)
{
    SERCERIP*p3=head;
    SERCERIP*p2,*p1,*p4,*p5;
    if((!head)||(!(head->next)))
    {
        return head;
    }

    while(p3->next->next != NULL)
    {
        p2=p3->next;
        p5=p2;
        p1=p2;
        int flag=0;
        while(p1->next != NULL)
        {
            if(p5->time > p1->next->time)
            {
                p5=p1->next;
                p4=p1;
                flag=1;
            }
            p1=p1->next;
        }
        if(flag)
        {
            if(p2==p4)
            {
                p2->next=p5->next;
                p5->next=p2;
                p3->next=p5;
            }
            else
            {
                SERCERIP* temp=p5->next;
                p5->next=p2->next;
                p3->next=p5;
                p4->next=p2;
                p2->next=temp;
            }
        }
        p3=p3->next;
    }
    if(head->next->time < head->time)
    {
        SERCERIP *link;
        link = head;
        head=head->next;
        link->next=head->next;
        head->next=link;
    }
#if 0
    SERCERIP* temp=head;
//	temp=temp->next;
    while(temp != NULL)
    {
        temp=temp->next;
    }
#endif
    return head;
}
static void FreeLinkServer(SERCERIP *head)
{
    SERCERIP* link=head;
    while(head)
    {
        link=head->next;
        free(head);
        head=link;
    }
}
/*
	�Ӷ��url�������ļ�
	�������3M
*/
#include<sys/stat.h>
int SH_DownloadFileSizeHttpFtp(SERCERIP *head,char *filename,void *fun,unsigned int size,int displaynumb)
{

    int downflag=1;
    if(GetServerNumb(head)>=2)
    {
        GetServerSeep(head);/*����*/
        head=SortLinkServer(head);/*����*/
    }
    SERCERIP *link=head;
#if 1
    while(link) /*����*/
    {
        if(!SH_DownloadFileFromUrl(link->url,filename,fun,displaynumb, NULL, 5))
        {
            struct stat st;
            if(!stat(filename,&st))
            {
                if(st.st_size > size)
                {
                    printf("%s ���سɹ� %s\n",link->url,filename);
                    downflag=0;
                    break;
                }
                else
                {
                    printf("%s ��С���� %d\n",filename,st.st_size);
                    break;
                }
            }
            remove(filename);
        }
        printf("%s ����ʧ��\n",link->url);
        link=link->next;
    }
#endif
    FreeLinkServer(head);
    return downflag;
}
int SH_DownloadFileHttpFtp(SERCERIP *head,char *filename,void *fun,int displaynumb, char *MD5, int pthread_num)
{
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    int downflag=1;
    if(GetServerNumb(head)>=2)
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        GetServerSeep(head);/*����*/
        head=SortLinkServer(head);/*����*/
    }
    SERCERIP *link=head;
#if 1
    while(link) /*����*/
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        if(!SH_DownloadFileFromUrl(link->url,filename,fun,displaynumb, MD5, pthread_num))
        {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
            printf("%s sucess %s\n",link->url,filename);
            downflag=0;
            break;
        }LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        printf("%s failed\n",link->url);
        link=link->next;
    }LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
#endif
    FreeLinkServer(head);LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    return downflag;
}





