package degasser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;
import java.util.Vector;

public class RemoveHeatingStageDialog extends CenterDialogBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JButton okButton, cancelButton;
	
	boolean cancel;
	
	JComboBox<String> heatingProfileList;
	
	HeatingProfilePanel heatingProfilePanel;
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public RemoveHeatingStageDialog(JFrame f, HeatingProfilePanel heatingProfilePanel) {

		super(f);
		this.setTitle("Remove Heating Stage");
		this.setModal(true);
		this.heatingProfilePanel = heatingProfilePanel;
		
		cancel = false;
	}
	
	boolean showRemoveHeatingStageDialog() {

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		JPanel p = new JPanel();

		c.fill = GridBagConstraints.HORIZONTAL;
		p.setLayout(gridbag);

		c.insets = new Insets(10, 10, 10, 10);
		
		JLabel heatingProfileLabel =
			new JLabel("Stage To Remove");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		gridbag.setConstraints(heatingProfileLabel, c);
		p.add(heatingProfileLabel);
		
		heatingProfileList = new JComboBox<String>(getHeatingProfileStages() );

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(heatingProfileList, c);
		p.add(heatingProfileList);
		
		// layout for the button panel
		GridBagLayout gridbagButton = new GridBagLayout();
		GridBagConstraints gridButton = new GridBagConstraints();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(gridbagButton);
		gridButton.fill = GridBagConstraints.HORIZONTAL;
		gridButton.insets = new Insets(0, 0, 0, 0);
		gridButton.weightx = 0.5;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(buttonPanel, c);
		p.add(buttonPanel);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});
		
		gridButton.gridx = 0;
		gridButton.gridy = 0;
		gridButton.fill = GridBagConstraints.NONE;
		gridbagButton.setConstraints(cancelButton, gridButton);
		buttonPanel.add(cancelButton);

		okButton = new JButton("OK");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});

		gridButton.gridx = 1;
		gridbagButton.setConstraints(okButton, gridButton);
		buttonPanel.add(okButton);

		getContentPane().add(p);
		setSize(500, 150);
		
		getRootPane().setDefaultButton(okButton);
		
		Dimension frmSize = getSize();
		Point loc = getLocation();
		
		Gui parent = (Gui) getParent();
		Dimension appSize = parent.getSize();
		Point appLocation = parent.getLocation();
		
		Dimension dialogSize = getSize();
		
		logger.debug("frame size width: " + frmSize.width);
		logger.debug("frame size height: " + frmSize.height);
		
		logger.debug("location x: " + loc.x);
		logger.debug("location y: " + loc.y);
		
		logger.debug("application size width: " + appSize.width);
		logger.debug("application size height: " + appSize.height);
		
		logger.debug("application x: " + appLocation.x);
		logger.debug("application y: " + appLocation.y);
		
		this.setLocation(
			(appSize.width - dialogSize.width) / 2 + appLocation.x,
			(appSize.height - dialogSize.height) / 2 + appLocation.y);
		
		setVisible(true);
		
		return cancel;
	}

	public void okButton_actionPerformed(ActionEvent ae) {
		
		int stageSelectedIndex = heatingProfileList.getSelectedIndex();
		
		removeStageFromHeatingProfile(stageSelectedIndex);
		
		setVisible(false);
		dispose();
	}

	public void cancelButton_actionPerformed(ActionEvent ae) {
		
		cancel = true;
		
		setVisible(false);
		dispose();
	}
	
	public void removeStageFromHeatingProfile(int heatingStage) {
		// move all remaining stages up one, unless it is the last one selected
		heatingProfilePanel.getHeatingProfileTabelModel().getHeatingProfile().removeHeatingStage(heatingStage);
	}
	
	public Vector<String> getHeatingProfileStages() {
		
		int numberOfHeatingStages = heatingProfilePanel.getHeatingProfileTabelModel().getHeatingProfile().getSize();
		
		Vector<String> heatingProfileStrings = new Vector<String>();
		
		for (int heatingStage = 0; heatingStage < numberOfHeatingStages; heatingStage++) {
			int stageName = heatingProfilePanel.getHeatingProfileTabelModel().getHeatingProfile().getHeatingStage(heatingStage).getStageNumber();
			heatingProfileStrings.add(Integer.toString(stageName) );
		}
		
		return heatingProfileStrings;
	}
 }