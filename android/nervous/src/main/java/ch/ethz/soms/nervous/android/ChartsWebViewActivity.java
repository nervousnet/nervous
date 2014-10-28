package ch.ethz.soms.nervous.android;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class ChartsWebViewActivity extends Activity {

	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charts_webview);

        String javascript_global_variables = getIntent().getStringExtra("javascript_global_variables");
        String selected_sensor = getIntent().getStringExtra("selected_sensor");

		webView = (WebView) findViewById(R.id.webView_charts);
		webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("javascript:"+javascript_global_variables);
//        webView.loadUrl("javascript:var screen_height = " +size.y);
//        webView.loadUrl("javascript:var labels_array = [\"Blabla\",\"February\",\"ciaooo\",\"May\",\"June\",\"July\"]");
//        webView.loadUrl("javascript:var x_axis_data_arrays = [10,30.5,1,15.5,16.5,9]");
//        webView.loadUrl("javascript:var y_axis_data_arrays = [-20,-4,27,3,11.2,-7]");
//        webView.loadUrl("javascript:var z_axis_data_arrays = [3,3,3,7,7,3]");
        webView.loadUrl("file:///android_asset/webview_charts_"+selected_sensor+".html");

	}

}