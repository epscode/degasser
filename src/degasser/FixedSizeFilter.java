package degasser;

/*
 * Created on Oct 15, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import javax.swing.text.*;

/**
 * @author emorris
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class FixedSizeFilter extends DocumentFilter {
		int maxSize;
    
		// limit is the maximum number of characters allowed.
		public FixedSizeFilter(int limit) {
			maxSize = limit;
		}
    
		// This method is called when characters are inserted into the document
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String str,
				AttributeSet attr) throws BadLocationException {
			replace(fb, offset, 0, str, attr);
		}
    
		// This method is called when characters in the document are replace with other characters
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
				String str, AttributeSet attrs) throws BadLocationException {
			int newLength = fb.getDocument().getLength()-length+str.length();
			if (newLength <= maxSize) {
				fb.replace(offset, length, str, attrs);
			} else {
				throw new BadLocationException("New characters exceeds max size of document", offset);
			}
		}
	} 