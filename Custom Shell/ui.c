#include <stdio.h>
#include <readline/readline.h>
#include <locale.h>
#include <unistd.h>
#include <stdbool.h>
#include <stdlib.h>
#include <sys/types.h>
#include <pwd.h>
#include <linux/limits.h>
#include <string.h>
#include <stdbool.h>
#include <signal.h>

#include "history.h"
#include "logger.h"
#include "ui.h"

static int readline_init(void);
static char prompt_str[1024] = "--[enter a command]--> ";

static bool scripting = false;

static char *line = NULL;
static size_t line_sz = 0;

static int last_status = 0;

int home_dir_sz;
int curr_dir_sz;
char user[BUFSIZ];
char host[BUFSIZ];
char curr_dir[PATH_MAX];
char home_dir[PATH_MAX];

int init = 1;
int command_num = 1;

/*This function set the status of the process
*/
void set_last_status(int status){

	last_status = status;
}

/*
This function initialize the user interfcae of the shell
*/
void init_ui(void)
{
    LOGP("Initializing UI...\n");

    char *locale = setlocale(LC_ALL, "en_US.UTF-8");
    LOG("Setting locale: %s\n",
            (locale != NULL) ? locale : "could not set locale!");
	 if (isatty(STDIN_FILENO)) {
		/* Interactive mode; turn on the prompt */
		LOGP("stdin is a TTY; entering interactive mode\n");
		scripting = false;   
	 } else {
		LOGP("data piped in on stdin; entering script mode\n");
		scripting = true;   
	 }
	
	line = malloc(sizeof(char)*1024);
	line_sz = 1024;

   	rl_startup_hook = readline_init;
}

/*
This function replace the old word with new word
@param s word to be replaced
@param oldW targeted word 
@ param newW new word that join to the old word
@return 0 if get the hostname successfully 
*/
char* replaceWord(const char* s, const char* oldW, 
                  const char* newW) 
{ 
    char* result; 
    int i, cnt = 0; 
    int newWlen = strlen(newW); 
    int oldWlen = strlen(oldW); 
  
    for (i = 0; s[i] != '\0'; i++) { 
        if (strstr(&s[i], oldW) == &s[i]) { 
            cnt++; 
  
            i += oldWlen - 1; 
        } 
    } 
 
    result = (char*)malloc(i + cnt * (newWlen - oldWlen) + 1); 
  
    i = 0; 
    while (*s) {
        if (strstr(s, oldW) == s) { 
            strcpy(&result[i], newW); 
            i += newWlen; 
            s += oldWlen; 
        } 
        else
            result[i++] = *s++; 
    } 
  
    result[i] = '\0'; 
    return result; 
} 

/*
This function gets the current directory of the process
*/
void get_home(void){
	struct passwd * pwu = getpwuid(getuid());
	strcpy(home_dir, pwu->pw_dir);
	home_dir_sz = strlen(home_dir);
	if (init) {
		init = 0;
		return;
	} 

	getcwd(curr_dir, BUFSIZ);
	curr_dir_sz = strlen(curr_dir);
}

/*
This function check if the string starts with a particular prefix
@param pre prefix
@param str string to be checked
return a boolean value if string starts with the rrefix or not
*/
bool start_with(const char *pre, const char *str)
{
    return strncmp(pre, str, strlen(pre)) == 0;
}

/*
This function display the shell prompt with command number, user name and host name, the current worling directory and process exit status
@return the information mentioned above
*/
char *prompt_line(void) {

	char *dir;
	get_home();

	strcpy(user, getlogin());
	gethostname(host, BUFSIZ);
	getcwd(curr_dir, PATH_MAX);
	curr_dir_sz = strlen(curr_dir);
	char *ret;

	   ret = strstr(curr_dir, home_dir);

	if(ret == NULL || !start_with(home_dir, curr_dir)){
		dir = curr_dir;
	   }else{
		char* result = NULL;   
		result = replaceWord(curr_dir, home_dir, "~"); 
		dir = result;  
	   }
	  
	sprintf(prompt_str, "[%s]-[ 💙🧡💗 %d]- [%s@%s:%s]$", last_status == 0 ? " 🥰 " : " 😳 ",command_num++, user, host, dir);
    
    fflush(stdout);
    return prompt_str;
}

/*
This function reads the user input command and return it
@return command
*/
char *read_command(void)
{
    if(scripting == true){
	ssize_t read_sz = getline(&line, &line_sz, stdin);//get line
	if(read_sz == -1){
		perror("getline");
		free(line);
		return NULL;
	}
	line[read_sz-1] = '\0';
	return line;
    }else{
    	return readline(prompt_line());
    }
}

/*This function intialize the realine funcitons
@return 0 if sucess
*/
int readline_init(void)
{
    rl_bind_keyseq("\\e[A", key_up);
    rl_bind_keyseq("\\e[B", key_down);
    rl_variable_bind("show-all-if-ambiguous", "on");
    rl_variable_bind("colored-completion-prefix", "on");
    rl_attempted_completion_function = command_completion;
    return 0;
}

/* This function shows the previous history entries
@param count times user press up key
@param key up arrow key
@return 0 if sucess
*/
int key_up(int count, int key)
{
	char *command = strdup(rl_line_buffer);//free it later
	LOG("reverse searching for %s\n",command);
    /* Modify the command entry text: */
    rl_replace_line("User pressed 'up' key", 1);

    /* Move the cursor to the end of the line: */
    rl_point = rl_end;

    // TODO: reverse history search

    return 0;
}
/* This function shows the history entries
@param count times user press down key
@param key down arrow key
@return 0 if sucess
*/

int key_down(int count, int key)
{
    /* Modify the command entry text: */
    rl_replace_line("User pressed 'down' key", 1);

    /* Move the cursor to the end of the line: */
    rl_point = rl_end;

    // TODO: forward history search

    return 0;
}

/*
This function autocomplete the user command
@param text to be aoto complete
@param start starting position of the text 
@ param end ending position of the text
@return the completed command 
*/
char **command_completion(const char *text, int start, int end)
{
    /* Tell readline that if we don't find a suitable completion, it should fall
     * back on its built-in filename completion. */
    rl_attempted_completion_over = 0;

    return rl_completion_matches(text, command_generator);
}

/**
 * This function is called repeatedly by the readline library to build a list of
 * possible completions. It returns one match per function call. Once there are
 * no more completions available, it returns NULL.

@param text command
@state indicator of if there is any data structure needs to be initialized
@return possible completions, NULL if not avaliable
 */
char *command_generator(const char *text, int state)
{
    // TODO: find potential matching completions for 'text.' If you need to
    // initialize any data structures, state will be set to '0' the first time
    // this function is called. You will likely need to maintain static/global
    // variables to track where you are in the search so that you don't start
    // over from the beginning.

    return NULL;
}
