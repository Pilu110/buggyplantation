package com.pidogames.buggyplantation.effect;


import android.graphics.Bitmap;
import android.graphics.Color;

public class PoisonEffect extends Effect {

	@Override
	public void applyEffect(Bitmap dst) {
		if(!enabled) return;
		int w = dst.getWidth();
		int h = dst.getHeight();
		int [] pixels = new int[w*h];
		dst.getPixels(pixels, 0, w, 0, 0, w, h);
		for(int i=0;i<pixels.length;i++){
			int green = Color.green(pixels[i]) + 64;
			if(green>255) green=255;
			pixels[i] = (pixels[i] & 0xffff00ff) | (green << 8);
		}
		dst.setPixels(pixels, 0, w, 0, 0, w, h);
	}

}
