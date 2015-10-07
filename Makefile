#
#  SSF.OS.BGP4 primary Makefile
#

SHELL = /bin/sh

RELEASE = 1.5.1

# ----- SETUP --------------------------------------------------------------- #

TOPDIR = ../../../..
include $(TOPDIR)/Makefile.common

JAVAC = javac -classpath $(SSFNET_TEST_CLASSPATH)
JAVA  = java  -classpath $(SSFNET_TEST_CLASSPATH)

.PHONY:	all checkpath quiet bgp app tests valid javadoc clean spotless dist

# ----- MAKE ALL ------------------------------------------------------------ #

all:	bgp app
	@echo '-------- SSF.OS.BGP4 BUILD COMPLETE --------'

# ----- MAKE CHECKPATH ------------------------------------------------------ #

checkpath:
	@if [ -f $(TOPDIR)/lib/ssfnet.jar ]; \
	then \
	  echo; \
	  echo 'WARNING: $(TOPDIR)/lib/ssfnet.jar exists'; \
	  echo '  To guarantee a fresh build, make sure ssfnet.jar is removed or renamed'; \
	  echo '  during compilation (or that it does not appear in CLASSPATH).'; \
	  echo; \
	fi

# ----- MAKE QUIET ---------------------------------------------------------- #

quiet:
	@echo -n 'Making all ... '
	@$(MAKE) all > /dev/null 2>&1
	@echo 'done.'

# ----- MAKE BGP ------------------------------------------------------------ #

