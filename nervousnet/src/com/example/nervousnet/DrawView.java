package com.example.nervousnet;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

public class DrawView extends View {
	private int maxW;
	private int maxH;
	private Context context;
	private MainActivity mainAc;
	private SensorLoggingToggleActivity sensLogTogAc;
	private Paint paintEdges, paintCircles, paintNodeLine, paintNodeFill;

	public DrawView(Context c, AttributeSet attrs) {
		super(c, attrs);
		context = c;

		if (c instanceof MainActivity) {
			mainAc = (MainActivity) c;
		} else if (c instanceof SensorLoggingToggleActivity) {
			sensLogTogAc = (SensorLoggingToggleActivity) c;
		}

		// and we set a new Paint with the desired attributes
		paintEdges = new Paint();
		paintEdges.setAntiAlias(true);
		paintEdges.setColor(0xFFBBBBBB);
		paintEdges.setStyle(Paint.Style.STROKE);
		paintEdges.setStrokeJoin(Paint.Join.ROUND);
		paintEdges.setStrokeWidth(3);

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
		float centerX = maxW * 0.8f, centerY = maxH * 0.4f;
		canvas.drawCircle(centerX, centerY, Math.min(maxW, maxH) * 0.18f,
				paintCircles);

		centerX = maxW * 0.6f;
		centerY = maxH * 0.68f;
		canvas.drawCircle(centerX, centerY, Math.min(maxW, maxH) * 0.3f,
				paintCircles);

		centerX = maxW * 0.22f;
		centerY = maxH * 0.75f;
		canvas.drawCircle(centerX, centerY, Math.min(maxW, maxH) * 0.18f,
				paintCircles);

		centerX = maxW * 0.2f;
		centerY = maxH * 0.1f;
		canvas.drawCircle(centerX, centerY, Math.min(maxW, maxH) * 0.25f,
				paintCircles);

		float r = Math.min(maxW, maxH) * 0.14f;
		for (float i = 0.2f; i < 1.5; i += 0.2) {
			drawCircleNode(canvas, centerX, centerY, r, Math.PI * i);
		}

		// Draw Path
		ArrayList<Pair<Float, Float>> path = new ArrayList<Pair<Float, Float>>();
		path.add(new Pair<Float, Float>(centerX, centerY));
		path.add(new Pair<Float, Float>(maxW * 0.3f, maxH * 0.2f));
		path.add(new Pair<Float, Float>(maxW * 0.34f, maxH * 0.15f));
		path.add(new Pair<Float, Float>(maxW * 0.36f, maxH * 0.21f));
		path.add(new Pair<Float, Float>(maxW * 0.3f, maxH * 0.2f));
		path.add(new Pair<Float, Float>(maxW * 0.36f, maxH * 0.21f));
		path.add(new Pair<Float, Float>(maxW * 0.5f, maxH * 0.3f));
		path.add(new Pair<Float, Float>(maxW * 0.7f, maxH * 0.2f));

		// "N"
		path.add(new Pair<Float, Float>(maxW * 0.85f, maxH * 0.35f));
		path.add(new Pair<Float, Float>(maxW * 0.85f, maxH * 0.45f));
		path.add(new Pair<Float, Float>(maxW * 0.75f, maxH * 0.35f));
		path.add(new Pair<Float, Float>(maxW * 0.75f, maxH * 0.45f));

		path.add(new Pair<Float, Float>(maxW * 0.6f, maxH * 0.68f));
		path.add(new Pair<Float, Float>(maxW * 0.22f, maxH * 0.75f));
		path.add(new Pair<Float, Float>(maxW * 0.5f, maxH * 0.8f));
		path.add(new Pair<Float, Float>(maxW * 0.6f, maxH * 0.68f));
		path.add(new Pair<Float, Float>(maxW * 0.6f, maxH * 0.68f));

		for (int i = 0; i < path.size() - 1; i++) {
			Pair<Float, Float> a = path.get(i);
			Pair<Float, Float> b = path.get(i + 1);
			drawNodeEdge(canvas, a.first, a.second, b.first, b.second);
		}

		centerX = maxW * 0.7f;
		centerY = maxH * 0.2f;
		r = Math.min(maxW, maxH) * 0.2f;
		for (float i = 0.1f; i < 0.5; i += 0.2) {
			drawCircleNode(canvas, centerX, centerY, r, Math.PI * i);
		}

		if (mainAc != null) {
			mainAc.resetButtons(maxW, maxH);
		} else if (sensLogTogAc != null) {

		}
	}

	private void drawCircleNode(Canvas canvas, float centerX, float centerY,
			float r, double angle) {
		drawNodeEdge(canvas, (float) (centerX + (r * Math.cos(angle))),
				(float) (centerY - (r * Math.sin(angle))), centerX, centerY);
	}

	private void drawNodeEdge(Canvas canvas, float x1, float y1, float x2,
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
		// toastToScreen("size Changed " + w + " " + h, false);
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