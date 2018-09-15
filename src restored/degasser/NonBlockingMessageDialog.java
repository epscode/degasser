package degasser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

public class NonBlockingMessageDialog extends CenterDialogBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JButton okButton;
	JFrame frame;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	public NonBlockingMessageDialog(JFrame frame) {

		super(frame);
		setSize(700, 100);
		
		this.frame = frame;
		this.setModal(false);	
	}

	void showDialog(String message, String title) {

		logger.info("showing message dialog " + message);

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		this.setTitle(title);

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
		gridButton.fill = GridBagConstraints.HORIZONTAL;
		gridButton.insets = new Insets(0, 0, 0, 0);
		gridButton.weightx = 1.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(buttonPanel, c);
		p.add(buttonPanel);

		okButton = new JButton("OK");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});

		gridButton.gridx = 0;
		gridButton.gridy = 0;
		gridButton.fill = GridBagConstraints.NONE;
		gridbagButton.setConstraints(okButton, gridButton);
		buttonPanel.add(okButton);

		getContentPane().add(p);
		
		this.getRootPane().setDefaultButton(okButton);
		setVisible(true);
	}

	public void okButton_actionPerformed(ActionEvent event) {

		setVisible(false);
		dispose();
	}
}
