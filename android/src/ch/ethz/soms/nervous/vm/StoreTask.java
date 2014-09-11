package ch.ethz.soms.nervous.vm;

import ch.ethz.soms.nervous.android.SensorDesc;
import android.content.Context;
import android.os.AsyncTask;

public class StoreTask extends AsyncTask<SensorDesc, Void, Void> {

	private Context context;
	
	public StoreTask(Context context)
	{
		this.context = context;
	}
	
	@Override
	protected Void doInBackground(SensorDesc... params) {

		if (params != null && params.length > 0) {
			NervousVM nervousVM = NervousVM.getInstance(context);
			for(int i = 0; i < params.length; i++)
			{
				nervousVM.storeSensor(params[i]);
			}
		}
		return null;
	}

}
