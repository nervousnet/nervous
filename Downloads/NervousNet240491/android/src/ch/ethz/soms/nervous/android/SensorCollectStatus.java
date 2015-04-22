package ch.ethz.soms.nervous.android;

class SensorCollectStatus {
	private boolean doMeasure = false;
	private boolean doShare = false;
	private int measureInterval = 1;
	private long measureDuration = -1;
	private int collectAmount = -1;
	private long sensorId;

	private int currentCollectAmount = 0;
	private long measureStop = -1;

	public SensorCollectStatus(long sensorId, boolean doMeasure, boolean doShare, int measureInterval, long measureDuration, int collectAmount) {
		this.doMeasure = doMeasure;
		this.doShare = doShare;
		this.measureInterval = measureInterval;
		this.measureDuration = measureDuration;
		this.collectAmount = collectAmount;
		this.sensorId = sensorId;
	}

	public synchronized void setMeasureStart(long measureStart) {
		currentCollectAmount = 0;
		if (measureDuration > -1) {
			measureStop = measureStop + measureDuration;
		}
	}

	public synchronized void increaseCollectAmount() {
		currentCollectAmount += 1;
	}

	public void setMeasureInterval(int measureInterval) {
		this.measureInterval = measureInterval;
	}

	public void setMeasureDuration(long measureDuration) {
		this.measureDuration = measureDuration;
	}

	public synchronized boolean isCollect() {
		return doMeasure;
	}

	public synchronized boolean isDone(long currentTime) {
		return (measureDuration > -1) ? currentTime >= measureStop : currentCollectAmount >= collectAmount;
	}

	public synchronized boolean isShare() {
		return doShare;
	}

	public synchronized long getMeasureDuration() {
		return measureDuration;
	}
	
	public long getMeasureInterval() {
		return measureInterval;
	}

	public long getSensorId() {
		return sensorId;
	}
}
