package degasser;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StatusColumnCellRenderer extends DefaultTableCellRenderer {

	boolean warning = false;
	boolean error = false;

	public void setWarning(boolean state) {
		warning = state;
	}

	public void setError(boolean state) {
		error = state;
	}

	public boolean isWarning() {
		return warning;
	}

	public boolean isError() {
		return error;
	}
	
	public void clear() {
		setWarning(false);
		setError(false);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int col) {

		// Cells are by default rendered as a JLabel.
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

		if (isWarning() )
			label.setBackground(Color.YELLOW);

		if (isError() )
			label.setBackground(Color.RED);
		
		if (!isWarning() && !isError() ) 
			label.setBackground(Color.WHITE);

		// Return the JLabel which renders the cell.
		return label;
	}
}