# Project: System Inspector

See: https://www.cs.usfca.edu/~mmalensek/cs326/assignments/project-1.html 

About This Project:

This is a Unix system inspector that inspect the system it runs on and print a user frindly report that's similiar to top command.The informatin it's getting is from the process information pseudo-filesystem(proc).

Program Options:

$ ./inspector -h
Usage: ./inspector [-ahlrst] [-p procfs_dir]


Options:
    * -a              Display all (equivalent to -lrst, default)
    * -h              Help/usage information
    * -l              Task List
    * -p procfs_dir   Change the expected procfs mount point (default: /proc)
    * -r              Hardware Information
    * -s              System Information
    * -t              Task Information

To compile and run:

```bash
make
./inspector
```

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








