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

	public void setystemState(SystemState systemState) {
		this.systemState = systemState;
	}

	public void readFan() {
		logger.info("read the fan value.");
		// serialCommunications.setProfileRunningImportant(false);
		// serialCommunications.commandResponse("readFanValue");
		boolean success = sendCommand("readFanValue", false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("readFanCommandReceived") == 0) {

					// readCurrentCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Fan Read Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("fanValue") == 0) {

					// currentValueError
					JOptionPane.showMessageDialog(frame, "Fan Read Response Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Read Fan OK - " + getResponseValue(), "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setFan(boolean fanValue) {

		logger.info("set the fan value: " + fanValue);
		setFanOn(fanValue);
		// serialCommunications.setProfileRunningImportant(true);
		// serialCommunications.commandResponse("setFanValue#" + (fanValue ? 1 : 0));

		boolean success = sendCommand("setFanValue#" + (fanValue ? 1 : 0), true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setFanCommandReceived") == 0) {

					// setFilamentCommandReceived#Error
					JOptionPane.showMessageDialog(frame, "Set Fan Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setFanValueReceived") == 0) {

					// setFanError
					JOptionPane.showMessageDialog(frame, "Set Fan Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				}

			} else if (serialCommunications.isOk()) {

				setFanOn(true);
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Set Fan Value OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	public void setFilamentsEnable(boolean filamentsEnableValue) {

		logger.info("set the filaments enable value: " + filamentsEnableValue);

		boolean success = sendCommand("setFilamentsEnableValue#" + (filamentsEnableValue ? 1 : 0), true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setFilamentsEnableCommandReceived") == 0) {

					// setFilamentCommandReceived#Error
					JOptionPane.showMessageDialog(frame, "Set Filaments Enable Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setFilamentsEnableValueReceived") == 0) {

					// setFanError
					JOptionPane.showMessageDialog(frame, "Set Filaments Enable Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				}

			} else if (serialCommunications.isOk()) {

				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Set Filaments Enable Control Value OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public boolean isFanOn() {
		return fanOn;
	}

	public void setFanOn(boolean fanOn) {
		this.fanOn = fanOn;
	}

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
		if (serialCommunications.isPortOpen()) {
			serialCommunications.setProfileRunningImportant(runningImportant);
			serialCommunications.commandResponse(command);
			return true;
		} else {
			return false;
		}
	}

	public void readPowerSupply() {
		logger.info("read the powerSupply value.");
		// serialCommunications.setProfileRunningImportant(false);
		// serialCommunications.commandResponse("readpowerSupplyValue");
		boolean success = sendCommand("readPowerSupplyValue", false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("readPowerSupplyCommandReceived") == 0) {

					// readCurrentCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Power Supply Read Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("powerSupplyValue") == 0) {

					// currentValueError
					JOptionPane.showMessageDialog(frame, "powerSupply Read Response Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Read Power Supply OK - " + getResponseValue(), "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	// send profile
	public void sendHeatingProfile() {

		System.out.println("sending heating profile");
		// serialCommunications.setProfileRunningImportant(true);
		// serialCommunications.commandResponse("sendProfile#" +
		// heatingProfile.getHeatingProfileCommand());

		boolean success = sendCommand("sendProfile#" + heatingProfile.getHeatingProfileCommand(), false);
		
		System.out.println("profile sent: " + heatingProfile.getHeatingProfileCommand() );

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("sendHeatingProfileCommandReceived") == 0) {

					// sendHeatingProfileCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Send Heating Profile Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("sendHeatingProfileValueReceived") == 0) {

					// sendHeatingProfileCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Send Heating Profile Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				JOptionPane.showMessageDialog(frame, "Sent Heating Profile OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	// send profile
	public void showHeatingProfile() {

		logger.info("show heating profile");

		boolean success = sendCommand("showHeatingProfile", false);
		// serialCommunications.setProfileRunningImportant(false);
		// serialCommunications.commandResponse("showHeatingProfile");

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("showHeatingProfileCommandReceived") == 0) {

					// showHeatingProfileCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Show Heating Profile Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("showHeatingProfileValueReceived") == 0) {

					// showHeatingProfileCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Show Heating Profile Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Show Heating Profile OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void startRun() {

		System.out.println("starting to run the heating profile");

		// serialCommunications.commandResponse("sendProfile#" +
		// heatingProfile.getHeatingProfileCommand());

		boolean success = sendCommand("sendProfile#" + heatingProfile.getHeatingProfileCommand(), false);
		
		System.out.println("profile sent: " + heatingProfile.getHeatingProfileCommand() );

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("sendHeatingProfileCommandReceived") == 0) {

					// sendHeatingProfileCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Send Heating Profile Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("sendHeatingProfileValueReceived") == 0) {

					// sendHeatingProfileCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Send Heating Profile Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				serialCommunications.setProfileRunningImportant(false);
				serialCommunications.commandResponse("startProfile");
				response = serialCommunications.getResponse();
				serialCommunications.resetCompleteResponse();
				if (serialCommunications.isError()) {
					processError(response);

					if (errorMessageString.compareTo("startCommandReceived") == 0) {

						// startCommandReceivedError
						JOptionPane.showMessageDialog(frame, "Start Command Error", "Control Message",
								JOptionPane.ERROR_MESSAGE);
					}

				} else if (serialCommunications.isOk()) {
					processOK(response);
					heatingProfile.setRunningProfile(true);
					heatingProfile.setSelectedStage(0);
					toggleRunningMenuItems(false);

					heatingTimerPanel.setTimeFromHeatingProfile();
					heatingTimerPanel.start();

					heatingTimerPanelStage.setTimeFromHeatingProfile();
					heatingTimerPanelStage.start();

					// startCommandReceivedOK
					/*
					 * JOptionPane.showMessageDialog(frame, "Start Command OK", "Control Message",
					 * JOptionPane.INFORMATION_MESSAGE);
					 */
				} else {
					logger.info("indeterminate state");

					// Unknown Error
					JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {

				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}

	public void stopRun() {

		logger.info("stopping running the heating profile");
		// serialCommunications.setProfileRunningImportant(false);
		// serialCommunications.commandResponse("abortProfile");

		boolean success = sendCommand("abortProfile", false);

		if (success) {

			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("abortCommandReceived") == 0) {

					// abortCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Stop Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);
				toggleRunningMenuItems(true);
				// JMenuItem powerSupplyMenuItem = (JMenuItem) menu.getMenu(3).getMenuComponent(9);
				// powerSupplyMenuItem.setText("Turn Off Additional Power Supply");

				startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");

				// startCommandReceivedOK
				//JOptionPane.showMessageDialog(frame, "Stop Command OK", "Control Message",
				//		JOptionPane.INFORMATION_MESSAGE);
			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void estimateFilaments() {

		logger.info("estimating the number of filaments");
		// serialCommunications.setProfileRunningImportant(true);
		// serialCommunications.commandResponse("estimateFilaments");

		boolean success = sendCommand("estimateFilaments", true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("estimateFilaments") == 0) {

					// estimateFilamentsReceivedError
					JOptionPane.showMessageDialog(frame, "Estimate Filaments Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("estimateFilamentsValue") == 0) {

					// estimateFilamentsReceivedError
					JOptionPane.showMessageDialog(frame, "Estimate Filaments Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("currentOverLimitError") == 0) {

					JOptionPane.showMessageDialog(frame, "Current Over Limit Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);
				// heatingProfile.setRunningProfile(true);
				// startButton.setText("<html><font size=\"20\">Stop Heating</font></html>");
				// toggleRunningMenuItems(false);
				// systemState.setFilamentsSet(true);
				logger.info("set filament state: " + systemState.isFilamentsSet());

				/*
				 * // startCommandReceivedOK int estimateFilaments = (int) getResponseValue();
				 * 
				 * // set filaments dialog box SetNumberOfFilamentsDialog
				 * setNumberOfFilamentsDialog = new SetNumberOfFilamentsDialog(frame);
				 * 
				 * setNumberOfFilamentsDialog.showDialog(estimateFilaments); int filaments =
				 * setNumberOfFilamentsDialog.getNumberOfFilementsValue();
				 * 
				 * setFilaments(filaments);
				 */
				// JOptionPane.showMessageDialog(frame, "Estimate Filaments OK - " +, "Control
				// Message",
				// JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void readCurrentValue() {

		logger.info("read the current value.");
		serialCommunications.setProfileRunningImportant(false);
		serialCommunications.commandResponse("readCurrentValue");

		boolean success = sendCommand("readCurrentValue", false);

		if (success) {

			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("readCurrentCommandReceived") == 0) {

					// readCurrentCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Current Read Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("currentValue") == 0) {

					// currentValueError
					JOptionPane.showMessageDialog(frame, "Current Read Response Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Read Current OK - " + getResponseValue() + " mA",
						"Control Message", JOptionPane.INFORMATION_MESSAGE);
			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public double getResponseValue() {
		return this.responseValue;
	}

	public void readVacuumValue() {

		logger.info("read the vacuum value.");
		// serialCommunications.setProfileRunningImportant(false);
		// serialCommunications.commandResponse("readVacuumValue");

		boolean success = sendCommand("readVacuumValue", false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("readVacuumCommandReceived") == 0) {

					// readVacuumCommandReceivedError
					JOptionPane.showMessageDialog(frame, "Vacuum Read Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("vacuumValue") == 0) {

					// vacuumValueError
					JOptionPane.showMessageDialog(frame, "Vaccum Read Response Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				}
			} else if (serialCommunications.isOk()) {
				processOK(response);

				// read Vacuum Value
				JOptionPane.showMessageDialog(frame, "Read Vacuum OK - " + getResponseValue() + " mT",
						"Control Message", JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void readTempValue() {

		logger.info("read the temperature value.");
		// serialCommunications.setProfileRunningImportant(false);
		// serialCommunications.commandResponse("readTempValue");

		boolean success = sendCommand("readVacuumValue", false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("readTempValueReceived") == 0) {

					if (errorMessageString.compareTo("readTempCommandReceived") == 0) {

						// read Temp Value
						JOptionPane.showMessageDialog(frame, "Temperature Read Command Error", "Control Message",
								JOptionPane.ERROR_MESSAGE);

					} else if (errorMessageString.compareTo("tempValue") == 0) {

						// tempValue
						JOptionPane.showMessageDialog(frame, "Read Temperature OK - getResponseValue()",
								"Control Message", JOptionPane.ERROR_MESSAGE);

					}
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Read Temperature OK - " + getResponseValue() + " C",
						"Control Message", JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setFilaments(int filamentsValue) {

		logger.info("set the number of filaments: " + filamentsValue);
		// serialCommunications.setProfileRunningImportant(true);
		// serialCommunications.commandResponse("setFilamentValue#" + filamentsValue);

		boolean success = sendCommand("setFilamentValue#" + filamentsValue, true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setFilamentCommandReceived") == 0) {

					// setFilamentCommandReceived#Error
					JOptionPane.showMessageDialog(frame, "Set Filaments Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setFilamentValueReceived") == 0) {

					// setFilamentsError
					JOptionPane.showMessageDialog(frame, "Set Filaments Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				systemState.setFilamentsSet(true);
				logger.info("set filament state: " + systemState.isFilamentsSet());

				// startCommandReceivedOK
				//JOptionPane.showMessageDialog(frame, "Set Filaments Value OK", "Control Message",
				//		JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setPowerSupply(boolean powerSupplyValue) {

		logger.info("set the power supply value: " + powerSupplyValue);
		// systemState.setPowerSupplyState(powerSupplyValue);
		// serialCommunications.setProfileRunningImportant(true);
		// serialCommunications.commandResponse("setpowerSupplyValue#" +
		// (powerSupplyValue ? 1 : 0));

		boolean success = sendCommand("setPowerSupplyValue#" + (powerSupplyValue ? 1 : 0), true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setPowerSupplyCommandReceived") == 0) {

					// setFilamentCommandReceived#Error
					JOptionPane.showMessageDialog(frame, "Set powerSupply Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setPowerSupplyValueReceived") == 0) {

					// setFilamentsError
					JOptionPane.showMessageDialog(frame, "Set Power Supply Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				}

			} else if (serialCommunications.isOk()) {

				// systemState.setPowerSupplyState(true);
				processOK(response);

				// startCommandReceivedOK
				// JOptionPane.showMessageDialog(frame, "Set Power Supply Value OK", "Control Message",
				//		JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setCurrentValue(int currentValue) {

		logger.info("set the current value: " + currentValue);
		// serialCommunications.setProfileRunningImportant(true); // NEEDS TO BE
		// EVERYWHERE
		// serialCommunications.commandResponse("setCurrentValue#" + currentValue);

		boolean success = sendCommand("setCurrentValue#" + currentValue, true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setCurrentCommandReceived") == 0) {

					// setCurrentError
					JOptionPane.showMessageDialog(frame, "Set Current Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setCurrentValueReceived") == 0) {

					// setCurrentError
					JOptionPane.showMessageDialog(frame, "Set Current Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("currentOverLimit") == 0) {

					// setCurrentError
					JOptionPane.showMessageDialog(frame, "Current Set Limit Exceeded", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
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
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setRelayValue(int relayValue) {

		logger.info("set the relay value: " + relayValue);
		// serialCommunications.setProfileRunningImportant(true); // NEEDS TO BE
		// EVERYWHERE
		// serialCommunications.commandResponse("setRelayValue#" + relayValue);

		boolean success = sendCommand("setRelayValue#" + relayValue, false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setRelayCommandReceived") == 0) {

					// setRelayError
					JOptionPane.showMessageDialog(frame, "Set Relay Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setRelayValueReceived") == 0) {

					// setRelayError
					JOptionPane.showMessageDialog(frame, "Set Relay Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				if (relayValue > 0) {
					// heatingProfile.setRunningProfile(true);
					//startButton.setText("<html><font size=\"20\">Abort Heating</font></html>");
					// toggleRunningMenuItems(false);
				} else {
					//heatingProfile.setRunningProfile(false);
					//startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
					// toggleRunningMenuItems(true);
				}

				// startCommandReceivedOK
				// JOptionPane.showMessageDialog(frame, "Set Relay Value OK", "Control Message",
				//		JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void readRelayValue() {

		logger.info("read the relay value.");
		// serialCommunications.setProfileRunningImportant(false);
		// serialCommunications.commandResponse("readRelayValue");

		boolean success = sendCommand("readRelayValue", true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("readRelayValueReceived") == 0) {

					if (errorMessageString.compareTo("readRelayCommandReceived") == 0) {

						JOptionPane.showMessageDialog(frame, "Relay Read Command Error", "Control Message",
								JOptionPane.ERROR_MESSAGE);

					} else if (errorMessageString.compareTo("relayValue") == 0) {

						// tempValue
						JOptionPane.showMessageDialog(frame, "Read Relay OK - getResponseValue()", "Control Message",
								JOptionPane.ERROR_MESSAGE);
					}
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Read Relay OK - " + getResponseValue() + " %", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void readFilamentsValue() {

		logger.info("read the filaments value.");
		// serialCommunications.setProfileRunningImportant(false);
		// serialCommunications.commandResponse("readFilamentsValue");

		boolean success = sendCommand("readFilamentsValue", true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("readFilamentsValueReceived") == 0) {

					if (errorMessageString.compareTo("readFilamentsCommandReceived") == 0) {

						// read Temp Value
						JOptionPane.showMessageDialog(frame, "Filaments Read Command Error", "Control Message",
								JOptionPane.ERROR_MESSAGE);

					} else if (errorMessageString.compareTo("filamentsValue") == 0) {

						// tempValue
						JOptionPane.showMessageDialog(frame, "Read Filaments OK - " + getResponseValue(),
								"Control Message", JOptionPane.ERROR_MESSAGE);
					}
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Read Filaments OK - " + getResponseValue(), "Control Message",
						JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setKp(double KpValue) {

		logger.info("set PID Kp value: " + KpValue);
		serialCommunications.setProfileRunningImportant(false);

		boolean success = sendCommand("setKpValue#" + KpValue, true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setKpValueCommandReceived") == 0) {

					JOptionPane.showMessageDialog(frame, "Set Kp Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setKpValueReceived") == 0) {

					JOptionPane.showMessageDialog(frame, "Set Kp Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				JOptionPane.showMessageDialog(frame, "Set Kp Value OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setKi(double KiValue) {

		logger.info("set PID Ki value: " + KiValue);
		serialCommunications.setProfileRunningImportant(false);

		boolean success = sendCommand("setKiValue#" + KiValue, true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setKiValueCommandReceived") == 0) {

					JOptionPane.showMessageDialog(frame, "Set Ki Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setKiValueReceived") == 0) {

					JOptionPane.showMessageDialog(frame, "Set Ki Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Set Ki Value OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setKd(double KdValue) {

		logger.info("set PID Kd value: " + KdValue);
		serialCommunications.setProfileRunningImportant(false);

		boolean success = sendCommand("setKdValue#" + KdValue, true);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setKdValueCommandReceived") == 0) {

					JOptionPane.showMessageDialog(frame, "Set Kd Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setKdValueReceived") == 0) {

					JOptionPane.showMessageDialog(frame, "Set Kd Value Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Set Kd Value OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	// unsolicited error messages
	public void unsolicitedErrorMessages(String errorMessage) {

		// overtemp shutdown
		// overtemp warning
		// vacuum warning?
		// fan warning?
		// unknown error
		// profile

		String message = processError(errorMessage);
		if (message.compareTo("overtempwarning") == 0) {

			JOptionPane.showMessageDialog(frame, "Warning - The Temperature has exceeded the warning limit",
					"Control Message", JOptionPane.INFORMATION_MESSAGE);

		} else if (message.compareTo("overtempshutdown") == 0) {

			JOptionPane.showMessageDialog(frame, "Error - The Temperature has exceeded the shutdown limit",
					"Control Message", JOptionPane.INFORMATION_MESSAGE);
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

		/*
		 * if (errorMessageString.compareTo("startCommandReceived") == 0) {
		 * 
		 * // startCommandReceivedError JOptionPane.showMessageDialog(frame,
		 * "Start Command Error", "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("abortCommandReceived") == 0) {
		 * 
		 * // abortCommandReceivedError JOptionPane.showMessageDialog(frame,
		 * "Stop Command Error", "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("readCurrentCommandReceived") == 0) {
		 * 
		 * // readCurrentCommandReceivedError JOptionPane.showMessageDialog(frame,
		 * "Current Read Command Error", "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("currentValue") == 0) {
		 * 
		 * // currentValueError JOptionPane.showMessageDialog(frame,
		 * "Current Read Response Error", "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("readVacuumCommandReceived") == 0) {
		 * 
		 * // readVacuumCommandReceivedError JOptionPane.showMessageDialog(frame,
		 * "Vacuum Read Command Error", "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("vacuumValue") == 0) {
		 * 
		 * // vacuumValueError JOptionPane.showMessageDialog(frame,
		 * "Vaccum Read Response Error", "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("readTempCommandReceived") == 0) {
		 * 
		 * // setTempCommandReceived#Error JOptionPane.showMessageDialog(frame,
		 * "Temperature Read Command Error", "Control Message",
		 * JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("tempValue") == 0) {
		 * 
		 * // tempValueError JOptionPane.showMessageDialog(frame,
		 * "Temperature Read Response Error", "Control Message",
		 * JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("setRelayCommandReceived") == 0) {
		 * 
		 * // setRelayError JOptionPane.showMessageDialog(frame,
		 * "Set Relay Command Error", "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("setRelayValueReceived") == 0) {
		 * 
		 * // setRelayError JOptionPane.showMessageDialog(frame,
		 * "Set Relay Value Error", "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("estimateFilamentsCommandReceived")
		 * == 0) {
		 * 
		 * // estimateFilamentsReceivedError JOptionPane.showMessageDialog(frame,
		 * "Estimate Filaments Command Error", "Control Message",
		 * JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("estimateFilamentsValueReceived") ==
		 * 0) {
		 * 
		 * // estimateFilamentsReceivedError JOptionPane.showMessageDialog(frame,
		 * "Estimate Filaments Value Error", "Control Message",
		 * JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("setFilamentCommandReceived") == 0) {
		 * 
		 * // setFilamentCommandReceived#Error JOptionPane.showMessageDialog(frame,
		 * "Set Filaments Command Error", "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("setFilamentValueReceived") == 0) {
		 * 
		 * // setFilamentsError JOptionPane.showMessageDialog(frame,
		 * "Set Filaments Value Error", "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("showHeatingProfileCommandReceived")
		 * == 0) {
		 * 
		 * // showHeatingProfileCommandReceivedError
		 * JOptionPane.showMessageDialog(frame, "Show Heating Profile Command Error",
		 * "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * } else if (errorMessageString.compareTo("showHeatingProfileValueReceived") ==
		 * 0) {
		 * 
		 * // showHeatingProfileCommandReceivedError
		 * JOptionPane.showMessageDialog(frame, "Show Heating Profile Value Error",
		 * "Control Message", JOptionPane.ERROR_MESSAGE);
		 * 
		 * }
		 */
	}

	public String processOK(String okString) {

		logger.info("command finished");
		logger.info("completed response: " + okString);

		String okMessageString = "";

		// currentValue#1.23OK

		// parse response
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

	/*
	 * public void setBoundsValues() {
	 * setBoundsValues(controlBounds.getFanTemperatureOnOffValue(),
	 * controlBounds.getTemperatureWarningValue(),
	 * controlBounds.getTemperatureShutdownValue(),
	 * controlBounds.getStageTimeLimitValue(), controlBounds.getCurrentLimitValue()
	 * );
	 */

	public void setBoundsValuesNoDialog() {

		logger.info("set the bounds values - no dialog");
		serialCommunications.setPortErrorDialog(false);

		/*
		 * serialCommunications.setProfileRunningImportant(true);
		 * 
		 * serialCommunications.commandResponse("setBoundsValues#" +
		 * controlBounds.getFanTemperatureOnOffValue() + "," +
		 * controlBounds.getTemperatureWarningValue() + "," +
		 * controlBounds.getTemperatureShutdownValue() + "," +
		 * controlBounds.getStageTimeLimitValue() + "," +
		 * controlBounds.getCurrentLimitValue());
		 */

		boolean success = sendCommand("setBoundsValues#" + controlBounds.getFanTemperatureOnOffValue() + ","
				+ controlBounds.getTemperatureWarningValue() + "," + controlBounds.getTemperatureShutdownValue() + ","
				+ controlBounds.getStageTimeLimitValue() + "," + controlBounds.getCurrentLimitValue() + "," +
				controlBounds.getVacuumLimitValue() + ","
				+ controlBounds.getKpValue() + "," + controlBounds.getKiValue() + "," + controlBounds.getKdValue(), false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setBoundsCommandReceived") == 0) {

					JOptionPane.showMessageDialog(frame, "Set Bounds Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setBoundsValuesReceived") == 0) {

					// setFanOnError
					JOptionPane.showMessageDialog(frame, "Set Bounds Values Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setBoundsValues() {

		logger.info("set the bounds values");
		serialCommunications.setPortErrorDialog(false);

		/*
		 * serialCommunications.setProfileRunningImportant(true);
		 * 
		 * serialCommunications.commandResponse("setBoundsValues#" +
		 * controlBounds.getFanTemperatureOnOffValue() + "," +
		 * controlBounds.getTemperatureWarningValue() + "," +
		 * controlBounds.getTemperatureShutdownValue() + "," +
		 * controlBounds.getStageTimeLimitValue() + "," +
		 * controlBounds.getCurrentLimitValue());
		 */

		boolean success = sendCommand("setBoundsValues#" + controlBounds.getFanTemperatureOnOffValue() + ","
				+ controlBounds.getTemperatureWarningValue() + "," + controlBounds.getTemperatureShutdownValue() + ","
				+ controlBounds.getStageTimeLimitValue() + "," + controlBounds.getCurrentLimitValue() + ","
				+ controlBounds.getVacuumLimitValue() + ","
				+ controlBounds.getKpValue() + "," + controlBounds.getKiValue() + "," + controlBounds.getKdValue(), false);

		if (success) {
			String response = serialCommunications.getResponse();
			serialCommunications.resetCompleteResponse();
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("setBoundsCommandReceived") == 0) {

					JOptionPane.showMessageDialog(frame, "Set Bounds Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} else if (errorMessageString.compareTo("setBoundsValuesReceived") == 0) {

					// setFanOnError
					JOptionPane.showMessageDialog(frame, "Set Bounds Values Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);
				}

			} else if (serialCommunications.isOk()) {
				processOK(response);

				// startCommandReceivedOK
				JOptionPane.showMessageDialog(frame, "Set Bounds Values OK", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setFanOnBoundsValue(int powerSupplyOnValue) {

		logger.info("set the power supply on value: " + powerSupplyOnValue);
		serialCommunications.setProfileRunningImportant(true);
		serialCommunications.commandResponse("setFanOnValue#" + powerSupplyOnValue);
		String response = serialCommunications.getResponse();
		serialCommunications.resetCompleteResponse();
		if (serialCommunications.isError()) {
			processError(response);

			if (errorMessageString.compareTo("setPowerSupplyOnCommandReceived") == 0) {

				// setpowerSupplyOnError
				JOptionPane.showMessageDialog(frame, "Set PowerSupply On Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);

			} else if (errorMessageString.compareTo("setPowerSupplyOnValueReceived") == 0) {

				// setPowerSupplyOnError
				JOptionPane.showMessageDialog(frame, "Set PowerSupply On Value Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}

		} else if (serialCommunications.isOk()) {
			processOK(response);

			// startCommandReceivedOK
			JOptionPane.showMessageDialog(frame, "Set Power Supply On Value OK", "Control Message",
					JOptionPane.INFORMATION_MESSAGE);

		} else {
			logger.info("indeterminate state");

			// Unknown Error
			JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	public void setTempWarningBoundsValue(int tempWarningValue) {

		logger.info("set the temp warning value: " + tempWarningValue);
		serialCommunications.setProfileRunningImportant(true);
		serialCommunications.commandResponse("setTempWarningValue#" + tempWarningValue);
		String response = serialCommunications.getResponse();
		serialCommunications.resetCompleteResponse();
		if (serialCommunications.isError()) {
			processError(response);

			if (errorMessageString.compareTo("setTempWarningCommandReceived") == 0) {

				// setTempWarningError
				JOptionPane.showMessageDialog(frame, "Set Temp Warning Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);

			} else if (errorMessageString.compareTo("setTempWarningValueReceived") == 0) {

				// setTempWarningError
				JOptionPane.showMessageDialog(frame, "Set Temp Warning Value Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}

		} else if (serialCommunications.isOk()) {
			processOK(response);

			// startCommandReceivedOK
			JOptionPane.showMessageDialog(frame, "Set Temp Warning Value OK", "Control Message",
					JOptionPane.INFORMATION_MESSAGE);

		} else {
			logger.info("indeterminate state");

			// Unknown Error
			JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void setTempShutdownBoundsValue(int tempShutdownValue) {

		logger.info("set the temp warning value: " + tempShutdownValue);
		serialCommunications.setProfileRunningImportant(true);
		serialCommunications.commandResponse("setTempWarningValue#" + tempShutdownValue);
		String response = serialCommunications.getResponse();
		serialCommunications.resetCompleteResponse();
		if (serialCommunications.isError()) {
			processError(response);

			if (errorMessageString.compareTo("setTempShutdownCommandReceived") == 0) {

				// setTempWarningError
				JOptionPane.showMessageDialog(frame, "Set Temp Shutdown Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);

			} else if (errorMessageString.compareTo("setTempShutdownValueReceived") == 0) {

				// setTempWarningError
				JOptionPane.showMessageDialog(frame, "Set Temp Shutdown Value Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}

		} else if (serialCommunications.isOk()) {
			processOK(response);

			// startCommandReceivedOK
			JOptionPane.showMessageDialog(frame, "Set Temp Shutdown Value OK", "Control Message",
					JOptionPane.INFORMATION_MESSAGE);

		} else {
			logger.info("indeterminate state");

			// Unknown Error
			JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void setStageTimeLimitBoundsValue(int stageTimeLimitValue) {

		logger.info("set stage time limit value: " + stageTimeLimitValue);
		serialCommunications.setProfileRunningImportant(true);
		serialCommunications.commandResponse("setStageTimeLimitValue#" + stageTimeLimitValue);
		String response = serialCommunications.getResponse();
		serialCommunications.resetCompleteResponse();
		if (serialCommunications.isError()) {
			processError(response);

			if (errorMessageString.compareTo("setStageTimeLimitValueCommandReceived") == 0) {

				// setTempWarningError
				JOptionPane.showMessageDialog(frame, "Set stage time limit Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);

			} else if (errorMessageString.compareTo("setStageTimeLimitValueReceived") == 0) {

				// setTempWarningError
				JOptionPane.showMessageDialog(frame, "Set stage time limit Value Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}

		} else if (serialCommunications.isOk()) {
			processOK(response);

			// startCommandReceivedOK
			JOptionPane.showMessageDialog(frame, "Set stage time limit Value OK", "Control Message",
					JOptionPane.INFORMATION_MESSAGE);

		} else {
			logger.info("indeterminate state");

			// Unknown Error
			JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	public void setCurrentLimitBoundsValue(int currentLimitValue) {

		logger.info("set current limit value: " + currentLimitValue);
		serialCommunications.setProfileRunningImportant(true);
		serialCommunications.commandResponse("setCurrentLimitValue#" + currentLimitValue);
		String response = serialCommunications.getResponse();
		serialCommunications.resetCompleteResponse();
		if (serialCommunications.isError()) {
			processError(response);

			if (errorMessageString.compareTo("setCurrentLimitValueCommandReceived") == 0) {

				// setTempWarningError
				JOptionPane.showMessageDialog(frame, "Set current limit Command Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);

			} else if (errorMessageString.compareTo("setCurrentLimitValueReceived") == 0) {

				// setTempWarningError
				JOptionPane.showMessageDialog(frame, "Set current limit Value Error", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}

		} else if (serialCommunications.isOk()) {
			processOK(response);

			// startCommandReceivedOK
			JOptionPane.showMessageDialog(frame, "Set current limit Value OK", "Control Message",
					JOptionPane.INFORMATION_MESSAGE);

		} else {
			logger.info("indeterminate state");

			// Unknown Error
			JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
					JOptionPane.INFORMATION_MESSAGE);
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
			if (serialCommunications.isError()) {
				processError(response);

				if (errorMessageString.compareTo("resetStateCommandReceived") == 0) {

					JOptionPane.showMessageDialog(frame, "Reset State Command Error", "Control Message",
							JOptionPane.ERROR_MESSAGE);

				} 

			} else if (serialCommunications.isOk()) {
				processOK(response);

				logger.info("successfully reset the controller state");

			} else {
				logger.info("indeterminate state");

				// Unknown Error
				JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
}
