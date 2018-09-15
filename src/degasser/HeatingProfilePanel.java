package degasser;

import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

class HeatingProfilePanel extends JPanel implements TableModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );
	
	JTable table;
	HeatingProfileTableModel heatingProfileTableModel;
	HeatingProfile heatingProfile;
	
	// TimerPanel heatingTimerPanel;
	// TimerPanelStage heatingTimerPanelStage;
	int selectedStage = 0;
	HeatingProgressElements heatingProgressElements;

	public HeatingProfileTableModel getHeatingProfileTableModel() {
		return heatingProfileTableModel;
	}

	public void setHeatingProfileTableModel(HeatingProfileTableModel heatingProfileTableModel) {
		this.heatingProfileTableModel = heatingProfileTableModel;
	}

	public HeatingProfilePanel(HeatingProfile heatingProfile, HeatingProgressElements heatingProgressElements) {
		
		super(new GridLayout(1, 1));
		
		this.heatingProfile = heatingProfile;
		// this.heatingTimerPanel = heatingTimerPanel;
		// this.heatingTimerPanelStage = heatingTimerPanelStage;
		this.heatingProgressElements = heatingProgressElements;
		
		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		// this.setBackground(Color.CYAN);

		boolean DEBUG = true;

		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Stage");
		columnNames.add("Time (minutes)");
		columnNames.add("Set Point Current (mA)");
		
		heatingProfileTableModel = new HeatingProfileTableModel(heatingProfile, heatingProgressElements);

		// scrollPane.setPreferredSize(new Dimension(screenSize.width - 100, 200) );
		//textArea.setPreferredSize(new Dimension(screenSize.width - 100, 200) );
		//this.setPreferredSize(new Dimension(screenSize.width - 100, 200) );
		
		// heatingProfileTableModel.setValueInTable(String.valueOf(15), 1, 1); 

		table = new JTable(heatingProfileTableModel);
		// table.setPreferredScrollableViewportSize(new Dimension(screenSize.width - 100, 300) );
		table.setFillsViewportHeight(true);
		
		// table.setDefaultEditor(Integer.class, new IntegerEditor(0,100) );
		
		table.getTableHeader().setFont(new Font("SansSerif", Font.ITALIC, 20) );
		table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20) );
		
		/*
		JTextField textField = new JTextField();
		textField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20) );
		textField.setBorder(new LineBorder(Color.BLACK));
		DefaultCellEditor dce = new DefaultCellEditor( textField );
		*/
		TableColumnModel columnModel = table.getColumnModel();
		// columnModel.getColumn(1).setCellEditor(dce);
		// columnModel.getColumn(2).setCellEditor(dce);
		
		columnModel.getColumn(1).setCellEditor(new IntegerEditor(1, 1200));
		columnModel.getColumn(2).setCellEditor(new IntegerEditor(0, 5000));
		
		table.setRowHeight(table.getRowHeight() + 10);

		/*
		if (DEBUG) {
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					JTable target = (JTable)e.getSource();
				    int row = target.getSelectedRow();
				    int column = target.getSelectedColumn();
				    setSelectedStage(row);
					printDebugData(table);
				}
			});
		*/
		
		/*
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JTable target = (JTable)e.getSource();
			    int row = target.getSelectedRow();
			    System.out.println("selected row: " + row);
			    heatingProfile.setSelectedStage(row);
				printDebugData(table);
			}
		});
		*/ 
		
		
		
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
	        public void valueChanged(ListSelectionEvent event) {
	        	
	        	// e.getValueIsAdjusting 
	        	// getValueIsAdjusting() method
	
				if (!event.getValueIsAdjusting() ) {
	            // do some actions here, for example
	            // print first column value from selected row
	            // System.out.println("table selection changed: " + table.getValueAt(table.getSelectedRow(), 0).toString() );
					System.out.println("getting a row: " + table.getSelectedRow() );
					
					heatingProfile.setSelectedStage(table.getSelectedRow() );
					
					if (table.getSelectedRow() != -1) {
						heatingProgressElements.setStageTimeFromHeatingProfile();
					} else {
						heatingProgressElements.setStageTimeFromHeatingProfile(0);
					}
					printDebugData(table);
					
				}
	        }
	    });
	    
		// table.getModel().addTableModelListener(this);
		
		// table.setRowSelectionInterval(0, 0);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		
		
		// scrollPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight() ) );
		
		this.add(scrollPane);
		
		// scrollPane.setPreferredSize(preferredSize);
		
		// this.setMinimumSize(new Dimension(this.getWidth(), 300) );
		this.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		this.setSize(new Dimension(this.getWidth(), this.getHeight() ) );
		// this.setBackground(Color.RED);
		
	}
	
	
	
	public void tableChanged(TableModelEvent e) {
		System.out.println("table changed: " + e.getSource());
		if(e.getType() == TableModelEvent.UPDATE) {
			int row = e.getFirstRow();
			int column = e.getColumn();
			System.out.println("table was updated");
			System.out.println(table.getValueAt(table.getSelectedRow(), 0).toString() );
			
			heatingProfile.setSelectedStage(row);
			printDebugData(table);
			
			// heatingTimerPanel.setTimeFromHeatingProfile();
			// heatingTimerPanelStage.setTimeFromHeatingProfile();
			
			heatingProgressElements.setOverallTimeFromHeatingProfile();
			heatingProgressElements.setStageTimeFromHeatingProfile();
		}
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
		
		// heatingProgressElements.setStageTimeFromHeatingProfile();
	}

	public int getSelectedStage() {
		int selectedStage = table.getSelectedRow();
		return selectedStage;
	}

	public void setExecutingStage(int stageNumber) {
		
		System.out.println("executing stage: " + stageNumber + " heating profile stages: " + heatingProfile.getSize() );
		
		this.getHeatingProfileTabelModel().fireTableDataChanged();
		if (stageNumber < heatingProfile.getSize() ) {
			System.out.println("after - executing stage: " + stageNumber + " heating profile stages: " + heatingProfile.getSize() );
			getTable().clearSelection();
			table.setRowSelectionInterval(stageNumber, stageNumber);
			heatingProfile.setSelectedStage(stageNumber);
			
			// heatingProgressElements.setStageTimeFromHeatingProfile();
			// this.getHeatingProfileTabelModel().fireTableDataChanged();
		} else if (stageNumber < 0) {
			table.setRowSelectionInterval(0, 0);
			heatingProfile.setSelectedStage(0);
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
	
	void setSelecctedRow(int selectedRow) {
		
	}

}
