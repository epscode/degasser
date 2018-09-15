package degasser;

import java.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;



class SetNumberOfFilamentsDialog extends CenterDialogBox {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4003036202003129675L;
	JFormattedTextField numberOfFilamentsField;
	int numberOfFilementsValue = 0;
	
	boolean okStatus = false;
	JButton ok, cancel;
	boolean filamentsSet = false;
	
	DecimalFormat integerDigit = new DecimalFormat("#0");
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public SetNumberOfFilamentsDialog(JFrame f) {
		
		super(f);
		
		this.setTitle("Set number of filaments");
		this.setModal(true);
		
		String warningString = "<html><font color=\"red\"><b>Warning: An inaccurate filament estimate is usually indicitive of a problem.</b></html>";
		
		JLabel warningLabel = new JLabel(warningString);
		
		NumberFormat format = NumberFormat.getInstance(java.util.Locale.US);
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(0);
	    formatter.setMaximum(Integer.MAX_VALUE);
	    formatter.setAllowsInvalid(true);
	    formatter.setCommitsOnValidEdit(true);
		
		numberOfFilamentsField = new JFormattedTextField(formatter);
		
		JLabel numberOfFilamentsLabel = new JLabel("Number of filaments");
		
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
		// gridMain.fill = GridBagConstraints.HORIZONTAL;
		gridMain.insets = new Insets(5, 5, 5, 5);
		grid.gridx = 0;
		grid.gridy = 0;
		gridbag.setConstraints(mainPanel, grid);
		panel.add(mainPanel);
		
		gridMain.gridwidth = 2;
		gridMain.weightx = 1.0;
		gridMain.gridx = 0;
		gridMain.gridy = 0;
		gridbagMain.setConstraints(warningLabel, gridMain);
		mainPanel.add(warningLabel);

		gridMain.gridwidth = 1;
		gridMain.weightx = 0.5;
		gridMain.gridx = 0;
		gridMain.gridy = 1;
		gridbagMain.setConstraints(numberOfFilamentsLabel, gridMain);
		mainPanel.add(numberOfFilamentsLabel);

		gridMain.gridx = 1;
		gridMain.gridy = 1;
		numberOfFilamentsField.setColumns(8);
		gridbagMain.setConstraints(numberOfFilamentsField, gridMain);
		mainPanel.add(numberOfFilamentsField);

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

		this.getContentPane().add(panel);
		setSize(600, 150);
		
	}
	
	public void showDialog(int numberOfFilementsValue) {
		setNumberOfFilementsValue(numberOfFilementsValue);
		numberOfFilamentsField.setText( (new Integer(numberOfFilementsValue) ).toString() );
		
		setVisible(true);
	}

	public void okButton_actionPerformed(ActionEvent ae) {
		
		try {
			// numberOfFilementsValue = ( ( (Number) numberOfFilamentsField.getValue() ).intValue() );
			
			String numberOfFilementsString = numberOfFilamentsField.getText();	
			numberOfFilementsValue = NumberFormat.getNumberInstance(java.util.Locale.US).parse(numberOfFilementsString).intValue();
			// int timeValue =  Integer.parseInt(timeString);
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
		    "An unknown error has occurred in set number of filaments dialog box.",
		    "Unknown Error",
		    JOptionPane.ERROR_MESSAGE);
		}	
	}

	public void cancelButton_actionPerformed(ActionEvent ae) {

		setVisible(false);
		dispose();
	}

	public int getNumberOfFilementsValue() {
		return numberOfFilementsValue;
	}

	public void setNumberOfFilementsValue(int numberOfFilementsValue) {
		this.numberOfFilementsValue = numberOfFilementsValue;
	}
	
	public boolean getOkStatus() {
		return okStatus;
	}
}

