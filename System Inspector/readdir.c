/**
 * readdir.c
 *
 * Demonstrates reading directory contents.
 */

#include <dirent.h>
#include <stdio.h>

int main(void) {
    DIR *directory;
    if ((directory = opendir("/proc")) == NULL) {
        perror("opendir");
        return 1;
    }

    struct dirent *entry;
    while ((entry = readdir(directory)) != NULL) {

	if(is_only_numeric(entry->d_name) == true){
	/*who owns the process dir, if it's owned by the same user
	running inspector, then we should print out the name of it*/
		char current[128];
		strcpy(current, "./");
		strcat(current, entry->d_name);

	}
        printf("-> %s\n", entry->d_name);
    }

    closedir(directory);

    return 0;
}

