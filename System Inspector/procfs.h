/**
 * @file
 *
 * Retrieves raw information from procfs, parses it, and provides easy-to-use
 * functions for displaying the data.
 */

#ifndef _PROCFS_H_
#define _PROCFS_H_

#include <sys/types.h>
/*! \struct load_avg
 * \brief struct to store info
 *
 * var load_avg:: one first data
 * var load_avg:: five second data 
 * var load_avg:: fifteen third data
 */
struct load_avg {
    double one; ///< first data
    double five; ///< five second data
    double fifteen; ///< fifteen third data
};

/*! \struct cpu_stats
 * @brief struct to store info
 *
 * var idle time
 * var total time 
 */
struct cpu_stats {
    long idle; ///< idle time
    long total; ///< total time
};

/*! \struct mem_stats
 *  \brief struct to store info
 *
 * var used memory
 * var total memory
 */
struct mem_stats {
    double used; ///< used mem
    double total; ///< total mem
};

/*! \struct task_stats
 * \brief struct to store info
 *
 * var total total number of tasks
 * var running state
 * var waiting state
 * var sleeping state
 * var stopped state
 * var zombie state
 * var task_info strct store task info
 */
struct task_stats {
    unsigned int total; ///< total num of task
    unsigned int running; ///< state
    unsigned int waiting; ///< state
    unsigned int sleeping; ///< state
    unsigned int stopped; ///< state
    unsigned int zombie; ///< state

    struct task_info *active_tasks; ///< task info struct
};

/*! \struct task_info
 *  \brief struct to store info
 *
 * var task_info::pid program id
 * var task_info::uid user id 
 * var task_info::name  user name
 * var task_info::state program state
 */
struct task_info {
    pid_t pid; ///< program id
    uid_t uid; ///< user id
    char name[26]; ///< user name
    char state[13]; ///< program state
};

/*! \int pfs_hostname(char *proc_dir, char *hostname_buf, size_t buf_sz);
This function reads the hostname from file description
    \param  proc_dir directory to read from
    \param hostname_buf hostname buffer 
    \param  buf_sz size of the file
@return 0 if get the hostname successfully 
*/
int pfs_hostname(char *proc_dir, char *hostname_buf, size_t buf_sz);

/*! \int pfs_kernel_version(char *proc_dir, char *version_buf, size_t buf_sz);
This function reads the kernel version from file description
	\param proc_dir directory to read from
	\param version_buf version buffer 
	\param buf_sz size of the file
@return 0 if success
*/
int pfs_kernel_version(char *proc_dir, char *version_buf, size_t buf_sz);

/*! \int pfs_cpu_model(char *proc_dir, char *model_buf, size_t buf_sz);
This function reads the cpumodel from file description
	\param proc_dir directory to read from
	\param model_buf hostname buffer 
	\param buf_sz size of the file
@return 0 if success
*/
int pfs_cpu_model(char *proc_dir, char *model_buf, size_t buf_sz);

/*! \int pfs_cpu_units(char *proc_dir);
This function reads the cpuinfo from file description 
	\param proc_dir directory to read from
 	\param hostname_buf hostname buffer 
	\ param buf_sz size of the file
@return proccsor units
*/
int pfs_cpu_units(char *proc_dir);

/*! \double pfs_uptime(char *proc_dir);
This function retrive the uptime from the proc description
	\param proc_dir directory to read from
@return uptime in doble eg: uptime = 148135.87
*/
double pfs_uptime(char *proc_dir);

/*! \int pfs_format_uptime(double time, char *uptime_buf);
This function print formatted uptime
	\param time uptime
	\param uptime_buf uptime buffer
@return 0 if uptime is written to the buffer eg: 17days, 5 hours, 38 minutes, 55 seconds
*/
int pfs_format_uptime(double time, char *uptime_buf);

/*! \
This function reads the load average info from file description
	\param proc_dir directory to read from
*/
struct load_avg pfs_load_avg(char *proc_dir);
/* Note: 'prev' is an input that represents the *last* CPU usage state. 'curr'
 * is an *output* that represents the current CPU usage state, and will be
 * passed back in to pfs_load_avg on its next call. */
/*! \
This function reads the stat from file description and figure out the current amount of time spent idle, and the current "total" time that CPU was runnig. Compare the current stats with the previous stats
that were passed in, so that we can get the cpu usage.
	\param procfs_dir directory to read from
	\param prev previous struct 
	\param curr curretn struct
@return cpu usage in double
*/
double pfs_cpu_usage( char *procfs_dir, struct cpu_stats *prev, struct cpu_stats *curr);
/*! \
This function reads the memory info from file description and calculate memory usage
\param proc_dir directory to read from
@return struc contains used and total mem stats
*/
struct mem_stats pfs_mem_usage(char *procfs_dir);

/* Note: these two functions create and destroy task_stats structs. Depending on
 * your implementation, this might just be a malloc/free. */

/*! \This function allocate memory space for task_stats strct
@return struc contains used and total mem stats
*/
struct task_stats *pfs_create_tstats();

/*! \This function frees the memory of task_stats struct
	\param tstats
*/

void pfs_destroy_tstats(struct task_stats *tstats);
/*! \
This function reads the task info from file description and calculate task usage
	\param proc_dir directory to read from
	\param tstats task struct
@return 0 if suceess
*/
int pfs_tasks(char *proc_dir, struct task_stats *tstats);

#endif
