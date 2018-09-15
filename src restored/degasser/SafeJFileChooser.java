package degasser;

import java.awt.*;
import javax.swing.*;

import org.apache.log4j.Logger;

import java.io.*;

public class SafeJFileChooser extends JFileChooser {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	// JFileChooser jfc = new JFileChooser() {
	String command;
	String extension ="";
	File newF;
	
	SafeJFileChooser(String extension) {
		this.extension = extension;
	}
	
	public int showSaveDialog(Component parent) {
		command = "save";
		return super.showSaveDialog(parent);
	}

	public int showOpenDialog(Component parent) {
		command = "open";
		return super.showOpenDialog(parent);
	}

	public void approveSelection() {
		int selection = -1;
		// need to add the extension on to the end of the file
		File f = getSelectedFile();
		String name = f.toString();
		String newName = name;
		if (name.endsWith(extension) == true) {
			newName = name;
		} else {
			newName = name + "." + extension;
		}
		logger.debug("from file dialog: " + newName);
		newF = new File(newName);
		if (command.equalsIgnoreCase("save") ) {
			if (newF.exists())
				selection =
					JOptionPane.showConfirmDialog(
						this,
						"File " + " already exists. Overwrite data in the file?",
						"File exist",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
			if (selection == JOptionPane.NO_OPTION)
				return;
			else
				super.approveSelection();
		} else
			super.approveSelection();
	}
	
	public File getFile() {
		return newF; 
	}
}