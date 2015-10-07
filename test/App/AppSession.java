/**
 * AppSession.java
 *
 * @author BJ Premore
 */


package SSF.OS.BGP4.test.App;


import java.util.*;
import com.renesys.raceway.SSF.*;
import com.renesys.raceway.DML.*;
import SSF.Net.*;
import SSF.Net.Util.*;
import SSF.OS.*;
import SSF.OS.BGP4.*;
import SSF.OS.BGP4.Util.*;


// ===== class SSF.OS.BGP4.test.AppSession ================================= //
/**
 * This is a test class which acts as a very primitive application, able to
 * generate and receive data (IP packets).
 */
public class AppSession extends ProtocolSession {

  // ........................ member data .......................... //

  /** The protocol below us on the stack (IP). */
  private IP ip;

  /** The BGPSession from the AS that this application is running in. */
  private BGPSession bgp;

  /** The IP address of the host this application is running on. */
  private IPaddress ipaddr;

  /** The NHI address prefix of the AS that this application is in. */
  private String as_nh;

  /** The NHI prefix of the host where this application is running. */
  private String nh;

  /** Whether or not the one-time-only setup stuff has been done. */
  private boolean setup = false;

  /** How often, in seconds, to send out a packet (can be set in config). */
  private int frequency = 1;

  /** Whether or not this app is a sender (generates packets). */
  private boolean sender = false;

  /** Whether or not this app is a receiver (accepts packets). */
  private boolean receiver = false;

  /** A hash table of destinations to send to. */
  private HashMap dests;

  /** Whether or not to send to all other hosts with AppSessions.  Only
   *  applies if this is a sender. */
  private boolean send2all = false;

  /** A pointer to this instance of AppSession, for use within the
   *  process in the init method. */
  private AppSession appsess;

  /** Whether or not to print messages associated with AppSession
   *  behavior. */
  private boolean verbose = false;

  // ----- constructor AppSession ------------------------------------------ //
  /**
   * We must have a zero-argument, public constructor (so that newInstance()
   * can be used to create a new AppSession instance).
   */
  public AppSession() {
    appsess = this;
  }

  // ----- AppSession.config ----------------------------------------------- //
  /**
   * Set the configurable values (specified in DML file).
   *
   * @param cfg  contains the values of AppSession's configurable attributes
   * @exception configException  if any of the calls to <code>find</code>
   *                             or <code>findSingle</code> throw such
   *                             an exception
   */
  public final void config(Configuration cfg) throws configException {
    super.config(cfg);
    String str;

    // get sender status
    str = (String)cfg.findSingle("sender");
    if (str != null) {
      sender = Boolean.valueOf(str).booleanValue();
    }

    // get frequency (how often to send out a packet)
    str = (String)cfg.findSingle("frequency");
    if (str != null) {
      frequency = Integer.valueOf(str).intValue();
    }

    if (sender) {
      dests = new HashMap();
      Host myhost = (Host)inGraph();
      for (Enumeration dsts=cfg.find("dest"); dsts.hasMoreElements();) {
        String dest = (String)dsts.nextElement();
        if (dest.equals("all")) {
          send2all = true;
        } else {
          if (send2all) {
            throw new Error("incompatible values for 'dest' attributes");
          }
          IPaddress ipa = new IPaddress(myhost.global_nhi_to_ip(dest));
          ipa.set_prefix_len(32);
          dests.put(dest.substring(0, dest.length()-3), ipa);
        }
      }
    }

    // get receiver status
    str = (String)cfg.findSingle("receiver");
    if (str != null) {
      receiver = Boolean.valueOf(str).booleanValue();
    }

    // get verbosity
    str = (String)cfg.findSingle("verbose");
    if (str != null) {
      verbose = Boolean.valueOf(str).booleanValue();
    }

  }

