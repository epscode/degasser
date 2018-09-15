package degasser;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;

class ControlBoundsDialog extends CenterDialogBox {

	JFrame frame;

	private static final long serialVersionUID = 1L;

	JButton okButton, cancelButton;

	boolean cancel;

	DecimalFormat integerDigit = new DecimalFormat("#0");

	JFormattedTextField fanTemperatureOnOffField;
	JFormattedTextField temperatureWarningField;
	JFormattedTextField temperatureShutdownField;
	JFormattedTextField currentLimitField;
	JFormattedTextField stageTimeLimitField;

	int fanTemperatureOnOffValue = 0;
	int temperatureWarningValue = 0;
	int temperatureShutdownValue = 0;
	int currentLimitValue = 0;
	int stageTimeLimitValue = 0;
	
	ControlBounds controlBounds;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	public ControlBoundsDialog(JFrame frame, ControlBounds controlBounds) {

		super(frame);
		setSize(425, 300);

		this.frame = frame;
		this.controlBounds = controlBounds;
		this.setModal(true);
		
		NumberFormat format = NumberFormat.getInstance(java.util.Locale.US);
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(0);
	    formatter.setMaximum(Integer.MAX_VALUE);
	    formatter.setAllowsInvalid(false);
	    formatter.setCommitsOnValidEdit(true);
	    formatter.setAllowsInvalid(false);


		fanTemperatureOnOffField = new JFormattedTextField(formatter);
		temperatureWarningField = new JFormattedTextField(formatter);
		temperatureShutdownField = new JFormattedTextField(formatter);
		stageTimeLimitField = new JFormattedTextField(formatter);
		currentLimitField = new JFormattedTextField(formatter);

		cancel = false;
	}

	public int getFanTemperatureOnOffValue() {
		return fanTemperatureOnOffValue;
	}

	public void setFanTemperatureOnOffValue(int fanTemperatureOnOffValue) {
		this.fanTemperatureOnOffValue = fanTemperatureOnOffValue;
	}

	public int getTemperatureWarningValue() {
		return temperatureWarningValue;
	}

	public void setTemperatureWarningValue(int temperatureWarningValue) {
		this.temperatureWarningValue = temperatureWarningValue;
	}

	public int getTemperatureShutdownValue() {
		return temperatureShutdownValue;
	}

	public void setTemperatureShutdownValue(int temperatureShutdownValue) {
		this.temperatureShutdownValue = temperatureShutdownValue;
	}

	public int getCurrentLimitValue() {
		return currentLimitValue;
	}

	public void setCurrentLimitValue(int currentLimitValue) {
		this.currentLimitValue = currentLimitValue;
	}

	public int getStageTimeLimitValue() {
		return stageTimeLimitValue;
	}

	public void setStageTimeLimitValue(int stageTimeLimitValue) {
		this.stageTimeLimitValue = stageTimeLimitValue;
	}

	boolean readPreferencesFile(String filename) {
		return true;
	}

	boolean writePreferencesFile(String filenames) {
		return true;
	}

	boolean showControlBoundsDialog() {

		// fan temp turn on
		// warning temp
		// shutoff temp
		// current limit
		// stage time limit
		
		

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		JPanel p = new JPanel();

		c.fill = GridBagConstraints.HORIZONTAL;
		p.setLayout(gridbag);

		c.insets = new Insets(10, 10, 10, 10);

		JLabel FanTemperatureOnOffLabel = new JLabel("Fan Temperature On / Off (Celsius)");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		gridbag.setConstraints(FanTemperatureOnOffLabel, c);
		p.add(FanTemperatureOnOffLabel);

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(fanTemperatureOnOffField, c);
		p.add(fanTemperatureOnOffField);

		JLabel temperatureWarningLabel = new JLabel("Temperature Warning (Celsius)");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(temperatureWarningLabel, c);
		p.add(temperatureWarningLabel);

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(temperatureWarningField, c);
		p.add(temperatureWarningField);

		JLabel temperatureShutdownLabel = new JLabel("Temperature Shutdown (Celsius)");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(temperatureShutdownLabel, c);
		p.add(temperatureShutdownLabel);

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(temperatureShutdownField, c);
		p.add(temperatureShutdownField);

		JLabel stageTimeLimitLabel = new JLabel("Stage Time Limit (minutes)");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(stageTimeLimitLabel, c);
		p.add(stageTimeLimitLabel);

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 3;
		gridbag.setConstraints(stageTimeLimitField, c);
		p.add(stageTimeLimitField);

		JLabel currentLimitLabel = new JLabel("Current Limit (mV)");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 4;
		gridbag.setConstraints(currentLimitLabel, c);
		p.add(currentLimitLabel);

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 4;
		gridbag.setConstraints(currentLimitField, c);
		p.add(currentLimitField);

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
		c.gridy = 5;
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

		getRootPane().setDefaultButton(okButton);

		Dimension frmSize = getSize();
		Point loc = getLocation();
		
		controlBounds.readBoundsFile();
		setBoundsValues();

		setVisible(true);

		return cancel;
	}

