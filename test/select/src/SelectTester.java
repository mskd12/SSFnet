/**
 * SelectTester.java
 *
 * @author BJ Premore
 */


package SSF.OS.BGP4.test.select.src;


import java.util.*;
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


// ===== class SSF.OS.BGP4.test.select.SelectTester ======================== //
/**
 * A test class, masquerading as a ProtocolSession, for testing the route
 * selection behavior of the BGPSession below it.  It doesn't behave at all
 * like a ProtocolSession, and the only reason that it is one is so that it can
 * easily be dropped into a simulation without having to modify any source code
 * to include it.  Only the DML file used needs to be modified.
 */
public class SelectTester extends ProtocolSession {

  // ........................ member data .......................... //

  /** The BGPSession with which this tester is associated. */
  private BGPSession bgp;

  /** Whether or not the setup work has been done yet. */
  private boolean setup = false;

  // ----- SelectTester() -------------------------------------------------- //
  /**
   * We must have a zero-argument, public constructor (so that
   * <code>newInstance()</code> can be used to create a new
   * <code>SelectTester</code> instance).
   */
  public SelectTester() {
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
   * Creates an SSF process which sends a message from a source router to each
   * of its two peers.  Later on (in the simulation), other routers will
   * receive two different advertisements about how to get to the source
   * router.  A check will be made to see if those routers select the better of
   * the two options.
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
              throw new Error("Select Tester couldn't get handle to BGP");
            }
            setup = true;
          } else {
            // first, wait a while to give BGP time to get set up
            waitFor(Net.seconds(100.0));

            // We are in AS X, NHI = 1:1.  Peer Y has NHI = 2:1, and peer Z has
            // NHI = 3:1.  We compose an update message to advertise a (bogus)
            // route to each of our two peers, Y and Z.
            HashMap msgs = new HashMap(2);
            for (int i=0; i<bgp.nbs.length-1; i++) { // skip last nb ('self')
              Route rte = new Route();
              rte.set_nlri(new IPaddress(Debug.bogusip));
              if (!Global.basic_attribs) {
                rte.set_origin(Origin.IGP);
              }
              rte.prepend_as(bgp.as_nh);
              rte.set_nexthop(bgp.nbs[i].return_ip);
              msgs.put(bgp.nbs[i], new UpdateMessage(bgp.nh, rte));
            }

            // We're cheating a bit by printing this message before both sends
            // (since we will be staggering them in time), but it doesn't hurt
            // the validity of the test.
            bgp.debug.valid(Global.SELECT, 1);

            // send message to peer Z

            PeerEntry peer = bgp.nbs[bgp.nh2peerind("3:1")];
            UpdateMessage msg = (UpdateMessage)msgs.get(peer);
            bgp.force_send(msg, peer, 0);
            bgp.reset_timer(peer, Timer.KEEPALIVE);
            bgp.reset_timer(peer, Timer.HOLD);

            // We stagger the messages to the two peers so that we can be sure
            // which peer receives their copy first.
            waitFor(Net.seconds(50.0));
          
            // send message to peer Y

            peer = bgp.nbs[bgp.nh2peerind("2:1")];
            msg  = (UpdateMessage)msgs.get(peer);

            bgp.force_send(msg, peer, 0);
            bgp.reset_timer(peer, Timer.KEEPALIVE);
            bgp.reset_timer(peer, Timer.HOLD);
            
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
    throw new Error("SelectTester.push() called");
  }

} // end of class SelectTester
