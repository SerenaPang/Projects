#include <fcntl.h>
#include <limits.h>
#include <math.h>
#include <ncurses.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>

#include <sys/types.h>
#include <pwd.h>

#include "logger.h"
#include "display.h"
#include "procfs.h"
#include "util.h"

#define DISP_PRINT(y, x, fmt, ...) \
    do { \
        if (use_curses) { \
            mvprintw(y, x, fmt, __VA_ARGS__); \
            clrtoeol(); \
        } else { \
            printf(fmt "\n", __VA_ARGS__); \
        } \
    } while (0) 

void display_separator(void);

static WINDOW *scr;
static int win_x;
static int win_y;
static bool use_curses = true;

void display_init(bool enable_curses)
{
    LOGP("Initializing the display...\n");

    if (enable_curses == false) {
        LOGP("Curses-based display disabled\n");
        use_curses = false;
        win_x = 80;
        win_y = 24;
        return;
    }

    LOGP("Attempting to redirect stderr to inspector.log\n");
    int fd = open("inspector.log", O_RDWR | O_CREAT | O_TRUNC, 0666);
    if (fd == -1) {
        perror("open");
    }

    int ret = dup2(fd, fileno(stderr));
    if (ret == -1) {
        perror("dup2");
    }

    scr = initscr();

    getmaxyx(scr, win_y, win_x);
    LOG("Created curses screen. Dimensions: %d x %d\n", win_x, win_y);

    /* Make functions such as getch() return immediately without waiting for
     * user input: */
    nodelay(scr, true);

    /* Do not print characters typed by the user: */
    noecho();

    /* Hide the cursor: */
    curs_set(0);
}

void display_separator(void)
{
    if (use_curses == false) {
        puts("");
    }
}

void display_refresh(char *procfs_loc)
{
    int retval = 0;

    char name[HOST_NAME_MAX + 1];
    retval = pfs_hostname(procfs_loc, name, HOST_NAME_MAX + 1);
    if (retval == -1) {
        strcpy(name, "(UNKNOWN)");
    }

    char vers[32];
    retval = pfs_kernel_version(procfs_loc, vers, 32);
    if (retval == -1) {
        strcpy(vers, "(UNKNOWN)");
    }
    DISP_PRINT(0, 0, "Hostname: %s | Kernel Version: %s", name, vers);

    char cpu_model[128];
    retval = pfs_cpu_model(procfs_loc, cpu_model, 128);
    if (retval == -1) {
        strcpy(cpu_model, "(UNKNOWN)");
    }
    int proc_units = pfs_cpu_units(procfs_loc);
    DISP_PRINT(1, 0, "CPU: %s, Processing Units: %d", cpu_model, proc_units);

    double uptime = pfs_uptime(procfs_loc);
    char uptime_str[1024];
    if (uptime == 0.0) {
        strcpy(uptime_str, "(UNKNOWN)");
    } else {
        pfs_format_uptime(uptime, uptime_str);
    }
    DISP_PRINT(2, 0, "Uptime: %s", uptime_str);

    display_separator();

    struct load_avg lavg = pfs_load_avg(procfs_loc);
    DISP_PRINT(4, 0, "Load Average (1/5/15 min): %.2f %.2f %.2f",
            lavg.one, lavg.five, lavg.fifteen);

    char cpu_bar[30] = { 0 };
    static struct cpu_stats prev_cpu;
    struct cpu_stats curr_cpu;
    double cpu_perc = pfs_cpu_usage(procfs_loc, &prev_cpu, &curr_cpu);
    prev_cpu = curr_cpu;
    draw_percbar(cpu_bar, cpu_perc);
    DISP_PRINT(5, 0, "CPU Usage:    %s", cpu_bar);

    char mem_bar[30] = { 0 };
    struct mem_stats mstats = pfs_mem_usage(procfs_loc);
    draw_percbar(mem_bar, mstats.used / mstats.total);
    DISP_PRINT(6, 0, "Memory Usage: %s (%.1f / %.1f GB)",
            mem_bar, mstats.used, mstats.total);

    display_separator();

    struct task_stats *tstats = pfs_create_tstats();
    if (tstats == NULL) {
        DISP_PRINT(8, 0, "%s", "Tasks: (UNKNOWN)");
        goto cleanup;
    }
    pfs_tasks(procfs_loc, tstats);
    DISP_PRINT(8, 0, "Tasks: %d total", tstats->total);
    DISP_PRINT(9, 0, "%d running, %d waiting, %d sleeping, %d stopped, %d zombie",
            tstats->running,
            tstats->waiting,
            tstats->sleeping,
            tstats->stopped,
            tstats->zombie);

    display_separator();

    DISP_PRINT(11, 0, "%9s | %25s | %12s | %15s", "PID", "Task Name", "State", "User");
    DISP_PRINT(12, 0, "%s",
            "----------+---------------------------+--------------+-----------------");

    for (int i = 0; i < tstats->total - tstats->sleeping; ++i) {
        char uname[16];
        uid_to_uname(uname, tstats->active_tasks[i].uid);
        DISP_PRINT(13 + i, 0, "%9d | %25s | %12s | %15s",
                tstats->active_tasks[i].pid,
                tstats->active_tasks[i].name,
                tstats->active_tasks[i].state,
                uname);
    }
    pfs_destroy_tstats(tstats);

cleanup:
    if (use_curses == true) {
        clrtobot();
        refresh();
    }
}

bool display_quit(void)
{
    int ch = getch();
    if (ch != ERR && ch != KEY_RESIZE) {
        return true;
    } else {
        return false;
    }
}

void display_stop(void)
{
    /* Restore the cursor: */
    curs_set(1);
    endwin();
}

