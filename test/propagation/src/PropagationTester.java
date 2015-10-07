/**
 * PropagationTester.java
 *
 * @author BJ Premore
 */


package SSF.OS.BGP4.test.propagation.src;


import com.renesys.raceway.SSF.*;
import com.renesys.raceway.DML.*;
import SSF.Net.*;
import SSF.OS.*;
import SSF.OS.BGP4.*;
import SSF.OS.BGP4.Comm.*;
import SSF.OS.BGP4.Timing.*;
import SSF.OS.BGP4.Timing.Timer;
import SSF.OS.BGP4.Path.*;
import SSF.OS.BGP4.Util.*;


// ===== class SSF.OS.BGP4.test.propagation.PropagationTester ============== //
/**
 * A test class, masquerading as a ProtocolSession, for testing the routing
 * information-propagation behavior of the BGPSession below it.  It doesn't
 * behave at all like a ProtocolSession, and the only reason that it is one is
 * so that it can easily be dropped into a simulation without having to modify
 * any source code to include it.  Only the DML file used needs to be modified.
 */
public class PropagationTester extends ProtocolSession {

  // ........................ member data .......................... //

  /** The BGPSession with which this tester is associated. */
  private BGPSession bgp;

  /** Whether or not the setup work has been done yet. */
  private boolean setup = false;

  // ----- PropagationTester() --------------------------------------------- //
  /**
   * We must have a zero-argument, public constructor (so that
   * <code>newInstance()</code> can be used to create a new
   * <code>PropagationTester</code> instance).
   */
  public PropagationTester() {
  }

  // ----- config ---------------------------------------------------------- //
  /**
   * Set the configurable values (specified in DML file).
   */
  public void config(Configuration cfg) throws configException {
    super.config(cfg);
  }

  // ----- init ------------------------------------------------------------ //
  /**
   * Creates an SSF process which first sends a message from the source router,
   * waits a short time, then checks the neighboring router's tables to see if
   * it arrived there properly.
   */
  public void init() throws ProtocolException {
    new process(inGraph()) {
        public boolean isSimple() {
          return false;
        }

        public void action() {
          if (!setup) {
            try {
              bgp = (BGPSession)inGraph().SessionForName("bgp");
            } catch (ProtocolException pex) {
              throw new Error("Propagation Tester couldn't get handle to BGP");
            }
            setup = true;
          } else {
            // first, wait a while to give BGP time to get set up
            waitFor(Net.seconds(7.0));

            // We are in host 3:1, and our one peer is in 2:1.  We'll compose
            // an update message advertising a (bogus) route to that peer.
            PeerEntry peer = bgp.nbs[bgp.nh2peerind("2:1")];

            Route rte = new Route();
            rte.set_nlri(new IPaddress(Debug.bogusip));
            if (!Global.basic_attribs) {
              rte.set_origin(Origin.IGP);
            }
            rte.prepend_as(bgp.as_nh);
            rte.set_nexthop(peer.return_ip);
            
            UpdateMessage msg = new UpdateMessage(bgp.nh, rte);

            bgp.force_send(msg, peer, 0);

            bgp.debug.valid(Global.PROPAGATION, 1);

            bgp.reset_timer(peer, Timer.KEEPALIVE);
            bgp.reset_timer(peer, Timer.HOLD);

            // Wait a bit and then check to make sure that the bogus route
            // found its way into the forwarding table at the other end of the
            // network (two ASes over).
            waitFor(Net.seconds(100.0));

            // Get a handle to the BGPSession at the neighboring router, and
            // then to the one at the neighbor's neighbor.
            BGPSession nb_bgp = null;
            BGPSession nb_nb_bgp = null;
            try {
              NIC nbnic =((ptpLinkLayer)(peer.iface.link_hw)).peer(peer.iface);
              nb_bgp = (BGPSession)((ProtocolSession)nbnic).inGraph().
                                                         SessionForName("bgp");
              PeerEntry nb_peer = nb_bgp.nbs[nb_bgp.nh2peerind("1:1")];
              NIC nb_nbnic = ((ptpLinkLayer)(nb_peer.iface.link_hw)).
                                                           peer(nb_peer.iface);
              nb_nb_bgp = (BGPSession)((ProtocolSession)nb_nbnic).inGraph().
                                                         SessionForName("bgp");
            } catch (ProtocolException p) {
              throw new Error("BGP not implemented in neighbor");
            }

            if (nb_nb_bgp.fwd_table.find(Debug.bogusip.intval()) != null) {
              bgp.debug.valid(Global.PROPAGATION, 4);
            }

            waitForever();
          }
        }
      };
  }

  // ----- now ------------------------------------------------------------- //
  /**
   * Returns the current simulation time.  A convenience method so that
   * functions, not just Processes, can get the current simulation time.
   *
   * @return the current simulation time
   */
  public double now() {
    return ((double)(inGraph().now()))/((double)Net.frequency);
  }

  // ----- push ------------------------------------------------------------ //
  /**
   * Handles incoming events.
   */
  public boolean push(ProtocolMessage message, ProtocolSession fromSession) {
    // there will never be any incoming events in this pseudo-ProtocolSession
    throw new Error("PropagationTester.push() called");
  }

} // end of class PropagationTester
