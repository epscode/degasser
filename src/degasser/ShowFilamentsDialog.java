package degasser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

public class ShowFilamentsDialog extends CenterDialogBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JButton okButton, cancelButton;
	JFrame frame;
	boolean filamentsSet = false;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	public ShowFilamentsDialog(JFrame frame) {

		super(frame);

		this.frame = frame;
		this.setModal(false);
	}

	void showDialog(String message, String title, boolean filamentsSet) {

		logger.info("showing message dialog " + message);

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		this.setTitle(title);
		this.filamentsSet = filamentsSet;

		JPanel p = new JPanel();

		c.fill = GridBagConstraints.HORIZONTAL;
		p.setLayout(gridbag);

		c.insets = new Insets(10, 10, 10, 10);

		JLabel messageLabel = new JLabel(message);
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		gridbag.setConstraints(messageLabel, c);
		p.add(messageLabel);

		// layout for the button panel
		GridBagLayout gridbagButton = new GridBagLayout();
		GridBagConstraints gridButton = new GridBagConstraints();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(gridbagButton);
		// gridButton.fill = GridBagConstraints.HORIZONTAL;
		gridButton.insets = new Insets(0, 0, 0, 0);
		gridButton.weightx = 1.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(buttonPanel, c);
		p.add(buttonPanel);

		/*
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});

		gridButton.fill = GridBagConstraints.NONE;
		gridButton.gridwidth = 1;
		gridButton.gridheight = 1;
		gridButton.weightx = 0.5;
		gridButton.gridx = 0;
		gridButton.gridy = 0;
		gridButton.ipady = 0;
		gridbagButton.setConstraints(cancelButton, gridButton);
		buttonPanel.add(cancelButton);
		*/
		
		okButton = new JButton("OK");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});

		// gridButton.weightx = 0.5;
		gridButton.gridx = 0;
		gridButton.gridy = 0;
		gridbagButton.setConstraints(okButton, gridButton);
		buttonPanel.add(okButton);

		this.getContentPane().add(p);
		setSize(400, 150);
		setVisible(true);

		getContentPane().add(p);

		this.getRootPane().setDefaultButton(okButton);
		setVisible(true);
	}

	public void okButton_actionPerformed(ActionEvent event) {

		setVisible(false);
		filamentsSet = true;
		dispose();
		
		filamentsSet = true;
	}

	public void cancelButton_actionPerformed(ActionEvent ae) {

		setVisible(false);
		filamentsSet = false;
		dispose();
	}
}
