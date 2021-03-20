#include <string.h>
#include <stdio.h>
#include "allocator.h"


int main(void){
	int *a = malloc(45);
	print_memory();
	char *b = malloc(500);
	void *c = malloc(72);
	void *d = malloc(16);

	a[0] = 45;
	//strcpy(b,"hello there");
	printf("the string is: %s\n",b);	

	free(c);
	free(d);
	free(a);
	free(b);

	print_memory();

	return 0;

}
