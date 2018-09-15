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

		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Stage");
		columnNames.add("Filaments");
		columnNames.add("Relay (%)");
		columnNames.add("Current (mA)");
		columnNames.add("Relay Temp (C)");
		columnNames.add("Vacuum (mT)");
		
		statusTableModel = new StatusTableModel(statusRow);

		table = new JTable(statusTableModel);
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
