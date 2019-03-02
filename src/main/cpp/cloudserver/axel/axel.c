/********************************************************************\
* Axel -- A lighter download accelerator for Linux and other Unices. *
*                                                                    *
* Copyright 2001 Wilmer van der Gaast                                *
\********************************************************************/

/* Main control								*/

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
#include "conn.h"
#include "../DownloadService.h"

/* Axel */
static void save_state( axel_t *axel );
static void *setup_thread( void * );
static void axel_message( axel_t *axel, char *format, ... );
static void axel_divide( axel_t *axel );

//static char *buffer = NULL;

/* Create a new axel_t structure					*/
axel_t *axel_new( conf_t *conf, int count, void *url )
{LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    search_t *res;
    axel_t *axel;
    url_t *u;
    char *s;
    int i;
    axel = malloc( sizeof( axel_t ) );
    memset( axel, 0, sizeof( axel_t ) );
    *axel->conf = *conf;
    axel->conn = malloc( sizeof( conn_t ) * axel->conf->num_connections );
    memset( axel->conn, 0, sizeof( conn_t ) * axel->conf->num_connections );
    if( axel->conf->max_speed > 0 )
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        if( (float) axel->conf->max_speed / axel->conf->buffer_size < 0.5 )
        {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
            if( axel->conf->verbose >= 2 )
                axel_message( axel, _("Buffer resized for this speed.") );
            axel->conf->buffer_size = axel->conf->max_speed;
        }
        axel->delay_time = (int) ( (float) 1000000 / axel->conf->max_speed * axel->conf->buffer_size * axel->conf->num_connections );
    }LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if( axel->buffer == NULL )
        axel->buffer = malloc( max( MAX_STRING, axel->conf->buffer_size ) );
    if( count == 0 )
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        axel->url = malloc( sizeof( url_t ) );
        axel->url->next = axel->url;
        strncpy( axel->url->text, (char *) url, MAX_STRING );
    }
    else
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        res = (search_t *) url;
        u = axel->url = malloc( sizeof( url_t ) );
        for( i = 0; i < count; i ++ )
        {
            strncpy( u->text, res[i].url, MAX_STRING );
            if( i < count - 1 )
            {
                u->next = malloc( sizeof( url_t ) );
                u = u->next;
            }
            else
            {
                u->next = axel->url;
            }
        }
    }LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    axel->conn[0].conf = axel->conf;
    if( !conn_set( &axel->conn[0], axel->url->text ) )
    {LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
        axel_message( axel, _("Could not parse URL.\n") );
        axel->ready = -1;

        printf("==================%s %d: return\n", __FILE__, __LINE__);
        return( axel );
    }
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if(axel->conf->interfaces != NULL)		//chenlin
    {
        axel->conn[0].local_if = axel->conf->interfaces->text;
        axel->conf->interfaces = axel->conf->interfaces->next;
    }
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);

    strncpy( axel->filename, axel->conn[0].file, MAX_STRING );
    http_decode( axel->filename );
    //printf("==============%s %s %d: axel->filename = %s\n", __FILE__, __func__, __LINE__, axel->filename);
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if( *axel->filename == 0 )	/* Index page == no fn		*/
        strncpy( axel->filename, axel->conf->default_filename, MAX_STRING );
    if( ( s = strchr( axel->filename, '?' ) ) != NULL && axel->conf->strip_cgi_parameters )
        *s = 0;		/* Get rid of CGI parameters		*/
    //printf("==============%s %s %d: axel->filename = %s\n", __FILE__, __func__, __LINE__, axel->filename);
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if( !conn_init( &axel->conn[0] ) )
    {
        axel_message( axel, axel->conn[0].message );
        printf("==================%s %d: return\n", __FILE__, __LINE__);
        axel->ready = -1;
        return( axel );
    }
    /* This does more than just checking the file size, it all depends
       on the protocol used.					*/
    LOGD("dfdf startserver %s, %d, %s",__FILE__,__LINE__,__FUNCTION__);
    if( !conn_info( &axel->conn[0] ) )
    {
        axel_message( axel, axel->conn[0].message );
        axel->ready = -1;

        printf("==================%s %d: return\n", __FILE__, __LINE__);
        return( axel );
    }
    printf("=========%s %d: Fsize = %lld\n", __FILE__, __LINE__, axel->conn[0].Fsize);
    s = conn_url( axel->conn );
    strncpy( axel->url->text, s, MAX_STRING );
    //printf("============%s %d: axel->url->text = %s\n", __FILE__, __LINE__, axel->url->text);
    axel->Fsize = axel->conn[0].Fsize;
    if( ( axel->size = axel->conn[0].size ) != INT_MAX )
    {
        if( axel->conf->verbose > 0 )
            axel_message( axel, _("File size: %lld bytes"), axel->size );
    }
    /* Wildcards in URL --> Get complete filename			*/
    if( strchr( axel->filename, '*' ) || strchr( axel->filename, '?' ) )
        strncpy( axel->filename, axel->conn[0].file, MAX_STRING );
    return( axel );
}