bgp:	checkpath
	$(JAVAC) *.java
	$(JAVAC) Comm/*.java
	$(JAVAC) Path/*.java
	$(JAVAC) Players/*.java
	$(JAVAC) Policy/*.java
	$(JAVAC) Timing/*.java
	$(JAVAC) Util/*.java
	$(JAVAC) Widgets/*.java

# ----- MAKE APP ------------------------------------------------------------ #

app:	checkpath
	$(JAVAC) test/App/*.java

# ----- MAKE TESTS ---------------------------------------------------------- #

# runs validation tests verbosely
tests:
	(cd test; $(MAKE))

# ----- MAKE VALID ---------------------------------------------------------- #

# runs validation tests tersely
valid:
	@(cd test; $(MAKE) valid)

# ----- MAKE JAVADOC -------------------------------------------------------- #

javadoc:
	@echo -n 'Building documentation ... '
	@if [ ! -e "doc/javadoc" ]; \
	then mkdir doc/javadoc; \
	fi
	@javadoc -d doc/javadoc SSF.OS.BGP4 \
                                SSF.OS.BGP4.Comm \
                                SSF.OS.BGP4.Path \
                                SSF.OS.BGP4.Players \
                                SSF.OS.BGP4.Policy \
                                SSF.OS.BGP4.Timing \
                                SSF.OS.BGP4.Widgets \
                                SSF.OS.BGP4.Util \
                                SSF.OS.BGP4.test.App > /dev/null 2>&1
	@echo 'done.'
	@echo 'Java documentation for SSF.OS.BGP4 can be found in doc/javadoc/'

# ----- MAKE CLEAN ---------------------------------------------------------- #

clean:
	@rm -f *.class
	@rm -f Comm/*.class
	@rm -f Path/*.class
	@rm -f Players/*.class
	@rm -f Policy/*.class
	@rm -f Timing/*.class
	@rm -f Widgets/*.class
	@(cd test; $(MAKE) clean) > /dev/null 2>&1
	@echo 'Done.'

# ----- MAKE SPOTLESS ------------------------------------------------------- #

spotless:
	@find . \( -name "*.class" -o -name ".*~" -o -name "*~" \) -exec rm -f {} \;
	@rm -rf doc/javadoc/*
	@if [ -e "doc/javadoc" ]; \
	then rmdir doc/javadoc; \
	fi
	@(cd test; $(MAKE) spotless) > /dev/null 2>&1
	@echo 'Done.'

# ----- MAKE DIST ----------------------------------------------------------- #

dist:
	@if [ -f ../BGP4_$(RELEASE).tar.gz ]; \
	then echo ' *** ERROR: distribution file BGP4_$(RELEASE).tar.gz already exists'; \
	else \
	  (cd ..; tar cpf BGP4_$(RELEASE).tar \
	          BGP4/Makefile \
	          BGP4/README \
	          BGP4/AdjRIBIn.java \
	          BGP4/AdjRIBOut.java \
	          BGP4/BGPSession.java \
	          BGP4/DampInfo.java \
	          BGP4/Debug.java \
	          BGP4/Global.java \
	          BGP4/InBuffer.java \
	          BGP4/LocRIB.java \
	          BGP4/Monitor.java \
	          BGP4/PeerEntry.java \
	          BGP4/RIBElement.java \
	          BGP4/Route.java \
	          BGP4/RouteInfo.java \
	          BGP4/RouteInfoIC.java \
	          BGP4/RouteInfoOOC.java \
	          BGP4/WeightedInBuffer.java \
	          BGP4/package.html \
	          BGP4/Comm/KeepAliveMessage.java \
	          BGP4/Comm/Message.java \
	          BGP4/Comm/NotificationMessage.java \
	          BGP4/Comm/OpenMessage.java \
	          BGP4/Comm/StartStopMessage.java \
	          BGP4/Comm/TransportMessage.java \
	          BGP4/Comm/UpdateMessage.java \
	          BGP4/Comm/package.html \
	          BGP4/Path/ASpath.java \
	          BGP4/Path/Aggregator.java \
	          BGP4/Path/AtomicAggregate.java \
	          BGP4/Path/Attribute.java \
	          BGP4/Path/ClusterList.java \
	          BGP4/Path/Communities.java \
	          BGP4/Path/LocalPref.java \
	          BGP4/Path/MED.java \
	          BGP4/Path/NextHop.java \
	          BGP4/Path/Origin.java \
	          BGP4/Path/OriginatorID.java \
	          BGP4/Path/Segment.java \
	          BGP4/Path/package.html \
	          BGP4/Players/AbstractPlayer.java \
	          BGP4/Players/BinPlayer.java \
	          BGP4/Players/DataPlayer.java \
	          BGP4/Players/TrafficPlayer.java \
	          BGP4/Players/VerbosePlayer.java \
	          BGP4/Players/package.html \
	          BGP4/Policy/Action.java \
	          BGP4/Policy/AtomicAction.java \
	          BGP4/Policy/AtomicPredicate.java \
	          BGP4/Policy/Clause.java \
	          BGP4/Policy/Predicate.java \
	          BGP4/Policy/Rule.java \
	          BGP4/Policy/package.html \
	          BGP4/Timing/EventTimer.java \
	          BGP4/Timing/DampReuseTimer.java \
	          BGP4/Timing/IdealMRAITimer.java \
	          BGP4/Timing/MRAITimeoutMessage.java \
	          BGP4/Timing/TimeoutMessage.java \
	          BGP4/Timing/Timer.java \
	          BGP4/Timing/package.html \
	          BGP4/Util/AS_descriptor.java \
	          BGP4/Util/Bit.java \
	          BGP4/Util/BitString.java \
	          BGP4/Util/IPaddress.java \
	          BGP4/Util/NHI.java \
	          BGP4/Util/Pair.java \
	          BGP4/Util/Parsing.java \
	          BGP4/Util/RadixTree.java \
	          BGP4/Util/RadixTreeIterator.java \
	          BGP4/Util/RadixTreeIteratorAction.java \
	          BGP4/Util/RadixTreeNode.java \
	          BGP4/Util/StringManip.java \
	          BGP4/Util/package.html \
	          BGP4/Widgets/Advertiser.java \
	          BGP4/Widgets/BGPCrasher.java \
	          BGP4/Widgets/BGPKiller.java \
	          BGP4/Widgets/BogusAdvertiser.java \
	          BGP4/Widgets/package.html \
	          BGP4/doc/HISTORY \
	          BGP4/doc/user-guide-ps.zip \
	          BGP4/doc/index.html \
	          BGP4/doc/schema-excerpt.html \
	          BGP4/doc/table1.html \
	          BGP4/doc/table2.html \
	          BGP4/doc/table3.html \
	          BGP4/doc/table4.html \
	          BGP4/doc/table5.html \
	          BGP4/doc/table6.html \
	          BGP4/doc/table7.html \
	          BGP4/doc/validation.html \
	          BGP4/doc/bgp-schema.dml \
	          BGP4/doc/streaming.dml \
	          BGP4/doc/examples/autoconfig.dml \
	          BGP4/doc/examples/manualconfig.dml \
	          BGP4/doc/examples/router123.dml \
	          BGP4/test/Makefile \
	          BGP4/test/dictionary.dml \
	          BGP4/test/App/AppMessage.java \
	          BGP4/test/App/AppSession.java \
	          BGP4/test/App/package.html \
	          BGP4/test/drop-peer/Makefile \
	          BGP4/test/drop-peer/drop-peer-raw.out \
	          BGP4/test/drop-peer/drop-peer.dml \
	          BGP4/test/drop-peer/drop-peer.gif \
	          BGP4/test/drop-peer/drop-peer.out \
	          BGP4/test/keep-peer/Makefile \
	          BGP4/test/keep-peer/keep-peer-raw.out \
	          BGP4/test/keep-peer/keep-peer.dml \
	          BGP4/test/keep-peer/keep-peer.gif \
	          BGP4/test/keep-peer/keep-peer.out \
	          BGP4/test/route-distrib/Makefile \
	          BGP4/test/route-distrib/route-distrib-raw.out \
	          BGP4/test/route-distrib/route-distrib.dml \
	          BGP4/test/route-distrib/route-distrib.gif \
	          BGP4/test/route-distrib/route-distrib.out \
	          BGP4/test/propagation/Makefile \
	          BGP4/test/propagation/propagation-raw.out \
	          BGP4/test/propagation/propagation.dml \
	          BGP4/test/propagation/propagation.gif \
	          BGP4/test/propagation/propagation.out \
	          BGP4/test/propagation/src/PropagationTester.java \
	          BGP4/test/select/Makefile \
	          BGP4/test/select/select-raw.out \
	          BGP4/test/select/select.dml \
	          BGP4/test/select/select.gif \
	          BGP4/test/select/select.out \
	          BGP4/test/select/src/SelectTester.java \
	          BGP4/test/forwarding1/Makefile \
	          BGP4/test/forwarding1/forwarding1-raw.out \
	          BGP4/test/forwarding1/forwarding1.dml \
	          BGP4/test/forwarding1/forwarding1.gif \
	          BGP4/test/forwarding1/forwarding1.out \
	          BGP4/test/withdrawals/Makefile \
	          BGP4/test/withdrawals/withdrawals-raw.out \
	          BGP4/test/withdrawals/withdrawals.dml \
	          BGP4/test/withdrawals/withdrawals.gif \
	          BGP4/test/withdrawals/withdrawals.out \
	          BGP4/test/withdrawals/src/WithdrawalsTester.java \
	          BGP4/test/forwarding2/Makefile \
	          BGP4/test/forwarding2/forwarding2-raw.out \
	          BGP4/test/forwarding2/forwarding2.dml \
	          BGP4/test/forwarding2/forwarding2.gif \
	          BGP4/test/forwarding2/forwarding2.out \
	          BGP4/test/ibgp/Makefile \
	          BGP4/test/ibgp/ibgp-raw.out \
	          BGP4/test/ibgp/ibgp.dml \
	          BGP4/test/ibgp/ibgp.gif \
	          BGP4/test/ibgp/ibgp.out \
	          BGP4/test/forwarding3/Makefile \
	          BGP4/test/forwarding3/forwarding3-raw.out \
	          BGP4/test/forwarding3/forwarding3.dml \
	          BGP4/test/forwarding3/forwarding3.gif \
	          BGP4/test/forwarding3/forwarding3.out \
	          BGP4/test/reflection/Makefile \
	          BGP4/test/reflection/reflection-raw.out \
	          BGP4/test/reflection/reflection.dml \
	          BGP4/test/reflection/reflection.gif \
	          BGP4/test/reflection/reflection.out \
	          BGP4/test/reflection/src/ReflectionTester.java \
	          BGP4/test/goodgadget/Makefile \
	          BGP4/test/goodgadget/goodgadget-raw.out \
	          BGP4/test/goodgadget/goodgadget.dml \
	          BGP4/test/goodgadget/goodgadget.gif \
	          BGP4/test/goodgadget/goodgadget.out \
	          BGP4/test/loopback/Makefile \
	          BGP4/test/loopback/loopback-raw.out \
	          BGP4/test/loopback/loopback.dml \
	          BGP4/test/loopback/loopback.gif \
	          BGP4/test/loopback/loopback.out \
	          BGP4/test/drop-peer2/Makefile \
	          BGP4/test/drop-peer2/drop-peer2-raw.out \
	          BGP4/test/drop-peer2/drop-peer2.dml \
	          BGP4/test/drop-peer2/drop-peer2.gif \
	          BGP4/test/drop-peer2/drop-peer2.out \
	          BGP4/test/reconnect/Makefile \
	          BGP4/test/reconnect/reconnect-raw.out \
	          BGP4/test/reconnect/reconnect.dml \
	          BGP4/test/reconnect/reconnect.gif \
	          BGP4/test/reconnect/reconnect.out); \
	  echo; \
	  gzip -q ../BGP4_$(RELEASE).tar; \
	  if [ -f ../BGP4_$(RELEASE).tar.gz ]; \
	  then echo 'created distribution file ../BGP4_$(RELEASE).tar.gz'; \
	  fi; \
	  echo; \
	  echo 'CHECKLIST FOR PREPARING A DISTRIBUTION'; \
	  echo '   - change ver# in this Makefile,'; \
	  echo '     doc/index.html (two places), and BGPSession.java'; \
	  echo '   - update doc/HISTORY file'; \
	  echo '   - add any new tests to test/Makefile (three places)'; \
	fi
