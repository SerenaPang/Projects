#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "history.h"
#define MAX 100

int total = 0; //tracking the number of total cmds, it could go over the capacity of the array
int id_counter = 0;//track the total number of the element in the array, can not ecxceed the size of the array

/*! \struct history_entry
 *  \brief struct to store info
 *
 * var history_entry::cmd_id
 * var history_entry::cmd_line  command
 */
struct history_entry
{
    char cmd_line[128]; ///<command line
    int cmd_id; ///<command id
    
};

/*! \struct history entry struct
*/
struct history_entry *hist_list;


/*This function allocate memory for the history struct
@param limit the cap of the history list
*/
void hist_init(unsigned int limit)
{

	hist_list = (struct history_entry*)    malloc(limit * sizeof(struct history_entry));
}

/**
This function frees the history struct
*/
void hist_destroy(void)
{
	free(hist_list);
}

/*
This function insert the element to the end of the array when the number of element in the array reach max
@param num_cmds number of total commands
@param new_list history list
*/
void insertAtEnd(int num_cmds, struct history_entry *new_list) {
    
    for (int i = 1; i < num_cmds; i++) {
        hist_list[i - 1] = hist_list[i];

    }

    hist_list[num_cmds - 1] = *new_list;
 
}

/**
Function to skip white space
@param chracter to be checked
@return status of if the charater is a white space or not
*/
int is_white_space(char c) {
    return (c == ' ' || c == '\t' || c == '\n');
}

/*
** function to get the length of a string
@param string to be checked
@return length of the string
*/
int get_str_len(char const *str) {
    int len = 0;
    while (str[len] != '\0') {
        len += 1;
    }
    return (len);
}

/*
** Iterate through the string, if it's a blank cmd, return 0
@param str string to be evaluate
@return 0 if string is blank, 1 otherwise
*/
int is_blank_cmd(char const *str) {

    int i;
    int len = get_str_len(str);
    int num_white_space = 0; 
    int is_blank;

   for(i = 0; i < len; i++){
        if(is_white_space(str[i])){
            num_white_space++;
            printf("%c\n", str[i]);
        }
         //printf("%c\n", str[i]);
    }

    if(num_white_space == len){
        is_blank = 0;
    }
    else{
        is_blank = 1;
    }

    return (is_blank);
}

/**This function adds command to the history array
@param cmd command to be add
*/
void hist_add(const char *cmd)
{
 struct history_entry * a_cmd = &hist_list[id_counter];

    if(is_blank_cmd(cmd) == 0){
            printf("it's a blank command");
    }else{
   
    a_cmd->cmd_id = total++; //populatin the list
    strcpy(a_cmd->cmd_line, cmd);//populatin the list
      if (id_counter == MAX) {
        id_counter--;
        
        insertAtEnd(total, a_cmd);
      }

    if (id_counter < MAX) {

        id_counter++;
    }

    //update the cmd id
     for(int i = 0; i < id_counter; i++){

            a_cmd->cmd_id = i;
        }

    }
}

/* This function should print history entries */
void hist_print(void)
{
    int i;
    for (i = 0; i < id_counter; i++) {
        //print stuff
        printf("%d %s \n",hist_list[i].cmd_id, hist_list[i].cmd_line);
    }
}


/**This function retrieves the most recent command starting with 'prefix', or NULL
 if no match found.
@param prefix prefix to search for 
@return the command start with particular prefix
 */
const char *hist_search_prefix(char *prefix)
{
    char *rencent_match;
    char *ret;
    int i;

    for (i = id_counter; i > 0; i--) {
         ret = strstr(hist_list[i].cmd_line, prefix);//not null if prefix exist

         if(ret == NULL){//no prefix in this cmd
                rencent_match = NULL;
         }else{
                rencent_match = hist_list[i].cmd_line;
                break;            
         }
    }
    return rencent_match;
}

/**This function retrieves a particular command number,return NULL if no match found.
@param command_number cmd number to be saerch for
@return command of that command number 
*/
const char *hist_search_cnum(int command_number)
{
    char *rencent_match;
    int i;
    for (i = id_counter; i > 0; i--) {
         if(command_number != hist_list[i].cmd_id){//no prefix in this cmd
                rencent_match = NULL;
         }else{
                rencent_match = hist_list[i].cmd_line;
                break;            
         }
    }
    return rencent_match;
}

/**This function retrieves the most recent command number.
@return last command number*/
unsigned int hist_last_cnum(void)
{
    printf("last_cmd: [%s]\n",  hist_list[id_counter-1].cmd_line);
    return id_counter-1;
}
