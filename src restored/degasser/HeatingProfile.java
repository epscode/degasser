package degasser;

import org.apache.log4j.Logger;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class HeatingProfile {

	Vector<HeatingStage> heatingStages;
	String filename = "";
	boolean runningProfile = false;
	int selectedStage = 0;
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public HeatingProfile() {
		// heatingStages.clear();
		filename = "";
		heatingStages = new Vector<HeatingStage>();
		
		addInitialHeatingStage();
	}
	
	public int getSelectedStage() {
		return selectedStage;
	}

	public void setSelectedStage(int selectedStage) {
		this.selectedStage = selectedStage;
	}

	Vector<Vector<String>> convertData() {
		
		Vector<Vector<String>> dataArray = new Vector<Vector<String>>();
		
		for (int stageNumber  = 0; stageNumber < getSize(); stageNumber++) {	
			HeatingStage heatingStage = getHeatingStage(stageNumber);
			Vector<String> dataRow = new Vector<String>();
			
			dataRow.add(Integer.toString(heatingStage.getStageNumber() ) );
			dataRow.add(Integer.toString(heatingStage.getTime() ) );
			dataRow.add(Integer.toString(heatingStage.getRelay() ) );
			
			dataArray.add(dataRow);
		}
		
		return dataArray;
	}
	
	// FINISH THIS LATER
	public String getHeatingProfileCommand() {
		
		String commandMessage = "";
		
		// stage# time current
		// 1,12,123
		
		for (int heatingStage = 0; heatingStage < heatingStages.size(); heatingStage++) {
			commandMessage += heatingStages.elementAt(heatingStage).getStageNumber();
			commandMessage += ",";
			commandMessage += heatingStages.elementAt(heatingStage).getTime();
			commandMessage += ",";
			commandMessage += heatingStages.elementAt(heatingStage).getRelay();
			
			if (heatingStage < (heatingStages.size() - 1) ) {
				commandMessage +=  "_";
			}
		}
		
		return commandMessage;
	}
	
	public boolean isFilenamePresent() {
		
		logger.debug("filename present from isFilenamePresent: " + filename);
		
		boolean isFilenamePresent = false;
		
		if (filename.compareTo("") != 0) {
			isFilenamePresent = true;
		}
		
		logger.debug("filename present is " + isFilenamePresent);
		return isFilenamePresent;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getHeatingStageField(int stageNumber, int field) {
		
		int value = getHeatingStage(stageNumber).getField(field);		
		return value;
	}
	
	public int getSize() {
		return heatingStages.size();
	}
	
	public Vector<HeatingStage> getHeatingStages() {
		return heatingStages;
	}
	
	public HeatingStage getHeatingStage(int stageNumber) {
		return heatingStages.elementAt(stageNumber);
	}
	
	public void newHeatingProfile() {
		filename = "";
		clearHeatingProfile();
		addInitialHeatingStage();
	}
	
	public boolean isRunningProfile() {
		return runningProfile;
	}

	public void setRunningProfile(boolean runningProfile) {
		this.runningProfile = runningProfile;
	}
	
	 public void setField(int stageNumber, int field, int value) {
		 heatingStages.elementAt(stageNumber).setField(field, value);
	 }
	
	public HeatingProfile(String filename) {
	}
	
	public void addInitialHeatingStage() {
		addHeatingStage(1, 0, 0);
	}

	public void addHeatingStage(int stageNumber, int time, int relay) {
		
		logger.debug("adding a heating stage.");
		logger.info("from add heating stage");
		logger.info("timeValue: " + time);
		logger.info("relayValue: " + relay);
		
		HeatingStage heatingStage = new HeatingStage(stageNumber, time, relay);
		heatingStages.add(heatingStage);
	}
	
	public void insertHeatingStage(int stageNumber, int time, int relay) {
		HeatingStage heatingStage = new HeatingStage(stageNumber, time, relay);
		heatingStages.insertElementAt(heatingStage, stageNumber);
		
		// push the rest of the stage numbers down
		for (int index = stageNumber; index < heatingStages.size(); index++) {
			heatingStages.get(index).setStageNumber(index + 1);
		}
		
		logger.debug("adding a heating stage.");
	}
	
	public void removeHeatingStage(int stageNumber) {
		
		// move all remaining stages up one, unless it is the last one selected
		heatingStages.remove(stageNumber);
		
		// move stage numbers up
		for (int index = stageNumber; index < heatingStages.size(); index++) {
			heatingStages.get(index).setStageNumber(index + 1);
		}
	}
	
	public void clearHeatingProfile() {
		heatingStages.clear();
	}
	
	public int calculateTotalRunTime() {
		
		int totalTime = 0;
		
		for (int stageNumber = 0; stageNumber < getSize(); stageNumber++) {
			totalTime += heatingStages.elementAt(stageNumber).getTime();
		}
		
		return totalTime;
	}
	
	public void editHeatingStage(int stageNumber, int time, int relay) {
		setField(stageNumber, 1, time);
		setField(stageNumber, 2, relay);
	}

	public void openHeatingProfile() {
		// getFilenameForOpening();
		readProfileFile();
	}

	public void saveAsHeatingProfile() {
		writeProfileFile();
	}

	void writeProfileFile() {
		
		logger.debug("trying to write the profile file: " + filename);
		try {
			FileWriter profileWriter = new FileWriter(filename);
			PrintWriter profilePrinter = new PrintWriter(profileWriter, true);
			
			logger.debug("number of heating stages from save: " +  heatingStages.size() );

			for (int stageNumber = 0; stageNumber < heatingStages.size(); stageNumber++) {
				profilePrinter.write(heatingStages.get(stageNumber).printStage() );
			}
			
			profilePrinter.close();
			profileWriter.close();

		} catch (IOException error) {
			logger.error("can't open file for saving");

			JOptionPane.showMessageDialog(null, "Error saving file " + filename, "File Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void readProfileFile() {
		
		logger.info("trying to read the data file: " + filename);
		try {
			BufferedReader profileFile = new BufferedReader(new FileReader(filename));

			String line;

			String[] fileTokens = new String[3];
			while ( (line = profileFile.readLine() ) != null) {
				int i = 0;
				StringTokenizer st = new StringTokenizer(line, " ");
				while (st.hasMoreTokens() ) {
					fileTokens[i] = st.nextToken();
					logger.debug("fileTokens[" + i + "]: " + fileTokens[i]);
					i++;
				}

				int stageNumber = (new Integer(fileTokens[0]) ).intValue();
				int time = (new Integer(fileTokens[1]) ).intValue();
				int relayValue = (new Integer(fileTokens[2]) ).intValue();

				heatingStages.add(new HeatingStage(stageNumber, time, relayValue) );
			}
			
			profileFile.close();
		} catch (Exception error) {
			logger.error("Error reading the profile file. " + error);

			JOptionPane.showMessageDialog(null, "Can not read profile file " + filename, "File Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public boolean warnNotSaved() {
		boolean proceed = false;

		// Custom button text
		Object[] options = { "OK", "Cancel" };

		int n = JOptionPane.showOptionDialog(null, "You have not saved this study. " + "Do you really want to proceed?",
				"Save Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

		logger.debug("n from warning dialog: " + n);

		if (n == 0) {
			proceed = true;
		} else {
			proceed = false;
		}

		logger.debug("proceed from warnUser: " + proceed);

		return proceed;
	}

	public boolean warnOverwriteData() {
		boolean proceed = false;

		Object[] options = { "OK", "Cancel" };

		int n = JOptionPane.showOptionDialog(null,
				"This will overwrite your sample data. " + "Do you really want to proceed?", "Overwrite Warning",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

		logger.debug("n from warning dialog: " + n);

		if (n == 0) {
			proceed = true;
		} else {
			proceed = false;
		}

		logger.debug("proceed from overwrite: " + proceed);

		return proceed;
	}
	
	public boolean isZeroTimeStage() {
		
		for (int stageNumber = 0; stageNumber < getSize(); stageNumber++) {
			int stageTime = heatingStages.elementAt(stageNumber).getTime();
		
			if (stageTime <= 0) {
				// flag error
				return true;
			}
		}
		return false;
	}
}