/* Open a local file to store the downloaded data			*/
int axel_open( axel_t *axel )
{
    int i, fd;
    long long int j;

    if( axel->conf->verbose > 0 )
        axel_message( axel, _("Opening output file %s"), axel->filename );
    snprintf(  axel->buffer, MAX_STRING, "%s.st", axel->filename );

    axel->outfd = -1;

    /* Check whether server knows about RESTart and switch back to
       single connection download if necessary			*/
    if( !axel->conn[0].supported )
    {
        axel_message( axel, _("Server unsupported, "
                              "starting from scratch with one connection.") );
        axel->conf->num_connections = 1;
        axel->conn = realloc( axel->conn, sizeof( conn_t ) );
        axel_divide( axel );
    }
    else if( ( fd = open(  axel->buffer, O_RDONLY ) ) != -1 )
    {
        read( fd, &axel->conf->num_connections, sizeof( axel->conf->num_connections ) );

        axel->conn = realloc( axel->conn, sizeof( conn_t ) * axel->conf->num_connections );
        memset( axel->conn + 1, 0, sizeof( conn_t ) * ( axel->conf->num_connections - 1 ) );

        axel_divide( axel );

        read( fd, &axel->bytes_done, sizeof( axel->bytes_done ) );
        for( i = 0; i < axel->conf->num_connections; i ++ )
            read( fd, &axel->conn[i].currentbyte, sizeof( axel->conn[i].currentbyte ) );

        axel_message( axel, _("State file found: %lld bytes downloaded, %lld to go."),
                      axel->bytes_done, axel->size - axel->bytes_done );

        close( fd );

        if( ( axel->outfd = open( axel->filename, O_WRONLY, 0666 ) ) == -1 )
        {
            axel_message( axel, _("Error opening local file") );
            printf("====================%s %d: Error opening local file\n", __FILE__, __LINE__);
            return( 0 );
        }
    }

    /* If outfd == -1 we have to start from scrath now		*/
    if( axel->outfd == -1 )
    {
        axel_divide( axel );

        if( ( axel->outfd = open( axel->filename, O_CREAT | O_WRONLY, 0666 ) ) == -1 )
        {
            axel_message( axel, _("Error opening local file") );
            perror("====================open:");
            printf("====================%s %d: Error opening local file\n", __FILE__, __LINE__);
            return( 0 );
        }

        /* And check whether the filesystem can handle seeks to
           past-EOF areas.. Speeds things up. :) AFAIK this
           should just not happen:				*/
        if( lseek( axel->outfd, axel->size, SEEK_SET ) == -1 && axel->conf->num_connections > 1 )
        {
            /* But if the OS/fs does not allow to seek behind
               EOF, we have to fill the file with zeroes before
               starting. Slow..				*/
            axel_message( axel, _("Crappy filesystem/OS.. Working around. :-(") );
            lseek( axel->outfd, 0, SEEK_SET );
            memset(  axel->buffer, 0, axel->conf->buffer_size );
            j = axel->size;
            while( j > 0 )
            {
                write( axel->outfd,  axel->buffer, min( j, axel->conf->buffer_size ) );
                j -= axel->conf->buffer_size;
            }
        }
    }

    return( 1 );
}

