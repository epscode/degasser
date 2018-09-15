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

public class HeatingStageTimerPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public final static int ONE_SECOND = 1000;

	private JProgressBar progressBar;
	HeatingProfile heatingProfile;
	private JLabel progressLabel;
	private Timer timer;
	private LongTask task;
	private String newline = "\n";
	long duration;
	String text = "";
	double percent = 1;
	long startTime;
	int heatingStage = 0; 

	public HeatingStageTimerPanel(HeatingProfile heatingProfile) {
		this.heatingProfile = heatingProfile;
		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int width = this.getWidth();
		
		setBackground(Color.BLACK);
		task = new LongTask();

		progressBar = new JProgressBar(0, task.getLengthOfTask() );
		progressBar.setUI(new ProgressbarTimer(screenSize.width - 25) );
		// progressLabel = new JLabel();
		// progressLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24) );
		// progressLabel.setForeground(Color.RED);
		
		// this.setLayout(new BorderLayout() );
		
		// setTime(0);
		
		// progressBar.setValue(0);
		// progressBar.setStringPainted(false);
		// progressBar.setPreferredSize(new Dimension(screenSize.width - 100, 100) );
		// this.setPreferredSize(new Dimension(screenSize.width - 100, 100) );
		
		// this.add(progressBar, BorderLayout.PAGE_START);
		// this.add(progressLabel, BorderLayout.PAGE_END);
		setLayout(new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        add(progressBar, gbc);
        
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
					progressBar.setValue(progressBar.getMinimum() );
					long localDuration = getTimeFromHeatingProfile(heatingStage);
					if (localDuration > 0) {
						start(getTimeFromHeatingProfile(heatingStage) );
						heatingStage++;
					}
					
					// setTime(25000);
					// start(25000);
				}
				text = getStringTime(count);
				progressLabel.setText(text);
				
				repaint();
			}
		});
	}
	
	public void resetHeatingStage() {
		heatingStage = 0;
	}
	
	public void setTime(long duration) {
		text = getStringTime(duration);
		progressLabel.setText(text);
	}
	
	public void setTimeFromHeatingProfile() {
		duration = getTimeFromHeatingProfile(0);
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
	
	public void setTime(int duration) {
		this.duration = duration;
		task.setLengthOfTask( (int) duration * 1000);
		progressBar.setMaximum( (int) duration);
		// task.setLengthOfTask(duration);
		// task.go();
	}
	
	public void startTimer() {
		
		task.go();
		timer.setInitialDelay(0);
		timer.start(); 
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
		progressBar.setValue(progressBar.getMinimum());
	}
	
	public void setIndeterminateTimer(boolean enableIndeterminate) {
		progressBar.setIndeterminate(enableIndeterminate); 
	}
}
