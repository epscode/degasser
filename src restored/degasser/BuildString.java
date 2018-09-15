package degasser;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BuildString {

	static int buildNumberInt = MyReleaseInfo.getBuildNumber();
	static String versionNumber = MyReleaseInfo.getVersion();
	static Date buildDate = MyReleaseInfo.getBuildDate();
	
	static DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy H:mm aaa z");
	static String buildDateString = dateFormat.format(buildDate);

	static String buildNumber = "Build #" + buildNumberInt + " on "
			+ buildDateString;
	
	static String buildString = "version number: " + versionNumber + 
	" build number: " + buildNumberInt + " on " + buildDateString;
	
	static String getBuildString() {
		return buildString;
	}
	
}
