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
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	GradientButton startButton;
	JFrame frame;
	HeatingProfile heatingProfile;
	TimerPanel heatingTimerPanel;
	TimerPanelStage heatingTimerPanelStage;
	Commands commands;
	SystemState systemState;

	public ControlPanel(JFrame frame, HeatingProfile heatingProfile, TimerPanel heatingTimerPanel,
			TimerPanelStage heatingTimerPanelStage, GradientButton startButton, Commands commands,
			SystemState systemState) {

		this.frame = frame;
		this.heatingProfile = heatingProfile;

		this.startButton = startButton;
		this.heatingTimerPanel = heatingTimerPanel;
		this.heatingTimerPanelStage = heatingTimerPanelStage;
		this.commands = commands;
		this.systemState = systemState;

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

		// startButton = new JButton("<html><font color = \"red\"
		// size=\"20\">Start</font></html>");
		// startButton = new GradientButton("<html><font size=\"20\">Start Heating
		// Profile</font></html>");
		startButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		// startButton.setForeground(Color.RED);
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startButton_actionPerformed(e);
			}
		});

		// startButton.setPreferredSize(new Dimension(screenSize.width - 100, 100) );
		this.setPreferredSize(new Dimension(this.getWidth(), 100));

		gridButton.gridx = 0;
		gridButton.gridy = 0;
		// gridButton.fill = GridBagConstraints.NONE;
		gridbagButton.setConstraints(startButton, gridButton);

		this.add(startButton);
	}

	/*
	public boolean isFilamentsSet() {

		JOptionPane.showMessageDialog(this, "Need to Set the Number of Filaments before running", "Error Message",
				JOptionPane.ERROR_MESSAGE);

		return filamentsSet;
	}
*/
	
	void startButton_actionPerformed(ActionEvent event) {
		startHeatingProfile();
	}

	/*
	void setRunning(boolean state) {

		if (state == true) {
			startButton.setText("<html><font size=\"20\">Abort</font></html>");
			heatingProfile.setRunningProfile(true);
		} else {
			startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");
			heatingProfile.setRunningProfile(true);
		}
	}
	*/

	void abortHeatingProfile() {
		// heatingProfile.setRunningProfile(false);
		// startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");

		heatingTimerPanel.cancelTimer();
		heatingTimerPanelStage.cancelTimer();
		commands.stopRun();
	}

	void startHeatingProfile() {
		if (systemState.isRunState()) {
			heatingProfile.setRunningProfile(false);
			// startButton.setText("<html><font size=\"20\">Start Heating Profile</font></html>");

			heatingTimerPanel.cancelTimer();
			heatingTimerPanelStage.cancelTimer();
			commands.stopRun();
		} else {

			// make sure number of filaments is set
			if (!systemState.isFilamentsSet() ) {
				logger.info("Number of filaments is not set");

				// JOptionPane.showMessageDialog(this, "Please estimate filaments before setting the current",
				// 		"Control Message", JOptionPane.ERROR_MESSAGE);
				
				commands.estimateFilaments();
				return;
			}

			int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to run this profile?",
					"Confirm Run", JOptionPane.OK_CANCEL_OPTION);

			logger.info("confirmation dialog result: " + result);
			if (result == 0) {
				if (!profileCheck() && systemState.isFilamentsSet() ) {
					commands.startRun();
					
					/*
					try {
						Thread.sleep(500);
					} catch (Exception error) {
						logger.info("couldn't sleep. " + error); 
					}
					if (systemState.isRunState()) {
						// startButton.setText("<html><font size=\"20\">Abort Heating Profile</font></html>");

						heatingTimerPanel.setTimeFromHeatingProfile();
						heatingTimerPanel.start();

						heatingTimerPanelStage.setTimeFromHeatingProfile();
						heatingTimerPanelStage.start();
					} else {
						logger.info("serial port comm start error");
					}
					*/
				}
			}
		}
	}

	public boolean profileCheck() {

		// true is an error, false is OK
		if (heatingProfile.calculateTotalRunTime() <= 0) {

			// flag error
			JOptionPane.showMessageDialog(this, "The heating profile doesn't have a valid run time",
					"Heating Profile Message", JOptionPane.ERROR_MESSAGE);

			return true;
		}

		if (heatingProfile.isZeroTimeStage() == true) {

			// flag error
			JOptionPane.showMessageDialog(this, "One of the heating profile stages doesn't have a valid run time",
					"Heating Profile Message", JOptionPane.ERROR_MESSAGE);

			return true;
		}

		logger.info("go ahead to run.");
		return false;
	}
}