package com.pidogames.buggyplantation.effect;


import android.graphics.Bitmap;
import android.graphics.Color;

public class DisappearEffect extends Effect {
	
	int duration;
	int i;
	
	public DisappearEffect(int duration){
		this.duration = duration;
		i = duration;
		enabled = true;
	}
	
	@Override
	public void applyEffect(Bitmap dst){
		if(!enabled) return;
		int w = dst.getWidth();
		int h = dst.getHeight();
		int intensity = 255*(duration - i)/duration;
		
		if(i<1) i=1;
		int [] pixels = new int[w*h];
		dst.getPixels(pixels, 0, w, 0, 0, w, h);
		for(int i=0;i<pixels.length;i++){
			int alpha = Color.alpha(pixels[i]) - intensity;
			if(alpha<0) alpha=0;
			pixels[i] = (pixels[i] & 0x00ffffff) | (alpha << 24);
		}
		dst.setPixels(pixels, 0, w, 0, 0, w, h);
		i--;
		
		if(i<2 && el!=null) el.OnEffectEnd(this);			
	}
	
}
