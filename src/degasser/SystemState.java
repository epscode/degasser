package degasser;

class SystemState {
	
	boolean runState = false;
	boolean powerSupplyState = false;
	boolean filamentsSet = false;
	
	int running;
	int stage;
	int filaments;
	int powersupplies;
	int current;
	double temp;
	double vacuum;
	
	SystemState() {
		
	}
	
	
	
	public int getRunning() {
		return running;
	}



	public void setRunning(int running) {
		this.running = running;
	}



	public boolean isRunState() {
		return runState;
	}
	public void setRunState(boolean runState) {
		this.runState = runState;
	}
	public boolean isPowerSupplyState() {
		return powerSupplyState;
	}
	public void setPowerSupplyState(boolean powerSupplyState) {
		this.powerSupplyState = powerSupplyState;
	}

	public boolean isFilamentsSet() {
		return filamentsSet;
	}

	public void setFilamentsSet(boolean filamentsSet) {
		this.filamentsSet = filamentsSet;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public int getFilaments() {
		return filaments;
	}

	public void setFilaments(int filaments) {
		this.filaments = filaments;
	}

	public int getPowersupplies() {
		return powersupplies;
	}

	public void setPowersupplies(int powersupplies) {
		this.powersupplies = powersupplies;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public double getVacuum() {
		return vacuum;
	}

	public void setVacuum(double vacuum) {
		this.vacuum = vacuum;
	}
	
	
	
	
	
}