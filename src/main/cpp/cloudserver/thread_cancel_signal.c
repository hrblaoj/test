#include "thread_cancel_signal.h"
#include "ShineLog.h"


void handle(int signal, siginfo_t *siginfo, void *u_contxt)
{
    LOGD("\thandle exec for kill\n");
    pthread_exit(NULL);
    return;
}

int InitCancelSignal(){

}