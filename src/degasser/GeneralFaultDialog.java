package degasser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.apache.log4j.Logger;

import java.text.*;

public class GeneralFaultDialog extends CenterDialogBox {

	JButton continueButton, cancel, abort;
	boolean decisionToAbort = false;
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public GeneralFaultDialog( JFrame f, String title, String errorMessage) {

		super(f);

		this.setTitle(title);
		this.setModal(true);
		this.setTitle(errorMessage);

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints grid = new GridBagConstraints();

		JPanel panel = new JPanel();

		grid.fill = GridBagConstraints.HORIZONTAL;
		panel.setLayout(gridbag);

		grid.insets = new Insets(0, 0, 0, 0);
		
		JTextArea message = new JTextArea(errorMessage);
		message.setEditable(false);
		message.setLineWrap(true);
		message.setWrapStyleWord(true);
		message.setBackground(
	      (Color)UIManager.get("Label.background"));
		message.setForeground(
	      (Color)UIManager.get("Label.foreground"));
		message.setFont(
	      (Font)UIManager.get("Label.font"));
		grid.gridx = 0;
		grid.gridy = 0;
		gridbag.setConstraints(message, grid);
		panel.add(message);
	
		// layout for the button panel
		GridBagLayout gridbagButton = new GridBagLayout();
		GridBagConstraints gridButton = new GridBagConstraints();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(gridbagButton);
		gridButton.fill = GridBagConstraints.HORIZONTAL;
		gridButton.insets = new Insets(0, 0, 0, 0);
		gridButton.weightx = 0.5;
		// grid.gridwidth = 3;
		// grid.weightx = 1.0;
		grid.gridx = 0;
		grid.gridy = 1;
		gridbag.setConstraints(buttonPanel, grid);
		panel.add(buttonPanel);

		continueButton = new JButton("Continue");
		continueButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				continueButton_actionPerformed(e);
			}
		});
		
		gridButton.fill = GridBagConstraints.NONE;
		gridButton.gridwidth = 1;
		gridButton.gridheight = 1;
		gridButton.weightx = 0.5;
		gridButton.gridx = 0;
		gridButton.gridy = 0;
		gridButton.ipady = 0;
		
		gridButton.gridx = 0;
		gridButton.gridy = 0;
		
		gridbagButton.setConstraints(continueButton, gridButton);
		buttonPanel.add(continueButton);

		getRootPane().setDefaultButton(continueButton);
		
		abort = new JButton("Abort");
		abort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abortButton_actionPerformed(e);
			}
		});
		
		gridButton.gridx = 1;
		gridButton.gridy = 0;
		
		gridbagButton.setConstraints(abort, gridButton);
		buttonPanel.add(abort);

		getContentPane().add(panel);
		setSize(300, 200);
		setVisible(true);
	}

	public void continueButton_actionPerformed(ActionEvent ae) {
		logger.info("choosing not to abort from the dialog.");
		decisionToAbort = false;
		setVisible(false);
		dispose();
	}
	
	public void abortButton_actionPerformed(ActionEvent ae) {
		logger.info("choosing to abort from the dialog.");
		decisionToAbort = true;
		setVisible(false);
		dispose();
	}
}