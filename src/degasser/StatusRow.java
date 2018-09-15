package degasser;

class StatusRow {
	
	int stage;
	int filaments;
	int powerSupplies;
	int relay;
	int current;
	double temp;
	double vacuum;
	
	StatusRow(int stage, int filaments, int powerSupplies, int relay, int current, double temp, double vacuum) {
		this.stage = stage;
		this.filaments = filaments;
		this.powerSupplies = powerSupplies;
		this.relay = relay;
		this.current = current;
		this.temp = temp;
		this.vacuum = vacuum;	
	}

	public int getPowerSupplies() {
		return powerSupplies;
	}
	
	public String getPowerSuppliesString() {
		return (new Integer(powerSupplies).toString() );
	}
	
	public void setPowerSupplies(int powerSupplies) {
		this.powerSupplies = powerSupplies;
	}

	public int getStage() {
		return stage;
	}
	
	public String getStageString() {
		return (new Integer(stage).toString() );
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public int getFilaments() {
		return filaments;
	}
	
	public String getFilamentsString() {
		return (new Integer(filaments).toString() );
	}

	public void setFilaments(int filaments) {
		this.filaments = filaments;
	}

	public int getRelay() {
		return relay;
	}
	
	public String getRelayString() {
		return (new Integer(relay).toString() );
	}

	public void setRelay(int relay) {
		this.relay = relay;
	}

	public double getCurrent() {
		return current;
	}
	
	public String getCurrentString() {
		return (new Integer(current).toString() );
	}

	public void setCurrent(int current) {
		this.current = current;
	}
	
	public String getTempString() {
		return (new Double(temp).toString() );
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
	
	public String getVacuumString() {
		return (new Double(vacuum).toString() );
	}

	public void setVacuum(double vacuum) {
		this.vacuum = vacuum;
	}
	
}