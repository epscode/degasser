package degasser;

import java.text.*;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultStyledDocument;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import org.apache.commons.io.FileUtils;

public class LogPanel extends CollapsiblePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	JTextArea textArea;
	JScrollPane scrollPane;
	JScrollBar vertical;
	JFrame frame;
	String tempPath;
	static boolean errorReported = false;

	JLabel test;

	JButton okButton, cancelButton;
	
	public void addText(String text) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		
				int appendingText = text.length();
				textArea.append(text);
				saveTempLogText(text);
				
				int characterCount = textArea.getText().length();
					
				System.out.println("character count: " + characterCount);
				if (characterCount > 1000) {
					System.out.println("character count is greater then 1000");
					String textAreaString = textArea.getText();
					// System.out.println("textarea character: ")
					String truncatedString = textAreaString.substring(appendingText - 1000);
					textArea.setText(truncatedString);
				}
				
				vertical.setValue( vertical.getMaximum() );
				textArea.setCaretPosition( textArea.getDocument().getLength() );
			}
		});
	}
	
	public void setCaratToEnd() {
		textArea.setCaretPosition( textArea.getDocument().getLength() );
	}
	
	public void justAddText(String text) {
		textArea.append(text);
		vertical.setValue( vertical.getMaximum() );
		textArea.setCaretPosition( textArea.getDocument().getLength() );
	}
	
	public void clearLogs() {
		
		textArea.setText("");
		textArea.setCaretPosition( textArea.getDocument().getLength() );
		
		String logsFilename = tempPath + "commlog.txt";
		
		logger.debug("trying to write the logs file: " + logsFilename);
		// System.out.println("trying to write the logs file: " + logsFilename);
		
		// FileWriter logsWriter = null;
		BufferedWriter logsPrinter = null;
		
		try {
			File file = new File(logsFilename);

			// if file doesn't exists, then create it
			if (!file.exists() ) {
				file.createNewFile();
			}
			
			// logsWriter = new FileWriter(logsFilename, true);
			logsPrinter = new BufferedWriter(new FileWriter(logsFilename, false) );
		
			logsPrinter.write("");
			
		} catch (IOException error) {
			logger.error("can't save log file: " + error);
			// System.out.println("can't save log file: " + error);

			if (!errorReported) {
				errorReported = false;
				
				JOptionPane.showMessageDialog(frame, "Error saving file " + logsFilename, "File Error",
					JOptionPane.ERROR_MESSAGE);
			}
		} finally {
			
			try {
				/*
				if (logsWriter != null) {
					logsWriter.close();
					// errorReported = false;
				}
				*/
				
				if (logsPrinter != null) {
					logsPrinter.close();
				}			

			} catch (IOException error) {

				logger.error("can't save log file: " + error);
				
				if (!errorReported) {
					errorReported = true;
					
					JOptionPane.showMessageDialog(frame, "Error saving file " + logsFilename, "File Error",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	public void saveTempLogText(String text) {
		
		String logsFilename = tempPath + "commlog.txt";
		
		logger.debug("trying to write the logs file: " + logsFilename);
		// System.out.println("trying to write the logs file: " + logsFilename);
		
		// FileWriter logsWriter = null;
		BufferedWriter logsPrinter = null;
		
		try {
			File file = new File(logsFilename);

			// if file doesn't exists, then create it
			if (!file.exists() ) {
				file.createNewFile();
			}
			
			// logsWriter = new FileWriter(logsFilename, true);
			logsPrinter = new BufferedWriter(new FileWriter(logsFilename, true));
		
			logsPrinter.append(text);
			
		} catch (IOException error) {
			logger.error("can't save log file: " + error);
			// System.out.println("can't save log file: " + error);

			if (!errorReported) {
				errorReported = false;
				
				JOptionPane.showMessageDialog(frame, "Error saving file " + logsFilename, "File Error",
					JOptionPane.ERROR_MESSAGE);
			}
		} finally {
			
			try {
				/*
				if (logsWriter != null) {
					logsWriter.close();
					// errorReported = false;
				}
				*/
				
				if (logsPrinter != null) {
					logsPrinter.close();
				}			

			} catch (IOException error) {

				logger.error("can't save log file: " + error);
				
				if (!errorReported) {
					errorReported = true;
					
					JOptionPane.showMessageDialog(frame, "Error saving file " + logsFilename, "File Error",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	public void saveLogs(String logsFilename) {
		
		logger.debug("trying to write the logs file: " + logsFilename);
		// System.out.println("trying to write the logs file: " + logsFilename);
		
		String logsTempFilename = tempPath + "commlog.txt";
		File tempLogfile = new File(logsTempFilename);
		File logsFile= new File(logsFilename);

		try {
			FileUtils.copyFile(tempLogfile, logsFile);

		} catch (IOException error) {
			logger.error("can't save log file: " + error);
			// System.out.println("can't save log file: " + error);

			JOptionPane.showMessageDialog(frame, "Error saving file " + logsFilename, "File Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public LogPanel(JFrame frame, String tempPath) {
		
		this.frame = frame;
		this.tempPath = tempPath;
		
		// super(new GridLayout(1, 1));
		

		// Get the screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();

		textArea = new JTextArea();

		textArea.setText("Degasser is not connected.\n");

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setCaretPosition( textArea.getDocument().getLength() );
		
		textArea.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20) );
		
		scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		vertical = scrollPane.getVerticalScrollBar();
		
		textArea.setEditable(false);
		
		// Border 
		// scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 25));
		// this.setMargin(new Insets(10, 10, 10, 10));
		// this.setBackground(Color.LIGHT_GRAY);
		
		this.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		// this.add(scrollPane);
		
		this.setMinimumSize(new Dimension(this.getWidth(), 200) );
		this.setPreferredSize(new Dimension(this.getWidth(), 200) );
		
		add(scrollPane, 0);
	}

	void cancelButton_actionPerformed(ActionEvent error) {

	}

	void okButton_actionPerformed(ActionEvent error) {

	}

	void addLine(String line) {
		textArea.append(line);
	}

}