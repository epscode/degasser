package degasser;

import java.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;

class SetRelayValueDialog extends CenterDialogBox {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4003036202003129675L;
	JFormattedTextField relayValueField;
	int relayValue = 0;
	boolean okStatus = false;
	JButton ok, cancel;
	
	DecimalFormat integerDigit = new DecimalFormat("##");
	// integerDigit.setValueClass(Integer.class);
	
	// HighlightedFormattedTextField relayValueField;
	
	// IntegerTextField relayValueField;
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public SetRelayValueDialog(JFrame f) {
		
		super(f);
		
		this.setTitle("Set Relay Value");
		this.setModal(true);
		
		NumberFormat format = NumberFormat.getInstance(java.util.Locale.US);
			
	    NumberFormatter formatter = new NumberFormatter(integerDigit);
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(0);
	    formatter.setMaximum(100);
	    formatter.setAllowsInvalid(true);
	    formatter.setCommitsOnValidEdit(true);
	    
		relayValueField = new JFormattedTextField(formatter);
		// relayValueField = new IntegerTextField();
		
		// relayValueField = new HighlightedFormattedTextField(integerDigit);
		
		JLabel relayValueLabel = new JLabel("Relay Value (%)");
		
		// layout 
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints grid = new GridBagConstraints();

		JPanel panel = new JPanel();

		panel.setLayout(gridbag);
		grid.fill = GridBagConstraints.HORIZONTAL;

		grid.insets = new Insets(0, 0, 0, 0);
		
		// layout for the main panel
		GridBagLayout gridbagMain = new GridBagLayout();
		GridBagConstraints gridMain = new GridBagConstraints();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(gridbagMain);
		gridMain.fill = GridBagConstraints.HORIZONTAL;
		gridMain.insets = new Insets(5, 5, 5, 5);
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

		gridMain.weightx = 0.4;
		gridMain.gridx = 0;
		gridMain.gridy = 0;
		gridbagMain.setConstraints(relayValueLabel, gridMain);
		mainPanel.add(relayValueLabel);

		gridMain.gridx = 1;
		gridMain.gridy = 0;
		relayValueField.setColumns(8);
		gridbagMain.setConstraints(relayValueField, gridMain);
		mainPanel.add(relayValueField);
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
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
		gridbagButton.setConstraints(cancel, gridButton);
		buttonPanel.add(cancel);

		ok = new JButton("OK");
		ok.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});

		this.getRootPane().setDefaultButton(ok);

		gridButton.weightx = 0.5;
		gridButton.gridx = 1;
		gridButton.gridy = 0;
		gridbagButton.setConstraints(ok, gridButton);
		buttonPanel.add(ok);
		
		relayValueField.setValue(relayValue);

		this.getContentPane().add(panel);
		setSize(300, 150);
		setVisible(true);
	}

	public void okButton_actionPerformed(ActionEvent ae) {
		
		try {
			String relayString = relayValueField.getText();
			relayValue =  (new Integer(relayString) ).intValue();
			// relayValue = ( ( (Number) relayValueField.getValue() ).intValue() );
			okStatus = true;
			setVisible(false);
			dispose();
			
		} catch (NumberFormatException error) {
			logger.info(
				"Something is wrong with the time window value you entered."
					+ " Couldn't convert it to a number.");
			
			JOptionPane.showMessageDialog(this,
			    "Something is wrong with one of the numbers entered.",
			    "Input Error",
			    JOptionPane.ERROR_MESSAGE);

			
			// throw up an error if something is wrong
		} catch (Exception error) {
		
			JOptionPane.showMessageDialog(this,
				"An unknown error has occurred in the set relay dialog box.",
				"Unknown Error",
				JOptionPane.ERROR_MESSAGE);
		}	
		
	}

	public void cancelButton_actionPerformed(ActionEvent ae) {

		setVisible(false);
		dispose();
	}

	public int getRelayValue() {
		return relayValue;
	}

	public void setRelayValue(int relayValue) {
		this.relayValue = relayValue;
	}
	
	public boolean getOkStatus() {
		return okStatus;
	}
	
	
}

