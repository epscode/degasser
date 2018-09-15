package degasser;

import org.apache.log4j.Logger;

public class HeatingStage {

	int stageNumber;
	int time; // in seconds
	int relay;
	
	// log4j
	static Logger logger = Logger.getLogger(Degasser.class.getName() );

	public HeatingStage(int stageNumber, int time, int relay) {
		this.stageNumber = stageNumber;
		this.time = time;
		this.relay = relay;
	}
	
	public String printStage() {
		return (stageNumber + " " + time + " " + relay + "\n");
	}

	public int getStageNumber() {
		return stageNumber;
	}

	public void setStageNumber(int stageNumber) {
		this.stageNumber = stageNumber;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getRelay() {
		return relay;
	}

	public void setCurrent(int relay) {
		this.relay = relay;
	}
	
	public void setField(int field, int value) {
		
		switch(field) {
		case (0):
			stageNumber = value;
			break;
		case (1) :
			time = value;
			break;
		case (2):
			relay = value;
			break;
		default:
				logger.error("shouldn't be able to get to this field");
		}
	}
	
	public int getField(int field) {
		
		int value = 0;
		switch(field) {
		case (0):
			value = stageNumber;
			break;
		case (1) :
			value = time;
			break;
		case (2):
			value = relay;
			break;
		default:
				logger.error("shouldn't be able to get to this field");
		}
		return  value;
	}
}