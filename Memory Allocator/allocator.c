/**
 * @file
 *
 * Explores memory management at the C runtime level.
 *
 * To use (one specific command):
 * LD_PRELOAD=$(pwd)/allocator.so command
 * ('command' will run with your allocator)
 *
 * To use (all following commands):
 * export LD_PRELOAD=$(pwd)/allocator.so
 * (Everything after this point will use your custom allocator -- be careful!)
 */

#include <pthread.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <sys/mman.h>
#include <unistd.h>
#include <stdlib.h>

#include "allocator.h"
#include "logger.h"

static struct mem_block *g_head = NULL; /*!< Start (head) of our linked list */
static struct mem_block *g_tail = NULL; /*!< End (tail) of our linked list */

static unsigned long g_allocations = 0; /*!< Allocation counter */
static unsigned long g_regions = 0; /*!< Allocation counter */

pthread_mutex_t alloc_mutex = PTHREAD_MUTEX_INITIALIZER; /*< Mutex for protecting the linked list */

/**
 * This functinon allocat block name, if no name is given, or malloc is used, set the default name to ne Allocation X(allocation id)
 * @param size size of the block
 * @param name block name
 * @return pointer to the malloc return
*/
void *malloc_name(size_t size, char *name){
	void *malloc_return = malloc(size);
	struct mem_block *block = (struct mem_block *)malloc_return  - 1;
	if(name != NULL){
		strcpy(block->name, name);
	}
	return malloc_return;
}
/**
 * Given a free block, this function will split it into two pieces and update
 * the linked list.
 *
 * @param block the block to split
 * @param size new size of the first block after the split is complete,
 * including header sizes. The size of the second block will be the original
 * block's size minus this parameter.
 *
 * @return address of the resulting second block (the original address will be
 * unchanged) or NULL if the block cannot be split.
 */
struct mem_block *split_block(struct mem_block *block, size_t size)
{
	struct mem_block *second_block;
	//second part of the splitted block; block->size:data+header
	size_t difference = block->size - size;
	size_t size_mem_block = sizeof(struct mem_block);

	
	if(difference >= size_mem_block ){
		//Address where the new block is going to start
		second_block = (struct mem_block *) ((char*) block + size);	
	//update the struct variables
		second_block->size = difference;
		second_block->free = true;
		second_block->next = block->next;
		second_block->prev = block;
		second_block->region_id = block->region_id;	
		snprintf(second_block->name, 32, "Allocation %lu", g_allocations++);

		//update the first block data	
		block->size = size;
		block->free = true;
		if (block->next != NULL) {
			block->next->prev = second_block;
		} else {
			g_tail = second_block;
		}
		block->next = second_block;
		return second_block;
	}
	 return NULL;
}

/**
 * Given a free block, this function attempts to merge it with neighboring
 * blocks --- both the previous and next neighbors --- and update the linked
 * list accordingly.
 * is it free? if not, return NULL immediately
 * @param block the block to merge
 *
 * @return address of the merged block or NULL if the block cannot be merged.
 */
struct mem_block *merge_block(struct mem_block *block)
{	
	struct mem_block *new_block = NULL;

	if(block == NULL){
	   return NULL;
	}
	//Case 1: just the previous block is free
	if(block->prev != NULL && block->prev->free && block->prev->region_id == block->region_id){
		new_block = block->prev;
		new_block->size = block->prev->size + block->size;
		new_block->next = block->next;
		
		//update head and tail
		if(new_block->next != NULL) {
			new_block->next->prev = new_block;
		} else {
			g_tail = new_block;
		}
	}
	//Case 2:just the right block is free
	if(block->next != NULL && block->next->free && block->region_id == block->next->region_id){
		new_block = block;
		new_block->size = block->next->size + block->size;
		new_block->next = block->next->next;
		
		//update tail
		if(block->next != NULL){
			new_block->next->prev = new_block;
		} else {
			g_tail = new_block;
		}
	}
      //return NULL if no blocks to be merged
      return new_block;
}

/**
 * Given a block size (header + data), locate a suitable location using the
 * first fit free space management algorithm.
 * loop thru the link list, find the first block that's free and has enough space for our request
 * @param size size of the block (header + data)
 * @return pointer to the first fit block
 */
void *first_fit(size_t size)
{
   struct mem_block *current_block = g_head;
 
   while(current_block != NULL){
	if(current_block->free == true && current_block->size >= size )	{
   		return current_block;
	}
	current_block = current_block->next;	
   }
	return NULL;
}

