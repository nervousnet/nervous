package com.example.nervousnet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class DrawView extends View {
	private int maxWidth;
	private int maxHeight;
	private Context context;
	private MainActivity mainAc;
	private Path mPath;
	private Paint paint;

	public DrawView(Context c, AttributeSet attrs) {
		super(c, attrs);
		context = c;
		mainAc = (MainActivity) c;

		// we set a new Path
		mPath = new Path();

		// and we set a new Paint with the desired attributes
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(8);
		setBackgroundColor(Color.GRAY);
	}


	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawLine(0, 0, getMaxWidth(), getMaxHeight(), paint);
		canvas.drawLine(getMaxWidth(), getMaxHeight(), getMaxWidth() - 100, 200, paint);

		paint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(maxWidth/2, maxHeight/2, Math.min(maxWidth, maxHeight)/2, paint);
		mainAc.resetButtons(maxWidth, maxHeight);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		setMaxWidth(w);
		setMaxHeight(h);
		toastToScreen("size CHanged " + w + " " + h,false);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}


	public int getMaxWidth() {
		return maxWidth;
	}


	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}
	
	private void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(context, msg, toastLength).show();
	}

}