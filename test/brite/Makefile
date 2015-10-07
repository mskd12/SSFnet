
SHELL = /bin/sh


TOPDIR = ../../../../../..
include $(TOPDIR)/Makefile.common

JAVAC = javac -classpath $(SSFNET_TEST_CLASSPATH)
JAVA  = java  -classpath $(SSFNET_TEST_CLASSPATH)
SCHEMAS = $(TOPDIR)/examples/net.dml
DICTIONARY = ./dictionary.dml


TESTNAME = isd1
TESTSUPPORT = isd1_networks
RUNTIME = 1000

all:
	@rm -f test-raw.out
	$(JAVA) SSF.Net.Net $(RUNTIME) $(TESTNAME).dml $(TESTSUPPORT).dml $(DICTIONARY) $(SCHEMAS) > test-raw.out 2>&1
