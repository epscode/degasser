package degasser;

import java.awt.event.*;
import java.text.NumberFormat;

import javax.swing.*;
import org.apache.log4j.Logger;
import java.util.Vector;

public class AddHeatingStageDialog extends HeatingStageDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JButton okButton, cancelButton;
	
	boolean cancel;
	
	// JComboBox<String> heatingProfileList;
	
	HeatingProfilePanel heatingProfilePanel;
	ControlBounds controlBounds;
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public AddHeatingStageDialog(JFrame f, HeatingProfilePanel heatingProfilePanel, ControlBounds controlBounds) {
		super(f, heatingProfilePanel);
		
		this.heatingProfilePanel = heatingProfilePanel;
		this.controlBounds = controlBounds;
		showHeatingStageDialog();
	}
	
	public void okButton_actionPerformed(ActionEvent event) {
		
		boolean exitDialog = true;
		
		try {
			// int stageSelectedIndex = heatingProfileList.getSelectedIndex();
			
			int timeValue = ( (Number) timeField.getValue()).intValue();
			int relayValue = ( (Number) relayField.getValue()).intValue();
			
			HeatingProfileTableModel heatingProfileTableModel = heatingProfilePanel.getHeatingProfileTabelModel();
			
			int stageIndex = heatingProfilePanel.heatingProfile.getSize();
			
			System.out.println("stage index: from add heating stage: " + stageIndex);
			
			heatingProfileTableModel.getHeatingProfile().addHeatingStage(stageIndex + 1, timeValue, relayValue);
			
			// heatingProfilePanel.setExecutingStage(stageIndex + 1);
			heatingProfileTableModel.getHeatingProfile().setSelectedStage(stageIndex);
			
		} catch (NumberFormatException error) {
			logger.info(
				"Something is wrong with one of the values you entered."
					+ " Couldn't convert it to a number.");
			
			JOptionPane.showMessageDialog(this,
			    "Something is wrong with one of the numbers entered.",
			    "Input Error",
			    JOptionPane.ERROR_MESSAGE);
			
			exitDialog = false;

			// throw up an error if something is wrong
		} catch (Exception error) {
			
			logger.info("an unknown error has occurred in the heating stage dialog box: ");
			StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
			for (int index = 0; index < stackTraceElement.length; index++) {
				String strackTrace = stackTraceElement[index].toString();
				logger.info(strackTrace);
			}
		
			JOptionPane.showMessageDialog(this,
					"An unknown error has occurred in the heating stage dialog box. " + error,
					"Unknown Error",
					JOptionPane.ERROR_MESSAGE);
			
			logger.info("an unknown error has occurred in the heating stage dialog box: " + error);
		
			exitDialog = false;
		}
		
		if (exitDialog) {
			setVisible(false);
			dispose();
		}
		
	}
	
public Vector<String> getHeatingProfileStages() {
		
		Vector<String> heatingProfileStrings = new Vector<String>();
		
		heatingProfileStrings.add("Add Heating Stage");
		
		return heatingProfileStrings;
	}
	
}