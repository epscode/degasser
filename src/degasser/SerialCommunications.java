package degasser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.awt.*;
import javax.swing.*;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import com.fazecast.jSerialComm.*;
import java.util.concurrent.*;

public class SerialCommunications implements SerialPortDataListener {

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	String serialPortString = "";
	JFrame frame;
	String filePath = "";
	boolean portOpen = false;
	LogPanel logPanel;
	GradientButton startButton;
	JMenuBar menu;
	String response = "";
	SerialPort serialPort;
	String completeResponse = "";
	String unsolicitedResponse = "";
	String commandName = "";
	HeatingProfile heatingProfile;
	StatusPanel statusPanel;
	double responseValue = 0.0;
	String statusLine = "";

	boolean finishedReponse = false;
	boolean waiting = false;
	boolean done = false;
	boolean error = false;
	boolean ok = false;
	boolean profileRunningImportant;
	boolean errorDialogNeeded = true;
	boolean continuingMessageNeeded = true;
	boolean continuingDialogNeeded = true;
	String errorMessageString = "";
	boolean firstRun = true;
	boolean portErrorDialog = true;
	ControlBounds controlBounds;
	SystemState systemState;
	TimerPanel heatingTimerPanel;
	TimerPanelStage heatingTimerPanelStage;
	HeatingProfilePanel heatingProfilePanel;

	public boolean isPortErrorDialog() {
		return portErrorDialog;
	}

	public void setPortErrorDialog(boolean portErrorDialog) {
		this.portErrorDialog = portErrorDialog;
	}

	public boolean isProfileRunningImportant() {
		return profileRunningImportant;
	}

	public void setProfileRunningImportant(boolean profileRunningImportant) {
		this.profileRunningImportant = profileRunningImportant;
	}

	public String getSerialPortString() {
		return serialPortString;
	}

	public void setSerialPortString(String serialPortString) {
		this.serialPortString = serialPortString;
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	// pass textbox and menu control object to the object and process commands,
	public SerialCommunications(JFrame frame, LogPanel logPanel, String tempPath, HeatingProfilePanel heatingProfilePanel,
			StatusPanel statusPanel, GradientButton startButton, JMenuBar menu, ControlBounds controlBounds, 
			SystemState systemState, TimerPanel heatingTimerPanel, TimerPanelStage heatingTimerPanelStage) {
		this.frame = frame;
		this.filePath = tempPath;
		this.logPanel = logPanel;
		this.heatingProfile = heatingProfile;
		this.statusPanel = statusPanel;
		this.startButton = startButton;
		this.menu = menu;
		this.systemState = systemState;
		this.controlBounds = controlBounds;
		this.heatingTimerPanel =  heatingTimerPanel;
		this.heatingTimerPanelStage = heatingTimerPanelStage;
		this.heatingProfilePanel = heatingProfilePanel;
		heatingProfile = this.heatingProfilePanel.heatingProfile;
	}

	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
			return;

		try {
			logger.info("got a serial event.");

			byte[] newData = new byte[serialPort.bytesAvailable()];

			int numRead = serialPort.readBytes(newData, newData.length);
			logger.info("Read " + numRead + " bytes.");
			response = (new String(newData, 0, newData.length));
			logger.info(response);

			completeResponse += response;

			// break up into last line

			// maybe ensure this has a line ending

			if (completeResponse.endsWith("\r\n")) {

				// if the port was just opened, consume everything until the first line is done
				if (firstRun) {
					statusLine = "";
					completeResponse = "";
					response = "";
					firstRun = false;
				}

				statusLine = getStatusLine(completeResponse);
				logger.info("status line: " + statusLine);

				// if (!statusLine.endsWith("Status") ) {
				// logPanel.addText(response);
				// }

				if (completeResponse.length() > 250) {
					logger.info("complete response is over 250 characters.");
					completeResponse = completeResponse.substring(100);
					logger.info("new response: " + completeResponse);
				}

				if (waiting) {
					// completeResponse += response; // removed for unsolicited responses
					logger.info("complete response: " + completeResponse);
					// wait for 'done' string
					// completeResponse = "OK\n";
					if (statusLine.endsWith("OK")) {

						// command received
						logger.info("command finished");
						logger.info("completed response: " + completeResponse);

						waiting = false;
						setOk(true);
						// reset completeResponse
						// completeResponse = "";
						done = true;
					}

					// CAN LOOK FOR END OF LINE CHARACTERS FOR CONTINUING RESPONSE
					if (statusLine.endsWith("Continuing")) {
						logger.info("response is an continuing message: " + statusLine);

						if (continuingMessageNeeded) {
							continuingMessages(statusLine);
						}
						completeResponse = "";
					}

					if (statusLine.endsWith("Error")) {
						// command received

						logger.info("response is an unsolicited error message: " + statusLine);
						if (errorDialogNeeded) {
							unsolicitedErrorMessages(statusLine);
						}
						completeResponse = "";
						// setError(true);

						done = true;
					}
				} else {
					// CAN LOOK FOR END OF LINE CHARACTERS FOR UNSOLICITED ERROR RESPONSE
					if (statusLine.endsWith("Error")) {
						logger.info("response is an unsolicited error message: " + statusLine);
						System.out.println("status line is an unsolicited error message: " + statusLine);
						// if (errorDialogNeeded) {
							System.out.println("getting to here?");
							unsolicitedErrorMessages(statusLine);
						// }
						completeResponse = "";
					}

					// CAN LOOK FOR END OF LINE CHARACTERS FOR CONTINUING RESPONSE
					if (statusLine.endsWith("Continuing")) {
						logger.info("response is an continuing message without waiting: " + statusLine);

						if (continuingMessageNeeded) {
							continuingMessages(statusLine);
						}
						completeResponse = "";
					}

					if (statusLine.endsWith("Status")) {
						// logger.info("status line is an unsolicited status message: " + statusLine);
						// logger.info("response is an unsolicited status message: " + completeResponse);
						System.out.println("status line is an unsolicited status message: " + statusLine);
						statusMessages(statusLine);
						completeResponse = "";
					}
					
					if (statusLine.endsWith("State")) {
						logger.info("status line is an unsolicited state message: " + statusLine);
						logger.info("response is an unsolicited state message: " + completeResponse);
						stateMessages(statusLine);
						completeResponse = "";
					}

				}
			}

		} catch (Exception error) {

			logger.info("from serialevent: " + error);

			String stackTraceString = ExceptionUtils.getStackTrace(error);
			logger.info(stackTraceString);
		}
	}

