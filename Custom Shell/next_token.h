#ifndef _UTIL_H_
#define _UTIL_H_

#include <sys/types.h>

/*! \This function iterate thourgh the context and tokenize the string
    \param  str_ptr string to be tokenized
    \param  delim delimeter
@return tokenized string
*/
char *next_token(char **str_ptr, const char *delim);

#endif
