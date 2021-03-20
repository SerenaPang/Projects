#ifndef _UTIL_H_
#define _UTIL_H_

#include <sys/types.h>
/*! \This function draws percentage bars
    \param buf cpu usage buffer 
    \param frac memory usage in double
@return 0 if get the hostname successfully 
*/
void draw_percbar(char *buf, double frac);
/*! \This function map user id to user name
    \param name_buf username buffer 
    \param  uid size user id
*/
void uid_to_uname(char *name_buf, uid_t uid);

/*! \This function read all the path to open files
    \param  proc_dir directory to read from
    \param path path to open file 
@return 0 if get the hostname successfully 
*/
int open_path(char *proc_dir, char *path);


/*! \This function read the lines of the file
    \param  fd directory to read from
    \param  buf file buffer 
    \param  sz size of the file
@return size of the file
*/	
ssize_t lineread(int fd, char *buf, size_t sz);

/*! \This function read only one line of the file
    \param  fd directory to read from
    \param  hostname_buf file buffer 
    \param  sz size of the file
@return size of the file
*/	
ssize_t one_lineread(int fd, char *hostname_buf, size_t sz);


/*! \This function iterate thourgh the context and tokenize the string
    \param  str_ptr string to be tokenized
    \param  delim delimeter
@return tokenized string
*/	
char *next_token(char **str_ptr, const char *delim);

#endif