/* Start downloading							*/
void axel_start( axel_t *axel )
{
    int i;

    /* HTTP might've redirected and FTP handles wildcards, so
       re-scan the URL for every conn				*/
    printf("===============%s %d: num_connections = %d\n", __FILE__, __LINE__, axel->conf->num_connections);
    for( i = 0; i < axel->conf->num_connections; i ++ )
    {
        conn_set( &axel->conn[i], axel->url->text );
        axel->url = axel->url->next;
        if(axel->conf->interfaces != NULL)		//chenlin
        {
            axel->conn[i].local_if = axel->conf->interfaces->text;
            axel->conf->interfaces = axel->conf->interfaces->next;
        }

        axel->conn[i].conf = axel->conf;
        if( i ) axel->conn[i].supported = 1;
    }

    if( axel->conf->verbose > 0 )
        axel_message( axel, _("Starting download") );

    for( i = 0; i < axel->conf->num_connections; i ++ )
    {
        if( axel->conn[i].currentbyte <= axel->conn[i].lastbyte )
        {
            if( axel->conf->verbose >= 2 )
            {
                axel_message( axel, _("Connection %i downloading from %s:%i using interface %s"),
                              i, axel->conn[i].host, axel->conn[i].port, axel->conn[i].local_if );
            }
            axel->conn[i].state = 1;
            if( pthread_create( &axel->conn[i].setup_thread, NULL, setup_thread, &axel->conn[i] ) != 0 )
            {
                axel_message( axel, _("pthread error!!!") );
                axel->conn[i].setup_thread = 0;		//chenlin
                axel->ready = -1;

            }
            else
            {
                pthread_detach(axel->conn[i].setup_thread);//chenlin
                printf("==================%s %d: pthread_create setup_thread = %ld\n", __FILE__, __LINE__, axel->conn[i].setup_thread);
                usleep(500000);		//chenlin
                axel->conn[i].last_transfer = gettime();
            }
        }
    }

    /* The real downloading will start now, so let's start counting	*/
    axel->start_time = gettime();
    axel->ready = 0;
}

