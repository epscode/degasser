package degasser;


import java.awt.event.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import org.apache.log4j.Logger;
import java.text.*;

public class HighlightedFormattedTextField extends JFormattedTextField
		implements FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8248285127135747718L;
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	// Constructor
	public HighlightedFormattedTextField() {
		addFocusListener(this);
	}

	public HighlightedFormattedTextField(Format format) {
		super(format);
		setDocument(new NumberFilterDocument() );
		addFocusListener(this);
	}

	// Focus event handlers
	/*
	 * public void focusGained(FocusEvent ev) { logger.info(
	 * "from one of the HighlightedFormattedTextFields-is focus gained being handled"
	 * ); this.selectAll(); }
	 */

	public void focusGained(FocusEvent evt) {

		logger.debug("from one of the HighlightedFormattedTextFields-is focus gained being handled");
		
		final JFormattedTextField comp = (JFormattedTextField) evt
				.getComponent();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				comp.selectAll();
			}
		});
	}

	public void focusLost(FocusEvent ev) {
		this.select(0, 0);
	}

	public class FormattedTextFieldVerifier extends InputVerifier {
		public boolean verify(JComponent input) {
			if (input instanceof JFormattedTextField) {
				JFormattedTextField ftf = (JFormattedTextField) input;
				AbstractFormatter formatter = ftf.getFormatter();
				if (formatter != null) {
					String text = ftf.getText();
					try {
						formatter.stringToValue(text);
						return true;
					} catch (ParseException pe) {
						return false;
					}
				}
			}
			return true;
		}

		public boolean shouldYieldFocus(JComponent input) {
			return verify(input);
		}
	}

	public class NumberFilterDocument extends PlainDocument {
		private StringBuffer __scratchBuffer;

		public NumberFilterDocument() {
			__scratchBuffer = new StringBuffer();
		}

		public void insertString(int offset, String text, AttributeSet aset)
				throws BadLocationException {
			if (text == null)
				return;

			__scratchBuffer.setLength(0);

			// Reject all strings that cause the contents of the field not
			// to be a valid number (i.e., string representation of a double)
			try {
				__scratchBuffer.append(getText(0, getLength()));
				__scratchBuffer.insert(offset, text);
				// Kludge: Append a 0 so that leading decimal points
				// and signs will be accepted
				__scratchBuffer.append('0');
			} catch (BadLocationException ble) {
				ble.printStackTrace();
				return;
			} catch (StringIndexOutOfBoundsException sioobe) {
				sioobe.printStackTrace();
				return;
			}

			try {
				Double.parseDouble(__scratchBuffer.toString());
			} catch (NumberFormatException nfe) {
				// Resulting string will not be number, so reject it
				return;
			}

			super.insertString(offset, text, aset);
		}
	}
}