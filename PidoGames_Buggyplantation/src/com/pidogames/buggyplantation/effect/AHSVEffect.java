package com.pidogames.buggyplantation.effect;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class AHSVEffect extends Effect {
	
	private float ar;
	private float hr;
	private float sr;
	private float vr;
	private int[] mask_pixels;
	
	public AHSVEffect(float ar, float hr, float sr, float vr, Bitmap mask) {
		super();
		this.ar = ar;
		this.hr = hr;
		this.sr = sr;
		this.vr = vr;
		if(mask!=null){
			int w = mask.getWidth();
			int h = mask.getHeight();
			mask_pixels = new int[w*h];
			mask.getPixels(mask_pixels, 0, w, 0, 0, w, h);			
		}
		else mask_pixels = null;
	}

	@Override
	public void applyEffect(Bitmap dst) {
		if(!enabled) return;
		int w = dst.getWidth();
		int h = dst.getHeight();
		
		int [] pixels = new int[w*h];
		float [] hsv = new float[3];
		dst.getPixels(pixels, 0, w, 0, 0, w, h);
		
		boolean mp = (mask_pixels!=null);
		for(int i=0;i<pixels.length;i++){
			if(!mp || Color.red(mask_pixels[i])>0){
				final int c = pixels[i];
				Color.colorToHSV(c, hsv);
				hsv[0] *= hr;
				hsv[1] *= sr;
				hsv[2] *= vr;
				int alpha = Color.alpha(c);
				if(ar!=1f) {
					alpha = (int)(alpha * ar);
					if(alpha>255) alpha = 255;
				}
				
				if(mp){
					final int c2 = Color.HSVToColor(alpha, hsv);
					float r = Color.red(mask_pixels[i])/255f;
					int cr = (int)(Color.red(c2)*r + Color.red(c)*(1-r));
					int cg = (int)(Color.green(c2)*r + Color.green(c)*(1-r));
					int cb = (int)(Color.blue(c2)*r + Color.blue(c)*(1-r));
					pixels[i] = Color.argb(alpha, cr, cg, cb);
				}
				else {
					pixels[i] = Color.HSVToColor(alpha, hsv);
				}
			}
		}
		dst.setPixels(pixels, 0, w, 0, 0, w, h);
	}

}
