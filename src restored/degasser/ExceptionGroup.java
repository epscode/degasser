package degasser;

import javax.swing.*;
import org.apache.log4j.Logger;
import java.awt.*;

/**
 * @author Eli Morris
 * @version 2.0
 * @link http://es.ucsc.edu/~emorris
 * 
 * <p>Title: Cryoslug</p>
 * <p>Description: Provides an interface for the cryogenic magnetometer system in the
 * Paleomagnetism Laboratory in the Earth & Planetary Sciences Dept.</p>
 * <p>Copyright: Copyright (c) 2003-2008 by Eli Morris & University of California</p>
 * <p>Company: University of California, Santa Cruz (UCSC)</p>
 * <p>License: GNU General Public License, Version 3 </p>
 * <p>For more information, please contact emorris@pmc.ucsc.edu</p>
 * 
 */

public class ExceptionGroup extends ThreadGroup {
	
	//log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );
	
  public ExceptionGroup() {
    super("ExceptionGroup");
  }
  public void uncaughtException(Thread t, Throwable e) {
    JOptionPane.showMessageDialog(findActiveFrame(),
        e.toString(), "Exception Occurred", JOptionPane.ERROR_MESSAGE);
    
    logger.error( BuildString.getBuildString() );
    logger.error("uncaught exception: ", e);
    // logger.error(e,e);
    // e.printStackTrace();
    
    
    
    
  }
  /**
   * I hate ownerless dialogs.  With this method, we can find the
   * currently visible frame and attach the dialog to that, instead
   * of always attaching it to null.
   */
  private Frame findActiveFrame() {
    Frame[] frames = JFrame.getFrames();
    for (int i = 0; i < frames.length; i++) {
      Frame frame = frames[i];
      if (frame.isVisible()) {
        return frame;
      }
    }
    return null;
  }
}