package degasser;

import gnu.io.SerialPort;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.*;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.text.*;
import java.util.Hashtable;

//log4j
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class Gui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	SerialCommunications serialCommunications;

	String firmwarePath;
	String extraPath;
	String helpPath;
	String tempPath;

	String preferencesFile = "";

	boolean isFilename = false;
	boolean on = true;
	boolean off = false;

	boolean started = false;

	Hashtable actions;
	private JTextField dummyField = new JTextField();
	private JMenuBar jMenuBar;

	HeatingProfile heatingProfile;
	HeatingProfilePanel heatingProfilePanel;
	HeatingProfileTimerPanel heatingProfileTimerPanel;
	HeatingStageTimerPanel heatingStageTimerPanel;
	TimerPanel heatingTimerPanel;
	TimerPanelStage heatingTimerPanelStage;
	ControlPanel controlPanel;
	LogPanel logPanel;
	Commands commands;
	String logsFilename = "";
	String firmwareFilename = "";
	boolean isFanOn = false;
	ControlBounds controlBounds;
	StatusRow statusRow;
	StatusPanel statusPanel;

	/*
	 * Serial.println("------------------------- menu -------------------------");
	 * Serial.println(" (a) abort heating sequence");
	 * Serial.println(" (s) start heating sequence"); //
	 * Serial.println(" (y) approve change to relay");
	 * Serial.println(" (u) read vacuum (millibar) -  NOT CONNECTED");
	 * Serial.println(" (c) read current (mA)");
	 * Serial.println(" (t) set relay (0-100%)"); //
	 * Serial.println(" (p) proportional tuning constant: " + String(Kp) ); //
	 * Serial.println(" (i) integral tuning constant: " + String(Ki) ); //
	 * Serial.println(" (d) derivative tuning constant: " + String(Kd) );
	 * Serial.println(" (f) set number of filaments: " + String(numberOfFilaments)
	 * ); // Serial.println(" (v) set current: " + String(setpoint) ); Serial.
	 * println(" (l) add / change a line to the heating sequence - format index,time(minutes),total current(mA)"
	 * ); Serial.println(" (r) remove a line from the heating sequence");
	 * Serial.println(" (j) display the heating sequence");
	 * Serial.println(" (e) Estimate number of filaments");
	 * Serial.println(" (m) display menu");
	 * 
	 * create log window at bottom of the screen time remaining - put in RED
	 * lettering and convert to hours:minutes:seconds below big button - start or
	 * stop below that a window with log messages with a scrollbar
	 */

	// The File Menu
	public JMenu jMenuFile;
	public JMenuItem jMenuFileNew;
	public JMenuItem jMenuFileOpen;
	public JMenuItem jMenuFileAddStage;
	public JMenuItem jMenuFileRemoveStage;
	public JMenuItem jMenuFileEditStage;
	public JMenuItem jMenuFileInsertStage;
	public JMenuItem jMenuFileSave;
	public JMenuItem jMenuFileSaveAs;
	public JMenuItem jMenuFileSaveLogsAs;
	public JMenuItem jMenuFilePreferences;
	public JMenuItem jMenuFileExit;

	// The Edit Menu
	public JMenu jMenuEdit;

	// The control Menu
	public JMenu jMenuControl;
	public JMenuItem jMenuControlControlBounds;
	public JMenuItem jMenuControlClearLogConsole;
	public JMenuItem jMenuControlSelectSerialPort;
	public JMenuItem jMenuControlOpenSerialPort;
	public JMenuItem jMenuControlCloseSerialPort;
	public JMenuItem jMenuControlStart;
	public JMenuItem jMenuControlStop;
	public JMenuItem jMenuControlReset;
	public JMenuItem jMenuContolReadCurrent;
	public JMenuItem jMenuContolEstimateFilaments;
	public JMenuItem jMenuControlSetRelay;
	public JMenuItem jMenuControlSetNumberOfFilaments;
	public JMenuItem jMenuControlReadCurrent;
	public JMenuItem jMenuControlReadVacuum;
	public JMenuItem jMenuControlSetCurrent;
	public JMenuItem jMenuControlSendHeatingProfile;
	public JMenuItem jMenuControlDislayHeatingProfile;
	public JMenuItem jMenuControlReadTemperature;
	public JMenuItem jMenuControlUploadFirmware;
	public JMenuItem jMenuControlSetFan;
	public JMenuItem jMenuControlReadRelay;
	public JMenuItem jMenuControlReadNumberOfFilaments;
	public JMenuItem jMenuControlReadFan;
	public JMenuItem jMenuControlSendControlBounds;

	// The Help Menu
	public JMenu jMenuHelp;
	public JMenuItem jMenuHelpManual;
	public JMenuItem jMenuHelpAbout;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	// Construct the frame
	public Gui() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testfunction() {
		logger.info("does this execute?");
	}

	private void jbInit() throws Exception {

		// log4J
		String log4JFilename = "log4j.xml";
		DOMConfigurator.configure(log4JFilename);

		String appId = "degasserid";
		boolean alreadyRunning;
		try {
			JUnique.acquireLock(appId);
			alreadyRunning = false;
		} catch (AlreadyLockedException error) {
			alreadyRunning = true;
		}
		if (alreadyRunning) {
			// Start sequence here

			JOptionPane.showMessageDialog(this, "Program is already running.", "Error", JOptionPane.ERROR_MESSAGE);

			logger.info("program is already running.");
			System.exit(0);
		}

		logger.info("Welcome to the degasser interface program");
		logger.info(BuildString.getBuildString());

		/*
		 * String filamentsMessage = "<html>The Number of Filaments Estimated is " + 13
		 * + "<br>" +
		 * "If this is incorrect, please change via the 'Set Filaments Command'</html>";
		 * 
		 * NonBlockingMessageDialog test = new NonBlockingMessageDialog(this);
		 * test.showDialog(filamentsMessage, "test");
		 */

		heatingProfile = new HeatingProfile();

		// timerBar.setTime(100000);

		// new resets everything and gets filename
		// add stage
		// delete stage // use serial port dialog

		// timerPanel.setSize(new Dimension(900, 200) );
		// heatingProfile = new HeatingProfile();

		// controlPanel.setSize(new Dimension(900, 200) );

		logPanel = new LogPanel();
		// logPanel.setSize(new Dimension(300, 200) );

		// JLabel timerLabel = new JLabel("Time Remaining: N/A");
		// ResizableLabel timerLabel = new ResizableLabel("Time Remaining: N/A");

		// JLabel timerLabel = new JLabel("<html><font color=\"purple\" size=\"7\">Time
		// Remaining: N/A</font></html>");
		// timerPanel.add(timerLabel);

		File baseFileObject = new File("");
		String absoluteBasePath = baseFileObject.getAbsolutePath();
		logger.debug("absolute current working directory: " + absoluteBasePath);

		helpPath = absoluteBasePath + "/help/";
		tempPath = absoluteBasePath + "/temp/";
		firmwarePath = absoluteBasePath + "/firmware/";

		GradientButton startButton = new GradientButton("<html><font size=\"20\">Start Heating Profile</font></html>");

		jMenuBar = new JMenuBar();

		statusRow = new StatusRow(0, 0, 0, 0, 0.0, 0.0);
		statusPanel = new StatusPanel(statusRow);

		String filepath = System.getProperty("user.dir");
		controlBounds = new ControlBounds(filepath, this);

		heatingProfilePanel = new HeatingProfilePanel(heatingProfile);
		// heatingStageTimerPanel = new HeatingStageTimerPanel(heatingProfile);
		// heatingProfileTimerPanel = new HeatingProfileTimerPanel(heatingProfile);
		heatingTimerPanel = new TimerPanel(heatingProfile, startButton, jMenuBar);
		heatingTimerPanelStage = new TimerPanelStage(heatingProfile, heatingProfilePanel, startButton);

		serialCommunications = new SerialCommunications(this, logPanel, tempPath, heatingProfile, statusPanel,
				startButton, jMenuBar);
		commands = new Commands(serialCommunications, heatingProfile, this, jMenuBar, startButton, controlBounds);

		controlPanel = new ControlPanel(this, heatingProfile, heatingTimerPanel, heatingTimerPanelStage, startButton,
				commands);

		// statusRow.setStage(5);

		// heatingProfilePanel.getHeatingProfileTabelModel().getHeatingProfile().addHeatingStage(1,
		// 1, 1);
		// addDataRowToHeatingProfile();

		// timer panel
		// control panel
		// profile panel
		// log panel

		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(10, 25, 10, 25);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		// gridBagConstraints.weightx = gridBagConstraints.weighty = 1.0;

		// gridBagConstraints.weightx = 1;
		// gridBagConstraints.gridheight = 100;
		// gridBagConstraints.gridwidth = 100;

		JPanel mainPanel = new JPanel();

		mainPanel.setLayout(gridbag);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridbag.setConstraints(statusPanel, gridBagConstraints);
		mainPanel.add(statusPanel);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridbag.setConstraints(heatingTimerPanelStage, gridBagConstraints);
		mainPanel.add(heatingTimerPanelStage);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		// gridBagConstraints.weightx = 1;
		gridbag.setConstraints(heatingTimerPanel, gridBagConstraints);
		mainPanel.add(heatingTimerPanel);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridbag.setConstraints(controlPanel, gridBagConstraints);
		mainPanel.add(controlPanel);

		// gridBagConstraints.weightx = gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridbag.setConstraints(heatingProfilePanel, gridBagConstraints);
		mainPanel.add(heatingProfilePanel);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridbag.setConstraints(logPanel, gridBagConstraints);
		mainPanel.add(logPanel);

		getContentPane().add(mainPanel, BorderLayout.CENTER);

		// The File Menu
		jMenuFile = new JMenu();
		jMenuFileNew = new JMenuItem();
		jMenuFileOpen = new JMenuItem();
		jMenuFileAddStage = new JMenuItem();
		jMenuFileRemoveStage = new JMenuItem();
		jMenuFileEditStage = new JMenuItem();
		jMenuFileInsertStage = new JMenuItem();
		jMenuFileSave = new JMenuItem();
		jMenuFileSaveAs = new JMenuItem();
		jMenuFileSaveLogsAs = new JMenuItem();
		jMenuFileExit = new JMenuItem();

		// The Control Menu
		jMenuControl = new JMenu();
		jMenuControlControlBounds = new JMenuItem();
		jMenuControlClearLogConsole = new JMenuItem();
		jMenuControlSelectSerialPort = new JMenuItem();
		jMenuControlOpenSerialPort = new JMenuItem();
		jMenuControlCloseSerialPort = new JMenuItem();
		jMenuControlStart = new JMenuItem();
		jMenuControlStop = new JMenuItem();
		jMenuControlReset = new JMenuItem();
		jMenuContolReadCurrent = new JMenuItem();
		jMenuContolEstimateFilaments = new JMenuItem();
		jMenuControlSetRelay = new JMenuItem();
		jMenuControlSetNumberOfFilaments = new JMenuItem();
		jMenuControlReadCurrent = new JMenuItem();
		jMenuControlReadVacuum = new JMenuItem();
		jMenuControlReadTemperature = new JMenuItem();
		jMenuControlDislayHeatingProfile = new JMenuItem();
		jMenuControlUploadFirmware = new JMenuItem();
		jMenuControlSetFan = new JMenuItem();
		jMenuControlSetCurrent = new JMenuItem();
		jMenuControlSendHeatingProfile = new JMenuItem();
		jMenuControlReadRelay = new JMenuItem();
		jMenuControlReadNumberOfFilaments = new JMenuItem();
		jMenuControlReadFan = new JMenuItem();
		jMenuControlSendControlBounds = new JMenuItem();

		createActionTable(dummyField);
		jMenuEdit = createEditMenu();

		// The Help Menu
		jMenuHelp = new JMenu();
		jMenuHelpManual = new JMenuItem();
		jMenuHelpAbout = new JMenuItem();

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		this.setTitle("Vacuum Degasser");

		/*
		 * // Calculate the frame location int x = (screenSize.width - getWidth() ) / 2
		 * - getWidth() / 2; int y = (screenSize.height - getHeight() ) / 2;
		 * 
		 * // Set the new frame location setLocation(x, y);
		 */

		this.setSize(screenSize.width, screenSize.height);

		// this.setSize(new Dimension(900, 700));

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				System.exit(0);
			}
		});

		jMenuFile.setText("File");

		jMenuHelpAbout.setText("About");
		jMenuHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpAbout_actionPerformed(e);
			}
		});

		jMenuFileNew.setText("New Heating Profile");
		jMenuFileNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileNew_actionPerformed(e);
			}
		});

		jMenuFileOpen.setText("Open Heating Profile...");
		jMenuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpen_actionPerformed(e);
			}
		});

		jMenuFileAddStage.setText("Add Stage to Heating Profile...");
		jMenuFileAddStage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileAddStage_actionPerformed(e);
			}
		});

		jMenuFileRemoveStage.setText("Remove Stage from Heating Profile...");
		jMenuFileRemoveStage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileRemoveStage_actionPerformed(e);
			}
		});

		jMenuFileEditStage.setText("Edit Stage in Heating Profile...");
		jMenuFileEditStage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileEditStage_actionPerformed(e);
			}
		});

		jMenuFileInsertStage.setText("Insert Stage into Heating Profile...");
		jMenuFileInsertStage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileInsertStage_actionPerformed(e);
			}
		});

		jMenuFileSave.setText("Save Heating Profile...");
		jMenuFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSave_actionPerformed(e);
			}
		});

		jMenuFileSaveAs.setText("Save Heating Profile As...");
		jMenuFileSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveAs_actionPerformed(e);
			}
		});

		jMenuFileSaveLogsAs.setText("Save logs As...");
		jMenuFileSaveLogsAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveLogsAs_actionPerformed(e);
			}
		});

		jMenuFileExit.setText("Exit");
		jMenuFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileExit_actionPerformed(e);
			}
		});

		jMenuControl.setText("Control");

		jMenuControlSelectSerialPort.setText("Select the Serial Port...");
		jMenuControlSelectSerialPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlSelectSerialPort_actionPerformed(e);
			}
		});

		jMenuControlControlBounds.setText("Set Control Value Bounds...");
		jMenuControlControlBounds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlControlBounds_actionPerformed(e);
			}
		});

		jMenuControlOpenSerialPort.setText("Open Degasser Connection");
		jMenuControlOpenSerialPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlOpenSerialPort_actionPerformed(e);
			}
		});

		jMenuControlCloseSerialPort.setText("Close Degasser Connection");
		jMenuControlCloseSerialPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlCloseSerialPort_actionPerformed(e);
			}
		});

		jMenuControlStart.setText("Start Heating Profile");
		jMenuControlStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlStart_actionPerformed(e);
			}
		});

		jMenuControlStop.setText("Abort Heating");
		jMenuControlStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlStop_actionPerformed(e);
			}
		});

		jMenuControlReset.setText("Reset");
		jMenuControlReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlReset_actionPerformed(e);
			}
		});

		jMenuControlReadCurrent.setText("Read Current");
		jMenuControlReadCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlReadCurrent_actionPerformed(e);
			}
		});

		jMenuControlReadFan.setText("Read Fan");
		jMenuControlReadFan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlReadFan_actionPerformed(e);
			}
		});

		jMenuControlReadVacuum.setText("Read Vacuum");
		jMenuControlReadVacuum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlReadVacuum_actionPerformed(e);
			}
		});

		jMenuControlReadTemperature.setText("Read Temperature");
		jMenuControlReadTemperature.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlReadTemperature_actionPerformed(e);
			}
		});

		jMenuControlDislayHeatingProfile.setText("Display Heating Profile");
		jMenuControlDislayHeatingProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlDislayHeatingProfile_actionPerformed(e);
			}
		});

		jMenuContolEstimateFilaments.setText("Estimate Filaments");
		jMenuContolEstimateFilaments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuContolEstimateFilaments_actionPerformed(e);
			}
		});

		jMenuControlSetRelay.setText("Set Relay...");
		jMenuControlSetRelay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlSetRelay_actionPerformed(e);
			}
		});

		jMenuControlSendControlBounds.setText("Send Control Bounds to the Degasser");
		jMenuControlSendControlBounds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlSendControlBounds_actionPerformed(e);
			}
		});

		jMenuControlSetFan.setText("Turn On Cooling Fan");
		jMenuControlSetFan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlSetFan_actionPerformed(e);
			}
		});

		jMenuControlSetNumberOfFilaments.setText("Set Number Of Filaments...");
		jMenuControlSetNumberOfFilaments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlSetNumberOfFilaments_actionPerformed(e);
			}
		});

		jMenuControlUploadFirmware.setText("Upload Firmware...");
		jMenuControlUploadFirmware.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlUploadFirmware_actionPerformed(e);
			}
		});

		jMenuControlClearLogConsole.setText("Clear Log Console");
		jMenuControlClearLogConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlClearLogConsole_actionPerformed(e);
			}
		});

		jMenuControlSetCurrent.setText("Set Current...");
		jMenuControlSetCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlSetCurrent_actionPerformed(e);
			}
		});

		jMenuControlSendHeatingProfile.setText("Send Heating Profile to the Degasser");
		jMenuControlSendHeatingProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlSendHeatingProfile_actionPerformed(e);
			}
		});

		jMenuControlReadRelay.setText("Read the Relay Value");
		jMenuControlReadRelay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlReadRelay_actionPerformed(e);
			}
		});

		jMenuControlReadNumberOfFilaments.setText("Read Number of Filaments");
		jMenuControlReadNumberOfFilaments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlReadNumberOfFilaments_actionPerformed(e);
			}
		});

		jMenuHelp.setText("Help");

		// The File Menu
		jMenuFile.add(jMenuFileNew);
		jMenuFile.add(jMenuFileOpen);
		jMenuFile.add(jMenuFileAddStage);
		jMenuFile.add(jMenuFileRemoveStage);
		jMenuFile.add(jMenuFileEditStage);
		jMenuFile.add(jMenuFileInsertStage);
		jMenuFile.add(jMenuFileSave);
		jMenuFile.add(jMenuFileSaveAs);
		jMenuFile.add(jMenuFileSaveLogsAs);
		jMenuFile.add(jMenuFileExit);

		// Adds the file menu to the menu bar
		jMenuBar.add(jMenuFile);

		// Adds the edit menu to the menu bar
		jMenuBar.add(jMenuEdit);

		jMenuControl.add(jMenuControlClearLogConsole);
		jMenuControl.addSeparator();
		jMenuControl.add(jMenuControlStart);
		jMenuControl.add(jMenuControlStop);
		jMenuControl.add(jMenuControlControlBounds);
		jMenuControl.addSeparator();
		jMenuControl.add(jMenuControlSelectSerialPort);
		jMenuControl.add(jMenuControlOpenSerialPort);
		jMenuControl.add(jMenuControlCloseSerialPort);
		jMenuControl.addSeparator();
		jMenuControl.add(jMenuContolEstimateFilaments);
		jMenuControl.add(jMenuControlReadRelay);
		jMenuControl.add(jMenuControlReadFan);
		jMenuControl.add(jMenuControlReadCurrent);
		jMenuControl.add(jMenuControlReadVacuum);
		jMenuControl.add(jMenuControlReadTemperature);
		jMenuControl.add(jMenuControlReadNumberOfFilaments);
		jMenuControl.add(jMenuControlDislayHeatingProfile);
		jMenuControl.addSeparator();
		jMenuControl.add(jMenuControlSetNumberOfFilaments);
		jMenuControl.add(jMenuControlSetRelay);
		jMenuControl.add(jMenuControlSetFan);
		jMenuControl.add(jMenuControlSetCurrent);
		jMenuControl.add(jMenuControlSendHeatingProfile);
		jMenuControl.addSeparator();
		jMenuControl.add(jMenuControlUploadFirmware);
		jMenuControl.add(jMenuControlSendControlBounds);

		jMenuBar.add(jMenuControl);

		// Adds the help menu entries to the help menu
		jMenuHelp.add(jMenuHelpAbout);

		// Adds the help menu to the menu bar
		jMenuBar.add(jMenuHelp);

		// Add the menu bar
		this.setJMenuBar(jMenuBar);
	}

	void jMenuControlUploadFirmware_actionPerformed(ActionEvent event) {

		firmwareFilename = "";

		logger.info("choosing file for uploading firmware");

		logger.info("uploading firmware.");
		uploadFirmwareFile();
	}

	public boolean uploadFirmwareFile() {

		boolean success = true;
		
		(new Thread(new UploadFirmware(firmwarePath, firmwareFilename, logPanel) ) ).start();

		return success;
	}

	public boolean getFirmwareFilenameForOpening() {
		String filename = "";
		String extension = "txt";
		SafeJFileChooser safeFileDialog = new SafeJFileChooser(extension);
		int returnVal = safeFileDialog.showOpenDialog(this);

		if (returnVal == SafeJFileChooser.APPROVE_OPTION) {
			File file = safeFileDialog.getSelectedFile();
			logger.debug("opening: " + file.getName());
			logger.debug("directory: " + file.getAbsolutePath());
			firmwareFilename = file.getAbsolutePath();

			return true;
		}

		logger.debug("open file command canceled by user.");
		return false;
	}

	void jMenuControlClearLogConsole_actionPerformed(ActionEvent event) {
		logPanel.clearLogs();
	}

	void jMenuControlControlBounds_actionPerformed(ActionEvent event) {

		logger.info("set control bounds dialog");

		ControlBoundsDialog controlBoundsDialog = new ControlBoundsDialog(this, controlBounds);
		controlBoundsDialog.showControlBoundsDialog();

		if (serialCommunications.isPortOpen()) {
			commands.setBoundsValues();
		}
	}

	void jMenuFileSaveLogsAs_actionPerformed(ActionEvent event) {

		// get filename for saving logs
		getLogFilenameForSaving();

		logPanel.saveLogs(logsFilename);
	}

	public boolean getLogFilenameForSaving() {

		String filename = "";
		String extension = "txt";
		SafeJFileChooser safeFileDialog = new SafeJFileChooser(extension);
		int returnVal = safeFileDialog.showSaveDialog(this);

		if (returnVal == SafeJFileChooser.APPROVE_OPTION) {
			File file = safeFileDialog.getSelectedFile();

			if (!file.getName().endsWith(extension)) {
				logger.debug("saving: " + file.getName() + "." + extension);
				logger.debug("directory: " + file.getAbsolutePath() + "." + extension);
				logsFilename = file.getAbsolutePath() + "." + extension;

			} else {
				logger.debug("saving: " + file.getName());
				logger.debug("directory: " + file.getAbsolutePath());
				filename = file.getAbsolutePath();

			}

			logger.debug("filename from saving dialog: " + filename);

			return true;
		}

		logger.debug("Save as command canceled by user.");
		return false;
	}

	void jMenuControlSetFan_actionPerformed(ActionEvent event) {
		if (commands.isFanOn()) {
			jMenuControlSetFan.setText("Turn On Cooling Fan");
			commands.setFan(false);
		} else {
			jMenuControlSetFan.setText("Turn Off Cooling Fan");
			commands.setFan(true);
		}
	}

	void jMenuControlReadFan_actionPerformed(ActionEvent event) {
		commands.readFan();
	}

	void jMenuControlSetCurrent_actionPerformed(ActionEvent event) {
		logger.info("Set current");

		// set current value dialog box
		SetCurrentValueDialog setCurrentValueDialog = new SetCurrentValueDialog(this);
		int currentValue = setCurrentValueDialog.getCurrentValue();
		logger.info("current value " + currentValue);

		boolean okStatus = setCurrentValueDialog.getOkStatus();
		if (okStatus) {
			commands.setCurrentValue(currentValue);
		}
	}

	void jMenuControlSendControlBounds_actionPerformed(ActionEvent event) {
		logger.info("send control bounds to the degasser.");

		int fanOnValue = controlBounds.getFanTemperatureOnOffValue();
		int tempWarningValue = controlBounds.getTemperatureWarningValue();
		int tempShutdownBoundsValue = controlBounds.getTemperatureShutdownValue();
		int stageTimeLimitValue = controlBounds.getStageTimeLimitValue();
		int currentLimitValue = controlBounds.getCurrentLimitValue();

		commands.setBoundsValues(fanOnValue, tempWarningValue, tempShutdownBoundsValue, stageTimeLimitValue,
				currentLimitValue);
	}

	void jMenuControlSendHeatingProfile_actionPerformed(ActionEvent event) {
		logger.info("Send Heating Profile");

		commands.sendHeatingProfile();
	}

	void jMenuFileNew_actionPerformed(ActionEvent event) {
		logger.info("new profile selected");

		// need to generate an action event after new profile
		heatingProfile.newHeatingProfile();
		heatingProfilePanel.heatingProfileTableModel.fireTableRowsInserted(1, 1);

		if (heatingProfile.isRunningProfile() == true) {
			JOptionPane.showMessageDialog(this, "Profile is running - Please stop it first.", "Control Message",
					JOptionPane.ERROR_MESSAGE);
		} else {
			// heatingProfile.newProfile();
		}
	}

	// File | Open action performed
	public void jMenuFileOpen_actionPerformed(ActionEvent event) {
		getFilenameForOpening();
		heatingProfile.clearHeatingProfile();
		heatingProfile.openHeatingProfile();
	}

	public void jMenuFileAddStage_actionPerformed(ActionEvent event) {
		new AddHeatingStageDialog(this, heatingProfilePanel, controlBounds);
		int totalRunTime = heatingProfile.calculateTotalRunTime(); // in seconds

		logger.info("total run time: " + totalRunTime * 60);
		heatingTimerPanel.setTimeFromHeatingProfile();
		heatingTimerPanelStage.setTimeFromHeatingProfile();
		int lastRow = heatingProfile.getSize() - 1;
		logger.info("last row number in table: " + lastRow);
		heatingProfilePanel.getHeatingProfileTabelModel().fireTableRowsInserted(0, lastRow);
		heatingProfilePanel.getTable().clearSelection();
		// heatingProfilePanel.setSelectedStage(lastRow);
		heatingProfilePanel.getTable().setRowSelectionInterval(lastRow, lastRow);
	}

	public void jMenuFileRemoveStage_actionPerformed(ActionEvent event) {

		int numberOfHeatingStages = heatingProfilePanel.getHeatingProfileTabelModel().getHeatingProfile().getSize();
		if (numberOfHeatingStages > 1) {
			RemoveHeatingStageDialog removeHeatingStage = new RemoveHeatingStageDialog(this, heatingProfilePanel);
			removeHeatingStage.showRemoveHeatingStageDialog();
			int totalRunTime = heatingProfile.calculateTotalRunTime(); // in seconds
			heatingTimerPanel.setTimeFromHeatingProfile();
			heatingTimerPanelStage.setTimeFromHeatingProfile();
			int lastRow = heatingProfile.getSize() - 1;
			heatingProfilePanel.getHeatingProfileTabelModel().fireTableRowsInserted(0, lastRow);
			heatingProfilePanel.getTable().clearSelection();
			// heatingProfilePanel.setSelectedStage(lastRow);
			heatingProfilePanel.getTable().setRowSelectionInterval(lastRow, lastRow);
		} else {
			JOptionPane.showMessageDialog(this, "There must be at least one stage in the heating profile",
					"Heating Profile", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void jMenuFileEditStage_actionPerformed(ActionEvent event) {
		new EditHeatingStageDialog(this, heatingProfilePanel);
		heatingTimerPanel.setTimeFromHeatingProfile();
		heatingTimerPanelStage.setTimeFromHeatingProfile();
		int lastRow = heatingProfile.getSize() - 1;
		heatingProfilePanel.getHeatingProfileTabelModel().fireTableRowsInserted(0, lastRow);
		// heatingProfilePanel.getTable().clearSelection();
		// heatingProfilePanel.getTable().setRowSelectionInterval(lastRow, lastRow);
		// heatingProfilePanel.setSelectedStage(lastRow);
	}

	public void jMenuFileInsertStage_actionPerformed(ActionEvent event) {
		new InsertHeatingStageDialog(this, heatingProfilePanel);
		int totalRunTime = heatingProfile.calculateTotalRunTime(); // in seconds
		heatingTimerPanel.setTimeFromHeatingProfile();
		heatingTimerPanelStage.setTimeFromHeatingProfile();
		int lastRow = heatingProfile.getSize() - 1;
		heatingProfilePanel.getHeatingProfileTabelModel().fireTableRowsInserted(0, lastRow);
		heatingProfilePanel.getTable().clearSelection();
		heatingProfilePanel.getTable().setRowSelectionInterval(lastRow, lastRow);
	}

	// File | Save action performed
	public void jMenuFileSaveAs_actionPerformed(ActionEvent e) {
		logger.debug("save as button activated.");

		if (getFilenameForSaving()) {
			heatingProfile.saveAsHeatingProfile();
		}
	}

	// File | Save action performed
	public void jMenuFileSave_actionPerformed(ActionEvent e) {
		logger.debug("save button activated.");

		boolean success = true;

		if (heatingProfile.isFilenamePresent() == false) {
			success = getFilenameForSaving();
		}

		if (success) {
			heatingProfile.saveAsHeatingProfile();
		}
	}

	// File | Exit action performed
	public void jMenuFileExit_actionPerformed(ActionEvent event) {
		System.exit(0);
	}

	// Create the edit menu.
	protected JMenu createEditMenu() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		JMenu menu = new JMenu("Edit");

		// These actions come from the default editor kit.
		// Get the ones we want and stick them in the menu.
		menu.add(getActionByName(DefaultEditorKit.cutAction));
		menu.add(getActionByName(DefaultEditorKit.copyAction));
		menu.add(getActionByName(DefaultEditorKit.pasteAction));

		return menu;
	}

	private Action getActionByName(String name) {
		return ((Action) (actions.get(name)));
	}

	private void createActionTable(JTextComponent textComponent) {
		logger.debug("what does this actiontable do");
		actions = new Hashtable();
		Action[] actionsArray = textComponent.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			actions.put(a.getValue(Action.NAME), a);
		}
	}

	void jMenuControlSelectSerialPort_actionPerformed(ActionEvent event) {
		logger.info("selected the serial port dialog box");
		new SerialPortDialog(this, tempPath);
	}

	void jMenuControlOpenSerialPort_actionPerformed(ActionEvent event) {
		logger.info("trying to open the serial port");

		if (!serialCommunications.isPortOpen()) {
			try {
				serialCommunications.readSerialPortFile();
				boolean success = serialCommunications.openPort();

				if (success) {
					commands.sendBlank();
					commands.setBoundsValues();
				}

			} catch (Exception error) {
				logger.info("serial port is already open.");

				JOptionPane.showMessageDialog(this, "Serial Port is Aleady open", "Control Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	void jMenuControlCloseSerialPort_actionPerformed(ActionEvent event) {
		logger.info("trying to close the serial port");

		if (serialCommunications.isPortOpen()) {
			boolean success = serialCommunications.closePort();
			if (success) {
				JOptionPane.showMessageDialog(null, "The Serial Port closed.", "Serial Port Message",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "The Serial Port did not close.", "Serial Port Message",
						JOptionPane.ERROR_MESSAGE);
			}

		} else {
			logger.info("serial port is already closed.");

			JOptionPane.showMessageDialog(this, "The serial port is aleady closed", "Control Message",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	void jMenuControlStart_actionPerformed(ActionEvent e) {
		logger.info("starting");

		controlPanel.startHeatingProfile();

		// disable menus
		jMenuControlControlBounds.setEnabled(false);
		jMenuControlSelectSerialPort.setEnabled(false);
		jMenuControlOpenSerialPort.setEnabled(false);
		jMenuControlCloseSerialPort.setEnabled(false);
		jMenuControlStart.setEnabled(false);
		jMenuContolEstimateFilaments.setEnabled(false);
		jMenuControlSetRelay.setEnabled(false);
		jMenuControlSetNumberOfFilaments.setEnabled(false);
		jMenuControlUploadFirmware.setEnabled(false);
		jMenuControlSetCurrent.setEnabled(false);
		jMenuControlSendHeatingProfile.setEnabled(false);
		jMenuControlSendControlBounds.setEnabled(false);
		jMenuFileNew.setEnabled(false);
		jMenuFileOpen.setEnabled(false);
		jMenuFileAddStage.setEnabled(false);
		jMenuFileRemoveStage.setEnabled(false);
		jMenuFileEditStage.setEnabled(false);
		jMenuFileInsertStage.setEnabled(false);
	}

	void jMenuControlStop_actionPerformed(ActionEvent event) {
		logger.info("stopping");
		controlPanel.startHeatingProfile();

		// disable menus
		jMenuControlControlBounds.setEnabled(true);
		jMenuControlSelectSerialPort.setEnabled(true);
		jMenuControlOpenSerialPort.setEnabled(true);
		jMenuControlCloseSerialPort.setEnabled(true);
		jMenuControlStart.setEnabled(true);
		jMenuContolEstimateFilaments.setEnabled(true);
		jMenuControlSetRelay.setEnabled(true);
		jMenuControlSetNumberOfFilaments.setEnabled(true);
		jMenuControlUploadFirmware.setEnabled(true);
		jMenuControlSetCurrent.setEnabled(true);
		jMenuControlSendHeatingProfile.setEnabled(true);
		jMenuControlSendControlBounds.setEnabled(true);
		jMenuFileOpen.setEnabled(true);
		jMenuFileAddStage.setEnabled(true);
		jMenuFileRemoveStage.setEnabled(true);
		jMenuFileEditStage.setEnabled(true);
		jMenuFileInsertStage.setEnabled(true);
	}

	void jMenuControlReset_actionPerformed(ActionEvent event) {
	}

	void jMenuControlSetRelay_actionPerformed(ActionEvent event) {
		logger.info("set relay value");

		// set relay value dialog box
		SetRelayValueDialog setRelayValueDialog = new SetRelayValueDialog(this);
		int relayValue = setRelayValueDialog.getRelayValue();
		logger.info("relay value " + relayValue);

		boolean okStatus = setRelayValueDialog.getOkStatus();
		if (okStatus) {
			commands.setRelayValue(relayValue);
		}
	}

	void jMenuControlReadRelay_actionPerformed(ActionEvent event) {
		commands.readRelayValue();
	}

	void jMenuControlReadNumberOfFilaments_actionPerformed(ActionEvent event) {
		commands.readFilamentsValue();
	}

	void jMenuControlSetNumberOfFilaments_actionPerformed(ActionEvent event) {

		logger.info("set number of filaments");

		// set number of filaments dialog box
		SetNumberOfFilamentsDialog setNumberOfFilamentsDialog = new SetNumberOfFilamentsDialog(this);
		
		setNumberOfFilamentsDialog.showDialog(1); // can deal with this later
		int filamentsValue = setNumberOfFilamentsDialog.getNumberOfFilementsValue();
		boolean okStatus = setNumberOfFilamentsDialog.getOkStatus();
		if (okStatus) {
			commands.setFilaments(filamentsValue);
		}
	}

	void jMenuContolEstimateFilaments_actionPerformed(ActionEvent event) {
		commands.estimateFilaments();
	}

	// File | About action performed
	public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
		logger.debug("should be showing the about box.");
		About_Box dlg = new About_Box(this);
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(true);
		dlg.pack();
		dlg.show();
	}

	public boolean getFilenameForSaving() {

		String filename = "";
		String extension = "txt";
		SafeJFileChooser safeFileDialog = new SafeJFileChooser(extension);
		int returnVal = safeFileDialog.showSaveDialog(this);

		if (returnVal == SafeJFileChooser.APPROVE_OPTION) {
			File file = safeFileDialog.getSelectedFile();

			if (!file.getName().endsWith(extension)) {
				logger.debug("saving: " + file.getName() + "." + extension);
				logger.debug("directory: " + file.getAbsolutePath() + "." + extension);
				filename = file.getAbsolutePath() + "." + extension;
				heatingProfile.setFilename(filename);
			} else {
				logger.debug("saving: " + file.getName());
				logger.debug("directory: " + file.getAbsolutePath());
				filename = file.getAbsolutePath();
				heatingProfile.setFilename(filename);
			}
			// frame.setTitle("Degasser - " + filename);
			logger.debug("filename from saving dialog: " + filename);

			return true;
		}

		logger.debug("Save as command canceled by user.");
		return false;
	}

	public void startButton_actionPerformed(ActionEvent event) {

	}

	public boolean getFilenameForOpening() {
		String filename = "";
		String extension = "txt";
		SafeJFileChooser safeFileDialog = new SafeJFileChooser(extension);
		int returnVal = safeFileDialog.showOpenDialog(this);

		if (returnVal == SafeJFileChooser.APPROVE_OPTION) {
			File file = safeFileDialog.getSelectedFile();
			logger.debug("opening: " + file.getName());
			logger.debug("directory: " + file.getAbsolutePath());
			filename = file.getAbsolutePath();
			heatingProfile.setFilename(filename);

			// panel.setTitle("Degasser - " + filename);
			return true;
		}

		logger.debug("open file command canceled by user.");
		return false;
	}

	/*
	 * public void saveHeatingProfile(String filename) {
	 * 
	 * this.filename = filename; if (isFilenamePresent() ) {
	 * logger.debug("filename is present for save file."); writeProfileFile(); }
	 * else {
	 * logger.debug("filename is not present for save file. bring up save as dialog"
	 * ); saveAsHeatingProfile(filename); }
	 * 
	 * }
	 */

	public void jMenuControlReadCurrent_actionPerformed(ActionEvent event) {
		commands.readCurrentValue();
	}

	public void jMenuControlReadVacuum_actionPerformed(ActionEvent event) {
		commands.readVacuumValue();
	}

	public void jMenuControlReadTemperature_actionPerformed(ActionEvent event) {
		commands.readTempValue();
	}

	public void jMenuControlDislayHeatingProfile_actionPerformed(ActionEvent event) {
		commands.showHeatingProfile();
	}

	public boolean sendCommandWithPortCheck(String command) {

		boolean success = false;

		logger.info("send command with running check: " + command);

		logger.info("state of running: " + heatingProfile.isRunningProfile());

		logger.info("state of serial port open: " + serialCommunications.isPortOpen());
		// need some error checking here
		if (!serialCommunications.isPortOpen()) {

			logger.debug("serial port is closed. trying to open.");
			try {
				serialCommunications.readSerialPortFile();
				serialCommunications.openPort();
			} catch (Exception error) {
				logger.info("error opening the serial port");

				JOptionPane.showMessageDialog(this, "There was a problem opening the serial port", "Control Message",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		if (serialCommunications.isPortOpen()) {
			serialCommunications.setCommandName(command);
			success = serialCommunications.sendCommand(command);
		}

		return success;
	}

	public boolean sendCommandWithRunningCheck(String command) {

		boolean success = false;

		logger.info("send command with running check: " + command);

		logger.info("state of running: " + heatingProfile.isRunningProfile());

		if (heatingProfile.isRunningProfile() == false) {

			logger.info("state of serial port open: " + serialCommunications.isPortOpen());
			// need some error checking here
			if (!serialCommunications.isPortOpen()) {

				logger.debug("serial port is closed. trying to open.");
				try {
					serialCommunications.readSerialPortFile();
					serialCommunications.openPort();
				} catch (Exception error) {

					logger.info("error opening the serial port");

					JOptionPane.showMessageDialog(this, "There was a problem opening the serial port",
							"Control Message", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Profile is already running", "Control Message",
					JOptionPane.ERROR_MESSAGE);
		}

		if (serialCommunications.isPortOpen()) {
			serialCommunications.setCommandName(command);
			success = serialCommunications.sendCommand(command);
		}

		return success;
	}

}