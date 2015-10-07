#
#  SSF.OS.BGP4 validation test Makefile
#

SHELL = /bin/sh


# ----- SETUP -----------------------------------------------------------------

TOPDIR = ../../../../..
include $(TOPDIR)/Makefile.common

JAVAC = javac -classpath $(SSFNET_TEST_CLASSPATH)
JAVA  = java  -classpath $(SSFNET_TEST_CLASSPATH)

# ----- MAKE TESTS ------------------------------------------------------------

tests:
	$(JAVAC) App/*.java
	(cd drop-peer;     $(MAKE))
	(cd drop-peer2;    $(MAKE))
	(cd keep-peer;     $(MAKE))
	(cd route-distrib; $(MAKE))
	(cd propagation;   $(MAKE))
	(cd select;        $(MAKE))
	(cd withdrawals;   $(MAKE))
	(cd loopback;      $(MAKE))
	(cd reconnect;     $(MAKE))
	(cd ibgp;          $(MAKE))
	(cd reflection;    $(MAKE))
	(cd goodgadget;    $(MAKE))
	(cd forwarding1;   $(MAKE))
	(cd forwarding2;   $(MAKE))
	(cd forwarding3;   $(MAKE))
	@echo '-------- SSF.OS.BGP4 testing complete --------'
	@echo ''

# ----- MAKE VALID ------------------------------------------------------------

# same as 'make tests' but less verbose

valid:
	@echo ''
	@echo '*** Validating SSF.OS.BGP4 ***'
	@echo ''
	@echo -n 'Building support code ... '
	@$(JAVAC) App/*.java
	@echo 'done.'
	@echo ''
	@echo -n 'running drop-peer validation test ...     '
	@(cd drop-peer; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running drop-peer2 validation test ...    '
	@(cd drop-peer2; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running keep-peer validation test ...     '
	@(cd keep-peer; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running route-distrib validation test ... '
	@(cd route-distrib; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running propagation validation test ...   '
	@(cd propagation; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running select validation test ...        '
	@(cd select; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running withdrawals validation test ...   '
	@(cd withdrawals; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running loopback validation test ...      '
	@(cd loopback; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running reconnect validation test ...     '
	@(cd reconnect; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running ibgp validation test ...          '
	@(cd ibgp; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running reflection validation test ...    '
	@(cd reflection; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running goodgadget validation test ...    '
	@(cd goodgadget; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running forwarding1 validation test ...   '
	@(cd forwarding1; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running forwarding2 validation test ...   '
	@(cd forwarding2; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo -n 'running forwarding3 validation test ...   '
	@(cd forwarding3; $(MAKE) valid) > /dev/null 2>&1
	@echo 'success.'
	@echo ''
	@echo '------- SSF.OS.BGP4 validation tests successful --------'
	@echo ''

# ----- MAKE CLEAN ------------------------------------------------------------

clean:
	@(cd drop-peer;     $(MAKE) clean) > /dev/null 2>&1
	@(cd drop-peer2;    $(MAKE) clean) > /dev/null 2>&1
	@(cd keep-peer;     $(MAKE) clean) > /dev/null 2>&1
	@(cd route-distrib; $(MAKE) clean) > /dev/null 2>&1
	@(cd propagation;   $(MAKE) clean) > /dev/null 2>&1
	@(cd select;        $(MAKE) clean) > /dev/null 2>&1
	@(cd withdrawals;   $(MAKE) clean) > /dev/null 2>&1
	@(cd loopback;      $(MAKE) clean) > /dev/null 2>&1
	@(cd reconnect;     $(MAKE) clean) > /dev/null 2>&1
	@(cd ibgp;          $(MAKE) clean) > /dev/null 2>&1
	@(cd reflection;    $(MAKE) clean) > /dev/null 2>&1
	@(cd goodgadget;    $(MAKE) clean) > /dev/null 2>&1
	@(cd forwarding1;   $(MAKE) clean) > /dev/null 2>&1
	@(cd forwarding2;   $(MAKE) clean) > /dev/null 2>&1
	@(cd forwarding3;   $(MAKE) clean) > /dev/null 2>&1

# ----- MAKE SPOTLESS ---------------------------------------------------------

spotless: clean
	@find . \( -name "*.class" -o -name ".*~" -o -name "*~" \) -exec rm -f {} \; > /dev/null 2>&1
	@echo 'Done.'
