package degasser;

import javax.swing.*;
import org.apache.log4j.Logger;

public class EditHeatingStageDialog extends HeatingStageDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	JButton okButton, cancelButton;
	
	boolean cancel;
	
	JComboBox<String> heatingProfileList;
	
	HeatingProfilePanel heatingProfilePanel;
	*/
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public EditHeatingStageDialog(JFrame f, HeatingProfilePanel heatingProfilePanel) {
		super(f, heatingProfilePanel);
		
		this.heatingProfilePanel = heatingProfilePanel;
		showHeatingStageDialog();
	}
}