  // ----- AppSession.init ------------------------------------------------- //
  /**
   * Creates an SSF process which first does a little setup work and then just
   * sends out a packet periodically, according to the frequency variable.
   */
  public final void init() {
    new process(inGraph()) {
      public boolean isSimple() {
        return true;
      }

      public void action() {
        if (!setup) {
          nh = ((Host)inGraph()).nhi;
          try {
            ip = (IP)inGraph().SessionForName("ip");
            ip.open(appsess, null);
          } catch (ProtocolException pex) {
            throw new Error("App couldn't get handle to IP");
          }

          if (!Global.distributed && Global.validation_test!=Global.NO_TEST) {
            // get handle to BGPSession
            try {
              NIC mynic = ((NIC)((Host)inGraph()).interfaceNumbers.
                                                         get(new Integer(1)));
              NIC bgpnic = ((ptpLinkLayer)(mynic.link_hw)).peer(mynic);
              bgp = (BGPSession)((ProtocolSession)bgpnic).inGraph().
                                                         SessionForName("bgp");
            } catch (ProtocolException p) {
              throw new Error("App couldn't get handle to BGPSession");
            }
          }

          // find out what AS I'm in
          Host my_host = (Host)inGraph();
          as_nh = AS_descriptor.get_as_nh(my_host);

          for (int i=0; i<ip.INTERFACE_COUNT; i++) {
            NIC nic = ip.INTERFACE_SET[i];
            ipaddr = new IPaddress(nic.ipAddr);
          }

          if (ip.INTERFACE_COUNT > 1) {
            throw new Error("host had multiple interfaces");
          }


          if (sender && send2all) {
            // this sender is supposed to send messages to all AppSessions
            // in the entire network

            Net anet = my_host.getNet(); // gets the top-level Net
            dests = new HashMap();

            // for each host in the entire network
            Configuration topnetcfg = anet.cidrMap.networkConfiguration();
            // Parse the whole DML file, and check which hosts are running
            // AppSession.  Very inefficient, but necessary in distributed
            // environment.
            searchTop(topnetcfg);
          }

          setup = true;
        } else {
          // ---------------- setup has already been done -------------------

          // go ahead and send messages (only senders reach this code block)

          for (Iterator it=dests.keySet().iterator(); it.hasNext(); ) {
            String dest_nh = (String)it.next();
            IPaddress dest_ip = (IPaddress)dests.get(dest_nh);

            AppMessage msg = new AppMessage(nh, dest_nh);
            IpHeader iph = new IpHeader(Protocols.TEST_PRTL_NUM,
                                        ipaddr.intval(), dest_ip.intval());

            iph.carryPayload(msg);
            try {
              ip.push(iph, appsess);
            } catch (ProtocolException pex) {
              throw new Error("couldn't push packet from App to IP");
            }

            double t = now();
            String ws = StringManip.ws(10 - (""+t).length());
            if (verbose) {
              System.out.println(hdr() + "--- sent msg to " + dest_nh);
            }
          }
        }

        long waittime = Net.seconds(9999999999.0);  // infinity
        if (sender) {
          waittime = Net.seconds(1.0) * frequency;
          if(frequency!=100) {
            frequency=100;
          }
        }
        waitFor(waittime);
      }
    };
  }

  // ----- AppSession.searchTop -------------------------------------------- //
  /**
   * Searches the top-level DML configuration for Nets and hosts, eventually
   * finding hosts running AppSessions and adding their addresses to a table.
   */
  private void searchTop(Configuration topnetcfg){
    try {
      String curr_nh = "";
      Configuration ncfg,hcfg;

      // Process all the Nets.
      for (Enumeration e=topnetcfg.find("Net"); e.hasMoreElements();) {
        ncfg = (Configuration)e.nextElement();
        searchNet(ncfg,curr_nh);
      }

      for (Enumeration e=topnetcfg.find("host"); e.hasMoreElements();) {
        hcfg = (Configuration)e.nextElement();
        searchHost(hcfg,curr_nh);
      }
    } catch (configException ce) {
      Debug.gerr("problem setting up AppSession in searchTop(): " + ce);
    }
  }

