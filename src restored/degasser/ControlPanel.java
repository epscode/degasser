package degasser;

import java.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import org.apache.log4j.Logger;

public class ControlPanel extends JPanel {
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );
	
	GradientButton startButton;
	JFrame frame;
	HeatingProfile heatingProfile;
	TimerPanel heatingTimerPanel;
	TimerPanelStage heatingTimerPanelStage;
	Commands commands;

	
	public ControlPanel(JFrame frame, HeatingProfile heatingProfile, TimerPanel heatingTimerPanel,
	TimerPanelStage heatingTimerPanelStage, GradientButton startButton, Commands commands) {
		
		this.frame = frame;
		this.heatingProfile = heatingProfile;
		
		this.startButton = startButton;
		this.heatingTimerPanel = heatingTimerPanel;
		this.heatingTimerPanelStage = heatingTimerPanelStage;
		this.commands = commands;
		
		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		// layout for the button panel
		GridBagLayout gridbagButton = new GridBagLayout();
		GridBagConstraints gridButton = new GridBagConstraints();
		
		gridButton.weightx = gridButton.weighty = 1.0;
		
		this.setLayout(gridbagButton);
		gridButton.fill = GridBagConstraints.BOTH;
		// gridButton.insets = new Insets(0, 0, 0, 0);
		// gridButton.weightx = 1.0;
		// gridButton.gridheight = screenSize.height;
		// gridButton.gridwidth = screenSize.width;
		
		// startButton = new JButton("<html><font color = \"red\" size=\"20\">Start</font></html>");
		// startButton = new GradientButton("<html><font size=\"20\">Start Heating Profile</font></html>");
		startButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24) );
		// startButton.setForeground(Color.RED);
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startButton_actionPerformed(e);
			}
		});
		
		// startButton.setPreferredSize(new Dimension(screenSize.width - 100, 100) );
		this.setPreferredSize(new Dimension(screenSize.width - 100, 100) );
		
		
		gridButton.gridx = 0;
		gridButton.gridy = 0;
		// gridButton.fill = GridBagConstraints.NONE;
		gridbagButton.setConstraints(startButton, gridButton);
		
		this.add(startButton);
	}
	
	void startButton_actionPerformed(ActionEvent event) {
		startHeatingProfile();
	}
	
	void setRunning(boolean state) {
		
		if (state == true) {
			startButton.setText("<html><font size=\"20\">Abort</font></html>");
			heatingProfile.setRunningProfile(true);
		} else {
			startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
			heatingProfile.setRunningProfile(true);
		}
	}
	
	void abortHeatingProfile() {
			heatingProfile.setRunningProfile(false);
			startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
			
			heatingTimerPanel.cancelTimer();
			heatingTimerPanelStage.cancelTimer();
			commands.stopRun();
	}
	
	void startHeatingProfile() {
		if (heatingProfile.isRunningProfile() ) {
			heatingProfile.setRunningProfile(false);
			startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
			
			heatingTimerPanel.cancelTimer();
			heatingTimerPanelStage.cancelTimer();
			commands.stopRun();
		} else {	
			
			int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to run this profile?",
			        "Confirm Run", JOptionPane.OK_CANCEL_OPTION);
			
			logger.info("confirmation dialog result: " + result);
			if (result == 0) {
				if (!profileCheck() ) {
					// heatingProfile.setRunningProfile(true);
					commands.startRun();
					
					if (heatingProfile.isRunningProfile() ) {
						startButton.setText("<html><font size=\"20\">Abort Heating Profile</font></html>");
						
						heatingTimerPanel.setTimeFromHeatingProfile();
						heatingTimerPanel.start();
						
						heatingTimerPanelStage.setTimeFromHeatingProfile();
						heatingTimerPanelStage.start();
					} else {
						logger.info("serial port comm start error");
					}
				}
			}
		}
	}
	
	public boolean profileCheck() {
		
		// true is an error, false is OK
		if (heatingProfile.calculateTotalRunTime() <= 0) {
			
			// flag error
			JOptionPane.showMessageDialog(this, "The heating profile doesn't have a valid run time", "Heating Profile Message",
					JOptionPane.ERROR_MESSAGE);
			
			return true;
		}
		
		if (heatingProfile.isZeroTimeStage() == true) {
			
			 // flag error
			JOptionPane.showMessageDialog(this, "One of the heating profile stages doesn't have a valid run time", "Heating Profile Message",
					JOptionPane.ERROR_MESSAGE);
			
			return true;
		}
		
		logger.info("go ahead to run.");
		return false;
	}
}