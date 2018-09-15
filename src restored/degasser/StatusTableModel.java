package degasser;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

class StatusTableModel extends AbstractTableModel {

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	private static final long serialVersionUID = 1L;

	Vector<String> columnNames = new Vector<String>();
	// Vector<String> data = new Vector<String>();
	StatusRow statusRow;
	
	
	private Object[][] data = {
	        { "Mary", "Campione", "Snowboarding", "test",
	            "test", "test" } };
	
	
	public boolean isCellEditable(int row, int column) {  
        return false;  
    }
	
	public StatusTableModel(StatusRow statusRow) {
		setColumnNames();
		
		
		this.statusRow = statusRow;
		/*
		data.add(statusRow.getStageString() );
		data.add(statusRow.getStageString() );
		data.add(statusRow.getStageString() );
		data.add(statusRow.getStageString() );
		data.add(statusRow.getStageString() );
		data.add(statusRow.getStageString() );
		
		// super.addRow(data);
		/*
		getValueAt(0, 0);
		getValueAt(0, 1); 
		getValueAt(0, 2); 
		getValueAt(0, 3); 
		getValueAt(0, 4); 
		getValueAt(0, 5); 
		
		// fireTableRowsInserted(0, 1);
		*/
	
		
		printDebugData();
	}
	
	public int getColumnCount() {
		return columnNames.size();
	}

	public int getRowCount() {
		return 1;
	}
	
	public void setColumnNames() {
		columnNames.add("Stage");
		columnNames.add("Filaments");
		columnNames.add("Relay (%)");
		columnNames.add("Current (mA)");
		columnNames.add("Relay Temp (C)");
		columnNames.add("Vacuum (mT)");
	}

	public String getColumnName(int col) {
		return columnNames.elementAt(col);
	}
	
	public void setElementStatus(int field, int value) {
		
	}

	public void setValueAt(String value, int row, int field) {
		
		// remember this sets the table
		logger.debug("Setting value at " + field + " to " + value + " (an instance of " + value.getClass() + ")");
		
		// fireTableCellUpdated(row, field);

		logger.debug("New value of data: ");
		printDebugData();
	}
	
	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i = 0; i < numRows; i++) {
			logger.debug("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				logger.debug("  " + getValueAt(i, j) );
			}
			logger.debug("");
		}
		logger.debug("--------------------------");
	}

	@Override
	public String getValueAt(int rowIndex, int columnIndex) {
		
		// logger.debug("is this firing?");
		// logger.debug("row Index: " + rowIndex + " column index: " + columnIndex) ;
		String valueString = "";
		
		switch(columnIndex) {
		case(0):
			valueString = (statusRow.getStageString() );
			break;
		case(1):
			valueString =  (statusRow.getFilamentsString() );
			break;
		case(2):
			valueString =  (statusRow.getRelayString() );
			break;
		case(3):
			valueString =  (statusRow.getCurrentString() );
			break;
		case(4):
			valueString =  (statusRow.getTempString() );
			break;
		case(5):
			valueString =  (statusRow.getVacuumString() );
			break;
		default:
			logger.info("trying to set a status column that does not exist: " + columnIndex);
			valueString =  "ERROR";
			
		}
		
		// data[rowIndex][columnIndex] = valueString;
		
		// fireTableCellUpdated(rowIndex, columnIndex);
		
		return valueString;
		
	}
}