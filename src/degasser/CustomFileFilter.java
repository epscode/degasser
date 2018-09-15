package degasser;

import java.io.File;
import javax.swing.filechooser.*;

public class CustomFileFilter extends FileFilter {

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = FileTypes.getExtension(f);
		if (extension != null) {
			if (extension.equals(FileTypes.txt) ) {
					return true;
			} else {
				return false;
			}
		}

		return false;
	}

	//The description of this filter
	public String getDescription() {
		return "Only .txt Files";
	}
}
