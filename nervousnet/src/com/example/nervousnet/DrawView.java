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
	private int maxW;
	private int maxH;
	private Context context;
	private MainActivity mainAc;
	private Paint paintEdges, paintCircles, paintNodeLine, paintNodeFill;

	public DrawView(Context c, AttributeSet attrs) {
		super(c, attrs);
		context = c;
		mainAc = (MainActivity) c;

		// and we set a new Paint with the desired attributes
		paintEdges = new Paint();
		paintEdges.setAntiAlias(true);
		paintEdges.setColor(Color.BLACK);
		paintEdges.setStyle(Paint.Style.STROKE);
		paintEdges.setStrokeJoin(Paint.Join.ROUND);
		paintEdges.setStrokeWidth(4);

		paintCircles = new Paint();
		paintCircles.setAntiAlias(true);
		paintCircles.setColor(0x66D1D1D1);
		paintCircles.setStyle(Paint.Style.FILL);
		paintCircles.setStrokeJoin(Paint.Join.ROUND);
		paintCircles.setStrokeWidth(4);

		paintNodeFill = new Paint();
		paintNodeFill.setAntiAlias(true);
		paintNodeFill.setColor(0xFFFFFFFF);
		paintNodeFill.setStyle(Paint.Style.FILL);
		paintNodeFill.setStrokeJoin(Paint.Join.ROUND);
		paintNodeFill.setStrokeWidth(4);

		paintNodeLine = new Paint();
		paintNodeLine.setAntiAlias(true);
		paintNodeLine.setColor(0xFF999999);
		paintNodeLine.setStyle(Paint.Style.STROKE);
		paintNodeLine.setStrokeJoin(Paint.Join.ROUND);
		paintNodeLine.setStrokeWidth(2);

		setBackgroundColor(0xFFFFFFFF);
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawCircle((maxW * 4 / 7), maxH / 3, Math.min(maxW, maxH) / 5,
				paintCircles);
		canvas.drawCircle((maxW * 1 / 8), maxH * 13 / 100,
				Math.min(maxW, maxH) * 200 / 750, paintCircles);

		canvas.drawLine(0, 0, maxW, maxH, paintEdges);
		canvas.drawLine(maxW, maxH, maxW - 100, 200, paintEdges);

		float x1 = maxW / 3, x2 = maxW / 2, y1 = maxH / 5, y2 = maxH / 2;
		drawNodeEdge(canvas, x1, x2, y1, y2);

		mainAc.resetButtons(maxW, maxH);
	}

	private void drawNodeEdge(Canvas canvas, float x1, float x2, float y1,
			float y2) {
		int radius = Math.min(maxH, maxW) / 50;

		canvas.drawLine(x1, y1, x2, y2, paintEdges);
		canvas.drawCircle(x1, y1, radius, paintNodeFill);
		canvas.drawCircle(x1, y1, radius, paintNodeLine);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		setMaxWidth(w);
		setMaxHeight(h);
		// toastToScreen("size CHanged " + w + " " + h, false);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public int getMaxHeight() {
		return maxH;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxH = maxHeight;
	}

	public int getMaxWidth() {
		return maxW;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxW = maxWidth;
	}

	private void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(context, msg, toastLength).show();
	}

}