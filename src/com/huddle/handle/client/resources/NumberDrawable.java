package com.huddle.handle.client.resources;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class NumberDrawable {
	
	public static Drawable createNumber(Activity activity, int number) {
		
		String n = Integer.toString(number);
	    DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm); 
		float width = dm.widthPixels;
		
		final float scale = (width / 320);
		float plus = 0;
		int minus = 0;
		if (number > 99) {
			plus = 3 * scale;
		}
		if (width <= 320.0) {
			minus = 2;
		}
		int dim = (int) (30 * scale + 0.5f) - minus;
		int txt = (int) (20 * (scale) + 0.5f) - minus;
		int extra = (int) (7 * scale + 0.5f);
		Bitmap returnedBitmap = Bitmap.createBitmap(dim + (int)(plus * 2), dim, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Paint paint = new Paint();
		paint.setColor(Color.rgb(35, 89, 158));
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		Paint paintText = new Paint();
		paintText.setColor(Color.WHITE);
		paintText.setTextSize(txt);
		paintText.setAntiAlias(true);
		paintText.setTextAlign(Align.CENTER);
		
		canvas.drawCircle(canvas.getWidth()/2 - plus, canvas.getHeight()/2, dim/2, paint);
		canvas.drawCircle(canvas.getWidth()/2 + plus, canvas.getHeight()/2, dim/2, paint);
		canvas.drawText(n, canvas.getWidth()/2, canvas.getHeight()/2 + extra, paintText);

		return new BitmapDrawable(returnedBitmap);
	}

}
