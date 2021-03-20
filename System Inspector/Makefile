# Output binary name
bin=inspector

# Set the following to '0' to disable log messages:
LOGGER ?= 1

# Compiler/linker flags
CFLAGS += -g -Wall -fPIC -DLOGGER=$(LOGGER)
LDLIBS += -lm -lncurses
LDFLAGS +=

# Source C files
src=inspector.c procfs.c display.c util.c
obj=$(src:.c=.o)

# Makefile recipes --
all: $(bin) libinspector.so

$(bin): $(obj)
	$(CC) $(CFLAGS) $(LDLIBS) $(LDFLAGS) $(obj) -o $@

libinspector.so: $(obj)
	$(CC) $(CFLAGS) $(LDLIBS) $(LDFLAGS) $(obj) -shared -o $@

docs: Doxyfile
	doxygen

clean:
	rm -f $(bin) $(obj) libinspector.so
	rm -rf docs

# Individual dependencies --
inspector.o: inspector.c logger.h
procfs.o: procfs.c procfs.h logger.h
display.o: display.c display.h procfs.h util.h logger.h
util.o: util.c util.h logger.h


# Tests --
test: $(bin) libinspector.so ./tests/run_tests
	@DEBUG="$(debug)" ./tests/run_tests $(run)

testupdate: testclean test

./tests/run_tests:
	rm -rf tests
	git clone https://github.com/USF-OS/Inspector-Tests.git tests

testclean:
	rm -rf tests
