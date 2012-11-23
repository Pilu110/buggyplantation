package com.pidogames.buggyplantation.effect;

import android.graphics.Bitmap;
import android.graphics.Color;

public class GrayScaleEffect extends Effect {

	@Override
	public void applyEffect(Bitmap dst) {
		if(!enabled) return;
		int w = dst.getWidth();
		int h = dst.getHeight();
		
		int [] pixels = new int[w*h];
		dst.getPixels(pixels, 0, w, 0, 0, w, h);
		for(int i=0;i<pixels.length;i++){
			int color = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/3;
			pixels[i] = (Color.alpha(pixels[i]) << 24) | (color << 16) | (color << 8) | color;
		}
		dst.setPixels(pixels, 0, w, 0, 0, w, h);
	}
	
}
