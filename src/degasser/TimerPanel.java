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
	boolean seconds = false;

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
		timer = new Timer(0, new ActionListener() {

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
	
	public void resetHeatingStage() {
		heatingStage = 0;
	}
	
	public void setTime(long duration) {
		text = getStringTime(duration);
		progressLabel.setText("Heating Profile Time Remaining: " + text);
	}
	
	public void setTimebaseSeconds(boolean seconds) {
		seconds = this.seconds;
	}
	
	public void setTimeFromHeatingProfile() {
		// duration = 120000;
		if (seconds) {
			duration = heatingProfile.calculateTotalRunTime() * 1000;
		} else {
			duration = heatingProfile.calculateTotalRunTime() * 1000 * 60;
		}
		setTime(duration);
	}
	
	public long getTimeFromHeatingProfile(int stageNumber) {
		int heatingStageTime = 0;
		
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

	public HeatingProfile getHeatingProfile() {
		return heatingProfile;
	}

	public void setHeatingProfile(HeatingProfile heatingProfile) {
		this.heatingProfile = heatingProfile;
	}
	
	
}