/* Main 'loop'								*/
int axel_do( axel_t *axel )
{
    int ret = 0;
    fd_set fds[1];
    int hifd, i;
    long long int remaining,size;
    struct timeval timeval[1];

    /* Create statefile if necessary				*/
    if( gettime() > axel->next_state )
    {
        save_state( axel );
        axel->next_state = gettime() + axel->conf->save_state_interval;
    }

    /* Wait for data on (one of) the connections			*/
    FD_ZERO( fds );
    hifd = 0;
    for( i = 0; i < axel->conf->num_connections; i ++ )
    {
        if( axel->conn[i].enabled )
            FD_SET( axel->conn[i].fd, fds );
        hifd = max( hifd, axel->conn[i].fd );
    }
    if( hifd == 0 )
    {
        /* No connections yet. Wait...				*/
        usleep( 100000 );
        goto conn_check;
    }
    else
    {
        timeval->tv_sec = 0;
        timeval->tv_usec = 100000;
        /* A select() error probably means it was interrupted
           by a signal, or that something else's very wrong...	*/
        if( select( hifd + 1, fds, NULL, NULL, timeval ) == -1 )
        {
            axel->ready = -1;

            printf("=================%s %d: select error\n", __FILE__, __LINE__);
            return -1;
        }
    }

    /* Handle connections which need attention			*/
    for( i = 0; i < axel->conf->num_connections; i ++ )
    {
        if( axel->conn[i].enabled )
        {
            if( FD_ISSET( axel->conn[i].fd, fds ) )
            {
                axel->conn[i].last_transfer = gettime();
                size = read( axel->conn[i].fd,  axel->buffer, axel->conf->buffer_size );
                if( size == -1 )
                {
                    if( axel->conf->verbose )
                    {
                        axel_message( axel, _("Error on connection %i! "
                                              "Connection closed"), i );
                    }
                    axel->conn[i].enabled = 0;
                    conn_disconnect( &axel->conn[i] );
                    continue;
                }
                else if( size == 0 )
                {
                    if( axel->conf->verbose )
                    {
                        /* Only abnormal behaviour if:		*/
                        if( axel->conn[i].currentbyte < axel->conn[i].lastbyte && axel->size != INT_MAX )
                        {
                            axel_message( axel, _("Connection %i unexpectedly closed"), i );
                        }
                        else
                        {
                            axel_message( axel, _("Connection %i finished"), i );
                        }
                    }
                    if( !axel->conn[0].supported )
                    {
                        printf("=====================%s %d: conn[0].supported = 0, axel->ready = 1\n", __FILE__, __LINE__);
                        axel->ready = 1;
                    }
                    axel->conn[i].enabled = 0;
                    conn_disconnect( &axel->conn[i] );
                    continue;
                }
                /* remaining == Bytes to go					*/
                remaining = axel->conn[i].lastbyte - axel->conn[i].currentbyte + 1;
                if( remaining < size )
                {
                    if( axel->conf->verbose )
                    {
                        axel_message( axel, _("Connection %i finished"), i );
                    }
                    axel->conn[i].enabled = 0;
                    conn_disconnect( &axel->conn[i] );
                    size = remaining;
                    /* Don't terminate, still stuff to write!	*/
                }
                /* This should always succeed..				*/
                off_t lseeksize=lseek( axel->outfd, axel->conn[i].currentbyte, SEEK_SET );
                if(lseeksize < axel->Fsize)
                {
                    long long int Wsize=size;
                    if((lseeksize+size) > axel->Fsize)
                    {
                        Wsize=axel->Fsize-lseeksize;
                    }
                    if(Wsize>0)
                    {
                        if( write( axel->outfd,  axel->buffer, Wsize ) != Wsize )
                        {
                            axel_message( axel, _("Write error!") );
                            axel->ready = -1;

                            printf("=================%s %d: Write error!\n", __FILE__, __LINE__);
                            return -1;
                        }
                    }

                }
                else
                {
                    printf("=================%s %d:�ļ�����%lld       %lld   \n", __FILE__, __LINE__, lseeksize,axel->Fsize);
                    axel->ready = -1;		//chenlin
                    return -1;
                }
                axel->conn[i].currentbyte += size;
                axel->bytes_done += size;
            }
            else
            {
                if( gettime() > axel->conn[i].last_transfer + axel->conf->connection_timeout )
                {
                    if( axel->conf->verbose )
                        axel_message( axel, _("Connection %i timed out"), i );
                    conn_disconnect( &axel->conn[i] );
                    axel->conn[i].enabled = 0;
                }
            }
        }
    }
    if( axel->ready )
    {
        printf("=================%s %d: axel->ready = %d\n", __FILE__, __LINE__, axel->ready);
        return -1;
    }

conn_check:
    /* Look for aborted connections and attempt to restart them.	*/
    for( i = 0; i < axel->conf->num_connections; i ++ )
    {
        if( !axel->conn[i].enabled && axel->conn[i].currentbyte < axel->conn[i].lastbyte )
        {
            if( axel->conn[i].state == 0 )
            {
                // Wait for termination of this thread
                //pthread_join(axel->conn[i].setup_thread, NULL);
				ret = 1;

                conn_set( &axel->conn[i], axel->url->text );
                axel->url = axel->url->next;
                /* axel->conn[i].local_if = axel->conf->interfaces->text;
                axel->conf->interfaces = axel->conf->interfaces->next; */
                if( axel->conf->verbose >= 2 )
                    axel_message( axel, _("Connection %i downloading from %s:%i using interface %s"),
                                  i, axel->conn[i].host, axel->conn[i].port, axel->conn[i].local_if );                

            }
            else
            {
                if( gettime() > axel->conn[i].last_transfer + axel->conf->reconnect_delay )
                {
					axel->conn[i].state = 0;
                    ret = 1;
					
                    printf("=================%s %d: test1\n", __FILE__, __LINE__);
                    //printf("==================%s %d: pthread_cancel setup_thread = %d\n", __FILE__, __LINE__, axel->conn[i].setup_thread);                    
                    if(axel->conn[i].setup_thread != 0)
                    {						
						printf("=================%s %d: test2\n", __FILE__, __LINE__);
                        //pthread_cancel(axel->conn[i].setup_thread );
                        int result = pthread_kill(axel->conn[i].setup_thread, SIGUSR2); LOGD("dfdf startserver pt_kl rlt %d %s, %d, %s",result,__FILE__,__LINE__,__FUNCTION__);
                        axel->conn[i].setup_thread = 0;
                    }
                    
                    printf("=================%s %d: test3\n", __FILE__, __LINE__);                   
                }
            }
        }
    }

    /* Calculate current average speed and finish_time		*/
    axel->bytes_per_second = (int) ( (double) ( axel->bytes_done - axel->start_byte ) / ( gettime() - axel->start_time ) );
    axel->finish_time = (int) ( axel->start_time + (double) ( axel->size - axel->start_byte ) / axel->bytes_per_second );

    /* Check speed. If too high, delay for some time to slow things
       down a bit. I think a 5% deviation should be acceptable.	*/
    if( axel->conf->max_speed > 0 )
    {
        if( (float) axel->bytes_per_second / axel->conf->max_speed > 1.05 )
            axel->delay_time += 10000;
        else if( ( (float) axel->bytes_per_second / axel->conf->max_speed < 0.95 ) && ( axel->delay_time >= 10000 ) )
            axel->delay_time -= 10000;
        else if( ( (float) axel->bytes_per_second / axel->conf->max_speed < 0.95 ) )
            axel->delay_time = 0;
        usleep( axel->delay_time );
    }

    /* Ready?							*/
    if( axel->bytes_done == axel->size )
    {
        printf("=====================%s %d: axel->bytes_done = %lld, axel->ready = 1\n", __FILE__, __LINE__, axel->bytes_done);
        axel->ready = 1;
    }

    //printf("=================%s %d: axel->ready = %d\n", __FILE__, __LINE__, axel->ready);
    if(ret == 1)
    {
        return -2;
    }
    else
    {
        return 0;
    }

}

