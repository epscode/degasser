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
	
	HeatingProgressElements heatingProgressElements;
	
	HeatingProfilePanel heatingProfilePanel;
	// HeatingProfileTimerPanel heatingProfileTimerPanel;
	// HeatingStageTimerPanel heatingStageTimerPanel;
	StageCommandBar stageCommandBar;
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
	boolean filamentsSet = false;
	boolean showLogPanel = false;
	SystemState systemState;

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
	public JMenuItem jMenuFilePreferences;
	public JMenuItem jMenuFileExit;

	// The Edit Menu
	public JMenu jMenuEdit;

	// The control Menu
	public JMenu jMenuControl;
	public JMenuItem jMenuControlSelectSerialPort;
	public JMenuItem jMenuControlStart;
	public JMenuItem jMenuControlStop;
	public JMenuItem jMenuControlEstimateFilaments;
	public JMenuItem jMenuControlFilamentsEnable;

	// The Diagnostic Menu
	public JMenu jMenuDiagnostic;
	public JMenuItem jMenuControlOpenSerialPort;
	public JMenuItem jMenuControlCloseSerialPort;
	public JMenuItem jMenuDiagnosticReadCurrent;
	public JMenuItem jMenuDiagnosticSetRelay;
	public JMenuItem jMenuDiagnosticReadVacuum;
	public JMenuItem jMenuDiagnosticSetCurrent;
	public JMenuItem jMenuDiagnosticSendHeatingProfile;
	public JMenuItem jMenuDiagnosticDislayHeatingProfile;
	public JMenuItem jMenuDiagnosticReadTemperature;
	public JMenuItem jMenuDiagnosticUploadFirmware;
	public JMenuItem jMenuDiagnosticReadRelay;
	public JMenuItem jMenuDiagnosticReadNumberOfFilaments;
	public JMenuItem jMenuDiagnosticSetControlBounds;
	public JMenuItem jMenuDiagnosticSendControlBounds;
	public JMenuItem jMenuDiagnosticReadControlBounds;
	public JMenuItem jMenuDiagnosticReadPowerSupply;
	public JMenuItem jMenuDiagnosticSetPowerSupply;
	public JMenuItem jMenuDiagnosticReadFan;
	public JMenuItem jMenuDiagnosticSetFan;
	public JMenuItem jMenuDiagnosticsSetNumberOfFilaments;
	public JMenuItem jMenuDiagnosticSaveLogsAs;
	public JMenuItem jMenuDiagnosticClearLogConsole;
	public JMenuItem JMenuDiagnosticShowLogPanel;

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

		heatingProfile = new HeatingProfile(this);
		
		heatingProgressElements = new HeatingProgressElements(heatingProfile);

		// timerBar.setTime(100000);

		// new resets everything and gets filename
		// add stage
		// delete stage // use serial port dialog

		// timerPanel.setSize(new Dimension(900, 200) );
		// heatingProfile = new HeatingProfile();

		// controlPanel.setSize(new Dimension(900, 200) );

		
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
		
		logPanel = new LogPanel(this, tempPath);

		GradientButton startButton = new GradientButton("<html><font size=\"20\">Estimate Number of Filaments</font></html>");

		jMenuBar = new JMenuBar();

		statusRow = new StatusRow(0, -1, 0, 0, 0, 0.0, 0.0);
		statusPanel = new StatusPanel(statusRow);

		String filepath = System.getProperty("user.dir");
		controlBounds = new ControlBounds(filepath, this);
		
		systemState = new SystemState();
		
		heatingProgressElements = new HeatingProgressElements(heatingProfile);

		heatingProfilePanel = new HeatingProfilePanel(heatingProfile, heatingProgressElements);
		
		// heatingStageTimerPanel = new HeatingStageTimerPanel(heatingProfile);
		// heatingProfileTimerPanel = new HeatingProfileTimerPanel(heatingProfile);
		heatingTimerPanel = new TimerPanel(this, heatingProfile, startButton, jMenuBar, heatingProgressElements);
		heatingTimerPanelStage = new TimerPanelStage(heatingProfile, heatingProfilePanel, startButton, heatingProgressElements);
		
		serialCommunications = new SerialCommunications(this, logPanel, tempPath, heatingProfilePanel, statusPanel,
				startButton, jMenuBar, controlBounds, systemState,  heatingTimerPanel, heatingTimerPanelStage);
		commands = new Commands(serialCommunications, heatingProfile, this, jMenuBar, startButton, controlBounds, systemState,
				heatingTimerPanel, heatingTimerPanelStage);

		controlPanel = new ControlPanel(this, heatingProfile, heatingTimerPanel, heatingTimerPanelStage, startButton,
				commands, systemState);
		
		stageCommandBar = new StageCommandBar(this, heatingProfilePanel, heatingTimerPanel, heatingTimerPanelStage);
		
		heatingProfilePanel.setExecutingStage(0);

		// String testparse = serialCommunications.processContinuing("numberOfFilaments#11Continuing");
		// logger.info("testparse: " + testparse);
		// logger.info("testvalue: " + serialCommunications.getResponseValue());
		
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
		gridbag.setConstraints(heatingTimerPanel, gridBagConstraints);
		mainPanel.add(heatingTimerPanel);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridbag.setConstraints(controlPanel, gridBagConstraints);
		mainPanel.add(controlPanel);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridbag.setConstraints(stageCommandBar, gridBagConstraints);
		mainPanel.add(stageCommandBar);
		
		/*
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridbag.setConstraints(heatingProfilePanel, gridBagConstraints);
		mainPanel.add(heatingProfilePanel);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridbag.setConstraints(logPanel, gridBagConstraints);
		mainPanel.add(logPanel);
*/
		getContentPane().add(mainPanel, BorderLayout.NORTH);
		getContentPane().add(heatingProfilePanel, BorderLayout.CENTER);
		// getContentPane().add(logPanel, BorderLayout.SOUTH);
		
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
		jMenuDiagnosticSaveLogsAs = new JMenuItem();
		jMenuFileExit = new JMenuItem();

		// The Control Menu
		jMenuControl = new JMenu();
		jMenuControlStart = new JMenuItem();
		jMenuControlStop = new JMenuItem();
		jMenuControlEstimateFilaments = new JMenuItem();
		jMenuControlFilamentsEnable = new JMenuItem();
		jMenuDiagnosticsSetNumberOfFilaments = new JMenuItem();

		// Diagnostic Menu
		jMenuDiagnostic = new JMenu();
		jMenuControlSelectSerialPort = new JMenuItem();
		jMenuControlOpenSerialPort = new JMenuItem();
		jMenuControlCloseSerialPort = new JMenuItem();
		jMenuDiagnosticReadControlBounds = new JMenuItem();
		jMenuDiagnosticSetControlBounds = new JMenuItem();
		jMenuDiagnosticReadCurrent = new JMenuItem();
		jMenuDiagnosticSetRelay = new JMenuItem();
		jMenuDiagnosticReadVacuum = new JMenuItem();
		jMenuDiagnosticReadTemperature = new JMenuItem();
		jMenuDiagnosticDislayHeatingProfile = new JMenuItem();
		jMenuDiagnosticUploadFirmware = new JMenuItem();
		jMenuDiagnosticSetCurrent = new JMenuItem();
		jMenuDiagnosticSendHeatingProfile = new JMenuItem();
		jMenuDiagnosticReadRelay = new JMenuItem();
		jMenuDiagnosticReadNumberOfFilaments = new JMenuItem();
		jMenuDiagnosticSendControlBounds = new JMenuItem();
		jMenuDiagnosticReadPowerSupply = new JMenuItem();
		jMenuDiagnosticSetPowerSupply = new JMenuItem();
		jMenuDiagnosticReadFan = new JMenuItem();
		jMenuDiagnosticSetFan = new JMenuItem();
		jMenuDiagnosticClearLogConsole = new JMenuItem();
		JMenuDiagnosticShowLogPanel = new JMenuItem();

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

		// this.setSize(screenSize.width, screenSize.height);

		// this.setPreferredSize(screenSize.width, screenSize.height);
		this.setSize(new Dimension(1200, 1000));

		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE ); 
		this.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				if (heatingProfile.exitCheck() ) {
					System.exit(0);
				} else {
					System.out.println("not exiting");
				}
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
		
		jMenuFileNew.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );

		jMenuFileOpen.setText("Open Heating Profile...");
		jMenuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpen_actionPerformed(e);
			}
		});
		
		jMenuFileOpen.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );

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
		
		jMenuFileSave.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );

		jMenuFileSaveAs.setText("Save Heating Profile As...");
		jMenuFileSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveAs_actionPerformed(e);
			}
		});

		jMenuDiagnosticSaveLogsAs.setText("Save logs As...");
		jMenuDiagnosticSaveLogsAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticSaveLogsAs_actionPerformed(e);
			}
		});

		jMenuFileExit.setText("Exit");
		jMenuFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileExit_actionPerformed(e);
			}
		});
		
		jMenuFileExit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );

		jMenuControl.setText("Control");

		jMenuControlSelectSerialPort.setText("Select the Serial Port...");
		jMenuControlSelectSerialPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlSelectSerialPort_actionPerformed(e);
			}
		});

		jMenuDiagnosticSetControlBounds.setText("Set Control Value Bounds...");
		jMenuDiagnosticSetControlBounds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticSetControlBounds_actionPerformed(e);
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

		jMenuControlStop.setText("Stop Heating");
		jMenuControlStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlStop_actionPerformed(e);
			}
		});

		jMenuDiagnostic.setText("Diagnostics");

		jMenuDiagnosticReadCurrent.setText("Read Current");
		jMenuDiagnosticReadCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticReadCurrent_actionPerformed(e);
			}
		});

		jMenuDiagnosticReadPowerSupply.setText("Read System Voltgae");
		jMenuDiagnosticReadPowerSupply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticReadPowerSupply_actionPerformed(e);
			}
		});

		jMenuDiagnosticReadVacuum.setText("Read Vacuum Pressure");
		jMenuDiagnosticReadVacuum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticReadVacuum_actionPerformed(e);
			}
		});

		jMenuDiagnosticReadTemperature.setText("Read Temperature");
		jMenuDiagnosticReadTemperature.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticReadTemperature_actionPerformed(e);
			}
		});

		jMenuDiagnosticDislayHeatingProfile.setText("Display Heating Profile");
		jMenuDiagnosticDislayHeatingProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticDislayHeatingProfile_actionPerformed(e);
			}
		});

		jMenuControlEstimateFilaments.setText("Estimate Filaments");
		jMenuControlEstimateFilaments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuContolEstimateFilaments_actionPerformed(e);
			}
		});
		
		jMenuControlFilamentsEnable.setText("Enable Filaments");
		jMenuControlFilamentsEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuControlFilamentsEnable_actionPerformed(e);
			}
		});

		jMenuDiagnosticSetRelay.setText("Set Relay...");
		jMenuDiagnosticSetRelay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticSetRelay_actionPerformed(e);
			}
		});

		jMenuDiagnosticSendControlBounds.setText("Send Control Bounds to the Degasser");
		jMenuDiagnosticSendControlBounds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticSendControlBounds_actionPerformed(e);
			}
		});

		jMenuDiagnosticSetPowerSupply.setText("Turn On Additional Power Supply");
		jMenuDiagnosticSetPowerSupply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticSetPowerSupply_actionPerformed(e);
			}
		});

		jMenuDiagnosticReadFan.setText("Read Relay Fan State");
		jMenuDiagnosticReadFan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticReadFan_actionPerformed(e);
			}
		});

		jMenuDiagnosticSetFan.setText("Turn On Relay Cooling Fan");
		jMenuDiagnosticSetFan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticSetFan_actionPerformed(e);
			}
		});

		jMenuDiagnosticsSetNumberOfFilaments.setText("Set Number Of Filaments...");
		jMenuDiagnosticsSetNumberOfFilaments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticSetNumberOfFilaments_actionPerformed(e);
			}
		});

		jMenuDiagnosticUploadFirmware.setText("Upload Firmware...");
		jMenuDiagnosticUploadFirmware.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticUploadFirmware_actionPerformed(e);
			}
		});

		jMenuDiagnosticClearLogConsole.setText("Clear Log Console");
		jMenuDiagnosticClearLogConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticClearLogConsole_actionPerformed(e);
			}
		});

		jMenuDiagnosticSetCurrent.setText("Set Current...");
		jMenuDiagnosticSetCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticSetCurrent_actionPerformed(e);
			}
		});

		jMenuDiagnosticSendHeatingProfile.setText("Send Heating Profile to the Degasser");
		jMenuDiagnosticSendHeatingProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticSendHeatingProfile_actionPerformed(e);
			}
		});

		jMenuDiagnosticReadRelay.setText("Read the Relay Value");
		jMenuDiagnosticReadRelay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticReadRelay_actionPerformed(e);
			}
		});

		jMenuDiagnosticReadNumberOfFilaments.setText("Read Number of Filaments");
		jMenuDiagnosticReadNumberOfFilaments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuDiagnosticReadNumberOfFilaments_actionPerformed(e);
			}
		});
		
		JMenuDiagnosticShowLogPanel.setText("Show / Hide Log Panel");
		JMenuDiagnosticShowLogPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JMenuDiagnosticShowLogPanel_actionPerformed(e);
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
		jMenuFile.add(jMenuFileExit);

		// Adds the file menu to the menu bar
		jMenuBar.add(jMenuFile);

		// Adds the edit menu to the menu bar
		jMenuBar.add(jMenuEdit);

		jMenuControl.add(jMenuControlStart);
		jMenuControl.add(jMenuControlStop);

		jMenuControl.addSeparator();
		
		jMenuControl.add(jMenuControlEstimateFilaments);
		// jMenuControl.add(jMenuControlFilamentsEnable);
		jMenuControl.add(jMenuDiagnosticsSetNumberOfFilaments);

		jMenuBar.add(jMenuControl);

		/*
		jMenuDiagnostic.add(jMenuDiagnosticReadRelay);
		jMenuDiagnostic.add(jMenuDiagnosticReadPowerSupply);
		jMenuDiagnostic.add(jMenuDiagnosticReadCurrent);
		jMenuDiagnostic.add(jMenuDiagnosticReadVacuum);
		jMenuDiagnostic.add(jMenuDiagnosticReadTemperature);
		jMenuDiagnostic.add(jMenuDiagnosticReadNumberOfFilaments);
		jMenuDiagnostic.add(jMenuDiagnosticDislayHeatingProfile);
		jMenuDiagnostic.addSeparator();
		*/

		jMenuDiagnostic.add(jMenuControlSelectSerialPort);
		jMenuDiagnostic.add(jMenuControlOpenSerialPort);
		jMenuDiagnostic.add(jMenuControlCloseSerialPort);
		jMenuDiagnostic.addSeparator();
		jMenuDiagnostic.add(jMenuDiagnosticSetRelay);
		jMenuDiagnostic.add(jMenuDiagnosticSetPowerSupply);
		jMenuDiagnostic.add(jMenuDiagnosticSetCurrent);
		jMenuDiagnostic.add(jMenuDiagnosticSendHeatingProfile);

		jMenuDiagnostic.addSeparator();
		// jMenuDiagnostic.add(jMenuDiagnosticUploadFirmware);
		jMenuDiagnostic.add(jMenuDiagnosticSetControlBounds);
		jMenuDiagnostic.add(jMenuDiagnosticDislayHeatingProfile);
		// jMenuDiagnostic.add(jMenuDiagnosticSendControlBounds);
		jMenuDiagnostic.add(jMenuDiagnosticsSetNumberOfFilaments);
		jMenuDiagnostic.add(jMenuDiagnosticSaveLogsAs);
		jMenuDiagnostic.add(jMenuDiagnosticClearLogConsole);
		// jMenuDiagnostic.add(jMenuDiagnosticSetFan);
		
		jMenuDiagnostic.add(JMenuDiagnosticShowLogPanel);

		jMenuBar.add(jMenuDiagnostic);

		// Adds the help menu entries to the help menu
		jMenuHelp.add(jMenuHelpAbout);

		// Adds the help menu to the menu bar
		jMenuBar.add(jMenuHelp);

		// Add the menu bar
		this.setJMenuBar(jMenuBar);
		
		// TestDialog testDialog = new TestDialog(this, heatingProfilePanel);
		
		logger.info("trying to open the serial port when opening the program");

		if (!serialCommunications.isPortOpen() ) {
			try {
				serialCommunications.readSerialPortFile();
				boolean success = serialCommunications.openPort();

				if (success) {
					commands.sendBlank();
					commands.setBoundsValuesNoDialog();	
					commands.resetState();
				}

			} catch (Exception error) {
				logger.info("serial port is already open.");

				JOptionPane.showMessageDialog(this, "Serial Port is Aleady open", "Control",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
	}

	void jMenuDiagnosticUploadFirmware_actionPerformed(ActionEvent event) {

		firmwareFilename = "";

		logger.info("choosing file for uploading firmware");

		logger.info("uploading firmware.");
		uploadFirmwareFile();
	}

	public boolean uploadFirmwareFile() {

		boolean success = true;

		(new Thread(new UploadFirmware(firmwarePath, firmwareFilename, logPanel))).start();

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

	void jMenuDiagnosticClearLogConsole_actionPerformed(ActionEvent event) {
		logPanel.clearLogs();
	}

	void jMenuDiagnosticSetControlBounds_actionPerformed(ActionEvent event) {

		logger.info("set control bounds dialog");

		ControlBoundsDialog controlBoundsDialog = new ControlBoundsDialog(this, controlBounds);
		controlBoundsDialog.showControlBoundsDialog();
		
		boolean okStatus = controlBoundsDialog.isOkStatus();

		if (okStatus) {
			if (serialCommunications.isPortOpen()) {
				commands.setBoundsValues();
			}
		}	
	}

	void jMenuDiagnosticSaveLogsAs_actionPerformed(ActionEvent event) {

		// get filename for saving logs
		if (getLogFilenameForSaving()) {
			logPanel.saveLogs(logsFilename);
		}
	}

	public boolean getLogFilenameForSaving() {

		// String filename = "";
		String extension = "txt";
		SafeJFileChooser safeFileDialog = new SafeJFileChooser(extension);
		int returnVal = safeFileDialog.showSaveDialog(this);

		if (returnVal == SafeJFileChooser.APPROVE_OPTION) {
			File file = safeFileDialog.getSelectedFile();

			if (!file.getName().endsWith(extension)) {
				logger.debug("saving: " + file.getName() + "." + extension);
				logger.debug("directory: " + file.getAbsolutePath() + "." + extension);
				
				// System.out.println("saving: " + file.getName() + "." + extension);
				// System.out.println("directory: " + file.getAbsolutePath() + "." + extension);
				
				logsFilename = file.getAbsolutePath() + "." + extension;

			} else {
				logger.debug("saving: " + file.getName());
				logger.debug("directory: " + file.getAbsolutePath());
				
				// System.out.println("saving: " + file.getName());
				// System.out.println("directory: " + file.getAbsolutePath());
				logsFilename = file.getAbsolutePath();

			}

			logger.debug("filename from saving dialog: " + logsFilename);
			// System.out.println("filename from saving dialog: " + logsFilename);

			return true;
		}

		logger.debug("Save as command canceled by user.");
		return false;
	}

	void jMenuDiagnosticSetPowerSupply_actionPerformed(ActionEvent event) {
		// commands.setPowerSupply(true);
		
		System.out.println("power supply: " +  systemState.isPowerSupplyState() );
		
		if (systemState.isPowerSupplyState()) {
			// jMenuDiagnosticSetPowerSupply.setText("Turn On Power Supply");
			commands.setPowerSupply(false);
		} else {
			// jMenuDiagnosticSetPowerSupply.setText("Turn Off Power Supply");
			commands.setPowerSupply(true);
		}	
	}

	void jMenuDiagnosticReadPowerSupply_actionPerformed(ActionEvent event) {
		// commands.readPowerSupply();
	}

	void jMenuDiagnosticSetCurrent_actionPerformed(ActionEvent event) {
		logger.info("Set current");

		/*
		 * logger.info("filamentsSet from set current: " + filamentsSet); filamentsSet =
		 * commands.isFilamentsSet();
		 * 
		 * if (!serialCommunications.isFilamentsSet() ) { filamentsSet = false; }
		 */

		// logger.info("filamentsSet from set current: " + filamentsSet);
		if (!systemState.isFilamentsSet() && !systemState.isFilamentsSet()) {
			logger.info("Number of filaments is not set");

			JOptionPane.showMessageDialog(this, "Please estimate filaments before setting the current",
					"Control", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// set current value dialog box
		SetCurrentValueDialog setCurrentValueDialog = new SetCurrentValueDialog(this);
		int currentValue = setCurrentValueDialog.getCurrentValue();
		logger.info("current value " + currentValue);

		boolean okStatus = setCurrentValueDialog.getOkStatus();

		if (okStatus) {
			commands.setCurrentValue(currentValue);
		}
	}

	void jMenuDiagnosticSendControlBounds_actionPerformed(ActionEvent event) {
		logger.info("send control bounds to the degasser.");

		/*
		int fanOnValue = controlBounds.getFanTemperatureOnOffValue();
		int tempWarningValue = controlBounds.getTemperatureWarningValue();
		int tempShutdownBoundsValue = controlBounds.getTemperatureShutdownValue();
		int stageTimeLimitValue = controlBounds.getStageTimeLimitValue();
		int currentLimitValue = controlBounds.getCurrentLimitValue();
		*/
		
		commands.setBoundsValues();
	}

	void jMenuDiagnosticSendHeatingProfile_actionPerformed(ActionEvent event) {
		logger.info("Send Heating Profile");

		commands.sendHeatingProfile();
	}

	void jMenuFileNew_actionPerformed(ActionEvent event) {
		logger.info("new profile selected");

		// need to generate an action event after new profile
		heatingProfile.newHeatingProfile();
		heatingProfilePanel.heatingProfileTableModel.fireTableDataChanged();

		if (heatingProfile.isRunningProfile() == true) {
			JOptionPane.showMessageDialog(this, "Profile is running - Please stop it first.", "Control",
					JOptionPane.ERROR_MESSAGE);
		} else {
			// heatingProfile.newProfile();
		}
	}

	// File | Open action performed
	public void jMenuFileOpen_actionPerformed(ActionEvent event) {
		if (getFilenameForOpening() ) {
			heatingProfile.clearHeatingProfile();
			heatingProfile.openHeatingProfile();
			heatingProfilePanel.getHeatingProfileTabelModel().fireTableDataChanged();
		}
	}

	public void jMenuFileAddStage_actionPerformed(ActionEvent event) {
		new AddHeatingStageDialog(this, heatingProfilePanel, controlBounds);
		int totalRunTime = heatingProfile.calculateTotalRunTime(); // in seconds

		logger.info("total run time: " + totalRunTime * 60);
		heatingTimerPanel.setTimeFromHeatingProfile();
		heatingTimerPanelStage.setTimeFromHeatingProfile();
		int lastRow = heatingProfile.getSize() - 1;
		logger.info("last row number in table: " + lastRow);
		heatingProfilePanel.getHeatingProfileTabelModel().fireTableDataChanged();
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
			heatingProfilePanel.getHeatingProfileTabelModel().fireTableDataChanged();
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
		heatingProfilePanel.getHeatingProfileTabelModel().fireTableDataChanged();
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
		heatingProfilePanel.getHeatingProfileTabelModel().fireTableDataChanged();
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
		
		if (heatingProfile.exitCheck() ) {
			System.exit(0);
		}
	}

	// Create the edit menu.
	protected JMenu createEditMenu() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// These actions come from the default editor kit.
		// Get the ones we want and stick them in the menu.
		/*
		 ActionMap am = textComponent.getActionMap();
	       Action paste = am.get("paste-from-clipboard");
	       Action copy = am.get("copy-to-clipboard");
	       Action cut = am.get("cut-to-clipboard");

	       cut.putValue(Action.NAME, "Cut");
	       copy.putValue(Action.NAME, "Copy");
	       paste.putValue(Action.NAME, "Paste");

	       JPopupMenu popup = new JPopupMenu("My Popup");
	       textComponent.setComponentPopupMenu(popup);
	       popup.add(new JMenuItem(cut));
	       popup.add(new JMenuItem(copy));
	       popup.add(new JMenuItem(paste));
		*/
		// undoAction = new UndoAction();
		// menu.add(undoAction);

		// redoAction = new RedoAction();
		// menu.add(redoAction);
		
		// jMenuEditCopy.setText("Copy");
		
		final int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
		
		
		// JMenuItem menuItem = null;
        
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem menuItem = new JMenuItem(new DefaultEditorKit.CutAction() );
        menuItem.setText("Cut");
        menuItem.setMnemonic(KeyEvent.VK_X);
        
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
         
        editMenu.add(menuItem);

        menuItem = new JMenuItem(new DefaultEditorKit.CopyAction() );
        menuItem.setText("Copy");
        menuItem.setMnemonic(KeyEvent.VK_C);
        
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
        
        editMenu.add(menuItem);

        menuItem = new JMenuItem(new DefaultEditorKit.PasteAction() );
        menuItem.setText("Paste");
        menuItem.setMnemonic(KeyEvent.VK_V);
        
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
        
        editMenu.add(menuItem);
		
		return editMenu;
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
					commands.setBoundsValuesNoDialog();
					
					commands.resetState();
				}

			} catch (Exception error) {
				logger.info("serial port is already open.");

				JOptionPane.showMessageDialog(this, "Serial Port is Aleady open", "Control",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	void jMenuControlCloseSerialPort_actionPerformed(ActionEvent event) {
		logger.info("trying to close the serial port");

		if (serialCommunications.isPortOpen()) {
			boolean success = serialCommunications.closePort();
			if (success) {
				JOptionPane.showMessageDialog(this, "The Serial Port closed.", "Control",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "The Serial Port did not close.", "Control",
						JOptionPane.ERROR_MESSAGE);
			}

		} else {
			logger.info("serial port is already closed.");

			JOptionPane.showMessageDialog(this, "The serial port is aleady closed", "Control",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	void jMenuControlStart_actionPerformed(ActionEvent e) {
		System.out.println("starting");

		System.out.println("filamentsSet from start: " + systemState.isFilamentsSet());
		if (!systemState.isFilamentsSet()) {
			System.out.println("Number of filaments is not set");

			JOptionPane.showMessageDialog(this, "Please estimate filaments before setting the current",
					"Control", JOptionPane.ERROR_MESSAGE);
			return;
		}

		controlPanel.startHeatingProfile();

		/*
		 * // disable menus jMenuDiagnosticSetControlBounds.setEnabled(false);
		 * jMenuControlSelectSerialPort.setEnabled(false);
		 * jMenuControlOpenSerialPort.setEnabled(false);
		 * jMenuControlCloseSerialPort.setEnabled(false);
		 * jMenuControlStart.setEnabled(false);
		 * jMenuContolEstimateFilaments.setEnabled(false);
		 * jMenuDiagnosticSetRelay.setEnabled(false);
		 * jMenuControlSetNumberOfFilaments.setEnabled(false);
		 * jMenuDiagnosticUploadFirmware.setEnabled(false);
		 * jMenuDiagnosticSetCurrent.setEnabled(false);
		 * jMenuDiagnosticSendHeatingProfile.setEnabled(false);
		 * jMenuDiagnosticSendControlBounds.setEnabled(false);
		 * jMenuFileNew.setEnabled(false); jMenuFileOpen.setEnabled(false);
		 * jMenuFileAddStage.setEnabled(false); jMenuFileRemoveStage.setEnabled(false);
		 * jMenuFileEditStage.setEnabled(false); jMenuFileInsertStage.setEnabled(false);
		 */
	}

	void jMenuControlStop_actionPerformed(ActionEvent event) {
		logger.info("stopping");
		controlPanel.abortHeatingProfile();

		/*
		 * // disable menus jMenuDiagnosticSetControlBounds.setEnabled(true);
		 * jMenuControlSelectSerialPort.setEnabled(true);
		 * jMenuControlOpenSerialPort.setEnabled(true);
		 * jMenuControlCloseSerialPort.setEnabled(true);
		 * jMenuControlStart.setEnabled(true);
		 * jMenuContolEstimateFilaments.setEnabled(true);
		 * jMenuDiagnosticSetRelay.setEnabled(true);
		 * jMenuControlSetNumberOfFilaments.setEnabled(true);
		 * jMenuDiagnosticUploadFirmware.setEnabled(true);
		 * jMenuDiagnosticSetCurrent.setEnabled(true);
		 * jMenuDiagnosticSendHeatingProfile.setEnabled(true);
		 * jMenuDiagnosticSendControlBounds.setEnabled(true);
		 * jMenuFileOpen.setEnabled(true); jMenuFileAddStage.setEnabled(true);
		 * jMenuFileRemoveStage.setEnabled(true); jMenuFileEditStage.setEnabled(true);
		 * jMenuFileInsertStage.setEnabled(true);
		 */
	}

	void jMenuControlReset_actionPerformed(ActionEvent event) {
	}

	void jMenuDiagnosticSetRelay_actionPerformed(ActionEvent event) {
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

	void jMenuDiagnosticReadRelay_actionPerformed(ActionEvent event) {
		// commands.readRelayValue();
	}

	void jMenuDiagnosticReadNumberOfFilaments_actionPerformed(ActionEvent event) {
		// commands.readFilamentsValue();
	}

	void jMenuDiagnosticSetNumberOfFilaments_actionPerformed(ActionEvent event) {

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

	public boolean isFilamentsSet() {
		return filamentsSet;
	}

	public void setFilamentsSet(boolean filamentsSet) {
		this.filamentsSet = filamentsSet;
	}

	void jMenuContolEstimateFilaments_actionPerformed(ActionEvent event) {
		commands.estimateFilaments();
	}
	
	void jMenuControlFilamentsEnable_actionPerformed(ActionEvent event) {
		if (systemState.getFilaments() == -1) {
			commands.setFilamentsEnable(true);
			jMenuControlFilamentsEnable.setText("Disable Filaments");
			
		} else {
			commands.setFilamentsEnable(false);
			jMenuControlFilamentsEnable.setText("Enable Filaments");
		}
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

	public void jMenuDiagnosticReadCurrent_actionPerformed(ActionEvent event) {
		// commands.readCurrentValue();
	}

	public void jMenuDiagnosticReadVacuum_actionPerformed(ActionEvent event) {
		// commands.readVacuumValue();
	}

	public void jMenuDiagnosticReadTemperature_actionPerformed(ActionEvent event) {
		// commands.readTempValue();
	}

	public void jMenuDiagnosticDislayHeatingProfile_actionPerformed(ActionEvent event) {
		commands.showHeatingProfile();
	}

	public boolean sendCommandWithPortCheck(String command) {

		boolean success = false;

		logger.info("send command with running check: " + command);

		logger.info("state of running: " + heatingProfile.isRunningProfile());

		logger.info("state of serial port open: " + serialCommunications.isPortOpen());
		// need some error checking here
		if (!serialCommunications.isPortOpen() ) {

			logger.debug("serial port is closed. trying to open.");
			try {
				serialCommunications.readSerialPortFile();
				serialCommunications.openPort();
			} catch (Exception error) {
				logger.info("error opening the serial port");

				JOptionPane.showMessageDialog(this, "There was a problem opening the serial port", "Control",
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
							"Control", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Profile is already running", "Control",
					JOptionPane.ERROR_MESSAGE);
		}

		if (serialCommunications.isPortOpen()) {
			serialCommunications.setCommandName(command);
			success = serialCommunications.sendCommand(command);
		}

		return success;
	}

	void jMenuDiagnosticReadFan_actionPerformed(ActionEvent event) {
		// commands.readFan();
	}

	void jMenuDiagnosticSetFan_actionPerformed(ActionEvent event) {
		/*
		if (commands.isFanOn()) {
			jMenuDiagnosticSetFan.setText("Turn On Relay Fan");
			commands.setFan(false);
		} else {
			jMenuDiagnosticSetFan.setText("Turn Off Relay Fan");
			commands.setFan(true);
		}
		*/
	}
	
	void JMenuDiagnosticShowLogPanel_actionPerformed(ActionEvent event) {
		
		if (!showLogPanel) {
			logPanel.setCaratToEnd();
			getContentPane().add(logPanel, BorderLayout.SOUTH);
			showLogPanel = true;
		} else {
			getContentPane().remove(logPanel);
			showLogPanel = false;
		}
		
		getContentPane().revalidate();
		getContentPane().repaint();
	}

}