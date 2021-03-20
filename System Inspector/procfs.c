#include "logger.h"
#include "procfs.h"
#include "util.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdbool.h>
#include <math.h>
#include <dirent.h>
#include <pwd.h>
#include <errno.h>
#include <fcntl.h>

/*
This function reads the hostname from file description
@param proc_dir directory to read from
@param hostname_buf hostname buffer 
@ param buf_sz size of the file
@return 0 if get the hostname successfully 
*/
int pfs_hostname(char *proc_dir, char *hostname_buf, size_t buf_sz)
{
	int fd = open_path(proc_dir, "sys/kernel/hostname");
	if(fd <= 0){
		perror("open_path");
		return -1;
	}
	one_lineread(fd, hostname_buf, buf_sz);
	
	int ret = close(fd);
	if(ret == -1){
		perror("close");
	}
    	return 0;
}

/*
This function reads the kernel version from file description
@param proc_dir directory to read from
@param version_buf version buffer 
@ param buf_sz size of the file
@return 0 if success
*/
int pfs_kernel_version(char *proc_dir, char *version_buf, size_t buf_sz)
{	
	int fd = open_path(proc_dir, "sys/kernel/osrelease");
	if(fd <= 0){
		perror("open_path");
		return -1;
	}
	one_lineread(fd, version_buf, buf_sz);
	size_t dash_pos = strcspn(version_buf,"-");
	version_buf[dash_pos] = '\0';
	close(fd);
	return 0;
}

/*
This function reads the cpumodel from file description
@param proc_dir directory to read from
@param model_buf hostname buffer 
@ param buf_sz size of the file
@return 0 if success
*/
int pfs_cpu_model(char *proc_dir, char *model_buf, size_t buf_sz)
{
	int fd = open_path(proc_dir, "cpuinfo");
	if(fd <= 0){
		perror("open_path");
		return -1;
	}

	size_t line_sz = 0;
	char line[256];
	while((line_sz = lineread(fd, line, 256)) > 0){
	  char *next_tok = line;
	  char *curr_tok;
	  while ((curr_tok = next_token(&next_tok, "\t:")) != NULL) {
		if(strcmp(curr_tok,"model name")== 0){
		   curr_tok = next_token(&next_tok, "\n\t:");
		  strncpy(model_buf, curr_tok + 1,128);
		}
	    }
	}
	close(fd);
	return 0;
}

/*
This function reads the cpuinfo from file description 
@param proc_dir directory to read from
@param hostname_buf hostname buffer 
@ param buf_sz size of the file
@return proccsor units
*/
int pfs_cpu_units(char *proc_dir)
{
	int fd = open_path(proc_dir, "cpuinfo");
	if(fd <= 0){
		perror("open_path");
		return -1;
	}
	
	int cpu_unit = 0;
	size_t line_sz = 0;
	char line[256];
	while((line_sz = lineread(fd, line, 256)) > 0){
	  char *next_tok = line;
	  char *curr_tok;
	
	  while ((curr_tok = next_token(&next_tok, "\t:")) != NULL) {
		if(strcmp(curr_tok,"processor")== 0){
		   curr_tok = next_token(&next_tok, "\n\t:");
		   cpu_unit++;
		}
	    }
	}

	close(fd);
    	return cpu_unit;
}


/*
This function retrive the uptime from the proc description
@param proc_dir directory to read from
@return uptime in doble eg: uptime = 148135.87
*/

double pfs_uptime(char *proc_dir)
{
	int fd = open_path(proc_dir, "uptime");
	if(fd <= 0){
		perror("open_path");
		return -1;
	}

        ssize_t buf_size = 128;
	char uptime_buf[buf_size];
	char buffer[buf_size];
	char *tmp;
	double uptime;

	ssize_t line_sz = lineread(fd, uptime_buf, buf_size);
	if(line_sz != 0){
		strcpy(buffer, uptime_buf);
	}
	tmp = buffer;

	uptime = atof(next_token(&tmp, " "));
	close(fd);
	return uptime;
}

/*
This function print formatted uptime
@param time uptime
@param uptime_buf uptime buffer
@return 0 if uptime is written to the buffer eg: 17days, 5 hours, 38 minutes, 55 seconds
*/
int pfs_format_uptime(double time, char *uptime_buf)
{
	int day, hour, min, sec;

	sec = (int) time;
	min = (sec / 60) % 60;
	hour = (sec / 3600) % 24;
	day = (sec / 86400) % 365;
	sec = sec % 60;

	sprintf(uptime_buf, "%d days, %d hours, %d minutes, %d seconds",day, hour, min, sec); 

    	return 0;
}