	String getStatusLine(String multipleLines) {

		String lines[] = multipleLines.split("\r\n");
		int okMessageLine = 0;
		int errorMessageLine = 0;
		int continuingMessageLine = 0;
		int statusMessageLine = 0;
		boolean okMessage = false;
		boolean errorMessage = false;
		boolean continuingMessage = false;
		boolean statusMessage = false;

		for (int lineNumber = 0; lineNumber < lines.length; lineNumber++) {
			logger.info("multiple lines: " + lineNumber + ": " + lines[lineNumber]);
			if (lines[lineNumber].endsWith("OK")) {
				okMessage = true;
				okMessageLine = lineNumber;
			} else if (lines[lineNumber].endsWith("Error")) {
				errorMessage = true;
				errorMessageLine = lineNumber;
			} else if (lines[lineNumber].endsWith("Continuing")) {
				continuingMessage = true;
				continuingMessageLine = lineNumber;
			} else if (lines[lineNumber].endsWith("Status")) {
				statusMessage = true;
				statusMessageLine = lineNumber;
			} else {
				// this is still messed up, getting duplicate lines
				if ((lines.length > 1) && ((lines.length - 1) > lineNumber)) {
					logPanel.addText(lines[lineNumber] + "\n");

				} else if (((lines.length - 1) == lineNumber) && (multipleLines.endsWith("\r\n"))) {
					logPanel.addText(lines[lineNumber] + "\n");
				}
			}
		}

		// got through lines

		if (okMessage) {
			logPanel.addText(lines[okMessageLine] + "\n");
			return (lines[okMessageLine]);
		} else if (errorMessage) {
			logPanel.addText(lines[errorMessageLine] + "\n");
			return (lines[errorMessageLine]);
		} else if (continuingMessage) {
			logPanel.addText(lines[continuingMessageLine] + "\n");
			return (lines[continuingMessageLine]);
		} else if (statusMessage) {
			return (lines[statusMessageLine]);
		}

		// logPanel.addText("are we here?");
		return lines[0];
	}

	String getResponse() {
		// return completeResponse;
		return statusLine;
	}

	void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	String getCommandName() {
		return commandName;
	}

	public boolean isPortOpen() {
		return portOpen;
	}

	public void setPortOpen(boolean portOpen) {
		this.portOpen = portOpen;
	}

