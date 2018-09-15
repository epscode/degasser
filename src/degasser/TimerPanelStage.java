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

public class TimerPanelStage extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public final static int ONE_SECOND = 1000;

	// private JProgressBar progressBar;
	// private JLabel progressLabel;
	// private Timer timer;
	
	HeatingProfile heatingProfile;
	HeatingProfilePanel heatingProfilePanel;
	private String newline = "\n";
	long duration;
	String text = "";
	double percent = 1;
	long startTime;
	int heatingStage = 1; 
	int cumlativeTimeOffset = 0;
	GradientButton startButton;
	
	HeatingProgressElements heatingProgressElements;
	// private JProgressBar progressBar;
	// private JLabel progressLabel;
	// private Timer timer;
	
	JProgressBar progressBar;
	JLabel progressLabel;
	Timer timer;
	LongTask task;

	public TimerPanelStage(HeatingProfile heatingProfile, HeatingProfilePanel heatingProfilePanel, GradientButton startButton, 
			HeatingProgressElements heatingProgressElements) {
		this.heatingProfile = heatingProfile;
		this.heatingProfilePanel = heatingProfilePanel;
		this.startButton = startButton;
		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		// progressBar = heatingProgressElements.getStageProgressBar();
		this.progressLabel = heatingProgressElements.getStageProgressLabel();
		// timer = heatingProgressElements.getStageTimer();
		// task = heatingProgressElements.getStageTask();
		
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
				
				progressBar.setValue(task.getCurrent() - (cumlativeTimeOffset / 1000) );
				logger.info("progressbar: " + progressBar.getValue() );
				if (startTime == -1) {
					count = duration;
					startTime = System.currentTimeMillis();
				} else {
					count = duration - System.currentTimeMillis() + startTime;
					logger.info("count from timer panel: " + duration + ": " + count);
					
				}
				if ( (count < 0) ) {
					logger.debug("made it to stop");
					logger.debug("duration from stop:" + duration);
					timer.stop();
					
					//progressBar.setValue(progressBar.getMinimum() );
					progressBar.setValue(0);
					long localDuration = getTimeFromHeatingProfile(heatingStage);
					if (localDuration > 0) {
						duration = getTimeFromHeatingProfile(heatingStage);
						logger.info("duration from inside timer: " + duration);
						// setTime(duration);
						heatingProfilePanel.setExecutingStage(heatingStage);
						count = 0;
						
						logger.info("heatingStage: " + heatingStage);
						cumlativeTimeOffset = 0;
						for (int stageIndex = 0; stageIndex < heatingStage; stageIndex++) {	
								cumlativeTimeOffset += getTimeFromHeatingProfile(stageIndex);
								logger.info("offset: " + cumlativeTimeOffset);
								logger.info("stageIndex: " + stageIndex);
								logger.info("heatingStage: " + heatingStage);				
						}
						
						heatingStage++;
						startTime = -1;
						// task.done();
						task.setLengthOfTask( (int) duration);
						logger.info("duration div 1000: " + ( (int) (duration / 1000) ) );
						progressBar.setMinimum(0);
						progressBar.setMaximum( (int) (duration / 1000) );
						
						// task.go();
						timer.setInitialDelay(0); 
						timer.start(); 
						// start(duration);
					} else {
						logger.info("resetting the timer");
						heatingStage = 1;
						heatingProfilePanel.setExecutingStage(0);
						task.stop();
						setTimeFromHeatingProfile();
					}
					
					// setTime(25000);
					// start(25000);
				} else {
					text = getStringTime(count);
					// text = getStringTime(duration);
					progressLabel.setText("Heating Stage Time Remaining: " + text);
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
		progressLabel.setText("Heating Stage Time Remaining: " + text);
	}
	
	public void setTimeFromHeatingProfile() {
		System.out.println("is this firing?");
		// duration = 120000;
		duration = heatingProfile.getHeatingStage(heatingProfile.getSelectedStage() ).getTime() * 1000 * 60;
		// heatingProfilePanel.setExecutingStage(0);
		
		setTime(duration);
	}
	
	public long getTimeFromHeatingProfile(int stageNumber) {
		long heatingStageTime = 0;
		
		if (stageNumber < heatingProfile.getSize() ) {
			logger.info("there is another stage in this profile.");
			heatingStageTime = heatingProfile.getHeatingStage(stageNumber).getTime() * 60 * 1000;
		} else {
			logger.info("there is NOT another stage in this profile.");
		}
		setTime(heatingStageTime);
		
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
			heatingStage = 1;
			cumlativeTimeOffset = 0;
			// duration = getTimeFromHeatingProfile(heatingStage);
			logger.info("duration from start: " + duration);
			logger.info("duration from start div 1000: " + ( (int) (duration / 1000) ) ); 
			// duration = millisSeconds; 
			startTime = -1; 
			task.setLengthOfTask( (int) duration);
			progressBar.setMinimum(0);
			progressBar.setMaximum( (int) (duration / 1000) );
			heatingProfilePanel.setExecutingStage(0);
			task.go();
			timer.setInitialDelay(0); 
			timer.start(); 
		}
	}
	
	/*
	public void start(long duration) { 
		logger.debug("starting the timerBar");
		
		this.duration = duration;
		
		cumlativeTimeOffset = 0;
		heatingStage = 1;
		duration = getTimeFromHeatingProfile(heatingStage);
		// duration = millisSeconds; 
		startTime = -1; 
		logger.info("from the start function: " + duration);
		logger.info("duration from start: " + duration);
		logger.info("duration from start div 1000: " + ( (int) (duration / 1000) ) ); 
		task.setLengthOfTask( (int) duration);
		progressBar.setValue(0);
		progressBar.setMinimum(0);
		progressBar.setMaximum( (int) duration / 1000);
		heatingProfilePanel.setExecutingStage(0);
		task.go();
		timer.setInitialDelay(0); 
		timer.start(); 
	}
	*/

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