/**
 * Given a block size (header + data), locate a suitable location using the
 * worst fit free space management algorithm. If there are ties (i.e., you find
 * multiple worst fit candidates with the same size), use the first candidate
 * found.
 *
 * @param size size of the block (header + data)
 * @return pointer to the worst fit block
*/
void *worst_fit(size_t size)
{
   struct mem_block *current_worst_block = NULL;
   struct mem_block *current_block = g_head;
 
   while(current_block != NULL){

	if(current_block->free == true && current_block->size >= size )	{
	   
		if(current_worst_block == NULL){
			current_worst_block = current_block;
	   	}else{		   
			if(current_worst_block->size < current_block->size){
			   current_worst_block = current_block;
		   	}
		}
	 }
	current_block = current_block->next;	
   }
   return current_worst_block;
}

/**
 * Given a block size (header + data), locate a suitable location using the
 * best fit free space management algorithm. If there are ties (i.e., you find
 * multiple best fit candidates with the same size), use the first candidate
 * found.
 *
 * @param size size of the block (header + data)
 * @return pointer to the best fit block
 */
void *best_fit(size_t size)
{
   struct mem_block *current_best_block = NULL;
   struct mem_block *current_block = g_head;
  
   while(current_block != NULL){

	if(current_block->free == true && current_block->size >= size )	{
	   
		if(current_best_block == NULL){
			current_best_block = current_block;
	   	}else{
		   
		if(current_best_block->size > current_block->size){
			current_best_block = current_block;
		   }		
		}
	 }
		current_block = current_block->next;	
   }
   return current_best_block;
}

/**
  * This function is using free space management (FSM) algorithms, find a block of
  *  memory that we can reuse. Return NULL if no suitable block is found.
  *  @param size size of the block
  *  @return data region of the reuse block
*/
void *reuse(size_t size)
{
   if(g_head == NULL){
	return NULL;
   }

   char *algo = getenv("ALLOCATOR_ALGORITHM");
   if (algo == NULL) {
      algo = "first_fit";
   }

   struct mem_block *block = NULL;
   if (strcmp(algo, "first_fit") == 0) {
     block = first_fit(size); 
   } else if (strcmp(algo, "best_fit") == 0) {
     block = best_fit(size); 
     
   } else if (strcmp(algo, "worst_fit") == 0) {
     block = worst_fit(size); 
   }

   if(block == NULL){
     return NULL;
   }
   //split the block that got returned , unless it's the exact size we need
   split_block(block, size);
  
   return block;
}

/**
  * This function allocate memory. You'll first check if you can reuse an existing block. If not, map a new memory region. It gets the pag  * size, number of pages needed and update allocation request to mmap with that size instead
  * @param size size of the block to be malloc
  *  @return pointer to the malloc block data region
*/
void *malloc(size_t size)
{
   pthread_mutex_lock(&alloc_mutex);

   size_t total_size = size + sizeof(struct mem_block);
   size_t aligned_size = total_size % 8 == 8 % 8 ? total_size : (total_size / 8) * 8 + 8;
   
   LOG("Allocation request size = %zu, total size = %zu aligned = %zu\n",size,total_size,aligned_size);
  
   struct mem_block *reused_block = reuse(aligned_size);

   char *scribble_env = getenv("ALLOCATOR_SCRIBBLE");
   int scribble = 1;
   if(scribble_env != NULL){
	scribble = atoi(scribble_env);
   }

   if(reused_block != NULL){
	LOG("Reusing blocks at %p\n", reused_block);
	//NOTE: We are assuming that reuse() returns a block header, not the data region
	if(scribble == 1){
		memset(reused_block + 1,0xAA,size);//scribble the data region of the block
	}
	reused_block->free = false;
   	pthread_mutex_unlock(&alloc_mutex);
	return reused_block + 1;
   }

   int page_size = getpagesize();
   size_t num_pages = aligned_size / page_size;
   if(aligned_size % page_size != 0){
	num_pages++;
   }

   size_t region_size = num_pages * page_size;

   LOG("New region size: %zu\n", region_size);
   struct mem_block  *block = mmap(
	NULL,
	region_size,
  	PROT_READ | PROT_WRITE, 
	MAP_PRIVATE| MAP_ANONYMOUS,
	-1,
	0);

	if(block == MAP_FAILED){
	  perror("mmap");
   	  pthread_mutex_unlock(&alloc_mutex);
	  return NULL;
	}
	
	snprintf(block->name, 32, "Allocation %lu", g_allocations++);
	block->size = region_size;
	block->free = true;	
	block->region_id = g_regions++;

	if(g_head == NULL && g_tail == NULL){
	   //First block
	   //LOG("Initiializing the first block\n");
	   block->next = NULL;
	   block->prev = NULL;
	   g_head = block;
	   g_tail = block;
	}else{
	   g_tail->next = block;
	   block->prev = g_tail;
	   block->next = NULL;
	   g_tail = block;
	   
	}

	split_block(block, aligned_size);
	block->free = false;
	if(scribble == 1){
		memset(block + 1,0xAA,size);//scribble the data region of the block
	}

   	pthread_mutex_unlock(&alloc_mutex);
	LOG("New Allocation: Block is at %p; data is at %p.\n",block,block+1);

       return block + 1;
}

