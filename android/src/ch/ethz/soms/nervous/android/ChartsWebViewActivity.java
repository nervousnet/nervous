package ch.ethz.soms.nervous.android;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.webkit.WebView;

public class ChartsWebViewActivity extends Activity {

	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* Load the webview that shows the plot from the corresponding html file in assets */
		
		setContentView(R.layout.charts_webview);

		//To debug webview
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }

		// Get javascript variable from intent and set it into the webview
        String javascript_global_variables = getIntent().getStringExtra("javascript_global_variables");
        Log.i("javascript var: ",javascript_global_variables);
        String type_of_plot = getIntent().getStringExtra("type_of_plot");

		webView = (WebView) findViewById(R.id.webView_charts);
		webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("javascript:" + javascript_global_variables);
        webView.loadUrl("file:///android_asset/webview_charts_"+type_of_plot+".html");
	}
}