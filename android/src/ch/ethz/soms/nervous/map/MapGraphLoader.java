package ch.ethz.soms.nervous.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class MapGraphLoader extends AsyncTask<Void, Void, Void> {

	private NervousMap map;
	private String uri;
	private int mapLayer;
	private JSONArray json;

	public MapGraphLoader(String uri, NervousMap map, int mapLayer) {
		this.uri = uri;
		this.map = map;
		this.mapLayer = mapLayer;
	}

	@Override
	protected Void doInBackground(Void... params) {
		json = load(uri);
		return null;
	}

	@Override
	protected void onPostExecute(Void param) {
		map.addMapGraph(mapLayer, new MapGraph(json));
	}

	private static JSONArray load(String uri) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response;
		try {
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				JSONArray json = streamToJson(is);
				try {
					is.close();
				} catch (Exception e) {
				}
				return json;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		return null;
	}

	private static JSONArray streamToJson(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		JSONArray jsonArray = new JSONArray();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				try {
					jsonArray.put(new JSONObject(line));
				} catch (JSONException e) {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		return jsonArray;
	}

}
