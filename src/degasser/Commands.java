package degasser;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

class Commands {

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	SerialCommunications serialCommunications;
	JFrame frame;
	HeatingProfile heatingProfile;
	String errorMessageString = "";
	double responseValue = 0.0;
	JMenuBar menu;
	GradientButton startButton;
	ControlBounds controlBounds;
	boolean fanOn = false;
	SystemState systemState;
	
	TimerPanel heatingTimerPanel;
	TimerPanelStage heatingTimerPanelStage;
	
	public Commands(SerialCommunications serialCommunications, HeatingProfile heatingProfile, JFrame frame,
			JMenuBar menu, GradientButton startButton, ControlBounds controlBounds, SystemState systemState,
			TimerPanel heatingTimerPanel, TimerPanelStage heatingTimerPanelStage) {
		this.serialCommunications = serialCommunications;
		this.heatingProfile = heatingProfile;
		this.frame = frame;
		this.menu = menu;
		this.startButton = startButton;
		this.controlBounds = controlBounds;
		this.systemState = systemState;
		this.heatingTimerPanel = heatingTimerPanel;
		this.heatingTimerPanelStage = heatingTimerPanelStage;
	}

	public void setystemState(SystemState systemState) {
		this.systemState = systemState;
	}

	public void setFilamentsEnable(boolean filamentsEnableValue) {

		logger.info("set the filaments enable value: " + filamentsEnableValue);

		boolean success = sendCommand("setFilamentsEnableValue#" + (filamentsEnableValue ? 1 : 0), true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isOk() ) {

				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Set Filaments Enable Control Value OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Set Filaments Enable Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void sendBlank() {
		logger.info("send a blank to try to clear the input buffer.");

		serialCommunications.setPortErrorDialog(false);
		serialCommunications.setProfileRunningImportant(false);
		serialCommunications.commandResponse("");
	}

	public boolean sendCommand(String command, boolean runningImportant) {

		// check if serial port is open
		if (!serialCommunications.isPortOpen()) {

			try {
				logger.info("trying to open the serial port");
				serialCommunications.readSerialPortFile();
				boolean success = serialCommunications.openPort();

				logger.info("success status from send command: " + success);

				if (success) {
					sendBlank();
					setBoundsValuesNoDialog();
				}

			} catch (Exception error) {
				logger.info("serial port is already open.");

				JOptionPane.showMessageDialog(frame, "Serial Port is Aleady open", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

		// ok, now is it open?
		if (serialCommunications.isPortOpen() ) {
			serialCommunications.setProfileRunningImportant(runningImportant);
			serialCommunications.commandResponse(command);
			return true;
		} else {
			return false;
		}
	}

	// send profile
	public void sendHeatingProfile() {

		System.out.println("sending heating profile");

		boolean success = sendCommand("sendProfile#" + heatingProfile.getHeatingProfileCommand(), false);
		
		System.out.println("profile sent: " + heatingProfile.getHeatingProfileCommand() );

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			
			if (serialCommunications.isOk() ) {
				processOK(response);

				JOptionPane.showMessageDialog(frame, "Sent Heating Profile OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Send Heating Profile Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// send profile
	public void showHeatingProfile() {

		logger.info("show heating profile");

		boolean success = sendCommand("showHeatingProfile", false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Show Heating Profile OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Show Heating Profile Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void startRun() {

		System.out.println("starting to run the heating profile");

		boolean success = sendCommand("sendProfile#" + heatingProfile.getHeatingProfileCommand(), false);
		
		System.out.println("profile sent: " + heatingProfile.getHeatingProfileCommand() );

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isOk() ) {
				processOK(response);

				serialCommunications.setProfileRunningImportant(false);
				serialCommunications.commandResponse("startProfile");
				response = serialCommunications.getResponse();
				serialCommunications.resetCompleteResponse();
				
				if (serialCommunications.isOk() ) {
					processOK(response);
					heatingProfile.setRunningProfile(true);
					heatingProfile.setSelectedStage(0);
					// toggleRunningMenuItems(false);

					heatingTimerPanel.setTimeFromHeatingProfile();
					heatingTimerPanel.start();

					heatingTimerPanelStage.setTimeFromHeatingProfile();
					heatingTimerPanelStage.start();

				} else {
					logger.info("indeterminate state");

					// Unknown Error
					JOptionPane.showMessageDialog(frame, "Start Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {

				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Start Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void stopRun() {

		logger.info("stopping running the heating profile");

		boolean success = sendCommand("abortProfile", false);

		if (success) {

			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isOk() ) {
				processOK(response);
				// toggleRunningMenuItems(true);
				// JMenuItem powerSupplyMenuItem = (JMenuItem) menu.getMenu(3).getMenuComponent(9);
				// powerSupplyMenuItem.setText("Turn Off Additional Power Supply");

				// startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");

				// startCommandReceivedOK
				//JOptionPane.showMessageDialog(frame, "Stop Command OK", "Control Message",
				//		JOptionPane.INFORMATION_MESSAGE);
			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Stop Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void estimateFilaments() {

		logger.info("estimating the number of filaments");
	
		boolean success = sendCommand("estimateFilaments", true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isOk() ) {
				processOK(response);
				
				HeatingProfile filamentsProfile = new HeatingProfile(frame);
				filamentsProfile.addHeatingStage(0, 1, 10); // think need to try non-integer times or seconds
				
				heatingTimerPanel.setHeatingProfile(filamentsProfile);              
				heatingTimerPanelStage.setHeatingProfile(filamentsProfile); 
				
				heatingTimerPanel.setTimebaseSeconds(true); 
				heatingTimerPanelStage.setTimebaseSeconds(true); 
				
				heatingTimerPanel.setTimeFromHeatingProfile();
				heatingTimerPanelStage.setTimeFromHeatingProfile();
				
				heatingTimerPanel.start();
				heatingTimerPanelStage.start();
				
				heatingTimerPanel.setTimebaseSeconds(false); 
				heatingTimerPanelStage.setTimebaseSeconds(false); 
				
				logger.info("set filament state: " + systemState.isFilamentsSet() );

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Estimate Filaments Command Error", "Error Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public double getResponseValue() {
		return this.responseValue;
	}

	public void setFilaments(int filamentsValue) {

		logger.info("set the number of filaments: " + filamentsValue);

		boolean success = sendCommand("setFilamentValue#" + filamentsValue, true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			
			if (serialCommunications.isOk() ) {
				processOK(response);

				systemState.setFilamentsSet(true);
				logger.info("set filament state: " + systemState.isFilamentsSet());

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Set Filaments Command Error", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setPowerSupply(boolean powerSupplyValue) {

		logger.info("set the power supply value: " + powerSupplyValue);

		boolean success = sendCommand("setPowerSupplyValue#" + (powerSupplyValue ? 1 : 0), true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			
			if (serialCommunications.isOk()) {
				processOK(response);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Set powerSupply Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void setCurrentValue(int currentValue) {

		logger.info("set the current value: " + currentValue);

		boolean success = sendCommand("setCurrentValue#" + currentValue, true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isOk()) {
				processOK(response);
				// heatingProfile.setRunningProfile(true);
				// startButton.setText("<html><font size=\"20\">Stop Heating</font></html>");
				toggleRunningMenuItems(false);

				// startCommandReceivedOK
				// JOptionPane.showMessageDialog(frame, "Set Current Value OK", "Control Message",
				//		JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Set Current Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void setRelayValue(int relayValue) {

		logger.info("set the relay value: " + relayValue);

		boolean success = sendCommand("setRelayValue#" + relayValue, false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isOk()) {
				processOK(response);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Set Relay Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public String processError(String errorString) {

		logger.info("command finished");
		logger.info("completed response: " + errorString);
		String responseNumber = "";

		// parse response
		int spaceIndex = errorString.lastIndexOf(' ');
		if (spaceIndex != -1) {
			String message = errorString.substring(spaceIndex);

			int responseNumberIndex = message.lastIndexOf('#');

			if (responseNumberIndex != -1) {
				responseNumber = message.substring(responseNumberIndex);
			} else {
				return "";
			}

			errorMessageString = message.substring(responseNumberIndex);
		} else {
			return "";
		}

		return errorMessageString;
	}

	public String processOK(String okString) {

		logger.info("command finished");
		logger.info("completed response: " + okString);

		String okMessageString = "";

		int okIndex = okString.lastIndexOf("OK");

		if (okIndex != -1) {
			String message = okString.substring(0, okIndex);
			int responseNumberIndex = message.lastIndexOf('#');
			String responseNumberString = "";
			if (responseNumberIndex != -1) {
				responseNumberString = message.substring(responseNumberIndex + 1);
				logger.info("responseNumberString: " + responseNumberString);
				responseValue = new Double(responseNumberString).doubleValue();
				okMessageString = message.substring(0, responseNumberIndex);
			}
		} else {
			okMessageString = "";
		}

		return okMessageString;
	}

	public void setBoundsValuesNoDialog() {

		logger.info("set the bounds values - no dialog");
		serialCommunications.setPortErrorDialog(false);

		boolean success = sendCommand("setBoundsValues#" + controlBounds.getFanTemperatureOnOffValue() + ","
				+ controlBounds.getTemperatureWarningValue() + "," + controlBounds.getTemperatureShutdownValue() + ","
				+ controlBounds.getStageTimeLimitValue() + "," + controlBounds.getCurrentLimitValue() + "," +
				controlBounds.getVacuumLimitValue() + ","
				+ controlBounds.getKpValue() + "," + controlBounds.getKiValue() + "," + controlBounds.getKdValue(), false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isOk()) {
				processOK(response);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Set Bounds Command Error", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setBoundsValues() {

		logger.info("set the bounds values");
		serialCommunications.setPortErrorDialog(false);

		boolean success = sendCommand("setBoundsValues#" + controlBounds.getFanTemperatureOnOffValue() + ","
				+ controlBounds.getTemperatureWarningValue() + "," + controlBounds.getTemperatureShutdownValue() + ","
				+ controlBounds.getStageTimeLimitValue() + "," + controlBounds.getCurrentLimitValue() + ","
				+ controlBounds.getVacuumLimitValue() + ","
				+ controlBounds.getKpValue() + "," + controlBounds.getKiValue() + "," + controlBounds.getKdValue(), false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Set Bounds Values OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Set Bounds Values Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	boolean toggleRunningMenuItems(boolean state) {

		// toggle menus
		/*
		 * menu.getMenu(0).getMenuComponent(0).setEnabled(state);
		 * menu.getMenu(0).getMenuComponent(1).setEnabled(state);
		 * menu.getMenu(0).getMenuComponent(2).setEnabled(state);
		 * menu.getMenu(0).getMenuComponent(3).setEnabled(state);
		 * menu.getMenu(0).getMenuComponent(4).setEnabled(state);
		 * menu.getMenu(0).getMenuComponent(5).setEnabled(state);
		 * menu.getMenu(0).getMenuComponent(9).setEnabled(state);
		 * 
		 * menu.getMenu(2).getMenuComponent(2).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(4).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(6).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(7).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(8).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(10).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(11).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(12).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(13).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(14).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(15).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(16).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(17).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(19).setEnabled(state); //
		 * menu.getMenu(2).getMenuComponent(20).setEnabled(state); //
		 * menu.getMenu(2).getMenuComponent(21).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(22).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(23).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(25).setEnabled(state);
		 * menu.getMenu(2).getMenuComponent(26).setEnabled(state);
		 */

		return true;
	}
	
	public void resetState() {
		
		logger.info("trying to reset the controller state.");

		serialCommunications.setProfileRunningImportant(true);

		boolean success = sendCommand("resetState", true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isOk()) {
				processOK(response);

				logger.info("successfully reset the controller state");

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "Reset State Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
