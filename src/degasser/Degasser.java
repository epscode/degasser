package degasser;

import javax.swing.UIManager;
import java.awt.*;

/**
 * <p>Title: Degasser Interface</p>
 * <p>Description: Provides an interface for a vacuum degasser.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: UCSC</p>
 * @author Eli Morris
 * @version 1.0
 */

public class Degasser {
  private boolean packFrame = false;

  //Construct the application
  public Degasser() {
	Gui frame = new Gui();
	// LayoutTest frame = new LayoutTest();
	//Validate frames that have preset sizes
	//Pack frames that have useful preferred size info, e.g. from their layout
	if (packFrame) {
	  frame.pack();
	}
	else {
	  frame.validate();
	}
	//Center the window
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension frameSize = frame.getSize();
	if (frameSize.height > screenSize.height) {
	  frameSize.height = screenSize.height;
	}
	if (frameSize.width > screenSize.width) {
	  frameSize.width = screenSize.width;
	}
	frame.setLocation((screenSize.width - frameSize.width) / 2, 
	(screenSize.height - frameSize.height) / 2);
	frame.setVisible(true);
  }
  
  //Main method
  public static void main(String[] args) {
  ThreadGroup exceptionThreadGroup = new ExceptionGroup();
	new Thread(exceptionThreadGroup, "Init thread") {
		public void run() {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			new Degasser();
		}
	}.start();
  }
}