package degasser;

import java.text.DecimalFormat;

import javax.swing.*;
import org.apache.log4j.Logger;

public class EditHeatingStageDialog extends HeatingStageDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	JComboBox<String> heatingProfileList;
	DecimalFormat integerDigit = new DecimalFormat("#0");
	// HeatingProfilePanel heatingProfilePanel;
	
	// JFormattedTextField timeField;
	// JFormattedTextField relayField;
	
	HighlightedFormattedTextField timeField;
	HighlightedFormattedTextField relayField;
	*/
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public EditHeatingStageDialog(JFrame f, HeatingProfilePanel heatingProfilePanel) {
		super(f, heatingProfilePanel);
		
		// this.heatingProfilePanel = heatingProfilePanel;
		showHeatingStageDialog();
		fillInitialValues();
	}
}