/* Close an axel connection						*/
void axel_close( axel_t *axel )
{
    int i;
    message_t *m;

    /* Terminate any thread still running				*/
    for( i = 0; i < axel->conf->num_connections; i ++ )
        /* don't try to kill non existing thread */
        if ( axel->conn[i].setup_thread != 0 )
        {
            //pthread_cancel( axel->conn[i].setup_thread );
            int result = pthread_kill(axel->conn[i].setup_thread, SIGUSR2);LOGD("dfdf startserver pt_kl rlt %d %s, %d, %s",result,__FILE__,__LINE__,__FUNCTION__);
            axel->conn[i].setup_thread = 0;
        }

    /* Delete state file if necessary				*/
    if( axel->ready == 1 )
    {
        snprintf(  axel->buffer, MAX_STRING, "%s.st", axel->filename );
        unlink(  axel->buffer );
    }
    /* Else: Create it.. 						*/
    else if( axel->bytes_done > 0 )
    {
        save_state( axel );
    }

    /* Delete any message not processed yet				*/
    while( axel->message )
    {
        m = axel->message;
        axel->message = axel->message->next;
        free( m );
    }

    /* Close all connections and local file				*/
    if(axel->outfd > 0)		//chenlin
    {
        close( axel->outfd );
    }

    for( i = 0; i < axel->conf->num_connections; i ++ )
        conn_disconnect( &axel->conn[i] );

    if(axel->buffer)//chenlin
    {
        free( axel->buffer);
    }
    if(axel->conn)//chenlin
    {
        free( axel->conn );
    }

    free( axel );
}

