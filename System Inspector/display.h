/**
 * @file
 *
 * Handles text-based UI functionality.
 */

#ifndef _DISPLAY_H_
#define _DISPLAY_H_

#include <stdbool.h>

void display_init(bool enable_curses);
void display_refresh(char *procfs_loc);
bool display_quit(void);
void display_stop(void);

#endif