/*
This function reads the load average info from file description
@param proc_dir directory to read from
@return load average
*/
struct load_avg pfs_load_avg(char *proc_dir)
{
	int fd = open_path(proc_dir, "loadavg");
	if(fd <= 0){
		perror("open_path");
	}
        ssize_t buf_size = 128;
	char loadavg_buf[buf_size];
   	struct load_avg lavg = { 0 };
	
	ssize_t line_sz = lineread(fd, loadavg_buf, buf_size);
	if(line_sz != 0){

		char *token = loadavg_buf;
		char *t;

		t = next_token(&token, " ");
		lavg.one = atof(t);		
		t = next_token(&token, " ");
		lavg.five = atof(t);		
		t = next_token(&token, " ");
		lavg.fifteen = atof(t);
	}

	close(fd);
   	return lavg;
}

/*
This function reads the stat from file description and figure out the current amount of time spent idle, and the current "total" time that CPU was runnig. Compare the current stats with the previous stats
that were passed in, so that we can get the cpu usage.
@param proc_dir directory to read from
@param prev previous struct 
@ param curr curretn struct
@return cpu usage in double
*/
double pfs_cpu_usage(char *proc_dir, struct cpu_stats *prev, struct cpu_stats *curr)
{
	double cpu_usage = 0.0;
	
	int fd = open_path(proc_dir, "stat");
	if(fd <= 0){
		perror("open_path");
		return -1;
	}

        ssize_t buf_size = 256;
	char cpu_buf[buf_size];

	sleep(1);
	printf("pre cpu: %ld\n", prev->total);
	printf("curr cpu: %ld\n", curr->total);
	ssize_t line_sz = lineread(fd, cpu_buf, buf_size);
	if(line_sz != 0){
		double total_u = 0.0;
		double total_1 = 0.0;
		double idle_u = 0.0;
		double idle_1;

		char *token = cpu_buf;
		next_token(&token, " ");//CPU USAGE

		int i;
		for(i = 0; i < 9; i++){
		   char *number = next_token(&token, " ");
		   total_1 = total_1 + atof(number);
		   if(i == 3){
			idle_1 = atof(number);
			curr->idle = idle_1;
			}
					}

			char total[256];
			sprintf(total,"%f",total_1);
			curr->total = total_1;
			total_u = curr->total - prev->total;
			//compare the curr with prev that were passed in
			if(total_u == 0.0 || prev->total > curr->total){
				return 0.0;			
			}
			close(fd);
			
			idle_u = curr->idle - prev->idle;
			cpu_usage = 1 - (idle_u / total_u);	
			   }
    	return cpu_usage;
}

/**This function reads the memory info from file description and calculate memory usage
@param proc_dir directory to read from
@return struc contains used and total mem stats
*/
struct mem_stats pfs_mem_usage(char *proc_dir)
{
	

	int fd = open_path(proc_dir, "meminfo");
	if(fd <= 0){
		perror("open_path");
	}
       
        struct mem_stats mstats = { 0 };
	size_t line_sz = 0;
	char line[256];
	double mem_used = 0;
	double mem_total = 0;
	double mem_ava = 0;	

	while((line_sz = lineread(fd, line, 256)) > 0){
	  char *next_tok = line;
	  char *curr_tok;
	
	  while ((curr_tok = next_token(&next_tok, "\t:")) != NULL) {
		if(strcmp(curr_tok,"MemTotal")== 0){
		   curr_tok = next_token(&next_tok, "\n\t:");
		   mem_total = atof(curr_tok);
		}
			
		if(strcmp(curr_tok,"MemAvailable")== 0){
		   curr_tok = next_token(&next_tok, "\n\t:");	
		   mem_ava = atof(curr_tok);

		}
	 	   	}
				}			

		   mem_total = mem_total/(1024*1024);
		   mem_ava = mem_ava/(1024*1024);
		   mem_used = mem_total - mem_ava;

		   mstats.total = mem_total;//Populate strcut
		   mstats.used = mem_used;

	close(fd);

    	return mstats;
}
/**
This function check if the input str is a numeric value
@param str string to be checked
*/
int is_numeric(const char *str){
        int i,sz,is_num; 
        sz = strlen(str);

        if(sz == 0){
                is_num = 0;
        }
        for( i = 0; i < sz; i++){
                if(isdigit(str[i]) == 0){
                        is_num = 0;
                }else{
                        is_num = 1;
                }
        }
        return is_num;
}



