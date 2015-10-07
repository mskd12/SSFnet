/**
 * AppMessage.java
 *
 * @author BJ Premore
 */


package SSF.OS.BGP4.test.App;


import SSF.OS.ProtocolMessage;
import SSF.OS.BGP4.Util.*;


// ===== class SSF.OS.BGP4.test.AppMessage ================================= //
/**
 * A simple message for end-to-end transmission containing only the source and
 * destination NHI addresses.  Expected to be used primarily for testing.
 */
public class AppMessage extends ProtocolMessage {

  /** The NHI prefix of the host where this message originated. */
  public String srcnh;

  /** The NHI prefix of this message's destination host. */
  public String dstnh;

  // ----- constructor AppMessage ------------------------------------------ //
  /**
   * Constructor for an AppMessage.
   *
   * @param snh  The NHI address where the message originated.
   * @param dnh  The NHI address of this message's destination.
   */
  AppMessage(String snh, String dnh) {
    super();
    srcnh = snh;
    dstnh = dnh;
  }
} // end of class AppMessage
