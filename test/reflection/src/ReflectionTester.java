/**
 * ReflectionTester.java
 *
 * @author BJ Premore
 */


package SSF.OS.BGP4.test.reflection.src;


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


// ===== class SSF.OS.BGP4.test.reflection.ReflectionTester ================ //
/**
 * A test class, masquerading as a ProtocolSession, for testing route reflector
 * behavior of BGP.  It doesn't behave at all like a ProtocolSession, and the
 * only reason that it is one is so that it can easily be dropped into a
 * simulation without having to modify any source code to include it.  Only the
 * DML file used needs to be modified.
 */
public class ReflectionTester extends ProtocolSession {

  // ........................ member data .......................... //

  /** The BGPSession with which this tester is associated. */
  private BGPSession bgp;

  /** Whether or not the setup work has been done yet. */
  private boolean setup = false;


  // ----- ReflectionTester() ---------------------------------------------- //
  /**
   * We must have a zero-argument, public constructor (so that
   * <code>newInstance()</code> can be used to create a new
   * <code>ReflectionTester</code> instance).
   */
  public ReflectionTester() {
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
   * Creates an SSF process which sends a simple message (with a bogus address
   * advertisement) from a source router.  This message will be traced through
   * the network by other parts of the validation test.
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
              throw new Error("Reflection Tester couldn't get handle to BGP");
            }
            setup = true;
          } else {
            // first, wait a while to give BGP time to get set up
            waitFor(Net.seconds(100.0));

            // We are in host 1:1, and our one peer is in 4:1.  We'll compose
            // an update message advertising a (bogus) route to that peer.
            PeerEntry peer = bgp.nbs[bgp.nh2peerind("4:1")];

            Route rte = new Route();
            rte.set_nlri(new IPaddress(Debug.bogusip));
            if (!Global.basic_attribs) {
              rte.set_origin(Origin.IGP);
            }
            rte.prepend_as(bgp.as_nh);
            rte.set_nexthop(peer.return_ip);
            UpdateMessage msg = new UpdateMessage(bgp.nh, rte);
            
            // send it to our only peer at 4:1
            bgp.force_send(msg, peer, 0);

            bgp.debug.valid(Global.REFLECTION, 1);

            bgp.reset_timer(peer, Timer.KEEPALIVE);
            bgp.reset_timer(peer, Timer.HOLD);

            // wait for the bogus route to be distributed, then check
            // to see that all routers in the network heard about it
            waitFor(Net.seconds(100.0));
            bgp.debug.valid(Global.REFLECTION, 4);

            // OK, the tester's part is done
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
    throw new Error("ReflectionTester.push() called");
  }

} // end of class ReflectionTester
