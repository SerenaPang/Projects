/**
 * @file
 *
 * Contains shell history data structures and retrieval functions.
 */

#ifndef _HISTORY_H_
#define _HISTORY_H_

/*! \This function initialize the history data struct
    \param int the cap of the history list
*/
void hist_init(unsigned int);
/*! \This function frees the history data structe
*/
void hist_destroy(void);
/*! \This function add the command to history list
    \param *command to be added 
*/
void hist_add(const char *);
/*! \This function print out the last 100 history commands
*/
void hist_print(void);
/*! \This function to search command start with prefix
    \param * prefix 
  @return recent command start with the prefix
*/
const char *hist_search_prefix(char *);
/*! \This function search for the command according to the cmd number
    \param  int  command number
@return the commad according to the cmd id
*/
const char *hist_search_cnum(int);
/*! \This function search for the last command number
@return the last command number
*/
unsigned int hist_last_cnum(void);

#endif
