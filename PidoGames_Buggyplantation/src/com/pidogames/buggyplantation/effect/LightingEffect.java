package com.pidogames.buggyplantation.effect;


import android.graphics.Bitmap;
import android.graphics.Color;

public class LightingEffect extends Effect {
	
	private int from;
	private int to;
	private float factor;
	
	public LightingEffect(float factor, int from){
		this(factor, from, 0);
	}

	public LightingEffect(float factor, int from, int to){
		this.factor = factor;
		this.from = from;
		this.to = to;
		enabled = true;		
	}
	
	@Override
	public void applyEffect(Bitmap dst) {
		if(!enabled) return;
		int w = dst.getWidth();
		int h = dst.getHeight();
		int intensity = (int)(from*factor);
		
		int [] pixels = new int[w*h];
		dst.getPixels(pixels, 0, w, 0, 0, w, h);
		for(int i=0;i<pixels.length;i++){
			int alpha = Color.alpha(pixels[i]);
			int red   = Color.red(pixels[i]) + intensity;
			int green = Color.green(pixels[i]) + intensity;
			int blue  = Color.blue(pixels[i]) + intensity;
			if(red>255) red=255;
			if(green>255) green=255;
			if(blue>255) blue=255;
			if(red<0) red=0;
			if(green<0) green=0;
			if(blue<0) blue=0;
			pixels[i] = Color.argb(alpha, red, green, blue); //(pixels[i] & 0xffff00ff) | (green << 8);
		}
		dst.setPixels(pixels, 0, w, 0, 0, w, h);
		
		if(from<=to) {
			if(el!=null) el.OnEffectEnd(this);	
		}
		else from--;
	}

}
