package degasser;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import javax.swing.*;
import org.apache.log4j.Logger;

class ControlBounds {

	private static final long serialVersionUID = 1L;
	
	String filename = "controlbounds.txt";

	int fanTemperatureOnOffValue = 0;
	int temperatureWarningValue = 0;
	int temperatureShutdownValue = 0;
	int currentLimitValue = 0;
	double vacuumLimitValue = 1E-5;
	int stageTimeLimitValue = 0;
	
	// pid parameters
	double KpValue; double KiValue; double KdValue;
	
	Frame frame;
	String filepath;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public ControlBounds(String filepath, Frame frame) {

		this.frame = frame;
		this.filepath = filepath;
		
		readBoundsFile();
	}
	
	public double getVacuumLimitValue() {
		return vacuumLimitValue;
	}

	public void setVacuumLimitValue(double vacuumLimitValue) {
		this.vacuumLimitValue = vacuumLimitValue;
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

	void writeBoundsFile() {

		String fullFilename = filepath + "/temp/controlbounds.txt";
		logger.debug("trying to write the control bounds file: " + fullFilename);
		try {
			FileWriter controlBoundsWriter = new FileWriter(fullFilename);
			PrintWriter controlBoundsPrinter = new PrintWriter(controlBoundsWriter, true);

			controlBoundsPrinter.println("fanTemperatureOnOff " + fanTemperatureOnOffValue);
			controlBoundsPrinter.println("temperatureWarning " + temperatureWarningValue);
			controlBoundsPrinter.println("temperatureShutdown " + temperatureShutdownValue);
			controlBoundsPrinter.println("stageTimeLimit " + stageTimeLimitValue);
			controlBoundsPrinter.println("currentLimit " + currentLimitValue);
			
			controlBoundsPrinter.println("Kp " + KpValue);
			controlBoundsPrinter.println("Ki " + KiValue);
			controlBoundsPrinter.println("Kd " + KdValue);

			controlBoundsPrinter.close();
			controlBoundsWriter.close();
		} catch (IOException error) {
			logger.error("can't open file for saving");

			JOptionPane.showMessageDialog(frame, "Error saving file " + fullFilename, "File Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void readBoundsFile() {

		String fullFilename = filepath + "/temp/controlbounds.txt";
		logger.info("trying to read the data file: " + fullFilename);
		try {
			BufferedReader controlBounds = new BufferedReader(new FileReader(fullFilename));

			String line;

			String[] fileTokens = new String[2];
			while ( (line = controlBounds.readLine() ) != null) {
				int i = 0;
				StringTokenizer st = new StringTokenizer(line, " ");
				while (st.hasMoreTokens() ) {
					fileTokens[i] = st.nextToken();
					logger.debug("fileTokens[" + i + "]: " + fileTokens[i]);
					i++;
				}

				if (fileTokens[0].compareTo("temperatureWarning") == 0) {
					temperatureWarningValue = (new Integer(fileTokens[1]) ).intValue();
					logger.info("temperature Warning Value: " + temperatureWarningValue);
				} else if (fileTokens[0].compareTo("fanTemperatureOnOff") == 0) {
					fanTemperatureOnOffValue = (new Integer(fileTokens[1]) ).intValue();
					logger.info("fanTemperature OnOffValue Value: " + fanTemperatureOnOffValue);
				} else if (fileTokens[0].compareTo("temperatureShutdown") == 0) {
					temperatureShutdownValue = (new Integer(fileTokens[1]) ).intValue();
					logger.info("temperature Shutdown Value: " + temperatureShutdownValue);
				} else if (fileTokens[0].compareTo("currentLimit") == 0) {
					currentLimitValue = (new Integer(fileTokens[1]) ).intValue();
					logger.info("currentLimitValue: " + currentLimitValue);
				} else if (fileTokens[0].compareTo("stageTimeLimit") == 0) {
					stageTimeLimitValue = (new Integer(fileTokens[1]) ).intValue();
					logger.info("stageTimeLimitValue: " + stageTimeLimitValue);
				} else if (fileTokens[0].compareTo("Kp") == 0) {
					KpValue = (new Double(fileTokens[1]) ).doubleValue();
					logger.info("Kp: " + KpValue);
				} else if (fileTokens[0].compareTo("Ki") == 0) {
					KiValue = (new Double(fileTokens[1]) ).doubleValue();
					logger.info("Ki: " + KiValue);
				} else if (fileTokens[0].compareTo("Kd") == 0) {
					KdValue = (new Double(fileTokens[1]) ).doubleValue();
					logger.info("Kd: " + KdValue);
				}	
			}

			controlBounds.close();
		} catch (IOException error) {
			JOptionPane.showMessageDialog(frame, "File Error Reading the Control Bounds File " + 
					fullFilename + " Exception: " + error, "File Error",
					JOptionPane.ERROR_MESSAGE);
			
		} catch (NumberFormatException error) {
			JOptionPane.showMessageDialog(frame, "Number Format Problem Reading the Control Bounds File " + 
					fullFilename + " Exception: " + error, "File Error",
					JOptionPane.ERROR_MESSAGE);
			
		} catch (Exception error) {
			logger.error("Error reading the control bounds file. " + error);

			JOptionPane.showMessageDialog(frame, "Unknown Error Reading the Control Bounds File" + 
					fullFilename + " Exception: " + error, "File Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
