package com.aviacomm.hwmp2p.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ChartView extends View {
	private float[] data;
	public final int CAPACITY = 20;
	public float MAXDATA = 30;
	public float MINDATA = 0;
	float between;
	int start = 0, end = 0;
	public int COLOR = Color.RED;
	Paint p;
	int width, height;
	float hstep, vstep;
	private final String TAG = "chart";

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		data = new float[CAPACITY];
		p = new Paint();
		p.setColor(COLOR);
		p.setStrokeWidth(5);
		between = MAXDATA - MINDATA;
	}


	public void setMAXDATA(float mAXDATA) {
		MAXDATA = mAXDATA;
		between = MAXDATA - MINDATA;
	}

	public void setMINDATA(float mINDATA) {
		MINDATA = mINDATA;
		between = MAXDATA - MINDATA;
	}

	public void setCOLOR(int cOLOR) {
		COLOR = cOLOR;
		p.setColor(COLOR);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		width = this.getWidth();
		height = this.getHeight();
		hstep = -((float) width) / (CAPACITY-2);
		vstep = ((float) height) / between;
		int index = minusAndCheckNegative(end), nextIndex = minusAndCheckNegative(index);
		float horizenPosition = width;
		Log.i(TAG, width + " " + height + " ");
		// 以当前点和之后的一个点画一条线。
		while (end != start && index != start) {
			canvas.drawLine(horizenPosition, height - vstep * data[index],
					horizenPosition + hstep, height - vstep * data[nextIndex],
					p);
			horizenPosition += hstep;
			index = nextIndex;
			nextIndex = minusAndCheckNegative(index);
		}
	}

	private int plusAndCheckOverflow(int number) {
		number++;
		return number < CAPACITY ? number : number - CAPACITY;
	}

	private int minusAndCheckNegative(int number) {
		number--;
		return number < 0 ? number + CAPACITY : number;
	}

	public void addOneData(float number) {
		if ((end + 1) % CAPACITY == start) {
			// it is full
			start = plusAndCheckOverflow(start);
		}
		if (number > MAXDATA)
			number = MAXDATA;
		if (number < MINDATA)
			number = MINDATA;
		data[end] = number;
		end = plusAndCheckOverflow(end);
	}
	public void updateView(){
		postInvalidate();
	}

}
