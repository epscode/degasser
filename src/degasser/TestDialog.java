package degasser;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;
import java.util.Vector;

public class TestDialog extends CenterDialogBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JButton okButton, cancelButton;
	
	boolean cancel;
	
	JComboBox<String> heatingProfileList;
	DecimalFormat integerDigit = new DecimalFormat("#0");
	HeatingProfilePanel heatingProfilePanel;
	
	// JFormattedTextField timeField;
	// JFormattedTextField relayField;
	
	HighlightedFormattedTextField timeField;
	HighlightedFormattedTextField relayField;
	
	// JFrame frame;
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public TestDialog(JFrame frame, HeatingProfilePanel heatingProfilePanel) {
		
		super(frame);
		setSize(325, 250);

		this.heatingProfilePanel = heatingProfilePanel;
		// this.frame = frame;
		// this.setModal(true);
		
		cancel = false;
	
	
	
		
		/*
		NumberFormat format = NumberFormat.getInstance(java.util.Locale.US);
	    NumberFormatter formatter = new NumberFormatter(format);
	    
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(0);
	    formatter.setMaximum(Integer.MAX_VALUE);
	    formatter.setAllowsInvalid(false);
	    formatter.setCommitsOnValidEdit(true);
	    formatter.setAllowsInvalid(false);
		*/
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		JPanel p = new JPanel();

		c.fill = GridBagConstraints.HORIZONTAL;
		p.setLayout(gridbag);

		c.insets = new Insets(10, 10, 10, 10);
		
		JLabel heatingProfileLabel =
			new JLabel("Stage");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		gridbag.setConstraints(heatingProfileLabel, c);
		p.add(heatingProfileLabel);
		
		heatingProfileList = new JComboBox<String>(getHeatingProfileStages() );
		
		/*
		heatingProfileList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				heatingProfileList_actionPerformed(e);
			}
		});
		*/

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(heatingProfileList, c);
		// p.add(heatingProfileList);
			
		JLabel timeLabel = new JLabel("Time (minutes)");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(timeLabel, c);
		p.add(timeLabel);
			
		timeField = new HighlightedFormattedTextField(integerDigit);
		// timeField.setFormatterFactory(tf);
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(timeField, c);
		p.add(timeField);
		
		JLabel relayLabel = new JLabel("Current (mA)");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(relayLabel, c);
		p.add(relayLabel);
			
		relayField = new HighlightedFormattedTextField(integerDigit);
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(relayField, c);
		p.add(relayField);
		
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
		c.gridy = 3;
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
		
		this.getRootPane().setDefaultButton(okButton);
		
		// fillInitialValues();
		
		timeField.setValue(new Integer(0) );
		relayField.setValue(new Integer(0) );
		setVisible(true);
	}
	
	public void heatingProfileList_actionPerformed(ActionEvent event) {
		int stageSelectedIndex = heatingProfileList.getSelectedIndex();
		timeField.setText(new Integer(heatingProfilePanel.heatingProfile.getHeatingStage(stageSelectedIndex).getTime() ).toString() );
		relayField.setText(new Integer(heatingProfilePanel.heatingProfile.getHeatingStage(stageSelectedIndex).getRelay() ).toString() );
	}
	
	public void fillInitialValues() {
		int initialStageIndex = heatingProfilePanel.getSelectedStage();
		
		if (initialStageIndex == -1) {
			initialStageIndex = 0;
		}
		
		// heatingProfileList.setSelectedIndex(initialStageIndex);
		timeField.setText(new Integer(heatingProfilePanel.heatingProfile.getHeatingStage(initialStageIndex).getTime() ).toString() );
		relayField.setText(new Integer(heatingProfilePanel.heatingProfile.getHeatingStage(initialStageIndex).getRelay() ).toString() );
	}

	public void okButton_actionPerformed(ActionEvent event) {
		
		boolean exitDialog = true;
		
		/*
		try {
			int stageSelectedIndex = heatingProfileList.getSelectedIndex();
			
			String timeString = timeField.getText();	
			int timeValue = NumberFormat.getNumberInstance(java.util.Locale.US).parse(timeString).intValue();
			// int timeValue =  Integer.parseInt(timeString);
			
			String relayString = relayField.getText();
			// int relayValue =  Integer.parseInt(relayString);
			int relayValue =  NumberFormat.getNumberInstance(java.util.Locale.US).parse(relayString).intValue();
			
			HeatingProfileTableModel heatingProfileTableModel = heatingProfilePanel.getHeatingProfileTabelModel();
			
			heatingProfileTableModel.getHeatingProfile().editHeatingStage(stageSelectedIndex, timeValue, relayValue);
			
		} catch (NumberFormatException error) {
			logger.info(
				"Something is wrong with one of the values you entered."
					+ " Couldn't convert it to a number. + error");
			
			JOptionPane.showMessageDialog(this,
			    "Something is wrong with one of the numbers entered. " + error,
			    "Input Error",
			    JOptionPane.ERROR_MESSAGE);
			
			exitDialog = false;

			// throw up an error if something is wrong
		} catch (Exception error) {
		
			JOptionPane.showMessageDialog(this,
					"An unknown error has occurred in the heating stage dialog box. " + error, 
					"Input Error",
					JOptionPane.ERROR_MESSAGE);
			
			exitDialog = false;
		}	
		*/
		if (exitDialog) {
			setVisible(false);
			dispose();
		}
	}
	
	/*
	public void debug() {
		
		for (int stageNumber = 0; stageNumber < heatingProfilePanel.getH.getSize(); stageNumber++) {
			
			HeatingStage heatingStage = heatingProfile.getHeatingStage(stageNumber);
			
			for (int field = 0; field < 3; field++) {
				logger.debug("stageNumber: " + heatingStage.getStageNumber() + 
						" timer: " +  heatingStage.getTime() + "relay: " +  heatingStage.getRelay() );
			}
		}
	}
*/
	public void cancelButton_actionPerformed(ActionEvent ae) {
		
		cancel = true;
		
		setVisible(false);
		dispose();
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