/**
  * This function frees memory. If the containing region is empty (i.e., there are no
  * more blocks in use), then it should be unmapped.
  * 
  *  @param ptr pointer to the block to be free 
  *
*/
void free(void *ptr)
{
    //pthread_mutex_lock(&alloc_mutex);
    LOG("Free request; address = %p\n",ptr);
    if (ptr == NULL) {
        /* Freeing a NULL pointer does nothing */
    	//pthread_mutex_unlock(&alloc_mutex);
        return;
    }
    struct mem_block *block = (struct mem_block *)ptr - 1;
    block->free = true;
    merge_block(block); // 1. merge 2. check region if true unmap else nothing.
    if ((block->prev == NULL || 
		block->prev->region_id != block->region_id) && 
		(block->next == NULL || 
		block->next->region_id != 
		block->region_id)) {
	    if (block->prev != NULL){
		block->prev->next = block->next;
	    }

	    if (block->next != NULL){
		block->next->prev = block->prev;
	    }

	    if (g_head == block){
		g_head = block->next;
	    }

	    if (g_tail == block){
		g_tail = block->prev;
	    }

	    LOG("Umapping memory region; size = %zu\n", block->size);
	    int ret = munmap(block, block->size);

	    if(ret == -1){
		perror("munmap");

	    pthread_mutex_unlock(&alloc_mutex);
	    return;
	    }
    }
    pthread_mutex_unlock(&alloc_mutex);
}

/** This function clearing the moemory region by zero out the particular memory place
 * @param nmemb block size
 * @param size target size
 * @return ptr pointer that points to where the 
*/
void *calloc(size_t nmemb, size_t size)
{
    void *ptr = malloc(nmemb * size);
    LOG("Clearing memory at %p\n",ptr);
    memset(ptr, 0, nmemb *size);
    return ptr;
}


/** This function realloc the momory region, we will move to the pointer to access the block meta data
  * check the size of the mata data against the block size
  * if the size of the meta data <= block size, return NULL,else we will move the old block content to the new block 
  * @param ptr pointer to block
  * @param size size to realloc
  * @return the new block pointer
*/
void *realloc(void *ptr, size_t size)
{
    if (ptr == NULL) {
        /* If the pointer is NULL, then we simply malloc a new block */
        return malloc(size);
    }

    if (size == 0) {
        /* Realloc to 0 is often the same as freeing the memory block... But the
         * C standard doesn't require this. We will free the block and return
         * NULL here. */
        free(ptr);
        return NULL;
    }
    struct mem_block *block = (struct mem_block *)ptr - 1;
    block->free = true;
    block = merge_block(block);
  
    // reallocation logic
   if(size <= block->size){
	block->size = size;
	split_block(block, size);
	memcpy(block + 1, ptr, size);
	block->free = false;
	return block + 1;
   }else{
	void *new_block = malloc(size);
	memcpy(new_block, ptr, block->size);
	free(ptr);
	return new_block;
   }
    return NULL;
}

/**
 * print_memory
 *
 * Prints out the current memory state, including both the regions and blocks.
 * Entries are printed in order, so there is an implied link from the topmost
 * entry to the next, and so on.
 */
void print_memory(void)
{
    puts("-- Current Memory State --");
    struct mem_block *current_block = g_head;
    struct mem_block *current_region = g_head;

    printf("[REGION] <%lu> <%p>\n", current_region->region_id, current_region);
    // implement memory printout
   while(current_block != NULL){
	//REGION:
	if(current_region->region_id != current_block->region_id){
		current_region = current_block;
		printf("[REGION] <%lu> <%p>\n",
			current_region->region_id,
			current_region);
	}
	printf("[BLOCK] <%p>-<%p> '%s' <%ld> [%s]\n",
		current_block,//start addr 
		current_block->next,//end addr
		current_block->name,
		current_block->size,
		current_block->free == true ? "FREE" : "USED");
		
	current_block = current_block->next;
   }
}

