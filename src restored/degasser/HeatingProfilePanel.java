package degasser;

import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;


import org.apache.log4j.Logger;

class HeatingProfilePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );
	
	JTable table;
	HeatingProfileTableModel heatingProfileTableModel;
	HeatingProfile heatingProfile;
	int selectedStage = 0;

	public HeatingProfilePanel(HeatingProfile heatingProfile) {
		
		super(new GridLayout(1, 1));
		
		this.heatingProfile = heatingProfile;
		
		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		this.setBackground(Color.CYAN);

		boolean DEBUG = false;

		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Stage");
		columnNames.add("Time (minutes)");
		columnNames.add("Set Point Current (mA)");
		
		
		heatingProfileTableModel = new HeatingProfileTableModel(heatingProfile);

		// scrollPane.setPreferredSize(new Dimension(screenSize.width - 100, 200) );
		//textArea.setPreferredSize(new Dimension(screenSize.width - 100, 200) );
		//this.setPreferredSize(new Dimension(screenSize.width - 100, 200) );
		
		// heatingProfileTableModel.setValueInTable(String.valueOf(15), 1, 1); 

		table = new JTable(heatingProfileTableModel);
		// table.setPreferredScrollableViewportSize(new Dimension(screenSize.width - 100, 300) );
		table.setFillsViewportHeight(true);
		
		table.getTableHeader().setFont(new Font("SansSerif", Font.ITALIC, 20) );
		table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20) );
		
		table.setRowHeight(table.getRowHeight() + 10);

		if (DEBUG) {
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					JTable target = (JTable)e.getSource();
				    int row = target.getSelectedRow();
				    // int column = target.getSelectedColumn();
				    setSelectedStage(row);
					printDebugData(table);
				}
			});
		}
		
		table.setRowSelectionInterval(0, 0);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		
		this.setMinimumSize(new Dimension(this.getWidth(), 300) );
		this.setPreferredSize(new Dimension(this.getWidth(), 300) );
		
		this.add(scrollPane);
	}
	
	public HeatingProfileTableModel getHeatingProfileTabelModel() {
		return heatingProfileTableModel;
	}
	
	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	
	public void setSelectedStage(int selectedStage) {
		this.selectedStage = selectedStage;
	}

	public int getSelectedStage() {
		int selectedStage = table.getSelectedRow();
		return selectedStage;
	}

	public void setExecutingStage(int stageNumber) {
		
		if (stageNumber < heatingProfile.getSize() ) {
			table.setRowSelectionInterval(stageNumber, stageNumber);
		}
	}

	public void clearExecutingStage() {
		table.clearSelection();
	}
	
	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j) );
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

}
