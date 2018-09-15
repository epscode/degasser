package degasser;

import org.apache.log4j.Logger;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class HeatingProgressElements {
	
	// for overall timer
	public JProgressBar overallProgressBar;
	public JLabel overallProgressLabel;
	public Timer overallTimer;
	public LongTask overallTask;
	
	// for stage timer
	public JProgressBar stageProgressBar;
	public JLabel stageProgressLabel;
	public Timer stageTimer;
	public LongTask stageTask;
	
	public HeatingProfile heatingProfile;
	
	HeatingProgressElements(HeatingProfile heatingProfile) {
		this.heatingProfile = heatingProfile;
		
		// overallProgressBar = new JProgressBar(); 
		overallProgressLabel = new JLabel();
		stageProgressLabel = new JLabel();
	}
	
	public LongTask getOverallTask() {
		return overallTask;
	}



	public void setOverallTask(LongTask overallTask) {
		this.overallTask = overallTask;
	}



	public LongTask getStageTask() {
		return stageTask;
	}



	public void setStageTask(LongTask stageTask) {
		this.stageTask = stageTask;
	}



	public JProgressBar getOverallProgressBar() {
		return overallProgressBar;
	}

	public void setOverallProgressBar(JProgressBar overallProgressBar) {
		this.overallProgressBar = overallProgressBar;
	}

	public JLabel getOverallProgressLabel() {
		return overallProgressLabel;
	}

	public void setOverallProgressLabel(JLabel overallProgressLabel) {
		this.overallProgressLabel = overallProgressLabel;
	}

	public Timer getOverallTimer() {
		return overallTimer;
	}

	public void setOverallTimer(Timer overallTimer) {
		this.overallTimer = overallTimer;
	}

	public JProgressBar getStageProgressBar() {
		return stageProgressBar;
	}

	public void setStageProgressBar(JProgressBar stageProgressBar) {
		this.stageProgressBar = stageProgressBar;
	}

	public JLabel getStageProgressLabel() {
		return stageProgressLabel;
	}

	public void setStageProgressLabel(JLabel stageProgressLabel) {
		this.stageProgressLabel = stageProgressLabel;
	}

	public Timer getStageTimer() {
		return stageTimer;
	}

	public void setStageTimer(Timer stageTimer) {
		this.stageTimer = stageTimer;
	}
	
	public void setTime(long duration) {
		
	}
	
	public void setOverallTimeFromHeatingProfile() {
		
		System.out.println("from overall time in heatingProgressElements.");
		
		// duration = 120000;
		long duration = heatingProfile.calculateTotalRunTime() * 1000 * 60;
		
		String text = getStringTime(duration);
		
		System.out.println("duration from heatingProgressElements: " + text);
		
		getOverallProgressLabel().setText("Heating Profile Time Remaining: " + text);
	}
	
public void setStageTimeFromHeatingProfile() {
		
		System.out.println("from stage time in heatingProgressElements.");
		
		// duration = 120000;
		long duration = heatingProfile.getHeatingStage(heatingProfile.getSelectedStage() ).getTime() * 1000 * 60;
		
		String text = getStringTime(duration);
		
		System.out.println("stage duration from heatingProgressElements: " + text);
		
		getStageProgressLabel().setText("Heating Stage Time Remaining: " + text);
	}

public void setStageTimeFromHeatingProfile(int time) {
	
	System.out.println("from stage time in heatingProgressElements.");
	
	// time = 9;
	// duration = 120000;
	long duration = time * 1000 * 60;
	
	String text = getStringTime(duration);
	
	System.out.println("stage duration from heatingProgressElements: " + text);
	
	getStageProgressLabel().setText("Heating Stage Time Remaining: " + text);
}
	
	private static final String getStringTime(long millis) {
		// logger.debug("number of millis from getStringTime: " + millis);
		int seconds = (int) (millis / 1000.0);
		int minutes = (int) (seconds / 60.0);
		// int hours = (int) (minutes / 60);
		// minutes -= hours * 60;
		// seconds -= (hours * 3600) + (minutes * 60);
		seconds -= (minutes * 60);
		return (
			// ((hours < 10) ? "0" + hours : "" + hours)
			//	+ ":"
				((minutes < 10) ? "0" + minutes : "" + minutes)
				+ ":"
				+ ((seconds < 10) ? "0" + seconds : "" + seconds));
	}
	
}