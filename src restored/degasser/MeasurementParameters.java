package degasser;

class MeasurementParameters {
	
	static double heaterPower;
	static int heatDuration;
	static int heatDelay;

	void setMeasurementParameters(double heaterPower, int heatDuration, int heatDelay) {
		MeasurementParameters.heaterPower = heaterPower;
		MeasurementParameters.heatDuration = heatDuration;
		MeasurementParameters.heatDelay = heatDelay;
	}

	public static double getHeaterPower() {
		return heaterPower;
	}

	public static void setHeaterPower(double heaterPower) {
		MeasurementParameters.heaterPower = heaterPower;
	}

	public static int getHeatDuration() {
		return heatDuration;
	}

	public static void setHeatDuration(int heatDuration) {
		MeasurementParameters.heatDuration = heatDuration;
	}

	public static int getHeatDelay() {
		return heatDelay;
	}

	public static void setHeatDelay(int heatDelay) {
		MeasurementParameters.heatDelay = heatDelay;
	}
	
	
}