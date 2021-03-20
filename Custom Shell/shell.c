#include <fcntl.h>
#include <pwd.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/param.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <sys/types.h>
#include <signal.h>

#include "history.h"
#include "logger.h"
#include "ui.h"
#include "next_token.h"


/*
This function make sure control c does not terminate the shell
@param signo sinal indicator
*/
void sigint_handler(int signo){
	if(isatty(STDIN_FILENO)) {
		printf("\n");
		printf(prompt_line());
	}
        fflush(stdout);
	
}

/**! \struct builtin_def
   \brief struct to store built in info
   
    var builtin_def:: name
    var function
*/
struct builtin_def{
	char name[128];
	int (*function)(int argc, char *args[]);
};

int exit_shell(int argc, char *args[]);
int change_dir(int argc, char *args[]);
int handle_builtins(int argc, char *args[]);
int history_list(int argc, char *args[]);


/**! \struct builtin_def

    \brief struct to store all built in info
*/
struct builtin_def builtins[] = {
	{ "exit",exit_shell },
	{ "bye",exit_shell },
	{ "cd", change_dir },
	{ "history", history_list },
	//{ "jobs", job_list },
//	{ "!", his_execution },
};


/**
built in function to exit shell gracefully
@param argc number of args
@param array of args
*/
int exit_shell(int argc, char *args[]){
	//exit, bye
	LOGP("Bye!👋😉 Have a nice day!🤙😉\n");
	exit(EXIT_SUCCESS); //or a shutdownshell to clar out all memoy allocated
	return 0;
}

/**
built in function to change directory
@param argc number of args
@param array of args
*/

int change_dir(int argc, char *args[]){
	//cd
	int ret;
	char home_dir[PATH_MAX];
	struct passwd * pwu = getpwuid(getuid());
	strcpy(home_dir, pwu->pw_dir);
	
	if(args[1] == NULL){
		//to current dir
		ret = chdir(home_dir);
	}else{
		ret = chdir(args[1]);
	}

	set_last_status(ret);

	if(ret == -1){
		perror("chdir");
	}
	return 0;
}

/**
built in function to prints out last 100 commands
@param argc number of args
@param array of args
*/

int history_list(int argc, char *args[]){
//	printf("adding %s to history.", args[0]);
	hist_print();	
	return 0;
}


/**
 function to handle all built in commands
@param argc number of args
@param array of args
*/
int handle_builtins(int argc, char *args[]){

	for(int i = 0; i < sizeof(builtins) / sizeof(struct builtin_def);++i){
		if(strcmp(args[0], builtins[i].name) == 0){	
			return builtins[i].function(argc, args);
		}
	}
	return -1;
}

/**
function to execute commmand
@param size number of args
@param array of args
*/
void execute_cmd(int size, char *args[]){
	
	int status = 0;
	hist_init(100);

    	init_ui();
    	char *command;

	    while (true) {
		command = read_command();
		if (command == NULL) {
		    break;
		}
        LOG("Input command: %s\n", command);
		
		int tokens = 0;
		char *next_tok = command;
		char *curr_tok;
		
		

		while((curr_tok = next_token(&next_tok, " \t\n\r")) != NULL){
		
			if(strstr(curr_tok,"#")!=NULL){
				break;
			}
			args[tokens++] = curr_tok;
		}
		args[tokens] = NULL;

		if(args[0] == NULL){
			continue;
		}
		
		int builtin_status = handle_builtins(tokens, args);
		if(builtin_status == 0){
		   continue;
		}	

		pid_t child = fork();

		if(child == -1){
			perror("fork");
		}
		else if(child == 0){
		       int ret = execvp(args[0], args);
			//add cmd to history
		       if(ret == -1){
				perror("execvp");
			}
			close(fileno(stdin));
			close(fileno(stdout));
			close(fileno(stderr));
			exit(EXIT_FAILURE);
		}
		else{
			//int status;
			waitpid(child, &status,0);
//			LOG("child exited.\n");
			set_last_status(status);
		}
		
	}
}

/**
driver function to initilize shell and execute commands
*/
int main(void)
{
	
	int size = 4096;
	char *args[size];
	signal(SIGINT, sigint_handler);
	execute_cmd(size, args);
    	
	return 0;
}
