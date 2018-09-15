package degasser;

import java.text.*;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

class IntegerTextField extends JFormattedTextField {
	
	NumberFormat format = NumberFormat.getInstance();
    NumberFormatter formatter = new NumberFormatter(format);
    
	public IntegerTextField() {
		
		// super();
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(0);
	    formatter.setMaximum(Integer.MAX_VALUE);
	    formatter.setAllowsInvalid(false);
	    formatter.setCommitsOnValidEdit(true);
	    formatter.setAllowsInvalid(false);
	    
	    this.setFormatter(formatter);  
	}
}
