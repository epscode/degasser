package degasser;

import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;


import org.apache.log4j.Logger;

class StatusPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );
	
	JTable table;
	StatusTableModel statusTableModel;
	HeatingProfile heatingProfile;
	StatusRow statusRow;
	
	StatusColumnCellRenderer tempStatusColor;
	StatusColumnCellRenderer vacuumStatusColor;
	
	void setTempStatusState(int state) {
		if (state == 0) {
			tempStatusColor.clear();
		} else if (state == 1) {
			tempStatusColor.clear();
			tempStatusColor.setWarning(true);
		} else if (state == 2) {
			tempStatusColor.clear();
			tempStatusColor.setError(true);
		} else {
			logger.info("not a valid state");
		}
	}
		
	void setVacuumStatusState(int state) {
		if (state == 0) {
			vacuumStatusColor.clear();
		} else if (state == 1) {
			vacuumStatusColor.clear();
			vacuumStatusColor.setWarning(true);
		} else if (state == 2) {
			vacuumStatusColor.clear();
			vacuumStatusColor.setError(true);
		} else {
			logger.info("not a valid state");
		}
	}
	
	public StatusPanel(StatusRow statusRow) {
		super(new GridLayout(1, 1));
		
		/*
		setLayout(new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
		*/
		this.statusRow = statusRow;
		
		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		this.setBackground(Color.CYAN);

		boolean DEBUG = false;

		
		statusTableModel = new StatusTableModel(statusRow);

		table = new JTable(statusTableModel);
		
		tempStatusColor = new StatusColumnCellRenderer();		
		table.getColumnModel().getColumn(5).setCellRenderer(tempStatusColor);
		
		// tempStatusColor.setError(true);
		
		vacuumStatusColor = new StatusColumnCellRenderer();		
		table.getColumnModel().getColumn(6).setCellRenderer(vacuumStatusColor);
		
		// vacuumStatusColor.setWarning(true);

		
		
		// table.setPreferredScrollableViewportSize(new Dimension(this.getWidth(), 60) );
		// table.setMinimumSize(new Dimension(this.getWidth(), 60) );
		table.setFillsViewportHeight(true);
		
		table.getTableHeader().setFont(new Font("SansSerif", Font.ITALIC, 20) );
		table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20) );
		
		table.setRowHeight(table.getRowHeight() + 10);
		// table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
		if (DEBUG) {
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					printDebugData(table);
				}
			});
		}

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		
		// this.setPreferredSize(new Dimension(screenSize.width - 100, 60) );
		// this.setPreferredSize(new Dimension(this.getWidth(), 60) );
		this.setMinimumSize(new Dimension(this.getWidth(), 60) );
		this.setPreferredSize(new Dimension(this.getWidth(), 60) );
		
		this.add(scrollPane);
	}
	
	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		// System.out.println("Value of data: ");
		for (int i = 0; i < numRows; i++) {
			// System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j) );
			}
			// System.out.println();
		}
		// System.out.println("--------------------------");
	}

}
