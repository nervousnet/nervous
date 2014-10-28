package ch.ethz.soms.nervous.android;

class SensorCollectStatus {
	private boolean doMeasure = false;
	private boolean doShare = false;
	private int measureFrequency = 1;
	private long measureDuration = -1;
	private int collectAmount = -1;

	private int currentCollectAmount = 0;
	private long measureStop = -1;

	public SensorCollectStatus(boolean doMeasure, boolean doShare, int measureFrequency, long measureDuration, int collectAmount) {
		this.doMeasure = doMeasure;
		this.doShare = doShare;
		this.measureFrequency = measureFrequency;
		this.measureDuration = measureDuration;
		this.collectAmount = collectAmount;
	}

	public synchronized void setMeasureStart(long measureStart) {
		if (measureDuration > -1) {
			this.measureStop = measureStop + measureDuration;
		}
	}

	public synchronized void increaseCollectAmount() {
		currentCollectAmount += 1;
	}

	public synchronized boolean isCollect(int serviceRound) {
		return doMeasure && (serviceRound % measureFrequency == 0);
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
}
