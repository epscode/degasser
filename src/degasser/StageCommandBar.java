package degasser;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

class StageCommandBar extends JPanel {

	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName());

	JButton addStageButton = new JButton("Add Stage");
	JButton removeStageButton = new JButton("Remove Stage");
	JButton insertStageButton = new JButton("Insert Stage");
	
	TimerPanel heatingTimerPanel;
	TimerPanelStage heatingTimerPanelStage;

	JFrame frame;
	HeatingProfilePanel heatingProfilePanel;

	StageCommandBar(JFrame frame, HeatingProfilePanel heatingProfilePanel, 
			TimerPanel heatingTimerPanel, TimerPanelStage heatingTimerPanelStage) {
		// super(frame, heatingProfilePanel);

		this.frame = frame;

		this.heatingProfilePanel = heatingProfilePanel;
		this.heatingTimerPanel = heatingTimerPanel;
		this.heatingTimerPanelStage = heatingTimerPanelStage;
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		// gridBagConstraints.insets = new Insets(0, 25, 0, 25);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		
		addStageButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		removeStageButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		insertStageButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));

		this.setLayout(gridbag);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridbag.setConstraints(addStageButton, gridBagConstraints);
		this.add(addStageButton);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridbag.setConstraints(removeStageButton, gridBagConstraints);
		this.add(removeStageButton);

		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridbag.setConstraints(insertStageButton, gridBagConstraints);
		this.add(insertStageButton);

		// add a stage
		addStageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addStageButton_actionPerformed(e);
			}
		});

		// remove a stage
		removeStageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeStageButton_actionPerformed(e);
			}
		});

		// insert a stage
		insertStageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insertStageButton_actionPerformed(e);
			}
		});
		
		this.setMinimumSize(new Dimension(this.getWidth(), 40) );
		this.setPreferredSize(new Dimension(this.getWidth(), 40) );
	}

	void addStageButton_actionPerformed(ActionEvent event) {

		int stageIndex = heatingProfilePanel.getHeatingProfileTabelModel().heatingProfile.getSize();
		heatingProfilePanel.getHeatingProfileTabelModel().getHeatingProfile().addHeatingStage(stageIndex + 1, 0, 0);

		heatingProfilePanel.getHeatingProfileTabelModel().fireTableDataChanged();
		heatingProfilePanel.setExecutingStage(stageIndex);
		
		heatingTimerPanel.setTimeFromHeatingProfile();
		heatingTimerPanelStage.setTimeFromHeatingProfile();
	}

	void removeStageButton_actionPerformed(ActionEvent event) {

		int stageSelectedIndex = heatingProfilePanel.heatingProfile.getSelectedStage();

		System.out.println("selected stage: " + stageSelectedIndex);
		
		int numberOfStages = heatingProfilePanel.heatingProfile.getSize();

		if (numberOfStages > 1) {
			heatingProfilePanel.getHeatingProfileTabelModel().getHeatingProfile().removeHeatingStage(stageSelectedIndex);

			heatingProfilePanel.getHeatingProfileTabelModel().fireTableDataChanged();

			if (stageSelectedIndex > 0) {
				heatingProfilePanel.setExecutingStage(stageSelectedIndex - 1);
			} else {
				heatingProfilePanel.setExecutingStage(0);
			}
		}
		
		heatingTimerPanel.setTimeFromHeatingProfile();
		heatingTimerPanelStage.setTimeFromHeatingProfile();
		
	}

	void insertStageButton_actionPerformed(ActionEvent event) {

		int stageSelectedIndex = heatingProfilePanel.heatingProfile.getSelectedStage();

		heatingProfilePanel.getHeatingProfileTabelModel().getHeatingProfile().insertHeatingStage(stageSelectedIndex, 0, 0);
		
		heatingProfilePanel.getHeatingProfileTabelModel().fireTableDataChanged();

		heatingProfilePanel.setExecutingStage(stageSelectedIndex + 1);
		
		heatingTimerPanel.setTimeFromHeatingProfile();
		heatingTimerPanelStage.setTimeFromHeatingProfile();
	}

	public Vector<String> getHeatingProfileStages() {

		int numberOfHeatingStages = heatingProfilePanel.getHeatingProfileTabelModel().getHeatingProfile().getSize();

		Vector<String> heatingProfileStrings = new Vector<String>();

		for (int heatingStage = 0; heatingStage < numberOfHeatingStages; heatingStage++) {
			int stageName = heatingProfilePanel.getHeatingProfileTabelModel().getHeatingProfile()
					.getHeatingStage(heatingStage).getStageNumber();
			heatingProfileStrings.add(Integer.toString(stageName));
		}

		return heatingProfileStrings;
	}
}