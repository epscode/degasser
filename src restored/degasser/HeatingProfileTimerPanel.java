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

public class HeatingProfileTimerPanel extends JPanel {
	
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
	int duration;
	String text = "";
	double percent = 1;
	long startTime;
	int heatingStage = 0; 

	public HeatingProfileTimerPanel(HeatingProfile heatingProfile) {
		this.heatingProfile = heatingProfile;
		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		setBackground(Color.GRAY);
		// this.setSize(new Dimension(screenSize.width - 500, 70) );
		task = new LongTask();

		duration = 30;
		task.setLengthOfTask(duration);
		// 0, task.getLengthOfTask() 
		progressBar = new JProgressBar(0, task.getLengthOfTask() );
		progressBar.setUI(new ProgressbarTimer(screenSize.width - 100) );
		
		progressLabel = new JLabel();
		progressLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24) );
		progressLabel.setForeground(Color.RED);
		
		// progressBar.setValue(0);
		// progressBar.setStringPainted(false);
		// progressBar.setPreferredSize(new Dimension(screenSize.width - 100, 50) );

		// this.setPreferredSize(new Dimension(screenSize.width - 100, 100) );
		
		setLayout(new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        add(progressBar, gbc);
		// this.add(progressLabel, BorderLayout.PAGE_END);
        
        timer = new Timer(ONE_SECOND, new ActionListener() {

			private int count = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				progressBar.setValue(task.getCurrent() );
				// normalPB.setValue(count);
				count++;
				if (count >= 100) {
					( (Timer) e.getSource() ).stop();
				}
			}
		});
        //timer.start();
	}
	
	public long getTimeFromHeatingProfile(int stageNumber) {
		long heatingStageTime = 0;
		
		if (stageNumber < heatingProfile.getSize() ) {
			heatingStageTime = heatingProfile.getHeatingStage(stageNumber).getTime();
		}
		
		logger.info("heating stage time from getTimeFromHeating: " + heatingStageTime);
		return heatingStageTime;
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
	
	public static final String getStringTime(long millis) {
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
}