/* time() with more precision						*/
double gettime()
{
    struct timeval time[1];

    gettimeofday( time, 0 );
    return( (double) time->tv_sec + (double) time->tv_usec / 1000000 );
}

/* Save the state of the current download				*/
void save_state( axel_t *axel )
{
    int fd, i;
    char fn[MAX_STRING+4];

    /* No use for such a file if the server doesn't support
       resuming anyway..						*/
    if( !axel->conn[0].supported )
        return;

    snprintf( fn, MAX_STRING, "%s.st", axel->filename );
    if( ( fd = open( fn, O_CREAT | O_TRUNC | O_WRONLY, 0666 ) ) == -1 )
    {
        return;		/* Not 100% fatal..			*/
    }
    write( fd, &axel->conf->num_connections, sizeof( axel->conf->num_connections ) );
    write( fd, &axel->bytes_done, sizeof( axel->bytes_done ) );
    for( i = 0; i < axel->conf->num_connections; i ++ )
    {
        write( fd, &axel->conn[i].currentbyte, sizeof( axel->conn[i].currentbyte ) );
    }
    close( fd );
}

/* Thread used to set up a connection					*/
void *setup_thread( void *c )
{
    struct sigaction sigact;
    sigact.sa_sigaction = CusturmHandleCloseThread;
    sigact.sa_flags = SA_SIGINFO;
    sigemptyset(&sigact.sa_mask);
    sigaction(SIGUSR2, &sigact, NULL);

    conn_t *conn = c;
    int oldstate;
	int ret = 0;

    pthread_detach(pthread_self());
	
    /* Allow this thread to be killed at any time.			*/
    //pthread_setcancelstate( 0, &oldstate );
    //pthread_setcanceltype( 1, &oldstate );

    if( conn_setup( conn ) )
    {
        conn->last_transfer = gettime();
		ret = conn_exec( conn );
        if( ret )
        {			
			printf("============%s %d: ret = %d\n", __FILE__, __LINE__, ret);
            conn->last_transfer = gettime();
            conn->enabled = 1;
            conn->state = 0;
//            pthread_detach(pthread_self());
			conn->setup_thread = 0;

            printf("==================%s %d: exit pthread_self = %ld\n", __FILE__, __LINE__, pthread_self());
            return( NULL );
        }
    }
	
    conn_disconnect( conn );
    conn->state = 0;
	conn->setup_thread = 0;


    printf("==================%s %d: exit pthread_self = %ld\n", __FILE__, __LINE__, pthread_self());

    return( NULL );
}

/* Add a message to the axel->message structure				*/
static void axel_message( axel_t *axel, char *format, ... )
{
    return ;

    message_t *m = malloc( sizeof( message_t ) ), *n = axel->message;
    va_list params;

    memset( m, 0, sizeof( message_t ) );
    va_start( params, format );
    vsnprintf( m->text, MAX_STRING, format, params );
    va_end( params );

    if( axel->message == NULL )
    {
        axel->message = m;
    }
    else
    {
        while( n->next != NULL )
            n = n->next;
        n->next = m;
    }
}

/* Divide the file and set the locations for each connection		*/
static void axel_divide( axel_t *axel )
{
    int i;

    axel->conn[0].currentbyte = 0;
    axel->conn[0].lastbyte = axel->size / axel->conf->num_connections - 1;
    for( i = 1; i < axel->conf->num_connections; i ++ )
    {
#ifdef DEBUG
        printf( "Downloading %lld-%lld using conn. %i\n", axel->conn[i-1].currentbyte, axel->conn[i-1].lastbyte, i - 1 );
#endif
        axel->conn[i].currentbyte = axel->conn[i-1].lastbyte + 1;
        axel->conn[i].lastbyte = axel->conn[i].currentbyte + axel->size / axel->conf->num_connections;
    }
    axel->conn[axel->conf->num_connections-1].lastbyte = axel->size - 1;
#ifdef DEBUG
    printf( "Downloading %lld-%lld using conn. %i\n", axel->conn[i-1].currentbyte, axel->conn[i-1].lastbyte, i - 1 );
#endif
}
