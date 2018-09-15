package degasser;

import javax.swing.*;
import org.apache.log4j.Logger;
import java.awt.*;

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