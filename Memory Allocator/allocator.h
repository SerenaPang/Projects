/**
 * @file
 *
 * Function prototypes and globals for our memory allocator implementation.
 */

#ifndef ALLOCATOR_H
#define ALLOCATOR_H

#include <stddef.h>
#include <stdbool.h>

/* -- Helper functions -- */
struct mem_block *split_block(struct mem_block *block, size_t size);
struct mem_block *merge_block(struct mem_block *block);
void *reuse(size_t size);
void *first_fit(size_t size);
void *worst_fit(size_t size);
void *best_fit(size_t size);
void print_memory(void);

/* -- C Memory API functions -- */
void *malloc(size_t size);
void free(void *ptr);
void *calloc(size_t nmemb, size_t size);
void *realloc(void *ptr, size_t size);

/* -- Data Structures -- */

/**
 * Defines metadata structure for both memory 'regions' and 'blocks.' This
 * structure is prefixed before each allocation's data area.
 */
struct mem_block {
    /**
     * The name of this memory block. If the user doesn't specify a name for the
     * block, it should be auto-generated based on the allocation ID. The format
     * should be 'Allocation X' where X is the allocation ID.
     */
    char name[32];

    /** Size of the block */
    size_t size;

    /** Whether or not this block is free */
    bool free;

    /**
     * The region this block belongs to.
     */
    unsigned long region_id;

    /** Next block in the chain */
    struct mem_block *next;

    /** Previous block in the chain */
    struct mem_block *prev;

    /**
     * "Padding" to make the total size of this struct 100 bytes. This serves no
     * purpose other than to make memory address calculations easier. If you
     * add members to the struct, you should adjust the padding to compensate
     * and keep the total size at 100 bytes; test cases and tooling will assume
     * a 100-byte header.
     */
    char padding[35];
} __attribute__((packed));

#endif
