package degasser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

class UploadFirmware implements Runnable {

	String firmwarePath = "";
	String firmwareFilename = "";
	LogPanel logPanel;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	UploadFirmware(String firmwarePath, String firmwareFilename, LogPanel logPanel) {
		this.firmwarePath = firmwarePath;
		this.firmwareFilename = firmwareFilename;
		this.logPanel = logPanel;
	}

	public void run() {

		logger.info("uploading thread");

		try {
			ProcessBuilder builder = new ProcessBuilder(firmwarePath + "upload.sh", firmwareFilename);

			builder.redirectErrorStream(true);

			Process p = builder.start();
			String s;
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = stdout.readLine()) != null) {
				logger.info(s);
				logPanel.addText(s + "\r\n");
			}

			logger.info("Exit value: " + p.waitFor());
			p.getInputStream().close();
			p.getOutputStream().close();
			p.getErrorStream().close();

			JOptionPane.showMessageDialog(null, "Firmware Upload Process Complete - Check Output", "Control Message",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception error) {
			logger.info("port did not open.");
		}
	}
}
