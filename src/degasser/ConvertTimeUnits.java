package degasser;

import javax.swing.JLabel;

public class ConvertTimeUnits {
	
	int hours = 0;
	int minutes= 0;
	int seconds = 0;
	
	int totalSeconds = 0;
	
	ConvertTimeUnits(int totalSeconds) {
		hours = totalSeconds / 3600;
		minutes = (totalSeconds % 60) - (hours * 60);
		seconds = totalSeconds - (hours * 60) - (minutes * 60);
	}

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setTotalSeconds(int totalSeconds) {
		this.totalSeconds = totalSeconds;
	}
	
	public String formatTimeString() {
		
		String timeString = hours + ":" + minutes + ":" + seconds;	
		return timeString;
	}
	
	public String getFormattedTimeString() {
	
		String formattedString = "<font color = \"red\" size=\"20\">Time Remaining: " + formatTimeString() + "</font>";
		return formattedString;
	}
}