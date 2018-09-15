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
	boolean okStatus = false;

	DecimalFormat integerDigit = new DecimalFormat("#0");

	JFormattedTextField fanTemperatureOnOffField;
	JFormattedTextField temperatureWarningField;
	JFormattedTextField temperatureShutdownField;
	JFormattedTextField currentLimitField;
	JFormattedTextField stageTimeLimitField;
	JFormattedTextField vacuumLimitField;

	// PID Control parameters
	JFormattedTextField KpField;
	JFormattedTextField KiField;
	JFormattedTextField KdField;

	ControlBounds controlBounds;

	int fanTemperatureOnOffValue = 0;
	int temperatureWarningValue = 0;
	int temperatureShutdownValue = 0;
	int currentLimitValue = 0;
	double vacuumLimitValue = 0.0;
	int stageTimeLimitValue = 0;

	double KpValue = 0.0;
	double KiValue = 0.0;
	double KdValue = 0.0;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	public ControlBoundsDialog(JFrame frame, ControlBounds controlBounds) {

		super(frame);
		setSize(425, 500);

		this.frame = frame;
		this.controlBounds = controlBounds;
		this.setModal(true);

		NumberFormat format = NumberFormat.getInstance(java.util.Locale.US);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		// formatter.setAllowsInvalid(false);
		// formatter.setCommitsOnValidEdit(true);

		DecimalFormat decimalFormat = new DecimalFormat("0.00000");
		NumberFormatter floatFormatter = new NumberFormatter(decimalFormat);
		
		DecimalFormat expFormat = new DecimalFormat("0.0E0");
		NumberFormatter expFormatter = new NumberFormatter(expFormat);
		// floatFormatter.setMinimum(0);
		// floatFormatter.setMaximum(Double.MAX_VALUE);
		// floatFormatter.setAllowsInvalid(false);
		// floatFormatter.setCommitsOnValidEdit(true);

		fanTemperatureOnOffField = new JFormattedTextField(formatter);
		temperatureWarningField = new JFormattedTextField(formatter);
		temperatureShutdownField = new JFormattedTextField(formatter);
		stageTimeLimitField = new JFormattedTextField(formatter);
		currentLimitField = new JFormattedTextField(formatter);
		vacuumLimitField = new JFormattedTextField(expFormatter);

		KpField = new JFormattedTextField(floatFormatter);
		KiField = new JFormattedTextField(floatFormatter);
		KdField = new JFormattedTextField(floatFormatter);
		
		

		cancel = false;
	}

	public JFormattedTextField getVacuumLimitField() {
		return vacuumLimitField;
	}

	public void setVacuumLimitField(JFormattedTextField vacuumLimitField) {
		this.vacuumLimitField = vacuumLimitField;
	}

	public JFormattedTextField getKpField() {
		return KpField;
	}

	public void setKpField(JFormattedTextField kpField) {
		KpField = kpField;
	}

	public JFormattedTextField getKiField() {
		return KiField;
	}

	public void setKiField(JFormattedTextField kiField) {
		KiField = kiField;
	}

	public JFormattedTextField getKdField() {
		return KdField;
	}

	public void setKdField(JFormattedTextField kdField) {
		KdField = kdField;
	}

	boolean readPreferencesFile(String filename) {
		return true;
	}

	boolean writePreferencesFile(String filenames) {
		return true;
	}

	boolean showControlBoundsDialog() {

		fanTemperatureOnOffField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				okButton_actionPerformed(event);
			}
		});
		
		fanTemperatureOnOffField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				System.out.println("focus gained");
				int FanTemperatureOnOffLength = (new Integer(controlBounds.getFanTemperatureOnOffValue() ).toString() ).length();
				System.out.println("FanTemperatureOnOffLength: " + FanTemperatureOnOffLength);
				fanTemperatureOnOffField.setCaretPosition(1);
			}
			
			public void focusLost(FocusEvent e) {
				
			}
		});

		temperatureWarningField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				okButton_actionPerformed(event);
			}
		});

		temperatureShutdownField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				okButton_actionPerformed(event);
			}
		});

		stageTimeLimitField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				okButton_actionPerformed(event);
			}
		});

		currentLimitField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				okButton_actionPerformed(event);
			}
		});

		vacuumLimitField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				okButton_actionPerformed(event);
			}
		});

		KpField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				okButton_actionPerformed(event);
			}
		});

		KiField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				okButton_actionPerformed(event);
			}
		});

		KdField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				okButton_actionPerformed(event);
			}
		});

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

		JLabel vacuumLimitLabel = new JLabel("Vacuum Limit (mT)");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 5;
		gridbag.setConstraints(vacuumLimitLabel, c);
		p.add(vacuumLimitLabel);

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 5;
		gridbag.setConstraints(vacuumLimitField, c);
		p.add(vacuumLimitField);

		// PID
		JLabel KpLabel = new JLabel("Kp");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 6;
		gridbag.setConstraints(KpLabel, c);
		p.add(KpLabel);

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 6;
		gridbag.setConstraints(KpField, c);
		p.add(KpField);

		JLabel KiLabel = new JLabel("Ki");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 7;
		gridbag.setConstraints(KiLabel, c);
		p.add(KiLabel);

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 7;
		gridbag.setConstraints(KiField, c);
		p.add(KiField);

		JLabel KdLabel = new JLabel("Kd");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 8;
		gridbag.setConstraints(KdLabel, c);
		p.add(KdLabel);

		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 8;
		gridbag.setConstraints(KdField, c);
		p.add(KdField);

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
		c.gridy = 9;
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

	public double getKpValue() {
		return KpValue;
	}

	public void setKpValue(double kpValue) {
		KpValue = kpValue;
	}

	public double getKiValue() {
		return KiValue;
	}

	public void setKiValue(double kiValue) {
		KiValue = kiValue;
	}

	public double getKdValue() {
		return KdValue;
	}

	public void setKdValue(double kdValue) {
		KdValue = kdValue;
	}

	public void okButton_actionPerformed(ActionEvent event) {

		try {
			fanTemperatureOnOffValue = ( (Number) fanTemperatureOnOffField.getValue() ).intValue();
			temperatureWarningValue =  ( (Number) temperatureWarningField.getValue() ).intValue();
			temperatureShutdownValue = ( (Number) temperatureShutdownField.getValue() ).intValue();
			stageTimeLimitValue = ( (Number) stageTimeLimitField.getValue() ).intValue();
			currentLimitValue = ( (Number) currentLimitField.getValue() ).intValue();
			vacuumLimitValue = ( (Number) vacuumLimitField.getValue() ).doubleValue();

			KpValue = ( (Number) KpField.getValue() ).doubleValue();
			KiValue = ( (Number) KiField.getValue() ).doubleValue();
			KdValue = ( (Number) KdField.getValue() ).doubleValue();

			controlBounds.setStageTimeLimitValue(stageTimeLimitValue);
			controlBounds.setCurrentLimitValue(currentLimitValue);
			controlBounds.setVacuumLimitValue(vacuumLimitValue);
			controlBounds.setFanTemperatureOnOffValue(fanTemperatureOnOffValue);
			controlBounds.setTemperatureShutdownValue(temperatureShutdownValue);
			controlBounds.setTemperatureWarningValue(temperatureWarningValue);

			// pid
			controlBounds.setKpValue(KpValue);
			controlBounds.setKiValue(KiValue);
			controlBounds.setKdValue(KdValue);

			controlBounds.writeBoundsFile();

			setVisible(false);
			dispose();

			okStatus = true;

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
			temperatureWarningField.setValue(controlBounds.getTemperatureWarningValue() );
			logger.info("temperature Warning Value: " + temperatureWarningValue);
			
			fanTemperatureOnOffField.setValue(controlBounds.getFanTemperatureOnOffValue() );
			logger.info("fanTemperature OnOffValue Value: " + fanTemperatureOnOffValue);
			
			temperatureShutdownField.setValue(controlBounds.getTemperatureShutdownValue() );
			logger.info("temperature Shutdown Value: " + temperatureShutdownValue);

			currentLimitField.setValue(controlBounds.getCurrentLimitValue() );
			logger.info("currentLimitValue: " + currentLimitValue);

			vacuumLimitField.setValue(controlBounds.getVacuumLimitValue() );
			logger.info("vaccumLimitValue: " + vacuumLimitValue);

			stageTimeLimitField.setValue(controlBounds.getStageTimeLimitValue() );
			logger.info("stageTimeLimitValue: " + stageTimeLimitValue);

			KpField.setValue(controlBounds.getKpValue() );
			logger.info("KpValue: " + KpValue);

			KiField.setValue(controlBounds.getKiValue() );
			logger.info("KiValue: " + KiValue);

			KdField.setValue(controlBounds.getKdValue() );
			logger.info("KdValue: " + KdValue);

		} catch (Exception error) {
			logger.error("Error reading the control bounds file. " + error);

			JOptionPane.showMessageDialog(frame, "Error Setting the Control Bounds", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean isOkStatus() {
		return okStatus;
	}

	public void setOkStatus(boolean okStatus) {
		this.okStatus = okStatus;
	}
}
