package ch.ethz.soms.nervous.vm;

import ch.ethz.soms.nervous.android.SensorDesc;
import android.os.AsyncTask;

public class StoreTask extends AsyncTask<SensorDesc, Void, Void> {

	@Override
	protected Void doInBackground(SensorDesc... params) {

		if (params != null && params.length > 0) {
			NervousVM nervousVM = NervousVM.getInstance();
			for(int i = 0; i < params.length; i++)
			{
				nervousVM.storeSensor(params[i]);
			}
		}
		return null;
	}

}
