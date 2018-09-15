package degasser;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class OpenSerialPort {
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );
	
	Enumeration portList;
	CommPortIdentifier portId;
	SerialPort serialPort;
	
	public SerialPort getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

	OutputStream outputStream;

	public OpenSerialPort() {
		logger.info("OpenSerialPort");
	}

	public SerialPort openPort(String serialPortString) {
		portList = CommPortIdentifier.getPortIdentifiers();

		logger.debug("Trying to open the serial port: " + serialPortString);
		logger.debug("portList: " + portList.toString() );
		
		while (portList.hasMoreElements() ) {
			portId = (CommPortIdentifier) portList.nextElement();
			logger.debug("port Id: " + portId.getName() );
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				logger.debug("serial port Id: " + portId.getName() );
				if (portId.getName().equals(serialPortString) ) {
					logger.debug("found correct com port.");
					// if (portId.getName().equals("/dev/term/a")) {
					try {
						serialPort =
							(SerialPort) portId.open("ProbeApp", 500);
					} catch (PortInUseException error) {
						logger.error(error + " trying to open to com port.");
						
						JOptionPane.showMessageDialog(null,
							    "Unable to open the serial port: " + serialPortString,
							    "Serial Port Error",
							    JOptionPane.ERROR_MESSAGE);
					}
					try {
						serialPort.setSerialPortParams(
							9600,
							SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
							
						// trying to set the flow control	
						serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
					} catch (UnsupportedCommOperationException error) {
						logger.debug(
							error
								+ " trying to set up the com port");
						
						JOptionPane.showMessageDialog(null,
							    "Unable to open the serial port.",
							    "Serial Error",
							    JOptionPane.ERROR_MESSAGE);
					}
					
					try {
							serialPort.enableReceiveTimeout(500);
						} catch (UnsupportedCommOperationException error) {
							logger.debug(
									error
										+ " trying to set up the com port");
							
							JOptionPane.showMessageDialog(null,
								    "Unable to open the serial port.",
								    "Serial Error",
								    JOptionPane.ERROR_MESSAGE);
						}

						// Add ownership listener to allow ownership event handling.
						// portId.addPortOwnershipListener(this);
				}
			}
		}
		
		if (serialPort == null) {
			logger.error("serial port is null in opening port."); 
			
			JOptionPane.showMessageDialog(null,
				    "Unable to open the serial port.",
				    "Serial Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		return serialPort;
	}
}