/*This function allocate memory for the task struct
@param task_stats task struct 
@return tsak struct
*/
struct task_stats *pfs_create_tstats()
{
   struct task_stats *my_stats = (struct task_stats *)calloc(1,sizeof(struct task_stats)); 
   my_stats->active_tasks = (struct task_info *)calloc(1,sizeof(struct task_info)); 
   return my_stats;
}

/*This function frees the task struct
@param task_stats task struct 
*/
void pfs_destroy_tstats(struct task_stats *tstats)
{
	free(tstats->active_tasks);
	free(tstats);
}

/*
This function reads the taskinfo from file description and analyze it
@param proc_dir directory to read from
@param task_stats task statestics
@return number of tasks 
*/

int pfs_tasks(char *proc_dir, struct task_stats *tstats){

	DIR *directory = opendir(proc_dir);
	struct dirent *entry;
	if (directory == NULL) {
		perror("opendir");
		return -1;
	}
	tstats->total = 0;
	tstats->running = 0;
	tstats->waiting = 0;
	tstats->sleeping = 0;
	tstats->stopped = 0;
	tstats->zombie = 0;
	int pros = 0;

	while ((entry = readdir(directory)) != NULL) {
	    if (entry->d_type == DT_DIR && isdigit(*(entry->d_name))) {
		printf("Process:  %s \n",entry->d_name);

		char curr[256];
		strcpy(curr,proc_dir);// /proc
		strcat(curr,"/");// /proc/
		strcat(curr, entry->d_name);// /proc/1
		strcat(curr,"/status");// /proc/1/status
	
		int pid_num = atoi(entry->d_name);		
		tstats->active_tasks[pros].pid = pid_num;
	
		int fd = open(curr, O_RDONLY);
		if(fd == -1){
		  return -1;
		}

		char line[1000];
            while ((lineread(fd, line, 256)) > 0) {	  
		 char *next_tok = line;
		 char *curr_tok;
		 while ((curr_tok = next_token(&next_tok, "\t:")) != NULL) {
			if(strcmp(curr_tok, "Name") == 0){
			  curr_tok = next_token(&next_tok,"\n\t");
		          printf("Name:  %s \n", curr_tok);
			  strcpy(tstats->active_tasks[pros].name, curr_tok);
			  tstats->active_tasks[pros].name[25] = '\0';
			}//end of reading name
			else if(strcmp(curr_tok, "Uid") == 0){
			  curr_tok = next_token(&next_tok,"\t ");
			  printf("Uid: %s \n", curr_tok);
			  tstats->active_tasks[pros].uid = atoi(curr_tok);
			}//end of reading uid

		 	else if(strcmp(curr_tok,"State")== 0){
			  curr_tok = next_token(&next_tok, " \n\t:");
				printf("State: %s \n", curr_tok);
		
				switch (curr_tok[0]) {
				case 'R':
				    tstats->running = tstats->running + 1;
				    strcpy(tstats->active_tasks[pros].state,"running");
				    pros++;
				    tstats->total = tstats->total+1;
				    break;
				case 'S':
				    tstats->sleeping = tstats->sleeping + 1;
				    tstats->total = tstats->total+1;
				    break;
				case 'D':
				    tstats->waiting = tstats->waiting + 1;
				    strcpy(tstats->active_tasks[pros].state,"disk sleep");
				    pros++;
				    tstats->total = tstats->total+1;
				    break;
				case 'Z':
				    tstats->zombie = tstats->zombie + 1;
				    strcpy(tstats->active_tasks[pros].state,"zombie");
				    pros++;
				    tstats->total = tstats->total+1;
				    break;
				case 'T':
				    tstats->stopped = tstats->stopped + 1;
				    strcpy(tstats->active_tasks[pros].state,"stopped");
				    pros++;
				    tstats->total = tstats->total+1;
				    break;
				case 't':
				    tstats->stopped = tstats->stopped + 1;
				    strcpy(tstats->active_tasks[pros].state,"tracing stop");
				    pros++;
				    tstats->total = tstats->total+1;
				    break;
				case 'I':
				    tstats->sleeping = tstats->sleeping + 1;
				    tstats->total = tstats->total+1;
				    break;
				}//end of switch cases
		
	    		}//end of reading state
		}//end of tokenize
	   }//end of reading lines
	  close(fd);
	 }//end of process name is not a number
        	printf("\n");
	  }//end of reading dir

	printf("Total: %d \n", tstats->total);
	closedir(directory);
	return 0;
}







