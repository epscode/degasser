package degasser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.apache.log4j.Logger;


/**
 * @author emorris
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class TimerPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public final static int ONE_SECOND = 1000;

	HeatingProgressElements heatingProgressElements;
	// private JProgressBar progressBar;
	// private JLabel progressLabel;
	// private Timer timer;
	// private LongTask task;
	
	JProgressBar progressBar;
	JLabel progressLabel;
	Timer timer;
	LongTask task;
	HeatingProfile heatingProfile;
	private String newline = "\n";
	long duration;
	String text = "";
	double percent = 1;
	long startTime;
	int heatingStage = 0; 
	GradientButton startButton;
	JMenuBar menu;
	JFrame frame;

	public TimerPanel(JFrame frame, HeatingProfile heatingProfile, GradientButton startButton, JMenuBar menu,
			HeatingProgressElements heatingProgressElements) {
		this.heatingProfile = heatingProfile;
		this.startButton = startButton;
		this.menu = menu;
		this.frame = frame;
		this.heatingProgressElements = heatingProgressElements;
		
		 // this.progressBar = heatingProgressElements.getOverallProgressBar();
		 this.progressLabel = heatingProgressElements.getOverallProgressLabel();
		 // timer = heatingProgressElements.getOverallTimer();
		 // task = heatingProgressElements.getOverallTask();
		
		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		task = new LongTask();

		progressBar = new JProgressBar(0, task.getLengthOfTask() );
		// progressLabel = new JLabel();
		progressLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24) );
		progressLabel.setForeground(Color.BLACK);
		progressBar.setBackground(Color.GRAY);
		
		this.setLayout(new BorderLayout() );
		
		// UIManager.put("ProgressBar.verticalSize", 100);
		
		// setTime(25000);
		// start(25000);
		
		setTime(0);
		// start(getTimeFromHeatingProfile(heatingStage) );
		
		progressBar.setValue(0);
		progressBar.setStringPainted(false);
		// progressBar.setPreferredSize(new Dimension(screenSize.width - 100, 100) );
		// this.setPreferredSize(new Dimension(screenSize.width - 100, 100) );
		progressBar.setUI(new ProgressbarTimer(screenSize.width - 100) );
		
		this.add(progressLabel, BorderLayout.PAGE_START);
		this.add(progressBar, BorderLayout.PAGE_END);
		
		//Create a timer.
		timer = new Timer(ONE_SECOND, new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				long count;
				progressBar.setValue(task.getCurrent() );
				if (startTime == -1) {
					count = duration;
					startTime = System.currentTimeMillis();
				} else {
					count = duration - System.currentTimeMillis() + startTime;
				}
				if (count < 0) {
					logger.debug("made it to stop");
					logger.debug("duration from stop:" + duration);
					timer.stop();
					task.stop();
					heatingProfile.setRunningProfile(false);
					toggleRunningMenuItems(true);
					progressBar.setValue(progressBar.getMinimum() );
					setTimeFromHeatingProfile();
					startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");

					JOptionPane.showMessageDialog(frame, "Heating Profile Completed", 
							"Heating Profile", 
							JOptionPane.INFORMATION_MESSAGE);
					// long localDuration = getTimeFromHeatingProfile(heatingStage);
					// if (localDuration > 0) {
					// 	start(getTimeFromHeatingProfile(heatingStage) );
					// 	heatingStage++;
					// }
					
					// setTime(25000);
					// start(25000);
				} else {
					text = getStringTime(count);
					// text = getStringTime(duration);
					progressLabel.setText("Heating Profile Time Remaining: " + text);
				}	
				repaint();
			}
		});
	}
	
	boolean toggleRunningMenuItems(boolean state) {
		
		/*
		// toggle menus
		menu.getMenu(0).getMenuComponent(0).setEnabled(state);
		menu.getMenu(0).getMenuComponent(1).setEnabled(state);
		menu.getMenu(0).getMenuComponent(2).setEnabled(state);
		menu.getMenu(0).getMenuComponent(3).setEnabled(state);
		menu.getMenu(0).getMenuComponent(4).setEnabled(state);
		menu.getMenu(0).getMenuComponent(5).setEnabled(state);
		menu.getMenu(0).getMenuComponent(9).setEnabled(state);
		
		menu.getMenu(2).getMenuComponent(2).setEnabled(state);
		menu.getMenu(2).getMenuComponent(4).setEnabled(state);
		menu.getMenu(2).getMenuComponent(6).setEnabled(state);
		menu.getMenu(2).getMenuComponent(7).setEnabled(state);
		menu.getMenu(2).getMenuComponent(8).setEnabled(state);
		menu.getMenu(2).getMenuComponent(10).setEnabled(state);
		menu.getMenu(2).getMenuComponent(11).setEnabled(state);
		menu.getMenu(2).getMenuComponent(12).setEnabled(state);
		menu.getMenu(2).getMenuComponent(13).setEnabled(state);
		menu.getMenu(2).getMenuComponent(14).setEnabled(state);
		menu.getMenu(2).getMenuComponent(15).setEnabled(state);
		menu.getMenu(2).getMenuComponent(16).setEnabled(state);
		menu.getMenu(2).getMenuComponent(17).setEnabled(state);
		menu.getMenu(2).getMenuComponent(19).setEnabled(state);
		menu.getMenu(2).getMenuComponent(20).setEnabled(state);
		menu.getMenu(2).getMenuComponent(21).setEnabled(state);
		menu.getMenu(2).getMenuComponent(22).setEnabled(state);
		menu.getMenu(2).getMenuComponent(23).setEnabled(state);
		menu.getMenu(2).getMenuComponent(25).setEnabled(state);
		menu.getMenu(2).getMenuComponent(26).setEnabled(state);
		*/ 
		
		/*
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
		jMenuFile.add(jMenuFileNew);
		jMenuFileOpen.setEnabled(false);
		jMenuFileAddStage.setEnabled(false);
		jMenuFileRemoveStage.setEnabled(false);
		jMenuFileEditStage.setEnabled(false);
		jMenuFileInsertStage.setEnabled(false);
		*/
		
		return true;
	}
	
	public void resetHeatingStage() {
		heatingStage = 0;
	}
	
	public void setTime(long duration) {
		text = getStringTime(duration);
		progressLabel.setText("Heating Profile Time Remaining: " + text);
	}
	
	public void setTimeFromHeatingProfile() {
		// duration = 120000;
		duration = heatingProfile.calculateTotalRunTime() * 1000 * 60;
		setTime(duration);
	}
	
	public long getTimeFromHeatingProfile(int stageNumber) {
		long heatingStageTime = 0;
		
		if (stageNumber < heatingProfile.getSize() ) {
			heatingStageTime = heatingProfile.getHeatingStage(stageNumber).getTime();
		}
		
		logger.info("heating stage time from getTimeFromHeating: " + heatingStageTime);
		return heatingStageTime;
	}

	private static final String getStringTime(long millis) {
		// logger.debug("number of millis from getStringTime: " + millis);
		int seconds = (int) (millis / 1000.0);
		int minutes = (int) (seconds / 60.0);
		// int hours = (int) (minutes / 60);
		// minutes -= hours * 60;
		// seconds -= (hours * 3600) + (minutes * 60);
		seconds -= (minutes * 60);
		return (
			// ((hours < 10) ? "0" + hours : "" + hours)
			//	+ ":"
				((minutes < 10) ? "0" + minutes : "" + minutes)
				+ ":"
				+ ((seconds < 10) ? "0" + seconds : "" + seconds));
	}

	/*
	public void startTimer(int duration) {
		task.setLengthOfTask(duration);
		progressBar.setMaximum(300);
		task.go();
		timer.start();
	}
	*/
	
	public void start() { 
		
		if (heatingProfile.isRunningProfile() ) {
			logger.debug("starting the timerBar without setting the time");
			// duration = getTimeFromHeatingProfile(heatingStage);
			logger.info("duration from start: " + duration);
			// duration = millisSeconds; 
			startTime = -1; 
			task.setLengthOfTask( (int) duration);
			progressBar.setMaximum( (int) duration / 1000);
			task.go();
			timer.setInitialDelay(0); 
			timer.start(); 
		}
	}
	
	public void start(long duration) { 
		logger.debug("starting the timerBar");
		this.duration = duration;
		// duration = millisSeconds; 
		startTime = -1; 
		task.setLengthOfTask( (int) duration);
		progressBar.setMaximum( (int) duration / 1000);
		task.go();
		timer.setInitialDelay(0); 
		timer.start(); 
	}

	public void cancelTimer() {
		timer.stop();
		task.stop();
		// setTime(0);
		setTimeFromHeatingProfile();
		progressBar.setValue(progressBar.getMinimum() );
	}
	
	public void setIndeterminateTimer(boolean enableIndeterminate) {
		progressBar.setIndeterminate(enableIndeterminate); 
	}
}
