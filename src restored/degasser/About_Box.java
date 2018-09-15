package degasser;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.apache.log4j.Logger;

import java.util.Date;
import java.text.*;

/**
 * @author Eli Morris
 * @version 1.0
 * @link http://es.ucsc.edu/~emorris
 * 
 * <p>Title: Degasser</p>
 * <p>Description: Provides an interface for a vacuum degasser in the Earth & Planetary Sciences Dept.</p>
 * <p>Copyright: Copyright (c) 2018 by Eli Morris & University of California</p>
 * <p>Company: University of California, Santa Cruz (UCSC)</p>
 * <p>License: GNU General Public License, Version 3 </p>
 * <p>For more information, please contact ermorris@ucsc.edu</p>
 * 
 */

public class About_Box extends JDialog implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4512555326869168676L;

	private JLabel label2 = new JLabel();

	private JLabel label3 = new JLabel();

	private JLabel label4 = new JLabel();

	private JLabel label5 = new JLabel();

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	public About_Box(Frame parent) {
		super(parent);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	JButton okButton; 

	// Component initialization
	private void jbInit() throws Exception {

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints grid = new GridBagConstraints();

		JPanel panel = new JPanel();

		panel.setLayout(gridbag);
		grid.fill = GridBagConstraints.HORIZONTAL;

		grid.insets = new Insets(5, 5, 5, 5);

		grid.weightx = 0.5;

		// layout for the main panel
		GridBagLayout gridbagMain = new GridBagLayout();
		GridBagConstraints gridMain = new GridBagConstraints();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(gridbagMain);
		gridMain.fill = GridBagConstraints.HORIZONTAL;
		gridMain.insets = new Insets(5, 5, 5, 5);
		gridMain.weightx = 0.5;
		// grid.gridwidth = 3;
		// grid.weightx = 1.0;
		grid.gridx = 0;
		grid.gridy = 0;
		gridbag.setConstraints(mainPanel, grid);
		panel.add(mainPanel);

		// layout for the button panel
		GridBagLayout gridbagButton = new GridBagLayout();
		GridBagConstraints gridButton = new GridBagConstraints();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(gridbagButton);
		gridButton.fill = GridBagConstraints.HORIZONTAL;
		gridButton.insets = new Insets(0, 0, 0, 0);
		gridButton.weightx = 0.5;
		grid.gridx = 0;
		grid.gridy = 1;
		gridbag.setConstraints(buttonPanel, grid);
		panel.add(buttonPanel);

		// MyReleaseInfo releaseInfo = new MyReleaseInfo();
		int buildNumberInt = MyReleaseInfo.getBuildNumber();
		String versionNumber = MyReleaseInfo.getVersion();
		// String buildTimeStamp = MyReleaseInfo.getBuildTimeStamp();
		Date buildDate = MyReleaseInfo.getBuildDate();
		String emailAddress = MyReleaseInfo.getMail();
		String webSiteAddress = MyReleaseInfo.getHome();
		// String company = MyReleaseInfo.getCompany();
		String copyright = MyReleaseInfo.getCopyright();
		String project = MyReleaseInfo.getProject();

		DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy H:mm aaa z");
		String buildDateString = dateFormat.format(buildDate);

		String buildNumber = "Build #" + buildNumberInt + " on "
				+ buildDateString;
		this.setTitle("About " + project);

		label2.setText("For more information, please visit " + webSiteAddress);
		label3.setText("Copyright " + copyright);
		label4.setText("email: " + emailAddress);
		label5.setText(buildNumber);
		
		// Project and version label	
		JLabel projectLabel = new JLabel(); 
		projectLabel.setFont(new Font("Serif", Font.BOLD, 18));
		projectLabel.setText(project + " Version " + versionNumber);
		gridMain.gridx = 0;
		gridMain.gridy = 0;
		gridbagMain.setConstraints(projectLabel, gridMain);
		mainPanel.add(projectLabel);
		
		// Copyright Label
		JLabel copyrightLabel = new JLabel(); 
		copyrightLabel.setFont(new Font("Serif", Font.PLAIN, 14));
		copyrightLabel.setText("Copyright " + copyright);
		gridMain.gridx = 0;
		gridMain.gridy = 1;
		gridbagMain.setConstraints(copyrightLabel, gridMain);
		mainPanel.add(copyrightLabel);
		
		// build Label
		JLabel buildLabel = new JLabel(); 
		buildLabel.setFont(new Font("Serif", Font.PLAIN, 14));
		buildLabel.setText(buildNumber);
		gridMain.gridx = 0;
		gridMain.gridy = 2;
		gridbagMain.setConstraints(buildLabel, gridMain);
		mainPanel.add(buildLabel);
		
		// website Label
		JLabel websiteLabel = new JLabel(); 
		websiteLabel.setFont(new Font("Serif", Font.PLAIN, 14));
		websiteLabel.setText("For more information, please visit " + webSiteAddress);
		gridMain.gridx = 0;
		gridMain.gridy = 3;
		gridbagMain.setConstraints(websiteLabel, gridMain);
		mainPanel.add(websiteLabel);
		
		// email Label
		JLabel emailLabel = new JLabel(); 
		emailLabel.setFont(new Font("Serif", Font.PLAIN, 14));
		emailLabel.setText("email: " + emailAddress);
		gridMain.gridx = 0;
		gridMain.gridy = 4;
		gridbagMain.setConstraints(emailLabel, gridMain);
		mainPanel.add(emailLabel);
		
		okButton = new JButton();
		okButton.setText("Ok");
		gridButton.gridx = 0;
		gridButton.gridy = 0;
		gridButton.fill = GridBagConstraints.NONE;
		gridbagButton.setConstraints(okButton, gridButton);
		buttonPanel.add(okButton);
		
		getRootPane().setDefaultButton(okButton);
		
		
		okButton.addActionListener(this);
		
		
		getContentPane().add(panel);
		setSize(450, 500);
		setResizable(false);
	}

	// Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			cancel();
		}
		super.processWindowEvent(e);
	}

	// Close the dialog
	void cancel() {
		dispose();
	}

	// Close the dialog on a button event
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			cancel();
		}
	}
}