	public boolean sendCommand(String command) {

		logger.info("trying to send command: " + command);
		command += "\r\n";
		logger.info("status of serial port: " + serialPort.isOpen());

		// clear system states
		setError(false);
		setOk(false);

		logPanel.addText(command);
		try {
			OutputStream outputStream = serialPort.getOutputStream();
			byte[] commandBytes = command.getBytes();
			outputStream.write(commandBytes);
			outputStream.close();
		} catch (IOException error) {
			JOptionPane.showMessageDialog(frame, "Serial Port Communications Error.", "Serial Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception error) {
			logger.error("send command failed. " + error);
			error.printStackTrace();
		}

		return true;
	}

	public boolean checkSerialPort() {
		boolean serialPortStatus = isPortOpen();

		if (!serialPortStatus) {
			JOptionPane.showMessageDialog(frame, "The Serial Port isn't open.", "Serial Error",
					JOptionPane.ERROR_MESSAGE);
		}

		return serialPortStatus;
	}

	public boolean commandResponse(String command) {

		// start timer and if time is up, error
		// otherwise if it hits the word OK, then do something based on which command
		// was given.
		// String response = "";
		long timeoutInSeconds = 5;
		done = false;
		waiting = true;
		long timeValue = 0;

		if (!isPortErrorDialog()) {

			if (isProfileRunningImportant()) {
				logger.info("running with profile check is important");
				if (!sendCommandWithRunningCheck(command, heatingProfile)) {
					logger.info("send command with running check failed");
					setError(true);
					return false;
				}
			} else {
				logger.info("running with profile check is NOT important");
				if (!sendCommandWithPortCheck(command)) {
					logger.info("send command with port check failed");
					setError(true);
					return false;
				}
			}

		} else {
			logger.info("send command with no port message");
			sendCommandNoPortMessage(command);
			setPortErrorDialog(true);
		}

		long startTime = System.currentTimeMillis() / 1000;

		while (!done) {
			try {
				Thread.sleep(100);
			} catch (Exception error) {
				logger.info("couldn't sleep run: " + error);
				setError(true);
			}

			// something like this
			long currentTime = System.currentTimeMillis() / 1000;
			if ((currentTime - startTime) >= timeoutInSeconds) {
				logger.info("command timed out");
				break;
			}
		}

		// completeResponse = "";
		logger.info("done with command in command response");

		return true;
	}

	public void resetCompleteResponse() {
		completeResponse = "";
	}

	public boolean openPort() throws Exception {

		boolean success = false;

		logger.info("serial port to open: " + serialPortString);
		try {
			serialPortString = readSerialPortFile();
			serialPort = SerialPort.getCommPort("/dev/" + serialPortString);
			success = serialPort.openPort();
			logger.info("serial port open success: " + success);

			if (success) {
				/*
				 * JOptionPane.showMessageDialog(null, "The Serial Port is opened.",
				 * "Serial Port Message", JOptionPane.INFORMATION_MESSAGE);
				 */

				// startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
				// heatingProfile.setRunningProfile(false);
				// toggleRunningMenuItems(true);

				serialPort.setBaudRate(115200);
				setPortOpen(true);
				serialPort.addDataListener(this);
				logPanel.clearLogs();
				logPanel.addText("port opened\n");

			} else {

				JOptionPane.showMessageDialog(frame, "The Serial Port did not open.", "Serial Message",
						JOptionPane.ERROR_MESSAGE);

				logger.info("the serial port didn't open.");

				done = true;

				return success;
			}
		} catch (Exception error) {
			logger.error("trying to open the serial port didn't work: " + error);

			throw new Exception(error);
		}
		return success;
	}

	boolean closePort() {

		boolean success = false;

		try {
			success = serialPort.closePort();
			setPortOpen(false);
		} catch (Exception error) {
			logger.error("couldn't close serial port. " + error);

			JOptionPane.showMessageDialog(frame, "Couldn't close serial port.\n" + error + "\n" + "Restart Program",
					"Serial Error", JOptionPane.ERROR_MESSAGE);
		}
		return success;
	}

	public String readSerialPortFile() {

		String comPortString = "";
		String serialPortFilename = filePath + "serialPortFile.txt";

		try {
			logger.info("trying to read the serial port file: " + serialPortFilename);

			BufferedReader in = new BufferedReader(new FileReader(serialPortFilename));

			comPortString = in.readLine();
			logger.info("comPortString as read from serial port file: " + comPortString);
			serialPortString = comPortString;

		} catch (Exception error) {
			logger.error("error reading the serialPortFilename file." + error);

			JOptionPane.showMessageDialog(frame, "There was a problem reading from the serial port file.",
					"File Read Error", JOptionPane.ERROR_MESSAGE);
		}
		return comPortString;
	}

	public void writeSerialPortFile(String selectedComPort) {

		String serialPortFilename = filePath + "serialPortFile.txt";

		try {
			logger.info("trying to write the serial port file: " + serialPortFilename);

			FileWriter portFile = new FileWriter(serialPortFilename, false);
			PrintWriter portWriter = new PrintWriter(portFile);

			logger.debug("selected com port from writeSerialPortFile: " + selectedComPort);

			portWriter.println(selectedComPort);

			portFile.close();

		} catch (Exception error) {
			logger.error("error writing the serial port file." + error);

			JOptionPane.showMessageDialog(frame, "There was a problem writing to the serial port file.",
					"File Write Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean sendCommandWithPortCheck(String command) {

		logger.info("send command with running check: " + command);

		logger.info("state of serial port open: " + isPortOpen());
		// need some error checking here
		if (!isPortOpen()) {

			logger.debug("serial port is closed. trying to open.");
			try {
				readSerialPortFile();
				if (!openPort()) {
					setError(true);
					return false;
				} else {
					TimeUnit.SECONDS.sleep(1);
				}
				logger.info("send a blank to try to clear the input buffer.");

				// clear the buffer
				sendCommand("");

			} catch (Exception error) {
				logger.info("error opening the serial port");

				JOptionPane.showMessageDialog(frame, "There was a problem opening the serial port", "Control Message",
						JOptionPane.ERROR_MESSAGE);

				setError(true);
				return false;
			}
		}

		setCommandName(command);
		boolean success = sendCommand(command);

		return success;
	}

	public boolean sendCommandNoPortMessage(String command) {

		logger.info("state of serial port open: " + isPortOpen());
		logger.info("send command - no port message");
		// need some error checking here
		if (!isPortOpen()) {

			logger.debug("serial port is closed. trying to open.");
			try {
				readSerialPortFile();
				if (!openPort()) {

					setError(true);
					return false;
				} else {
					TimeUnit.SECONDS.sleep(1);
				}
			} catch (Exception error) {

				logger.info("error opening the serial port");

				setError(true);
				return false;
			}
		}

		setCommandName(command);
		boolean success = sendCommand(command);

		return success;

	}

	public boolean sendCommandWithRunningCheck(String command, HeatingProfile heatingProfile) {

		logger.info("send command with running check: " + command);
		logger.info("state of running: " + heatingProfile.isRunningProfile());

		if (heatingProfile.isRunningProfile() == false) {

			logger.info("state of serial port open: " + isPortOpen());
			// need some error checking here
			if (!isPortOpen()) {

				logger.debug("serial port is closed. trying to open.");
				try {
					readSerialPortFile();
					if (!openPort()) {

						setError(true);
						return false;
					} else {
						TimeUnit.SECONDS.sleep(1);
					}
				} catch (Exception error) {

					logger.info("error opening the serial port");

					JOptionPane.showMessageDialog(frame, "There was a problem opening the serial port",
							"Control Message", JOptionPane.ERROR_MESSAGE);

					setError(true);
					return false;
				}
			}

			setCommandName(command);
			boolean success = sendCommand(command);

			return success;

		} else {
			JOptionPane.showMessageDialog(frame, "Profile is already running", "Control Message",
					JOptionPane.ERROR_MESSAGE);

			setError(true);
			return false;
		}
	}
	
	public void stateMessages(String stateMessage) {
		String message = processState(stateMessage);
		
		if (stateMessage.compareTo("aborted") == 0) {
			startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
			
			heatingProfile.setRunningProfile(false);
			
			toggleRunningMenuItems(true);
			
			heatingTimerPanel.cancelTimer();
			heatingTimerPanelStage.cancelTimer();
			
			// JMenuItem powerSupplyMenuItem =  (JMenuItem) menu.getMenu(3).getMenuComponent(9);
			// powerSupplyMenuItem.setText("Turn On Additional Power Supply");
		} 
	}

	public String processError(String errorString) {

		String errorMessageString = "";
		logger.info("error response string: " + errorString);

		int errorIndex = errorString.lastIndexOf("Error");
		if (errorIndex != -1) {
			errorString = errorString.substring(0, errorIndex);
		} else {
			logger.info("message should end in error");
			return errorString;
		}

		// parse response
		int spaceIndex = errorString.lastIndexOf(' ');
		int returnIndex = errorString.lastIndexOf("\r\n");
		if (spaceIndex != -1) {
			String message = errorString.substring(spaceIndex);
			/*
			 * int responseNumberIndex = message.lastIndexOf('#'); String responseNumber =
			 * ""; if (responseNumberIndex != -1) { responseNumber =
			 * message.substring(responseNumberIndex); errorMessageString =
			 * message.substring(responseNumberIndex); }
			 */
			logger.info("message had a space at beginning.");
			return message;
		} else if (returnIndex != -1) {
			String message = errorString.substring(returnIndex);
			logger.info("message had a linebreak at beginning.");
			return message;
		}

		logger.info("message didn't have a linebreak or space");
		// System.out.println("error message string after parsing");
		return errorString;
	}
	
	public String processState(String stateString) {

		String stateMessageString = "";
		logger.info("state response string: " + stateString);

		int stateIndex = stateString.lastIndexOf("State");
		if (stateIndex != -1) {
			stateString = stateString.substring(0, stateIndex);
		} else {
			logger.info("message should end in state");
			return stateString;
		}

		// parse response
		int spaceIndex = stateString.lastIndexOf(' ');
		int returnIndex = stateString.lastIndexOf("\r\n");
		if (spaceIndex != -1) {
			String message = stateString.substring(0, spaceIndex);
			/*
			 * int responseNumberIndex = message.lastIndexOf('#'); String responseNumber =
			 * ""; if (responseNumberIndex != -1) { responseNumber =
			 * message.substring(responseNumberIndex); errorMessageString =
			 * message.substring(responseNumberIndex); }
			 */
			logger.info("message had a space at beginning.");

			int responseNumberIndex = message.lastIndexOf('#');
			String responseNumberString = "";
			if (responseNumberIndex != -1) {
				responseNumberString = message.substring(responseNumberIndex + 1);
				logger.info("responseNumberString: " + responseNumberString);
				responseValue = new Double(responseNumberString).doubleValue();
			}

			return message;
		} else if (returnIndex != -1) {
			String message = stateString.substring(returnIndex);
			logger.info("message had a linebreak at beginning.");
			return message;
		}

		logger.info("message didn't have a linebreak or space");
		
		int responseNumberIndex = stateString.lastIndexOf('#');
		String message = stateString.substring(0, responseNumberIndex);
		
		logger.info("message: " +  message);
		logger.info("stateString: " + stateString);
		logger.info("responseNumberIndex: " +  responseNumberIndex);
		
		String responseNumberString = "";
		if (responseNumberIndex != -1) {
			responseNumberString = stateString.substring(responseNumberIndex + 1);
			logger.info("responseNumberString: " + responseNumberString);
			responseValue = new Double(responseNumberString).doubleValue();
		}
		
		return message;
	}

	public String processContinuing(String continuingString) {

		String continuingMessageString = "";
		logger.info("continuing response string: " + continuingString);

		int continuingIndex = continuingString.lastIndexOf("Continuing");
		if (continuingIndex != -1) {
			continuingString = continuingString.substring(0, continuingIndex);
		} else {
			logger.info("message should end in continuing");
			return continuingString;
		}

		// parse response
		int spaceIndex = continuingString.lastIndexOf(' ');
		int returnIndex = continuingString.lastIndexOf("\r\n");
		if (spaceIndex != -1) {
			String message = continuingString.substring(0, spaceIndex);
			/*
			 * int responseNumberIndex = message.lastIndexOf('#'); String responseNumber =
			 * ""; if (responseNumberIndex != -1) { responseNumber =
			 * message.substring(responseNumberIndex); errorMessageString =
			 * message.substring(responseNumberIndex); }
			 */
			logger.info("message had a space at beginning.");

			int responseNumberIndex = message.lastIndexOf('#');
			String responseNumberString = "";
			if (responseNumberIndex != -1) {
				responseNumberString = message.substring(responseNumberIndex + 1);
				logger.info("responseNumberString: " + responseNumberString);
				responseValue = new Double(responseNumberString).doubleValue();
			}

			return message;
		} else if (returnIndex != -1) {
			String message = continuingString.substring(returnIndex);
			logger.info("message had a linebreak at beginning.");
			return message;
		}

		logger.info("message didn't have a linebreak or space");
		
		int responseNumberIndex = continuingString.lastIndexOf('#');
		String message = continuingString.substring(0, responseNumberIndex);
		
		logger.info("message: " +  message);
		logger.info("continuingString: " + continuingString);
		logger.info("responseNumberIndex: " +  responseNumberIndex);
		
		String responseNumberString = "";
		if (responseNumberIndex != -1) {
			responseNumberString = continuingString.substring(responseNumberIndex + 1);
			logger.info("responseNumberString: " + responseNumberString);
			responseValue = new Double(responseNumberString).doubleValue();
		}
		
		return message;
	}

	public String processStatus(String okString) {

		logger.info("command finished");
		logger.info("completed response: " + okString);

		String okMessageString = "";

		// currentValue#1.23OK

		// parse response
		int okIndex = okString.lastIndexOf("Status");

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

	// continuing messages
	public void statusMessages(String statusMessage) {

		System.out.println("statusMessages function: " + statusMessage);

		// parse response
		int okIndex = statusMessage.lastIndexOf("Status");

		if (okIndex != -1) {
			statusMessage = statusMessage.substring(0, okIndex);
		}

		System.out.println("removed status: " + statusMessage);

		String messages[] = statusMessage.split(" ");

		System.out.println("messages length:" + messages.length);
		for (int messageNumber = 0; messageNumber < messages.length; messageNumber++) {
			System.out.println(messageNumber + ": " + messages[messageNumber]);

			// pick apart message and value
			String statusParts[] = messages[messageNumber].split("#");
			String statusCommand = statusParts[0];
			String statusValueString = statusParts[1];

			System.out.println("status command: " + statusCommand + " status Value: " + statusValueString);

			double statusValue = new Double(statusValueString).doubleValue();

			// parse response and update field
			if (statusCommand.compareTo("stage") == 0) {
				statusPanel.statusRow.setStage( (int) statusValue);
				statusPanel.statusTableModel.fireTableCellUpdated(0, 0);
			} else if (statusCommand.compareTo("filaments") == 0) {
				
					// JMenuItem enableFilamentsMenuItem =  (JMenuItem) menu.getMenu(2).getMenuComponent(6);
					
					if (statusValue < 0) {
						systemState.setFilamentsSet(false);
						// enableFilamentsMenuItem.setText("Enable Filaments");
					} else if (statusValue > 0) {
						systemState.setFilamentsSet(true);				
						// enableFilamentsMenuItem.setText("Disable Filaments");
					} 		
					
					// System.out.println("filament value: " + statusValue);
					systemState.setFilaments( (int) statusValue);
					statusPanel.statusRow.setFilaments((int) statusValue);
					statusPanel.statusTableModel.fireTableCellUpdated(0, 1);
				
			} else if (statusCommand.compareTo("powerSupplies") == 0) {
				statusPanel.statusRow.setPowerSupplies((int) statusValue);
				statusPanel.statusTableModel.fireTableCellUpdated(0, 1);
				
				// JMenuItem powerSupplyMenuItem = (JMenuItem) menu.getMenu(3).getMenuComponent(5);
				
					// System.out.println("power supply from status: " + systemState.isPowerSupplyState() );
					if (statusValue == 0) {
						systemState.setPowerSupplyState(false);		
						// powerSupplyMenuItem.setText("Turn On Additional Power Supply");
					} else if (statusValue == 1) {
						systemState.setPowerSupplyState(true);
						// powerSupplyMenuItem.setText("Turn Off Additional Power Supply");
					}
					systemState.setPowersupplies( (int) statusValue);
					// System.out.println("number of power supplies from serial comms: " + statusValue);
			} else if (statusCommand.compareTo("relay") == 0) {
				statusPanel.statusRow.setRelay((int) statusValue);
				statusPanel.statusTableModel.fireTableCellUpdated(0, 2);
			} else if (statusCommand.compareTo("current") == 0) {
				statusPanel.statusRow.setCurrent((int) statusValue);
				statusPanel.statusTableModel.fireTableCellUpdated(0, 3);
			} else if (statusCommand.compareTo("temp") == 0) {
				statusPanel.statusRow.setTemp(statusValue);
				System.out.println("relay temp status value: " + statusValue);
				if (statusValue < controlBounds.getTemperatureWarningValue() ) {
					statusPanel.setTempStatusState(0);
					System.out.println("relay temp is acceptable level");
				} else if ( (statusValue >= controlBounds.getTemperatureWarningValue() ) && 
						(statusValue < controlBounds.getTemperatureShutdownValue() ) ) {
					System.out.println("relay temp eexceeded warning level");
					statusPanel.setTempStatusState(1);
				} else if (statusValue >= controlBounds.getTemperatureShutdownValue() ) {
					System.out.println("relay temp exceeded shutdown level");
					statusPanel.setTempStatusState(2);
				}
				statusPanel.statusTableModel.fireTableCellUpdated(0, 4);
			} else if (statusCommand.compareTo("vacuum") == 0) {
				statusPanel.statusRow.setVacuum(statusValue);
				
				System.out.println("vacuum status value: " + statusValue);
				if (statusValue < controlBounds.getVacuumLimitValue() ) {
					statusPanel.setVacuumStatusState(0);
					System.out.println("vacuum is acceptable level");
				} else if (statusValue >= controlBounds.getVacuumLimitValue() ) {
					System.out.println("exceeded pressure shutdown level");
					statusPanel.setVacuumStatusState(2);
				}
				
				statusPanel.statusTableModel.fireTableCellUpdated(0, 5);
			} else if (statusCommand.compareTo("running") == 0) {
				
				if (statusValue != systemState.getRunning() ) {
					if (statusValue == 0) {
						systemState.setRunState(false);
						if (systemState.isFilamentsSet() ) {
							startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
						} else {
							startButton.setText("<html><font size=\"20\">Estimate Number of Filaments</font></html>");
						}
					}
					if (statusValue == 1) {
						systemState.setRunState(true);	
						startButton.setText("<html><font size=\"20\">Stop Heating</font></html>");
					}
					toggleRunningMenuItems(!systemState.isRunState() );
					System.out.println("running is: " +  ( (int) statusValue) );
					systemState.setRunning( ( (int) statusValue) );
				}	
			} else {
				logger.info("status message doesn't correspond to anything");
			}
			
		}
	}

	/*
	 * public String processContinuing(String continuingString) {
	 * 
	 * logger.info("command finished"); logger.info("completed response: " +
	 * continuingString);
	 * 
	 * String continuingStringMessageString = "";
	 * 
	 * // currentValue#1.23OK
	 * 
	 * // parse response int okIndex = okString.lastIndexOf("OK");
	 * 
	 * if (okIndex != -1) { String message = okString.substring(0, okIndex); int
	 * responseNumberIndex = message.lastIndexOf('#'); String responseNumberString =
	 * ""; if (responseNumberIndex != -1) { responseNumberString =
	 * message.substring(responseNumberIndex + 1);
	 * logger.info("responseNumberString: " + responseNumberString); responseValue =
	 * new Double(responseNumberString).doubleValue(); okMessageString =
	 * message.substring(0, responseNumberIndex); } } else { okMessageString = ""; }
	 * 
	 * return okMessageString; }
	 */

	// continuing messages
	public void continuingMessages(String continuingMessage) {

		logger.info("continuingMessages function: " + continuingMessage);

		// estimate filaments
		String message = processContinuing(continuingMessage);
		logger.info("continuing message before branch: " + message);
		if (continuingDialogNeeded) {
			continuingDialogNeeded = false;

			logger.info("continuing message: " + message);

			if (message.compareTo("numberOfFilaments") == 0) {

				int filamentEstimate = (int) responseValue;

				// startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
				// heatingProfile.setRunningProfile(false);
				// toggleRunningMenuItems(true);

				/*
				 * JOptionPane.showMessageDialog(frame, "Filament Estimate - There are " +
				 * filamentEstimate + " Filaments", "Control Message",
				 * JOptionPane.INFORMATION_MESSAGE);
				 */

				continuingDialogNeeded = true;

				// startCommandReceivedOK
				// int filamentEstimate = (int) getResponseValue();

				logger.info("estimated value for filaments obtained: " + filamentEstimate);

				// set filaments dialog box
				// SetNumberOfFilamentsDialog setNumberOfFilamentsDialog = new
				// SetNumberOfFilamentsDialog(frame);

				// setNumberOfFilamentsDialog.showDialog(filamentEstimate);
				// int filaments = setNumberOfFilamentsDialog.getNumberOfFilementsValue();

				// setFilaments(filaments);

				String filamentsMessage = "<html>The Number of Filaments Estimated is " + filamentEstimate + "<p>"
						+ "If this is incorrect, please change via the Set Filaments Command</html>";

				// String filamentsMessage = "test";
				// NonBlockingMessageDialog nonBlockingMessageDialog = new
				// NonBlockingMessageDialog(frame);
				// nonBlockingMessageDialog.showDialog(filamentsMessage, "Filaments Estimate ");

				ShowFilamentsDialog showFilamentsDialog = new ShowFilamentsDialog(frame);
				showFilamentsDialog.showDialog(filamentsMessage, "Filaments Estimate", systemState.isFilamentsSet() );

			} else if (message.compareTo("powerSupplyOnError") == 0) {
				// setFilamentCommandReceived#Error
				JOptionPane.showMessageDialog(frame,
						"Filament Estuimate is Not Realistic - Please check that the power supply is on",
						"Control Message", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public double getResponseValue() {
		return responseValue;
	}

	public void setResponseValue(double responseValue) {
		this.responseValue = responseValue;
	}

	public void setFilaments(int filamentsValue) {

		logger.info("set the number of filaments: " + filamentsValue);
		setProfileRunningImportant(true);
		commandResponse("setFilamentValue#" + filamentsValue);
		String response = getResponse();
		resetCompleteResponse();
		if (isError()) {
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

		} else if (isOk()) {
			processOK(response);

			// startCommandReceivedOK
			JOptionPane.showMessageDialog(frame, "Set Filaments Value OK", "Control Message",
					JOptionPane.INFORMATION_MESSAGE);

		} else {
			logger.info("indeterminate state");

			// Unknown Error
			JOptionPane.showMessageDialog(frame, "An unknown error occurred - " + response, "Control Message",
					JOptionPane.INFORMATION_MESSAGE);
		}
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

	// unsolicited error messages
	public void unsolicitedErrorMessages(String errorMessage) {

		logger.info("unsolicitedErrorMessages function: " + errorMessage);

		// overtemp shutdown
		// overtemp warning
		// vacuum warning?
		// fan warning?
		// unknown error
		// profile

		String message = processError(errorMessage);
		logger.info("error message from unsolicited messages: " + errorMessage);
		logger.info("processed error message from unsolicited messages: " + message);
		logger.info("errorDialogNeeded state: " + errorDialogNeeded);

		if (errorDialogNeeded) {
			errorDialogNeeded = true;
			
			if (message.compareTo("invalidstage") == 0) { 

				NonBlockingMessageDialog errorMessageDialog = new NonBlockingMessageDialog(frame);
				errorMessageDialog.showDialog("Error - Invalid Heating Stage",
						"Control Message");
				
			} else if (message.compareTo("tempread") == 0) { 

				NonBlockingMessageDialog errorMessageDialog = new NonBlockingMessageDialog(frame);
				errorMessageDialog.showDialog("Error - The temperature isn't reading correctly",
						"Control Message");
				
			} else if (message.compareTo("profileread") == 0) { 

				NonBlockingMessageDialog errorMessageDialog = new NonBlockingMessageDialog(frame);
				errorMessageDialog.showDialog("Error - Couldn't read the degassing profile correctly",
						"Control Message");
				
			} else if (message.compareTo("pressureread") == 0) { 

				NonBlockingMessageDialog errorMessageDialog = new NonBlockingMessageDialog(frame);
				errorMessageDialog.showDialog("Error - The pressure isn't reading correctly",
						"Control Message");
				
			} else if (message.compareTo("currentread") == 0) { 

				NonBlockingMessageDialog errorMessageDialog = new NonBlockingMessageDialog(frame);
				errorMessageDialog.showDialog("Error - The current isn't reading correctly",
						"Control Message");
				
			} else if (message.compareTo("overtempwarning") == 0) { 

				/*
				NonBlockingMessageDialog errorMessageDialog = new NonBlockingMessageDialog(frame);
				errorMessageDialog.showDialog("Warning - The temperature has exceeded the warning limit",
						"Control Message");
						*/

				// JOptionPane.showMessageDialog(frame, "Warning - The temperature has exceeded
				// the warning limit",
				// "Control Message", JOptionPane.ERROR_MESSAGE);
			} else if (message.compareTo("overtempshutdown") == 0) {

				// JOptionPane.showMessageDialog(frame, "Error - The temperature has exceeded
				// the shutdown limit",
				// "Control Message", JOptionPane.ERROR_MESSAGE);

				// startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
				// heatingProfile.setRunningProfile(false);
				// toggleRunningMenuItems(true);

				NonBlockingMessageDialog errorMessageDialog = new NonBlockingMessageDialog(frame);
				errorMessageDialog.showDialog("Error - The temperature has exceeded the shutdown limit",
						"Control Message");

			} else if (message.compareTo("filamentswitchisoff") == 0) {

				// JOptionPane.showMessageDialog(frame, "Error - Check to make sure the power
				// supply switch is enabled.",
				// "Control Message", JOptionPane.ERROR_MESSAGE);

				// systemState.setFilamentsSet(false);

				// startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
				// heatingProfile.setRunningProfile(false);
				// toggleRunningMenuItems(true);

				NonBlockingMessageDialog errorMessageDialog = new NonBlockingMessageDialog(frame);
				errorMessageDialog.showDialog("Error - Check to make sure the power supply switch is enabled.",
						"Control Message");

			} else if (message.compareTo("overpressureshutdown") == 0) {

				// JOptionPane.showMessageDialog(frame, "Error - The pressure has exceeded the
				// shutdown limit.",
				// "Control Message", JOptionPane.ERROR_MESSAGE);

				// startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
				// heatingProfile.setRunningProfile(false);
				// toggleRunningMenuItems(true);

				NonBlockingMessageDialog errorMessageDialog = new NonBlockingMessageDialog(frame);
				errorMessageDialog.showDialog("Error - The pressure has exceeded the shutdown limit.",
						"Control Message");

			} else if (message.compareTo("pressureread") == 0) {

				// JOptionPane.showMessageDialog(frame, "Error - The pressure is not reading
				// correctly.",
				// "Control Message", JOptionPane.ERROR_MESSAGE);

				// startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
				// heatingProfile.setRunningProfile(false);
				// toggleRunningMenuItems(true);

				NonBlockingMessageDialog errorMessageDialog = new NonBlockingMessageDialog(frame);
				errorMessageDialog.showDialog("Error - The pressure is not reading correctly.", "Control Message");

				// need to fix algorithm for setting the aux power supply on and off
			} else if (message.compareTo("auxPowerSupplyOn") == 0) { 

				// JMenuItem powerSupplyMenuItem =  (JMenuItem) menu.getMenu(3).getMenuComponent(5);
				// powerSupplyMenuItem.setText("Turn Off Additional Power Supply");
				
			} else if (message.compareTo("auxPowerSupplyOff") == 0) {
				
				// JMenuItem powerSupplyMenuItem =  (JMenuItem) menu.getMenu(3).getMenuComponent(5);
				// powerSupplyMenuItem.setText("Turn On Additional Power Supply");
			}
		}
	}

	boolean toggleRunningMenuItems(boolean state) {
		/*
		
		// disable editing profile during a run
		 menu.getMenu(0).getMenuComponent(0).setEnabled(state);
		 menu.getMenu(0).getMenuComponent(1).setEnabled(state);
		 menu.getMenu(0).getMenuComponent(2).setEnabled(state);
		 menu.getMenu(0).getMenuComponent(3).setEnabled(state);
		 menu.getMenu(0).getMenuComponent(4).setEnabled(state);
		 menu.getMenu(0).getMenuComponent(5).setEnabled(state);
		 menu.getMenu(0).getMenuComponent(9).setEnabled(state);
		 
		 menu.getMenu(2).getMenuComponent(0).setEnabled(state);
		 menu.getMenu(2).getMenuComponent(3).setEnabled(state);
		 
		 heatingProfilePanel.getHeatingProfileTableModel().toggleEditing(state);
		
		
*/
		return true;
	}
}