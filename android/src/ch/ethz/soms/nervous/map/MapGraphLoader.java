package ch.ethz.soms.nervous.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class MapGraphLoader extends AsyncTask<Void, Void, Void> {

	public final static int PARSE_LINEWISE = 0;
	public final static int PARSE_WHOLE = 1;

	private NervousMap map;
	private String uri;
	private int mapLayer;
	private int identifier;
	private Context context;
	private MapGraph mapGraph;
	private boolean success = false;
	private boolean reload = false;
	private int parseMode = 0;

	public MapGraphLoader(Context context, String uri, NervousMap map, int mapLayer, int identifier, String youUuid, boolean reload) {
		this.uri = uri;
		this.map = map;
		this.mapLayer = mapLayer;
		this.identifier = identifier;
		this.context = context;
		this.mapGraph = new MapGraph(context, identifier, youUuid);
		this.reload = reload;
		this.parseMode = PARSE_LINEWISE;
	}

	public MapGraphLoader(Context context, String uri, NervousMap map, int mapLayer, int identifier, int poiLayerSelect, boolean reload) {
		this.uri = uri;
		this.map = map;
		this.mapLayer = mapLayer;
		this.identifier = identifier;
		this.context = context;
		this.mapGraph = new MapGraph(context, identifier, poiLayerSelect);
		this.reload = reload;
		this.parseMode = PARSE_WHOLE;
	}

	@Override
	protected Void doInBackground(Void... params) {
		load(uri);
		File file = new File(context.getCacheDir(), "MapGraph_" + String.valueOf(mapLayer) + "_" + String.valueOf(identifier));
		if (file.exists()) {
			switch (parseMode) {
			case PARSE_LINEWISE:
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String jsonLine;
					while ((jsonLine = br.readLine()) != null) {
						try {
							JSONObject jo = new JSONObject(jsonLine);
							mapGraph.addFromJson(jo);
						} catch (JSONException e) {
						}
					}
					success = true;
					br.close();
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
				break;
			case PARSE_WHOLE:
				try {
					StringBuilder sb = new StringBuilder();
					BufferedReader br = new BufferedReader(new FileReader(file));
					String jsonLine;
					while ((jsonLine = br.readLine()) != null) {
						sb.append(jsonLine);
					}
					br.close();
					try {
						JSONArray jo = new JSONArray(sb.toString());
						mapGraph.addFromJson(jo);
					} catch (JSONException e) {
					}
					success = true;
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
				break;
			}
			return null;
		}
		success = false;
		return null;
	}

	@Override
	protected void onPostExecute(Void param) {
		if (success) {
			// Add the new graph to the map
			map.removeMapGraph(mapLayer, mapGraph.getIdentifier());
			map.addMapGraph(mapLayer, mapGraph, reload);
		}
	}

	private void load(String uri) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response;
		try {
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				File file = new File(context.getCacheDir(), "MapGraph_" + String.valueOf(mapLayer) + "_" + String.valueOf(identifier));
				FileOutputStream fis = new FileOutputStream(file, false);
				int read = 0;
				byte[] data = new byte[1024];
				while ((read = is.read(data)) != -1) {
					fis.write(data, 0, read);
				}
				try {
					is.close();
				} catch (Exception e) {
				}
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
	}

}
