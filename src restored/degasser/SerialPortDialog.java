package degasser;

import com.fazecast.jSerialComm.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.*;

import org.apache.log4j.Logger;

import java.text.*;
import java.util.Enumeration;
import java.util.Vector;

public class SerialPortDialog extends CenterDialogBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JButton okButton, cancelButton;
	String filePath = "";
	String serialPortString = "";
	
	boolean cancel;
	
	JComboBox<String> comList;
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public SerialPortDialog(JFrame f, String tempPath) {

		super(f);
		this.setTitle("Serial Port");
		this.setModal(true);
		this.filePath =  tempPath;
		
		logger.info("temp path from serial dialog box: " + filePath);
		
		cancel = false;
		
		String currentSerialPort = readSerialPortFile();
		setSerialPortString(currentSerialPort);
		showSerialPortDialog();
	}
	
	boolean showSerialPortDialog() {

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		JPanel p = new JPanel();

		c.fill = GridBagConstraints.HORIZONTAL;
		p.setLayout(gridbag);

		c.insets = new Insets(10, 10, 10, 10);
		
		JLabel comPortLabel =
			new JLabel("RS232 Port");
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		gridbag.setConstraints(comPortLabel, c);
		p.add(comPortLabel);
		
		comList = new JComboBox<String>(getSerialPorts() );
		int selectedIndex = matchComPorts(getSerialPorts() );
		comList.setSelectedIndex(selectedIndex);
		
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(comList, c);
		p.add(comList);
		
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
	
	public int matchComPorts(Vector<String> comPortList) {
		
		int matchPosition = -1;
		String currentPort = getSerialPortString();
		
		for (int index = 0; index < comPortList.size(); index++) {
			String comPortChoice = comPortList.elementAt(index);
			if (comPortChoice.compareTo(currentPort) == 0) {
				matchPosition = index;
			}
		}
		
		return matchPosition;
	}
	
	public String getSerialPortString() {
		return serialPortString;
	}

	public void setSerialPortString(String serialPortString) {
		this.serialPortString = serialPortString;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void okButton_actionPerformed(ActionEvent ae) {

		String selectedComPort = (String) comList.getSelectedItem();
		
		setSerialPortString(selectedComPort);
		
		writeSerialPortFile(selectedComPort);
		setVisible(false);
		dispose();
	}

	public void cancelButton_actionPerformed(ActionEvent ae) {
		
		cancel = true;
		
		setVisible(false);
		dispose();
	}
	
	public Vector<String> getSerialPorts() {
		
		SerialPort[] ports = SerialPort.getCommPorts();
		Vector<String> serialPorts = new Vector<String>();
		
		for (int index = 0; index < ports.length; index++) {
			serialPorts.add(ports[index].getSystemPortName() );
			logger.info("serial port name: " + ports[index].getSystemPortName() );
		}
		return serialPorts;
	}
	
public void writeSerialPortFile(String selectedComPort) {
		
		
		String serialPortFilename = filePath + "serialPortFile.txt";
		
		try {
			logger.info("trying to write the serial port file: " + serialPortFilename);

			FileWriter portFile = new FileWriter(serialPortFilename, false);
			PrintWriter portWriter = new PrintWriter(portFile);
				
			logger.debug("selected com port from writeSerialPortFile: " + selectedComPort);
			
			portWriter.println(selectedComPort);
			
			portFile.close();
				
		} catch (Exception error) {
			logger.error("error writing the serial port file." + error);
			
			JOptionPane.showMessageDialog(null,
					"There was a problem writing to the serial port file.", 
				    "File Write Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public String readSerialPortFile() {
		
		String comPortString = "";
		String serialPortFilename = filePath + "serialPortFile.txt";
		
		try {
			logger.info("trying to read the serial port file: " + serialPortFilename);
			
			BufferedReader in = new BufferedReader(new FileReader(serialPortFilename) );

			comPortString = in.readLine();
			logger.info("comPortString as read from serial port file: " + comPortString);
			serialPortString = comPortString;
			in.close();
						
		} catch (Exception error) {
			logger.error("error reading the serialPortFilename file." + error);
			
			JOptionPane.showMessageDialog(null,
					"There was a problem reading from the serial port file.",
				    "File Read Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		return comPortString;
	}
 }