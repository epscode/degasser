package degasser;


import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.*;

//Create a new JFrame  
public class CenterDialogBox extends JDialog {
	
	CenterDialogBox(JFrame frame) {
		super(frame);
	}
	
	private static final long serialVersionUID = 1L;

	public void setSize(int width, int height) {
		super.setSize(width, height);

		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();

		// Calculate the frame location
		int x = screenSize.width / 2 - width / 2;
		int y = screenSize.height /2  - height / 2;

		// Set the new frame location
		setLocation(x, y);
	}

	public void setSize(Dimension size) {
		setSize(size.width, size.height);
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}