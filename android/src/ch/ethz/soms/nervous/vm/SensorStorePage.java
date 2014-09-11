package ch.ethz.soms.nervous.vm;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorStorePage {

	public SensorStorePage(long sensorID, long currentPage) {
		// TODO Auto-generated constructor stub
	}

	public void store(SensorData protoSensor) {
		protoSensor.getRecordTime();
		// TODO
	}

}
