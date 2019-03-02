/********************************************************************\
* Axel -- A lighter download accelerator for Linux and other Unices. *
*                                                                    *
* Copyright 2001 Wilmer van der Gaast                                *
\********************************************************************/

/* Text interface							*/

/*
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License with
  the Debian GNU/Linux distribution in file /usr/doc/copyright/GPL;
  if not, write to the Free Software Foundation, Inc., 59 Temple Place,
  Suite 330, Boston, MA  02111-1307  USA
*/

#include "axel.h"
#include "../ShineLog.h"

static pthread_mutex_t g_ExitFlagMutex = PTHREAD_MUTEX_INITIALIZER;
static int g_ExitFlag = 0;

void axel_SetExitFlag(int i){
    pthread_mutex_lock(&g_ExitFlagMutex);
    if(0 == g_ExitFlag && i == 1)
        g_ExitFlag = 1;
    else if(1 == g_ExitFlag && i == 0)
        g_ExitFlag = 0;
    pthread_mutex_unlock(&g_ExitFlagMutex);
}

int axel_GetExitFlag(){
    pthread_mutex_lock(&g_ExitFlagMutex);
    if(g_ExitFlag) {
        g_ExitFlag = 0;
        pthread_mutex_unlock(&g_ExitFlagMutex);
        return 1;
    }
    else {
        pthread_mutex_unlock(&g_ExitFlagMutex);
        return 0;
    }
}

static void print_alternate_output(axel_t *axel,void *fun,int displaynumb,char *filename) ;
static void print_version();


