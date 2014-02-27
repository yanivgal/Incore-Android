package com.yaniv.incore;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class Circle extends View {

	int screenWidth;
	int screenHeight;
	
	ArrayList<Integer> x;
	ArrayList<Integer> y;
	ArrayList<Integer> radius;
	
	Paint paint;
	
	public Circle(Context context) {
		super(context);
		
		x = new ArrayList<Integer>();
		y = new ArrayList<Integer>();
		radius = new ArrayList<Integer>();
		
		paint = new Paint();
        paint.setColor(0xFF0000);
        paint.setAlpha(255);
        paint.setStrokeWidth(2.0f);
        paint.setStyle(Paint.Style.STROKE);
	}
	
	public void addCircle(int x, int y, int radius) {
		
		this.x.add(x / 2);
		this.y.add(y / 2);
		this.radius.add(radius / 2);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for (int i = 0; i < x.size(); i++) {
	        canvas.drawCircle(x.get(i), y.get(i), radius.get(i), paint);
		}
//        invalidate();
	}

}
