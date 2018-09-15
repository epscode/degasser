package degasser;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

public class InsertHeatingStageDialog extends HeatingStageDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JFrame frame;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public InsertHeatingStageDialog(JFrame frame, HeatingProfilePanel heatingProfilePanel) {
		super(frame, heatingProfilePanel);

		this.heatingProfilePanel = heatingProfilePanel;
		showHeatingStageDialog();
	}

	public void okButton_actionPerformed(ActionEvent event) {

		boolean exitDialog = true;

		try {
			int stageSelectedIndex = 
				heatingProfileList.getSelectedIndex();
			
			String timeString = timeField.getText();
			int timeValue = Integer.parseInt(timeString);

			String relayString = relayField.getText();
			int relayValue = Integer.parseInt(relayString);
			
			HeatingProfileTableModel heatingProfileTableModel = heatingProfilePanel.getHeatingProfileTabelModel();

			heatingProfileTableModel.getHeatingProfile().insertHeatingStage(stageSelectedIndex, timeValue,
					relayValue);

		} catch (NumberFormatException error) {
			logger.info("Something is wrong with one of the values you entered." + " Couldn't convert it to a number.");

			JOptionPane.showMessageDialog(this, "Something is wrong with one of the numbers entered.", "Input Error",
					JOptionPane.ERROR_MESSAGE);

			exitDialog = false;

			// throw up an error if something is wrong
		} catch (Exception error) {
			
			logger.info("Error occurred in the insert heating stage dialog box: " + error);
			StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
			for (int index = 0; index < stackTraceElement.length; index++) {
				String strackTrace = stackTraceElement[index].toString();
				logger.info(strackTrace);
			}
			
			JOptionPane.showMessageDialog(this, "An unknown error has occurred in heating stage dialog box.",
					"Unknown Error", JOptionPane.ERROR_MESSAGE);	
		}

		if (exitDialog) {
			setVisible(false);
			dispose();
		}
	}
}