/*ִ������*/
//int download_main( int argc, char *argv[] )
int download_main(char *filename,int speed, int threadnumb, char *url,void *fun,int displaynumb, char *MD5)
//int main( int argc, char *argv[] )
{LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    char fn[MAX_STRING] = "";
    int do_search = 0;
    char *filebugg=filename;
    conf_t conf[1] = {};
    axel_t *axel;
    int i = 0,z;
    char s[MAX_STRING] = "";
    int ret = 0;
    if( !conf_init( conf ) )
    {
        return( 1 );
    }
    int tmplen=strlen(filename);
    for(z=tmplen-1; z>0; z--)
    {
        if((filename[z]=='/')||(filename[z]=='\\'))
        {
            filebugg=&filename[z+1];
            break;
        }
    }
    //printf("%s\n",filebugg);
    conf->max_speed=speed;
    strncpy( fn, filename, MAX_STRING );
    conf->num_connections = threadnumb;
    strncpy( s, url, MAX_STRING );
    printf( _("Initializing download: %s\n"), s );
    axel = axel_new( conf, 0, s );LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if( axel->ready == -1 )
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        printf("===============%s %d: return\n", __FILE__, __LINE__);
        axel_close( axel );
        return( 1 );
    }LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if( *fn )
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        struct stat buf;

        if( stat( fn, &buf ) == 0 )
        {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
            if( S_ISDIR( buf.st_mode ) )
            {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
                size_t fnlen = strlen(fn);
                size_t axelfnlen = strlen(axel->filename);

                if (fnlen + 1 + axelfnlen + 1 > MAX_STRING)
                {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
                    fprintf( stderr, _("Filename too long!\n"));
                    return ( 1 );
                }
                LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
                fn[fnlen] = '/';
                memcpy(fn+fnlen+1, axel->filename, axelfnlen);
                fn[fnlen + 1 + axelfnlen] = '\0';
            }
        }LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        strcpy( axel->filename, fn );
    }
    if( !axel_open( axel ) )
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        printf("======================%s %d: axel_open return\n", __FILE__, __LINE__);
        return( 1 );
    }
    axel_start( axel );
    axel->start_byte = axel->bytes_done;
    int cnt = 0;
    while( !axel->ready)
    {//LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        long long int prev;//LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        prev = axel->bytes_done;//LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        ret = axel_do( axel );//LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        if(ret == -2)
        {//LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
            printf("axel_do -2\n");
            sleep(1);
            break;
        }

        if(axel_GetExitFlag()){
            axel_close( axel );
            return -1;
        }
        if( conf->alternate_output )
        {//LOGD("dfdf startserver alternate_output %d %s, %d, %s",conf->alternate_output,__FILE__,__LINE__,__FUNCTION__);
            //!axel->message &&
            if( prev != axel->bytes_done )
            {//LOGD("dfdf startserver bytes_done %lld %s, %d, %s",axel->bytes_done,__FILE__,__LINE__,__FUNCTION__);
                print_alternate_output( axel ,fun,displaynumb,filebugg);
            }
            //LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        }
    }
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    printf("===================%s %d: axel->Fsize = %lld, axel->bytes_done = %lld, \n", __FILE__, __LINE__, axel->Fsize, axel->bytes_done);
    if(axel->ready == 1)
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        if((axel->Fsize - axel->bytes_done == 0) || (axel->Fsize - axel->bytes_done < 1024))
        {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
            i = 0;	//������ɣ��ɹ�
        }
        else
        {
            i = -1;
        }
    }
    else
    {
        i = -1;	//ʧ��
    }
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if(i == 0)		//sucess, check MD5 chenlin
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        strcpy(MD5, axel->conn[0].MD5);
    }
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    axel_close( axel );


    return( i );
}
typedef void (*PGSFUN)(char*,int);
static void print_alternate_output(axel_t *axel,void *fun,int displaynumb,char *filename)
{//LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    long long int done=axel->bytes_done;
    long long int total=axel->Fsize;
    int i,j=0;
    static double oldtime=0;
    double now = gettime();
//	printf("\r[%3ld%%] [", min(100,(long)(done*100./total+.5) ) );
#if 0
    for(i=0; i<axel->conf->num_connections; i++)
    {
        for(; j<((double)axel->conn[i].currentbyte/(total+1)*50)-1; j++)
            putchar('.');

        if(axel->conn[i].currentbyte<axel->conn[i].lastbyte)
        {
            if(now <= axel->conn[i].last_transfer + axel->conf->connection_timeout/2 )
                putchar(i+'0');
            else
                putchar('#');
        }
        else
            putchar('.');

        j++;

        for(; j<((double)axel->conn[i].lastbyte/(total+1)*50); j++)
            putchar(' ');
    }
#endif
    //LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if((now-oldtime)>=1)
    {
        oldtime=now;
        if(!fun)
            return ;
        char disbuff[512] = "";
        float process = 0.0;

        process = min(100,(done/(total / 100.0)));
        //LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        if(axel->bytes_per_second > 1048576)//min(100,(long)(done*100./total+.5) )
            sprintf(disbuff,"[%s]  [%lld  %lld  %.2f%%] [%.1fMB/s]",filename,done,total, process,(double) axel->bytes_per_second / (1024*1024));
        else if(axel->bytes_per_second > 1024)
            sprintf(disbuff,"[%s]  [%lld  %lld  %.2f%%] [%.1fKB/s]",filename,done,total, process,(double) axel->bytes_per_second / 1024 );
        else
            sprintf(disbuff,"[%s]  [%lld  %lld  %.2f%%] [%.1fB/s]",filename,done,total, process,(double) axel->bytes_per_second);
        printf("===%s===\n",disbuff);
        memset(disbuff, '\0', sizeof(disbuff));
        sprintf(disbuff, "%.2f", process);LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        ((PGSFUN)fun)(disbuff,displaynumb);
        //LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    }
#if 0
    if(done<total)
    {
        int seconds,minutes,hours,days;
        seconds=axel->finish_time - now;
        minutes=seconds/60;
        seconds-=minutes*60;
        hours=minutes/60;
        minutes-=hours*60;
        days=hours/24;
        hours-=days*24;
        if(days)
            printf(" [%2dd%2d]",days,hours);
        else if(hours)
            printf(" [%2dh%02d]",hours,minutes);
        else
            printf(" [%02d:%02d]",minutes,seconds);
    }
#endif
    //LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    fflush( stdout );
    //LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
}


void print_version()
{
    printf( _("Axel version %s (%s)\n"), AXEL_VERSION_STRING, ARCH );
    printf( "\nCopyright 2001-2002 Wilmer van der Gaast.\n" );
}
