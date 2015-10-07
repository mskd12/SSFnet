
SHELL = /bin/sh


TOPDIR = ../../../../../..
include $(TOPDIR)/Makefile.common

JAVAC = javac -classpath $(SSFNET_TEST_CLASSPATH)
JAVA  = java  -classpath $(SSFNET_TEST_CLASSPATH)
SCHEMAS = $(TOPDIR)/examples/net.dml
DICTIONARY = ../dictionary.dml


TESTNAME = drop-peer
RUNTIME = 500

# ----- MAKE ALL --------------------------------------------------------------

all:
	@rm -f test-raw.out test.out
	$(JAVA) SSF.Net.Net $(RUNTIME) $(TESTNAME).dml $(DICTIONARY) $(SCHEMAS) > test-raw.out 2>&1
	@grep "^~#" test-raw.out > test.out
	@echo '---------------- $(TESTNAME): completed (see test.out and test-raw.out)'
	@echo
	@if [ -z "`diff $(TESTNAME).out test.out`" ]; \
	then \
	  echo ' **** TEST SUCCESSFUL ****   ($(TESTNAME).out & test.out identical)'; \
	else \
	  echo ' **** TEST FAILED ****   ($(TESTNAME).out & test.out differ)'; \
	fi
	@echo

# ----- MAKE VALID ------------------------------------------------------------

# basically the same as 'make all' but much less verbose

valid:
	@rm -f test-raw.out test.out
	@$(JAVA) SSF.Net.Net $(RUNTIME) $(TESTNAME).dml $(DICTIONARY) $(SCHEMAS) > test-raw.out 2>&1
	@grep "^~#" test-raw.out > test.out
	@if [ -z "`diff $(TESTNAME).out test.out`" ]; \
	then \
	  echo ' - $(TESTNAME) test successful'; \
	else \
	  echo ' **** $(TESTNAME) test failed'; \
	  exit 1; \
	fi

# ----- MAKE CLEAN ------------------------------------------------------------

clean:
	rm -f test-raw.out test.out

