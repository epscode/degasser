package degasser;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

class HeatingProfileTableModel extends AbstractTableModel {

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	private static final long serialVersionUID = 1L;

	Vector<String> columnNames = new Vector<String>();
	// Vector<HeatingStage> heatingStages = new Vector<HeatingStage>();
	// Vector<Vector<String>> stageStrings;
	HeatingProfile heatingProfile;
	boolean state = true;

	// TimerPanel heatingTimerPanel;
	// TimerPanelStage heatingTimerPanelStage;

	HeatingProgressElements heatingProgressElements;

	public boolean isCellEditable(int row, int column) {
		if (column != 0) {
			return state;
		}
		return false;
	}

	void toggleEditing(boolean state) {
		this.state = state;
	}

	public HeatingProfileTableModel(HeatingProfile heatingProfile, HeatingProgressElements heatingProgressElements) {
		setColumnNames();
		this.heatingProfile = heatingProfile;
		this.heatingProgressElements = heatingProgressElements;
		// this.heatingTimerPanel = heatingTimerPanel;
		// this.heatingTimerPanelStage = heatingTimerPanelStage;
		printDebugData();
	}

	public HeatingProfile getHeatingProfile() {
		return heatingProfile;
	}

	public int getColumnCount() {
		return columnNames.size();
	}

	public int getRowCount() {
		return heatingProfile.getSize();
	}

	public void setColumnNames() {
		columnNames.add("Stage");
		columnNames.add("Time (minutes)");
		columnNames.add("Current Setpoint (mA)");
	}

	public String getColumnName(int col) {
		return columnNames.elementAt(col);
	}

	/*
	 * public String getValueAt(int stageNumber, int field) {
	 * 
	 * // this.fireTableCellUpdated(stageNumber, field); return
	 * (String.valueOf(getValueFromHeatingProfile(stageNumber, field) ) ); }
	 */

	public Integer getValueAt(int stageNumber, int field) {

		// this.fireTableCellUpdated(stageNumber, field);
		return (getValueFromHeatingProfile(stageNumber, field));
	}

	public int getValueFromHeatingProfile(int stageNumber, int field) {

		logger.info("getting value from heating profile. stagenumber: " + stageNumber + " field: " + field);

		Integer value = heatingProfile.getHeatingStage(stageNumber).getField(field);

		// String value = (stageStrings.get(stageNumber)).get(field);
		return value;
	}

	/*
	 * public String getValueAt(int stageNumber, int field) {
	 * this.fireTableCellUpdated(stageNumber, field);
	 * 
	 * return "test"; }
	 */

	/*
	 * public String getValueFromHeatingProfile(int stageNumber, int field) {
	 * 
	 * logger.info("getting value from heating profile. stagenumber: " + stageNumber
	 * + " field: " + field);
	 * 
	 * int value = heatingProfile.getHeatingStage(stageNumber).getField(field);
	 * 
	 * // String value = (stageStrings.get(stageNumber)).get(field); return
	 * String.valueOf(value); }
	 */

	public void setValueAt(Object value, int row, int col) {
		// rowData[row][col] = value;

		System.out.println("setting the value");
		logger.info("setting value in table.");

		// int cellValue = Integer.valueOf((String) value);

		int cellValue = ((Integer) value).intValue();

		System.out.println("row: " + row + " column: " + col + " value: " + cellValue);

		heatingProfile.setField(row, col, cellValue);

		heatingProgressElements.setOverallTimeFromHeatingProfile();
		heatingProgressElements.setStageTimeFromHeatingProfile();

		printDebugData();

		heatingProfile.writeProfileFileToScreen();
		fireTableCellUpdated(row, col);
	}

	public void addDataRowToHeatingProfile() {

		logger.info("add row triggered");

		// heatingProfile.addHeatingStage(1, 2, 3);

		// fireTableRowsInserted(0, 1);

		/*
		 * Vector<String> dataRow = new Vector<String>();
		 * 
		 * dataRow.add("1"); dataRow.add("2"); dataRow.add("3");
		 * 
		 * stageStrings.add(dataRow);
		 */
	}

	public void setElementInHeatingProfile(int stageNumber, int value) {

	}

	public void setValueInTable(String value, int stageNumber, int field) {

		// remember this sets the table, not the heating value, somehow get the string
		// array and then set the table value!!

		logger.debug("Setting value at " + stageNumber + "," + field + " to " + value + " (an instance of "
				+ value.getClass() + ")");

		/*
		 * if (stageNumber > getRowCount() ) { Vector<String> dataRow= new
		 * Vector<String>();
		 * 
		 * dataRow.add("1"); dataRow.add("2"); dataRow.add("3");
		 * 
		 * stageStrings.add(dataRow); }
		 */

		// Vector<String> stageString = stageStrings.get(stageNumber);
		// (heatingProfile.get(stageNumber) ).setElementAt(value, field);

		// stageStrings.setElementAt(stageString, stageNumber);

		fireTableRowsInserted(1, 1);

		logger.debug("New value of data:");
		printDebugData();
	}

	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i = 0; i < numRows; i++) {
			System.out.println("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.println("  " + getValueAt(i, j));
			}
			System.out.println("");
		}
		System.out.println("--------------------------");
	}
}