  // ----- AppSession.searchNet -------------------------------------------- //
  /**
   * Searches a Net, eventually finding hosts running AppSessions and adding
   * their addresses to a table.
   */
  private void searchNet(Configuration netcfg, String parent_nh){
    Configuration ncfg,hcfg;
    idrange netids;
    try{
      netids = new idrange();
      netids.config(netcfg);

      for (Enumeration e=netcfg.find("Net"); e.hasMoreElements();) {
        ncfg = (Configuration)e.nextElement();
        for (int netid=netids.minid; netid<=netids.maxid; netid++) {
          searchNet(ncfg,(("".equals(parent_nh))?(""+netid):
                          (parent_nh+cidrBlock.NHI_SEPARATOR+netid)));
        }
      }

      for (Enumeration e=netcfg.find("host"); e.hasMoreElements();) {
        hcfg = (Configuration)e.nextElement();
        for (int netid=netids.minid; netid<=netids.maxid; netid++) {
          searchHost(hcfg,(("".equals(parent_nh))?(""+netid):
                           (parent_nh+cidrBlock.NHI_SEPARATOR+netid)));
        }
      }
    } catch (configException ce) {
      Debug.gerr("problem setting up AppSession in searchNet(): " + ce);
    }
  }

  // ----- AppSession.searchHost ------------------------------------------- //
  /**
   * Searches a host, checking to see if it's running an AppSession, and if so,
   * adding its address to a table.
   */
  private void searchHost(Configuration hcfg, String parent_nh) {
    String host_nh;
    idrange hostids;
    Configuration gcfg,scfg;
    boolean hasapp = false;
    try {
      gcfg = (Configuration)hcfg.findSingle("graph");

      for (Enumeration e=gcfg.find("ProtocolSession");
                                             e.hasMoreElements() && !hasapp;) {
        scfg = (Configuration)e.nextElement();
        if (((String)scfg.findSingle("name")).equals("test")) {
          hasapp = true;
        }
      }
      if (hasapp) { // there is an AppSession running at the host
        hostids = new idrange();
        hostids.config(hcfg);
        for (int hostid=hostids.minid; hostid<=hostids.maxid; hostid++) {
          host_nh = cidrBlock.nhi_concat(parent_nh,hostid);
          if (!host_nh.equals(((Host)inGraph()).nhi)) { // don't send to self
            // Add the IP address of this host to table of destinations.
            IPaddress ipa = new IPaddress(((Host)inGraph()).
                                            global_nhi_to_ip(host_nh + "(1)"));
            ipa.set_prefix_len(32);
            dests.put(host_nh, ipa);
          }
        }
      }
    } catch (configException ce) {
      Debug.gerr("problem setting up AppSession in searchHost(): " + ce);
    }
  }

  // ----- AppSession.now -------------------------------------------------- //
  /**
   * A convenience method so that any functions, not just Processes, can get
   * the current simulation time.
   *
   * @return the current simulation time
   */
  public final double now() {
    return ((double)(inGraph().now()))/((double)Net.frequency);
  }

  // ----- Debug.hdr ------------------------------------------------------- //
  /**
   * Constructs a standardized output format prefix.
   *
   * @return the standardized output prefix as a string
   */
  public final String hdr() {
    double t = now();
    String wsa = StringManip.ws(12 - (""+t).length());
    String wsb = StringManip.ws(8 - nh.length());
    String str = t + wsa + "app@" + nh + wsb;
    return str;
  }

  // ----- AppSession.push ------------------------------------------------- //
  /**
   * This process handles incoming events.
   *
   * @param message      The incoming event/message.
   * @param fromSession  The protocol session from which the message came.
   * @return  whether or not <code>push</code> executed without error
   */
  public final boolean push(ProtocolMessage message,
                            ProtocolSession fromSession) {

    AppMessage msg = (AppMessage)message;

    if (receiver) {
      if (verbose) {
        System.out.println(hdr() + "+++ rcvd msg from " + msg.srcnh);
      }
      if (!Global.distributed && Global.validation_test != Global.NO_TEST) {
        bgp.debug.valid(Global.FORWARDING1, 1);
        bgp.debug.valid(Global.FORWARDING2, 1);
        bgp.debug.valid(Global.FORWARDING3, 1);
        bgp.debug.valid(Global.FORWARDING4, 1);
      }
    } else {
      if (verbose) {
        System.out.println(hdr() + "xxx ignored msg (not a receiver)");
      }
    }

    return true;
  } // end of push()

} // end of class AppSession
