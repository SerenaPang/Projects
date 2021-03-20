#include <math.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>

#include "util.h"
#include "logger.h"
/**
* Opens a particular path under the proc file system and returns a file desciption
* to the opened file.
* @param proc_dir location of the proc file system
* @param path subdirectory under procfs to open
*
* @return file descriptor of the opened path or -1 on failure
*/

int open_path(char *proc_dir, char *path){
	if(proc_dir == NULL || path == NULL){
		errno = EINVAL;
		return -1;
	}

	size_t str_size = strlen(proc_dir) + strlen(path) + 2;
	char *full_path = malloc(str_size);
	if(full_path == NULL){
		return -1;
	}
	snprintf(full_path, str_size, "%s/%s",proc_dir, path);

	LOG("Opening path: %s\n",full_path);
	int fd = open(full_path, O_RDONLY);
	free(full_path);
	return fd;
}
/**
* Opens a particular path under the proc file system and returns a file desciption
* to the opened file, and read one line from that file.
* @param fd location of the proc file system
* @param buf file buffer
* @param sz size of the file
* @return size of the file
*/
ssize_t one_lineread(int fd, char *buf, size_t sz){
	ssize_t read_sz = lineread(fd, buf, sz);
	if(read_sz <= 0){
		return read_sz;
	}

	buf[read_sz-1] = '\0';
	return read_sz;
}

/**
* Opens a particular path under the proc file system and returns a file desciption
* to the opened file, and read one line from that file.
* @param fd location of the proc file system
* @param buf file buffer
* @param sz size of the file
* @return size of the file
*/
ssize_t lineread(int fd, char *buf, size_t sz)
{
    	size_t count = 0;
    	while(count < sz){
        	char c;
        	ssize_t read_sz = read(fd, &c,1);
    	if(read_sz  <= 0){
        	return read_sz;
                	    }
   	 else{
        	buf[count] = c;
        	count += read_sz;       
       		 if (c == '\n'){
       			 return count;
                      		}
        	 }
                      }
   	 return count;
}
/**
 * Retrieves the next token from a string.
 *
 * @param str_ptr: maintains context in the string,i.e., where the next token in the
 *   string will be. If the function returns token N, then str_ptr will be
 *   updated to point to token N+1. To initialize, declare a char * that points
 *   to the string being tokenized. The pointer will be updated after each
 *   successive call to next_token.
 *
 * @param  delim: the set of characters to use as delimiters
 *
 * @returns: char pointer to the next token in the string.
 */
char *next_token(char **str_ptr, const char *delim)
{
    if (*str_ptr == NULL) {
        return NULL;
    }

    size_t tok_start = strspn(*str_ptr, delim);
    size_t tok_end = strcspn(*str_ptr + tok_start, delim);

    /* Zero length token. We must be finished. */
    if (tok_end  == 0) {
        *str_ptr = NULL;
        return NULL;
    }

    /* Take note of the start of the current token. We'll return it later. */
    char *current_ptr = *str_ptr + tok_start;

    /* Shift pointer forward (to the end of the current token) */
    *str_ptr += tok_start + tok_end;

    if (**str_ptr == '\0') {
        /* If the end of the current token is also the end of the string, we
         * must be at the last token. */
        *str_ptr = NULL;
    } else {
        /* Replace the matching delimiter with a NUL character to terminate the
         * token string. */
        **str_ptr = '\0';

        /* Shift forward one character over the newly-placed NUL so that
         * next_pointer now points at the first character of the next token. */
        (*str_ptr)++;
    }

    return current_ptr;
}

/**This function draws percentage display bar 
  *@param buf:: display bar buf
  *@param frac:: usage in double
  */
void draw_percbar(char *buf, double frac) {
	double not_rounded = frac*100;
	frac = round(frac*100);
	int hashes = frac /5;
	 buf[0] = '[';
	 buf[21] = ']';
	 buf[22] = ' ';
	 buf[29] = '\0';


	if(not_rounded <= 0 || isnan(frac)){
		buf[23] = '0';
		buf[24] = '.';
		buf[25] = '0';
		buf[26] = '%';
		buf[27] = '\0';
		
		}
	else if(not_rounded >= 100.0 ||isinf(frac)){
		buf[23] = '1';
		buf[24] = '0';
		buf[25] = '0';
		buf[26] = '.';
		buf[27] = '0';
		buf[28] = '%';
		
	} else {
		char perc_buf[15];
		sprintf(perc_buf, "%.1f%c",not_rounded, '%');

		int len = strlen(perc_buf);
		int j = 23;
		int index;
		for(index = 0; index < len; index++){		
			buf[j] = perc_buf[index];
			j++;
		}
		buf[j] = '\0';
	}

	int i;

	if(isinf(frac)&& frac>0){
		
		for(i = 1;i<21;i++){
			buf[i] = '#';
		}
	}else{
	
		
	for(i = 1 ; i < 21; i++){

	if(i <= hashes){
		 buf[i]= '#';
	 }
	 else{
		 buf[i] ='-';
	 }

		      }
				}
}

/*
* This function parse the /etc/passwd
* @param name_buf name buffer
* @param uid user id
*/
void uid_to_uname(char *name_buf, uid_t uid){

	int fd = open("/etc/passwd", O_RDONLY);
	if(fd == -1){
		close(fd);
		return;
	}
	 char line[1000];
	printf("reading passwd \n");
         while ((lineread(fd, line, 256)) > 0) {       
                char *next_tok = line;
                char *curr_tok = NULL; 
		
		curr_tok = next_token(&next_tok,":");
		printf("uname: %s \n", curr_tok);
		char u_name[16] = {0};
		strncpy(u_name, curr_tok, 16);
		next_token(&next_tok, ":");// x
		curr_tok = next_token(&next_tok, ":");// curr_tok = uid
		
	    }//end of reading lines
	close(fd);
}