	public void okButton_actionPerformed(ActionEvent event) {

		try {
			String fanTemperatureOnOffString = fanTemperatureOnOffField.getText();	
			fanTemperatureOnOffValue = NumberFormat.getNumberInstance(java.util.Locale.US).parse(fanTemperatureOnOffString).intValue();
			
			String temperatureWarningString = temperatureWarningField.getText();	
			temperatureWarningValue = NumberFormat.getNumberInstance(java.util.Locale.US).parse(temperatureWarningString).intValue();

			String temperatureShutdownString = temperatureShutdownField.getText();	
			temperatureShutdownValue = NumberFormat.getNumberInstance(java.util.Locale.US).parse(temperatureShutdownString).intValue();
			
			String stageTimeLimitString = stageTimeLimitField.getText();	
			stageTimeLimitValue = NumberFormat.getNumberInstance(java.util.Locale.US).parse(stageTimeLimitString).intValue();

			String currentLimitString = currentLimitField.getText();	
			currentLimitValue = NumberFormat.getNumberInstance(java.util.Locale.US).parse(currentLimitString).intValue();

			controlBounds.setStageTimeLimitValue(stageTimeLimitValue);
			controlBounds.setCurrentLimitValue(currentLimitValue);
			controlBounds.setFanTemperatureOnOffValue(fanTemperatureOnOffValue);
			controlBounds.setTemperatureShutdownValue(temperatureShutdownValue);
			controlBounds.setTemperatureWarningValue(temperatureWarningValue);
			
			controlBounds.writeBoundsFile();

			setVisible(false);
			dispose();

		} catch (NumberFormatException error) {
			logger.info("Something is wrong with one of the values you entered." + " Couldn't convert it to a number.");

			JOptionPane.showMessageDialog(this, "Something is wrong with one of the numbers entered.", "Input Error",
					JOptionPane.ERROR_MESSAGE);

			// throw up an error if something is wrong
		} catch (Exception error) {

			JOptionPane.showMessageDialog(this, "An unknown error has occurred in control bounds dialog box.",
					"Unknown Error", JOptionPane.ERROR_MESSAGE);
		}

		setVisible(false);
		dispose();
	}

	public void cancelButton_actionPerformed(ActionEvent ae) {
		cancel = true;

		setVisible(false);
		dispose();
	}

	public void setBoundsValues() {

		logger.info("trying to set the data bounds values");
		try {
			temperatureWarningField.setText(new Integer(controlBounds.temperatureWarningValue).toString() );
			logger.info("temperature Warning Value: " + temperatureWarningValue);

			fanTemperatureOnOffField.setText(new Integer(controlBounds.fanTemperatureOnOffValue).toString() );
			logger.info("fanTemperature OnOffValue Value: " + fanTemperatureOnOffValue);

			temperatureShutdownField.setText(new Integer(controlBounds.temperatureShutdownValue).toString() );
			logger.info("temperature Shutdown Value: " + temperatureShutdownValue);

			currentLimitField.setText(new Integer(controlBounds.currentLimitValue).toString() );
			logger.info("currentLimitValue: " + currentLimitValue);

			stageTimeLimitField.setText(new Integer(controlBounds.stageTimeLimitValue).toString() );
			logger.info("stageTimeLimitValue: " + stageTimeLimitValue);
		} catch (Exception error) {
			logger.error("Error reading the control bounds file. " + error);

			JOptionPane.showMessageDialog(frame, "Error Setting the Control Bounds", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
