//
// Created by hrblaoj on 2018/3/30.
//

#ifndef SHINEKTV_ANDROID_SHINEPATH_H
#define SHINEKTV_ANDROID_SHINEPATH_H


#define  MAIN_ROOT_PATH "/sdcard/" //"/storage/sata/"   "/mnt/media_rw/sata/"
#define  MAIN_ROOT_PATH2 "/storage/sata2/"  //"/storage/sata2/"   "/mnt/media_rw/sata2/"

#define MAIN_ROOT_PATH_2CREATE "/storage/sata/"
#define MAIN_ROOT_PATH2_2CREATE "/storage/sata2/"

#define MAIN_ROOT_PATH_2ACCESS "/mnt/media_rw/sata/"
#define MAIN_ROOT_PATH2_2ACCESS "/mnt/media_rw/sata2/"

//#define  MAIN_ROOT_PATH "/mnt/sata/" //"/storage/sata/"   "/mnt/media_rw/sata/"
//#define  MAIN_ROOT_PATH2 "/mnt/sata2/"  //"/storage/sata2/"   "/mnt/media_rw/sata2/"
//
//#define MAIN_ROOT_PATH_2CREATE "/mnt/sata/"
//#define MAIN_ROOT_PATH2_2CREATE "/mnt/sata2/"
//
//#define MAIN_ROOT_PATH_2ACCESS "/mnt/media_rw/sata/"
//#define MAIN_ROOT_PATH2_2ACCESS "/mnt/media_rw/sata2/"


#ifdef __cplusplus
extern "C" {
#endif

char *strrpc(char *str,char *oldstr,char *newstr);
#ifdef __cplusplus
}
#endif


#endif //SHINEKTV_ANDROID_SHINELOG_H
