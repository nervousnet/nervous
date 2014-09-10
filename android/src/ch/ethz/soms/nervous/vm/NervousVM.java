package ch.ethz.soms.nervous.vm;

import ch.ethz.soms.nervous.android.SensorDesc;

public class NervousVM {

	private static NervousVM nervousStorage;

	public static synchronized NervousVM getInstance() {
		if (nervousStorage == null) {
			nervousStorage = new NervousVM();
		}
		return nervousStorage;
	}
	
	public NervousVM() {
		
	}
	
	
	public void storeSensor(SensorDesc sensorDesc)
	{
		// TODO
	}
	
}
