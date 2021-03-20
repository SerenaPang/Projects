# Project 3: Memory Allocator







See: https://www.cs.usfca.edu/~mmalensek/cs326/assignments/project-3.html 

Introduction to Memory Allocator

This Memory Allocator implement malloc, calloc, realloc, and free functions. It used the free space management algorithms (First fit, Best fit,Worst fit) to split up and reuse empty regions. After freeing a block, it checked the neighboring blocks to determine whether it can merge with them or not. When allocating the memory, it split the blocks that are not 100% used. 

Structure of the Memory Allocator
 
The implementation only use mmap and allocate entire regions of memory at a time. The linked list keeps track of the block allocations.The mem_block struct has the information for the region and the block.

Build/Compile the memory allocator

make

Use the memory allocator:

LD_PRELOAD=$(pwd)/allocator.so ls /


To compile and use the allocator:

```bash
make
LD_PRELOAD=$(pwd)/allocator.so ls /
```

(in this example, the command `ls /` is run with the custom memory allocator instead of the default).

## Testing

To execute the test cases, use `make test`. To pull in updated test cases, run `make testupdate`. You can also run a specific test case instead of all of them:

```
# Run all test cases:
make test

# Run a specific test case:
make test run=4

# Run a few specific test cases (4, 8, and 12 in this case):
make test run='4 8 12'
```
