#ifndef SHINEKTV_ANDROID_THREAD_CANCEL_SIGNAL_H
#define SHINEKTV_ANDROID_THREAD_CANCEL_SIGNAL_H


#include <limits.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>
#include <stdarg.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <netinet/in_systm.h>
#include <netinet/in.h>
#include <netinet/ip.h>
#include <arpa/inet.h>
#include <net/if.h>
#include <pthread.h>

#ifdef __cplusplus

extern "C" {
#endif

void handle(int signal, siginfo_t *siginfo, void *u_contxt);
int InitCancelSignal();

#ifdef __cplusplus

}
#endif


#endif