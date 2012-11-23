package com.pidogames.buggyplantation.effect;

import android.graphics.Bitmap;
import android.graphics.Color;

public class LightMultiplyEffect extends Effect {
	
	private float factor;
	
	public LightMultiplyEffect(float factor){
		super();
		this.factor = factor;
	}
	
	public void setFactor(float factor){
		this.factor = factor;
	}
	
	public float getFactor(){
		return factor;
	}
	
	@Override
	public void applyEffect(Bitmap dst) {
		if(factor==1.0f) return;
		if(!enabled) return;
		int w = dst.getWidth();
		int h = dst.getHeight();
		
		int [] pixels = new int[w*h];
		dst.getPixels(pixels, 0, w, 0, 0, w, h);
		for(int i=0;i<pixels.length;i++){
			
			int alpha = Color.alpha(pixels[i]);
			int red   = (int)(Color.red(pixels[i]) * factor);
			int green = (int)(Color.green(pixels[i]) * factor);
			int blue  = (int)(Color.blue(pixels[i]) * factor);
			if(red>255) red=255;
			if(green>255) green=255;
			if(blue>255) blue=255;
			//if(red<0) red=0;
			//if(green<0) green=0;
			//if(blue<0) blue=0;
			pixels[i] = Color.argb(alpha, red, green, blue);
		}
		dst.setPixels(pixels, 0, w, 0, 0, w, h);		
	}

}
